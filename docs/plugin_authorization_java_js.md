# Managed plugin instances in Java and JavaScript

Bind an existing worker once, and connect ordinary Start/Stop buttons to its run.
Enabling the plugin only loads its UI. The SDK requests a slot when Start is clicked,
verifies the website's signed permit inside the plugin, and then invokes startup.
Java's complete example is `ManagedInstanceSamplePlugin`; JavaScript's is
`js/authorization/example.ts`.

```typescript
const run = titan.instance(this, bot); // bot.start(), bot.tick(tick), bot.stop(reason)
const result = await run.start();      // result.started / result.reason
run.stop();                           // safe during pending admission or startup
```

Java uses `Instance.bind(this, bot)`, `run.start().thenAccept(...)`, `run.stop()`,
and `run.status()`. `Instance.Bot.start()` returns false to reject partial startup.
An existing worker needs no interface change: bind
`Instance.adapt(bot::start, bot::tick, reason -> bot.stop())`. Use `adaptChecked`
instead when its start method returns a boolean that must be preserved.
Use `run.controls(panel)` to draw standard status/reason and Start/Stop buttons,
and forward panel actions to `run.control(actionId)`. JavaScript can attach an entire
ready-made panel with `panels = [run.panel()]`.
Keep these methods and lifecycle callbacks on the plugin/game thread. A background
task completing a Java stage is marshalled back before cleanup. Do not block a
callback while waiting for its own Start future.

The SDK owns request correlation, reports every 20 seconds, acknowledgement,
permit expiry, and cleanup after disable/unload, failed startup, or tick exceptions.
Startup has a 30-second deadline after approval. If capacity is full, an explicit
Start stays Starting and waits until the signed server retry time to check again.
The wait is cancellable with Stop, consumes no capacity, and does not invoke bot
startup or count against that startup deadline. The retry time is advisory because
another running instance may renew. Existing permits remain usable during a temporary
outage only until their signed expiry, at most two minutes for newly issued permits.
Previously issued five-minute permits retain their original expiry during rollout.
Entitlement read proofs keep their separate five-minute maximum. New starts fail closed
when transport, verification, deployment flags, or approved build identity is missing.
Calling Start again while starting/running does not request another slot.

During a renewal outage the status becomes `reconnecting`, while the existing
verified permit still allows work. A five-second report timeout and bounded jitter
retry within 15 seconds avoid waiting for the next ordinary reporting interval.
Short permits are renewed earlier. A late response cannot revive an expired run.
The host independently fences admission/renewal by a suspend-aware monotonic clock;
Java also uses a plugin-owned watchdog and signed wall-clock expiry cap. Watchdogs
signal cancellation and queue cleanup on the owner thread; they never execute user
cleanup on a background thread.

Changing the signed-in Titan account stops managed work and clears entitlement
snapshots in games opened by the previous account. Reopen those game tabs under
the new account; switching back does not revive old Start requests. Temporary
gateway outages do not invalidate the account's game session.

For finite work use `run.run(async cancellation => { ... })` in JavaScript, or
`run.run(cancellation -> completionStage)` in Java. Task settlement completes the
run. Java's returned stage reports startup; the worker's stage defines its lifetime.
JavaScript's returned Promise also awaits the task. Owned workers must observe the
cancellation signal and finish promptly; stop also cancels Java tasks implementing
`Future`. An event-driven worker whose lifetime cannot be inferred calls
`run.complete()` when finished. Signal worker shutdown in `bot.stop` and avoid
starting unrelated detached work. JavaScript async startup receives the same
cancellation signal and must check it after each await before side effects.

Stop retires the run before invoking user cleanup, so a thrown or blocked stop hook
cannot keep renewing it. The server retains a reservation until its last issued
permit expires, including a permit whose reply was lost. `releasePendingUntil`
(a Java getter, a JavaScript property) is the latest signed Unix-seconds reservation
deadline observed, including a verified Stop acknowledgement. Submitting requests
does not extend it; denied admission does not fabricate a new hold. Lost renewal
or Stop replies can leave a later server deadline unknown, so this is the last
confirmed reservation, not a promise of availability. A subsequent Start keeps
the old confirmed hold visible while waiting for fresh online admission.
The runtime does not forcibly kill arbitrary threads; code that ignores cancellation
can continue executing, but cannot obtain new authorization through that retired run.

## Reading the customer's entitlements

```typescript
const access = titan.entitlements(this);
const own = await access.check();
const dependency = await access.check("plugin.approved_dependency");
const library = await access.list();
const unsubscribe = access.onChanged(snapshot => updateUi(snapshot));
```

Java exposes equivalent methods through `Entitlements.forPlugin(this)`;
`onChanged` returns an `AutoCloseable` subscription. Check results distinguish
`granted`, `denied`, and `unknown`; transport failures, expired proofs, and invalid
signatures are unknown. A successful reason is the empty string. Signed scope
denials are not evidence that the customer lacks some unrelated purchase.
They have Java decision `SCOPE_DENIED` or JavaScript `scopeError: true`.
`verifiedAt` records receipt verification time. Public checks use
`public.clienttier`, `public.clientfeatures`, or an approved public `feature.*` key;
their entitlement `value` contains the tier, feature list, or boolean respectively.
Both runnable samples include a **Refresh library** button, cached freshness label,
and entitlement rows alongside their independent Start/Stop controls.

Lists include only the calling publisher's approved plugins, declared approved
dependencies, and public client tier/features for the current customer. They expose
no arbitrary customer lookup. `list()` combines pages of one signed snapshot;
`snapshot()` reads the last cache, with current/stale/unavailable freshness and a
complete flag. Subscriptions refresh every minute and notify freshness changes.
Every page must agree on snapshot ID, total count, exact offset, and unique keys.
A fresh signed denial clears an older cached approval and fences older in-flight
lists; refresh/transport failures retain display data marked stale.
An unavailable snapshot is different from a verified empty list. Access and ownership
are separate fields; null instance allowances mean unlimited, while zero means zero.
Rows also include display name, access mode, delivery/runtime eligibility, instance
usage (active and retiring), extra allowance expiries, and the next access change.
Reading any entitlement never grants a concurrent run or consumes a slot.

## Packaging

Java SDK 0.1.90 and `net.titan.dev` 0.1.2 bundle `net.titan:titan-authorization`
into each plugin and relocate its classes to a namespace derived from the artifact.
This avoids sharing another plugin's verifier through the embedded class loader.
Keep the API dependency compile-only, and apply the convention plugin's normal JAR
task; do not replace it with a thin JAR that omits the authorization module. The
module uses Java 11 JCA RSA/SHA-256 and has no runtime dependency beyond the API.

For JavaScript, run the bundled `js/authorization/build.py` on the compiled
plugin source before upload or bytecode compilation. Its pinned pure-JavaScript
verifier and public keys stay inside that plugin artifact. Both SDKs validate the
signed caller certificate, locally declared plugin identity, artifact/process/run
bindings, request nonce, exact result hash, algorithm, key, purpose, and expiry.
Old hosts lacking the optional transport return unavailable rather than success.

Plugin-local verification protects against a forged host boolean and replayed
responses. It cannot make modified plugin code enforce checks that an attacker
removes, or prevent arbitrary code in the same process from bypassing its own logic.
Server-side acquisition and reservation limits remain authoritative.
