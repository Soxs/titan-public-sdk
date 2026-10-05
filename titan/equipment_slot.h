/// @file titan/equipment_slot.h
/// @brief Equipment slot enum + worn-items widget id catalog.
///
/// Mirrors RuneLite's `net.runelite.api.EquipmentInventorySlot` ordinals 1:1
/// so plugin code can talk about HEAD/CAPE/.../AMMO with readable names. The
/// slot ordinal is also the slot index inside the EQUIPMENT item container
/// (`titan::InventoryID::EQUIPMENT`, container id 94) -- read a snapshot via
/// `titan::state::itemContainer(...)` and index into `items` by `slot`.
///
/// `EquipmentSlotInfo::slotWidgetPackedId(slot)` returns the packed
/// `(group, child)` id of the corresponding widget on the worn-items
/// interface (group 387). Only 11 of the 14 slot ordinals are reachable
/// on the equipment screen -- ARMS / HAIR / JAW (6 / 8 / 11) have no
/// widget and the helper returns `0` for them.
///
/// The constants here are version-stable engine values, not per-revision
/// offsets. RuneLite re-exports them from `InterfaceID.Wornitems.SLOT*`;
/// see `shared/titan/utils/equipment.h` for the unequip dispatch that
/// uses them.
///
/// Added in SDK 40 alongside the equipment utility helpers.

#pragma once

#include <cstdint>

namespace titan {

/// Equipment slot ordinals matching RuneLite's `EquipmentInventorySlot`.
/// Values are stable engine semantics (slot index inside the EQUIPMENT
/// `ItemContainer`).
enum class EquipmentSlot : int32_t {
    HEAD   = 0,
    CAPE   = 1,
    AMULET = 2,
    WEAPON = 3,
    BODY   = 4,
    SHIELD = 5,
    ARMS   = 6,
    LEGS   = 7,
    HAIR   = 8,
    GLOVES = 9,
    BOOTS  = 10,
    JAW    = 11,
    RING   = 12,
    AMMO   = 13,
};

namespace EquipmentSlotInfo {

/// Lowest valid ordinal (HEAD).
constexpr int32_t kMinOrdinal = 0;
/// Highest valid ordinal (AMMO). Inclusive upper bound for slot scans.
constexpr int32_t kMaxOrdinal = 13;
/// Total slot count (14). The EQUIPMENT container's capacity is at least
/// this; older revisions sometimes report 14, newer ones add tail slots
/// (Dizana's quiver, etc.) past AMMO.
constexpr int32_t kSlotCount = 14;

/// Worn-items interface group id. Equivalent to `InterfaceID.Wornitems`
/// in RuneLite (= 387).
constexpr uint32_t kWornItemsGroup = 387u;

/// @return Display name for @p slot ("HEAD" .. "AMMO"), or `nullptr` for
///         out-of-range ordinals.
inline const char* name(EquipmentSlot slot) {
    switch (slot) {
        case EquipmentSlot::HEAD:   return "HEAD";
        case EquipmentSlot::CAPE:   return "CAPE";
        case EquipmentSlot::AMULET: return "AMULET";
        case EquipmentSlot::WEAPON: return "WEAPON";
        case EquipmentSlot::BODY:   return "BODY";
        case EquipmentSlot::SHIELD: return "SHIELD";
        case EquipmentSlot::ARMS:   return "ARMS";
        case EquipmentSlot::LEGS:   return "LEGS";
        case EquipmentSlot::HAIR:   return "HAIR";
        case EquipmentSlot::GLOVES: return "GLOVES";
        case EquipmentSlot::BOOTS:  return "BOOTS";
        case EquipmentSlot::JAW:    return "JAW";
        case EquipmentSlot::RING:   return "RING";
        case EquipmentSlot::AMMO:   return "AMMO";
    }
    return nullptr;
}

/// @return Packed widget id `(group << 16) | child` for the worn-items
///         slot widget that fronts @p slot, or `0` when the slot has no
///         on-screen widget (ARMS / HAIR / JAW, ordinals 6 / 8 / 11).
///
/// Child indices come from RuneLite's `InterfaceID.Wornitems.SLOT*`:
/// SLOT0..SLOT5 map to children 15..20, SLOT7 to 21, SLOT9 to 22,
/// SLOT10 to 23, SLOT12 to 24, SLOT13 to 25. Ordinals 6 / 8 / 11 are
/// "logical-only" slots (kit composition layers) with no clickable
/// equipment widget.
inline uint32_t slotWidgetPackedId(EquipmentSlot slot) {
    constexpr auto pack = [](uint32_t child) -> uint32_t {
        return (kWornItemsGroup << 16) | child;
    };
    switch (slot) {
        case EquipmentSlot::HEAD:   return pack(15);
        case EquipmentSlot::CAPE:   return pack(16);
        case EquipmentSlot::AMULET: return pack(17);
        case EquipmentSlot::WEAPON: return pack(18);
        case EquipmentSlot::BODY:   return pack(19);
        case EquipmentSlot::SHIELD: return pack(20);
        case EquipmentSlot::LEGS:   return pack(21);
        case EquipmentSlot::GLOVES: return pack(22);
        case EquipmentSlot::BOOTS:  return pack(23);
        case EquipmentSlot::RING:   return pack(24);
        case EquipmentSlot::AMMO:   return pack(25);
        case EquipmentSlot::ARMS:
        case EquipmentSlot::HAIR:
        case EquipmentSlot::JAW:
        default:
            return 0u;
    }
}

/// @return true when @p ordinal is in `[kMinOrdinal, kMaxOrdinal]`.
inline bool isValid(int32_t ordinal) {
    return ordinal >= kMinOrdinal && ordinal <= kMaxOrdinal;
}

/// Cast a raw slot index from an `ItemContainer` snapshot to the typed
/// enum. Out-of-range indices map to `EquipmentSlot::HEAD`; callers
/// should guard with `isValid()` first when the source isn't trusted.
inline EquipmentSlot fromOrdinal(int32_t ordinal) {
    if (!isValid(ordinal)) return EquipmentSlot::HEAD;
    return static_cast<EquipmentSlot>(ordinal);
}

}  // namespace EquipmentSlotInfo

}  // namespace titan
