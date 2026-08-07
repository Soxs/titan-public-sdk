package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

final class DbRowIDEntries_33 {
    private DbRowIDEntries_33() {}

    static GamevalEntry[] entries() {
        return new GamevalEntry[] {
            new GamevalEntry(16896, "SKILL_FEATURE_AGILITY_VAMPYRIUM_PILLAR_JUMPS", "skill_feature_agility_vampyrium_pillar_jumps", "rowtypes"),
            new GamevalEntry(16897, "SKILL_FEATURE_AGILITY_VAMPYRIUM_SLIDES", "skill_feature_agility_vampyrium_slides", "rowtypes"),
            new GamevalEntry(16899, "SKILL_FEATURE_FISHING_LEECHFIN", "skill_feature_fishing_leechfin", "rowtypes"),
            new GamevalEntry(16900, "SKILL_FEATURE_HITPOINTS_NECKLACEOFRUPTURE", "skill_feature_hitpoints_necklaceofrupture", "rowtypes"),
            new GamevalEntry(16901, "SKILL_FEATURE_HUNTER_STYMPHIKE", "skill_feature_hunter_stymphike", "rowtypes"),
            new GamevalEntry(16902, "SKILL_FEATURE_HUNTER_LETVEK", "skill_feature_hunter_letvek", "rowtypes"),
            new GamevalEntry(16903, "SKILL_FEATURE_SLAYER_VENATOR", "skill_feature_slayer_venator", "rowtypes"),
            new GamevalEntry(16904, "SKILL_FEATURE_STRENGTH_CRIMSON_KISTEN", "skill_feature_strength_crimson_kisten", "rowtypes"),
            new GamevalEntry(16905, "SKILL_FEATURE_WOODCUTTING_BLOODWOOD_LOGS", "skill_feature_woodcutting_bloodwood_logs", "rowtypes"),
            new GamevalEntry(16906, "CIRCLE_RADIUS_2", "circle_radius_2", "rowtypes"),
            new GamevalEntry(16907, "CIRCLE_RADIUS_3", "circle_radius_3", "rowtypes"),
            new GamevalEntry(16908, "CIRCLE_RADIUS_4", "circle_radius_4", "rowtypes"),
            new GamevalEntry(16909, "CIRCLE_RADIUS_5", "circle_radius_5", "rowtypes"),
            new GamevalEntry(16910, "CIRCLE_RADIUS_6", "circle_radius_6", "rowtypes"),
            new GamevalEntry(16911, "CIRCLE_RADIUS_7", "circle_radius_7", "rowtypes"),
            new GamevalEntry(16912, "CIRCLE_RADIUS_8", "circle_radius_8", "rowtypes"),
            new GamevalEntry(16913, "CIRCLE_RADIUS_9", "circle_radius_9", "rowtypes"),
            new GamevalEntry(16914, "COMBAT_INTERFACE_SLASHFLAIL", "combat_interface_slashflail", "rowtypes"),
            new GamevalEntry(16915, "UNCOOKED_BERRY_PIE_RECIPE", "uncooked_berry_pie_recipe", "rowtypes"),
            new GamevalEntry(16916, "PIE_SHELL_RECIPE", "pie_shell_recipe", "rowtypes"),
            new GamevalEntry(16917, "BREAD_RECIPE", "bread_recipe", "rowtypes"),
            new GamevalEntry(16918, "PASTRY_DOUGH_RECIPE", "pastry_dough_recipe", "rowtypes"),
            new GamevalEntry(16919, "BREAD_DOUGH_RECIPE", "bread_dough_recipe", "rowtypes"),
            new GamevalEntry(16920, "PMOON_RIVER1", "pmoon_river1", "rowtypes"),
            new GamevalEntry(16921, "PMOON_RIVER2", "pmoon_river2", "rowtypes"),
            new GamevalEntry(16922, "SOTFA_RIVER1", "sotfa_river1", "rowtypes"),
            new GamevalEntry(16923, "SOTFA_RIVER2", "sotfa_river2", "rowtypes"),
            new GamevalEntry(16924, "BRONZE_ARROWS", "bronze_arrows", "rowtypes"),
            new GamevalEntry(16925, "IRON_ARROWS", "iron_arrows", "rowtypes"),
            new GamevalEntry(16926, "STEEL_ARROWS", "steel_arrows", "rowtypes"),
            new GamevalEntry(16927, "MITHRIL_ARROWS", "mithril_arrows", "rowtypes"),
            new GamevalEntry(16928, "ADAMANT_ARROWS", "adamant_arrows", "rowtypes"),
            new GamevalEntry(16929, "RUNE_ARROWS", "rune_arrows", "rowtypes"),
            new GamevalEntry(16930, "AMETHYST_ARROWS", "amethyst_arrows", "rowtypes"),
            new GamevalEntry(16931, "DRAGON_ARROWS", "dragon_arrows", "rowtypes"),
            new GamevalEntry(16932, "SLAYER_BROAD_ARROWS", "slayer_broad_arrows", "rowtypes"),
            new GamevalEntry(16933, "ATLATL_DART", "atlatl_dart", "rowtypes"),
            new GamevalEntry(16934, "MAGIC_ENCHANT_SILVTHRILL_ROD", "magic_enchant_silvthrill_rod", "rowtypes"),
            new GamevalEntry(16935, "MAGIC_ENCHANT_SILVTHRILL_ROD2", "magic_enchant_silvthrill_rod2", "rowtypes"),
            new GamevalEntry(16936, "MAGIC_ENCHANT_SILVER_SICKLE_DIAMOND", "magic_enchant_silver_sickle_diamond", "rowtypes"),
            new GamevalEntry(16937, "SLAYERAREA_VAMPYRIUM", "slayerarea_vampyrium", "rowtypes"),
            new GamevalEntry(16938, "SLAYER_BOSS_TASK_MAGGOT_KING", "slayer_boss_task_maggot_king", "rowtypes"),
            new GamevalEntry(16939, "SLAYER_TASK_MAGGOT_KING", "slayer_task_maggot_king", "rowtypes"),
        };
    }

    static Optional<GamevalEntry> byId(int id) {
        for (GamevalEntry entry : entries()) {
            if (entry.id() == id) return Optional.of(entry);
        }
        return Optional.empty();
    }

}
