# Titan Plugin SDK -- changelog

Mirrors the changelog block at the top of
[`shared/titan/detail/abi.h`](detail/abi.h). When a new entry is added to
that block, add the same line here before the SDK distribution tarball is
rebuilt.

Convention: one bullet per change, prefixed with:

- `+` for additions (new HostApi / PluginApi / struct / symbol)
- `~` for non-breaking changes (renamed macros with deprecation, doc tweaks)
- `-` for removals (only on a major version bump)

Each bullet should be terse. Deeper context lives in the commit history or
in `PUBLIC_API.md`.

---

## v146 -- Widget model type and id; the SDK floor gates native loads again

- `+` Widget handles and snapshots expose the client's model source kind and
  the id it selects: C++ `Widget::modelType()`/`modelId()` and
  `WidgetSnapshot::modelType`/`modelId`, JS/TS `WidgetState.modelType`/
  `modelId` (included in `snapshot()`), and Java `Widget.modelType()`/
  `modelId()` in 0.1.75. Kinds match RuneLite's `WidgetModelType`
  (0 none, 1 model, 2 NPC chathead, 3 local player chathead, 4 item,
  5 player, 6 NPC chathead by index). `-1` means unavailable; `modelId` is
  also `-1` for a widget without a model.
- `+` `HostCoreV1::sdkRelease` publishes the host's source SDK release.
- `~` `titan/utils` headers include only the generated `interface_id.h`, not
  all of `gamevals.h` (about 25 MB), so files using them compile up to 4x
  faster. Include `<titan/gamevals.h>` or the specific generated header for
  other gameval constants.
- `-` `WidgetState` grows, so `kMinSupportedSdkVersion` is raised to 146 and
  enforced again. The host refuses native DLLs built against an older SDK;
  plugins built against 146 refuse an older host. Rebuild every native DLL
  and publish it together with the client and controller.

## v145 -- Packaged HTML side panels and HTML HUD overlays

- `+` Optional `PluginHtmlPanelsV1`, `PluginHtmlOverlaysV1`, and `HostHtmlUiV1`
  capabilities. Existing Native ABI v1 DLLs do not need rebuilding; frozen
  UI records, table offsets, and array strides remain unchanged.
- `+` C++ `HtmlPanelBundle`, `HtmlSidePanel`, and `HtmlOverlayPanel`; Java
  repeatable HTML annotations and facades; JS/TS discriminated panel definitions.
- `+` Validated embedded resources, bounded typed messages, replayable state,
  constructor metadata, and runtime overlay size/visibility controls.
- `~` Native and HTML side panels share eight slots. HTML overlays have eight
  definitions per plugin; existing native overlay limits are unchanged.

See [the HTML UI contract](../docs/html_ui.md) for packaging, bridge,
security, threading, and runtime distribution requirements.

## v144 -- Break Handler observation: see a break without registering

- `+` `HostApi::breakHandlerObserve`: copies the break command the
  coordinator has currently published -- the host's global command, not a
  participant's copy -- for any loaded, enabled plugin. Nothing is
  registered or recorded, so it never joins a pause quorum or moves a report
  epoch. Phase `BREAK_PHASE_NONE` means no break is in progress.
- `+` `titan::BreakHandler::observe(plugin)` / `isBreakInProgress(plugin)`
  and `BreakCommand::isBreakInProgress()` (any phase but none).
- `+` JS/TS: `titan.breakHandler.observe` / `isBreakInProgress`.
- `+` Java: `BreakHandler.observe` / `isBreakInProgress` and
  `BreakCommand.isBreakInProgress()`. Java SDK 0.1.69.
- `~` Appended after `previewPillWrite`, so plugins built against 135..143
  keep loading. A plugin built against 144 needs a 144+ client: an older
  host refuses to load it, and JS/Java callers on an older client find no
  `observe`. `kMinSupportedSdkVersion` remains 135.

## v143 -- Section parents: settings sections nested inside other sections

- `+` `titan::SectionOptions::parent` (`const Section*`) and
  `Section::parent()`. The controller draws a nested section as a
  collapsing header inside its parent's, after the parent's own settings,
  ordered by `position` among its siblings. A parent holding only
  sub-sections still shows; collapsing it folds them away.
- `+` `PluginProtocol::Section::parentIndex`: the parent's 1-based position
  in the same `getSections()` array, 0 at the top level, with
  `PluginProtocol::sectionParentIndex()`, `checkedSectionParent()` and
  `sanitizeSectionParents()`. A parent outside the list, the section itself,
  or a loop back to it leaves the section at the top level. On the wire a
  parent travels by key in both the fixed and the records codecs.
- `+` JS/TS: `SectionOptions.parent` (the `Section` another `section()` call
  returned) and `Section.parentKey`.
- `+` Java: `@ConfigSection(parent = "<keyName>")` and
  `Section.parentKey()`. The scanner and the native descriptor parser both
  reject an unknown parent, a section that is its own parent, and parents
  that loop. Java SDK 0.1.68.
- `~` JS/TS: `Section.isClosedByDefault`, declared since the section API
  landed, is now also set at runtime.
- `~` `parentIndex` is carved from the struct's padding, so
  `sizeof(Section)` and every other offset are unchanged and a pre-143
  plugin reads as flat. `kMinSupportedSdkVersion` stays 135.

## v142 -- Preview pills: short labels over a tab's Home-grid thumbnail

- `+` `HostApi::previewPillWrite` with the `PreviewPillWrite` POD, the
  `PREVIEW_PILL_CLEAR` / `PREVIEW_PILL_COUNTDOWN` flags and the
  `kPreviewPill*` limits. Each plugin owns its own pills, which the host
  stamps from the calling instance; the `pluginId` argument must agree with
  it. Appended tail only; `kMinSupportedSdkVersion` stays 135.
- `+` `<titan/preview_pills.h>`: `titan::previewPills(plugin)` with `set`,
  `clear` and `clearAll`, and `titan::PreviewPill` (`text`, `tone`,
  `countdown`).
- `+` JS/TS: the frozen `titan.previewPills` facade (`set` / `clear` /
  `clearAll`), taking the exact Plugin object passed to `titan.register`,
  and the `PreviewPill` type (`text`, `tone`, `countdownMs`).
- `+` Java: `net.titan.api.pills.PreviewPills.of(plugin)` with `set`,
  `clear` and `clearAll`, and the immutable `PreviewPill`
  (`of`, `countdown`, `withTone`, `withCountdown`). Java SDK 0.1.67.
- `~` A pill is text, a `PanelTone` and an optional `HH:MM:SS` countdown the
  controller ticks itself, so a running timer is set once and never
  republished. It stops at 00:00:00 until the plugin clears or replaces it.
- `~` At most 2 pills per plugin and 8 per tab; a new pill past either limit
  is refused. Keys are 1-32 characters of `[A-Za-z0-9._:/-]`; text becomes
  one line of UTF-8, cut to 63 bytes. Pills keep their first-set order.
- `~` Only an enabled plugin may set a pill; disabling, unloading or
  reloading it drops every pill it set. The client-internal backend has no
  plugin identity, so its entry fails closed.
- `~` Transport: the pill table rides the existing preview shared-memory
  section (`PreviewProtocol` v5) behind its own sequence lock, so pills cost
  no IPC round trips. A client/controller preview-version skew shows no
  preview, as before.

## v141 -- Cross-Tab Store: values shared by every tab one controller launched

- `+` `HostApi::crossTabWrite` / `crossTabRead` and
  `PluginApi::onCrossTabChanged`, with the `CrossTabWrite` /
  `CrossTabEntryInfo` / `CrossTabChangeEvent` PODs and the `CROSS_TAB_*`
  flags. Each plugin owns one namespace, which the host stamps from the
  calling instance; the `pluginId` argument must agree with it. Values live
  in controller memory for one controller sign-in session and are never
  written to disk. Appended tails only; `kMinSupportedSdkVersion` stays 135.
- `+` `<titan/cross_tab.h>`: `titan::crossTab(plugin)` with `put`, `erase`,
  `putIf`, `eraseIf`, `get` (refuses secret keys), `getSecret` (into the
  zeroing, move-only `titan::SecureBuffer`) and `info`; plus
  `titan::Plugin::onCrossTabChanged(const titan::CrossTabChange&)`.
- `+` JS/TS: the frozen `titan.crossTab` facade with the same operations,
  each taking the exact Plugin object passed to `titan.register`; `bigint`
  versions and write ids; `get` refuses secrets and `getSecret` returns an
  `ArrayBuffer` whose memory is zeroed when the engine frees it;
  `Plugin.onCrossTabChanged(change)` and the `titan.CrossTabChangeKind` /
  `CrossTabChangeOrigin` / `CrossTabChangeCause` enums.
- `+` Java: `net.titan.api.crosstab.CrossTab.of(plugin)` with `put`,
  `putString`, `erase`, `putIf` / `eraseIf` (`OptionalLong` write id),
  `get`, `getSecret` (a `byte[]` to zero) and `info`, plus the
  `CrossTabChanged` event, posted whether or not the plugin is enabled.
  Java SDK 0.1.66.
- `~` `put` / `erase` apply locally at once and are resent until the
  controller acknowledges them; last writer wins, and the outbox does not
  coalesce. `putIf` / `eraseIf` are proposals against an expected version
  (0 = absent) and report `Outcome` or `Rejected`. `Rejected` carries the
  key's confirmed version: re-read the key. An erase reaches every tab,
  including tabs that never held the key.
- `~` `CROSS_TAB_SECRET` changes handling, not access: secrets are never
  logged or persisted, travel only to tabs this controller launched, reach
  other tabs redacted, and are refused from them. Other plugins in the same
  tab are not kept out.
- `~` Change events run on the game thread from the MainLoop drain for
  every loaded plugin, enabled or not; an instance gets one `Replay` per key
  when it binds. Handlers must stay cheap. The client-internal backend has
  no plugin identity, so its two entries fail closed.

## v140 -- Plugin-authored setting changes persist like UI edits

- `+` `HostApi::markSettingChanged(pluginId, settingKey, value, hidden)`.
  A plugin reports that it changed one of its OWN settings. A non-null
  value is written into `plugin_settings.json` exactly as a side-panel
  edit is, so it survives a restart instead of being replayed over at the
  next tab init; a non-null `hidden` is presentation-only. Either may be
  null. Appended tail entry; `kMinSupportedSdkVersion` stays 135.
- `~` Both travel WITH the report instead of being read back from the next
  snapshot. `JavaPluginAdapter` and `ScriptPluginAdapter` both serve
  `buildSettings` from a cache -- Java's is only ever written by a
  controller-driven `setSetting`, and its hidden flag is frozen at load --
  so reading later would hand the controller a stale value with a fresh
  signal, and a Java `setHidden` would never reach the panel at all.
- `~` Reporting takes no host lock beyond its own and never calls
  `markPluginStateDirty`, so a plugin may write a setting while holding its
  own lock, or from a worker thread, without risking a lock cycle against
  the dispatch path. The renderer-state cache that call refreshes is
  derived purely from which plugins are enabled.
- `~` JS/TS writes are coerced to the setting's DECLARED control type
  before being reported, so assigning `1` to a checkbox or `"5"` to a
  slider cannot persist a wrongly-typed value.
- `~` Every typed C++ setter reports for you -- `set()`, `reset()`,
  `ColorSetting::set`, `MatrixSetting::set`/`setCell`/`toggleCell` -- and
  `setHidden()` reports a presentation-only change that repaints without
  saving. Plugin source is unchanged. Writing the value a setting already
  holds is a no-op, so a per-tick re-assert costs nothing.
- `~` Host-driven writes arrive through `apply()` and deliberately do NOT
  report, so an `IntSetting` clamp, a matrix availability mask, or a
  `ProtectedStringSetting` whose DPAPI unwrap failed can never be mistaken
  for plugin intent and written over the user's saved value.
- `-` `titan::IntSetting::set` now clamps to `[min, max]`, which only
  `apply()` did before. A plugin write that lands out of range would
  otherwise be persisted and replayed on every launch.
- `+` `titan::MatrixSetting::toggleCell(row, column)` returning the cell's
  new state, and `::grid()` returning a row-major copy -- matching JS
  `matrixSetting.toggle`/`toGrid` and Java `MatrixSetting.toggle`/`toGrid`.
- `+` Java: `net.titan.api.config.ConfigManager`, injectable, with `set`
  (boolean / int), `setString`, `setEnum`, `setGrid`, `setCell`,
  `toggleCell`, `reset`, `setHidden` and `isHidden`. The injected `Config`
  proxy stays read-only; this is the write side, and it closes the gap
  where a Java plugin could not change its own config at all.
  `ConfigSetting.setHidden` makes Java visibility runtime-mutable like
  C++ and JS/TS. Java SDK 0.1.65.
- `~` JS/TS: `setting.value = x` and `setting.reset()` now persist;
  `setting.isHidden` stays presentation-only. No API change.

## Unreleased -- Structural bank PIN detection

- `~` `Bank::isPinVisible` keys off visible bank PIN keypad children (interface 213) instead of scanning for the prompt text, which missed pads whose instruction text sits in the secondary text slot, differs in case, or carries colour tags. `Bank::pinRequestedDigitIndex` sweeps every visible widget in the pad group rather than reading `FRAME` alone. C++ and Java; behaviour only, no ABI or SDK bump.

## Unreleased -- Dynamic plugin settings counts

- `~` Remove the 40-setting limit across native, JS/TS and Java configs, controller catalogs, and live/legacy snapshots. Native collection grows host-owned buffers through the existing `getSettings` callback; the setting ABI and minimum supported SDK are unchanged, so existing SDK plugins do not need rebuilding. Individual control metadata and IPC payload byte limits remain in place.

## Unreleased -- GE input and diagnostics

- `~` Pause actions while submitted offer snapshots settle; fail only after an identity/state mismatch persists for three seconds. The inspector retains each request's last observed offer and automatically reveals failed-request details.
- `~` Match search results by item ID and dispatch Select on the row background, which owns the native handler. Keep widget clicks inside chatbox result bounds; include search children and the selected target in GE diagnostics.
- `~` Search and quantity entry use bounded edits of the observed input; remove the 80-backspace burst that could clear the field but lose the replacement text. Matching searches wait for results without retyping.
- `+` Dev Tools Grand Exchange Inspector with queue/options, UI and input state, native offers, action history, cancellation/release and diagnostic export. Disabled auto-open is explicit in request status and examples show how to enable it.

## Unreleased -- GE queue without plugin ownership

