/// @file titan/utils/equipment.h
/// @brief Plugin-side equipment query + unequip helpers.
///
/// Header-only inline wrappers around existing SDK primitives:
///   - `titan::state::itemContainer(InventoryID::EQUIPMENT)` (SDK 26)
///     for reads against the authoritative `ClientInvCache` hashtable.
///     Empty slots are filtered out of the snapshot.
///   - `titan::state::itemDef(id)` (SDK 26) for runtime / cache name
///     resolution so name-based queries match the in-game hover label.
///   - `titan::state::widgets().interact(...)` for the unequip dispatch
///     (CC_OP / identifier=1 against the worn-items slot widget).
///
/// Mirrors the `Equipment::*` namespace in `client/actions/equipment.h`.
/// Client-internal code uses the in-process `Widgets::interact` overload
/// directly; plugins use this header. No new ABI surface -- everything
/// composes existing primitives.
///
/// Java reference: `theplug.utils.core.api.EquipmentUtils` -- the read
/// surface (`getEquipment`, `contains*`, `count`, name lookups) plus
/// `unequipItem(int)` which fires `LegacyMenuEntry("Remove", "",
/// identifier=1, opcode=CC_OP(57), p0=-1, p1=<wornitems slot widget>)`.
///
/// Added in SDK 40.

#pragma once

#include "../client.h"
#include "../equipment_slot.h"
#include "../inventory_id.h"
#include "../menu_action.h"

#include <cctype>
#include <cstdint>
#include <initializer_list>
#include <optional>
#include <string>
#include <string_view>
#include <vector>

namespace titan {
namespace utils {
namespace Equipment {

/// One occupied equipment slot. Slot index matches the RuneLite
/// `EquipmentInventorySlot` ordinal.
struct EquippedItem {
    EquipmentSlot slot = EquipmentSlot::HEAD;
    int32_t itemId = -1;
    int32_t quantity = 0;
    /// Display name as the in-game menu builder shows it (varbit/varp
    /// transforms applied when the runtime ItemDef path is available).
    /// Empty when the item id was not resolvable.
    std::string name;

