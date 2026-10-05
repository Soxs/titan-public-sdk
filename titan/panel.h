/// @file titan/panel.h
/// @brief Fluent panel builder.
///
/// A Plugin's buildPanel(Panel&) method receives a Panel and appends commands
/// to it (text, buttons, checkboxes, sliders, tables, etc.). The SDK serializes
/// the resulting command list into the PanelElement wire format, and the
/// controller replays those commands with ImGui on its side.

#pragma once

#include "detail/abi.h"
#include "detail/host.h"

#include <cstdint>
#include <cstring>
#include <fstream>
#include <functional>
#include <string>
#include <utility>
#include <vector>

namespace titan {

enum class PanelTone : int32_t {
    neutral = 0,
    accent = 1,
    success = 2,
    warning = 3,
    danger = 4,
    info = 5,
};

enum class PanelButtonStyle : int32_t {
    normal = 0,
    primary = 1,
    secondary = 2,
    danger = 3,
    ghost = 4,
};

enum class PanelInputFlags : int32_t {
    none = 0,
    password = 1 << 0,
    multiline = 1 << 1,
};

inline PanelInputFlags operator|(PanelInputFlags a, PanelInputFlags b) {
    return static_cast<PanelInputFlags>(
        static_cast<int32_t>(a) | static_cast<int32_t>(b));
}

inline PanelInputFlags operator&(PanelInputFlags a, PanelInputFlags b) {
    return static_cast<PanelInputFlags>(
        static_cast<int32_t>(a) & static_cast<int32_t>(b));
}

/// Fluent builder for plugin side-panels. Use from Plugin::buildPanel.
class Panel {
public:
    using Type = TitanNativeRecords::PanelElementType;

    // --- Text ---
    Panel& text(const std::string& s)         { pushEl(Type::text, s); return *this; }
    Panel& wrapped(const std::string& s)      { pushEl(Type::textWrapped, s); return *this; }
    Panel& disabled(const std::string& s)     { pushEl(Type::textDisabled, s); return *this; }
    Panel& bullet(const std::string& s)       { pushEl(Type::bulletText, s); return *this; }
    Panel& colored(const std::string& s, uint32_t color) {
        auto& el = pushEl(Type::textColored, s);
        el.color = color;
        return *this;
    }
    Panel& status(const std::string& s, PanelTone tone = PanelTone::neutral) {
        auto& el = pushEl(Type::textColored, s);
        el.intVal3 = static_cast<int32_t>(tone) + 1;
        return *this;
    }
    Panel& label(const std::string& label, const std::string& value) {
        auto& el = pushEl(Type::labelText, label);
        copyFixed(el.textSecondary, sizeof(el.textSecondary), value);
        return *this;
    }
    /// Inline "(?)" marker that reveals @p text as a tooltip on hover. Use it
    /// to keep help text out of the layout until the user asks for it (put it
    /// on a `sameLine()` after a section header or label).
    Panel& help(const std::string& text) { pushEl(Type::helpMarker, text); return *this; }
    /// Compact colored status pill (e.g. "Ready", "AFK"). Sits inline, so it
    /// can follow a `label(...).sameLine()` run.
    Panel& badge(const std::string& text, PanelTone tone = PanelTone::neutral) {
        auto& el = pushEl(Type::badge, text);
        el.intVal3 = static_cast<int32_t>(tone) + 1;
        return *this;
    }

    // --- Layout ---
    Panel& separator()                        { pushEl(Type::separator, ""); return *this; }
    Panel& separatorText(const std::string& s){ pushEl(Type::separatorText, s); return *this; }
    Panel& section(const std::string& s)       { return separatorText(s); }
    Panel& spacing()                          { pushEl(Type::spacing, ""); return *this; }
    Panel& sameLine()                         { pushEl(Type::sameLine, ""); return *this; }
    Panel& newLine()                          { pushEl(Type::newLine, ""); return *this; }
    Panel& indent()                           { pushEl(Type::indent, ""); return *this; }
    Panel& unindent()                         { pushEl(Type::unindent, ""); return *this; }
    Panel& dummy(float w, float h) {
        auto& el = pushEl(Type::dummy, "");
        el.widthVal = w;
        el.heightVal = h;
        return *this;
    }

