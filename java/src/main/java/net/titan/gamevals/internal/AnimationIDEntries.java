package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class AnimationIDEntries {
    private AnimationIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[] out = new GamevalEntry[14408];
        int offset = 0;
        offset = copy(out, offset, AnimationIDEntries_0.entries());
        offset = copy(out, offset, AnimationIDEntries_1.entries());
        offset = copy(out, offset, AnimationIDEntries_2.entries());
        offset = copy(out, offset, AnimationIDEntries_3.entries());
        offset = copy(out, offset, AnimationIDEntries_4.entries());
        offset = copy(out, offset, AnimationIDEntries_5.entries());
        offset = copy(out, offset, AnimationIDEntries_6.entries());
        offset = copy(out, offset, AnimationIDEntries_7.entries());
        offset = copy(out, offset, AnimationIDEntries_8.entries());
        offset = copy(out, offset, AnimationIDEntries_9.entries());
        offset = copy(out, offset, AnimationIDEntries_10.entries());
        offset = copy(out, offset, AnimationIDEntries_11.entries());
        offset = copy(out, offset, AnimationIDEntries_12.entries());
        offset = copy(out, offset, AnimationIDEntries_13.entries());
        offset = copy(out, offset, AnimationIDEntries_14.entries());
        offset = copy(out, offset, AnimationIDEntries_15.entries());
        offset = copy(out, offset, AnimationIDEntries_16.entries());
        offset = copy(out, offset, AnimationIDEntries_17.entries());
        offset = copy(out, offset, AnimationIDEntries_18.entries());
        offset = copy(out, offset, AnimationIDEntries_19.entries());
        offset = copy(out, offset, AnimationIDEntries_20.entries());
        offset = copy(out, offset, AnimationIDEntries_21.entries());
        offset = copy(out, offset, AnimationIDEntries_22.entries());
        offset = copy(out, offset, AnimationIDEntries_23.entries());
        offset = copy(out, offset, AnimationIDEntries_24.entries());
        offset = copy(out, offset, AnimationIDEntries_25.entries());
        offset = copy(out, offset, AnimationIDEntries_26.entries());
        offset = copy(out, offset, AnimationIDEntries_27.entries());
        offset = copy(out, offset, AnimationIDEntries_28.entries());
        return out;
    }

    private static int copy(GamevalEntry[] out, int offset, GamevalEntry[] part) {
        System.arraycopy(part, 0, out, offset, part.length);
        return offset + part.length;
    }

    public static Optional<GamevalEntry> byId(int id) {
        Optional<GamevalEntry> hit;
        hit = AnimationIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_3.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_4.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_5.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_6.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_7.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_8.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_9.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_10.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_11.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_12.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_13.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_14.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_15.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_16.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_17.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_18.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_19.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_20.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_21.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_22.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_23.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_24.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_25.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_26.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_27.byId(id);
        if (hit.isPresent()) return hit;
        hit = AnimationIDEntries_28.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
