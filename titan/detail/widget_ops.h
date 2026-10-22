/// @file titan/detail/widget_ops.h
/// @brief Size-query copies of a widget's op base and op strings (SDK 159).
///
/// The write* helpers are the host side of HostApi::copyWidgetName and
/// copyWidgetActions; the read* helpers decode them for the C++ facade and
/// the host's JS and Java bridges. `copy` wraps copyWidgetName (out,
/// capacity, required) or copyWidgetActions (out, capacity, required, count)
/// for one widget address and returns a WidgetOpsStatus.
#pragma once

#include "abi.h"

#include <cstddef>
#include <cstdint>
#include <cstring>
#include <optional>
#include <string>
#include <vector>

namespace titan::detail {

/// Upper bound on one name/action copy; the host itself bounds each string.
constexpr uint32_t kMaxWidgetOpsCopyBytes = 1u << 20;

/// Host side of copyWidgetName: *required is the size including the NUL, and
/// the text is written only when `capacity` holds it.
inline uint8_t writeWidgetName(const std::string& name, char* out, uint32_t capacity,
                               uint32_t* required) {
    using Status = TitanPluginSdk::WidgetOpsStatus;
    if (!required) return static_cast<uint8_t>(Status::Unavailable);
    *required = 0;
    if (name.size() >= kMaxWidgetOpsCopyBytes) return static_cast<uint8_t>(Status::Unavailable);
    *required = static_cast<uint32_t>(name.size()) + 1;
    if (out && capacity >= *required) {
        std::memcpy(out, name.data(), name.size());
        out[name.size()] = '\0';
    }
    return static_cast<uint8_t>(Status::Present);
}

/// Host side of copyWidgetActions: every slot followed by a NUL, written only
/// when `capacity` holds the whole block. Zero slots means no op array.
inline uint8_t writeWidgetActions(const std::vector<std::string>& slots, char* out,
                                  uint32_t capacity, uint32_t* required, uint32_t* count) {
    using Status = TitanPluginSdk::WidgetOpsStatus;
    if (!required || !count) return static_cast<uint8_t>(Status::Unavailable);
    *required = 0;
    *count = 0;
    size_t total = 0;
    for (const auto& slot : slots) {
        total += slot.size() + 1;
        if (total > kMaxWidgetOpsCopyBytes) return static_cast<uint8_t>(Status::Unavailable);
    }
    *required = static_cast<uint32_t>(total);
    *count = static_cast<uint32_t>(slots.size());
    if (out && capacity >= total) {
        for (const auto& slot : slots) {
            std::memcpy(out, slot.data(), slot.size());
            out += slot.size();
            *out++ = '\0';
        }
    }
    return static_cast<uint8_t>(Status::Present);
}

/// The live widget can change between the sizing and copying calls, so a
/// grown block is re-queried a few times before giving up.
template <typename Copy>
inline bool copyWidgetOpsBlock(Copy copy, std::vector<char>& buffer, uint32_t& required) {
    buffer.clear();
    for (int attempt = 0; attempt < 4; ++attempt) {
        const uint32_t capacity = static_cast<uint32_t>(buffer.size());
        required = 0;
        if (copy(buffer.empty() ? nullptr : buffer.data(), capacity, &required)
                != static_cast<uint8_t>(TitanPluginSdk::WidgetOpsStatus::Present)
            || required > kMaxWidgetOpsCopyBytes) {
            return false;
        }
        if (required <= capacity) return true;
        buffer.assign(required, '\0');
    }
    return false;
}

/// nullopt when the host cannot read the name; empty when the widget has none.
template <typename Copy>
inline std::optional<std::string> readWidgetName(Copy copy) {
    std::vector<char> buffer;
    uint32_t required = 0;
    if (!copyWidgetOpsBlock(copy, buffer, required) || required == 0
            || buffer.size() < required || buffer[required - 1] != '\0') {
        return std::nullopt;
    }
    return std::string(buffer.data(), required - 1);
}

/// nullopt when the host cannot read the slots; empty when the widget has no
/// op array; otherwise one string per native slot, empty where unset.
template <typename Copy>
inline std::optional<std::vector<std::string>> readWidgetActions(Copy copy) {
    std::vector<char> buffer;
    uint32_t required = 0, count = 0;
    const bool copied = copyWidgetOpsBlock(
        [&](char* out, uint32_t capacity, uint32_t* size) { return copy(out, capacity, size, &count); },
        buffer, required);
    if (!copied || count > required || buffer.size() < required) return std::nullopt;
    std::vector<std::string> slots;
    slots.reserve(count);
    const char* cursor = buffer.data();
    const char* end = cursor + required;
    while (slots.size() < count) {
        const auto* nul = static_cast<const char*>(
            std::memchr(cursor, '\0', static_cast<size_t>(end - cursor)));
        if (!nul) return std::nullopt;
        slots.emplace_back(cursor, nul);
        cursor = nul + 1;
    }
    if (cursor != end) return std::nullopt;
    return slots;
}

}  // namespace titan::detail
