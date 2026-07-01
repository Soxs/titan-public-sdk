package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.AnimationIDEntries;

public final class AnimationID
        implements AnimationIDConstants_0,
                   AnimationIDConstants_1,
                   AnimationIDConstants_2,
                   AnimationIDConstants_3,
                   AnimationIDConstants_4,
                   AnimationIDConstants_5,
                   AnimationIDConstants_6,
                   AnimationIDConstants_7,
                   AnimationIDConstants_8,
                   AnimationIDConstants_9,
                   AnimationIDConstants_10,
                   AnimationIDConstants_11,
                   AnimationIDConstants_12,
                   AnimationIDConstants_13,
                   AnimationIDConstants_14,
                   AnimationIDConstants_15,
                   AnimationIDConstants_16,
                   AnimationIDConstants_17,
                   AnimationIDConstants_18,
                   AnimationIDConstants_19,
                   AnimationIDConstants_20,
                   AnimationIDConstants_21,
                   AnimationIDConstants_22,
                   AnimationIDConstants_23,
                   AnimationIDConstants_24,
                   AnimationIDConstants_25,
                   AnimationIDConstants_26,
                   AnimationIDConstants_27,
                   AnimationIDConstants_28 {
    private AnimationID() {}

    public static final String SOURCE_CATALOG = "seqtypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return AnimationIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return AnimationIDEntries.byId(id);
    }
}
