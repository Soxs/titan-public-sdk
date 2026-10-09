# Packaged HTML UI

SDK 145 adds HTML side panels and HUD overlays alongside existing ImGui panels.
The renderer uses the pinned **Ultralight Free 2.0.0-beta.2** runtime in an isolated
helper process. It is optional: native plugins, native panels, and the rest of
TitanClient continue to work when the HTML runtime is unavailable.

The equivalent counter samples are
[C++](../examples/html-ui/native_counter.cpp),
[TypeScript](../examples/html-ui/counter.ts), and
[Java](../examples/html-ui/java/HtmlSamplePlugin.java).
The Java sample's `html-counter` resources stay inside its JAR. The C++ sample
uses embedded string literals; the TypeScript sample uses an in-source map.

## Plugin APIs

```cpp
titan::HtmlPanelBundle bundle("index.html");
bundle.text("index.html", "text/html", "<!doctype html><script src='app.js'></script>");
bundle.text("app.js", "application/javascript", "titanHtml.postMessage('ready', {});");
auto& page = htmlPanel("tools", "Tools", bundle);
page.icon("lucide:code").onMessage([this](const titan::HtmlPanelMessage& message) {
    // MainLoop/game thread, lifecycle-gated. Check type and payload schema.
});
page.setState(R"({"count":1})");
page.postMessage("updated", R"({"count":1})", "request-1");
auto& hud = htmlOverlayPanel("tools", bundle);
hud.setSize(320, 200);
hud.setVisible(true);
```

```java
@PluginDescriptor(id="tools", name="Tools", description="Packaged HTML tools",
    author="Example", version="1.0.0")
@HtmlSidePanel(id="tools", title="Tools", resourceRoot="ui", entrypoint="index.html")
@HtmlOverlayPanel(id="hud", resourceRoot="hud", entrypoint="index.html")
public final class ToolsPlugin implements Plugin {
    public void onLoad() {
        HtmlPanels.of(this).setState("tools", "{\"count\":1}");
        HtmlOverlays.of(this).setState("hud", "{\"count\":1}");
    }
    public void onHtmlPanelMessage(String id, HtmlMessage message) {
        HtmlPanels.of(this).postMessage(id, "updated", "{\"count\":1}", null);
    }
}
```

```ts
class ToolsPlugin extends titan.Plugin {
    id = "tools";
    name = "Tools";
    panels: titan.PanelDef[] = [{
        kind: "html", id: "tools", title: "Tools",
        bundle: { version: 1, entrypoint: "index.html", resources: {
            "index.html": { mime: "text/html", content: "<!doctype html><p>Tools</p>" }
        } },
        onMessage: message => titan.htmlPanels.postMessage(this, "tools", "reply", message.payload, message.id)
    }];
    onEnable() { titan.htmlPanels.setState(this, "tools", { count: 1 }); }
}
titan.register(new ToolsPlugin());
```

Class-style JS/TS plugins declare overlays with
`this.htmlOverlayPanel({ id: "hud", bundle, anchor: "TopCenter" })`.
The returned owner-bound handle exposes `setState`, `postMessage`, `setVisible`,
`setSize`, and `onMessage`. Constructor-time state, size, and visibility survive
registration; transient messages require an active view. Object-style plugins
can declare `htmlOverlays` entries with `kind: "html"` and use
`titan.htmlOverlays` with their owning plugin object. Both paths share validation
and limits. Registration and callback configuration close at `titan.register`;
handles from a retired plugin cannot operate a replacement.

C++ payloads and state are validated JSON strings, without a third-party JSON
header dependency. Java uses JSON strings. JS/TS accepts JSON values and rejects
undefined, functions, cycles, malformed Unicode, nonfinite numbers, and nesting
beyond 32 levels. C++ and Java receive a typed message carrying `type`, raw JSON
payload, and an optional correlation ID; JS receives the parsed envelope.

Register HTML surfaces unconditionally and set their visibility from plugin
state. C++ `htmlUiAvailable()` and JS/TS facade `available` report whether a
connected renderer has been observed for this client; false includes unobserved
as well as unavailable. Before the first rendered activation, and after the
last activation closes, false does not prove that the runtime is missing.
Do not hide a first overlay or omit a declaration based on that flag, because
doing so prevents the activation that establishes availability. No helper is
started merely to probe an otherwise unused feature. Requested surfaces show
native diagnostics and retry controls when renderer startup fails.

HTML and native side panels share eight slots per plugin. IDs are unique across
both kinds. HTML IDs contain 1..31 ASCII letters, digits, `_`, `-`, or `.`;
existing native ID rules are unchanged. The overlay
namespace is separate from side panels. HTML/native overlay name collisions
are rejected; existing native-to-native name behavior is preserved. HTML has
eight overlay definitions per plugin and at most eight active overlays per
client. The controller admits at most 32 active HTML views, reserving one slot
for its selected sidebar; memory limits can reject a view before that count.
An admission failure shows a diagnostic and retains canonical state. Native
overlay counts are unchanged.

