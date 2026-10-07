/// @file titan/utils/bank.h
/// @brief Plugin-side bank state, query, action, PIN, and loadout helpers.
///
/// Header-only inline wrappers around existing SDK primitives:
///   - `titan::state::widgets()` for visibility / interaction dispatch
///   - `titan::state::vars()` for varbit reads (noted mode, quantity type)
///   - `titan::state::itemCache()` for bank reads; live containers for actions
///   - `titan::queries::objects()` for nearest-bank lookup
///   - `titan::keyboard::typeString()` for Withdraw-X and PIN input
///
/// Mirrors the pattern of `<titan/utils/inventory.h>` and
/// `<titan/utils/equipment.h>`.  Client-internal code can call these
/// same helpers (they dispatch through InternalBackend).
///
/// Java reference: `theplug.utils.core.api.BankUtils`
///
/// Added in SDK 44.

#pragma once

#include "../bank_sets.h"
#include "../client.h"
#include "../generated/gamevals/interface_id.h"
#include "../inventory_id.h"
#include "../item_cache.h"
#include "../keyboard.h"
#include "../menu_action.h"
#include "../query.h"
#include "../varbits.h"

#include <algorithm>
#include <cctype>
#include <cstdint>
#include <cstring>
#include <functional>
#include <optional>
#include <span>
#include <string>
#include <string_view>
#include <vector>

