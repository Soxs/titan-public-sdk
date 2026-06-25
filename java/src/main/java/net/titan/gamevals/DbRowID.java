package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.DbRowIDEntries;

public final class DbRowID
        implements DbRowIDConstants_0 {
    private DbRowID() {}

    public static final String SOURCE_CATALOG = "rowtypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return DbRowIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return DbRowIDEntries.byId(id);
    }
}
