package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.DbRowIDEntries;

public final class DbRowID
        implements DbRowIDConstants_0,
                   DbRowIDConstants_1,
                   DbRowIDConstants_2,
                   DbRowIDConstants_3,
                   DbRowIDConstants_4,
                   DbRowIDConstants_5,
                   DbRowIDConstants_6,
                   DbRowIDConstants_7,
                   DbRowIDConstants_8,
                   DbRowIDConstants_9,
                   DbRowIDConstants_10,
                   DbRowIDConstants_11,
                   DbRowIDConstants_12,
                   DbRowIDConstants_13,
                   DbRowIDConstants_14,
                   DbRowIDConstants_15,
                   DbRowIDConstants_16,
                   DbRowIDConstants_17,
                   DbRowIDConstants_18,
                   DbRowIDConstants_19,
                   DbRowIDConstants_20,
                   DbRowIDConstants_21,
                   DbRowIDConstants_22,
                   DbRowIDConstants_23,
                   DbRowIDConstants_24,
                   DbRowIDConstants_25,
                   DbRowIDConstants_26,
                   DbRowIDConstants_27,
                   DbRowIDConstants_28,
                   DbRowIDConstants_29,
                   DbRowIDConstants_30,
                   DbRowIDConstants_31,
                   DbRowIDConstants_32 {
    private DbRowID() {}

    public static final String SOURCE_CATALOG = "rowtypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return DbRowIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return DbRowIDEntries.byId(id);
    }
}
