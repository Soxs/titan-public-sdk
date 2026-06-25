package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

final class DbTableIDEntries_0 {
    private DbTableIDEntries_0() {}

    static GamevalEntry[] entries() {
        return new GamevalEntry[] {
            new GamevalEntry(0, "ID_0", "", "tabletypes"),
            new GamevalEntry(1, "ID_1", "", "tabletypes"),
        };
    }

    static Optional<GamevalEntry> byId(int id) {
        for (GamevalEntry entry : entries()) {
            if (entry.id() == id) return Optional.of(entry);
        }
        return Optional.empty();
    }

}