namespace titan {
namespace utils {
namespace Bank {

// -----------------------------------------------------------------------
// Internal helpers
// -----------------------------------------------------------------------

namespace detail {

inline bool icontains(std::string_view haystack, std::string_view needle) {
    if (needle.empty()) return true;
    if (needle.size() > haystack.size()) return false;
    auto lower = [](unsigned char c) -> char {
        return static_cast<char>(std::tolower(c));
    };
    for (std::size_t i = 0; i + needle.size() <= haystack.size(); ++i) {
        std::size_t j = 0;
        for (; j < needle.size(); ++j) {
            if (lower(static_cast<unsigned char>(haystack[i + j])) !=
                lower(static_cast<unsigned char>(needle[j])))
                break;
        }
        if (j == needle.size()) return true;
    }
    return false;
}

inline int32_t bankQuantityType() {
    return ::titan::state::vars().varbit(::titan::Varbits::BANK_QUANTITY_TYPE);
}

constexpr uint32_t kCcOp    = static_cast<uint32_t>(::titan::MenuAction::Id::CcOp);
constexpr uint32_t kCcOpLow = static_cast<uint32_t>(::titan::MenuAction::Id::CcOpLowPriority);

}  // namespace detail

// -----------------------------------------------------------------------
// State reads
// -----------------------------------------------------------------------

inline bool isOpen() {
    auto w = ::titan::state::widgets().find(::titan::gamevals::InterfaceID::Bankmain::ITEMS);
    return w && w->visible();
}

inline bool isGeOpen() {
    auto w = ::titan::state::widgets().find(
        ::titan::gamevals::InterfaceID::GeOffersSide::ITEMS);
    return w && w->visible();
}

/// True while the chatbox modal input line is up -- the Withdraw-X
/// "Enter amount:" prompt or the bank search's "Enter name:" prompt.
///
/// Structural, not textual: a text scan matches hidden widgets that still
/// hold the previous prompt's text, so it latches true once the first
/// prompt has closed. Java reference: `BankUtils.isSearchOpen`.
inline bool isSearchOpen() {
    auto w = ::titan::state::widgets().find(
        ::titan::gamevals::InterfaceID::Chatbox::MES_TEXT2);
    return w && w->visible();
}

inline bool isNotedMode() {
    return ::titan::state::vars().varbit(::titan::Varbits::BANK_WITHDRAWNOTES) == 1;
}

inline int32_t getBankTab() {
    return ::titan::state::vars().varbit(::titan::Varbits::CURRENT_BANK_TAB);
}

inline bool isMainTabOpen() {
    return getBankTab() == 0;
}

// -----------------------------------------------------------------------
// Bank container queries
// -----------------------------------------------------------------------

/// Prefer a readable open bank, then the C++ host's remembered bank, then empty.
/// A valid empty live bank wins over remembered contents. Closed native
/// containers can outlive a login, so their presence alone is not live evidence.
inline std::vector<::titan::ItemContainerSlot> getAll() {
    if (isOpen()) {
        if (auto snap = ::titan::state::itemContainer(static_cast<int32_t>(::titan::InventoryID::BANK))) {
            auto& items = snap->items;
            items.erase(std::remove_if(items.begin(), items.end(),
                [](const auto& item) { return item.quantity <= 0; }), items.end());
            return items;
        }
    }
    return ::titan::state::itemCache().getBankItems();
}

inline bool contains(int32_t itemId) {
    for (auto& s : getAll()) {
        if (s.itemId == itemId && s.quantity > 0) return true;
    }
    return false;
}

inline bool contains(std::string_view name) {
    for (auto& s : getAll()) {
        auto def = ::titan::state::itemDef(s.itemId);
        if (s.quantity > 0 && def && detail::icontains(def->name, name)) return true;
    }
    return false;
}

inline bool contains(int32_t itemId, int32_t minQty) {
    int32_t total = 0;
    for (auto& s : getAll()) {
        if (s.itemId == itemId) {
            total += s.quantity;
            if (total >= minQty) return true;
        }
    }
    return false;
}

inline int32_t count(int32_t itemId) {
    int32_t total = 0;
    for (auto& s : getAll()) {
        if (s.itemId == itemId) total += s.quantity;
    }
    return total;
}

inline int32_t count(std::string_view name) {
    int32_t total = 0;
    for (auto& s : getAll()) {
        auto def = ::titan::state::itemDef(s.itemId);
        if (def && detail::icontains(def->name, name))
            total += s.quantity;
    }
    return total;
}

inline std::optional<::titan::ItemContainerSlot> find(int32_t itemId) {
    for (auto& s : getAll()) {
        if (s.itemId == itemId && s.quantity > 0) return s;
    }
    return std::nullopt;
}

inline std::optional<::titan::ItemContainerSlot> find(std::string_view name) {
    for (auto& s : getAll()) {
        auto def = ::titan::state::itemDef(s.itemId);
        if (s.quantity > 0 && def && detail::icontains(def->name, name)) return s;
    }
    return std::nullopt;
}

// -----------------------------------------------------------------------
// Actions
// -----------------------------------------------------------------------

inline bool close() {
    return ::titan::state::widgets().interact(
        detail::kCcOp, 1, 11, ::titan::gamevals::InterfaceID::Bankmain::FRAME);
}

inline bool setNotedMode(bool noted) {
    if (isNotedMode() == noted) return true;
    return ::titan::state::widgets().interact(
        detail::kCcOp, 1, -1, ::titan::gamevals::InterfaceID::Bankmain::POTIONSTORE_CONTAINER);
}

inline bool depositAll() {
    return ::titan::state::widgets().interact(
        detail::kCcOp, 1, -1, ::titan::gamevals::InterfaceID::Bankmain::DEPOSITINV);
}

inline bool depositEquipment() {
    return ::titan::state::widgets().interact(
        detail::kCcOp, 1, -1, ::titan::gamevals::InterfaceID::Bankmain::DEPOSITWORN);
}

inline bool depositAllOfSlot(int32_t slot) {
    return ::titan::state::widgets().interact(
        detail::kCcOpLow, 8, slot,
        ::titan::gamevals::InterfaceID::Bankside::ITEMS);
}

inline bool depositOneOfSlot(int32_t slot) {
    int32_t qty = detail::bankQuantityType();
    int32_t id = (qty == 0) ? 2 : 3;
    return ::titan::state::widgets().interact(
        detail::kCcOp, id, slot,
        ::titan::gamevals::InterfaceID::Bankside::ITEMS);
}

inline bool depositAllOfItem(int32_t itemId) {
    auto inv = ::titan::state::itemContainer(
        static_cast<int32_t>(::titan::InventoryID::INVENTORY));
    if (!inv) return false;
    for (auto& item : inv->items) {
        if (item.itemId == itemId) return depositAllOfSlot(item.slot);
    }
    return false;
}

inline bool depositOneOfItem(int32_t itemId) {
    auto inv = ::titan::state::itemContainer(
        static_cast<int32_t>(::titan::InventoryID::INVENTORY));
    if (!inv) return false;
    for (auto& item : inv->items) {
        if (item.itemId == itemId) return depositOneOfSlot(item.slot);
    }
    return false;
}

inline bool depositAllExcept(std::span<const int> keepIds) {
    auto invSnap = ::titan::state::itemContainer(
        static_cast<int32_t>(::titan::InventoryID::INVENTORY));
    if (!invSnap) return false;
    for (auto& item : invSnap->items) {
        bool keep = false;
        for (int kid : keepIds) {
            if (item.itemId == kid) { keep = true; break; }
        }
        if (!keep) return depositAllOfSlot(item.slot);
    }
    return false;
}

// Cached slots are historical data, never withdrawal/action targets.
inline std::optional<::titan::ItemContainerSlot> findLive(int32_t itemId) {
    if (!isOpen()) return {};
    auto bank = ::titan::state::itemContainer(static_cast<int32_t>(::titan::InventoryID::BANK));
    if (bank) for (const auto& item : bank->items)
        if (item.itemId == itemId && item.quantity > 0) return item;
    return {};
}

inline bool withdrawItem(int32_t itemId) {
    auto slot = findLive(itemId);
    if (!slot) return false;
    int32_t qty = detail::bankQuantityType();
    int32_t id = (qty == 0) ? 1 : 2;
    return ::titan::state::widgets().interact(
        detail::kCcOp, id, slot->slot,
        ::titan::gamevals::InterfaceID::Bankmain::ITEMS);
}

inline bool withdrawAllItem(int32_t itemId) {
    auto slot = findLive(itemId);
    if (!slot) return false;
    int32_t qty = detail::bankQuantityType();
    int32_t id = (qty == 4) ? 1 : 7;
    uint32_t op = (qty == 4) ? detail::kCcOp : detail::kCcOpLow;
    return ::titan::state::widgets().interact(
        op, id, slot->slot,
        ::titan::gamevals::InterfaceID::Bankmain::ITEMS);
}

/// Withdraw a specific amount.
/// For amounts 1/5/10: uses the matching menu option.
/// For other amounts: if BANK_REQUESTEDQUANTITY already equals @p amount,
/// clicks the fast path; otherwise clicks Withdraw-X and returns true
/// so the caller can type the amount on the next tick when isSearchOpen().
inline bool withdrawItemAmount(int32_t itemId, int32_t amount) {
    auto slot = findLive(itemId);
    if (!slot) return false;
    int32_t qty = detail::bankQuantityType();

    if (amount == 1) {
        int32_t id = (qty == 0) ? 1 : 2;
        return ::titan::state::widgets().interact(
            detail::kCcOp, id, slot->slot,
            ::titan::gamevals::InterfaceID::Bankmain::ITEMS);
    }
    if (amount == 5) {
        int32_t id = (qty == 1) ? 1 : 3;
        return ::titan::state::widgets().interact(
            detail::kCcOp, id, slot->slot,
            ::titan::gamevals::InterfaceID::Bankmain::ITEMS);
    }
    if (amount == 10) {
        int32_t id = (qty == 2) ? 1 : 4;
        return ::titan::state::widgets().interact(
            detail::kCcOp, id, slot->slot,
            ::titan::gamevals::InterfaceID::Bankmain::ITEMS);
    }

    int32_t lastQty = ::titan::state::vars().varbit(
        ::titan::Varbits::BANK_REQUESTEDQUANTITY);
    if (lastQty == amount) {
        int32_t id = (qty == 3) ? 1 : 5;
        return ::titan::state::widgets().interact(
            detail::kCcOp, id, slot->slot,
            ::titan::gamevals::InterfaceID::Bankmain::ITEMS);
    }

    // Withdraw-X is op 6 under CC_OP -- observed live, and what the Java
    // reference sends (BankUtils.withdrawItemAmount). The op list is fixed
    // (2=1, 3=5, 4=10, 5=lastX, 6=X, 7=All); op 1 is only a duplicate of
    // whichever quantity the bank buttons have selected, so for qty == 3 it is
    // Withdraw-lastX -- the wrong amount, and no prompt.
    return ::titan::state::widgets().interact(
        detail::kCcOp, 6, slot->slot,
        ::titan::gamevals::InterfaceID::Bankmain::ITEMS);
}

inline bool interactItemInBank(int32_t itemId, int32_t identifier = 9) {
    auto slot = findLive(itemId);
    if (!slot) return false;
    return ::titan::state::widgets().interact(
        detail::kCcOp, identifier, slot->slot,
        ::titan::gamevals::InterfaceID::Bankmain::ITEMS);
}

// -----------------------------------------------------------------------
// Opening a bank
// -----------------------------------------------------------------------

inline bool isNearBank(int32_t distance = 15) {
    auto match = ::titan::queries::objects(distance)
        .where([](const ::titan::TileObject& o) {
            return ::titan::BankSets::isBankObject(o.id());
        })
        .first();
    return match.has_value();
}

inline bool open() {
    auto objs = ::titan::queries::objects(15)
        .where([](const ::titan::TileObject& o) {
            return ::titan::BankSets::isBankObject(o.id());
        })
        .toVector();
    if (!objs.empty()) {
        auto lp = ::titan::state::client().localPlayer();
        if (lp) {
            auto lpTile = lp->tile();
            std::sort(objs.begin(), objs.end(),
                [&lpTile](const ::titan::TileObject& a, const ::titan::TileObject& b) {
                    auto da = std::abs(a.tileX() - lpTile.x) + std::abs(a.tileY() - lpTile.y);
                    auto db = std::abs(b.tileX() - lpTile.x) + std::abs(b.tileY() - lpTile.y);
                    return da < db;
                });
        }
        if (objs[0].interact("Bank")) return true;
        if (objs[0].interact("Use")) return true;
    }
    auto lp = ::titan::state::client().localPlayer();
    if (!lp) return false;
    auto npc = ::titan::queries::npcs()
        .hasAction("Bank")
        .nearestTo(*lp);
    if (npc) return npc->interact("Bank");
    return false;
}

// -----------------------------------------------------------------------
// PIN helpers
// -----------------------------------------------------------------------

/// True while the bank PIN keypad (interface 213) is up.
///
/// Structural, not textual, for the same reason as `isSearchOpen`: a text
/// scan for the prompt reads only `textPrimary`, matches case-sensitively,
/// and needs a packed id on the hit, so it misses pads whose instruction
/// text sits in the secondary slot or carries colour tags. `visible()`
/// already fails when the widget's group is inactive, so a visible pad
/// child implies the pad is open.
inline bool isPinVisible() {
    using PinUi = ::titan::gamevals::InterfaceID::BankpinKeypad;
    auto visible = [](int32_t packedId) {
        auto w = ::titan::state::widgets().find(static_cast<uint32_t>(packedId));
        return w && w->visible();
    };
    // FRAME and CANCEL persist for the whole pad session; the digit slots
    // and the shuffled letter keys do not.
    return visible(PinUi::FRAME) || visible(PinUi::CANCEL)
        || visible(PinUi::UNIVERSE);
}

/// Index (0-3) of the digit the pad is asking for, or -1 when the pad is
/// down or has not named a digit yet.
///
/// Sweeps every visible widget in the pad group rather than reading `FRAME`
/// alone: the instruction text does not reliably live on that one child.
/// `FRAME` is included in the sweep.
inline int32_t pinRequestedDigitIndex() {
    const auto widgets = ::titan::queries::widgets(
            static_cast<uint32_t>(::titan::gamevals::InterfaceID::BANKPIN_KEYPAD))
        .isVisible()
        .toVector();
    for (const auto& widget : widgets) {
        std::string_view txt(widget.text());
        if (detail::icontains(txt, "FIRST"))  return 0;
        if (detail::icontains(txt, "SECOND")) return 1;
        if (detail::icontains(txt, "THIRD"))  return 2;
        if (detail::icontains(txt, "FOURTH")) return 3;
    }
    return -1;
}

inline bool typePin(const std::string& pin) {
    if (pin.size() != 4) return false;
    for (char c : pin) {
        if (c < '0' || c > '9') return false;
    }
    int32_t idx = pinRequestedDigitIndex();
    if (idx < 0 || idx > 3) return false;
    char digit[2] = { pin[static_cast<size_t>(idx)], '\0' };
    return ::titan::keyboard::typeString(digit);
}

// -----------------------------------------------------------------------
// Loadout data types
// -----------------------------------------------------------------------

struct LoadoutItemGroup {
    std::vector<int32_t> itemIds;
    int32_t amount = 1;
};

struct ConsumableGroup {
    std::vector<int32_t> itemIds;
    std::function<bool()> condition;
    int32_t limit = 0;
    int32_t consumes = 0;
};

struct Loadout {
    std::vector<LoadoutItemGroup> inventory;
    std::vector<LoadoutItemGroup> gear;
    std::vector<ConsumableGroup> consumables;
};

// -----------------------------------------------------------------------
// LoadoutRunner
// -----------------------------------------------------------------------

class LoadoutRunner {
public:
    enum class Status {
        Idle,
        Active,
        WaitingForBank,
        WaitingForSearchDialog,
        Complete
    };

