/// @file titan/plugin.h
/// @brief Public plugin SDK entry point: titan::Plugin base + registration macros.
///
/// Plugin authors write a subclass of titan::Plugin, declare settings / sections /
/// overlays as member variables (they self-register via their constructors),
/// override the lifecycle virtuals they care about, and invoke
/// TITAN_REGISTER_PLUGIN(MyClass) at file scope. That macro emits the
/// TitanPlugin_QueryNative export and its Native ABI v1 module descriptor.
/// Each created instance owns its callback tables and imported host view.
///
/// Example:
/// @code
///   #include <titan/plugin.h>
///
///   class MyPlugin : public titan::Plugin {
///       TITAN_PLUGIN("my_plugin", "My Plugin")
///
///       titan::BoolSetting hello{this, "hello", "Hello", true};
///
///       void onGameTick(int tick) override {
///           if (tick % 10 == 0 && hello) titan::log("hi");
///       }
///   };
///   TITAN_REGISTER_PLUGIN(MyPlugin, "my_plugin")
/// @endcode

#pragma once

#include "detail/abi.h"
#include "detail/backend.h"
#include "detail/backend_external.h"
#include "detail/host.h"
#include "detail/native_abi.h"
#include "detail/native_lifetime.h"
#include "detail/plugin_context.h"
#include "detail/registrable.h"
#include "events.h"
#include "panel.h"
#include "html_panels.h"

#include <algorithm>
#include <cstring>
#include <functional>
#include <memory>
#include <string>
#include <utility>
#include <vector>

namespace titan {

// Forward declarations for the OverlayPanel factory below. The full
// definitions live in <titan/overlay_panel.h>, which includes this
// header. Plugin code that wants the factory must include
// <titan/overlay_panel.h> for the inline method bodies.
enum class Anchor : uint8_t;
class OverlayPanel;

/// One change to a key of this plugin's Cross-Tab Store namespace, delivered
/// to Plugin::onCrossTabChanged (SDK 141). It never carries the value: read
/// it with titan::crossTab(*this) from <titan/cross_tab.h>.
struct CrossTabChange {
    enum Kind : uint8_t {
        Set = TitanPluginSdk::CROSS_TAB_CHANGE_SET,           ///< present at `version`
        Erased = TitanPluginSdk::CROSS_TAB_CHANGE_ERASED,     ///< absent
        Rejected = TitanPluginSdk::CROSS_TAB_CHANGE_REJECTED, ///< write `writeId` was refused
    };
    enum Origin : uint8_t {
        Remote = TitanPluginSdk::CROSS_TAB_ORIGIN_REMOTE,    ///< another tab or instance wrote it
        Replay = TitanPluginSdk::CROSS_TAB_ORIGIN_REPLAY,    ///< this instance just bound
        Outcome = TitanPluginSdk::CROSS_TAB_ORIGIN_OUTCOME,  ///< the result of this instance's write
    };
    enum Cause : uint8_t {
        Writer = TitanPluginSdk::CROSS_TAB_CAUSE_WRITER,
        SessionReset = TitanPluginSdk::CROSS_TAB_CAUSE_SESSION_RESET,
        Limit = TitanPluginSdk::CROSS_TAB_CAUSE_LIMIT,
        Conflict = TitanPluginSdk::CROSS_TAB_CAUSE_CONFLICT,
        NotPermitted = TitanPluginSdk::CROSS_TAB_CAUSE_NOT_PERMITTED,
    };

    std::string key;
    Kind kind = Set;
    Origin origin = Remote;
    Cause cause = Writer;
    bool secret = false;
    bool redacted = false;  ///< a secret whose value this tab may not hold
    /// Set/Erased: the version it happened at. Outcome: the version the write
    /// got. Rejected: the key's version after the revert -- re-read the key.
    /// 0 for a pending value, a session reset, and a key a resync left out.
    uint64_t version = 0;
    /// Outcome and Rejected: the write's id, as putIf/eraseIf returned it
    /// (put and erase do not hand theirs out). Otherwise 0.
    uint64_t writeId = 0;
};

/// Abstract base class every native plugin inherits from.
///
/// Lifecycle callbacks are virtual no-ops by default so subclasses can override
/// only what they need. Identity (id / name / optional panel title) is supplied
/// via the TITAN_PLUGIN macro. Settings, sections and overlays declared as
/// members register themselves with this instance during construction.
class Plugin {
public:
    virtual ~Plugin() = default;

    // --- Identity (supplied by TITAN_PLUGIN) ---
    virtual const char* id() const = 0;
    virtual const char* name() const = 0;

    // --- Metadata (supplied by TITAN_PLUGIN_META or individual overrides).
    //     Added in SDK 23. Default is empty string so plugins built against
    //     the older macro still compile unchanged.
    virtual const char* description() const { return ""; }
    virtual const char* author() const { return ""; }
    virtual const char* version() const { return ""; }

    // --- Dependencies (SDK 66) ---
    /// Plugin ids this plugin depends on. The host loads dependencies before
    /// this plugin and rejects cycles / missing deps. Supplied via the
    /// TITAN_PLUGIN_DEPS (type-based) or TITAN_PLUGIN_DEP_IDS (string-based)
    /// macros; default is no dependencies.
    virtual std::vector<const char*> dependencies() const { return {}; }

    /// Whether the plugin should start enabled on a fresh install (no saved
    /// controller state). This is pure metadata -- the *controller* reads
    /// it via `getDefaultEnabled` while scanning plugin DLLs, and uses it
    /// as the initial `enabled` state when no persisted controller config
    /// exists for this plugin id. The client-side plugin host starts
    /// plugins disabled and waits for the controller to push the real state
    /// via `setEnabled`.
    ///
    /// Default is **false** so plugins are opt-in by design -- no surprise
    /// automation, overlays, or background behaviour activates on install
    /// until the user explicitly enables it in the controller. Plugins
    /// that want to ship on by default (e.g. login helpers) must declare
    /// it via the 6th `TITAN_PLUGIN_META` parameter or override this
    /// virtual directly.
    virtual bool isDefaultEnabled() const { return false; }

    // TODO(sdk): Add author-declared `alwaysEnabled` metadata for plugins
    // that do not expose a user-controlled enable/disable state. Show a checked,
    // dimmed toggle in the controller and enforce the policy in every toggle
    // entry point, independently of saved state and isDefaultEnabled(). Host
    // teardown, fault handling and hot reload must still stop/unload the plugin.
    // Add this through a versioned metadata extension; keep Native ABI v1 frozen.

    // --- Enable/disable lifecycle ---
    bool isEnabled() const { return enabled_; }

    /// Called when the host is about to enable or disable the plugin.
    /// Default: forwards to onEnable()/onDisable().
    virtual void onEnabledChanged(bool enabled) {
        if (enabled) onEnable(); else onDisable();
    }
    virtual void onEnable() {}
    virtual void onDisable() {}
    /// Final unload only, after new work is refused and finite calls drain.
    /// Signal and join any load-lifetime workers here. This is independent of
    /// the UI enabled state and runs before the instance destructor.
    virtual void onUnload() {}

    // --- Tick callbacks ---
    virtual void onClientTick() {}
    /// Called once per outer client MAIN_LOOP iteration, including title/login
    /// screens. Check `state::login().isWorldReady()` before live gameplay
    /// queries; static definition-cache reads are available in every state.
    /// Added in SDK 118.
    virtual void onMainLoop() {}
    virtual void onGameTick(int32_t /*tickCount*/) {}

    // --- Setting changes ---
    /// Fired after a setting was successfully updated from the controller.
    virtual void onSettingChanged(const std::string& /*settingKey*/) {}

    // --- Cross-Tab Store (SDK 141) ---
    /// A key in this plugin's namespace changed in another tab or instance,
    /// this instance just bound (one Replay per key it can see), or one of
    /// its own writes has an outcome. Game thread, whether or not the plugin
    /// is enabled, with the host's dispatch lock held: record what changed
    /// and wake a worker; never block here. See <titan/cross_tab.h>.
    virtual void onCrossTabChanged(const CrossTabChange& /*change*/) {}

    // --- Entity lifecycle ---
    virtual void onNpcSpawned(const Npc&) {}
    virtual void onNpcDespawned(const Npc&) {}
    virtual void onPlayerSpawned(const Player&) {}
    virtual void onPlayerDespawned(const Player&) {}
    virtual void onTileObjectSpawned(const TileObject&) {}
    virtual void onTileObjectDespawned(const TileObject&) {}
    virtual void onProjectileSpawned(const Projectile&) {}
    virtual void onProjectileDespawned(const Projectile&) {}
    virtual void onProjectileMoved(const Projectile&) {}
    virtual void onGraphicsObjectSpawned(const GraphicsObject&) {}
    virtual void onGraphicsObjectDespawned(const GraphicsObject&) {}
    virtual void onGraphicsObjectMoved(const GraphicsObject&) {}

