package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class InventoryIDEntries {
    private InventoryIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            InventoryIDEntries_0.entries(),
            InventoryIDEntries_1.entries(),
            InventoryIDEntries_2.entries(),
        };
        int size = 0;
        for (GamevalEntry[] part : parts) size += part.length;
        GamevalEntry[] out = new GamevalEntry[size];
        int offset = 0;
        for (GamevalEntry[] part : parts) {
            System.arraycopy(part, 0, out, offset, part.length);
            offset += part.length;
        }
        return out;
    }

    public static Optional<GamevalEntry> byId(int id) {
        Optional<GamevalEntry> hit;
        hit = InventoryIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = InventoryIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = InventoryIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
