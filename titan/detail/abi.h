/// @file titan/detail/abi.h
/// @brief Native payload definitions and local host/plugin dispatch views.
///
/// Plugin authors normally include the fluent SDK in shared/titan/*.h.
/// The DLL boundary is Native ABI v1, declared in native_abi.h: a fixed
/// TitanPlugin_QueryNative bootstrap returns module/plugin-owned descriptors
/// and independently queried, sized capability tables. HostApi and PluginApi
/// below are local convenience views and are never copied across that boundary.
///
/// Native ABI v1 is a deliberate pre-release reset. Rebuild every existing
/// native plugin once; legacy TitanCreatePlugin DLLs are not supported.
/// After that reset, kSdkVersion identifies a source SDK release only. It is
/// not a native load gate. Required interface id/major/minimum-size contracts
/// decide compatibility; absent optional operations import as null.
///
/// Existing payload layouts, array extents, enum values and meanings are
/// frozen. A structSize tag does not make an array element extensible. Add a
/// new payload type and negotiate a new interface for incompatible changes.
/// Only capability tables permit appended optional functions, with bounds
/// checks against the producer's published table size. See native_payload_v1.h,
/// native_records.h and native_abi.h for explicit baseline checks.
///
/// Preserve these public binary contracts when obfuscating: the bootstrap
/// export name, descriptors, capability tables, payloads, calling conventions
/// and operation semantics. Implementations and local convenience views may
/// change without changing the binary contract. Cross-DLL ownership must
/// return to its allocating module; exceptions never cross the boundary.
///
/// The changelog below records source SDK releases, including historical
/// compatibility policies from before the Native ABI v1 reset. Those old
/// SDK-version windows do not describe the current native loader.
///
/// --- Changelog (most recent first) ---
///
/// v144 -- Break Handler observation: see a break without registering.
///   + HostApi::breakHandlerObserve: copies the break command the coordinator
///     has currently published -- the host's global command, not a
///     participant's copy -- for any loaded, enabled plugin. Nothing is
///     registered or recorded, so it never joins a pause quorum or moves a
///     report epoch. Phase BREAK_PHASE_NONE means no break is in progress.
///   + titan::BreakHandler::observe(plugin) / isBreakInProgress(plugin) and
///     BreakCommand::isBreakInProgress() (any phase but none). Mirrored in
///     JS/TS (titan.breakHandler.observe / isBreakInProgress) and Java
///     (BreakHandler.observe / isBreakInProgress,
///     BreakCommand.isBreakInProgress()); Java SDK 0.1.69.
///   ~ Appended after previewPillWrite, so plugins built against 135..143
///     keep loading. A plugin built against 144 needs a 144+ client: an
///     older host refuses to load it (createPlugin), and JS/Java callers on
///     an older client find no observe. The ExternalBackend sdkVersion guard
///     is defensive only. kMinSupportedSdkVersion remains 135.
///
/// v143 -- Section parents: settings sections nested inside other sections.
///   + titan::SectionOptions::parent (const Section*) and Section::parent().
///     The controller draws a nested section as a collapsing header inside
///     its parent's, after the parent's own settings, ordered by position
///     among its siblings; a parent with only sub-sections still shows.
///   + TitanNativeRecords::Section::parentIndex: the parent's 1-based position in
///     the same getSections() array, 0 at the top level. Carved from the
///     struct's padding, so sizeof(Section) and every other offset are
///     unchanged; pre-143 plugins zeroed those bytes and read as flat.
///   + TitanNativeRecords::sectionParentIndex() / checkedSectionParent() /
///     sanitizeSectionParents(): a parent outside the list, the section
///     itself, or a loop back to it leaves the section at the top level.
///     On the wire a parent travels by key ("pk") in both the fixed and the
///     records codecs, which stay interchangeable.
///   + Mirrored in JS/TS (SectionOptions.parent taking a Section, and
///     Section.parentKey) and in Java (@ConfigSection(parent = "<keyName>")
///     and Section.parentKey()); Java SDK 0.1.68. The Java scanner and the
///     Java descriptor parser reject an unknown, self or looping parent.
///   ~ JS/TS: Section.isClosedByDefault, declared since the section API
///     landed, is now set at runtime too.
///   ~ Padding carve-out only; kMinSupportedSdkVersion remains 135.
///
/// v142 -- Preview pills: short labels over a tab's Home-grid thumbnail.
///   + HostApi::previewPillWrite with the PreviewPillWrite POD, the
///     PREVIEW_PILL_* flags and the kPreviewPill* limits. Each plugin owns its
///     own pills, which the host stamps from the calling instance; the
///     pluginId argument must agree with it.
///   + A pill is text, a titan::PanelTone and an optional HH:MM:SS countdown
///     that the controller ticks itself, so a running timer is set once.
///     At most 2 per plugin and 8 per tab; text is cut to 63 UTF-8 bytes.
///   + titan::previewPills(plugin) facade and titan::PreviewPill in
///     <titan/preview_pills.h>.
///   + Mirrored in JS/TS (the frozen titan.previewPills facade, taking the
///     registered Plugin object) and in Java (net.titan.api.pills
///     PreviewPills.of(plugin) and PreviewPill); Java SDK 0.1.67.
///   ~ Only an enabled plugin may set a pill. Disabling, unloading or
///     reloading a plugin drops every pill it set.
///   ~ Appended HostApi tail only; kMinSupportedSdkVersion remains 135. The
///     client-internal backend has no plugin identity, so its entry fails
///     closed.
///
/// v141 -- Cross-Tab Store: values shared by every tab one controller launched.
///   + HostApi::crossTabWrite / crossTabRead and PluginApi::onCrossTabChanged,
///     with the CrossTabWrite / CrossTabEntryInfo / CrossTabChangeEvent PODs
///     and the CROSS_TAB_* flags. Each plugin owns one namespace, which the
///     host stamps from the calling instance; the pluginId argument must
///     agree with it. Values live in controller memory for one controller
///     sign-in session and are never written to disk.
///   + put/erase apply locally at once and are sent up until the controller
///     acknowledges them; last writer wins. putIf/eraseIf are proposals
///     against an expected version (0 = absent) and report their outcome.
///     An erase reaches every tab, including tabs that never held the key.
///   + CROSS_TAB_SECRET changes handling, not access: secrets are never
///     logged or persisted, travel only to tabs this controller launched,
///     reach other tabs redacted, and are read back only into zeroing
///     buffers. Other plugins in the same tab are not kept out.
///   + titan::crossTab(plugin) facade, titan::SecureBuffer and
///     titan::Plugin::onCrossTabChanged in <titan/cross_tab.h> / plugin.h.
///   + Mirrored in JS/TS (the frozen titan.crossTab facade, taking the
///     registered Plugin object; bigint versions; getSecret returns an
///     ArrayBuffer zeroed when freed; Plugin.onCrossTabChanged and the
///     titan.CrossTabChange* enums) and in Java (net.titan.api.crosstab
///     CrossTab.of(plugin) and the CrossTabChanged event); Java SDK 0.1.66.
///   ~ Change events run on the game thread for every loaded plugin, enabled
///     or not; the instance gets one Replay per key when it binds.
///   ~ Appended HostApi / PluginApi tails only; kMinSupportedSdkVersion
///     remains 135. The client-internal backend has no plugin identity, so
///     its two entries fail closed.
///
/// v140 -- Plugin-authored setting changes persist like UI edits.
///   + HostApi::markSettingChanged(pluginId, settingKey, value, hidden):
///     the plugin reports that it changed one of its OWN settings. A
///     non-null value is written into plugin_settings.json exactly as a
///     side-panel edit would be, so the change survives a restart; a
///     non-null hidden is presentation-only and just refreshes the panel.
///     Either may be null to mean "this did not change".
///   ~ Both travel WITH the report rather than being read back from the
///     next snapshot. The host serves Java and JS/TS plugins' settings from
///     caches it refreshes on its own schedule -- and never refreshes the
///     Java hidden flag at all -- so reading later would pair a fresh
///     signal with a stale value, and a Java setHidden would never land.
///   ~ Reporting takes no host lock beyond its own: it records the change
///     and bumps the state revision, so a plugin may call it while holding
///     its own lock or from a worker thread.
///   ~ titan::BoolSetting/Int/Color/Combo/String/ProtectedString/Matrix
///     .set()/.reset()/.setCell()/.toggleCell() and setHidden() call it
///     for you -- plugin code is unchanged. Writing the value a setting
///     already holds is a no-op. Host-driven writes arrive through apply()
///     and deliberately do NOT report, so a clamp, a matrix availability
///     mask or a failed DPAPI unwrap can never overwrite a saved value.
///   - titan::IntSetting::set now clamps to [min, max] like apply() always
///     did. A plugin that wrote out of range used to keep the raw value;
///     it would now be persisted, so it is sanitized at the source.
///   + titan::MatrixSetting::toggleCell and ::grid(), matching JS
///     matrixSetting.toggle/toGrid and Java MatrixSetting.toggle/toGrid.
///   ~ Mirrored in JS/TS (setting.value / setting.isHidden / reset) with
///     no API change, and in Java, which gains the writable
///     net.titan.api.config.ConfigManager (set/setString/setEnum/setGrid/
///     setCell/toggleCell/reset/setHidden/isHidden) plus runtime
///     ConfigSetting.setHidden; Java SDK 0.1.65.
///   ~ Appended HostApi tail only; kMinSupportedSdkVersion remains 135.
///
/// Unreleased -- Structural bank PIN detection.
///   ~ Bank::isPinVisible keys off visible keypad children instead of prompt
///     text; pinRequestedDigitIndex sweeps the pad group. No ABI or SDK bump.
///
/// Unreleased -- GE queue without plugin ownership.
///   ~ Shell and SDKs share the queue; plugin disable/unload does not cancel purchases.
///   ~ Existing SDK 139 ABI arguments retained but ignored; Java SDK 0.1.64.
///
/// v139 -- Shared host-driven GE buying utility.
///   + Owner-scoped queue, partial-fill retries, cancellation and collection across all SDKs.
///   + Append-only request callbacks and snapshots; selling is an unsupported stub.
///   ~ Minimum SDK remains 135; Java SDK 0.1.63.
///
/// v138 -- Shared persisted per-character bank memory.
///   + ItemCache snapshots/counts across C++, JS/TS and Java; bank queries use them.
///   + Dev Tools item-cache inspector with freshness and storage status.
///   ~ Banking actions resolve live slots; minimum SDK remains 135.
///
/// v137 -- Shared asynchronous item prices and searchable item metadata.
///   + Append-only requests and owned cache snapshots across C++, JS/TS and Java.
///   + Coalesced bounded requests; exact 64-bit prices; no account data submitted.
///   ~ kMinSupportedSdkVersion remains 135.
///
/// Unreleased -- Dynamic plugin settings counts.
///   ~ Host-owned settings buffers grow through the existing getSettings ABI.
///     The 40-setting limit is removed; no plugin rebuild or SDK bump required.
///
/// v136 -- Grand Exchange snapshots and native offer-change events.
///   + Append-only queries and callbacks across C++, JS/TS and Java.
///   + Owned snapshots; price/spent remain 64-bit (JS bigint, Java long).
///   ~ kMinSupportedSdkVersion remains 135.
///
/// v135 -- Checkbox matrix config control (ABI BREAK -- rebuild every plugin)
///   + titan::MatrixSetting / titan::MatrixRow: a grid of checkboxes with row
///     and column labels and per-cell availability, so one control replaces N
///     repetitive per-row BoolSettings and costs one of the 40 per-plugin
///     settings instead of N. A row names the column labels it has, so the
///     grid's shape is declared by name and a misspelled column is reported
///     rather than silently shifting a cell.
///   + TitanNativeRecords::ControlType::checkboxMatrix = 8. The value is an
///     integer cell bitmask (bit = row * columns + column, bit 31 never set,
///     at most TitanNativeRecords::kMaxMatrixCells = 31 cells).
///   - TitanNativeRecords::Setting gains matrixAvailable / matrixRows /
///     matrixColumns, and kMaxSettingOptions grows 30 -> 32. Plugins write into
///     a host-allocated Setting[] whose stride is sizeof(Setting), so growing
///     the struct is an ABI break: kMinSupportedSdkVersion moves 116 -> 135 and
///     EVERY native plugin must be rebuilt. Java and JS/TS plugins are
///     unaffected -- they never compile against the C ABI. The client refuses
///     a stale plugin with "Rebuild the plugin against the matching SDK
///     checkout" rather than misreading it.
///   ~ kProtocolVersion 31 -> 32: the snapshot payload schema changed. Same
///     class as the bump to 18 (color setting control metadata).
///   ~ Mirrored across the TypeScript SDK (matrixSetting), the QuickJS runtime,
///     and Java (@ConfigItem columns/rows + @MatrixRow on an abstract
///     boolean[][] config method; Java SDK 0.1.59).
///
/// v134 -- Bank search detection and Withdraw-X op index
///   ~ Bank::isSearchOpen() tests Chatbox::MES_TEXT2 visibility instead of
///     scanning widget text for "Enter amount:" / "Enter name:". The text scan
///     matched hidden widgets still holding the previous prompt's text, so the
///     predicate latched true once the first prompt closed, and it walked the
///     whole widget table on every call. Matches the Java reference
///     (BankUtils.isSearchOpen).
///   ~ Bank::withdrawItemAmount() dispatches Withdraw-X as CC_OP op 6, as
///     observed live and as the Java reference sends it. It sent CC_OP_LOW
///     op 6, and for BANK_QUANTITY_TYPE == 3 CC_OP op 1 -- the selected
///     quantity button (Withdraw-lastX), the wrong amount and no prompt.
///   ~ C++, JS/TS and Java surfaces; Java SDK 0.1.58. No HostApi/PluginApi or
///     request layout change; kMinSupportedSdkVersion remains 116.
///
/// v133 -- Bank deposit box helpers
///   + titan::utils::DepositBox (C++ <titan/utils/deposit_box.h>), JS/TS
///     titan.utils.depositBox and Java net.titan.api.utils.DepositBox:
///     isOpen, isDepositAllSelected, close, depositInventory, depositWorn,
///     depositLootingBag, selectDepositAll. Composition over existing widget
///     interact and varbit reads; Java SDK 0.1.56.
///   ~ close() sends the frame's close X component operation (CC_OP op 1 on
///     dynamic child 11 of BankDepositbox::FRAME). Legacy WidgetClose (26) is
///     the native root-owned closure on callback clients and does not close
///     this interface; the MLM and Test Gauntlet plugins now use the helper.
///   ~ No HostApi/PluginApi or request layout change; kMinSupportedSdkVersion
///     remains 116.
///
/// v132 -- Magic cast performs the spell's own Cast option
///   ~ utils::magic cast(spell) now dispatches CC_OP against the catalog's
///     menuEntryId + 1 instead of aliasing select(): a non-targeted spell such
///     as a teleport is a plain widget op, not a WidgetTarget selection.
///     Matches what the web walker's CastSpell dispatch already does.
///   ~ select(spell) keeps the WidgetTarget source selection; callers that
///     relied on cast(spell) selecting must call select(spell).
///   ~ C++, JS/TS and Java surfaces; Java SDK 0.1.54. No HostApi/PluginApi or
///     request layout change; kMinSupportedSdkVersion remains 116.
///
/// v131 -- Full-frame game screenshot for plugins
///   + HostApi::screenshotSubmit/Poll/CopyPng/Release: the /tabs capture (backbuffer after
///     the AboveWidgets pass, PNG) as an in-process submit/poll/copy/release handle.
///   + ScreenshotStatusState + ScreenshotPhase; titan::screenshot() in C++, titan.screenshot
///     in JS/TS, Titan.screenshot() in Java (Java SDK 0.1.53).
///   ~ Appended HostApi tail; kMinSupportedSdkVersion remains 116.
///
/// v130 -- Native player interaction parity
///   + Item::useOn(Player) in C++, JS/TS and Java composes source 25 -> target 14.
///   ~ Callback player ordinary 44..51 and selected 14/15 use proved native builders.
///   ~ Existing Magic player casts keep target 15; Java SDK 0.1.51.
///   ~ No HostApi/PluginApi or request layout change; minimum SDK remains unchanged.
///
/// v129 -- Native ground-item interactions
///   + Item::useOn(GroundItem) across C++, JS/TS and Java queues source 25 -> target 16.
///   + Java InteractionBackend.useInventoryItemOnGroundItem composes the pair; Java SDK 0.1.50.
///   ~ Callback ground targets accept 16/17 (17 canonical), with the unique stack and quantity bound.
///   ~ Cache definitions default Take to slot 3; explicit Hidden clears the corresponding slot.
///   ~ Ground actions match complete labels case-insensitively in their definition slots;
///     Examine uses 1004, and unknown actions or missing loaded definitions are rejected.
///     Before cache loading only the native Take/Examine defaults resolve.
///   ~ kSdkVersion 129; HostApi/entry layouts and kMinSupportedSdkVersion remain unchanged.
///
/// v128 -- Explicit source selection and dependent target action
///   + Nullable HostApi tail operation accepts one owned source/target pair.
///   + Callback clients bind the exact selected source before the dependent target.
///   ~ Existing entry layouts and kMinSupportedSdkVersion remain unchanged.
///
/// v127 -- Actor overhead text
///   + Length-aware nullable Actor text/cycle reads and utterance events across C++, JS/TS, and Java.
///   + Appended ABI callbacks use caller-owned buffers and borrowed pointer/length views.
///
/// v126 -- Native loading game state
///   + `LoginGameStateAbi::LOADING` exposes native `Client.GameState` 25.
///   + C++, JS/TS, and Java `LoginGameState` enums expose the same value for
///     polling and `GameStateChangedEvent` callbacks.
///   ~ Mapping-only public API addition; `kMinSupportedSdkVersion` remains 116.
///
/// v125 -- Native client game cycle
///   + `HostApi::getGameCycle` exposes the analyzer-backed signed 32-bit
///     native game-cycle clock used by graphics-object start cycles.
///   + C++ `ClientFacade::gameCycle()`, JS/TS `state.client.gameCycle`, and
///     Java `Client.gameCycle()` expose the same live value.
///   ~ The game cycle advances nominally every 20 ms (50 Hz); 30 cycles make
///     one 600 ms server tick. It is distinct from `ClientState::tickCount`.
///   ~ Additive `HostApi` tail append; `kMinSupportedSdkVersion` remains 116.
///
/// v124 -- Dynamic widget children beyond 256
///   ~ `kMaxWidgetDynamicChildren` raised 256 -> 2048. It is now purely the
///     per-call export/sizing bound for the SDK children wrappers; the native
///     reader's bound became a separate corruption ceiling (64k), so every
///     real dynamic child is addressable, interactable, and text-settable
///     (`widgetInteractAtPath` / `setWidgetTextAtSlot` no longer reject
///     slots >= 256).
///   ~ `HostApi::getWidgetChildren` gained the `(nullptr, 0)` sizing-probe
///     contract (returns the true child count); non-null calls still return
///     the written count.
///   ~ C++/JS/Java children wrappers switched to count-then-fill; their
///     truncation flags now report honestly at the 2048 bound (the Java
///     bridge previously hardcoded `truncated=false`).
///   ~ Synthetic clicks on dynamic children are constrained to the parent
///     widget's screen bounds: child-parent intersection when overlapping,
///     random-in-parent when the child lies outside (e.g. a bank item
///     scrolled out of view).
///   ~ `kMinSupportedSdkVersion` remains 116 (no struct resized).
///
/// v123 -- Outline convex/concave mode
///   ~ `HostApi::drawEntityOutline` / `drawTileObjectOutline` gained a
///     trailing `uint32_t mode` argument: 0 = convex hull (the prior
///     behaviour), 1 = concave hull (a single non-self-intersecting polygon
///     that hugs the projected vertices and follows concavities). This
///     revises the SDK-122 outline signature, which is safe because SDK-122
///     outlines were unreleased (added in the same dev cycle); no shipped
///     plugin depends on the 4-arg form.
///   + `titan::OutlineMode` enum + `mode` parameter on the `entityOutline` /
///     `tileObjectOutline` facades (default Convex).
///   ~ `kMinSupportedSdkVersion` remains 116.
///
/// v122 -- True model-vertex outlines
///   + `HostApi::drawEntityOutline` / `drawTileObjectOutline`: draw the
///     2D convex hull of an entity's actual projected model vertices --
///     a tight silhouette that follows the real mesh, versus drawEntityHull
///     which hulls the 8 AABB corners. Sourced from the model handle the
///     host's model-AABB hook binds per typecode each frame, and the
///     `Model` vertex arrays emitted by the FindModelGeometry analyzer pass.
///   + `titan::overlay()` facades `entityOutline` / `tileObjectOutline`
///     (typed + raw-pointer overloads).
///   ~ Additive HostApi tail append; existing fn-pointer offsets unchanged,
///     so plugins built against SDK 116..121 keep loading. The facades
///     no-op on hosts older than 122 (null pointer) and on revisions
///     without Model geometry. `kMinSupportedSdkVersion` remains 116.
///
/// v121 -- Typecode-only clickbox picking cache (behavioural)
///   ~ The host's world-keyed picking-cache fallback was removed: the
///     typecode-keyed cache is now the ONLY picking evidence behind
///     drawEntityClickbox / drawTileObjectClickbox / drawEntityHull /
///     drawTileObjectHull. It could return a same-tile neighbour's box
///     (including actor/loc cross-matches); a missing box is better
///     than a wrong one. Consequences: `typecode = 0` (or a cache miss)
///     now degrades actors to the synthesized 1-tile footprint and
///     draws nothing for tile objects, instead of falling back to
///     world-keyed matching. This also applies to click-point sampling
///     for synthetic actions (degraded to the tile footprint when the
///     typecode cannot be resolved). The `titan::overlay()` facades
///     compute typecodes automatically, so plugins using typed
///     overloads are unaffected.
///   ~ No ABI/struct/signature changes -- function-pointer table is
///     identical, so this is a behavioural bump only. `kSdkVersion`
///     121; `kMinSupportedSdkVersion` unchanged (116).
///
/// v120 -- Cheap live-state freshness epoch
///   + `HostApi::getLiveStateEpoch`: returns a monotonic epoch equal to
///     `ClientState::tickCount` at every observable point, published atomically
///     once per frame. Entity wrappers read it (one atomic load) on each
///     accessor to decide whether their cached state is still current, instead
///     of building a full ClientState via getClientState just to read tickCount.
///   ~ Additive HostApi tail append; kMinSupportedSdkVersion remains 116. The
///     SDK falls back to getClientState when the pointer is null (old host), so
///     behaviour is unchanged -- purely a per-accessor cost reduction.
///
/// v119 -- Account Profiles credential staging + generic login submit
///   + `HostApi::stageLoginCredentials`: resolve an exact Account Profiles
///     label and queue its standard or Jagex credentials without exposing
///     secrets to the caller.
///   + `HostApi::submitLoginCredentials`: hold Enter across one MainLoop
///     update on either supported credential screen, then release it.
///   ~ Additive HostApi tail append; kMinSupportedSdkVersion remains 116.
///
/// v118 -- Main-loop event across the SDKs
///   + `PluginApi::onMainLoop`: no-argument callback fired from the existing
///     outer MAIN_LOOP detour in every client state, including title/login
///     screens and gameplay. The callback runs before the MainLoop dispatcher
///     drain and native MAIN_LOOP body. Live gameplay queries require a
///     world-ready state; static definition-cache reads remain available.
///   ~ Additive PluginApi tail append; kMinSupportedSdkVersion remains 116 so
///     SDK 116/117 native plugins continue to load with a null callback.
///
/// v117 -- WorldPoint::isInScene across the SDKs
///   + `titan::WorldPos::isInScene()` (C++), `WorldPoint.isInScene()` (Java)
///     and `titan.worldPoint.isInScene(point)` (JS): true when a world tile
///     lies inside the scene the client currently has loaded. Each also
///     takes an explicit base/size overload for callers holding a snapshot.
///   ~ Header-only/API-shape addition over the existing getClientState
///     call; no HostApi table change, but kSdkVersion 117 so plugins can
///     feature-detect it.
///
/// v116 -- Drop-safe frame schedulers (breaking: rebuild required)
///   ~ HostApi::runOnClientTick / runOnRender gained a third parameter,
///     void (*cleanup)(void* userData). The host invokes cleanup exactly
///     once per request: after the callback ran, or when the queued request
///     is dropped without running (queue-cap eviction, shutdown/reload
///     clearAll). Callbacks must no longer free userData themselves.
///     In-place fn-pointer signature change (table layout unchanged);
///     kMinSupportedSdkVersion raised to 116 so pre-116 binaries are
///     rejected at load instead of calling through a mismatched signature.
///
/// v115 -- Generic mouse-input events (onMousePressed / onMouseReleased)
///   + MouseButtonEvent and PluginApi::onMousePressed/onMouseReleased: the
///     client delivers real (non-synthetic) mouse button press/release events
///     to plugins carrying client-area x/y, the button (Left/Right/Middle),
///     and the KeyboardMods bitmask. A plugin sets event->consumed = 1 to
///     suppress the native click; the host then force-consumes the paired
///     button-up so the game's button state stays balanced. Double-click
///     messages arrive as an extra press. These fire on the input/pump
///     thread (NOT the game thread) -- copy out the event fields and defer
///     any titan::* game-state/action calls to a game-thread callback such
///     as onClientTick.
///
/// v114 -- Web walk executor + per-step action payload access
///   + WebWalkRequestState / WebWalkStatusState / WebWalkPhase / WebWalkFlag
///     and HostApi::webWalkStart/Status/Cancel/Release: the client follows a
///     generated route in-game (walking, with transports/teleports arriving
///     in later milestones) until arrival, failure, or cancel. One session
///     per client; a new start supersedes the previous one.
///   + HostApi::webWalkAdvance for WebWalkFlag::ManualTick sessions, letting
///     a plugin drive the follower one decision per game tick itself.
///   + HostApi::webPathCopyStepPayload: the UTF-8 JSON action payload of one
///     step of a completed internal web path request, previously
///     deliberately withheld from the ABI, for plugins building custom
///     executors. Size-query convention via outRequired.
///
/// v113 -- Read-only native world-map display state
///   + Append a fixed-record WorldMapState snapshot and
///     HostApi::getWorldMapState. The host publishes the viewport, global
///     display centre, current/target zoom, and validated pixels-per-tile
///     scale atomically, or fails closed when the generated analyzer contract
///     is incomplete or the map is not loaded and visible.
///   + Add the C++, JavaScript/TypeScript, and Java world-map facades with
///     RuneLite-compatible world/screen projection helpers.
///   ~ Runtime semantics are empirically validated only on Windows OSRS
///     revision 240.1. Unsupported/partial bundles omit the capability; no
///     older- or cross-revision runtime validation is claimed.
///   ~ Additive HostApi change; kMinSupportedSdkVersion remains 111.
///
/// v112 -- Web-walker path generation and bulk collision snapshots
///   + Append fixed-record web-path submit/poll/copy/cancel/release operations
///     to HostApi. Route steps retain stable edge ids and both endpoints'
///     route-space/copy identities while action descriptors remain owned by
///     the selected provider.
///   + Append bulk cached-region and immutable live-scene collision snapshot
///     operations so a DEV provider can consume the same inputs as the built-in
///     engine without private client headers.
///   + Add the C++, JavaScript/TypeScript, and Java `webWalker` facades.
///   ~ Route costs are deterministic integer points in every layer. Legacy
///     authored cost 1 is 10 points and one normal walked tile is 5 points.
///   ~ Native currentScene(expectedWidth, expectedHeight) uses matching client
///     snapshot dimensions for one exact HostApi copy pass; currentScene()
///     retains the general two-pass form.
///   ~ Additive HostApi change; kMinSupportedSdkVersion remains 111.
///
/// v111 -- Expanded item containers
///   ~ ItemContainerState and ItemContainerChangedEvent fixed slot arrays grow
///     from 1024 to 2048 entries so current maximum-size banks are represented
///     in full across the native plugin ABI.
///   - The fixed-buffer resize is an ABI break and raises
///     kMinSupportedSdkVersion to 111. Native plugins must rebuild.
///
/// v110 -- Widget sprite id SDK parity
///   + WidgetState appends the primary native SpriteId and exposes it through
///     C++, JavaScript/TypeScript, and Java 0.1.36 widget handles/snapshots.
///   - WidgetState grows, so kMinSupportedSdkVersion is raised to 110 and
///     native plugins must rebuild.
///
/// v109 -- Interface-scale accessor for UI-frame -> window geometry
///   + Append getInterfaceScale to HostApi so plugins can read the live
///     OSRS in-game interface-scale factor (and canvas pillarbox origin)
///     the host derives from the canvas coordinate transform. Lets plugins
///     that draw their own local pixel geometry (bar widths, offsets)
///     scale it to match widget-frame overlays at 150%/200% interface
///     scaling. Returns identity (1.0) when the analyzer did not detect
///     the canvas transform on the bound revision.
///   ~ Additive HostApi change; kMinSupportedSdkVersion remains 107.
///     Mirrored in the C++ CameraFacade, JavaScript/TypeScript, and Java.
///
/// v108 -- WorldView-aware tile-object live refresh
///   + Append getTileObjectsOnTileInWorldView to HostApi so live tile-object
///     handles re-resolve against their captured WorldView rather than the
///     current scene.
///   ~ Per-tile tile-object results preserve the requested plane and live
///     refresh rejects plane/WorldView identity drift.
///   ~ Additive HostApi change; kMinSupportedSdkVersion remains 107.
///
/// v107 -- Live runtime ItemDef sub-operations
///   + ItemCompositionState appends the fixed 5 x 20 opcode-43 subOps label
///     matrix populated from the live ITEM_DEF_LOOKUP result; cache fallback
///     preserves the same shape.
///   + Runtime ItemComposition exposes subOps in C++, JavaScript/TypeScript,
///     and Java 0.1.33.
///   ~ Existing inventory Item::interact(action) calls resolve ordinary
///     actions first, then live opcode-43 sub-operations. This is a host-side
///     behavior change with no additional HostApi entry.
///   - The fixed ItemCompositionState growth is an ABI break and raises
///     kMinSupportedSdkVersion to 107.
///
/// v106 -- Versioned cache-definition snapshots and item sub-operations
///   - ItemDefSnapshot, NpcDefSnapshot, ObjDefSnapshot, and VarbitDefSnapshot
///     now begin with structSize/apiVersion. This is an ABI break and raises
///     kMinSupportedSdkVersion to 106.
///   + ItemDefSnapshot exposes the raw fixed 5 x 20 opcode-43 sub-operation
///     label matrix. Mirrored by C++, JavaScript/TypeScript, and Java 0.1.32.
///   ~ HostApi ordering and all item note fields/semantics are unchanged.
///
/// v105 -- Live cross-tick object/ground-item/widget handles (JS + C++ parity)
///   ~ JS scene objects + ground items are now live cross-tick handles (re-
///     resolve per tick) instead of frozen snapshots, with new `.exists` and
///     `.snapshot()`. Matches the Java SDK, which already shipped this.
///   ~ C++ `TileObject` / `GroundItem` made live like `Player`/`Npc` (per-tick
///     `live()` re-resolve, `exists()`, `snapshot()`). Header-only, no ABI change.
///   ~ JS widget live-sync fix: cached widgets now re-resolve across ticks and
///     expose `.exists` (previously a no-op; Java widgets were already live).
///   ~ Fail-soft native interact: a despawned object's `.interact()` is a safe
///     no-op (entityPtr re-resolved at call time). Benefits all runtimes.
///   + JS `getTileObjectsOnTile` / `getGroundItemsOnTile` bindings over the
///     existing HostApi pointers. No HostApi table change;
///     kMinSupportedSdkVersion unchanged -- existing plugins load with no rebuild.
///
/// v104 -- Panel builder polish: combo, disabled scope, help marker, badge
///   + `titan::Panel::combo(label, actionId, items, selectedIndex)` exposes the
///     existing combo element via the fluent SDK (items ride the wire as a
///     newline-separated list; the controller renders a full-width dropdown
///     with the label above). Mirrored in JS/TS and Java.
///   + `Panel::beginDisabled(disabled=true)` / `endDisabled()` wrap a run of
///     controls in an ImGui disabled scope (greyed + non-interactive).
///   + `Panel::help(text)` adds an inline "(?)" tooltip marker; `Panel::badge(
///     text, tone)` adds a compact colored status pill.
///   ~ Renderer fix: `sliderInt` / `sliderFloat` / `combo` now draw the label
///     above a full-width control so long labels no longer clip off-panel.
///   + New `PanelElementType` values 113..116; additive and ignored by older
///     controllers. Composition-only; kMinSupportedSdkVersion unchanged.
///
/// v103 -- NPC overhead icon runtime-override fix
///   ~ Npc::overheadIcon() now returns the effective slot-0 HeadIcon ordinal:
///     runtime per-instance overrides (ClientNpc::SetHeadIcon, e.g. bosses
///     swapping protection prayers) take precedence over NpcType cache
///     defaults, and both resolve to the sprite frame-index (the HeadIcon
///     ordinal) rather than the sprite-archive id. Previously override icons
///     were undetectable (icon value not decoded) and cache defaults returned
///     the archive id. hasHeadIconOverride() is unchanged.
///   ~ Behavioural fix only; no ABI/struct/table change.
///     kMinSupportedSdkVersion unchanged.
///
/// v102 -- Prayer action utility
///   + New header-only `titan::utils::Prayers` helpers expose prayer active
///     reads plus raw toggle and idempotent set/enable/disable actions for all
///     55 standard and Ruinous Powers entries.
///   + Mirrored in Java 0.1.30 and JavaScript/TypeScript.
///   ~ Composition-only addition; kMinSupportedSdkVersion unchanged.
///
/// v101 -- NPC combat/exclusion and widget text query filters
///   + NpcQuery adds definition-backed combatLevelAbove/Below/Between filters
///     and exact-identity exclude(Npc) / exclude({...}) filters.
///   + WidgetQuery::textContains({...}) matches any supplied text.
///     Mirrored in Java 0.1.29 and JavaScript/TypeScript.
///   ~ Composition-only addition; kMinSupportedSdkVersion unchanged.
///
/// v100 -- World-area and inventory query filters
///   + LocatableQuery::within(WorldArea) keeps entities whose absolute world
///     point is contained by the area, including plane and WorldView checks.
///   + InventoryQuery::hasAction(action) filters on runtime inventory actions;
///     InventoryQuery::isNoted() filters on the cache definition's noted flag.
///     Mirrored in Java 0.1.28 and JavaScript/TypeScript.
///   ~ Composition-only addition; kMinSupportedSdkVersion unchanged.
///
/// v99 -- Multi-action NPC and tile-object query filters
///   + NpcQuery::hasAction({...}) and ObjectQuery::hasAction({...}) keep
///     entities exposing any supplied action. Java and JavaScript/TypeScript
///     mirror the behavior with varargs (Java SDK 0.1.27).
///   ~ Single-action hasAction(action) behavior remains source-compatible.
///     Composition-only addition; kMinSupportedSdkVersion unchanged.
///
/// v98 -- Config button setting
///   + titan::ButtonSetting: a value-less action config item. Clicking it in
///     the controller invokes the attached std::function onClick instead of
///     storing a value. Mirrored across the TypeScript SDK (buttonSetting),
///     QuickJS runtime, and Java (@ConfigButton default void). onClick runs on
///     the game thread (Phase::MainLoop, including the login screen).
///   + TitanNativeRecords::ControlType::button (additive enum value; no wire
///     struct or Command change).
///   ~ Additive only; kMinSupportedSdkVersion unchanged. Old controllers that
///     don't know the button control fall through the render default and
///     simply don't show it.
///
/// v97 -- Break Handler, owned services, profile/proxy activation
///   + Adds versioned POD break registration / command / report / participant
///     records and appends instance-addressed Break Handler participant plus
///     coordinator operations to HostApi. Native instance pointers are used
///     only to validate the caller and are never retained by the host.
///   + Adds exact-instance/load-generation-owned service publication for the
///     load-lifetime account_profiles POD service.
///   + Adds sanitized proxy route list/set/status and MainLoop-safe Standard,
///     launcher Enter, and click-to-play login operations used by Profiles. No
///     profile or proxy secret is returned through the HostApi.
///   + titan::Panel gains beginTabBar/endTabBar/beginTabItem/endTabItem
///     (tab protocol element types already rendered by the controller).
///     Mirrored in the TypeScript SDK, QuickJS runtime, and Java Panel API.
///   ~ Additive only (panel-tab helpers add no HostApi/PluginApi table change);
///     kMinSupportedSdkVersion unchanged.
///
/// v96 -- Panel layout containers + alignment (SOCKS5 proxy UI groundwork)
///   + titan::Panel gains beginGroup/endGroup, beginChild/endChild,
///     beginCard/endCard (framed auto-height cards via PanelChildFlag), and
///     beginAlign/endAlign (center/right-align a button run). Mirrored in the
///     TypeScript SDK, QuickJS runtime, and Java Panel API.
///   + PanelElementType gains proxyCombo (first-party, controller-rendered),
///     alignBegin/alignEnd; PanelChildFlag flags added.
///   ~ Additive only (new wire element types + panel-builder helpers; no
///     HostApi/PluginApi table change), kMinSupportedSdkVersion unchanged.
///
/// v95 -- Unbounded plugin enumeration
///   + HostApi::getPluginCount (appended at end of HostApi) lets the plugin
///     manager facade size its enumeration buffer dynamically instead of a
///     fixed 128 cap.
///   ~ Additive only (new fn pointer at the end); kSdkVersion 95,
///     kMinSupportedSdkVersion unchanged so plugins built against 91..94
///     keep loading without a rebuild.
///
/// v94 -- Public mouse click-point resolver utility
///   + HostApi::resolveActionClickPoint exposes the shared target-aware
///     click resolver to native plugins without dispatching the action.
///   + C++ / JS / TS / Java move the public resolver surface to
///     utils.mouse / titan::utils::Mouse instead of Client.
///   ~ kSdkVersion 94; kMinSupportedSdkVersion unchanged.
///
/// v93 -- Target-aware synthetic click resolution
///   + SyntheticActionEntry appends optional target metadata so callers that
///     already hold a Player/NPC/TileObject/GroundItem snapshot can pass the
///     native entity pointer, footprint, layer, and packed loc tag into click
///     resolution instead of forcing the host to rediscover the target from a
///     raw menu tuple.
///   ~ C++ / JS / TS / Java magic target casts feed target metadata into the
///     shared native click-point resolver.
///   ~ kSdkVersion 93; kMinSupportedSdkVersion unchanged.
///
/// v92 -- Player equipment composition
///   + HostApi::getPlayerComposition exposes raw PlayerModel appearance
///     equipment slots with item-id normalization metadata.
///   + C++ / JS / TS / Java player wrappers expose getPlayerComposition().
///   ~ kSdkVersion 92; kMinSupportedSdkVersion unchanged.
///
/// v91 -- Native game-state event
///   + LoginGameStateAbi values now match native Client.GameState values.
///   + PluginApi::onGameStateChanged and GameStateChangedEvent expose native
///     SetGameState transitions.
///   - kSdkVersion 91; kMinSupportedSdkVersion raised to 91 because the
///     public game-state enum changed numeric semantics.
///
/// v90 -- Projectile coordinate names
///   ~ ProjectileState uses startY/targetY for horizontal map Y, height for
///     vertical position, and sceneY for precise horizontal scene Y.
///   ~ GraphicsObjectState uses preciseY/sceneY for horizontal map Y.
///   ~ C++ / JS / TS / Java projectile and graphics object wrappers expose
///     the corrected names.
///   ~ JS / TS titan.Skill adds PascalCase aliases such as Cooking, and
///     titan.state.skills.* rejects invalid skill args instead of Attack.
///   ~ kSdkVersion 90; kMinSupportedSdkVersion unchanged.
///
/// v89 -- Resolver-backed live handles
///   + HostApi appends direct identity resolvers for players, NPCs, and
///     retained widget paths so Java / JS / C++ live handles read through the
///     same internal C++ object lookup path instead of list-scan snapshots.
///   + HostApi appends a WorldView-aware actor path queue resolver so live
///     actor handles preserve non-current WorldView coordinates.
///   ~ kSdkVersion 89; kMinSupportedSdkVersion unchanged.
///
/// v88 -- Live state handles
///   + Java / JS / TS mutable client-backed state handles are interned by
///     stable runtime identity and observe current state on access; Java handles expose
///     exists(), snapshot(), and identity-based equals/hashCode.
///   + C++ Player/Npc/Actor/Widget wrappers are live handles and expose
///     exists(), snapshot(), equality, and std::hash identity support where
///     applicable.
///   ~ Java typed bridge carries WorldView identity for actors, tile objects,
///     and ground items.
///   ~ kSdkVersion 88; kMinSupportedSdkVersion unchanged.
///
/// v87 -- Native gameval catalogs
///   + Generated native gameval constants live under `titan::gamevals::*`
///     with RuneLite-style class names such as ItemID, ObjectID, NpcID,
///     VarbitID, and QuestID.
///   + JS / TS expose the same catalogs as `titan.gamevals.*`; Java exposes
///     `net.titan.gamevals.*` classes for plugin autocomplete.
///   ~ Header-only / generated SDK surface plus JS bootstrap wiring;
///     kSdkVersion 87; kMinSupportedSdkVersion unchanged.
///
/// v86 -- Melee distance helpers
///   + C++ / JS / Java expose RuneLite-style orthogonal-only
///     isInMeleeDistance helpers on WorldPoint, WorldArea, and locatable
///     entity surfaces.
///   ~ JS / TS expose instance checks on state.client.isInInstance()
///     for parity with C++ / Java client facades.
///   ~ kSdkVersion 86; kMinSupportedSdkVersion unchanged.
///
/// v85 -- WorldView-aware locatables
///   + WorldPointState, LocalPointState, and locatable entity states carry
///     WorldView identity so coordinates do not lose their source view.
///   + General entity getters enumerate active WorldViews by default.
///   - kSdkVersion 85; kMinSupportedSdkVersion raised to 85 because
///     core entity/coordinate structs grew and native plugins must rebuild.
///
/// v84 -- WorldView-addressed overlay projection
///   + ClientState exposes current WorldView id plus top-level base/plane/scene
///     dimensions so plugins can convert absolute world tiles for top-level
///     source overlays.
///   + HostApi appends explicit WorldView-addressed worldToScreen, tile height,
///     tile quad/region, and text draw calls.
///   + C++ / JS facades expose WorldView::TOP_LEVEL / CURRENT constants and
///     InWorldView overlay helpers.
///   ~ kSdkVersion 84; kMinSupportedSdkVersion unchanged.
///
/// v83 -- SLR-backed world metadata
///   + WorldMetadataState plus HostApi::getWorldMetadata /
///     refreshWorldMetadata expose Jagex SLR world id, host, activity,
///     location, population, region code, and background TCP ping cache.
///   + C++ / JS / Java expose additive world metadata helpers while the
///     legacy native GameWorld list remains unchanged.
///   ~ TitanNativeRecords::kMaxPanelElements raised to 8192 for dense tables.
///   ~ kSdkVersion 83; kMinSupportedSdkVersion unchanged.
///
/// v82 -- Client walking destination
///   + LocalPointState plus HostApi getLocalDestinationLocation and
///     getWorldDestinationLocation expose the minimap red-flag walking
///     destination.
///   + C++ / JS / Java Client APIs expose local/world destination accessors.
///   ~ kSdkVersion 82; kMinSupportedSdkVersion unchanged.
///
/// v81 -- WorldView-aware reads and instance templates
///   + HostApi WorldView lookup helpers expose active, id-addressed, and
///     top-level/default WorldView pointers.
///   + InstanceTemplateChunksState plus HostApi template chunk and
///     bidirectional WorldPoint instance conversion helpers.
///   + C++ / JS / Java expose conversion on WorldPoint/Locatable; Java Client
///     also exposes direct bridge methods.
///   ~ kSdkVersion 81; kMinSupportedSdkVersion unchanged.
///
/// v80 -- Side-panel icon library and tinting (BREAKING)
///   + PanelDescriptor::icon now accepts consistent icon specs:
///     `awesome:gear`, `lucide:house`, `phosphor:gear:bold`, or a legacy
///     Font Awesome glyph. Unprefixed strings remain Font Awesome-compatible.
///   + PanelDescriptor::iconColor tints all non-image side-panel nav icons
///     (Font Awesome, Lucide, Phosphor, and letter fallback) as ARGB
///     0xAARRGGBB. Custom image icons still take precedence and are not tinted.
///   + C++ SidePanel::iconColor, JS/TS PanelDef.iconColor, and Java
///     @SidePanel(iconColor=...) expose the same setting.
///   - kSdkVersion 80; kMinSupportedSdkVersion raised to 80 because
///     TitanNativeRecords::PanelDescriptor layout changed.
///
/// v79 -- Consolidated developer tools (BREAKING)
///   + HostApi::setInternalToolVisible / getInternalToolVisible replace the
///     per-inspector debug-window toggles.
///   - Removed HostApi::setCacheExplorerVisible, setChatInspectorVisible,
///     setCs2InspectorVisible, setPacketInspectorVisible,
///     setVarbitInspectorVisible, setItemContainerExplorerVisible,
///     setWorldHopperVisible, setActionInspectorVisible,
///     setSoundEffectInspectorVisible, setHitsplatInspectorVisible, and
///     setActorSpotAnimInspectorVisible.
///   - kSdkVersion 79; kMinSupportedSdkVersion raised to 79 because HostApi
///     layout changed.
///
/// v78 -- Actor animation changed event
///   + PluginApi::onAnimationChanged + AnimationChangedEvent expose accepted
///     Actor::Animation field changes from the native ACTOR_ANIMATION setter.
///   + Client hook support for ACTOR_ANIMATION / SetAnimation dispatches only
///     when the post-original actor animation field differs from the pre-call
///     value, including changes to or from -1.
///   ~ kSdkVersion 78; kMinSupportedSdkVersion unchanged.
///
/// v77 -- Projectile actor references (BREAKING)
///   + ProjectileState::sourceEntity and ::targetEntity now carry decoded
///     actor hash indexes, or -1 when no actor is targeted.
///   + ProjectileState now carries rawSourceEntity/rawTargetEntity and
///     sourceEntityType/targetEntityType for packed-value debugging and
///     type-safe actor resolution.
///   + C++ / JS / Java projectile wrappers expose sourceActor/targetActor,
///     and projectile queries expose targetingActor/fromActor.
///   - kSdkVersion 77; kMinSupportedSdkVersion raised to 77 because
///     ProjectileState semantics/layout changed.
///
/// v76 -- Hitsplat/spotanim split
///   + Corrected HITSPLAT_ADDER signature to `(actor,type,value,delay,cycle,limit)`.
///   + Added SPOTANIM_ADDER hook/event as `onActorSpotAnim`, with clear/removal
///     ids filtered separately from hitsplats.
///   + Added ActorSpotAnimState + HostApi::getActorSpotAnims for reading
///     current actor-attached spot animations from analyzer layouts.
///   + HostApi::setActorSpotAnimInspectorVisible + in-client Actor SpotAnim Inspector
///     for the new spotanim event stream.
///   ~ kSdkVersion 76; kMinSupportedSdkVersion unchanged.
///
/// v75 -- Hitsplat Inspector
///   + HostApi::setHitsplatInspectorVisible + in-client `HitsplatInspector`
///     debug UI (live log / per-type frequency view) fed by resolved
///     onHitsplatApplied events, toggled by the `hitsplat_inspector` plugin.
///   ~ kSdkVersion 75; kMinSupportedSdkVersion unchanged.
///
/// v74 -- Hitsplat applied event
///   + PluginApi::onHitsplatApplied + HitsplatAppliedEvent struct. Events
///     resolve the native actor pointer into an embedded PlayerState/NpcState
///     snapshot when possible; JS/Java expose that as actor/null.
///   + Client-side hook support for analyzer HITSPLAT_ADDER. Missing or zero
///     offsets disable cleanly; clear/removal writes are filtered.
///   ~ kSdkVersion 74; kMinSupportedSdkVersion unchanged.
///
/// v73 -- Actor pose and stationarity exposure
///   + PlayerState and NpcState now carry movementPose and idlePose, the
///     raw actor pose ids previously surfaced internally under
///     state-oriented names.
///   + NpcState now carries stationary, and C++ / JS / Java NPC wrappers
///     expose isStationary the same way players do.
///   ~ Offset bundle keys remain MovementState / IdleState for this revision.
///   - kSdkVersion 73; kMinSupportedSdkVersion raised to 73 because
///     PlayerState and NpcState grew and native plugins must rebuild.
///
/// v72 -- Polished side-panel helpers
///   + C++ / JS / Java panel builders now expose semantic section/status,
///     button-style, password/multiline input, and paired collapsible helpers.
///   ~ Helpers encode into existing PanelElement fields; no PluginApi,
///     HostApi, struct-layout, or IPC protocol changes.
///   ~ kSdkVersion 72; kMinSupportedSdkVersion unchanged.
///
/// v71 -- Tile object dynamic animation id
///   + TileObjectState now carries `animation`, the active dynamic scenery
///     sequence id for scenery/gameobject locs, or -1 when absent/unreadable.
///   + C++ / JS / Java TileObject wrappers expose animation/getAnimation.
///   - kSdkVersion 71; kMinSupportedSdkVersion raised to 71 because
///     TileObjectState grew and native plugins must rebuild.
///
/// v70 -- Tile object scene typecode + orientation
///   + TileObjectState now carries the raw 1-byte scene typecode plus the
///     derived scene object type and orientation. The packed ID path stays
///     unchanged.
///   + C++ / JS / Java tile-object wrappers expose sceneTypecode,
///     sceneObjectType / shape, and orientation accessors.
///   + Analyzer output now includes EntityTypecode alongside EntityPackedId
///     and annotates the TypeCode2 helper for audit/debug.
///   - kSdkVersion 70; kMinSupportedSdkVersion raised to 70 because
///     TileObjectState grew and native plugins must rebuild.
///
/// v69 -- Sound event + audio playback toggle
///   + PluginApi::onSoundPlayed + SoundPlayedEvent struct (kind = synth /
///     jingle). Fires when the native client plays a queued synth sound effect
///     (captured at the queue drain) or a MIDI jingle (captured at PlayJingle).
///     Plugins set event->consumed = 1 to mark the event handled for handler
///     ordering; current playback suppression is global-only via the audio
///     toggle.
///   + HostApi::setAudioPlaybackDisabled / getAudioPlaybackDisabled -- global
///     toggle that suppresses all discrete sound-effect playback (and the
///     downstream definition lookup / scheduler allocation) at the hook.
///   + Fluent `titan::state::audio()` facade, JS `titan.audio`, Java
///     Client.setAudioPlaybackDisabled / audioPlaybackDisabled.
///   + HostApi::setSoundEffectInspectorVisible + in-client `SoundEffectInspector`
///     debug UI (live log / frequency view / mute toggle) fed by the
///     sound-effect hook, toggled by the `sound_effect_inspector` plugin.
///   ~ Additive only (new struct, appended PluginApi/HostApi fn pointers);
///     kSdkVersion 69, kMinSupportedSdkVersion unchanged so older plugins
///     keep loading.
///
/// v68 -- GraphicsObject animation sequence state (BREAKING)
///   + GraphicsObjectState now carries the active sequence pointer, sequence
///     id, current frame, frame cycle, loop count, total cycle, and a
///     SequenceState snapshot for PvM dodge timing.
///   + C++/Java/JS SDKs expose GraphicsObject animation accessors. The legacy
///     seqTypePtr name remains as an alias of seqPtr.
///   - `kSdkVersion` 68; `kMinSupportedSdkVersion` raised to 68 because
///     GraphicsObjectState grew and native plugins must rebuild.
///
/// v67 -- Unified ARGB colour convention (behavioural)
///   ~ Every plugin-facing colour is now plain ARGB (0xAARRGGBB) across the
///     whole API: native C++, JS, and Java all pass ARGB. The host performs
///     the single ARGB->ImGui-ABGR red/blue swap at the overlay/panel root
///     (client `hostDraw*` / `hostOverlayPanel*`, controller panel renderer),
///     so plugins never swap channels themselves. This matches what the docs
///     already described for panels and fixes the entity hull/clickbox docs
///     that previously said "ABGR".
///   ~ No ABI/struct/signature changes -- function-pointer table is identical,
///     so this is a behavioural bump only. `kSdkVersion` 67;
///     `kMinSupportedSdkVersion` unchanged (65). Plugins that previously
///     pre-swapped colours to ABGR must now author plain ARGB.
///
/// v66 -- Plugin dependency graph + cross-plugin service registry
///   + PluginApi::getDependencies enumerates the plugin ids this plugin
///     depends on (RuneLite-style). The host loads dependencies first and
///     rejects cycles / missing deps. Append-only PluginApi addition.
///   + C++ TITAN_PLUGIN_DEPS(TypeA, TypeB) (type-based, via each plugin's
///     static kPluginId) and TITAN_PLUGIN_DEP_IDS("a","b") (string escape
///     hatch); Java @PluginDescriptor.dependencies()/dependencyIds().
///   + HostApi::registerPluginService / getPluginService -- a string-keyed
///     opaque service registry so a dependency can publish an interface that
///     dependents resolve and call. Fluent: titan::registerService /
///     titan::service. Append-only HostApi addition.
///   + Multi-plugin DLLs: TITAN_REGISTER_PLUGINS(A, B, ...) emits
///     TitanGetPluginCount, TitanCreatePluginAt and TitanPluginIdList
///     (newline-joined id string, read statically by the store uploader).
///     TITAN_REGISTER_PLUGIN now emits them too (count == 1) so the host has a
///     single enumeration path.
///   ~ detail::self() refreshes the per-DLL current-plugin ambient on every
///     dispatch so titan::plugins().self() is correct in a multi-plugin DLL.
///   ~ `kSdkVersion` 66; `kMinSupportedSdkVersion` unchanged (65). Older
///     plugins load unchanged and report no dependencies.
///
/// v65 -- Multiple side panels per plugin + image icons (BREAKING)
///   + A plugin may now expose several side panels, each with its own id,
///     title, and optional icon. PluginApi::getPanels enumerates them;
///     getPanelElements / onPanelAction now take a `panelId` selector.
///   + PluginApi::getPanelIcon streams a custom PNG image icon per panel
///     (RuneLite-style nav icons), decoded by the controller via WIC.
///   + C++ titan::Plugin::panel("id","Title", build).onAction(..).icon(..)
///     .image(..); JS/TS `panels: PanelDef[]`; Java @SidePanel annotations +
///     Plugin.buildPanel/onPanelAction with net.titan.api.panel.Panel. All
///     four surfaces reach full parity.
///   - Removed singular PluginApi::getHasPanel/getPanelTitle and the
///     panelId-less getPanelElements/onPanelAction. TITAN_PANEL macro and the
///     virtual buildPanel/onPanelAction/hasPanel/panelTitle hooks are gone.
///   ~ Wire protocol bumped to 21 (Plugin carries PanelDescriptor[],
///     Message gains panelId, Command::getPanelIcon added).
///   - `kSdkVersion` 65; `kMinSupportedSdkVersion` raised to 65 (PluginApi
///     table reorder/resize -- native plugins must rebuild).
///
/// v64 -- Slot-aware recursive widget queries
///   + HostApi::getWidgets / getWidgetChildrenAtPath enumerate loaded widgets
///     and direct dynamic children with retained root-plus-slot addresses.
///   + HostApi::setWidgetTextAtPath / widgetInteractAtPath resolve retained
///     dynamic paths at dispatch time so stale or null paths fail closed.
///   + C++/JS/TS titan::queries::widgets([groupId]) with text, visibility,
///     id, type, content-type, item-id filters, chainable children(), and
///     direct-child slot(index) traversal.
///   ~ Widget text mutation uses analyzer-backed EASTL range assignment when
///     available, supporting inline/heap transitions and text up to 256 UTF-8
///     bytes. Older offset bundles retain the 22-byte inline fallback.
///   ~ Menu-entry APIs consistently expose `identifier` for the DoAction
///     identity field.
///   ~ Widget Explorer text filtering reuses the SDK's bounded loaded-widget
///     traversal. `kSdkVersion` 64; `kMinSupportedSdkVersion` unchanged.
///
/// v63 -- Slot-addressed dynamic widget text writes
///   + HostApi::setWidgetTextAtSlot(parentPackedId, slot, text) for exact
///     dynamic-child text mutation without ambiguous packed-id lookup.
///   + C++/JS/TS widget helpers retain dynamic parent/slot context for
///     children(parentPackedId) snapshots, enabling child.setText(text) and
///     child.interact(opcode, identifier) convenience calls.
///   + `VarPlayerID::nameOf(id)` plus its JS/TS mirror annotate named varp ids.
///   ~ Dynamic-child enumeration preserves null placeholders so native slot
///     indexes cannot drift. Ordinary widget snapshots retain packed-id
///     setter and explicit-interaction behavior.
///   ~ Var Inspector adds a read-only VarPlayers tab with raw-id lookup and
///     live named-catalog values.
///   ~ `kSdkVersion` 63; `kMinSupportedSdkVersion` unchanged.
///
/// v62 -- VarClient runtime access + Var Inspector
///   + HostApi VarClient int/string/optional-long getters and setters.
///   + C++/JS/TS `state::vars` VarClient accessors with lossless JS bigint
///     handling for optional 64-bit values.
///   + `VarClientInt::nameOf` and `VarClientStr::nameOf` catalog annotation.
///   ~ The persisted `varbit_inspector` plugin now presents a read-only
///     VarClient diagnostics tab under the visible "Var Inspector" title.
///   ~ `kSdkVersion` 62; `kMinSupportedSdkVersion` unchanged.
///
/// v61 -- VarClient compatibility catalogs
///   + Header-only `titan::VarClientInt::*` and `titan::VarClientStr::*`
///     catalogs mirroring RuneLite's deprecated compatibility constants.
///   + JS/TS `titan.VarClientInt.*` and `titan.VarClientStr.*` mirrors.
///   ~ `kSdkVersion` 61; `kMinSupportedSdkVersion` unchanged.
///
/// v60 -- ItemComposition inventory-action parity
///   ~ `HostApi::getItemComposition` now preserves runtime ItemDef
///     inventory-action slots exactly, including empty positional gaps, so
///     plugin code can reproduce `Inventory::interact` action-index logic.
///   ~ `ItemCompositionState::inventoryActions` capacity raised to match
///     the runtime reader's 32-slot sanity cap. This resizes a fixed ABI
///     struct, so `kMinSupportedSdkVersion` is raised to 60.
///
/// v59 -- Actor path queue locatable support
///   ~ Actor logical tile/world position now comes from PathQueue[0] when
///     available, with precise-coordinate fallback for empty/invalid queues.
///   ~ Actor localPoint remains render/interpolated PreciseX/Y.
///   + WorldPointState ABI struct plus HostApi::getActorPathQueue for
///     exposing valid actor path queue entries as world points.
///   + C++ Player/Npc/Actor pathQueue(), with matching JS/TS pathQueue actor
///     properties. The movement target is the final valid queue entry.
///   ~ kSdkVersion 59; kMinSupportedSdkVersion unchanged.
///
/// v58 -- Varbit fallback chain + source-tagged definitions
///   + `VarbitDefSnapshot::source` (uint8_t) tags the origin of the
///     definition: 0 = live in-memory VarBitType cache, 1 = native
///     GET_VARBIT decode, 2 = JS5 disk cache. Older plugins reading the
///     snapshot ignore the new byte; new plugins can branch on staleness
///     via `titan.state.cache.varbit(id).source` ("live" | "native" |
///     "disk").
///   + `VarbitDefSource` enum mirrored on the JS side as a string field.
///   ~ JS/TS locatable wrappers now expose `localPoint` alongside
///     `tile` and `worldPoint`.
///   ~ `HostApi::getVarbit` (and `titan.state.vars.varbit(id)`) now
///     self-heal when the in-memory VarBitType cache misses: the client
///     falls back to the native GET_VARBIT call (which JS5-loads the
///     type on demand) and finally to a disk-cache extract. Previously
///     returned `-1` for any varbit the game hadn't touched this
///     session (e.g. out-of-area minigame varbits). First out-of-area
///     read on a varbit may incur a one-time JS5 load.
///   ~ `HostApi::getVarbitDef` (and `titan.state.cache.varbit(id)`) now
///     prefers the live definition when present and falls back to the
///     JS5 disk cache; plugins always see fresh `{varpIndex, lowBit,
///     highBit}` when the game has loaded the varbit.
///   ~ `kSdkVersion` 58; `kMinSupportedSdkVersion` unchanged. Field
///     appended at the end of `VarbitDefSnapshot` -- pre-v58 plugins
///     keep loading.
///
/// v57 -- Map-tile graphics objects (spot anims)
///   + GraphicsObjectState struct -- in-flight `MapSpotAnim` snapshot
///     (RuneLite's `GraphicsObject`): plane, precise/scene/world tile
///     position, height offset, spot-anim definition id, start cycle,
///     owning WorldView, and the inline SeqState / cached SeqType
///     pointers for sequence-state inspection.
///   + HostApi::getGraphicsObjects -- enumerate active spot anims rooted
///     at `WorldView::GraphicsObjectList`. Returns 0 when the analyzer
///     didn't detect the list head on the bound revision.
///   + PluginApi::onGraphicsObjectSpawned / onGraphicsObjectDespawned /
///     onGraphicsObjectMoved callbacks dispatched off the same per-tick
///     diff pipeline as projectiles.
///   + titan::GraphicsObject entity wrapper (Locatable<GraphicsObject>),
///     titan::GraphicsObjectQuery with spotAnim(id) / startedAfterTick(t)
///     / activeBefore(t) filters, and titan::queries::graphicsObjects().
///   + JS/TS `titan.getGraphicsObjects()`, `titan.queries.graphicsObjects()`,
///     and `onGraphicsObjectSpawned/Despawned/Moved` plugin hooks.
///   ~ `kSdkVersion` 57; `kMinSupportedSdkVersion` unchanged.
///
/// v56 -- Exact TileObject interact via HostApi
///   + HostApi::interactTileObject(action, TileObjectState*) for instance-
///     exact loc interactions. `TileObject::interact()` now preserves the
///     selected object's tile/action state instead of re-resolving nearest
///     by loc id through HostApi::interactObject.
///   ~ `kSdkVersion` 56; `kMinSupportedSdkVersion` unchanged.
///
/// v55 -- Slot-aware inventory interact via HostApi
///   + HostApi::interactInventoryItemAtSlot fn pointer so external plugins
///     dispatch through Widgets::interact (proper widget click-bounds)
///     instead of executeSyntheticAction with (0,0) click coords.
///   ~ `kSdkVersion` 55; `kMinSupportedSdkVersion` unchanged.
///
/// v54 -- Active interaction predicate
///   + C++ Actor::isInteracting() delegates to the underlying player/NPC.
///   + JS/TS ActorBase.isInteracting() mirrors the entity-overlay active
///     interaction predicate before resolving raw interacting() targets.
///   ~ interactingWith(...), interactingWithLocal(), and
///     notTargetedByOtherPlayers() query filters now ignore stale interaction
///     targets by requiring isInteracting() first.
///   ~ `kSdkVersion` 54; `kMinSupportedSdkVersion` unchanged.
///
/// v53 -- In-frame menu-action replacement
///   + MenuOptionClickedEvent replacement fields appended so plugins can
///     replace an intercepted DoAction while preserving the native click
///     frame. `consumed` still blocks, and `replaced` selects the appended
///     replacement action payload.
///   + C++ MenuClickEvent and JS/TS MenuOptionClicked replacement helpers.
///   ~ `kSdkVersion` 53; `kMinSupportedSdkVersion` unchanged.
///
/// v52 -- Locatable line of sight
///   + Header-only RuneLite-style line-of-sight helpers on WorldPoint,
///     WorldArea, and Locatable entity wrappers.
///   + ClientState::sceneSizeX / sceneSizeY appended for safe world-to-scene
///     line-of-sight conversion.
///   + JS/TS locatable hasLineOfSight helpers and Tile Overlay LOS highlight.
///   ~ `kSdkVersion` 52; `kMinSupportedSdkVersion` unchanged.
///
/// v51 -- Widget text setter
///   + HostApi::setWidgetText plus C++/JS/TS widget setter helpers:
///     `state::widgets().setText(...)`, `WidgetSnapshot::setText(...)`,
///     `titan.state.widgets.setText(...)`, and JS `widget.text = ...`.
///   ~ `kSdkVersion` 51; `kMinSupportedSdkVersion` unchanged.
///
/// v50 -- Ground item ownership
///   + GroundItemState::ownershipType (uint32_t) -- raw ClientObj
///     ownership value: 0=None, 1=SelfPlayer, 2=OtherPlayer,
///     3=GroupIronman.
///   + GroundItemOwnershipAbi constants and titan::GroundItem fluent
///     accessors for ownership-aware looting, including canLoot().
///   + GroundItemQuery::canLoot() and JS/TS ground-item canLoot helpers.
///   + ClientFacade account-mode helpers: accountType(), isIronman(),
///     isIronMan(), isGroupIronman(), isGroupIronMan(), with matching
///     JS/TS titan.state.client properties.
///   ~ `kSdkVersion` 50; `kMinSupportedSdkVersion` unchanged.
///
/// v49 -- Magic API shape and widget constants
///   + Header-only titan::utils::Magic catalog: SpellBook, Standard,
///     Ancient, Lunar, Necromancy, SpellInfo, info(...), currentSpellBook(),
///     isAutoCasting(), lastHomeTeleportUsage(), isHomeTeleportOnCooldown(),
///     canCast(...).
///   + RuneLite InterfaceID.MagicSpellbook constants mirrored under
///     titan::gamevals::InterfaceID::MagicSpellbook and JS/TS gamevals.
///   + select/cast/castOn magic action shape added as no-op stubs that
///     return false; no selected-spell getters/checks or dispatch helpers.
///   + VarPlayerID::LAST_HOME_TELEPORT.
///   ~ Header-only/API-shape addition; kSdkVersion 49.
///
/// v48 -- Actor health bars + hitsplats
///   + PlayerState::healthRatio, healthScale (int32_t, -1 = N/A) --
///     interpolated headbar value read from the entity's headbar vector
///     (entity + HeadbarVectorBegin/End). healthRatio is the current
///     bar fill in [0, healthScale]; healthScale is the bar's maximum
///     width from the HeadbarType config.
///   + PlayerState::hasHealthBar (uint8_t) -- non-zero when the entity
///     has at least one active headbar entry.
///   + NpcState::healthRatio, healthScale, hasHealthBar -- same fields.
///   + Player/Npc/Actor fluent accessors: healthRatio(), healthScale(),
///     healthPercent(), hasHealthBar().
///   + NpcQuery/PlayerQuery: withHealthBar(), noHealthBar(),
///     healthPercentBelow(f), healthPercentAbove(f).
///   ~ Composition-only addition to query.h; struct layout append-only.
///     kSdkVersion 48.
///
/// v47 -- Query API expansion
///   + NpcQuery: interactingWith(Actor), interactingWithLocal(),
///     notInteracting(), isAnimating(), notAnimating(), animation(id),
///     overheadActive() / overheadActive(HeadIcon), overrideTransform(id),
///     sizeEquals(s).
///   + PlayerQuery: interactingWith(Actor) replaces interactingWith(int32_t),
///     interactingWithLocal(), notInteracting(), isAnimating(),
///     notAnimating(), animation(id), isIdle(), isSkulled(),
///     overheadActive() / overheadActive(HeadIcon),
///     combatLevelAbove/Below/Between.
///   + ObjectQuery: layer(id).
///   + GroundItemQuery: maxQuantity(n).
///   + InventoryQuery: minQuantity(n), maxQuantity(n), excludeIds({...}),
///     excludeNames({...}).
///   + ProjectileQuery: targetingEntity(idx), fromEntity(idx),
///     startedAfterTick(t), endsBeforeTick(t), activeDuring(t).
///   + QueryBase: sortBy(cmp).
///   + LocatableQueryBase: onTile(Tile), atWorldPoint(WorldPoint),
///     sortedByDistanceTo(origin), nearest() (zero-arg local-player shorthand).
///   ~ namesAnyOf semantics aligned to case-insensitive substring
///     (was equality in C++; JS/TS already used substring).
///   ~ ProjectileQuery .d.ts now extends LocatableQuery<Projectile>.
///   ~ JS projectile objects now include tileX/tileY/worldX/worldY.
///   ~ JS within/nearestTo now respect plane (cross-plane filtered).
///   ~ JS tile objects now include the `layer` property.
///   ~ Composition-only addition: no new HostApi entries, no struct
///     layout changes. kSdkVersion 47.
///
/// v46 -- OverlayPanel API
///   + AnchorAbi namespace (DYNAMIC / TOP_CENTER / LEFT_CENTER /
///     RIGHT_CENTER / ABOVE_CHATBOX_RIGHT / TOOLTIP -- minimal
///     semantic set; corner anchors are intentionally omitted because
///     users free-position into corners via Alt-drag rather than
///     snapping to them. Edge-center / chatbox / tooltip anchors all
///     resolve relative to game widgets, not the full window).
///   + OverlayPanelStyleAbi struct -- sticky theming (background,
///     border colour + thickness, corner radius, padding, gap,
///     per-component colour overrides).
///   + HostApi::overlayPanelRegister / overlayPanelUnregister /
///     overlayPanelBegin / overlayPanelEnd / overlayPanelSetStyle /
///     overlayPanelTitle / overlayPanelLine / overlayPanelProgressBar
///     -- structured HUD panels with anchor-based auto layout,
///     Alt-drag repositioning, rounded borders, full theming, and
///     per-machine persistence at
///     %USERPROFILE%\.titanclient\overlay_layout.json.
///   + titan::OverlayPanel (subclass of titan::Overlay),
///     titan::OverlayPanelStyle, titan::Anchor, and the
///     Plugin::overlayPanel(...) lambda factory in
///     shared/titan/overlay_panel.h.
///
/// v45 -- Run energy and weight
///   + ClientState::runEnergy (0-10000) and ClientState::weight (signed kg)
///     exposed as direct Client struct fields. Readable via
///     `titan::state::client().runEnergy()` / `.weight()` in C++ and
///     `titan.state.client.runEnergy` / `.weight` in JS/TS.
///
/// v44 -- Interaction staleness detection
///   + PlayerState::interactingPhase, NpcState::interactingPhase -- lifecycle
///     flag from the game's interaction sub-struct (0 = active, non-zero =
///     stale/consumed, 0xFF = offset unavailable). Matches the game's own
///     FUN_0014d890 predicate gate on 238.1+.
///
/// v43 -- Human-like delayed keyboard typing
///   + HostApi::typeKeyboardString -- type a string with randomized
///     inter-character delays; each character dispatched on a separate
///     pump-thread drain cycle. Optional C callback fires on a
///     caller-selected phase (pump thread, ClientTick, PreGameLoop).
///   + HostApi::cancelKeyboardType -- cancel an in-progress typeString
///   + HostApi::isKeyboardTyping -- query whether a type operation is
///     active
///   + KeyboardTypeCallbackPhase namespace (PumpThread / ClientTick /
///     PreGameLoop ordinals)
///
/// v42 -- Keyboard injection + CS2 typed args
///   + TitanHookArg struct for typed CS2 args
///   + HostApi::sendKeyboardString -- inject a string into the game's
///     internal keyboard producer chain (NO OS APIs; silent no-op when
///     the analyzer didn't emit the producer RVAs for this revision)
///   + HostApi::sendKeyboardKey -- press+release a named key with
///     optional shift/ctrl/alt modifiers
///   + HostApi::runClientScriptTyped -- run a CS2 script with mixed
///     int + SSO-inline string arguments via the game's HookReq path
///   ! Widget hook introspection (getWidgetHooks/invokeWidgetHook) was
///     planned for v42 but removed: on modern OSRS revisions widget
///     listeners are not stored as static IfType slots, so static
///     introspection is infeasible. Plugins that need to dispatch
///     widget-driven scripts can use `runClientScriptTyped` directly
///     once they know the script id.
///
/// v41 -- SDK shape unification + Inventory utility helpers (HARD BREAK)
///   ! Source-level break: every public factory in `shared/titan/*.h`
///     moved out of the flat `namespace titan` and into one of the
///     three top-level shape namespaces:
///       - Queries (chainable list views): `titan::queries::npcs()`,
///         `players()`, `objects()`, `groundItems()`, `inventory()`,
///         `projectiles()` (was `titan::npcs()` etc.).
///       - State (subsystem facades): `titan::state::client()`,
///         `camera()`, `hider()`, `cache()`, `vars()`, `skills()`,
///         `prayers()`, `script()`, `widgets()`, `idle()`, `login()`,
///         `walk()`, `itemContainer()`, `itemDef()`, `collisions()`,
///         and the new `titan::state::world::{current, list, hop,
///         hopByListIndex, hopIngame}` (the latter five renamed from
///         the v40 `titan::currentWorld / worldList / hopToWorldId /
///         hopToListIndex / hopToWorldIngame` flat helpers).
///       - Utils (header-only composition): `titan::utils::Dialogue`,
///         `Combat`, `Equipment` (already there in v40, just no longer
///         shadowed at the top level) plus the new `Inventory`
///         namespace (see below).
///     Top-level free helpers (`titan::log`, `titan::logf`,
///     `titan::addChatMessage`, `titan::runOnClientTick`,
///     `titan::runOnRender`) and registration macros / `titan::Plugin`
///     stay where they were.
///   + New SDK header `<titan/utils/inventory.h>`: header-only
///     `namespace titan::utils::Inventory` -- state predicates
///     (`isOpen`, `size`, `emptySlots`, `isFull`, `isEmpty`), reads
///     (`getAll`, `get(id|name)`, `getSlot`, `getByIds`, `getByNames`),
///     predicates (`contains`, `contains(id, qty)`, `count`,
///     `containsAny`, `containsAll`) and the `drop(id|name)` action.
///     Composed over `titan::queries::inventory()` (item enumeration)
///     and `titan::state::widgets().get((149<<16)|0)` (the inventory
///     parent widget for the visibility check) -- no new HostApi /
///     IBackend entries. Mirrors the in-process
///     `client/actions/inventory.{h,cpp}` namespace and the
///     RuneLite-side `theplug.utils.core.api.InventoryUtils` /
///     `Inventory` static utility.
///   + TypeScript types restructured: `titan.queries.*`, `titan.state.*`,
///     `titan.utils.*` are now the only entry points; flat aliases
///     dropped. `titan.utils.inventory.*` exposed in JS plugins
///     (forwarded through new natives that compose the same
///     primitives). World helpers regrouped under `titan.state.world`
///     in both the d.ts and the JS bootstrap. `invokeMenuAction` moved
///     under `titan.state.client.invokeMenuAction`.
///   ~ Source-only break: HostApi function-pointer ordering and struct
///     layouts are unchanged. The advertised `sdkVersion` field a
///     plugin emits is what the host compares against; old plugin
///     binaries built against v40 or earlier embed a `sdkVersion < 41`
///     and are rejected at load with the standard "rebuild against
///     SDK v41" message.
///   ~ `kSdkVersion` 41, `kMinSupportedSdkVersion` 41 (forward-compat
///     window collapsed to the new shape).
///   ~ Polish pass within v41 (no further version bump): naming
///     consistency + JS/TS shape parity. Predicate prefixes
///     normalised (`is*` / `has*` / `contains`), `get` -> `find` for
///     optional-returning lookups (`WidgetsFacade::find`,
///     `Inventory::find`, `Equipment::find`), hider noun setters
///     renamed to `setPlayers` / `setNpcs` / `setSelf` / `setScene`
///     paired with `isPlayersHidden` etc., id-bearing methods
///     collapsed to bare `id()` (`Npc::id`, `TileObject::id`,
///     `Item::id`, `GroundItem::id`), `worldPos()` method on
///     Player/Npc/Tile renamed to `worldPoint()` (the `WorldPoint`
///     / `WorldPos` type aliases stay for source compat),
///     `Section::sectionKey/sectionName` -> `key/name`,
///     `getInteracting` -> `interacting`, predicate renames
///     (`Plugin::isEnabled` / `isDefaultEnabled`,
///     `PluginHandle::isValid` / `isEnabled`, `World::isMembers` /
///     `isBeta`, `EquippedItem::isValid`,
///     `Section::isClosedByDefault`, `SettingCommon::isHidden`,
///     `Projectile::hasMoved`, `Actor::isEmpty`). JS/TS zero-arg
///     utility methods converted to readonly properties
///     (`utils.dialogue.inDialogue`, `isQuestCompletionOpen`,
///     `continueWidgetPackedId`, `utils.combat.specialAttackPercentage`,
///     `isSpecialAttackEnabled`, `isAutoRetaliateEnabled`). Missing
///     TS/JS surface filled in: overhead-icon fields on Player/Npc,
///     `Query.namesAnyOf`, `InventoryQuery.slot/slotsAnyOf/
///     slotsBetween`, `utils.{inventory,equipment}.getByIds/
///     getByNames`, `PluginHandle.setEnabled`. TS cache type aliases
///     re-named (`NpcCacheDef` -> `NpcDef`, `ObjCacheDef` -> `ObjDef`,
///     `VarbitCacheDef` -> `VarbitDef`). `PUBLIC_API.md` gains a
///     "Naming and call-style conventions" section codifying the
///     rules for future symbols. ABI struct layouts and HostApi
///     function-pointer table unchanged; `kSdkVersion` and
///     `kMinSupportedSdkVersion` stay at 41.
///
/// v40 -- Equipment utility helpers
///   + New SDK header `<titan/equipment_slot.h>`: `titan::EquipmentSlot`
///     enum (HEAD=0..AMMO=13, RuneLite-aligned) plus
///     `titan::EquipmentSlotInfo::{name, slotWidgetPackedId, isValid,
///     fromOrdinal}` helpers. `slotWidgetPackedId` returns the packed
///     `(group=387, child)` id for the worn-items slot widget that
///     fronts each ordinal (or `0` for ARMS/HAIR/JAW which have no
///     clickable equipment widget).
///   + New SDK header `<titan/utils/equipment.h>`: header-only
///     `namespace titan::utils::Equipment` -- `getAll`, `get(slot|id|
///     name)`, `getByIds`, `getByNames`, `contains`, `contains(id, qty)`,
///     `count(id|ids)`, `containsAny` / `containsAll` for ids and names,
///     plus `unequip(slot|id|name)`. Reads compose
///     `titan::itemContainer(InventoryID::EQUIPMENT)` and
///     `titan::itemDef`; `unequip` fires
///     `titan::widgets().interact(CC_OP, /*identifier=*/1, /*p0=*/-1,
///     <wornitems slot widget>)`.
///   + Client-internal mirror `Equipment::*` in
///     `client/actions/equipment.{h,cpp}` matching the Combat /
///     Dialogue pattern (same surface, in-process primitives).
///   + JS: `_titan.equipment.{getAll, get, getSlot, contains, count,
///     containsAny, containsAll, unequip, unequipSlot}` plus a
///     `titan.EquipmentSlot` enum mirror. Forwards through the
///     `_equip*` natives to `titan::utils::Equipment::*`.
///   + TypeScript types: `EquipmentSlot` enum, `EquippedItem`
///     interface, `titan.equipment` surface.
///   ~ Composition-only addition: no new HostApi entries, no IBackend
///     virtuals, no struct layout changes. `kSdkVersion` 40,
///     `kMinSupportedSdkVersion` stays 34 (older plugins remain
///     loadable).
///   * Java reference: `theplug.utils.core.api.EquipmentUtils`. The
///     `getEquipmentWidgets(int...)` helper from the Java side is
///     intentionally not ported -- callers that want the underlying
///     widget enumerate via `titan::widgets().get(slotWidgetPackedId(s))`.
///
/// v39 -- RuneLite-leaning TS/JS surface + widget search-by-text
///   + HostApi::getWidgetByText(query, outState) -- returns 1 and fills
///     @p outState when `WidgetReader::findByText` locates a widget whose
///     *primary* display text contains @p query (case-sensitive substring).
///     The output matches `getWidget()` field population; `packedId` is read
///     from the widget when available. Fails (returns 0) when @p query is
///     null/empty, the client isn't loaded, no match exists, or the match
///     has no packed id.
///   + IBackend::getWidgetByText + `ExternalBackend` / `InternalBackend`
///     wiring; `titan::widgets().findByText(query)` on `WidgetsFacade`.
///   + JS: `_titan.getWidgetByText(q)` + `_titan.widgets.findByText(q)`
///     (shorthand attaches `interact` like `widgets.get`).
///   + New `<titan/collision.h>`: `titan::CollisionFlag::*` masks,
///     `titan::collisions()` with `flag(plane,x,y)` and
///     `isBlocked(plane,x,y,dx,dy)` built only from
///     `IBackend::getCollisionFlag` (composition-only).
///   + JS bootstrap: `_titan.collisions.{flag,isBlocked,Flag.*}` mirroring
///     the same masks; implements `isBlocked` in script using `flag()` reads.
///   + TypeScript (`shared/titan-plugin-sdk.d.ts`): `WorldPoint`,
///     `MenuOptionClicked`, `OverlayLayer`, `Npc.id`, `TileObject.id`,
///     `LocalPoint`, `titan.dialogue` / `titan.combat` / `titan.skills` /
///     `titan.prayers`, runtime type-catalog mirrors (`Skill`, `Prayer`, …),
///     `WorldArea` class + region/distance helpers, `titan.camera.posX/Y/Z`,
///     `titan.collisions`, `titan.widgets.findByText`. See `PUBLIC_API.md`.
///   ~ `kSdkVersion` 39. `kMinSupportedSdkVersion` stays 34 (append-only
///     HostApi; older plugins null-check the new pointer).
///
/// v38 -- Widget child enumeration + dialog option selection
///   + HostApi::getWidgetChildren(parentPackedId, outStates, maxOut) --
///     populate up to `maxOut` WidgetState entries from the parent's
///     dynamic-children vector and return the count actually written.
///     Each entry is fully populated (text, screen bounds, hidden,
///     visible, packedId) the same way `getWidget()` populates a single
///     widget. Enables plugins to enumerate dialog options, inventory
///     dynamic children, etc., without an ABI round-trip per child.
///   + titan::widgets().children(packedId) -> std::vector<WidgetSnapshot>
///     fluent wrapper on WidgetsFacade. Returns an empty vector when the
///     parent is missing or has no dynamic children.
///   + titan::utils::Dialogue::selectOption(needles),
///     titan::utils::Dialogue::handleDialogue(needles), and
///     titan::utils::Dialogue::hasOption(needles) added to
///     <titan/utils/dialogue.h>. Iterate the dynamic children of widget
///     (219, 1) and fire WIDGET_CONTINUE (opcode 30, identifier=0,
///     param0=<child slot>, param1=packed(219,1)) on the first matching
///     option. Mirrors the keyboard-press dialog-option helpers from the
///     RuneLite-side WidgetUtils reference.
///   + JS: `_titan.getWidgetChildren(parentPackedId)` +
///     `_titan.widgets.children(parentPackedId)` shorthand returning an
///     array of widget-state objects. TypeScript types updated in
///     shared/titan-plugin-sdk.d.ts.
///   ~ HostApi::getWidgetChildren is appended at the end of HostApi;
///     `kMinSupportedSdkVersion` is NOT raised. Plugins compiled against
///     v37 and earlier keep loading; the new fn pointer is null on older
///     hosts and the fluent wrapper falls back to an empty result.
///
/// v37 -- SDK type catalogs + VarsFacade split + menu dispatch
///   + New SDK headers promoted from client/game/types/*:
///     - <titan/prayer.h> -- titan::Prayer enum + PrayerInfo::{varbitId, name}.
///     - <titan/skill.h> -- titan::Skill enum + SkillInfo::{name, MAX_SKILLS}.
///     - <titan/menu_action.h> -- titan::MenuAction::Id opcode enum +
///       normalize / isWidgetCcFamily / isCcOpFamily / nameFor, plus a
///       fully-specified Entry struct used by client().invokeMenuAction.
///     - <titan/varbits.h> -- titan::Varbits constants (~200) with
///       inline nameOf(int). Replaces the client-only varbits.cpp.
///     - <titan/var_player.h> -- titan::VarPlayerID constants.
///     - <titan/local_point.h> -- titan::LocalPoint struct (plane-less,
///       sub-tile, Euclidean distance).
///     - <titan/world_area.h> -- titan::WorldArea struct (contains /
///       distanceTo / center vs titan::WorldPos).
///   + titan::skills() fluent facade (SkillsFacade) with boosted(int|Skill),
///     real(int|Skill), experience(int|Skill). Routes to existing
///     IBackend::getBoostedSkillLevel / getRealSkillLevel / getSkillExperience.
///   + titan::prayers() fluent facade (PrayersFacade) with
///     isActive(int|Prayer). Routes to existing IBackend::isPrayerActive.
///   + ClientFacade::invokeMenuAction(MenuAction::Id, identifier, p0, p1, wvId=-1)
///     and invokeMenuAction(const MenuAction::Entry&) wrapping the existing
///     IBackend::executeSyntheticEntry virtual. No new ABI entry.
///   + New helpers on existing titan::Tile / titan::WorldPos:
///     distanceTo / distanceTo2D / isInScene on Tile; distanceTo /
///     distanceTo2D / regionId / regionX / regionY / dx / dy / dz and
///     default operator== / operator!= on WorldPos.
///   + `using titan::WorldPoint = titan::WorldPos;` alias for naming
///     parity with RuneLite / client convention.
///   - BREAKING (source-only) VarsFacade::prayerActive / boosted / real /
///     experience removed. Replace with titan::prayers().isActive(...) and
///     titan::skills().boosted / real / experience(...). Underlying HostApi
///     / IBackend entries are unchanged, so the binary ABI is unaffected --
///     kMinSupportedSdkVersion stays at 34.
///   ~ client/game/types/varbits.cpp removed; nameOf is inline in the SDK
///     header. The client-side aliases in client/game/types/*.h continue to
///     expose top-level Varbits / VarPlayerID / Prayer / Skill /
///     MenuActionDef names for source compatibility.
///   + <titan/gamevals.h> -- titan::gamevals::InterfaceID packed widget id
///     catalog. Used by dialogue/combat helpers for fixed UI targets such as
///     the make button, quest-scroll close/content, dialog-options parent,
///     special-attack orb, auto-retaliate toggle, and click-to-continue
///     widgets.
///   + <titan/utils/dialogue.h> -- header-only titan::utils::Dialogue
///     inline helpers (continueMake, continueDialogue, inDialogue,
///     isQuestCompletionOpen, closeQuestCompletion,
///     getContinueWidgetPackedId). Ports the keyboard-input shim from the
///     RuneLite-side WidgetUtils reference; every helper dispatches
///     through titan::widgets().interact(...).
///   + <titan/utils/combat.h> -- header-only titan::utils::Combat inline
///     helpers (enableSpecialAttack(skipMovement=false),
///     getSpecialAttackPercentage, isSpecialAttackEnabled,
///     isAutoRetaliateEnabled, setAutoRetaliate). skipMovement is retained
///     for source compatibility and does not suppress clicks.
///   + VarPlayerID::SPECIAL_ATTACK_ENABLED (300) and
///     VarPlayerID::AUTO_RETALIATE (172) added to <titan/var_player.h>
///     for the new combat helpers.
///   ~ Composition-only addition: no new HostApi entries, no struct
///     layout changes, kSdkVersion and kMinSupportedSdkVersion are
///     unchanged.
///
/// v36 -- Generic widget interaction
///   + HostApi::widgetInteract(opcode, identifier, param0, param1) --
///     generalised widget-family DoAction dispatch. Resolves the parent
///     widget from `param1` (packed `(group << 16) | child`), walks the
///     dynamic children at `param0` (fallback to parent bounds when
///     `param0 < 0` or out of range), computes a Gaussian-weighted click
///     point from the resolved widget's screen bounds, and hands a
///     populated synthetic menu entry to the native `doActionArg11Builder`
///     path. Covers every widget-family opcode: CC_OP (57), CC_OP_LOW
///     (1007), WIDGET_FIRST..FIFTH_OPTION (39-43), WIDGET_TARGET (25),
///     WIDGET_TARGET_ON_WIDGET (58), and related.
///   + titan::widgets().interact(opcode, identifier, param0, param1)
///     fluent wrapper on the existing WidgetsFacade, plus a
///     `WidgetSnapshot::interact(opcode, identifier, param0)` convenience
///     that auto-fills `param1` with the snapshot's own packed id.
///   + WidgetState::packedId echoed back by hostGetWidget so consumers
///     (IPC forwarders, future event callbacks, scripts) can identify
///     the widget without threading the lookup key separately. Field
///     appended at end of WidgetState; zero on pre-SDK-36 hosts.
///   + JS: `_titan.widgetInteract(opcode, identifier, param0, param1)` +
///     `_titan.widgets.interact(...)` shorthand on the existing
///     `_titan.widgets` object, plus a per-widget `ws.interact(opcode,
///     identifier, param0)` that auto-fills `param1` with `ws.packedId`.
///   ~ Inventory::dispatchInventoryAction now thunks through
///     Widgets::interact (semantics unchanged; one less copy of the
///     synthetic-menu-entry build).
///   ~ HostApi::widgetInteract + WidgetState::packedId appended at end
///     of their respective structs; kMinSupportedSdkVersion is NOT
///     raised -- plugins compiled against v35 and earlier keep running.
///   + titan::HeadIcon enum + titan::HeadIconInfo helper namespace
///     (<titan/head_icon.h>) -- 15-ordinal enum mirroring RuneLite
///     net.runelite.api.HeadIcon, with name / shortName / isPrayer /
///     isCurse / color / isValid / fromRaw helpers. Header-only; no
///     ABI surface.
///   ~ Added fluent predicates titan::{Player,Npc,Actor}::isOverheadActive()
///     (any icon) and isOverheadActive(HeadIcon) (specific icon match),
///     plus titan::Player::isSkulled(). Pure derivation of existing v35
///     ABI fields (overheadIcon / skullIcon / hasHeadIconOverride); no
///     HostApi / struct changes; kSdkVersion NOT bumped. Matches the
///     shape of existing isIdle() / isAnimating() / isStationary() helpers.
///
/// v35 -- Actor overhead icons
///   + PlayerState::overheadIcon (int32_t, -1 = none) -- live overhead
///     prayer icon read from `ClientPlayer + PLAYER_OVERHEAD_ICON_OFFSET`.
///   + PlayerState::skullIcon    (int32_t, -1 = none) -- live skull icon
///     from `ClientPlayer + PLAYER_SKULL_ICON_OFFSET`.
///   + NpcState::overheadIcon    (int32_t, -1 = none) -- primary overhead
///     icon sourced from the NpcType cache-default head icon graphics
///     vector (`NPC_TYPE_HEAD_ICON_GRAPHICS_OFFSET`). Covers the bulk of
///     icon-bearing NPCs (bosses etc.).
///   + NpcState::hasHeadIconOverride (uint8_t) -- non-zero when a
///     per-instance runtime override is set via `ClientNpc::SetHeadIcon`
///     (`NPC_HEAD_ICON_CUSTOMISATION_OFFSET`).
///   + FindPlayerOverheadIcon / FindNpcHeadIcon analyzer passes.
///   ~ Fields are appended at the end of PlayerState / NpcState so
///     plugins compiled against v34 and earlier keep running unchanged.
///     `kMinSupportedSdkVersion` is NOT raised.
///
/// v34 -- Typecode-keyed clickbox (ABI break)
///   ~ drawEntityClickbox / drawEntityHull / drawTileObjectClickbox /
///     drawTileObjectHull now take an additional `uint64_t typecode`
///     argument after the raw pointer. The typecode is the engine's
///     64-bit `ModelTypecodeType` that uniquely identifies each picked
///     entity; the host's picking cache is now keyed by typecode so
///     same-tile stacks (player on loc, stacked NPCs, item on loc)
///     no longer collide. The titan::overlay() facades compute the
///     typecode automatically from the Player / NPC / TileObject they
///     receive; raw-pointer overloads accept it as an explicit arg.
///   + TileObjectState::layer (int32_t) records the loc's scene layer
///     (0=Wall, 1=Decor, 2=Scenery, 3=GroundDecor). Required to build
///     the full typecode for locs; populated by the host's TileObject
///     resolver.
///   + kMinSupportedSdkVersion bumped past v33 because this is a
///     pointer-table shape change.
///
/// v33 -- Entity clickbox + hull overlays
///   + HostApi::drawEntityClickbox(entityPtr, outline, fill) -- projects the
///     entity's cached world-space AABB (read from its GraphNode) through W2S
///     and draws 12 wireframe edges plus optional translucent faces.
///     Returns silently as a no-op when the analyzer did not detect
///     GRAPH_NODE_AABB_OFFSET / ACTOR_GRAPH_NODE_OFFSET on this revision.
///   + HostApi::drawTileObjectClickbox(locPtr, outline, fill) -- same for
///     scene objects (walls, decor, standing locs, ground decor). Uses
///     LOC_GRAPH_NODE_OFFSET; same graceful disable when missing.
///   + HostApi::drawEntityHull(entityPtr, outline, fill) -- projects the
///     same AABB, then draws the 2D convex hull of the 8 projected corners
///     as a single closed polyline. Produces a clean entity silhouette
///     with no interior edges -- the recommended default for "highlight
///     this thing" overlays. A future SDK may source this from real
///     mesh vertices; today's implementation approximates via AABB.
///   + HostApi::drawTileObjectHull(locPtr, outline, fill) -- tile-object
///     equivalent.
///   + titan::overlay() facades for all four: entityClickbox /
///     tileObjectClickbox / entityHull / tileObjectHull, each with a
///     typed overload (Actor / TileObject) and a raw-pointer overload.
///   + titan::TileObject::entityPtr() exposes the raw loc pointer.
///   ~ New analyzer passes FindClickboxLayout + FindModelClickbox emit the
///     supporting offsets (ACTOR_GRAPH_NODE_OFFSET, GRAPH_NODE_AABB_OFFSET,
///     GRAPH_NODE_FLAGS_OFFSET, AABB_STRUCT_SIZE, LOC_GRAPH_NODE_OFFSET)
///     under the new GraphNode / Aabb / Loc namespaces in the generated
///     struct header.
///
/// v32 -- Use-on item API
///   + HostApi::useInventoryItemOnItem(srcSlot, srcItemId, tgtSlot,
///     tgtItemId) -- two-packet WIDGET_TARGET -> WIDGET_TARGET_ON_WIDGET
///     inventory-to-inventory flow (the `Use knife on logs` case).
///     srcSlot / tgtSlot < 0 means "resolve the slot from the item id".
///   + HostApi::useInventoryItemOnNpc(srcSlot, srcItemId, npcHashIndex)
///     -- WIDGET_TARGET on the source slot followed by an NPC menu
///     invocation keyed on the hash index.
///   + HostApi::useInventoryItemOnObject(srcSlot, srcItemId, locId,
///     tileX, tileY) -- WIDGET_TARGET on the source slot followed by a
///     game-object menu invocation at the tile.
///   + titan::Item::useOn(const Item&) / useOn(const Npc&) /
///     useOn(const TileObject&) fluent helpers in actor.h. Dispatches
///     the matching HostApi entry; returns `true` when the complete
///     two-action sequence was accepted. The input backend preserves
///     ordering and supplies the required frame spacing.
///
/// v31 -- In-game world hop SDK + panel element budget bump
///   + HostApi::hopToWorldIngame(int32_t id) -- drives the in-game
///     footer CC_OP sequence (opens logout tab, opens switcher,
///     selects world, confirms). Companion to the existing
///     hopToWorldId() which remains the title-screen native
///     changeWorld path. Plugins check titan::login().isLoggedIn()
///     and call the right one.
///   ~ kMaxPanelElements bumped from 256 to 1024 in
///     `shared/plugin_protocol.h`. Pure resource-bound change; the
///     msgpack wire array has no cap. Lets list-heavy panels (world
///     hopper, container inspectors) render every row without
///     pagination. ~308 KB static scratch buffer on the client side
///     (up from ~77 KB). No protocol version bump -- older decoders
///     truncate at their local cap, matching prior behaviour.
///
/// v30 -- Actor idle / stationary semantic fix
///   ~ PlayerState.idle renamed to PlayerState.stationary. The field that
///     used to be called "idle" actually reports whether the player has no
///     pending movement (movementPose == idlePose); a stationary player
///     can still be animating (fishing / mining / etc.), so the old name
///     was misleading. Layout is unchanged -- this is a pure source-level
///     rename; plugins built against older headers reading `.idle` get
///     the correct stationary-semantic value they were already getting.
///   ~ PlayerState.stationary slot repurposed as PlayerState.unknownPlayerFlag.
///     The byte at this offset was previously labelled "stationary" but its
///     role is not yet known; it is no longer populated by the client. Old
///     plugins reading `.stationary` now see the raw unknown byte from the
///     game struct -- which already was the case, we just no longer claim
///     to understand what it means.
///   + titan::Player::isAnimating() -- animation() != -1.
///   + titan::Npc::isAnimating() -- animation() != -1.
///   + titan::Player::isIdle() reintroduced with the correct semantic:
///     isStationary() && !isAnimating(). The old isIdle() was renamed to
///     isStationary().
///
/// v29 -- Action Inspector
///   + HostApi::setActionInspectorVisible
///   + In-client `ActionInspector` debug UI: DoActionHook ring buffer
///     with text / opcode filtering, per-record replay through the
///     native arg11 builder (falls back to clickExecutor), replay-trace
///     diagnostics (subject probes, parity checks, terminal status),
///     synthetic MenuEntry construction, and JSON evidence export.
///     Replaces the "DoAction Debug" block that previously lived inside
///     the developer menu's Debug tab. Reached via the external
///     `action_inspector` plugin (ships disabled by default, matching
///     the chat / cs2 / packet inspector family).
///
/// v28 -- World hop + world list
///   + titan::Worlds {id, flags, string0, string1} struct + helpers
///   + HostApi::getCurrentWorld(int32_t* out) -- live world id,
///     returns 0 when the analyzer didn't emit CURRENT_WORLD_OFFSET
///   + HostApi::getWorldList(WorldState* out, uint32_t cap) -- full
///     snapshot; returns 0 when the list globals are unmapped
///   + HostApi::hopToWorldId(int32_t id) -- native SwitchToWorld call
///     routed through the game thread; returns 0 when the id is
///     unknown or the hop function is unavailable
///   + HostApi::hopToListIndex(uint32_t idx) -- the lower-tier variant
///     (works even when GameWorld fields aren't mapped); index into
///     the runtime m_list as the game's own UI does
///   ~ Tiered availability: level 1 = current-world read, level 2
///     adds hopToListIndex, level 3 adds list + hopToWorldId. Each
///     level gates on its required Offsets fields being non-zero per
///     `no-hardcoded-client-offsets`.
///   * Note: GameState::HoppingWorld event is NOT wired on rev 237.5
///     because the hop path doesn't transition the login-index state
///     machine on this build. Other revisions may restore it once
///     `LOGIN_STATE_HOPPING_VALUE` becomes detectable.
///
/// v27 -- Item Container Explorer
///   + HostApi::setItemContainerExplorerVisible
///   + In-client `ItemContainerExplorer` debug UI: two-pane inspector
///     for the INVENTORY / EQUIPMENT / BANK widget-backed containers +
///     arbitrary container-id probe, with native-pointer validation
///     (from `FindItemContainers`) and per-item drill-down that shows
///     the runtime ItemDef name, stackable flag, noted variant, and the
///     runtime inventory-action slot list mapped to the CC_OP / EXAMINE_ITEM
///     opcodes `Inventory::interact()` would emit.
///   ~ `Inventory::interact()` / `InvItem::drop()` now resolve action
///     labels through the runtime ItemDef accessor (with cache
///     fallback), so `.interact("Drop")` / `.interact("Examine")`
///     works for every item - previously these silently failed on
///     items where the cache's 5-slot array did not list Drop / Examine
///     explicitly (the menu builder appends them at runtime). No ABI
///     change on the inventory-facing structs; this is a pure quality
///     improvement on the existing dispatch.
///   ~ `InvItem::name` now prefers the runtime-resolved display string
///     (varbit / varp / noted transforms applied) when available,
///     matching the in-game hover label exactly.
///
/// v26 -- Item containers + runtime ItemDef
///   + kMaxItemContainerSlots / kMaxItemCompositionActions /
///     kMaxItemCompositionActionLen constants
///   + ItemContainerState / ItemContainerChangedEvent /
///     ItemCompositionState structs
///   + HostApi::getItemContainer(id, out) -- widget-backed container read
///     (RuneLite-style InventoryID semantics; INVENTORY=93,
///     EQUIPMENT=94, BANK=95, others return 0 when unmapped)
///   + HostApi::getItemComposition(id, out) -- runtime ItemDef with
///     varbit/varp transforms + menu-builder actions; falls back to
///     cache when ITEM_DEF_LOOKUP is unmapped (signalled via
///     runtimeResolved flag on the snapshot)
///   + PluginApi::onItemContainerChanged -- tick-level-diff event (no
///     native write chokepoint on this rev; see the container-negative
///     finding in osrs-map)
///   + New public SDK header shared/titan/inventory_id.h with the
///     RuneLite-parity InventoryID enum + nameOf() helper
///
/// v25 -- Varbit Inspector
///   + HostApi::setVarbitInspectorVisible
///   + In-client `VarbitInspector` debug UI that hooks
///     `SetVarbitHook::addListener` and renders a live change log plus a
///     per-varbit hit-count / frequency view. Reached via the external
///     `varbit_inspector` plugin (ships disabled by default, matching
///     the chat / cs2 / packet inspector family).
///   + `SetVarbitHook::addListener(Listener)` -- new in-process slot for
///     client-internal listeners. External plugins keep using
///     `Plugin::onVarbitChanged`; behaviour is unchanged for them.
///
/// v24 -- Correct ChatMessageType ordinals + first-handshake fix
///   ~ `titan.ChatMessageType` (JS) / `ChatMessageType` (TS) constants
///     now match RuneLite's ChatMessageType enum 1:1. Renamed PUBLIC ->
///     PUBLICCHAT (= 2), SERVER -> GAMEMESSAGE (= 0), CLAN_CHAT (= 41)
///     corrected from 3 (PRIVATECHAT). Added full set: PRIVATECHAT,
///     PRIVATECHATOUT, FRIENDSCHAT, CLAN_MESSAGE, CLAN_GUEST_*,
///     TRADE_SENT, TRADE, TRADEREQ, CHALREQ_*, BROADCAST (= 14),
///     CONSOLE, WELCOME, DIALOG, MESBOX, NPC_SAY, LEVELUPMESSAGE,
///     DIDYOUKNOW, and several more. `titan.chat.system()` now routes
///     through GAMEMESSAGE, `titan.chat.say()` through PUBLICCHAT.
///   ~ `titan::Plugin::_setEnabledFromHost` always fires
///     `onEnabledChanged` on the first call after plugin construction
///     (controller initial-state handshake). Previously the callback was
///     skipped when the persisted controller state matched the then-current
///     DLL default `enabled_ = true`, leaving visibility-toggle plugins
///     (chat / cs2 / packet inspectors) with their windows closed until
///     the user manually toggled.
///
/// v23 -- Plugin metadata + layered SDK groundwork
///   + HostApi::addChatMessage
///   + HostApi::setChatInspectorVisible
///   + HostApi::setCs2InspectorVisible
///   + HostApi::setPacketInspectorVisible
///   + PluginApi::getDescription / getAuthor / getVersion / getDefaultEnabled
///   + PluginInfo::description / author / version
///   + ChatMessageEvent struct
///   + kMaxDescriptionLen / kMaxAuthorLen / kMaxVersionLen
///   ~ TITAN_PLUGIN_META 6-arg macro supersedes TITAN_PLUGIN
///   ~ Introduced `detail::IBackend` + `ExternalBackend` / `InternalBackend`
///     dispatch split (internal layering; no public API surface change).
///
/// v22 -- Chat event + inject
///   + HostApi::addChatMessage
///   + PluginApi::onChatMessage
///   + ChatMessageEvent struct
///
/// v21 -- Varbit event
///   + PluginApi::onVarbitChanged
///   + VarbitChangedEvent struct
///
/// v20 -- Pre-changelog baseline; history backfilled on demand from git log.

