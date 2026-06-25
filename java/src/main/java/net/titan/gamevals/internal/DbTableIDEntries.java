package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class DbTableIDEntries {
    private DbTableIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[] out = new GamevalEntry[2];
        int offset = 0;
        offset = copy(out, offset, DbTableIDEntries_0.entries());
        return out;
    }

    private static int copy(GamevalEntry[] out, int offset, GamevalEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
    }

    public static Optional<GamevalEntry> byId(int id) {
        Optional<GamevalEntry> hit;
        hit = DbTableIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
