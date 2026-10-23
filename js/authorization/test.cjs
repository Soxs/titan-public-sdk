const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const crypto = require('node:crypto');
const root = __dirname;
const fixture = JSON.parse(fs.readFileSync(path.join(root, '../../authorization/test-fixtures/plugin-authorization.json'), 'utf8'));
const runtime = fs.readFileSync(path.join(root, 'runtime.js'), 'utf8');
const prelude = fs.readFileSync(path.join(root, 'titan-authorization.js'), 'utf8');
const publicKeys = fs.readFileSync(path.join(root, 'keys.json'), 'utf8').replace(/\r\n/g, '\n');
function sdk(host, keys, exposeVerifier = false) {
    let source = prelude.replace(publicKeys, JSON.stringify(keys));
    if (exposeVerifier) source = source.replace('const instances = new WeakMap();', 'host.verifyForTest = receipt; const instances = new WeakMap();');
    class TestDate extends Date { static now() { return fixture.now * 1000; } }
    const context = vm.createContext({ titan: host, Date: TestDate });
    vm.runInContext(source, context);
    return context.titan;
}
const fixtureKey = fixture.public_key_pem.replace(/-----[^-]+-----|\s/g, '');
const verifierHost = { _authorizationNow: () => 1000 };
sdk(verifierHost, { [fixture.kid]: fixtureKey }, true);
for (const name of ['acquire', 'report', 'check', 'list', 'denied']) {
    const operation = name === 'denied' ? 'acquire' : name;
    const envelope = { ...fixture.cases[name], caller_context: fixture.caller_context };
    const pending = { op: operation, request: fixture.bindings.request_id, challenge: fixture.bindings.challenge, started: 1000, context: fixture.context };
    const result = verifierHost.verifyForTest(JSON.stringify(envelope), pending);
    assert.equal(result.result.operation, operation);
    assert.equal(result.deadline, 301000);
    assert.throws(() => verifierHost.verifyForTest(JSON.stringify({ ...envelope, result_json: envelope.result_json + ' ' }), pending));
    assert.throws(() => verifierHost.verifyForTest(JSON.stringify(envelope), { ...pending, challenge: 'wrong' }));
    assert.throws(() => verifierHost.verifyForTest(JSON.stringify(envelope), { ...pending, context: { ...fixture.context, runtime_generation: 4 } }));
    assert.throws(() => verifierHost.verifyForTest(JSON.stringify(envelope).replace('"proof":', '"proof":"duplicate","proof":'), pending));
}

