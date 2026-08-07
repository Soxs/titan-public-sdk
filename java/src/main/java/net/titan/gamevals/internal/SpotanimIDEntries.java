package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class SpotanimIDEntries {
    private SpotanimIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            SpotanimIDEntries_0.entries(),
            SpotanimIDEntries_1.entries(),
            SpotanimIDEntries_2.entries(),
            SpotanimIDEntries_3.entries(),
            SpotanimIDEntries_4.entries(),
            SpotanimIDEntries_5.entries(),
            SpotanimIDEntries_6.entries(),
            SpotanimIDEntries_7.entries(),
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
        hit = SpotanimIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = SpotanimIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = SpotanimIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        hit = SpotanimIDEntries_3.byId(id);
        if (hit.isPresent()) return hit;
        hit = SpotanimIDEntries_4.byId(id);
        if (hit.isPresent()) return hit;
        hit = SpotanimIDEntries_5.byId(id);
        if (hit.isPresent()) return hit;
        hit = SpotanimIDEntries_6.byId(id);
        if (hit.isPresent()) return hit;
        hit = SpotanimIDEntries_7.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