#pragma once

#include <cstdint>

#include "native_records.h"

/// Private ABI namespace. Public plugin code should use `namespace titan` instead.
namespace TitanPluginSdk {

/// Current SDK version advertised by this header. Bumped whenever a new
/// public symbol lands in `shared/titan/`. See the changelog at the top of
/// this file.
constexpr uint32_t kSdkVersion = 144;

/// Immutable cache-definition payload contract for Native ABI v1. SDK source
/// releases do not change this value or the records carrying it.
constexpr uint32_t kNativePayloadVersion = 1;

/// Historical SDK-window floor retained for local source compatibility and
/// pre-reset changelog/tests. Native ABI v1 does not consult this constant.
constexpr uint32_t kMinSupportedSdkVersion = 135;

/// Upper bound for one VarClient string snapshot including its NUL terminator.
/// The host getter exposes a required-size ABI; clients use this cap before
/// allocating so malformed runtime data fails closed.
constexpr uint32_t kMaxVarClientStringBytes = 16u * 1024u * 1024u + 1u;

/// Historical export names retained for local source compatibility only.
/// Native ABI v1 loaders do not query these names and registration macros do
/// not emit them; the only factory entry is TitanPlugin_QueryNative.
constexpr const char* kCreateSymbolName = "TitanCreatePlugin";
constexpr const char* kPluginCountSymbolName = "TitanGetPluginCount";
constexpr const char* kCreatePluginAtSymbolName = "TitanCreatePluginAt";
/// Data export carrying every plugin id in the DLL as a single newline-joined,
/// NUL-terminated string. Read statically by the server-side PE parser to
/// verify uploaded multi-plugin binaries. See app/Services/PeFileReader.php.
constexpr const char* kPluginIdListSymbolName = "TitanPluginIdList";
constexpr uint32_t kMaxErrorLen = TitanNativeRecords::kMaxErrorLen;
constexpr uint32_t kMaxLabelLen = TitanNativeRecords::kMaxNameLen;
constexpr uint32_t kMaxActionCount = 5;
constexpr uint32_t kMaxItemSubOpCount = 20;
/// Plugin metadata string capacities (description / author / version).
/// Consumed by `PluginInfo` and the `TITAN_PLUGIN_META` macro. Raised in SDK 23.
constexpr uint32_t kMaxDescriptionLen = TitanNativeRecords::kMaxDescriptionLen;
constexpr uint32_t kMaxAuthorLen = TitanNativeRecords::kMaxAuthorLen;
constexpr uint32_t kMaxVersionLen = TitanNativeRecords::kMaxVersionLen;

/// Entity type constants for the interacting-target resolution API.
namespace EntityType {
    constexpr uint8_t LOCATION = 0;
    constexpr uint8_t NPC      = 1;
    constexpr uint8_t PLAYER   = 2;
    constexpr uint8_t NONE     = 0x7F;
}

struct ProjectileActorRefAbi {
    int32_t hashIndex = -1;
    int32_t entityType = EntityType::NONE;
};

inline constexpr ProjectileActorRefAbi decodeProjectileActorRef(int32_t raw) {
    if (raw == 0) return {};
    if (raw > 0) return {raw - 1, EntityType::NPC};
    return {-raw - 1, EntityType::PLAYER};
}

/// Render layer constants for the per-overlay renderOverlay(layer) dispatch.
namespace RenderLayerAbi {
    constexpr uint8_t ABOVE_SCENE   = 0;
    constexpr uint8_t ABOVE_WIDGETS = 1;
}

/// Anchor ordinals for OverlayPanel. Mirrored by titan::Anchor in
/// shared/titan/overlay_panel.h. Only the values with semantics that
/// free positioning can't replicate are exposed -- corner / canvas
/// anchors from the original RuneLite OverlayPosition enum are
/// dropped; users free-position into those areas via Alt-drag.
namespace AnchorAbi {
    constexpr uint8_t DYNAMIC             = 0;
    constexpr uint8_t TOP_CENTER          = 1;
    constexpr uint8_t LEFT_CENTER         = 2;
    constexpr uint8_t RIGHT_CENTER        = 3;
    constexpr uint8_t ABOVE_CHATBOX_RIGHT = 4;
    constexpr uint8_t TOOLTIP             = 5;
}

/// Sticky style for an OverlayPanel. Mirrored by titan::OverlayPanelStyle
/// in shared/titan/overlay_panel.h. Plugins fill this once and pass it
/// to overlayPanelSetStyle; the host caches and applies it every frame
/// until replaced.
struct OverlayPanelStyleAbi {
    uint32_t background;
    uint32_t borderColor;
    float    borderThickness;
    float    cornerRadius;
    int32_t  padHorizontal;
    int32_t  padVertical;
    int32_t  lineGap;
    uint32_t titleColor;
    uint32_t lineLeftColor;
    uint32_t lineRightColor;
    uint32_t barFillColor;
    uint32_t barBgColor;
};

/// Snapshot of top-level client state: base pointers, tick count, and local player info.
struct ClientState {
    uint64_t clientBase = 0;
    uint64_t worldViewPtr = 0;
    uint64_t scenePtr = 0;
    uint64_t localPlayerEntity = 0;
    int32_t tickCount = 0;
    int32_t plane = 0;
    int32_t localPlayerIndex = -1;
    int32_t playerCount = 0;
    int32_t baseX = 0;
    int32_t baseY = 0;
    int32_t runEnergy = 0;
    int32_t weight = 0;
    int32_t sceneSizeX = 0;
    int32_t sceneSizeY = 0;
    int32_t currentWorldViewId = -1;
    int32_t topLevelBaseX = 0;
    int32_t topLevelBaseY = 0;
    int32_t topLevelPlane = 0;
    int32_t topLevelSceneSizeX = 0;
    int32_t topLevelSceneSizeY = 0;
    int32_t topLevelLocalPlayerTileX = 0;
    int32_t topLevelLocalPlayerTileY = 0;
    int32_t topLevelLocalPlayerPlane = 0;
    uint8_t topLevelLocalPlayerTileValid = 0;
};

/// Absolute world tile coordinate for ABI arrays.
/// Mirrors `titan::WorldPoint` / `WorldPos` without exposing C++ methods.
struct WorldPointState {
    int32_t x = 0;
    int32_t y = 0;
    int32_t z = 0;
    int32_t worldViewId = -1;
};

/// Scene-local coordinate in 1/128th-tile precision.
struct LocalPointState {
    int32_t x = 0;
    int32_t y = 0;
    int32_t worldViewId = -1;
};

// --- SDK 113: native world-map display state ------------------------------

constexpr uint32_t kWorldMapApiVersion = 1;

/// Immutable, atomically validated state for the visible native world map.
/// The host maps the game's native Z map axis onto Titan's public world Y
/// coordinate. `viewport*` is the physical overlay/clipping rectangle;
/// `logicalViewport*` is the widget-frame rectangle used by the native map
/// renderer before `interfaceScale*` and `canvasOrigin*` are applied. A
/// successful HostApi::getWorldMapState call guarantees both rectangles are
/// coherent, the map area is loaded, zoom/scales are finite and positive,
/// and the complete analyzer-derived field contract is present. Callers must
/// treat a failed call as unavailable rather than retaining an older snapshot.
struct WorldMapState {
    uint32_t structSize = sizeof(WorldMapState);
    uint32_t apiVersion = kWorldMapApiVersion;
    int32_t globalCenterX = 0;
    int32_t globalCenterY = 0;
    float currentZoom = 0.0f;
    float targetZoom = 0.0f;
    float pixelsPerTile = 0.0f;
    int32_t viewportX = 0;
    int32_t viewportY = 0;
    int32_t viewportWidth = 0;
    int32_t viewportHeight = 0;
    int32_t logicalViewportX = 0;
    int32_t logicalViewportY = 0;
    int32_t logicalViewportWidth = 0;
    int32_t logicalViewportHeight = 0;
    float interfaceScaleX = 0.0f;
    float interfaceScaleY = 0.0f;
    int32_t canvasOriginX = 0;
    int32_t canvasOriginY = 0;
};

constexpr uint32_t kInstanceTemplatePlaneCount = 4;
constexpr uint32_t kInstanceTemplateChunkXCount = 13;
constexpr uint32_t kInstanceTemplateChunkYCount = 13;
constexpr uint32_t kInstanceTemplateChunkCount =
    kInstanceTemplatePlaneCount *
    kInstanceTemplateChunkXCount *
    kInstanceTemplateChunkYCount;

/// RuneLite-style instance template chunks, flattened plane-major:
/// `[plane][chunkX][chunkY]`. Invalid cells are -1 because packed value 0
/// can represent source plane/chunk/rotation zero.
struct InstanceTemplateChunksState {
    int32_t chunks[kInstanceTemplateChunkCount] = {};
    uint8_t instanced = 0;
};

// --- SDK 112: web walker and bulk collision snapshots ----------------------

constexpr uint32_t kWebWalkerApiVersion = 1;
constexpr uint32_t kCollisionSnapshotApiVersion = 1;
constexpr uint32_t kCollisionRegionSize = 64;
constexpr uint32_t kCollisionPlaneCount = 4;
constexpr uint32_t kCollisionRegionFlagCount =
    kCollisionRegionSize * kCollisionRegionSize * kCollisionPlaneCount;
constexpr uint32_t kWebPathMaxForbiddenTiles = 4096;
/// Maximum number of fixed step records exposed by one web-path request.
/// This bounds every language bridge allocation at the HostApi boundary.
constexpr uint32_t kWebPathMaxSteps = 16'384;
constexpr uint32_t kWebPathDefaultTimeoutMs = 60'000;
constexpr uint32_t kWebPathMaxTimeoutMs = 10 * 60'000;
constexpr uint32_t kWebPathMessageCapacity = 192;
constexpr uint32_t kWebPathStepNameCapacity = 96;
/// Cost values in fixed WebPath ABI records are integer points. Legacy
/// authored cost 1 is ten points; a normal walked tile costs five points.
constexpr uint32_t kWebPathCostPointsPerWhole = 10;
constexpr uint32_t kWebPathWalkCostPoints = 5;

namespace CollisionSnapshotStatus {
    constexpr uint8_t Unavailable    = 0;
    constexpr uint8_t Ready          = 1;
    constexpr uint8_t MissingRegion  = 2;
    constexpr uint8_t BufferTooSmall = 3;
    constexpr uint8_t InvalidRequest = 4;
}

namespace WebPathRouteSpace {
    constexpr uint32_t Global          = 0;
    constexpr uint32_t CurrentInstance = 1;
}

/// Instance-copy identity has no meaning in global route space. Core graph
/// nodes retain their own sentinel, while every fixed ABI record uses -1.
constexpr int32_t webPathAbiInstanceCopyId(
        uint32_t routeSpace, int32_t copyId) noexcept {
    return routeSpace == WebPathRouteSpace::Global ? -1 : copyId;
}

namespace WebPathRequestFlag {
    /// Ignore `start` and snapshot the current local-player tile.
    constexpr uint32_t UseLocalPlayer = 1u << 0;
}

namespace WebPathOption {
    constexpr uint32_t Transports            = 1u << 0;
    constexpr uint32_t Teleports             = 1u << 1;
    constexpr uint32_t EquippedItemTeleports = 1u << 2;
    constexpr uint32_t MinigameTeleports     = 1u << 3;
    constexpr uint32_t PohRoutes             = 1u << 4;
    constexpr uint32_t Charters              = 1u << 5;
    constexpr uint32_t AvoidWilderness       = 1u << 6;

