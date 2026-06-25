package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.DbTableIDEntries;

public final class DbTableID
        implements DbTableIDConstants_0 {
    private DbTableID() {}

    public static final String SOURCE_CATALOG = "tabletypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return DbTableIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return DbTableIDEntries.byId(id);
    }
}
