package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

final class InventoryIDEntries_2 {
    private InventoryIDEntries_2() {}

    static GamevalEntry[] entries() {
        return new GamevalEntry[] {
            new GamevalEntry(1024, "ID_1024", "", "invtypes"),
            new GamevalEntry(1025, "ID_1025", "", "invtypes"),
        };
    }

    static Optional<GamevalEntry> byId(int id) {
        for (GamevalEntry entry : entries()) {
            if (entry.id() == id) return Optional.of(entry);
        }
        return Optional.empty();
    }

}
