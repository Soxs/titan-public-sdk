# Managed plugin instances and entitlements

SDK 160 introduces managed native instances through an optional host capability.
Java SDK 0.1.99 and the bundled JavaScript helper provide equivalent interfaces;
see [their examples and packaging instructions](plugin_authorization_java_js.md).
Existing native ABI records and the supported SDK floor (154) do not change.
SDK 171 adds confirmed completion and an optional managed-lifecycle interface
so disabled native plugins can finish cleanup without receiving ordinary callbacks.

## Bind once, start and stop

```cpp
#include <titan/authorization.h>

// In the plugin constructor; the shared_ptr keeps the bot alive through shutdown.
run = titan::instance(*this, std::make_shared<MyBot>());
run.controls();  // Optional standard Start/Stop/status panel.

// Or wire existing button handlers:
run.start();
run.stop();
```

`MyBot` supplies `start()`, optional `tick()`, and `stop()`. A bool-returning
`start()` returning false is a startup failure. Different method names can be
adapted with `titan::authorization::Bot{startCallback, tickCallback, stopCallback}`.
Callbacks and captured objects must remain valid until shutdown completes.

Binding and opening the UI consume no capacity. Start first registers a local
Starting run and obtains a verified server permit, then calls the bot. Repeated
Start coalesces. Stop also cancels pending admission, is idempotent, and stops
renewal before cleanup. Ignoring the Start result is safe. To implement custom
messages, inspect its shared future after it becomes ready; never block the
MainLoop with `get()` while a request is pending.

When capacity is full, an explicit Start waits in Starting and displays when it
will check again. It retries with fresh online approval at the server's advisory
retry time (at most two seconds plus SDK jitter on the updated server). Stop
cancels this wait. Waiting consumes no slot and does not invoke bot startup or
use its 30-second startup budget. Existing integration code stays unchanged.

Ticks are forwarded only during valid authorization. Startup must finish within
30 seconds; use ticks or a managed worker for ongoing work. A finite job can use:

```cpp
run.run([](std::stop_token cancellation) {
    while (!cancellation.stop_requested() && hasMoreWork()) {
        doOneBoundedUnitOfWork();
    }
});
```

Completion and exceptions stop that run automatically. Persistent bots call Stop
when their work is finished. Cancellation is cooperative: work must check its
token and use bounded waits. Unmanaged threads cannot be safely terminated by the
SDK. Native plugin worker leases retain the module until workers can exit safely.
Managed callback exceptions, disable, unload, and authorization loss stop renewal.
The runtime retains the scope even if a temporary SDK handle is destroyed.

The [complete native example](../examples/authorization/managed_bot.cpp)
includes standard controls and an entitlement list panel. Build it with the normal
`titan_add_plugin` helper and `titan_sdk`; the SDK links its CNG verifier into the
plugin automatically. There are no customer IDs, credentials, or listing slugs to
configure in plugin code.

## Entitlement information

```cpp
auto access = titan::entitlements(*this);
auto mine = access.check();
auto dependency = access.check("plugin.some_dependency");
auto library = access.list();
auto cached = access.snapshot();
auto unsubscribe = access.onChanged([](const auto& snapshot) {
    // Update UI using this verified, scoped snapshot.
});
```

Reads cover this customer's plugins from the calling publisher, approved
versioned dependencies, and public client tier/features. They do not expose
unrelated purchases or grant/billing identifiers. `check()` returns Entitled,
NotEntitled, Unavailable, or ScopeDenied; network failure is not a denial.
`public.clienttier`, `public.clientfeatures`, and approved `feature.*` keys support
client feature messages. Logical ownership and runtime eligibility are separate.

Entries include identity, display name, access sources, effective expiry, the
next scheduled change, included instances, purchased extras and their expiries,
and timestamped active/retiring usage. A missing included/total count means
unlimited. Displayed free capacity never grants permission to start.

`list()` handles pagination against one immutable snapshot. Partial, reordered,
mixed, or failed pages cannot become a complete empty library. The previous
snapshot remains available for display, marked stale when its proof expires.
`accessExpiresAt` describes access; `verifiedAt` and `validUntil` describe the
proof. No scheduled access expiry does not mean access can never be revoked.
Subscriptions are removed when the owner is stopped or unloaded.

## Outages and reservation timing

New run permits last at most two minutes and may end sooner for scheduled entitlement
or capacity changes. Reports run every 20 seconds, immediately on changes, and
earlier when a short deadline requires it. The controller multiplexes bounded work
over its gateway socket and reserves a worker for terminal cancellations.
Client MainLoop liveness is independent of game ticks and rendering.
Entitlement read proofs retain a separate five-minute maximum. Previously issued
five-minute run permits and their reservations remain valid through their original
deadlines during rollout; they are never shortened to free capacity early.

