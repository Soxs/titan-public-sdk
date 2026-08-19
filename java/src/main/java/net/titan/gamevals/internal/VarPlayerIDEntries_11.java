package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

final class VarPlayerIDEntries_11 {
    private VarPlayerIDEntries_11() {}

    static GamevalEntry[] entries() {
        return new GamevalEntry[] {
            new GamevalEntry(5632, "CASTLE_DRAKAN_ROOM_STATUS_1", "castle_drakan_room_status_1", "varptypes"),
            new GamevalEntry(5633, "CASTLE_DRAKAN_ROOM_STATUS_2", "castle_drakan_room_status_2", "varptypes"),
            new GamevalEntry(5634, "CASTLE_DRAKAN_ROOM_STATUS_3", "castle_drakan_room_status_3", "varptypes"),
            new GamevalEntry(5635, "CASTLE_DRAKAN_ROOM_STATUS_4", "castle_drakan_room_status_4", "varptypes"),
            new GamevalEntry(5636, "CASTLE_DRAKAN_DOOR_STATUS_1", "castle_drakan_door_status_1", "varptypes"),
            new GamevalEntry(5637, "CASTLE_DRAKAN_DOOR_STATUS_2", "castle_drakan_door_status_2", "varptypes"),
            new GamevalEntry(5638, "CASTLE_DRAKAN_DOOR_STATUS_3", "castle_drakan_door_status_3", "varptypes"),
            new GamevalEntry(5639, "CASTLE_DRAKAN_DOOR_STATUS_4", "castle_drakan_door_status_4", "varptypes"),
            new GamevalEntry(5640, "CASTLE_DRAKAN_ENEMY_STATUS_1", "castle_drakan_enemy_status_1", "varptypes"),
            new GamevalEntry(5641, "CASTLE_DRAKAN_ENEMY_STATUS_2", "castle_drakan_enemy_status_2", "varptypes"),
            new GamevalEntry(5642, "CASTLE_DRAKAN_CONSUMABLE_STATUS_1", "castle_drakan_consumable_status_1", "varptypes"),
            new GamevalEntry(5643, "CASTLE_DRAKAN_CONSUMABLE_STATUS_2", "castle_drakan_consumable_status_2", "varptypes"),
            new GamevalEntry(5644, "SANGVESTI_GENERAL", "sangvesti_general", "varptypes"),
            new GamevalEntry(5645, "SANGVESTI_GENERAL_2", "sangvesti_general_2", "varptypes"),
            new GamevalEntry(5661, "SOTFA_FOREST_GENERAL", "sotfa_forest_general", "varptypes"),
            new GamevalEntry(5662, "VAMPIRE_SECONDARY", "vampire_secondary", "varptypes"),
            new GamevalEntry(5663, "AGILITY_SLIDE_XP_COOLDOWN", "agility_slide_xp_cooldown", "varptypes"),
            new GamevalEntry(5665, "MAGGOT_KING_ATTEMPTS", "maggot_king_attempts", "varptypes"),
            new GamevalEntry(5666, "DRAKAN_ENCOUNTER_ATTEMPTS", "drakan_encounter_attempts", "varptypes"),
            new GamevalEntry(5667, "DRAKAN_FINAL_MIN_HP", "drakan_final_min_hp", "varptypes"),
            new GamevalEntry(5668, "SANGVESTI_GENERAL_3", "sangvesti_general_3", "varptypes"),
            new GamevalEntry(5669, "SANGVESTI_PLAYER_POS", "sangvesti_player_pos", "varptypes"),
            new GamevalEntry(5670, "SANGVESTI_PLAYER_LAST_DEATH_POS", "sangvesti_player_last_death_pos", "varptypes"),
            new GamevalEntry(5671, "AFK_BLOODWOOD_TREE", "afk_bloodwood_tree", "varptypes"),
            new GamevalEntry(5673, "CA_TASK_COMPLETED_20", "ca_task_completed_20", "varptypes"),
            new GamevalEntry(5705, "WYRMSCRAIG_VARP", "wyrmscraig_varp", "varptypes"),
            new GamevalEntry(5706, "GOAT_PIT_VARP", "goat_pit_varp", "varptypes"),
            new GamevalEntry(5709, "GOLEM_CRAFTING_CORE", "golem_crafting_core", "varptypes"),
            new GamevalEntry(5710, "GOLEM_CRAFTING_SIDE", "golem_crafting_side", "varptypes"),
            new GamevalEntry(5712, "TOTAL_MAD_ANGEL_KILLS", "total_mad_angel_kills", "varptypes"),
            new GamevalEntry(5715, "FFG_PRIMARY", "ffg_primary", "varptypes"),
            new GamevalEntry(5719, "SLAYER_MODIFIERS", "slayer_modifiers", "varptypes"),
            new GamevalEntry(5720, "SLAYER_MORTIMER_TASKS_COMPLETED", "slayer_mortimer_tasks_completed", "varptypes"),
            new GamevalEntry(5721, "SLAYER_REWARDS_BLOCKED_14", "slayer_rewards_blocked_14", "varptypes"),
            new GamevalEntry(5722, "SLAYER_MISC_PERM", "slayer_misc_perm", "varptypes"),
            new GamevalEntry(5723, "SLAYER_MISC_PERM_2", "slayer_misc_perm_2", "varptypes"),
            new GamevalEntry(5724, "SLAYER_MISC_PERM_3", "slayer_misc_perm_3", "varptypes"),
            new GamevalEntry(5725, "SKILLPET_RUNECRAFTING_TRACKING", "skillpet_runecrafting_tracking", "varptypes"),
        };
    }

    static Optional<GamevalEntry> byId(int id) {
        for (GamevalEntry entry : entries()) {
            if (entry.id() == id) return Optional.of(entry);
        }
        return Optional.empty();
    }

}
