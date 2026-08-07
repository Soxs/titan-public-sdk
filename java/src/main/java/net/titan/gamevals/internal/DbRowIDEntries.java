package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class DbRowIDEntries {
    private DbRowIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            DbRowIDEntries_0.entries(),
            DbRowIDEntries_1.entries(),
            DbRowIDEntries_2.entries(),
            DbRowIDEntries_3.entries(),
            DbRowIDEntries_4.entries(),
            DbRowIDEntries_5.entries(),
            DbRowIDEntries_6.entries(),
            DbRowIDEntries_7.entries(),
            DbRowIDEntries_8.entries(),
            DbRowIDEntries_9.entries(),
            DbRowIDEntries_10.entries(),
            DbRowIDEntries_11.entries(),
            DbRowIDEntries_12.entries(),
            DbRowIDEntries_13.entries(),
            DbRowIDEntries_14.entries(),
            DbRowIDEntries_15.entries(),
            DbRowIDEntries_16.entries(),
            DbRowIDEntries_17.entries(),
            DbRowIDEntries_18.entries(),
            DbRowIDEntries_19.entries(),
            DbRowIDEntries_20.entries(),
            DbRowIDEntries_21.entries(),
            DbRowIDEntries_22.entries(),
            DbRowIDEntries_23.entries(),
            DbRowIDEntries_24.entries(),
            DbRowIDEntries_25.entries(),
            DbRowIDEntries_26.entries(),
            DbRowIDEntries_27.entries(),
            DbRowIDEntries_28.entries(),
            DbRowIDEntries_29.entries(),
            DbRowIDEntries_30.entries(),
            DbRowIDEntries_31.entries(),
            DbRowIDEntries_32.entries(),
            DbRowIDEntries_33.entries(),
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
        hit = DbRowIDEntries_33.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
