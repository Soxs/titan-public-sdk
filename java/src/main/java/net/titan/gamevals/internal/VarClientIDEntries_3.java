package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

final class VarClientIDEntries_3 {
    private VarClientIDEntries_3() {}

    static GamevalEntry[] entries() {
        return new GamevalEntry[] {
            new GamevalEntry(1536, "SLOTINFO_24_LONG", "slotinfo_24_long", "varctypes"),
            new GamevalEntry(1537, "SLOTINFO_25_LONG", "slotinfo_25_long", "varctypes"),
            new GamevalEntry(1538, "SLOTINFO_26_LONG", "slotinfo_26_long", "varctypes"),
            new GamevalEntry(1539, "SLOTINFO_27_LONG", "slotinfo_27_long", "varctypes"),
            new GamevalEntry(1540, "SLOTINFO_OTHER_00_LONG", "slotinfo_other_00_long", "varctypes"),
            new GamevalEntry(1541, "SLOTINFO_OTHER_01_LONG", "slotinfo_other_01_long", "varctypes"),
            new GamevalEntry(1542, "SLOTINFO_OTHER_02_LONG", "slotinfo_other_02_long", "varctypes"),
            new GamevalEntry(1543, "SLOTINFO_OTHER_03_LONG", "slotinfo_other_03_long", "varctypes"),
            new GamevalEntry(1544, "SLOTINFO_OTHER_04_LONG", "slotinfo_other_04_long", "varctypes"),
            new GamevalEntry(1545, "SLOTINFO_OTHER_05_LONG", "slotinfo_other_05_long", "varctypes"),
            new GamevalEntry(1546, "SLOTINFO_OTHER_06_LONG", "slotinfo_other_06_long", "varctypes"),
            new GamevalEntry(1547, "SLOTINFO_OTHER_07_LONG", "slotinfo_other_07_long", "varctypes"),
            new GamevalEntry(1548, "SLOTINFO_OTHER_08_LONG", "slotinfo_other_08_long", "varctypes"),
            new GamevalEntry(1549, "SLOTINFO_OTHER_09_LONG", "slotinfo_other_09_long", "varctypes"),
            new GamevalEntry(1550, "SLOTINFO_OTHER_10_LONG", "slotinfo_other_10_long", "varctypes"),
            new GamevalEntry(1551, "SLOTINFO_OTHER_11_LONG", "slotinfo_other_11_long", "varctypes"),
            new GamevalEntry(1552, "SLOTINFO_OTHER_12_LONG", "slotinfo_other_12_long", "varctypes"),
            new GamevalEntry(1553, "SLOTINFO_OTHER_13_LONG", "slotinfo_other_13_long", "varctypes"),
            new GamevalEntry(1554, "SLOTINFO_OTHER_14_LONG", "slotinfo_other_14_long", "varctypes"),
            new GamevalEntry(1555, "SLOTINFO_OTHER_15_LONG", "slotinfo_other_15_long", "varctypes"),
            new GamevalEntry(1556, "SLOTINFO_OTHER_16_LONG", "slotinfo_other_16_long", "varctypes"),
            new GamevalEntry(1557, "SLOTINFO_OTHER_17_LONG", "slotinfo_other_17_long", "varctypes"),
            new GamevalEntry(1558, "SLOTINFO_OTHER_18_LONG", "slotinfo_other_18_long", "varctypes"),
            new GamevalEntry(1559, "SLOTINFO_OTHER_19_LONG", "slotinfo_other_19_long", "varctypes"),
            new GamevalEntry(1560, "SLOTINFO_OTHER_20_LONG", "slotinfo_other_20_long", "varctypes"),
            new GamevalEntry(1561, "SLOTINFO_OTHER_21_LONG", "slotinfo_other_21_long", "varctypes"),
            new GamevalEntry(1562, "SLOTINFO_OTHER_22_LONG", "slotinfo_other_22_long", "varctypes"),
            new GamevalEntry(1563, "SLOTINFO_OTHER_23_LONG", "slotinfo_other_23_long", "varctypes"),
            new GamevalEntry(1564, "SLOTINFO_OTHER_24_LONG", "slotinfo_other_24_long", "varctypes"),
            new GamevalEntry(1565, "SLOTINFO_OTHER_25_LONG", "slotinfo_other_25_long", "varctypes"),
            new GamevalEntry(1566, "SLOTINFO_OTHER_26_LONG", "slotinfo_other_26_long", "varctypes"),
            new GamevalEntry(1567, "SLOTINFO_OTHER_27_LONG", "slotinfo_other_27_long", "varctypes"),
            new GamevalEntry(1568, "PVPTUT_UI_SELECTED_TUTORIAL", "pvptut_ui_selected_tutorial", "varctypes"),
        };
    }

    static Optional<GamevalEntry> byId(int id) {
        for (GamevalEntry entry : entries()) {
            if (entry.id() == id) return Optional.of(entry);
        }
        return Optional.empty();
    }

}