    void enqueue(Loadout loadout) {
        queue_.push_back(std::move(loadout));
    }

    bool isRunning() const {
        return current_.has_value() || !queue_.empty();
    }

    void clear() {
        current_ = std::nullopt;
        queue_.clear();
        awaitingSearchDialog_ = false;
        pendingAmount_ = 0;
    }

    Status tick() {
        if (isPinVisible()) return Status::Active;

        if (!current_) {
            if (queue_.empty()) return Status::Idle;
            current_ = std::move(queue_.front());
            queue_.erase(queue_.begin());
            phase_ = Phase::Deposit;
        }

        if (!isOpen()) return Status::WaitingForBank;

        if (awaitingSearchDialog_) {
            if (isSearchOpen()) {
                ::titan::keyboard::typeString(
                    std::to_string(pendingAmount_).c_str());
                awaitingSearchDialog_ = false;
                pendingAmount_ = 0;
                return Status::Active;
            }
            return Status::WaitingForSearchDialog;
        }

        switch (phase_) {
        case Phase::Deposit:
            if (tickDeposit()) return Status::Active;
            phase_ = Phase::Gear;
            [[fallthrough]];
        case Phase::Gear:
            if (tickGear()) return Status::Active;
            phase_ = Phase::Consumables;
            [[fallthrough]];
        case Phase::Consumables:
            if (tickConsumables()) return Status::Active;
            phase_ = Phase::Inventory;
            [[fallthrough]];
        case Phase::Inventory:
            if (tickInventory()) return Status::Active;
            break;
        }

        current_ = std::nullopt;
        phase_ = Phase::Deposit;
        return Status::Complete;
    }

private:
    enum class Phase { Deposit, Gear, Consumables, Inventory };