    constexpr uint32_t Default = Transports | Teleports
        | EquippedItemTeleports | PohRoutes | AvoidWilderness;
}

namespace WebPathPhase {
    constexpr uint32_t None      = 0;
    constexpr uint32_t Queued    = 1;
    constexpr uint32_t Running   = 2;
    constexpr uint32_t Complete  = 3;
    constexpr uint32_t Failed    = 4;
    constexpr uint32_t Cancelled = 5;
}

namespace WebPathResult {
    constexpr uint32_t None                    = 0;
    constexpr uint32_t Exact                   = 1;
    constexpr uint32_t PartialWithinThreeTiles = 2;
    constexpr uint32_t NoPath                  = 3;
    constexpr uint32_t Timeout                 = 4;
    constexpr uint32_t Cancelled               = 5;
    constexpr uint32_t InvalidRequest          = 6;
    constexpr uint32_t NotLoggedIn             = 7;
    constexpr uint32_t CollisionUnavailable    = 8;
    constexpr uint32_t ProviderUnavailable     = 9;
    constexpr uint32_t Busy                    = 10;
    constexpr uint32_t InternalError           = 11;
}

namespace WebPathStepKind {
    constexpr uint32_t Walk      = 0;
    constexpr uint32_t Transport = 1;
    constexpr uint32_t Teleport  = 2;
}

/// Metadata for a copied live collision scene. The collision flag buffer is
/// supplied separately to keep this fixed record small and language-neutral.
/// Flags are flattened `[plane][sceneX][sceneY]` with `sceneY` contiguous.
struct CollisionSceneSnapshotState {
    uint32_t structSize = sizeof(CollisionSceneSnapshotState);
    uint32_t apiVersion = kCollisionSnapshotApiVersion;
    int32_t baseX = 0;
    int32_t baseY = 0;
    int32_t width = 0;
    int32_t height = 0;
    int32_t worldViewId = -1;
    uint32_t flagCount = 0;
    InstanceTemplateChunksState templates{};
};

/// Fixed portion of a path request. Forbidden points are passed as a separate
/// ABI array to `HostApi::webPathSubmit`.
struct WebPathRequestState {
    uint32_t structSize = sizeof(WebPathRequestState);
    uint32_t apiVersion = kWebWalkerApiVersion;
    WorldPointState start{};
    WorldPointState destination{};
    uint32_t routeSpace = WebPathRouteSpace::Global;
    uint32_t options = WebPathOption::Default;
    uint32_t timeoutMs = kWebPathDefaultTimeoutMs;
    uint32_t requestFlags = WebPathRequestFlag::UseLocalPlayer;
};

struct WebPathSummaryState {
    uint32_t structSize = sizeof(WebPathSummaryState);
    uint32_t apiVersion = kWebWalkerApiVersion;
    uint64_t requestId = 0;
    uint32_t phase = WebPathPhase::None;
    uint32_t result = WebPathResult::None;
    uint32_t totalCost = 0;  ///< Integer cost points.
    uint32_t stepCount = 0;  ///< Never greater than kWebPathMaxSteps.
    uint32_t exploredNodes = 0;
    uint32_t elapsedMs = 0;
    WorldPointState start{};
    WorldPointState requestedDestination{};
    WorldPointState reachedDestination{};
    char message[kWebPathMessageCapacity] = {};
};

struct WebPathStepState {
    uint32_t structSize = sizeof(WebPathStepState);
    uint32_t apiVersion = kWebWalkerApiVersion;
    uint64_t edgeId = 0;
    uint32_t kind = WebPathStepKind::Walk;
    uint32_t subtype = 0;
    uint32_t fromRouteSpace = WebPathRouteSpace::Global;
    uint32_t toRouteSpace = WebPathRouteSpace::Global;
    uint32_t edgeCost = 0;  ///< Integer cost points.
    uint32_t accumulatedCost = 0;  ///< Integer cost points.
    int32_t fromInstanceCopyId = -1;
    int32_t toInstanceCopyId = -1;
    WorldPointState from{};
    WorldPointState to{};
    char name[kWebPathStepNameCapacity] = {};
};

// --- SDK 114: web walk executor -----------------------------------------

constexpr uint32_t kWebWalkApiVersion = 1;

/// Behaviour flags for a web walk session.
namespace WebWalkFlag {
constexpr uint32_t ManageRun = 1u << 0;
/// Drink a stamina potion below 50% energy when one is carried.
constexpr uint32_t DrinkStamina = 1u << 1;
/// The session does not advance on its own game-tick pump; the owning
/// plugin drives it by calling webWalkAdvance once per game tick.
constexpr uint32_t ManualTick = 1u << 2;
constexpr uint32_t Default = ManageRun;
}  // namespace WebWalkFlag

namespace WebWalkPhase {
constexpr uint32_t None = 0;
constexpr uint32_t Planning = 1;
constexpr uint32_t Walking = 2;
constexpr uint32_t Transiting = 3;
constexpr uint32_t Arrived = 4;
constexpr uint32_t Failed = 5;
constexpr uint32_t Cancelled = 6;
}  // namespace WebWalkPhase

inline constexpr bool isTerminalWebWalkPhase(uint32_t phase) noexcept {
    return phase == WebWalkPhase::Arrived || phase == WebWalkPhase::Failed
        || phase == WebWalkPhase::Cancelled;
}

/// Start-a-walk request. The embedded path request supplies the destination,
/// route options, forbidden tiles and timeout; its start is always the local
/// player (the UseLocalPlayer flag is implied and enforced by the host).
struct WebWalkRequestState {
    uint32_t structSize = sizeof(WebWalkRequestState);
    uint32_t apiVersion = kWebWalkApiVersion;
    WebPathRequestState path{};
    uint32_t walkFlags = WebWalkFlag::Default;
    /// Chebyshev arrival tolerance around the destination; zero = exact tile.
    uint32_t arriveRadius = 0;
    /// Hard budget for the whole walk in game ticks; zero = unlimited.
    uint32_t maxDurationTicks = 0;
};

struct WebWalkStatusState {
    uint32_t structSize = sizeof(WebWalkStatusState);
    uint32_t apiVersion = kWebWalkApiVersion;
    uint64_t walkId = 0;
    uint32_t phase = WebWalkPhase::None;
    /// Planning failure detail (WebPathResult) when the underlying route
    /// request failed; WebPathResult::None otherwise.
    uint32_t pathResult = WebPathResult::None;
    uint32_t currentStepIndex = 0;
    uint32_t stepCount = 0;
    uint32_t ticksActive = 0;
    uint32_t replanCount = 0;
    /// Stable code of the follower's most recent decision, for overlays.
    uint32_t lastDecision = 0;
    uint32_t reserved0 = 0;
    WorldPointState destination{};
    char currentStepName[kWebPathStepNameCapacity] = {};
    char message[kWebPathMessageCapacity] = {};
};

/// Actor-attached spot animation entry. These are the per-actor spotanims
/// stored directly on Player/NPC actors, distinct from map-tile
/// GraphicsObject spotanims rooted in WorldView::GraphicsObjectList.
struct ActorSpotAnimState {
    int32_t slot = 0;
    int32_t id = -1;
    int32_t height = 0;
    int32_t expireCycle = 0;
};

/// Availability/status for PlayerModel equipment composition snapshots.
namespace PlayerCompositionStatus {
    constexpr uint8_t Unavailable    = 0;
    constexpr uint8_t Available      = 1;
    constexpr uint8_t MissingOffsets = 2;
    constexpr uint8_t NullPlayer     = 3;
    constexpr uint8_t NullModel      = 4;
    constexpr uint8_t NpcTransform   = 5;
    constexpr uint8_t BadVector      = 6;
}

/// Kind of a raw PlayerModel equipment slot value.
namespace PlayerCompositionSlotKind {
    constexpr uint8_t Empty      = 0;
    constexpr uint8_t Item       = 1;
    constexpr uint8_t NonItem    = 2;
    constexpr uint8_t UnknownRaw = 3;
}

constexpr uint32_t kMaxPlayerCompositionSlots = 32;

struct PlayerCompositionSlotState {
    int32_t slotIndex = -1;
    int32_t rawValue = 0;
    int32_t itemId = -1;
    uint8_t kind = PlayerCompositionSlotKind::Empty;
};

struct PlayerCompositionState {
    uint8_t status = PlayerCompositionStatus::Unavailable;
    int32_t itemIdBase = -1;
    int32_t npcTransformId = -1;
    uint32_t slotCount = 0;
    PlayerCompositionSlotState slots[kMaxPlayerCompositionSlots] = {};
};

/// Per-player snapshot: position, animation, combat level, interaction target.
struct PlayerState {
    uint64_t entityPtr = 0;
    int32_t hashIndex = -1;
    int32_t tileX = 0;
    int32_t tileY = 0;
    int32_t plane = 0;
    int32_t worldX = 0;
    int32_t worldY = 0;
    int32_t preciseX = 0;
    int32_t preciseY = 0;
    int32_t orientation = 0;
    int32_t animation = 0;
    int32_t interactingIndex = -1;
    uint8_t interactingType = 0x7F;
    int32_t combatLevel = 0;
    uint8_t hidden = 0;
    // Renamed in SDK v30: was `idle`. The field actually reports whether
    // the player has no pending movement (movementPose == idlePose); a
    // stationary player can still be animating. The layout slot is
    // unchanged -- plugins compiled against older headers reading `.idle`
    // end up reading this same byte with the same semantics.
    uint8_t stationary = 0;
    // Renamed in SDK v30: was `stationary`. Role TBD -- previously this
    // slot carried a byte flag we thought was a "stationary" boolean, but
    // the actual stationary signal lives in the pair above. The client no
    // longer populates this field; it stays in the struct at the same
    // layout position for ABI compatibility.
    uint8_t unknownPlayerFlag = 0;
    char name[kMaxLabelLen] = {};
    // --- v35: overhead icons ---
    /// Live overhead prayer icon index, or `-1` when none is active.
    /// Mirrors RuneLite `Actor.getOverheadIcon()` (when resolved against
    /// the `HeadIcon` enum). Its historical position is frozen in Native
    /// ABI v1; the complete PlayerState array stride must remain unchanged.
    int32_t overheadIcon = -1;
    /// Live skull icon index, or `-1` when unskulled. Matches RuneLite
    /// `Player.getSkullIcon()`.
    int32_t skullIcon = -1;
    // --- v44: interaction staleness ---
    /// Interaction lifecycle phase (0 = active, non-zero = stale/consumed).
    /// 0xFF when the offset is unavailable on this revision.
    uint8_t interactingPhase = 0xFF;
    // --- v48: health bars ---
    /// Current headbar fill value in [0, healthScale]. -1 when no bar.
    int32_t healthRatio = -1;
    /// Headbar maximum width (from HeadbarType config). -1 when no bar.
    int32_t healthScale = -1;
    /// Non-zero when the entity has at least one active headbar.
    uint8_t hasHealthBar = 0;
    // --- v73: actor poses ---
    /// Current movement pose id. The offset bundle key remains
    /// `MovementState` for now.
    int32_t movementPose = 0;
    /// Idle/rest pose id. The offset bundle key remains `IdleState` for now.
    int32_t idlePose = 0;
    /// Owning WorldView id. `-1` means current/unknown for older snapshots.
    int32_t worldViewId = -1;
    /// Owning WorldView pointer.
    uint64_t worldViewPtr = 0;
};

/// Per-NPC snapshot: position, definition ID, transform, size, and actions.
struct NpcState {
    uint64_t entityPtr = 0;
    uint64_t definitionPtr = 0;
    int32_t hashIndex = -1;
    int32_t npcId = -1;
    int32_t tileX = 0;
    int32_t tileY = 0;
    int32_t plane = 0;
    int32_t worldX = 0;
    int32_t worldY = 0;
    int32_t preciseX = 0;
    int32_t preciseY = 0;
    int32_t orientation = 0;
    int32_t animation = 0;
    int32_t interactingIndex = -1;
    uint8_t interactingType = 0x7F;
    int32_t overrideTransform = 0;
    int32_t sizeX = 1;
    int32_t sizeY = 1;
    char name[kMaxLabelLen] = {};
    char actions[kMaxActionCount][kMaxLabelLen] = {};
    // --- v35: overhead icons ---
    /// Primary overhead icon index for this NPC, or `-1` when none.
    /// Populated from `NpcType::headIconGraphics[0]` (cache default) if
    /// available. Dynamic per-instance overrides set by
    /// `ClientNpc::SetHeadIcon` are reflected in `hasHeadIconOverride`
    /// below but their per-slot values are not yet surfaced in this
    /// struct -- read them via the backend if needed.
    int32_t overheadIcon = -1;
    /// True when the server has set a runtime per-NPC overhead icon
    /// override (independent of the cache default). Mirrors RuneLite's
    /// "does the overhead icon come from a dynamic source".
    uint8_t hasHeadIconOverride = 0;