Overlays default to Dynamic anchor, priority 50, 220×160 CSS pixels, visible,
and click-through. Width must be 80..600; height must be 24..600. Size and
visibility setters update live overlays. Opt into buttons, scrolling, and text
input with bits 1, 2, and 4 respectively in C++/JS. Java's
`@HtmlOverlayPanel(interactive=true)` enables all three; its default is false.
Tooltip anchors stay click-through.
The controller owns side-panel dimensions. PNG icons are embedded bytes in C++,
JAR resources in Java, or base64 in JS, with the existing 256 KiB icon limit.

HTML overlays use the existing anchor layout and Alt-drag interaction at
`AboveWidgets`. Their saved positions live in a separate HTML layout namespace,
so native positions are preserved. Alt-drag takes priority over page input.
Interactive hit testing uses the whole viewport, including transparent pixels.
Escape, outside clicks, hiding, and focus loss release keyboard focus; paired
mouse/key releases remain bound to the surface that received the press.
Foreground ImGui windows take input priority over HTML rendered behind them.
Overlay visibility follows the target game window and world-ready state,
independently of the selected controller tab.

Editable fields support Windows IME preedit, clause underlining, commits and
cancellation. The helper reports the composition/caret rectangle in CSS pixels;
the owning HWND thread positions the native candidate window after applying DPI
and the panel's client-area origin. Focus and composition revisions fence late
updates within the same activation. Page-driven composition dismissal cancels
the native IME without inserting text twice. Changing the editable target or
input mode ends any pending composition. The pinned engine preserves the field's
text when the page makes it read-only; returning it to editable does not resume
composition, and a stale native commit cannot insert the text again.
Composition text is bounded to
4096 UTF-8 bytes and 64 clauses, with UTF-16 selection boundaries checked before
entering the engine. Editor feedback has a separate coalesced transport slot,
so it does not consume the 64 application-message slots. Automated renderer
tests exercise these editor operations; actual Windows IME candidate UI and
keyboard layouts still require the interactive checks below.

## Bundle v1

A bundle contains version `1`, an explicit entrypoint, and a list of resources
with normalized path, explicit MIME type, and raw bytes. Resource names are
case-sensitive, but case collisions are rejected. Paths have at most 255 ASCII
characters from letters, digits, `_`, `-`, `.`, and `/`. Absolute paths, empty
segments, dot segments, backslashes, NUL, URL syntax, and every `%` spelling are
rejected. No decoding or filesystem extraction occurs during bundle resolution.

The entrypoint must exist and be nonempty `text/html`. All text must be valid
UTF-8. Supported MIME types are `text/html`, `text/css`, `text/javascript`,
`application/javascript`, `application/json`, `text/plain`, `image/png`,
`image/jpeg`, `image/webp`, `font/woff`, and `font/woff2`. SVG, GIF, AVIF, TTF,
OTF, executable/object formats, and arbitrary binary MIME types are excluded
from bundle v1. At most 128 resources are allowed, with 2 MiB per resource and
8 MiB total raw bytes. Marketplace/released plugins need no loose UI files.

Keep executable scripts in packaged `.js` resources and styles in packaged
`.css` resources. Do not use inline scripts, eval, dynamic code generation,
CDNs, remote fonts, or network calls. Packaged JSON is accessible through
`await titanHtml.resource("data.json")`; JSON resources resolve to a JSON value,
other supported text resources to text, and missing/binary resources to null.

## Bridge and replay

The page has one versioned object, `titanHtml`:

```js
titanHtml.onState((state, revision) => render(state));
render(titanHtml.getState());
titanHtml.onMessage(message => showReply(message.payload));
titanHtml.postMessage("increment", {}, "request-1");
```

Messages use `{version:1, type, payload, id?}` with no extra fields. `type` is
1..128 printable ASCII bytes without spaces. `id`, when present, is at most
128 UTF-8 bytes and has no NUL. Duplicate JSON keys, invalid Unicode, malformed
JSON, and messages over 64 KiB are rejected. Type and payload schema validation
also belongs in the plugin callback; an arbitrary page message must never be
interpreted as a game action without that check.

### Saved view state

Leaving a side panel destroys its page (see Isolation below), as do hiding an
overlay for ten seconds, minimizing the controller and live resizing. The bridge
saves the window scroll position automatically and restores it when the same
surface opens again. It retries for up to 30 animation frames while late
content lays out, and stops as soon as the user scrolls, types or clicks. A page
can also save a small JSON value of its own for its next activation:

```js
const saved = titanHtml.getViewState(); // null, or the value saved last time
titanHtml.saveViewState({ tab: "filters", expanded: ["loot"] });
```

