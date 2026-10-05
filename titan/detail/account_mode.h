/// @file titan/detail/account_mode.h
/// @brief Shared account-mode predicates used by SDK facades.

#pragma once

#include <cstdint>

namespace titan::detail {

inline bool isIronmanAccountType(int32_t accountType) {
    return accountType >= 1 && accountType <= 6;
}

inline bool isGroupIronmanAccountType(int32_t accountType) {
    return accountType == 4 || accountType == 5 || accountType == 6;
}

inline bool groundItemOwnershipLootableForAccount(uint32_t ownershipType,
                                                  int32_t accountType) {
    const bool ironman = isIronmanAccountType(accountType);
    switch (ownershipType) {
        case 0: return true;                         // None / public spawn
        case 1: return true;                         // Self player
        case 2: return !ironman;                     // Other player
        case 3: return isGroupIronmanAccountType(accountType);
        default: return !ironman;                    // Unknown: fail closed for irons
    }
}

}  // namespace titan::detail