    /// Find the 1-based action slot matching @p action (case-insensitive substring), or 0 if none.
    int findAction(const char* action) const {
        if (!action || !action[0]) return 0;
        auto lower = [](char c) -> char { return (c >= 'A' && c <= 'Z') ? static_cast<char>(c + 32) : c; };
        for (uint32_t i = 0; i < kMaxActionCount; ++i) {
            if (actions[i][0] == '\0') continue;
            for (const char* h = actions[i]; *h; ++h) {
                const char* hi = h;
                const char* ni = action;
                while (*hi && *ni && lower(*hi) == lower(*ni)) { ++hi; ++ni; }
                if (!*ni) return static_cast<int>(i) + 1;
            }
        }
        return 0;
    }

    bool hasAction(const char* action) const { return findAction(action) != 0; }

    // --- v44: interaction staleness ---
    /// Interaction lifecycle phase (0 = active, non-zero = stale/consumed).
    /// 0xFF when the offset is unavailable on this revision.
    uint8_t interactingPhase = 0xFF;
    // --- v48: health bars ---
    int32_t healthRatio = -1;
    int32_t healthScale = -1;
    uint8_t hasHealthBar = 0;
    // --- v73: actor poses / stationarity ---
    /// Current movement pose id. The offset bundle key remains
    /// `MovementState` for now.
    int32_t movementPose = 0;
    /// Idle/rest pose id. The offset bundle key remains `IdleState` for now.
    int32_t idlePose = 0;
    /// Non-zero when movementPose == idlePose.
    uint8_t stationary = 0;
    /// Owning WorldView id. `-1` means current/unknown for older snapshots.
    int32_t worldViewId = -1;
    /// Owning WorldView pointer.
    uint64_t worldViewPtr = 0;
};

/// Tile-based scene object (wall, floor decoration, game object, etc.).
struct TileObjectState {
    int32_t tileX = 0;
    int32_t tileY = 0;
    int32_t plane = 0;
    int32_t locId = -1;
    int32_t sizeX = 1;
    int32_t sizeY = 1;
    uint64_t packedId = 0;
    uint64_t entityPtr = 0;
    uint64_t definitionPtr = 0;
    char type[kMaxLabelLen] = {};
    char name[kMaxLabelLen] = {};
    char actions[kMaxActionCount][kMaxLabelLen] = {};
    /// Scene layer the loc was picked up from:
    ///   0 = Wall, 1 = Decor, 2 = Scenery (standing loc), 3 = GroundDecor.
    /// -1 when the host couldn't classify (e.g. ground-item-only path).
    /// Native loc picking uses the raw packed scene tag in packedId; layer
    /// is metadata for filtering/debug display. Populated by the host's
    /// light-object resolver (SDK v34+).
    int32_t layer = -1;
    int32_t worldX = 0;
    int32_t worldY = 0;
    /// Raw 1-byte scene-object typecode from TypeCode2, or -1 when unavailable.
    /// Bit layout:
    ///   bits 0..4 = scene object shape/type
    ///   bit 5     = reserved/unknown
    ///   bits 6..7 = orientation
    int32_t sceneTypecode = -1;
    /// Derived scene object type/shape (sceneTypecode & 0x1f), or -1.
    int32_t sceneObjectType = -1;
    /// Derived orientation ((sceneTypecode >> 6) & 3), or -1.
    int32_t orientation = -1;
    /// Active dynamic scenery animation id, or -1 when static/unavailable.
    int32_t animation = -1;
    /// Owning WorldView id. `-1` means current/unknown for older snapshots.
    int32_t worldViewId = -1;
    /// Owning WorldView pointer.
    uint64_t worldViewPtr = 0;
};

namespace GroundItemOwnershipAbi {
constexpr uint32_t NONE = 0;
constexpr uint32_t SELF_PLAYER = 1;
constexpr uint32_t OTHER_PLAYER = 2;
constexpr uint32_t GROUP_IRONMAN = 3;
}

/// Ground item visible on a tile.
struct GroundItemState {
    int32_t tileX = 0;
    int32_t tileY = 0;
    int32_t plane = 0;
    int32_t itemId = -1;
    int32_t quantity = 0;
    char name[kMaxLabelLen] = {};
    int32_t worldX = 0;
    int32_t worldY = 0;
    /// Raw ClientObj ownership type; 0xFFFFFFFF when unavailable/read failed.
    uint32_t ownershipType = GroundItemOwnershipAbi::NONE;
    /// Owning WorldView id. `-1` means current/unknown for older snapshots.
    int32_t worldViewId = -1;
    /// Owning WorldView pointer.
    uint64_t worldViewPtr = 0;
};

/// In-flight projectile with source/target actor refs, trajectory, and animation IDs.
struct ProjectileState {
    uint64_t basePtr = 0;
    int32_t plane = 0;
    int32_t startX = 0;
    /// Horizontal map/scene Y coordinate. The engine field was historically
    /// surfaced as Z, but it is not height.
    int32_t startY = 0;
    int32_t targetX = 0;
    /// Horizontal map/scene Y coordinate. The engine field was historically
    /// surfaced as Z, but it is not height.
    int32_t targetY = 0;
    /// Decoded actor hash index, or -1 when no actor source is encoded.
    int32_t sourceEntity = -1;
    /// Decoded actor hash index, or -1 when no actor target is encoded.
    int32_t targetEntity = -1;
    int32_t spotAnimId = 0;
    int32_t startTick = 0;
    int32_t endTick = 0;
    int32_t sceneX = 0;
    /// Current vertical position.
    int32_t height = 0;
    /// Current precise horizontal scene Y position.
    int32_t sceneY = 0;
    uint32_t yaw = 0;
    uint32_t pitch = 0;
    uint8_t moved = 0;
    int32_t worldX = 0;
    int32_t worldY = 0;
    /// Raw packed signed game value. 0 = none, >0 = NPC + 1, <0 = ~(player hash).
    int32_t rawSourceEntity = 0;
    /// Raw packed signed game value. 0 = none, >0 = NPC + 1, <0 = ~(player hash).
    int32_t rawTargetEntity = 0;
    /// EntityType::PLAYER / NPC / NONE for the decoded sourceEntity hash.
    int32_t sourceEntityType = EntityType::NONE;
    /// EntityType::PLAYER / NPC / NONE for the decoded targetEntity hash.
    int32_t targetEntityType = EntityType::NONE;
};

/// Runtime animation sequence snapshot. Pointer fields are raw game-memory
/// pointers exposed for advanced inspection; arrays are not copied across the
/// ABI.
struct SequenceState {
    /// Raw `Sequence*` pointer. Zero means no active sequence.
    uint64_t ptr = 0;
    int32_t id = 0;
    uint32_t flags = 0;
    int32_t frameCount = 0;
    uint64_t frameIds = 0;
    uint64_t frameLengths = 0;
    int32_t totalDuration = 0;
    int32_t frameStep = 0;
    uint16_t repeatLimit = 0;
};

/// Map-tile graphics object (RuneLite's `GraphicsObject`) -- an active
/// spot animation rooted at `WorldView::GraphicsObjectList`. Spell impacts,
/// teleport puffs, item-drop sparkles, and similar tile-anchored effects
/// surface here. Added in SDK 57.
struct GraphicsObjectState {
    /// Pointer to the in-memory `MapSpotAnim` -- used as the diff key for
    /// spawn / despawn detection. Same identity semantics as
    /// `ProjectileState::basePtr`.
    uint64_t basePtr = 0;
    /// Floor plane (0..3) the spot anim lives on.
    int32_t plane = 0;
    /// Spot-anim definition id (index into the SpotAnim def cache).
    int32_t spotAnimId = 0;
    /// Game cycle the spot anim started on (server tick the MAP_ANIM
    /// packet fired). Combined with the def's duration drives client-side
    /// timing for things like Vengeance recoil flashes.
    int32_t startCycle = 0;
    /// World-space height offset (signed, added to the tile's terrain
    /// height before rendering).
    int32_t height = 0;
    /// Precise X coordinate (scene-local, 7-bit subtile precision).
    int32_t preciseX = 0;
    /// Precise Y coordinate (scene-local, 7-bit subtile precision). The
    /// engine field was historically surfaced as Z, but it is not height.
    int32_t preciseY = 0;
    /// Scene-local tile X (`preciseX >> 7`). Pre-shifted for convenience
    /// so JS / TS doesn't have to repeat the shift.
    int32_t sceneX = 0;
    /// Scene-local tile Y (`preciseY >> 7`).
    int32_t sceneY = 0;
    /// Absolute world tile X (`sceneX + WorldView::BaseX`).
    int32_t worldX = 0;
    /// Absolute world tile Y (`sceneY + WorldView::BaseY`).
    int32_t worldY = 0;
    /// Owning `WorldView*` pointer captured at construction. Plugins can
    /// cross-check against the live WorldView pointer to filter stale
    /// entries from other instances.
    uint64_t worldViewPtr = 0;
    /// Address of the inline `SeqState` subobject inside the MapSpotAnim
    /// (16 bytes). Zero when the analyzer didn't detect the field. Useful
    /// only for advanced sequence-state introspection -- the wrapper does
    /// NOT dereference it.
    uint64_t seqStateAddr = 0;
    /// Legacy pointer field name. From SDK 68 onward this aliases the active
    /// `Sequence*` pointer.
    uint64_t seqTypePtr = 0;
    /// Active `Sequence*`. Zero means the graphics object animation has
    /// finished/despawned.
    uint64_t seqPtr = 0;
    /// Active sequence id, or -1 when `seqPtr == 0`.
    int32_t animationId = -1;
    /// Embedded sequence-state timing fields, read as unsigned 16-bit values.
    uint16_t frameCycle = 0;
    uint16_t currentFrame = 0;
    uint16_t loopCount = 0;
    uint16_t totalCycle = 0;
    /// Snapshot of the active sequence layout. All fields are zero when
    /// `seqPtr == 0`.
    SequenceState animation = {};
    /// Owning WorldView id. `-1` means current/unknown for older snapshots.
    int32_t worldViewId = -1;
};

/// Camera position, orientation, viewport dimensions, and zoom level.
struct CameraState {
    int32_t posX = 0;
    int32_t posY = 0;
    int32_t posZ = 0;
    int32_t yaw = 0;
    int32_t pitch = 0;
    int32_t viewportW = 0;
    int32_t viewportH = 0;
    int32_t zoom = 0;
    uint8_t valid = 0;
};

/// Item definition snapshot for cross-DLL transfer.
struct ItemDefSnapshot {
    /// Caller-owned record contract. Callers initialize both fields before
    /// invoking HostApi::getItemDef; the host rejects unsupported versions or
    /// undersized records without modifying caller memory.
    uint32_t structSize = sizeof(ItemDefSnapshot);
    uint32_t apiVersion = kNativePayloadVersion;
    int32_t id = 0;
    char name[kMaxLabelLen] = {};
    uint8_t members = 0;
    uint8_t stackable = 0;
    uint8_t noted = 0;
    uint8_t reserved0 = 0;
    int32_t noteId = -1;
    int32_t linkedId = -1;
    char inventoryActions[kMaxActionCount][kMaxLabelLen] = {};
    char groundActions[kMaxActionCount][kMaxLabelLen] = {};
    /// Raw opcode-43 labels indexed by inventory action then submenu slot.
    /// Empty strings preserve positional gaps.
    char subOps[kMaxActionCount][kMaxItemSubOpCount][kMaxLabelLen] = {};
};

/// NPC definition snapshot for cross-DLL transfer.
struct NpcDefSnapshot {
    uint32_t structSize = sizeof(NpcDefSnapshot);
    uint32_t apiVersion = kNativePayloadVersion;
    int32_t id = 0;
    char name[kMaxLabelLen] = {};
    int32_t combatLevel = 0;
    int32_t size = 1;
    char actions[kMaxActionCount][kMaxLabelLen] = {};
    int32_t transformVarbit = -1;
    int32_t transformVarp = -1;
    int32_t transformDefault = -1;
};

/// Object definition snapshot for cross-DLL transfer.
struct ObjDefSnapshot {
    uint32_t structSize = sizeof(ObjDefSnapshot);
    uint32_t apiVersion = kNativePayloadVersion;
    int32_t id = 0;
    char name[kMaxLabelLen] = {};
    char actions[kMaxActionCount][kMaxLabelLen] = {};
    int32_t sizeX = 1;
    int32_t sizeY = 1;
    uint8_t blocksMovement = 0;
    uint8_t reserved0[3] = {};
    int32_t transformVarbit = -1;
    int32_t transformVarp = -1;
    int32_t transformDefault = -1;
};

/// CS2 script execution result for cross-DLL transfer.
static constexpr uint32_t kMaxCs2IntResults = 32;

struct Cs2ScriptResult {
    uint8_t success = 0;
    uint32_t intCount = 0;
    int32_t ints[kMaxCs2IntResults] = {};
};

/// Maximum SSO-inline string length for `TitanHookArg::stringVal` payloads
/// (eastl SSO cap on rev 237.5). Strings longer than this are rejected by
/// `runClientScriptTyped`. Added in SDK 42.
static constexpr uint32_t kMaxHookArgStringLen = 22;

/// Maximum number of typed args passed to `runClientScriptTyped`. The cap
/// is generous but bounded so fixed-capacity cross-DLL structs stay small.
/// Added in SDK 42.
static constexpr uint32_t kMaxHookArgs = 16;

/// Modifier-bit flags for `HostApi::sendKeyboardKey`. Added in SDK 42.
namespace KeyboardMods {
    constexpr uint32_t SHIFT = 1u;
    constexpr uint32_t CTRL  = 2u;
    constexpr uint32_t ALT   = 4u;
}

/// SpecialKey ordinals for `HostApi::sendKeyboardKey`. Kept numeric so JS /
/// TS bindings can embed the enum as plain ints. Added in SDK 42.
namespace KeyboardKey {
    constexpr int32_t ENTER       = 0;
    constexpr int32_t ESCAPE      = 1;
    constexpr int32_t BACKSPACE   = 2;
    constexpr int32_t DELETE_KEY  = 3;
    constexpr int32_t TAB         = 4;
    constexpr int32_t SPACE       = 5;
    constexpr int32_t HOME        = 6;
    constexpr int32_t END_KEY     = 7;
    constexpr int32_t PAGE_UP     = 8;
    constexpr int32_t PAGE_DOWN   = 9;
    constexpr int32_t INSERT      = 10;
    constexpr int32_t ARROW_UP    = 11;
    constexpr int32_t ARROW_DOWN  = 12;
    constexpr int32_t ARROW_LEFT  = 13;
    constexpr int32_t ARROW_RIGHT = 14;
    constexpr int32_t F1          = 15;
    constexpr int32_t F2          = 16;
    constexpr int32_t F3          = 17;
    constexpr int32_t F4          = 18;
    constexpr int32_t F5          = 19;
    constexpr int32_t F6          = 20;
    constexpr int32_t F7          = 21;
    constexpr int32_t F8          = 22;
    constexpr int32_t F9          = 23;
    constexpr int32_t F10         = 24;
    constexpr int32_t F11         = 25;
    constexpr int32_t F12         = 26;
    constexpr int32_t SHIFT       = 27;
    constexpr int32_t CONTROL     = 28;
    constexpr int32_t ALT         = 29;
}

/// Callback-phase ordinals for `HostApi::typeKeyboardString`. Selects
/// which dispatcher the completion callback is routed to. Added in SDK 43.
namespace KeyboardTypeCallbackPhase {
    constexpr int32_t PUMP_THREAD   = 0;  ///< MessagePumpDispatcher (default)
    constexpr int32_t CLIENT_TICK   = 1;  ///< GameThreadDispatcher::Phase::ClientTick
    constexpr int32_t PRE_GAME_LOOP = 2;  ///< GameThreadDispatcher::Phase::PreGameLoop
}

/// One typed CS2 script argument for `runClientScriptTyped`. Tag = 1
/// (int) or 2 (string). String payload is SSO-inline only: up to
/// `kMaxHookArgStringLen` bytes + NUL terminator. Added in SDK 42.
struct TitanHookArg {
    uint8_t type = 1;   ///< 1 = int, 2 = string
    int32_t intVal = 0;
    char    stringVal[kMaxHookArgStringLen + 1] = {};
};

/// Widget snapshot for cross-DLL transfer. Populated by HostApi::getWidget().
static constexpr uint32_t kMaxWidgetTextLen = 256;

struct WidgetState {
    int32_t screenX = 0;
    int32_t screenY = 0;
    int32_t width = 0;
    int32_t height = 0;
    int32_t relativeX = 0;
    int32_t relativeY = 0;
    int32_t scrollX = 0;
    int32_t scrollY = 0;
    int32_t type = 0;
    int32_t contentType = 0;
    int32_t opacity = 0;
    int32_t itemId = -1;
    int32_t itemQuantity = 0;
    int32_t parentId = -1;
    uint8_t hidden = 0;
    uint8_t selfHidden = 0;
    uint8_t visible = 0;
    char text[kMaxWidgetTextLen] = {};
    /// Packed widget id `(groupId << 16) | childId` echoed back by the host
    /// so consumers (IPC forwarders, future event callbacks, scripts) can
    /// identify the widget without threading the lookup key separately.
    /// Added in SDK 36; zero on pre-SDK-36 clients.
    int32_t packedId = 0;
    /// Primary native widget sprite id. Added in SDK 110. A value of -1 means
    /// the widget has no sprite or the SpriteId offset is unavailable.
    int32_t spriteId = -1;
};

/// Maximum retained dynamic-child depth for widget query snapshots. Matches
/// Widget Explorer's bounded recursive traversal.
constexpr uint32_t kMaxWidgetAddressDepth = 12;
/// Upper bound exported through the SDK wrappers for one dynamic-children
/// enumeration. Raised from 256 to 2048 in SDK 124; the bank items container
/// (~800-1220 children) is the sizing driver. The internal native reader is
/// bounded only by its own corruption ceiling, so larger widgets remain
/// readable and are truncated only when copied across this per-call bound.
constexpr uint32_t kMaxWidgetDynamicChildren = 2048;
constexpr uint32_t kMaxWidgetQueryResults = 50000;
constexpr uint32_t kAllWidgetGroups = 0xFFFFFFFFu;

/// Stable address of a flat widget or recursively nested dynamic child.
/// `rootPackedId` resolves through the flat group table. The first `depth`
/// entries of `slots` then select native dynamic-child slots beneath it.
struct WidgetAddressState {
    uint32_t rootPackedId = 0;
    uint32_t depth = 0;
    int32_t slots[kMaxWidgetAddressDepth] = {};
};

/// Widget query snapshot: normal widget fields plus an exact retained address.
struct WidgetQueryState {
    WidgetState widget = {};
    WidgetAddressState address = {};
};

/// Varbit definition snapshot for cross-DLL transfer.
///
/// `source` indicates where the `{varpIndex, lowBit, highBit}` triple was
/// resolved from (SDK 58+):
///   0 = `VarbitDefSource::LiveCache` -- read from the game's in-memory
///       VarBitType hash cache. Kept for older hosts and diagnostics.
///   1 = `VarbitDefSource::Native` -- decoded via the native GET_VARBIT
///       call which JS5-loads the type on demand. Kept for diagnostics and
///       compatibility.
///   2 = `VarbitDefSource::Disk` -- read from Titan-owned JS5 cache data.
///       Current hosts return this for `HostApi::getVarbitDef`; the normal
///       plugin definition path does not walk live VarBitType state.
/// Pre-SDK-58 clients leave `source` at 0 (LiveCache default) so old
/// plugins keep their existing semantics.
struct VarbitDefSnapshot {
    uint32_t structSize = sizeof(VarbitDefSnapshot);
    uint32_t apiVersion = kNativePayloadVersion;
    int32_t id = 0;
    int32_t varpIndex = 0;
    int32_t lowBit = 0;
    int32_t highBit = 0;
    uint8_t source = 0;
    uint8_t reserved0[3] = {};
};

/// Origin of a `VarbitDefSnapshot`. See `VarbitDefSnapshot::source`.
enum class VarbitDefSource : uint8_t {
    LiveCache = 0,
    Native    = 1,
    Disk      = 2,
};

/// Identity + lifecycle snapshot for an installed plugin. Returned by the
/// plugin-manager queries (listPlugins, getPlugin).
struct PluginInfo {
    char    id[TitanNativeRecords::kMaxIdLen] = {};
    char    name[TitanNativeRecords::kMaxNameLen] = {};
    uint8_t enabled = 0;
    uint8_t hasPanel = 0;
    /// Metadata strings (SDK 23). Empty when the plugin did not declare them
    /// via TITAN_PLUGIN_META (native) or the TypeScript equivalent (script).
    char    description[kMaxDescriptionLen] = {};
    char    author[kMaxAuthorLen] = {};
    char    version[kMaxVersionLen] = {};
};

/// Per-slot inventory item snapshot.
struct InventoryItemState {
    int32_t slot = -1;        ///< 0..27 within the inventory widget
    int32_t itemId = -1;
    int32_t quantity = 0;
    char    name[kMaxLabelLen] = {};
};

/// Upper bound exported through the fixed native plugin ABI for a single
/// ItemContainer snapshot. Raised from 1024 to 2048 in SDK 111. The internal
/// native reader is not limited by this value; larger future containers remain
/// readable and are truncated only when copied across this legacy fixed ABI.
constexpr uint32_t kMaxItemContainerSlots = 2048;

/// Snapshot of a single item container (inventory, bank, equipment, ...).
/// Populated by `HostApi::getItemContainer()` from the native ClientInvCache
/// hashtable. Added in SDK 26; native-cache backing and validation were added
/// later without changing the public container-id contract.
struct ItemContainerState {
    /// RuneLite-style container id: INVENTORY=93, EQUIPMENT=94, BANK=95,
    /// ...; see `titan::InventoryID` for the named set.
    int32_t containerId = -1;
    /// Total slot count in the native ClientInvCache vectors.
    int32_t capacity = 0;
    /// Number of occupied entries emitted into `items` / `quantities`.
    /// Always <= min(capacity, kMaxItemContainerSlots).
    int32_t writtenCount = 0;
    /// Slot index per occupied entry. Empty slots are skipped: `slots[i]`
    /// is the real slot index of the i-th emitted item.
    int32_t slots[kMaxItemContainerSlots] = {};
    int32_t itemIds[kMaxItemContainerSlots] = {};
    int32_t quantities[kMaxItemContainerSlots] = {};
};

/// Event payload delivered to plugins when an item container's contents
/// change. Detection is tick-level diff on this revision (no native
/// single-chokepoint write hook exists; rev 237.5 writes widget slots
/// directly in TCP_IN + the CS2 `cc_setobject` handler). The host samples
/// the container at game-tick cadence and fires this event when the
/// resulting snapshot differs from the previous one for the same
/// container id. Fields mirror `ItemContainerState` exactly plus a tick
/// stamp. Added in SDK 26.
struct ItemContainerChangedEvent {
    int32_t containerId = -1;
    int32_t capacity = 0;
    int32_t writtenCount = 0;
    int32_t slots[kMaxItemContainerSlots] = {};
    int32_t itemIds[kMaxItemContainerSlots] = {};
    int32_t quantities[kMaxItemContainerSlots] = {};
    /// Current game tick captured when the diff was observed; 0 when the
    /// tick count offset is unavailable.
    int32_t gameTick = 0;
};

/// Runtime ItemDef snapshot (RuneLite's `ItemComposition` equivalent).
/// Returned by `HostApi::getItemComposition()`. Distinct from the
/// cache-only `ItemDefSnapshot`: game-thread calls may use the GAME's runtime
/// lookup (`ITEM_DEF_LOOKUP = FUN_005be060` on rev 237.5) so varbit/varp
/// transforms apply and runtime inventory-action/sub-operation slot layouts are
/// preserved exactly. Off-thread calls use the live table or raw cache only.
/// When no live definition is available (including an off-thread table miss),
/// `runtimeResolved` is 0 and the fields fall back to a cache read -- plugin
/// code can branch on that flag when it needs strict runtime fidelity. Added
/// in SDK 26.
constexpr uint32_t kMaxItemCompositionActions = 32;
constexpr uint32_t kMaxItemCompositionActionLen = kMaxLabelLen;

struct ItemCompositionState {
    int32_t id = 0;
    char    name[kMaxLabelLen] = {};
    uint8_t stackable = 0;
    /// The other item id in the note pair, in either direction:
    /// unnoted -> noted, noted -> unnoted. -1 when there is no note pair.
    int32_t linkedNoteId = -1;
    /// Number of inventory-action slots copied into `inventoryActions`.
    /// Runtime snapshots preserve positional gaps exactly; cache fallback
    /// returns the raw 5-slot cache encoding.
    uint32_t inventoryActionsCount = 0;
    /// Action strings from the runtime ItemDef / cache fallback. Empty
    /// strings are meaningful positional gaps and are not compacted.
    /// Capacity matches ItemDefRuntime's sanity cap.
    char inventoryActions[kMaxItemCompositionActions][kMaxItemCompositionActionLen] = {};
    /// 1 when the fields came from the live table/native resolver. 0 when we
    /// fell back to CacheReader's raw 5-slot inventory-action array.
    uint8_t runtimeResolved = 0;
    /// Opcode-43 submenu labels indexed by inventory action then submenu
    /// slot. Live runtime snapshots are populated from the analyzer-described
    /// nested ItemDef vectors; cache fallback uses the raw cache definition.
    /// Empty strings preserve the fixed positional shape.
    char subOps[kMaxActionCount][kMaxItemSubOpCount][kMaxLabelLen] = {};
};

/// Maximum number of worlds returned by `HostApi::getWorldList`. OSRS
/// has historically peaked around 500 worlds globally; 2048 is ample
/// headroom without being wasteful.
constexpr uint32_t kMaxWorldListEntries = 2048;

/// Per-world metadata. Mirrors the native GameWorld layout fields the
/// analyzer detects. Strings are fixed-capacity ASCII for ABI
/// stability; actual content is truncated to @c kMaxLabelLen.
/// Added in SDK 28.
struct WorldState {
    int32_t id = 0;
    /// Raw flags int. Bit 0 = members, bit 16 = beta (on 237.5).
    /// Use `WorldState::isMembers()` / `WorldState::isBeta()` in the
    /// C++ facade rather than bit-twiddling plugin-side.
    uint32_t flags = 0;
    /// First eastl::basic_string from the GameWorld entry (usually the
    /// activity / mini-game / world-type tag).
    char string0[kMaxLabelLen] = {};
    /// Second eastl::basic_string from the GameWorld entry (usually the
    /// location / region).
    char string1[kMaxLabelLen] = {};
};

/// SLR-backed world metadata from Jagex's official Old School world list.
/// Added in SDK 83. This is intentionally separate from WorldState, which
/// remains the native GameWorld snapshot for compatibility.
struct WorldMetadataState {
    int32_t id = 0;
    uint32_t flags = 0;
    char host[kMaxLabelLen] = {};
    char activity[kMaxLabelLen] = {};
    uint8_t location = 0;
    uint8_t padding1 = 0;
    int16_t population = 0;
    int32_t pingMs = -1;
    char region[kMaxLabelLen] = {};
};

/// Native Client.GameState enum exposed via `HostApi::getLoginAccountState()`.
namespace LoginGameStateAbi {
    constexpr int32_t UNKNOWN             = -1;
    constexpr int32_t LOGIN_SCREEN        = 10;
    constexpr int32_t LOGIN_AUTHENTICATOR = 11;
    constexpr int32_t LOGGING_IN          = 20;
    constexpr int32_t LOADING             = 25;
    constexpr int32_t LOGGED_IN           = 30;
    constexpr int32_t HOPPING             = 45;
}

/// Snapshot of the current login-screen / account state. Populated by
/// `HostApi::getLoginAccountState()`. Strings are fixed-capacity ASCII and
/// truncated to @c kMaxLabelLen.
/// Added in SDK 19.
struct LoginAccountState {
    /// Raw native login index (analyzer-derived semantics; see `osrs::loginIndex`).
    int32_t loginIndex = -1;
    /// `LoginGameStateAbi` value read from native Client.GameState.
    int32_t gameState = LoginGameStateAbi::UNKNOWN;
    /// Active title-screen field toggle: 0=username, 1=password, -1=unknown.
    int32_t fieldToggle = -1;
    /// Feature flags populated by the host based on analyzer coverage.
    uint8_t oauthSwitchAvailable = 0;
    uint8_t credentialSetAvailable = 0;
    uint8_t displayNameAvailable = 0;
    char username[kMaxLabelLen] = {};
    char displayName[kMaxLabelLen] = {};
};

/// Versioned capability and transition snapshot for login orchestration.
/// Kept separate from the SDK-19 LoginAccountState so older plugins can keep
/// passing the smaller legacy record without a host-side overwrite.
inline constexpr uint32_t kLoginFlowApiVersion = 1;
struct LoginFlowState {
    uint32_t structSize = sizeof(LoginFlowState);
    uint32_t apiVersion = kLoginFlowApiVersion;
    int32_t gameState = LoginGameStateAbi::UNKNOWN;
    int32_t loginIndex = -1;
    int32_t jagexLauncherIndex = 0;
    uint8_t oauthSetterAvailable = 0;
    uint8_t sessionSetterAvailable = 0;
    uint8_t launcherSubmitAvailable = 0;
    uint8_t standardAcknowledgeAvailable = 0;
    uint8_t worldReady = 0;
    uint8_t reserved[3]{};
    uint64_t transitionGeneration = 0;
    uint64_t loggingInGeneration = 0;
    uint64_t failedLoginGeneration = 0;
};
static_assert(sizeof(LoginFlowState) == 56,
              "sanitized LoginFlowState must not grow account string storage");

namespace LoginOperationAdvanceAbi {
    constexpr uint8_t UNAVAILABLE = 0;
    constexpr uint8_t PENDING = 1;
    constexpr uint8_t COMPLETED = 2;
    constexpr uint8_t FAILED = 3;
}

/// Event payload delivered to plugins when a CS2 script executes.
struct ScriptFiredEvent {
    int32_t scriptId = 0;
    int32_t intArgCount = 0;
    int32_t intArgs[kMaxCs2IntResults] = {};
    int32_t intResultCount = 0;
    int32_t intResults[kMaxCs2IntResults] = {};
};

/// Event payload delivered to plugins when a varbit's resolved value changes.
/// Fires only after SET_VARBIT has been observed to actually change the
/// extracted bit-field (no-op writes are filtered out by the host).
/// Added in SDK 21.
struct VarbitChangedEvent {
    /// Varbit type id (index into the VarBitType cache), i.e. the first arg
    /// passed to the native SetVarbit function.
    int32_t varbitId = 0;
    /// Resolved value of the varbit before the write (using the VarBitType
    /// entry's baseVarp + low/high bit range to extract from the VarPlayer
    /// array).
    int32_t oldValue = 0;
    /// Resolved value of the varbit after the write.
    int32_t newValue = 0;
    /// Current game tick (Client.getTickCount) captured at dispatch time;
    /// 0 if the tick-count offset is unavailable.
    int32_t gameTick = 0;
};

/// Event payload delivered when native SetGameState accepts a state change.
/// Values are LoginGameStateAbi native client values, including LOADING (25).
/// Added in SDK 91.
struct GameStateChangedEvent {
    int32_t oldState = LoginGameStateAbi::UNKNOWN;
    int32_t newState = LoginGameStateAbi::UNKNOWN;
    int32_t tickCount = 0;
};

/// Maximum length of a chat message payload (bytes, excluding NUL). Large
/// enough for any realistic in-game line (public chat caps are far smaller)
/// while keeping the event struct a fixed-size, POD copy.
constexpr uint32_t kMaxChatMessageLen = 512;

/// Event payload delivered to plugins when the native chat pipeline adds a
/// line to the in-game chatbox. Fires for every `AddChat` arrival --
/// server-delivered chat, local system messages, and plugin-injected lines
/// via `HostApi::addChatMessage`. All strings are fixed-capacity ASCII,
/// truncated silently when the source exceeds the buffer.
/// Added in SDK 22.
struct ChatMessageEvent {
    /// Chat type. Ordinals match RuneLite's ChatMessageType 1:1, see
    /// https://github.com/runelite/runelite/blob/master/runelite-api/src/main/java/net/runelite/api/ChatMessageType.java
    /// Common values: 0 GAMEMESSAGE, 2 PUBLICCHAT, 3 PRIVATECHAT,
    /// 14 BROADCAST, 41 CLAN_CHAT, 99 CONSOLE, 108 WELCOME. Any ordinal
    /// emitted by the native AddChat pipeline reaches the listener
    /// unchanged; filter client-side.
    int32_t type = 0;
    /// Sender display name. Empty on system / server messages.
    char name[kMaxLabelLen] = {};
    /// Rendered chat text (may contain colour tags like {@code <col=ff0000>}).
    char message[kMaxChatMessageLen] = {};
    /// Sender prefix string (used by clan/group chat for the clan name;
    /// empty on most message types).
    char sender[kMaxLabelLen] = {};
    /// Current game tick captured at dispatch time; 0 when unavailable.
    int32_t gameTick = 0;
};

/// Event payload delivered to plugins when the player activates a menu option.
/// Plugins can set `consumed` to 1 to prevent the action from reaching the game.
/// `identifier` is the menu-entry identity field.
struct MenuOptionClickedEvent {
    uint32_t opcode = 0;
    int32_t identifier = 0;
    int32_t param0 = 0;
    int32_t param1 = 0;
    int32_t worldViewId = -1;
    int32_t clickX = 0;
    int32_t clickY = 0;
    char actionText[kMaxLabelLen] = {};
    char targetText[kMaxLabelLen] = {};
    uint8_t consumed = 0;
    uint8_t replaced = 0;
    uint32_t replacementOpcode = 0;
    int32_t replacementIdentifier = 0;
    int32_t replacementParam0 = 0;
    int32_t replacementParam1 = 0;
    uint32_t replacementWorldViewId = 0;
    int32_t replacementClickX = 0;
    int32_t replacementClickY = 0;
    char replacementActionText[kMaxLabelLen] = {};
    char replacementTargetText[kMaxLabelLen] = {};
};

/// Discriminates the source of an `onSoundPlayed` event.
enum SoundKind : int32_t {
    SOUND_KIND_SYNTH = 0,   ///< Queued JagFX/wave sound effect (combat, spells, NPCs, area sounds).
    SOUND_KIND_JINGLE = 1,  ///< MIDI jingle (level-ups, quests, music stings).
};

/// Event payload delivered to plugins when the native client plays a sound.
/// Covers two kinds (see `kind`): queued synth/wave sound effects (captured at
/// the queue drain) and MIDI jingles (captured at `PlayJingle`). Plugins set
/// `consumed` to 1 to mark the event handled for handler ordering; the global
/// `setAudioPlaybackDisabled` toggle suppresses playback. `gameTick` is
/// captured at dispatch time, 0 when unavailable.
///
/// Field meaning by kind:
///   - Synth : `soundId` JagFX id, `loops` loop count, `packedPos` packed
///             position/range, `durationMs` = -1.
///   - Jingle: `soundId` jingle id, `durationMs` ms, `loops`/`packedPos` = -1.
struct SoundPlayedEvent {
    int32_t kind = 0;        ///< SoundKind (synth / jingle).
    int32_t soundId = 0;
    int32_t loops = 0;       ///< Synth loop count; -1 for jingles.
    int32_t durationMs = 0;  ///< Jingle duration in ms; -1 for synths.
    int32_t packedPos = 0;   ///< Synth packed position/range; -1 for jingles.
    int32_t gameTick = 0;
    uint8_t consumed = 0;
};

/// Which physical mouse button an `onMousePressed`/`onMouseReleased` event
/// refers to. Kept numeric so JS/TS bindings can embed the enum as plain ints.
/// Added in SDK 115.
namespace MouseButton {
    constexpr int32_t LEFT   = 0;
    constexpr int32_t RIGHT  = 1;
    constexpr int32_t MIDDLE = 2;
}

/// Event payload delivered to plugins for a real (non-synthetic) mouse button
/// press or release, before the native client processes it. `x`/`y` are the
/// game-window client-area pixels of the cursor (the same physical pixel space
/// as HostApi::getMousePos and WorldMapState viewport bounds). `button` is a
/// MouseButton ordinal; `modifiers` is a KeyboardMods bitmask (SHIFT/CTRL/ALT)
/// sampled at the moment of the click. A plugin sets `consumed` to 1 on a
/// PRESS to stop the native client seeing the click; the host then also
/// suppresses the matching release so the game's button state cannot desync
/// (a stuck drag/camera). Setting `consumed` on a RELEASE has no suppression
/// effect -- press consumption alone governs the pair, so a release whose
/// press went through natively can never strand the game down-without-up.
/// Double-click messages arrive as an extra press.
///
/// SUPPRESSION SCOPE: consumption suppresses the click in the game's
/// WndProc-fed (V1) input pipeline only. The game's low-level (V2) input
/// pipeline observes real hardware input independently, so a consumed click
/// is best-effort gameplay suppression, not input invisibility.
///
/// THREADING: unlike the game-thread event callbacks, these fire on the input
/// (message-pump) thread, which may not be the game loop. Do not read game
/// memory or call titan::* game-state/action queries from the handler; copy the
/// fields out and act from a game-thread callback such as onClientTick.
/// Added in SDK 115.
struct MouseButtonEvent {
    int32_t x = 0;
    int32_t y = 0;
    int32_t button = 0;     ///< MouseButton ordinal (LEFT/RIGHT/MIDDLE).
    int32_t modifiers = 0;  ///< KeyboardMods bitmask sampled at click time.
    int32_t gameTick = 0;   ///< Current game tick at capture time, or 0.
    uint8_t consumed = 0;
};

/// Event payload delivered to plugins when the native client applies a
/// hitsplat/hitmark to a player or NPC. The hook's raw actor pointer is used
/// only inside the host to resolve `actorType` plus the matching entity
/// snapshot; public JS/Java wrappers expose `actor` as a typed Player/NPC
/// object or null when the actor cannot be resolved. Added in SDK 74;
/// corrected in SDK 76 to match the real HITSPLAT_ADDER signature.
struct HitsplatAppliedEvent {
    /// TitanPluginSdk::EntityType: PLAYER, NPC, or NONE when unresolved.
    uint8_t actorType = EntityType::NONE;
    uint8_t reserved[7] = {};
    /// Populated when actorType == EntityType::PLAYER.
    PlayerState player = {};
    /// Populated when actorType == EntityType::NPC.
    NpcState npc = {};
    int32_t type = 0;      ///< Native hitsplat type id.
    int32_t value = 0;     ///< Damage/value payload.
    int32_t limit = 0;     ///< Native limit field.
    int32_t delay = 0;     ///< Native delay field.
    int32_t cycle = 0;     ///< Native cycle field.
    int32_t gameTick = 0;  ///< Current game tick at capture time, or 0.
};

/// Event payload delivered to plugins when the native client applies an
/// actor-attached spotanim to a player or NPC. Clear/removal ids (`id == -1`
/// or `id == 0xffff`) are filtered before dispatch. This is distinct from
/// hitsplats and from map-tile GraphicsObject spotanims.
struct ActorSpotAnimEvent {
    uint8_t actorType = EntityType::NONE;
    uint8_t reserved[7] = {};
    PlayerState player = {};
    NpcState npc = {};
    int32_t slot = 0;
    int32_t id = -1;
    int32_t height = 0;
    int32_t delay = 0;
    int32_t cycle = 0;
    int32_t gameTick = 0;
};

/// Event payload delivered to plugins when the native client accepts an
/// actor animation change through ACTOR_ANIMATION / SetAnimation. Same-value
/// resets and rejected lower-priority native requests are filtered by comparing
/// Actor::Animation before and after the original setter returns.
struct AnimationChangedEvent {
    uint8_t actorType = EntityType::NONE;
    uint8_t reserved[7] = {};
    PlayerState player = {};
    NpcState npc = {};
    int32_t oldAnimation = -1;
    int32_t newAnimation = -1;
    int32_t gameTick = 0;
};

/// Accepted utterance snapshot. Text is UTF-8 and may contain embedded NULs.
/// overheadText is borrowed only for this callback; copy exactly overheadTextLength
/// bytes to retain it. No C++ allocation ownership crosses the DLL boundary.
struct OverheadTextChangedEvent {
    uint8_t actorType = EntityType::NONE;
    uint8_t reserved[7] = {};
    PlayerState player = {};
    NpcState npc = {};
    const char* overheadText = nullptr;
    uint64_t overheadTextLength = 0;
    int32_t gameTick = 0;
};

namespace OverheadTextReadResult {
    constexpr uint8_t Invalid = 0;
    constexpr uint8_t Success = 1;
    constexpr uint8_t BufferTooSmall = 2;
}
namespace OverheadTextCapability {
    constexpr uint32_t DirectAccess = 1;
    constexpr uint32_t Events = 2;
}

// --- SDK 131: full-frame game screenshot ---------------------------------

constexpr uint32_t kScreenshotApiVersion = 1;
/// Outstanding (unreleased) screenshot requests per client; submit fails past it.
constexpr uint32_t kScreenshotMaxUnreleased = 4;
/// Upper bound for one encoded PNG. The raw frame shares the 16 MiB cap of
/// the controller's /tabs capture; the host fails a request whose PNG would
/// exceed this rather than hand back a truncated image.
constexpr uint32_t kScreenshotMaxPngBytes = 16u * 1024u * 1024u;
constexpr uint32_t kScreenshotMessageCapacity = 128;

namespace ScreenshotPhase {
constexpr uint32_t None = 0;     ///< Unknown or released handle.
constexpr uint32_t Pending = 1;  ///< Waiting for a presented frame, or encoding it.
constexpr uint32_t Ready = 2;    ///< PNG available through screenshotCopyPng.
constexpr uint32_t Failed = 3;   ///< See ScreenshotStatusState::message.
}

/// One screenshot request's status. Caller-initialized header, like the web
/// walker records: the host rejects a stale structSize/apiVersion.
struct ScreenshotStatusState {
    uint32_t structSize = sizeof(ScreenshotStatusState);
    uint32_t apiVersion = kScreenshotApiVersion;
    uint64_t requestId = 0;
    uint32_t phase = ScreenshotPhase::None;
    uint32_t width = 0;     ///< Captured frame size in pixels; zero until Ready.
    uint32_t height = 0;
    uint32_t pngBytes = 0;  ///< Encoded size; zero until Ready.
    char message[kScreenshotMessageCapacity] = {};
};
static_assert(sizeof(ScreenshotStatusState) == 160,
              "ScreenshotStatusState ABI size changed");

/// C-layout synthetic action entry for struct-based dispatch across the DLL boundary.
/// Equivalent to passing all executeSyntheticAction parameters as a single struct.
/// `identifier` is the menu-entry identity field.
/// Leave clickX/clickY at -1/-1 to randomize the synthetic click on the
/// active game screen.
struct SyntheticActionEntry {
    uint32_t opcode = 0;
    int32_t identifier = 0;
    int32_t param0 = 0;
    int32_t param1 = 0;
    int32_t worldViewId = -1;
    int32_t clickX = -1;
    int32_t clickY = -1;
    const char* actionText = nullptr;
    const char* targetText = nullptr;
    /// Suppress the synthetic click phase and dispatch DoAction directly.
    /// Applies to both packet and WndProc input modes.
    uint8_t skipClick = 0;
    /// Optional target metadata for target-aware clickbox resolution. Leave
    /// these at defaults when dispatching a raw captured menu tuple.
    int32_t targetPlane = -1;
    int32_t targetSizeX = 1;
    int32_t targetSizeY = 1;
    int32_t targetLayer = -1;
    uint64_t targetEntityPtr = 0;
    uint64_t targetPackedId = 0;
};

/// SDK 128. The host copies all values/text before returning. Source is opcode
/// 25 with packed widget in param1 and child in param0. Inventory callers pin
/// its item; spell callers can leave the item unspecified for live binding.
/// Both click flags must agree. True means queued, not successful game action.
struct SelectedActionPair {
    SyntheticActionEntry source;
    SyntheticActionEntry target;
    uint8_t hasExpectedSourceItem = 0;
    int32_t expectedSourceItemId = -1;
};

/// C-layout request for resolving the click point Titan would use for a
/// target-aware menu action. This does not dispatch the action.
struct ActionClickPointSpec {
    uint32_t opcode = 0;
    int32_t identifier = 0;
    int32_t param0 = 0;
    int32_t param1 = 0;
    /// -1 = current WorldView, 0 = top-level, >0 = concrete WorldView id.
    int32_t worldViewId = -1;
    int32_t targetPlane = -1;
    int32_t targetSizeX = 1;
    int32_t targetSizeY = 1;
    int32_t targetLayer = -1;
    uint64_t targetEntityPtr = 0;
    uint64_t targetPackedId = 0;
};

// --- Break Handler registry (SDK 97) -------------------------------

constexpr uint32_t kBreakRegistryApiVersion = 1;
constexpr uint32_t kBreakPluginIdCapacity = 64;
constexpr uint32_t kBreakDisplayNameCapacity = 96;
constexpr uint32_t kBreakReasonCapacity = 192;

/// Coordinator command phase. Values are stable across the native, Java,
/// and JavaScript bridges.
enum BreakPhaseAbi : uint8_t {
    BREAK_PHASE_NONE = 0,
    BREAK_PHASE_PREPARE = 1,
    BREAK_PHASE_ACTIVE = 2,
    BREAK_PHASE_RESUME = 3,
};

enum BreakModeAbi : uint8_t {
    BREAK_MODE_AFK = 0,
    BREAK_MODE_LOGOUT = 1,
};

/// Registration role copied into the host-owned registry. The host resolves
/// all other identity metadata (runtime and load generation) from the exact
/// plugin instance supplied alongside this record.
enum BreakRegistrationRoleAbi : uint8_t {
    BREAK_REGISTRATION_PARTICIPATES = 0,
    BREAK_REGISTRATION_OWNS_SCHEDULE = 1,
};

enum BreakReportStateAbi : uint8_t {
    BREAK_REPORT_NONE = 0,
    BREAK_REPORT_RUNNING = 1,
    BREAK_REPORT_PREPARING = 2,
    BREAK_REPORT_SAFE_PAUSED = 3,
    BREAK_REPORT_DEFERRED = 4,
    BREAK_REPORT_ERROR = 5,
};

/// Native participant registration request. This record is caller-owned and
/// contains no pointers, callbacks, or dynamically-owned values. pluginId and
/// displayName must be NUL-terminated inside their fixed buffers.
struct BreakRegistrationState {
    uint32_t structSize = 0;
    uint32_t apiVersion = kBreakRegistryApiVersion;
    uint8_t role = BREAK_REGISTRATION_PARTICIPATES;
    uint8_t reserved[7] = {};
    char pluginId[kBreakPluginIdCapacity] = {};
    char displayName[kBreakDisplayNameCapacity] = {};
};

/// Native participant report request. epoch may be zero to select the exact
/// epoch most recently observed by this registration through poll(); a
/// non-zero value must equal that observed epoch. The host always verifies
/// the observed epoch against the current command before accepting a report.
struct BreakReportState {
    uint32_t structSize = 0;
    uint32_t apiVersion = kBreakRegistryApiVersion;
    uint64_t epoch = 0;
    uint8_t state = BREAK_REPORT_NONE;
    uint8_t reserved[7] = {};
    uint32_t code = 0;
    uint32_t retryAfterMs = 0;
    char pluginId[kBreakPluginIdCapacity] = {};
    char reason[kBreakReasonCapacity] = {};
};

static_assert(sizeof(BreakRegistrationState) == 176,
              "BreakRegistrationState ABI size changed");
static_assert(sizeof(BreakReportState) == 288,
              "BreakReportState ABI size changed");

/// One immutable command copied from the host-owned registry. The struct is
/// caller-owned; strings are always NUL-terminated by the host.
struct BreakCommandState {
    uint32_t structSize = 0;
    uint32_t apiVersion = kBreakRegistryApiVersion;
    uint64_t epoch = 0;
    uint8_t phase = BREAK_PHASE_NONE;
    uint8_t mode = BREAK_MODE_AFK;
    uint8_t reserved[6] = {};
    char triggeringOwnerId[kBreakPluginIdCapacity] = {};
};

/// Coordinator-facing snapshot of a registration. No plugin-language object,
/// native pointer, callback, or dynamically-owned string crosses this record.
struct BreakParticipantState {
    uint32_t structSize = 0;
    uint32_t apiVersion = kBreakRegistryApiVersion;
    uint64_t token = 0;
    uint64_t generation = 0;
    /// Monotonic registration-local start/stop transition counter. Lets the
    /// coordinator observe stop->start cycles that occur between snapshots.
    uint64_t activityGeneration = 0;
    uint64_t lastObservedEpoch = 0;
    uint8_t runtimeKind = 0; // PluginRuntimeKind: native=0, script=1, java=2
    uint8_t configurable = 0;
    uint8_t active = 0;
    uint8_t enabled = 0;
    uint8_t reportState = BREAK_REPORT_NONE;
    uint8_t reserved[3] = {};
    uint32_t reportCode = 0;
    uint32_t retryAfterMs = 0;
    char pluginId[kBreakPluginIdCapacity] = {};
    char displayName[kBreakDisplayNameCapacity] = {};
    char reportReason[kBreakReasonCapacity] = {};
    BreakCommandState command = {};
};

// --- Sanitized proxy route + login submission (SDK 97) ------------

constexpr uint32_t kProxyRouteApiVersion = 1;
constexpr uint32_t kProxyRouteIdCapacity = 64;
constexpr uint32_t kProxyRouteLabelCapacity = 128;
constexpr uint32_t kProxyFailureStageCapacity = 64;

enum ProxyRouteKindAbi : uint32_t {
    PROXY_ROUTE_DIRECT = 0,
    PROXY_ROUTE_PROXY = 1,
    PROXY_ROUTE_BLOCKED = 2,
};

struct SanitizedProxyRouteState {
    uint32_t structSize = 0;
    uint32_t apiVersion = kProxyRouteApiVersion;
    char proxyId[kProxyRouteIdCapacity] = {};
    char label[kProxyRouteLabelCapacity] = {};
};

struct ProxyRouteStatusState {
    uint32_t structSize = 0;
    uint32_t apiVersion = kProxyRouteApiVersion;
    uint64_t generation = 0;
    uint32_t kind = PROXY_ROUTE_DIRECT;
    int32_t failureCode = 0;
    char proxyId[kProxyRouteIdCapacity] = {};
    char failureStage[kProxyFailureStageCapacity] = {};
    /// Non-zero only when the active route generation completed its egress
    /// probe. The measured address remains host-internal.
    uint8_t egressReady = 0;
    uint8_t reserved[7] = {};
};
static_assert(sizeof(ProxyRouteStatusState) == 160,
              "ProxyRouteStatusState ABI size changed");

/// Function table provided by the host (injected client DLL) to plugins.
/// Covers game state queries, rendering primitives, input, walking, interactions,
/// and thread dispatch. Rendering pointers are null when loaded by the controller.
enum class GrandExchangeOfferState : int32_t {
    Unknown = -1, Empty = 0, CancelledBuy = 1, CancelledSell = 2,
    Buying = 3, Bought = 4, Selling = 5, Sold = 6,
};

/// Owned value snapshot. Slot indices are zero-based; monetary values are
/// lossless on both 32-bit and 64-bit native offer layouts. No native handles.
struct GrandExchangeOffer {
    int32_t slot = -1;
    int32_t itemId = 0;
    int32_t totalQuantity = 0;
    int32_t quantitySold = 0;
    int64_t price = 0;
    int64_t spent = 0;
    GrandExchangeOfferState state = GrandExchangeOfferState::Unknown;
    uint8_t status = 0;
    uint8_t type = 0;
};

/// SDK 138. Owned remembered bank snapshot for the current character. Known
/// empty is distinct from unknown. live means confirmed by an open bank now;
/// lastObservedAt is UTC Unix seconds. Stored slots are never action targets.
struct BankCacheState {
    ItemContainerState bank{};
    int64_t lastObservedAt = 0;
    uint8_t known = 0;
    uint8_t live = 0;
    uint8_t loading = 0;
    uint8_t reserved[5]{};
    char account[80]{};
    char error[256]{};
};

/// SDK 139. The C++ host owns the GE buy queue for all runtimes.
enum class GeRequestPhase : int32_t {
    Queued, Opening, Selecting, Quantity, Pricing, Confirming, Buying,
    Cancelling, Collecting, Completed, Cancelled, Failed
};
struct GeBuyOptions {
    int32_t itemId = -1;
    int32_t quantity = 0;
    int32_t maxAttempts = 3;
    int32_t waitPerAttemptMs = 10000;
    int32_t timeoutMs = 180000;
    uint8_t toInventory = 1;
    uint8_t noted = 1;
    uint8_t autoOpen = 0;
    uint8_t reserved = 0;
    /// 0 uses the guide price and the original utility's bounded +5% retries.
    /// A positive ceiling is checked against the actual UI price before confirm.
    int64_t maxUnitPrice = 0;
};
struct GeRequestState {
    uint64_t requestId = 0;
    int32_t itemId = -1;
    int32_t quantity = 0;
    int32_t filled = 0;
    int32_t remaining = 0;
    int32_t attempts = 0;
    int32_t slot = -1;
    GeRequestPhase phase = GeRequestPhase::Queued;
    int64_t unitPrice = 0;
    int64_t spent = 0;
    char message[256]{};
};

struct GrandExchangeOfferChangedEvent {
    GrandExchangeOffer offer{};
    int32_t slot = -1;
};

/// SDK 137. Optional amounts use present bits: 1 = buyLimit, 2 = highAlch.
/// UTF-8 strings are always terminated and never borrowed from client memory.
struct ItemPriceMetadata {
    int32_t id = 0;
    uint32_t present = 0;
    int64_t buyLimit = 0, highAlch = 0;
    uint8_t members = 0;
    uint8_t reserved[7]{};
    char name[257]{};
    char examine[4097]{};
};

/// present bits: 1 = high, 2 = low, 4 = highTime, 8 = lowTime.
/// Times are UTC Unix seconds. Missing values are distinct from a price of 0.
struct ItemPrice {
    int32_t id = 0;
    uint32_t present = 0;
    int64_t high = 0, low = 0, highTime = 0, lowTime = 0;
    int64_t fetchedAt = 0, lastAttemptAt = 0;
    uint8_t loading = 0, pending = 0;
    uint8_t reserved[6]{};
    char error[512]{};
};

struct ItemPriceStatus {
    int64_t catalogRevision = 0, catalogFetchedAt = 0, catalogLastAttemptAt = 0;
    int32_t pendingCount = 0, loadingItem = -1; // -1 idle, 0 catalog, >0 item id
    uint8_t available = 0, catalogLoading = 0, catalogPending = 0;
    uint8_t reserved[5]{};
    char error[512]{};
};

// --- Cross-Tab Store (SDK 141) -------------------------------------
//
// Values every tab launched by one controller sees for the life of the
// controller session. Each plugin owns one namespace, which the host stamps
// from the calling instance. Semantics: <titan/cross_tab.h>.

/// Keys are 1..kCrossTabMaxKeyLen characters of [A-Za-z0-9._:/-].
constexpr uint32_t kCrossTabMaxKeyLen = 63;
/// CrossTabChangeEvent::key capacity, NUL included.
constexpr uint32_t kCrossTabKeyCapacity = kCrossTabMaxKeyLen + 1;
/// Largest value, secret or not. Zero-length values are allowed.
constexpr uint32_t kCrossTabMaxValueBytes = 16u * 1024u;
/// crossTabRead's result when the key is absent or the call is refused.
constexpr uint32_t kCrossTabAbsent = 0xFFFFFFFFu;

/// CrossTabWrite::flags and CrossTabEntryInfo::flags. Values are stable
/// across the native, Java and JavaScript bridges.
enum CrossTabFlagAbi : uint32_t {
    CROSS_TAB_SECRET = 1u << 0,       ///< handle the value as a secret
    CROSS_TAB_ERASE = 1u << 1,        ///< write only: erase instead of put
    CROSS_TAB_CONDITIONAL = 1u << 2,  ///< write only: expectedVersion must match
    // 1u << 3 is reserved.
};

/// CrossTabChangeEvent::kind.
enum CrossTabChangeKindAbi : uint8_t {
    CROSS_TAB_CHANGE_SET = 0,       ///< the key is present at `version`
    CROSS_TAB_CHANGE_ERASED = 1,    ///< the key is absent
    CROSS_TAB_CHANGE_REJECTED = 2,  ///< write `writeId` was refused; re-read the key
};

/// CrossTabChangeEvent::origin.
enum CrossTabChangeOriginAbi : uint8_t {
    CROSS_TAB_ORIGIN_REMOTE = 0,   ///< another tab or plugin instance wrote it
    CROSS_TAB_ORIGIN_REPLAY = 1,   ///< the instance just bound: one event per key
    CROSS_TAB_ORIGIN_OUTCOME = 2,  ///< the result of this instance's own write
};

/// CrossTabChangeEvent::cause.
enum CrossTabChangeCauseAbi : uint8_t {
    CROSS_TAB_CAUSE_WRITER = 0,         ///< an ordinary write
    CROSS_TAB_CAUSE_SESSION_RESET = 1,  ///< the controller session ended
    CROSS_TAB_CAUSE_LIMIT = 2,          ///< a namespace or store limit
    CROSS_TAB_CAUSE_CONFLICT = 3,       ///< expectedVersion did not match
    CROSS_TAB_CAUSE_NOT_PERMITTED = 4,  ///< a secret from an attached tab, or signed out
};

/// One write. Caller-owned; the host copies the key and value before it
/// returns. Unknown flag bits, a non-zero expectedVersion without
/// CROSS_TAB_CONDITIONAL, and an erase carrying a value or the secret flag
/// are refused.
struct CrossTabWrite {
    uint32_t structSize = sizeof(CrossTabWrite);
    uint32_t flags = 0;             ///< CrossTabFlagAbi bits
    const char* key = nullptr;      ///< NUL-terminated
    const uint8_t* data = nullptr;  ///< put only; may be null when size is 0
    uint32_t size = 0;              ///< put only; at most kCrossTabMaxValueBytes
    uint32_t reserved = 0;
    uint64_t expectedVersion = 0;   ///< CROSS_TAB_CONDITIONAL only; 0 = absent
};
static_assert(sizeof(CrossTabWrite) == 40, "CrossTabWrite ABI size changed");

/// A key's state as this tab sees it. Caller-initialized header.
struct CrossTabEntryInfo {
    uint32_t structSize = sizeof(CrossTabEntryInfo);
    uint32_t flags = 0;       ///< CROSS_TAB_SECRET or 0
    uint64_t version = 0;     ///< controller-assigned; 0 while pending
    uint32_t size = 0;        ///< value bytes; 0 when redacted
    uint8_t pending = 0;      ///< this tab's own put, not yet confirmed
    uint8_t redacted = 0;     ///< a secret whose value this tab may not hold
    uint8_t reserved[2] = {};
};
static_assert(sizeof(CrossTabEntryInfo) == 24, "CrossTabEntryInfo ABI size changed");

/// One change to a key of the receiving plugin's namespace. Owned by the
/// host and valid only for the callback; it never carries the value.
struct CrossTabChangeEvent {
    uint32_t structSize = sizeof(CrossTabChangeEvent);
    char key[kCrossTabKeyCapacity] = {};
    uint8_t kind = CROSS_TAB_CHANGE_SET;
    uint8_t origin = CROSS_TAB_ORIGIN_REMOTE;
    uint8_t cause = CROSS_TAB_CAUSE_WRITER;
    uint8_t secret = 0;
    uint8_t redacted = 0;
    uint8_t reserved[7] = {};
    /// Set/Erased: the version it happened at. Outcome: the version the write
    /// got. Rejected: the key's confirmed version after the revert. 0 for a
    /// pending value, a session reset, and a key a full resync left out.
    uint64_t version = 0;
    /// Outcome and Rejected: the id crossTabWrite returned. Otherwise 0.
    uint64_t writeId = 0;
};
static_assert(sizeof(CrossTabChangeEvent) == 96, "CrossTabChangeEvent ABI size changed");

// --- Preview pills (SDK 142) ----------------------------------------
//
// Short labels a plugin pins over its tab's thumbnail on the controller's
// Home grid. Each plugin owns its own pills, which the host stamps from the
// calling instance. Semantics: <titan/preview_pills.h>.

/// Keys are 1..kPreviewPillMaxKeyLen characters of [A-Za-z0-9._:/-].
constexpr uint32_t kPreviewPillMaxKeyLen = 32;
/// Text past this many UTF-8 bytes is cut at a character boundary.
constexpr uint32_t kPreviewPillMaxTextBytes = 63;
/// Most pills one plugin shows at once.
constexpr uint32_t kPreviewPillsPerPlugin = 2;
/// Most pills one tab shows at once, across every plugin.
constexpr uint32_t kPreviewPillsPerTab = 8;
/// Longest countdown: 999:59:59. A longer one is shortened to it.
constexpr uint64_t kPreviewPillMaxCountdownMs = 3'599'999'000ull;

/// PreviewPillWrite::flags. Values are stable across the native, Java and
/// JavaScript bridges.
enum PreviewPillFlagAbi : uint32_t {
    /// Remove `key`. With a null or empty key, remove every pill this plugin
    /// shows. `text`, `tone` and `countdownMs` are ignored.
    PREVIEW_PILL_CLEAR = 1u << 0,
    /// Draw a live HH:MM:SS countdown of `countdownMs` after the text. The
    /// controller ticks it; nothing needs republishing while it runs.
    PREVIEW_PILL_COUNTDOWN = 1u << 1,
};

/// One pill write. Caller-owned; the host copies everything before it
/// returns. Unknown flag bits, CLEAR combined with COUNTDOWN, and a set
/// with empty text and no countdown are refused.
struct PreviewPillWrite {
    uint32_t structSize = sizeof(PreviewPillWrite);
    uint32_t flags = 0;          ///< PreviewPillFlagAbi bits
    const char* key = nullptr;   ///< NUL-terminated
    const char* text = nullptr;  ///< NUL-terminated UTF-8; may be null with a countdown
    int32_t tone = 0;            ///< titan::PanelTone value; unknown values draw neutral
    uint32_t reserved = 0;
    uint64_t countdownMs = 0;    ///< PREVIEW_PILL_COUNTDOWN only: time left from now
};
static_assert(sizeof(PreviewPillWrite) == 40, "PreviewPillWrite ABI size changed");

// Consumer-local dispatch view. Native ABI v1 imports negotiated functions
// into this allocation; this structure itself is not a cross-DLL contract.
struct HostApi {
    uint32_t sdkVersion = kSdkVersion;