    // --- Interactive controls ---
    Panel& button(const std::string& label, int32_t actionId,
                  PanelButtonStyle style = PanelButtonStyle::normal,
                  float width = 0.0f, float height = 0.0f) {
        auto& el = pushEl(Type::button, label);
        el.actionId = actionId;
        el.intVal3 = static_cast<int32_t>(style);
        el.widthVal = width;
        el.heightVal = height;
        return *this;
    }
    Panel& smallButton(const std::string& label, int32_t actionId,
                       PanelButtonStyle style = PanelButtonStyle::normal) {
        auto& el = pushEl(Type::smallButton, label);
        el.actionId = actionId;
        el.intVal3 = static_cast<int32_t>(style);
        return *this;
    }
    Panel& primaryButton(const std::string& label, int32_t actionId,
                         float width = -1.0f, float height = 0.0f) {
        return button(label, actionId, PanelButtonStyle::primary, width, height);
    }
    Panel& secondaryButton(const std::string& label, int32_t actionId,
                           float width = -1.0f, float height = 0.0f) {
        return button(label, actionId, PanelButtonStyle::secondary, width, height);
    }
    Panel& dangerButton(const std::string& label, int32_t actionId,
                        float width = -1.0f, float height = 0.0f) {
        return button(label, actionId, PanelButtonStyle::danger, width, height);
    }
    Panel& selectable(const std::string& label, int32_t actionId, bool selected = false) {
        auto& el = pushEl(Type::selectable, label);
        el.actionId = actionId;
        el.boolVal = selected ? 1 : 0;
        return *this;
    }
    Panel& checkbox(const std::string& label, int32_t actionId, bool value) {
        auto& el = pushEl(Type::checkbox, label);
        el.actionId = actionId;
        el.boolVal = value ? 1 : 0;
        return *this;
    }
    Panel& sliderInt(const std::string& label, int32_t actionId,
                     int32_t value, int32_t minValue, int32_t maxValue) {
        auto& el = pushEl(Type::sliderInt, label);
        el.actionId = actionId;
        el.intVal = value;
        el.intVal2 = minValue;
        el.intVal3 = maxValue;
        return *this;
    }
    Panel& sliderFloat(const std::string& label, int32_t actionId,
                       float value, float minValue, float maxValue) {
        auto& el = pushEl(Type::sliderFloat, label);
        el.actionId = actionId;
        el.floatVal = value;
        el.floatVal2 = minValue;
        el.floatVal3 = maxValue;
        return *this;
    }
    Panel& inputText(const std::string& label, int32_t actionId, const std::string& value,
                     PanelInputFlags flags = PanelInputFlags::none) {
        auto& el = pushEl(Type::inputText, label);
        el.actionId = actionId;
        el.intVal2 = -1;  // no submit action
        el.intVal3 = static_cast<int32_t>(flags);
        copyFixed(el.textSecondary, sizeof(el.textSecondary), value);
        return *this;
    }

    /// Same as `inputText` but additionally fires @p submitActionId when
    /// the user presses Enter inside the field. The regular @p actionId
    /// still fires on both Enter and focus-loss (for value-change
    /// propagation); @p submitActionId fires ONLY on Enter. Use this to
    /// wire a "form submit" button equivalent - e.g. hitting Enter in a
    /// password field triggers unlock in the same way clicking Unlock
    /// would.
    Panel& inputText(const std::string& label, int32_t actionId,
                     int32_t submitActionId, const std::string& value,
                     PanelInputFlags flags = PanelInputFlags::none) {
        auto& el = pushEl(Type::inputText, label);
        el.actionId = actionId;
        el.intVal2 = submitActionId;
        el.intVal3 = static_cast<int32_t>(flags);
        copyFixed(el.textSecondary, sizeof(el.textSecondary), value);
        return *this;
    }
    Panel& inputPassword(const std::string& label, int32_t actionId,
                         const std::string& value) {
        return inputText(label, actionId, value, PanelInputFlags::password);
    }
    Panel& inputPassword(const std::string& label, int32_t actionId,
                         int32_t submitActionId, const std::string& value) {
        return inputText(label, actionId, submitActionId, value,
                         PanelInputFlags::password);
    }

