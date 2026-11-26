# Titan runtime provider SPI

`net.titan:titan-runtime-spi` is a Java 11 library with no external dependencies.
Its version follows the Titan Java SDK. Providers use it as `compileOnly`;
the embedded host supplies the SPI classes. It does not export Guice, Gson,
Titan's plugin interface, or a RuneLite implementation.

Implement `net.titan.runtime.spi.RuntimeProvider` and register its class in
`META-INF/services/net.titan.runtime.spi.RuntimeProvider`. Ship the provider and
its private dependencies under `java/compat/<provider-id>/*.jar`, alongside
`java/titan-java-embedded.jar`. There must be exactly one provider per directory.
Each directory has a separate dependency realm. Only JDK/platform classes and
the SPI are inherited; Titan's Guice and SDK classes are not visible. This is
dependency isolation, not a security sandbox.

The host calls optional `initialize(ProviderContext)` once before inspection or
creation, under the provider's context classloader. Its default delegates to
`initialize(Path)`, preserving older providers. The absolute storage path is
`~/.titanclient/provider-data/<provider-id>`; providers create it only as needed.
Tests and inspector processes can override the root with
`-Dtitan.java.provider.data.dir=<temporary-directory>`. Initialization and catalog
inspection must read local/cache data only. Providers may enqueue an asynchronous
startup refresh after capturing that local snapshot; user-requested refreshes
also run outside the host's lifecycle monitor. Neither refresh may alter the
active runtime's admission snapshot.
Providers that own background refresh work implement `closeAsync()` and complete
its stage only when that work has stopped. The default implementation wraps
synchronous `close()`. The host begins each provider's cancellation, awaits
every completion without blocking the EDT or game thread, then closes the
dependency realms after successful cleanup.

The optional provider-only `titan.fonts.custom.v1` service accepts exactly `{}`
through `HostServices.invoke`. It uses only the registry-bound provider storage
directory's `fonts` child; callers cannot supply a path. The nonblocking reply
is `{schemaVersion:1,status:"loading"|"available"|"unavailable",families:[...],diagnostic:"..."}`.
The host registers plain TTF/OTF files on its own background worker. Only
successfully registered AWT family names are returned (at most 64, each at most
1024 UTF-8 bytes); diagnostic text is bounded to 4096 bytes and replies to
128 KiB. An available empty list means no optional fonts, whereas unavailable
is an explicit failure. Clients may poll while loading from owned background
work and must not block a game/controller callback or the EDT waiting for it.
No typed SPI additions or native ABI changes are needed; older hosts report
unsupported. File work is bounded by 4096 entries, 64 files, 8 MiB per file and
32 MiB per provider. Existing AWT families are never replaced. Registration is
JVM-wide and irreversible through public JDK APIs: provider reload reuses its
first root/catalog; changes require a game restart. Closing a provider revokes
new service access without waiting for a stalled process-owned scan. That scan
retains only its original host-bound root and JDK data, without claiming
to revoke existing Font objects or family lookup from other Java code.

An entry-scoped host that supports `titan.plugins.enablePreference.v1` can
read `pluginEnablePreference(pluginId, loadGeneration)` for current entries in
its own provider and game tab. The selection starts from the admitted entry's
configured default and follows explicit changes. It survives failed startup.
Another tab can have a different selection. Retirement revokes the old identity,
including when a replacement uses the same entry ID.

On Windows, `titan.window.attention.capabilities.v1` accepts `{}` and returns
`{"schemaVersion":1,"taskbar":true}` (or false). The active, ready plugin host
may submit `titan.window.attention.request.v1` with `{}`; the passive provider
cannot. A `{"status":"queued"}` response acknowledges bounded asynchronous
admission only, while `{"status":"unavailable"}` means no request was queued.
`titan.window.front.request.v1` is the same, and asks the controller to bring the game's
window to the front, which Windows may refuse.
The controller checks the same selected game and selection revision before
one `FlashWindow(hwnd, TRUE)` call. It does not change selection or focus.
Queued requests expire after 500ms; disable, retirement and host shutdown drop
unstarted work, while an already-admitted OS attempt may finish. There is no
automatic retry or delivery promise. Older controllers, unselected games,
detached frames and Wine are unavailable. This service adds no ABI payload or
function-table changes and does not qualify tray notifications.

On Windows, `titan.window.frameFocus.v1` accepts `{}` and returns
`{"status":"available","focused":true}` or the corresponding false value.
Focus belongs to the selected game and includes its controller and embedded
sidebar. Home, another selected game, and a separate foreground dialog leave
that game unfocused. A detached game, a missing controller selection or a
focus transition can return `{"status":"unavailable"}`. The read needs no
game-thread callback and grants no window activation permission.

`ProviderContext` supplies `providerId()`, `runtimeGeneration()`,
`storageDirectory()` and `host()`. A provider owns this context across all its
plugin generations. Its strictly allowlisted host permits passive game reads,
owned snapshots and one input observation channel. It denies configuration,
panels, embedded windows, scheduling, lifecycle status, synthetic input
and consumption. Those operations retain their plugin-context ownership.
Qualified render callbacks additionally receive the bounded raster authority
described below; a provider cannot draw outside its actual native render pass.
Inspection receives a context with no live capabilities; a context does not
enable plugins or grant their delivery entitlement.

The native principal is separate from plugin descriptors. The internal
`@provider/<id>` token cannot be used as a plugin ID. Shutdown stops provider
reads and callbacks immediately, waits for successful plugin cleanup, then
awaits provider `closeAsync()`, drains admitted passive reads and provider-tick
callbacks, retires its native principal and closes the realm. Calls execute
outside the principal's short admission lock; a slow game read does not serialize
unrelated EDT geometry reads. Neither the provider tick pump nor its callbacks
hold the embedded runtime monitor. Shutdown reports pending while calls drain;
normal in-flight work is not a cleanup failure.
Observation close remains available during orderly cleanup. Pending or failed
cleanup prevents runtime replacement; failed cleanup must not release a realm
that still owns work.

The controller can open settings for a loaded provider instance after successful
load and between completed lifecycle transitions. The optional
`PluginInstance.onConfigurationRequest(requestJson, dispatchIfCurrent)` returns
a copied snapshot or queues editor work and returns immediately. A provider
can return null when it has no editor. Use the supplied dispatcher on the
destination thread for deferred reads and writes. It checks the owning load and
lifecycle revision, restores host attribution, and counts admitted work through
retirement. Changing enablement revokes the previous dispatcher. Opening
settings for a disabled instance leaves ordinary event admission disabled.
When an enable attempt fails, a completed rollback can restore this settings
entry point. Startup retains its failure status. Failed revocation or incomplete
provider or UI cleanup keeps the entry point closed.

The optional `onClientTick()` callback runs once per actual native client tick,
before per-plugin client-tick callbacks, under the provider's classloader.
It must not block and does not establish server-tick or tick-end semantics.
The host visits all providers before reporting a callback failure. Providers
must still fence each queued plugin callback by its activation; the shared
context does not authorize plugin actions.

Providers can opt into shared observational delivery with `providerEventTypes()`.
The host snapshots this declaration once after initialization. The base eligible names
are `clientTick`, `gameTick`, `gameStateChanged` and `varbitChanged`; the separately
qualified optional `clientCycleEnd`, `animationChanged` and `overheadTextChanged` are also accepted. Unsupported
declarations fail explicitly. `titan.events.provider.capabilities.v1` returns
`{schemaVersion:1,types:[...]}` only for an admitted native principal with the
three ingress methods and the interest-publication transport installed. The host
intersects the declared base set with that initial native capability snapshot.
Capability lists allow up to 64 unique nonempty names of at most 64 UTF-8 bytes;
unknown future names are ignored and do not grant delivery or mutation authority.
Missing capabilities retain per-plugin routing for older providers. A provider
whose shared implementation requires all routes must reject live activation when
that full capability is absent, while keeping offline catalog inspection usable.

Qualified client ticks arrive through the existing `onClientTick()`. Game ticks
use `onGameTick(int)` and the remaining events use `onEvent(type, objectJson)`.
Native ingress occurs once at accepted fanout, after observational FIFO admission,
before Java adapters. The corresponding per-plugin callbacks are suppressed only
for qualified opted-in routes. There is no tick-value or payload deduplication and
no retry/fallback after a shared callback fails. The host still visits other
providers after one fails. Shared callbacks run outside the embedded runtime
monitor under the provider loader with no selected plugin scope. Providers must
attribute and fence each subscriber individually; one shared bus can then apply
framework priority globally. Shutdown drains all provider callback kinds before
closing a realm. Inspection invokes none of these live callbacks.

The state-change payload is `{oldState,newState,tickCount}` and the varbit payload
is `{varbitId,oldValue,newValue,gameTick}`. Payloads are strict JSON objects bounded
to 1 MiB of UTF-8. This ingress is observational: it has no mutation reply and
does not qualify mutable input/menu/sound callbacks, focus changes,
precise item-container mutation phases or an inferred tick-end hook.

An optional `statChanged` observation carries `{schemaVersion:1,skillId,xp,level,boostedLevel}`,
with four signed 32-bit integer fields. A source emits it synchronously on the game
thread at each skill update, using the XP, real level and boosted level at that update.
Repeated identical updates retain their order. The Windows host currently lacks
this observation point and omits the name from its capability list.

For sampled delivery, `HostServices.supports("titan.skills.snapshot.v1")` checks
the skill arrays and the game-loop and login-state hooks. On the game thread,
`pollSkillChanges` accepts a reusable 304-byte, little-endian writable direct
buffer. The first 16 bytes track continuity. The remaining bytes hold 24
triples of signed XP, real level and boosted level. Clear the first eight bytes
to start an observation. The result is a bitmask of changed skill indices,
zero for a baseline or unchanged sample, or -1 for an unavailable observation.
Each observer owns its buffer. A missed client tick or a new game session
seeds the next comparison.

The optional `worldChanged` callback carries `{}`. It runs on the game thread
when the active game completes world selection, including repeated selections
of the same world.

On Windows the optional `itemContainerChanged` source samples inventory (93),
equipment (94) and bank (95) before the provider game tick. Its object payload
has `schemaVersion` 1, `containerId` and the observed positive decimal-string
`incarnation`. A provider can read the current dense contents through
`titan.items.container.v1`. A present changed sample compares all slots and
quantities. Loss of continuity requires a fresh baseline.

The optional `titan.grandExchange.changes.v1` capability supplies
`pollGrandExchangeChanges` on the game thread. It takes an observer-owned
280-byte writable little-endian direct buffer and returns an eight-bit mask
of changed offer slots. Zero means unchanged, -1 means unavailable and -2
marks a new baseline. The first 24 bytes are opaque. Slot layout and reset
rules are specified by `HostServices.pollGrandExchangeChanges`.

A provider can call `titan.events.provider.subscriptions.v1` with a positive
decimal-string `revision` and a `types` array containing its current optional
event subscriptions. An empty list releases those subscriptions. The host
intersects the latest revision with the provider's declared types, enabled
owners and installed native sources. Older revisions cannot restore a listener.

The optional `clientPaint` event carries `{}` once after each complete native game
draw, on the game thread which drew it. The scene and every widget of that frame
have been drawn and the game's logic has not run again. Request it only while
there is something to paint. Inside it a provider may create, publish and reuse
raster surfaces. `rasterDraw` returns false and HUD operations throw, because the
native frame belongs to the render callback. Pixels published there stay drawable
by later render callbacks until they are replaced, with every plugin which
contributed to them checked again for each draw. While they are being replaced, a
draw must satisfy the contributors of both. `titan.render.viewport.v1` describes
the scene of the frame just drawn. The event is not offered when the game's final
draw is not hooked, and not delivered when that draw is not on the game thread.

Query `titan.render.paintReads.capabilities.v1` with `{}` for a boolean. When true,
`titan.render.paintReads.v1` accepts `{}` inside a client paint and returns
`{schemaVersion:1,reads:{operation:reply}}`, at most 64 KiB. Each reply is what that
passive operation answers for the frame just drawn: the five camera values,
`titan.render.viewport.v1`, `titan.widgets.topLevel.v1`, and world 0's
`titan.worldViews.snapshot.v1` and `titan.scene.identity.v1`. The host leaves out a
read it cannot make. Ask the operation itself for anything left out. Outside a
client paint the request throws.

The optional `varClientIntChanged` and `varClientStrChanged` observations each carry
`{schemaVersion:1,id}` for a nonnegative game varc ID. Both are synchronous game-thread
events at successful return from their respective typed setter on the active game
VarClient object. Sources qualify independently. An unchanged value still produces
an event. Long writes have no corresponding event. The per-type subscriber interest
lets native code skip the Java call when the last listener leaves.

`clientCycleEnd` is a separate optional observation of a qualified completed native
outer client cycle, not a server tick or an alias of `clientTick`. Its passive
capability operation `titan.events.clientCycleEnd.capabilities.v1` accepts only `{}`
and returns exactly `{schemaVersion:1,sourceQualified:boolean,observedCompletion:boolean}`.
Requests and responses are bounded to 4096 UTF-8 bytes; booleans and the lexical
integer version are strict, and an observed completion requires a qualified
source. `supports` requires `sourceQualified` but may be true before the first
completion. The general event capability includes `clientCycleEnd` only after
the first qualified completion. Providers must retain their requested declaration
while `observedCompletion` is false rather than caching startup unavailability.

Native code detects the optional host entry `EmbeddedRuntime.providerClientCycleEnd()`
before advertising the source. It sends one qualification pulse even with no
current event interest. The registry refreshes pending cycle-end qualification
against both current capabilities before dispatch, publishes the new interest
mask, and retains qualification for later activations. Only a ready, enabled
owner creates provider delivery demand. The provider receives
`onEvent("clientCycleEnd", "{}")`; any nonempty or malformed payload is rejected.
There is no per-plugin fallback, payload deduplication, or generic
`HostServices.schedule` queue for this phase. Providers own their queue ordering
and must use each plugin's callback admission and activation fence. Like the other
shared ingress paths, shutdown drains in-flight completion callbacks before
closing the provider realm.

The host privately publishes aggregate qualified event demand by provider/native
generation and a positive monotonic revision. Bits 1, 2, 4 and 8 represent the
four base event names above; bit 16 represents qualified `clientCycleEnd` demand.
Only active owners whose startup succeeded contribute;
publication succeeds before readiness is reported. Disable/unload revokes demand
immediately, and an older delayed revision cannot restore it. Native masks avoid
shared event JNI calls while all owners are disabled. This is conservative
provider-level demand, so an enabled UI-only owner may keep the declared event
types active; it is not an exact subscriber census. The declaration creates no
lifecycle/item-container scanner demand and exposes no mutable interest setter
through the passive provider SPI. The existing client-tick service pump remains
available to older providers.

### Optional exact animation observations

`titan.events.animationChanged.capabilities.v1` accepts `{}` and returns a strict
JSON boolean. True requires the complete existing animation source to be installed
and active for the current native source epoch, exact actor identity observations,
and the optional monitor-free `EmbeddedRuntime.providerAnimationChanged(String)`
entrypoint. The existing general event capability list is unchanged. A provider
must declare `animationChanged` and support both this capability and the demand
operation below; older hosts cannot authorize this event by listing its name alone.

The passive provider alone can invoke `titan.events.animationChanged.interest.v1`
with `{"revision":"1","enabled":true}`. Revision is a canonical positive decimal
string no greater than `9223372036854775807`; enabled is a boolean. True means the
revision was published, false means it was stale. Missing/retired principals and
enabling an unavailable source fail explicitly. Disabling remains possible after
source loss. Providers publish current subscriber demand on registration/removal,
using increasing revisions. This controls observations only, not game mutation or
event injection. Requests are bounded to 1024 UTF-8 bytes.

Native capture requires both subscriber demand and the ready-owner event mask
(bit 32). A channel-owned atomic epoch is checked before any actor capture or
registry lock. Captured events retain that epoch and exact runtime endpoint;
subscription off/on, activation changes, retirement and runtime replacement revoke
old tickets. The epoch is rechecked inside counted callback admission. No JNI
subscriber polling occurs per frame, and an unrelated UI-only plugin creates no
animation capture demand. Callback failures are not retried.

The event payload is exactly
`{"schemaVersion":1,"kind":"npc","worldViewId":7,"index":0,"worldIncarnation":"8","actorIncarnation":"9"}`.
Kind is `npc` or `player`; selectors are integers in 0..65535. Tokens are distinct,
canonical nonzero uint64 decimal strings. Extra/missing fields, duplicate keys,
malformed tokens and payloads over 1024 UTF-8 bytes are rejected before provider
delivery. These are qualified actor identities, not definition IDs. The host
delivers once per original observation to each qualified provider, outside registry
monitors, and suppresses all legacy per-instance animation payloads. Before fanout,
it snapshots each provider's acknowledged subscriber-demand revision and exact
ready owner identities and activations. A changed membership or demand revision
skips that provider for this observation, so an earlier callback cannot redirect
it to a newly enabled, reloaded or newly interested recipient. Providers
must resolve and validate the current actor and fence every plugin subscriber by
its own activation; the shared callback grants no plugin action authority.

### Optional captured overhead-text observations

`titan.events.overheadTextChanged.capabilities.v1` accepts `{}` and returns a
strict JSON boolean. It requires the installed qualified utterance source, exact
actor identity capture and the optional monitor-free
`EmbeddedRuntime.providerOverheadTextChanged(String)` entrypoint. The required
general event capability response remains unchanged. Declare `overheadTextChanged`
and negotiate both this capability and `titan.events.overheadTextChanged.interest.v1`.
Listing the name in an older general response alone grants no authority.