    // --- Logging ---
    void (*log)(const char* msg) = nullptr;

    // --- Game state queries ---
    uint8_t (*getClientState)(ClientState* outState) = nullptr;
    uint32_t (*getPlayers)(PlayerState* outPlayers, uint32_t maxPlayers) = nullptr;
    uint8_t (*getPlayerComposition)(uint64_t playerEntityPtr,
                                    PlayerCompositionState* outComposition) = nullptr;
    uint32_t (*getNpcs)(NpcState* outNpcs, uint32_t maxNpcs) = nullptr;
    uint32_t (*getTileObjects)(int32_t radius, TileObjectState* outObjects, uint32_t maxObjects) = nullptr;
    uint32_t (*getGroundItems)(int32_t radius, GroundItemState* outItems, uint32_t maxItems) = nullptr;
    uint32_t (*getTileObjectsOnTile)(int32_t plane, int32_t tileX, int32_t tileY,
                                     TileObjectState* outObjects, uint32_t maxObjects) = nullptr;
    uint32_t (*getGroundItemsOnTile)(int32_t plane, int32_t tileX, int32_t tileY,
                                     GroundItemState* outItems, uint32_t maxItems) = nullptr;
    uint32_t (*getProjectiles)(ProjectileState* outProjectiles, uint32_t maxProjectiles) = nullptr;
    /// SDK 57+. Enumerate active map-tile graphics objects (spot anims) rooted
    /// at `WorldView::GraphicsObjectList`. Returns 0 when the analyzer hasn't
    /// detected the list head on the bound revision.
    uint32_t (*getGraphicsObjects)(GraphicsObjectState* outGraphicsObjects,
                                   uint32_t maxGraphicsObjects) = nullptr;

