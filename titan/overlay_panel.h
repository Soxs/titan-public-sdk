/// @file titan/overlay_panel.h
/// @brief Anchored, draggable HUD overlay panels (RuneLite-style).
///
/// An OverlayPanel is a structured HUD element that the host lays out
/// against an anchor (Dynamic / TopCenter / AboveChatboxRight / Tooltip)
/// and that the user can reposition with Alt-drag. The plugin emits
/// content from render() via builder methods (title / line /
/// progressBar); the host computes width/height, paints the background,
/// and handles drag/snap.
///
/// Panels always run on Layer::AboveWidgets. The class extends
/// titan::Overlay so that registration, dispatch, and lifetime piggy-back
/// on the existing overlay machinery -- no separate plugin list, no
/// separate thunk.
///
/// Two ways to declare a panel:
///
/// **Lambda factory (recommended for simple panels):**
/// @code
///   class MyPlugin : public titan::Plugin {
///       MyPlugin() {
///           overlayPanel("main", titan::Anchor::Dynamic,
///                        [this](titan::OverlayPanel& p) {
///               p.title("My HUD");
///               p.line("Status:", status_);
///           });
///       }
///   };
/// @endcode
///
/// **Subclass form (for stateful panels):**
/// @code
///   class MyOverlay : public titan::OverlayPanel {
///   public:
///       MyOverlay(MyPlugin* p)
///           : OverlayPanel(p, "main", titan::Anchor::Dynamic, 50),
///             plugin_(p) {}
///       void render() override {
///           title("My HUD");
///           line("Status:", plugin_->status());
///       }
///   private:
///       MyPlugin* plugin_;
///   };
/// @endcode

#pragma once

#include "detail/abi.h"
#include "detail/backend.h"
#include "detail/registrable.h"
#include "render.h"
#include "plugin.h"

#include <cstdint>
#include <functional>
#include <utility>

namespace titan {

/// Anchor positions for an OverlayPanel. Mirrored by
/// TitanPluginSdk::AnchorAbi. Only the values with semantics that free
/// positioning can't replicate are exposed -- corner / canvas anchors
/// from the original RuneLite OverlayPosition enum are intentionally
/// dropped because users free-position into those areas via Alt-drag
/// (panels become Dynamic once moved).
enum class Anchor : uint8_t {
    /// The overlay positions itself (the host skips auto-stacking).
    /// Default for panels that haven't been dragged yet -- they land
    /// at the top-left margin until the user moves them.
    Dynamic            = 0,
    /// Stack horizontally centered along the top of the game viewport
    /// (widget 161.15). Falls back to the window-relative top-center
    /// when the viewport widget isn't loaded.
    TopCenter          = 1,
    /// Stack vertically centered along the left edge of the game
    /// viewport. Falls back to the window-relative left edge when the
    /// viewport widget isn't loaded.
    LeftCenter         = 2,
    /// Stack vertically centered along the right edge of the game
    /// viewport. Falls back to the window-relative right edge when the
    /// viewport widget isn't loaded.
    RightCenter        = 3,
    /// Anchored above the chatbox on the right side. Used for things
    /// like XP trackers in RuneLite. Falls back to a sensible bottom-
    /// right position when the chatbox widget cannot be resolved on
    /// the current revision.
    AboveChatboxRight  = 4,
    /// Follows the mouse cursor (panel renders just below-right of
    /// the cursor with a small offset). Drag is disabled for tooltips.
    Tooltip            = 5,
};

/// Default palette + layout. Chosen so most panels look good without
/// any per-call overrides; matches the existing chompy_hunter overlay
/// closely (with rounded corners + subtle border as cosmetic upgrades).
namespace overlay_panel_defaults {
    constexpr uint32_t kBackground       = 0xC8141821;  // semi-transparent dark slate
    constexpr uint32_t kBorderColor      = 0xFF3B5566;  // 1 px subtle outline
    constexpr float    kBorderThickness  = 1.0f;
    constexpr float    kCornerRadius     = 4.0f;
    constexpr int32_t  kPadHorizontal    = 8;
    constexpr int32_t  kPadVertical      = 6;
    constexpr int32_t  kLineGap          = 2;
    constexpr uint32_t kTitle            = 0xFF9DEBFF;  // matches chompy header
    constexpr uint32_t kLineLeft         = 0xFFEAF2F8;
    constexpr uint32_t kLineRight        = 0xFFEAF2F8;
    constexpr uint32_t kBarFill          = 0xFF60E060;
    constexpr uint32_t kBarBg            = 0xFF333333;
    constexpr int32_t  kPreferredWidth   = 220;
}

/// Sticky style applied to a panel. Setters update one field at a time;
/// setStyle() replaces all fields at once. Style persists across frames
/// until changed -- typically applied once in the panel's constructor.
struct OverlayPanelStyle {
    uint32_t background      = overlay_panel_defaults::kBackground;
    uint32_t borderColor     = overlay_panel_defaults::kBorderColor;
    float    borderThickness = overlay_panel_defaults::kBorderThickness;
    float    cornerRadius    = overlay_panel_defaults::kCornerRadius;
    int32_t  padHorizontal   = overlay_panel_defaults::kPadHorizontal;
    int32_t  padVertical     = overlay_panel_defaults::kPadVertical;
    int32_t  lineGap         = overlay_panel_defaults::kLineGap;
    uint32_t titleColor      = overlay_panel_defaults::kTitle;
    uint32_t lineLeftColor   = overlay_panel_defaults::kLineLeft;
    uint32_t lineRightColor  = overlay_panel_defaults::kLineRight;
    uint32_t barFillColor    = overlay_panel_defaults::kBarFill;
    uint32_t barBgColor      = overlay_panel_defaults::kBarBg;
};

/// Anchored, themeable, draggable HUD panel. Subclass of titan::Overlay
/// so it self-registers with the plugin via the existing overlay list
/// and dispatches through the existing thunkRenderOverlay loop.
///
/// Construction registers a stable handle with the host keyed by
/// (pluginId, name); the host loads any persisted layout override
/// (anchor / priority / free position) at register time.
class OverlayPanel : public Overlay {
public:
    /// Most plugins should NOT instantiate this directly -- prefer the
    /// much shorter Plugin::overlayPanel(...) lambda factory.
    OverlayPanel(Plugin* owner, const char* name,
                 Anchor defaultAnchor = Anchor::Dynamic,
                 int32_t defaultPriority = 50);

