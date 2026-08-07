package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

final class VarbitIDEntries_14 {
    private VarbitIDEntries_14() {}

    static GamevalEntry[] entries() {
        return new GamevalEntry[] {
            new GamevalEntry(7253, "COLLECTION_ITEM_SNAKEBOSS_SCALE", "collection_item_snakeboss_scale", "varbittypes"),
            new GamevalEntry(7254, "COLLECTION_ITEM_OLMPET", "collection_item_olmpet", "varbittypes"),
            new GamevalEntry(7255, "MYQ5", "myq5", "varbittypes"),
            new GamevalEntry(7256, "COLLECTION_ITEM_TWISTED_BOW", "collection_item_twisted_bow", "varbittypes"),
            new GamevalEntry(7257, "COLLECTION_ITEM_ELDER_MAUL", "collection_item_elder_maul", "varbittypes"),
            new GamevalEntry(7258, "COLLECTION_ITEM_KODAI_INSIGNIA", "collection_item_kodai_insignia", "varbittypes"),
            new GamevalEntry(7259, "COLLECTION_ITEM_DRAGON_CLAWS", "collection_item_dragon_claws", "varbittypes"),
            new GamevalEntry(7279, "COLLECTION_ITEM_VERZIKPET", "collection_item_verzikpet", "varbittypes"),
            new GamevalEntry(7280, "COLLECTION_ITEM_SCYTHE_OF_VITUR_UNCHARGED", "collection_item_scythe_of_vitur_uncharged", "varbittypes"),
            new GamevalEntry(7281, "COLLECTION_ITEM_GHRAZI_RAPIER", "collection_item_ghrazi_rapier", "varbittypes"),
            new GamevalEntry(7282, "COLLECTION_ITEM_SANGUINESTI_STAFF_UNCHARGED", "collection_item_sanguinesti_staff_uncharged", "varbittypes"),
            new GamevalEntry(7287, "COLLECTION_ITEM_VIAL_BLOOD", "collection_item_vial_blood", "varbittypes"),
            new GamevalEntry(7288, "COLLECTION_ITEM_SINHAZA_SHROUD_TIER1", "collection_item_sinhaza_shroud_tier1", "varbittypes"),
            new GamevalEntry(7289, "COLLECTION_ITEM_SINHAZA_SHROUD_TIER2", "collection_item_sinhaza_shroud_tier2", "varbittypes"),
            new GamevalEntry(7290, "COLLECTION_ITEM_SINHAZA_SHROUD_TIER3", "collection_item_sinhaza_shroud_tier3", "varbittypes"),
            new GamevalEntry(7291, "COLLECTION_ITEM_SINHAZA_SHROUD_TIER4", "collection_item_sinhaza_shroud_tier4", "varbittypes"),
            new GamevalEntry(7292, "COLLECTION_ITEM_SINHAZA_SHROUD_TIER5", "collection_item_sinhaza_shroud_tier5", "varbittypes"),
            new GamevalEntry(7293, "COLLECTION_ITEM_WILDERNESS_CAPE_ZERO", "collection_item_wilderness_cape_zero", "varbittypes"),
            new GamevalEntry(7294, "COLLECTION_ITEM_WILDERNESS_CAPE_I", "collection_item_wilderness_cape_i", "varbittypes"),
            new GamevalEntry(7295, "COLLECTION_ITEM_WILDERNESS_CAPE_X", "collection_item_wilderness_cape_x", "varbittypes"),
            new GamevalEntry(7296, "COLLECTION_ITEM_CHEFS_HAT_GOLD", "collection_item_chefs_hat_gold", "varbittypes"),
            new GamevalEntry(7297, "COLLECTION_ITEM_GOLDEN_APRON", "collection_item_golden_apron", "varbittypes"),
        };
    }

    static Optional<GamevalEntry> byId(int id) {
        for (GamevalEntry entry : entries()) {
            if (entry.id() == id) return Optional.of(entry);
        }
        return Optional.empty();
    }

}