The passive-provider-only interest operation accepts the same bounded
`{"revision":"1","enabled":true}` request and strict boolean acknowledgment as
animation interest. Its revisions and demand are independent of animation.
Disabling remains possible after source loss; enabling requires the current
qualified source. Native capture requires ready-owner bit 64 and current
subscriber demand. The atomic channel ticket is captured before any actor reads
or registry locks. Off/on subscription changes, activation changes, retirement
and endpoint replacement revoke earlier tickets, including deferred delivery.
There is no per-frame subscriber JNI polling or source work for a provider with
no demand.

The exact event has the six animation identity fields plus nonnull `text`:
`{"schemaVersion":1,"kind":"npc","worldViewId":7,"index":0,"worldIncarnation":"8","actorIncarnation":"9","text":"Hello"}`.
The same selector/token validation applies. Text is bounded to 65,536 UTF-8
bytes and the complete JSON to 524,288 bytes. Empty text is preserved, and the
generic transport preserves valid Unicode, including non-BMP characters and
embedded NUL, without normalization or truncation. Malformed Unicode, null text,
extra fields and duplicate keys are rejected. A particular source may qualify a
narrower character set; unsupported source text must not be silently rewritten.
Native-to-Java delivery uses the UTF-8-to-UTF-16 JNI helper, so it does not pass
ordinary UTF-8 into `NewStringUTF`.

This is captured utterance text from the qualified observation, not a later
`getOverheadText` read. It does not promise events for expiration, every setter,
or legacy SDK notifications. Equal observations are not deduplicated. Delivery
uses the same counted, monitor-free provider fanout and pre-callback demand plus
ready-owner membership snapshots as animation, with independent demand revisions.
Legacy per-instance overhead payloads never enter this qualified provider route.
Providers still resolve the current exact actor and fence each subscriber's own
activation. The shared callback grants no plugin action authority.

Synchronous menu delivery is a separate optional contract:
`providerMenuEventTypes()` declares `PostMenuSort`, `MenuOpened` and/or
`menuOptionClicked`; `onMenuEvent(type, json)` returns a bounded JSON object.
The host intersects the immutable declaration with
`titan.menu.capabilities.v1`: `{schemaVersion:1,protocol:1,synchronousEvents:[...],
knownEntryClicksOnly:true,customEntries:true,insertionEvents:false,
deprioritization:false}`. Events/custom entries are unavailable when native menu
hooks or the Java ingress are absent. Current open/editable state is queried
separately. `MenuEntriesAdded` tells of a rebuilt menu's own entries at once, before
`PostMenuSort`: `entries` holds each in the order in which the game inserted it, with the
fields of `getEntries` and `low`, whether the game sorts it under the ordinary ones. Rows of
submenus are among them, after their heading. The edits of other menu events are allowed
then too.

The native Java-adapter pass visits a qualified provider once at its first ready
member's position. Remaining members do not redispatch its shared bus. Other
native/Java/script positions stay in their existing runtime order; grouping
necessarily replaces interleaved per-plugin provider positions. Membership comes
from the admitted Java registry, including DEV JARs, never a user-supplied label.
Nested events get independent dispatch frames. No global Java runtime monitor
surrounds plugin callbacks, and retirement drains admitted owner/provider work.
Failures do not retry through the per-plugin path.

A game menu is rebuilt many times a second. When `HostServices.supports` reports
`titan.menu.interests.v1`, a provider can call it with a positive decimal-string
`revision` and a `types` array holding those of `PostMenuSort`, `MenuOpened` and
`MenuEntriesAdded` it wants now. Native code then skips the rest before it builds a payload or enters
Java. Clicks are always delivered. A provider which has said nothing receives
everything it declared, and an older revision changes nothing.

`RuntimeProvider.pluginEventTypes()` names the observational events which a
provider's plugin instances take one by one. It is read once after initialization.
Null, the default, keeps every event the host sends to each plugin. With any other
set the host sends none of the rest to an instance, including `mainLoop` and
`MenuEntryAdded`. Panel requests, settings, key input, sounds and game ticks are
always sent.

`titan.menu.read.v1` accepts `{"op":"state"}`, `getEntries` (optionally with a
parent handle), or `getEntry` with `handle:{generation,id}`. These are game-thread
reads available to the passive provider principal. Responses use `ok` and an
actionable `error` on failure. State adds `available`, `generation`, `open`,
`editable` and native capability bits. Entry records have opaque generation/ID,
parent handle, canonical logical opcode, identifier/param0/param1/itemId/worldViewId,
flags, UTF-8 option/target and `actionKnown`. Unknown action identity and submenu
headings must not be treated as valid game-action fields.

The plugin-scoped `titan.menu.request.v1` adds `create` (generation/index and
optional `initial` fields), `update` (handle/fields), `setEntries`
(generation/handle array/optional parent), `remove` (handle), and `setOnClick`
(handle/enabled/`delivery:"provider"`). It also accepts those read operations and
the plugin-only `callbackCurrent` query (handle, result `current:boolean`).
Mutations bind the actual plugin/load generation and native runtime handle to the
current synchronous provider transaction; JSON cannot choose an owner. All
operations retain native handle, phase and text bounds. Creation may synchronously
invoke other runtimes, so reread the returned handle before exposing its state.
Requests/results are limited to 1 MiB of UTF-8, entry arrays to 4096 and text
mutations to 255 UTF-8 bytes without NUL. Existing ABI records remain unchanged.

Known-entry click payloads include the full owned `entry`, plus the existing
pending-action fields. Unknown/direct actions do not fabricate entry identity.
The provider owns its subscriber/consumer ordering and resolves its current
consumer after subscribers. Immediately before invoking it, check
`callbackCurrent` through the captured plugin's host, then use that owner's
callback admission. Native provider markers prevent a later duplicate callback.
Explicit callback removal may clear another member of the same provider;
automatic disable/unload cleanup remains owner-specific. The click response is
`{consumed:boolean,action?:{identifier:int32,param0:int32,param1:int32}}`.
`consumed` is required even when false. Successful delivery acknowledges the
current native callback registration. An undelivered provider callback suppresses
the action even if owner retirement removed its registration. Replacements after
acknowledgment require their own completion; removal after completion is allowed.
Consumption is monotonic; all three optional action fields validate atomically.
They preserve other prior replacement fields. Opcode/world/text/coordinate
replacement is not authorized by this response. A malformed/failed click ingress
suppresses the native action. Native selection/generation checks still run before
forwarding an unconsumed action, and nested callbacks cannot drain lifecycle
changes while the enclosing provider transaction is active.
Mutations issued by an admitted synchronous callback may finish as retirement
starts; this is not a rollback transaction for earlier native edits. Providers
must still admit each locally staged action mutation through the current owner's
callback gate. Revoked/unowned work cannot borrow another subscriber's ingress.

Shared rendering is optional through `providerRenderLayers(): Set<Integer>` and
`onRenderOverlay(int)`. Layers 1 and 2 use the original native
`titan.render.provider.capabilities.v1` contract,
`{schemaVersion:1,layers:[1,2],raster:true}`, advertised only when provider
ingress, render-interest publication, and prepared raster commit are installed.
The additive optional field `aboveScene:true` grants layer 0. The original
`layers:[1,2]` list stays unchanged for older parsers; a missing, false, or
non-boolean `aboveScene` grants no additional layer. The native implementation
grants layer 0 only at the verified final DrawUI callsite before the game UI is
drawn. Sandwich and swap fallbacks do not qualify that phase.

The host snapshots requested layers and their qualification at provider
initialization. If layer 0 is unavailable then, a later capability change requires
a provider reload. This admission is separate from current hook readiness in
`titan.render.capabilities.v1`, which must also advertise layer 0 before use.
Providers remain responsible for their content's clipping and geometry; a render
phase alone does not supply HUD anchors or a fixed/resizable-mode decision.
Matching per-plugin render delivery is suppressed only for qualified routes.
Each actual native layer fanout enters each
active provider once, regardless of plugin count. A failed callback does not
retry or fall back; other providers still receive the pass. Inspection never
renders, and shutdown drains rendering before closing the provider or realm.

Native render demand uses a separate generation/revision publication, with
`1 << layer` bits (legacy mask 6, mask 7 with the explicit scene grant). Missing
scene qualification preserves the original mask and per-plugin layer-0 route.
Demand is conservative: any admitted owner can
keep its provider's declared layers active. Publication succeeds before startup
readiness, and disable/unload/failed status revokes demand. Older providers keep
their per-plugin callbacks. Provider callbacks run outside the embedded runtime
monitor, with no selected plugin owner.

Inside `onRenderOverlay`, the provider can create one shared raster per phase,
paint all active contributors into it, then call `rasterPublish` and `rasterDraw`.
Every plugin's painting must run through that plugin's `dispatchCallback`.
The host automatically captures immutable owner/generation/activation stamps
(at most 1,024 distinct contributors per pass), without accepting identities
from provider JSON. Cross-provider contributions invalidate the pass. A thrown
contributor aborts that pass; it does not authorize presenting partial pixels.

Publication stages owned CPU pixels and snapshots contributors. Draw prepares
the GPU upload outside all plugin-owner locks, then validates every contributor
under a stable lock order and commits only the native prepared enqueue ticket.
No painting, GL upload, or user callback runs under those locks. The native host
serializes conventional lifecycle mutation against its render fanout; the Java
gate also revokes admission before forwarding an asynchronous failed/disabled
status. Retirement or re-enable between painting and commit therefore refuses
the composite. A successful commit linearizes before a later revocation.

Provider raster handles must belong to that context and receive fresh authorization
on each draw pass. A host advertising `titan.raster.later.v1` gives a client paint `rasterLater()`: what
another thread runs by it publishes as that paint would, with the plugins which had painted until then, also
after the paint has ended. It runs once, outside any other paint, and does nothing once the provider has
retired. A painter which fails later in the same paint takes these pixels off the screen too.
A host advertising `titan.raster.reuse.v1` accepts
`rasterReuse(surface, lastPublishedSequence)` after painting when the resulting
pixel data and dimensions exactly match the last full publication. It also
checks the current contributors and their activations. A refused reuse can
fall back to publishing the current pixels. Each draw still checks the current
native surface and all contributing plugins. `rasterPublish`/`rasterDraw` returning
false means discard the frame (including readiness/revocation refusal); an
exception denotes an invalid operation or transport failure. `rasterClose`
remains available outside rendering during orderly cleanup. Surfaces and native
principal retire only after provider work drains. This enables global framework
ordering without a full framebuffer texture for each individual plugin.

Starting with SPI 0.2.18, an above-widgets compositor can use
`titan.overlay.canvasDrag.v1` to make a manually placed painter draggable.
Register its region with `canvasDragOpen`
and submit the visible rectangle in canvas pixels with `canvasDragFrame`.
The native HUD captures the Alt gesture and returns the preview or final
top-left. Persist only the release, in the painter's own coordinate system.
Change the placement revision when settings change to invalidate an older drag.
Close the handle when the painter is removed or its presentation changes.

With `titan.overlay.canvasDock.v1`, the same handle can also participate in the
native HUD layout. Call `canvasDockFrame` with the saved anchor and the last
painted rectangle. A release identifies the chosen anchor, including free
placement when released outside a target. Later layout updates move the painted
box as the viewport, chatbox or other panels in the stack change. Only a release
changes the saved placement.

Inspection receives immutable original JAR bytes and must return an empty list
for another format. It must not initialize plugin classes or start plugin work.
Each descriptor supplies a stable host ID, original entry class and dependencies.
The host converts this into its existing Java metadata transport. The original
JAR needs neither Titan annotations nor an added descriptor. Inspection and load
operate on the same artifact hash. No inspection result grants download trust,
store entitlement, or execution authorization.

Providers may optionally return `BundledPlugin` entries from `bundledPlugins()`
for code shipped in their own distribution. Each entry carries a descriptor,
package ID, compatibility baseline, and the lowercase SHA-256 of the actual
artifact supplying those classes. Adapted artifacts use their own hash, not
the original upstream hash. Inspection must not initialize plugin classes.
`createBundled(entry, context)` receives the exact catalog entry and must
revalidate its artifact provenance before using the existing provider realm.
The default catalog is empty and creation is unsupported.
The optional six-argument `BundledPlugin` constructor also declares lifecycle and
item-container event interests; UI-only entries can set both false so the native
host does not enable unrelated entity scans. Four-argument callers retain both
interests for compatibility.

`InspectorMain --bundled` and `EmbeddedRuntime.inspectBundled()` emit the normal
metadata array with `sourceKind: "bundled"`, `providerId`, `packageId`,
`compatibilityBaseline`, and `artifactSha256`. The host forces these entries to
default disabled. `loadBundled(providerId, pluginId, artifactSha256, state, generation)`
matches the selected provider, catalog ID and expected artifact hash; enabling
requires an explicit state or later enable operation. Bundled IDs are reserved
against normal artifact loads. This trusted catalog route is separate from JAR
inspection and does not grant download trust, entitlement or execution authority.

Provider-managed sources use a separate optional `installedPlugins()` catalog
of immutable `InstalledPlugin` entries: descriptor, strict source ID, package ID,
compatibility baseline, actual artifact SHA-256, and optional event interests.
The default list is empty; `createInstalled(entry, context)` defaults unsupported.
The host reads the bundled and installed catalogs at startup and reserves each
plugin ID. After committing a verified new package, a provider can call
`ProviderContext.installedPluginsChanged()` from its worker. The host rereads
that provider's local catalog, admits new packages, and publishes their entries
disabled in the tab's Plugin List. Existing package versions and entrypoints
stay pinned for that tab. New entries go through normal source, hash, dependency
and ID checks before becoming available.
If one new package fails those checks, other valid packages can still appear.
The host logs which package failed and exposes the reason in its Java runtime status.
Across all providers, the host accepts at most 128 bundled entries and 1,024
installed entries. Each complete catalog must fit 1 MiB of ASCII-escaped JSON,
matching the native decoder's byte limit. Both limits are checked before caching
the catalog or admitting a plugin; oversized catalogs fail explicitly.

`InspectorMain --installed` and `EmbeddedRuntime.inspectInstalled()` return normal
descriptors augmented with `sourceKind: "managed"`, `providerId`, `sourceId`,
`packageId`, `compatibilityBaseline`, and `artifactSha256`, always default disabled.
`loadInstalled(providerId, sourceId, packageId, pluginId, artifactSha256, state,
generation)` matches every selected identity field and passes the exact cached
entry to its provider. The provider must revalidate retained artifact bytes and
source provenance at creation. This route supplies no replacement JAR, implicit
network fetch, entitlement grant, or bypass of native authorization.

`create` produces a `PluginInstance` using the provider's own injector. The host
calls load, enable/disable, game tick, render, event, setting and unload callbacks.
The optional `onLoadAsync`, `onEnableAsync`, `onDisableAsync` and `onUnloadAsync`
hooks return completion stages; defaults call their synchronous counterparts.
The host orders these stages without waiting under the game/lifecycle monitor.
Game events begin only after startup succeeds. Disable/re-enable invalidates old
work; unload immediately retires admission and waits through the stage chain for
late startup and cleanup before closing the provider realm. Failed retirement
is reported and pins that realm for process lifetime instead of assuming its
Swing objects and workers have stopped. `titan.lifecycle.state.v1` publishes
`starting`, `running`, `disabled` or `failed` with an optional error.
Reloading the same ID waits for successful cleanup: pending cleanup asks callers
to retry later, while failed cleanup requires a client restart.
Before replacing the whole embedded runtime, native hosts call `shutdown()` and
poll the nonblocking `shutdownStatus()` JSON. It reports `active`, `pending`,
`ready`, or `failed` with an optional error. Retain the old runtime object until
`ready`; a completed call to `shutdown()` alone does not prove that asynchronous
provider cleanup or realm closure has finished.
The provider adapts native event names and payloads explicitly to its framework;
an unfamiliar event does not imply a compatible schema. Event replies are JSON
objects, and an unhandled mutable event must not claim consumption.

The host scopes services to the exact plugin/load generation and switches the
thread context classloader for provider callbacks. Unload retires that context;
disable invalidates already queued work even if the plugin is later reenabled.
Providers must stop and drain their own workers and release UI resources during
unload. Avoid a synchronous cross-thread wait from a lifecycle/event callback:
the runtime serializes these callbacks, and Swing must not wait back on it.

Plugin contexts advertising `callbacks.dispatch` expose `isCallbackReady()` as
an eligibility hint and `dispatchCallback(Runnable)` as final synchronous admission.
The gate rejects work during startup, pending native event-interest publication,
disable or retirement. It establishes the owning plugin and context classloader
on the calling thread, but does not schedule work or change thread affinity.
Admission and readiness transitions share the owner's short state lock; work
runs outside that lock. Disabling after a readiness query therefore cannot use
that stale query to authorize a later dispatch. Exceptions still release admission.
Cleanup starts immediately, including when requested by the callback itself;
retirement waits for admitted callbacks to leave before closing resources.

Shared provider buses, schedulers, input listeners and queues must use this gate
for externally driven or cross-owner callbacks, not infer readiness from their
own completed startup alone. Same-owner local startup calls may retain explicit
startup scope. The passive provider host and older implementations return false
for both methods. A provider requiring the gate must validate its capability
before activation, and must preserve queued work when admission returns false.

Plugin hosts advertising `ui.context` provide one host-owned AWT event queue.
The optional `ui.timer.context` capability additionally binds an exact, stopped
`javax.swing.Timer` through `registerUiTimerContext(timer, dispatcher, clearer)`
on the EDT before it starts. The host checks the running Java's private
invocation layout (11, 17, 21 and 25 are tested) and requires the `java.desktop/javax.swing`
and `java.desktop/java.awt.event` package opens. A Java with another layout, and older hosts,
decline the capability. Timer/listener identities remain unchanged. Binding is
captured when an event is posted, so revocation and same-object re-registration
cannot reattribute queued old callbacks. The host counts entered callbacks for
retirement; the provider must stop its timers on the EDT during cleanup. A timer
cannot be transferred to another plugin load. Unregistered timers receive no
plugin authority, and Timer subclasses or already-running timers are rejected.

