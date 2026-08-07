package net.titan.gamevals.internal;

import java.util.Optional;

import net.titan.gamevals.GamevalEntry;
import net.titan.gamevals.QuestEntry;

final class VarbitIDEntries_33 {
    private VarbitIDEntries_33() {}

    static GamevalEntry[] entries() {
        return new GamevalEntry[] {
            new GamevalEntry(16994, "SCRAMBLED_EGG_VIS", "scrambled_egg_vis", "varbittypes"),
            new GamevalEntry(16995, "SCRAMBLED_NAILS_GIVEN", "scrambled_nails_given", "varbittypes"),
            new GamevalEntry(17219, "SLAYER_LONGER_CUSTODIANS", "slayer_longer_custodians", "varbittypes"),
            new GamevalEntry(17223, "GEMSTONE_CRAB_INTRO", "gemstone_crab_intro", "varbittypes"),
            new GamevalEntry(17224, "TLATI_LOST_LOVER_XILO", "tlati_lost_lover_xilo", "varbittypes"),
            new GamevalEntry(17225, "TLATI_LOST_LOVER_FABIA", "tlati_lost_lover_fabia", "varbittypes"),
            new GamevalEntry(17226, "MET_AUBURN_MOUNTAIN_GUIDE", "met_auburn_mountain_guide", "varbittypes"),
            new GamevalEntry(17227, "ENT_TOTEMS_INTRO", "ent_totems_intro", "varbittypes"),
            new GamevalEntry(17228, "ENT_TOTEMS_BROKEN_CHAT", "ent_totems_broken_chat", "varbittypes"),
            new GamevalEntry(17229, "ENT_TOTEMS_CARVE_CHAT", "ent_totems_carve_chat", "varbittypes"),
            new GamevalEntry(17230, "ENT_TOTEMS_DECORATE_CHAT", "ent_totems_decorate_chat", "varbittypes"),
            new GamevalEntry(17231, "FORESTRY_FORESTER_MET", "forestry_forester_met", "varbittypes"),
            new GamevalEntry(17282, "AUBURN_ADVERTISEMENTS_LAST", "auburn_advertisements_last", "varbittypes"),
            new GamevalEntry(17289, "CUSTODIAN_LOREBOOK", "custodian_lorebook", "varbittypes"),
            new GamevalEntry(17290, "AUBURN_ECOLOGICAL_REPORTS", "auburn_ecological_reports", "varbittypes"),
            new GamevalEntry(17291, "BOWSTRING_SPOOL_CHARGES", "bowstring_spool_charges", "varbittypes"),
            new GamevalEntry(17293, "BOWSTRING_SPOOL_SIZE", "bowstring_spool_size", "varbittypes"),
            new GamevalEntry(17299, "LEAGUE_RANK_NOTIFICATION", "league_rank_notification", "varbittypes"),
            new GamevalEntry(17300, "LEAGUE_SHOP_NOTED_ITEMS_DISABLED", "league_shop_noted_items_disabled", "varbittypes"),
            new GamevalEntry(17301, "LEAGUE_RELIC_SELECTION_6", "league_relic_selection_6", "varbittypes"),
            new GamevalEntry(17302, "LEAGUE_RELIC_SELECTION_7", "league_relic_selection_7", "varbittypes"),
            new GamevalEntry(17303, "LEAGUE_SUPERIORS_AUTO_UNLOCKED", "league_superiors_auto_unlocked", "varbittypes"),
            new GamevalEntry(17304, "LEAGUE_DANGEROUS_TELEPORT_WARNING_DISABLE", "league_dangerous_teleport_warning_disable", "varbittypes"),
            new GamevalEntry(17305, "LEAGUE_LAST_TELEPORT_INSIDE_HOUSE", "league_last_teleport_inside_house", "varbittypes"),
            new GamevalEntry(17306, "LEAGUE_BANKERS_NOTE_MODE", "league_bankers_note_mode", "varbittypes"),
            new GamevalEntry(17307, "LEAGUE_BANKERS_NOTE_QUANTITY", "league_bankers_note_quantity", "varbittypes"),
            new GamevalEntry(17308, "LEAGUE_GUARDIAN_AOE_DISABLED", "league_guardian_aoe_disabled", "varbittypes"),
            new GamevalEntry(17309, "LEAGUE_MESSAGE_BUFFER", "league_message_buffer", "varbittypes"),
            new GamevalEntry(17310, "LEAGUE_TASKS_IS_SEARCHING", "league_tasks_is_searching", "varbittypes"),
            new GamevalEntry(17311, "LEAGUE_TASKS_SEARCH_LISTEN_FOR_KEYBOARD", "league_tasks_search_listen_for_keyboard", "varbittypes"),
            new GamevalEntry(17312, "LEAGUE_TASKS_CHATBOX_OPENED", "league_tasks_chatbox_opened", "varbittypes"),
            new GamevalEntry(17313, "LEAGUE_TOB_MODE", "league_tob_mode", "varbittypes"),
            new GamevalEntry(17314, "LEAGUE_COX_CHALLENGE_MODE", "league_cox_challenge_mode", "varbittypes"),
            new GamevalEntry(17315, "DEATH_ANIMATION_OVERRIDE", "death_animation_override", "varbittypes"),
            new GamevalEntry(17316, "VENGEANCE_ANIMATION_OVERRIDE", "vengeance_animation_override", "varbittypes"),
            new GamevalEntry(17317, "HIGH_ALCHEMY_ANIMATION_OVERRIDE", "high_alchemy_animation_override", "varbittypes"),
            new GamevalEntry(17318, "LEAGUE_GUARDIAN_ACTIVE", "league_guardian_active", "varbittypes"),
            new GamevalEntry(17319, "LEAGUE_GUARDIAN_LAST_STYLE", "league_guardian_last_style", "varbittypes"),
            new GamevalEntry(17326, "LEAGUE_4_HIGH_ALCH_ANIMATION_UNLOCKED", "league_4_high_alch_animation_unlocked", "varbittypes"),
            new GamevalEntry(17327, "LEAGUE_4_VENGEANCE_ANIMATION_UNLOCKED", "league_4_vengeance_animation_unlocked", "varbittypes"),
            new GamevalEntry(17328, "LEAGUE_4_DEATH_ANIMATION_UNLOCKED", "league_4_death_animation_unlocked", "varbittypes"),
            new GamevalEntry(17329, "LEAGUE_4_HOME_TELEPORT_UNLOCKED", "league_4_home_teleport_unlocked", "varbittypes"),
            new GamevalEntry(17347, "ENT_TOTEMS_SITE_1_TRAIL_BUFF_ACTIVE", "ent_totems_site_1_trail_buff_active", "varbittypes"),
            new GamevalEntry(17348, "ENT_TOTEMS_SITE_2_TRAIL_BUFF_ACTIVE", "ent_totems_site_2_trail_buff_active", "varbittypes"),
            new GamevalEntry(17351, "ENT_TOTEMS_SITE_3_TRAIL_BUFF_ACTIVE", "ent_totems_site_3_trail_buff_active", "varbittypes"),
            new GamevalEntry(17352, "ENT_TOTEMS_SITE_4_TRAIL_BUFF_ACTIVE", "ent_totems_site_4_trail_buff_active", "varbittypes"),
            new GamevalEntry(17353, "ENT_TOTEMS_SITE_5_TRAIL_BUFF_ACTIVE", "ent_totems_site_5_trail_buff_active", "varbittypes"),
        };
    }

    static Optional<GamevalEntry> byId(int id) {
        for (GamevalEntry entry : entries()) {
            if (entry.id() == id) return Optional.of(entry);
        }
        return Optional.empty();
    }

}
