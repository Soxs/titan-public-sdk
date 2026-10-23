// Plugin-local authorization. build.py bundles the pinned RSA implementation.
function installAuthorization(host, forge, trustedKeys) {
    'use strict';
    const MAX_BODY = 1048576, MAX_PROOF = 16384;
    function invalid() { throw new Error('invalid_authorization_receipt'); }
    function strictJson(text) {
        if (typeof text !== 'string' || text.length > MAX_BODY) invalid();
        let pos = 0;
        function ws() { while (pos < text.length && /[ \r\n\t]/.test(text[pos])) pos++; }
        function string() {
            const start = pos++;
            while (pos < text.length) {
                const c = text[pos++];
                if (c === '"') {
                    const value = JSON.parse(text.slice(start, pos));
                    for (let i = 0; i < value.length; i++) {
                        const n = value.charCodeAt(i);
                        if (n >= 0xd800 && n <= 0xdbff) { const next = value.charCodeAt(++i); if (!(next >= 0xdc00 && next <= 0xdfff)) invalid(); }
                        else if (n >= 0xdc00 && n <= 0xdfff) invalid();
                    }
                    return value;
                }
                if (c === '\\') pos++;
                else if (c.charCodeAt(0) < 32) invalid();
            }
            invalid();
        }
        function value(depth) {
            if (depth > 32) invalid(); ws();
            const c = text[pos];
            if (c === '"') return string();
            if (c === '{') {
                pos++; const object = Object.create(null); ws();
                if (text[pos] === '}') { pos++; return Object.freeze(object); }
                while (true) {
                    ws(); if (text[pos] !== '"') invalid(); const key = string();
                    if (Object.prototype.hasOwnProperty.call(object, key)) invalid();
                    ws(); if (text[pos++] !== ':') invalid(); object[key] = value(depth + 1); ws();
                    const next = text[pos++]; if (next === '}') return Object.freeze(object); if (next !== ',') invalid();
                }
            }
            if (c === '[') {
                pos++; const array = []; ws(); if (text[pos] === ']') { pos++; return Object.freeze(array); }
                while (true) { array.push(value(depth + 1)); ws(); const next = text[pos++]; if (next === ']') return Object.freeze(array); if (next !== ',') invalid(); }
            }
            for (const [literal, result] of [['true', true], ['false', false], ['null', null]]) {
                if (text.slice(pos, pos + literal.length) === literal) { pos += literal.length; return result; }
            }
            const match = /^-?(0|[1-9][0-9]*)/.exec(text.slice(pos)); if (!match) invalid();
            pos += match[0].length; const number = Number(match[0]); if (!Number.isSafeInteger(number)) invalid(); return number;
        }
        const result = value(0); ws(); if (pos !== text.length) invalid(); return result;
    }
    function object(value) { if (!value || typeof value !== 'object' || Array.isArray(value)) invalid(); return value; }
    function number(object, key) { if (!Number.isSafeInteger(object[key])) invalid(); return object[key]; }
    function string(object, key) { if (typeof object[key] !== 'string') invalid(); return object[key]; }
    function b64(value) {
        if (typeof value !== 'string' || !/^[A-Za-z0-9_-]+$/.test(value)) invalid();
        const padded = value.replace(/-/g, '+').replace(/_/g, '/') + '='.repeat((4 - value.length % 4) % 4);
        const result = forge.util.decode64(padded);
        if (forge.util.encode64(result).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_') !== value) invalid();
        return result;
    }
    function hash(value) { return forge.md.sha256.create().update(value, 'utf8').digest().toHex(); }
    const parsedKeys = Object.create(null);
    function signed(compact, purpose, maxAge) {
        if (typeof compact !== 'string' || compact.length > MAX_PROOF) invalid();
        const parts = compact.split('.'); if (parts.length !== 3) invalid();
        const header = object(strictJson(forge.util.decodeUtf8(b64(parts[0]))));
        if (header.alg !== 'RS256' || header.typ !== 'titan-authorization+jwt') invalid();
        for (const key of ['crit', 'jku', 'jwk', 'x5u', 'x5c', 'b64']) if (Object.prototype.hasOwnProperty.call(header, key)) invalid();
        const der = trustedKeys[string(header, 'kid')]; if (!der) invalid();
        const key = parsedKeys[header.kid] || (parsedKeys[header.kid] = forge.pki.publicKeyFromAsn1(forge.asn1.fromDer(forge.util.decode64(der))));
        const digest = forge.md.sha256.create().update(parts[0] + '.' + parts[1], 'raw').digest().getBytes();
        if (!key.verify(digest, b64(parts[2]), 'RSASSA-PKCS1-V1_5')) invalid();
        const claims = object(strictJson(forge.util.decodeUtf8(b64(parts[1]))));
        const now = Math.floor(Date.now() / 1000), iat = number(claims, 'iat'), exp = number(claims, 'exp'), nbf = number(claims, 'nbf');
        if (claims.v !== 2 || claims.iss !== 'https://titan.lc' || claims.aud !== 'titan-authorization' || claims.purpose !== purpose ||
            iat <= 0 || exp <= iat || exp - iat > maxAge || nbf > iat || iat > now + 30 || nbf > now + 30 || exp <= now) invalid();
        return claims;
    }
    function receipt(raw, pending) {
        const envelope = object(strictJson(raw));
        const caller = signed(envelope.caller_context, 'plugin_runtime_context', 1800);
        const claims = signed(envelope.proof, pending.op === 'check' || pending.op === 'list' ? 'plugin_entitlements' : 'plugin_run', 300);
        for (const key of ['client_instance_id', 'runtime_plugin_id', 'build_sha256', 'development']) {
            if (pending.context[key] === undefined || pending.context[key] !== caller[key] || pending.context[key] !== claims[key]) invalid();
        }
        function generation(value) { if (typeof value === 'number' && Number.isSafeInteger(value) && value > 0) return String(value); if (typeof value === 'string' && /^[1-9][0-9]{0,15}$/.test(value) && Number.isSafeInteger(Number(value))) return value; invalid(); }
        if (generation(pending.context.runtime_generation) !== generation(caller.runtime_generation) || generation(pending.context.runtime_generation) !== generation(claims.runtime_generation)) invalid();
        for (const key of ['sub', 'did', 'dv', 'caller_plugin_id', 'canonical_plugin_id']) if (caller[key] === undefined || caller[key] !== claims[key]) invalid();
        if (claims.caller_context_sha256 !== hash(envelope.caller_context) || claims.operation !== pending.op ||
            claims.request_id !== pending.request || claims.challenge !== pending.challenge) invalid();
        if (typeof envelope.result_json !== 'string' || forge.util.encodeUtf8(envelope.result_json).length > MAX_BODY || claims.result_sha256 !== hash(envelope.result_json)) invalid();
        const result = object(strictJson(envelope.result_json)); if (result.operation !== pending.op) invalid();
        if (pending.run && claims.run_id !== pending.run && (claims.run_id !== undefined || result.allowed !== false)) invalid();
        const deadline = pending.started + Math.min(claims.exp - claims.iat, claims.exp - Math.floor(Date.now() / 1000)) * 1000;
        if (deadline <= host._authorizationNow()) invalid();
        return { claims, result, deadline };
    }
    function client(owner, hooks) {
        const pending = new Map(); let context;
        const pluginId = owner && owner.id;
        const listener = {
            pulse() {
                for (const [id, item] of Array.from(pending.entries())) {
                    let raw;
                    try { raw = host._authorizationPoll(owner, id); } catch (error) { pending.delete(id); item.reject(error); continue; }
                    if (!raw) {
                        if (host._authorizationNow() - item.started < (item.op === 'report' ? 5000 : 30000)) continue;
                        host._authorizationCancel(owner, id); pending.delete(id); item.reject(new Error('authorization_timeout')); continue;
                    }
                    pending.delete(id);
                    try { item.resolve(receipt(raw, item)); } catch (error) { item.reject(error); }
                }
                if (hooks.pulse) hooks.pulse();
            },
            tick(tick) { listener.pulse(); if (hooks.tick) hooks.tick(tick); },
            stop(reason) { cancel(reason); if (hooks.stop) hooks.stop(reason); }
        };
        function cancel(reason) { const copy = Array.from(pending.entries()); pending.clear(); for (const [id, item] of copy) { host._authorizationCancel(owner, id); item.reject(new Error(reason)); } }
        return {
            request(op, payload, run) {
                return new Promise((resolve, reject) => {
                    try {
                        if (!host._authorizationSubscribe || !host._authorizationSubscribe(owner, listener)) throw new Error('authorization_unavailable');
                        const next = object(strictJson(host._authorizationContext(owner)));
                        if (typeof pluginId !== 'string' || !pluginId || next.runtime_plugin_id !== pluginId) throw new Error('wrong_plugin');
                        const changed = context && JSON.stringify(context) !== JSON.stringify(next); context = next;
                        if (changed) { cancel('session_changed'); if (hooks.stop) hooks.stop('session_changed'); }
                        if (pending.size >= 16) throw new Error('too_many_requests');
                        const challenge = host._authorizationNonce(), request = host._authorizationNonce();
                        if (!/^[A-Za-z0-9_-]{43}$/.test(challenge) || !/^[A-Za-z0-9_-]{43}$/.test(request)) throw new Error('entropy_unavailable');
                        const started = host._authorizationNow();
                        const id = host._authorizationSubmit(owner, op, JSON.stringify({ request_id: request, challenge, payload }));
                        if (!id) throw new Error('authorization_unavailable');
                        pending.set(id, { op, payload, run, challenge, request, started, context, resolve, reject });
                    } catch (error) { reject(error); }
                });
            }, cancel,
            stop() { if (host._authorizationStop) host._authorizationStop(owner); },
            close() { cancel('closed'); if (host._authorizationUnsubscribe) host._authorizationUnsubscribe(owner, listener); }
        };
    }
    const instances = new WeakMap();
    function instance(owner, bot) {
        if (!owner || !bot || typeof bot.start !== 'function' || typeof bot.stop !== 'function') throw new TypeError('instance requires an owner and start/stop worker');
        const previous = instances.get(owner); if (previous) { if (previous.bot !== bot) throw new Error('One instance binding per plugin'); return previous.run; }
        let state = 'stopped', epoch = 0, sequence = 0, permitSequence = 0, deadline = 0, wallDeadline = 0, startDeadline = 0, nextReport = 0, retryAttempt = 0, reason = '';
        let runId, leaseId, entered = false, reportPending = false, startPromise, resolveStart, closed = false;
        let releasePendingUntil = 0;
        let finitePending = false;
        function running() { return state === 'running' || state === 'reconnecting'; }
        function expired() { return host._authorizationNow() >= deadline || Date.now() >= wallDeadline || (host._authorizationExpired && host._authorizationExpired(owner)); }
        function setDeadline(value) { if (host._authorizationSetDeadline) host._authorizationSetDeadline(owner, value); }
        const transport = client(owner, { pulse, tick, stop: reason => stop(reason) });
        function applyPermit(response, permit) {
            if (permit.run_id !== runId || typeof permit.lease_id !== 'string' || !permit.lease_id || (leaseId && permit.lease_id !== leaseId)) invalid();
            const revision = number(permit, 'permit_sequence');
            let nextDeadline = Math.min(response.deadline, response.deadline + (number(permit, 'permit_expires_at') - response.claims.exp) * 1000);
            if (revision <= 0 || revision < permitSequence || nextDeadline <= host._authorizationNow()) invalid();
            if (revision === permitSequence && permitSequence) nextDeadline = Math.min(deadline, nextDeadline);
            let nextWall = number(permit, 'permit_expires_at') * 1000;
            if (revision === permitSequence && permitSequence) nextWall = Math.min(wallDeadline, nextWall);
            permitSequence = revision; leaseId = permit.lease_id; deadline = nextDeadline; wallDeadline = nextWall;
            setDeadline(state === 'starting' ? Math.min(startDeadline, deadline) : deadline);
        }
        function start() {
            if (closed) return Promise.resolve({ started: false, reason: 'closed' });
            if (state === 'stopping') return Promise.resolve({ started: false, reason: 'stopping' });
            if (state === 'starting') return startPromise;
            if (running() && !expired()) return Promise.resolve({ started: true, reason: 'already_running' });
            if (running()) stop('permit_expired');
            const generation = ++epoch; state = 'starting'; entered = false; reportPending = false; retryAttempt = 0; reason = '';
            runId = host._authorizationNonce ? host._authorizationNonce() : ''; leaseId = undefined; permitSequence = 0;
            startDeadline = host._authorizationNow ? host._authorizationNow() + 30000 : 0;
            setDeadline(startDeadline);
            startPromise = new Promise(resolve => { resolveStart = resolve; });
            releasePendingUntil = Math.max(releasePendingUntil, Math.floor(Date.now() / 1000) + 300);
            transport.request('acquire', { run_id: runId, sequence: ++sequence }, runId).then(response => {
                if (generation !== epoch || state !== 'starting') return;
                if (!response.result.allowed) { stop(response.result.code || 'denied'); return; }
                try {
                    applyPermit(response, response.result);
                    if (host._authorizationNow() >= startDeadline) throw new Error('startup_timeout');
                    entered = true;
                    const cancellation = Object.freeze({ get cancelled() { return generation !== epoch || expired(); } });
                    Promise.resolve(bot.start(cancellation)).then(ok => {
                        if (generation !== epoch || state !== 'starting') return;
                        if (ok === false || host._authorizationNow() >= startDeadline || expired()) { stop('startup_failed'); return; }
                        state = 'running'; nextReport = 0; reason = 'started'; setDeadline(deadline);
                        const resolve = resolveStart; resolveStart = undefined; resolve({ started: true, reason: 'started' }); report();
                    }, () => { if (generation === epoch) stop('startup_failed'); });
                } catch (error) { stop('startup_failed'); }
            }, () => { if (generation === epoch) stop('authorization_unavailable'); });
            return startPromise;
        }
        function stop(detail = 'stopped') {
            if (state === 'stopped' || state === 'stopping') return;
            ++epoch; state = 'stopping'; reason = detail; reportPending = false; finitePending = false; transport.cancel(reason);
            if (runId) transport.request('cancel', Object.assign({ run_id: runId, sequence: ++sequence }, leaseId ? { lease_id: leaseId } : {}), runId).catch(() => {});
            transport.stop(); deadline = 0;
            if (resolveStart) { resolveStart({ started: false, reason }); resolveStart = undefined; }
            const cleanup = entered; entered = false;
            try { if (cleanup) { const result = bot.stop(reason); if (result && typeof result.catch === 'function') result.catch(() => {}); } }
            catch (ignored) {} finally { state = 'stopped'; }
        }
        function report() {
            if (!running() || reportPending) return;
            if (expired()) { stop('permit_expired'); return; }
            const generation = epoch; reportPending = true; nextReport = Math.max(host._authorizationNow() + 1000, Math.min(host._authorizationNow() + 20000, deadline - 10000));
            releasePendingUntil = Math.max(releasePendingUntil, Math.floor(Date.now() / 1000) + 300);
            transport.request('report', { sequence: ++sequence, complete: true, runs: [{ run_id: runId, lease_id: leaseId, ack_sequence: permitSequence, state: 'running' }] }).then(response => {
                if (generation !== epoch || !running()) return; reportPending = false;
                if (expired()) { stop('permit_expired'); return; }
                try {
                    if (response.result.allowed === false) { stop(response.result.code || 'denied'); return; }
                    const runs = response.result.runs; if (!Array.isArray(runs) || runs.length !== 1) invalid();
                    if (!runs[0].allowed) { stop(runs[0].code || 'denied'); return; }
                    applyPermit(response, runs[0]);
                    state = 'running'; reason = ''; retryAttempt = 0;
                } catch (error) { stop('invalid_permit'); }
            }, () => { if (generation === epoch && running()) {
                reportPending = false;
                if (expired()) { stop('permit_expired'); return; }
                state = 'reconnecting'; reason = 'authorization_unavailable';
                const cap = Math.min(15000, 1000 * Math.pow(2, Math.min(retryAttempt++, 4)));
                nextReport = host._authorizationNow() + cap / 2 + Math.random() * cap / 2;
            } });
        }
        function pulse() {
            const now = host._authorizationNow();
            if (state === 'starting' && now >= startDeadline) { stop('startup_timeout'); return; }
            if (!running()) return;
            if (expired()) { stop('permit_expired'); return; }
            if (now >= nextReport && !reportPending) report();
        }
        function tick(tick) { if (!running() || expired()) return; try { if (bot.tick) bot.tick(tick); } catch (error) { stop('callback_failed'); } }
        const run = Object.freeze({ start, stop, complete: () => stop('completed'),
            get status() { return state; }, get reason() { return reason; }, get active() { return running() && !expired(); },
            get releasePendingUntil() { return releasePendingUntil > Math.floor(Date.now() / 1000) ? releasePendingUntil : 0; },
            async run(task) {
                if (finitePending) return { started: false, reason: 'task_already_running' };
                finitePending = true; const admission = start(), generation = epoch;
                const result = await admission;
                if (!result.started || generation !== epoch || !run.active) {
                    if (generation === epoch) finitePending = false;
                    return result.started ? { started: false, reason: 'cancelled' } : result;
                }
                try { await task(Object.freeze({ get cancelled() { return generation !== epoch || expired(); } })); }
                catch (error) { if (generation === epoch) stop('task_failed'); return { started: false, reason: 'task_failed' }; }
                finally { if (generation === epoch) stop('completed'); }
                return result;
            },
            controls(panel) {
                panel.label('Status', state).label('Reason', reason).button('Start', 0x415a0001).button('Stop', 0x415a0002);
                if (!run.active && run.releasePendingUntil) panel.wrapped('A previous permit may reserve the slot for up to five minutes.');
            },
            control(action) { if (action === 0x415a0001) { start(); return true; } if (action === 0x415a0002) { stop(); return true; } return false; },
            panel(id = 'run', title = 'Run') { return { id, title, build: panel => run.controls(panel), onAction: action => run.control(action) }; },
            close() { stop(); closed = true; transport.close(); instances.delete(owner); }
        });
        instances.set(owner, { bot, run }); return run;
    }
    function entitlements(owner) {
        let current = Object.freeze({ entries: Object.freeze([]), publicClient: Object.freeze({}), complete: false, freshness: 'unavailable', issuedAt: 0, validUntil: 0 });
        let deadline = 0, listing, nextRefresh = 0, closed = false, cacheRevision = 0;
        const listeners = new Set();
        const transport = client(owner, { pulse() {
            if (current.complete && current.freshness === 'current' && host._authorizationNow() >= deadline) { current = Object.freeze(Object.assign({}, current, { freshness: 'stale' })); changed(); }
            if (listeners.size && !listing && host._authorizationNow() >= nextRefresh) list();
        }, stop() { invalidate(); } });
        function changed() { for (const listener of Array.from(listeners)) try { listener(current); } catch (ignored) {} }
        function invalidate() { cacheRevision++; deadline = 0; current = Object.freeze(Object.assign({}, current, { entries: Object.freeze([]), complete: false, freshness: 'unavailable' })); changed(); }
        function stale() { if (current.complete) { deadline = 0; current = Object.freeze(Object.assign({}, current, { freshness: 'stale' })); changed(); } }
        async function check(key) {
            if (closed) return Object.freeze({ decision: 'unknown', reason: 'closed' });
            try {
                const response = await transport.request('check', key === undefined ? {} : { plugin_id: key });
                const entry = response.result.entitlement;
                if (!response.result.allowed || (entry && entry.entitled === false)) {
                    invalidate();
                }
                return Object.freeze({ decision: entry && entry.entitled === true ? 'granted' : entry && entry.entitled === false || response.result.code === 'not-entitled' ? 'denied' : 'unknown',
                    reason: response.result.code || '', scopeError: response.result.code === 'scope-denied', entitlement: entry, verifiedAt: Math.floor(Date.now() / 1000), validUntil: response.claims.exp });
            } catch (error) { stale(); return Object.freeze({ decision: 'unknown', reason: 'authorization_unavailable' }); }
        }
        function list() {
            if (closed) return Promise.resolve(current); if (listing) return listing;
            const revision = cacheRevision;
            listing = (async () => {
                const entries = [], keys = new Set(); let cursor, snapshotId, total, earliest = Infinity;
                for (let page = 0; page < 64; page++) {
                    const response = await transport.request('list', cursor ? { cursor } : {}), result = response.result;
                    if (closed || revision !== cacheRevision) invalid();
                    if (!result.allowed) { invalidate(); invalid(); }
                    if (!Array.isArray(result.entries) || (snapshotId && snapshotId !== result.snapshot_id)) invalid();
                    const count = number(result, 'total_entries');
                    if (count < 0 || count > 10000 || (total !== undefined && total !== count) || number(result, 'offset') !== entries.length) invalid();
                    total = count; snapshotId = string(result, 'snapshot_id');
                    for (const entry of result.entries) { const key = string(object(entry), 'key'); if (keys.has(key)) invalid(); keys.add(key); entries.push(entry); }
                    if (entries.length > total) invalid();
                    earliest = Math.min(earliest, response.deadline);
                    if (result.complete === true) {
                        if (result.next_cursor || entries.length !== total) invalid(); deadline = earliest;
                        current = Object.freeze({ entries: Object.freeze(entries), publicClient: result.public_client, snapshotId,
                            issuedAt: number(result, 'issued_at'), validUntil: number(result, 'valid_until'), complete: true, freshness: 'current' });
                        changed(); return current;
                    }
                    cursor = string(result, 'next_cursor'); if (!cursor || !result.entries.length || entries.length >= total) invalid();
                }
                invalid();
            })().catch(() => { stale(); return current; }).finally(() => { listing = undefined; nextRefresh = host._authorizationNow() + 60000; });
            return listing;
        }
        return Object.freeze({ check, list, snapshot: () => {
                if (current.complete && current.freshness === 'current' && host._authorizationNow() >= deadline) current = Object.freeze(Object.assign({}, current, { freshness: 'stale' }));
                return current;
            },
            onChanged(listener) { if (typeof listener !== 'function') throw new TypeError('listener'); listeners.add(listener); list(); return () => listeners.delete(listener); },
            close() { closed = true; transport.close(); invalidate(); listeners.clear(); }
        });
    }
    const facade = Object.create(host);
    Object.defineProperties(facade, { instance: { value: instance, enumerable: true }, entitlements: { value: entitlements, enumerable: true } });
    return facade;
}