    // --- Camera ---
    uint8_t (*getCameraState)(CameraState* outState) = nullptr;

    // --- Rendering (null when loaded by controller) ---
    void (*setImGuiContext)(void* ctx) = nullptr;
    uint8_t (*worldToScreen)(int32_t worldX, int32_t worldY, int32_t worldZ,
                             int32_t* screenX, int32_t* screenY) = nullptr;
    uint8_t (*tileToScreen)(int32_t tileX, int32_t tileY, int32_t plane, int32_t heightOffset,
                            int32_t* screenX, int32_t* screenY) = nullptr;
    int32_t (*getTileHeight)(int32_t preciseX, int32_t preciseY, int32_t plane) = nullptr;
    void (*drawTileQuad)(int32_t tileX, int32_t tileY, int32_t plane,
                         uint32_t fillColor, uint32_t outlineColor) = nullptr;
    void (*drawTileRegion)(int32_t minTileX, int32_t minTileY, int32_t maxTileX, int32_t maxTileY, int32_t plane,
                           uint32_t fillColor, uint32_t outlineColor) = nullptr;
    void (*drawEntityBox)(int32_t preciseX, int32_t preciseY, int32_t plane,
                          int32_t tileSize, int32_t height, uint32_t color) = nullptr;
    void (*drawTextAtWorld)(int32_t worldX, int32_t worldY, int32_t worldZ,
                            const char* text, uint32_t color, uint8_t centered) = nullptr;

    // --- Scene queries ---
    int32_t (*getCollisionFlag)(int32_t plane, int32_t tileX, int32_t tileY) = nullptr;

    // --- Input ---
    uint8_t (*getMousePos)(int32_t* outX, int32_t* outY) = nullptr;

    // --- Screen-space rendering ---
    void (*drawScreenText)(int32_t screenX, int32_t screenY,
                           const char* text, uint32_t color) = nullptr;
    void (*drawScreenRect)(int32_t x, int32_t y, int32_t w, int32_t h,
                           uint32_t color) = nullptr;
    void (*drawScreenLine)(int32_t x1, int32_t y1, int32_t x2, int32_t y2,
                           uint32_t color, float thickness) = nullptr;

    // --- Var system ---
    // getVarbit is cache-backed: Titan JS5 varbit definition + direct varp
    // read. It does not call native GET_VARBIT or walk live VarBitType state.
    int32_t (*getVarbit)(int32_t varbitId) = nullptr;
    int32_t (*getVarp)(int32_t varpId) = nullptr;

    // --- Prayer ---
    uint8_t (*isPrayerActive)(int32_t prayerOrdinal) = nullptr;

    // --- Skills ---
    int32_t (*getBoostedSkillLevel)(int32_t skillId) = nullptr;
    int32_t (*getRealSkillLevel)(int32_t skillId) = nullptr;
    int32_t (*getSkillExperience)(int32_t skillId) = nullptr;

    // --- Synthetic action (matches dev tools debug tab fields) ---
    uint8_t (*executeSyntheticAction)(uint32_t opcode, int32_t identifier,
                                     int32_t param0, int32_t param1,
                                     int32_t worldViewId,
                                     int32_t clickX, int32_t clickY,
                                     const char* actionText,
                                     const char* targetText,
                                     uint8_t skipClick) = nullptr;
    uint8_t (*executeSyntheticEntry)(const SyntheticActionEntry* entry) = nullptr;

    // --- Walking ---
    uint8_t (*walkTo)(int32_t sceneX, int32_t sceneY) = nullptr;
    uint8_t (*walkToWorld)(int32_t worldX, int32_t worldY, int32_t plane) = nullptr;

    // --- Entity hiding (render-function hooks) ---
    // entityType: 0 = other players, 1 = NPCs, 2 = local player (self), 3 = 3D scene
    void (*setEntityHidden)(uint8_t entityType, uint8_t hidden) = nullptr;
    uint8_t (*getEntityHidden)(uint8_t entityType) = nullptr;

    // --- Interact API ---
    // action: case-insensitive action text (e.g. "Attack", "Talk-to")
    // idOrNeg1: NPC/loc ID filter, or -1 to match any
    // nameOrNull: name substring filter, or nullptr to match any
    // interactObject is a nearest-by-id/name convenience path. Use
    // interactTileObject when the caller already has a TileObjectState.
    uint8_t (*interactNpc)(const char* action, int32_t npcIdOrNeg1,
                           const char* nameOrNull) = nullptr;
    uint8_t (*interactNpcByIndex)(const char* action, int32_t hashIndex) = nullptr;
    uint8_t (*interactObject)(const char* action, int32_t locIdOrNeg1,
                              const char* nameOrNull) = nullptr;
    uint8_t (*interactGroundItem)(const char* action, int32_t itemId,
                                  int32_t tileX, int32_t tileY) = nullptr;

    // --- Find API ---
    uint8_t (*findNearestNpc)(int32_t npcIdOrNeg1, const char* nameOrNull,
                              NpcState* outNpc) = nullptr;
    uint8_t (*findNearestObject)(int32_t locIdOrNeg1, const char* nameOrNull,
                                 TileObjectState* outObject) = nullptr;

    // --- Interaction target resolution ---
    uint8_t (*getInteracting)(int32_t interactingIndex, uint8_t interactingType,
                              PlayerState* outPlayer, NpcState* outNpc) = nullptr;

    // --- Thread dispatch ---
    // SDK 116: the host calls cleanup(userData) EXACTLY ONCE per request --
    // after callback ran, or when the queued request is dropped without
    // running (queue-cap eviction, shutdown/reload clearAll). callback must
    // NOT free userData. cleanup may be null for fire-and-forget userData.
    void (*runOnClientTick)(void (*callback)(void* userData), void* userData,
                            void (*cleanup)(void* userData)) = nullptr;
    void (*runOnRender)(void (*callback)(void* userData), void* userData,
                        void (*cleanup)(void* userData)) = nullptr;

    // --- Cache definition lookups ---
    // SDK 106+: callers brace-initialize the snapshot so structSize and
    // apiVersion are populated. Invalid records fail without being modified.
    uint8_t (*getItemDef)(int32_t id, ItemDefSnapshot* out) = nullptr;
    uint8_t (*getNpcDef)(int32_t id, NpcDefSnapshot* out) = nullptr;
    uint8_t (*getObjDef)(int32_t id, ObjDefSnapshot* out) = nullptr;
    // getVarbitDef is cache-backed and returns VarbitDefSource::Disk on
    // success for current hosts.
    uint8_t (*getVarbitDef)(int32_t id, VarbitDefSnapshot* out) = nullptr;

    // --- Internal developer tools (SDK 79) ---
    /// Toggle an in-client developer tool by stable id. Current ids include
    /// `dev_tools`, `cache_explorer`, `widget_explorer`,
    /// `widget_explorer_bounds`, `chat_inspector`, `cs2_inspector`,
    /// `packet_inspector`, `action_inspector`, `varbit_inspector`,
    /// `item_container_explorer`, `sound_effect_inspector`,
    /// `hitsplat_inspector`, `spotanim_inspector`, `overhead_inspector`, and
    /// `world_hopper`.
    void (*setInternalToolVisible)(const char* toolId, uint8_t visible) = nullptr;
    /// Returns 1 when the internal developer tool is currently visible/enabled.
    uint8_t (*getInternalToolVisible)(const char* toolId) = nullptr;

    // --- Worlds (SDK 28) ---
    /// Write the live current-world id into @p outWorld. Returns 1 on
    /// success, 0 when CURRENT_WORLD_OFFSET is unmapped (level-1
    /// availability failed). Safe to call from any thread.
    uint8_t (*getCurrentWorld)(int32_t* outWorld) = nullptr;
    /// Snapshot up to @p cap entries of the native world list into
    /// @p out. Returns the number of entries written. Returns 0 when
    /// level-3 availability failed (no list base or no field offsets).
    uint32_t (*getWorldList)(WorldState* out, uint32_t cap) = nullptr;
    /// Dispatch a native hop to the world with the given id. Resolves
    /// the id through the live m_list then SEH-wraps
    /// `TitleScreen::SwitchToWorld`. Returns 1 on success, 0 when
    /// level-3 availability failed or no entry matched. Must be called
    /// on the game thread (use the client's thread dispatcher from IPC).
    uint8_t (*hopToWorldId)(int32_t worldId) = nullptr;
    /// Lower-tier variant: hop by position in the live `m_list`. Works
    /// whenever level-2 availability holds, even if the field offsets
    /// couldn't be detected structurally. Returns 1 on success.
    uint8_t (*hopToListIndex)(uint32_t idx) = nullptr;
    /// In-game hop path: drives the native 3x `CC_OP` footer-click
    /// sequence used when the player is already logged in
    /// (opens the logout tab, opens the world switcher, selects the
    /// world, confirms). Progresses asynchronously across several
    /// `ClientTick`s; the return value is "accepted?" -- 1 when the
    /// request was queued on the game thread, 0 when the state
    /// machine is already busy, the world id isn't in the list, or
    /// the analyzer data needed to drive the widget clicks is
    /// missing. Use this on the logged-in state; use `hopToWorldId`
    /// on the title screen.  SDK 31+.
    uint8_t (*hopToWorldIngame)(int32_t worldId) = nullptr;