    ~OverlayPanel() override;

    OverlayPanel(const OverlayPanel&) = delete;
    OverlayPanel& operator=(const OverlayPanel&) = delete;

    /// Override Overlay::invoke to wrap the user's render() in a host
    /// begin/end pair. Plain overlays don't get this wrapping.
    void invoke() override;

    /// Subclass hook -- mirrors titan::Overlay::render(). Builder calls
    /// inside this body emit components into the host. Default impl is
    /// empty; the lambda-factory wrapper overrides it.
    void render() override {}

    // --- Builder methods (call from inside render()) -----------------

    /// Bold title row (~22 px). Default colour matches a soft cyan
    /// header tone. Returns *this for chained calls.
    OverlayPanel& title(const char* text,
                        uint32_t color = overlay_panel_defaults::kTitle);

    /// Two-column line: left label, right value (~16 px row). The right
    /// text is right-aligned inside the panel's content rect.
    OverlayPanel& line(const char* left, const char* right,
                       uint32_t leftColor  = overlay_panel_defaults::kLineLeft,
                       uint32_t rightColor = overlay_panel_defaults::kLineRight);

    /// Single-value variant: left text, no value column.
    OverlayPanel& line(const char* text,
                       uint32_t color = overlay_panel_defaults::kLineLeft) {
        return line(text, "", color, color);
    }

    /// Progress bar (~12 px) with [minVal..maxVal] range. Values are
    /// clamped; equal min/max produces an empty bar.
    OverlayPanel& progressBar(int32_t value, int32_t minVal, int32_t maxVal,
                              uint32_t fillColor = overlay_panel_defaults::kBarFill,
                              uint32_t bgColor   = overlay_panel_defaults::kBarBg);

    // --- Width hint (sticky) -----------------------------------------

    /// Hard-clamps to [80, 600]. Sticky -- applies until changed.
    OverlayPanel& setPreferredWidth(int32_t pixels);

    // --- Theming (sticky) --------------------------------------------
    //
    // All theming is sticky: set once and the panel keeps that style
    // across frames. Convenience setters mutate the panel's current
    // Style and re-publish it; setStyle() replaces all fields at once.

    OverlayPanel& setStyle(const OverlayPanelStyle& s);
    const OverlayPanelStyle& style() const { return cachedStyle_; }

