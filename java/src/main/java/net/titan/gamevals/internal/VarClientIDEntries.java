package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class VarClientIDEntries {
    private VarClientIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[] out = new GamevalEntry[1507];
        int offset = 0;
        offset = copy(out, offset, VarClientIDEntries_0.entries());
        offset = copy(out, offset, VarClientIDEntries_1.entries());
        offset = copy(out, offset, VarClientIDEntries_2.entries());
        return out;
    }

    private static int copy(GamevalEntry[] out, int offset, GamevalEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
    }

    public static Optional<GamevalEntry> byId(int id) {
        Optional<GamevalEntry> hit;
        hit = VarClientIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarClientIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarClientIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
