/// @file titan/detail/native_records.h
/// @brief Frozen metadata and UI records for native ABI 1.
///
/// These records belong to the DLL contract. They deliberately do not include
/// or alias the controller IPC protocol. Never grow a record, an embedded
/// array, or a string capacity here: the host and plugin exchange arrays of
/// these records using their frozen sizeof as the element stride. Introduce
/// a separately negotiated interface and new record types for new layouts.
/// Existing enum values and field semantics must also remain stable.
///
/// C++ facades construct these records inside the plugin. The client translates
/// them field by field to its independently evolving controller protocol.
#pragma once

#include <cstddef>
#include <cstdint>
#include <cstring>
#include <type_traits>
#include <vector>

namespace TitanNativeRecords {

constexpr uint32_t kMaxSectionsPerPlugin = 16;
constexpr uint32_t kMaxSettingOptions = 32;
constexpr uint32_t kMaxMatrixCells = 31;
constexpr uint32_t kMaxPanelsPerPlugin = 8;
constexpr uint32_t kMaxPanelIconLen = 64;
constexpr uint32_t kMaxPanelIconBytes = 256 * 1024;
constexpr uint32_t kMaxIdLen = 32;
constexpr uint32_t kMaxNameLen = 64;
constexpr uint32_t kMaxDescriptionLen = 256;
constexpr uint32_t kMaxAuthorLen = 64;
constexpr uint32_t kMaxVersionLen = 16;
constexpr uint32_t kMaxStringLen = 4096;
constexpr uint32_t kMaxErrorLen = 160;
constexpr uint32_t kMaxLogMessage = 256;
constexpr uint32_t kMaxPanelTextLen = 128;
constexpr uint32_t kMaxPanelSecondaryLen = 128;
constexpr uint32_t kMaxPanelElements = 8192;

enum class ValueType : uint32_t {
    boolean = 1,
    integer = 2,
    string = 3
};
enum class ControlType : uint8_t {
    autoDetect = 0,
    checkbox = 1,
    slider = 2,
    combo = 3,
    textInput = 4,
    protectedText = 5,
    color = 6,
    button = 7,
    checkboxMatrix = 8,
    numberInput = 9
};

struct Value {
    ValueType type = ValueType::boolean;
    int32_t intValue = 0;
    uint8_t boolValue = 0;
    char stringValue[kMaxStringLen] = {};
};

struct SettingOption {
    int32_t intValue = 0;
    char label[kMaxNameLen] = {};
};
struct Setting {
    char key[kMaxIdLen] = {};
    char name[kMaxNameLen] = {};
    char section[kMaxIdLen] = {};
    char tooltip[kMaxStringLen] = {};
    Value value = {};
    Value defaultValue = {};
    ControlType controlType = ControlType::autoDetect;
    uint8_t optionCount = 0;
    uint8_t hidden = 0;
    uint8_t padding[2] = {};
    int32_t position = 0;
    int32_t minValue = 0;
    int32_t maxValue = 0;
    SettingOption options[kMaxSettingOptions] = {};
    uint32_t matrixAvailable = 0;
    uint8_t matrixRows = 0;
    uint8_t matrixColumns = 0;
    uint8_t matrixPadding[2] = {};
};
inline bool normalizeMatrixSetting(Setting& setting, int32_t rows, int32_t columns,
                                   int32_t available) {
    const int32_t optionCount = static_cast<int32_t>(setting.optionCount);
    const bool wellFormed =
        rows > 0 && columns > 0 && rows <= 0xFF && columns <= 0xFF &&
        optionCount == rows + columns &&
        static_cast<uint32_t>(rows) * static_cast<uint32_t>(columns) <= kMaxMatrixCells;
    if (!wellFormed) {
        setting.matrixRows = 0;
        setting.matrixColumns = 0;
        setting.matrixAvailable = 0;
        setting.optionCount = 0;
        setting.value.intValue = 0;
        setting.defaultValue.intValue = 0;
        return false;
    }
    const uint32_t cells = static_cast<uint32_t>(rows) * static_cast<uint32_t>(columns);
    const uint32_t cellMask = (1u << cells) - 1u;
    setting.matrixRows = static_cast<uint8_t>(rows);
    setting.matrixColumns = static_cast<uint8_t>(columns);
    setting.matrixAvailable = static_cast<uint32_t>(available) & cellMask;
    setting.value.intValue &= static_cast<int32_t>(setting.matrixAvailable);
    setting.defaultValue.intValue &= static_cast<int32_t>(setting.matrixAvailable);
    return true;
}
struct Section {
    char key[kMaxIdLen] = {};
    char name[kMaxNameLen] = {};
    char description[kMaxStringLen] = {};
    int32_t position = 0;
    uint8_t closedByDefault = 0;
    uint8_t parentIndex = 0;
    uint8_t padding[2] = {};
};
static_assert(sizeof(Section) == kMaxIdLen + kMaxNameLen + kMaxStringLen + 8,
              "getSections() fills a host-allocated Section[], so its stride is ABI");
static_assert(offsetof(Section, parentIndex) == offsetof(Section, closedByDefault) + 1,
              "parentIndex must stay in the bytes that were padding before SDK 143");
static_assert(kMaxSectionsPerPlugin <= 255, "parentIndex is one byte");
inline uint8_t sectionParentIndex(const Section* sections, size_t count, size_t self,
                                  const char* parentKey) {
    if (!sections || !parentKey || !parentKey[0]) return 0;
    char wanted[kMaxIdLen] = {};
    for (size_t n = 0; n + 1 < kMaxIdLen && parentKey[n]; ++n) wanted[n] = parentKey[n];
    for (size_t i = 0; i < count && i < 255; ++i) {
        if (i != self && std::strncmp(sections[i].key, wanted, kMaxIdLen) == 0) {
            return static_cast<uint8_t>(i + 1);
        }
    }
    return 0;
}
inline uint8_t checkedSectionParent(const Section* sections, size_t count, size_t self) {
    if (!sections || self >= count) return 0;
    const auto link = [&](size_t at) -> size_t {  // 0 = no usable parent
        const size_t parent = sections[at].parentIndex;
        return parent > count || parent == at + 1 ? 0 : parent;
    };
    const size_t parent = link(self);
    if (parent == 0) return 0;
    size_t at = self;
    for (size_t step = 0; step < count && link(at) != 0; ++step) {
        at = link(at) - 1u;
        if (at == self) return 0;
    }
    return static_cast<uint8_t>(parent);
}
inline void sanitizeSectionParents(Section* sections, size_t count) {
    if (!sections) return;
    std::vector<uint8_t> checked(count, 0);
    for (size_t i = 0; i < count; ++i) checked[i] = checkedSectionParent(sections, count, i);
    for (size_t i = 0; i < count; ++i) sections[i].parentIndex = checked[i];
}
struct PanelDescriptor {
    char id[kMaxIdLen] = {};
    char title[kMaxNameLen] = {};
    char icon[kMaxPanelIconLen] = {};
    uint32_t iconColor = 0;
    uint8_t hasImageIcon = 0;
    uint8_t padding[3] = {};
};

enum class PanelElementType : uint8_t {
    text             = 0,
    textWrapped      = 1,
    textColored      = 2,
    textDisabled     = 3,
    bulletText       = 4,
    labelText        = 5,
    separator        = 10,
    separatorText    = 11,
    spacing          = 12,
    sameLine         = 13,
    newLine          = 14,
    indent           = 15,
    unindent         = 16,
    dummy            = 17,
    button           = 20,
    smallButton      = 21,
    selectable       = 30,
    treeNode         = 40,
    treePop          = 41,
    collapsingHeader = 42,
    beginTable       = 50,
    endTable         = 51,
    tableNextRow     = 52,
    tableNextColumn  = 53,
    tableSetupColumn = 54,
    tableHeadersRow  = 55,
    checkbox         = 60,
    sliderInt        = 61,
    sliderFloat      = 62,
    inputText        = 63,
    combo            = 64,
    proxyCombo       = 65,
    progressBar      = 70,
    beginTabBar      = 80,
    endTabBar        = 81,
    beginTabItem     = 82,
    endTabItem       = 83,
    beginGroup       = 90,
    endGroup         = 91,
    beginChild       = 92,
    endChild         = 93,
    pushStyleColor   = 100,
    popStyleColor    = 101,
    setTooltip       = 110,
    alignBegin       = 111,
    alignEnd         = 112,
    beginDisabled    = 113,
    endDisabled      = 114,
    helpMarker       = 115,
    badge            = 116,
};
enum PanelChildFlag : int32_t {
    kPanelChildBorder      = 1 << 0,
    kPanelChildPadding     = 1 << 1,
    kPanelChildAutoResizeY = 1 << 2,
    kPanelChildFrame       = 1 << 3,
};
struct PanelElement {
    PanelElementType type = PanelElementType::text;
    uint8_t  padding1[3]  = {};
    char     text[kMaxPanelTextLen]              = {};
    char     textSecondary[kMaxPanelSecondaryLen] = {};
    uint32_t color       = 0xFFFFFFFF;
    int32_t  actionId    = -1;
    int32_t  intVal      = 0;
    int32_t  intVal2     = 0;
    int32_t  intVal3     = 0;
    float    floatVal    = 0.0f;
    float    floatVal2   = 0.0f;
    float    floatVal3   = 0.0f;
    float    widthVal    = 0.0f;
    float    heightVal   = 0.0f;
    uint8_t  boolVal     = 0;
    uint8_t  padding2[3] = {};
};

// Every size, alignment, and offset is part of native ABI 1, including array stride.
static_assert(std::is_standard_layout_v<Value> && std::is_trivially_copyable_v<Value>);
static_assert(sizeof(Value) == 4108 && alignof(Value) == 4);
static_assert(offsetof(Value, type) == 0);
static_assert(offsetof(Value, intValue) == 4);
static_assert(offsetof(Value, boolValue) == 8);
static_assert(offsetof(Value, stringValue) == 9);
static_assert(std::is_standard_layout_v<SettingOption> && std::is_trivially_copyable_v<SettingOption>);
static_assert(sizeof(SettingOption) == 68 && alignof(SettingOption) == 4);
static_assert(offsetof(SettingOption, intValue) == 0);
static_assert(offsetof(SettingOption, label) == 4);
static_assert(std::is_standard_layout_v<Setting> && std::is_trivially_copyable_v<Setting>);
static_assert(sizeof(Setting) == 14644 && alignof(Setting) == 4);
static_assert(offsetof(Setting, key) == 0);
static_assert(offsetof(Setting, name) == 32);
static_assert(offsetof(Setting, section) == 96);
static_assert(offsetof(Setting, tooltip) == 128);
static_assert(offsetof(Setting, value) == 4224);
static_assert(offsetof(Setting, defaultValue) == 8332);
static_assert(offsetof(Setting, controlType) == 12440);
static_assert(offsetof(Setting, optionCount) == 12441);
static_assert(offsetof(Setting, hidden) == 12442);
static_assert(offsetof(Setting, padding) == 12443);
static_assert(offsetof(Setting, position) == 12448);
static_assert(offsetof(Setting, minValue) == 12452);
static_assert(offsetof(Setting, maxValue) == 12456);
static_assert(offsetof(Setting, options) == 12460);
static_assert(offsetof(Setting, matrixAvailable) == 14636);
static_assert(offsetof(Setting, matrixRows) == 14640);
static_assert(offsetof(Setting, matrixColumns) == 14641);
static_assert(offsetof(Setting, matrixPadding) == 14642);
static_assert(std::is_standard_layout_v<Section> && std::is_trivially_copyable_v<Section>);
static_assert(sizeof(Section) == 4200 && alignof(Section) == 4);
static_assert(offsetof(Section, key) == 0);
static_assert(offsetof(Section, name) == 32);
static_assert(offsetof(Section, description) == 96);
static_assert(offsetof(Section, position) == 4192);
static_assert(offsetof(Section, closedByDefault) == 4196);
static_assert(offsetof(Section, parentIndex) == 4197);
static_assert(offsetof(Section, padding) == 4198);
static_assert(std::is_standard_layout_v<PanelDescriptor> && std::is_trivially_copyable_v<PanelDescriptor>);
static_assert(sizeof(PanelDescriptor) == 168 && alignof(PanelDescriptor) == 4);
static_assert(offsetof(PanelDescriptor, id) == 0);
static_assert(offsetof(PanelDescriptor, title) == 32);
static_assert(offsetof(PanelDescriptor, icon) == 96);
static_assert(offsetof(PanelDescriptor, iconColor) == 160);
static_assert(offsetof(PanelDescriptor, hasImageIcon) == 164);
static_assert(offsetof(PanelDescriptor, padding) == 165);
static_assert(std::is_standard_layout_v<PanelElement> && std::is_trivially_copyable_v<PanelElement>);
static_assert(sizeof(PanelElement) == 304 && alignof(PanelElement) == 4);
static_assert(offsetof(PanelElement, type) == 0);
static_assert(offsetof(PanelElement, padding1) == 1);
static_assert(offsetof(PanelElement, text) == 4);
static_assert(offsetof(PanelElement, textSecondary) == 132);
static_assert(offsetof(PanelElement, color) == 260);
static_assert(offsetof(PanelElement, actionId) == 264);
static_assert(offsetof(PanelElement, intVal) == 268);
static_assert(offsetof(PanelElement, intVal2) == 272);
static_assert(offsetof(PanelElement, intVal3) == 276);
static_assert(offsetof(PanelElement, floatVal) == 280);
static_assert(offsetof(PanelElement, floatVal2) == 284);
static_assert(offsetof(PanelElement, floatVal3) == 288);
static_assert(offsetof(PanelElement, widthVal) == 292);
static_assert(offsetof(PanelElement, heightVal) == 296);
static_assert(offsetof(PanelElement, boolVal) == 300);
static_assert(offsetof(PanelElement, padding2) == 301);

} // namespace TitanNativeRecords
