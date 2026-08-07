package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

public final class ObjectIDEntries {
    private ObjectIDEntries() {}

    public static GamevalEntry[] entries() {
        GamevalEntry[][] parts = {
            ObjectIDEntries_0.entries(),
            ObjectIDEntries_1.entries(),
            ObjectIDEntries_2.entries(),
            ObjectIDEntries_3.entries(),
            ObjectIDEntries_4.entries(),
            ObjectIDEntries_5.entries(),
            ObjectIDEntries_6.entries(),
            ObjectIDEntries_7.entries(),
            ObjectIDEntries_8.entries(),
            ObjectIDEntries_9.entries(),
            ObjectIDEntries_10.entries(),
            ObjectIDEntries_11.entries(),
            ObjectIDEntries_12.entries(),
            ObjectIDEntries_13.entries(),
            ObjectIDEntries_14.entries(),
            ObjectIDEntries_15.entries(),
            ObjectIDEntries_16.entries(),
            ObjectIDEntries_17.entries(),
            ObjectIDEntries_18.entries(),
            ObjectIDEntries_19.entries(),
            ObjectIDEntries_20.entries(),
            ObjectIDEntries_21.entries(),
            ObjectIDEntries_22.entries(),
            ObjectIDEntries_23.entries(),
            ObjectIDEntries_24.entries(),
            ObjectIDEntries_25.entries(),
            ObjectIDEntries_26.entries(),
            ObjectIDEntries_27.entries(),
            ObjectIDEntries_28.entries(),
            ObjectIDEntries_29.entries(),
            ObjectIDEntries_30.entries(),
            ObjectIDEntries_31.entries(),
            ObjectIDEntries_32.entries(),
            ObjectIDEntries_33.entries(),
            ObjectIDEntries_34.entries(),
            ObjectIDEntries_35.entries(),
            ObjectIDEntries_36.entries(),
            ObjectIDEntries_37.entries(),
            ObjectIDEntries_38.entries(),
            ObjectIDEntries_39.entries(),
            ObjectIDEntries_40.entries(),
            ObjectIDEntries_41.entries(),
            ObjectIDEntries_42.entries(),
            ObjectIDEntries_43.entries(),
            ObjectIDEntries_44.entries(),
            ObjectIDEntries_45.entries(),
            ObjectIDEntries_46.entries(),
            ObjectIDEntries_47.entries(),
            ObjectIDEntries_48.entries(),
            ObjectIDEntries_49.entries(),
            ObjectIDEntries_50.entries(),
            ObjectIDEntries_51.entries(),
            ObjectIDEntries_52.entries(),
            ObjectIDEntries_53.entries(),
            ObjectIDEntries_54.entries(),
            ObjectIDEntries_55.entries(),
            ObjectIDEntries_56.entries(),
            ObjectIDEntries_57.entries(),
            ObjectIDEntries_58.entries(),
            ObjectIDEntries_59.entries(),
            ObjectIDEntries_60.entries(),
            ObjectIDEntries_61.entries(),
            ObjectIDEntries_62.entries(),
            ObjectIDEntries_63.entries(),
            ObjectIDEntries_64.entries(),
            ObjectIDEntries_65.entries(),
            ObjectIDEntries_66.entries(),
            ObjectIDEntries_67.entries(),
            ObjectIDEntries_68.entries(),
            ObjectIDEntries_69.entries(),
            ObjectIDEntries_70.entries(),
            ObjectIDEntries_71.entries(),
            ObjectIDEntries_72.entries(),
            ObjectIDEntries_73.entries(),
            ObjectIDEntries_74.entries(),
            ObjectIDEntries_75.entries(),
            ObjectIDEntries_76.entries(),
            ObjectIDEntries_77.entries(),
            ObjectIDEntries_78.entries(),
            ObjectIDEntries_79.entries(),
            ObjectIDEntries_80.entries(),
            ObjectIDEntries_81.entries(),
            ObjectIDEntries_82.entries(),
            ObjectIDEntries_83.entries(),
            ObjectIDEntries_84.entries(),
            ObjectIDEntries_85.entries(),
            ObjectIDEntries_86.entries(),
            ObjectIDEntries_87.entries(),
            ObjectIDEntries_88.entries(),
            ObjectIDEntries_89.entries(),
            ObjectIDEntries_90.entries(),
            ObjectIDEntries_91.entries(),
            ObjectIDEntries_92.entries(),
            ObjectIDEntries_93.entries(),
            ObjectIDEntries_94.entries(),
            ObjectIDEntries_95.entries(),
            ObjectIDEntries_96.entries(),
            ObjectIDEntries_97.entries(),
            ObjectIDEntries_98.entries(),
            ObjectIDEntries_99.entries(),
            ObjectIDEntries_100.entries(),
            ObjectIDEntries_101.entries(),
            ObjectIDEntries_102.entries(),
            ObjectIDEntries_103.entries(),
            ObjectIDEntries_104.entries(),
            ObjectIDEntries_105.entries(),
            ObjectIDEntries_106.entries(),
            ObjectIDEntries_107.entries(),
            ObjectIDEntries_108.entries(),
            ObjectIDEntries_109.entries(),
            ObjectIDEntries_110.entries(),
            ObjectIDEntries_111.entries(),
            ObjectIDEntries_112.entries(),
            ObjectIDEntries_113.entries(),
            ObjectIDEntries_114.entries(),
            ObjectIDEntries_115.entries(),
            ObjectIDEntries_116.entries(),
            ObjectIDEntries_117.entries(),
            ObjectIDEntries_118.entries(),
            ObjectIDEntries_119.entries(),
            ObjectIDEntries_120.entries(),
            ObjectIDEntries_121.entries(),
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
        hit = ObjectIDEntries_0.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_1.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_2.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_3.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_4.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_5.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_6.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_7.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_8.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_9.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_10.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_11.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_12.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_13.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_14.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_15.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_16.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_17.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_18.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_19.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_20.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_21.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_22.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_23.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_24.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_25.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_26.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_27.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_28.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_29.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_30.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_31.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_32.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_33.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_34.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_35.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_36.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_37.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_38.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_39.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_40.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_41.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_42.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_43.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_44.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_45.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_46.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_47.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_48.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_49.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_50.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_51.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_52.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_53.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_54.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_55.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_56.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_57.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_58.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_59.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_60.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_61.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_62.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_63.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_64.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_65.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_66.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_67.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_68.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_69.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_70.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_71.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_72.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_73.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_74.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_75.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_76.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_77.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_78.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_79.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_80.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_81.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_82.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_83.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_84.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_85.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_86.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_87.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_88.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_89.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_90.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_91.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_92.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_93.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_94.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_95.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_96.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_97.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_98.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_99.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_100.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_101.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_102.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_103.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_104.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_105.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_106.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_107.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_108.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_109.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_110.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_111.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_112.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_113.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_114.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_115.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_116.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_117.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_118.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_119.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_120.byId(id);
        if (hit.isPresent()) return hit;
        hit = ObjectIDEntries_121.byId(id);
        if (hit.isPresent()) return hit;
        return Optional.empty();
    }

}
