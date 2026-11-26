#pragma once

#include <cstddef>
#include <cstdint>
#include <algorithm>
#include <iterator>
#include <limits>
#include <optional>
#include <stdexcept>
#include <string>
#include <string_view>

namespace titan::settingUnits {

// Numeric suffix metadata is single-line display text, never a format string.
// The native ABI transports it in a 64-byte option label; owned IPC records
// and managed runtimes can carry the full 256-byte limit.
inline bool valid(std::string_view text, size_t maxBytes = 256) {
    if (text.size() > maxBytes) return false;
    for (size_t at = 0; at < text.size();) {
        const auto first = static_cast<unsigned char>(text[at++]);
        uint32_t code = first;
        unsigned tail = 0;
        if (first >= 0xc2 && first <= 0xdf) { code = first & 31; tail = 1; }
        else if (first >= 0xe0 && first <= 0xef) { code = first & 15; tail = 2; }
        else if (first >= 0xf0 && first <= 0xf4) { code = first & 7; tail = 3; }
        else if (first >= 0x80) return false;
        const unsigned count = tail;
        if (at + tail > text.size()) return false;
        while (tail--) {
            const auto next = static_cast<unsigned char>(text[at++]);
            if ((next & 0xc0) != 0x80) return false;
            code = (code << 6) | (next & 63);
        }
        if ((count == 1 && code < 0x80) || (count == 2 && code < 0x800)
            || (count == 3 && code < 0x10000) || code > 0x10ffff
            || (code >= 0xd800 && code <= 0xdfff)) return false;
        if (code <= 0x1f || (code >= 0x7f && code <= 0x9f)
            || code == 0x2028 || code == 0x2029 || code == 0x061c
            || code == 0x200e || code == 0x200f || (code >= 0x202a && code <= 0x202e)
            || (code >= 0x2066 && code <= 0x206f)) return false;
    }
    return true;
}

template<class ControlType> constexpr bool numeric(ControlType control) noexcept {
    return control == ControlType::slider || control == ControlType::numberInput
        || control == ControlType::integer64 || control == ControlType::real64;
}

// ABI 1 Setting has no units field. These numeric controls do not use options;
// reserve one tagged option without changing a public record or function table.
// Other controls, including combo/matrix IDs equal to INT32_MIN, are untouched.
template<class Setting> void encodeNative(Setting& setting, std::string_view units) {
    if (units.empty()) return;
    if (!numeric(setting.controlType) || setting.optionCount != 0
        || !valid(units, std::size(setting.options[0].label) - 1))
        throw std::invalid_argument("Numeric setting units exceed the native UTF-8 label or conflict with options");
    auto& option = setting.options[0];
    option.intValue = (std::numeric_limits<int32_t>::min)();
    std::copy(units.begin(), units.end(), std::begin(option.label));
    std::fill(std::begin(option.label) + units.size(), std::end(option.label), '\0');
    setting.optionCount = 1;
}

template<class Setting> bool nativeTagged(const Setting& setting) noexcept {
    return numeric(setting.controlType) && setting.optionCount == 1
        && setting.options[0].intValue == (std::numeric_limits<int32_t>::min)();
}

template<class Setting> std::optional<std::string> decodeNative(const Setting& setting) {
    if (!nativeTagged(setting)) return {};
    const auto& label = setting.options[0].label;
    const auto end = std::find(std::begin(label), std::end(label), '\0');
    // Optional metadata received from an older or custom native plugin is not
    // an authority to discard its numeric setting or abort a whole snapshot.
    if (end == std::end(label)) return {};
    const std::string_view value(std::begin(label), static_cast<size_t>(end - std::begin(label)));
    if (!valid(value, std::size(label) - 1)) return {};
    return std::string(value);
}

} // namespace titan::settingUnits