    /// Dropdown selector. @p items are the option labels; @p selectedIndex is
    /// the current selection. Fires @p actionId with the chosen index (an
    /// integer Value) when the user picks a different option. The label is
    /// drawn above the full-width control.
    Panel& combo(const std::string& label, int32_t actionId,
                 const std::vector<std::string>& items, int32_t selectedIndex) {
        auto& el = pushEl(Type::combo, label);
        el.actionId = actionId;
        el.intVal = selectedIndex;
        std::string joined;
        for (size_t i = 0; i < items.size(); ++i) {
            if (i) joined.push_back('\n');
            joined += items[i];
        }
        copyFixed(el.textSecondary, sizeof(el.textSecondary), joined);
        return *this;
    }

    // --- Disabled scope ---
    /// Grey-out and make non-interactive everything until the matching
    /// `endDisabled()`. Pass @p disabled=false to leave the block enabled
    /// (useful for a data-driven gate). Always pair with `endDisabled()`.
    Panel& beginDisabled(bool disabled = true) {
        auto& el = pushEl(Type::beginDisabled, "");
        el.boolVal = disabled ? 1 : 0;
        return *this;
    }
    Panel& endDisabled() { pushEl(Type::endDisabled, ""); return *this; }

    // --- Progress ---
    Panel& progress(float fraction, const std::string& overlay = "") {
        auto& el = pushEl(Type::progressBar, overlay);
        el.floatVal = fraction;
        return *this;
    }

    // --- Tree / collapsing ---
    Panel& collapsing(const std::string& label) { pushEl(Type::collapsingHeader, label); return *this; }
    Panel& beginCollapsible(const std::string& label, bool defaultOpen = false) {
        auto& el = pushEl(Type::collapsingHeader, label);
        el.intVal3 = kCollapsibleBlockMarker;
        el.boolVal = defaultOpen ? 1 : 0;
        return *this;
    }
    Panel& endCollapsible() {
        auto& el = pushEl(Type::treePop, "");
        el.intVal3 = kCollapsibleBlockMarker;
        return *this;
    }
    Panel& treeNode(const std::string& label)   { pushEl(Type::treeNode, label);         return *this; }
    Panel& treePop()                            { pushEl(Type::treePop, "");              return *this; }

    // --- Tabs ---
    /// Emit a tab bar. Between beginTabBar()/endTabBar(), emit one or more
    /// beginTabItem(label)/endTabItem() blocks. The controller renders only the
    /// selected tab's contents (non-selected tab bodies are skipped), so emit
    /// every tab's content unconditionally -- do not branch on selection.
    Panel& beginTabBar(const std::string& id) {
        pushEl(Type::beginTabBar, id);
        return *this;
    }
    Panel& endTabBar() {
        pushEl(Type::endTabBar, "");
        return *this;
    }
    Panel& beginTabItem(const std::string& label) {
        pushEl(Type::beginTabItem, label);
        return *this;
    }
    Panel& endTabItem() {
        pushEl(Type::endTabItem, "");
        return *this;
    }

    // --- Tables ---
    Panel& beginTable(const std::string& id, int32_t columns, int32_t flags = 0) {
        auto& el = pushEl(Type::beginTable, id);
        el.intVal = columns;
        el.intVal2 = flags;
        return *this;
    }
    Panel& endTable() {
        pushEl(Type::endTable, "");
        return *this;
    }
    Panel& tableNextRow() {
        pushEl(Type::tableNextRow, "");
        return *this;
    }
    Panel& tableNextColumn() {
        pushEl(Type::tableNextColumn, "");
        return *this;
    }
    Panel& tableSetupColumn(const std::string& label, int32_t flags = 0, float width = 0.0f) {
        auto& el = pushEl(Type::tableSetupColumn, label);
        el.intVal = flags;
        el.floatVal = width;
        return *this;
    }
    Panel& tableHeadersRow() {
        pushEl(Type::tableHeadersRow, "");
        return *this;
    }

    // --- Tooltip on previous item ---
    Panel& tooltip(const std::string& t) { pushEl(Type::setTooltip, t); return *this; }

    // --- Horizontal alignment ---
    enum class Align : int32_t { left = 0, center = 1, right = 2 };
    /// Align a same-line run of buttons within the content width. Emit the
    /// buttons (with sameLine between them) between beginAlign()/endAlign().
    Panel& beginAlign(Align a) {
        auto& el = pushEl(Type::alignBegin, "");
        el.intVal = static_cast<int32_t>(a);
        return *this;
    }
    Panel& endAlign() { pushEl(Type::alignEnd, ""); return *this; }

    // --- Groups / child regions ---
    Panel& beginGroup() { pushEl(Type::beginGroup, ""); return *this; }
    Panel& endGroup()   { pushEl(Type::endGroup, "");   return *this; }

    /// Begin a child region. `childFlags` is a mask of
    /// `TitanNativeRecords::PanelChildFlag`. width/height of 0 means "use available
    /// width" / "auto (with AutoResizeY)". Always pair with `endChild()`.
    Panel& beginChild(const std::string& id, int32_t childFlags = 0,
                      float width = 0.0f, float height = 0.0f) {
        auto& el = pushEl(Type::beginChild, id);
        el.intVal2 = childFlags;
        el.widthVal = width;
        el.heightVal = height;
        return *this;
    }
    Panel& endChild() { pushEl(Type::endChild, ""); return *this; }

    /// Convenience: a framed, padded, auto-height "card" container. Pair with
    /// `endCard()`. `id` must be unique within the panel.
    Panel& beginCard(const std::string& id) {
        return beginChild(id,
                          TitanNativeRecords::kPanelChildFrame |
                          TitanNativeRecords::kPanelChildAutoResizeY |
                          TitanNativeRecords::kPanelChildPadding);
    }
    Panel& endCard() { pushEl(Type::endChild, ""); return *this; }

    /// Raw escape hatch: append a custom element directly. Returns a reference
    /// to the stored element so extra fields can be set in place.
    TitanNativeRecords::PanelElement& push(Type t, const std::string& text) {
        return pushEl(t, text);
    }

    const std::vector<TitanNativeRecords::PanelElement>& _elements() const { return elements_; }

private:
    static constexpr int32_t kCollapsibleBlockMarker = 0x54434F4C; // "TCOL"

    TitanNativeRecords::PanelElement& pushEl(Type t, const std::string& text) {
        TitanNativeRecords::PanelElement el = {};
        el.type = t;
        copyFixed(el.text, sizeof(el.text), text);
        elements_.push_back(el);
        return elements_.back();
    }

    static void copyFixed(char* dst, size_t dstSize, const std::string& src) {
        if (!dst || dstSize == 0) return;
        dst[0] = '\0';
        const size_t n = (std::min)(src.size(), dstSize - 1);
        std::memcpy(dst, src.data(), n);
        dst[n] = '\0';
    }