    bool isValid() const { return itemId > 0; }
};

namespace detail {

/// Case-insensitive substring search used by the name-based query
/// helpers. Matches the `containsIgnoreCase` semantics used in
/// `client/actions/inventory.cpp`.
inline bool icontains(std::string_view haystack, std::string_view needle) {
    if (needle.empty()) return true;
    if (needle.size() > haystack.size()) return false;
    auto lower = [](unsigned char c) {
        return static_cast<char>(std::tolower(c));
    };
    for (std::size_t i = 0; i + needle.size() <= haystack.size(); ++i) {
        std::size_t j = 0;
        for (; j < needle.size(); ++j) {
            if (lower(static_cast<unsigned char>(haystack[i + j])) !=
                lower(static_cast<unsigned char>(needle[j]))) break;
        }
        if (j == needle.size()) return true;
    }
    return false;
}

/// Resolve the runtime / cache-backed display name for @p itemId.
/// Returns an empty string when neither path produced a definition.
inline std::string resolveName(int32_t itemId) {
    if (itemId <= 0) return {};
    auto def = ::titan::state::itemDef(itemId);
    if (!def) return {};
    return def->name;
}

/// Build an `EquippedItem` from a single container slot snapshot.
/// Filters out empty slots (`itemId <= 0`) and any slot ordinal past
/// the documented `kSlotCount` (newer revisions append tail slots like
/// the Dizana's quiver that this surface intentionally ignores -- they
/// have no `EquipmentSlot` ordinal mapping).
inline std::optional<EquippedItem> makeItem(const ::titan::ItemContainerSlot& slot) {
    if (slot.itemId <= 0) return std::nullopt;
    if (!::titan::EquipmentSlotInfo::isValid(slot.slot)) return std::nullopt;
    EquippedItem out;
    out.slot = ::titan::EquipmentSlotInfo::fromOrdinal(slot.slot);
    out.itemId = slot.itemId;
    out.quantity = (slot.quantity > 0) ? slot.quantity : 1;
    out.name = resolveName(slot.itemId);
    return out;
}

inline bool dispatchUnequipSlot(EquipmentSlot slot) {
    const uint32_t widgetPacked = ::titan::EquipmentSlotInfo::slotWidgetPackedId(slot);
    if (widgetPacked == 0u) return false;
    return ::titan::state::widgets().interact(
        static_cast<uint32_t>(::titan::MenuAction::Id::CcOp),
        /*identifier=*/1,
        /*param0=*/-1,
        /*param1 (widget packed id)=*/static_cast<int32_t>(widgetPacked));
}

}  // namespace detail

// ---------------------------------------------------------------------------
// Reading equipment state
// ---------------------------------------------------------------------------

/// Snapshot every occupied equipment slot. Empty when the equipment
/// container hasn't been populated on this revision (analyzer didn't
/// detect the layout, or the player has yet to receive an UPDATE_INV
/// for the EQUIPMENT id this session).
inline std::vector<EquippedItem> getAll() {
    std::vector<EquippedItem> out;
    auto snap = ::titan::state::itemContainer(
        static_cast<int32_t>(::titan::InventoryID::EQUIPMENT));
    if (!snap) return out;
    out.reserve(snap->items.size());
    for (const auto& s : snap->items) {
        if (auto eq = detail::makeItem(s)) out.push_back(std::move(*eq));
    }
    return out;
}

/// @return The item occupying @p slot, or `std::nullopt` when empty.
inline std::optional<EquippedItem> find(EquipmentSlot slot) {
    for (auto& item : getAll()) {
        if (item.slot == slot) return item;
    }
    return std::nullopt;
}

/// @return The first equipped item whose id matches @p itemId.
inline std::optional<EquippedItem> find(int32_t itemId) {
    for (auto& item : getAll()) {
        if (item.itemId == itemId) return item;
    }
    return std::nullopt;
}

/// @return The first equipped item whose display name contains @p name
///         (case-insensitive substring match).
inline std::optional<EquippedItem> find(std::string_view name) {
    for (auto& item : getAll()) {
        if (detail::icontains(item.name, name)) return item;
    }
    return std::nullopt;
}

/// @return Every equipped item whose id appears in @p ids. Order
///         matches the slot ordering, not @p ids.
inline std::vector<EquippedItem> getByIds(std::initializer_list<int32_t> ids) {
    std::vector<EquippedItem> out;
    for (auto& item : getAll()) {
        for (int32_t id : ids) {
            if (item.itemId == id) {
                out.push_back(item);
                break;
            }
        }
    }
    return out;
}

/// @return Every equipped item whose display name contains any needle in
///         @p names (case-insensitive substring).
inline std::vector<EquippedItem> getByNames(
        std::initializer_list<std::string_view> names) {
    std::vector<EquippedItem> out;
    for (auto& item : getAll()) {
        for (auto needle : names) {
            if (detail::icontains(item.name, needle)) {
                out.push_back(item);
                break;
            }
        }
    }
    return out;
}

// ---------------------------------------------------------------------------
// Counting and queries
// ---------------------------------------------------------------------------

/// @return true when @p itemId is currently equipped.
inline bool contains(int32_t itemId) {
    return find(itemId).has_value();
}

/// @return true when @p itemId is equipped with at least @p minQuantity
///         charges / arrows / etc. Useful for ammo / charged items.
inline bool contains(int32_t itemId, int32_t minQuantity) {
    auto item = find(itemId);
    return item && item->quantity >= minQuantity;
}

/// @return true when any equipped item's name contains @p name
///         (case-insensitive substring).
inline bool contains(std::string_view name) {
    return find(name).has_value();
}

/// @return Combined quantity of every equipped slot whose id matches
///         @p itemId. Useful for ammo stacks split across the AMMO and
///         WEAPON slots (e.g. ranged + ammo combos).
inline int32_t count(int32_t itemId) {
    int32_t total = 0;
    for (auto& item : getAll()) {
        if (item.itemId == itemId) total += item.quantity;
    }
    return total;
}

/// @return Combined quantity of every equipped slot whose id appears in
///         @p ids.
inline int32_t count(std::initializer_list<int32_t> ids) {
    int32_t total = 0;
    for (auto& item : getAll()) {
        for (int32_t id : ids) {
            if (item.itemId == id) {
                total += item.quantity;
                break;
            }
        }
    }
    return total;
}

/// @return true when any id in @p ids is currently equipped.
inline bool containsAny(std::initializer_list<int32_t> ids) {
    for (int32_t id : ids) {
        if (contains(id)) return true;
    }
    return false;
}

/// @return true when every id in @p ids is currently equipped.
inline bool containsAll(std::initializer_list<int32_t> ids) {
    for (int32_t id : ids) {
        if (!contains(id)) return false;
    }
    return true;
}

/// @return true when at least one needle in @p names matches an
///         equipped item's display name (case-insensitive substring).
inline bool containsAny(std::initializer_list<std::string_view> names) {
    for (auto needle : names) {
        if (contains(needle)) return true;
    }
    return false;
}

/// @return true when every needle in @p names matches at least one
///         equipped item's display name.
inline bool containsAll(std::initializer_list<std::string_view> names) {
    for (auto needle : names) {
        if (!contains(needle)) return false;
    }
    return true;
}

// ---------------------------------------------------------------------------
// Unequip
// ---------------------------------------------------------------------------

/// Remove the item currently equipped in @p slot. No-op (returns false)
/// when the slot has no clickable widget on the equipment screen
/// (ARMS / HAIR / JAW) or when the slot is empty.
///
/// Reproduces the Java `LegacyMenuEntry` shape from EquipmentUtils:
///   `{option="Remove", target="", identifier=1, opcode=CC_OP(57),
///     p0=-1, p1=<wornitems slot packed widget id>}`.
///
/// @return true when the remove click was accepted / queued. The equipment
///         container updates later, usually on a subsequent game tick; observe
///         `onItemContainerChanged` to confirm state changed.
inline bool unequip(EquipmentSlot slot) {
    auto item = find(slot);
    if (!item) return false;
    return detail::dispatchUnequipSlot(slot);
}

/// Find @p itemId in the equipment container and remove it. Equivalent
/// to `unequip(find(itemId)->slot)` with a single read.
inline bool unequip(int32_t itemId) {
    auto item = find(itemId);
    if (!item) return false;
    return detail::dispatchUnequipSlot(item->slot);
}

/// Find the first equipped item whose name contains @p name and remove
/// it.
inline bool unequip(std::string_view name) {
    auto item = find(name);
    if (!item) return false;
    return detail::dispatchUnequipSlot(item->slot);
}

}  // namespace Equipment
}  // namespace utils
}  // namespace titan
