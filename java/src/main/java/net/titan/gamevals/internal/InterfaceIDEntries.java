package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class InterfaceIDEntries {
    private InterfaceIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            InterfaceIDEntries_0.entries(),
            InterfaceIDEntries_1.entries(),
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
        hit = InterfaceIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = InterfaceIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
