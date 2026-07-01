package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class DBTableIDEntries {
    private DBTableIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[] out = new GamevalEntry[246];
        int offset = 0;
        offset = copy(out, offset, DBTableIDEntries_0.entries());
        return out;
    }

    private static int copy(GamevalEntry[] out, int offset, GamevalEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
    }

    public static Optional<GamevalEntry> byId(int id) {
        Optional<GamevalEntry> hit;
        hit = DBTableIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