During a short outage existing runs show Reconnecting and retain their bot state.
A verified renewal before expiry resumes Running without repeating startup.
New starts require online approval. At the verified deadline managed callbacks
close and cancellation is requested. A late reply cannot restart the bot: the
user must explicitly Start again. The watchdog uses a suspend-aware clock and
does not depend on the plugin callback loop.

`authorization_expired` means that no verified renewal arrived before the last
permit deadline; it does not mean the instance limit was reached. Native SDK
renewal diagnostics record the failure category, recovery, and remaining permit
time without logging credentials or proofs. The terminal reason stays unchanged.
The controller preserves recognized runtime-context errors such as
`runtime-build-denied` so a rejected artifact identity can be distinguished from
a transport outage. It also forwards the HTTP status without response bodies;
native diagnostics identify service-unavailable (503), rate-limit (429), and
authentication (401/403) failures. Unsigned errors never replace a signed
authorization decision.

Publishing a newer plugin build must not invalidate refreshes for an older loaded
build that remains approved, published, and unretired. The website must bind the
refreshed context to the exact matching artifact and its approved dependencies.
This server change does not require a plugin rebuild; the additional native
diagnostics require rebuilding the plugin with the updated SDK.

Routine website maintenance must keep the signed runtime authorization routes
available, including credential refresh and gateway verification. A maintenance
503 can otherwise consume the entire two-minute permit window and stop active
runs even when capacity and entitlements are valid. The website deployment
runbook distinguishes this from a full shutdown for incompatible runtime/schema
changes. A real backend outage or full shutdown can still expire a run; neither
the controller nor the plugin extends a permit locally.

Changing the signed-in Titan account closes managed authorization in games opened
by the previous account and clears entitlement snapshots. Reopen those game tabs
under the new account. Switching back does not revive old Start requests. A
temporary gateway disconnect does not trigger this account-change fence.

Stop works offline: it immediately stops local renewal and requests server
retirement. The server fences further issuance when it receives that request;
an offline stop cannot immediately fence requests already in transit.
After startup callbacks, managed workers and successful cleanup have all finished,
the SDK sends a separate `cancel` with `payload.completed: true`. The server frees
capacity immediately in that transaction and retains the run's tombstone so a
delayed acquire or report cannot revive it. A signed `released: true` receipt with
`held_until: 0` clears only that run's displayed hold. Late older receipts cannot
restore a released hold or clear a newer run's reservation.

A cleanup exception, unfinished worker, uncertain disconnect or lost completion
keeps the bounded reservation until confirmation or the highest permit already
issued expires. Terminal delivery retries for at most 330 seconds, including
after disable/unload, without enabling new work. A plain socket/IPC timeout never
asserts completion. The controller can confirm completion when the retained OS
process handle is signalled, and drains those requests before stopping its gateway
or discarding sign-in credentials. Routine credential refresh can rebind completion
to the same verified account/device; a different sign-in cannot adopt it.

`releasePendingUntil` is the latest signed reservation deadline still observed for
unreleased runs; submitting requests never extends it. A lost renewal can leave a
later server deadline unknown. Online admission always decides availability.

## Server and rollout contract

The website owns durable account/canonical-plugin reservations and capacity
contributions. The gateway forwards authenticated, correlated requests; it does not
own an in-memory counter. Complete reports reconcile existing runs and never
allocate unknown runs. Cancellation tombstones fence delayed admissions. One
unacknowledged successor is allowed per acknowledged permit. Expired reservations
stop consuming capacity by timestamp, without waiting for a cleanup job.

For an existing installation, deploy the additive `completed_at` migration and
website first, then the gateway. Existing clients/SDKs remain accepted: legacy
`cancel` without `completed: true`, stopping reports, omitted runs and socket loss
retain their existing expiry behavior. No SDK floor, client minimum version or
feature-flag change is required for this server rollout. Then deploy the updated
controller/client and rebuild managed plugins with the updated SDKs to opt into
immediate completion release. Old SDKs cannot confirm settled cleanup; their
ordinary Stop continues to use expiry until rebuilt. The updated controller can
confirm a terminated process even when its loaded plugin used an older SDK.

Fresh installations ship with plugin authorization disabled. Only approved
builds marked compatible may enable finite listing limits. Run the website's
real SQL concurrency test against the production database engine before enabling
limits; SQLite tests do not establish row-lock behavior across workers.

Local validation must set `TITAN_AUTO_UPLOAD_DLL=OFF` and
`TITAN_AUTO_PUBLISH_DLL=OFF`. A successful local build does not deploy the feature.
Signed checks add independent verification in each plugin, but cannot control
execution in a completely patched client and plugin.
