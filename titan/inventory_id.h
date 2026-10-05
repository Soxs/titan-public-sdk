/// @file titan/inventory_id.h
/// @brief Well-known item-container ids mirroring RuneLite's `InventoryID`.
///
/// Pass one of these to `titan::state::itemContainer(id)` or compare against
/// `ItemContainerChangedEvent::containerId()` in your plugin's
/// `onItemContainerChanged`. Unmapped ids on the current client revision
/// silently return "no container available" from the host read path.
///
/// Added in SDK 26 alongside the item-container surface.

#pragma once

#include <cstdint>

namespace titan {

/// Identifier set for the containers the native client exposes. Values
/// match RuneLite's `InventoryID` constants 1:1 where they overlap.
/// Any id the server has sent an `UPDATE_INV` for works at runtime --
/// the ClientInvCache hashtable is keyed on the same int, so reads for
/// ids outside this enum Just Work. The enum lists the canonical set
/// so plugin code can use readable names for the common containers.
enum class InventoryID : int32_t {
    INVENTORY = 93,
    EQUIPMENT = 94,
    BANK      = 95,
};

/// Returns the identifier name (e.g. "INVENTORY") for the given id, or
/// `nullptr` when the id has no named constant. Used by debug surfaces
/// that want to annotate raw container ids; production plugin code should
/// reference the enum directly. Inline to keep the SDK header-only.
inline const char* inventoryIdName(int32_t id) {
    switch (id) {
        case static_cast<int32_t>(InventoryID::INVENTORY): return "INVENTORY";
        case static_cast<int32_t>(InventoryID::EQUIPMENT): return "EQUIPMENT";
        case static_cast<int32_t>(InventoryID::BANK):      return "BANK";
        default: return nullptr;
    }
}

}  // namespace titan