- `~` GE buying is available directly from the JS shell and all SDKs without plugin context. Queue queries and clearing operate on the shared queue; plugin disable/unload no longer cancels purchases. Logout/account-change protection remains.
- `~` Native SDK 139 ABI signatures remain compatible; legacy owner arguments are ignored. Java SDK 0.1.64 adds ownerless Client methods with deprecated forwarding overloads.

## v139 -- Shared queued GE buying

- `+` C++ host-owned buying queue with native, JS/TS and Java bindings; guide-price retries, exact partial-fill accounting, item/note/bank collection, optional web walking, price ceiling, cancellation and owned request outcomes.
- `+` Append-only SDK 139 request callbacks; minimum SDK remains 135. Java SDK 0.1.63. Selling remains an explicit unsupported stub.
- `~` Logout/character changes and plugin disable stop automation; existing offers remain for manual management. Requests and cache updates are driven in C++.

## v138 -- Shared remembered bank items

- `+` Per-character ItemCache with asynchronous JSON persistence, owned snapshots, known/live/loading metadata, and item-ID and name/exclusion counts across C++, JS/TS and Java.
- `+` Dev Tools item-cache inspector with item IDs, names, quantities, freshness, filtering, and clipboard export.
- `~` Bank read helpers prefer a readable open bank, then the C++ ItemCache, then empty; valid empty live banks override remembered stock. Java/JS expose bindings to the C++ cache. Bank actions resolve live slots. Bank snapshots preserve zero-quantity placeholders and reject partial reads.
- `+` Append-only `HostApi::getItemCacheBank`, guarded at SDK 138; minimum SDK remains 135. Java SDK 0.1.62.

## v137 -- Shared asynchronous item prices

- `+` `state::itemPrices()` / `state.itemPrices` / Java `Titan.itemPrices()` expose one client-wide OSRS Wiki catalogue and price cache with owned metadata, exact 64-bit optional amounts, request status, timestamps and catalogue revisions.
- `+` Append-only HostApi catalogue/price requests and snapshot reads, version guarded at SDK 137; minimum native SDK remains 135. Java SDK 0.1.61.
- `~` Lazy worker, fair coalesced FIFO requests, bounded queue/cache/JSON, separate catalogue and price TTLs, stale-data retention and retry backoff. Plugin disable does not cancel shared work; explicit client cleanup joins it before DLL unload.

## v136 -- Grand Exchange offers and events

- `+` Owned personal offer snapshots, all seven RuneLite offer states plus `Unknown`, zero-based slot access, and availability queries across C++, JS/TS, and Java.
- `+` `onGrandExchangeOfferChanged` / Java `GrandExchangeOfferChanged` events with retained offer snapshots, initial empty-slot notifications, and each native offer replacement preserved in order.
- `+` `HostApi::getGrandExchangeOffer`, `getGrandExchangeOffers`, `isGrandExchangeAvailable`, and `PluginApi::onGrandExchangeOfferChanged`. Appended fields are version guarded; minimum native SDK remains 135. Java SDK 0.1.60.
- `~` Price and completed gold use C++ `int64_t`, JS/TS `bigint`, and Java `long`, preserving newer clients' 64-bit amounts.
- `~` Full offer collections wait for captured and lifecycle-deferred offer callbacks to settle before becoming available. Lifecycle and GE events drain before client-tick initialization, including the MainLoopHook fallback, so initial EMPTY notifications cannot overtake a hydrated tracker. Owned events and individual slot reads remain available during delivery; no ABI layout change.

## v135 -- Checkbox matrix config control (ABI BREAK -- rebuild every plugin)

- `+` `titan::MatrixSetting` / `titan::MatrixRow`: a grid of checkboxes with row and column labels and per-cell availability, so one control replaces N repetitive per-row `BoolSetting`s and costs one of the 40 per-plugin settings instead of N. A row names the column labels it has, so the grid's shape is declared by name and a misspelled column is reported rather than silently shifting a cell.
- `+` `PluginProtocol::ControlType::checkboxMatrix`. The value is an integer cell bitmask (`bit = row * columns + column`, bit 31 never set, at most `PluginProtocol::kMaxMatrixCells` = 31 cells).
- `-` `PluginProtocol::Setting` gains `matrixAvailable` / `matrixRows` / `matrixColumns`, and `kMaxSettingOptions` grows 30 -> 32. Plugins write into a host-allocated `Setting[]` whose stride is `sizeof(Setting)`, so growing the struct is an ABI break: **minimum SDK moves 116 -> 135 and every native plugin must be rebuilt.** Java and JS/TS plugins are unaffected -- they never compile against the C ABI. The client refuses a stale plugin with "Rebuild the plugin against the matching SDK checkout" rather than misreading it.
- `~` `kProtocolVersion` 31 -> 32: the snapshot payload schema changed. Same class as the bump to 18 (color setting control metadata).
- `~` Mirrored across the TypeScript SDK (`matrixSetting`), the QuickJS runtime, and Java (`@ConfigItem columns/rows` plus `@MatrixRow` on an **abstract** `boolean[][]` config method -- the only config item with no method body, since its cells and defaults come from the annotation). Java SDK 0.1.59.

## v134 -- Bank search detection and Withdraw-X op index

- `~` `Bank::isSearchOpen()` tests `Chatbox::MES_TEXT2` visibility instead of scanning widget text for `"Enter amount:"` / `"Enter name:"`. The text scan matched hidden widgets still holding the previous prompt's text, so the predicate latched true once the first prompt closed, and it walked the whole widget table on every call. Matches the Java reference (`BankUtils.isSearchOpen`).
- `~` `Bank::withdrawItemAmount()` dispatches Withdraw-X as `CC_OP` op 6, as observed live and as the Java reference sends it. It sent `CC_OP_LOW_PRIORITY` op 6, and for `BANK_QUANTITY_TYPE == 3` `CC_OP` op 1 -- the selected quantity button (Withdraw-lastX), the wrong amount and no prompt.
- `~` C++, JS/TS and Java surfaces; Java SDK 0.1.58. No HostApi/PluginApi or request layout change; minimum SDK remains 116.

## v133 -- Bank deposit box helpers

- `+` `titan::utils::DepositBox` (`<titan/utils/deposit_box.h>`), `titan.utils.depositBox` (JS/TS) and `net.titan.api.utils.DepositBox` (Java): `isOpen`, `isDepositAllSelected`, `close`, `depositInventory`, `depositWorn`, `depositLootingBag`, `selectDepositAll`. Composition over existing widget interact and varbit reads; Java SDK 0.1.56.
- `~` `close()` sends the frame's close X component operation (CC_OP op 1 on dynamic child 11 of `BankDepositbox::FRAME`). Legacy `WidgetClose` (26) is the native root-owned closure on callback clients and does not close this interface; the MLM and Test Gauntlet plugins now use the helper.
- `~` No HostApi/PluginApi or request layout change; minimum SDK remains 116.

## v132 -- Magic cast performs the spell's own Cast option

- `~` `utils::magic` `cast(spell)` now dispatches CC_OP against the catalog's `menuEntryId + 1` instead of aliasing `select()`; a non-targeted spell such as a teleport is a plain widget op, not a WidgetTarget selection.
- `~` `select(spell)` keeps the WidgetTarget source selection; callers that relied on `cast(spell)` selecting must call `select(spell)`.
- `~` C++, JS/TS and Java surfaces; Java SDK 0.1.54. No HostApi/PluginApi change; minimum SDK remains 116.

## v131 -- Full-frame game screenshot for plugins

- `+` `HostApi::screenshotSubmit/Poll/CopyPng/Release`: the `/tabs` capture (backbuffer after the AboveWidgets pass, PNG) as an in-process submit/poll/copy/release handle.
- `+` `ScreenshotStatusState` + `ScreenshotPhase`; `titan::screenshot()` in C++, `titan.screenshot` in JS/TS, `Titan.screenshot()` in Java; Java SDK 0.1.53.
- `~` Appended HostApi tail; `kMinSupportedSdkVersion` remains 116.

## v130 -- Native player interaction parity

- `+` `Item::useOn(Player)` in C++, JS/TS and Java composes source 25 -> target 14.
- `~` Callback player ordinary 44..51 and selected 14/15 use proved native builders.
- `~` Existing Magic player casts keep target 15; Java SDK 0.1.51.
- `~` No HostApi/PluginApi or request layout change; minimum SDK remains unchanged.

## v129 -- Native ground-item interactions

- `+` `Item::useOn(GroundItem)` across C++, JS/TS and Java queues source 25 -> target 16.
- `+` Java `InteractionBackend.useInventoryItemOnGroundItem` composes the pair; Java SDK 0.1.50.
- `~` Callback ground targets accept 16/17 (17 canonical), with the unique stack and quantity bound.
- `~` Cache definitions default Take to slot 3; explicit Hidden clears the corresponding slot.
- `~` Ground actions match complete labels case-insensitively in their definition slots;
  Examine uses 1004, and unknown actions or missing loaded definitions are rejected.
  Before cache loading only the native Take/Examine defaults resolve.
- `~` `kSdkVersion` 129; HostApi/entry layouts and `kMinSupportedSdkVersion` remain unchanged.

## v128 -- Explicit source selection and dependent target action

- `+` Nullable HostApi tail operation accepts one owned source/target pair.
- `+` Callback clients bind the exact selected source before the dependent target.
- `~` Existing entry layouts and `kMinSupportedSdkVersion` remain unchanged.

## v127 -- Actor overhead text

- `+` Complete nullable `Actor::getOverheadText()` and `getOverheadTextCyclesRemaining()` across C++, JS/TypeScript, and Java.
- `+` `OverheadTextChanged` reports every accepted player/NPC utterance, including identical and empty strings; expiry is excluded.
- `+` Appended ABI uses caller-owned read buffers and callback-scoped pointer/length text views; minimum SDK remains 116.

## v126 -- Native loading game state

- `+` `LoginGameStateAbi::LOADING` exposes native `Client.GameState` 25.
- `+` C++, JS/TypeScript, and Java `LoginGameState` enums expose the same
  value for polling and `GameStateChangedEvent` callbacks.
- `~` Mapping-only public API addition; `kMinSupportedSdkVersion` remains 116.

## v125 -- Native client game cycle

- `+` `HostApi::getGameCycle` exposes the analyzer-backed signed 32-bit
  `Client.GameCycle` clock used by graphics-object start cycles.
- `+` C++ `ClientFacade::gameCycle()`, JS/TS `state.client.gameCycle`, and
  Java `Client.gameCycle()` expose the same live value.
- `~` The game cycle advances nominally every 20 ms (50 Hz); 30 cycles make
  one 600 ms server tick. It is distinct from `ClientState::tickCount`.
- `~` Additive `HostApi` tail append; `kMinSupportedSdkVersion` remains 116.

## v124 -- Dynamic widget children beyond 256

- `~` `kMaxWidgetDynamicChildren` raised 256 -> 2048. It is now purely the
  per-call export/sizing bound for the SDK children wrappers; the native
  reader's bound became a separate corruption ceiling (64k), so every real
  dynamic child is addressable, interactable, and text-settable
  (`widgetInteractAtPath` / `setWidgetTextAtSlot` no longer reject
  slots >= 256).
- `~` `HostApi::getWidgetChildren` gained the `(nullptr, 0)` sizing-probe
  contract (returns the true child count); non-null calls still return the
  written count.
- `~` C++/JS/Java children wrappers switched to count-then-fill; their
  truncation flags now report honestly at the 2048 bound (the Java bridge
  previously hardcoded `truncated=false`).
- `~` Synthetic clicks on dynamic children are constrained to the parent
  widget's screen bounds: child-parent intersection when overlapping,
  random-in-parent when the child lies outside (e.g. a bank item scrolled
  out of view).
- `~` `kMinSupportedSdkVersion` remains 116 (no struct resized).

## v123 -- Outline convex/concave mode

- `~` `HostApi::drawEntityOutline` / `drawTileObjectOutline` gained a trailing
  `uint32_t mode` argument: 0 = convex hull (the prior behaviour), 1 = concave
  hull (a single non-self-intersecting polygon that hugs the projected
  vertices and follows concavities). This revises the SDK-122 outline
  signature, which is safe because SDK-122 outlines were unreleased (added in
  the same dev cycle); no shipped plugin depends on the 4-arg form.
- `+` `titan::OutlineMode` enum + `mode` parameter on the `entityOutline` /
  `tileObjectOutline` facades (default Convex).
- `~` `kMinSupportedSdkVersion` remains 116.

## v122 -- True model-vertex outlines

- `+` `HostApi::drawEntityOutline` / `drawTileObjectOutline` draw the 2D convex
  hull of an entity's actual projected model vertices -- a tight silhouette
  following the real mesh, versus `drawEntityHull` (which hulls the 8 AABB
  corners). Sourced from the model handle the host's model-AABB hook binds per
  typecode each frame plus the `Model` vertex arrays from the FindModelGeometry
  analyzer pass.
- `+` `titan::overlay()` facades `entityOutline` / `tileObjectOutline` (typed +
  raw-pointer overloads).
- `~` Additive `HostApi` tail append; existing fn-pointer offsets unchanged, so
  plugins built against SDK 116..121 keep loading. Facades no-op on hosts older
  than 122 and on revisions without `Model` geometry. `kMinSupportedSdkVersion`
  remains 116.

## v121 -- Typecode-only clickbox picking cache (behavioural)

