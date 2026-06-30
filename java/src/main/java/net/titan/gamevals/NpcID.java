package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.NpcIDEntries;

public final class NpcID
        implements NpcIDConstants_0,
                   NpcIDConstants_1,
                   NpcIDConstants_2,
                   NpcIDConstants_3,
                   NpcIDConstants_4,
                   NpcIDConstants_5,
                   NpcIDConstants_6,
                   NpcIDConstants_7,
                   NpcIDConstants_8,
                   NpcIDConstants_9,
                   NpcIDConstants_10,
                   NpcIDConstants_11,
                   NpcIDConstants_12,
                   NpcIDConstants_13,
                   NpcIDConstants_14,
                   NpcIDConstants_15,
                   NpcIDConstants_16,
                   NpcIDConstants_17,
                   NpcIDConstants_18,
                   NpcIDConstants_19,
                   NpcIDConstants_20,
                   NpcIDConstants_21,
                   NpcIDConstants_22,
                   NpcIDConstants_23,
                   NpcIDConstants_24,
                   NpcIDConstants_25,
                   NpcIDConstants_26,
                   NpcIDConstants_27,
                   NpcIDConstants_28,
                   NpcIDConstants_29,
                   NpcIDConstants_30,
                   NpcIDConstants_31 {
    private NpcID() {}

    public static final String SOURCE_CATALOG = "npctypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return NpcIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return NpcIDEntries.byId(id);
    }
}
