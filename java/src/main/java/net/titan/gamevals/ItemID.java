package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.ItemIDEntries;

public final class ItemID
        implements ItemIDConstants_0,
                   ItemIDConstants_1,
                   ItemIDConstants_2,
                   ItemIDConstants_3,
                   ItemIDConstants_4,
                   ItemIDConstants_5,
                   ItemIDConstants_6,
                   ItemIDConstants_7,
                   ItemIDConstants_8,
                   ItemIDConstants_9,
                   ItemIDConstants_10,
                   ItemIDConstants_11,
                   ItemIDConstants_12,
                   ItemIDConstants_13,
                   ItemIDConstants_14,
                   ItemIDConstants_15,
                   ItemIDConstants_16,
                   ItemIDConstants_17,
                   ItemIDConstants_18,
                   ItemIDConstants_19,
                   ItemIDConstants_20,
                   ItemIDConstants_21,
                   ItemIDConstants_22,
                   ItemIDConstants_23,
                   ItemIDConstants_24,
                   ItemIDConstants_25,
                   ItemIDConstants_26,
                   ItemIDConstants_27,
                   ItemIDConstants_28,
                   ItemIDConstants_29,
                   ItemIDConstants_30,
                   ItemIDConstants_31,
                   ItemIDConstants_32,
                   ItemIDConstants_33,
                   ItemIDConstants_34,
                   ItemIDConstants_35,
                   ItemIDConstants_36,
                   ItemIDConstants_37,
                   ItemIDConstants_38,
                   ItemIDConstants_39,
                   ItemIDConstants_40,
                   ItemIDConstants_41,
                   ItemIDConstants_42,
                   ItemIDConstants_43,
                   ItemIDConstants_44,
                   ItemIDConstants_45,
                   ItemIDConstants_46,
                   ItemIDConstants_47,
                   ItemIDConstants_48,
                   ItemIDConstants_49,
                   ItemIDConstants_50,
                   ItemIDConstants_51,
                   ItemIDConstants_52,
                   ItemIDConstants_53,
                   ItemIDConstants_54,
                   ItemIDConstants_55,
                   ItemIDConstants_56,
                   ItemIDConstants_57,
                   ItemIDConstants_58,
                   ItemIDConstants_59,
                   ItemIDConstants_60,
                   ItemIDConstants_61,
                   ItemIDConstants_62,
                   ItemIDConstants_63,
                   ItemIDConstants_64,
                   ItemIDConstants_65 {
    private ItemID() {}

    public static final String SOURCE_CATALOG = "objtypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return ItemIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return ItemIDEntries.byId(id);
    }
}