`getViewState()` is available as soon as page scripts run, before the first
`onState` callback. It returns the value restored for this activation, or the
latest value saved since. `saveViewState(value)` replaces that value and
coalesces with other saves (only the latest one is kept), so calling it on every
change is fine. The value must serialize to at most 16,320 UTF-8 bytes of JSON
with at most 32 levels of nesting. Larger, unserializable or deeper values throw
in the page (`RangeError`, `TypeError` or `Error`) and leave the previous value
in place; they never stop the renderer. To handle scrolling yourself, set
`history.scrollRestoration = "manual"` in a page script; the bridge reads it
once, when the page becomes ready.

View state belongs to the page, not the plugin. It stays in controller memory
and is replayed only to the next activation of the same client epoch, PID,
plugin, load generation, surface kind and ID. The plugin never receives it. A
later save from a retired activation cannot overwrite a newer page's state. A
renderer failure, plugin reload, client restart, sign-out or controller exit
discards it, and the controller keeps at most 64 surfaces' states. Durable or
plugin-relevant choices such as filters still belong in canonical state. Hosts
older than this feature do not define these two functions, so check for them
when a page must also run there.

Canonical state is the latest valid JSON value, initially `null`, with its own
monotonic revision. The page's `onState` callback receives that revision as a
decimal string, preserving all 64 bits instead of rounding a JavaScript Number.
State coalesces independently of transient queues. Every
accepted state/message update increments the surface's monotonic sequence.
Transient queues are FIFO, bounded to 64 messages and 1 MiB per direction per
surface. SDK producer overflow rejects the new message explicitly. Page-origin
overflow terminates that activation with a native diagnostic, since the page
bridge has no synchronous admission return. Neither path evicts an older message.
Publishing a transient while inactive returns false in C++/JS and
`HtmlUiResult.INACTIVE` in Java (`result.accepted()` provides a boolean).
Store durable UI facts
in canonical state, not in transient messages.

Opening a panel, switching identity, or reactivating after reload establishes a
fresh activation. Activation changes discard transients and replay canonical
state; repeating the same activation is an idempotent retry. Acknowledgements
must refer to the current activation and an issued sequence. Old epoch, PID,
plugin, load-generation, panel, and nonce combinations are rejected. Native,
Java, and JS message callbacks are dispatched through MainLoop under the plugin
lifecycle gate. Immutable resource reads may occur on other threads only while
the plugin's lifetime is pinned. No renderer/input callback waits for a
synchronous multi-second client pipe request.

Routine HTML polling, resource copies and activation bookkeeping retain the
plugin's lifetime without suppressing scene or panel rendering. Only message
delivery executes user code and uses the existing callback exclusion. Native
HTML copy/read/reset callbacks must operate on synchronized framework data;
they must not invoke plugin handlers or HostApi. The SDK-provided callbacks
already follow this contract. No native ABI layout or SDK floor change is needed.

MainLoop services at most four HTML requests and yields between requests after
2 ms; an individual callback is not preempted. Failed lifecycle admission leaves
the request at the FIFO head. Unchanged state revisions omit the state bytes
without discarding messages or visibility updates. The render thread snapshots
the active HTML mappings under a short lock and keeps their memory alive until
the frame finishes; input contention does not discard an otherwise valid image.
Replacement and retirement invalidate that exact mapping, even when a resize
reuses the activation nonce.

The Debug renderer diagnostics include `htmlDispatch`: admitted request count,
maximum request duration, admission deferrals, and per-layer skips attributable
to HTML user callbacks versus other lifecycle work. Ordinary polling must not
increment either skip counter when no lifecycle operation or callback is active.

Controller input admission counts commands across the producer queue, transport
queue and in-flight slot together (64 commands / 1 MiB per view). Adjacent pointer
moves keep the latest position; adjacent wheel events at the same position and
with matching modifiers add their deltas without crossing direction changes or
discrete input. Motion uses at most 48 slots, reserving 16 for button/key edges.
Stationary pointers do not enqueue commands. Under pressure, new motion/scroll
can be dropped; losing a button, key or composition event stops the activation
so the document cannot retain a stuck press. Page messages and discrete input
retain FIFO ordering. Lifecycle requests and page actions take priority over
periodic client polling; obsolete queued requests are cancelled before pipe I/O.
The private helper transport delivers at most 16 ordered input events per batch
(at most 1 MiB), without crossing non-input commands or activation boundaries.
This avoids one render-cycle delay per individual pointer/key event.

Theme values are CSS custom properties: `--titan-bg`, `--titan-text`,
`--titan-muted`, `--titan-accent`, `--titan-border`, and `--titan-font-size`.
Use them in packaged styles; theme changes update the active document.

## Isolation and runtime delivery

Page code receives no game SDK objects, native pointers, or arbitrary host
script execution. CSP is combined with a memory-only resource resolver,
capability-free low-integrity AppContainer, restricted helper process/job,
activation checks, and fail-closed helper termination on forbidden navigation
or subdocument callbacks. Popups, frames, forms, network access, workers,
downloads, dialogs, and foreign/custom/file navigation are forbidden. The
host rejects input/output after a helper is quarantined; the
earlier Stop-only fixture remains a negative control because Stop alone did
not prevent a blank frame from painting.

