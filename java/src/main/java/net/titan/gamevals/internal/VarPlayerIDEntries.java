package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class VarPlayerIDEntries {
    private VarPlayerIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[] out = new GamevalEntry[2917];
        int offset = 0;
        offset = copy(out, offset, VarPlayerIDEntries_0.entries());
        offset = copy(out, offset, VarPlayerIDEntries_1.entries());
        offset = copy(out, offset, VarPlayerIDEntries_2.entries());
        offset = copy(out, offset, VarPlayerIDEntries_3.entries());
        offset = copy(out, offset, VarPlayerIDEntries_4.entries());
        offset = copy(out, offset, VarPlayerIDEntries_5.entries());
        return out;
    }

    private static int copy(GamevalEntry[] out, int offset, GamevalEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
    }

    public static Optional<GamevalEntry> byId(int id) {
        Optional<GamevalEntry> hit;
        hit = VarPlayerIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_3.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_4.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_5.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