    // --- Other events ---
    virtual void onMenuOptionClicked(MenuClickEvent&) {}
    virtual void onScriptFired(const ScriptEvent&) {}

    /// Called when the native client requests a discrete sound effect.
    /// Call `event.consume()` to stop later sound handlers in this dispatch,
    /// or use `titan::state::audio().setPlaybackDisabled(true)` to mute native
    /// playback globally. Covers synth sound effects and MIDI jingles
    /// (`event.kind()`). Fires on the game thread. Added in SDK 69.
    virtual void onSoundPlayed(SoundPlayedEvent&) {}

    /// Called when the native client applies a visible hitsplat to an actor.
    /// The host resolves the actor before dispatch; `event.actor()` is a
    /// Player/Npc variant or empty when resolution failed. Clear/removal
    /// writes from the old false hook are not part of this event. Added in
    /// SDK 74; native signature corrected in SDK 76.
    virtual void onHitsplatApplied(const HitsplatAppliedEvent&) {}

    /// Called when the native client applies an actor-attached spotanim to an
    /// actor. The host resolves the actor before dispatch; `event.actor()` is
    /// a Player/Npc variant or empty when resolution failed. Clear/removal
    /// ids are filtered by the client. Added in SDK 76.
    virtual void onActorSpotAnim(const ActorSpotAnimEvent&) {}

    /// Called when the native client accepts an actor animation change. The
    /// host resolves the actor before dispatch; `event.actor()` is a
    /// Player/Npc variant or empty when resolution failed. Same-animation
    /// resets and rejected lower-priority native requests are filtered by the
    /// client. Added in SDK 78.
    virtual void onOverheadTextChanged(const OverheadTextChangedEvent&) {}
    virtual void onGrandExchangeOfferChanged(const GrandExchangeOfferChangedEvent&) {}
    virtual void onAnimationChanged(const AnimationChangedEvent&) {}

    /// Called when a varbit's resolved value changes. Fires only when the
    /// new value actually differs from the old value (no-op writes are
    /// filtered by the host). Added in SDK 21.
    virtual void onVarbitChanged(const VarbitChangedEvent&) {}

    /// Called for every chat line the native pipeline adds to the chatbox:
    /// server messages, local system lines, and plugin-injected lines via
    /// `titan::addChatMessage()`. Strings inside the event wrapper are
    /// snapshots owned by the host and truncated to fixed capacity.
    /// Added in SDK 22.
    virtual void onChatMessage(const ChatMessageEvent&) {}

    /// Called when the native client accepts a Client.GameState transition
    /// through SetGameState. Loading (25) is exposed in SDK 126. Added in SDK 91.
    virtual void onGameStateChanged(const GameStateChangedEvent&) {}

    /// Called when an item container's slot contents differ from the
    /// previous tick's snapshot. Fires at game-tick cadence after
    /// onGameTick. Only fires for containers that have an entry in the
    /// native ClientInvCache hashtable -- i.e. those the server has
    /// sent an UPDATE_INV for at least once this session. Added in
    /// SDK 26.
    virtual void onItemContainerChanged(const ItemContainerChangedEvent&) {}

    /// Called for a real (non-synthetic) mouse button PRESS before the native
    /// client processes it. `event` carries client-area x/y, the button, and
    /// the keyboard-modifier state; call `event.consume()` to stop the click
    /// reaching the game's WndProc input pipeline (the host then also
    /// suppresses the matching release). The game's low-level input pipeline
    /// still observes real hardware input, so consumption is best-effort
    /// gameplay suppression, not input invisibility. Double-click messages
    /// arrive as an extra press.
    ///
    /// THREADING: fires on the input (message-pump) thread, which may NOT be
    /// the game thread. Do not read game state or call `titan::*` queries here;
    /// copy the fields out and act from a game-thread callback such as
    /// `onClientTick`. Added in SDK 115.
    virtual void onMousePressed(MouseButtonEvent&) {}

    /// Called for a real (non-synthetic) mouse button RELEASE. Same payload and
    /// threading as `onMousePressed`, but observation-only with respect to
    /// suppression: `consume()` here does not suppress the native release
    /// (press consumption alone governs the pair). If the matching press was
    /// consumed, the host suppresses that release natively and this callback
    /// is NOT invoked for it, keeping the game's button state balanced.
    /// Added in SDK 115.
    virtual void onMouseReleased(MouseButtonEvent&) {}

    // --- Side panels ---
    /// Register a side panel. A plugin may register several; each one gets its
    /// own nav button in the controller's right-hand rail. Call from the
    /// plugin's constructor body. Returns a reference to the SidePanel so the
    /// optional setters (onAction / icon / iconColor / image) can be chained:
    ///
    /// @code
    ///   MyPlugin() {
    ///       panel("main", "My Tools", [this](titan::Panel& p) {
    ///           p.button("Refresh", 1);
    ///       }).onAction([this](int32_t id, const auto&) { ... })
    ///         .icon("awesome:gear")
    ///         .iconColor(0xFF68CC92);
    ///   }
    /// @endcode
    SidePanel& panel(const char* id, const char* title,
                     std::function<void(Panel&)> build) {
        _validateSidePanelId(id);
        auto sp = std::make_unique<SidePanel>(id ? id : "", title ? title : "",
                                              std::move(build));
        SidePanel& ref = *sp;
        panels_.push_back(std::move(sp));
        return ref;
    }

    /// Register embedded HTML resources. Metadata/callbacks are configured in
    /// the constructor; state/messages may be published throughout the lifetime.
    HtmlSidePanel& htmlPanel(const char* id, const char* title, const HtmlPanelBundle& bundle) {
        _requireHtmlRegistrationOpen();
        _validateSidePanelId(id);
        auto panel = std::make_unique<HtmlSidePanel>(id, title ? title : "", bundle);
        auto& result = *panel; htmlPanels_.push_back(std::move(panel)); return result;
    }
    HtmlOverlayPanel& htmlOverlayPanel(const char* id, const HtmlPanelBundle& bundle, HtmlOverlayOptions options = {}) {
        _requireHtmlRegistrationOpen();
        if (!id || !TitanHtmlUi::validId(id)) throw std::invalid_argument("invalid HTML overlay ID");
        if (htmlOverlays_.size() >= TitanHtmlUi::kMaxHtmlOverlays) throw std::invalid_argument("a plugin may register at most eight HTML overlays");
        if (_findHtml(TitanHtmlUi::SurfaceKind::Overlay, id)) throw std::invalid_argument("duplicate HTML overlay ID");
        if (std::find(nativeOverlayNames_.begin(), nativeOverlayNames_.end(), id) != nativeOverlayNames_.end())
            throw std::invalid_argument("HTML overlay ID collides with a native overlay name");
        auto panel = std::make_unique<HtmlOverlayPanel>(id, bundle, options);
        auto& result = *panel; htmlOverlays_.push_back(std::move(panel)); return result;
    }
    /// True only after a connected renderer has been observed for this client.
    /// False includes unobserved and unavailable, including old hosts. Register
    /// HTML surfaces unconditionally and choose visibility from plugin state,
    /// never from this flag: a first visible activation establishes availability.
    /// HTML support is optional and never prevents native callbacks from loading.
    bool htmlUiAvailable(TitanHtmlUi::SurfaceKind kind = TitanHtmlUi::SurfaceKind::SidePanel) const {
        auto* api = host(); const uint32_t flag = kind == TitanHtmlUi::SurfaceKind::SidePanel
            ? TitanPluginSdk::kHtmlUiSidePanels : TitanPluginSdk::kHtmlUiOverlays;
        return api && api->htmlUiCapabilities &&
            (api->htmlUiCapabilities() & (flag | TitanPluginSdk::kHtmlUiRuntimeAvailable)) ==
                (flag | TitanPluginSdk::kHtmlUiRuntimeAvailable);
    }

    // --- Host / backend access ---
    /// Pointer to this instance's local view of negotiated host operations.
    /// Prefer the fluent API or `backend()` for new SDK code so the
    /// dispatch layer is consistent with the `titan::` fluent facades.
    const TitanPluginSdk::HostApi* host() const {
        return nativeBackend_ ? nativeBackend_->hostApi() : detail::host();
    }
    detail::ExternalBackend* _sdkBackend() const noexcept { return nativeBackend_; }
    void _bindSdkBackend(detail::ExternalBackend* backend) noexcept { nativeBackend_ = backend; }
    /// Active `IBackend` for this plugin instance. Normally
    /// plugin code should not touch this directly -- the `titan::` fluent
    /// facades go through it.
    detail::IBackend* backend() const { return nativeBackend_ ? nativeBackend_ : detail::backend(); }

