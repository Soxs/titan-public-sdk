package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class AnimationIDEntries {
    private AnimationIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            AnimationIDEntries_0.entries(),
            AnimationIDEntries_1.entries(),
            AnimationIDEntries_2.entries(),
            AnimationIDEntries_3.entries(),
            AnimationIDEntries_4.entries(),
            AnimationIDEntries_5.entries(),
            AnimationIDEntries_6.entries(),
            AnimationIDEntries_7.entries(),
            AnimationIDEntries_8.entries(),
            AnimationIDEntries_9.entries(),
            AnimationIDEntries_10.entries(),
            AnimationIDEntries_11.entries(),
            AnimationIDEntries_12.entries(),
            AnimationIDEntries_13.entries(),
            AnimationIDEntries_14.entries(),
            AnimationIDEntries_15.entries(),
            AnimationIDEntries_16.entries(),
            AnimationIDEntries_17.entries(),
            AnimationIDEntries_18.entries(),
            AnimationIDEntries_19.entries(),
            AnimationIDEntries_20.entries(),
            AnimationIDEntries_21.entries(),
            AnimationIDEntries_22.entries(),
            AnimationIDEntries_23.entries(),
            AnimationIDEntries_24.entries(),
            AnimationIDEntries_25.entries(),
            AnimationIDEntries_26.entries(),
            AnimationIDEntries_27.entries(),
            AnimationIDEntries_28.entries(),
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
