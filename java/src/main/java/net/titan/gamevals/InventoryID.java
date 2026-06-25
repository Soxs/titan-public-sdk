package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.InventoryIDEntries;

public final class InventoryID
        implements InventoryIDConstants_0,
                   InventoryIDConstants_1 {
    private InventoryID() {}

    public static final String SOURCE_CATALOG = "invtypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return InventoryIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return InventoryIDEntries.byId(id);
    }
}