    // --- Overlay registration (constructor-friendly) ---

    /// Register a render callback for the given layer.
    ///
    /// Prefer calling this from the plugin's constructor body:
    ///
    /// @code
    ///   MyPlugin() {
    ///       onRender(titan::Layer::AboveScene,   [this] { drawWorld(); });
    ///       onRender(titan::Layer::AboveWidgets, [this] { drawHud(); });
    ///   }
    /// @endcode
    ///
    /// Unlike the declarative `titan::Overlay member{this, layer, lambda};`
    /// style, lambdas registered here see a complete class body: helper
    /// methods can be declared anywhere in the class, and IDE indexers
    /// resolve them correctly without needing to hoist the overlay to the
    /// bottom of the class.
    void onRender(Layer layer, std::function<void()> fn) {
        auto overlay = std::make_unique<detail::CallbackOverlay>(layer, std::move(fn));
        overlays_.push_back(overlay.get());
        ownedOverlays_.push_back(std::move(overlay));
    }

    // --- Overlay panel registration (constructor-friendly) ---

    /// Construct a lambda-driven OverlayPanel and register it.
    ///
    /// Returns a reference to the panel so styling setters (setStyle,
    /// setBackgroundColor, setCornerRadius, ...) can be chained onto
    /// the registration call. The panel's lifetime is tied to the
    /// plugin instance.
    ///
    /// @code
    ///   MyPlugin() {
    ///       overlayPanel("main", titan::Anchor::Dynamic,
    ///                    [this](titan::OverlayPanel& p) {
    ///           p.title("My HUD");
    ///           p.line("Status:", status_);
    ///       }).setCornerRadius(8.0f);
    ///   }
    /// @endcode
    ///
    /// Definitions live in <titan/overlay_panel.h>; include that header
    /// to use the factory.
    OverlayPanel& overlayPanel(const char* name, Anchor anchor,
                               int32_t priority,
                               std::function<void(OverlayPanel&)> fn);

    /// Convenience overload defaulting priority=50.
    OverlayPanel& overlayPanel(const char* name, Anchor anchor,
                               std::function<void(OverlayPanel&)> fn);

    // --- Internal: called by self-registering members (public so helpers can reach them) ---
    void _registerSection(detail::SectionBase* s) { sections_.push_back(s); }
    void _registerSetting(detail::SettingBase* s) { settings_.push_back(s); }
    void _registerOverlay(detail::OverlayBase* o) { overlays_.push_back(o); }

    const std::vector<detail::SectionBase*>& _sections() const { return sections_; }
    const std::vector<detail::SettingBase*>& _settings() const { return settings_; }
    const std::vector<detail::OverlayBase*>& _overlays() const { return overlays_; }
    const std::vector<std::unique_ptr<SidePanel>>& _panels() const { return panels_; }
    const std::vector<std::unique_ptr<HtmlSidePanel>>& _htmlPanels() const { return htmlPanels_; }
    const std::vector<std::unique_ptr<HtmlOverlayPanel>>& _htmlOverlays() const { return htmlOverlays_; }
    HtmlSidePanel* _findHtml(TitanHtmlUi::SurfaceKind kind, const char* id) const {
        if (!id) return nullptr;
        if (kind == TitanHtmlUi::SurfaceKind::SidePanel) { for (const auto& p : htmlPanels_) if (p->id() == id) return p.get(); }
        else if (kind == TitanHtmlUi::SurfaceKind::Overlay) { for (const auto& p : htmlOverlays_) if (p->id() == id) return p.get(); }
        return nullptr;
    }
    void _freezeHtml() noexcept {
        htmlRegistrationFrozen_ = true;
        for (auto& p : htmlPanels_) p->_freeze();
        for (auto& p : htmlOverlays_) p->_freeze();
    }
    void _registerOverlayPanelName(const char* name) {
        if (_findHtml(TitanHtmlUi::SurfaceKind::Overlay, name)) throw std::invalid_argument("native overlay name collides with an HTML overlay ID");
        nativeOverlayNames_.emplace_back(name ? name : "");
    }
    /// Look up a registered side panel by its id. Returns null when no panel
    /// with that id exists.
    SidePanel* _findPanel(const char* id) const {
        if (!id) return nullptr;
        for (const auto& p : panels_) {
            if (p->id() == id) return p.get();
        }
        return nullptr;
    }

