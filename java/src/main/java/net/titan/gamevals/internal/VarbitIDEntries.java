package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class VarbitIDEntries {
    private VarbitIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            VarbitIDEntries_0.entries(),
            VarbitIDEntries_1.entries(),
            VarbitIDEntries_2.entries(),
            VarbitIDEntries_3.entries(),
            VarbitIDEntries_4.entries(),
            VarbitIDEntries_5.entries(),
            VarbitIDEntries_6.entries(),
            VarbitIDEntries_7.entries(),
            VarbitIDEntries_8.entries(),
            VarbitIDEntries_9.entries(),
            VarbitIDEntries_10.entries(),
            VarbitIDEntries_11.entries(),
            VarbitIDEntries_12.entries(),
            VarbitIDEntries_13.entries(),
            VarbitIDEntries_14.entries(),
            VarbitIDEntries_15.entries(),
            VarbitIDEntries_16.entries(),
            VarbitIDEntries_17.entries(),
            VarbitIDEntries_18.entries(),
            VarbitIDEntries_19.entries(),
            VarbitIDEntries_20.entries(),
            VarbitIDEntries_21.entries(),
            VarbitIDEntries_22.entries(),
            VarbitIDEntries_23.entries(),
            VarbitIDEntries_24.entries(),
            VarbitIDEntries_25.entries(),
            VarbitIDEntries_26.entries(),
            VarbitIDEntries_27.entries(),
            VarbitIDEntries_28.entries(),
            VarbitIDEntries_29.entries(),
            VarbitIDEntries_30.entries(),
            VarbitIDEntries_31.entries(),
            VarbitIDEntries_32.entries(),
            VarbitIDEntries_33.entries(),
            VarbitIDEntries_34.entries(),
            VarbitIDEntries_35.entries(),
            VarbitIDEntries_36.entries(),
            VarbitIDEntries_37.entries(),
            VarbitIDEntries_38.entries(),
            VarbitIDEntries_39.entries(),
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
        hit = VarbitIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_3.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_4.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_5.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_6.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_7.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_8.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_9.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_10.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_11.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_12.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_13.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_14.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_15.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_16.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_17.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_18.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_19.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_20.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_21.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_22.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_23.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_24.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_25.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_26.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_27.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_28.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_29.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_30.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_31.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_32.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_33.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_34.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_35.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_36.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_37.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_38.byId(id);
        if (hit.isPresent()) return hit;
        hit = VarbitIDEntries_39.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
