/// @file titan/setting.h
/// @brief Self-registering settings and sections for titan::Plugin.
///
/// Plugins declare BoolSetting / IntSetting / ComboSetting / StringSetting /
/// ColorSetting and Section members on their subclass. Each one pushes itself
/// onto the owning Plugin's registry at construction time, so the SDK thunks
/// can serialize them into the TitanNativeRecords::Setting wire format and route
/// setSetting updates back to the typed member without the plugin author
/// writing a single if/else chain.

#pragma once

#include "detail/abi.h"
#include "detail/registrable.h"
#include "plugin.h"

#include <algorithm>
#include <cstring>
#include <functional>
#include <string>
#include <utility>
#include <vector>

namespace titan {

// ---------------------------------------------------------------------------
// Section
// ---------------------------------------------------------------------------

class Section;

/// Optional metadata that can be passed to a Section constructor.
struct SectionOptions {
    /// Long description rendered as a tooltip on the collapsing header.
    std::string description;
    /// Relative ordering versus other sections. Lower values render first.
    int32_t position = 0;
    /// If true, the section is collapsed when the plugin is first shown.
    bool closedByDefault = false;
    /// Section of the same plugin to nest this one inside (SDK 143). The
    /// controller draws it as a collapsing header within the parent's, after
    /// the parent's own settings, ordered by `position` among its siblings.
    /// Null, a section of another plugin, or a parent chain that loops back
    /// leaves it at the top level.
    const Section* parent = nullptr;
};

/// A named group of settings. Declared as a member of the Plugin subclass:
///
/// @code
///   titan::Section combat{this, "combat", "Combat"};
///   titan::Section looting{this, "looting", "Looting",
///       {.description = "Ground-item pickup", .closedByDefault = true}};
///   // SDK 143: nested inside Looting, after Looting's own settings.
///   titan::Section lootFilters{this, "lootFilters", "Filters",
///       {.parent = &looting}};
/// @endcode
class Section : public detail::SectionBase {
public:
    Section(Plugin* owner, const char* key, const char* name,
            SectionOptions opts = {})
        : key_(key ? key : ""),
          name_(name ? name : ""),
          opts_(std::move(opts)) {
        if (owner) owner->_registerSection(this);
    }

    const char* key() const override { return key_.c_str(); }
    const char* name() const { return name_.c_str(); }
    int32_t position() const { return opts_.position; }
    bool isClosedByDefault() const { return opts_.closedByDefault; }
    const std::string& description() const { return opts_.description; }
    /// The section this one is nested in, or null (SDK 143).
    const Section* parent() const { return opts_.parent; }
    const detail::SectionBase* parentSection() const override { return opts_.parent; }

    void serialize(TitanNativeRecords::Section& out) const override {
        out = {};
        detail::copyFixed(out.key, sizeof(out.key), key_);
        detail::copyFixed(out.name, sizeof(out.name), name_);
        detail::copyFixed(out.description, sizeof(out.description), opts_.description);
        out.position = opts_.position;
        out.closedByDefault = opts_.closedByDefault ? 1 : 0;
    }

private:
    std::string key_;
    std::string name_;
    SectionOptions opts_;
};

// ---------------------------------------------------------------------------
// Setting base helpers
// ---------------------------------------------------------------------------

/// Metadata shared by every typed setting. Pass as the last constructor
/// argument when any of these need to be overridden.
struct SettingMeta {
    /// Owning section (optional). Settings with a null section render under a
    /// synthetic "General" group.
    Section* section = nullptr;
    /// Relative ordering within the section.
    int32_t position = 0;
    /// Initial hidden flag. Can be flipped at runtime via setHidden().
    bool hidden = false;
    /// Hover tooltip text shown in the controller UI.
    std::string tooltip;
};

namespace detail {

/// Tell the host the PLUGIN changed one of its own settings (SDK 140).
///
/// @p setting non-null when the VALUE changed; its current value is sent
/// with the report so the host records the two together. Reading the value
/// back later would race the snapshot caches the managed runtimes serve.
///
/// @p hidden non-null when the VISIBILITY changed. Presentation only: the
/// panel repaints and nothing is written to the user's config.
///
/// Only plugin-authored writes come through here. A value the controller
/// pushed down lands in apply() instead, which never reports -- that is what
/// keeps an IntSetting clamp, a matrix availability mask or a blanked secret
/// from being mistaken for plugin intent and written over the saved value.
///
/// Safe on a host that predates SDK 140 (and inside client.dll, whose
/// backend installs the same entry): the call degrades to a no-op and the
/// value simply stays unpersisted, as it was before.
inline void notifySettingChanged(Plugin* owner, const std::string& key,
                                 const SettingBase* setting,
                                 const uint8_t* hidden) {
    if (!owner || key.empty()) return;
    auto* b = backend();
    if (!b) return;
    const char* pluginId = owner->id();
    if (!pluginId || !pluginId[0]) return;
    if (!setting) {
        b->markSettingChanged(pluginId, key.c_str(), nullptr, hidden);
        return;
    }
    TitanNativeRecords::Value value{};
    setting->buildValue(value);
    b->markSettingChanged(pluginId, key.c_str(), &value, hidden);
}

/// CRTP base that implements the registrable SettingBase boilerplate for all
/// the concrete setting types. Subclasses supply the value-payload type via
/// `Value` and the `ValueType` / `buildValue()` helpers.
template <typename Derived, typename T>
class SettingCommon : public SettingBase {
public:
    SettingCommon(Plugin* owner, const char* key, const char* name,
                  T defaultValue, SettingMeta meta)
        : key_(key ? key : ""),
          name_(name ? name : ""),
          value_(defaultValue),
          defaultValue_(std::move(defaultValue)),
          meta_(std::move(meta)) {
        owner_ = owner;
        if (owner) owner->_registerSetting(this);
    }

    // --- Runtime state ---
    const T& value() const { return value_; }
    const T& defaultValue() const { return defaultValue_; }
    bool isHidden() const override { return meta_.hidden; }
    /// Show or hide this setting in the controller panel at runtime. The
    /// value is untouched and stays saved, so a setting hidden behind a
    /// mode switch keeps what the user last chose. Reports to the host so
    /// the panel repaints; a no-op flip costs nothing.
    void setHidden(bool v) override {
        if (meta_.hidden == v) return;
        meta_.hidden = v;
        const uint8_t hidden = v ? 1 : 0;
        notifySettingChanged(owner_, key_, /*setting=*/nullptr, &hidden);
    }
    int32_t position() const override { return meta_.position; }
    const std::string& tooltip() const { return meta_.tooltip; }
    Section* section() const { return meta_.section; }
    const char* key() const override { return key_.c_str(); }
    const char* name() const { return name_.c_str(); }

    /// Replace the current value and fire the plugin's onSettingChanged hook
    /// as if the controller had performed the update.
    ///
    /// SDK 140: the new value is also saved to the user's controller config,
    /// so it survives a restart just like a side-panel edit. The host is
    /// told before the hook runs, so the write is recorded even if the
    /// plugin's own onSettingChanged throws. Every typed setter funnels
    /// here -- reset(), ColorSetting::set, MatrixSetting::set/setCell.
    ///
    /// Writing the value it already holds is a no-op: no report, and no
    /// onSettingChanged. That matches the JS accessor, and it means a
    /// plugin may re-assert a value every tick without churning the
    /// controller's settings file.
    void set(T newValue) {
        if (value_ == newValue) return;
        value_ = std::move(newValue);
        notifySettingChanged(owner_, key_, this, /*hidden=*/nullptr);
        if (owner_) owner_->onSettingChanged(key_);
    }

    void reset() override { set(defaultValue_); }

    /// Implicit conversion so `if (mySetting) {...}` and `x + mySetting` work.
    operator const T&() const { return value_; }

protected:
    Plugin* owner_ = nullptr;
    std::string key_;
    std::string name_;
    T value_;
    T defaultValue_;
    SettingMeta meta_;

    void fillMeta(TitanNativeRecords::Setting& out) const {
        copyFixed(out.key, sizeof(out.key), key_);
        copyFixed(out.name, sizeof(out.name), name_);
        copyFixed(out.tooltip, sizeof(out.tooltip), meta_.tooltip);
        if (meta_.section) {
            copyFixed(out.section, sizeof(out.section), meta_.section->key());
        }
        out.position = meta_.position;
        out.hidden = meta_.hidden ? 1 : 0;
    }
};

}  // namespace detail

// ---------------------------------------------------------------------------
// Boolean setting (checkbox)
// ---------------------------------------------------------------------------

class BoolSetting : public detail::SettingCommon<BoolSetting, bool> {
public:
    BoolSetting(Plugin* owner, const char* key, const char* name,
                bool defaultValue, SettingMeta meta = {})
        : SettingCommon(owner, key, name, defaultValue, std::move(meta)) {}

    void buildValue(TitanNativeRecords::Value& out) const override {
        out = {};
        out.type = TitanNativeRecords::ValueType::boolean;
        out.boolValue = value_ ? 1 : 0;
    }

    void serialize(TitanNativeRecords::Setting& out) const override {
        out = {};
        fillMeta(out);
        out.value.type = TitanNativeRecords::ValueType::boolean;
        out.value.boolValue = value_ ? 1 : 0;
        out.defaultValue.type = TitanNativeRecords::ValueType::boolean;
        out.defaultValue.boolValue = defaultValue_ ? 1 : 0;
        out.controlType = TitanNativeRecords::ControlType::checkbox;
    }

    std::string apply(const TitanNativeRecords::Value& v) override {
        if (v.type != TitanNativeRecords::ValueType::boolean)
            return "Expected boolean value";
        value_ = v.boolValue != 0;
        return {};
    }
};

// ---------------------------------------------------------------------------
// Integer slider setting
// ---------------------------------------------------------------------------

class IntSetting : public detail::SettingCommon<IntSetting, int32_t> {
public:
    IntSetting(Plugin* owner, const char* key, const char* name,
               int32_t defaultValue, int32_t minValue, int32_t maxValue,
               SettingMeta meta = {})
        : SettingCommon(owner, key, name, defaultValue, std::move(meta)),
          min_(minValue), max_(maxValue) {}

    int32_t min() const { return min_; }
    int32_t max() const { return max_; }

    /// Clamp to [min, max], exactly as a controller-driven write does.
    /// Hides SettingCommon::set so every write path sanitizes, the same
    /// reason ColorSetting hides it to normalize and MatrixSetting to mask.
    /// Without this a plugin could persist a value its own slider cannot
    /// represent, and the controller would replay it on every launch.
    void set(int32_t newValue) {
        detail::SettingCommon<IntSetting, int32_t>::set(
            (std::max)(min_, (std::min)(max_, newValue)));
    }

    void buildValue(TitanNativeRecords::Value& out) const override {
        out = {};
        out.type = TitanNativeRecords::ValueType::integer;
        out.intValue = value_;
    }

    void serialize(TitanNativeRecords::Setting& out) const override {
        out = {};
        fillMeta(out);
        out.value.type = TitanNativeRecords::ValueType::integer;
        out.value.intValue = value_;
        out.defaultValue.type = TitanNativeRecords::ValueType::integer;
        out.defaultValue.intValue = defaultValue_;
        out.controlType = TitanNativeRecords::ControlType::slider;
        out.minValue = min_;
        out.maxValue = max_;
    }

    std::string apply(const TitanNativeRecords::Value& v) override {
        if (v.type != TitanNativeRecords::ValueType::integer)
            return "Expected integer value";
        value_ = (std::max)(min_, (std::min)(max_, v.intValue));
        return {};
    }

private:
    int32_t min_ = 0;
    int32_t max_ = 0;
};

/// An IntSetting typed into a number box with -/+ step buttons rather than
/// dragged on a slider -- for values that need an exact number, such as
/// quantities. Same value, range and clamping as IntSetting.
class IntInputSetting : public IntSetting {
public:
    using IntSetting::IntSetting;

    void serialize(TitanNativeRecords::Setting& out) const override {
        IntSetting::serialize(out);
        out.controlType = TitanNativeRecords::ControlType::numberInput;
    }
};

// ---------------------------------------------------------------------------
// Color setting (integer 0xRRGGBB value rendered with a color picker)
// ---------------------------------------------------------------------------

class ColorSetting : public detail::SettingCommon<ColorSetting, int32_t> {
public:
    ColorSetting(Plugin* owner, const char* key, const char* name,
                 int32_t defaultValue, SettingMeta meta = {})
        : SettingCommon(owner, key, name, normalize(defaultValue), std::move(meta)) {}

    void set(int32_t newValue) {
        detail::SettingCommon<ColorSetting, int32_t>::set(normalize(newValue));
    }

    void buildValue(TitanNativeRecords::Value& out) const override {
        out = {};
        out.type = TitanNativeRecords::ValueType::integer;
        out.intValue = normalize(value_);
    }

    void serialize(TitanNativeRecords::Setting& out) const override {
        out = {};
        fillMeta(out);
        out.value.type = TitanNativeRecords::ValueType::integer;
        out.value.intValue = normalize(value_);
        out.defaultValue.type = TitanNativeRecords::ValueType::integer;
        out.defaultValue.intValue = normalize(defaultValue_);
        out.controlType = TitanNativeRecords::ControlType::color;
    }

    std::string apply(const TitanNativeRecords::Value& v) override {
        if (v.type != TitanNativeRecords::ValueType::integer)
            return "Expected integer value";
        value_ = normalize(v.intValue);
        return {};
    }

private:
    static int32_t normalize(int32_t value) {
        return value & 0x00FFFFFF;
    }
};

// ---------------------------------------------------------------------------
// Combo setting (integer value with labeled options)
// ---------------------------------------------------------------------------

struct ComboChoice {
    int32_t value = 0;
    std::string label;
};

class ComboSetting : public detail::SettingCommon<ComboSetting, int32_t> {
public:
    ComboSetting(Plugin* owner, const char* key, const char* name,
                 int32_t defaultValue, std::vector<ComboChoice> choices,
                 SettingMeta meta = {})
        : SettingCommon(owner, key, name, defaultValue, std::move(meta)),
          choices_(std::move(choices)) {}

    const std::vector<ComboChoice>& choices() const { return choices_; }

    void buildValue(TitanNativeRecords::Value& out) const override {
        out = {};
        out.type = TitanNativeRecords::ValueType::integer;
        out.intValue = value_;
    }

    void serialize(TitanNativeRecords::Setting& out) const override {
        out = {};
        fillMeta(out);
        out.value.type = TitanNativeRecords::ValueType::integer;
        out.value.intValue = value_;
        out.defaultValue.type = TitanNativeRecords::ValueType::integer;
        out.defaultValue.intValue = defaultValue_;
        out.controlType = TitanNativeRecords::ControlType::combo;
        const size_t n = (std::min)(choices_.size(),
            static_cast<size_t>(TitanNativeRecords::kMaxSettingOptions));
        out.optionCount = static_cast<uint8_t>(n);
        for (size_t i = 0; i < n; ++i) {
            out.options[i].intValue = choices_[i].value;
            detail::copyFixed(out.options[i].label, sizeof(out.options[i].label),
                              choices_[i].label);
        }
    }

    std::string apply(const TitanNativeRecords::Value& v) override {
        if (v.type != TitanNativeRecords::ValueType::integer)
            return "Expected integer value";
        // Accept any integer — the UI guarantees it's a valid option.
        value_ = v.intValue;
        return {};
    }

private:
    std::vector<ComboChoice> choices_;
};

// ---------------------------------------------------------------------------
// String setting (text input)
// ---------------------------------------------------------------------------

class StringSetting : public detail::SettingCommon<StringSetting, std::string> {
public:
    StringSetting(Plugin* owner, const char* key, const char* name,
                  std::string defaultValue, SettingMeta meta = {})
        : SettingCommon(owner, key, name, std::move(defaultValue), std::move(meta)) {}

    /// Convenience: allow implicit conversion to const char*.
    const char* c_str() const { return value_.c_str(); }

    void buildValue(TitanNativeRecords::Value& out) const override {
        out = {};
        out.type = TitanNativeRecords::ValueType::string;
        detail::copyFixed(out.stringValue, sizeof(out.stringValue), value_);
    }

    void serialize(TitanNativeRecords::Setting& out) const override {
        out = {};
        fillMeta(out);
        out.value.type = TitanNativeRecords::ValueType::string;
        detail::copyFixed(out.value.stringValue, sizeof(out.value.stringValue), value_);
        out.defaultValue.type = TitanNativeRecords::ValueType::string;
        detail::copyFixed(out.defaultValue.stringValue, sizeof(out.defaultValue.stringValue),
                          defaultValue_);
        out.controlType = TitanNativeRecords::ControlType::textInput;
    }

    std::string apply(const TitanNativeRecords::Value& v) override {
        if (v.type != TitanNativeRecords::ValueType::string)
            return "Expected string value";
        value_.assign(v.stringValue);
        return {};
    }
};

// ---------------------------------------------------------------------------
// Protected string setting (password / PIN — DPAPI encrypted at rest)
// ---------------------------------------------------------------------------

class ProtectedStringSetting : public detail::SettingCommon<ProtectedStringSetting, std::string> {
public:
    ProtectedStringSetting(Plugin* owner, const char* key, const char* name,
                           std::string defaultValue, SettingMeta meta = {})
        : SettingCommon(owner, key, name, std::move(defaultValue), std::move(meta)) {}

    const char* c_str() const { return value_.c_str(); }

    void buildValue(TitanNativeRecords::Value& out) const override {
        out = {};
        out.type = TitanNativeRecords::ValueType::string;
        detail::copyFixed(out.stringValue, sizeof(out.stringValue), value_);
    }

    void serialize(TitanNativeRecords::Setting& out) const override {
        out = {};
        fillMeta(out);
        out.value.type = TitanNativeRecords::ValueType::string;
        detail::copyFixed(out.value.stringValue, sizeof(out.value.stringValue), value_);
        out.defaultValue.type = TitanNativeRecords::ValueType::string;
        detail::copyFixed(out.defaultValue.stringValue, sizeof(out.defaultValue.stringValue),
                          defaultValue_);
        out.controlType = TitanNativeRecords::ControlType::protectedText;
    }

    std::string apply(const TitanNativeRecords::Value& v) override {
        if (v.type != TitanNativeRecords::ValueType::string)
            return "Expected string value";
        value_.assign(v.stringValue);
        return {};
    }
};

// ---------------------------------------------------------------------------
// Button setting (value-less action control)
// ---------------------------------------------------------------------------

/// A clickable button in the plugin config UI. Unlike the other settings it
/// holds no persisted value: clicking it invokes @p onClick.
///
/// THREADING(settings/panel): the click is marshalled by the client onto the
/// game thread (`Phase::MainLoop`) before `apply()` runs, so @p onClick always
/// executes on the game thread AND fires in pre-login states (title screen /
/// Jagex launcher). Keep the handler non-blocking -- it runs inside the game's
/// main loop. See PluginRegistry::setSettingValue for the dispatch and
/// client/plugins/game_thread_adapter_call.h for the managed-runtime path.
///
/// @code
///   titan::ButtonSetting resetStats_{
///       this, "resetStats", "Reset stats",
///       [this] { kills_ = 0; },
///       {.section = &actions_, .tooltip = "Zero the kill counter"}};
/// @endcode
class ButtonSetting : public detail::SettingCommon<ButtonSetting, bool> {
public:
    ButtonSetting(Plugin* owner, const char* key, const char* name,
                  std::function<void()> onClick, SettingMeta meta = {})
        : SettingCommon(owner, key, name, /*defaultValue=*/false, std::move(meta)),
          onClick_(std::move(onClick)) {}

    void buildValue(TitanNativeRecords::Value& out) const override {
        out = {};
        out.type = TitanNativeRecords::ValueType::boolean;
        out.boolValue = 0;
    }

    void serialize(TitanNativeRecords::Setting& out) const override {
        out = {};
        fillMeta(out);
        // Placeholder value: buttons carry no state, but the wire Value must
        // still be typed. Use a boolean 0 for both value and default.
        out.value.type = TitanNativeRecords::ValueType::boolean;
        out.value.boolValue = 0;
        out.defaultValue.type = TitanNativeRecords::ValueType::boolean;
        out.defaultValue.boolValue = 0;
        out.controlType = TitanNativeRecords::ControlType::button;
    }

    std::string apply(const TitanNativeRecords::Value& /*v*/) override {
        if (onClick_) onClick_();
        return {};
    }

private:
    std::function<void()> onClick_;
};

// ---------------------------------------------------------------------------
// Checkbox matrix setting (a grid of booleans in one control)
// ---------------------------------------------------------------------------

/// One row of a MatrixSetting.
///
/// A row names the column labels it actually has, so the grid's shape is
/// declared in the same vocabulary the UI renders rather than by position.
/// Columns a row does not name do not exist on it: they render as a blank gap
/// and can never be checked.
struct MatrixRow {
    /// Row label, rendered down the left-hand side.
    std::string label;
    /// Column labels present on this row. Empty means every column.
    std::vector<std::string> cells;
    /// Subset of `cells` that starts checked. Empty means none.
    std::vector<std::string> checked;
};

/// A grid of checkboxes: one row per MatrixRow, one column per column label.
/// Collapses N repetitive BoolSettings -- the "Floor 1..5 x Bridge / Grapple /
/// Brazier / Portal" shape -- into one control and one setting entry instead of N.
///
/// @code
///   titan::Section route_{this, "route", "Route"};
///
///   titan::MatrixSetting obstacles_{
///       this, "obstacles", "Obstacles to handle",
///       /*columns=*/ {"Bridge", "Grapple", "Brazier", "Portal"},
///       /*rows=*/ {
///           {"Floor 1", {"Bridge", "Grapple"}},
///           {"Floor 2", {"Grapple", "Brazier"}},
///           {"Floor 3", {"Grapple", "Brazier", "Portal"}},
///           {"Floor 4", {"Bridge", "Brazier", "Portal"}},
///           {"Floor 5", {"Bridge", "Grapple", "Portal"}, /*checked=*/{"Portal"}},
///       },
///       {.section = &route_, .tooltip = "Per-floor obstacle handling"}};
///
///   if (obstacles_.cell("Floor 3", "Grapple")) { /* ... */ }
///   if (obstacles_.cell(2, 1))                 { /* the same cell */ }
/// @endcode
///
/// The value is one cell bitmask, `bit = row * columnCount() + column`
/// (row-major). cell() / setCell() are the ergonomic accessors; mask() is the
/// raw int when you want to compare or store the grid wholesale.
///
/// LIMITS. `rowCount() * columnCount() <= kMaxCells` (31): the mask is a single
/// int32 and bit 31 is permanently reserved, so the value stays non-negative in
/// Java's signed int and in every JSON number encoder along the way. An
/// oversized declaration is CLAMPED rather than rejected -- trailing ROWS drop
/// first, because the column count is what the bit layout depends on, so every
/// surviving bit keeps addressing the same cell. That matches how the rest of
/// the SDK handles overflow (thunkGetSettings and ComboSetting::serialize both
/// std::min-truncate in silence), but unlike those it is REPORTED:
/// configError() holds the first diagnostic and the first serialize() logs it
/// once. Unknown column names in `cells` / `checked` are reported the same way.
///
/// WIRE. See TitanNativeRecords::ControlType::checkboxMatrix. The grid shape rides
/// in the Setting POD's matrixRows / matrixColumns / matrixAvailable fields and
/// its labels in `options` (every column label, then every row label).
class MatrixSetting : public detail::SettingCommon<MatrixSetting, int32_t> {
public:
    /// Maximum addressable cells (rows * columns). Bit 31 is reserved.
    static constexpr size_t kMaxCells = TitanNativeRecords::kMaxMatrixCells;
    /// Returned by rowIndex() / columnIndex() for an unknown label.
    static constexpr size_t kNoIndex = static_cast<size_t>(-1);

    /// Primary constructor. See the class comment for the worked example.
    MatrixSetting(Plugin* owner, const char* key, const char* name,
                  std::vector<std::string> columnLabels,
                  const std::vector<MatrixRow>& rows, SettingMeta meta = {})
        : SettingCommon(owner, key, name, /*defaultValue=*/0, std::move(meta)),
          columnLabels_(std::move(columnLabels)) {
        for (const auto& row : rows) rowLabels_.push_back(row.label);
        clampShape();
        const int32_t defaults = resolveRows(rows);
        defaultValue_ = defaults;
        value_ = defaults;
    }

    /// Explicit-mask constructor, for grids computed at runtime rather than
    /// written out by hand. Both masks use `bit = row * columnLabels.size() +
    /// column`. @p defaultMask is intersected with @p availableMask, and both
    /// are intersected with the (possibly clamped) grid.
    MatrixSetting(Plugin* owner, const char* key, const char* name,
                  std::vector<std::string> columnLabels,
                  std::vector<std::string> rowLabels, int32_t availableMask,
                  int32_t defaultMask, SettingMeta meta = {})
        : SettingCommon(owner, key, name, /*defaultValue=*/0, std::move(meta)),
          columnLabels_(std::move(columnLabels)),
          rowLabels_(std::move(rowLabels)) {
        clampShape();
        const int32_t grid = gridMask();
        if ((availableMask & ~grid) != 0) {
            noteError("availability mask has bits outside the grid");
        }
        availableMask_ = availableMask & grid;
        defaultValue_ = defaultMask & availableMask_;
        value_ = defaultValue_;
    }

    // --- Shape ---
    size_t rowCount() const { return rowLabels_.size(); }
    size_t columnCount() const { return columnLabels_.size(); }
    const std::vector<std::string>& rowLabels() const { return rowLabels_; }
    const std::vector<std::string>& columnLabels() const { return columnLabels_; }
    const char* rowLabel(size_t row) const {
        return row < rowLabels_.size() ? rowLabels_[row].c_str() : "";
    }
    const char* columnLabel(size_t col) const {
        return col < columnLabels_.size() ? columnLabels_[col].c_str() : "";
    }
    /// Index of a row label, or kNoIndex when it is not part of this grid.
    size_t rowIndex(const std::string& label) const {
        return indexOf(rowLabels_, label);
    }
    /// Index of a column label, or kNoIndex when it is not part of this grid.
    size_t columnIndex(const std::string& label) const {
        return indexOf(columnLabels_, label);
    }

    /// Single-bit mask for (row, column); 0 when the pair is off-grid.
    int32_t bit(size_t row, size_t col) const {
        if (row >= rowLabels_.size() || col >= columnLabels_.size()) return 0;
        const size_t idx = row * columnLabels_.size() + col;
        if (idx >= kMaxCells) return 0;
        return static_cast<int32_t>(static_cast<uint32_t>(1) << idx);
    }

    // --- Cells ---
    /// True when the cell exists and is interactive.
    bool isAvailable(size_t row, size_t col) const {
        const int32_t b = bit(row, col);
        return b != 0 && (availableMask_ & b) != 0;
    }
    bool isAvailable(const std::string& row, const std::string& col) const {
        return isAvailable(rowIndex(row), columnIndex(col));
    }

    /// State of one cell. Unavailable and off-grid cells always read false.
    bool cell(size_t row, size_t col) const {
        const int32_t b = bit(row, col);
        return b != 0 && (value_ & b) != 0;
    }
    bool cell(const std::string& row, const std::string& col) const {
        return cell(rowIndex(row), columnIndex(col));
    }

    /// Set one cell and fire onSettingChanged. Unavailable and off-grid cells
    /// are ignored, so a caller can never desync value_ from availableMask_.
    void setCell(size_t row, size_t col, bool on) {
        const int32_t b = bit(row, col);
        if (b == 0 || (availableMask_ & b) == 0) return;
        set(on ? (value_ | b) : (value_ & ~b));
    }
    void setCell(const std::string& row, const std::string& col, bool on) {
        setCell(rowIndex(row), columnIndex(col), on);
    }

    /// Flip one cell and return its NEW state. False also means "nothing
    /// happened" for an off-grid or unavailable cell, matching JS
    /// `matrixSetting.toggle` and Java `MatrixSetting.toggle`.
    bool toggleCell(size_t row, size_t col) {
        const int32_t b = bit(row, col);
        if (b == 0 || (availableMask_ & b) == 0) return false;
        const bool next = (value_ & b) == 0;
        set(next ? (value_ | b) : (value_ & ~b));
        return next;
    }
    bool toggleCell(const std::string& row, const std::string& col) {
        return toggleCell(rowIndex(row), columnIndex(col));
    }

    /// Row-major copy of the whole grid, `rowCount` x `columnCount`.
    /// Unavailable cells read false. Matches JS `matrixSetting.toGrid()`
    /// and Java `MatrixSetting.toGrid()`.
    ///
    /// This allocates, so hoist it out of per-tick loops; `cell(r, c)` and
    /// `mask()` read the same state without allocating.
    std::vector<std::vector<bool>> grid() const {
        std::vector<std::vector<bool>> out;
        out.reserve(rowLabels_.size());
        for (size_t r = 0; r < rowLabels_.size(); ++r) {
            std::vector<bool> row;
            row.reserve(columnLabels_.size());
            for (size_t c = 0; c < columnLabels_.size(); ++c) {
                row.push_back(cell(r, c));
            }
            out.push_back(std::move(row));
        }
        return out;
    }

    /// Raw cell bitmask. Invariant: `mask() & ~availableMask() == 0`.
    int32_t mask() const { return value_; }
    int32_t availableMask() const { return availableMask_; }
    int32_t defaultMask() const { return defaultValue_; }

    /// One row's bits, shifted down so bit 0 is that row's first column.
    /// `rowMask(r) != 0` answers "does this row want anything at all?".
    int32_t rowMask(size_t row) const {
        if (row >= rowLabels_.size() || columnLabels_.empty()) return 0;
        const size_t cols = columnLabels_.size();
        const size_t shift = row * cols;
        if (shift >= kMaxCells) return 0;
        const uint32_t keep = cols >= 31
            ? 0x7FFFFFFFu
            : ((static_cast<uint32_t>(1) << cols) - 1u);
        return static_cast<int32_t>((static_cast<uint32_t>(value_) >> shift) & keep);
    }

    /// Replace the whole mask. Hides SettingCommon::set so every write path
    /// sanitizes, exactly as ColorSetting hides it to normalize.
    void set(int32_t newMask) {
        detail::SettingCommon<MatrixSetting, int32_t>::set(sanitize(newMask));
    }

    /// Empty when the declaration was well-formed; otherwise the first problem
    /// found while resolving and clamping it. Also logged once on the first
    /// serialize(). Useful in plugin self-tests.
    const std::string& configError() const { return configError_; }

    void buildValue(TitanNativeRecords::Value& out) const override {
        out = {};
        out.type = TitanNativeRecords::ValueType::integer;
        out.intValue = sanitize(value_);
    }

    void serialize(TitanNativeRecords::Setting& out) const override {
        out = {};
        fillMeta(out);
        out.value.type = TitanNativeRecords::ValueType::integer;
        out.value.intValue = sanitize(value_);
        out.defaultValue.type = TitanNativeRecords::ValueType::integer;
        out.defaultValue.intValue = defaultValue_;
        out.controlType = TitanNativeRecords::ControlType::checkboxMatrix;
        // Columns FIRST: the bit layout depends only on the column count, so if
        // the shared options array is ever truncated downstream, whole trailing
        // rows fall off and every surviving bit keeps its meaning.
        const size_t maxOpts = static_cast<size_t>(TitanNativeRecords::kMaxSettingOptions);
        const size_t cols = (std::min)(columnLabels_.size(), maxOpts);
        const size_t rows = (std::min)(rowLabels_.size(), maxOpts - cols);
        out.matrixColumns = static_cast<uint8_t>(cols);
        out.matrixRows = static_cast<uint8_t>(rows);
        out.matrixAvailable = static_cast<uint32_t>(availableMask_);
        size_t n = 0;
        for (size_t c = 0; c < cols; ++c, ++n) {
            out.options[n].intValue = static_cast<int32_t>(c);
            detail::copyFixed(out.options[n].label, sizeof(out.options[n].label),
                              columnLabels_[c]);
        }
        for (size_t r = 0; r < rows; ++r, ++n) {
            out.options[n].intValue = static_cast<int32_t>(r);
            detail::copyFixed(out.options[n].label, sizeof(out.options[n].label),
                              rowLabels_[r]);
        }
        out.optionCount = static_cast<uint8_t>(n);
        reportConfigErrorOnce();
    }

    std::string apply(const TitanNativeRecords::Value& v) override {
        if (v.type != TitanNativeRecords::ValueType::integer)
            return "Expected integer value";
        // Mask, do not reject. Three reasons:
        //  1. The controller replays every persisted value on plugin load. A
        //     grid whose shape changed between plugin versions would otherwise
        //     resurrect bits for cells the user can no longer see or clear --
        //     a setting stuck on with no UI to turn it off.
        //  2. A controller that predates checkboxMatrix can send back arbitrary
        //     ints, including negatives (i.e. bit 31 set).
        //  3. Returning a non-empty error here fails thunkSetSetting, which
        //     fails the IPC setSettingValue, which ABORTS the controller's
        //     whole apply-saved-state loop for this target -- one stale int
        //     would stop every later setting and the enable handshake.
        // Cost is a single AND, and `mask() & ~availableMask() == 0` is the
        // invariant cell(), setCell() and the renderer all rely on.
        value_ = sanitize(v.intValue);
        return {};
    }

private:
    static size_t indexOf(const std::vector<std::string>& labels,
                          const std::string& label) {
        for (size_t i = 0; i < labels.size(); ++i) {
            if (labels[i] == label) return i;
        }
        return kNoIndex;
    }

    int32_t sanitize(int32_t raw) const { return raw & availableMask_; }

    /// Every cell in the (already clamped) grid, ignoring availability.
    int32_t gridMask() const {
        const size_t cells = rowLabels_.size() * columnLabels_.size();
        if (cells == 0) return 0;
        const size_t n = cells < kMaxCells ? cells : kMaxCells;
        return static_cast<int32_t>((static_cast<uint32_t>(1) << n) - 1u);
    }

    void noteError(const std::string& msg) {
        ++errorCount_;
        if (configError_.empty()) configError_ = msg;
    }

    /// Trim the declared shape to what one Setting POD can carry. Rows go
    /// first: the bit layout depends only on the column count, so dropping
    /// trailing rows leaves every surviving bit addressing the same cell.
    void clampShape() {
        const size_t maxLabels = static_cast<size_t>(TitanNativeRecords::kMaxSettingOptions);
        if (rowLabels_.empty() || columnLabels_.empty()) {
            noteError("a matrix needs at least one row and one column label");
            rowLabels_.clear();
            columnLabels_.clear();
            return;
        }
        for (size_t i = 1; i < columnLabels_.size(); ++i) {
            if (indexOf(columnLabels_, columnLabels_[i]) != i) {
                noteError("duplicate column label '" + columnLabels_[i] + "'");
                break;
            }
        }
        for (size_t i = 1; i < rowLabels_.size(); ++i) {
            if (indexOf(rowLabels_, rowLabels_[i]) != i) {
                noteError("duplicate row label '" + rowLabels_[i] + "'");
                break;
            }
        }
        if (columnLabels_.size() > maxLabels - 1) {
            noteError("column count " + std::to_string(columnLabels_.size()) +
                      " clamped to " + std::to_string(maxLabels - 1));
            columnLabels_.resize(maxLabels - 1);
        }
        const size_t byLabels = maxLabels - columnLabels_.size();
        const size_t byCells = kMaxCells / columnLabels_.size();
        const size_t maxRows = byLabels < byCells ? byLabels : byCells;
        if (rowLabels_.size() > maxRows) {
            noteError("row count " + std::to_string(rowLabels_.size()) +
                      " clamped to " + std::to_string(maxRows) +
                      " (rows * columns <= " + std::to_string(kMaxCells) + ")");
            rowLabels_.resize(maxRows);
        }
    }

    /// Fill availableMask_ from the declared rows; return the default-ON mask.
    /// Every `cells` / `checked` entry is resolved against columnLabels_, so a
    /// misspelled column name is reported instead of silently shifting a cell.
    int32_t resolveRows(const std::vector<MatrixRow>& rows) {
        availableMask_ = 0;
        int32_t defaults = 0;
        if (rowLabels_.empty() || columnLabels_.empty()) return 0;
        for (size_t r = 0; r < rowLabels_.size() && r < rows.size(); ++r) {
            const MatrixRow& row = rows[r];
            int32_t rowAvailable = 0;
            if (row.cells.empty()) {
                // An unqualified row has every column.
                for (size_t c = 0; c < columnLabels_.size(); ++c) {
                    rowAvailable |= bit(r, c);
                }
            } else {
                for (const auto& name : row.cells) {
                    const size_t c = columnIndex(name);
                    if (c == kNoIndex) {
                        noteError("row '" + row.label + "' names unknown column '" +
                                  name + "'");
                        continue;
                    }
                    rowAvailable |= bit(r, c);
                }
            }
            availableMask_ |= rowAvailable;
            for (const auto& name : row.checked) {
                const size_t c = columnIndex(name);
                if (c == kNoIndex) {
                    noteError("row '" + row.label + "' checks unknown column '" +
                              name + "'");
                    continue;
                }
                const int32_t b = bit(r, c);
                if ((rowAvailable & b) == 0) {
                    noteError("row '" + row.label + "' checks column '" + name +
                              "', which it does not have");
                    continue;
                }
                defaults |= b;
            }
        }
        return defaults;
    }

    /// One-shot diagnostic, deferred to serialize() on purpose: a setting is a
    /// Plugin member initializer, so its constructor runs before the host binds
    /// detail::backend() -- logging from there would silently go nowhere.
    void reportConfigErrorOnce() const {
        if (configError_.empty() || reported_) return;
        reported_ = true;
        if (auto* b = detail::backend()) {
            const std::string msg =
                "[titan] MatrixSetting '" + key_ + "': " + configError_ + " (" +
                std::to_string(errorCount_) + " issue(s); serialized shape " +
                std::to_string(rowLabels_.size()) + "x" +
                std::to_string(columnLabels_.size()) + ")";
            b->log(msg.c_str());
        }
    }

    std::vector<std::string> columnLabels_;
    std::vector<std::string> rowLabels_;
    int32_t availableMask_ = 0;
    std::string configError_;
    size_t errorCount_ = 0;
    mutable bool reported_ = false;
};

}  // namespace titan