    /// Called by the thunk to atomically set the enabled flag before calling
    /// onEnabledChanged. Not meant for user code; prefer
    /// titan::plugins().self().enable() / .disable().
    ///
    /// The first call after plugin construction ALWAYS fires
    /// `onEnabledChanged` -- this is the initial-state handshake from the
    /// controller. Plugins that wire up side effects in `onEnabledChanged`
    /// (e.g. toggling an internal UI window) rely on this guarantee; without
    /// it, a plugin whose persisted controller state matches the runtime's
    /// initial disabled state would never see the initial callback.
    ///
    /// Subsequent calls fire only on actual state transitions.
    void _setEnabledFromHost(bool enabled) {
        const bool prev = enabled_;
        const bool first = !enabledInitialised_;
        enabled_ = enabled;
        enabledInitialised_ = true;
        if (first || prev != enabled) onEnabledChanged(enabled);
    }

private:
    void _requireHtmlRegistrationOpen() const {
        if (htmlRegistrationFrozen_)
            throw std::logic_error("HTML surfaces must be registered in the plugin constructor before interface publication");
    }
    void _validateSidePanelId(const char* id) const {
        if (!id || !TitanHtmlUi::validId(id)) throw std::invalid_argument("side panel ID must contain 1..31 ASCII letters, digits, '.', '_' or '-'");
        if (panels_.size() + htmlPanels_.size() >= TitanHtmlUi::kMaxSidePanels)
            throw std::invalid_argument("native and HTML side panels share the eight-panel limit");
        if (_findPanel(id) || _findHtml(TitanHtmlUi::SurfaceKind::SidePanel, id))
            throw std::invalid_argument("duplicate native/HTML side panel ID");
    }
    detail::ExternalBackend* nativeBackend_ = nullptr;
    bool enabled_ = false;
    /// Set by the first `_setEnabledFromHost` call so we can distinguish
    /// "controller pushed matching initial state" from "no callback yet".
    bool enabledInitialised_ = false;
    std::vector<detail::SectionBase*> sections_;
    std::vector<detail::SettingBase*> settings_;
    std::vector<detail::OverlayBase*> overlays_;
    // Heap-owned overlays registered via onRender(). Empty when the plugin
    // only uses the declarative titan::Overlay member style.
    std::vector<std::unique_ptr<detail::OverlayBase>> ownedOverlays_;
    // Heap-owned overlay panels registered via overlayPanel(). Stored
    // via the OverlayBase pointer (fully visible in this TU through
    // detail/registrable.h) so Plugin's defaulted destructor compiles
    // even in TUs that don't include <titan/overlay_panel.h>.
    std::vector<std::unique_ptr<detail::OverlayBase>> ownedOverlayPanels_;
    // Side panels registered via panel(). Each maps to a nav button in the
    // controller's right-hand rail.
    std::vector<std::unique_ptr<SidePanel>> panels_;
    std::vector<std::unique_ptr<HtmlSidePanel>> htmlPanels_;
    std::vector<std::unique_ptr<HtmlOverlayPanel>> htmlOverlays_;
    std::vector<std::string> nativeOverlayNames_;
    bool htmlRegistrationFrozen_ = false;
};

namespace detail {

/// Helper — look up a setting by key within a plugin's registry.
inline SettingBase* findSetting(Plugin* p, const char* key) {
    if (!p || !key) return nullptr;
    for (auto* s : p->_settings()) {
        if (std::strcmp(s->key(), key) == 0) return s;
    }
    return nullptr;
}

// ---------------------------------------------------------------------------
// PluginApi thunks -- each one bounces a C callback into the Plugin virtual.
// ---------------------------------------------------------------------------

/// Resolve the dispatching plugin instance from the C-ABI userData pointer.
///
/// Every PluginApi thunk routes through here, so this is also where the
/// per-DLL "current plugin" ambient (read by `titan::plugins().self()` and the
/// fluent facades) is refreshed. In a multi-plugin DLL several Plugin instances
/// share one `pluginRef()` static; updating it on every dispatch boundary makes
/// `self()` resolve to whichever plugin the host is currently calling into,
/// rather than whichever was constructed last.
///
/// Thread-safety: dispatch is serialized on the game thread, so the single
/// static is safe for the current threading model. If plugins ever dispatch
/// concurrently this must become thread-local.
inline Plugin* self(void* ud) {
    auto* p = static_cast<Plugin*>(ud);
    return p;
}

inline const char* thunkGetId(void* ud) { return self(ud)->id(); }
inline const char* thunkGetName(void* ud) { return self(ud)->name(); }
inline const char* thunkGetDescription(void* ud) { return self(ud)->description(); }
inline const char* thunkGetAuthor(void* ud) { return self(ud)->author(); }
inline const char* thunkGetVersion(void* ud) { return self(ud)->version(); }
inline uint8_t thunkGetDefaultEnabled(void* ud) { return self(ud)->isDefaultEnabled() ? 1 : 0; }

inline uint8_t thunkGetEnabled(void* ud) { return self(ud)->isEnabled() ? 1 : 0; }
inline void thunkSetEnabled(void* ud, uint8_t v) { self(ud)->_setEnabledFromHost(v != 0); }
inline void thunkPrepareUnload(void* ud) { self(ud)->onUnload(); }

inline uint32_t thunkGetSections(void* ud, TitanNativeRecords::Section* out, uint32_t max) {
    Plugin* p = self(ud);
    const auto& list = p->_sections();
    const uint32_t count = static_cast<uint32_t>(
        (std::min)(list.size(), static_cast<size_t>(max)));
    for (uint32_t i = 0; i < count; ++i) list[i]->serialize(out[i]);
    // SDK 143: a parent crosses the ABI as its 1-based position in this same
    // array. One the host never receives -- another plugin's section, or one
    // past `max` -- leaves the section at the top level.
    for (uint32_t i = 0; i < count; ++i) {
        const SectionBase* parent = list[i]->parentSection();
        if (!parent) continue;
        for (uint32_t j = 0; j < count; ++j) {
            if (j != i && list[j] == parent) {
                out[i].parentIndex = static_cast<uint8_t>(j + 1);
                break;
            }
        }
    }
    TitanNativeRecords::sanitizeSectionParents(out, count);
    return count;
}

inline uint32_t thunkGetSettings(void* ud, TitanNativeRecords::Setting* out, uint32_t max) {
    Plugin* p = self(ud);
    const auto& list = p->_settings();
    const uint32_t count = static_cast<uint32_t>(
        (std::min)(list.size(), static_cast<size_t>(max)));
    for (uint32_t i = 0; i < count; ++i) list[i]->serialize(out[i]);
    return count;
}

inline void copyFixed(char* dst, size_t dstSize, const std::string& src) {
    if (!dst || dstSize == 0) return;
    dst[0] = '\0';
    const size_t n = (std::min)(src.size(), dstSize - 1);
    std::memcpy(dst, src.data(), n);
    dst[n] = '\0';
}

inline void copyFixed(char* dst, size_t dstSize, const char* src) {
    if (!dst || dstSize == 0) return;
    dst[0] = '\0';
    if (!src) return;
    const size_t len = std::strlen(src);
    const size_t n = (std::min)(len, dstSize - 1);
    std::memcpy(dst, src, n);
    dst[n] = '\0';
}

inline uint8_t thunkSetSetting(void* ud, const char* key,
                               const TitanNativeRecords::Value* value,
                               char* errOut, uint32_t errOutLen) {
    Plugin* p = self(ud);
    if (!key || !value) {
        copyFixed(errOut, errOutLen, "Missing key or value");
        return 0;
    }
    SettingBase* s = findSetting(p, key);
    if (!s) {
        copyFixed(errOut, errOutLen, "Unknown setting");
        return 0;
    }
    const std::string err = s->apply(*value);
    if (!err.empty()) {
        copyFixed(errOut, errOutLen, err);
        return 0;
    }
    p->onSettingChanged(key);
    return 1;
}

inline void thunkOnClientTick(void* ud) { self(ud)->onClientTick(); }
inline void thunkOnMainLoop(void* ud) { self(ud)->onMainLoop(); }
inline void thunkOnGameTick(void* ud, int32_t tick) { self(ud)->onGameTick(tick); }

inline void thunkRenderOverlay(void* ud, uint8_t layer) {
    Plugin* p = self(ud);
    for (auto* o : p->_overlays()) {
        if (o->overlayLayer() == layer) o->invoke();
    }
}

inline void thunkOnProjectileSpawned(void* ud, const TitanPluginSdk::ProjectileState* p) {
    if (p) self(ud)->onProjectileSpawned(Projectile{*p});
}
inline void thunkOnProjectileDespawned(void* ud, const TitanPluginSdk::ProjectileState* p) {
    if (p) self(ud)->onProjectileDespawned(Projectile{*p});
}
inline void thunkOnProjectileMoved(void* ud, const TitanPluginSdk::ProjectileState* p) {
    if (p) self(ud)->onProjectileMoved(Projectile{*p});
}
inline void thunkOnGraphicsObjectSpawned(void* ud, const TitanPluginSdk::GraphicsObjectState* g) {
    if (g) self(ud)->onGraphicsObjectSpawned(GraphicsObject{*g});
}
inline void thunkOnGraphicsObjectDespawned(void* ud, const TitanPluginSdk::GraphicsObjectState* g) {
    if (g) self(ud)->onGraphicsObjectDespawned(GraphicsObject{*g});
}
inline void thunkOnGraphicsObjectMoved(void* ud, const TitanPluginSdk::GraphicsObjectState* g) {
    if (g) self(ud)->onGraphicsObjectMoved(GraphicsObject{*g});
}
inline void thunkOnNpcSpawned(void* ud, const TitanPluginSdk::NpcState* n) {
    if (n) self(ud)->onNpcSpawned(Npc{*n});
}
inline void thunkOnNpcDespawned(void* ud, const TitanPluginSdk::NpcState* n) {
    if (n) self(ud)->onNpcDespawned(Npc{*n});
}
inline void thunkOnPlayerSpawned(void* ud, const TitanPluginSdk::PlayerState* p) {
    if (p) self(ud)->onPlayerSpawned(Player{*p});
}
inline void thunkOnPlayerDespawned(void* ud, const TitanPluginSdk::PlayerState* p) {
    if (p) self(ud)->onPlayerDespawned(Player{*p});
}
inline void thunkOnTileObjectSpawned(void* ud, const TitanPluginSdk::TileObjectState* o) {
    if (o) self(ud)->onTileObjectSpawned(TileObject{*o});
}
inline void thunkOnTileObjectDespawned(void* ud, const TitanPluginSdk::TileObjectState* o) {
    if (o) self(ud)->onTileObjectDespawned(TileObject{*o});
}
inline void thunkOnMenuOptionClicked(void* ud, TitanPluginSdk::MenuOptionClickedEvent* e) {
    if (!e) return;
    MenuClickEvent wrapper{e};
    self(ud)->onMenuOptionClicked(wrapper);
}
inline void thunkOnScriptFired(void* ud, const TitanPluginSdk::ScriptFiredEvent* e) {
    if (!e) return;
    ScriptEvent wrapper{e};
    self(ud)->onScriptFired(wrapper);
}
inline void thunkOnVarbitChanged(void* ud, const TitanPluginSdk::VarbitChangedEvent* e) {
    if (!e) return;
    VarbitChangedEvent wrapper{e};
    self(ud)->onVarbitChanged(wrapper);
}
inline void thunkOnChatMessage(void* ud, const TitanPluginSdk::ChatMessageEvent* e) {
    if (!e) return;
    ChatMessageEvent wrapper{e};
    self(ud)->onChatMessage(wrapper);
}
inline void thunkOnGameStateChanged(
        void* ud, const TitanPluginSdk::GameStateChangedEvent* e) {
    if (!e) return;
    GameStateChangedEvent wrapper{e};
    self(ud)->onGameStateChanged(wrapper);
}
inline void thunkOnSoundPlayed(void* ud, TitanPluginSdk::SoundPlayedEvent* e) {
    if (!e) return;
    SoundPlayedEvent wrapper{e};
    self(ud)->onSoundPlayed(wrapper);
}
inline void thunkOnHitsplatApplied(void* ud,
        const TitanPluginSdk::HitsplatAppliedEvent* e) {
    if (!e) return;
    HitsplatAppliedEvent wrapper{e};
    self(ud)->onHitsplatApplied(wrapper);
}
inline void thunkOnActorSpotAnim(void* ud,
        const TitanPluginSdk::ActorSpotAnimEvent* e) {
    if (!e) return;
    ActorSpotAnimEvent wrapper{e};
    self(ud)->onActorSpotAnim(wrapper);
}
inline void thunkOnOverheadTextChanged(void* ud, const TitanPluginSdk::OverheadTextChangedEvent* e) {
    if (!e) return;
    OverheadTextChangedEvent wrapper{e};
    self(ud)->onOverheadTextChanged(wrapper);
}
inline void thunkOnGrandExchangeOfferChanged(void* ud,
        const TitanPluginSdk::GrandExchangeOfferChangedEvent* e) {
    if (!e) return;
    GrandExchangeOfferChangedEvent wrapper{e};
    self(ud)->onGrandExchangeOfferChanged(wrapper);
}
inline void thunkOnAnimationChanged(void* ud,
        const TitanPluginSdk::AnimationChangedEvent* e) {
    if (!e) return;
    AnimationChangedEvent wrapper{e};
    self(ud)->onAnimationChanged(wrapper);
}
inline void thunkOnItemContainerChanged(void* ud,
        const TitanPluginSdk::ItemContainerChangedEvent* e) {
    if (!e) return;
    ItemContainerChangedEvent wrapper{e};
    self(ud)->onItemContainerChanged(wrapper);
}
inline void thunkOnMousePressed(void* ud, TitanPluginSdk::MouseButtonEvent* e) {
    if (!e) return;
    MouseButtonEvent wrapper{e};
    self(ud)->onMousePressed(wrapper);
}
inline void thunkOnMouseReleased(void* ud, TitanPluginSdk::MouseButtonEvent* e) {
    if (!e) return;
    MouseButtonEvent wrapper{e};
    self(ud)->onMouseReleased(wrapper);
}
inline void thunkOnCrossTabChanged(void* ud,
        const TitanPluginSdk::CrossTabChangeEvent* e) {
    if (!e || e->structSize < sizeof(*e)) return;
    CrossTabChange change;
    size_t keyLength = 0;
    while (keyLength < sizeof(e->key) && e->key[keyLength] != '\0') ++keyLength;
    change.key.assign(e->key, keyLength);
    change.kind = static_cast<CrossTabChange::Kind>(e->kind);
    change.origin = static_cast<CrossTabChange::Origin>(e->origin);
    change.cause = static_cast<CrossTabChange::Cause>(e->cause);
    change.secret = e->secret != 0;
    change.redacted = e->redacted != 0;
    change.version = e->version;
    change.writeId = e->writeId;
    self(ud)->onCrossTabChanged(change);
}

inline void thunkDestroy(void* ud) {
    Plugin* p = self(ud);
    // Clear the ambient before delete so a stale `plugins().self()` after
    // teardown resolves to "no current plugin" instead of a dangling pointer.
    if (pluginRef() == p) pluginRef() = nullptr;
    delete p;
}

inline uint32_t thunkGetPanels(void* ud, TitanNativeRecords::PanelDescriptor* out, uint32_t max) {
    Plugin* p = self(ud);
    const auto& panels = p->_panels();
    const uint32_t count = static_cast<uint32_t>(
        (std::min)(panels.size(), static_cast<size_t>(max)));
    for (uint32_t i = 0; i < count; ++i) {
        TitanNativeRecords::PanelDescriptor d = {};
        copyFixed(d.id, sizeof(d.id), panels[i]->id());
        copyFixed(d.title, sizeof(d.title), panels[i]->title());
        copyFixed(d.icon, sizeof(d.icon), panels[i]->iconGlyph());
        d.iconColor = panels[i]->iconColor();
        d.hasImageIcon = panels[i]->hasImage() ? 1 : 0;
        out[i] = d;
    }
    return count;
}

inline uint32_t thunkGetPanelElements(void* ud, const char* panelId,
                                      TitanNativeRecords::PanelElement* out, uint32_t max) {
    Plugin* p = self(ud);
    SidePanel* sp = p->_findPanel(panelId);
    if (!sp) return 0;
    Panel panel;
    sp->build(panel);
    const auto& elements = panel._elements();
    const uint32_t count = static_cast<uint32_t>(
        (std::min)(elements.size(), static_cast<size_t>(max)));
    for (uint32_t i = 0; i < count; ++i) out[i] = elements[i];
    return count;
}

inline void thunkOnPanelAction(void* ud, const char* panelId, int32_t actionId,
                               const TitanNativeRecords::Value* value) {
    Plugin* p = self(ud);
    SidePanel* sp = p->_findPanel(panelId);
    if (!sp) return;
    TitanNativeRecords::Value v = {};
    if (value) v = *value;
    sp->action(actionId, v);
}

inline uint32_t thunkGetPanelIcon(void* ud, const char* panelId,
                                  uint8_t* out, uint32_t maxBytes) {
    Plugin* p = self(ud);
    SidePanel* sp = p->_findPanel(panelId);
    if (!sp) return 0;
    return sp->iconBytes(out, maxBytes);
}

inline uint32_t thunkGetDependencies(void* ud,
                                     char outIds[][TitanNativeRecords::kMaxIdLen],
                                     uint32_t maxIds) {
    Plugin* p = self(ud);
    const std::vector<const char*> deps = p->dependencies();
    const uint32_t count = static_cast<uint32_t>(
        (std::min)(deps.size(), static_cast<size_t>(maxIds)));
    for (uint32_t i = 0; i < count; ++i) {
        copyFixed(outIds[i], TitanNativeRecords::kMaxIdLen, deps[i] ? deps[i] : "");
    }
    return count;
}

/// Lower a parameter pack of plugin types to their static kPluginId strings
/// (used by TITAN_PLUGIN_DEPS for RuneLite-style type-based dependencies).
template <typename... Plugins>
inline std::vector<const char*> pluginDependencyIds() {
    return { Plugins::kPluginId... };
}

// Every native callback installs its instance context and catches C++ exceptions
// in the allocating module. Multiple plugins and concurrent callback threads
// never share a mutable ambient owner.
template <auto Callback> struct GuardedPluginCallback;
template <typename R, typename... Args, R (*Callback)(void*, Args...)>
struct GuardedPluginCallback<Callback> {
    static R invoke(void* userData, Args... args) noexcept {
        auto* plugin = static_cast<Plugin*>(userData);
        auto* backend = plugin ? plugin->_sdkBackend() : nullptr;
        ScopedBackendContext context(backend ? backend : detail::backend(),
            plugin ? plugin->host() : detail::host(), plugin);
        try { return Callback(userData, args...); }
        catch (...) {
            try {
                if (auto* api = detail::host(); api && api->log)
                    api->log("[Native SDK] Plugin callback threw a C++ exception");
            } catch (...) {}
            if constexpr (!std::is_void_v<R>) return R{};
        }
    }
};

template <TitanHtmlUi::SurfaceKind Kind>
inline const auto& htmlSurfaces(Plugin* plugin) {
    if constexpr (Kind == TitanHtmlUi::SurfaceKind::SidePanel) return plugin->_htmlPanels();
    else return plugin->_htmlOverlays();
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint32_t thunkHtmlDescriptors(void* ud, TitanPluginSdk::HtmlUiRecords::DescriptorV1* out, uint32_t capacity) {
    const auto& panels = htmlSurfaces<Kind>(self(ud));
    if (out) for (size_t i = 0; i < panels.size() && i < capacity; ++i) {
        const auto& p = *panels[i]; const auto& d = p.descriptor(); const auto& bundle = p.bundle();
        auto& target = out[i]; target = {}; target.kind = static_cast<uint32_t>(Kind);
        copyFixed(target.id, sizeof(target.id), d.id.c_str()); copyFixed(target.title, sizeof(target.title), d.title.c_str());
        copyFixed(target.icon, sizeof(target.icon), d.icon.c_str()); copyFixed(target.entrypoint, sizeof(target.entrypoint), bundle.entrypoint.c_str());
        target.iconColor = d.iconColor; target.iconBytes = static_cast<uint32_t>(d.iconPng.size());
        target.resourceCount = static_cast<uint32_t>(bundle.resources.size());
        for (const auto& r : bundle.resources) target.bundleBytes += static_cast<uint32_t>(r.bytes.size());
        target.width = d.width; target.height = d.height; target.priority = d.priority;
        target.anchor = d.anchor; target.input = d.input; target.visible = d.visible ? 1 : 0;
    }
    return static_cast<uint32_t>(panels.size());
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint8_t thunkHtmlResource(void* ud, const char* id, uint32_t index, TitanPluginSdk::HtmlUiRecords::ResourceV1* out) {
    const auto* p = self(ud)->_findHtml(Kind, id); if (!p || !out || index >= p->bundle().resources.size()) return 0;
    const auto& r = p->bundle().resources[index]; *out = {};
    copyFixed(out->path, sizeof(out->path), r.path.c_str()); copyFixed(out->mime, sizeof(out->mime), r.mime.c_str());
    out->bytes = static_cast<uint32_t>(r.bytes.size()); return 1;
}
inline uint32_t copyHtmlBytes(const std::vector<uint8_t>& source, uint32_t offset, uint8_t* out, uint32_t capacity) {
    if (!out || offset >= source.size()) return 0;
    const auto count = static_cast<uint32_t>((std::min)(source.size() - offset, static_cast<size_t>(capacity)));
    if (count) std::memcpy(out, source.data() + offset, count); return count;
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint32_t thunkHtmlCopyResource(void* ud, const char* id, uint32_t index, uint32_t offset, uint8_t* out, uint32_t capacity) {
    const auto* p = self(ud)->_findHtml(Kind, id); if (!p || index >= p->bundle().resources.size()) return 0;
    return copyHtmlBytes(p->bundle().resources[index].bytes, offset, out, capacity);
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint8_t thunkHtmlSnapshot(void* ud, const char* id, uint64_t activation, TitanPluginSdk::HtmlUiRecords::SnapshotV1* out) {
    auto* p = self(ud)->_findHtml(Kind, id); if (!p || !out) return 0; TitanHtmlUi::UpdateBatch batch;
    if (!p->_updates().snapshot(activation, batch)) return 0;
    *out = {}; out->activation = batch.activation; out->lastSequence = batch.lastSequence; out->stateRevision = batch.stateRevision;
    out->stateBytes = static_cast<uint32_t>(batch.state.size()); out->messageCount = static_cast<uint32_t>(batch.messages.size());
    out->visible = p->_visible() ? 1 : 0;
    const auto size = p->_size(); out->width = size.first; out->height = size.second; return 1;
}
inline uint32_t copyHtmlString(const std::string& source, char* out, uint32_t capacity) {
    const auto size = static_cast<uint32_t>(source.size()); if (out && capacity >= size && size) std::memcpy(out, source.data(), size); return size;
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint32_t thunkHtmlState(void* ud, const char* id, uint64_t revision, char* out, uint32_t capacity) {
    auto* p = self(ud)->_findHtml(Kind, id); std::string state;
    return p && p->_updates().copyState(revision, state) ? copyHtmlString(state, out, capacity) : 0;
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint32_t thunkHtmlMessage(void* ud, const char* id, uint64_t activation, uint32_t index, char* out, uint32_t capacity, uint64_t* sequence) {
    auto* p = self(ud)->_findHtml(Kind, id); TitanHtmlUi::Update update; if (sequence) *sequence = 0;
    if (!p || !sequence || !p->_updates().copyMessage(activation, index, update)) return 0;
    *sequence = update.sequence; return copyHtmlString(TitanHtmlUi::serializeMessage({update.type, update.payload, update.correlationId}), out, capacity);
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint8_t thunkHtmlAcknowledge(void* ud, const char* id, uint64_t activation, uint64_t through) {
    auto* p = self(ud)->_findHtml(Kind, id); return p && p->_updates().acknowledge(activation, through) ? 1 : 0;
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint8_t thunkHtmlReset(void* ud, const char* id, uint64_t activation) {
    auto* p = self(ud)->_findHtml(Kind, id); return p && p->_updates().reset(activation) ? 1 : 0;
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint8_t thunkHtmlDispatch(void* ud, const char* id, uint64_t activation, const char* message, uint32_t bytes) {
    auto* p = self(ud)->_findHtml(Kind, id);
    return p && message && bytes <= TitanHtmlUi::kMaxJsonBytes && p->_dispatch(activation, std::string_view(message, bytes)) ? 1 : 0;
}
template <TitanHtmlUi::SurfaceKind Kind>
inline uint32_t thunkHtmlIcon(void* ud, const char* id, uint32_t offset, uint8_t* out, uint32_t capacity) {
    const auto* p = self(ud)->_findHtml(Kind, id); return p ? copyHtmlBytes(p->descriptor().iconPng, offset, out, capacity) : 0;
}
template <TitanHtmlUi::SurfaceKind Kind>
inline TitanPluginSdk::HtmlUiCallbacks htmlCallbacks() {
    return {&GuardedPluginCallback<&thunkHtmlDescriptors<Kind>>::invoke,
        &GuardedPluginCallback<&thunkHtmlResource<Kind>>::invoke, &GuardedPluginCallback<&thunkHtmlCopyResource<Kind>>::invoke,
        &GuardedPluginCallback<&thunkHtmlSnapshot<Kind>>::invoke, &GuardedPluginCallback<&thunkHtmlState<Kind>>::invoke,
        &GuardedPluginCallback<&thunkHtmlMessage<Kind>>::invoke, &GuardedPluginCallback<&thunkHtmlAcknowledge<Kind>>::invoke,
        &GuardedPluginCallback<&thunkHtmlReset<Kind>>::invoke, &GuardedPluginCallback<&thunkHtmlDispatch<Kind>>::invoke,
        &GuardedPluginCallback<&thunkHtmlIcon<Kind>>::invoke};
}

inline void populatePluginApi(Plugin* instance, TitanPluginSdk::PluginApi* outApi) {
    *outApi = {};
    outApi->sdkVersion = TitanPluginSdk::kSdkVersion;
    outApi->userData = instance;
    if (instance) {
        instance->_freezeHtml();
        if (!instance->_htmlPanels().empty()) outApi->htmlPanels = htmlCallbacks<TitanHtmlUi::SurfaceKind::SidePanel>();
        if (!instance->_htmlOverlays().empty()) outApi->htmlOverlays = htmlCallbacks<TitanHtmlUi::SurfaceKind::Overlay>();
    }

    outApi->getId = &GuardedPluginCallback<&thunkGetId>::invoke;
    outApi->getName = &GuardedPluginCallback<&thunkGetName>::invoke;
    outApi->getPanels = &GuardedPluginCallback<&thunkGetPanels>::invoke;
    outApi->getDescription = &GuardedPluginCallback<&thunkGetDescription>::invoke;
    outApi->getAuthor = &GuardedPluginCallback<&thunkGetAuthor>::invoke;
    outApi->getVersion = &GuardedPluginCallback<&thunkGetVersion>::invoke;
    outApi->getDefaultEnabled = &GuardedPluginCallback<&thunkGetDefaultEnabled>::invoke;

    outApi->getEnabled = &GuardedPluginCallback<&thunkGetEnabled>::invoke;
    outApi->setEnabled = &GuardedPluginCallback<&thunkSetEnabled>::invoke;
    outApi->getSettings = &GuardedPluginCallback<&thunkGetSettings>::invoke;
    outApi->getSections = &GuardedPluginCallback<&thunkGetSections>::invoke;
    outApi->setSetting = &GuardedPluginCallback<&thunkSetSetting>::invoke;

    outApi->onClientTick = &GuardedPluginCallback<&thunkOnClientTick>::invoke;
    outApi->onGameTick = &GuardedPluginCallback<&thunkOnGameTick>::invoke;
    outApi->renderOverlay = &GuardedPluginCallback<&thunkRenderOverlay>::invoke;

    outApi->getPanelElements = &GuardedPluginCallback<&thunkGetPanelElements>::invoke;
    outApi->onPanelAction = &GuardedPluginCallback<&thunkOnPanelAction>::invoke;
    outApi->getPanelIcon = &GuardedPluginCallback<&thunkGetPanelIcon>::invoke;

    outApi->destroy = &GuardedPluginCallback<&thunkDestroy>::invoke;
    outApi->prepareUnload = &GuardedPluginCallback<&thunkPrepareUnload>::invoke;

    outApi->onProjectileSpawned = &GuardedPluginCallback<&thunkOnProjectileSpawned>::invoke;
    outApi->onProjectileDespawned = &GuardedPluginCallback<&thunkOnProjectileDespawned>::invoke;
    outApi->onProjectileMoved = &GuardedPluginCallback<&thunkOnProjectileMoved>::invoke;
    outApi->onGraphicsObjectSpawned = &GuardedPluginCallback<&thunkOnGraphicsObjectSpawned>::invoke;
    outApi->onGraphicsObjectDespawned = &GuardedPluginCallback<&thunkOnGraphicsObjectDespawned>::invoke;
    outApi->onGraphicsObjectMoved = &GuardedPluginCallback<&thunkOnGraphicsObjectMoved>::invoke;
    outApi->onNpcSpawned = &GuardedPluginCallback<&thunkOnNpcSpawned>::invoke;
    outApi->onNpcDespawned = &GuardedPluginCallback<&thunkOnNpcDespawned>::invoke;
    outApi->onPlayerSpawned = &GuardedPluginCallback<&thunkOnPlayerSpawned>::invoke;
    outApi->onPlayerDespawned = &GuardedPluginCallback<&thunkOnPlayerDespawned>::invoke;
    outApi->onTileObjectSpawned = &GuardedPluginCallback<&thunkOnTileObjectSpawned>::invoke;
    outApi->onTileObjectDespawned = &GuardedPluginCallback<&thunkOnTileObjectDespawned>::invoke;
    outApi->onMenuOptionClicked = &GuardedPluginCallback<&thunkOnMenuOptionClicked>::invoke;
    outApi->onScriptFired = &GuardedPluginCallback<&thunkOnScriptFired>::invoke;
    outApi->onVarbitChanged = &GuardedPluginCallback<&thunkOnVarbitChanged>::invoke;
    outApi->onChatMessage = &GuardedPluginCallback<&thunkOnChatMessage>::invoke;
    outApi->onSoundPlayed = &GuardedPluginCallback<&thunkOnSoundPlayed>::invoke;
    outApi->onHitsplatApplied = &GuardedPluginCallback<&thunkOnHitsplatApplied>::invoke;
    outApi->onActorSpotAnim = &GuardedPluginCallback<&thunkOnActorSpotAnim>::invoke;
    outApi->onOverheadTextChanged = &GuardedPluginCallback<&thunkOnOverheadTextChanged>::invoke;
    outApi->onGrandExchangeOfferChanged = &GuardedPluginCallback<&thunkOnGrandExchangeOfferChanged>::invoke;
    outApi->onAnimationChanged = &GuardedPluginCallback<&thunkOnAnimationChanged>::invoke;
    outApi->onGameStateChanged = &GuardedPluginCallback<&thunkOnGameStateChanged>::invoke;
    outApi->onItemContainerChanged = &GuardedPluginCallback<&thunkOnItemContainerChanged>::invoke;
    outApi->onMousePressed = &GuardedPluginCallback<&thunkOnMousePressed>::invoke;
    outApi->onMouseReleased = &GuardedPluginCallback<&thunkOnMouseReleased>::invoke;
    outApi->getDependencies = &GuardedPluginCallback<&thunkGetDependencies>::invoke;
    outApi->onMainLoop = &GuardedPluginCallback<&thunkOnMainLoop>::invoke;
    outApi->onCrossTabChanged = &GuardedPluginCallback<&thunkOnCrossTabChanged>::invoke;
}

// Local test/embedding helper. This table never crosses Native ABI v1.
template <typename PluginT>
uint8_t createPlugin(const TitanPluginSdk::HostApi* hostApi,
                     TitanPluginSdk::PluginApi* outApi,
                     char* errOut, uint32_t errOutLen) {
    if (!hostApi || !outApi || hostApi->sdkVersion < TitanPluginSdk::kSdkVersion) {
        copyFixed(errOut, errOutLen, "Plugin was built against a newer SDK than this client");
        return 0;
    }
    static ExternalBackend backend{hostApi};
    backend.setHostApi(hostApi);
    hostRef() = hostApi;
    backendRef() = &backend;
    try {
        auto instance = std::make_unique<PluginT>();
        instance->_bindSdkBackend(&backend);
        pluginRef() = instance.get();
        populatePluginApi(instance.get(), outApi);
        instance.release();
        return 1;
    } catch (const std::exception& ex) { copyFixed(errOut, errOutLen, ex.what()); return 0; }
    catch (...) { copyFixed(errOut, errOutLen, "Plugin construction failed"); return 0; }
}

struct NativePluginStorage final : TitanPluginSdk::NativeAbi::PluginDescriptorV1 {
    TitanPluginSdk::HostApi hostView{};
    ExternalBackend backend{&hostView};
    std::unique_ptr<Plugin> instance;
    std::unique_ptr<TitanPluginSdk::NativeAbi::PluginInterfaceSet> callbacks;

    ~NativePluginStorage() {
        ScopedBackendContext scope(&backend, &hostView, instance.get());
        instance.reset();
    }
    static void destroyStorage(const TitanPluginSdk::NativeAbi::PluginDescriptorV1* descriptor) noexcept {
        delete static_cast<const NativePluginStorage*>(descriptor);
    }
};

inline void installNativeModuleFallback(const TitanPluginSdk::HostApi& host) {
    // One immutable fallback for all PluginT factories in this DLL. Native
    // threads carry explicit instance context; stateless compatibility reads
    // never race another factory rewriting the fallback slots.
    struct Fallback {
        const TitanPluginSdk::HostApi host;
        ExternalBackend backend;
        explicit Fallback(const TitanPluginSdk::HostApi& source)
            : host(source), backend(&host) {
            hostRef() = &host;
            backendRef() = &backend;
        }
    };
    static Fallback fallback{host};
    (void)fallback;
}

template <typename PluginT>
uint8_t createNativePlugin(const TitanPluginSdk::NativeAbi::InterfaceProviderV1* host,
                          const TitanPluginSdk::NativeAbi::PluginDescriptorV1** out,
                          char* error, uint32_t capacity) noexcept {
    using namespace TitanPluginSdk::NativeAbi;
    if (!out) return 0;
    *out = nullptr;
    try {
        auto storage = std::make_unique<NativePluginStorage>();
        if (!importHostApi(host, storage->hostView) || !lifetime(host)) {
            copyFixed(error, capacity, "Native ABI v1 instance lifetime interface is unavailable");
            return 0;
        }
        // Payload layouts change only together with kMinSupportedSdkVersion,
        // so an older host would fill this plugin's records with its layout.
        if (storage->hostView.sdkVersion < TitanPluginSdk::kMinSupportedSdkVersion) {
            copyFixed(error, capacity, "Plugin built against SDK "
                + std::to_string(TitanPluginSdk::kSdkVersion) + " needs a client with SDK "
                + std::to_string(TitanPluginSdk::kMinSupportedSdkVersion)
                + " or newer; update TitanClient");
            return 0;
        }
        // Stateless facade fallback for plugin-created threads. Ownership-sensitive
        // calls require an explicit owner/PluginThreadContext on such threads.
        installNativeModuleFallback(storage->hostView);
        storage->backend.setLifetime(lifetime(host));
        ScopedBackendContext context(&storage->backend, &storage->hostView, nullptr);
        TitanPluginSdk::PluginApi api{};
        storage->instance = std::make_unique<PluginT>();
        storage->instance->_bindSdkBackend(&storage->backend);
        populatePluginApi(storage->instance.get(), &api);
        storage->callbacks = std::make_unique<PluginInterfaceSet>(api);
        storage->interfaces = storage->callbacks->provider();
        storage->userData = storage->instance.get();
        storage->destroy = &NativePluginStorage::destroyStorage;
        *out = storage.release();
        return 1;
    } catch (const std::exception& ex) { copyFixed(error, capacity, ex.what()); return 0; }
    catch (...) { copyFixed(error, capacity, "Native plugin construction failed"); return 0; }
}

template <typename PluginT>
const TitanPluginSdk::NativeAbi::InterfaceRequirement* nativeRequirements(uint32_t* count) noexcept {
    using namespace TitanPluginSdk::NativeAbi;
    if (!count) return nullptr;
    if constexpr (requires { PluginT::kRequiredNativeInterfaces; }) {
        *count = static_cast<uint32_t>(std::size(PluginT::kRequiredNativeInterfaces));
        return PluginT::kRequiredNativeInterfaces;
    } else {
        static constexpr InterfaceRequirement required[] = {
            {kLifetimeInterfaceId, 1, sizeof(LifetimeV1)}
        };
        *count = 1;
        return required;
    }
}

template <typename... Plugins>
const TitanPluginSdk::NativeAbi::ModuleDescriptorV1* nativeModule(uint32_t version) noexcept {
    using namespace TitanPluginSdk::NativeAbi;
    if (version != kAbiVersion) return nullptr;
    static const ModuleDescriptorV1 module = [] {
        ModuleDescriptorV1 value{};
        value.pluginCount = sizeof...(Plugins);
        value.getRequirements = +[](uint32_t index, uint32_t* count) -> const InterfaceRequirement* {
            using Fn = const InterfaceRequirement* (*)(uint32_t*);
            static constexpr Fn getters[] = {&nativeRequirements<Plugins>...};
            if (!count) return nullptr;
            *count = 0;
            return index < sizeof...(Plugins) ? getters[index](count) : nullptr;
        };
        value.createPlugin = +[](uint32_t index, const InterfaceProviderV1* host,
                                 const PluginDescriptorV1** out, char* error, uint32_t capacity) -> uint8_t {
            using Fn = uint8_t (*)(const InterfaceProviderV1*, const PluginDescriptorV1**, char*, uint32_t);
            static constexpr Fn factories[] = {&createNativePlugin<Plugins>...};
            if (out) *out = nullptr;
            if (index >= sizeof...(Plugins)) { copyFixed(error, capacity, "Plugin index out of range"); return 0; }
            return factories[index](host, out, error, capacity);
        };
        return value;
    }();
    return &module;
}

// ---------------------------------------------------------------------------
// Multi-plugin DLL support (SDK 66)
// ---------------------------------------------------------------------------

/// Number of plugins in a registration pack; local test/embedding helper.
template <typename... Plugins>
constexpr uint32_t pluginCount() {
    return static_cast<uint32_t>(sizeof...(Plugins));
}

/// Instantiate from a registration pack for local tests/embedding. These
/// local HostApi/PluginApi views never cross the Native ABI v1 boundary.
template <typename... Plugins>
uint8_t createPluginAt(uint32_t index, const TitanPluginSdk::HostApi* hostApi,
                       TitanPluginSdk::PluginApi* outApi, char* errOut,
                       uint32_t errOutLen) {
    using Factory = uint8_t (*)(const TitanPluginSdk::HostApi*,
                                TitanPluginSdk::PluginApi*, char*, uint32_t);
    static constexpr Factory factories[] = { &createPlugin<Plugins>... };
    if (index >= sizeof...(Plugins)) {
        copyFixed(errOut, errOutLen, "Plugin index out of range");
        return 0;
    }
    return factories[index](hostApi, outApi, errOut, errOutLen);
}

/// Fixed-capacity, compile-time-constructible buffer holding every plugin id
/// in a DLL as a single newline-joined, NUL-terminated string. Exported as
/// `TitanPluginIdList` so the server-side PE parser can read the full id set
/// statically (the struct's only member is the char array, so the export's
/// bytes *are* the string). Capacity is generous; ids beyond it are dropped
/// rather than overflowing.
template <size_t Cap>
struct IdListBuffer {
    char data[Cap] = {};
};

/// Build an IdListBuffer from a pack of plugin types (joined with '\n').
template <typename... Plugins>
constexpr IdListBuffer<256> makeIdList() {
    constexpr size_t kCap = 256;
    IdListBuffer<kCap> buf{};
    const char* ids[] = { Plugins::kPluginId... };
    size_t pos = 0;
    bool first = true;
    for (const char* id : ids) {
        if (!id) continue;
        if (!first && pos + 1 < kCap) buf.data[pos++] = '\n';
        first = false;
        for (const char* c = id; *c && pos + 1 < kCap; ++c) buf.data[pos++] = *c;
    }
    buf.data[pos] = '\0';
    return buf;
}

}  // namespace detail
}  // namespace titan

// ---------------------------------------------------------------------------
// Macros
// ---------------------------------------------------------------------------

/// Declare the plugin identity (and optional panel info). Place at the top of
/// the class body, after the opening brace.
///
/// @code
///   class MyPlugin : public titan::Plugin {
///       TITAN_PLUGIN("my_plugin", "My Plugin")
///       ...
///   };
/// @endcode
#define TITAN_PLUGIN(PLUGIN_ID, PLUGIN_NAME)                                   \
public:                                                                        \
    static constexpr const char* kPluginId = PLUGIN_ID;                        \
    const char* id() const override { return PLUGIN_ID; }                      \
    const char* name() const override { return PLUGIN_NAME; }                  \
private:

/// Declare full plugin metadata (id, name, description, author, version,
/// defaultEnabled). Prefer this over the bare TITAN_PLUGIN macro so the
/// controller UI can show rich info in the plugin list and so the
/// controller knows whether to ship the plugin enabled or disabled on
/// first install.
///
/// `PLUGIN_DEFAULT_ENABLED` is a bool: `true` to default the plugin on,
/// `false` to ship it off (e.g. debug / developer tools). The controller
/// reads this on a fresh install (no saved state for the plugin id) and
/// uses it as the initial enabled state; subsequent launches use whatever
/// the user last toggled.
///
/// @code
///   class MyPlugin : public titan::Plugin {
///       TITAN_PLUGIN_META(
///           "my_plugin",
///           "My Plugin",
///           "Does something nice.",
///           "Alice",
///           "1.0.0",
///           true)  // default enabled
///       ...
///   };
/// @endcode
#define TITAN_PLUGIN_META(PLUGIN_ID, PLUGIN_NAME, PLUGIN_DESC, PLUGIN_AUTHOR, PLUGIN_VERSION, PLUGIN_DEFAULT_ENABLED) \
public:                                                                        \
    static constexpr const char* kPluginId = PLUGIN_ID;                        \
    const char* id() const override          { return PLUGIN_ID; }             \
    const char* name() const override        { return PLUGIN_NAME; }           \
    const char* description() const override { return PLUGIN_DESC; }           \
    const char* author() const override      { return PLUGIN_AUTHOR; }         \
    const char* version() const override     { return PLUGIN_VERSION; }        \
    bool isDefaultEnabled() const override   { return (PLUGIN_DEFAULT_ENABLED); } \
private:

/// Emit TitanPlugin_QueryNative for the given Plugin subclass,
/// along with a data export `TitanPluginIdStr` carrying the plugin id as
/// a null-terminated string. Exactly one call per plugin DLL.
///
/// The id string is emitted as a top-level dllexport so the server-side
/// PE parser can read it statically (without executing the DLL) to verify
/// that an uploaded plugin binary matches the plugin slug the uploader
/// claims. See app/Services/PeFileReader.php on the server side.
///
/// PLUGIN_ID is a string literal that MUST match the first argument of
/// the class's TITAN_PLUGIN or TITAN_PLUGIN_META call. The build will
/// silently succeed if they differ; only upload-time PE parsing catches
/// the mismatch (and rejects the upload).
///
/// @code
///   class MyPlugin : public titan::Plugin {
///       TITAN_PLUGIN("my_plugin", "My Plugin")
///       ...
///   };
///   TITAN_REGISTER_PLUGIN(MyPlugin, "my_plugin")
/// @endcode
#define TITAN_REGISTER_PLUGIN(CLASS_NAME, PLUGIN_ID)                           \
    extern "C" __declspec(dllexport) const char TitanPluginIdStr[] = PLUGIN_ID; \
    TITAN_REGISTER_PLUGINS(CLASS_NAME)

/// Native ABI v1 entry point. The ID exports remain available to static PE
/// metadata readers. No SDK-sized output table crosses the DLL boundary.
#define TITAN_REGISTER_PLUGINS(...)                                            \
    extern "C" __declspec(dllexport)                                           \
    const ::titan::detail::IdListBuffer<256> TitanPluginIdList =                 \
        ::titan::detail::makeIdList<__VA_ARGS__>();                             \
    extern "C" __declspec(dllexport)                                           \
    const ::TitanPluginSdk::NativeAbi::ModuleDescriptorV1*                      \
    TitanPlugin_QueryNative(uint32_t abiVersion) {                             \
        return ::titan::detail::nativeModule<__VA_ARGS__>(abiVersion);          \
    }

/// Declare the host interfaces this plugin requires. Undeclared interfaces
/// remain optional; the C++ facade checks individual imported operations.
#define TITAN_REQUIRE_NATIVE_INTERFACES(...)                                  \
public:                                                                       \
    inline static constexpr ::TitanPluginSdk::NativeAbi::InterfaceRequirement \
        kRequiredNativeInterfaces[] = {__VA_ARGS__};                          \
private:

/// Declare plugin dependencies by TYPE (RuneLite-style). Each argument is a
/// titan::Plugin subclass; its static kPluginId is read at compile time and
/// added to the load-order graph. The host loads the referenced plugins
/// first, so they are safe to look up via titan::plugins() in onEnable().
/// Place inside the class body.
///
/// @code
///   class MyPlugin : public titan::Plugin {
///       TITAN_PLUGIN("my_plugin", "My Plugin")
///       TITAN_PLUGIN_DEPS(CoreServicePlugin, OverlayKitPlugin)
///       ...
///   };
/// @endcode
#define TITAN_PLUGIN_DEPS(...)                                                  \
public:                                                                        \
    std::vector<const char*> dependencies() const override {                   \
        return ::titan::detail::pluginDependencyIds<__VA_ARGS__>();             \
    }                                                                          \
private:

/// Declare plugin dependencies by ID STRING. Escape hatch for cross-language
/// dependencies where the depended-on plugin's C++ type is not available on
/// the include path. Prefer TITAN_PLUGIN_DEPS for same-language deps.
///
/// @code
///   TITAN_PLUGIN_DEP_IDS("some_java_plugin", "another_plugin")
/// @endcode
#define TITAN_PLUGIN_DEP_IDS(...)                                               \
public:                                                                        \
    std::vector<const char*> dependencies() const override {                   \
        return { __VA_ARGS__ };                                                \
    }                                                                          \
private:
