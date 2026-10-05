/// @file titan/definition_extras.h
/// @brief Optional widget, item-action and NPC definition metadata.
#pragma once

#include "detail/backend.h"

#include <array>
#include <cstdint>
#include <optional>
#include <string>
#include <vector>

namespace titan::definitions {

/// Spawned NPC definition before varbit/varp transformations. Game thread only.
/// Supply the snapshot's worldViewId and hashIndex; missing NPC/capability is
/// nullopt. A negative worldViewId selects the current live world view.
inline std::optional<int32_t> npcBaseId(int32_t worldViewId, int32_t hashIndex) {
    auto* backend = detail::backend();
    if (!backend || hashIndex < 0) return std::nullopt;
    int32_t baseId = -1;
    return backend->getNpcBaseId(worldViewId, hashIndex, &baseId) && baseId >= 0
        ? std::optional<int32_t>{baseId} : std::nullopt;
}

/// Read a root widget or an exact dynamic-child path on the game thread.
/// nullopt means unavailable; a populated -1 means the widget has no model.
inline std::optional<int32_t> widgetModelId(
        const TitanPluginSdk::WidgetAddressState& address) {
    auto* backend = detail::backend();
    if (!backend || address.depth > TitanPluginSdk::kMaxWidgetAddressDepth) return std::nullopt;
    for (uint32_t index = 0; index < address.depth; ++index) {
        if (address.slots[index] < 0) return std::nullopt;
    }
    int32_t modelId = -1;
    return backend->getWidgetModelIdAtPath(&address, &modelId)
        ? std::optional<int32_t>{modelId} : std::nullopt;
}

/// Cache definition params 451..458 in their original eight-row order.
/// These are distinct from runtime equipment sub-operations. Empty known rows
/// remain empty strings; nullopt means the host capability/item is unavailable.
/// Owns the returned strings and never truncates an action label.
inline std::optional<std::array<std::string, 8>> itemWornActions(int32_t itemId) {
    auto* backend = detail::backend();
    if (!backend || itemId < 0) return std::nullopt;
    std::array<std::string, 8> result;
    for (uint32_t index = 0; index < result.size(); ++index) {
        uint32_t required = 0;
        // Bound allocation independently of an optional provider's response.
        constexpr uint32_t maxLabelBytes = 64u * 1024u;
        if (!backend->copyItemWornAction(itemId, index, nullptr, 0, &required)
            || required == 0 || required > maxLabelBytes) return std::nullopt;
        std::vector<char> buffer(required, '\0');
        uint32_t copied = 0;
        if (!backend->copyItemWornAction(itemId, index, buffer.data(), required, &copied)
            || copied == 0 || copied > required || buffer[copied - 1] != '\0') {
            return std::nullopt;
        }
        result[index].assign(buffer.data(), copied - 1);
    }
    return result;
}

} // namespace titan::definitions
