package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.VarClientIDEntries;

public final class VarClientID
        implements VarClientIDConstants_0,
                   VarClientIDConstants_1,
                   VarClientIDConstants_2 {
    private VarClientID() {}

    public static final String SOURCE_CATALOG = "varctypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return VarClientIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return VarClientIDEntries.byId(id);
    }
}