    // --- Inventory ---
    uint8_t (*containsInventoryItem)(int32_t itemId) = nullptr;
    uint8_t (*interactInventoryItem)(int32_t itemId, const char* action) = nullptr;
    /// Populate @p out with up to @p max occupied inventory slots. Returns the
    /// actual count written. Empty slots are skipped. (SDK 18+)
    uint32_t (*getInventoryItems)(InventoryItemState* out, uint32_t max) = nullptr;

    // --- CS2 script execution ---
    uint8_t (*runClientScript)(int32_t scriptId,
        const int32_t* intArgs, uint32_t intArgCount,
        Cs2ScriptResult* outResult) = nullptr;

    /// Query a quest's completion state by its struct ID.
    /// Returns: 0 = in progress, 1 = not started, 2 = finished, -1 = error.
    int32_t (*getQuestState)(int32_t questId) = nullptr;

    // --- Local player ---
    uint8_t (*getLocalPlayer)(PlayerState* outPlayer) = nullptr;

    // --- Idle timer ---
    int32_t (*getIdleTimeRemaining)() = nullptr;
    void (*resetIdleTimer)() = nullptr;

    // --- Widget queries ---
    uint8_t (*getWidget)(uint32_t packedId, WidgetState* outState) = nullptr;

    // --- Plugin manager (new in SDK 18) ---
    /// Return up to @p max plugin info records. Output is the actual count
    /// (capped at max). Records are in implementation-defined order.
    uint32_t (*listPlugins)(PluginInfo* out, uint32_t max) = nullptr;
    /// Look up a single plugin by id. Returns 1 and fills @p out if found, 0 otherwise.
    uint8_t  (*getPlugin)(const char* pluginId, PluginInfo* out) = nullptr;
    /// Queue an enabled-state change. The change takes effect at the end of the
    /// current dispatch pass; it is safe to call from inside onGameTick/onEnable
    /// even for the caller's own plugin id. Returns 1 if the id is known.
    uint8_t  (*setPluginEnabled)(const char* pluginId, uint8_t enabled) = nullptr;
    /// Returns: 1 = enabled, 0 = disabled, -1 = unknown id.
    int8_t   (*isPluginEnabled)(const char* pluginId) = nullptr;

    // --- Login / account switch API (new in SDK 19) -------------------------
    /// Populate @p out with the current login state snapshot (loginIndex,
    /// gameState, username, displayName, feature availability). Returns 1 on
    /// success. Zero when the analyzer did not detect the login flow.
    uint8_t (*getLoginAccountState)(LoginAccountState* out) = nullptr;
    /// Write @p username to the login screen username buffer. Dispatched on
    /// the game thread. No-op when unavailable.
    void (*setLoginUsername)(const char* username) = nullptr;
    /// Write @p password to the login screen password buffer. Dispatched on
    /// the game thread.
    void (*setLoginPassword)(const char* password) = nullptr;
    /// Write @p code to the 2FA authenticator buffer. Dispatched on game thread.
    void (*setLoginAuthenticator)(const char* code) = nullptr;
    /// Overwrite the native loginIndex integer (2 = standard login,
    /// 10 = Jagex launcher). Dispatched on the game thread.
    void (*setLoginIndex)(int32_t loginIndex) = nullptr;
    /// Call TitleScreen::SetDisplayName with @p displayName. Dispatched on
    /// the game thread.
    void (*setLoginDisplayName)(const char* displayName) = nullptr;
    /// Call DesktopAuthLibWrapper::SetOAuth2Credentials. Dispatched on game thread.
    void (*setLoginOAuth2Credentials)(const char* accessToken,
                                      const char* refreshToken) = nullptr;
    /// Call DesktopAuthLibWrapper::SetGameSessionCredentials. Dispatched on game thread.
    void (*setLoginGameSessionCredentials)(const char* sessionId,
                                           const char* characterId) = nullptr;
    /// Composite: setDisplayName + setGameSessionCredentials + setLoginIndex(jagex).
    void (*setLoginCharacter)(const char* displayName,
                              const char* characterId,
                              const char* sessionId) = nullptr;
    /// Clear every Jagex token and flip back to the standard login screen.
    void (*resetLoginCharacter)() = nullptr;

    // --- Chat injection (new in SDK 22) -------------------------------------
    /// Insert a local chat line into the player's chatbox. No server packet
    /// is sent -- the message appears only on this client (RuneLite-style
    /// `client.addChatMessage` equivalent). Dispatched onto the game thread;
    /// a null sender is treated as an empty string.
    ///
    /// The in-process chat hook will also fire `onChatMessage` for this
    /// line (re-entry is serialized by a thread-local guard), so listeners
    /// observe plugin-injected messages the same way they see server ones.
    ///
    /// Disabled (silent no-op) when the analyzer did not detect the
    /// addChatMessage dispatch or the eastl helpers on this revision.
    void (*addChatMessage)(int32_t type, const char* name,
                           const char* message, const char* sender) = nullptr;

    // --- Item containers + runtime ItemDef (new in SDK 26) ------------------
    /// Snapshot the items in the given RuneLite-style container id
    /// (`titan::InventoryID`). Returns 1 on success, 0 when the container
    /// is absent from the native cache or the analyzer-provided cache layout
    /// fails validation. Safe to call from game-thread events AND from
    /// render-thread overlays.
    uint8_t (*getItemContainer)(int32_t containerId,
                                ItemContainerState* outState) = nullptr;

    /// Resolve the RUNTIME ItemDef for the given item id. Game-thread calls may
    /// invoke native resolution; off-thread calls warn, check the live table,
    /// then use raw cache metadata on a miss without invoking native code. Sets
    /// `outState->runtimeResolved = 1` for a live definition and 0 for raw cache
    /// fallback. Returns 1 on success or 0 when the id is unavailable.
    uint8_t (*getItemComposition)(int32_t itemId,
                                  ItemCompositionState* outState) = nullptr;

    // --- Use-on item API (new in SDK 32) ------------------------------------
    /// Queue @p srcSlot / @p srcItemId in the inventory with
    /// `WIDGET_TARGET` (opcode 25), immediately followed in the same ordered
    /// input sequence by `WIDGET_TARGET_ON_WIDGET` on @p tgtSlot /
    /// @p tgtItemId. This is
    /// the "use knife on logs" path. Pass `srcSlot < 0` or `tgtSlot < 0`
    /// to resolve the slot from the item id (first match wins); pass
    /// both to target the specific slot/item pair, which is important
    /// when the inventory has duplicate stacks. Returns 1 when the complete
    /// sequence was accepted, 0 when either side could not be resolved,
    /// queued, or the inventory isn't open.
    uint8_t (*useInventoryItemOnItem)(int32_t srcSlot, int32_t srcItemId,
                                      int32_t tgtSlot, int32_t tgtItemId) = nullptr;
    /// Queue @p srcSlot / @p srcItemId with `WIDGET_TARGET`, followed in the
    /// same ordered input sequence by the first NPC menu entry for the hash
    /// index @p npcHashIndex (so the selected item lands on the NPC).
    /// Returns 1 when the complete sequence was accepted.
    uint8_t (*useInventoryItemOnNpc)(int32_t srcSlot, int32_t srcItemId,
                                     int32_t npcHashIndex) = nullptr;
    /// Queue @p srcSlot / @p srcItemId with `WIDGET_TARGET`, followed in the
    /// same ordered input sequence by a game-object menu entry for
    /// @p locId at (@p tileX, @p tileY). Returns 1 when the complete
    /// sequence was accepted.
    uint8_t (*useInventoryItemOnObject)(int32_t srcSlot, int32_t srcItemId,
                                        int32_t locId, int32_t tileX,
                                        int32_t tileY) = nullptr;

    // --- Entity clickbox overlays (new in SDK 33) --------------------------
    /// Draw the accurate world-space AABB clickbox around @p entityPtr
    /// (a live `jag::game::Entity*` -- player or NPC pointer obtained from
    /// `getPlayers` / `getNpcs`). The AABB is read from the entity's
    /// GraphNode cache, projected through W2S, and rendered as 12 wireframe
    /// edges plus optional translucent face fills.
    ///
    /// No-op when the analyzer did not detect the GraphNode / AABB offsets
    /// on this revision, or when the entity's bounds are stale / offscreen.
    /// Safe to call from any overlay layer on the render thread.
    ///
    /// @param outline ARGB colour (0xAARRGGBB) for the edges (alpha==0 hides edges).
    /// @param fill    Optional translucent fill for the six AABB faces
    ///                (alpha==0 hides fills).
    ///
    /// @param typecode Engine's 64-bit `ModelTypecodeType` for this
    ///                 entity. The host keys its picking cache by this
    ///                 value so same-tile stacks (player on loc, two
    ///                 NPCs on one tile, item on loc) never collide.
    ///                 The `titan::overlay()` facade computes it from
    ///                 `Player::hashIndex() + tile + plane` (type=0)
    ///                 or `NPC::hashIndex() + tile + plane` (type=1)
    ///                 automatically. Passing `0` disables the picking
    ///                 cache entirely (no world-keyed fallback exists);
    ///                 the host then draws from GraphNode data or, for
    ///                 actors, a synthesized approximate footprint.
    void (*drawEntityClickbox)(uint64_t entityPtr, uint64_t typecode,
                               uint32_t outline, uint32_t fill) = nullptr;

    /// Same as drawEntityClickbox but for scene objects (walls, decor,
    /// standing locs, ground decor). @p locPtr is the raw loc pointer
    /// reachable from `TileObjectState::entityPtr` or direct Square-slot
    /// reads. @p typecode is the native raw scene tag (`packedId`) for locs.
    /// Silent no-op when no matching cache entry exists.
    void (*drawTileObjectClickbox)(uint64_t locPtr, uint64_t typecode,
                                   uint32_t outline, uint32_t fill) = nullptr;

    /// Draw the 2D convex hull of the entity's projected AABB corners --
    /// a clean closed silhouette useful as a lightweight model outline.
    /// Sources from the same picking cache as drawEntityClickbox; future
    /// SDK versions may upgrade the source to actual model vertex data
    /// when the analyzer can extract per-instance geometry.
    void (*drawEntityHull)(uint64_t entityPtr, uint64_t typecode,
                           uint32_t outline, uint32_t fill) = nullptr;
    /// Loc / scene-object equivalent of drawEntityHull.
    void (*drawTileObjectHull)(uint64_t locPtr, uint64_t typecode,
                               uint32_t outline, uint32_t fill) = nullptr;

    // --- Generic widget interaction (new in SDK 36) ------------------------
    /// Dispatch a widget-family DoAction through the native
    /// `doActionArg11Builder` path. @p opcode is a MenuAction opcode
    /// (CC_OP=57, CC_OP_LOW=1007, WIDGET_FIRST..FIFTH_OPTION=39..43,
    /// WIDGET_TARGET=25, WIDGET_TARGET_ON_WIDGET=58, ...). @p identifier is
    /// the menu-entry identifier (the CC_OP sub-action index, 0 for
    /// non-CC_OP). It is not a widget packed id. @p param0 is the
    /// dynamic-child slot index on
    /// the target widget, or -1 for "whole widget / no slot". @p param1
    /// is the packed widget id `(groupId << 16) | childId`.
    ///
    /// The host resolves the parent widget from @p param1, walks dynamic
    /// children to @p param0 (falling back to the parent bounds when the
    /// child is unavailable), computes a Gaussian-weighted click point
    /// from the resolved widget's screen rectangle, and queues the
    /// action for the next client tick. When the click target is a dynamic
    /// child, the click point is constrained to the parent widget's screen
    /// bounds (SDK 124+): child-parent intersection when they overlap, a
    /// random point inside the parent when the child lies outside it.
    /// Returns 1 when the action was
    /// accepted (the game may still reject invalid widget / slot combos).
    uint8_t (*widgetInteract)(uint32_t opcode, int32_t identifier,
                              int32_t param0, int32_t param1) = nullptr;

    // --- Widget child enumeration (new in SDK 38) --------------------------
    /// Enumerate the dynamic children of the widget at @p parentPackedId.
    /// Resolves the parent via the standard widget lookup, then walks the
    /// children vector at `widget + childrenDataPtr` and populates one
    /// `WidgetState` per native slot (capped at @p maxOut). Null placeholders
    /// are preserved so slot indexes cannot drift. Non-null entries are filled
    /// the same way `getWidget()` fills a single widget -- screen bounds,
    /// `text`, `hidden` / `selfHidden` / `visible`, `type`, `contentType`,
    /// `parentId`, etc.
    ///
    /// `WidgetState::packedId` for each child is the child's own
    /// packed-component-id when the analyzer mapped that field, or
    /// `(parentGroup << 16) | i` (where `i` is the child's slot index in
    /// the dynamic-children array) as a deterministic fallback. The slot
    /// index is also recoverable from the child's position in the output
    /// array; the plugin keeps that index when it later calls
    /// `widgetInteract(WIDGET_CONTINUE, 0, /*param0=*/i, /*param1=*/parentPackedId)`.
    ///
    /// @return Number of children written into @p outStates. When
    ///         @p outStates is null or @p maxOut is 0, acts as a sizing
    ///         probe and returns the true dynamic-child count instead
    ///         (SDK 124+; pre-124 hosts returned 0 for that form).
    ///         Returns 0 when the parent is missing or has no children.
    uint32_t (*getWidgetChildren)(uint32_t parentPackedId,
                                  WidgetState* outStates,
                                  uint32_t maxOut) = nullptr;

    // --- Widget text search (new in SDK 39) ---------------------------------
    /// Find the first widget whose *primary* display text contains @p query
    /// (case-sensitive substring; same semantics as the client's
    /// `WidgetReader::findByText`). On success returns 1 and fills @p outState
    /// the same way `getWidget()` does. Returns 0 when @p query is null or
    /// empty, @p outState is null, the client isn't ready, no widget matched,
    /// or the matched widget has no packed component id.
    uint8_t (*getWidgetByText)(const char* query, WidgetState* outState) = nullptr;

    // --- Keyboard injection + CS2 typed args + widget hooks (new in SDK 42) ---
    /// Inject a UTF-8 string into the game's internal keyboard producer.
    /// Each printable character fires `OnKeyChar`; `\n` / `\b` / `\t` are
    /// translated into a full press/release cycle of the corresponding
    /// special key. Dispatched on the game thread; returns 1 when queued
    /// or immediately executed, 0 on null input / dispatcher failure.
    ///
    /// The host NEVER uses `PostMessageW` / `SendInput` / `WM_CHAR`. The
    /// call path is indistinguishable from the game's own internal key
    /// events. Returns silently when the analyzer could not locate the
    /// keyboard producer functions / `Keyboard*` instance on this
    /// revision (the injection feature disables gracefully).
    uint8_t (*sendKeyboardString)(const char* utf8) = nullptr;

    /// Send a single keypress cycle (down + up) for a named key with
    /// optional modifier brackets. @p key is a `KeyboardKey::*` ordinal;
    /// @p modMask is a bitmask of `KeyboardMods::{SHIFT,CTRL,ALT}`.
    /// Returns 1 when the work was queued, 0 when the feature is
    /// unavailable or @p key is out of range.
    uint8_t (*sendKeyboardKey)(int32_t key, uint32_t modMask) = nullptr;

    /// Run a CS2 script with mixed int + SSO-string arguments via the
    /// game's HookReq (`CS2_TRIGGER_HANDLER`) path. @p args is a plain
    /// array of `TitanHookArg`; each string payload is limited to
    /// `kMaxHookArgStringLen` bytes (longer strings cause the call to
    /// fail with success=0 on @p outResult). Returns 1 when the script
    /// fired and the int-stack snapshot was written back to @p outResult,
    /// 0 when required analyzer data is missing or args are malformed.
    uint8_t (*runClientScriptTyped)(int32_t scriptId,
        const TitanHookArg* args, uint32_t argCount,
        Cs2ScriptResult* outResult) = nullptr;

    // --- SDK 43: Human-like delayed keyboard typing ---

    /// Type a string with randomized inter-character delays. Each
    /// character is dispatched on a separate pump-thread drain, spread
    /// across real time. Returns 1 when the typing operation was started,
    /// 0 when the keyboard subsystem is unavailable or @p utf8 is null.
    ///
    /// @p callbackPhase is a `KeyboardTypeCallbackPhase::*` ordinal
    /// selecting which dispatcher the completion callback runs on.
    /// @p onDone may be null for fire-and-forget usage. If non-null it
    /// is called with completed=1 when all characters have been typed, or
    /// completed=0 if the operation was cancelled. @p userData is passed
    /// through to the callback.
    ///
    /// If a type operation is already in progress, the old one is
    /// cancelled (its callback fires with completed=0) before the new
    /// one starts.
    uint8_t (*typeKeyboardString)(const char* utf8,
        int32_t minDelayMs, int32_t maxDelayMs,
        int32_t callbackPhase,
        void (*onDone)(uint8_t completed, void* userData),
        void* userData) = nullptr;

    /// Cancel any in-progress typeKeyboardString operation. The pending
    /// callback (if any) fires with completed=0.
    void (*cancelKeyboardType)() = nullptr;

    /// Returns 1 while a typeKeyboardString operation is in progress.
    uint8_t (*isKeyboardTyping)() = nullptr;

    // --- SDK 46: OverlayPanel API ----------------------------------

    /// Register a named panel. Returns a stable handle (>= 0) that the
    /// plugin uses for all subsequent begin/emit/end calls. The host
    /// keys persisted positions by (pluginId, panelName); calling
    /// register a second time with the same key returns the same
    /// handle. The defaultAnchor / defaultPriority are used only on
    /// first registration and when no saved override exists.
    ///
    /// Returns -1 when @p pluginId or @p panelName is null/empty.
    int32_t (*overlayPanelRegister)(const char* pluginId,
                                    const char* panelName,
                                    uint8_t defaultAnchor,
                                    int32_t defaultPriority) = nullptr;

    /// Drop the registration. Saved layout entries persist in
    /// overlay_layout.json so the panel re-loads its position next time
    /// the plugin registers it.
    void (*overlayPanelUnregister)(int32_t handle) = nullptr;

    /// Begin per-frame component emission. Resets the panel's component
    /// list. Begin/end alone does not make a panel visible; the host
    /// renders and hit-tests the panel only if at least one component is
    /// appended before end. @p preferredWidth is a hint (clamped to >=80,
    /// <=600). Style (background colour, border, padding, etc.) is sticky
    /// -- set it once via overlayPanelSetStyle.
    void (*overlayPanelBegin)(int32_t handle,
                              int32_t preferredWidth) = nullptr;

    /// End per-frame emission. No-op for visibility; panels with no
    /// emitted components remain hidden for this frame.
    void (*overlayPanelEnd)(int32_t handle) = nullptr;

    /// Replace the panel's sticky style. Style persists across frames
    /// until this is called again. Pass nullptr to restore defaults.
    void (*overlayPanelSetStyle)(int32_t handle,
                                 const OverlayPanelStyleAbi* style) = nullptr;

    /// Append a title row to the panel for this frame.
    void (*overlayPanelTitle)(int32_t handle, const char* text,
                              uint32_t color) = nullptr;

    /// Append a label/value line. @p right may be empty for single-
    /// column lines.
    void (*overlayPanelLine)(int32_t handle, const char* left,
                             const char* right,
                             uint32_t leftColor,
                             uint32_t rightColor) = nullptr;

    /// Append a progress bar row with [minVal..maxVal] range. Values
    /// are clamped client-side; equal min/max produces an empty bar.
    void (*overlayPanelProgressBar)(int32_t handle,
                                    int32_t value, int32_t minVal,
                                    int32_t maxVal,
                                    uint32_t fillColor,
                                    uint32_t bgColor) = nullptr;

    // --- SDK 51: Widget text setter --------------------------------

    /// Replace a live widget's display text. The host resolves @p packedId
    /// through the standard widget table and writes the mapped EASTL text
    /// field(s) on the game thread. Returns 1 when the write was applied or
    /// accepted for game-thread execution, 0 when the widget is missing, text
    /// is null, or @p text is too long. Current hosts accept up to 256 UTF-8
    /// bytes when the analyzer exported native EASTL range assignment; older
    /// offset bundles retain the 22-byte inline fallback.
    uint8_t (*setWidgetText)(uint32_t packedId, const char* text) = nullptr;

    // --- SDK 55: Slot-aware inventory interact ----------------------

    /// Interact with the item at a specific inventory slot. Resolves the
    /// slot's widget child for click-bounds and dispatches through the
    /// same Widgets::interact path as the internal backend, avoiding the
    /// executeSyntheticAction detour that external plugins previously took
    /// for duplicate-item-id inventories.
    uint8_t (*interactInventoryItemAtSlot)(int32_t slot, int32_t itemId,
                                           const char* action) = nullptr;

    // --- SDK 56: Exact tile-object interact -------------------------

    /// Dispatch @p action against the exact TileObjectState instance passed by
    /// the plugin. This is distinct from interactObject(), which intentionally
    /// remains a nearest-by-loc-id/name convenience path.
    uint8_t (*interactTileObject)(const char* action,
                                  const TileObjectState* object) = nullptr;

    // --- SDK 59: Actor path queue ----------------------------------

    /// Populate @p out with up to @p max world-point entries from an actor's
    /// path queue. Entries are ordered exactly like the client queue:
    /// index 0 is the logical/server tile, and the last entry is the active
    /// movement target. Returns 0 for empty, invalid, or unavailable queues.
    uint32_t (*getActorPathQueue)(uint64_t entityPtr,
                                  WorldPointState* out,
                                  uint32_t max) = nullptr;

    // --- SDK 62: VarClient values ---------------------------------

    /// Read one client-side integer variable into @p outValue. Returns 1 on
    /// success and 0 when the analyzer-backed VarClient service is unavailable
    /// or the id does not currently resolve to an integer entry.
    uint8_t (*getVarClientInt)(int32_t id, int32_t* outValue) = nullptr;
    /// Queue an integer write for the next client tick. Returns whether the
    /// write was accepted for execution.
    uint8_t (*setVarClientInt)(int32_t id, int32_t value) = nullptr;

    /// Return the required UTF-8 byte count including the trailing NUL for one
    /// client-side string variable. Returns 0 when unavailable and 1 for an
    /// empty string. When @p out and @p capacity are provided, writes a bounded
    /// NUL-terminated prefix so callers can query, allocate, and retry.
    uint32_t (*getVarClientString)(int32_t id, char* out,
                                   uint32_t capacity) = nullptr;
    /// Queue a string write for the next client tick. Returns whether the write
    /// was accepted for execution.
    uint8_t (*setVarClientString)(int32_t id, const char* value) = nullptr;

    /// Optional 64-bit VarClient support. Returns 0 when this revision did not
    /// export both native long helpers or the id is not a long entry.
    uint8_t (*getVarClientLong)(int32_t id, int64_t* outValue) = nullptr;
    uint8_t (*setVarClientLong)(int32_t id, int64_t value) = nullptr;

    // --- SDK 63: Slot-addressed dynamic widget text writes ----------

    /// Queue a display-text write for the exact dynamic child at @p slot under
    /// @p parentPackedId. The host resolves the parent and child on the next
    /// client tick. Invalid, missing, or null slots fail closed without
    /// falling back to the parent widget.
    uint8_t (*setWidgetTextAtSlot)(uint32_t parentPackedId, int32_t slot,
                                   const char* text) = nullptr;

    // --- SDK 64: Slot-aware recursive widget queries ----------------

    /// Enumerate loaded flat widgets plus recursively reachable dynamic
    /// descendants. Pass kAllWidgetGroups for every loaded group or a concrete
    /// group id for a scoped query. Returns the total bounded result count even
    /// when @p outStates is null or @p maxOut is smaller. @p outTruncated is set
    /// when a safety bound clipped the live traversal.
    uint32_t (*getWidgets)(uint32_t groupId, WidgetQueryState* outStates,
                           uint32_t maxOut, uint8_t* outTruncated) = nullptr;

    /// Enumerate non-null direct dynamic children beneath a retained address.
    /// Each child extends the parent's exact native slot path.
    uint32_t (*getWidgetChildrenAtPath)(const WidgetAddressState* parent,
                                        WidgetQueryState* outStates,
                                        uint32_t maxOut) = nullptr;

    /// Queue a display-text write against the exact retained widget path.
    uint8_t (*setWidgetTextAtPath)(const WidgetAddressState* address,
                                   const char* text) = nullptr;

    /// Queue an interaction against the addressed widget (`childSlot == -1`)
    /// or one exact direct dynamic child beneath it (`childSlot >= 0`).
    uint8_t (*widgetInteractAtPath)(const WidgetAddressState* address,
                                    uint32_t opcode, int32_t identifier,
                                    int32_t childSlot) = nullptr;

    // --- SDK 66: Cross-plugin service registry ----------------------

    /// Publish a service pointer under @p serviceId so dependent plugins can
    /// retrieve it. The pointer is an opaque interface the providing and
    /// consuming plugins agree on (typically a shared abstract base declared
    /// in a common header). The host stores the raw pointer and does NOT own
    /// it -- the providing plugin must keep it alive while registered. A
    /// provider should register in its constructor / onEnable so consumers
    /// (which the host loads later, per the dependency graph) can resolve it.
    /// Passing a null service unregisters the id.
    void (*registerPluginService)(const char* serviceId, void* service) = nullptr;

    /// Resolve a service previously published via registerPluginService.
    /// Returns null when no service is registered under @p serviceId. The
    /// consumer casts the result back to the agreed interface type.
    void* (*getPluginService)(const char* serviceId) = nullptr;

    // --- SDK 69: Audio playback toggle ------------------------------
    /// Globally suppress sound playback. When enabled, the sound hooks fire
    /// `onSoundPlayed` but skip the native game call (synth entries are dropped
    /// from the queue; jingles are not played). Independent of (and OR-ed with)
    /// per-event `consumed`.
    void (*setAudioPlaybackDisabled)(uint8_t disabled) = nullptr;
    /// Read the current global audio-playback-disabled state (1 = disabled).
    uint8_t (*getAudioPlaybackDisabled)() = nullptr;
    /// SDK 76+. Snapshot actor-attached spotanims for a live Player/NPC
    /// entity pointer. Returns 0 when the actor is null, the analyzer did not
    /// emit the spotanim map layouts, or no active entries are present.
    uint32_t (*getActorSpotAnims)(uint64_t entityPtr,
                                  ActorSpotAnimState* out,
                                  uint32_t max) = nullptr;

    // --- SDK 81: WorldView lookup + instance template conversion ----
    /// Resolve a WorldView pointer by WorldView id from Client::WorldViewMap.
    uint8_t (*getWorldViewById)(int32_t worldViewId, uint64_t* outPtr) = nullptr;
    /// Resolve the top-level/default WorldView pointer.
    uint8_t (*getTopLevelWorldView)(uint64_t* outPtr) = nullptr;
    /// Snapshot the active instance template chunks.
    uint8_t (*getInstanceTemplateChunks)(InstanceTemplateChunksState* out) = nullptr;
    /// Convert a current local-instance WorldPoint to its source-world point.
    uint8_t (*worldPointFromLocalInstance)(const WorldPointState* in,
                                           WorldPointState* out) = nullptr;
    /// Convert a source-world WorldPoint into the current local instance.
    uint8_t (*worldPointToLocalInstance)(const WorldPointState* in,
                                         WorldPointState* out) = nullptr;

    // --- SDK 82: Client walking destination ------------------------
    /// Active minimap red-flag destination in scene-local precise coords.
    uint8_t (*getLocalDestinationLocation)(LocalPointState* out) = nullptr;
    /// Active minimap red-flag destination in world coords.
    uint8_t (*getWorldDestinationLocation)(WorldPointState* out) = nullptr;

    // --- SDK 83: SLR-backed world metadata -------------------------
    /// Snapshot Jagex SLR-backed world metadata plus cached measured ping.
    uint32_t (*getWorldMetadata)(WorldMetadataState* out, uint32_t cap) = nullptr;
    /// Force an asynchronous SLR refresh and ping probe queue.
    uint8_t (*refreshWorldMetadata)() = nullptr;

    // --- SDK 84: WorldView-addressed overlay projection --------------
    uint8_t (*worldToScreenInWorldView)(int32_t worldViewId,
                                        int32_t preciseX,
                                        int32_t worldY,
                                        int32_t preciseY,
                                        int32_t plane,
                                        int32_t* screenX,
                                        int32_t* screenY) = nullptr;
    int32_t (*getTileHeightInWorldView)(int32_t worldViewId,
                                        int32_t preciseX,
                                        int32_t preciseY,
                                        int32_t plane) = nullptr;
    void (*drawTileQuadInWorldView)(int32_t worldViewId,
                                    int32_t tileX,
                                    int32_t tileY,
                                    int32_t plane,
                                    uint32_t fillColor,
                                    uint32_t outlineColor) = nullptr;
    void (*drawTileRegionInWorldView)(int32_t worldViewId,
                                      int32_t minTileX,
                                      int32_t minTileY,
                                      int32_t maxTileX,
                                      int32_t maxTileY,
                                      int32_t plane,
                                      uint32_t fillColor,
                                      uint32_t outlineColor) = nullptr;
    void (*drawTextAtWorldInWorldView)(int32_t worldViewId,
                                       int32_t preciseX,
                                       int32_t worldY,
                                       int32_t preciseY,
                                       int32_t plane,
                                       const char* text,
                                       uint32_t color,
                                       uint8_t centered) = nullptr;

    // --- SDK 85: WorldView-aware entity interactions ----------------
    uint8_t (*interactNpcByIndexInWorldView)(const char* action,
                                             int32_t hashIndex,
                                             int32_t worldViewId) = nullptr;
    uint8_t (*interactGroundItemInWorldView)(const char* action,
                                             int32_t itemId,
                                             int32_t tileX,
                                             int32_t tileY,
                                             int32_t worldViewId) = nullptr;

    // --- SDK 89: Direct identity resolvers for live handles --------
    uint8_t (*getPlayerByIndexInWorldView)(int32_t hashIndex,
                                           int32_t worldViewId,
                                           PlayerState* outPlayer) = nullptr;
    uint8_t (*getNpcByIndexInWorldView)(int32_t hashIndex,
                                        int32_t worldViewId,
                                        NpcState* outNpc) = nullptr;
    uint8_t (*getWidgetAtPath)(const WidgetAddressState* address,
                               WidgetState* outState) = nullptr;
    uint32_t (*getActorPathQueueInWorldView)(uint64_t entityPtr,
                                             int32_t worldViewId,
                                             WorldPointState* out,
                                             uint32_t max) = nullptr;

    // --- SDK 94: target-aware click-point resolver ------------------
    uint8_t (*resolveActionClickPoint)(const ActionClickPointSpec* action,
                                       int32_t* screenX,
                                       int32_t* screenY) = nullptr;

    // --- SDK 95: unbounded plugin enumeration -----------------------
    /// Upper-bound count of plugins known to the host, used to size the
    /// buffer for listPlugins() so enumeration is no longer capped by a
    /// fixed constant. May slightly exceed the count listPlugins() returns
    /// (dedup); callers use it as a growth hint. Appended at the end of
    /// HostApi (never inserted) so existing fn-pointer offsets are unchanged
    /// and plugins built against SDK 91..94 keep loading.
    uint32_t (*getPluginCount)() = nullptr;

    // --- SDK 97: host-owned Break Handler registry ------------------
    // `pluginInstance` is compared with the currently loaded native
    // PluginApi::userData and is never retained or dereferenced by the host.
    // Registration and report data crosses the DLL only in fixed-capacity,
    // size/version-tagged records; the host validates every enum and string.
    // Java/JavaScript bridges validate their language object first and call
    // the same registry through PluginHost's runtime-identity entrypoints.
    uint8_t (*breakHandlerRegisterPlugin)(
        const void* pluginInstance,
        const BreakRegistrationState* registration) = nullptr;
    uint8_t (*breakHandlerStart)(const void* pluginInstance,
                                 const char* pluginId) = nullptr;
    uint8_t (*breakHandlerStop)(const void* pluginInstance,
                                const char* pluginId) = nullptr;
    uint8_t (*breakHandlerUnregisterPlugin)(const void* pluginInstance,
                                            const char* pluginId) = nullptr;
    uint8_t (*breakHandlerPoll)(const void* pluginInstance,
                                const char* pluginId,
                                BreakCommandState* outCommand) = nullptr;
    uint8_t (*breakHandlerReport)(
        const void* pluginInstance,
        const BreakReportState* report) = nullptr;

    /// Coordinator operations are accepted only from the loaded native
    /// `break_handler` plugin instance. Snapshot returns the total count even
    /// when `out` is null or `capacity` is smaller.
    uint32_t (*breakHandlerCoordinatorSnapshot)(
        const void* coordinatorInstance, const char* coordinatorId,
        BreakParticipantState* out, uint32_t capacity) = nullptr;
    uint8_t (*breakHandlerCoordinatorPublish)(
        const void* coordinatorInstance, const char* coordinatorId,
        const BreakCommandState* command) = nullptr;
    uint8_t (*breakHandlerCoordinatorClear)(
        const void* coordinatorInstance, const char* coordinatorId,
        uint64_t expectedEpoch) = nullptr;