Each active view has its own helper process and private memory resource map,
scoped by epoch, PID, plugin, load generation, surface kind/ID, and nonce.
Ultralight Free's filesystem callback supplies a path without the requesting
view. A production negative control demonstrated that a second view in a shared
helper could execute a victim view's script when given its nonce. Separate
processes remove that shared resource map; the known-nonce attack is tested
against both the deliberately unsafe test topology and the production topology.
The internal `file:///titan/<nonce>/` spelling addresses the private virtual
filesystem only. It never delegates plugin paths to Windows files. New document
navigation, including other file URLs, is rejected.
Trusted ICU/certificate buffers are authenticated before engine initialization
and removed from the resolver before any view exists. Page resource requests
can then resolve only that view's bundle and the fixed bridge. The smoke tests
also verify lazy locale/font behavior after removing the bootstrap namespace.

Helpers start only for visible views. A view hidden for ten seconds retires its
helper; showing it again uses a fresh activation that replays canonical state
and the page's saved view state.
Leaving the sidebar destroys its helper. Page view state travels through a
separate coalesced transport slot that the host reads until the helper exits,
so a save made just before the switch is kept. A return within ten seconds can show
one cached, noninteractive image (at most 8 MiB), without an overlay, until the
new activation paints; a client stall still shows its waiting badge. The image must match client epoch, PID, plugin,
load generation, panel, dimensions, DPI and theme. Authentication loss, owner
removal, background suspension or a theme change clears it. It never admits input or replays transients.
The first activation still pays renderer startup cost.

Retaining an initialized Free 2.0.0-beta.2 renderer across destroyed pages was
rejected in testing: an old page's interval called the bridge after its owning
context was removed, triggering fail-stop. Calling `Renderer::PurgeMemory()`
after page destruction did not prevent that callback. Production therefore
keeps process destruction as the activation boundary; it does not ignore old
callbacks to make process reuse appear safe.
The controller keeps a single active HTML sidebar. Overlays render CPU frames
in the game process, which contains no HTML engine. The target duplicates a
controller-owned read-only mapping only after accepting the current command;
dropped asynchronous IPC commands therefore create no remote handles.

Floating game overlays continue receiving state and messages while the
controller is minimized or occluded. A 33 ms background broker beat services
them without drawing the controller or uploading sidebar textures. The sidebar
retires first; authentication, version, and shutdown gates still apply. Catalog
refreshes include unselected clients, so plugin reloads and native-to-HTML
changes do not require selecting that client's tab. Late snapshot results
cannot replace a newer client epoch or revision. Steady native-only sessions
retain the existing idle behavior.

The aggregate helper job caps private commit at 576 MiB, with a separate
192 MiB admission budget for control/frame mappings, immutable frame copies,
and pending hidden bundles. Each helper also has a 256 MiB process limit and
a CPU job cap. SDK/controller queues and graphics-driver allocations have their
own bounds and are not represented as helper private commit. Resource exhaustion
stops or rejects the affected view and surfaces a diagnostic. Nine simple
64×64 views with the pinned Free 2 beta measured about 338 MiB helper private
commit and 119 MiB control/frame
budget on the development machine; these are fixture measurements, not a claim
of acceptable game-frame performance or equivalence to a shared browser process.
Physical surfaces are bounded to 4096 pixels per dimension and 8 Mi pixels in
total. DPI scale is accepted from 0.5 through 8; dimensions must independently
fit those limits. Input coordinates are converted from device pixels to CSS
pixels before entering Ultralight.

The renderer target is `TitanHtmlRenderer`. Configure the pinned SDK through
`TITAN_ULTRALIGHT_SDK_ROOT`; `scripts/acquire_ultralight.py` acquires the audited
Free 2.0.0-beta.2 pin. CMake verifies pinned DLL hashes and stages the helper, runtime
DLLs, engine resources, and license notices under `html-ui` beside the
controller. `TITAN_HTML_UI_TESTS=ON` enables the production helper smoke tests.
Native validation builds must keep `TITAN_AUTO_UPLOAD_DLL=OFF` and
`TITAN_AUTO_PUBLISH_DLL=OFF` and have one owner per build directory.