    std::vector<TitanNativeRecords::PanelElement> elements_;
};

/// One registered side panel. A plugin may register several (see
/// Plugin::panel). Each carries a stable `id` (used to route panel content and
/// actions), a display `title` for the nav button tooltip, an optional icon
/// spec (`awesome:gear`, `lucide:house`, `phosphor:gear:bold`, or a legacy Font
/// Awesome glyph), an optional icon tint, an optional custom PNG image icon,
/// plus the build and action callbacks. Construct via Plugin::panel(...) and
/// chain the optional setters.
///
/// @code
///   panel("stats", "Statistics", [this](titan::Panel& p) {
///       p.label("Kills", std::to_string(kills_));
///   }).icon("awesome:chart-bar")            // Font Awesome alias
///     .iconColor(0xFF68CC92)
///     .onAction([this](int32_t id, const auto&) { handle(id); });
///
///   panel("config", "Config", [this](titan::Panel& p) { ... })
///     .icon("lucide:settings")
///     .image("icons/config.png");           // PNG takes precedence
/// @endcode
class SidePanel {
public:
    using ActionFn = std::function<void(int32_t, const TitanNativeRecords::Value&)>;

    SidePanel(std::string id, std::string title, std::function<void(Panel&)> build)
        : id_(std::move(id)), title_(std::move(title)), build_(std::move(build)) {}

    /// Handle control interactions inside this panel.
    SidePanel& onAction(ActionFn fn) { action_ = std::move(fn); return *this; }
    /// Set a side-panel icon spec. Use `awesome:alias`, `lucide:name`,
    /// `phosphor:name[:weight]`, or a legacy raw Font Awesome glyph.
    SidePanel& icon(std::string glyph) { iconGlyph_ = std::move(glyph); return *this; }
    /// Set the icon tint as ARGB (0xAARRGGBB). 0 uses the controller theme.
    SidePanel& iconColor(uint32_t argb) { iconColor_ = argb; return *this; }
    /// Use a custom PNG image icon loaded lazily from a file path (resolved
    /// relative to the process cwd / absolute). Takes precedence over a glyph.
    SidePanel& image(std::string path) {
        imagePath_ = std::move(path);
        imageBytes_.clear();
        return *this;
    }
    /// Use a custom PNG image icon supplied as in-memory bytes (e.g. embedded
    /// via an `#embed` / byte array). Takes precedence over a glyph.
    SidePanel& image(const uint8_t* bytes, size_t len) {
        imageBytes_.assign(bytes, bytes + len);
        imagePath_.clear();
        return *this;
    }

    const std::string& id() const { return id_; }
    const std::string& title() const { return title_; }
    const std::string& iconGlyph() const { return iconGlyph_; }
    uint32_t iconColor() const { return iconColor_; }
    bool hasImage() const { return !imageBytes_.empty() || !imagePath_.empty(); }

    void build(Panel& p) const { if (build_) build_(p); }
    void action(int32_t actionId, const TitanNativeRecords::Value& v) const {
        if (action_) action_(actionId, v);
    }

    /// Copy up to `maxBytes` of the resolved PNG image into `out`. Returns the
    /// byte count, or 0 when there is no image icon. File-backed images are
    /// loaded once and cached.
    uint32_t iconBytes(uint8_t* out, uint32_t maxBytes) const {
        const std::vector<uint8_t>* src = nullptr;
        if (!imageBytes_.empty()) {
            src = &imageBytes_;
        } else if (!imagePath_.empty()) {
            if (loadedImage_.empty()) loadImageFile();
            src = &loadedImage_;
        }
        if (!src || src->empty() || !out || maxBytes == 0) return 0;
        const uint32_t n = src->size() < maxBytes
            ? static_cast<uint32_t>(src->size()) : maxBytes;
        std::memcpy(out, src->data(), n);
        return n;
    }

private:
    void loadImageFile() const {
        std::ifstream f(imagePath_, std::ios::binary);
        if (!f) return;
        loadedImage_.assign(std::istreambuf_iterator<char>(f),
                            std::istreambuf_iterator<char>());
    }

    std::string id_;
    std::string title_;
    std::string iconGlyph_;
    uint32_t iconColor_ = 0;
    std::string imagePath_;
    std::vector<uint8_t> imageBytes_;
    std::function<void(Panel&)> build_;
    ActionFn action_;
    mutable std::vector<uint8_t> loadedImage_;
};

}  // namespace titan