    std::optional<Loadout> current_;
    std::vector<Loadout> queue_;
    Phase phase_ = Phase::Deposit;
    bool awaitingSearchDialog_ = false;
    int32_t pendingAmount_ = 0;

    bool isItemInAnyGroup(int32_t itemId) const {
        if (!current_) return false;
        for (auto& g : current_->inventory) {
            for (int32_t id : g.itemIds) if (id == itemId) return true;
        }
        for (auto& g : current_->gear) {
            for (int32_t id : g.itemIds) if (id == itemId) return true;
        }
        for (auto& g : current_->consumables) {
            for (int32_t id : g.itemIds) if (id == itemId) return true;
        }
        return false;
    }

    int32_t countInInventory(const std::vector<int32_t>& ids) const {
        int32_t total = 0;
        auto inv = ::titan::state::itemContainer(
            static_cast<int32_t>(::titan::InventoryID::INVENTORY));
        if (!inv) return 0;
        for (auto& slot : inv->items) {
            for (int32_t id : ids) {
                if (slot.itemId == id) { total += slot.quantity; break; }
            }
        }
        return total;
    }

    int32_t countEquipped(const std::vector<int32_t>& ids) const {
        int32_t total = 0;
        auto equip = ::titan::state::itemContainer(
            static_cast<int32_t>(::titan::InventoryID::EQUIPMENT));
        if (!equip) return 0;
        for (auto& slot : equip->items) {
            for (int32_t id : ids) {
                if (slot.itemId == id) { total += slot.quantity; break; }
            }
        }
        return total;
    }