On the EDT, register `registerUiIsolation(unownedDispatcher)` before entering
plugin construction/startup, then register actual panel roots and embedding
Frames with `registerUiContext(root, ownedDispatcher, unownedDispatcher)`.
The rootless isolation also covers a startup modal before the first panel exists.
The owned dispatcher enters the captured activation synchronously or discards
stale work. The unowned dispatcher must synchronously run work exactly once with
its provider's private attribution removed, then restore that attribution. It
must not reject work merely because its activation has retired. Register it
again for each activation. The provider-wide read-only host denies these methods.

Before every event, the broker clears every live provider's attribution, sets a
host context classloader, and enters only the nearest registered component root
or registered owner Window. Registering a panel does not register its containing
Frame; owned dialogs require the Frame registration. An explicitly targeted root
takes precedence over the posting callback. Ordinary `invokeLater` posted from
an owned UI event captures that event's binding.

When `ui.context.callbacks` is available, a provider can register
`registerUiCallbackContext(ownedDispatcher, unownedDispatcher)` on the EDT once
per activation. Swing invocations posted inside that activation's admitted
`dispatchCallback` inherit its binding, including posts from game-thread callbacks.
Closing the registration cancels pending invocations that captured it. Work on
independent worker threads still needs explicit provider capture. With
`ui.context.workerPosts.v1`, the registration implements
`HostServices.WorkerUiContext`. Capture the handle when scheduling managed work.
Call `withUiPosts(work)` on the worker to tag its AWT posts for the same
activation, including during startup. The host checks admission on EDT delivery.
Closing the registration cancels late posts from an already-running worker.

Direct synchronous calls into another component bypass the AWT event
queue and do not select a new owner. Providers must not push their own event queue.

Closing a component-root registration from any thread immediately drops its
callback references. Rootless isolation grants
no action authority and remains through an unfinished lifecycle stage, including
when close is requested during startup. Disable/unload removes it after lifecycle
cleanup, so a still-open startup modal cannot expose its suspended provider scope.
Disable and unload revoke roots automatically; queued stale work is cancelled.
The host begins unload cleanup without waiting for active UI callbacks,
then waits for those callbacks to leave before completing retirement or closing
the realm. This lets modal disposal finish the callback it is cancelling.
The original two-argument `registerUiContext` remains available, but cannot clear
private provider attribution in nested/modal dispatch; providers with private
scope state must use the three-argument form and rootless isolation.

AWT cancellation is a private host adapter for the running Java's
desktop classes. The JVM opens only `java.desktop/java.awt.event` and
`java.desktop/java.awt` to the unnamed host module. Admission verifies the exact
completion fields/methods before installing the broker. Retired `InvocationEvent`
and `PeerEvent` work completes with cancellation without executing its Runnable;
the original completion path wakes synchronous waiters and invokes completion
listeners under unowned scope. Original `SequencedEvent` and `SentEvent` disposal
also releases focus/send protocols and cancels known nested invocation work.
Arbitrary custom `ActiveEvent` protocols have no generic cancellation contract.
The public SPI continues to use Java 11 JDK types only.

Host requests are an explicit allowlist, not reflection or native evaluation.
`invoke` takes `{ "args": [...] }` and returns the JSON result directly.
`supports` describes implementation availability, while a valid read may still
report unavailable game state. Queries requiring game state must execute on the
actual native game thread; `titan.thread.isGameThread.v1` queries that thread.
`schedule("clientTick", callback)` and `schedule("render", callback)` use the
actual host queues. There is no synthetic tick-end phase.

`titan.render.capabilities.v1` is an owner-scoped native query returning
`{schemaVersion:1,layers:[...],widgetHooks:false}`. Its layer IDs describe only
actual native render callbacks; phase zero appears only with the layered renderer,
phase one requires the installed and ready foreground hook, and phase two is the
final overlay callback.
`titan.render.viewport.v1` must be invoked during native rendering and returns
`{x,y,width,height,generation}` in physical Canvas pixels, or null when fresh scene
geometry and its Canvas transform are unavailable. These read-only render queries
do not impose the game-thread guard used by game-state reads. Neither capability
implies widget-specific hooks or a synthetic render phase.

The viewport object optionally contains `projectionScale`, a positive integer
zoom in scene pixels. The selected draw and current render camera must agree
on zoom, source identities, Canvas origin, unit aspect and exact 1:1 dimensions.
Providers using integer projection in physical Canvas pixels can use the value
for that paint. A missing field leaves the rectangle available independently.

The host operation namespace is framework-neutral. Providers map their own API
methods to `titan.client.tick.v1`, `titan.client.gameCycle.v1`,
`titan.client.topLevelPlane.v1`, `titan.client.topLevelBaseX.v1`,
`titan.client.topLevelBaseY.v1`, `titan.client.runEnergy.v1`,
`titan.client.weight.v1`, `titan.client.instanced.v1`,
`titan.client.gameState.v1`, `titan.vars.varbit.v1`, `titan.vars.varp.v1` and
`titan.input.mousePosition.v1`. Variable queries take one integer ID; other
scalar queries take no arguments. Game state is the native integer state ID,
energy is in hundredths of a percent, and mouse position is `{x,y}` or null.
Mouse position samples the Windows cursor in the current game window's client
coordinates. Each call owns its copy and can run during rendering. A failed
cursor lookup, failed coordinate conversion or changed game window returns
null. Other game-state reads retain their game-thread requirement.

An independent optional `titan.client.gameState.capabilities.v1` operation accepts
only `{}` and returns exactly
`{schemaVersion:1,exactReads:boolean,threadSafeReads:boolean,maxResponseBytes:4096}`.
Both booleans must be true for support. This capability does not require the
game-thread query transport. `titan.client.gameState.snapshot.v1` also accepts
only `{}` and returns one of these exact envelopes:

- `{schemaVersion:1,status:"present",rawState:integer}` carries the full signed
  32-bit native value. Unknown state IDs and negative values remain valid data;
  the provider decides how its own enum represents them.
- `{schemaVersion:1,status:"unavailable",reason:string}` carries a nonempty
  diagnostic of at most 2,048 UTF-8 bytes, with no `rawState` field. It is not a
  null/absent state or a default enum value.

Responses are limited to 4,096 UTF-8 bytes. Unknown or duplicate fields, missing
fields, invalid Unicode, and integer strings, fractions, exponent notation or
overflow are rejected. The native reader uses a qualified source descriptor,
request-owned scalar storage, and source-generation and singleton checks around
the read. It may be called from the EDT, rendering or a worker without a game
tick or event having occurred. It does not read the legacy shared login buffer
or cache state-change events. Each invocation has independent owned results;
provider retirement rejects new calls and waits asynchronously for admitted
reads to finish before retiring native authority. Capability availability does
not guarantee the game singleton remains available for each read. The existing
`titan.client.gameState.v1` and SDK login-state APIs retain their previous
threading and conversion behavior; this adds no ABI or SPI record layout.

Camera readers use the same scalar envelope with one independent source per
field. Each prefix in the table accepts `.capabilities.v1` and `.snapshot.v1`
with an empty JSON object. A present snapshot carries the indicated signed
32-bit value. A missing field, failed read or change of game source yields
an unavailable result.

| Prefix | Snapshot field | Meaning |
|---|---|---|
| `titan.camera.posX` | `posX` | Camera's first horizontal local coordinate |
| `titan.camera.posY` | `posY` | Camera's vertical coordinate |
| `titan.camera.posZ` | `posZ` | Camera's second horizontal local coordinate |
| `titan.camera.pitch` | `pitch` | Current render tilt in 14-bit angle units |
| `titan.camera.yaw` | `yaw` | Current render heading in the same units |
| `titan.camera.pitchTarget` | `pitchTarget` | Input target tilt before rendering, in the same units |

Each call takes its own observation. A caller collecting several fields can
observe a game update between calls. Values retain their signed representation
including zero. One revolution corresponds to 16,384 angle units.

The independently qualified `titan.client.worldId.capabilities.v1` and
`titan.client.worldId.snapshot.v1` operations use the same empty requests,
capability shape, strict signed 32-bit integers and 4,096/2,048-byte response/
diagnostic bounds. A present world-ID response is exactly
`{schemaVersion:1,status:"present",worldId:integer}`; unavailable is exactly
`{schemaVersion:1,status:"unavailable",reason:string}`. It reads the current
client's raw world-ID field into request-owned storage, with source-generation
and singleton revalidation. Zero and negative IDs remain data. This is available
off-thread independently of game-state reads and world-list availability; it
does not search metadata, identify a WorldView, or infer world-type flags. The
legacy SDK `currentWorld()` route is unchanged.

`titan.log.v1` writes one line to the client's log, as `{level,message}` with a level
of `info`, `warn` or `error`. Query `titan.log.capabilities.v1` with `{}` for a
boolean. Messages are cut at 1,800 characters, control characters become spaces, and
the line carries the provider's ID. Any thread may write. Bound your own rate.

`titan.client.worldFlags.capabilities.v1` and `titan.client.worldFlags.snapshot.v1`
follow the same protocol, with the property mask in `worldFlags`. The host reads the
client's selected world ID and takes that world's properties from the published
world list, on any thread. It is not a read of the client's own property field. An
unlisted or not yet loaded world is unavailable; zero is a real mask.

The per-field operations `titan.client.runEnergy` and `titan.client.weight`
use the same `.capabilities.v1` and `.snapshot.v1` suffixes, empty request and
response bounds. Present responses carry a signed integer in `runEnergy` or
`weight`. Run energy is in hundredths of a percent and weight is in kilograms.
The host reads each field independently into caller-owned storage and verifies
the client and source generation. Zero energy and negative weight are valid.
An unreadable field yields an unavailable response with a reason.

`titan.client.gameCycle.capabilities.v1` and `titan.client.gameCycle.snapshot.v1`
take the same empty request. A present snapshot carries a signed 32-bit
`gameCycle`, including zero and values after wraparound. Depending on the loaded
revision, the host observes a client field or a clock in the game image. Both
reads validate the source generation. Client-relative reads also recheck the
active client. The game cycle advances independently of the server tick count.

The optional `titan.grandExchange.capabilities.v1` operation takes `{}` and
returns a strict JSON boolean. True identifies an installed qualified offer
source, independently of whether a complete settled snapshot is available
right now. `titan.grandExchange.offers.v1` takes the same empty request and
returns exactly `{schemaVersion:1,status:"present",offers:[...]}` or
`{schemaVersion:1,status:"unavailable"}`. Both operations use
`HostServices.supports`/`invoke`; older hosts report unsupported. Requests are
bounded to 256 UTF-8 bytes and offer responses to 32 KiB.

A present snapshot contains 1–64 complete slots in order, with each record
exactly `{slot,itemId,totalQuantity,quantitySold,price,spent,status,type}`. `slot`
equals its zero-based array index. Item ID and quantities are signed 32-bit
JSON integers. `price` and `spent` are canonical signed 64-bit decimal **strings**
to preserve values beyond JSON consumers' floating-point precision. Canonical
zero is `"0"`; leading zeroes, plus signs, fractions and exponents are invalid.
The row's numeric `status` and `type` each preserve an observed unsigned byte
in 0–255. The generic host does not convert them into a normalized state enum.
Money requires a qualified native 64-bit source; a legacy widened value is not
substituted. Values are copied without clamping or arithmetic inference.
An empty array, partial or null slots, uninitialized source or pending native
event delivery cannot stand in for an empty exchange and is unavailable.

These are settled, request-owned SDK copies readable on any admitted thread,
including an owned EDT startup callback. No game-thread query or scheduling is
required. The native source and owner are rechecked around the copy. Passive
provider calls are counted through retirement and discarded when admission is
revoked; plugin-entry reads also reject a changed load/activation before return.
The result has no retained native handles. This read-only capability neither
places/cancels offers nor grants offer-change event delivery.

`titan.skills.baseLevelPrefix.capabilities.v1` accepts only `{}` and returns
exactly `{schemaVersion:1,exactReads:boolean,threadSafeReads:boolean,
maxResponseBytes:4096,prefixCount:24}`. Both booleans must be true for support.
The independent `titan.skills.baseLevelPrefix.snapshot.v1` operation takes `{}`
and returns exactly `{schemaVersion:1,status:"present",prefixCount:24,
levels:[24 signed 32-bit integers]}` or
`{schemaVersion:1,status:"unavailable",reason:string}`. Unavailable responses
have no prefix count or cells. All 24 qualified base-level cells must read
successfully from the same resolved client and source generation; a partial
prefix is unavailable. Values are neither clamped nor summed by the generic
host. A native transition may interleave individual aligned cell reads, as it
can with a normal sequential getter loop; this is not an atomic frame snapshot.
It supports EDT/worker callers with the same owned-result retirement guarantees
and strict response/diagnostic bounds as the scalar operations. These 24 cells
are a named-skill prefix, not proof of a full native array or a 25th XP cell.
This capability does not provide overall experience or world-type properties,
and has independent availability from the individual skill getters.

`titan.skills.experienceArray.capabilities.v1` accepts `{}` and returns
`{schemaVersion:1,exactReads:boolean,coherentReads:boolean,threadSafeReads:boolean,maxResponseBytes:4096,slotCount:25}`.
All three flags must be true for support. `titan.skills.experienceArray.snapshot.v1`
accepts `{}` and returns either
`{schemaVersion:1,status:"present",slotCount:25,experience:[25 signed 32-bit integers]}`
or `{schemaVersion:1,status:"unavailable",reason:string}` with a nonempty reason
of at most 2048 UTF-8 bytes. Each present result must contain all cells, including
the unnamed slot, from a single coherent observation of the owning game.
The returned copy remains safe to read on worker and UI threads. A host that can
only establish the 24 named skills leaves the operation unavailable.

The optional `titan.vars.typedInt.capabilities.v1` operation takes `{}` and
returns `{schemaVersion:1,exactReads:boolean,threadSafeReads:boolean,maxResponseBytes:4096}`.
Both flags must be true. `titan.vars.typedInt.snapshot.v1` takes `{"id":29}`
with a signed 32-bit ID. Each bounded response includes `schemaVersion:1`,
the requested `id` and one `status` obtained from a coherent observation of
the owning game's normal integer-varp storage.

| Status | Additional field | Live observation |
| --- | --- | --- |
| `intStorageAbsent` | None | Integer array is null, before indexing or checking the type table |
| `typeStorageAbsent` | None | Integer array exists and the type array is null |
| `outOfBounds` | `length` (nonnegative int32) | ID falls outside the first indexed array, type first then integer storage |
| `longType` | None | The indexed type flag identifies a long varp |
| `present` | `value` (int32) | The indexed type flag identifies an int and the integer slot was read |
| `unavailable` | `reason` (1–2048 UTF-8 bytes) | The source cannot establish the applicable result |

A host with only the legacy integer cell read leaves this capability unavailable.

The optional `titan.vars.varbitDefinition.capabilities.v1` operation takes
`{}` and returns a boolean indicating whether the current process can observe
definition identities. `titan.vars.varbitDefinition.v1` takes `{"id":37}` on
the game thread. A successful response contains the requested `id`, a positive
uint64 decimal string `incarnation`, an unsigned 16-bit parent `index` and
inclusive bit positions `low` and `high` in 0–31. On a cache miss, the host
can invoke its existing game-thread varbit getter once and look up the entry
again. Only a successful lookup in the running game's cache yields a
definition. An unavailable entry returns JSON null.

The optional `titan.vars.capabilities.v1` query reports
`{schemaVersion:1,exactVarbits:true,gameThreadOnly:true}` when the existing native
varbit getter and varp-array anchor are available and the getter has not faulted.
`titan.vars.varbit.exact.v1` takes `{"args":[id]}` with one nonnegative signed
32-bit integer. The host and native boundary both enforce the game thread.
It invokes the running game's getter directly, without consulting disk-cache
definitions or the legacy varbit fallback. All signed 32-bit results, including
`-1`, are valid values. Missing live state, recursive entry or a contained native
fault returns native JSON null, which the Java host converts into an unavailable
capability error. A contained getter fault disables this capability for the
remaining client DLL lifetime; it is not cleared by login or a plugin reload.
Capability availability does not guarantee that every requested definition is
ready. Strings, fractions and overflowing IDs are rejected. The existing legacy
`titan.vars.varbit.v1` operation and public SDK getter retain their old semantics;
they must not substitute for this exact contract. This operation adds no varbit
mutation or off-thread/render snapshot capability.

### Typed client-variable reads

`titan.varcs.capabilities.v1` accepts `{}` and reports whether the host
has the typed map layout and the game-owned string accessor.
`titan.varcs.lookup.v1` accepts a signed 32-bit `id` and a `kind` of
`int` or `string`. Call it on the game thread under a current owner.
A response with `status: "present"` contains the signed integer or copied
UTF-8 string in `value`. Empty strings, embedded NULs and zero are values.
`missing` means the key was absent from a complete map. `differentType`
means its tag did not match. `unavailable` means the host could not establish
the result, including an unreadable entry, duplicate key, invalid layout or
an exceeded bound (65,536 entries or 65,536 bytes of string data). A missing
capability, stale owner or invalid thread produces an error before delivery.

