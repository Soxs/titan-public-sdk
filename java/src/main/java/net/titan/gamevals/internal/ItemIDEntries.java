package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class ItemIDEntries {
    private ItemIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            ItemIDEntries_0.entries(),
            ItemIDEntries_1.entries(),
            ItemIDEntries_2.entries(),
            ItemIDEntries_3.entries(),
            ItemIDEntries_4.entries(),
            ItemIDEntries_5.entries(),
            ItemIDEntries_6.entries(),
            ItemIDEntries_7.entries(),
            ItemIDEntries_8.entries(),
            ItemIDEntries_9.entries(),
            ItemIDEntries_10.entries(),
            ItemIDEntries_11.entries(),
            ItemIDEntries_12.entries(),
            ItemIDEntries_13.entries(),
            ItemIDEntries_14.entries(),
            ItemIDEntries_15.entries(),
            ItemIDEntries_16.entries(),
            ItemIDEntries_17.entries(),
            ItemIDEntries_18.entries(),
            ItemIDEntries_19.entries(),
            ItemIDEntries_20.entries(),
            ItemIDEntries_21.entries(),
            ItemIDEntries_22.entries(),
            ItemIDEntries_23.entries(),
            ItemIDEntries_24.entries(),
            ItemIDEntries_25.entries(),
            ItemIDEntries_26.entries(),
            ItemIDEntries_27.entries(),
            ItemIDEntries_28.entries(),
            ItemIDEntries_29.entries(),
            ItemIDEntries_30.entries(),
            ItemIDEntries_31.entries(),
            ItemIDEntries_32.entries(),
            ItemIDEntries_33.entries(),
            ItemIDEntries_34.entries(),
            ItemIDEntries_35.entries(),
            ItemIDEntries_36.entries(),
            ItemIDEntries_37.entries(),
            ItemIDEntries_38.entries(),
            ItemIDEntries_39.entries(),
            ItemIDEntries_40.entries(),
            ItemIDEntries_41.entries(),
            ItemIDEntries_42.entries(),
            ItemIDEntries_43.entries(),
            ItemIDEntries_44.entries(),
            ItemIDEntries_45.entries(),
            ItemIDEntries_46.entries(),
            ItemIDEntries_47.entries(),
            ItemIDEntries_48.entries(),
            ItemIDEntries_49.entries(),
            ItemIDEntries_50.entries(),
            ItemIDEntries_51.entries(),
            ItemIDEntries_52.entries(),
            ItemIDEntries_53.entries(),
            ItemIDEntries_54.entries(),
            ItemIDEntries_55.entries(),
            ItemIDEntries_56.entries(),
            ItemIDEntries_57.entries(),
            ItemIDEntries_58.entries(),
            ItemIDEntries_59.entries(),
            ItemIDEntries_60.entries(),
            ItemIDEntries_61.entries(),
            ItemIDEntries_62.entries(),
            ItemIDEntries_63.entries(),
            ItemIDEntries_64.entries(),
            ItemIDEntries_65.entries(),
            ItemIDEntries_66.entries(),
            ItemIDEntries_67.entries(),
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
        hit = ItemIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_3.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_4.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_5.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_6.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_7.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_8.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_9.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_10.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_11.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_12.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_13.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_14.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_15.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_16.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_17.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_18.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_19.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_20.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_21.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_22.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_23.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_24.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_25.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_26.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_27.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_28.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_29.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_30.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_31.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_32.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_33.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_34.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_35.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_36.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_37.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_38.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_39.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_40.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_41.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_42.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_43.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_44.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_45.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_46.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_47.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_48.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_49.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_50.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_51.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_52.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_53.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_54.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_55.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_56.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_57.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_58.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_59.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_60.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_61.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_62.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_63.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_64.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_65.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_66.byId(id);
        if (hit.isPresent()) return hit;
        hit = ItemIDEntries_67.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