    OverlayPanel& setBackgroundColor(uint32_t argb);
    OverlayPanel& setBorderColor(uint32_t argb);
    OverlayPanel& setBorderThickness(float pixels);
    OverlayPanel& setCornerRadius(float pixels);
    OverlayPanel& setPadding(int32_t horizontal, int32_t vertical);
    OverlayPanel& setLineGap(int32_t pixels);
    OverlayPanel& setTitleColor(uint32_t argb);
    OverlayPanel& setLineColors(uint32_t leftArgb, uint32_t rightArgb);
    OverlayPanel& setProgressBarColors(uint32_t fillArgb, uint32_t bgArgb);

    /// Convenience: scale every alpha channel by a 0..1 multiplier.
    /// Useful for fade-in / fade-out effects without recomputing every
    /// colour. Re-publishes the current style with rebased alphas.
    OverlayPanel& setOpacity(float alpha01);

    int32_t handle() const { return handle_; }

private:
    void publishStyle();

    int32_t handle_ = -1;
    int32_t preferredWidth_ = overlay_panel_defaults::kPreferredWidth;
    OverlayPanelStyle cachedStyle_{};
};

namespace detail {

/// Lambda-driven OverlayPanel used by Plugin::overlayPanel(...). Plugin
/// authors do not construct this directly.
class CallbackOverlayPanel final : public OverlayPanel {
public:
    using Fn = std::function<void(OverlayPanel&)>;
    CallbackOverlayPanel(Plugin* owner, const char* name,
                         Anchor anchor, int32_t priority, Fn fn)
        : OverlayPanel(owner, name, anchor, priority), fn_(std::move(fn)) {}
    void render() override { if (fn_) fn_(*this); }
private:
    Fn fn_;
};

}  // namespace detail

// ---------------------------------------------------------------------------
// Implementation (header-only inline)
// ---------------------------------------------------------------------------

inline Plugin* reserveOverlayPanelName(Plugin* owner, const char* name) {
    if (owner) owner->_registerOverlayPanelName(name);
    return owner;
}

inline OverlayPanel::OverlayPanel(Plugin* owner, const char* name,
                                  Anchor defaultAnchor, int32_t defaultPriority)
    : Overlay(reserveOverlayPanelName(owner, name), Layer::AboveWidgets) {
    if (auto* b = detail::backend()) {
        const char* pid = (owner ? owner->id() : "");
        const char* n   = (name ? name : "");
        handle_ = b->overlayPanelRegister(pid, n,
                                          static_cast<uint8_t>(defaultAnchor),
                                          defaultPriority);
        // Publish default style so the manager has a sticky baseline
        // even if the plugin never calls a theming setter.
        publishStyle();
    }
}

inline OverlayPanel::~OverlayPanel() {
    if (handle_ < 0) return;
    if (auto* b = detail::backend()) {
        b->overlayPanelUnregister(handle_);
    }
}

inline void OverlayPanel::invoke() {
    auto* b = detail::backend();
    if (!b || handle_ < 0) return;
    b->overlayPanelBegin(handle_, preferredWidth_);
    render();
    b->overlayPanelEnd(handle_);
}

inline OverlayPanel& OverlayPanel::title(const char* text, uint32_t color) {
    if (auto* b = detail::backend()) {
        b->overlayPanelTitle(handle_, text ? text : "", color);
    }
    return *this;
}

inline OverlayPanel& OverlayPanel::line(const char* left, const char* right,
                                        uint32_t leftColor, uint32_t rightColor) {
    if (auto* b = detail::backend()) {
        b->overlayPanelLine(handle_,
                            left ? left : "",
                            right ? right : "",
                            leftColor, rightColor);
    }
    return *this;
}

inline OverlayPanel& OverlayPanel::progressBar(int32_t value, int32_t minVal,
                                               int32_t maxVal,
                                               uint32_t fillColor,
                                               uint32_t bgColor) {
    if (auto* b = detail::backend()) {
        b->overlayPanelProgressBar(handle_, value, minVal, maxVal,
                                   fillColor, bgColor);
    }
    return *this;
}

inline OverlayPanel& OverlayPanel::setPreferredWidth(int32_t pixels) {
    if (pixels < 80) pixels = 80;
    if (pixels > 600) pixels = 600;
    preferredWidth_ = pixels;
    return *this;
}

inline void OverlayPanel::publishStyle() {
    auto* b = detail::backend();
    if (!b || handle_ < 0) return;
    TitanPluginSdk::OverlayPanelStyleAbi abi{};
    abi.background       = cachedStyle_.background;
    abi.borderColor      = cachedStyle_.borderColor;
    abi.borderThickness  = cachedStyle_.borderThickness;
    abi.cornerRadius     = cachedStyle_.cornerRadius;
    abi.padHorizontal    = cachedStyle_.padHorizontal;
    abi.padVertical      = cachedStyle_.padVertical;
    abi.lineGap          = cachedStyle_.lineGap;
    abi.titleColor       = cachedStyle_.titleColor;
    abi.lineLeftColor    = cachedStyle_.lineLeftColor;
    abi.lineRightColor   = cachedStyle_.lineRightColor;
    abi.barFillColor     = cachedStyle_.barFillColor;
    abi.barBgColor       = cachedStyle_.barBgColor;
    b->overlayPanelSetStyle(handle_, &abi);
}

inline OverlayPanel& OverlayPanel::setStyle(const OverlayPanelStyle& s) {
    cachedStyle_ = s;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setBackgroundColor(uint32_t argb) {
    cachedStyle_.background = argb;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setBorderColor(uint32_t argb) {
    cachedStyle_.borderColor = argb;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setBorderThickness(float pixels) {
    cachedStyle_.borderThickness = pixels;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setCornerRadius(float pixels) {
    cachedStyle_.cornerRadius = pixels;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setPadding(int32_t horizontal, int32_t vertical) {
    cachedStyle_.padHorizontal = horizontal;
    cachedStyle_.padVertical = vertical;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setLineGap(int32_t pixels) {
    cachedStyle_.lineGap = pixels;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setTitleColor(uint32_t argb) {
    cachedStyle_.titleColor = argb;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setLineColors(uint32_t leftArgb, uint32_t rightArgb) {
    cachedStyle_.lineLeftColor = leftArgb;
    cachedStyle_.lineRightColor = rightArgb;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setProgressBarColors(uint32_t fillArgb, uint32_t bgArgb) {
    cachedStyle_.barFillColor = fillArgb;
    cachedStyle_.barBgColor = bgArgb;
    publishStyle();
    return *this;
}

inline OverlayPanel& OverlayPanel::setOpacity(float alpha01) {
    if (alpha01 < 0.0f) alpha01 = 0.0f;
    if (alpha01 > 1.0f) alpha01 = 1.0f;
    auto rebase = [alpha01](uint32_t argb) -> uint32_t {
        const uint32_t origAlpha = (argb >> 24) & 0xFFu;
        const uint32_t newAlpha  = static_cast<uint32_t>(origAlpha * alpha01 + 0.5f);
        return (argb & 0x00FFFFFFu) | ((newAlpha & 0xFFu) << 24);
    };
    cachedStyle_.background     = rebase(cachedStyle_.background);
    cachedStyle_.borderColor    = rebase(cachedStyle_.borderColor);
    cachedStyle_.titleColor     = rebase(cachedStyle_.titleColor);
    cachedStyle_.lineLeftColor  = rebase(cachedStyle_.lineLeftColor);
    cachedStyle_.lineRightColor = rebase(cachedStyle_.lineRightColor);
    cachedStyle_.barFillColor   = rebase(cachedStyle_.barFillColor);
    cachedStyle_.barBgColor     = rebase(cachedStyle_.barBgColor);
    publishStyle();
    return *this;
}

// ---------------------------------------------------------------------------
// Plugin::overlayPanel(...) factory definitions
//
// Defined here (not in plugin.h) because the body needs the complete
// type of detail::CallbackOverlayPanel to construct it via make_unique.
// Plugin authors who want the factory include <titan/overlay_panel.h>;
// the storage in Plugin uses unique_ptr<OverlayBase> so plugin.h alone
// compiles without this header for pure metadata scans.
// ---------------------------------------------------------------------------

inline OverlayPanel& Plugin::overlayPanel(const char* name, Anchor anchor,
                                          int32_t priority,
                                          std::function<void(OverlayPanel&)> fn) {
    auto p = std::make_unique<detail::CallbackOverlayPanel>(
        this, name, anchor, priority, std::move(fn));
    OverlayPanel& ref = *p;
    ownedOverlayPanels_.push_back(std::move(p));
    return ref;
}

inline OverlayPanel& Plugin::overlayPanel(const char* name, Anchor anchor,
                                          std::function<void(OverlayPanel&)> fn) {
    return overlayPanel(name, anchor, /*priority=*/50, std::move(fn));
}

}  // namespace titan