`titan.skills.boostedLevel.v1`, `titan.skills.realLevel.v1`, and
`titan.skills.experience.v1` return the corresponding integer game value for one
exact numeric Titan SDK skill ID. IDs outside the SDK enum, aggregate skills,
fractional values and coercions from strings are rejected. Providers must map
their own skill enum explicitly; these calls do not synthesize aggregate levels
or experience arrays. In `titan.skills.capabilities.v1`, `exactReads` reports
whether all three fields are available. Hosts advertising `threadSafeReads`
also report independent `boosted`, `base` and `experience` flags. These reads
copy one aligned signed cell and revalidate the client and layout generation
before returning. They can run on the EDT, render and worker threads. Older
hosts require the game thread. An unavailable read raises an error. Zero and
negative values from a successful read retain their values.

`titan.client.destination.capabilities.v1` returns `schemaVersion: 1`, the
`exactReads` and `worldViewReads` booleans, and `maxResponseBytes: 4096`.
`titan.client.destination.snapshot.v1` captures the minimap walking destination
on the owning game thread. Both operations take an empty object. The v1 result
contains the schema version and one of the following states.

| Status | Additional fields |
|---|---|
| `absent` | None. At least one observed scene tile coordinate is zero. |
| `present` | Nonzero signed 32-bit `sceneX` and `sceneY` in tiles, plus `worldViewId: 0` for the verified top-level scene. |
| `unavailable` | A nonempty `reason` of up to 2048 UTF-8 bytes. |

Only a nonempty destination needs world and scene qualification. A missing
coordinate source, a failed read, or a world replaced during capture produces
an unavailable result. Each result belongs to this request and this game.

`titan.hintArrows.capabilities.v1` accepts only `{}` and returns a JSON boolean
indicating whether the existing SDK server-slot getter is present. It does not
promise that the current game state is readable. On the native game thread,
`titan.hintArrows.server.v1` accepts only `{}` and returns an owned object:
`{"status":"present","kind":"none"}`, or kind `npc`, `player`, `coordinate`,
`worldEntity` or `unknown`. Only `npc`, `player` and `worldEntity` include an
`index`, preserving the SDK's signed 32-bit value. `{"status":"absent"}` means
the SDK verified that slot zero was missing; `{"status":"unavailable"}` means
it could not read the slot. Neither is an empty (`none`) hint arrow.

Requests and replies are bounded to 256 UTF-8 bytes. Extra or duplicate fields,
invalid kinds/statuses and noninteger/out-of-range indices are rejected. These
passive reads retain owner/generation checks and retirement drain, carry no
native pointer or raw stored kind, and do not resolve actors, infer a world
coordinate, or authorize hint-arrow writes.

`titan.worldMap.controller.capabilities.v1` accepts `{}` and reports
`schemaVersion:1`, `identityReads` and `zoomReads`. On the game thread,
`titan.worldMap.controller.snapshot.v1` accepts `{}` for lookup or a string
`expectedIncarnation` for a retained controller. A present record carries a
nonzero unsigned decimal incarnation and either signed 32-bit `zoomBits`
(IEEE 754 current zoom) or `zoomUnavailable`. An unguarded lookup can return
`absent` after a verified null pointer. A failed or stale read returns
`unavailable` with a diagnostic. The operations use owned JSON and work
independently of the map widget, selected area, target zoom and viewport.

`titan.actors.capabilities.v1` accepts only `{}`. Its qualified contract is
`{schemaVersion:1,exactReads:true,worldViewReads:true,maxIndex:65535,
maxWorldViewId:65535,maxResponseBytes:2097152,worldViewIds:"native-explicit",
identity:"observed-native-instance"}`. The host requires both read qualifications
and a native game-thread query transport before advertising support. Capability
queries are thread-independent; actor and world-view captures require the actual
native game thread. These optional operations return owned JSON through the
existing host boundary, without the legacy shared actor scratch buffer, a new
public SPI method or a native payload-layout change.

`titan.actors.interactNpcAvailable.v1` takes `{}` and returns a boolean for an
enabled plugin owner. `titan.actors.interactNpc.v1` requires the native game
thread and a retained target, for example
`{"kind":"npc","worldViewId":7,"index":42,"expectedWorldIncarnation":"71","expectedIncarnation":"92","action":"Talk-to"}`.
The action is a nonblank label of up to 255 UTF-8 bytes. The active game
rechecks both identities and submits once to its world-aware NPC action
function. A true return acknowledges SDK admission. A replaced target,
missing option or SDK refusal returns false. Plugin code can observe
gameplay effects through ordinary events.

`titan.widgets.interactAvailable.v1` takes `{}`. Enabled plugin owners can
use `titan.widgets.interact.v1` on the native game thread with a single named
action and the incarnation returned by the exact widget service. For example,
`{"args":[42,0],"expectedIncarnation":"15","action":"Pay/All"}` selects
direct child zero of packed widget 42. Use child index `-1` for the flat
widget. A true response acknowledges queue admission. Titan revalidates the
receiver when consuming the request and checks plugin activation through
dispatch. Menu label, submenu, visible-child and selected-target matching
follow the SDK's named widget action rules.

Optional `topLevelReads`, `listReads` and `localPlayerReads` capability booleans independently enable
the role and collection operations below; omitted flags mean false. If
`listReads` is true, the capability must also report `maxListEntries:32768`,
`listMembership:"native-registered-map"` and `listOrder:"native-bucket-chain"`.
Known optional fields are type-checked even when disabled. Their absence does
not disable the existing explicit world and actor snapshot operations.

`titan.worldViews.snapshot.v1` takes `{worldViewId}` and optionally
`expectedIncarnation`, a positive uint64 decimal string from a previous world
capture. `titan.actors.snapshot.v1` takes
`{kind:"player"|"npc",worldViewId,index}` and optionally
`expectedWorldIncarnation` and `expectedIncarnation`. An actor incarnation guard
requires the world incarnation guard too. World-view IDs and actor indices must
be exact numeric integers in `0..65535`; there is no current-world, top-level
alias or framework-specific ID conversion at this boundary. Decimal/exponent
notation, string coercion, unknown members and duplicate JSON keys are rejected.
Requests are bounded to 1 KiB. Callers cannot supply an owner, pointer or runtime
generation. Both operations are permitted through the passive provider principal
and retain the normal admission check and asynchronous in-flight retirement drain.

`titan.worldViews.topLevel.v1` accepts only `{}` and returns the world snapshot
envelope with `worldViewId:0`. Present results share the explicit world's token.
An absent result means that the native default-world role is null; it does not
assert that registered world 0 or its actors are absent, and must not retire
their identities. Retained world reads use the existing explicit snapshot and
incarnation guard rather than a selector supplied to this role query.

SDK 183 adds the optional game-thread read `titan.worldEntities.lookup.v1`.
It accepts exactly `{index,parent,entity}` (up to 512 bytes). The numeric index
is an integer in 0..65535. Both guards are canonical unsigned 64-bit decimal
strings; `"0"` requests an unguarded observation. The response contains
`status`, `index`, `definitionId`, `childWorldId` and decimal string tokens
`parent`, `entity`, `child`. `PRESENT` identifies one entry in the top-level
world's entity table. A verified child points to a registered world with the
same ID and incarnation; an unqualified child uses ID -1 and token `"0"`.
`ABSENT` confirms that the selected slot is empty in the observed parent.
`STALE` rejects a retained parent or entity guard. `UNAVAILABLE` reports
failures to observe the table coherently. Tokens are local to one game.

SDK 184 separately negotiates `titan.worldEntities.definition.v1` using
`supports`. Its game-thread request takes `{index,parent,entity,definition}`
with the same index and unsigned string guard rules. The response includes
`status`, `index`, `definitionId` and the unsigned decimal strings `parent`,
`entity`, `definition`. A `PRESENT` result has a nonnegative ID and three
nonzero tokens. `ABSENT` reports a confirmed empty slot with no retained entity
or definition guard, ID -1 and only the parent token set. `STALE` reports a
failed guard. `UNAVAILABLE` includes a missing or unreadable definition on a
present entry. Both return ID -1 and zero tokens.

Two entities that currently witness the same definition and numeric ID share
one observed definition token. Equal IDs at different addresses get different
tokens. Changing the ID, losing the last observed witness, or retiring the
parent/session expires the old association. A failed observation does not
retire a witness. The host tracks up to one association per unsigned 16-bit
slot and one per witnessed definition. Revalidating a token requires an
indexed entity witness. Tokens cannot be used as addresses or resolved on
their own. Replacements wholly between observations may be undetectable.

The independently negotiated `titan.worldViews.atPoint.capabilities.v1` takes
`{}` and returns a strict boolean. An absent operation, malformed reply or
`false` means the host cannot qualify point selection; ordinary explicit-world
or top-level support does not imply this capability.
`titan.worldViews.atPoint.v1` takes exactly `{x:int32,y:int32}` on the native
game thread. Both coordinates retain the full signed range, including negative
values. There is no plane filter, implicit selector, caller-supplied owner or
retained-world token. Requests are bounded to 256 UTF-8 bytes.

With the same grant `titan.worldViews.list.v1` takes exactly `{}` on the game thread and
returns `{schemaVersion:1,status:"present",worldViewIds}`, every registered world's ID in
ascending order, or `{schemaVersion:1,status:"unavailable",reason}`.

Point selection captures a complete checked world roster. A uniquely matching
non-top-level world wins using the original signed base-plus-size bounds; a
complete no-child match selects the positively observed default role even when
the point is outside that default world's geometry. Multiple matching children
are unavailable when original iteration order cannot be qualified. Missing
geometry needed for child selection, incomplete membership, role ambiguity,
source change or unreadable data never becomes an empty result.

A present reply is the existing world envelope with `schemaVersion:1`,
`status:"present"`, its actual `worldViewId`, shared `incarnation`, `fields`
and `unavailableFields`. Geometry remains independently optional, especially
for default-role fallback. A positively read null default after a complete
no-child match returns exactly `{schemaVersion:1,status:"absent"}`.
An unavailable result is exactly
`{schemaVersion:1,status:"unavailable",reason:"..."}` with a nonempty diagnostic
of at most 4096 UTF-8 bytes. Neither missing-result form names a world or
retires independently held world identities. The entire response is limited to
32768 UTF-8 bytes. Native source/identity and owner checks surround publication;
passive provider retirement and managed activation/load checks fence the
completed reply as for the other owned reads. Selection performs no mutations
or scheduled game actions.

The optional `playerNameReads` capability enables `titan.actors.playerName.v1`.
It accepts the player snapshot request with both incarnation guards and returns
the same guarded actor envelope. On the native game thread the host reads the
public SDK player for that explicit world and index, then checks its identity
against the retained actor. The optional `fields.displayName` contains 1–62
printable ASCII characters. An empty, potentially truncated or ambiguously
encoded SDK name gives a field diagnostic. Regular actor snapshot reads incur
no SDK display-name lookup.

`titan.actors.list.v1` takes `{kind:"player"|"npc",worldViewId}` and optionally
`expectedWorldIncarnation`. It echoes `schemaVersion:1`, `kind`, `worldViewId`
and `status`. A present result contains `worldIncarnation` and a complete
`actors` array of `{index,incarnation}` rows, with at most 32768 entries and no
field payloads. Indices and actor tokens must each be unique within the list;
no actor token may equal the world token. Array order is preserved. Present
with an empty array means a confirmed empty collection; absent means the world
is missing and is allowed only for an unguarded request. Unavailable carries
only the bounded reason, without tokens or a partial actor array. Requested
world guards must match. Oversized, partial or malformed lists are rejected
whole, never truncated to a successful prefix. Both operations require the
game thread and use the same passive principal and retirement drain as other
actor reads.

`titan.actors.localPlayer.v1` accepts only `{}` and requires `localPlayerReads`.
It returns `{schemaVersion:1,status:"present",player:<exact actor snapshot>}`
only for a positive SDK local-player role bound to the same explicit world/index
and native actor/world identities as the exact resolver. Native code rereads the
SDK role and rechecks the resolver's incarnation guards before publication.
The nested snapshot has `kind:"player"` and `status:"present"`; no pointers or
aggregate SDK fields cross this boundary. Failure returns
`{schemaVersion:1,status:"unavailable",reason:<bounded diagnostic>}`. There is
deliberately no `absent` result: the SDK role's unsuccessful result cannot prove
absence. Unknown/current-world aliases, missing identities and disagreement
remain unavailable. The usual 2 MiB bound, game-thread requirement, passive-owner
admission and retirement drain apply. Older hosts omit this optional capability.
This sampled SDK role does not claim another framework's local-player override
cache or spawn/despawn event timing.

These collections are experimental native registered-map membership in native
bucket/chain order. That qualification does not establish identical RuneLite
active-actor membership, iteration order or despawn-event semantics. Consumers
must not infer those additional guarantees from a complete native list.

Every response echoes `schemaVersion:1` and `worldViewId`; an actor response also
echoes `kind` and `index`. `status` is `present`, `absent` or `unavailable`.
Present world views include `incarnation`; present actors include
`worldIncarnation` and `incarnation`. All tokens are positive uint64 decimal
strings. The actor's world token is in the same identity domain as the world
snapshot token. Native readers compare retained guards during the same lookup
transaction, including missing results, and report an expired guard unavailable.
The generic Java host additionally rejects a present response whose tokens do
not match requested guards. It rejects `absent` for a guarded world or a retained
actor request carrying `expectedIncarnation`; those missing retained identities
are unavailable. An actor slot lookup guarded only by the world incarnation can
return confirmed absence within that same world. The host never substitutes a
replacement identity.

Present results also contain `fields` and `unavailableFields` objects; the latter
maps omitted field names to nonempty reasons of at most 4 KiB UTF-8 each. A field
cannot appear in both. World snapshots optionally supply `baseX`, `baseY` and
`plane`, captured with the same observed-read journal and identity guard. Bases
are exact signed 32-bit integers, with no scene-range clamp; plane is an exact
integer in `0..3`. The host rejects malformed supplied values, including null,
strings, decimal/exponent notation and integer overflow. Native readers omit
each unavailable or out-of-contract field and give its reason independently;
partial geometry does not invalidate an otherwise qualified world identity.
Empty field objects from older hosts remain valid, and no missing field is
filled with zero. Changing geometry does not change the incarnation token.
World snapshots may also supply signed int32 `sizeX` and `sizeY`, independently
qualified per-view tile dimensions. The native reader compares each WorldView
field with its own Scene backing dimension under the same observed-read journal.
Missing layout/Scene, disagreement or read failure omits the affected field with
a reason; it never supplies global/default dimensions. Size changes preserve
incarnation identity. Older hosts may omit both fields, and unknown optional
fields remain opaque extensions.
Actor snapshots optionally supply `animationFrame`, the raw signed 16-bit native
frame widened to a JSON integer, and `overheadCycle`, the raw signed 32-bit native
overhead-text counter. Their exact ranges are `-32768..32767` and
`-2147483648..2147483647`. Negative values are preserved, including when the
primary animation ID is inactive or unavailable. The fields are independently
qualified; missing data stays omitted with an unavailable-field diagnostic,
never a fabricated zero or inactive-frame normalization. Malformed supplied
values reject the response. Mutable counters do not change actor incarnation.
Older hosts may omit both, and unknown optional fields remain supported as
opaque values. These scalar fields do not infer actor coordinates, names or a
local-player role; the separately qualified role operation is described above.

Actor snapshots can independently supply `poseAnimationId` and
`idlePoseAnimationId` as signed int32 values. These are the current movement-pose
and idle/rest-pose IDs already exposed by the typed SDK, respectively. The exact
reader requires each layout key to be explicitly present and retains successful
read evidence through the actor/world publication fence. Missing or unreadable
fields carry their own unavailable-field diagnostic. Zero, negative IDs and
inactive values are preserved without borrowing the primary animation ID.
Older hosts may omit either field; no additional required capability is added.

The legacy `titan.client.topLevelBaseX.v1`, `titan.client.topLevelBaseY.v1` and
`titan.client.topLevelPlane.v1` operations remain available under their original contracts.
They read the cached SDK client state and can return zero when unavailable;
they do not qualify these exact world fields. Providers requiring qualified
geometry must use world/top-level snapshots and their field diagnostics, without
falling back to those legacy scalar operations.

Confirmed absence carries no tokens, fields or reason. Unavailable results carry
only a nonempty reason of at most 4 KiB beyond the echoed envelope, with no
fabricated identity or fields. Responses are bounded to 2 MiB UTF-8 and 32 nesting
levels. Malformed or mismatched envelopes raise protocol errors, not null actors.
Observed-instance identity does not establish allocation identity across an
unobserved removal and address reuse. Exact framework active-actor enumeration,
full local-player absence/transition semantics, mutations, render-thread reads
and actor events are separate qualifications and are not implied by these reads.

`titan.widgets.selection.capabilities.v1` accepts only `{}` and reports
`{schemaVersion:1,selectionReads:boolean,widgetReads:boolean,maxResponseBytes:2097152}`.
The source flag and exact selected target have independent qualification; target
reads require both. `titan.widgets.selection.snapshot.v1` requires exactly
`{includeWidget:boolean}` and the actual native game thread. Both operations are
read-only and admitted for the passive provider principal with its normal
generation checks and retirement drain.

A stable source returns `{schemaVersion:1,status:"present",selected:boolean}`.
With `includeWidget:true`, an active source additionally returns `widget`, the
same exact envelope described below, including its existing incarnation domain.
An inactive source returns no widget even if target reads are unavailable.
An active source can have an absent or unavailable target; neither clears the
selection flag. A state-only request never resolves a target. Source/read/session
failure returns `{schemaVersion:1,status:"unavailable",reason:string}` without
selected/target fields, never a fabricated inactive state. Reasons are nonempty
and bounded to 4 KiB UTF-8; the entire response is bounded to 2 MiB.