    bool tryWithdraw(int32_t itemId, int32_t needed) {
        if (needed == 1) {
            return withdrawItem(itemId);
        }
        int32_t lastQty = ::titan::state::vars().varbit(
            ::titan::Varbits::BANK_REQUESTEDQUANTITY);
        if (needed == 5 || needed == 10 || lastQty == needed) {
            return withdrawItemAmount(itemId, needed);
        }
        if (withdrawItemAmount(itemId, needed)) {
            awaitingSearchDialog_ = true;
            pendingAmount_ = needed;
            return true;
        }
        return false;
    }

    bool tickDeposit() {
        auto inv = ::titan::state::itemContainer(
            static_cast<int32_t>(::titan::InventoryID::INVENTORY));
        if (!inv) return false;
        for (auto& slot : inv->items) {
            if (!isItemInAnyGroup(slot.itemId)) {
                return depositAllOfSlot(slot.slot);
            }
        }
        return false;
    }

    bool tickGear() {
        if (!current_) return false;
        for (auto& g : current_->gear) {
            int32_t have = countEquipped(g.itemIds);
            if (have >= g.amount) continue;
            int32_t needed = g.amount - have;
            for (int32_t id : g.itemIds) {
                int32_t invCount = countInInventory({id});
                if (invCount > 0) {
                    return interactItemInBank(id, 9);
                }
                if (Bank::contains(id)) {
                    return tryWithdraw(id, needed);
                }
            }
        }
        return false;
    }

    bool tickConsumables() {
        if (!current_) return false;
        for (auto& c : current_->consumables) {
            if (!c.condition || !c.condition()) continue;
            if (c.limit > 0 && c.consumes >= c.limit) continue;
            int32_t invCount = countInInventory(c.itemIds);
            if (invCount > 0) {
                c.consumes++;
                continue;
            }
            for (int32_t id : c.itemIds) {
                if (Bank::contains(id)) {
                    return withdrawItem(id);
                }
            }
        }
        return false;
    }

    bool tickInventory() {
        if (!current_) return false;
        for (auto& g : current_->inventory) {
            int32_t have = countInInventory(g.itemIds);
            if (have >= g.amount) continue;
            int32_t needed = g.amount - have;
            for (int32_t id : g.itemIds) {
                if (Bank::contains(id)) {
                    return tryWithdraw(id, needed);
                }
            }
        }
        return false;
    }
};

}  // namespace Bank
}  // namespace utils
}  // namespace titan
