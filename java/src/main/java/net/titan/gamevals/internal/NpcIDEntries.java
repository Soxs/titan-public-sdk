package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class NpcIDEntries {
    private NpcIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[] out = new GamevalEntry[16292];
        int offset = 0;
        offset = copy(out, offset, NpcIDEntries_0.entries());
        offset = copy(out, offset, NpcIDEntries_1.entries());
        offset = copy(out, offset, NpcIDEntries_2.entries());
        offset = copy(out, offset, NpcIDEntries_3.entries());
        offset = copy(out, offset, NpcIDEntries_4.entries());
        offset = copy(out, offset, NpcIDEntries_5.entries());
        offset = copy(out, offset, NpcIDEntries_6.entries());
        offset = copy(out, offset, NpcIDEntries_7.entries());
        offset = copy(out, offset, NpcIDEntries_8.entries());
        offset = copy(out, offset, NpcIDEntries_9.entries());
        offset = copy(out, offset, NpcIDEntries_10.entries());
        offset = copy(out, offset, NpcIDEntries_11.entries());
        offset = copy(out, offset, NpcIDEntries_12.entries());
        offset = copy(out, offset, NpcIDEntries_13.entries());
        offset = copy(out, offset, NpcIDEntries_14.entries());
        offset = copy(out, offset, NpcIDEntries_15.entries());
        offset = copy(out, offset, NpcIDEntries_16.entries());
        offset = copy(out, offset, NpcIDEntries_17.entries());
        offset = copy(out, offset, NpcIDEntries_18.entries());
        offset = copy(out, offset, NpcIDEntries_19.entries());
        offset = copy(out, offset, NpcIDEntries_20.entries());
        offset = copy(out, offset, NpcIDEntries_21.entries());
        offset = copy(out, offset, NpcIDEntries_22.entries());
        offset = copy(out, offset, NpcIDEntries_23.entries());
        offset = copy(out, offset, NpcIDEntries_24.entries());
        offset = copy(out, offset, NpcIDEntries_25.entries());
        offset = copy(out, offset, NpcIDEntries_26.entries());
        offset = copy(out, offset, NpcIDEntries_27.entries());
        offset = copy(out, offset, NpcIDEntries_28.entries());
        offset = copy(out, offset, NpcIDEntries_29.entries());
        offset = copy(out, offset, NpcIDEntries_30.entries());
        offset = copy(out, offset, NpcIDEntries_31.entries());
        return out;
    }

    private static int copy(GamevalEntry[] out, int offset, GamevalEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
    }

    public static Optional<GamevalEntry> byId(int id) {
        Optional<GamevalEntry> hit;
        hit = NpcIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_3.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_4.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_5.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_6.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_7.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_8.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_9.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_10.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_11.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_12.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_13.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_14.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_15.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_16.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_17.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_18.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_19.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_20.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_21.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_22.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_23.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_24.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_25.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_26.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_27.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_28.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_29.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_30.byId(id);
        if (hit.isPresent()) return hit;
        hit = NpcIDEntries_31.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