- `~` The host's world-keyed picking-cache fallback was removed: the
  typecode-keyed cache is now the only picking evidence behind
  `drawEntityClickbox` / `drawTileObjectClickbox` / `drawEntityHull` /
  `drawTileObjectHull`. `typecode = 0` (or a cache miss) now degrades actors
  to the synthesized 1-tile footprint and draws nothing for tile objects,
  instead of falling back to world-keyed matching (which could return a
  same-tile neighbour's box, including actor/loc cross-matches). Click-point
  sampling for synthetic actions degrades the same way when the typecode
  cannot be resolved. `titan::overlay()` typed overloads compute typecodes
  automatically and are unaffected.
- `~` No ABI/struct/signature changes -- behavioural bump only.
  `kMinSupportedSdkVersion` remains 116.

## v120 -- Cheap live-state freshness epoch

- `+` `HostApi::getLiveStateEpoch` returns a monotonic epoch equal to
  `ClientState::tickCount`, published atomically once per frame. Entity
  wrappers read it (one atomic load) on each accessor instead of building a
  full `ClientState` just to compare `tickCount`.
- `~` Additive `HostApi` tail append. `kMinSupportedSdkVersion` remains 116;
  the SDK falls back to `getClientState` when the pointer is null (older host),
  so behaviour is unchanged -- purely a per-accessor cost reduction. No new
  plugin-facing `titan::` symbol.

## v119 -- Account Profiles credential staging + generic login submit

- `+` `HostApi::stageLoginCredentials` resolves an exact Account Profiles label
  and queues its standard or Jagex credentials without exposing secrets to the
  caller.
- `+` `HostApi::submitLoginCredentials` holds Enter across one MainLoop update on
  either supported credential screen, then releases it.
- `~` Additive `HostApi` tail append; `kMinSupportedSdkVersion` remains 116.

## v118 -- Main-loop event

- `+` `PluginApi::onMainLoop` / `titan::Plugin::onMainLoop()` fires once per
  outer `MAIN_LOOP` iteration before the MainLoop dispatcher drain and native
  loop body, including title/login screens and gameplay. Static definition
  caches are available in every state; live gameplay queries require
  `state::login().isWorldReady()`.
- `~` Additive `PluginApi` tail append. `kMinSupportedSdkVersion` remains 116,
  so SDK 116/117 native plugins continue to load with no MainLoop callback.

## v116 -- Drop-safe frame schedulers (breaking: rebuild required)

- `~` `HostApi::runOnClientTick` / `runOnRender` gained a third parameter,
  `void (*cleanup)(void* userData)`. The host invokes `cleanup` exactly once
  per request: after the callback ran, or when the queued request is dropped
  without running (queue-cap eviction, shutdown/reload `clearAll`).
  Callbacks must no longer free `userData` themselves. The
  `titan::runOnClientTick`/`titan::runOnRender` facades are source-compatible
  and now free their heap thunk on every path (previously it leaked whenever
  a queued task was dropped). In-place fn-pointer signature change;
  `kMinSupportedSdkVersion` raised to 116, so pre-116 plugin binaries are
  rejected at load and must rebuild.

## v115 -- Generic mouse-input events

- `+` `MouseButtonEvent` and `PluginApi::onMousePressed` /
  `PluginApi::onMouseReleased`: the client delivers real (non-synthetic)
  mouse button press/release events to plugins, carrying client-area x/y,
  the button (`MouseButton::LEFT/RIGHT/MIDDLE`), and the `KeyboardMods`
  bitmask sampled at click time. `consume()` on a press suppresses the
  native click and its paired release so the game's button state stays
  balanced; `consume()` on a release is handler-ordering only (never
  suppresses). Suppression covers the WndProc (V1) input pipeline; the
  game's low-level (V2) pipeline still observes real hardware input.
  Double-click messages arrive as an extra press. No events fire until the
  plugin host is fully initialized (`isReady()`), so the injection window
  is untouched.
- `~` These fire on the input (message-pump) thread, NOT the game thread:
  copy the fields out of the event and defer game-state reads or `titan::*`
  action calls to a game-thread callback such as `onClientTick`. Delivery
  uses a bounded lock acquire that falls back to pass-through under
  contention, so input latency is never traded for delivery.

## v114 -- Web walk executor + per-step action payload access

- `+` `WebWalkRequestState` / `WebWalkStatusState` / `WebWalkPhase` /
  `WebWalkFlag` and `HostApi::webWalkStart/Status/Cancel/Release`: the client
  follows a generated route in-game until arrival, failure, or cancel. One
  session per client; a new start supersedes the previous one.
- `+` `HostApi::webWalkAdvance` for `WebWalkFlag::ManualTick` sessions.
- `+` `HostApi::webPathCopyStepPayload`: the UTF-8 JSON action payload of one
  step of a completed internal web path request, for plugins building custom
  executors. Size-query convention via `outRequired`.

## v113 -- Native world-map display state

- `+` `HostApi::getWorldMapState` appends one fixed, versioned snapshot of the
  coherent physical and logical map viewports, interface transform, global
  display centre, current/target zoom, and renderer-validated logical
  pixels-per-tile scale.
- `+` `titan::state::worldMap()` / `titan.state.worldMap` / the Java client
  facade expose read-only snapshot, world-to-screen, screen-to-world, and
  pixel/tile conversion helpers using the currently interpolated zoom.
- `~` The capability is atomic and fail-closed: incomplete generated fields,
  missing scale proof, an unloaded/hidden map, malformed native state, older
  clients, and missing callbacks return unavailable with no guessed layout.
- `~` Runtime semantics have been empirically validated only on Windows OSRS
  revision 240.1. No older- or cross-revision runtime validation is claimed;
  unsupported bundles omit the capability cleanly.
- `~` Additive HostApi change; `kMinSupportedSdkVersion` remains 111.

## v112 -- Asynchronous web paths and collision snapshots

- `+` `HostApi` appends bulk cached-region and immutable live-scene collision
  snapshots, including the current instance-template table.
- `+` `titan::webWalker()` / `titan.webWalker` / `Titan.webWalker()` expose
  read-only asynchronous path submit, poll, step-copy, cancel, and release.
- `+` Fixed ABI request, summary, step, and DEV provider records keep C++
  objects out of DLL boundaries; the provider override is host-gated to the
  entitled `web_walker_provider` DEV session.
- `~` Route costs are deterministic integer points in C++, JavaScript, Java,
  fixed ABI, and provider records. Legacy authored cost `1` is `10` points and
  one normal walked tile is `5` points (the requested 0.5 weighting).
- `~` Native `currentScene(expectedWidth, expectedHeight)` can use matching
  `ClientSnapshot` dimensions to allocate and fill the live collision scene in
  one HostApi pass; `currentScene()` retains the general two-pass form.
- `~` `Tile`, `WorldPos`, and `WorldPoint` move to the lightweight public
  `<titan/world_point.h>` header; `<titan/actor.h>` remains source-compatible.
- `~` Additive HostApi change; `kMinSupportedSdkVersion` remains 111.

## v111 -- Expanded item containers

- `~` `ItemContainerState` and `ItemContainerChangedEvent` fixed slot arrays
  grow from 1,024 to 2,048 entries so current maximum-size banks are exposed in
  full to native, JavaScript/TypeScript, and Java plugins.
- `~` The internal native reader validates both parallel vector lengths against
  a separate corruption ceiling instead of treating the ABI export capacity as
  a native-container validity limit.
- `-` The fixed-buffer resize is an ABI break, so `kMinSupportedSdkVersion` is
  raised to 111 and native plugins must rebuild.

## v110 -- Widget sprite id SDK parity

- `+` Widget handles and snapshots expose the primary native widget sprite id
  as C++ `Widget::spriteId()` / `WidgetSnapshot::spriteId`, JavaScript and
  TypeScript `WidgetState.spriteId`, and Java `Widget.spriteId()` in 0.1.36.
- `~` Missing sprite data or an unavailable `SpriteId` offset returns `-1`;
  the alternate sprite field remains internal.
- `-` `WidgetState` grows, so `kMinSupportedSdkVersion` is raised to 110 and
  native plugins must rebuild.

## v109 -- Interface-scale accessor for UI-frame -> window geometry

- `+` `HostApi::getInterfaceScale` exposes the live OSRS in-game
  interface-scale factor (and canvas pillarbox origin) the host derives from
  the canvas coordinate transform. Lets plugins scale their own fixed-pixel
  geometry to match widget overlays at 150%/200% interface scaling.
- `+` Mirrored in the C++ `CameraFacade` (`interfaceScale()`,
  `interfaceScaleX()` / `interfaceScaleY()`), JavaScript/TypeScript
  (`titan.state.camera.interfaceScale()` / `interfaceScaleX` /
  `interfaceScaleY`), and Java.
- `~` Additive HostApi change; returns identity (1.0) when the analyzer did
  not detect the canvas transform. `kMinSupportedSdkVersion` remains 107.

## v108 -- WorldView-aware tile-object live refresh

- `+` `HostApi::getTileObjectsOnTileInWorldView` re-resolves tile-object
  handles against their captured WorldView instead of the current scene.
- `~` Per-tile tile-object results preserve the requested plane; C++, Java,
  and JavaScript live refresh reject plane or WorldView identity drift.
- `~` Additive HostApi change; `kMinSupportedSdkVersion` remains 107.

## v107 -- Live runtime ItemDef sub-operations

- `+` Runtime `ItemComposition` now exposes the fixed 5-by-20 opcode-43
  `subOps` matrix populated from the live `ITEM_DEF_LOOKUP` result. Cache
  fallback preserves the same positional shape and empty slots.
- `+` Mirrored in C++, JavaScript/TypeScript, and Java 0.1.33.
- `~` Existing inventory-item `interact(action)` calls now resolve ordinary
  actions first and then live opcode-43 sub-operations in every SDK runtime.
  This reuses the existing HostApi interaction functions and adds no ABI entry.
- `-` `ItemCompositionState` grows by the fixed matrix, so
  `kMinSupportedSdkVersion` is raised to 107 and native plugins must rebuild.

## v106 -- Versioned cache-definition snapshots and item sub-operations

- `-` `ItemDefSnapshot`, `NpcDefSnapshot`, `ObjDefSnapshot`, and
  `VarbitDefSnapshot` now begin with caller-initialized `structSize` and
  `apiVersion`. Getters reject legacy, undersized, or unknown-version records
  without writing to them. `kMinSupportedSdkVersion` is raised to 106, so
  native plugins must rebuild.
- `+` Raw item opcode-43 submenu labels are decoded and exposed as a fixed
  5-by-20 `subOps` matrix in C++, JavaScript/TypeScript, and Java 0.1.32.
  Empty strings preserve cache slot gaps.
- `~` HostApi ordering and all note ids, naming, post-processing, and public
  note semantics are unchanged.

## v105 -- Live cross-tick object/ground-item/widget handles (JS + C++ parity)

- `~` JS scene objects (`titan.state.objects.*`) and ground items
  (`titan.state.groundItems.*`) are now **live cross-tick handles** instead of
  frozen snapshots: field reads (`name`/`actions`/`worldPoint`/`animation`/
  `entityPtr`/`quantity`/...) re-resolve against the live tile once per tick.
  New `.exists` (false once the loc/stack despawns) and `.snapshot()` (freeze a
  capture-time copy). This brings JS in line with what the Java SDK already
  ships for these types. Repeated queries now return the same handle per slot
  (identity dedup, like actors); use `.snapshot()` if you need capture-time
  values.
- `~` C++ `titan::TileObject` / `titan::GroundItem` are now live, mirroring
  `Player`/`Npc`: accessors and `interact()` re-resolve through a per-tick
  `live()`, plus `exists()` and `snapshot()`. Header-only -- no ABI change.
- `~` JS widget live-sync fix: `titan.state.widgets.find(packedId)` handles now
  actually re-resolve across ticks (`visible`/`hidden`/`itemId`/`screenX`/...
  update; `.exists` flips when the widget unloads). Previously the sync was a
  no-op; Java widgets were already live.
- `~` Fail-soft native interact: a despawned object's `.interact()` is now a
  safe no-op (entityPtr re-resolved from the live tile at call time) instead of
  dispatching an action into freed scene memory. Benefits all runtimes.
- `+` JS bindings `titan.getTileObjectsOnTile(plane,x,y)` /
  `titan.getGroundItemsOnTile(plane,x,y)` back the live re-resolve (existing
  HostApi pointers; no HostApi table change). `kMinSupportedSdkVersion`
  unchanged -- every existing precompiled `.jsc`, native, and Java plugin keeps
  loading with no rebuild.

## v104 -- Panel builder polish: combo, disabled scope, help marker, badge

- `+` `titan::Panel::combo(label, actionId, items, selectedIndex)` exposes the
  existing combo element through the fluent SDK. Items ride the wire as a
  newline-separated list and the controller renders a full-width dropdown with
  the label above. Mirrored in JS/TS and Java.
- `+` `Panel::beginDisabled(disabled = true)` / `endDisabled()` wrap a run of
  controls in an ImGui disabled scope (greyed + non-interactive).
- `+` `Panel::help(text)` inline `(?)` tooltip marker and
  `Panel::badge(text, tone)` compact colored status pill.
- `~` Renderer fix: `sliderInt` / `sliderFloat` / `combo` draw the label above a
  full-width control so long labels no longer clip off-panel.
- `+` New `PanelElementType` values 113..116; additive, ignored by older
  controllers. `kMinSupportedSdkVersion` unchanged.

## v103 -- NPC overhead icon runtime-override fix

- `~` `Npc::overheadIcon()` now returns the effective slot-0 `HeadIcon`
  ordinal. Runtime per-instance overrides (`ClientNpc::SetHeadIcon`, e.g.
  bosses swapping protection prayers mid-fight) take precedence over the
  NpcType cache defaults, and both resolve to the sprite frame index (the
  `HeadIcon` ordinal) rather than the sprite-archive id. Previously override
  icons were undetectable and cache defaults returned the archive id.
  `hasHeadIconOverride()` is unchanged.
- `~` Behavioural fix only; no ABI/struct/table change.
  `kMinSupportedSdkVersion` unchanged.

---

## v102 -- Prayer action utility

- `+` New header-only `titan::utils::Prayers` helpers expose prayer active
  reads plus raw toggle and idempotent set/enable/disable actions for all
  55 standard and Ruinous Powers entries.
- `+` Mirrored in Java 0.1.30 and JavaScript/TypeScript.
- `~` Composition-only addition; `kMinSupportedSdkVersion` unchanged.

---

## v101 -- NPC combat/exclusion and widget text query filters

- `+` `NpcQuery` adds definition-backed `combatLevelAbove`,
  `combatLevelBelow`, and `combatLevelBetween` filters plus exact-identity
  `exclude(Npc)` / `exclude({...})` filters.
- `+` `WidgetQuery::textContains({...})` matches any supplied text.
  Mirrored in Java 0.1.29 and JavaScript/TypeScript.
- `~` Composition-only addition; `kMinSupportedSdkVersion` unchanged.

---

## v100 -- World-area and inventory query filters

- `+` `LocatableQuery::within(WorldArea)` keeps entities whose absolute world
  point is contained by the area, including plane and WorldView checks.
- `+` `InventoryQuery::hasAction(action)` filters on runtime inventory actions;
  `InventoryQuery::isNoted()` filters on the cache definition's noted flag.
  Mirrored in Java 0.1.28 and JavaScript/TypeScript.
- `~` Composition-only addition; `kMinSupportedSdkVersion` unchanged.

---

## v99 -- Multi-action NPC and tile-object query filters

- `+` `NpcQuery::hasAction({...})` and `ObjectQuery::hasAction({...})` keep
  entities exposing any supplied action. Java and JavaScript/TypeScript mirror
  the behavior with varargs (Java SDK 0.1.27).
- `~` Single-action `hasAction(action)` behavior remains source-compatible.
  Composition-only addition; `kMinSupportedSdkVersion` unchanged.

---

## v98 -- Config button setting

- `+` `titan::ButtonSetting`: a value-less action config item. Clicking it in
  the controller invokes the attached `std::function<void()> onClick` instead
  of storing a value. `onClick` runs on the game thread (`Phase::MainLoop`,
  including the login screen).
- `+` Mirrored across the TypeScript SDK (`buttonSetting`), the QuickJS
  runtime, and Java (`@ConfigButton` on a `default void` method; Java SDK
  0.1.25 adds `net.titan.api.config.ConfigButton` + `ButtonSetting`).
- `+` `PluginProtocol::ControlType::button` (additive enum value; no wire
  struct or `Command` change).
- `~` Additive only; `kMinSupportedSdkVersion` unchanged. Old controllers fall
  through the render default and simply don't show the button.

---

## v97 -- Break Handler, owned services, and profile activation

- `+` Versioned fixed-size POD registration, command, report, and participant
  records plus an append-only HostApi registry shared by native, Java, and
  JavaScript plugins. Native registration/report calls pass no allocated text
  or language-owned object across the DLL boundary.
- `+` Instance-based Break Handler utilities use `register` / `unregister`
  in Java and JavaScript; native C++ uses `registerPlugin` for registration
  because `register` is a C++ keyword, while retaining `unregister`. All
  runtimes share start/stop, polling, safe-pause reports, and coordinator
  semantics.
- `+` Java 0.1.24 adds `net.titan.api.BreakHandler`, `BreakCommand`,
  `BreakPhase`, and `BreakMode`; JavaScript/TypeScript adds
  `titan.breakHandler` with the same instance-based participant lifecycle.
- `+` `titan::Panel` gains `beginTabBar`/`endTabBar`/`beginTabItem`/
  `endTabItem` across native C++, Java, and JavaScript/TypeScript (the
  controller already rendered the underlying tab protocol elements).
- `+` `titan::registerService(owner, id, ptr)` publishes a service owned by an
  exact plugin instance/load generation. Owned entries survive ordinary UI
  disable and are removed automatically when that generation unloads.
- `~` In-tree service publishers use generation-owned registration. The
  legacy SDK 66-96 ownerless overload remains a shutdown-cleared compatibility
  path because those binaries cannot identify their publishing instance.
- `+` Native `<titan/account_profiles.h>` defines the load-lifetime,
  versioned POD `titan.account_profiles.v1` service: sanitized vault/profile
  snapshots, unlock-existing, and pollable Standard/Jagex activation.
- `+` Native `titan::state::proxy()` exposes sanitized route list/set/status,
  and `LoginFacade::submitLauncherCredentials()` queues Jagex launcher
  submission on MainLoop. The preserved API now holds Enter through a
  login-screen update and releases it on poll/cancellation; this is a host
  behavior change and does not change the SDK/ABI version.
- `~` Break Handler calls validate the exact runtime plugin instance on every
  operation. The host retains only stable identity and load generation; no
  native pointer, Java object, JavaScript value, callback, or secret is stored
  in the registry. Additive only; `kMinSupportedSdkVersion` is unchanged.

## v96 -- Panel layout containers + alignment

- `+` `titan::Panel`: `beginGroup`/`endGroup`, `beginChild`/`endChild`,
  `beginCard`/`endCard` (framed auto-height cards), `beginAlign`/`endAlign`
  (center/right-align a button run). Mirrored in TypeScript, QuickJS, and Java.
- `+` `PanelElementType`: `proxyCombo` (first-party, controller-rendered),
  `alignBegin`/`alignEnd`; `PanelChildFlag` flags.
- `~` Additive only (new wire element types + panel-builder helpers; no
  HostApi/PluginApi table change).

## v95 -- Unbounded plugin enumeration

- `+` `HostApi::getPluginCount` (appended at end of HostApi) lets the plugin manager facade size its enumeration buffer dynamically instead of a fixed 128 cap.
- `~` Additive only; `kSdkVersion` 95, `kMinSupportedSdkVersion` unchanged so plugins built against 91..94 keep loading without a rebuild.

## v94 -- Public mouse click-point resolver utility

- `+` `HostApi::resolveActionClickPoint` exposes target-aware click resolution without dispatching.
- `+` C++ / JS / TS / Java expose the public resolver at `utils.mouse` / `titan::utils::Mouse`.
- `-` The public resolver is no longer on Java/JS client state APIs.
- `~` `kSdkVersion` 94; `kMinSupportedSdkVersion` unchanged.

## v93 -- Target-aware synthetic click resolution

- `+` `SyntheticActionEntry` carries optional target metadata for clickbox resolution.
- `~` C++ / JS / TS / Java magic target casts feed target metadata into the native resolver.
- `~` `kSdkVersion` 93; `kMinSupportedSdkVersion` unchanged.

## v92 -- Player equipment composition

- `+` `HostApi::getPlayerComposition` exposes raw PlayerModel appearance equipment slots with item-id normalization metadata.
- `+` C++ / JS / TS / Java player wrappers expose `getPlayerComposition()`.
- `~` `kSdkVersion` 92; `kMinSupportedSdkVersion` unchanged.

## v91 -- Native game-state event

- `+` `LoginGameState` values now match native `Client.GameState` values.
- `+` `PluginApi::onGameStateChanged` and `GameStateChangedEvent` expose native `SetGameState` transitions.
- `-` `kSdkVersion` 91; `kMinSupportedSdkVersion` raised to 91 because the public game-state enum changed numeric semantics.

## v90 -- Projectile coordinate names

- `~` `ProjectileState` uses `startY` / `targetY` for horizontal map Y, `height` for vertical position, and `sceneY` for precise horizontal scene Y.
- `~` `GraphicsObjectState` uses `preciseY` / `sceneY` for horizontal map Y.
- `~` C++ / JS / TS / Java projectile and graphics object wrappers expose the corrected names.
- `~` JS / TS `titan.Skill` adds PascalCase aliases such as `Cooking`, and `titan.state.skills.*` now rejects invalid skill arguments instead of coercing them to Attack.
- `~` `kSdkVersion` 90; `kMinSupportedSdkVersion` unchanged.

## v89 -- Resolver-backed live handles

- `+` `HostApi` appends direct identity resolvers for players, NPCs, and retained widget paths so Java / JS / C++ live handles read through the same internal C++ object lookup path instead of list-scan snapshots.
- `+` `HostApi` appends a WorldView-aware actor path queue resolver; actor `pathQueue()` values now preserve the actor's WorldView identity.
- `~` `kSdkVersion` 89; `kMinSupportedSdkVersion` unchanged.

## v88 -- Live state handles

- `+` Java / JS / TS mutable client-backed state handles are interned by stable runtime identity and observe current state on access; Java handles expose `exists()`, `snapshot()`, and identity-based `equals` / `hashCode`.
- `+` C++ `Player`, `Npc`, `Actor`, and `Widget` wrappers are live handles and expose `exists()`, `snapshot()`, equality, and `std::hash` identity support where applicable.
- `~` Java typed bridge carries WorldView identity for actors, tile objects, and ground items.
- `~` `kSdkVersion` 88; `kMinSupportedSdkVersion` unchanged.

## v86 -- Melee distance helpers

- `+` C++ / JS / Java expose RuneLite-style orthogonal-only `isInMeleeDistance` helpers on `WorldPoint`, `WorldArea`, and locatable entity surfaces.
- `~` JS / TS expose instance checks on `state.client.isInInstance()` for parity with C++ / Java client facades.
- `~` `kSdkVersion` 86; `kMinSupportedSdkVersion` unchanged.

## v85 -- WorldView-aware entities (BREAKING)

- `+` `WorldPointState`, `LocalPointState`, actor, tile-object, ground-item, and graphics-object snapshots carry WorldView identity.
- `+` General player/NPC/tile-object/ground-item/graphics-object getters enumerate active WorldViews; locatable queries can filter current/top-level/specific WorldView ids.
- `+` Entity render helpers and interactions route through each entity's WorldView where supported.
- `-` `kSdkVersion` 85; `kMinSupportedSdkVersion` raised to 85 because entity struct layouts grew.

## v84 -- WorldView-addressed overlay projection

- `+` `ClientState` exposes current WorldView id plus top-level base, plane, and scene dimensions.
- `+` `HostApi` appends explicit WorldView-addressed W2S, tile-height, tile-region, and text overlay calls.
- `+` C++ / JS facades expose `WorldView::TOP_LEVEL` / `WorldView.TOP_LEVEL` and `InWorldView` overlay helpers.
- `~` `kSdkVersion` 84; `kMinSupportedSdkVersion` unchanged.

## v83 -- SLR-backed world metadata

- `+` `WorldMetadataState` plus `HostApi::getWorldMetadata` and `refreshWorldMetadata` expose Jagex SLR world id, host, activity, location, population, and measured ping cache.
- `+` C++ / JS / Java world facades expose `metadata()` / `worldMetadata()` and manual metadata refresh helpers.
- `~` `PluginProtocol::kMaxPanelElements` raised to 8192 for dense tables.
- `~` `kSdkVersion` 83; `kMinSupportedSdkVersion` unchanged.

## v82 -- Client walking destination

- `+` `LocalPointState` plus `HostApi::getLocalDestinationLocation` and `getWorldDestinationLocation` expose the minimap red-flag walking destination.
- `+` C++ / JS / Java `Client` APIs expose nullable local/world destination accessors.
- `~` `kSdkVersion` 82; `kMinSupportedSdkVersion` unchanged.

## v81 -- WorldView-aware reads and instance templates

- `+` `HostApi` exposes WorldView lookup by id, top-level/default WorldView lookup, instance template chunks, and bidirectional instance WorldPoint conversion.
- `+` C++ / JS / Java expose conversion through `WorldPoint` and `Locatable` helpers, with Java `Client` bridge methods and instance state/chunks available from client/world-point facades.
- `~` `kSdkVersion` 81; `kMinSupportedSdkVersion` unchanged.

## v80 -- Side-panel icon library and tinting (BREAKING)

- `+` Side-panel icon specs now support `awesome:`, `lucide:`, and `phosphor:` prefixes; unprefixed values remain Font Awesome-compatible.
- `+` `PanelDescriptor::iconColor`, C++ `SidePanel::iconColor`, JS/TS `PanelDef.iconColor`, and Java `@SidePanel(iconColor=...)` tint all non-image nav icons.
- `-` `kSdkVersion` 80; `kMinSupportedSdkVersion` raised to 80 because `PluginProtocol::PanelDescriptor` layout changed.

## v79 -- Consolidated developer tools (BREAKING)

- `+` `HostApi::setInternalToolVisible` and `getInternalToolVisible` replace the per-inspector debug-window toggles.
- `-` Removed the old per-inspector HostApi visibility functions.
- `-` `kSdkVersion` 79; `kMinSupportedSdkVersion` raised to 79 because HostApi layout changed.

## v78 -- Actor animation changed event

- `+` `PluginApi::onAnimationChanged` and `AnimationChangedEvent` expose accepted `Actor::Animation` field changes with resolved `PlayerState` / `NpcState` actor snapshots.
- `+` Client hook support for `ACTOR_ANIMATION` / SetAnimation dispatches only after the original setter accepts a different animation value, including changes to or from `-1`.
- `~` `kSdkVersion` 78; `kMinSupportedSdkVersion` unchanged.

## v77 -- Projectile actor references (BREAKING)

- `+` `ProjectileState::sourceEntity` and `targetEntity` now expose decoded actor hash indexes, with `-1` for no actor.
- `+` `ProjectileState` now carries `rawSourceEntity`, `rawTargetEntity`, `sourceEntityType`, and `targetEntityType`.
- `+` C++ / JS / Java projectiles expose `sourceActor()` / `targetActor()` and projectile queries expose `targetingActor(actor)` / `fromActor(actor)`.
- `-` `kSdkVersion` 77; `kMinSupportedSdkVersion` raised to 77 because projectile actor reference semantics and layout changed.

## v76 -- Hitsplat/spotanim split

- `+` Corrected `HITSPLAT_ADDER` signature to `(actor,type,value,delay,cycle,limit)`.
- `+` Added `SPOTANIM_ADDER` hook/event as `onActorSpotAnim`, with clear/removal ids filtered separately from hitsplats.
- `+` Added `ActorSpotAnimState` and `HostApi::getActorSpotAnims` for actor-attached spot animation snapshots.
- `+` Added `HostApi::setActorSpotAnimInspectorVisible` and the in-client Actor SpotAnim Inspector.
- `~` `kSdkVersion` 76; `kMinSupportedSdkVersion` unchanged.

## v75 -- Hitsplat Inspector

- `+` `HostApi::setHitsplatInspectorVisible` toggles the in-client Hitsplat Inspector debug UI.
- `+` New reference plugin: [`plugins/hitsplat_inspector/`](../../plugins/hitsplat_inspector).
- `~` `kSdkVersion` 75; `kMinSupportedSdkVersion` unchanged.

## v74 -- Hitsplat applied event

- `+` `PluginApi::onHitsplatApplied` and `HitsplatAppliedEvent` expose applied hitsplats with resolved `PlayerState` / `NpcState` actor snapshots.
- `+` Client hook support for optional analyzer `HITSPLAT_ADDER`; missing or zero offsets disable cleanly and clear/removal writes are filtered.
- `~` `kSdkVersion` 74; `kMinSupportedSdkVersion` unchanged.

## v73 -- Actor pose and stationarity exposure

- `+` `PlayerState` and `NpcState` now carry `movementPose` and `idlePose`, the raw actor pose ids previously surfaced internally under state-oriented names.
- `+` NPC wrappers in C++ / JS / Java now expose `isStationary`; C++ / JS / Java player and NPC wrappers expose `movementPose` / `idlePose`.
- `~` Offset bundle keys remain `MovementState` / `IdleState` for this revision.
- `-` `kSdkVersion` 73; `kMinSupportedSdkVersion` raised to 73 because `PlayerState` and `NpcState` grew and native plugins must rebuild.

## v72 -- Polished side-panel helpers

- `+` C++ / JS / Java panel builders now expose semantic section/status, button-style, password/multiline input, and paired collapsible helpers.
- `~` Helpers encode into existing `PanelElement` fields; no `PluginApi`, `HostApi`, struct-layout, or IPC protocol changes.
- `~` `kSdkVersion` 72; `kMinSupportedSdkVersion` unchanged.

## v71 -- Tile object dynamic animation id

- `+` `TileObjectState` now carries `animation`, the active dynamic scenery sequence id for scenery/gameobject locs, or `-1` when absent/unreadable.
- `+` C++ / JS / Java `TileObject` wrappers expose `animation` / `getAnimation`.
- `-` `kSdkVersion` 71; `kMinSupportedSdkVersion` raised to 71 because `TileObjectState` grew and native plugins must rebuild.

## v70 -- Tile object scene typecode + orientation

- `+` `TileObjectState` now carries the raw scene-object typecode byte plus the derived scene object type and orientation. The packed ID path is unchanged.
- `+` C++ / JS / Java `TileObject` wrappers expose `sceneTypecode`, `sceneObjectType` / `shape`, and `orientation`.
- `+` Analyzer output now includes `EntityTypecode` alongside `EntityPackedId`, plus `TYPECODE2_FUNC` for audit/debug.
- `-` `kSdkVersion` 70; `kMinSupportedSdkVersion` raised to 70 because `TileObjectState` grew and native plugins must rebuild.

## v69 -- Sound event + audio playback toggle

- `+` `PluginApi::onSoundPlayed` + `SoundPlayedEvent` struct (with `kind` = synth / jingle). Fires when the native client plays a queued synth sound effect (captured at the queue drain) or a MIDI jingle (captured at `PlayJingle`). Fields: `kind`, `soundId`, `loops` (synth), `durationMs` (jingle), `packedPos` (synth), `gameTick`, `consumed`. Set `event->consumed = 1` to mark the event handled for handler ordering; current playback suppression is global-only via the audio toggle.
- `+` `HostApi::setAudioPlaybackDisabled` / `getAudioPlaybackDisabled` -- global toggle that suppresses all sound playback (synth entries dropped from the queue; jingles not played) at the hooks.
- `+` Fluent `titan::state::audio()`, JS `titan.audio`, Java `Client.setAudioPlaybackDisabled` / `audioPlaybackDisabled`.
- `+` `HostApi::setSoundEffectInspectorVisible` + in-client `SoundEffectInspector` debug UI (live log / per-sound frequency view / mute toggle) fed by the sound-effect hook, toggled by the new `sound_effect_inspector` plugin.
- `~` Additive only (new struct + appended `PluginApi`/`HostApi` fn pointers); `kSdkVersion` 69, `kMinSupportedSdkVersion` unchanged so older plugins keep loading.

## v68 -- GraphicsObject animation sequence state

- `+` `GraphicsObjectState` now carries active sequence pointer/id, current frame, frame cycle, loop count, total cycle, and a `SequenceState` snapshot.
- `+` C++/Java/JS SDKs expose `GraphicsObject` animation accessors; legacy `seqTypePtr` aliases `seqPtr`.
- `-` `kSdkVersion` 68; `kMinSupportedSdkVersion` raised to 68 because native plugins must rebuild for the expanded `GraphicsObjectState`.

## v66 -- Plugin dependency graph + cross-plugin service registry

- `+` `PluginApi::getDependencies` enumerates the plugin ids this plugin depends on (RuneLite-style). The host loads dependencies before dependents and rejects cycles / missing deps. Append-only `PluginApi` addition.
- `+` C++ `TITAN_PLUGIN_DEPS(TypeA, TypeB)` (type-based, via each plugin's static `kPluginId`) and `TITAN_PLUGIN_DEP_IDS("a","b")` (string escape hatch); Java `@PluginDescriptor.dependencies()` / `dependencyIds()`.
- `+` `HostApi::registerPluginService` / `getPluginService` -- string-keyed opaque service registry so a dependency can publish an interface that dependents resolve and call. Fluent: `titan::registerService` / `titan::service`. Append-only `HostApi` addition.
- `+` Multi-plugin DLLs: `TITAN_REGISTER_PLUGINS(A, B, ...)` emits `TitanGetPluginCount`, `TitanCreatePluginAt` (per-index instantiation) and `TitanPluginIdList` (newline-joined id string read statically by the store uploader). `TITAN_REGISTER_PLUGIN` now also emits these (count == 1) so the host has a single enumeration path. Build with `titan_add_plugin(... SLUGS a b c)`.
- `~` `titan::detail::self()` refreshes the per-DLL current-plugin ambient on every dispatch so `titan::plugins().self()` resolves correctly inside a multi-plugin DLL.
- `~` `kSdkVersion` 66; `kMinSupportedSdkVersion` unchanged (65). Older plugins load unchanged and report no dependencies.

## v65 -- Multiple side panels per plugin + image icons (BREAKING)

- `+` A plugin may now expose several side panels, each with its own id, title, and optional icon. `PluginApi::getPanels` enumerates them; `getPanelElements` / `onPanelAction` now take a `panelId` selector.
- `+` `PluginApi::getPanelIcon` streams a custom PNG image icon per panel (RuneLite-style nav icons), decoded by the controller via WIC.
- `+` C++ `titan::Plugin::panel("id","Title", build).onAction(..).icon(..).image(..)`; JS/TS `panels: PanelDef[]`; Java `@SidePanel` annotations + `Plugin.buildPanel`/`onPanelAction` with `net.titan.api.panel.Panel`. All four surfaces reach full parity.
- `-` Removed singular `PluginApi::getHasPanel`/`getPanelTitle` and the panelId-less `getPanelElements`/`onPanelAction`. The `TITAN_PANEL` macro and the virtual `buildPanel`/`onPanelAction`/`hasPanel`/`panelTitle` hooks are gone.
- `~` Wire protocol bumped to 21 (`Plugin` carries `PanelDescriptor[]`, `Message` gains `panelId`, `Command::getPanelIcon` added).
- `-` `kSdkVersion` 65; `kMinSupportedSdkVersion` raised to 65 (PluginApi table reorder/resize -- native plugins must rebuild).

## v64 -- Slot-aware recursive widget queries

- `+` Bounded `WidgetAddressState` / `WidgetQueryState` ABI records and nullable host callbacks enumerate loaded widgets, enumerate direct children beneath retained paths, write text by path, and interact by path.
- `+` C++ `titan::queries::widgets([groupId])` and JS/TS `titan.queries.widgets(groupId?)` expose loaded flats plus recursive dynamic descendants, widget-specific filters, direct-child `.slot(index)` traversal, `.children()`, generic terminals, and traversal truncation.
- `~` Queried widget snapshots retain their root-plus-slot path, so chained `.first()->interact(...)` / `.setText(...)` calls re-resolve the exact live target and fail closed for stale segments.
- `~` Widget text mutation uses analyzer-backed EASTL range assignment when available, supporting inline/heap transitions and text up to 256 UTF-8 bytes. Older offset bundles retain the 22-byte inline fallback.
- `~` Menu-entry APIs consistently expose `identifier` for the DoAction identity field.
- `~` Widget Explorer search reuses the same loaded-widget traversal as widget queries. `kSdkVersion` 64; `kMinSupportedSdkVersion` unchanged.

## v63 -- Slot-addressed dynamic widget text writes

- `+` Nullable `HostApi::setWidgetTextAtSlot(parentPackedId, slot, text)` queues an exact dynamic-child text write and fails closed for invalid, missing, or null slots without touching the parent.
- `+` C++/JS/TS `widgets.setText(parentPackedId, slot, text)` overloads and `children(parentPackedId)` snapshots retain dynamic parent/slot context for `child.setText(text)` and `child.interact(opcode, identifier)`.
- `+` `titan::VarPlayerID::nameOf(id)` and its JS/TS mirror annotate named varp ids.
- `~` Dynamic-child enumeration preserves null placeholders so native slot indexes cannot drift. Ordinary widget snapshots retain packed-id setter and explicit-interaction behavior.
- `~` Var Inspector adds a read-only `VarPlayers` tab with raw-id lookup and live named-catalog values.
- `~` `kSdkVersion` 63; `kMinSupportedSdkVersion` unchanged.

## v62 -- VarClient runtime access + Var Inspector

- `+` Six nullable `HostApi` VarClient entries expose int, string, and optional-long reads/writes. Numeric getters use out parameters; strings use a bounded required-size ABI.
- `+` C++ `titan::state::vars()` and JS/TS `titan.state.vars` expose VarClient int/string/optional-long accessors. JS long values use `bigint`.
- `+` `titan::VarClientInt::nameOf(id)` / `titan::VarClientStr::nameOf(id)` and JS/TS mirrors annotate compatibility-catalog ids.
- `~` The persisted `varbit_inspector` plugin is visibly renamed to “Var Inspector” and adds a read-only VarClient diagnostics tab.
- `~` `kSdkVersion` 62; `kMinSupportedSdkVersion` unchanged.

## v61 -- VarClient compatibility catalogs

- `+` Header-only `<titan/var_client_int.h>` and `<titan/var_client_str.h>` catalogs expose `titan::VarClientInt::*` and `titan::VarClientStr::*`, mirroring RuneLite's deprecated compatibility constants.
- `+` JS/TS mirrors exposed as `titan.VarClientInt.*` and `titan.VarClientStr.*`.
- `~` `kSdkVersion` 61; `kMinSupportedSdkVersion` unchanged.

## v60 -- ItemComposition inventory-action parity

- `~` `HostApi::getItemComposition` now preserves runtime ItemDef inventory-action slots exactly, including empty positional gaps, so plugin code can reproduce `Inventory::interact` action-index logic.
- `~` `TitanPluginSdk::ItemCompositionState::inventoryActions` capacity raised from 8 to 32 entries to match the runtime reader sanity cap.
- `~` `kSdkVersion` 60; `kMinSupportedSdkVersion` 60 because resizing `ItemCompositionState` is a fixed-ABI struct layout break.

## v59 -- Actor path queue locatable support

- `~` Actor logical tile/world position now comes from `PathQueue[0]` when available, with precise-coordinate fallback for empty/invalid queues.
- `~` Actor `localPoint` remains the render/interpolated `PreciseX/Y` position.
- `+` `TitanPluginSdk::WorldPointState` and `HostApi::getActorPathQueue(entityPtr, out, max)` expose valid actor path queue entries as world points.
- `+` C++ `Player` / `Npc` / `Actor` wrappers expose `pathQueue()`.
- `+` JS/TS actor wrappers expose `pathQueue: WorldPoint[]`.
- `~` `kSdkVersion` 59; `kMinSupportedSdkVersion` unchanged.

## v58 -- Varbit fallback chain + source-tagged definitions

- `+` `TitanPluginSdk::VarbitDefSnapshot::source` (uint8_t) tags the origin of the definition: 0 = live in-memory VarBitType cache, 1 = native GET_VARBIT decode, 2 = JS5 disk cache.
- `+` `TitanPluginSdk::VarbitDefSource` enum mirrored on the JS side as `titan.state.cache.varbit(id).source` (`"live" | "native" | "disk"`).
- `~` JS/TS locatable wrappers now expose `localPoint` alongside `tile` and `worldPoint`.
- `~` `titan.state.vars.varbit(id)` is now self-healing -- on a live-cache miss the client falls back to native GET_VARBIT (JS5-loads the type on demand), then to a disk-cache extract. Previously returned `-1` for any varbit the game hadn't touched this session (e.g. out-of-area minigame varbits).
- `~` `titan.state.cache.varbit(id)` now prefers the live in-memory definition and falls back to the JS5 disk cache so plugins always see fresh `{varpIndex, lowBit, highBit}` when the game has loaded the varbit.
- `~` `kSdkVersion` 58; `kMinSupportedSdkVersion` unchanged. Field appended at the end of `VarbitDefSnapshot`.

## v57 -- GraphicsObject (map spot anim) queries + events

- `+` `TitanPluginSdk::GraphicsObjectState` struct mirroring the analyzer-derived `MapSpotAnim` fields (`basePtr`, `spotAnimId`, `startCycle`, `plane`, `height`, `preciseX/Z`, `sceneX/Z`, `worldX/Y`, `worldViewPtr`, `seqStateAddr`, `seqTypePtr`).
- `+` `HostApi::getGraphicsObjects(out, max) -> count` and `IBackend::getGraphicsObjects` exposed to ExternalBackend / InternalBackend; walks `WorldView::GraphicsObjectList`.
- `+` `PluginApi::onGraphicsObjectSpawned / onGraphicsObjectDespawned / onGraphicsObjectMoved` lifecycle events, diffed identically to projectiles (scene reset clears, scene-change reseeds, basePtr identity drives spawn/despawn).
- `+` Fluent C++ wrappers: `titan::GraphicsObject` (Locatable), `titan::GraphicsObjectQuery` with `spotAnim(id)` / `onPlane(plane)` / `startedAfterTick(t)` / `startedBeforeTick(t)`, factory `titan::queries::graphicsObjects()`.
- `+` JS/TS surface: `titan.getGraphicsObjects()` raw getter, `titan.queries.graphicsObjects()` query, `Plugin.onGraphicsObject{Spawned,Despawned,Moved}` callbacks; entries carry `tile`, `worldPoint`, and `hasLineOfSight` via the standard Locatable methods.
- `~` `kSdkVersion` 57; `kMinSupportedSdkVersion` unchanged.

## v56 -- Exact TileObject interact via HostApi

- `+` `HostApi::interactTileObject(action, TileObjectState*)` for exact loc-instance interactions.
- `~` C++ `TileObject::interact()` and JS `object.interact()` preserve the selected object's tile/action state instead of re-resolving nearest by loc id.
- `~` `HostApi::interactObject(action, locId/name)` remains the nearest-by-id/name convenience path.
- `~` `kSdkVersion` 56; `kMinSupportedSdkVersion` unchanged.

## v55 -- Slot-aware inventory interact via HostApi

+ `HostApi::interactInventoryItemAtSlot` fn pointer so external plugins dispatch through `Widgets::interact` (proper widget click-bounds) instead of the `executeSyntheticAction` detour that sent clicks to (0,0).
~ `ExternalBackend::interactInventoryItemAtSlot` now forwards directly to the host instead of reimplementing action resolution client-side.

## v54 -- Active interaction predicate

- `+` C++ `Actor::isInteracting()` delegates to the underlying player/NPC.
- `+` JS/TS `ActorBase.isInteracting()` mirrors the entity-overlay active
  interaction predicate before resolving raw `interacting()` targets.
- `~` `interactingWith(...)`, `interactingWithLocal()`, and
  `notTargetedByOtherPlayers()` query filters now ignore stale interaction
  targets by requiring `isInteracting()` first.
- `~` `kSdkVersion` 54; `kMinSupportedSdkVersion` unchanged.

## v53 -- In-frame menu-action replacement

- `+` `MenuOptionClickedEvent` replacement fields appended for in-frame
  DoAction replacement while preserving original event fields.
- `+` C++ `MenuClickEvent::replaceWith(...)`, replacement setters,
  `clearReplacement()`, and `replaced()`.
- `+` JS/TS `MenuOptionClicked.replaceWith(...)`,
  `clearReplacement()`, `consume()`, and `replaced`.
- `~` `kSdkVersion` 53; `kMinSupportedSdkVersion` unchanged.

## v52 -- Locatable line of sight

- `+` RuneLite-style line-of-sight helpers on `WorldPoint`, `WorldArea`,
  and locatable C++ entity wrappers.
- `+` JS/TS `.hasLineOfSight(...)` helpers for locatable wrappers and
  `WorldArea`.
- `+` `ClientState::sceneSizeX` / `sceneSizeY` for safe world-to-scene LOS
  conversion.
- `+` Tile Overlay setting to highlight tiles the local player has line of
  sight to.
- `~` `kSdkVersion` 52; `kMinSupportedSdkVersion` unchanged.

## v51 -- Widget text setter

- `+` `HostApi::setWidgetText(packedId, text)` with C++ facade helpers
  `state::widgets().setText(...)` and `WidgetSnapshot::setText(...)`.
- `+` JS/TS widget text setters: `titan.state.widgets.setText(...)`,
  per-widget `setText(...)`, and assignment through `widget.text = "..."`.
- `~` `kSdkVersion` 51; `kMinSupportedSdkVersion` unchanged.

## v50 -- Ground item ownership

- `+` `GroundItemState::ownershipType` with raw ClientObj ownership:
  `0=None`, `1=SelfPlayer`, `2=OtherPlayer`, `3=GroupIronman`.
- `+` `GroundItemOwnership` constants exposed to C++ and JS/TS plugins,
  plus `titan::GroundItem::ownershipType()` and `canLoot()`.
- `+` `GroundItemQuery::canLoot()` and JS/TS `GroundItem.canLoot()` /
  `GroundItemQuery.canLoot()`.
- `+` `state::client().accountType()`, `.isIronman()`, and
  `.isGroupIronman()` in C++, with matching `titan.state.client`
  JS/TS properties and `isIronMan` / `isGroupIronMan` aliases.
- `~` `kSdkVersion` 50; `kMinSupportedSdkVersion` unchanged.

## v49 -- Magic API shape and widget constants

- `+` Header-only `titan::utils::Magic` catalog with spellbook enums,
  TP_RL-derived spell metadata, state predicates, home-teleport timing,
  and `canCast(...)`.
- `+` RuneLite `InterfaceID.MagicSpellbook` constants mirrored to native,
  JS, and TypeScript catalogs. These now live under generated gamevals.
- `+` `VarPlayerID::LAST_HOME_TELEPORT`.
- `+` Magic `select` / `cast` / `castOn` action shape added as
  no-op stubs returning `false`; selected-spell getters/checks and real
  dispatch are intentionally omitted.
- `~` `kSdkVersion` 49; `kMinSupportedSdkVersion` unchanged.

## v48 -- Actor health bars + hitsplats

- `+` `PlayerState` / `NpcState`: `healthRatio`, `healthScale`,
  and `hasHealthBar`.
- `+` Player/NPC/Actor fluent accessors for health bar state and
  health percent.
- `+` Player/NPC query filters for health bars and health-percent
  thresholds.
- `~` `kSdkVersion` 48; `kMinSupportedSdkVersion` unchanged.

## v47 -- Query API expansion

- `+` `NpcQuery`: `interactingWith(Actor)`, `interactingWithLocal()`,
  `notInteracting()`, `isAnimating()`, `notAnimating()`, `animation(id)`,
  `overheadActive()` / `overheadActive(HeadIcon)`, `overrideTransform(id)`,
  `sizeEquals(s)`.
- `+` `PlayerQuery`: `interactingWith(Actor)` (replaces raw `int32_t`
  overload), `interactingWithLocal()`, `notInteracting()`,
  `isAnimating()`, `notAnimating()`, `animation(id)`, `isIdle()`,
  `isSkulled()`, `overheadActive()` / `overheadActive(HeadIcon)`,
  `combatLevelAbove(n)`, `combatLevelBelow(n)`, `combatLevelBetween(lo, hi)`.
- `+` `ObjectQuery`: `layer(id)`.
- `+` `GroundItemQuery`: `maxQuantity(n)`.
- `+` `InventoryQuery`: `minQuantity(n)`, `maxQuantity(n)`,
  `excludeIds({...})`, `excludeNames({...})`.
- `+` `ProjectileQuery`: `targetingEntity(idx)`, `fromEntity(idx)`,
  `startedAfterTick(t)`, `endsBeforeTick(t)`, `activeDuring(t)`.
- `+` `QueryBase`: `sortBy(cmp)`, `empty()` (JS/TS parity).
- `+` `LocatableQueryBase`: `onTile(Tile)`, `atWorldPoint(WorldPoint)`,
  `sortedByDistanceTo(origin)`, `nearest()` (zero-arg, resolves local player).
- `~` `namesAnyOf` semantics aligned to case-insensitive **substring**
  (was equality in C++; JS/TS already used substring).
- `~` `ProjectileQuery` in `.d.ts` now extends `LocatableQuery<Projectile>`.
- `~` JS projectile objects now include `tileX` / `tileY` / `worldX` / `worldY`.
- `~` JS `within` / `nearestTo` now respect `plane` (cross-plane filtered).
- `~` JS tile objects now include the `layer` property.
- `~` `kSdkVersion` 47; `kMinSupportedSdkVersion` unchanged.

## v46 -- OverlayPanel API

- `+` `AnchorAbi` namespace (`DYNAMIC` / `TOP_CENTER` / `LEFT_CENTER` /
  `RIGHT_CENTER` / `ABOVE_CHATBOX_RIGHT` / `TOOLTIP`) -- minimal
  semantic set; corner anchors are intentionally omitted because users
  free-position into corners via Alt-drag rather than snapping to
  them. Edge-center / chatbox / tooltip anchors all resolve relative
  to game widgets, not the full window.
- `+` `OverlayPanelStyleAbi` struct -- sticky theming (background,
  border colour + thickness, corner radius, padding, gap, per-component
  colour overrides).
- `+` `HostApi::overlayPanelRegister` / `overlayPanelUnregister` /
  `overlayPanelBegin` / `overlayPanelEnd` / `overlayPanelSetStyle` /
  `overlayPanelTitle` / `overlayPanelLine` / `overlayPanelProgressBar`
  -- structured HUD panels with anchor-based auto layout, Alt-drag
  repositioning, rounded borders, full theming, and per-machine
  persistence at `%USERPROFILE%\.titanclient\overlay_layout.json`.
- `+` `titan::OverlayPanel` (subclass of `titan::Overlay`),
  `titan::OverlayPanelStyle`, `titan::Anchor`, and the
  `Plugin::overlayPanel(name, anchor, [priority,] lambda)` factory in
  [`shared/titan/overlay_panel.h`](overlay_panel.h).
- `+` JS: `Plugin.overlayPanel({ name, anchor, priority?, style?, render })`
  + `titan.OverlayAnchor` enum.
- `~` `kSdkVersion` 46; `kMinSupportedSdkVersion` unchanged.

## v45 -- Run energy and weight

- `+` `ClientState::runEnergy` (int32, 0-10000) -- direct Client struct field.
- `+` `ClientState::weight` (int32, signed kg) -- direct Client struct field.
- `+` `titan::state::client().runEnergy()` / `.weight()` (C++ facade).
- `+` `titan.state.client.runEnergy` / `.weight` (JS/TS).

## v43 -- Human-like delayed keyboard typing

- `+` `HostApi::typeKeyboardString` -- type a string with randomized
  inter-character delays; each character dispatched on a separate
  pump-thread drain cycle. Optional C callback fires on a
  caller-selected phase (pump thread, ClientTick, PreGameLoop).
- `+` `HostApi::cancelKeyboardType` -- cancel an in-progress type.
- `+` `HostApi::isKeyboardTyping` -- query whether typing is active.
- `+` `KeyboardTypeCallbackPhase` namespace (ordinals for callback
  dispatch target).
- `+` JS: `_titan.keyboard.typeString(text, opts?, onDone?)` +
  `_titan.keyboard.cancelTypeString()` + `_titan.keyboard.isTyping()`.
- `~` `kSdkVersion` 43; `kMinSupportedSdkVersion` unchanged.

## v42 -- Keyboard injection + CS2 typed args

- `+` `HostApi::sendKeyboardString` -- inject a string into the game's
  internal keyboard producer chain (NO OS APIs; silent no-op when the
  analyzer didn't emit the producer RVAs for this revision).
- `+` `HostApi::sendKeyboardKey` -- press+release a named key with
  optional shift/ctrl/alt modifiers.
- `+` `HostApi::runClientScriptTyped` -- run a CS2 script with mixed
  int + SSO-inline string arguments via the game's HookReq path.
- `+` `TitanHookArg` struct for typed CS2 args.
- `+` `KeyboardMods`, `KeyboardKey` constant namespaces.
- `+` JS: `_titan.keyboard.sendString(s)`, `_titan.keyboard.sendKey(k, mods?)`.
- `~` `kSdkVersion` 42; `kMinSupportedSdkVersion` unchanged.

## v41 -- SDK shape unification + Inventory utility helpers (HARD BREAK)

- `-` **Source-level break.** Every public factory in `shared/titan/*.h`
  moved out of the flat `namespace titan` and into one of three explicit
  shape namespaces:
  - **Queries** -- `titan::queries::npcs()`, `players()`, `objects()`,
    `groundItems()`, `inventory()`, `projectiles()` (was
    `titan::npcs()` etc.).
  - **State** -- `titan::state::client()`, `camera()`, `hider()`,
    `cache()`, `vars()`, `skills()`, `prayers()`, `script()`,
    `widgets()`, `idle()`, `login()`, `walk()`, `itemContainer()`,
    `itemDef()`, `collisions()`, plus the new
    `titan::state::world::{current, list, hop, hopByListIndex,
    hopIngame}` (renamed from the v40 flat
    `titan::currentWorld / worldList / hopToWorldId / hopToListIndex /
    hopToWorldIngame`).
  - **Utils** -- `titan::utils::Dialogue`, `Combat`, `Equipment`
    (already there in v40, just no longer shadowed at the top level)
    plus the new `Inventory` namespace (see below).
  Top-level free helpers stay where they were: `titan::log`,
  `titan::logf`, `titan::addChatMessage`, `titan::runOnClientTick`,
  `titan::runOnRender`, plus `titan::Plugin` / `titan::overlay()` /
  `titan::plugins()` and the registration macros.
- `+` New SDK header `<titan/utils/inventory.h>` -- header-only
  `namespace titan::utils::Inventory`. State predicates (`isOpen`,
  `size`, `emptySlots`, `isFull`, `isEmpty`), reads (`getAll`,
  `get(id|name)`, `getSlot`, `getByIds`, `getByNames`), predicates
  (`contains`, `contains(id, qty)`, `count(id|ids)`, `containsAny`,
  `containsAll`) and `drop(id|name)`. Composed over
  `titan::queries::inventory()` (item enumeration) and
  `titan::state::widgets().get((149<<16)|0)` (the inventory parent
  widget for the visibility check). Mirrors
  `client/actions/inventory.{h,cpp}` and the RuneLite-side
  `theplug.utils.core.api.InventoryUtils`.
- `+` JS bootstrap: `_titan.queries`, `_titan.state`, `_titan.utils`
  are the only entry points. New `_titan.utils.inventory.*` matching
  the C++ surface. `titan.state.world.*` exposed via new natives
  (`getCurrentWorld`, `getWorldList`, `hopToWorldId`,
  `hopToListIndex`, `hopToWorldIngame`).
  `titan.state.client.invokeMenuAction(entry)` replaces the top-level
  `titan.invokeMenuAction`.
- `+` TypeScript (`shared/titan-plugin-sdk.d.ts`) restructured into
  the same three-shape layout. New `titan.utils.inventory` surface;
  flat aliases dropped.
- `+` `PUBLIC_API.md` rewritten with a "Three shapes of the SDK"
  intro section. Symbol tables regrouped under Queries / State /
  Utils. Both quickstarts (native + JS) updated to the v41
  spellings. Reference-plugin pattern table updated.
- `~` Source-only break: HostApi function-pointer ordering and struct
  layouts are unchanged. Old plugin binaries built against v40 or
  earlier embed `sdkVersion < 41` and are rejected at load with the
  standard "rebuild against SDK v41" message.
- `~` `kSdkVersion` 41, `kMinSupportedSdkVersion` 41 (forward-compat
  window collapsed to the new shape).
- `~` Polish pass within v41 (no further version bump): naming
  consistency + JS/TS shape parity. Predicate prefixes normalised
  (`is*` / `has*` / `contains`), `get` -> `find` for optional-returning
  lookups (`WidgetsFacade::find`, `Inventory::find`, `Equipment::find`),
  hider noun setters renamed to `setPlayers` / `setNpcs` / `setSelf`
  / `setScene` paired with `isPlayersHidden` etc., id-bearing methods
  collapsed to bare `id()` (`Npc::id`, `TileObject::id`, `Item::id`,
  `GroundItem::id`), `worldPos()` method on Player/Npc/Tile renamed
  to `worldPoint()` (the `WorldPoint` / `WorldPos` type aliases stay
  for source compat), `Section::sectionKey/sectionName` -> `key/name`,
  `getInteracting` -> `interacting`, predicate renames on
  `Plugin::isEnabled` / `isDefaultEnabled`, `PluginHandle::isValid`
  / `isEnabled`, `World::isMembers` / `isBeta`, `EquippedItem::isValid`,
  `Section::isClosedByDefault`, `SettingCommon::isHidden`,
  `Projectile::hasMoved`, `Actor::isEmpty`. JS/TS zero-arg utility
  methods converted to readonly properties (`utils.dialogue.inDialogue`,
  `isQuestCompletionOpen`, `continueWidgetPackedId`,
  `utils.combat.specialAttackPercentage`, `isSpecialAttackEnabled`,
  `isAutoRetaliateEnabled`). Missing TS/JS surface filled in: overhead
  -icon fields on Player/Npc, `Query.namesAnyOf`, `InventoryQuery.slot`
  / `slotsAnyOf` / `slotsBetween`, `utils.inventory.getByIds` /
  `getByNames`, `utils.equipment.getByIds` / `getByNames`,
  `PluginHandle.setEnabled`. TS cache type aliases re-named
  (`NpcCacheDef` -> `NpcDef`, `ObjCacheDef` -> `ObjDef`,
  `VarbitCacheDef` -> `VarbitDef`). `PUBLIC_API.md` gains a "Naming
  and call-style conventions" section codifying the rules so future
  symbols have an obvious bucket and spelling.

---

## v40 -- Equipment utility helpers

- `+` New SDK header `<titan/equipment_slot.h>` -- `titan::EquipmentSlot`
  enum (HEAD=0..AMMO=13; matches RuneLite's `EquipmentInventorySlot`
  ordinals) and `titan::EquipmentSlotInfo::{name, slotWidgetPackedId,
  isValid, fromOrdinal}` helpers. `slotWidgetPackedId` returns the
  packed worn-items widget id (group 387) for slots that have a
  clickable equipment-screen widget; ARMS / HAIR / JAW return `0`.
- `+` New SDK header `<titan/utils/equipment.h>` -- header-only
  `namespace titan::utils::Equipment`. Surface mirrors the Java
  `EquipmentUtils` 1:1: `getAll`, `get(slot|id|name)`, `getByIds`,
  `getByNames`, `contains` (incl. `(id, minQuantity)` overload),
  `count(id|ids)`, `containsAny` / `containsAll` for both ids and
  names, plus `unequip(slot|id|name)`. Reads compose
  `titan::itemContainer(InventoryID::EQUIPMENT)` and `titan::itemDef`;
  `unequip` fires `titan::widgets().interact(CC_OP, /*identifier=*/1,
  /*p0=*/-1, <wornitems slot widget>)` -- same RuneLite-style menu
  shape used by the dialogue / combat helpers.
- `+` Client-internal mirror `Equipment::*` in
  `client/actions/equipment.{h,cpp}` paralleling Combat / Dialogue.
- `+` JS bindings: `_titan.equipment.{getAll, get, getSlot, contains,
  count, containsAny, containsAll, unequip, unequipSlot}` plus a
  `titan.EquipmentSlot` enum mirror.
- `+` TypeScript types: `EquipmentSlot` const-object, `EquippedItem`
  interface, `titan.equipment` surface in
  `shared/titan-plugin-sdk.d.ts`.
- `~` Composition-only addition: no new HostApi entries, no IBackend
  virtuals, no struct layout changes. `kSdkVersion` 40,
  `kMinSupportedSdkVersion` stays 34.

---

## v39 -- RuneLite-leaning JS/TS surface + widget search-by-text

- `+` `HostApi::getWidgetByText(query, outState)` -- first widget whose
  primary display text contains `query` (case-sensitive substring; same as
  `WidgetReader::findByText`). Fails when there is no packed id on the match.
- `+` `titan::widgets().findByText(query)` fluent wrapper;
  `IBackend::getWidgetByText` + `ExternalBackend` / `InternalBackend` wiring.
- `+` JS: `_titan.getWidgetByText` / `_titan.widgets.findByText`.
- `+` `<titan/collision.h>` -- `titan::CollisionFlag::*`, `titan::collisions()`
  (`flag`, `isBlocked` over existing `getCollisionFlag` only).
- `+` JS: `_titan.collisions.{flag,isBlocked,Flag}`.
- `+` TypeScript reshaping (no JS plugins shipped yet): `WorldPoint`,
  `MenuOptionClicked`, `OverlayLayer`, `Npc.id`, `TileObject.id`,
  `titan.dialogue` / `titan.combat` / `titan.skills` / `titan.prayers`,
  runtime catalog mirrors, `WorldArea` + helpers, `titan.camera.pos*`,
  `titan.collisions`, trimmed `titan.vars`, removed legacy free-function
  declarations from `.d.ts`. See `PUBLIC_API.md`.
- `~` `kSdkVersion` 39. `kMinSupportedSdkVersion` remains 34.

---

## v38 -- Widget child enumeration + dialog option selection

- `+` `HostApi::getWidgetChildren(parentPackedId, outStates, maxOut)` --
  populate up to `maxOut` `WidgetState` entries from the parent widget's
  dynamic-children vector and return the count written. Each entry is
  filled the same way `getWidget()` fills a single widget (screen
  bounds, `text`, `hidden` / `selfHidden` / `visible`, `type`,
  `contentType`, `parentId`, ...). Child `packedId` is the child's own
  packed-component-id when available, or `(parentGroup << 16) | i` as a
  deterministic fallback so callers always see a stable id.
- `+` `titan::widgets().children(parentPackedId)` fluent wrapper on
  `WidgetsFacade` returning `std::vector<WidgetSnapshot>`. Empty vector
  when the parent is missing, has no dynamic children, or the host is
  pre-SDK-38. Cap currently at 128 children per call -- re-invoke
  against a paged parent if you ever need more (no dialog or inventory
  comes close to that bound).
- `+` `titan::utils::Dialogue::hasOption(needles)`,
  `titan::utils::Dialogue::selectOption(needles)`, and
  `titan::utils::Dialogue::handleDialogue(needles)` in
  `<titan/utils/dialogue.h>`. Iterate dynamic children of widget
  `(219, 1)` and fire `WIDGET_CONTINUE` (opcode 30, `identifier = 0`,
  `param0 = <child slot>`, `param1 = pack(219, 1)`) against the first
  option whose text contains any needle (case-insensitive). Mirrors the
  keyboard-press dialog-option selection from the RuneLite-side
  `WidgetUtils` reference -- no OS-level keyboard input.
- `+` JS: `_titan.getWidgetChildren(parentPackedId)` +
  `_titan.widgets.children(parentPackedId)` shorthand returning an
  array of widget-state objects (one per dynamic child). TypeScript
  types updated in `shared/titan-plugin-sdk.d.ts`.
- `~` `HostApi::getWidgetChildren` is appended at the end of `HostApi`;
  `kMinSupportedSdkVersion` is NOT raised. Plugins compiled against v37
  and earlier keep loading -- the new fn pointer is null on older hosts
  and `titan::widgets().children(...)` returns an empty vector.

---

## v37 -- SDK type catalogs + VarsFacade split + menu dispatch

- `+` New SDK headers promoted from `client/game/types/*`:
  - `<titan/prayer.h>` -- `titan::Prayer` enum + `titan::PrayerInfo::{varbitId, name}`.
  - `<titan/skill.h>` -- `titan::Skill` enum + `titan::SkillInfo::{name, MAX_SKILLS}`.
  - `<titan/menu_action.h>` -- `titan::MenuAction::Id` opcode enum + `normalize` / `isWidgetCcFamily` / `isCcOpFamily` / `nameFor` / `Entry` struct.
  - `<titan/varbits.h>` -- `titan::Varbits` constants (~200) + inline `nameOf(int)`.
  - `<titan/var_player.h>` -- `titan::VarPlayerID` constants.
  - `<titan/local_point.h>` -- `titan::LocalPoint` struct (plane-less, sub-tile, Euclidean distance).
  - `<titan/world_area.h>` -- `titan::WorldArea` struct (contains / distanceTo / center).
- `+` `titan::skills()` fluent facade (`SkillsFacade`) with `boosted(int|Skill)`,
  `real(int|Skill)`, `experience(int|Skill)`. Routes to existing
  `IBackend::getBoostedSkillLevel` / `getRealSkillLevel` / `getSkillExperience`.
- `+` `titan::prayers()` fluent facade (`PrayersFacade`) with
  `isActive(int|Prayer)`. Routes to existing `IBackend::isPrayerActive`.
- `+` `ClientFacade::invokeMenuAction(MenuAction::Id, identifier, param0, param1, wvId=-1)`
  and `invokeMenuAction(const MenuAction::Entry&)` wrapping the existing
  `IBackend::executeSyntheticEntry` virtual. Enables direct synthetic
  menu dispatch from plugin code without reaching for raw `SyntheticActionEntry`.
- `+` New helpers on existing `titan::Tile` / `titan::WorldPos`:
  `distanceTo(other)`, `distanceTo2D(other)`, `isInScene(...)` on `Tile`;
  `distanceTo(other)`, `distanceTo2D(other)`, `regionId()`, `regionX()`,
  `regionY()`, `dx(d)` / `dy(d)` / `dz(d)`, and default `==` / `!=` on `WorldPos`.
- `+` `using titan::WorldPoint = titan::WorldPos;` alias for naming parity
  with RuneLite / client-side convention.
- `-` **BREAKING (source-only)** `VarsFacade::prayerActive` /
  `boosted` / `real` / `experience` removed. Replace with
  `titan::prayers().isActive(...)` and
  `titan::skills().boosted / real / experience(...)`. The underlying
  `HostApi` / `IBackend` entries (`isPrayerActive`, `getBoostedSkillLevel`,
  `getRealSkillLevel`, `getSkillExperience`) are unchanged, so the
  binary ABI is not affected -- only the C++ fluent wrapper moves.
  `kMinSupportedSdkVersion` therefore stays at 34.
- `~` `client/game/types/varbits.cpp` removed; `Varbits::nameOf` is now
  inline in the SDK header. The top-level `Varbits`, `VarPlayerID`,
  `Prayer`, `Skill`, `MenuActionDef` names in `client/game/types/*.h`
  are preserved as thin aliases for source compatibility.
- `+` Packed widget ids for dialogue/combat helpers (`MakeButton`,
  `AutoRetaliate`, `QuestscrollClose`, `QuestscrollContent`,
  `DialogOptions`, `SpecOrb`, plus "click here to continue" candidates).
  These are now provided by generated `titan::gamevals::InterfaceID`.
- `+` `<titan/utils/dialogue.h>` -- header-only `titan::utils::Dialogue`
  inline helpers (`continueMake`, `continueDialogue`, `inDialogue`,
  `isQuestCompletionOpen`, `closeQuestCompletion`,
  `getContinueWidgetPackedId`). Replaces the keyboard-input shim in the
  RuneLite-side `WidgetUtils` reference port; every helper dispatches
  through `titan::widgets().interact(...)`.
- `+` `<titan/utils/combat.h>` -- header-only `titan::utils::Combat`
  inline helpers (`enableSpecialAttack(skipMovement=false)`,
  `getSpecialAttackPercentage`, `isSpecialAttackEnabled`,
  `isAutoRetaliateEnabled`, `setAutoRetaliate`). `skipMovement` is retained
  for source compatibility and does not suppress clicks.
- `+` `VarPlayerID::SPECIAL_ATTACK_ENABLED` (300) and
  `VarPlayerID::AUTO_RETALIATE` (172) added to `<titan/var_player.h>`
  for the new combat helpers.
- `~` Composition-only addition: no new HostApi entries, no struct layout
  changes, `kSdkVersion` and `kMinSupportedSdkVersion` are unchanged.

---

## v36 -- Generic widget interaction

- `+` `HostApi::widgetInteract(opcode, identifier, param0, param1)` -- dispatch
  any widget-family DoAction (CC_OP=57, CC_OP_LOW=1007, WIDGET_*_OPTION=39..43,
  WIDGET_TARGET=25, WIDGET_TARGET_ON_WIDGET=58, ...) against a widget slot.
  Resolves the parent widget from `param1` (packed `(group << 16) | child`),
  walks dynamic children at `param0` (falls back to parent bounds when the
  child is unavailable), computes a Gaussian-weighted click point from the
  resolved widget's screen bounds, and hands a synthetic menu entry to the
  native `doActionArg11Builder` path.
- `+` `titan::widgets().interact(opcode, identifier, param0, param1)` fluent
  wrapper on the existing `WidgetsFacade`, plus
  `WidgetSnapshot::interact(opcode, identifier, param0)` that auto-fills
  `param1` with the snapshot's own packed id.
- `+` `TitanPluginSdk::WidgetState::packedId` populated by `hostGetWidget`.
  Gives IPC forwarders, future event callbacks, and scripts the widget
  identifier without re-threading the lookup key. Zero on pre-SDK-36 hosts.
- `+` JS: `_titan.widgetInteract(opcode, identifier, param0, param1)` +
  `_titan.widgets.interact(...)` shorthand, plus a per-widget
  `ws.interact(opcode, identifier, param0)` method on the object returned
  by `_titan.widgets.get(...)` (uses `ws.packedId` as `param1`).
- `~` `Inventory::dispatchInventoryAction` now thunks through
  `Widgets::interact`; semantics unchanged.
- `~` `HostApi::widgetInteract` and `WidgetState::packedId` appended at the
  end of their respective structs; `kMinSupportedSdkVersion` is NOT raised
  -- plugins compiled against v35 and earlier keep running unchanged.
- `+` `titan::HeadIcon` enum + `titan::HeadIconInfo` helper namespace
  (`<titan/head_icon.h>`) -- 15-ordinal enum mirroring RuneLite
  `net.runelite.api.HeadIcon`, with `name` / `shortName` / `isPrayer` /
  `isCurse` / `color` / `isValid` / `fromRaw` helpers. Header-only; no
  ABI surface.
- `~` Added fluent predicates `titan::{Player,Npc,Actor}::isOverheadActive()`
  (any icon) and `isOverheadActive(HeadIcon)` (specific icon match), plus
  `titan::Player::isSkulled()`. Pure derivation of existing v35 ABI
  fields (`overheadIcon`, `skullIcon`, `hasHeadIconOverride`); no HostApi /
  struct changes; `kSdkVersion` NOT bumped. Matches the shape of existing
  `isIdle()` / `isAnimating()` / `isStationary()` helpers.

---

## v35 -- Actor overhead icons

- `+` `PlayerState::overheadIcon` (int32_t, -1 = none) -- live overhead prayer
  icon read from `ClientPlayer + PLAYER_OVERHEAD_ICON_OFFSET`.
- `+` `PlayerState::skullIcon` (int32_t, -1 = none) -- live skull icon from
  `ClientPlayer + PLAYER_SKULL_ICON_OFFSET`.
- `+` `NpcState::overheadIcon` (int32_t, -1 = none) -- primary overhead icon
  sourced from the NpcType cache-default head icon graphics vector.
- `+` `NpcState::hasHeadIconOverride` (uint8_t) -- non-zero when a
  per-instance runtime override is set via `ClientNpc::SetHeadIcon`.
- `+` `FindPlayerOverheadIcon` / `FindNpcHeadIcon` analyzer passes.
- `~` Fields appended at the end of `PlayerState` / `NpcState`; existing
  plugins compiled against v34 and earlier keep running unchanged.

---

## v33 -- Entity clickbox + hull overlays

- `+` HostApi::drawEntityClickbox(entityPtr, outline, fill) -- project the
  game's cached world-space AABB for a player / NPC and draw 12 wireframe
  edges plus optional translucent face fills. Reads the AABB from the
  entity's `jag::graphics::GraphNode` cache via the new
  `Clickbox::readActorAabb` helper; matches what the game's own picker
  uses in `DoEntityPicking`.
- `+` HostApi::drawTileObjectClickbox(locPtr, outline, fill) -- same for
  scene objects (walls, decor, standing locs, ground decor).
- `+` HostApi::drawEntityHull(entityPtr, outline, fill) -- project the
  same AABB, compute the 2D convex hull of the 8 projected corners, and
  render as a closed filled/outlined polyline. Clean silhouette without
  interior edges; the recommended default for "highlight this entity"
  overlays. Future SDK revisions may upgrade the vertex source to real
  model geometry once the analyzer can extract per-instance batches.
- `+` HostApi::drawTileObjectHull(locPtr, outline, fill) -- tile-object
  equivalent.
- `+` `titan::overlay()` facades for all four shapes: `entityClickbox`,
  `tileObjectClickbox`, `entityHull`, `tileObjectHull`. Each accepts a
  typed overload (Npc / Player / TileObject) and a raw-pointer overload.
- `+` `titan::TileObject::entityPtr()` -- exposes the raw loc pointer.
- `+` Analyzer passes `FindClickboxLayout` + `FindModelClickbox` emit
  the supporting offsets under new `GraphNode` / `Aabb` / `Loc`
  namespaces in `osrs_offsets.json`. Missing offsets gracefully disable
  the feature (silent no-op).
- `+` `WorldOverlay::drawAabb` / `drawAabbHull` / `computeConvexHull`
  internal primitives.
- `+` JS bindings: `titan.overlay.entityClickbox`, `entityHull`,
  `tileObjectClickbox`, `tileObjectHull` (and `*Raw` variants).

---

## v32 -- Use-on item API

- `+` `titan::Item::useOn(const Item&)` -- two-packet
  `WIDGET_TARGET` -> `WIDGET_TARGET_ON_WIDGET` flow (the knife-on-logs
  case). Queues the second packet onto the next client tick so the
  game can process the source selection.
- `+` `titan::Item::useOn(const Npc&)` -- select the item, then
  invoke an NPC menu entry on the next client tick.
- `+` `titan::Item::useOn(const TileObject&)` -- select the item,
  then invoke a loc menu entry on the next client tick.
- `+` HostApi::useInventoryItemOnItem / useInventoryItemOnNpc /
  useInventoryItemOnObject vtable entries (nullable; pre-SDK-32
  clients report 0 via the `ExternalBackend` null-guard).
- `+` JS bindings `titan.useInventoryItemOnItem` /
  `useInventoryItemOnNpc` / `useInventoryItemOnObject`, plus a
  shape-dispatched `item.useOn(target)` helper on the JS Item
  prototype.

---

## v31 -- In-game world hop SDK + panel element budget bump

- `+` `titan::hopToWorldIngame(int)` -- drives the native 3x `CC_OP`
  footer-click sequence (opens logout tab, opens switcher, selects
  world, confirms) for the logged-in state. Returns "accepted?" --
  the real hop completes asynchronously across several client ticks.
- `+` `HostApi::hopToWorldIngame` vtable entry (nullable; pre-SDK-31
  clients report 0 via the `ExternalBackend` null-guard).
- `~` `titan::hopToWorldId` and `titan::hopToListIndex` are now
  documented as strictly the **title-screen** path (native
  `changeWorld`, MainLoop phase). Plugins check
  `titan::login().isLoggedIn()` and dispatch to the right one. The
  prior implicit auto-route inside the host has been removed so the
  SDK primitives are transparent about their semantics.
- `~` `kMaxPanelElements` bumped from 256 to 1024 in
  `shared/plugin_protocol.h`. Pure resource-bound change; the wire
  array length is variable. Lets list-heavy panels (world hopper,
  container inspectors) render every row without pagination. No
  protocol version bump -- older decoders truncate at their local
  cap, matching prior behaviour.

---

## v30 -- Actor idle / stationary semantic fix

- `~` `PlayerState.idle` renamed to `PlayerState.stationary`. The field
  actually reports whether the player has no pending movement
  (`movementPose == idlePose`); a stationary player can still be
  animating, so the old name was misleading. Layout is unchanged -- pure
  source-level rename; plugins built against older headers reading
  `.idle` receive the same byte with the same semantics.
- `~` `PlayerState.stationary` slot renamed to
  `PlayerState.unknownPlayerFlag`. Role is TBD -- previously mis-labelled
  as a "stationary" boolean. The client no longer populates this field.
- `+` `titan::Player::isAnimating()` -- `animation() != -1`.
- `+` `titan::Npc::isAnimating()` -- `animation() != -1`.
- `+` `titan::Player::isIdle()` reintroduced with the correct semantic:
  `isStationary() && !isAnimating()`. The previous `isIdle()` (which
  checked movement-queue state only) was renamed to `isStationary()`.

---

## v29 -- Action Inspector

- `+` `HostApi::setActionInspectorVisible(uint8_t)` -- toggles the new
  in-client Action Inspector floating window.
- `+` In-client `ActionInspector` debug UI: DoActionHook ring buffer with
  text / opcode filtering, per-record replay (native arg11 builder with
  clickExecutor fallback), full replay-trace diagnostics (subject probes,
  parity checks, terminal status), synthetic MenuEntry construction, and
  JSON evidence export.
- `~` Reached via the external `action_inspector` plugin (ships disabled
  by default, matching the chat / cs2 / packet / varbit inspector
  family). Replaces the "DoAction Debug" block that previously lived
  inside the developer menu's Debug tab.

---

## v28 -- World hop + world list

- `+` `titan::World` struct: `id`, `flags`, `string0`, `string1` (plus
  `members()` / `beta()` convenience predicates).
- `+` `titan::currentWorld()` returns the live world id or `nullopt`.
- `+` `titan::worldList()` returns a full snapshot of the native
  `GameWorld::m_list` (level 3 availability; empty otherwise).
- `+` `titan::hopToWorldId(int)` dispatches the native
  `TitleScreen::SwitchToWorld` call for the entry matching the id.
- `+` `titan::hopToListIndex(size_t)` lower-tier variant that hops by
  `m_list` position.
- `+` `HostApi::getCurrentWorld`, `getWorldList`, `hopToWorldId`,
  `hopToListIndex` (4 new vtable entries).
- `+` `TitanPluginSdk::WorldState` ABI struct + `kMaxWorldListEntries`.
- `+` JS SDK `titan.World`, `titan.currentWorld()`, `titan.worldList()`,
  `titan.hopToWorldId()`, `titan.hopToListIndex()`.
- `~` Hop dispatch is auto-marshalled onto the game thread via the
  existing `GameThreadDispatcher` (same pattern as `Inventory::interact`).
- `~` Note: `GameState::HoppingWorld` event does NOT fire during a hop
  on rev 237.5 because the hop path on this build doesn't transition
  the login-index state machine. The enum value stays reserved for
  future revisions where the transition is detectable.

---

## v27 -- Item Container Explorer + runtime ItemDef in inventory API

- `+` `HostApi::setItemContainerExplorerVisible(uint8_t)` -- toggles the
  in-client Item Container Explorer floating window.
- `+` In-client `ItemContainerExplorer` debug UI that renders a two-pane
  inspector over the widget-backed container facade: container list (with
  native-pointer validation colour-coding), slot grid, and per-item
  drill-down that surfaces the runtime ItemDef name, stackable flag,
  noted variant, and the runtime inventory-action slot list each action
  mapped to the exact CC_OP / CC_OP_LOW_PRIORITY / EXAMINE_ITEM opcode
  `Inventory::interact()` would emit.
- `+` External [`item_container_explorer`](../../plugins/item_container_explorer/)
  reference plugin -- thin visibility-toggle wrapper following the
  chat / cs2 / packet / varbit inspector pattern. Ships disabled by
  default.
- `~` `Inventory::interact()` / `InvItem::drop()` now resolve action
  labels through the runtime ItemDef accessor (with cache fallback), so
  `.interact("Drop")` / `.interact("Examine")` work for every item --
  previously these silently failed on items whose cache's 5-slot array
  didn't list the label explicitly (the in-game menu builder appends
  Drop / Examine at runtime, and the cache-only resolver never saw
  them). No changes to ABI structs; pure quality improvement on the
  existing dispatch path.
- `~` `InvItem::name` now prefers the runtime-resolved display string
  (varbit / varp / noted transforms applied) when available, matching
  the in-game hover label exactly.

## v26 -- Item containers + runtime ItemDef

- `+` `HostApi::getItemContainer(id, out)` -- widget-backed item-container
  read (modern OSRS stores inventory/bank/equipment as dynamic children of
  widgets; see the container-negative finding in osrs-map for why there's
  no native hashtable to hook). Container id matches RuneLite's
  `InventoryID`: `INVENTORY=93`, `EQUIPMENT=94`, `BANK=95`.
- `+` `HostApi::getItemComposition(id, out)` -- runtime ItemDef accessor
  (RuneLite's `Client.getItemDefinition` equivalent). When the analyzer
  detected `ITEM_DEF_LOOKUP` on the running revision the fields include
  varbit/varp transforms and the menu-builder action list (Drop / Examine
  / Wield / ...). Falls back to cache-file reads plus static Drop/Examine
  rules when the runtime function is unmapped; callers branch on the
  returned `runtimeResolved` flag.
- `+` `PluginApi::onItemContainerChanged` -- tick-level-diff event. The
  host polls the well-known RuneLite containers every game tick and fires
  whenever a snapshot differs from the previous. No native single-write
  chokepoint exists on rev 237.5 (writes happen in TCP_IN and the CS2
  `cc_setobject` opcode handler directly).
- `+` `titan::ItemContainerState`, `ItemContainerChangedEvent`,
  `ItemCompositionState` structs; `kMaxItemContainerSlots`,
  `kMaxItemCompositionActions`, `kMaxItemCompositionActionLen` caps.
- `+` New public header [`shared/titan/inventory_id.h`](inventory_id.h)
  with `titan::InventoryID` enum + `inventoryIdName()` helper.
- `+` C++ fluent facades `titan::itemContainer(int)` and
  `titan::itemDef(int)`; event wrapper `titan::ItemContainerChangedEvent`
  (in `shared/titan/events.h`).
- `+` TypeScript / QuickJS parity: `titan.itemContainer()`,
  `titan.itemDef()`, `titan.InventoryID`, `Plugin.onItemContainerChanged`.
- `+` Analyzer pass [`FindItemDefLookup.java`](../../src/main/java/com/soxsoft/passes/FindItemDefLookup.java)
  -- detects `ITEM_DEF_LOOKUP` via intersection of callees of
  `CS2_SCRIPT_SWITCH`, `WIDGET_RENDER_MODEL`, and depth-2 `DO_ACTION`
  (yields `FUN_005be060` uniquely on rev 237.5). Emits `ITEM_DEF_LOOKUP`
  plus struct offsets (`Name`, `Stackable`, `ActionsBegin/End/Stride`,
  `LinkedNoteId`) in a new `ItemDef` namespace of `osrs_structs.h`.

## v25 -- Varbit Inspector

- `+` `HostApi::setVarbitInspectorVisible(uint8_t)` -- toggles the new
  in-client Varbit Inspector floating window.
- `+` `SetVarbitHook::addListener(Listener)` -- in-process slot so
  client-internal debug UIs can see the same varbit-change stream that
  external plugins see via `Plugin::onVarbitChanged`.
- `+` New reference plugin: [`plugins/varbit_inspector/`](../../plugins/varbit_inspector).
  Ships disabled by default. Live log + per-varbit frequency view for
  reverse-engineering which varbit an in-game action toggles.

## v24 -- Correct ChatMessageType ordinals + first-handshake fix

- `~` `titan.ChatMessageType` (JS) / `ChatMessageType` (TS) constants
  now match [RuneLite's `ChatMessageType` enum](https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/ChatMessageType.java)
  1:1. Renamed `PUBLIC` -> `PUBLICCHAT` (= 2), `SERVER` -> `GAMEMESSAGE`
  (= 0). `CLAN_CHAT` corrected from 3 to 41. Added the full set of
  RuneLite ordinals including the clan / trade / examine / engine
  families. `titan.chat.system()` now routes through `GAMEMESSAGE`
  (ordinal 0); `titan.chat.say()` through `PUBLICCHAT` (ordinal 2).
- `~` `titan::Plugin::_setEnabledFromHost` now always fires
  `onEnabledChanged` on the first call after plugin construction. The
  old gating on `prev != enabled` dropped the controller's initial-state
  handshake when the persisted state matched the then-current DLL default
  (`true`), which left visibility-toggle plugins (chat / cs2 / packet
  inspectors) closed at launch until the user manually toggled.
- `+` Chat Inspector's compose / send debug tool -- in-client only,
  invokes `titan::addChatMessage(...)`.

## v23 -- Plugin metadata + layered SDK groundwork

- `+` `HostApi::addChatMessage` -- inject a local chat line
- `+` `HostApi::setChatInspectorVisible` / `setCs2InspectorVisible` / `setPacketInspectorVisible` -- toggle in-client debug windows
- `+` `PluginApi::getDescription` / `getAuthor` / `getVersion` / `getDefaultEnabled`
- `+` `PluginInfo::description` / `author` / `version`
- `+` `ChatMessageEvent` struct and `Plugin::onChatMessage` virtual
- `+` `kMaxDescriptionLen` / `kMaxAuthorLen` / `kMaxVersionLen`
- `+` `kMinSupportedSdkVersion = 20` constant (formalises the forward-compat
  window that had been implicit before)
- `+` `IBackend` interface + `ExternalBackend` / (future) `InternalBackend`
  dispatch layer (internal refactor; no public-API surface change)
- `~` `TITAN_PLUGIN_META(id, name, description, author, version, defaultEnabled)`
  supersedes the 2-arg `TITAN_PLUGIN` macro. The old macro keeps working
  for one major cycle.
- `~` Plugin list in the controller now shows metadata tooltips.

## v22 -- Chat event + inject

- `+` `HostApi::addChatMessage`
- `+` `PluginApi::onChatMessage`
- `+` `ChatMessageEvent` struct

## v21 -- Varbit event

- `+` `PluginApi::onVarbitChanged`
- `+` `VarbitChangedEvent` struct

## v20 -- Pre-changelog baseline

SDK versions prior to v20 are not supported. Backfill from git history if
needed; the stable contract starts here.

---

## Versioning policy (recap)

- Minor bump: new public symbol appended. Plugins built against an older
  minor still load.
- Major bump: breaking change. `kMinSupportedSdkVersion` moves past the
  broken range; plugins must rebuild. Deprecated symbols remain visible as
  comments for one major cycle to smooth the migration.

See [`PUBLIC_API.md`](PUBLIC_API.md) and the policy block at the top of
[`detail/abi.h`](detail/abi.h) for the complete contract.