const pair = crypto.generateKeyPairSync('rsa', { modulusLength: 2048 });
const testKeys = { test: pair.publicKey.export({ type: 'spki', format: 'der' }).toString('base64') };
function sign(claims) {
    const header = Buffer.from(JSON.stringify({ alg: 'RS256', typ: 'titan-authorization+jwt', kid: 'test' })).toString('base64url');
    const payload = Buffer.from(JSON.stringify(claims)).toString('base64url');
    return header + '.' + payload + '.' + crypto.sign('RSA-SHA256', Buffer.from(header + '.' + payload), pair.privateKey).toString('base64url');
}
function harness(options = {}) {
    let now = 1000, next = 0n, stopped = 0, issued = 0;
    const listeners = new Set(), requests = new Map(), operations = [], deadlines = new WeakMap();
    const context = fixture.context;
    const certificate = sign(context);
    const host = {
        _authorizationNow: () => now,
        _authorizationSetDeadline: (owner, deadline) => deadlines.set(owner, deadline),
        _authorizationExpired: owner => deadlines.has(owner) && now >= deadlines.get(owner),
        _authorizationNonce: () => crypto.randomBytes(32).toString('base64url'),
        _authorizationContext: () => JSON.stringify(context),
        _authorizationSubscribe: (owner, listener) => { listeners.add(listener); return true; },
        _authorizationUnsubscribe: (owner, listener) => listeners.delete(listener),
        _authorizationSubmit(owner, op, body) { const id = ++next; requests.set(id, { op, body: JSON.parse(body), issuedAt: now }); operations.push(op); return id; },
        _authorizationCancel(owner, id) { requests.delete(id); },
        _authorizationStop() { stopped++; for (const [id, item] of requests) if (item.op !== 'cancel') requests.delete(id); },
        _authorizationPoll(owner, id) {
            if (options.delay || options.offline) return '';
            const item = requests.get(id); if (!item) return ''; requests.delete(id);
            const { op, body } = item;
            const allowed = !options.denied;
            let result;
            if (op === 'acquire') {
                issued++;
                result = { ...JSON.parse(fixture.cases.acquire.result_json), run_id: body.payload.run_id, allowed, code: allowed ? null : 'instance-limit' };
            } else if (op === 'report') {
                result = { ...JSON.parse(fixture.cases.report.result_json), sequence: body.payload.sequence,
                    runs: body.payload.runs.map(run => ({ ...JSON.parse(fixture.cases.acquire.result_json), run_id: run.run_id, state: 'running' })) };
            } else if (op === 'list' || op === 'check') result = JSON.parse(fixture.cases[op].result_json);
            else result = { operation: op, allowed: false, code: 'run-retired' };
            if (options.topDeny && (op === 'report' || op === 'acquire')) result = { operation: op, allowed: false, code: 'feature-disabled' };
            if (options.checkDenied && op === 'check') result = { ...result, allowed: false, code: 'not-entitled', entitlement: { ...result.entitlement, entitled: false } };
            if (options.duplicatePage && op === 'list') result = { ...result, entries: [...result.entries, ...result.entries], total_entries: 2 };
            if (options.wrongOffset && op === 'list') result = { ...result, offset: 1 };
            const result_json = JSON.stringify(result);
            const claims = { ...fixture.context, ...fixture.bindings, purpose: op === 'check' || op === 'list' ? 'plugin_entitlements' : 'plugin_run',
                exp: fixture.now + 300, operation: op, request_id: body.request_id, challenge: body.challenge,
                caller_context_sha256: crypto.createHash('sha256').update(certificate).digest('hex'),
                result_sha256: crypto.createHash('sha256').update(result_json).digest('hex') };
            if (body.payload.run_id && !options.topDeny) claims.run_id = body.payload.run_id;
            const response = { proof: sign(claims), result_json, caller_context: certificate };
            if (options.tamper) response.result_json += ' ';
            return JSON.stringify(response);
        }
    };
    return { api: sdk(host, testKeys), options, operations, get stopped() { return stopped; }, get issued() { return issued; },
        advance(ms) { now += ms; }, pulse() { for (const listener of [...listeners]) listener.pulse(); }, tick() { for (const listener of [...listeners]) listener.tick(1); } };
}
const flush = async () => { await Promise.resolve(); await Promise.resolve(); await Promise.resolve(); };
(async () => {
    {
        const h = harness(), owner = { id: fixture.context.runtime_plugin_id }, calls = [];
        const run = h.api.instance(owner, { start() { calls.push('start'); return true; }, tick() { calls.push('tick'); }, stop() { calls.push('stop'); throw Error('cleanup'); } });
        const start = run.start(); assert.equal(run.start(), start); assert.deepEqual(calls, []);
        h.pulse(); await flush(); assert.equal((await start).started, true); assert.equal(run.active, true);
        h.tick(); assert.deepEqual(calls, ['start', 'tick']); run.stop(); assert.equal(run.active, false);
        assert.equal(h.stopped, 1); assert.equal(h.operations.filter(x => x === 'cancel').length, 1);
    }
    for (const options of [{ denied: true }, { tamper: true }]) {
        const h = harness(options); let starts = 0;
        const run = h.api.instance({ id: fixture.context.runtime_plugin_id }, { start() { starts++; }, stop() {} });
        const result = run.start(); h.pulse(); await flush(); assert.equal((await result).started, false); assert.equal(starts, 0);
    }
    {
        const h = harness({ delay: true }); let starts = 0;
        const run = h.api.instance({ id: fixture.context.runtime_plugin_id }, { start() { starts++; }, stop() {} });
        const result = run.start(); run.stop(); h.options.delay = false; h.pulse(); await flush();
        assert.equal((await result).started, false); assert.equal(starts, 0);
    }
    {
        const h = harness(); let cleanup = 0;
        const run = h.api.instance({ id: fixture.context.runtime_plugin_id }, { start() { return false; }, stop() { cleanup++; } });
        const result = run.start(); h.pulse(); await flush(); assert.equal((await result).started, false); assert.equal(cleanup, 1);
    }
    {
        const h = harness(); let cleanup = 0;
        const run = h.api.instance({ id: fixture.context.runtime_plugin_id }, { start() {}, stop() { cleanup++; } });
        const result = run.start(); h.pulse(); await flush(); await result;
        h.options.offline = true; h.advance(300001); h.pulse(); await flush();
        assert.equal(run.active, false); assert.equal(cleanup, 1);
    }
    {
        const h = harness(), access = h.api.entitlements({ id: fixture.context.runtime_plugin_id });
        const check = access.check(); h.pulse(); await flush(); assert.equal((await check).decision, 'granted');
        const list = access.list(); h.pulse(); await flush(); const snapshot = await list;
        assert.equal(snapshot.complete, true); assert.equal(snapshot.entries.length, 1);
        h.advance(300001); h.pulse(); assert.equal(access.snapshot().freshness, 'stale');
    }
    {
        const h = harness(), access = h.api.entitlements({ id: fixture.context.runtime_plugin_id });
        let listing = access.list(); h.pulse(); await flush(); assert.equal((await listing).freshness, 'current');
        h.options.checkDenied = true; const check = access.check(); h.pulse(); await flush();
        assert.equal((await check).decision, 'denied'); assert.equal(access.snapshot().complete, false);
    }
    for (const option of ['duplicatePage', 'wrongOffset']) {
        const h = harness(), access = h.api.entitlements({ id: fixture.context.runtime_plugin_id });
        let listing = access.list(); h.pulse(); await flush(); await listing;
        h.options[option] = true; listing = access.list(); h.pulse(); await flush();
        assert.equal((await listing).freshness, 'stale'); assert.equal(access.snapshot().entries.length, 1);
    }
    {
        const h = harness(), calls = [];
        const run = h.api.instance({ id: fixture.context.runtime_plugin_id }, { start() {}, stop(reason) { calls.push(reason); } });
        const result = run.start(); h.pulse(); await flush(); await result;
        h.options.topDeny = true; h.pulse(); await flush();
        assert.deepEqual(calls, ['feature-disabled']); assert.equal(run.active, false);
    }
    {
        const h = harness(); let complete, calls = 0, cleanup = 0;
        const run = h.api.instance({ id: fixture.context.runtime_plugin_id }, { start() {}, stop() { cleanup++; } });
        const work = run.run(() => { calls++; return new Promise(resolve => { complete = resolve; }); });
        assert.equal((await run.run(() => { calls++; })).reason, 'task_already_running');
        h.pulse(); await flush(); assert.equal(calls, 1); complete(); await work;
        assert.equal(cleanup, 1); assert.equal(run.active, false);
    }
    {
        const h = harness(); let ticks = 0;
        const run = h.api.instance({ id: fixture.context.runtime_plugin_id }, { start() {}, tick() { ticks++; }, stop() {} });
        const start = run.start(); h.pulse(); await flush(); await start;
        h.options.offline = true; h.advance(5001); h.pulse(); await flush();
        assert.equal(run.status, 'reconnecting'); assert.equal(run.active, true); h.tick(); assert.equal(ticks, 1);
        h.options.offline = false; h.advance(15001); h.pulse(); await flush(); h.pulse(); await flush();
        assert.equal(run.status, 'running'); run.stop();
    }
    {
        const h = harness(); let cleanup = 0;
        const run = h.api.instance({ id: fixture.context.runtime_plugin_id }, { start() {}, stop() { cleanup++; } });
        const result = run.run(async () => { throw Error('task failure'); });
        h.pulse(); await flush();
        assert.equal((await result).reason, 'task_failed');assert.equal((await result).started, false);
        assert.equal(run.active, false);assert.equal(cleanup, 1);
    }
    console.log('Authorization: server fixtures, signature/session binding, denial, cancellation, cleanup, outage and listing tests passed.');
})().catch(error => { console.error(error); process.exitCode = 1; });
