package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class VarPlayerIDEntries {
    private VarPlayerIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            VarPlayerIDEntries_0.entries(),
            VarPlayerIDEntries_1.entries(),
            VarPlayerIDEntries_2.entries(),
            VarPlayerIDEntries_3.entries(),
            VarPlayerIDEntries_4.entries(),
            VarPlayerIDEntries_5.entries(),
            VarPlayerIDEntries_6.entries(),
            VarPlayerIDEntries_7.entries(),
            VarPlayerIDEntries_8.entries(),
            VarPlayerIDEntries_9.entries(),
            VarPlayerIDEntries_10.entries(),
            VarPlayerIDEntries_11.entries(),
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
        hit = VarPlayerIDEntries_6.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_7.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_8.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_9.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_10.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarPlayerIDEntries_11.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
