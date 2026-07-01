package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class VarbitIDEntries {
    private VarbitIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[] out = new GamevalEntry[13109];
        int offset = 0;
        offset = copy(out, offset, VarbitIDEntries_0.entries());
        offset = copy(out, offset, VarbitIDEntries_1.entries());
        offset = copy(out, offset, VarbitIDEntries_2.entries());
        offset = copy(out, offset, VarbitIDEntries_3.entries());
        offset = copy(out, offset, VarbitIDEntries_4.entries());
        offset = copy(out, offset, VarbitIDEntries_5.entries());
        offset = copy(out, offset, VarbitIDEntries_6.entries());
        offset = copy(out, offset, VarbitIDEntries_7.entries());
        offset = copy(out, offset, VarbitIDEntries_8.entries());
        offset = copy(out, offset, VarbitIDEntries_9.entries());
        offset = copy(out, offset, VarbitIDEntries_10.entries());
        offset = copy(out, offset, VarbitIDEntries_11.entries());
        offset = copy(out, offset, VarbitIDEntries_12.entries());
        offset = copy(out, offset, VarbitIDEntries_13.entries());
        offset = copy(out, offset, VarbitIDEntries_14.entries());
        offset = copy(out, offset, VarbitIDEntries_15.entries());
        offset = copy(out, offset, VarbitIDEntries_16.entries());
        offset = copy(out, offset, VarbitIDEntries_17.entries());
        offset = copy(out, offset, VarbitIDEntries_18.entries());
        offset = copy(out, offset, VarbitIDEntries_19.entries());
        offset = copy(out, offset, VarbitIDEntries_20.entries());
        offset = copy(out, offset, VarbitIDEntries_21.entries());
        offset = copy(out, offset, VarbitIDEntries_22.entries());
        offset = copy(out, offset, VarbitIDEntries_23.entries());
        offset = copy(out, offset, VarbitIDEntries_24.entries());
        offset = copy(out, offset, VarbitIDEntries_25.entries());
        return out;
    }

    private static int copy(GamevalEntry[] out, int offset, GamevalEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
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
        return Optional.empty();
    }

}
