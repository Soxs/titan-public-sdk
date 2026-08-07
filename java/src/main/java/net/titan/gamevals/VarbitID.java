package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.VarbitIDEntries;

public final class VarbitID
        implements VarbitIDConstants_0,
                   VarbitIDConstants_1,
                   VarbitIDConstants_2,
                   VarbitIDConstants_3,
                   VarbitIDConstants_4,
                   VarbitIDConstants_5,
                   VarbitIDConstants_6,
                   VarbitIDConstants_7,
                   VarbitIDConstants_8,
                   VarbitIDConstants_9,
                   VarbitIDConstants_10,
                   VarbitIDConstants_11,
                   VarbitIDConstants_12,
                   VarbitIDConstants_13,
                   VarbitIDConstants_14,
                   VarbitIDConstants_15,
                   VarbitIDConstants_16,
                   VarbitIDConstants_17,
                   VarbitIDConstants_18,
                   VarbitIDConstants_19,
                   VarbitIDConstants_20,
                   VarbitIDConstants_21,
                   VarbitIDConstants_22,
                   VarbitIDConstants_23,
                   VarbitIDConstants_24,
                   VarbitIDConstants_25,
                   VarbitIDConstants_26,
                   VarbitIDConstants_27,
                   VarbitIDConstants_28,
                   VarbitIDConstants_29,
                   VarbitIDConstants_30,
                   VarbitIDConstants_31,
                   VarbitIDConstants_32,
                   VarbitIDConstants_33,
                   VarbitIDConstants_34,
                   VarbitIDConstants_35,
                   VarbitIDConstants_36,
                   VarbitIDConstants_37,
                   VarbitIDConstants_38,
                   VarbitIDConstants_39 {
    private VarbitID() {}

    public static final String SOURCE_CATALOG = "varbittypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return VarbitIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return VarbitIDEntries.byId(id);
    }
}
