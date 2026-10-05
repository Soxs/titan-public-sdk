/// @file titan/bank_sets.h
/// @brief Known bank / chest / deposit-box object IDs for proximity and
///        open-bank helpers.
///
/// Ported from TP_RL `BankSets.java`. Values are OSRS object IDs
/// (RuneLite `ObjectID.*`). When new bank objects are added to the game,
/// append them to the appropriate array.
///
/// Added in SDK 44.

#pragma once

#include <algorithm>
#include <array>
#include <cstdint>

namespace titan {
namespace BankSets {

// --- Bank booths (traditional booths, no chests / boxes) -----------------

inline constexpr std::array kBooths = {
    6084,  // BANK_BOOTH
    10083, // BANK_BOOTH_10083
    10355, // BANK_BOOTH_10355
    10357, // BANK_BOOTH_10357
    10517, // BANK_BOOTH_10517
    10584, // BANK_BOOTH_10584
    10768, // BANK
    11338, // BANK_BOOTH_11338
    12798, // BANK_BOOTH_12798
    12799, // BANK_BOOTH_12799
    12800, // BANK_BOOTH_12800
    12801, // BANK_BOOTH_12801
    14367, // BANK_BOOTH_14367
    14368, // BANK_BOOTH_14368
    16642, // BANK_BOOTH_16642
    16700, // BANK_BOOTH_16700
    18491, // BANK_BOOTH_18491
    20325, // BANK_BOOTH_20325
    20326, // BANK_BOOTH_20326
    20327, // BANK_BOOTH_20327
    20328, // BANK_BOOTH_20328
    22819, // BANK_BOOTH_22819
    24101, // BANK_BOOTH_24101
    24347, // BANK_BOOTH_24347
    25808, // BANK_BOOTH_25808
    27254, // BANK_BOOTH_27254
    27260, // BANK_BOOTH_27260
    27263, // BANK_BOOTH_27263
    27265, // BANK_BOOTH_27265
    27267, // BANK_BOOTH_27267
    27292, // BANK_BOOTH_27292
    27718, // BANK_BOOTH_27718
    27719, // BANK_BOOTH_27719
    27720, // BANK_BOOTH_27720
    27721, // BANK_BOOTH_27721
    28429, // BANK_BOOTH_28429
    28430, // BANK_BOOTH_28430
    28431, // BANK_BOOTH_28431
    28432, // BANK_BOOTH_28432
    28433, // BANK_BOOTH_28433
    28546, // BANK_BOOTH_28546
    28547, // BANK_BOOTH_28547
    28548, // BANK_BOOTH_28548
    28549, // BANK_BOOTH_28549
    32666, // BANK_BOOTH_32666
    34810, // NULL_34810
    36559, // BANK_BOOTH_36559
    37959, // BANK_BOOTH_37959
    39238, // BANK_BOOTH_39238
};

// --- Bank chests / boxes / misc ------------------------------------------

inline constexpr std::array kChests = {
    2693,  // BANK_CHEST
    4483,  // BANK_CHEST_4483
    10562, // BANK_CHEST_10562
    14382, // BANK_CHEST_14382
    14886, // BANK_CHEST_14886
    16695, // BANK_CHEST_16695
    16696, // BANK_CHEST_16696
    19051, // BANK_CHEST_19051
    21301, // BANK_CHEST_21301
    26707, // BANK_CHEST_26707
    26711, // BANK_CHEST_26711
    28594, // BANK_CHEST_28594
    28595, // BANK_CHEST_28595
    28816, // BANK_CHEST_28816
    28861, // BANK_CHEST_28861
    29321, // BANK_CHEST_29321
    30087, // BANK_CHEST_30087
    30267, // BANK_CHEST_30267
    30926, // BANK_CHEST_30926
    30989, // BANK_CHEST_30989
    31948, // BANK_BOX
    31949, // BANK_BOX_31949
    34343, // BANK_CHEST_34343
    36219, // MINE_CART_36219
    41315, // BANK_CHEST_41315
    41493, // BANK_CHEST_41493
    47420,
};

// --- Deposit boxes / pots (deferred from runtime; kept for future use) ---

inline constexpr std::array kDepositBoxes = {
    10529, // BANK_DEPOSIT_BOX
    10530, // BANK_DEPOSIT_CHEST
    25937, // BANK_DEPOSIT_BOX_25937
    26254, // BANK_DEPOSIT_BOX_26254
    29103, // BANK_DEPOSIT_BOX_29103
    29104, // BANK_DEPOSIT_BOX_29104
    29105, // BANK_DEPOSIT_BOX_29105
    29106, // BANK_DEPOSIT_BOX_29106
    29108, // BANK_DEPOSIT_POT
    29327, // BANK_DEPOSIT_BOX_29327
    30268, // BANK_DEPOSIT_BOX_30268
    31726, // BANK_DEPOSIT_BOX_31726
    32665, // BANK_DEPOSIT_BOX_32665
    34344, // BANK_DEPOSIT_BOX_34344
    36086, // BANK_DEPOSIT_BOX_36086
    39239, // BANK_DEPOSIT_BOX_39239
};

// --- Combined sets -------------------------------------------------------

/// All bankable objects (booths + chests + GE). Used by `open()` and
/// `isNearBank()`. Does NOT include deposit boxes.
inline constexpr auto kAllBanksAndChests = [] {
    constexpr int32_t kGrandExchangeBooth = 10060;
    constexpr int32_t kExtraBoothId       = 10356;
    constexpr std::size_t n = kBooths.size() + kChests.size() + 2;
    std::array<int32_t, n> out{};
    std::size_t i = 0;
    for (auto v : kBooths) out[i++] = v;
    for (auto v : kChests) out[i++] = v;
    out[i++] = kGrandExchangeBooth;
    out[i++] = kExtraBoothId;
    return out;
}();

/// Check whether @p objectId is a known bank/chest object.
inline bool isBankObject(int32_t objectId) {
    for (auto id : kAllBanksAndChests) {
        if (id == objectId) return true;
    }
    return false;
}

}  // namespace BankSets
}  // namespace titan
