# Titan Java Plugin API

Public Java SDK for TitanClient plugins.

Plugins can subscribe on the game thread to these observations in
`net.titan.api.events`. `WorldChanged` fires after each completed world
selection. `CanvasSizeChanged.width()` and `height()` give the physical
pixel dimensions of the game drawable, in resize order. `VarClientIntChanged`
and `VarClientStrChanged` expose `index()` after each observed write,
including repeated writes of the same value. `WidgetLoaded` gives the group of
an interface whose load scripts have run, and `WidgetClosed` the group of one
about to close, with how it was opened and whether its widgets go. `LootReceived`
tells of what a monster or a player left for the account; `loot()` has whose it was
and the items ([rules](../titan/PUBLIC_API.md#loot-sdk-194)).

Use [Swing panels and Java2D overlays](../docs/java_swing_and_graphics.md)
for a real sidebar component and a HUD with Titan's native Alt-drag placement.

For `WorldView -> Scene -> Tile[][][]` access, live-handle freshness and RuneLite
adapter migration, see the [scene API guide](../../docs/scene_api.md) (SDK 174 /
Java 0.2.03). Repeated tile reads use complete cached scene captures instead of
one native object query per tile.

For a runnable standalone starter that plugin authors can copy or fork, see
[`Soxs/titan-java-sample-plugin`](https://github.com/Soxs/titan-java-sample-plugin).

## Consume The SDK

Published releases use Maven coordinates:

```gradle
repositories {
    maven { url = uri('https://raw.githubusercontent.com/Soxs/titan-public-sdk/main/maven/releases') }
    mavenCentral()
}

dependencies {
    compileOnly 'net.titan:titan-plugin-api:latest.release'
}
```

Use `compileOnly`: TitanClient's embedded Java runtime supplies the API classes at
runtime. Plugin JARs should contain plugin classes annotated with
`@PluginDescriptor`, not their own copy of the SDK or Guice.

Use `latest.release` while developing to pick up the newest published SDK.
For reproducible plugin releases, pin an exact SDK version such as `0.1.24`.

The SDK exposes Guice as a compile dependency so plugin authors can use:

```java
import com.google.inject.Inject;
```

TitanClient's embedded runtime provides the Guice runtime.

## RuneLite plugins

A plugin written with RuneLite's API and framework needs one line of Gradle:

```gradle
plugins { id 'net.titan.runelite' version '0.2.0' }
```

It brings `net.titan:titan-runelite` (RuneLite's plugin framework, HTTP types and game
API, with the libraries they use), Titan's SDK and Lombok, all compile-only, and the
tasks of the Titan loop. No RuneLite repository or dependency is needed.

| Task | |
|---|---|
| `run` | Build, and open Titan with the plugin. Hot reload it when that tab is already open. |
| `reload` | Build, and hot reload it in the open tab. `reload -t` does it on every save. |
| `runDebug` | `run` with a Java debugger port. |
| `build` | Build, check the JAR, and list the RuneLite calls and events Titan does not supply yet. |

Start with the [template](https://github.com/Soxs/titan-runelite-plugin-template), or copy the same
[starter project](../examples/runelite-plugin). It has IntelliJ and VS Code
configurations and instructions for coding agents.

The plugin extends `net.runelite.client.plugins.Plugin`. Titan's SDK is there beside
RuneLite's: `@Inject private net.titan.api.Client titan`, or the static `net.titan.api.Titan`.
Events, overlays, panels, settings and scheduling are RuneLite's. Titan's event bus, `Plugin`,
configuration, panels, `Titan.getInjector()` and managed instances belong to Titan-style plugins.

[What Titan supplies of RuneLite](../README.md#runelite-support), in numbers.

Titan gives its SDK to a developer's own RuneLite plugin, from a JAR it loads itself. A
plugin installed from RuneLite's Plugin Hub does not see it.

## Using the RuneLite game API from a Titan plugin

A Titan-style plugin can use RuneLite's game API too. Add the extended RuneLite API
alongside the ordinary Titan plugin SDK. The
matching client supplies both dependencies at runtime. Keep them out of your
plugin JAR.

```gradle
dependencies {
    compileOnly 'net.titan:titan-plugin-api:0.2.20'
    compileOnly 'net.titan:titan-runelite-api:1.13.6-titan.3'
}
```

In a private client checkout you can try these coordinates before SDK
publication by adding `includeBuild("<path to the checkout>/shared/java")`
in your plugin's Gradle settings. Use the Java 11 target.

Use Guice's `@Inject` on a `net.runelite.api.Client` field or constructor
parameter in an ordinary Titan plugin. The client belongs to the current game
tab. The attached sources follow RuneLite's packages, including
`net.runelite.api.coords`, `net.runelite.api.events` and `net.runelite.api.widgets`.

```java
import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.WorldView;

// Inside your Titan Plugin. Call from its game-thread callback for a selected NPC.
@Inject private Client client;

private boolean talkTo(int worldViewId, int npcIndex) {
    WorldView world = client.getWorldView(worldViewId);
    if (world == null) return false;
    NPC npc = world.npcs().byIndex(npcIndex);
    return npc != null && npc.interact("Talk-to");
}
```

`NPC.interact(String)`, `Player.interact(int)` and `net.runelite.api.widgets.Widget.interact(String)`
are Titan extensions. A true result means Titan accepted the request. For players,
pass the one-based slot (1–8) of an action currently offered on the top-level
player's menu. Titan checks the player and option again at dispatch. Widget
actions use the visible menu label and support Titan's
submenu syntax, such as `widget.interact("Op/Subop")`. Observe subsequent
game state to determine the outcome.
Use the usual Titan event bus and lifecycle for ordinary Titan plugins. RuneLite
game API coverage depends on which methods the installed compatibility provider
implements. Plugins using this extension require the matching Titan runtime.

## Swing side panels

Ordinary Titan Java plugins can host real Swing components in the same native
sidebar as other plugin panels. Register once in each `onEnable()`:

```java
import net.titan.api.swing.SwingPanel;
import net.titan.api.swing.SwingPanels;
import javax.swing.JButton;
import javax.swing.JPanel;

private SwingPanel sidebar;

@Override public void onEnable() {
    sidebar = SwingPanels.of(this).register("tools", "My tools", () -> {
        JPanel panel = new JPanel(); // Factory runs on the EDT when selected.
        panel.add(new JButton("Example"));
        return panel;
    });
}

@Override public void onDisable() {
    if (sidebar != null) sidebar.close(); // Optional; host also revokes on disable.
}
```

The full registration overload accepts priority and optional owned PNG icon
bytes. The factory must return an unparented component tree exclusive to this
plugin. The entry is published after successful enablement; `ready()` reports
registration errors. Older or unsupported hosts fail explicitly without opening
a standalone window. No RuneLite classes are required.

Command-capable hosts also support action-only toolbar buttons and ordered
right-click menus on either buttons or content panels:

```java
import java.util.List;
import net.titan.api.swing.SwingCommand;

// In onEnable; both callbacks and menu suppliers execute on the owning EDT.
SwingPanel action = SwingPanels.of(this).action("reset", "Reset counter",
    () -> resetCounter(),
    () -> List.of(new SwingCommand("Reset counter", () -> resetCounter()),
                  new SwingCommand("Show tools", () -> sidebar.requestOpen())));

// The additional last argument on register(...) attaches a dynamic popup menu.
// The component factory stays lazy even when its toolbar popup is opened.
```

The menu supplier runs for each popup request and returns at most 32 commands
in display order. Titles contain 1–256 UTF-8 bytes without ASCII controls.
Selecting a command invokes the callback captured for that popup once, even
if a later supplier result uses the same title for a different action. Opening
a menu invokes no command. A normal content-panel click opens its panel;
an action-only click invokes its action and creates no Swing frame. Its
`requestOpen()` returns false. These additive methods report unavailable on
older hosts; existing content registrations keep their original contract.

Disable, close, reload and re-registration invalidate old commands and queued
EDT work. Accepted actions are not automatically retried after an uncertain
completion. Callback-posted Swing events retain the exact registration context;
retirement prevents them from acquiring a later enable cycle.

Swing listeners run on the EDT with the exact owning plugin context. They are
not game-thread callbacks. Use the SDK's explicit scheduling APIs for game-thread
work. Arbitrary worker threads and completion-stage continuations do not gain
Swing callback attribution. Closing a registration revokes queued callbacks
immediately and asynchronously disposes its embedded peer and owned popups;
`closeAsync()` completes after disposal and admitted callbacks unwind. Never
wait on either future from the EDT or a lifecycle hook. Re-enable must create a
new registration; old handles cannot select or remove its replacement. Native
sidebar hide/reopen preserves the panel tree within the same enable cycle.

Use the [Swing Counter example](../docs/java_swing_and_graphics.md) to test
sidebar editing alongside an anchored Java2D HUD.

## Plugin injection (0.2.04+)

Each loaded plugin has a stable Guice injector for its lifetime. The plugin's
concrete class resolves to the same instance that receives lifecycle callbacks,
including self-references injected into helpers. Loading or unloading another
plugin does not recreate its injector, singleton helpers or injected members.
Unloading and reloading the plugin creates a fresh scope.

`Titan.getInjector()` and `Titan.getInstance(...)` use the current plugin's scope
inside its construction, injection and callbacks. For work outside those
callbacks, inject and retain `com.google.inject.Injector` in the plugin or pass
the required dependencies to the worker. Discard that injector on unload. The
injected EventBus retains the plugin's owner even when used from a worker.
Lookups outside a plugin context use the aggregate host injector and are not a
way to retrieve plugin-private singleton helpers.

Ordinary `Plugin` implementations keep constructor injection. To opt into
RuneLite-style `configure()` and `@Provides` support, also implement Guice's
`Module` interface (or extend `AbstractModule`):

```java
public final class ExamplePlugin implements Plugin, com.google.inject.Module {
    @Inject private com.google.inject.Injector injector;
    @Inject private Helper helper;

    @Override
    public void configure(com.google.inject.Binder binder) {
        binder.bind(Helper.class).in(com.google.inject.Scopes.SINGLETON);
    }

    @com.google.inject.Provides
    @com.google.inject.Singleton
    PluginState provideState() {
        return new PluginState();
    }
}
```

A module plugin must have a no-argument constructor. Titan constructs it, binds
that exact instance, installs the module and lets Guice inject its members once.
Constructors and `configure()` on module plugins define bindings; use injected
parameters in providers and `onLoad()` for service-dependent initialization.
The injector is not usable during module construction/configuration. Helpers
only persist when explicitly scoped, for example with `@Singleton` or the
binding above; unscoped bindings retain normal Guice behavior.

Module bindings are private to that plugin. Declare plugin dependencies so
their instances are loaded before the dependent scope is constructed. A scope
cannot silently construct another unmanaged Titan plugin. Existing foreign
plugin/config bindings become unavailable when their owner unloads and cannot
attach to its replacement. Descriptor-declared config interfaces are already
bound by Titan; do not also provide the same config key in the module.

These rules apply to the Titan-managed plugin entry point. If an adapter creates
a separate RuneLite plugin object, it must bind that exact object in its own
retained injector, install its module and inject it once. Do not bundle another
Guice runtime or create a fresh injector for each helper lookup.

## Selected widgets (0.2.02+)

`Client.isWidgetSelected()` reports whether a spell or an item selected with
Use is selected for targeting. `Client.getSelectedWidget()` returns that live
`Widget`, or `null` when selection is inactive, unsupported, or its widget no
longer resolves, matching RuneLite. The boolean can still be true when the
widget is absent. Dynamic selections retain their child address for later
reads and interactions; a selection without a child returns the base widget.

## Overlay layers (0.1.96+)

`OverlayLayer.ABOVE_SCENE` draws below native widgets. `ABOVE_WIDGETS` retains
protocol value 1 and draws above ordinary widgets but below native menus and
tooltips. Use `ABOVE_ALL` (protocol value 2) for overlays that must also cover
native menus and tooltips. Structured overlay panels remain on `ABOVE_WIDGETS`.

## Item sprites and outlines (0.1.95+)

`Titan.itemSprites().item(itemId, quantity)` returns an owned native icon when
ready. An accepted cache miss returns `ItemSprites.Status.PENDING`; request it
again on a later callback. `NOT_READY` means the host has not yet observed a
successful native item render, resources are unavailable, or the queue is full;
retry later. Requests are safe on plugin workers. The host performs native
rendering on its owner thread, and no native pointers reach Java.

Prepare icons on a game-thread callback after login and world readiness, then
publish an immutable list through a
`volatile List<SpriteImage> prepared = List.of()` field.
Clear it on game-state changes and plugin disable:

```java
// In the game-thread callback, with login/world readiness already checked:
if (prepared.isEmpty()) {
    ItemSprites.Result result = Titan.itemSprites().item(4151, 1);
    if (result.ready()) {
        SpriteImage image = result.image().orElseThrow();
        prepared = List.of(image, image.outline(0xff00ff00, 1));
    }
}

// In renderOverlay: use only the published images and copied positions.
for (SpriteImage image : prepared) Overlay.draw().sprite(image, 100, 100);
```

Keep widget queries, requests, pixel copies, and outline preparation out of the
render callback. Cache reusable images and outlines in the plugin rather than
preparing them every tick. `SpriteImage` is immutable;
`pixels()` returns a copy. Its ARGB32 pixels use straight alpha. Outlines are pure
CPU outer rings, preserve the original canvas origin using negative offsets,
and accept thickness 1..8. Default item options omit quantity text, border, and
shadow so the outline follows the item silhouette. Quantity still selects the
correct native stack variant. `ItemSprites.Options` can enable native border,
RGB shadow, or `QuantityText.ALWAYS` when drawing ordinary icons.

Drawing is valid only inside an overlay callback, with finite scale greater
than zero and at most 16; unsupported hosts return
`UNSUPPORTED` for requests and `false` for drawing. See
[item sprites](../../docs/item-sprites.md) for an inventory outline example and
the matching C++/JavaScript APIs.

## Menu entries (0.1.94+)

Use `Client.getMenuEntries()`, `setMenuEntries(...)`, `swapMenuEntries(...)`,
`createMenuEntry(index)` and `removeMenuEntry(...)` from synchronous
`PostMenuSort`, `MenuOpened` or `MenuEntryAdded` subscribers. Entries use
RuneLite's bottom-to-top order; `createMenuEntry(-1)` inserts at the top.
`isMenuEditingAvailable()` reports native support and `isMenuEditable()` reports
whether the current callback can edit the menu.

```java
@Subscribe
public void onPostMenuSort(PostMenuSort event) {
    if (!client.isMenuEditable()) return;
    client.createMenuEntry(-1)
        .setOption("Inspect")
        .setTarget("My plugin")
        .setType(MenuAction.RUNELITE)
        .onClick(entry -> logger.info("Clicked " + entry.getOption()));
}
```

Setters are fluent and apply to the live entry. `setOpcode(int)` also accepts
plugin-defined opcodes; `getType()`/`setType(int)` are aliases using the existing
integer `MenuAction` constants. A custom opcode identifies a plugin action and
does not dispatch a native game operation by its number. Existing
`MenuOptionClicked` consume/replace subscriptions remain available. An `onClick`
callback runs before an existing entry's normal game action and does not consume
it. Global `MenuOptionClicked` consumption suppresses both that action and the
entry callback. Custom entries remain host-only. A callback failure suppresses
the action.

Java SDK 0.1.97 preserves all 32 bits of custom opcodes, including negative Java
`int` values such as `0xffffffff` (`-1`). The native protocol receives their
unsigned representation. World-view IDs must fit the native signed 32-bit field;
out-of-range `long` values are rejected before crossing JNI.

Existing game entries retain their original native action identity. Their labels
can be edited, but changing opcode, identifier, parameters, item ID or world-view
ID is rejected. Custom entries support those logical fields.

`setMenuEntries` atomically reorders siblings and can omit leaf entries to remove
them. It operates on roots; use a submenu entry's `getChildren()` and
`setChildren(...)` for its existing children. The game's own submenus remain attached and
cannot be removed, reassigned or added to.

From SDK 194 an ordinary root entry can have a submenu of plugins' own.
`createSubMenuEntry(index)` adds to it, `getChildren()` and `setChildren(...)` read and
change it, and `deleteSubMenu()` removes it. The game's menu has no place for one, so its
entries are drawn right under their heading, with the heading's option before their own.
`getSubMenuBounds()` is where the game draws one of its own submenus, and
`GameData.menuBounds()` where it draws the menu. Entry text must fit in 255 UTF-8 bytes and contain no
NUL. Rejected operations throw `IllegalStateException`, and invalid Java
arguments throw `IllegalArgumentException`.

Handles expire when the menu is rebuilt. Create entries and register their
callbacks again for each menu generation. Callbacks run synchronously with the
owning plugin's context and are removed on disable or unload. Keep all menu
handlers short; retained entries can be read as snapshots, but stale edits are
rejected. Older native hosts report unavailable and expose no entries.

## Managed instances and entitlements (0.1.92)

Apply `net.titan.dev` 0.1.3 and keep `titan-plugin-api` compile-only. The convention
plugin bundles and relocates `titan-authorization` into your plugin JAR, so signed
receipts are verified independently inside each plugin. This helper is packaged;
the shared API remains provided by the host.

Bind an existing bot with `Instance.bind(this, bot)` from `net.titan.authorization`,
then connect buttons to `run.start()` and `run.stop()`. The SDK acquires capacity
before startup and handles reporting, outages, expiry and lifecycle cleanup.
`Entitlements.forPlugin(this)` provides `check()`, `check(key)`, `list()`,
`snapshot()` and `onChanged(...)` for the customer's scoped library.
See the public SDK's `docs/plugin_authorization_java_js.md` and managed instance
sample for complete examples. These APIs require the matching server/client rollout.

## Keyboard interception (0.1.88+, client SDK 157+)

Subscribe to `net.titan.api.events.KeyInputEvent` to consume or remap input before
the game receives it. Key codes are raw Win32 VK values; they are not the internal
`KeyboardKey` IDs used by the typing API. Modifier masks use `KeyboardModifiers`.

```java
@Subscribe
public void onKeyEvent(KeyInputEvent event) {
    if (event.type().equals(KeyInputEvent.KEYDOWN) && event.keyCode() == 'W') {
        event.replaceWith(0x26); // VK_UP, preserving the original modifiers
    }
}
```

`consume()` suppresses the event and cancels any replacement. `replaceWith(vk,
mods)` consumes the original key edge and substitutes a key with the requested
modifiers. The first keydown fixes the route for its repeats and paired release,
even if a later callback consumes or changes that release. Focus or input-capture
changes release active replacements. Original typed characters from consumed or
replaced keys are suppressed, and replacements do not generate new typed
characters. Other `CHAR` events expose one UTF-16 code unit through `character()`;
they may be consumed but cannot be replaced.

Handlers run synchronously on the window input thread. Keep them short and avoid
game-thread-only APIs; cache any needed game state from normal game events. A
staged Java runtime predating this feature leaves input unchanged.

## Actor game-data snapshots (0.1.86+)

`Actor.animationProgress()` returns immutable primary/action animation progress;
`NPC.modelOverrides()` returns an immutable runtime body-model ID list. Read them
from a game-thread callback on a live actor. Results own their data and remain
valid after the actor changes. Calls on a retained actor validate its original
slot, pointer and world view; detached actors return `INVALID_ACTOR`.

```java
ActorAnimationProgress progress = actor.animationProgress();
if (progress.active() && progress.frame() == 0) {
    // The primary sequence is currently at frame/progress zero.
}
NpcModelOverrides overrides = npc.modelOverrides();
if (overrides.active()) {
    List<Integer> ids = overrides.modelIds(); // Immutable, possibly empty.
}
```

`AnimationSequenceMode.CLASSIC` uses a frame index and supports
`frameCycleAvailable()`/`frameCycle()`. `CACHED` uses progress in client ticks and
has no meaningful frame-cycle value. Signed frame sentinels are preserved.
Inactive progress has ID/frame `-1`; unavailable data never represents frame
zero. Equal-ID animation resets still do not produce `onAnimationChanged`.
Polling only observes state at the chosen callback phase.

`ActorDataStatus` distinguishes `UNAVAILABLE`, `INVALID_ACTOR`, `UNREADABLE`,
`INACTIVE` (also `NONE` for overrides), `ACTIVE`, `BUFFER_TOO_SMALL` and
`WRONG_THREAD`. `available()` is true for active or successfully read inactive
data. An active model customization with an empty list differs from no
customization; the list preserves signed IDs, including `-1`. It contains stored
runtime overrides, not NPC definition models. Hosts without the optional native
capability return `UNAVAILABLE`.

## Packaged HTML sidebars and overlays (0.1.74+)

Java SDK 0.1.98 provides `net.titan.api.panel.Panels.of(this).requestOpen("tools")`
for both `@SidePanel` and `@HtmlSidePanel`. Call it while the plugin is enabled,
for example in a menu action. It requests selection and expansion of the owning
client's sidebar without switching game tabs. Unknown IDs, disabled or retired
instances and older runtimes return false; true means accepted, not yet shown.
Keep the owner-bound facade on the plugin or pass it to worker helpers. Native
panels do not require the HTML renderer. The embedded adapter polls only request
counters at most once every 100 ms; delivery also depends on the next host and
controller update. Disabling discards requests not yet handed to the host.

`@HtmlSidePanel` is separate from the existing ImGui `@SidePanel`. Both kinds
share eight sidebar slots and one ID namespace. Repeatable `@HtmlOverlayPanel`
declarations have eight slots in a separate overlay namespace; their IDs must
not collide with a native `OverlayPanel` name. Each HTML ID is 1–31 characters
of `[A-Za-z0-9._-]`. Overlays default to 220×160 CSS pixels and click-through
input; supported dimensions are width 80–600 and height 24–600.

```java
@PluginDescriptor(id="html_counter", name="HTML Counter",
    description="Packaged HTML counter", author="Your Name", version="1.0.0")
@HtmlSidePanel(id="counter", title="Counter", resourceRoot="counter")
public final class CounterPlugin implements Plugin {
    private final HtmlPanels panels = HtmlPanels.of(this);
    private int count;
    @Override public void onLoad() { publish(); }
    @Override public void onHtmlPanelMessage(String id, HtmlMessage message) {
        if (id.equals("counter") && message.type().equals("increment")) {
            ++count;
            publish();
            panels.postMessage(id, "updated", Integer.toString(count),
                message.correlationId().orElse(null));
        }
    }
    private void publish() {
        panels.setState("counter", "{\"count\":" + count + "}");
    }
}
```

Import annotations from `net.titan.api.plugins` and helpers/message types from
`net.titan.api.html`. `HtmlOverlays.of(this)` has the same `setState` and
`postMessage` methods, plus `setSize(id, width, height)` and
`setVisible(id, visible)`; overlay messages use `Plugin.onHtmlOverlayMessage`.
Helpers retain the exact loaded plugin instance. They are thread-safe, return
`HtmlUiResult`, and need no JSON library: inputs and callback payloads are JSON
strings. Constructor writes return `STALE`; `onLoad` may initialize state.
Callbacks execute on MainLoop under the native lifecycle gate. A new plugin
instance cannot inherit an old helper's authority.

Put `index.html`, `style.css`, `app.js`, and resources under
`src/main/resources/counter/`. The default entrypoint is `index.html`; specify
`entrypoint` to change it. Resources are copied from the declaring JAR, including
marketplace JARs held in memory. No resource extraction or loose release files
are needed. The sample in `java/titan-sample-plugin` includes a sidebar and an
overlay using this contract.

Declare HTML surfaces unconditionally and choose overlay visibility from plugin
state. Renderer startup is lazy; an unused HTML feature starts no helper.
The controller shows native diagnostics and retry controls if a requested
surface cannot start. The C++/JS observed-availability flag is not a runtime
installation preflight and must not gate initial declarations or visibility.

V1 bundles allow at most 128 resources, 2 MiB per resource and 8 MiB total.
Paths are relative ASCII paths of at most 255 characters using letters, digits,
`/`, `.`, `_` and `-`; empty/dot/traversal segments, percent escapes, absolute
paths, backslashes, duplicate names and case collisions are rejected. HTML,
CSS, JS, JSON, plain text, PNG, JPEG, WebP, WOFF and WOFF2 are supported through
their usual file extensions. Text must be valid UTF-8. SVG, GIF, ICO, TTF and
arbitrary binary MIME types are not supported.

Use bundled classic scripts and stylesheets. The page's `titanHtml` bridge
exposes `getState`, `onState`, `postMessage`, `onMessage` and `resource` for
packaged JSON/text resources. Remote URLs, fetch/XHR, frames and browser host
objects are unavailable. Theme CSS variables include `--titan-bg`,
`--titan-text`, `--titan-muted`, `--titan-accent`, and `--titan-border`.

The v1 message is `{version:1, type, payload, id?}`. Type is 1–128 printable
non-space ASCII characters; optional correlation ID is at most 128 UTF-8 bytes
without NUL. Complete messages and state are limited to 64 KiB UTF-8 with at
most 32 JSON nesting levels; duplicate keys and malformed Unicode are rejected.
State coalesces to the latest replacement and replays after switching or
recreating the surface. It survives resize and disable/enable; unload destroys it.
Transient messages preserve order while active, with a 64-message/1 MiB queue
per surface and an 8 MiB Java-client aggregate queue budget. Acknowledgements
free only delivered messages from the matching activation. Overflow rejects the
new write; deactivation drops transients. A successful write confirms acceptance,
not page execution. State revisions and message sequences are independent.

The optional annotation `image` names a packaged PNG icon in the same JAR and
is copied at inspection/load time with a 256 KiB limit. Existing native panel
icons retain their prior behavior.

Browser/cache persistence is a separate renderer concern: packaged JAR delivery
does not establish that the renderer makes zero plaintext disk writes.

## Main Loop Event (0.1.43+)

`MainLoop` fires from the client's outer loop in every state, including the
title/login screens. Static cache definitions are always available. Gate live
client, entity, widget, scene, and projection queries on world readiness:

```java
import com.google.inject.Inject;
import net.titan.api.Client;
import net.titan.api.eventbus.Subscribe;
import net.titan.api.events.MainLoop;
import net.titan.api.utils.Cache;
import net.titan.api.utils.Login;

public final class MyPlugin {
    @Inject private Client client;

    @Subscribe
    public void onMainLoop(MainLoop event) {
        Cache.item(4151); // safe in every state
        if (!Login.isWorldReady()) return;
        client.localPlayer().ifPresent(player -> use(player));
    }
}
```

## Queued GE Buying (0.1.64+, native SDK 139)

`net.titan.api.utils.Ge` is a thin binding to the C++ host queue. Submit once
with `Ge.addBuyToQueue(itemId, quantity)` or
`Ge.addBuyToQueue(GeBuyOptions)`, retain the returned handle, and poll
`Ge.request(handle)`. Zero means rejected or unsupported. Check
`GeRequest.isSuccessful()` separately from `isComplete()`; failed requests can
have partial fills. Release terminal handles with `Ge.release(handle)`.
No plugin context is required. The queue is shared; disabling/unloading a plugin
does not cancel purchases. Cancel explicitly with `Ge.abortRequest(handle)`;
`Ge.clearExchangeQueue()` cancels all pending requests in the shared queue.

Defaults are noted collection to inventory, no auto-opening, three attempts,
10 seconds per offer and a three-minute overall deadline. The three-argument
boolean overload enables auto-opening. Options also expose collection mode,
attempt/deadline limits and an optional unit-price ceiling. With SDK 175 hosts,
`new GeBuyOptions(itemId, quantity).withInitialPriceSteps(4)` starts with four
+5% button presses, matching the old `ExchangeRequest.setAttempts(4)` price.
The initial steps (0–100, default 0) do not consume the attempt budget; retries
add another step and still respect the ceiling. Nonzero steps are rejected on
hosts without the optional capability. The user supplies
coins in inventory/bank; Java owns no workflow or cache. Selling is a stub.
See the [cross-SDK contract and examples](../titan/PUBLIC_API.md#queued-ge-buying-sdk-v139--java-0164)
for retries, cancellation, collection and lifecycle behavior.

## Remembered Bank Items (0.1.62+, native SDK 138)

`ItemCache.bank()` (also `client.getItemCacheBank()`) returns the current
character's shared bank snapshot. Check `known()` before treating a zero count
as missing stock, and `live()` before treating remembered quantities as current.
`ItemCache.count(995)` and `getItemsCountInBank(int... ids)` return remembered
quantities; `countByName(names, ignore)` and the string overloads support
case-insensitive substring matching and exclusions.

`ItemCache` is a read-only binding to the C++ service: observation, account
switching, state, and persistence all run in C++. Java only reads and queries
returned snapshots.

`Bank.getAll/find/contains/count` prefer a readable open bank, then fall back to
ItemCache, then empty. A valid empty live bank overrides remembered stock.
Cached items and
their collection are owned read-only values; use Bank action helpers to resolve
current slots while the bank is open. A previously saved bank is restored by
character name on login, and opening the bank confirms its contents. The cache
persists in `%USERPROFILE%\.titanclient\item_cache` independently of plugins.
Unknown banks, stale memory, and loading/storage errors are visible in the
snapshot and the Dev Tools Item Cache Inspector.

## Shared Item Prices (0.1.61+, native SDK 137)

`Titan.itemPrices()` and `client.itemPrices()` read the same client-wide public
Wiki catalog and quote cache used by native and JavaScript plugins. Call
`requestCatalog()` or `request(itemId)` to enqueue a refresh; requests coalesce
and respect the shared cache and retry limits. Reads do not start network work.

`item(id)`, `items()`, `price(id)`, and `status()` return immutable owned values.
Prices, alchemy values, buy limits, and trade timestamps use `OptionalLong` so
missing data stays distinct from zero and 64-bit GP remains exact. Failed
refreshes retain earlier quotes with `fetchedAt()`, `lastAttemptAt()`, and
`error()`; `status().catalogRevision()` lets consumers cache catalog lists.
Only public item IDs are requested: no private offers or account data are sent.

## Grand Exchange Offers (0.1.60+, native SDK 136)

`Client.getGrandExchangeOffers()` returns immutable snapshots of the local
player's slots in slot order, including empty slots. `getGrandExchangeOffer(slot)`
returns an `Optional` for one zero-based slot. Use
`client.isGrandExchangeAvailable()` to distinguish unavailable native data from
an empty offer. These APIs are also available through `Titan.client()`.

```java
import net.titan.api.GrandExchangeOffer;
import net.titan.api.eventbus.Subscribe;
import net.titan.api.events.GrandExchangeOfferChanged;

@Subscribe
public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged event) {
    GrandExchangeOffer offer = event.getGrandExchangeOffer();
    int slot = event.getSlot();
    long price = offer.getPrice();
    long completedGold = offer.getSpent();
    // Retaining this offer is safe: later updates cannot change the snapshot.
}
```

States follow the RuneLite names: `EMPTY`, `BUYING`, `BOUGHT`, `SELLING`, `SOLD`,
`CANCELLED_BUY`, `CANCELLED_SELL`, and `UNKNOWN`. `getQuantitySold()` is the
completed quantity on both buy and sell offers; `getSpent()` is completed gold
on either side. Price and completed gold are Java `long`, preserving the native
client's 64-bit values. This API reads existing offers; it does not submit or
cancel trades.

On initial observation after attach/login, events first provide a synthetic
`EMPTY` baseline for every slot, followed by observed offers. This initialization
does not mean a trade was cancelled or collected. Later native updates produce
events even when the offer values are unchanged. Delivery occurs on the game
thread at the next safe main-loop phase; use the event's retained snapshot when
processing that update, since a fresh client query may already reflect a later
offer.

## Hot Reload For Plugin Authors

The `net.titan.dev` Gradle plugin (published into the same Maven repo as the
API) provides the TitanClient dev loop: it stages your built JAR into a
versioned dev session, writes the `session.json` manifest the controller reads,
and launches/recycles a dedicated DEV tab. After the first launch a plain
`build` re-stages the next generation so the in-tab refresh button hot-reloads
it without restarting the client.

Declare the plugin repo in `settings.gradle` and apply the plugin in
`build.gradle`:

```gradle
// settings.gradle
pluginManagement {
    repositories {
        maven { url = uri('https://raw.githubusercontent.com/Soxs/titan-public-sdk/main/maven/releases') }
        gradlePluginPortal()
    }
}
```

```gradle
// build.gradle
plugins {
    id 'java-library'
    id 'net.titan.dev' version 'latest.release'
}

dependencies {
    compileOnly 'net.titan:titan-plugin-api:latest.release'
}
```

Run TitanLauncher once so it syncs TitanClient into
`%USERPROFILE%\.titanclient\repository`. The Gradle dev plugin auto-detects
that synced release. Use `titanClientRoot` or the `titanDev` block only when
you want to target a custom install or local source build:

```gradle
titanDev {
    clientRoot = '%USERPROFILE%/.titanclient/repository'
    sessionSlug = 'my-plugin'   // defaults to the project name
    javaDebugPort = 5005        // used by runViaTitanDebug
}
```

Loop:

```powershell
.\gradlew.bat runViaTitan        # launch/recycle the DEV tab
.\gradlew.bat build              # rebuild; post-build hook re-stages the next gen
# click the refresh icon next to the plugin in the controller side panel
```

`runViaTitanDebug` is identical but enables a JDWP agent (default port 5005).
Reload works at the login screen as well as in-game, and an empty/failed build
that produces no JAR keeps the currently loaded plugins instead of unloading
them. This is the recommended path for plugin authors; the
`runTitanClient`/`publishToMavenLocal` flow below is for SDK contributors
editing the API itself.

## Package Layout

- `net.titan.api`: game-facing services, core types, and catalogs such as
  `Client`, `Player`, `Logger`, `Prayer`, `Varbits`,
  `ChatMessageType`, `ScriptId`, and `QuestId`.
- `net.titan.gamevals`: generated game constants, including `InterfaceID`
  with packed widget IDs and `pack`, `group`, and `child` helpers.
- `net.titan.api.plugins`: `Plugin` and `PluginDescriptor`.
- `net.titan.api.config`: config interfaces, annotations, sections, and
  setting metadata.
- `net.titan.api.events`: event classes such as `GameTick`.
- `net.titan.api.eventbus`: `EventBus` and `Subscribe`.
- `net.titan.api.queries`: fluent NPC, player, object, item, projectile,
  graphics-object, inventory, and widget queries.
- `net.titan.api.utils`: composed helpers including `Bank`, `Inventory`,
  `Equipment`, `Combat`, `Walk`, `Dialogue`, `Login`, `Plugins`, `Script`,
  `Cache`, and `Magic`.
- `net.titan.api.overlay`: in-game overlay draw and panel helpers, including
  WorldView-aware projection/draw methods.

## Native settings controls

Titan chooses the Plugin List editor from the return type of a `@ConfigItem`.
For example, this declaration adds a width and height editor. The getter
returns an owned copy for the plugin to use.

```java
@ConfigItem(keyName = "panelSize", name = "Panel size", description = "Size in pixels")
default java.awt.Dimension panelSize() {
    return new java.awt.Dimension(180, 64);
}
```

`Dimension` can also return `null`, like the optional `Point` control. The editor
shows **Default size** with an explicit **Set size** action; opening it does not
change the setting. `ConfigManager.setDimension(..., null)` persists a null size
as the string value `"null"`; it does not remove the config key. The plugin receives
Java `null` and decides its default sizing. Reset restores the declared getter's
default, whether null or a concrete size. Concrete dimensions remain nonnegative
and are copied on read and write.

Use `long` for exact signed 64-bit values, `double` for finite numbers,
`Set<MyEnum>` for multi-select, `java.awt.Point` for an optional position,
and `java.awt.Font` for family, size and style. The SDK also provides
`net.titan.api.config.Keybind` and `ModifierlessKeybind` for key capture.
For an `int`, `numberInput = true` selects a number field. Combining
`color = true` and `alpha = true` selects an RGBA picker.

Numeric getters can add a display-only suffix with `units = "ms"`, `"tiles"`
or `"%"`. This works for integer sliders/number fields, `long` and `double`;
it does not change the stored number, range, reset value or programmatic writes.
Units are optional literal text, at most 256 UTF-8 bytes, with control characters,
line separators and bidi formatting rejected. They are invalid on other controls.

```java
@ConfigItem(keyName = "delay", name = "Delay", description = "Wait between checks",
    numberInput = true, min = 0, max = 5000, units = "ms")
default int delay() { return 250; }
```

Directly constructed `IntSetting` and `ValueSetting<Long/Double>` holders also
accept a trailing `String units` constructor argument. Existing constructors
retain empty units, and hosts predating this metadata retain the numeric value.

## Checkbox Matrix Config Items (0.1.59+)

A `boolean[][]`-returning `@ConfigItem` with `columns()` and `rows()` renders as
a checkbox grid instead of a run of separate booleans. Each `@MatrixRow` names
the columns it has, so a column a row omits renders as a blank gap and can never
be checked; `checked` seeds the initial state.

```java
@ConfigItem(keyName = "obstacles", name = "Obstacles", description = "...",
    columns = {"Bridge", "Grapple", "Brazier", "Portal"},
    rows = {
        @MatrixRow(label = "Floor 1", cells = {"Bridge", "Grapple"}),
        @MatrixRow(label = "Floor 2", cells = {"Grapple", "Brazier"}),
        @MatrixRow(label = "Floor 3", cells = {"Bridge", "Portal"},
                   checked = {"Portal"}),
    })
boolean[][] obstacles();
```

Unlike every other config item the method is **abstract**: its cells and their
initial state come from the annotation, so there is no body to read a default
from. Every name in `cells` / `checked` is resolved against `columns` at scan
time, so a misspelling fails the load rather than moving a checkbox one cell
over. The value is a cell bitmask (`bit = row * columns().length + column`),
capped at 31 cells. `config.obstacles()` returns a fresh row-major snapshot on
every call -- hoist it out of per-tick loops.

## Item Sub-operations (0.1.32+)

`ItemDefinition.subOps()` exposes raw cache opcode-43 submenu labels as a
deeply unmodifiable five-by-twenty list. Empty strings preserve the original
parent/submenu slot indexes.

Java 0.1.33 adds the same deeply unmodifiable shape to
`ItemComposition.subOps()`. Runtime-resolved compositions use the live ItemDef
vectors; cache fallback uses the raw opcode-43 matrix. Existing
`InventoryItem.interact(action)` calls transparently resolve ordinary actions
first and then these live submenu labels.

## Coordinated Breaks (0.1.24+)

`net.titan.api.BreakHandler` mirrors the native and JavaScript Break Handler
utility. Every call takes the exact `Plugin` object loaded by TitanClient.
Helper classes may retain that owner and forward it; the embedded runtime
validates object identity and the host stores only stable plugin id/load
generation.

```java
import com.google.inject.Inject;
import net.titan.api.BreakCommand;
import net.titan.api.BreakHandler;
import net.titan.api.Client;
import net.titan.api.eventbus.Subscribe;
import net.titan.api.events.ClientTick;
import net.titan.api.plugins.Plugin;

public final class MyPlugin implements Plugin {
    @Inject
    private Client client;

    @Override
    public void onEnable() {
        BreakHandler.register(this); // true: configurable schedule owner
        BreakHandler.start(this);
        BreakHandler.poll(this);
        BreakHandler.running(this);
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        BreakCommand command = BreakHandler.poll(this);
        if (command.shouldBreak()) {
            stopIssuingNewWork();
            if (hasReachedSafeBoundary()) {
                BreakHandler.paused(this);
            }
        } else if (command.shouldResume() && clientIsWorldReady()) {
            resumeWork();
            BreakHandler.running(this);
        }
    }

    @Override
    public void onDisable() {
        BreakHandler.stop(this);
        BreakHandler.unregister(this);
    }

    private boolean clientIsWorldReady() {
        return client.loggedIn()
            && client.localPlayer().isPresent()
            && client.currentWorldViewPtr().isPresent()
            && client.sceneSizeX() > 0
            && client.sceneSizeY() > 0;
    }

    private void stopIssuingNewWork() {
        // Cancel/quiet this plugin's workers without starting another action.
    }

    private boolean hasReachedSafeBoundary() {
        // Replace with the plugin-specific worker/player-idle checks.
        return false;
    }

    private void resumeWork() {
        // Restart this plugin's workers after the world-ready check.
    }
}
```

Pass `false` to `register(plugin, false)` for a participant-only plugin:
it joins global pause/resume quorums but does not own a schedule. `stop` keeps
the registration visible; `unregister` implies stop and removes it.
`poll`, `shouldBreak`, `isBreakActive`, and `shouldResume` record the epoch
used by subsequent `paused`, `defer`, `error`, and `running` reports. Stale
reports fail closed. Cleanup is idempotent and the host also unregisters on
disable, fault, reload, or unload.

## Build And Publish

```powershell
.\gradlew.bat clean build
.\gradlew.bat publishToMavenLocal
```

Set `TITAN_MAVEN_REPOSITORY_URL`, `TITAN_MAVEN_USERNAME`, and
`TITAN_MAVEN_PASSWORD` to enable the configured remote Maven publication.
The canonical outbound sync publishes releases into
`Soxs/titan-public-sdk/maven/releases` automatically.

## Test Modified SDK Sources In TitanClient

Run TitanLauncher once so it syncs a release into
`%USERPROFILE%\.titanclient\repository`, then run:

```powershell
.\gradlew.bat runTitanClient
```

The auto-detected TitanClient folder must include the embedded Java runtime at:

```text
java/titan-java-embedded.jar
```

You can still edit the included `gradle.properties` file and set
`titanClientRoot` to the folder that directly contains `controller.exe` when
testing a custom/source build:

```properties
titanClientRoot=%USERPROFILE%/.titanclient/repository
```

`runTitanClient` will also try the launcher repository, common TitanClient
install folders, and local source-build output folders if `titanClientRoot` is
left blank.

The task builds this public API JAR, stages it under
`%USERPROFILE%\.titanclient\java-dev`, and launches:

```text
controller.exe --dev-mode --launch-new-client
```

TitanClient starts its embedded JVM with the staged API JAR first on the Java
classpath. This lets SDK contributors test API-compatible changes without
access to the private runtime source.

You can also provide the installed location as a one-off Gradle property:

```powershell
.\gradlew.bat runTitanClient "-PtitanClientRoot=%USERPROFILE%/.titanclient/repository"
```

Or set it for the current PowerShell session:

```powershell
$env:TITAN_CLIENT_ROOT = "%USERPROFILE%/.titanclient/repository"
.\gradlew.bat runTitanClient
```

For a local TitanClient source build, build `controller` first and use the
build output folder. Make sure Java runtime staging is enabled:

```powershell
cmake -S . -B build -DTITAN_BUILD_JAVA_RUNTIME=ON
cmake --build build --config Release --target controller
.\gradlew.bat runTitanClient "-PtitanClientRoot=C:\path\to\SoxClientOSRS\build\controller\Release"
```

Changes that require new embedded runtime behavior or native bridge methods
still need a TitanClient maintainer to update and distribute the runtime.

## Host fonts and skill icons

`Fonts.data(Fonts.Face.REGULAR)` returns an owned copy of the running host's
TrueType bytes. `Fonts.font(face)` returns an optional size-16 AWT `Font`, using
the actual regular, bold or small face; retain it or use `deriveFont` for other
sizes. It does not install fonts globally or substitute a system font when the
host asset is unavailable.

`SkillIcons.get(Skill.SAILING, false)` returns an optional immutable
`SpriteImage`. The `small` flag selects the original small artwork. The integer
overload uses the SDK's skill IDs, including Sailing at 23; invalid IDs and
unavailable hosts return an empty optional. Pixel arrays and font bytes are
owned copies. These local resource reads work on workers without game-state,
game-thread or RuneLite compatibility initialization.

The public SDK contains the API and no third-party font or icon files. Assets
are supplied by the installed Titan host, whose resource notices apply. Older
hosts return empty results. A derived font or image already returned to a plugin
remains a Java value after runtime shutdown; a new query requires a live backend.
