package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

final class VarPlayerIDEntries_11 {
    private VarPlayerIDEntries_11() {}

    static GamevalEntry[] entries() {
        return new GamevalEntry[] {
            new GamevalEntry(5632, "ID_5632", "", "varptypes"),
            new GamevalEntry(5633, "ID_5633", "", "varptypes"),
            new GamevalEntry(5634, "ID_5634", "", "varptypes"),
            new GamevalEntry(5635, "ID_5635", "", "varptypes"),
            new GamevalEntry(5636, "ID_5636", "", "varptypes"),
            new GamevalEntry(5637, "ID_5637", "", "varptypes"),
            new GamevalEntry(5638, "ID_5638", "", "varptypes"),
            new GamevalEntry(5639, "ID_5639", "", "varptypes"),
            new GamevalEntry(5640, "ID_5640", "", "varptypes"),
            new GamevalEntry(5641, "ID_5641", "", "varptypes"),
            new GamevalEntry(5642, "ID_5642", "", "varptypes"),
            new GamevalEntry(5643, "ID_5643", "", "varptypes"),
            new GamevalEntry(5644, "ID_5644", "", "varptypes"),
            new GamevalEntry(5645, "ID_5645", "", "varptypes"),
            new GamevalEntry(5646, "ID_5646", "", "varptypes"),
            new GamevalEntry(5647, "ID_5647", "", "varptypes"),
            new GamevalEntry(5648, "ID_5648", "", "varptypes"),
            new GamevalEntry(5649, "ID_5649", "", "varptypes"),
            new GamevalEntry(5650, "ID_5650", "", "varptypes"),
            new GamevalEntry(5651, "ID_5651", "", "varptypes"),
            new GamevalEntry(5652, "ID_5652", "", "varptypes"),
            new GamevalEntry(5653, "ID_5653", "", "varptypes"),
            new GamevalEntry(5654, "ID_5654", "", "varptypes"),
            new GamevalEntry(5655, "ID_5655", "", "varptypes"),
            new GamevalEntry(5656, "ID_5656", "", "varptypes"),
            new GamevalEntry(5657, "ID_5657", "", "varptypes"),
            new GamevalEntry(5658, "ID_5658", "", "varptypes"),
            new GamevalEntry(5659, "ID_5659", "", "varptypes"),
            new GamevalEntry(5660, "ID_5660", "", "varptypes"),
            new GamevalEntry(5661, "ID_5661", "", "varptypes"),
            new GamevalEntry(5662, "ID_5662", "", "varptypes"),
            new GamevalEntry(5663, "ID_5663", "", "varptypes"),
            new GamevalEntry(5664, "ID_5664", "", "varptypes"),
            new GamevalEntry(5665, "ID_5665", "", "varptypes"),
            new GamevalEntry(5666, "ID_5666", "", "varptypes"),
            new GamevalEntry(5667, "ID_5667", "", "varptypes"),
        };
    }

    static Optional<GamevalEntry> byId(int id) {
        for (GamevalEntry entry : entries()) {
            if (entry.id() == id) return Optional.of(entry);
        }
        return Optional.empty();
    }

}