The exact SDK is the official [Free Windows x64 2.0.0-beta.2 archive](https://ultralig.ht/api/v1/sdk/download?platform=windows-x64&version=2.0.0-beta.2),
26,711,303 bytes, publisher SHA-256
`e17b3ca2f3c2063f1d14971e7ef756f28e71f2d426a91d2e21c5236d2dc67e6d`.
The script verifies archive size/hash and the runtime, resource, and license
file hashes. It does not substitute a moving `latest` build or accept an account
agreement. This is an explicit beta dependency. Earlier probes found that Free
1.3 could not render the required WebP/WOFF2 fixtures and Free 1.4.0 still could
not render WOFF2. The selected beta passes real PNG/JPEG, lossy/lossless WebP,
WOFF, and WOFF2 tests, including CSS with and without a WOFF2 format hint.

The archived Free V2 license is conditional, not a blanket commercial license.
Its limited commercial distribution grant excludes government agencies and
entities whose trailing-twelve-month gross turnover, including parent and
affiliate turnover, is at least US$200,000, or whose total investment is at least
US$200,000. The archived agreement specifies the applicable paid-license
transition after those thresholds. No paid features or paid SDK are used here.
Redistribution must meet that archived agreement's eligibility and end-user
terms, preserve dynamic linking, and include the vendor's credits and notices.
The controller shows them on the Settings > Credits page.
`html-ui/ultralight-license/` carries `LICENSE.txt`, `EULA.txt`, and `NOTICES.md`
unchanged, with an additional runtime attribution file. The license's end-user
terms must accompany the product's distribution terms. No Ultralight SDK source
or plugin assets are included in the runtime upload allowlist.

TitanLauncher already synchronizes nested runtime-manifest entries and verifies
their published SHA-256 values. Its source and version need no change for this
layout. Production deployment requires publishing the updated controller-runtime
artifact through the normal release process; source changes alone do not deploy
it. Missing or
invalid runtime files yield a native diagnostic/retry path, not a fatal client
startup failure. This feature does not install WebView2 or require an Ultralight
Pro license.

The build hashes the final helper, four vendor DLLs, five VC runtime DLLs,
engine resources, and notices into a bounded `runtime-manifest.json`. Its
SHA-256 is embedded in the controller host. Before launching a helper, a worker
authenticates that manifest and its exact file inventory, rejects links and
unexpected files, and holds read handles that deny writes and replacement until
host shutdown. Shared pins avoid rehashing the large DLLs for every view; each
activation still checks the inventory. Integrity failures produce the native
repair diagnostic without blocking the UI. These checks detect corruption and
replacement; they are not attestation against an administrator controlling the
controller process. The ordinary Windows tests cover malformed/tampered files,
held writers, writable mappings, replacement, and cleanup.

Titan's host/helper use `/MT` (and `/MTd` for Debug); the vendor DLLs dynamically
depend on `msvcp140.dll`, `msvcp140_2.dll`, `msvcp140_atomic_wait.dll`,
`vcruntime140.dll`, and `vcruntime140_1.dll`. CMake stages
the licensed release DLLs from the installed MSVC redistributable component
beside the helper. The runtime upload allowlist requires them and checks their
valid Microsoft Authenticode publisher identity before publication. Windows
10/11 provides the Universal C Runtime. No redistributable installer, elevation,
or additional launcher download flow is needed for this app-local distribution.
The real helper smoke test verifies that all five modules load from the packaged
runtime directory, rather than succeeding accidentally through machine-wide DLLs.

Memory-only packaged resources are not a claim that the OS or engine can never
write plaintext. Runtime/profile/cache locations and temporary AppContainer
profiles require sentinel persistence tests and best-effort cleanup. Report
the observed residual-write results for the exact shipped engine pin; do not
describe another runtime version as covered by this pin's audit.

The dedicated manual Windows workflow, `html-ui-smoke.yml`, builds Debug and
Release helpers without private walker access. It runs the production host,
known-nonce isolation/negative control, DPI/input/replay/hidden lifecycle, and
the browser attack matrix. The matrix reuses the milestone-0 attack program,
checks a live loopback canary, and scans the fresh profile after confirmed helper
exit and before cleanup. Its sentinel scan is bounded to 4096 entries, 16 MiB
per file, and 64 MiB total, checks UTF-8/ASCII and UTF-16LE marker spellings, and
rejects reparse points/hard links. A complete clean scan of those locations is
an observation, not proof that no other engine/OS location or encoding can hold
plaintext. Helper-only soak results do not substitute for interactive
controller/game testing of focus, IME, modal overlap, clipping, and frame times.
The smoke build adds a test-only decodable bootstrap image and cleanup observers.
That image is removed from the memory resolver before any page exists; product
builds exclude it. The known-nonce negative control deliberately uses an unsafe
shared-helper topology and is never part of the shipped host.

## Validation record (2026-10-07)

The final source passed both complete native builds, including controller,
client, helper, native plugins, and tests. The test preset's Java-runtime build
option was off; the Java SDK, both runtime test targets, and embedded fat JAR
were built separately with Java 21. Upload and publication were disabled in
every validation configuration. The existing gameval manifest edit and recorded
walker revision were preserved.

| Validation | Result |
| --- | --- |
| Full test-preset Debug build and CTest | Build passed; 253/253 tests passed (107.15 s) |
| Full test-preset Release build and CTest | Build passed; 253/253 tests passed (56.82 s) |
| Isolated frozen Native ABI v1 fixture | 12/12 tests passed |
| Retained pre-feature Native ABI v1 DLL | Loaded by the new compatibility test without rebuilding the DLL |
| Retained SDK 144 Titan Fletcher DLL, current Debug and Release importers | Native side-panel and anchored-overlay callbacks passed without rebuilding the DLL |
| Isolated bundle/wire/input/editor/policy/snapshot/adapter/codec/integrity suite | 9/9 tests passed |
| Real AppContainer/Free renderer suite, Debug and Release | 52/52 tests passed in each configuration |
| Runtime unavailable configuration | Two ordinary tests passed; real-renderer test explicitly skipped |
| TypeScript SDK/sample typecheck and JavaScript contract tests | Passed |
| Java SDK tests | 108 tests passed |
| Java runtime-core tests | 84 tests passed |
| Java embedded-runtime tests | 23 tests passed |
| Java sample JAR and embedded runtime fat JAR | Built successfully |
| Native build-loop, build-scope, template, development-script, staging-lock and cleanup checks | Passed |
| Public SDK export tests | 12 passed; one symlink-privilege case skipped |
| Controller runtime staging, Debug and Release | Embedded manifest hash and all 16 staged file hashes matched |

The runtime-integrity executable passed 82 checks; three symlink-creation cases
reported explicit skips because this test account lacks that privilege. Those
three cases remain unverified on this machine. The retained old DLL's SHA-256 was
`f6ab02ffa7aa697250c00bfc2604d74eb695f24fa79482b33fc902e2db812234`.
Frozen records, optional-interface absence, short-table negotiation, missing
panel-kind decoding, and native registration behavior are covered separately.
This establishes compatibility for the retained fixture and tested contracts;
it is not a promise about every third-party DLL.

The separate retained UI test loaded the same SDK 144 `titan_fletcher.dll`
with both current Debug and Release hosts. Its SHA-256 remained
`f93ff9a10006dcf4ac557c82d0ea788915df9967a295d12d569f8028df8fe6cc`
before and after the tests. The `selections` native side panel produced 287
command records, with its 168-byte descriptor and caller capacity guards intact.
The `titan_fletcher_status` overlay retained anchor value 4, priority 50, and
255-pixel width; its title and ten lines crossed the existing host interface,
and it unregistered before DLL unload. Both new HTML interfaces were absent.
This harness supplies bounded host stubs, dispatches no game actions, and uses
an empty isolated profile that remained empty. It tests actual old binary UI
contracts and lifetime, not visual rendering in a game client.

All 34 browser-attack cases completed their post-exit sentinel scans. No case
contacted the loopback canary, produced forbidden output, or found a marker in
the scanned profile/report locations. Those fresh locations contained zero
files and zero scanned bytes. This result is intentionally scoped to the
configured scan roots and exact renderer pin, not all possible OS persistence.

Additional focused checks reject direct/indirect eval, Function constructors,
inline scripts and handlers, and string timers, including after a page removes
or weakens its CSP meta element. Packaged scripts, function timers, and normal
event listeners remain functional. PNG and SVG data images decode and render;
SVG-in-image scripts, event handlers, top-level navigation, and remote image
requests remain inert, with no loopback-canary contact. This concerns image data
URLs only; SVG is still not an accepted packaged-resource MIME type. These
expanded CSP/data-image cases passed in Debug and Release on the final helper.

The final Release helper soak completed 3,600 seconds and 1,000 scheduled
activation, visibility, theme, and viewport actions. It began with one sidebar
and eight overlays at 220×160 pixels, then exercised hidden-view retirement and
load-generation changes. Actions against already retired views can be no-ops;
this is a component stress harness, not 1,000 real controller/game UI actions.
No stale frame, message, or editor result escaped. Its final measurements were:

| Helper-soak metric | Observed result |
| --- | --- |
| Total helper processes over the hour | 203 |
| Cumulative aggregate-job user plus kernel CPU | 24.375 seconds |
| Peak sum of live helper private bytes | 339.67 MiB |
| Aggregate-job peak memory | 350.11 MiB |
| Peak queued data | 1,891 bytes |
| Completed frames / messages / editor updates | 218 / 203 / 206 |
| Idle-view retirements | 398 |
| Live helpers / queued bytes / frame-control bytes after shutdown | 0 / 0 / 0 |

CPU accounting includes exited helpers; the per-minute live-process counters
must not be summed to estimate hour CPU. The static fixture produces few changed
frames and does not measure animated HUD or game rendering cost. `Host::poll`
reported a maximum of 0 ms at the coarse `GetTickCount64` resolution; this is
not a zero-cost or input-latency measurement. Parent private bytes exclude shared
frame/control mappings. The separate nine-view Release smoke measured 338.35 MiB
of helper private bytes and 119.05 MiB of frame/control allocations; these are
different memory categories, not interchangeable RSS measurements. Other native
validation ran on the same machine during the soak, so it is not an isolated
performance benchmark.

Repeated host construction/destruction ran five 20-second, 1,000-transition
rounds per configuration. Debug's post-destruction handle counts were 175, 176,
178, 178, and 178; Release's were 176, 177, 179, 179, and 179. Each configuration's
final three rounds had identical handle-type inventories, with no retained Job
or Process handles and the same eleven Section handles as the cold process.
Every round ended with zero live helpers, queued bytes, and frame/control bytes.
These are process-local harness measurements, not controller or game HWND
measurements. The hour-run executable, helper, and manifest hashes were unchanged
through the Release repetitions; subsequent changes were test assertions only.

The final hygiene inspection found no renderer/probe processes, temporary HTML
profile folders or mappings, or residual unique AppContainer access entries.
It inspected 131 nodes across six known runtime trees without read errors.
These observations cover the completed runs and controlled helper failures;
cleanup after a hard crash of the parent controller process remains unverified.

### Reproducing the automated checks

These integration commands target the full client checkout, not a standalone
SDK export. Use its pinned walker/gameval setup in `docs/dev_loop.md` and
`.github/workflows/pr-quality.yml`. Set `VCPKG_ROOT` and `JAVA_HOME` locally
as described there. The commands below use dedicated build directories; do not
configure or build a directory concurrently from an IDE or another shell.

```powershell
python scripts/acquire_ultralight.py --destination build-html-deps/sdk
cmake --preset tests -DTITAN_GENERATE_GAMEVALS=OFF -DWEB_WALKER_BUILD_PROVIDER=ON -DTITAN_AUTO_UPLOAD_DLL=OFF -DTITAN_AUTO_PUBLISH_DLL=OFF "-DTITAN_ULTRALIGHT_SDK_ROOT=$PWD/build-html-deps/sdk"
cmake --build build-tests --config Debug --parallel 2
ctest --test-dir build-tests -C Debug --output-on-failure --timeout 300
cmake --build build-tests --config Release --parallel 2
ctest --test-dir build-tests -C Release --output-on-failure --timeout 300

cmake -S tests/fixtures/native_abi_v1 -B build-native-abi -A x64 -DTITAN_AUTO_UPLOAD_DLL=OFF -DTITAN_AUTO_PUBLISH_DLL=OFF
cmake --build build-native-abi --config Release --parallel 2
ctest --test-dir build-native-abi -C Release --output-on-failure

cmake -S tests/fixtures/html_ui_contracts -B build-html-contracts -A x64 "-DCMAKE_TOOLCHAIN_FILE=$env:VCPKG_ROOT/scripts/buildsystems/vcpkg.cmake" -DVCPKG_TARGET_TRIPLET=x64-windows-static -DTITAN_AUTO_UPLOAD_DLL=OFF -DTITAN_AUTO_PUBLISH_DLL=OFF
cmake --build build-html-contracts --config Release --parallel 2
ctest --test-dir build-html-contracts -C Release --output-on-failure

npm.cmd run typecheck
node tests/html_ui_js_tests.cjs
python tests/native_dev_loop_tests.py
python tests/native_runtime_build_scope_tests.py
python tests/native_template_setup_tests.py
python tests/native_dev_scripts_tests.py
python tests/native_stage_lock_tests.py
python tests/native_stage_cleanup_tests.py
python tests/public_sdk_export_tests.py
```

The manual retained UI target requires an existing pre-HTML Titan Fletcher DLL;
it deliberately does not rebuild or download that input. Supply its path and
an existing empty scratch profile directory:

```powershell
cmake --build build-tests --config Release --target native_retained_ui_compatibility_tests --parallel 2
./build-tests/Release/native_retained_ui_compatibility_tests.exe "$retainedDll" "$emptyScratchProfile"
```

From `shared/java`, run:

```powershell
./gradlew.bat --no-daemon test -PtitanGenerateGamevals=false
```

From `java`, run the runtime tests and packaged samples against this checkout's
SDK version (the quoted dotted property is important in PowerShell):

```powershell
./gradlew.bat --no-daemon :titan-java-runtime-core:test :titan-java-embedded:test :titan-sample-plugin:jar :titan-java-embedded:fatJar -PtitanGenerateGamevals=false '-PtitanSdkVersion=0.1.74'
```

The isolated real-renderer suite avoids private walker dependencies:

```powershell
cmake -S html_ui -B build-html-host -A x64 "-DCMAKE_TOOLCHAIN_FILE=$env:VCPKG_ROOT/scripts/buildsystems/vcpkg.cmake" "-DVCPKG_MANIFEST_DIR=$PWD/tests/fixtures/html_ui_contracts" -DVCPKG_TARGET_TRIPLET=x64-windows-static "-DTITAN_ULTRALIGHT_SDK_ROOT=$PWD/build-html-deps/sdk" -DTITAN_HTML_UI_TESTS=ON -DTITAN_AUTO_UPLOAD_DLL=OFF -DTITAN_AUTO_PUBLISH_DLL=OFF
cmake --build build-html-host --config Debug --parallel 2
ctest --test-dir build-html-host -C Debug --output-on-failure
cmake --build build-html-host --config Release --parallel 2
ctest --test-dir build-html-host -C Release --output-on-failure
./build-html-host/Release/html_ui_host_smoke.exe ./build-html-host/runtime/Release soak 3600
./build-html-host/Release/html_ui_host_smoke.exe ./build-html-host/runtime/Release soak-repeat
```

An absent SDK makes the real-renderer suite report an explicit skip. It does
not establish integration success. The dedicated workflow uploads test
diagnostics only; it does not publish runtime or plugin artifacts.

### Input and sidebar-switch validation (2026-10-07)

The input overflow fix passed the Debug repository CTest suite (255/255), the
standalone HTML contracts (10/10), the real-renderer suite (53/53), and
`npm.cmd run typecheck`. The Debug controller/helper build completed with both
upload/publish options off; all 16 staged runtime hashes and the controller's
embedded manifest matched. A probe using that staged runtime rendered the
actual World Hopper bundle, restored its state and shut down cleanly.

The real-helper queue test sent changed pointer positions and alternating
scroll input at 144 Hz for ten seconds, with 60 clicks. All input was admitted;
the final run measured 78 ms at both the 95th percentile and maximum for click
delivery. A separate burst of 5,000 moves and 1,000 scroll events preserved the
final position and scroll distance. Tests also cover reserved button/key
capacity, ordered IME composition, stationary/exit motion, resize releases and
preview owner/DPI/theme/expiry guards. These are automated renderer measurements,
not end-to-end game input latency. No Release rebuild or publication was done
for this follow-up.

### Saved view state validation (2026-10-07)

The Debug real-renderer suite passed 54/54, including the known-nonce negative
control, all 34 browser-attack cases, the World Hopper case (whose page now
saves view state) and the new `view-state` case. That case replays a saved page
value and scroll position into a fresh activation and receives page saves and
captured scroll positions through the host. It also keeps a save made
immediately before `Host::close`, and checks that oversized and over-deep saves
throw in the page without stopping the renderer. `html_panel_policy_tests`
covers nonce fencing, identity isolation, size limits, forgetting on failure
and the 64-entry bound; removing the nonce fence makes it fail. The World
Hopper Node (20) and native parity tests passed. `controller/html_panel_host.cpp`
compiled in the Debug test tree, but the controller was not linked or run
interactively, and no Release build was made.

### First-load timing (2026-10-07)

`html_ui_host_smoke.exe RUNTIME first-load [runs]` is a manual benchmark, not a
CTest case. It measures `Host::open` to the first published frame for a blank
page and the World Hopper bundle. Each case runs on a fresh host, like the
controller's first HTML panel of a session, and on a long-lived host, like
every later panel. Debug medians on the development machine, after the helper
began stepping the engine at the 4 ms input floor until a page's first frame
(previously at the 60 Hz frame interval):

| Case | Fresh host | Long-lived host |
| --- | --- | --- |
| Blank page | 99 ms (118 ms before) | 62 ms (75 ms before) |
| World Hopper, one 40-row page | 167 ms (188 ms before) | 133 ms (152 ms before) |
| World Hopper, full 260-world list | 214 ms | 162 ms |

World Hopper no longer pages: the plugin sends every filtered world that fits
the 64 KiB state limit and the page builds 60 rows per animation frame, so
only the first screenful precedes its first paint. The helper also wraps the
already validated state as text instead of parsing and re-serializing it,
which took about 7 ms per state update for a 33 KB list in Debug. Run-to-run
spread on this machine is about 10 ms.

Temporary helper instrumentation, since removed, split a long-lived World
Hopper load into roughly 33 ms of sandboxed process launch, 8 ms of ICU and
certificate hashing, 1.4 ms for `Renderer::Create`, about 20 ms from page
creation to DOMReady, 16 ms of the page's first state render and 46 ms in the
first `Renderer::Render`. That last cost is one-time text set-up in each new
helper: a script-free text render at helper start reduced the page's first
`Render` to about 3 ms but took about 54 ms itself, so it only helps when done
before a panel is opened. A fresh host also spends about 40 ms authenticating
and pinning the runtime before its first helper starts. These are helper-side
measurements; they exclude the client activation round trip and controller
texture upload.

### Interactive acceptance still required

No world-ready game client was available during this validation. Consequently,
the helper smoke/soak cannot establish the following end-to-end results:

- Controller sidebar clipping under menus/modals, embedded and floating game
  windows, live resize, minimize/restore, and monitor/DPI transitions.
- Actual OpenGL overlay compositing with native overlays, saved native layout
  preservation, Alt-drag, selection, Tab/Shift-Tab, Escape, wheel and paired
  input ownership, and native Windows IME candidate windows.
- Repeated real client/tab/native-panel/HTML-panel changes, concurrent overlays,
  themes and plugin hot reloads, with actual controller HWND/COM/GL resources.
- Native-only versus HTML-enabled game frame times, texture-upload cost,
  end-to-end input latency, total helper CPU and controller responsiveness.

Run those checks with a world-ready test client and representative plugins.
Record at least one hour and 1,000 real UI transitions, including shutdown,
and retain the native-only baseline. Do not infer that Ultralight is faster,
that interactive acceptance passed, or that production deployment occurred
from the automated results above.
