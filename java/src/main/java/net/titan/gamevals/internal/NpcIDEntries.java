package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class NpcIDEntries {
    private NpcIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            NpcIDEntries_0.entries(),
            NpcIDEntries_1.entries(),
            NpcIDEntries_2.entries(),
            NpcIDEntries_3.entries(),
            NpcIDEntries_4.entries(),
            NpcIDEntries_5.entries(),
            NpcIDEntries_6.entries(),
            NpcIDEntries_7.entries(),
            NpcIDEntries_8.entries(),
            NpcIDEntries_9.entries(),
            NpcIDEntries_10.entries(),
            NpcIDEntries_11.entries(),
            NpcIDEntries_12.entries(),
            NpcIDEntries_13.entries(),
            NpcIDEntries_14.entries(),
            NpcIDEntries_15.entries(),
            NpcIDEntries_16.entries(),
            NpcIDEntries_17.entries(),
            NpcIDEntries_18.entries(),
            NpcIDEntries_19.entries(),
            NpcIDEntries_20.entries(),
            NpcIDEntries_21.entries(),
            NpcIDEntries_22.entries(),
            NpcIDEntries_23.entries(),
            NpcIDEntries_24.entries(),
            NpcIDEntries_25.entries(),
            NpcIDEntries_26.entries(),
            NpcIDEntries_27.entries(),
            NpcIDEntries_28.entries(),
            NpcIDEntries_29.entries(),
            NpcIDEntries_30.entries(),
            NpcIDEntries_31.entries(),
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