Native capture rereads the complete selected source around exact target lookup
and verifies the same client, installed source epoch and session revision. Any
negative native child index selects the flat component (`-1` in the envelope).
A selected child above 65535 is currently unqualified: target requests return
outer unavailable, while state-only requests can still return selected true.
Packed IDs preserve signed 32-bit bits, including an explicit unavailable target
for ranges the exact reader cannot qualify. These are observations, not selection
mutation or proof against an unobserved native ABA change.

`titan.widgets.capabilities.v1` accepts only `{}` and reports
`{schemaVersion:1,exactReads:true,maxChildIndex:65535,maxResponseBytes:2097152}`.
Optional `identity` (string), `geometry`, `childEnumeration` and `directChildReads` (booleans)
describe additional qualifications; the basic read contract does not imply
screen geometry, enumeration, mutations or widget-specific render callbacks.
Capability queries are thread-independent. The passive provider principal may
invoke the advertised widget reads, with the same admission and retirement drain
as its other owned reads; it receives no widget action authority.

`invoke("titan.widgets.snapshot.v1", "{\"args\":[packedId,childIndex]}")`
requires the actual native game thread. The required `args` member contains
exactly two numeric integers. `packedId` preserves the complete signed Java
32-bit bit pattern of a packed interface/component ID. `childIndex` is `-1`
for the flat component or `0..65535` for a direct dynamic child. An unqualified
packed-ID range is unavailable, not an absent widget; it must not be inferred
from the integer's sign. Integers use plain decimal JSON notation; forms such as
`1.0` and `1e0` are rejected consistently with the native request parser.
Strings, fractions, overflow, unknown members and
duplicate JSON keys are rejected before snapshot dispatch.

When `directChildReads:true` is advertised, a nonnegative child request may
also supply `expectedRootIncarnation`, a positive uint64 decimal string from a
previous flat-component capture. The native reader validates the actual root
fingerprint in the same identity transaction as the child's present/absent
result. A replaced, missing or unreadable root produces `unavailable`; it never
retargets the request to another parent's child. A guard on child index `-1` is
invalid. The host rejects guarded requests when this capability is absent.
Ordinary args-only requests retain their existing contract.

Query `titan.widgets.text.capabilities.v1` with `{}` to read
`{schemaVersion:1,primaryTextReads:boolean}`. When available,
`titan.widgets.text.v1` accepts the same address and optional root incarnation.
The `fields.textPrimary` string in a present response contains at most
65,536 bytes of complete, validated UTF-8, including tags and non-breaking
spaces. An empty string is valid. Unreadable text has a reason in
`unavailableFields`. The native game-thread reader rechecks the captured bytes
and the receiver before publication. Only the explicit operation copies the text.

Query `titan.widgets.layout.capabilities.v1` with `{}` for a boolean. When true,
`titan.widgets.layout.v1` accepts `{args:[packedId,childIndex],incarnation}` on the
game thread and returns `{schemaVersion:1,status:"present",parentId,selfHidden,
hidden,bounds}`. The parent of an interface's root is the widget it is opened
inside, or -1. `hidden` covers every ancestor and a widget which is not attached
to the top-level interface. `bounds` is `[x,y,width,height]` in physical canvas
pixels before any parent clips it, or null while it is not attached. Another
instance at the same address is `{status:"unavailable",reason}`.
`titan.widgets.topLevel.v1` accepts `{}` and returns the open top-level interface
group, -1 for none or null when it cannot be read.

Query `titan.widgets.items.capabilities.v1` with `{}` for a boolean. When true,
`titan.widgets.items.v1` accepts `{interfaces:[group],layers:[packedId]}`, at most 64
together, on the game thread. It returns `{schemaVersion:1,status:"present",sets}`
with one set per interface and then per layer. A set is null while that interface
or layer is not drawn. Otherwise it lists at most 4,096 rows in all of
`[packedId,childIndex,incarnation,parentId,itemId,quantity,x,y,width,height]`. A row
is a graphic widget with an item other than -1 and 6512, not hidden, whose top left
is inside the clip of the layer above it. A child of another dynamic child is
placed as a direct child.

`HostServices.titanApi()` offers Titan's own SDK to one provider plugin, or null. It
mirrors `GameApi`, which offers a provider's game API to a Titan plugin. `TitanApi.loader()`
finds the classes of `net.titan.api`, which the host defines, and nothing else. A
provider may let that plugin's own classes resolve them there and inject `client()` as
`clientType()`. The provider's realm does not see them, and a provider's own host has none.
Calls belong to the plugin whose callback makes them.

### Named game operations

A host whose `supports` answers true for `titan.game.read.<name>.v1` reads it through `snapshot` on the
game thread. The request is `{"numbers":[...],"texts":[...]}`, where both may be left out, a number is a
whole one and a token of 64 bits may be a decimal string. The answer is little-endian bytes, or null
when there is nothing to answer with now. A text is its UTF-8 length and its bytes, padded to four.
`titan.game.change.<name>.v1` is the same for an active plugin's own host, which a provider's is not,
and answers no bytes. A request which is not one of its operation throws. A widget's incarnation may be 0, for the one which is there now.

| Read | Numbers | Answer |
|---|---|---|
| `actorStates` | 0 players or 1 NPCs, world view ID, its incarnation | `0x41535431`, the number of rows, and for each 24 integers: index, incarnation (two), what is known (1 definition, 2 interaction, 4 health, 8 animation, 16 orientation, 32 spot animations, 64 position, 128 name, 256 overhead text), definition, interaction type, index and phase, health ratio and scale, animation, orientation, spot animations (count, hash, the first in the lowest of four slots), X, Y, pose, idle pose, path tile X and Y, a player's combat level, the cycles its overhead text is there for, and a zero. Then a name for each row which has one, and then an overhead text for each which has one |
| `groundItems` | World view ID, and its incarnation unless it is any | The number of piles, the server tick, and for each plane, scene X and Y, identity (64 bits), item, quantity, the ticks at which others see it and it goes, ownership, 1 when others never see it |
| `graphicsObjects` | The same | The number of objects, and for each identity (64 bits), spot animation, scene-local X and Y in 1/128 of a tile, plane, height, the client cycle it starts at, its animation or -1 when that is over, and its frame |
| `hoveredTile` | | 1, and the plane, X and Y of the top-level scene's tile which the game's own "Walk here" goes to, or 0 while its menu has no such entry. It stays while the menu is open |
| `menuPlace` | Neither, or the menu's generation and a row of it which has one of the game's own submenus | 1, and the X, Y, width and height which the game draws the open menu or that submenu with, in the canvas, or 0 while no menu is drawn |
| `loot` | The number of one of the last 64 of `LootReceived` | 1, whose it was (0 a monster's, 1 a player's, 2 as the server says), NPC ID, index, combat level, world X and Y, plane, name, and the number of items with the ID and quantity of each, or 0. Loot is followed only while a plugin of the Titan SDK hears of it |
| `minimapView` | | What is known (1 the heading, 2 the zoom), the heading the minimap is turned by in 2048 to a turn, and its pixels to a tile. What is not known is 0 |
| `localPlayer` | | 1, the world view ID of the account's own player, its incarnation (64 bits), the player's index and its incarnation (64 bits), or 0 while there is none |
| `projectiles` | | The number of projectiles, and for each identity (64 bits) and spot animation; the level it starts on, that tile's X and Y in the world, its height there and the actor it starts at; the same five of where it ends; the client cycles it starts and ends at, its slope and how far from its start it begins; its X and Y in 1/128 of a tile and its height (three doubles); its orientation, its animation or -1 when that is over, and its frame. An actor is an NPC's index and one, or a player's index and one, negative, or 0 |
| `values` | Pairs, at most 512, of 0 a varbit, 1 a varp, 2 a boosted level, 3 a level or 4 experience, and its ID or skill | For each pair 1 and its value, or 0 and 0 where it cannot be read |
| `worldEntity` | The ID of its world | 1, its X and Y in the top-level world in 1/128 of a tile, the way it is turned in 2048 to a turn, and the X and Y of the point of its own world which is there, or 0 while there is none such |
| `worldMap` | | The area which the world map shows, and the X and Y of the tile in the middle of it, or -1 three times before it has shown one |
| `topLevelInstance` | | 1 when the top-level world is built from templates, or 0, also while the account is in another |
| `friends` | | The number of friends, negative while the list is not loaded, with each one's name, previous name, world and rank, and the number of ignored players with each one's name and previous name |
| `friendsChat` | | Name, owner, the account's rank, the rank which may kick, and the number of users with each one's name, world and rank. An empty name is no channel |
| `clanChannel` | 0 the clan, 1 the ironman group, -1 the one which is visited | 1 when there is one, its name, the ranks which may talk and kick, and the number of users with each one's name, rank and world |
| `clanSettings` | The same | 1 when there is one, its name, and the number of members with each one's name, rank and the day it joined |
| `input` | | 256 bits of Windows virtual keys which are down, the mouse button (0, 1 left, 2 right, 4 middle), milliseconds since the last mouse and since the last key message, and the time of the last press of a button (64 bits, milliseconds since 1970) |
| `screenshot` | 0 | Asks for a picture of the next frame, overlays included: its number (64 bits), or 0 while too many are asked for |
| | 1, its number | 0 while it is being taken, 1 when it is there or 2 when it is not to be had, its width, height and the size of its PNG |
| | 2, its number, from where | The PNG's bytes from there, at most 2 MB |
| | 3, its number | Lets go of it |
| `watch` | The kinds of `gameEvent` which are wanted: 1 `beforeRender`, 2 `widgetListener`, 4 `actorPacket`, 8 `spotAnim`, 16 `stat`, 32 `exchangeOffer` | The kinds which this game tells of. An earlier host answers nothing, and tells of the first two |
| `widgetFamily` | Packed ID, child index, incarnation, and 0 the children which scripts made, 1 its interface's own or 2 those of the interface opened in it | The number of widgets and each one's packed ID, child index and incarnation (64 bits). Null for a widget which has gone |
| `widgetFields` | Packed ID, child index, incarnation | Which of the following are known, a bit each, and they: the modes of X, Y, width and height, the alignments of its text in X and Y, the height of a line, 1 when it has a listener, the width its content scrolls in, and its transparency |
| `widgetListener` | Packed ID, child index, incarnation, and the opcode which sets the listener, or 0 for the load listener | The number of its values, the script first, and each as 0 and an integer, 1 and a text, or 2 and a long |
| `scriptStacks` | | The size of the interpreter's integer stack and its integers, and the size of its object stack and for each place 1 and its text, or 0. Inside a script moment those of the script, and elsewhere what the last one left |
| `scriptComponents` | | Inside a script moment: the script's component and its second one, each as a packed ID and a child's index, or -1 |
| `chatLine` | A line's number | 1, its channel, name, message and sender, or 0 |
| `chatLines` | A channel | The number of its lines and each one's number, the newest first |

| Change | Numbers | Texts |
|---|---|---|
| `varcInt` | Client variable, value | |
| `varcString` | Client variable | Its value |
| `sound` | Effect, loops, delay | |
| `widgetListener` | Packed ID, child index, incarnation, opcode, script or -1 for none, and for each argument 0 and an integer, or 1 and which of the texts | Its texts |
| `scriptStacks` | Inside a script moment: the size of the integer stack and of the object stack, or -1 to keep one, the number of integers, each one's place and value, and the place of each text | The texts |
| `runScript` | Script, 1 when it may send to the server, mouse X and Y, operation, sub operation, key code and character, packed ID and child of its source and of its target or -1, and for each argument 0 and an integer, 1 and 0 for the next text, or 2 and a long. Just what the script takes, logged in and outside any other script | The text which stands for `event_opbase`, and then the arguments' |
| `chatLine` | A line's number, and 0 for its name, 1 its message or 2 its sender | The text. The chat shows it when it is next built |
| `overheadText` | 1 for an NPC, its world view ID, that one's incarnation, its index and its incarnation, or -1 and where an SDK accessor told that it is | What it says, for as long as the game shows it |
| `world` | 0 and 0 for a click nearer to the open world switcher, 1 and a world to hop to, or 2 and the world of the login screen | |
| `gameState` | 25 to load the scene anew, or 40 to drop the connection, of a player in the game. The game's next cycle begins with it | |
| `animationSmoothing` | 1 or 0, of Titan's own smoothing | |
| `idleTimer` | | The player counts as having done something just now |

The provider event `gameEvent` is `{schemaVersion:1,kind,numbers,texts}`, for the kinds which `watch` asked for.
`actorPacket` comes when the game has read a packet of the players (0) or of a world's NPCs (1 and the world, or -1),
`spotAnim` when it changed an actor's spot animations (1 for an NPC, its world and its index), `stat` when the server
told of a skill (the skill, its experience, its level and its boosted level) and `exchangeOffer` when the game replaced an
offer (slot, item, quantity, sold, price, spent, status and type).
Clans are not read inside a script moment. Ordinary plugins have the same operations as `GameData`.

A plugin whose `supports` answers true for `titan.chat.insert.v1` can add a line to its
client's chat on the game thread. The request is `{type,name,message,sender,notify}`:
a type from 0 to 65535, three texts of at most 4,096 characters without a NUL, and whether
chat callbacks hear of the line. Nothing is sent. The reply is the line's number, or
`null` when the game did not take it. The line exists when the call returns, and its
`chatMessage` event, when there is one, comes first. The game shows Windows-1252 text,
and any other character becomes a question mark. A provider's own host does not have
the operation. A `chatMessage` event may carry `uid`, its line's number.

A plugin whose `supports` answers true for `titan.widgets.write.v1` can change one exact
widget on the game thread. The request is `{id,child,incarnation,write,integers}`, with
`text` where the write takes one: the packed id, the child index or -1, the incarnation
an exact read reported, and the number of what to do. A number from 1000 to 1307 is the
game's own component opcode, with the values its scripts give it: 1000 position, 1001
size, 1003 hidden, 1005 and 1006 clicks and scrolling stop there, 1100 scroll position,
1101 colour, 1102 filled, 1103 transparency, 1104 line width, 1105 to 1107 sprite, its
angle and tiling, 1108 model, 1112 text, 1113 font, 1114 text alignment, 1115 text
shadow, 1116 border, 1117 sprite shadow, 1118 and 1119 flips, 1120 scroll size, 1213
item, 1300 option (counted from 1), 1303 and 1304 dragging, 1305 name, 1306 target verb
and 1307 no options. 10000 plus an index sets one field by itself: original x, y, width
and height, the x, y, width and height modes, the text's x and y alignment and line
height, item, item quantity, has-listener, relative x and y, width, height, scroll
width, scroll height, scroll x and scroll y. 20000 gives the widget its size and
position again, 20001 does so for everything in a layer, 20002 creates a child with
`[type,index]` (index -1 is after the last) and 20003 deletes a layer's children. The
last two take a widget which is no child itself. A text holds at most 4,096 bytes. The
reply is `absent` where the widget is gone or is another one, `refused` where the game
build or the values do not allow it, and otherwise the created child's index or 0. The
change is made when the call returns. A provider's own host does not have the operation.

Query `titan.cache.files.capabilities.v1` with `{}` for a boolean. When true, the
`titan.cache.file.v1` snapshot accepts `{index,group,file}` and `titan.cache.fileIds.v1`
accepts `{index,group}`, on any thread. They copy files of the game's own local cache,
each checked against that cache's reference table. The snapshot's first byte is 0 for
present, followed by the file, 1 for a file which the table does not list or 2 for a
cache which is not open yet. The IDs arrive as `{schemaVersion:1,status:"present",ids}`
or with status `absent` or `notReady`. A listed file which cannot be read is unavailable.

The optional `widgetLoaded` event carries `{schemaVersion:1,groupId}` for an interface whose
load listeners have run, the top-level one included. `widgetClosed` carries
`{schemaVersion:1,groupId,modalMode,unload}` for one about to close, whose widgets are still
there. Both come on the game thread.

The optional `sceneObjectChanged` event is one change which the game has made of a loaded
scene's walls, decorations, floor decorations or scenery, delivered on the game thread as it
makes it: `{schemaVersion:1,worldViewId,incarnation,changes,plane,x,y,layer,removed,added}`.
`incarnation` and `changes` are decimal strings, the second the number of this change in its
scene, so a gap says that one was missed. `layer` is 0 for a wall, 1 for a decoration, 2 for
scenery and 3 for a floor decoration. `removed` and `added` are null or
`{id,tag,identity,placedPlane,sizeX,sizeY,config,nonInteractive}`, with `tag` and `identity` decimal
strings. An identity tells an object from others until it is removed and is no address.

The optional `scriptMoment` event is a script of the game's, about to run or about to return,
delivered on the game thread and inside the interpreter. It carries
`{schemaVersion:1,scriptId,phase,nested}` with `phase` `before` or `after`, and `arguments` before a
script the game starts whose arguments are all integers. A host offers it only where it follows
every script, those called by others included. A provider names the scripts with
`titan.scripts.interest.v1`:
`{revision,before:{every,ids},after:{every,ids}}`, with a growing positive decimal string and at most
4,096 ids from 0 to 65535 for each phase. Each replaces the one before. `every` is for a listener whose
ids cannot be known, and the game runs tens of thousands of scripts in a frame. Nothing is named once a
provider retires.

During the event the named operations `scriptStacks` read and change the interpreter's stacks. A plugin's own host, and not a
provider's, takes `titan.scripts.setArgument.v1` with `{index,value}`, counted among the script's integers, and answers whether
there was such an argument. The event of a script which the game started has `request`, when the host can read it: `arguments`
(numbers, texts, `{"long":"1"}` or null), `mouse`, `op`, `subOp`, `key`, `source` and `target` (a packed ID and a child's index, or
-1 twice) and `opbase`. The interpreter cannot start another script meanwhile. Actions wait for the next client tick.

