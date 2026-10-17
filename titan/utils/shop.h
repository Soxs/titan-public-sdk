/// @file titan/utils/shop.h
/// @brief Plugin-side shop state and action helpers.
///
/// Header-only wrappers around `titan::state::widgets()` for the shared shop
/// interface: `InterfaceID::Shopmain` (the stock grid) and
/// `InterfaceID::Shopside` (the inventory panel shown beside it). Every shop
/// NPC opens the same interface. Mirrors `<titan/utils/deposit_box.h>`.
///
/// Buying and selling fire the slot's own component operation (CC_OP) for the
/// largest quantity button that does not exceed the request -- Buy/Sell 50,
/// 10, 5 or 1 -- once per call, so a caller repeats until it holds what it
/// needs. Like the bank helpers, actions report queue acceptance, not
/// gameplay success. The close X is a component operation on a dynamic child
/// of the frame (the same frame script the bank and deposit box use).
///
/// Added in SDK 153.

#pragma once

#include "../client.h"
#include "../generated/gamevals/interface_id.h"
#include "../menu_action.h"

#include <cstdint>
#include <optional>
#include <string>
#include <vector>

namespace titan {
namespace utils {
namespace Shop {

/// One stocked slot of the shop's grid. `quantity` is 0 for an item the shop
/// lists but is out of.
struct ShopItem {
    int32_t itemId = -1;
    int32_t quantity = 0;
    int32_t slot = -1;   ///< dynamic child slot of `Shopmain::ITEMS`
};

namespace detail {

constexpr uint32_t kCcOp = static_cast<uint32_t>(::titan::MenuAction::Id::CcOp);

/// Dynamic child slot of `Shopmain::FRAME` that carries the close X.
constexpr int32_t kCloseChildSlot = 11;

/// CC_OP identifiers on a `Shopmain::ITEMS` slot (Buy) or a
/// `Shopside::ITEMS` slot (Sell): op 1 is Value, ops 2-5 are 1/5/10/50.
constexpr int32_t kQuantity1Op = 2;
constexpr int32_t kQuantity5Op = 3;
constexpr int32_t kQuantity10Op = 4;
constexpr int32_t kQuantity50Op = 5;

/// Placeholder item id the client shows in empty inventory slots.
constexpr int32_t kEmptySlotItemId = 6512;

inline int32_t quantityOp(int32_t quantity) {
    if (quantity >= 50) return kQuantity50Op;
    if (quantity >= 10) return kQuantity10Op;
    if (quantity >= 5) return kQuantity5Op;
    return kQuantity1Op;
}

inline bool hasItem(int32_t itemId) {
    return itemId >= 0 && itemId != kEmptySlotItemId;
}

inline std::string lower(std::string s) {
    for (char& c : s) {
        if (c >= 'A' && c <= 'Z') c = static_cast<char>(c + ('a' - 'A'));
    }
    return s;
}

/// Case-insensitive substring match against the item's cache name (noted
/// forms carry their unnoted item's name).
inline bool nameContains(int32_t itemId, const std::string& needleLower) {
    if (needleLower.empty() || !hasItem(itemId)) return false;
    const auto def = ::titan::state::cache().item(itemId);
    return def && lower(def->name).find(needleLower) != std::string::npos;
}

/// Slot of the first `Shopside::ITEMS` child matching @p match, or -1.
template <typename Match>
int32_t inventorySlot(const Match& match) {
    const auto children = ::titan::state::widgets().children(
        static_cast<uint32_t>(::titan::gamevals::InterfaceID::Shopside::ITEMS));
    for (size_t i = 0; i < children.size(); ++i) {
        if (match(children[i].itemId())) return static_cast<int32_t>(i);
    }
    return -1;
}

inline bool sellSlot(int32_t slot, int32_t quantity) {
    if (slot < 0) return false;
    return ::titan::state::widgets().interact(
        kCcOp, quantityOp(quantity), slot, ::titan::gamevals::InterfaceID::Shopside::ITEMS);
}

} // namespace detail

// -----------------------------------------------------------------------
// State reads
// -----------------------------------------------------------------------

/// True while a shop interface is open (its stock grid is visible).
inline bool isOpen() {
    const auto widget = ::titan::state::widgets().find(
        static_cast<uint32_t>(::titan::gamevals::InterfaceID::Shopmain::ITEMS));
    return widget && widget->visible();
}

/// Every stocked slot, in grid order. Empty when the shop is closed.
inline std::vector<ShopItem> items() {
    std::vector<ShopItem> out;
    if (!isOpen()) return out;
    const auto children = ::titan::state::widgets().children(
        static_cast<uint32_t>(::titan::gamevals::InterfaceID::Shopmain::ITEMS));
    for (size_t i = 0; i < children.size(); ++i) {
        if (!detail::hasItem(children[i].itemId())) continue;
        out.push_back(ShopItem{children[i].itemId(), children[i].itemQuantity(),
                               static_cast<int32_t>(i)});
    }
    return out;
}

/// Grid slot (the CC_OP `param0`) of @p itemId, or nullopt when the shop is
/// closed or does not list it.
inline std::optional<int32_t> slotOf(int32_t itemId) {
    for (const auto& item : items()) {
        if (item.itemId == itemId) return item.slot;
    }
    return std::nullopt;
}

/// The shop's stock of @p itemId; 0 when closed, unlisted or sold out.
inline int32_t stock(int32_t itemId) {
    for (const auto& item : items()) {
        if (item.itemId == itemId) return item.quantity;
    }
    return 0;
}

/// True when the shop has at least @p quantity of @p itemId in stock.
inline bool has(int32_t itemId, int32_t quantity = 1) {
    return stock(itemId) >= quantity;
}

/// The item's general store value (cache `ItemDef::storeValue`); 0 when the
/// definition is unknown. Shops scale their actual price from this by stock.
inline int32_t storeValue(int32_t itemId) {
    const auto def = ::titan::state::cache().item(itemId);
    return def ? def->storeValue : 0;
}

/// True while the shop is open and an inventory item's name contains
/// @p name (case-insensitive).
inline bool hasItemToSell(const std::string& name) {
    if (!isOpen()) return false;
    const std::string needle = detail::lower(name);
    return detail::inventorySlot([&](int32_t itemId) {
        return detail::nameContains(itemId, needle);
    }) >= 0;
}

// -----------------------------------------------------------------------
// Actions (queue acceptance, not gameplay success)
// -----------------------------------------------------------------------

/// Buy @p itemId with the largest quantity button not above @p quantity
/// (50, 10, 5 or 1). False when the shop is closed or holds fewer than
/// @p quantity.
inline bool buy(int32_t itemId, int32_t quantity) {
    if (!isOpen() || quantity <= 0 || !has(itemId, quantity)) return false;
    const auto slot = slotOf(itemId);
    if (!slot) return false;
    return ::titan::state::widgets().interact(
        detail::kCcOp, detail::quantityOp(quantity), *slot,
        ::titan::gamevals::InterfaceID::Shopmain::ITEMS);
}

/// Sell the first inventory slot holding @p itemId, with the largest
/// quantity button not above @p quantity (50, 10, 5 or 1).
inline bool sell(int32_t itemId, int32_t quantity) {
    if (!isOpen() || quantity <= 0 || !detail::hasItem(itemId)) return false;
    return detail::sellSlot(
        detail::inventorySlot([&](int32_t id) { return id == itemId; }), quantity);
}

/// Sell the first inventory item whose name contains @p name
/// (case-insensitive), with the largest quantity button not above @p quantity.
inline bool sellByName(const std::string& name, int32_t quantity) {
    if (!isOpen() || quantity <= 0) return false;
    const std::string needle = detail::lower(name);
    return detail::sellSlot(
        detail::inventorySlot([&](int32_t itemId) {
            return detail::nameContains(itemId, needle);
        }),
        quantity);
}

/// Close the shop through its frame's close X; true immediately when no shop
/// is open.
inline bool close() {
    if (!isOpen()) return true;
    return ::titan::state::widgets().interact(
        detail::kCcOp, 1, detail::kCloseChildSlot,
        ::titan::gamevals::InterfaceID::Shopmain::FRAME);
}

} // namespace Shop
} // namespace utils
} // namespace titan
