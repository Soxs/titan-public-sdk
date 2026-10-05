/// @file titan/utils/deposit_box.h
/// @brief Plugin-side bank deposit box state and action helpers.
///
/// Header-only inline wrappers around `titan::state::widgets()` and
/// `titan::state::vars()` for the shared bank deposit box interface
/// (`InterfaceID::BANK_DEPOSITBOX`, every deposit box object opens the
/// same interface). Mirrors the pattern of `<titan/utils/bank.h>`.
///
/// Every action is the interface's own component operation (CC_OP), which
/// is what a real click sends on both the legacy and native-callback menu
/// conventions. In particular the close X is a component operation on a
/// dynamic child of the frame; legacy `WidgetClose` (26) means the native
/// root-owned closure and does not close this interface on callback clients.
///
/// Added in SDK 133.

#pragma once

#include "../client.h"
#include "../gamevals.h"
#include "../menu_action.h"
#include "../varbits.h"

#include <cstdint>

namespace titan {
namespace utils {
namespace DepositBox {

namespace detail {

constexpr uint32_t kCcOp = static_cast<uint32_t>(::titan::MenuAction::Id::CcOp);

/// Dynamic child slot of `BankDepositbox::FRAME` that carries the close X
/// (confirmed live on 240.7). The same frame script places the bank's close
/// X at slot 11 of `Bankmain::FRAME`, see `Bank::close()`.
constexpr int32_t kCloseChildSlot = 11;

/// `Varbits::BANK_QUANTITY_TYPE` value for the "All" deposit quantity.
constexpr int32_t kQuantityAll = 4;

inline bool widgetVisible(int32_t packedId) {
    const auto widget = ::titan::state::widgets().find(static_cast<uint32_t>(packedId));
    return widget && widget->visible();
}

inline bool componentOp(int32_t packedId, int32_t childSlot = -1) {
    return ::titan::state::widgets().interact(kCcOp, 1, childSlot, packedId);
}

} // namespace detail

// -----------------------------------------------------------------------
// State reads
// -----------------------------------------------------------------------

/// True while the deposit box interface is open (its inventory menu button
/// or worn-equipment panel is visible).
inline bool isOpen() {
    return detail::widgetVisible(::titan::gamevals::InterfaceID::BankDepositbox::MENU_BUTTON) ||
           detail::widgetVisible(::titan::gamevals::InterfaceID::BankDepositbox::WORN);
}

/// True when the deposit quantity mode is "All".
inline bool isDepositAllSelected() {
    return ::titan::state::vars().varbit(::titan::Varbits::BANK_QUANTITY_TYPE) == detail::kQuantityAll;
}

// -----------------------------------------------------------------------
// Actions (queue acceptance, not gameplay success)
// -----------------------------------------------------------------------

/// Close the deposit box through its frame's close X component operation.
inline bool close() {
    return detail::componentOp(::titan::gamevals::InterfaceID::BankDepositbox::FRAME, detail::kCloseChildSlot);
}

/// Deposit the whole inventory.
inline bool depositInventory() {
    return detail::componentOp(::titan::gamevals::InterfaceID::BankDepositbox::DEPOSIT_INV);
}

/// Deposit all worn equipment.
inline bool depositWorn() {
    return detail::componentOp(::titan::gamevals::InterfaceID::BankDepositbox::DEPOSIT_WORN);
}

/// Deposit the looting bag's contents.
inline bool depositLootingBag() {
    return detail::componentOp(::titan::gamevals::InterfaceID::BankDepositbox::DEPOSIT_LOOTINGBAG);
}

/// Select the "All" deposit quantity; true immediately when already selected.
inline bool selectDepositAll() {
    if (isDepositAllSelected()) return true;
    return detail::componentOp(::titan::gamevals::InterfaceID::BankDepositbox::ALL);
}

} // namespace DepositBox
} // namespace utils
} // namespace titan