`titan.scripts.replace.v1` has a provider's scripts in place of the game's: `{revision,scripts:[{id,file}]}`, at most 256, each a
file of the game's script format in hexadecimal. An empty list ends it, and so does the provider's retirement. The instruction
6599 of such a script takes a text off the stack and tells of it: a `scriptMoment` whose `phase` is `callback` and whose `name` is
that text, with the calling script's number. The reply is whether the host can replace scripts.

Such a script may call back for every item of a container, so a provider can say which calls back it hears of:
`titan.scripts.interest.v1` takes an optional fourth member, `callbacks:{every,names}`, with at most 1,024 names of 1 to 256
bytes each. The text is taken off the stack either way. A call back whose name no provider named is not told of, unless some
provider said `every` or has an interest without the member, which hears of them all as before the member existed. A host from
before it refuses the request, and the provider sends the three members again.

With `stacks:true` beside `every` and `names`, the interpreter's stacks come with each call back the provider is told of: the
event has `stacks`, the bytes which `titan.game.read.scriptStacks.v1` answers with at that moment, in lower-case hexadecimal,
after its `name`. A handler reads them before anything else, and asking for them was a second trip for every item of the bank.
They come only where every provider with a script interest takes them, as one event goes to them all, and only while they are
no more than 8,192 bytes; a provider reads longer ones itself. A call back's event is written
`{"schemaVersion":1,"scriptId":N,"phase":"callback","nested":B,"name":"...","stacks":"..."}` in that order, and a host or a
provider may read one which is so written where it stands and parse any other.

Query `titan.quests.status.capabilities.v1` with `{}` for a boolean. When true,
`titan.quests.status.v1` accepts `{id}` on the game thread and returns `{id,status}`
with the game's signed quest status, or null when its script did not run.

Action arrays are a separate opt-in read: query
`titan.widgets.actions.capabilities.v1` with `{}` for
`{schemaVersion:1,actionReads:boolean,maxActions:255,maxResponseBytes:2097152}`.
When available, `titan.widgets.actions.v1` accepts the same exact widget request
and returns the same identity-fenced envelope, including independently optional
`fields.rawActions`. Those are ordered, complete UTF-8 native strings; zero
length and empty strings do not establish Java null semantics. No labels are
trimmed or normalized. Counts above 255, strings above 64 KiB, aggregate text
above 256 KiB, unreadable values and changing storage fail explicitly. Ordinary
snapshot/selection/bulk-child queries do not read the array, and no additional
request keys or fields are sent to strict older hosts. The optional operation
has the same native game-thread and owner-lifetime restrictions as snapshots.

Query `titan.widgets.children.capabilities.v1` with `{}` for bulk-read support.
When `childSnapshotReads` is true, `titan.widgets.children.snapshot.v1` accepts
`{"packedId":123,"expectedRootIncarnation":"1"}` on the native game thread.
A `present` response echoes the packed ID and root incarnation and includes a
`children` array in direct-slot order. Each element is a positive uint64 decimal
identity string or a confirmed null slot. The response has schema version 1 and
fits in 2 MiB. Each call captures 1–65,536 slots in one native request.
A changed root or slot, failed read or zero-length table yields `unavailable`
with a reason. The source cannot distinguish an empty backing array from a null
backing array. The operation covers flat parent widgets with direct children.

Flat snapshots may include `fields.directChildCount`, an exact integer in
`0..65536`, or a per-field unavailable reason. Its count is revalidated with the
root observation. This qualifies direct lookup bounds, not a distinction
between null and empty child arrays or any nested-parent hierarchy. Content/count
changes retain root identity; confirmed child removal and root replacement
retire the affected child identities. The native registry admits at most 65,536
root/child records and reports exhaustion rather than evicting live identities.

Snapshots may contain independently qualified `fields.relativeX`, `relativeY`,
`width` and `height`: raw signed 32-bit computed layout values. Positions are
relative, not canvas coordinates, and dimensions are not original layout inputs.
The separate `fields.originalX`, `originalY`, `originalWidth` and
`originalHeight` are independently read signed 32-bit layout inputs, never
reconstructed from computed values.
There is no clamp or synthetic default. Missing qualification/read failure is
reported per field; the other widget values and identity remain usable. The
current native implementation requires an exact-image evidence record in
addition to matching exports; generic adjacency-derived offsets alone do not
admit these fields. Canvas bounds and visibility have separate source requirements.

`fields.scrollY` reports the widget's current vertical scroll position as a signed
32-bit integer. It uses the same field as Titan SDK `Widget.scrollY()` and
requires a successful read on the requested widget instance. Zero is a valid
position. Consumers can check `unavailableFields.scrollY` for a read failure.
Older providers may omit the field. Scroll height and scroll writes require
separate operations.

Snapshots may also contain `fields.modelType` and `fields.modelId`: the widget's
raw **primary** model identity values, each a signed 32-bit integer. Zero,
negative values and both int32 extremes remain data; neither field is filtered
by the other or converted to an enum/default. Availability is independent per
field, so one primary value can be present while the other has an
`unavailableFields` reason. Older snapshots may omit both fields altogether.
The native qualification requires the analyzer's complete four-field
primary/alternate model identity export and its installed binary/epoch contract;
it does not guess primary fields from offset order or a partial pair. This read
does not expose alternate identity values, choose an active model, construct a
model, or qualify animations, sprites, geometry or mutation methods.

The generic host retains an open, opaque `fields` map: adding these optional
values does not introduce a field allowlist. Consumers must validate each model
value as a plain-decimal signed int32 before exposing a framework getter, and
must report missing/unavailable values rather than fabricating zero or `-1`.

The owned JSON response is limited to 2 MiB of UTF-8 and 32 nesting levels.
Every envelope contains exact `schemaVersion:1`, matching `packedId` and
`childIndex`, and `status` (`present`, `absent` or `unavailable`). A present
snapshot adds a positive uint64 decimal-string `incarnation`, a `fields` object
and an `unavailableFields` object mapping omitted field names to reasons. Values
remain raw; this generic host performs no framework-specific text normalization,
slot compaction or default substitution. A field cannot occur in both objects.
Confirmed absence contains no identity or field objects. An unavailable result
contains a nonempty `reason` and no fabricated identity or fields. Providers must
distinguish those cases and validate the field-specific representation before
exposing it. An invalid/mismatched envelope raises a protocol error, never null.
These operations reuse the existing JSON host request boundary; they add no
public SPI method, native payload layout or SDK ABI requirement.

`titan.items.capabilities.v1` reports `{schemaVersion:1,exactSlots:true,
identity:"observed-native-instance",mutationEvents:false,maxSlots:65536}` when
the exact native container reader is available. `titan.items.container.v1` takes
one signed 32-bit container ID in `args` and requires the actual game thread.
Its owned response includes `schemaVersion`, `containerId` and `status`:
`present`, `absent`, or `unavailable`. Only a confirmed missing hashtable key is
`absent`; missing layout, unreadable cells or invalid vector shape are
`unavailable` with a `reason`. A present result adds opaque `incarnation`,
`capacity`, `itemIds` and `quantities`; both dense arrays have exactly `capacity`
entries and preserve native values without slot filtering or quantity defaults.
Responses are bounded to 2 MiB; captures do not use the shared Java scratch buffer
or the truncated legacy fixed-array container ABI.
The incarnation stays stable through content changes and changes after observed
instance replacement, confirmed absence or a session reset. Removal and address
reuse entirely between observations cannot be distinguished without a verified
mutation hook. This operation does not promise mutation events or linked-node
traversal. Providers must not treat an unavailable/stale capture as an empty
container or silently rebind a retained object to another incarnation.

The independent optional `titan.items.containers.capabilities.v1` operation
accepts `{}` and returns a literal boolean. When available,
`invoke("titan.items.containers.v1", "{}")` captures complete owned container
membership on the actual native game thread, without reading item-slot arrays.
A present response is `{schemaVersion:1,status:"present",tableIncarnation:"…",
entries:[{containerId:93,incarnation:"…"}]}`. IDs are signed 32-bit integers;
all tokens are opaque, positive canonical unsigned 64-bit decimal strings.
Container tokens use the same identities as the existing per-ID operation.
IDs and tokens are unique within a capture, and the table token is distinct
from every container token. The table identity survives membership changes;
observed table/source replacement or a session reset retires it. An observed
initialized empty table is `present` with `entries:[]`. Unreadable, incomplete
or unqualified membership is `{schemaVersion:1,status:"unavailable",reason:"…"}`,
never a partial list or inferred empty table. There is no `absent` table result.
This route promises no encounter order, live iterator, node links, or mutation
events. Requests are bounded to 256 UTF-8 bytes, responses to 512 KiB, membership
to 4,096 entries and diagnostics to 4,096 UTF-8 bytes. Unknown/duplicate fields,
duplicate identities and oversized lists are rejected. Passive provider and
ordinary plugin calls retain their exact lifecycle admission through capture;
retirement or activation replacement rejects a pending reply. Older hosts
simply lack this optional operation; the per-ID contract is unchanged.

`titan.items.sprite.capabilities.v1` reports `{schemaVersion:1,ownedArgb:true,
maxDimension:512}` when the existing native sprite contract is installed.
Implementation availability is separate from an individual request's readiness.
`snapshot("titan.items.sprite.v1", json)` takes
`{"args":[itemId,quantity,border,shadowRgb,quantityText]}`: exact integers with
item ID 0..65535, positive quantity, border 0..2, RGB24 shadow and quantity text
0 (hidden) or 1 (always). Invalid request shapes/ranges throw. Calls may run on
workers: cache misses queue host-owned work, never run the native renderer on
the caller, and never synchronously wait for rendering.

The owned binary response is little-endian SPR1, with eleven 32-bit header words
in this order: magic `0x31525053`, header bytes `44`, status, width, height,
x offset, y offset, canvas width, canvas height, pixel format, pixel count.
Pixel format 1 means row-major **straight ARGB32**, not premultiplied pixels.
Ready status 0 is followed by exactly `pixelCount` 32-bit pixels; count equals
width times height. Width/height and canvas dimensions are 1..512; offsets are
-512..512. The maximum packet is 1,048,620 bytes. Each call owns its data.

Other statuses are 1 pending, 2 unsupported, 3 invalid, 4 not ready, 5 failed,
6 buffer too small. These responses contain only the 44-byte header, with zero
image dimensions/offsets/count and pixel format 1. Malformed native results
become failed, never an invented image. Pending and not-ready may be retried on
a later real client callback; providers must preserve terminal errors. Sprite
requests do not imply native drawing, custom zoom, note conversion or additional
quantity-text modes.

`titan.items.definition.capabilities.v1` advertises independent `definitions`
and `parameters` booleans with `schemaVersion:1`. The current native response is
`{schemaVersion:1,definitions:<live reader available>,parameters:false,
cacheMatchesGame:false,verifiedDiskParameters:<disk parameter archive available>,
reason:<configuration-reference attestation gap>}`. `verifiedDiskParameters`
reports internal archive integrity only; it does not enable live parameters.
The live definition response supplies the ID and any successfully captured
name, inventory actions, subops, stackability and linked note ID. The
`stackable` flag tests the raw definition mode for equality to one, and
`linkedNoteId` preserves the signed link including -1. Cache-derived price,
membership, tradeability and template links require an active-game cache
reference. Parameter requests report the reason when that reference is missing.
`titan.items.definition.v1` takes `{"args":[itemId]}` and
`titan.items.param.v1` takes `{"args":[itemId,parameterId]}`. Both require the
native game thread. Item IDs are exact integers 0..65535; parameter IDs are
0..16777215. The host forwards the bounded native JSON result unchanged;
framework-specific interpretation, defaults and unsupported fields belong to
the provider. These queries do not expose mutable native definitions or allow
arbitrary member access.

`titan.npcs.definition.capabilities.v1` is an independent optional strict boolean
capability, queried with `{}`. `titan.npcs.definition.v1` takes only
`{"id":42}` on the native game thread. The request preserves signed int32 IDs;
the native source explicitly reports negative IDs unavailable. Requests are
bounded to 256 UTF-8 bytes and responses to 8192 bytes. Older hosts return no
grant without changing existing actor-composition capabilities.

A present response has `schemaVersion:1`, `status:"present"`, the
requested `id`, a canonical positive uint64 decimal `incarnation`, positive
int32 `size`, and either `name` or `nameUnavailable`. Names are complete nonempty
printable ASCII of at most 255 bytes. `nameUnavailable` is a nonempty diagnostic
of at most 4096 UTF-8 bytes; it preserves independently qualified ID and size.
Its only optional field is `noTransforms:true`, which records positively observed
absence of transforms on that base definition. Missing transform metadata leaves
this field absent; `false` and `null` are invalid. The marker is independent of
name availability and cannot appear on an unavailable response. A later positive
contradiction in transform presence retires that definition incarnation rather
than changing an earlier capture's meaning.
An unavailable response has only `schemaVersion:1`, `status:"unavailable"` and
a nonempty `reason` of at most 4096 UTF-8 bytes. A failed lookup never establishes
absence or supplies a null/default definition.

This source uses the separately available live NPC-definition getter and copies
the base definition while its temporary strong reference remains held. It does
not substitute the legacy configuration/object getter, a disk cache, a spawned
NPC, or a transformed definition. Its identity journal is shared with qualified
actor-derived base compositions; no native pointer is exposed. The returned
fields are owned captured values, not mutable native backing. Current provider
admission, load/activation and native source checks surround each lookup.
The protocol adds no definition writes, transform resolution or other fields.
Checked test exports containing a getter do not establish its availability in
an installed runtime; a missing or unqualified live source keeps this capability
false.

`titan.objects.definition.capabilities.v1` is an independent optional strict
boolean capability, queried with `{}`. `titan.objects.definition.v1` takes
`{"id":42}` or `{"id":42,"expectedIncarnation":"9"}` on the native game
thread. IDs retain the full signed int32 domain; they are not clamped or
normalized. The optional guard is a canonical positive uint64 decimal string.
Requests are at most 256 UTF-8 bytes. Unsupported hosts decline this operation
without changing item or actor capabilities.

The response is an owned object with `schemaVersion:1`, matching `id`, and
`status:"present"` or `status:"unavailable"`. Unavailable has only a `reason`
in addition to those fields; a failed lookup does not establish null/absence.
Present responses include a positive `incarnation`, plus `fields` and
`unavailableFields`. Every field is represented exactly once, by its value or a
nonempty diagnostic: `name` (string), `sizeX`, `sizeY`, `varbitId`, `varpId`
(signed int32), and `noTransforms` (only `true`). The latter requires a fully
checked zero transform count and null transform array; unknown or present
collections yield a diagnostic, not false or an invented empty array.
A host may also say `transforms`, in either map: what the object can be, its default
last, as 1 to 1,024 signed int32 with -1 for nothing. It never appears with `noTransforms`.
Strings/diagnostics are bounded to 4096 UTF-8 bytes and the response to 16384.

The source is the live base object-definition lookup. A host that derives the
ID from the native Examine operation requires a stable zero transform count.
The current name source qualifies complete nonempty printable ASCII. An empty
name or an unsupported encoding leaves the other captured fields usable.
Tokens track the cache owner, base pointer, ID and independently observed
immutable fields. A change to a known value, owner or source retires the token.
Identical storage recycled wholly between observations can be indistinguishable.
No pointer is serialized, and a guarded
lookup cannot rebind an old token. Native source checks and counted managed
owner/activation checks reject replies spanning retirement. This grants no
definition mutation or access to unlisted fields.

`titan.objects.definitionActions.capabilities.v1` probes the optional action
vector with `{}`. On the game thread, `titan.objects.definitionActions.v1`
accepts `{"id":42,"expectedIncarnation":"9"}` and uses the same unavailable
envelope as a definition lookup. A present response includes the schema
version, status, matching ID and incarnation, and an owned `actions` array.
Slots retain their native order. The native source qualifies up to 256
complete printable ASCII labels and an allocated empty vector. Ambiguous
slots, source replacement and a response exceeding 16 KiB make the read
unavailable. Raw labels and game-selected menu operations have distinct APIs.

`titan.objects.definitionActionCount.capabilities.v1` probes support for reading
the action vector length. Its game-thread operation, `titan.objects.definitionActionCount.v1`,
uses the same retained selector and unavailable response as the label query.
A present response contains `schemaVersion`, `status`, `id`, `incarnation` and an
integer `count` from 0 through 256. The count includes sparse slots. Titan checks
the vector header before and after rechecking the definition's identity, without
copying individual action labels. A verified allocated empty vector has count zero.

`titan.canvas.geometry.v1` returns `{x,y,width,height,deviceScale,visible,focused}`
for the native game window. Canvas and input positions retain native game units;
deviceScale is separate metadata for UI transforms, not an extra scaling step.

`snapshot("titan.scene.snapshot.v1", { "args": [worldViewId, knownGeneration] })`
returns the existing owned SCN1 scene packet. Null means unavailable; an empty
packet means that the caller already has this generation. The packet contains
one coherent scene generation, rather than references to native transient data.

Typed raster, Swing and input functions are optional. Check `raster`,
`swing.embedding`, `swing.window`, `input.observe`, `input.synthetic` and `input.consume`
separately. Native ownership checks still apply to every surface and approved
parent window; a supported embedding implementation does not authorize an
arbitrary HWND. Swing functions require the EDT. Input observation/synthesis does
not imply that consuming native input has been qualified.

Windows mouse and wheel observations include client coordinates `x`, `y`
and the signed physical desktop coordinates `screenX`, `screenY` captured with
the message. Wheel records carry `wheelDelta` in AWT direction, measured in
120ths of a detent, alongside whole-detent `wheelRotation`, AWT `scrollType`
and the Windows `scrollAmount`. Whole detents accumulate separately for each
axis and observer activation. A horizontal wheel includes the AWT Shift
modifier. Older hosts may omit the desktop point and precise wheel fields.

