/// @file titan/utils/inventory.h
/// @brief Plugin-side inventory state, query, and action helpers.
///
/// Header-only inline wrappers around existing SDK primitives:
///   - `titan::queries::inventory()` for live slot enumeration; the
///     query already routes through `HostApi::getInventoryItems`, so
///     every helper below is a per-tick snapshot.
///   - `titan::state::widgets().find((149<<16)|0)` for the open-state
///     check (mirrors `Inventory::isOpen()` in
///     `client/actions/inventory.cpp` which calls `WidgetReader::
///     isVisible` on the same packed id).
///   - `Item::interact("Drop")` for drop dispatch -- matches the
///     two-tier "Drop" / "Destroy" resolver used by the RuneLite-side
///     `InventoryUtils::dropItem(int)`.
///
/// Mirrors the `Inventory::*` namespace in `client/actions/inventory.h`.
/// Client-internal code uses the in-process helpers directly; plugins
/// use this header. No new ABI surface -- everything composes existing
/// primitives.
///
/// Java reference: `theplug.utils.core.api.InventoryUtils` /
/// RuneLite's `Inventory` static utility -- the read surface
/// (`getAllItems`, `findItem(s)*`, `count*`, `containsItem*`), the
/// state predicates (`isFull`, `isEmpty`, `getEmptySlots`, `isOpen`),
/// and the `dropItem(int)` action.
///
/// Added in SDK 41.

#pragma once

#include "../actor.h"
#include "../client.h"
#include "../query.h"

#include <cctype>
#include <cstdint>
#include <initializer_list>
#include <optional>
#include <string>
#include <string_view>
#include <vector>

