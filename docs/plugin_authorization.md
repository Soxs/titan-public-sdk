# Managed plugin instances and entitlements

SDK 160 introduces managed native instances through an optional host capability.
Java SDK 0.1.90 and the bundled JavaScript helper provide equivalent interfaces;
see [their examples and packaging instructions](plugin_authorization_java_js.md).
Existing native ABI records and the supported SDK floor (154) do not change.

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

Permits last at most five minutes and may end sooner for scheduled entitlement
or capacity changes. Reports run every 20 seconds, immediately on changes, and
earlier when a short deadline requires it. The controller batches work over its
gateway socket. Client MainLoop liveness is independent of game ticks and rendering.

During a short outage existing runs show Reconnecting and retain their bot state.
A verified renewal before expiry resumes Running without repeating startup.
New starts require online approval. At the verified deadline managed callbacks
close and cancellation is requested. A late reply cannot restart the bot: the
user must explicitly Start again. The watchdog uses a suspend-aware clock and
does not depend on the plugin callback loop.

Stop works offline. Capacity can remain reserved until the highest permit already
issued expires, including a reply lost in transit. `releasePendingUntil` is a
conservative upper bound, not a promise of immediate reuse. The standard panel
shows the remaining bound. A stopped plugin cannot continue renewal because of
a cleanup exception. This policy is separate from client access continuation.

## Server and rollout contract

The website owns durable account/canonical-plugin reservations and capacity
contributions. The gateway forwards authenticated, ordered requests; it does not
own an in-memory counter. Complete reports reconcile existing runs and never
allocate unknown runs. Cancellation tombstones fence delayed admissions. One
unacknowledged successor is allowed per acknowledged permit. Expired reservations
stop consuming capacity by timestamp, without waiting for a cleanup job.

Website changes ship with plugin authorization disabled. Deploy migrations and
website support, then gateway, controller/client, and SDK artifacts. Only approved
builds marked compatible may enable finite listing limits. Run the website's
real SQL concurrency test against the production database engine before enabling
limits; SQLite tests do not establish row-lock behavior across workers.

Local validation must set `TITAN_AUTO_UPLOAD_DLL=OFF` and
`TITAN_AUTO_PUBLISH_DLL=OFF`. A successful local build does not deploy the feature.
Signed checks add independent verification in each plugin, but cannot control
execution in a completely patched client and plugin.
