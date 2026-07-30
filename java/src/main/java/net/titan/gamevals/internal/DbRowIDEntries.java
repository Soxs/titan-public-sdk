package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class DbRowIDEntries {
    private DbRowIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[] out = new GamevalEntry[16788];
        int offset = 0;
        offset = copy(out, offset, DbRowIDEntries_0.entries());
        offset = copy(out, offset, DbRowIDEntries_1.entries());
        offset = copy(out, offset, DbRowIDEntries_2.entries());
        offset = copy(out, offset, DbRowIDEntries_3.entries());
        offset = copy(out, offset, DbRowIDEntries_4.entries());
        offset = copy(out, offset, DbRowIDEntries_5.entries());
        offset = copy(out, offset, DbRowIDEntries_6.entries());
        offset = copy(out, offset, DbRowIDEntries_7.entries());
        offset = copy(out, offset, DbRowIDEntries_8.entries());
        offset = copy(out, offset, DbRowIDEntries_9.entries());
        offset = copy(out, offset, DbRowIDEntries_10.entries());
        offset = copy(out, offset, DbRowIDEntries_11.entries());
        offset = copy(out, offset, DbRowIDEntries_12.entries());
        offset = copy(out, offset, DbRowIDEntries_13.entries());
        offset = copy(out, offset, DbRowIDEntries_14.entries());
        offset = copy(out, offset, DbRowIDEntries_15.entries());
        offset = copy(out, offset, DbRowIDEntries_16.entries());
        offset = copy(out, offset, DbRowIDEntries_17.entries());
        offset = copy(out, offset, DbRowIDEntries_18.entries());
        offset = copy(out, offset, DbRowIDEntries_19.entries());
        offset = copy(out, offset, DbRowIDEntries_20.entries());
        offset = copy(out, offset, DbRowIDEntries_21.entries());
        offset = copy(out, offset, DbRowIDEntries_22.entries());
        offset = copy(out, offset, DbRowIDEntries_23.entries());
        offset = copy(out, offset, DbRowIDEntries_24.entries());
        offset = copy(out, offset, DbRowIDEntries_25.entries());
        offset = copy(out, offset, DbRowIDEntries_26.entries());
        offset = copy(out, offset, DbRowIDEntries_27.entries());
        offset = copy(out, offset, DbRowIDEntries_28.entries());
        offset = copy(out, offset, DbRowIDEntries_29.entries());
        offset = copy(out, offset, DbRowIDEntries_30.entries());
        offset = copy(out, offset, DbRowIDEntries_31.entries());
        offset = copy(out, offset, DbRowIDEntries_32.entries());
        return out;
    }

    private static int copy(GamevalEntry[] out, int offset, GamevalEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
    }

    public static Optional<GamevalEntry> byId(int id) {
        Optional<GamevalEntry> hit;
        hit = DbRowIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_3.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_4.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_5.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_6.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_7.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_8.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_9.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_10.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_11.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_12.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_13.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_14.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_15.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_16.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_17.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_18.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_19.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_20.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_21.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_22.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_23.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_24.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_25.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_26.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_27.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_28.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_29.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_30.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_31.byId(id);
        if (hit.isPresent()) return hit;
        hit = DbRowIDEntries_32.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