For buttons 1–3, `clickCount` follows the Windows multi-click time and
distance settings. A completed gesture produces a release followed by a
separate clicked observation (AWT ID 500). Its `pressSequence` identifies the
physical press on this subscription. The queue discards incomplete gesture
history after loss or retirement. A drag cancels the pending click.

`registerWindow(Window)` admits a real modeless top-level window to the initiating
plugin's game and activation. Call on the EDT after creating its peer but before
showing it. Native admission cloaks the peer before publication; only the owning
game's controller may authorize its presentation. An owner-process native worker
applies controller requests without waiting for the EDT. This requires a live controller presentation
session and does not reparent the AWT window or convert its coordinates. A non-null
AWT owner must have an active UI registration for the same plugin and activation.
UI root claims use weak identity and retain retirement tombstones, so a pending
cleanup cannot lend another plugin's frame to a new dialog.

Close the returned registration from any thread to revoke native presentation,
then dispose the Window on the EDT and await disposal before completing plugin
cleanup. Closing a registration does not dispose the peer. Native retirement
failure throws and retains the registration for cleanup; it must not be reported
as success. Disable/unload also close tracked registrations, including handles
returned after their activation retired. The passive provider context cannot
register windows. Other top-level windows need their own registration, and a
registered Dialog must remain modeless.

Plugin contexts may use `titan.input.synthetic.open.v1` with `{}` to open a
synthetic-only channel without adding another physical observation stream.
This requires an enabled plugin activation and does not itself send input.
Provider contexts deny it; shared observers must route attributed synthetic
actions through the correct active plugin context.
After opening, `titan.input.synthetic.activation.v1` with `{}` returns the exact
positive native activation as a JSON integer. Preserve it without floating-point
conversion, revalidate the captured plugin activation, and include its decimal
string as `activation` in every synthetic submission. Native admission compares
the token at delivery, preventing an old press from entering a newly enabled
activation. Provider contexts deny this operation too. The older `inputOpen()`
contract is unchanged.

Hosts may additionally advertise `input.focus`. After `inputOpen()`, opt in with
`invoke("titan.input.focus.open.v1", "{}")`, which returns the JSON boolean `true`
on admission. The same FIFO then includes `{kind:"focus",id:1004|1005,
origin:"native-window",temporary:false,sequence,when,modifiers}` records. Older
observers that do not opt in receive only their existing input kinds. These are
actual game-window focus/foreground notifications, excluding the sidebar and
the host's synthetic WndProc path. Opening does not emit an initial-state event;
repeated Windows notifications with unchanged focus are coalesced. `inputClose()`
revokes the opt-in along with the observation activation. Polling and EDT delivery
remain asynchronous, and existing dropped-record counters/queue bounds apply.
Focus observation does not authorize focus synthesis or consumption, or qualify
RuneLite's synchronous `FocusChanged` callback timing.

The optional `input.focus.watermark` capability (also discoverable by the exact
operation name) adds `invoke("titan.input.focus.watermark.v1", "{}")`. It returns
raw canonical decimal text from `0` through `9223372036854775807`, not a quoted
JSON string. Each physical focus record carries `focusOrdinal` as a **positive
decimal string** in the same domain. The native source allocates that ordinal
at ingress, before OS queries, reentrant cleanup, owner snapshots or enqueueing.
One ordinal is shared by all observers of that message. Duplicate notifications
and dropped observations may leave gaps; the ordinal is not a loss counter.

The source counter never resets on input close/open, plugin activation, profile
or provider replacement within that native client lifetime. A listener can open
observation and read its watermark before registering, then reject queued
records whose ordinal is at or below that watermark. The query neither opens
input nor emits an initial state, and needs no EDT or game-thread dispatch. It
requires a current admitted native owner and the installed observation hook;
provider startup may read it before callbacks become ready. Exhaustion refuses
the optional query and stops attaching ordinals, without changing ordinary AWT
records or physical input. Older hosts do not advertise this capability; neither
queue-local `sequence` nor wall-clock `when` is a substitute for it. Provider and
plugin lifecycle fences still apply independently of the source watermark.

The optional provider event `canvasSizeChanged` observes physical native game
drawable dimensions. Its bounded JSON payload is exactly
`{"schemaVersion":1,"width":800,"height":600,"sequence":"17"}`. Width and height
are nonnegative signed 32-bit integers, including positively observed zero.
Sequence is a canonical positive decimal string through `9223372036854775807`;
the payload limit is 256 UTF-8 bytes. The source observes actual `WM_SIZE`
notifications on the process's inner game drawable, copies its client rectangle,
and drains distinct successive dimensions in chronological order on the game
thread. A/B/A changes are retained; equal successive dimensions are suppressed.
It does not derive events from overlay sizes, a parent window, an EDT geometry
poll, or synthetic input, and does not replay an initial size.

`invoke("titan.events.canvasSizeChanged.watermark.v1", "{}")` returns canonical
nonnegative signed 64-bit decimal text, with the same limits as the focus
watermark. It is independently optional and available to an admitted owner
without entering the game thread. A sequence is reserved before the native
rectangle read. A subscriber must reject event sequences at or below its
registration watermark, so queued observations cannot be lent to a newly enabled
activation. Native capture also retains the original counted provider endpoint;
runtime replacement cannot retarget a queued event. The counter does not reset
on plugin or provider replacement. Gaps are allowed and are not loss counters.

This source describes observed native drawable changes, not individual Java
canvas-width/height field assignments or their synchronous script ordering.
Read failure, source-window destruction, sequence exhaustion or overflow of its
1024-entry FIFO makes the source and watermark unavailable until the native
runtime restarts. A provider may preserve plugin-origin manual events when the
optional source is absent, but must not fabricate native notifications. Once the
source is granted, malformed ordering metadata must fail closed.

Key observation records may also include `keyLocation`, using AWT's integer
locations 0..4 (unknown, standard, left, right, numpad). This is additive metadata:
the existing `keyCode` and `keyChar` are unchanged. `WM_CHAR` always has location
0; older hosts omit the field. It does not change the synthetic-input contract.

`setEmbeddedPhysicalBounds` has its own `swing.embedding.physicalBounds`
capability and accepts native pixels; the host derives and verifies peer scaling.
The original `setEmbeddedBounds` retains its logical-bounds contract.

Dynamic navigation uses `registerPanel(PanelDescriptor)`, `removePanel(id)` and
`openPanel(id)`, or the equivalent `titan.panel.register.v1`,
`titan.panel.remove.v1` and `titan.panel.open.v1` operations. Requests contain
`id`; registration also contains `title`, `priority` and optional
`iconPngBase64`. Native ownership, parent validation and readiness govern when a
panel can appear. Register/open require the current enable activation.

The optional `titan.panel.commands.v1` capability adds toolbar actions and popup
menus. Use the additive `PanelDescriptor` constructor with a fresh lowercase
32-hex `registration`, `actionOnly` and `hasPopup`. Token-bound entries use
`removePanel(id, registration)` and `openPanel(id, registration)`; action-only
entries never create a panel. Older hosts reject the optional API and retain
the original descriptor behavior.

`onPanelCommand` receives `popup` or `command` on the EDT, without the runtime
lifecycle monitor. The request carries the native identity (`pid`, `epoch`,
`pluginId`, `loadGeneration`, `panelId`, `registration`), authenticated
`controllerPid`, and a positive numeric `requestId` up to `Long.MAX_VALUE`.
Requests must increase within a registration and authenticated controller PID.
A newly authenticated controller starts a fresh sequence; queued work and late
completions belonging to the previous controller are rejected. A popup completion contains
`status: "ready"`, a fresh 32-hex `menuToken`, and up to 32 ordered `commands`
with consecutive decimal-string `id` values starting at `"0"` and nonempty
single-line `title` values up to 256 UTF-8 bytes. The same token and command ID
select the captured callback once. Empty `menuToken` and `commandId` select an
action-only entry's primary callback. Providers must consume snapshots before
calling user code. A popup query snapshots current metadata without executing
its commands.

Completion status is `ready`, `stale` or `error`; pending/polling belongs to the
native transport and never repeats a click. The host supplies exact identity,
request ID and operation echoes, validates any provider-supplied echoes, and
bounds escaped command completions to 32 KiB. Counted admission spans queued
EDT work and its completion stage. Retirement rejects unstarted callbacks,
suppresses obsolete results and retains the provider realm until entered work
finishes. Token-bound panel lifecycle identities also carry `registration` so
late geometry cannot attach a replacement entry.

`onPanelLifecycle` is entered on the Swing EDT outside the game/lifecycle monitor.
Its JSON contains `operation` (`activate`, `resize`, `deactivate`), `identity`
(`epoch`, `loadGeneration`, `pid`, `pluginId`, `panelId`, a 32-character lowercase
hex `activation`), `revision`, decimal-string `parentHwnd`, `width`, `height` and
`dpi`. It returns a completion stage of JSON with `status` (`ready`, `error`,
`stale`) and optional `error`. Never join an unfinished stage or wait for another
host callback. The host bounds pending UI work, fences stale activations, and
returns the completion against the original native identity. Swing state belongs
to the EDT; these callbacks can overlap game callbacks. JNI JSON is escaped to
ASCII so supplementary Unicode survives modified UTF-8 transport.

The generic configuration operations persist opaque string keys and values:
`titan.config.get.v1` takes `{key}` and returns a string or null;
`titan.config.put.v1` takes `{key,value}` and returns the previous value;
`titan.config.remove.v1` takes `{key}` and returns the removed value;
`titan.config.keys.v1` takes `{prefix}` and returns matching full keys.
`titan.profile.current.v1` currently returns
`{id:"global",displayName:"Titan global",accountScoped:false}`. This is an
explicit global Titan configuration profile, not a fabricated account profile.
Account-scoped queries remain unsupported until a validated selection API exists.
Storage is isolated by owner/profile under the user's `.titanclient/provider-config`
directory, with hashed path components, OS file locks and atomic replacement.
Changes reload current data, so concurrent game processes preserve each
other's edits. A read uses the document which was checked last while the file's time
and size are still those, and only once the file is two seconds old, which is when
its time tells it from the next one. Corrupt files are reported and preserved. Configuration remains
available during orderly asynchronous cleanup; the context expires afterward.

Optional provider-shared configuration uses the same global profile and string
semantics through the **owning plugin's** `HostServices`:

| Operation | Request | Result |
| --- | --- | --- |
| `titan.config.shared.get.v1` | `{key}` | String or null |
| `titan.config.shared.put.v1` | `{key,value}` | Previous string or null |
| `titan.config.shared.putIfAbsent.v1` | `{key,value}` | Existing string, or null when inserted |
| `titan.config.shared.compareAndSet.v1` | `{key,expected,value}`; expected/value are strings or null, all fields required | Boolean match |
| `titan.config.shared.getMany.v1` | `{keys:["group.x","group.y"]}` | One snapshot containing every requested key, with a string or null per key |
| `titan.config.shared.compareAndSetMany.v1` | `{expected:{"group.x":null,"group.y":null},values:{"group.x":"5","group.y":"8"}}` | True if the entire tuple matched and was committed |
| `titan.config.shared.remove.v1` | `{key}` | Removed string or null |
| `titan.config.shared.keys.v1` | `{prefix}` (prefix defaults to empty) | Sorted full keys |
| `titan.config.shared.snapshot.v1` | `{}` | `{revision,values}`: every key and its string, and the number they were read under as a decimal string, `"-1"` where there is none |
| `titan.config.shared.migrate.v1` | `{}` | `{alreadyMigrated,imported,conflicts:string[],skippedDeleted:string[],profile?,valuesHash?}` |

The registry binds the namespace to the provider ID validated at admission,
independently of later provider callbacks. Caller-supplied scope and all extra
request fields are rejected. Plugins of that provider share one store; different
providers, ordinary Titan plugins, and legacy per-plugin stores remain separate.
An unbound host delegate and the passive `ProviderContext.host()` do not expose
these operations. A scoped host permits orderly cleanup calls and rejects calls
after successful or failed retirement. Admitted storage transactions drain before
successful realm closure, without holding the owner monitor during disk I/O.

A provider whose plugins read settings as often as they read a field can keep them in memory. The owning plugin's
`HostServices.sharedConfigurationRevision()` is a number which changes whenever the shared document may have changed:
at once when this game writes it, and when another game does, or somebody edits the file, as soon as the file system
tells the host, which is some milliseconds later. What `titan.config.shared.snapshot.v1` answered is what the document
holds for as long as the number is the one which came with it. The number is read without a request and costs about
0.03 us, where one `get` looks at the file and costs about 24 us; a watched file is still looked at once a second, in
case the file system did not tell. It is negative where the host cannot watch the folder, and for a caller whose
`get` it would refuse: that caller asks for every setting, and is answered or refused, as before. What a provider
holds can be some milliseconds behind another game's write. The operations which must not be (`putIfAbsent`,
`compareAndSet`, `compareAndSetMany`, `migrate`) read the file under its lock as they always did.

Migration reads only the calling owner's legacy file, merges missing keys, and
atomically records its one-time marker with the imported values. Existing shared
values win; differing keys are retained in `conflicts`. Legacy files remain
untouched. Repeated migration returns `alreadyMigrated:true`, `imported:0`, and
the original diagnostics. There is no per-read legacy fallback. Explicit removal,
including removal of an absent key, leaves a tombstone so later migrations cannot
restore it; these skipped keys appear in `skippedDeleted`. An explicit `put` or
`putIfAbsent` can replace that tombstone. Even an absent source gets a marker, so
later writes by an older runtime do not silently reimport old configuration.

Hosts can also return the profile ID and a lowercase SHA-256 fingerprint of
its visible shared keys and values. Both come from the same locked transaction.
A changed fingerprint lets a provider invalidate cached values that another
game tab edited between migrations, even when the migration imported zero keys.
Older hosts omit these fields.
`compareAndSet` atomically compares the current value (null means absent) and
writes the replacement only on a match. A null replacement reserves a deletion
tombstone; a string replacement clears one. Mismatches leave the file unchanged,
and an identical matched value returns true without a needless rewrite. Both
strings use the normal value bounds. Providers can repair malformed defaults
against the value actually observed, preserving a concurrent user's correction.

Providers can discover the two optional batch operations independently. A batch
accepts 1–32 distinct keys in the calling provider's current profile. Both maps
in a write must contain exactly the same keys. A null expectation means absent,
and a null replacement creates a deletion tombstone. One file transaction covers
the comparison and all replacements. A stale comparison returns false. Invalid
keys or values and storage failures reject the whole write. This lets two game
tabs edit a multi-field setting without committing a mixture of their choices.

Shared files live under `.titanclient/provider-config/shared` with hashed profile
and provider components. Every transaction reloads the latest document under the
same OS lock before atomic replacement. `putIfAbsent` and migrations therefore
have one winner across game processes. Keys and prefixes are at most 512 UTF-8
bytes, values at most 1 MiB, and a document at most 4 MiB. Values plus tombstones
are bounded to 4,096 entries; migration markers are separately bounded to 1,024
(5,120 total entries). Each marker retains at most 4,096 bounded diagnostic keys,
also subject to the document limit. This permits a full legacy 4,096-key file to
migrate when its encoded document fits. Invalid/corrupt source or target data and
oversized transactions fail before replacement; the existing files survive.
Legacy and shared requests allow up to 6 MiB + 4 KiB of encoded JSON, or
12 MiB + 4 KiB for shared `compareAndSet` and `compareAndSetMany`. These bounds include worst-case JSON
escaping of the decoded values and key; they do not raise the 1 MiB decoded value
limit or the 4 MiB persisted document limit.
Account-profile selection and cross-process change notifications remain outside
this contract. Providers must attribute writes to their actual plugin caller.

Build with gameval generation disabled when validating only this module:

```powershell
.\gradlew.bat -PtitanGenerateGamevals=false :titan-runtime-spi:build
```

Full RuneLite/Quest Helper compatibility is a separate runtime implementation
and qualification task; this contract does not claim it.

### Optional retained actor interaction

The independent `titan.actors.interactionState.capabilities.v1` operation accepts
`{}` and returns a strict boolean. Its game-thread read
`titan.actors.interactionState.v1` takes the same actor selector and both retained
incarnation guards described below. A present reply has exactly `schemaVersion:1`,
`kind`, `worldViewId`, `index`, the matching `worldIncarnation` and `incarnation`,
and a boolean `interacting`. An unavailable reply has the version, selector,
`status:"unavailable"` and a nonempty `reason`, without a boolean or tokens.
Present replies also carry `status:"present"`. Requests are bounded to 1 KiB,
replies to 8 KiB, and reasons to 4 KiB of UTF-8.

This predicate uses positively observed phase/type values only. An active phase
with NPC or player type is true even when the target index is unreadable or no
target actor exists. An observed inactive phase or non-actor type is false.
Unqualified native fallback values are unavailable. No target lookup or complete
world traversal is required. Both SDK observations, exact receiver identity,
source epoch and provider/activation admission are fenced before publication.
Old hosts do not acquire this capability merely by supporting target resolution.

`titan.actors.interacting.capabilities.v1` accepts `{}` and returns a strict
boolean. This independent capability leaves the original actor capability
envelope unchanged. Older hosts report it unsupported. The capability describes
the installed sources; an individual observation can still be unavailable.

`titan.actors.interacting.v1` is a game-thread read accepting the existing
`kind`, `worldViewId`, and `index` selector with both
`expectedWorldIncarnation` and `expectedIncarnation` required. Selectors are
unsigned 16-bit values and incarnation guards are canonical positive uint64
decimal strings. Requests are bounded to 1 KiB and replies to 32 KiB. The host
counts the read and checks the exact provider load and activation before and
after native delegation.