namespace titan {
namespace utils {
namespace Inventory {

/// Standard inventory capacity. The widget at packed id `(149<<16)|0`
/// is hard-coded to 28 slots in the engine.
constexpr int32_t kCapacity = 28;

/// Packed widget id of the inventory tab (`group=149, child=0`).
constexpr uint32_t kInventoryPackedId = (149u << 16) | 0u;

namespace detail {

/// Case-insensitive substring search. Mirrors the `containsIgnoreCase`
/// semantics used in `client/actions/inventory.cpp` and the equivalent
/// helper in `<titan/utils/equipment.h>`.
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

}  // namespace detail

// ---------------------------------------------------------------------------
// State predicates
// ---------------------------------------------------------------------------

/// @return true when the inventory widget is currently visible (the
/// inventory tab is selected and not occluded by another panel).
/// Reads the same packed widget id as the client-internal
/// `Inventory::isOpen()` helper.
inline bool isOpen() {
    auto w = ::titan::state::widgets().find(kInventoryPackedId);
    return w && w->visible();
}

/// @return Number of occupied slots. Empty slots and the client's null
/// placeholder item id 6512 are filtered out by the host before the
/// snapshot is materialised, so this is just the item-vector length.
inline int32_t size() {
    return static_cast<int32_t>(::titan::queries::inventory().count());
}

/// @return Number of free slots (`kCapacity - size()`).
inline int32_t emptySlots() { return kCapacity - size(); }

/// @return true when every slot is occupied.
inline bool isFull() { return size() >= kCapacity; }

/// @return true when no slot is occupied.
inline bool isEmpty() { return size() == 0; }

// ---------------------------------------------------------------------------
// Reading inventory state
// ---------------------------------------------------------------------------

/// Snapshot every occupied slot. Empty slots and placeholder id 6512 are
/// filtered out by the host.
inline std::vector<::titan::Item> getAll() {
    return ::titan::queries::inventory().toVector();
}

/// @return The first slot whose item id matches @p itemId, or
///         `std::nullopt` when nothing matches.
inline std::optional<::titan::Item> find(int32_t itemId) {
    auto items = getAll();
    for (auto& it : items) {
        if (it.id() == itemId) return it;
    }
    return std::nullopt;
}

/// @return The first slot whose display name contains @p name
///         (case-insensitive substring), or `std::nullopt`.
inline std::optional<::titan::Item> find(std::string_view name) {
    auto items = getAll();
    for (auto& it : items) {
        if (detail::icontains(it.name(), name)) return it;
    }
    return std::nullopt;
}

/// @return The item occupying slot @p slot, or `std::nullopt` when the
///         slot is empty or out of range.
inline std::optional<::titan::Item> getSlot(int32_t slot) {
    if (slot < 0 || slot >= kCapacity) return std::nullopt;
    auto items = getAll();
    for (auto& it : items) {
        if (it.slot() == slot) return it;
    }
    return std::nullopt;
}

/// @return Every slot whose item id appears in @p ids, in slot-order
///         (not @p ids order).
inline std::vector<::titan::Item> getByIds(std::initializer_list<int32_t> ids) {
    std::vector<::titan::Item> out;
    for (auto& it : getAll()) {
        for (int32_t wantedId : ids) {
            if (it.id() == wantedId) {
                out.push_back(it);
                break;
            }
        }
    }
    return out;
}

/// @return Every slot whose display name contains any needle in
///         @p names (case-insensitive substring).
inline std::vector<::titan::Item> getByNames(
        std::initializer_list<std::string_view> names) {
    std::vector<::titan::Item> out;
    for (auto& it : getAll()) {
        for (auto needle : names) {
            if (detail::icontains(it.name(), needle)) {
                out.push_back(it);
                break;
            }
        }
    }
    return out;
}

// ---------------------------------------------------------------------------
// Counting and predicates
// ---------------------------------------------------------------------------

/// @return true when @p itemId occupies at least one slot.
inline bool contains(int32_t itemId) {
    return find(itemId).has_value();
}

/// @return true when @p itemId occupies a slot AND that slot's
///         quantity is at least @p minQuantity. Useful for stackable
///         items where the bare contains check would accept a stack
///         of one.
inline bool contains(int32_t itemId, int32_t minQuantity) {
    int32_t total = 0;
    for (auto& it : getAll()) {
        if (it.id() == itemId) {
            total += it.quantity();
            if (total >= minQuantity) return true;
        }
    }
    return false;
}

/// @return true when any slot's display name contains @p name
///         (case-insensitive substring).
inline bool contains(std::string_view name) {
    return find(name).has_value();
}

/// @return Combined quantity of every slot whose item id matches
///         @p itemId. Stackable items naturally collapse into a single
///         slot; non-stackables sum to the slot count.
inline int32_t count(int32_t itemId) {
    int32_t total = 0;
    for (auto& it : getAll()) {
        if (it.id() == itemId) total += it.quantity();
    }
    return total;
}

/// @return Combined quantity of every slot whose item id appears in
///         @p ids.
inline int32_t count(std::initializer_list<int32_t> ids) {
    int32_t total = 0;
    for (auto& it : getAll()) {
        for (int32_t wantedId : ids) {
            if (it.id() == wantedId) {
                total += it.quantity();
                break;
            }
        }
    }
    return total;
}

/// @return true when at least one id in @p ids occupies a slot.
inline bool containsAny(std::initializer_list<int32_t> ids) {
    for (int32_t id : ids) {
        if (contains(id)) return true;
    }
    return false;
}

/// @return true when every id in @p ids occupies at least one slot.
inline bool containsAll(std::initializer_list<int32_t> ids) {
    for (int32_t id : ids) {
        if (!contains(id)) return false;
    }
    return true;
}

/// @return true when at least one needle in @p names matches a slot's
///         display name (case-insensitive substring).
inline bool containsAny(std::initializer_list<std::string_view> names) {
    for (auto needle : names) {
        if (contains(needle)) return true;
    }
    return false;
}

/// @return true when every needle in @p names matches at least one
///         slot's display name.
inline bool containsAll(std::initializer_list<std::string_view> names) {
    for (auto needle : names) {
        if (!contains(needle)) return false;
    }
    return true;
}

// ---------------------------------------------------------------------------
// Actions
// ---------------------------------------------------------------------------

/// Drop the first slot whose item id matches @p itemId. Resolves the
/// runtime "Drop" / "Destroy" action label for the item via the
/// internal two-tier resolver (so destroy-only items like dragon items
/// also work). No-op when the item is not present.
/// @return true when a drop click was queued.
inline bool drop(int32_t itemId) {
    auto item = find(itemId);
    if (!item) return false;
    return item->interact("Drop");
}

/// Drop the first slot whose display name contains @p name
/// (case-insensitive substring).
inline bool drop(std::string_view name) {
    auto item = find(name);
    if (!item) return false;
    return item->interact("Drop");
}

}  // namespace Inventory
}  // namespace utils
}  // namespace titan