    // --- SDK 97: load-lifetime owned services ----------------------
    /// Publish a native service together with the exact owning Plugin
    /// instance. The host copies the stable id/load generation and removes
    /// the entry automatically when that adapter unloads. Passing null for
    /// `service` unregisters only an entry owned by the same generation. A
    /// constructor-time address may be held opaquely until adapter creation,
    /// but is never dereferenced by the host.
    uint8_t (*registerPluginServiceOwned)(const void* pluginInstance,
                                          const char* pluginId,
                                          const char* serviceId,
                                          void* service) = nullptr;

    // --- SDK 97: sanitized proxy routing + launcher submit ----------
    /// Returns the total sanitized catalogue count. No endpoint, measured
    /// egress address, username, password, or other proxy secret crosses this
    /// ABI; status exposes only whether the current probe is ready.
    uint32_t (*listSanitizedProxyRoutes)(SanitizedProxyRouteState* out,
                                         uint32_t capacity) = nullptr;
    uint8_t (*setProxyRoute)(const char* proxyId,
                             ProxyRouteStatusState* outStatus) = nullptr;
    uint8_t (*getProxyRouteStatus)(ProxyRouteStatusState* outStatus) = nullptr;
    uint8_t (*submitLoginLauncherCredentials)() = nullptr;
    /// Queue one standard-login Enter press from MainLoop.
    uint8_t (*submitLoginStandardCredentials)() = nullptr;
    uint8_t (*acknowledgeStandardLogin)() = nullptr;
    /// Pollable MainLoop click-to-play operation: 0 not found/failed,
    /// 1 pending, 2 dispatched.
    uint8_t (*advanceLoginClickToPlay)() = nullptr;
    /// Versioned, non-secret login capabilities and latched transitions.
    uint8_t (*getLoginFlowState)(LoginFlowState* out) = nullptr;
    /// Pollable MainLoop launcher Enter submission. Returns LoginOperationAdvanceAbi.
    uint8_t (*advanceLoginLauncherCredentials)() = nullptr;
    /// Pollable MainLoop logout UI operation. Returns LoginOperationAdvanceAbi.
    /// Exact LoginScreen confirmation remains caller-owned.
    uint8_t (*advanceLoginLogout)() = nullptr;
    /// Invalidate pending launcher/click-to-play work for a cancelled profile
    /// activation. Does not cancel the independent logout recovery operation.
    void (*cancelLoginProfileOperations)() = nullptr;
    /// Invalidate pending host-owned logout UI work on coordinator teardown.
    void (*cancelLoginLogoutOperation)() = nullptr;

    // --- SDK 108: WorldView-aware tile-object live resolution -------
    uint32_t (*getTileObjectsOnTileInWorldView)(
        int32_t worldViewId, int32_t plane, int32_t tileX, int32_t tileY,
        TileObjectState* outObjects, uint32_t maxObjects) = nullptr;

    // --- SDK 109: interface-scale / canvas-origin accessor ----------
    /// Write the live OSRS in-game interface-scale factor into
    /// @p outScaleX / @p outScaleY. This is the ratio of physical
    /// (rendered) UI-frame pixels to widget-frame units the host derives
    /// from the game's canvas coordinate transform:
    /// `physical = widget * interfaceScale + canvasOrigin`.
    ///
    /// At 100% interface scaling the factor is 1.0; at 150% it is ~1.5,
    /// at 200% ~2.0. The value is independent of Windows display scaling.
    /// Plugins that draw their own local pixel geometry (fixed bar widths,
    /// anchor offsets) multiply that geometry by this factor so it lines
    /// up with the scaled widget overlays the host already positions.
    ///
    /// @p outCanvasOriginX / @p outCanvasOriginY optionally receive the
    /// physical-pixel canvas origin (fixed-mode pillarbox/letterbox
    /// offset); pass null to ignore.
    ///
    /// Returns 1 when the live canvas transform was read, 0 when the
    /// analyzer did not detect it on this revision -- in the 0 case the
    /// scale outputs are set to identity (1.0) and the origin outputs to
    /// 0 so callers can use the values unconditionally.
    uint8_t (*getInterfaceScale)(float* outScaleX, float* outScaleY,
                                 int32_t* outCanvasOriginX,
                                 int32_t* outCanvasOriginY) = nullptr;

    // --- SDK 112: bulk collision snapshots + web walker ------------
    /// Copy one cache collision region, flattened `[plane][x][y]` with y
    /// contiguous. `outCount` always receives the required flag count when
    /// non-null. Passing a null/short buffer is a non-mutating size query.
    uint8_t (*copyCachedCollisionRegion)(uint32_t regionId,
                                         int32_t* outFlags,
                                         uint32_t capacity,
                                         uint32_t* outCount) = nullptr;

    /// Copy the current scene collision maps and their coordinate/instance
    /// metadata as one immutable snapshot. The call fails closed if any tile
    /// cannot be read. Flags use `[plane][sceneX][sceneY]` layout.
    uint8_t (*copyCurrentCollisionScene)(CollisionSceneSnapshotState* outScene,
                                         int32_t* outFlags,
                                         uint32_t capacity,
                                         uint32_t* outCount) = nullptr;

    uint8_t (*webPathSubmit)(const WebPathRequestState* request,
                             const WorldPointState* forbiddenTiles,
                             uint32_t forbiddenTileCount,
                             uint64_t* outRequestId) = nullptr;
    uint8_t (*webPathPoll)(uint64_t requestId,
                           WebPathSummaryState* outSummary) = nullptr;
    uint8_t (*webPathCopySteps)(uint64_t requestId,
                                WebPathStepState* outSteps,
                                uint32_t capacity,
                                uint32_t* outCount) = nullptr;
    uint8_t (*webPathCancel)(uint64_t requestId) = nullptr;
    uint8_t (*webPathRelease)(uint64_t requestId) = nullptr;

    // --- SDK 113: native world-map display state ------------------
    /// Copy one coherent snapshot of the visible, loaded native world map.
    /// `outState` must carry the current structSize/apiVersion header. Returns
    /// zero and clears the record when the complete generated field contract,
    /// renderer-validated scale metadata, native state, or viewport is
    /// unavailable. No revision-specific fallback layout is permitted.
    uint8_t (*getWorldMapState)(WorldMapState* outState) = nullptr;

    // --- SDK 114: web walk executor + per-step payload access -----
    /// Begin walking the local player to the request's destination. At most
    /// one walk session is active per client; a new start supersedes (and
    /// cancels) the previous session, whose handle stays pollable until
    /// released. Returns zero on validation failure or when the world is not
    /// ready.
    uint8_t (*webWalkStart)(const WebWalkRequestState* request,
                            const WorldPointState* forbiddenTiles,
                            uint32_t forbiddenTileCount,
                            uint64_t* outWalkId) = nullptr;
    uint8_t (*webWalkStatus)(uint64_t walkId,
                             WebWalkStatusState* outStatus) = nullptr;
    uint8_t (*webWalkCancel)(uint64_t walkId) = nullptr;
    uint8_t (*webWalkRelease)(uint64_t walkId) = nullptr;
    /// Advance a WebWalkFlag::ManualTick session by one follower tick. Call
    /// once per game tick from the owning plugin's onGameTick. Returns zero
    /// for unknown, terminal, or auto-ticked sessions.
    uint8_t (*webWalkAdvance)(uint64_t walkId) = nullptr;
    /// UTF-8 JSON action payload for one step of a completed web path
    /// request (internal backend only). A null/short buffer is a size query:
    /// `outRequired` receives the byte count including the NUL terminator.
    uint8_t (*webPathCopyStepPayload)(uint64_t requestId, uint32_t stepIndex,
                                      char* outUtf8, uint32_t capacity,
                                      uint32_t* outRequired) = nullptr;

    // --- SDK 119: profile staging + generic credential submit -----
    /// Resolve an exact Account Profiles label and queue the profile's
    /// standard or Jagex credentials. Secrets remain owned by Account
    /// Profiles. Returns 1 when staging was accepted.
    uint8_t (*stageLoginCredentials)(const char* profileLabel) = nullptr;
    /// Queue a one-shot held Enter on the standard or live Jagex credential
    /// screen. The key is released on the following MainLoop iteration.
    uint8_t (*submitLoginCredentials)() = nullptr;

    // --- SDK 120: cheap live-state freshness epoch ------------------
    /// Return the current freshness epoch: a monotonic value equal to
    /// `ClientState::tickCount` at every point a plugin can observe, published
    /// atomically once per frame. Entity wrappers read this (one atomic load)
    /// on each accessor to decide whether their cached state is still current,
    /// instead of building a full ClientState via getClientState just to read
    /// tickCount. Appended at the tail of HostApi so existing fn-pointer offsets
    /// are unchanged; plugins built against SDK 116..119 keep loading, and the
    /// SDK falls back to getClientState when this pointer is null (old host).
    int32_t (*getLiveStateEpoch)() = nullptr;

    // --- SDK 122/123: true model-vertex outlines --------------------
    /// Draw the entity's TRUE model silhouette from its projected model
    /// vertices, rotated and anchored exactly where the engine renders it.
    /// Unlike drawEntityHull (which hulls the 8 AABB corners), this follows
    /// the real mesh, so tall/thin or irregular models get a tight outline
    /// instead of a box. @p typecode identifies the entity's model handle
    /// (bound by the host's model-AABB hook every frame); pass the same
    /// value used for drawEntityClickbox. @p mode selects the polygon: 0 =
    /// convex hull (clean tight silhouette), 1 = concave hull (hugs the
    /// vertices, follows concavities). Silent no-op when the model handle
    /// isn't bound this frame or the host lacks Model geometry offsets.
    /// Appended at the HostApi tail (ABI-safe); null on hosts older than
    /// SDK 122. The @p mode parameter was added in SDK 123 (the unreleased
    /// SDK-122 signature had no mode); pass 0 for the prior convex behaviour.
    void (*drawEntityOutline)(uint64_t entityPtr, uint64_t typecode,
                              uint32_t outline, uint32_t fill,
                              uint32_t mode) = nullptr;

    /// Loc / scene-object equivalent of drawEntityOutline. @p typecode is
    /// the native raw scene tag (TileObjectState::packedId).
    void (*drawTileObjectOutline)(uint64_t locPtr, uint64_t typecode,
                                  uint32_t outline, uint32_t fill,
                                  uint32_t mode) = nullptr;

    // --- SDK 125: current native client game cycle -----------------
    /// Return the analyzer-backed signed 32-bit native game-cycle value.
    /// This clock advances at the nominal 20 ms client logic cadence and is
    /// distinct from ClientState::tickCount; 30 cycles make one server tick.
    /// Null when the host predates SDK 125 or the loaded analyzer bundle did
    /// not provide a valid game-cycle source for the current revision.
    int32_t (*getGameCycle)() = nullptr;

    // SDK 127. Game-thread only. outLength receives the required UTF-8 byte
    // length, excluding any terminator (none is written). Returns Invalid,
    // Success, or BufferTooSmall; query with nullptr/0 then allocate locally.
    // Invalid is distinct from a successful empty string. Never truncates.
    uint8_t (*getActorOverheadText)(uint64_t entityPtr, char* out,
                                  uint64_t capacity, uint64_t* outLength) = nullptr;
    uint8_t (*getActorOverheadTextCyclesRemaining)(uint64_t entityPtr,
                                                  int32_t* out) = nullptr;
    /// Bit 1: validated direct access. Bit 2: all five utterance hooks installed.
    uint32_t (*getOverheadTextCapabilities)() = nullptr;
    /// SDK 128. Callback mode admits supported source/target legs together and
    /// cancels the target when source selection fails or changes. Legacy mode
    /// preserves its ordered two-action path. No stored native handles accepted.
    uint8_t (*executeSelectedActionPair)(const SelectedActionPair* pair) = nullptr;

    // --- SDK 131: full-frame game screenshot -------------------------
    /// Queue one capture of the game's presented frame -- the same image the
    /// controller's /tabs command returns: the full backbuffer after the
    /// AboveWidgets overlay pass, PNG-encoded. The pixels are read back on
    /// the next presented frame and encoded on a worker thread, so poll for
    /// ScreenshotPhase::Ready. At most kScreenshotMaxUnreleased handles may
    /// be outstanding; release each one. A request that never sees a
    /// presented frame fails on its own after a few seconds. Callable from
    /// any plugin callback thread. Returns zero when nothing was queued.
    uint8_t (*screenshotSubmit)(uint64_t* outRequestId) = nullptr;
    /// `outStatus` must carry the current structSize/apiVersion header.
    /// Returns zero for an unknown or released handle.
    uint8_t (*screenshotPoll)(uint64_t requestId,
                              ScreenshotStatusState* outStatus) = nullptr;
    /// Copy a Ready request's PNG. A null/short buffer is a size query:
    /// `outRequired` receives the byte count and the call returns 1 without
    /// writing. Returns zero for any handle that is not Ready.
    uint8_t (*screenshotCopyPng)(uint64_t requestId, uint8_t* out,
                                 uint32_t capacity, uint32_t* outRequired) = nullptr;
    uint8_t (*screenshotRelease)(uint64_t requestId) = nullptr;

    /// SDK 136. Thread-safe copies of the game-thread snapshot. Unavailable
    /// data is distinct from a valid Empty offer. Never returns native pointers.
    bool (*getGrandExchangeOffer)(int32_t slot, GrandExchangeOffer* out) = nullptr;
    /// nullptr/0 queries required count. A short buffer is left untouched and
    /// returns the required count; otherwise copies all slots. 0 = unavailable.
    /// Full snapshots wait until captured offer events finish delivery; single
    /// slots and owned event payloads remain independently readable.
    int32_t (*getGrandExchangeOffers)(GrandExchangeOffer* out, int32_t capacity) = nullptr;
    bool (*isGrandExchangeAvailable)() = nullptr;

    /// SDK 137. Idempotent async requests; fresh/pending requests coalesce.
    /// False means invalid id, full bounded queue, or client shutting down.
    bool (*requestItemPriceCatalog)() = nullptr;
    bool (*requestItemPrice)(int32_t id) = nullptr;
    bool (*getItemPriceStatus)(ItemPriceStatus* out) = nullptr;
    bool (*getItemPriceMetadata)(int32_t id, ItemPriceMetadata* out) = nullptr;
    /// nullptr/0 queries count. Short buffers receive no writes. 0 = no catalog.
    int32_t (*getItemPriceItemIds)(int32_t* out, int32_t capacity) = nullptr;
    bool (*getItemPrice)(int32_t id, ItemPrice* out) = nullptr;
    /// SDK 138. Thread-safe owned snapshot, including unknown/loading status.
    /// False only when unsupported/shutting down; never reads another account.
    bool (*getItemCacheBank)(BankCacheState* out) = nullptr;
    /// SDK 139. Shared GE queue; handles survive completion until released.
    /// The first argument is ignored (pass nullptr), retained for ABI compatibility.
    uint64_t (*geSubmitBuy)(const char* legacyUnused, const GeBuyOptions* options) = nullptr;
    bool (*geGetRequest)(const char* legacyUnused, uint64_t id, GeRequestState* out) = nullptr;
    /// nullptr/0 queries count. Short buffers receive no writes. Includes terminal handles.
    int32_t (*geGetRequests)(const char* legacyUnused, GeRequestState* out, int32_t capacity) = nullptr;
    bool (*geCancelRequest)(const char* legacyUnused, uint64_t id) = nullptr;
    bool (*geReleaseRequest)(const char* legacyUnused, uint64_t id) = nullptr;

    /// SDK 140. Report that the plugin itself changed one of its own
    /// settings. The typed titan::Setting members call this for you; call
    /// it directly only when you bypass them.
    ///
    /// @p value the setting's NEW value, which the controller saves to the
    /// user's config exactly as it saves a side-panel edit, or nullptr if
    /// the value did not change.
    ///
    /// @p hidden the setting's NEW visibility, or nullptr if it did not
    /// change. Visibility is presentation-only: the panel repaints and
    /// nothing is written to the user's config.
    ///
    /// Both null is a no-op. Both set is a value change that also changed
    /// visibility.
    ///
    /// The value travels WITH the report rather than being read back from
    /// the plugin later, and that is load-bearing: the host serves managed
    /// (Java / JS) plugins' settings from a cache it refreshes on its own
    /// schedule, so a report that carried only "something changed" would
    /// pair a fresh signal with a stale value and persist the wrong one.
    ///
    /// Cheap and thread-safe: it records the value against a per-(plugin,
    /// setting) counter and marks the snapshot dirty. The controller
    /// persists on its next page, so calling it every tick coalesces into
    /// a single write of the latest value.
    ///
    /// The host NEVER routes its own writes through here -- a value the
    /// controller pushed down arrives via PluginApi::setSetting, so a
    /// clamp or a failed decode cannot be mistaken for plugin intent.
    void (*markSettingChanged)(const char* pluginId, const char* settingKey,
                               const TitanNativeRecords::Value* value,
                               const uint8_t* hidden) = nullptr;

    // --- SDK 141: Cross-Tab Store --------------------------------------
    // `pluginInstance` is compared with the namespace binding the host made
    // when it loaded that exact instance, and is never dereferenced;
    // `pluginId` must name the same plugin. A call from a plugin's
    // constructor fails: the instance is not bound until the host has
    // wrapped it. Both entries take only the store's own lock, never the
    // host's, so they are safe from any thread and from inside the plugin's
    // own locks.

    /// Queue one put or erase (CrossTabWrite::flags). Returns 1 and the write
    /// id (never 0) when accepted, 0 when refused locally: bad key or flags,
    /// value too large, a namespace limit, a secret in a tab this controller
    /// did not launch, or a full outbox.
    uint8_t (*crossTabWrite)(const void* pluginInstance, const char* pluginId,
                             const CrossTabWrite* write,
                             uint64_t* outWriteId) = nullptr;
    /// Read a key's value as this tab sees it. Returns its size, copying the
    /// value into @p out only when it fits in @p capacity; a null or short
    /// buffer is a size query and writes nothing to @p out. @p info, when
    /// non-null, is filled for a present key. Returns kCrossTabAbsent for an
    /// absent key or a refused call. A redacted secret reads as size 0.
    uint32_t (*crossTabRead)(const void* pluginInstance, const char* pluginId,
                             const char* key, uint8_t* out, uint32_t capacity,
                             CrossTabEntryInfo* info) = nullptr;

    // --- SDK 142: Preview pills ------------------------------------------
    /// Set or clear one of this plugin's preview pills. `pluginInstance` and
    /// `pluginId` are checked exactly as crossTabWrite checks them, so a call
    /// from the plugin's constructor fails. Returns 1 when applied, 0 when
    /// refused: an unbound or disabled plugin, a bad key or flags, empty text
    /// without a countdown, or a new pill past kPreviewPillsPerPlugin or
    /// kPreviewPillsPerTab. Clearing a pill that is not shown returns 1.
    /// Takes only the pill store's own lock, never the host's, so it is safe
    /// from any thread and from inside the plugin's own locks.
    uint8_t (*previewPillWrite)(const void* pluginInstance, const char* pluginId,
                                const PreviewPillWrite* write) = nullptr;

    // --- SDK 144: Break Handler observation ------------------------------
    /// Copy the break command the coordinator has currently published into
    /// `outCommand` without registering. This is the host's global command,
    /// not a participant's copy, so nothing is recorded: no pause quorum is
    /// joined and no report epoch moves. `pluginInstance` and `pluginId` are
    /// checked exactly as breakHandlerPoll checks them. Returns 1 for a
    /// loaded, enabled plugin -- phase BREAK_PHASE_NONE when no break is in
    /// progress -- and 0 when refused. `outCommand` must carry the current
    /// structSize and apiVersion.
    uint8_t (*breakHandlerObserve)(const void* pluginInstance,
                                   const char* pluginId,
                                   BreakCommandState* outCommand) = nullptr;

    /// Optional immutable service tables owned by the host for its entire
    /// lifetime. This whitelist never returns pointers owned by plugin DLLs.
    void* (*getHostService)(const char* serviceId) = nullptr;

    // Optional native HostDefinitionExtrasV1 operations. This consumer-local
    // view is not the DLL ABI; existing native tables and payloads stay frozen.
    /// Game-thread read. A successful result may be -1 (widget has no model).
    uint8_t (*getWidgetModelIdAtPath)(const WidgetAddressState* address,
                                     int32_t* outModelId) = nullptr;
    /// Cache params 451..458, indexed 0..7. Required size includes NUL. Null
    /// or short buffers query the size without writing. Known empty rows
    /// succeed with size 1; unavailable capability/item or bad index fails.
    uint8_t (*copyItemWornAction)(int32_t itemId, uint32_t actionIndex,
                                 char* outUtf8, uint32_t capacity,
                                 uint32_t* outRequired) = nullptr;
    /// Game-thread read of the spawned NPC definition before transformations.
    /// Resolve the NPC's live identity in the given world view; negative view
    /// selects the current view. Missing identity/definition returns 0.
    uint8_t (*getNpcBaseId)(int32_t worldViewId, int32_t hashIndex,
                           int32_t* outBaseId) = nullptr;
};

/// Consumer-local callback view, populated from the negotiated native tables.
/// The descriptor owns destruction; native_abi.h defines the DLL contract.
struct PluginApi {
    uint32_t sdkVersion = kSdkVersion;
    void* userData = nullptr;

    // --- Identity (must always be set) ---
    const char* (*getId)(void* userData) = nullptr;
    const char* (*getName)(void* userData) = nullptr;
    /// Enumerate the plugin's side panels (SDK 65: replaces the singular
    /// getHasPanel/getPanelTitle pair). Writes up to `maxPanels` descriptors
    /// and returns the count. A plugin with no custom panel returns 0.
    uint32_t (*getPanels)(void* userData, TitanNativeRecords::PanelDescriptor* outPanels,
                          uint32_t maxPanels) = nullptr;

    // --- Metadata (SDK 23; optional, may be null for pre-SDK-23 plugins) ---
    /// One-line description shown as a tooltip in the plugin list.
    const char* (*getDescription)(void* userData) = nullptr;
    /// Author / team name.
    const char* (*getAuthor)(void* userData) = nullptr;
    /// Short version string (e.g. "1.0.3").
    const char* (*getVersion)(void* userData) = nullptr;
    /// Returns 1 if the plugin should be enabled by default on first install
    /// (no persisted controller state). 0 to ship disabled. The host calls
    /// this once at load time and applies it via `setEnabled`. Null defaults
    /// to enabled.
    uint8_t (*getDefaultEnabled)(void* userData) = nullptr;

    // --- Settings ---
    uint8_t (*getEnabled)(void* userData) = nullptr;
    void (*setEnabled)(void* userData, uint8_t enabled) = nullptr;
    uint32_t (*getSettings)(void* userData, TitanNativeRecords::Setting* outSettings, uint32_t maxSettings) = nullptr;
    uint8_t (*setSetting)(void* userData, const char* settingKey,
                          const TitanNativeRecords::Value* value,
                          char* errOut, uint32_t errOutLen) = nullptr;

    // --- Sections (new in SDK 18) ---
    /// Serialize the plugin's declared sections into outSections. Returns the count
    /// written (capped at maxSections). Optional: plugins without sections may
    /// leave this null.
    uint32_t (*getSections)(void* userData, TitanNativeRecords::Section* outSections, uint32_t maxSections) = nullptr;

    // --- Lifecycle (called by host, may be null) ---
    void (*onEnable)(void* userData) = nullptr;
    void (*onDisable)(void* userData) = nullptr;
    void (*onGameTick)(void* userData, int32_t tickCount) = nullptr;
    /// Called for each render layer pass. @p layer matches the RenderLayerAbi constants.
    /// The plugin's thunk is responsible for dispatching to any per-overlay handlers
    /// registered for that layer.
    void (*renderOverlay)(void* userData, uint8_t layer) = nullptr;

    /// Build the ImGui command list for the panel identified by `panelId`
    /// (SDK 65: panelId selects among the plugin's panels). Writes up to
    /// `maxElements` and returns the count.
    uint32_t (*getPanelElements)(void* userData, const char* panelId,
                                 TitanNativeRecords::PanelElement* outElements,
                                 uint32_t maxElements) = nullptr;
    /// Handle a control interaction on the panel identified by `panelId`.
    void (*onPanelAction)(void* userData, const char* panelId, int32_t actionId,
                          const TitanNativeRecords::Value* value) = nullptr;
    /// Fetch the custom PNG image icon for `panelId` (SDK 65). Writes up to
    /// `maxBytes` into `outBytes` and returns the byte count, or 0 when the
    /// panel has no image icon. May be null on plugins that never use image
    /// icons.
    uint32_t (*getPanelIcon)(void* userData, const char* panelId,
                             uint8_t* outBytes, uint32_t maxBytes) = nullptr;

    void (*destroy)(void* userData) = nullptr;

    // --- Entity spawn / despawn events ---
    void (*onClientTick)(void* userData) = nullptr;
    void (*onProjectileSpawned)(void* userData, const ProjectileState* proj) = nullptr;
    void (*onProjectileDespawned)(void* userData, const ProjectileState* proj) = nullptr;
    void (*onProjectileMoved)(void* userData, const ProjectileState* proj) = nullptr;
    /// SDK 57+. Fired the first tick a `MapSpotAnim` appears in the live
    /// `WorldView::GraphicsObjectList`.
    void (*onGraphicsObjectSpawned)(void* userData, const GraphicsObjectState* obj) = nullptr;
    /// SDK 57+. Fired the first tick a previously-tracked `MapSpotAnim` is
    /// no longer in the list (despawn / scene unload).
    void (*onGraphicsObjectDespawned)(void* userData, const GraphicsObjectState* obj) = nullptr;
    /// SDK 57+. Fired when an active `MapSpotAnim` changes scene-space
    /// position between ticks.
    void (*onGraphicsObjectMoved)(void* userData, const GraphicsObjectState* obj) = nullptr;
    void (*onNpcSpawned)(void* userData, const NpcState* npc) = nullptr;
    void (*onNpcDespawned)(void* userData, const NpcState* npc) = nullptr;
    void (*onPlayerSpawned)(void* userData, const PlayerState* player) = nullptr;
    void (*onPlayerDespawned)(void* userData, const PlayerState* player) = nullptr;
    void (*onTileObjectSpawned)(void* userData, const TileObjectState* obj) = nullptr;
    void (*onTileObjectDespawned)(void* userData, const TileObjectState* obj) = nullptr;

    /// Called when a menu option is about to be executed. Set event->consumed = 1 to block it.
    void (*onMenuOptionClicked)(void* userData, MenuOptionClickedEvent* event) = nullptr;

    /// Called when a CS2 script executes. Fires for every script -- filter by scriptId early.
    void (*onScriptFired)(void* userData, const ScriptFiredEvent* event) = nullptr;

    /// Called when a varbit's resolved value changes. Fires only on actual
    /// value changes (no-op writes are filtered). Added in SDK 21.
    void (*onVarbitChanged)(void* userData, const VarbitChangedEvent* event) = nullptr;

    /// Called when the native chat pipeline adds a line to the in-game
    /// chatbox. Fires for every `AddChat` arrival (server + local + plugin
    /// injects). Fixed-capacity strings inside the event are owned by the
    /// host and truncate silently when the source line is long; do not
    /// retain pointers past the callback. Added in SDK 22.
    void (*onChatMessage)(void* userData, const ChatMessageEvent* event) = nullptr;

    /// Called when an item container's slot contents differ from the
    /// previous tick's snapshot. The host diffs on the game thread AFTER
    /// dispatching `onGameTick` so plugin code can rely on a fresh tick
    /// counter. Event data is valid only for the duration of the
    /// callback; copy out what you need. Added in SDK 26.
    void (*onItemContainerChanged)(void* userData,
        const ItemContainerChangedEvent* event) = nullptr;

    /// Called when the plugin's own setting has just changed (after setSetting succeeded).
    /// @p settingKey may be null for the enabled-flag change.
    void (*onSettingChanged)(void* userData, const char* settingKey) = nullptr;

    /// SDK 66+. Enumerate the plugin ids this plugin depends on. The host
    /// loads dependencies before dependents and rejects cycles / missing
    /// deps. Writes up to `maxIds` NUL-terminated ids (each capped to
    /// TitanNativeRecords::kMaxIdLen) and returns the count. Null / 0 means no
    /// dependencies.
    uint32_t (*getDependencies)(void* userData,
                                char outIds[][TitanNativeRecords::kMaxIdLen],
                                uint32_t maxIds) = nullptr;

    /// SDK 69+. Called when the native client requests a discrete sound
    /// effect, upstream of definition lookup and backend scheduling. Set
    /// event->consumed = 1 to mark the event handled and stop later sound
    /// handlers in this dispatch. Per-sound playback suppression is not
    /// supported by the current hook; use HostApi audio playback toggles for
    /// global muting. Fires on the game thread; do not retain the pointer.
    /// Covers both synth sound effects and MIDI jingles (see `event->kind`).
    void (*onSoundPlayed)(void* userData, SoundPlayedEvent* event) = nullptr;

    /// SDK 74+. Called when the native client applies a hitsplat to a
    /// resolved player or NPC. The event embeds PlayerState/NpcState
    /// snapshots and sets actorType to PLAYER, NPC, or NONE when the host
    /// could not resolve the actor. SDK 76 corrected the native signature and
    /// replaced the old auxiliary field with `limit`.
    void (*onHitsplatApplied)(void* userData,
                              const HitsplatAppliedEvent* event) = nullptr;

    /// SDK 76+. Called when the native client applies an actor-attached
    /// spotanim to a resolved player or NPC. Clear/removal ids are filtered
    /// before dispatch. This is distinct from hitsplats/damage.
    void (*onActorSpotAnim)(void* userData, const ActorSpotAnimEvent* event) = nullptr;

    /// SDK 78+. Called when the native client accepts an actor animation field
    /// change through ACTOR_ANIMATION / SetAnimation. Fires only when the
    /// post-original Actor::Animation value differs from the pre-call value.
    void (*onAnimationChanged)(void* userData,
                               const AnimationChangedEvent* event) = nullptr;

    /// SDK 91+. Called when the native client accepts a Client.GameState
    /// transition through SetGameState.
    void (*onGameStateChanged)(void* userData,
                               const GameStateChangedEvent* event) = nullptr;

    /// SDK 115+. Called for a real (non-synthetic) mouse button PRESS before
    /// the native client processes it, carrying client-area x/y, the button,
    /// and the KeyboardMods bitmask. Set event->consumed = 1 to suppress the
    /// native click (the host then force-consumes the paired release). Fires on
    /// the input/pump thread -- do not read game memory here; defer to a
    /// game-thread callback. Double-clicks arrive as an extra press.
    void (*onMousePressed)(void* userData, MouseButtonEvent* event) = nullptr;

    /// SDK 115+. Called for a real (non-synthetic) mouse button RELEASE, same
    /// payload and threading as onMousePressed. Observation-only with respect
    /// to suppression: setting event->consumed here does NOT suppress the
    /// native release (press consumption alone governs the pair). If the
    /// matching press was consumed, the host suppresses that release natively
    /// and does NOT invoke this callback for it, so the game's button state
    /// stays balanced.
    void (*onMouseReleased)(void* userData, MouseButtonEvent* event) = nullptr;

    /// SDK 118+. Called once per outer MAIN_LOOP iteration before the
    /// MainLoop dispatcher drain and native loop body. Unlike onClientTick,
    /// this also fires on title/login screens. Static definition-cache reads
    /// are always available; callers must check world-ready state before live
    /// client/entity/widget/scene/projection queries.
    void (*onMainLoop)(void* userData) = nullptr;

    /// SDK 127. Every accepted utterance, including equal and empty text.
    /// Actor identity is captured before dispatch; expiry is excluded. The
    /// borrowed event and its text view are valid only until callback return.
    void (*onOverheadTextChanged)(void* userData,
                                  const OverheadTextChangedEvent* event) = nullptr;

    /// SDK 136. Native slot replacement, including repeated equal updates.
    /// Initial observation of a login emits Empty for all slots, followed by
    /// observed offers. Payload is owned; copy it to retain beyond the callback.
    void (*onGrandExchangeOfferChanged)(void* userData,
        const GrandExchangeOfferChangedEvent* event) = nullptr;

    /// SDK 141. A key in this plugin's Cross-Tab Store namespace changed, the
    /// instance just bound (one Replay per key), or one of its own writes has
    /// an outcome. Game thread, from the MainLoop drain, whether or not the
    /// plugin is enabled. The event is owned by the host; it never carries
    /// the value.
    void (*onCrossTabChanged)(void* userData,
                              const CrossTabChangeEvent* event) = nullptr;

    /// Final instance teardown, after new work is refused and finite calls
    /// drain. Signal and join load-lifetime workers here; this is independent
    /// of the plugin's UI enabled state. No new work may be submitted.
    void (*prepareUnload)(void* userData) = nullptr;
};

/// Historical local embedding signatures. These tables must never cross a
/// Native ABI v1 DLL boundary; native_abi.h defines the descriptor factory.
using CreatePluginFn = uint8_t (*)(const HostApi* hostApi, PluginApi* outApi, char* errOut, uint32_t errOutLen);

/// Local multi-plugin embedding signatures, retained with CreatePluginFn.
using PluginCountFn = uint32_t (*)();
using CreatePluginAtFn = uint8_t (*)(uint32_t index, const HostApi* hostApi,
                                     PluginApi* outApi, char* errOut,
                                     uint32_t errOutLen);

}  // namespace TitanPluginSdk