A reply always contains `schemaVersion:1` and the receiver's `kind`,
`worldViewId`, and `index`. A `status:"present"` reply also contains the matching
`worldIncarnation`, `incarnation`, and `target`. The target is either a complete
present actor snapshot from the existing actor contract or JSON null for proven
absence. An unavailable reply instead contains `status:"unavailable"` and a
nonempty `reason`, with no target or incarnation fields. Unknown fields,
malformed snapshots and conflicting actor/world identities are rejected.

The current source qualifies active phase 0 with type 1 for NPCs and type 2 for
players. Successfully observed inactive phases or other types yield null.
Legacy unreadable sentinels (phase 255, type 127, or target index -1 when needed)
yield unavailable. An active target preserves its exact index, including zero.
The complete checked world registry is searched for nonzero-world matches:
exactly one child match wins even when the top-level world also contains that
index, and multiple child matches remain unavailable because their iteration
order is unqualified. Only after proving there are no child matches does the
reader consult the authoritative top-level role. Null requires a complete,
stable absence observation in a present top-level world; a missing fallback
role is unavailable. Receiver fields, actor identities and source state
are rechecked before publication. Native addresses never cross this interface.

### Optional retained actor positions

The optional `titan.actors.details.capabilities.v1` reply contains the baseline
actor capability fields plus independent `npcDetailReads` and `pathHeadReads`
booleans. It requires `{}`. The original `titan.actors.capabilities.v1` response
does not add these fields, so older strict host parsers remain compatible. The
generic host probes the detail capability when `supports` is called for a detail
operation.

`titan.actors.npcDetails.v1` takes an NPC selector with its world and actor
incarnations on the game thread. The result uses the actor snapshot envelope.
`baseNpcId` identifies the spawned definition when available. A confirmed
non-transforming definition can also supply `npcId`, `displayName` and
`footprintSize`. The current display-name contract covers complete nonempty
printable ASCII up to 62 bytes. The square footprint is a positive tile count,
including one, captured directly from the definition and checked against the
retained actor. Each missing field has its own reason in `unavailableFields`.

`titan.actors.pathHead.v1` accepts the existing explicit kind/world/index actor
request, with both positive incarnation guards required. It uses the existing
actor snapshot envelope; present logical world fields are signed int32
`worldX/worldY/plane`. Missing SDK identities, aliases, stale captures or
unreadable path/world sources are unavailable, never absent or zero substitutes.
A known path head with zero pending movement steps is valid. The same capture may
have signed `localX/localY`, the interpolated scene position in 1/128 tile, read
independently of the path head. Both are present or neither is, and an unreadable
position is named in `unavailableFields`. Older hosts have neither.

This is a counted read-only operation for the current owner on the actual game
thread. Native code binds the public actor accessor's current identity to the
retained tokens and uses the existing checked server path-head source. No raw
pointer is published; neither aggregate fine-coordinate fallbacks nor pending
queue size supplies the result. Existing hosts without the optional flags remain
compatible and decline these requests.

### Optional complete actor spot animations

`titan.actors.spotAnimations.capabilities.v1` accepts `{}` and returns a strict
boolean. This separate capability does not change older actor descriptors.
`titan.actors.spotAnimations.v1` is a counted, game-thread read with the exact
request `{kind,worldViewId,index,expectedWorldIncarnation,expectedIncarnation}`.
Both positive uint64 incarnation strings are required; kind is `npc` or `player`
and the explicit world/index are integers in 0–65535.

A successful response contains `schemaVersion:1`, `status:"present"`, the
matching kind/world/index, `worldIncarnation`, `incarnation`, and `entries`.
Each of at most 128 entries is exactly `{slot,id}`, both signed int32 values.
Each slot occurs once. IDs may repeat. The source preserves signed values.
Consumers that interpret the first animation must supply their own ordering
contract. A validated actor with equal range bounds on all three observations
has an empty collection, including a fresh actor whose bounds are both null.
Unreadable bounds, a mixed null pair, and retained native clear markers
(`-1` or `65535`) make the observation unavailable.
An unavailable response contains only schemaVersion/status/kind/worldViewId/index
and a nonempty `reason` of at most 4096 UTF-8 bytes. The entire response is at
most 32768 UTF-8 bytes. No pointers or partial lists cross this boundary.

The native source repeats the complete existing slot/payload/id observation and
its structural extent, then checks source layout and epoch. The provider binds
the observation to the SDK actor/world pointers and retained exact incarnations
before and after capture. Source, actor, owner or activation retirement rejects
the reply. The operation grants no animation mutation and is unavailable on
older hosts without this independent capability.

### Optional observed NPC compositions

`titan.actors.npcComposition.capabilities.v1` accepts only `{}` and returns
`{schemaVersion:1,compositionReads:boolean,selection:"observed-plain-definition"}`.
The separate operation leaves existing actor capability schemas unchanged.
`titan.actors.npcComposition.v1` requires an NPC selector and both retained
incarnation guards. A present actor envelope has exactly three fields:
`compositionIncarnation` (canonical positive uint64 string), `id` (nonnegative
int32) and `size` (positive int32), with no unavailable fields. Unavailable
identity, transformed definitions or missing typed values return an unavailable
actor envelope; they never establish null or a fallback definition.

This counted game-thread read shares opaque identity across actors observing
the same actual definition, without serializing addresses. The captured values
are independent of the actor's display overrides and do not attest offline cache
metadata. Session/layout changes retire the native observation journal. Existing
hosts can decline this optional operation without changing their baseline actor
protocol.

Hosts can also advertise `titan.actors.npcCompositionName.capabilities.v1`.
It uses the same capability envelope. The corresponding
`titan.actors.npcCompositionName.v1` read accepts the same guarded NPC request.
Its present response adds exactly one of `fields.name` or
`unavailableFields.name`. The name is a complete string of 1–255 printable
ASCII bytes from the observed base definition, with spaces preserved. An
unavailable name still permits reading ID and size. Both operations share
definition identities. A newly readable name can complete an existing
observation. A different observed name for the same address starts a new
incarnation.

### World terrain and collision grids

Use `supports` with each snapshot operation, or call
`titan.scene.terrain.capabilities.v1` with `{}` to read its version 1
descriptor. The boolean fields `settings`, `heights`, `collision`,
`instanceTemplates` and `staticObjectSlots` describe independent optional
sources. A missing grant is unavailable. Older hosts can omit the new fields
without changing existing terrain support.

On the game thread, pass `{"worldViewId":0,"expectedWorldIncarnation":"1"}`
to `HostServices.snapshot`, substituting the ID and current incarnation from
the world snapshot. A successful read returns owned bytes. A stale or
unreadable world yields no grid. Reacquire the grid to observe later changes.

| Operation | Kind | Payload in plane, X, Y order |
| --- | --- | --- |
| `titan.scene.tileSettings.v1` | 1 | One unsigned settings byte per tile |
| `titan.scene.tileHeights.v1` | 2 | Signed int32 raw corner heights, including both far edges |
| `titan.scene.collisionFlags.v1` | 3 | Signed int32 scene-indexed movement and sight flags per tile of the current world |
| `titan.scene.instanceTemplates.v1` | 4 | Signed int32 normalized template cells, four planes of 13 by 13 chunks |

The 32-byte little-endian header contains six int32 fields (magic
`0x54455231`, kind, world ID, plane count, tile width, tile height) and
one uint64 world incarnation. Width and height are each 1–256 tiles.
Terrain supports 1–8 planes. Collision captures contain four planes.
For kind 2 each plane has `(width + 1) * (height + 1)` corners. Kinds 1 and 3
have `width * height` cells. Kind 4 requires header dimensions 104 by 104 tiles
and exactly four planes of 13 by 13 cells. Grid reads copy one payload per call.

Instance templates require agreement between the current world and the
authoritative top-level world. Each observed cell is either `-1` for an absent
chunk, or `(sourcePlane << 24) | (sourceChunkX << 14) | (sourceChunkY << 3) |
(rotation << 1)` with planes/rotation 0–3, X 0–1023 and Y 0–2047. Zero is valid.
This normalized contract does not observe the original packed table's low bit
or establish whether the world is instanced. Unreadable cells, malformed source
values, other worlds and source/owner changes make the complete capture
unavailable. The source does not convert a failed read into an absent chunk.

### Scene objects

The optional boolean grants `objects` and `objectHulls` in
`titan.scene.terrain.capabilities.v1` allow two `HostServices.snapshot` operations on the
game thread. An identity reply (below) then carries `objectChanges`, a decimal string of how
many changes its scene has had.

`titan.scene.objects.v1` takes exactly `{"worldViewId":7,"sceneIncarnation":"10"}` and returns
everything the scene holds, little-endian: int32 magic `0x534F4231`, int32 world ID, uint64
incarnation, uint64 changes, int32 base X, base Y, size X and size Y, and int32 counts of tiles,
objects and references. Each tile is uint32 `(plane * sizeX + x) * sizeY + y`, uint32 first
reference, uint32 reference count and three int32 slots for its wall, decoration and floor
decoration: an object's index, -1 for none or -2 for one which could not be read. Each object is
32 bytes: uint64 tag, uint64 identity, int32 ID, uint16 X and Y of its south-west tile, then one
byte each of layer, plane, placed plane, size X, size Y, config, flags and zero. A size of 0 is
one which could not be read. Config is the shape in the low five bits and the rotation in the
high two. Flag 1 is not interactive and flag 2 is found on its own south-west tile. References
are uint32 object indices, scenery by tile in the order the tile holds it. A change made
afterwards carries the next number and is told of by `sceneObjectChanged`.

`titan.scene.objectHulls.v1` takes `{"worldViewId","sceneIncarnation","objects"}` with 1 to 512
objects, each `[tag, identity, plane, layer, x, y, sizeX, sizeY]` with the first two decimal
strings. It returns int32 magic `0x534F4831`, int32 count, and for each object an int32 point
count followed by int16 X and Y canvas points in order. The count is 0 when the object has no
outline now and -1 when its place no longer holds it. The outline is the convex hull of the
model's points, or of its bounding box until the host has captured the model.

`null` from either means that the scene is not the loaded one or could not be read.

### Drawn models

The optional boolean grants `actorModels` and `objectModels` in
`titan.scene.terrain.capabilities.v1` allow two more `HostServices.snapshot` operations on the
game thread. They tell what the game last drew something as. Asking for an outline or a mesh
makes the host copy the model as the game draws it, so the first answer has neither.

`titan.actors.models.v1` takes `{"actors"}` with 1 to 512 of `[kind, worldViewId, index,
worldIncarnation, incarnation, parts]`: kind 0 a player and 1 an NPC, both incarnations decimal
strings, and parts 1 for the outline plus 2 for the mesh. `titan.scene.objectModels.v1` takes
`{"worldViewId","sceneIncarnation","objects"}` with 1 to 512 of `[tag, identity, plane, layer, x, y,
sizeX, sizeY, half]`, where half 1 is the second model of a wall or decoration.

Both return, little-endian: int32 magic `0x44524D31`, int32 count, and for each a header of eight
int32: status, orientation, model height, height offset, and counts of outline points, vertices,
faces and transparency bytes. Status is -1 for one which is not there any more, 0 for one which has
not been drawn lately or is not copied yet, 1 for one which is known and 2 for a half an object does
not have. An actor's orientation is in 2048ths of a turn and int32 minimum when it is not known. For
an object it is -2 for a model which the scene keeps, the animation ID or -1 of one which the game
makes as it draws it, and int32 minimum when neither could be told. The vertex
count is -1 without a mesh. Then follow the points as int16 X and Y, the vertices as int32 X of all,
Y of all and Z of all, the faces as int32 first corners, second and third, and the transparencies
as bytes padded to four, none when no face is transparent. A packet leaves meshes out rather than
grow past 16 MiB.

### Static scene-object slot companion

Query `titan.scene.identity.capabilities.v1` with `{}` for a boolean. When true,
`titan.scene.identity.v1` accepts `{worldViewId}` on the game thread and returns
`{schemaVersion:1,status:"present",worldViewId,baseX,baseY,sizeX,sizeY,incarnation}`
or `{schemaVersion:1,status:"unavailable",reason}`. `incarnation` is the decimal
string of the `sceneIncarnation` which the complete snapshot reports. It tells two
scenes apart, including one loaded again at the same place, without capturing what
either holds.

`titan.scene.staticObjectSlots.v1` is a game-thread `HostServices.snapshot`
operation granted only by `staticObjectSlots:true` in the descriptor above.
Its request is exactly
`{"worldViewId":7,"generation":"1","sceneIncarnation":"10"}`.
The world ID is an unsigned 16-bit number. Both identities are canonical
positive unsigned 64-bit decimal strings taken from the consumer's complete
SCN1 capture. Caller-supplied owner or load identity fields are rejected.

`titan.scene.sceneryAnchors.v1` uses the same world, generation and scene
incarnation request. The optional terrain grant is `sceneryAnchors`. On the
game thread, the owned SCA1 reply uses a 28-byte little-endian header with
magic 0x53434131, world id, generation, incarnation and row count. Each row
contains three signed 32-bit integers: ascending SCN1 object index, anchor X
and anchor Y. Only verified scenery locs with a matching member tile, native
identity and decoded origin qualify. Other entries on layer 2 include ground
objects and unqualified renderables. A stale publication is unavailable.

The owned SLO1 response has a 28-byte little-endian header: int32 magic
`0x534C4F31`, int32 world ID, uint64 scene generation, uint64 scene incarnation
and int32 row count. It is followed by three int32 slots per SCN1 tile row in
the same order: wall, decorative, ground. Each slot is `-1` for positively
observed absence, `-2` for unresolved occupancy, or a nonnegative index into
that exact SCN1 object array. There are at most 262144 rows and objects, so
nonnegative indices must be below 262144 and below the captured object count.
Consumers must cross-check row membership, object kind and selector against
their SCN1 data; the generic transport validates envelope identity and size
without repeating that per-cell decoding pass.

`null` means the exact source/publication is unavailable, and never means all
slots are empty. An empty byte array is invalid. A zero-row 28-byte response is
valid only with a matching zero-row SCN1 capture. The native source rechecks
owner and scene publication around capture. Managed host calls pin and recheck
the exact load/activation; passive provider calls recheck admission and closing
before returning. None of these read operations grants mutation or callback
authority. Missing optional JNI methods on older runtimes remain unavailable.
## Small terrain point observations

The optional boolean `terrainPoint` in `titan.scene.terrain.capabilities.v1`
grants `HostServices.snapshot("titan.scene.terrainPoint.v1", request)`. It is
independent of the bulk settings/heights grants. Missing or false means the
operation is unavailable on that host; callers must not infer support from a
bulk-grid capability. This is a game-thread read with exact owner/load and
managed activation admission before and after capture.

The request has exactly `worldViewId` (0..65535),
`expectedWorldIncarnation` (canonical positive unsigned 64-bit decimal string),
`plane` (signed int32), and tile indices `x`, `y` (0..255). JSON is bounded to
256 UTF-8 bytes. The tile must lie inside the retained world's observed size.
Malformed selectors throw; missing, stale, unreadable or unqualified source
data returns null, never an empty packet or fabricated height.

A successful result is exactly 64 little-endian bytes:

| Offset | Value |
| --- | --- |
| 0 | int32 magic `0x54455231` (`TER1`) |
| 4 | int32 kind 5 |
| 8 | int32 requested world ID |
| 12 | int32 plane count, exactly 4 |
| 16, 20 | int32 world sizes X/Y, each 1..256 |
| 24 | uint64 retained world incarnation |
| 32, 36 | int32 requested plane and effective plane |
| 40, 44 | int32 requested tile X/Y |
| 48, 52, 56, 60 | int32 corners h00, h10, h01, h11 |

The source verifies both raw arrays and their shared world/scene topology. For
requested planes below 3, mask `0x2` in settings plane 1 at that tile selects the
next plane; otherwise the requested plane is retained. The effective plane
must be 0..3. The transport preserves the signed requested plane, including
the potentially valid -1-to-0 bridge case. Consumers with additional invalid
plane semantics must preserve those semantics themselves. Corners retain all
signed int32 values, including zero; no interpolation, coordinate scaling or
terrain fallback occurs in the transport. The source reads at most one flag
byte and four corners, then rechecks its topology and source epoch. The outer
host rechecks world incarnation, dimensions, authoritative SDK world identity
and owner before returning the owned packet. No native addresses are exposed.

## Stored server hint targets

`titan.hintArrows.target.capabilities.v1` accepts `{}` and returns schema version
1 with boolean `pointReads` and `targetWrites`. A passive provider may read the
existing server slot. Edits require a managed plugin activation.

On the game thread, `titan.hintArrows.storedPoint.v1` takes `{}`. The response
has a `status` of `present` with signed `x`, `y`, and `plane`, `nonCoordinate`
for another observed kind, `absent` for no server slot, or `unavailable`.
The plane is stored with the arrow. Its availability requires the image's
validated coordinate-plane field.

`titan.hintArrows.updateTarget.v1` takes `{"kind":"none"}` or a `coordinate`
target with `x`, `y` (0..65535), `plane` (0..3), `subX`, `subY` (0..128), and
`height` (0..255). It updates slot zero synchronously, preserving its display
flags, and returns the numeric SDK `HintArrowUpdateResult`. Only 1 means applied.

An active plugin can use `titan.hintArrows.updateActor.v1` to change slot zero
to an NPC or player while preserving its display flags. Query
`titan.hintArrows.actorTarget.capabilities.v1` with `{}` for its boolean
availability. The request carries `kind` (`npc` or `player`), `worldViewId`,
`index`, `expectedWorldIncarnation` and `expectedIncarnation` from an owned
actor snapshot. The game-thread operation checks the top-level world and
selected target before writing. It returns the same `HintArrowUpdateResult`
codes as the coordinate operation. An older host can still support coordinate
targets independently.
