package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.VarPlayerIDEntries;

public final class VarPlayerID
        implements VarPlayerIDConstants_0,
                   VarPlayerIDConstants_1,
                   VarPlayerIDConstants_2,
                   VarPlayerIDConstants_3,
                   VarPlayerIDConstants_4,
                   VarPlayerIDConstants_5 {
    private VarPlayerID() {}

    public static final String SOURCE_CATALOG = "varptypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return VarPlayerIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return VarPlayerIDEntries.byId(id);
    }
}
