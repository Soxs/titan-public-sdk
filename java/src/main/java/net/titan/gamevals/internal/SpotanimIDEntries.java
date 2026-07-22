package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class SpotanimIDEntries {
    private SpotanimIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[] out = new GamevalEntry[4010];
        int offset = 0;
        offset = copy(out, offset, SpotanimIDEntries_0.entries());
        offset = copy(out, offset, SpotanimIDEntries_1.entries());
        offset = copy(out, offset, SpotanimIDEntries_2.entries());
        offset = copy(out, offset, SpotanimIDEntries_3.entries());
        offset = copy(out, offset, SpotanimIDEntries_4.entries());
        offset = copy(out, offset, SpotanimIDEntries_5.entries());
        offset = copy(out, offset, SpotanimIDEntries_6.entries());
        offset = copy(out, offset, SpotanimIDEntries_7.entries());
        return out;
    }

    private static int copy(GamevalEntry[] out, int offset, GamevalEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
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
