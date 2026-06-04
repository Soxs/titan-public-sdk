package net.titan.api;

import java.util.Arrays;

/// Known bank / chest / deposit-box object IDs for proximity and open-bank
/// helpers. Mirrors the C++ {@code titan::BankSets} catalog (ported from
/// TP_RL {@code BankSets.java}).
public final class BankSets {
    private BankSets() {}

    /// Grand Exchange booth object id; included in {@link #ALL_BANKS_AND_CHESTS}.
    public static final int GRAND_EXCHANGE_BOOTH = 10060;
    /// Extra booth id appended to {@link #ALL_BANKS_AND_CHESTS}.
    public static final int EXTRA_BOOTH_ID = 10356;

    /// Traditional bank booths (no chests / boxes).
    public static final int[] BOOTHS = {
        6084, 10083, 10355, 10357, 10517, 10584, 10768, 11338, 12798, 12799,
        12800, 12801, 14367, 14368, 16642, 16700, 18491, 20325, 20326, 20327,
        20328, 22819, 24101, 24347, 25808, 27254, 27260, 27263, 27265, 27267,
        27292, 27718, 27719, 27720, 27721, 28429, 28430, 28431, 28432, 28433,
        28546, 28547, 28548, 28549, 32666, 34810, 36559, 37959, 39238,
    };

    /// Bank chests / boxes / misc.
    public static final int[] CHESTS = {
        2693, 4483, 10562, 14382, 14886, 16695, 16696, 19051, 21301, 26707,
        26711, 28594, 28595, 28816, 28861, 29321, 30087, 30267, 30926, 30989,
        31948, 31949, 34343, 36219, 41315, 41493, 47420,
    };

    /// Deposit boxes / pots (kept for future use).
    public static final int[] DEPOSIT_BOXES = {
        10529, 10530, 25937, 26254, 29103, 29104, 29105, 29106, 29108, 29327,
        30268, 31726, 32665, 34344, 36086, 39239,
    };

    /// All bankable objects (booths + chests + GE). Used by {@code Bank.open()}
    /// and {@code Bank.isNearBank()}. Does NOT include deposit boxes.
    public static final int[] ALL_BANKS_AND_CHESTS = buildAllBanksAndChests();

    private static int[] buildAllBanksAndChests() {
        int[] out = new int[BOOTHS.length + CHESTS.length + 2];
        int i = 0;
        for (int v : BOOTHS) out[i++] = v;
        for (int v : CHESTS) out[i++] = v;
        out[i++] = GRAND_EXCHANGE_BOOTH;
        out[i++] = EXTRA_BOOTH_ID;
        return out;
    }

    /// Returns whether {@code objectId} is a known bank/chest object.
    public static boolean isBankObject(int objectId) {
        for (int id : ALL_BANKS_AND_CHESTS) {
            if (id == objectId) return true;
        }
        return false;
    }

    /// Returns whether {@code objectId} is a known deposit-box object.
    public static boolean isDepositBox(int objectId) {
        return Arrays.stream(DEPOSIT_BOXES).anyMatch(id -> id == objectId);
    }
}
