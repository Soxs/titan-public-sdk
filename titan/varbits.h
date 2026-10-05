/// @file titan/varbits.h
/// @brief Varbit ID constants for reading game configuration state.
///
/// Canonical SDK location for the `Varbits::*` constant catalog. Mirrors
/// RuneLite's `net.runelite.api.Varbits`. Values are packed bit-fields
/// stored within VarPlayer (varp) values; the game client uses varbit
/// IDs to extract specific flags / counters from the underlying varp
/// array.
///
/// Client-internal code can include `client/game/types/varbits.h`, which
/// forwards this header into the project's top-level `Varbits` namespace
/// to match the convention used by `prayer.h`, `skill.h`, etc.
///
/// `nameOf(int)` is defined inline in this header (not in a .cpp) so
/// plugin TUs can resolve it directly without linking a client-side
/// translation unit.

#pragma once

namespace titan {

/// Known varbit IDs grouped by gameplay system. Values are stable across
/// revisions in the vast majority of cases; when a Jagex update shifts
/// one, update both the constant here AND the corresponding `case` in
/// `nameOf` below.
namespace Varbits {

// --- Prayers -- standard prayer book ---
constexpr int QUICK_PRAYER              = 4103;
constexpr int PRAYER_THICK_SKIN         = 4104;
constexpr int PRAYER_BURST_OF_STRENGTH  = 4105;
constexpr int PRAYER_CLARITY_OF_THOUGHT = 4106;
constexpr int PRAYER_SHARP_EYE          = 4122;
constexpr int PRAYER_MYSTIC_WILL        = 4123;
constexpr int PRAYER_ROCK_SKIN          = 4107;
constexpr int PRAYER_SUPERHUMAN_STRENGTH = 4108;
constexpr int PRAYER_IMPROVED_REFLEXES  = 4109;
constexpr int PRAYER_RAPID_RESTORE      = 4110;
constexpr int PRAYER_RAPID_HEAL         = 4111;
constexpr int PRAYER_PROTECT_ITEM       = 4112;
constexpr int PRAYER_HAWK_EYE           = 4124;
constexpr int PRAYER_MYSTIC_LORE        = 4125;
constexpr int PRAYER_STEEL_SKIN         = 4113;
constexpr int PRAYER_ULTIMATE_STRENGTH  = 4114;
constexpr int PRAYER_INCREDIBLE_REFLEXES = 4115;
constexpr int PRAYER_PROTECT_FROM_MAGIC  = 4116;
constexpr int PRAYER_PROTECT_FROM_MISSILES = 4117;
constexpr int PRAYER_PROTECT_FROM_MELEE  = 4118;
constexpr int PRAYER_EAGLE_EYE          = 4126;
constexpr int PRAYER_MYSTIC_MIGHT       = 4127;
constexpr int PRAYER_RETRIBUTION        = 4119;
constexpr int PRAYER_REDEMPTION         = 4120;
constexpr int PRAYER_SMITE               = 4121;
constexpr int PRAYER_CHIVALRY           = 4128;
constexpr int PRAYER_PIETY              = 4129;
constexpr int PRAYER_PRESERVE           = 5466;
constexpr int PRAYER_RIGOUR             = 5464;
constexpr int PRAYER_AUGURY             = 5465;
constexpr int PRAYER_DEADEYE            = 16090;
constexpr int PRAYER_MYSTIC_VIGOUR      = 16091;

// --- Ruinous Powers ---
constexpr int PRAYER_RP_REJUVENATION      = 14840;
constexpr int PRAYER_RP_ANCIENT_STRENGTH  = 14829;
constexpr int PRAYER_RP_ANCIENT_SIGHT     = 14830;
constexpr int PRAYER_RP_ANCIENT_WILL      = 14831;
constexpr int PRAYER_RP_PROTECT_ITEM      = 14966;
constexpr int PRAYER_RP_RUINOUS_GRACE     = 14841;
constexpr int PRAYER_RP_DAMPEN_MAGIC      = 14964;
constexpr int PRAYER_RP_DAMPEN_RANGED     = 14963;
constexpr int PRAYER_RP_DAMPEN_MELEE      = 14962;
constexpr int PRAYER_RP_TRINITAS          = 14832;
constexpr int PRAYER_RP_BERSERKER         = 14844;
constexpr int PRAYER_RP_PURGE             = 14839;
constexpr int PRAYER_RP_METABOLISE        = 14843;
constexpr int PRAYER_RP_REBUKE            = 14850;
constexpr int PRAYER_RP_VINDICATION       = 14851;
constexpr int PRAYER_RP_DECIMATE          = 14833;
constexpr int PRAYER_RP_ANNIHILATE        = 14834;
constexpr int PRAYER_RP_VAPORISE          = 14835;
constexpr int PRAYER_RP_FUMUS_VOW         = 14845;
constexpr int PRAYER_RP_UMBRA_VOW         = 14847;
constexpr int PRAYER_RP_CRUORS_VOW        = 14846;
constexpr int PRAYER_RP_GLACIES_VOW       = 14848;
constexpr int PRAYER_RP_WRATH             = 14842;
constexpr int PRAYER_RP_INTENSIFY         = 14965;

// --- Spellbook & prayer book ---
constexpr int PRAYERBOOK    = 14826;
constexpr int SPELLBOOK     = 4070;
constexpr int SPELLBOOK_SUBMENU = 9730;

// --- Buff timers ---
constexpr int RUN_SLOWED_DEPLETION_ACTIVE = 25;
constexpr int STAMINA_EFFECT    = 24;
constexpr int ANTIFIRE           = 3981;
constexpr int SUPER_ANTIFIRE     = 6101;
constexpr int MAGIC_IMBUE        = 5438;
constexpr int VENGEANCE_ACTIVE   = 2450;
constexpr int VENGEANCE_COOLDOWN = 2451;
constexpr int IMBUED_HEART_COOLDOWN = 5361;
constexpr int RING_OF_ENDURANCE_EFFECT = 10385;

// --- Divine potions ---
constexpr int DIVINE_SUPER_ATTACK   = 8429;
constexpr int DIVINE_SUPER_STRENGTH = 8430;
constexpr int DIVINE_SUPER_DEFENCE  = 8431;
constexpr int DIVINE_RANGING        = 8432;
constexpr int DIVINE_MAGIC          = 8433;
constexpr int DIVINE_SUPER_COMBAT   = 13663;
constexpr int DIVINE_BASTION        = 13664;
constexpr int DIVINE_BATTLEMAGE     = 13665;

// --- Spell activeness / cooldowns ---
constexpr int DEATH_CHARGE          = 12411;
constexpr int DEATH_CHARGE_COOLDOWN = 12138;
constexpr int RESURRECT_THRALL      = 12413;
constexpr int SHADOW_VEIL           = 12414;
constexpr int SHADOW_VEIL_COOLDOWN  = 12291;

// --- Overloads ---
constexpr int NMZ_OVERLOAD_REFRESHES_REMAINING = 3955;
constexpr int COX_OVERLOAD_REFRESHES_REMAINING = 5418;

// --- Combat area flags ---
constexpr int MULTICOMBAT_AREA = 4605;
constexpr int IN_WILDERNESS    = 5963;
constexpr int PVP_SPEC_ORB     = 8121;

// --- Account ---
constexpr int ACCOUNT_TYPE = 1777;

// --- Equipped weapon type ---
constexpr int EQUIPPED_WEAPON_TYPE = 357;

// --- Boss health ---
constexpr int BOSS_HEALTH_CURRENT = 6099;
constexpr int BOSS_HEALTH_MAXIMUM = 6100;
constexpr int BOSS_HEALTH_OVERLAY = 12389;

// --- Slayer ---
constexpr int SLAYER_POINTS      = 4068;
constexpr int SLAYER_TASK_STREAK = 4069;
constexpr int SLAYER_TASK_BOSS   = 4723;
constexpr int SUPERIOR_ENABLED   = 5362;

// --- Raids ---
constexpr int IN_RAID            = 5432;
constexpr int RAID_STATE         = 5425;
constexpr int RAID_TOTAL_POINTS  = 5431;
constexpr int THEATRE_OF_BLOOD   = 6440;
constexpr int TOA_RAID_LEVEL     = 14380;
constexpr int TOA_RAID_DAMAGE    = 14325;

// --- Bank ---
constexpr int BANK_LEAVEPLACEHOLDERS  = 3755;
constexpr int BANK_WITHDRAWNOTES      = 3958;
constexpr int BANK_REARRANGE_MODE     = 3959;
constexpr int BANK_REQUESTEDQUANTITY  = 3960;
constexpr int CURRENT_BANK_TAB        = 4150;
constexpr int BANK_QUANTITY_TYPE      = 6590;

// --- Diary ---
constexpr int DIARY_ARDOUGNE_EASY  = 4458;
constexpr int DIARY_ARDOUGNE_MEDIUM = 4459;
constexpr int DIARY_ARDOUGNE_HARD  = 4460;
constexpr int DIARY_ARDOUGNE_ELITE = 4461;
constexpr int DIARY_DESERT_EASY    = 4483;
constexpr int DIARY_DESERT_MEDIUM  = 4484;
constexpr int DIARY_DESERT_HARD    = 4485;
constexpr int DIARY_DESERT_ELITE   = 4486;
constexpr int DIARY_FALADOR_EASY   = 4462;
constexpr int DIARY_FALADOR_MEDIUM = 4463;
constexpr int DIARY_FALADOR_HARD   = 4464;
constexpr int DIARY_FALADOR_ELITE  = 4465;
constexpr int DIARY_VARROCK_EASY   = 4479;
constexpr int DIARY_VARROCK_MEDIUM = 4480;
constexpr int DIARY_VARROCK_HARD   = 4481;
constexpr int DIARY_VARROCK_ELITE  = 4482;
constexpr int DIARY_LUMBRIDGE_EASY   = 4495;
constexpr int DIARY_LUMBRIDGE_MEDIUM = 4496;
constexpr int DIARY_LUMBRIDGE_HARD   = 4497;
constexpr int DIARY_LUMBRIDGE_ELITE  = 4498;
constexpr int DIARY_MORYTANIA_EASY   = 4487;
constexpr int DIARY_MORYTANIA_MEDIUM = 4488;
constexpr int DIARY_MORYTANIA_HARD   = 4489;
constexpr int DIARY_MORYTANIA_ELITE  = 4490;
constexpr int DIARY_KANDARIN_EASY    = 4475;
constexpr int DIARY_KANDARIN_MEDIUM  = 4476;
constexpr int DIARY_KANDARIN_HARD    = 4477;
constexpr int DIARY_KANDARIN_ELITE   = 4478;
constexpr int DIARY_FREMENNIK_EASY   = 4491;
constexpr int DIARY_FREMENNIK_MEDIUM = 4492;
constexpr int DIARY_FREMENNIK_HARD   = 4493;
constexpr int DIARY_FREMENNIK_ELITE  = 4494;
constexpr int DIARY_WILDERNESS_EASY   = 4466;
constexpr int DIARY_WILDERNESS_MEDIUM = 4467;
constexpr int DIARY_WILDERNESS_HARD   = 4468;
constexpr int DIARY_WILDERNESS_ELITE  = 4469;
constexpr int DIARY_WESTERN_EASY      = 4471;
constexpr int DIARY_WESTERN_MEDIUM    = 4472;
constexpr int DIARY_WESTERN_HARD      = 4473;
constexpr int DIARY_WESTERN_ELITE     = 4474;
constexpr int DIARY_KARAMJA_EASY      = 3578;
constexpr int DIARY_KARAMJA_MEDIUM    = 3599;
constexpr int DIARY_KARAMJA_HARD      = 3611;
constexpr int DIARY_KARAMJA_ELITE     = 4566;
constexpr int DIARY_KOUREND_EASY      = 7925;
constexpr int DIARY_KOUREND_MEDIUM    = 7926;
constexpr int DIARY_KOUREND_HARD      = 7927;
constexpr int DIARY_KOUREND_ELITE     = 7928;

// --- Misc gameplay ---
constexpr int TELEBLOCK        = 4163;
constexpr int NMZ_ABSORPTION   = 3956;
constexpr int NMZ_POINTS       = 3949;
constexpr int DRAGONFIRE_SHIELD_COOLDOWN = 6539;
constexpr int MENAPHITE_REMEDY = 14448;
constexpr int BUFF_STAT_BOOST  = 14344;
constexpr int COLOSSEUM_DOOM   = 9801;

// --- UI ---
constexpr int TRANSPARENT_CHATBOX = 4608;
constexpr int SIDE_PANELS         = 4607;
constexpr int EXPERIENCE_TRACKER_POSITION = 4692;

/// Returns the identifier name (e.g. "QUICK_PRAYER") for the given varbit
/// id, or `nullptr` when the id has no named constant in this header.
/// Used by debug surfaces that annotate raw ids; production plugin code
/// should reference the constants directly.
///
/// Implementation note: this is a flat `switch` over every constant above
/// rather than a lookup table. Keeps the header self-contained (no
/// `<unordered_map>` include, no `.cpp` to link) and compiles to a jump
/// table on MSVC. ~200 cases; cold code, called only by debug UIs.
inline const char* nameOf(int id) {
    switch (id) {
        case QUICK_PRAYER: return "QUICK_PRAYER";
        case PRAYER_THICK_SKIN: return "PRAYER_THICK_SKIN";
        case PRAYER_BURST_OF_STRENGTH: return "PRAYER_BURST_OF_STRENGTH";
        case PRAYER_CLARITY_OF_THOUGHT: return "PRAYER_CLARITY_OF_THOUGHT";
        case PRAYER_SHARP_EYE: return "PRAYER_SHARP_EYE";
        case PRAYER_MYSTIC_WILL: return "PRAYER_MYSTIC_WILL";
        case PRAYER_ROCK_SKIN: return "PRAYER_ROCK_SKIN";
        case PRAYER_SUPERHUMAN_STRENGTH: return "PRAYER_SUPERHUMAN_STRENGTH";
        case PRAYER_IMPROVED_REFLEXES: return "PRAYER_IMPROVED_REFLEXES";
        case PRAYER_RAPID_RESTORE: return "PRAYER_RAPID_RESTORE";
        case PRAYER_RAPID_HEAL: return "PRAYER_RAPID_HEAL";
        case PRAYER_PROTECT_ITEM: return "PRAYER_PROTECT_ITEM";
        case PRAYER_HAWK_EYE: return "PRAYER_HAWK_EYE";
        case PRAYER_MYSTIC_LORE: return "PRAYER_MYSTIC_LORE";
        case PRAYER_STEEL_SKIN: return "PRAYER_STEEL_SKIN";
        case PRAYER_ULTIMATE_STRENGTH: return "PRAYER_ULTIMATE_STRENGTH";
        case PRAYER_INCREDIBLE_REFLEXES: return "PRAYER_INCREDIBLE_REFLEXES";
        case PRAYER_PROTECT_FROM_MAGIC: return "PRAYER_PROTECT_FROM_MAGIC";
        case PRAYER_PROTECT_FROM_MISSILES: return "PRAYER_PROTECT_FROM_MISSILES";
        case PRAYER_PROTECT_FROM_MELEE: return "PRAYER_PROTECT_FROM_MELEE";
        case PRAYER_EAGLE_EYE: return "PRAYER_EAGLE_EYE";
        case PRAYER_MYSTIC_MIGHT: return "PRAYER_MYSTIC_MIGHT";
        case PRAYER_RETRIBUTION: return "PRAYER_RETRIBUTION";
        case PRAYER_REDEMPTION: return "PRAYER_REDEMPTION";
        case PRAYER_SMITE: return "PRAYER_SMITE";
        case PRAYER_CHIVALRY: return "PRAYER_CHIVALRY";
        case PRAYER_PIETY: return "PRAYER_PIETY";
        case PRAYER_PRESERVE: return "PRAYER_PRESERVE";
        case PRAYER_RIGOUR: return "PRAYER_RIGOUR";
        case PRAYER_AUGURY: return "PRAYER_AUGURY";
        case PRAYER_DEADEYE: return "PRAYER_DEADEYE";
        case PRAYER_MYSTIC_VIGOUR: return "PRAYER_MYSTIC_VIGOUR";

        case PRAYER_RP_REJUVENATION: return "PRAYER_RP_REJUVENATION";
        case PRAYER_RP_ANCIENT_STRENGTH: return "PRAYER_RP_ANCIENT_STRENGTH";
        case PRAYER_RP_ANCIENT_SIGHT: return "PRAYER_RP_ANCIENT_SIGHT";
        case PRAYER_RP_ANCIENT_WILL: return "PRAYER_RP_ANCIENT_WILL";
        case PRAYER_RP_PROTECT_ITEM: return "PRAYER_RP_PROTECT_ITEM";
        case PRAYER_RP_RUINOUS_GRACE: return "PRAYER_RP_RUINOUS_GRACE";
        case PRAYER_RP_DAMPEN_MAGIC: return "PRAYER_RP_DAMPEN_MAGIC";
        case PRAYER_RP_DAMPEN_RANGED: return "PRAYER_RP_DAMPEN_RANGED";
        case PRAYER_RP_DAMPEN_MELEE: return "PRAYER_RP_DAMPEN_MELEE";
        case PRAYER_RP_TRINITAS: return "PRAYER_RP_TRINITAS";
        case PRAYER_RP_BERSERKER: return "PRAYER_RP_BERSERKER";
        case PRAYER_RP_PURGE: return "PRAYER_RP_PURGE";
        case PRAYER_RP_METABOLISE: return "PRAYER_RP_METABOLISE";
        case PRAYER_RP_REBUKE: return "PRAYER_RP_REBUKE";
        case PRAYER_RP_VINDICATION: return "PRAYER_RP_VINDICATION";
        case PRAYER_RP_DECIMATE: return "PRAYER_RP_DECIMATE";
        case PRAYER_RP_ANNIHILATE: return "PRAYER_RP_ANNIHILATE";
        case PRAYER_RP_VAPORISE: return "PRAYER_RP_VAPORISE";
        case PRAYER_RP_FUMUS_VOW: return "PRAYER_RP_FUMUS_VOW";
        case PRAYER_RP_UMBRA_VOW: return "PRAYER_RP_UMBRA_VOW";
        case PRAYER_RP_CRUORS_VOW: return "PRAYER_RP_CRUORS_VOW";
        case PRAYER_RP_GLACIES_VOW: return "PRAYER_RP_GLACIES_VOW";
        case PRAYER_RP_WRATH: return "PRAYER_RP_WRATH";
        case PRAYER_RP_INTENSIFY: return "PRAYER_RP_INTENSIFY";

        case PRAYERBOOK: return "PRAYERBOOK";
        case SPELLBOOK: return "SPELLBOOK";
        case SPELLBOOK_SUBMENU: return "SPELLBOOK_SUBMENU";

        case RUN_SLOWED_DEPLETION_ACTIVE: return "RUN_SLOWED_DEPLETION_ACTIVE";
        case STAMINA_EFFECT: return "STAMINA_EFFECT";
        case ANTIFIRE: return "ANTIFIRE";
        case SUPER_ANTIFIRE: return "SUPER_ANTIFIRE";
        case MAGIC_IMBUE: return "MAGIC_IMBUE";
        case VENGEANCE_ACTIVE: return "VENGEANCE_ACTIVE";
        case VENGEANCE_COOLDOWN: return "VENGEANCE_COOLDOWN";
        case IMBUED_HEART_COOLDOWN: return "IMBUED_HEART_COOLDOWN";
        case RING_OF_ENDURANCE_EFFECT: return "RING_OF_ENDURANCE_EFFECT";

        case DIVINE_SUPER_ATTACK: return "DIVINE_SUPER_ATTACK";
        case DIVINE_SUPER_STRENGTH: return "DIVINE_SUPER_STRENGTH";
        case DIVINE_SUPER_DEFENCE: return "DIVINE_SUPER_DEFENCE";
        case DIVINE_RANGING: return "DIVINE_RANGING";
        case DIVINE_MAGIC: return "DIVINE_MAGIC";
        case DIVINE_SUPER_COMBAT: return "DIVINE_SUPER_COMBAT";
        case DIVINE_BASTION: return "DIVINE_BASTION";
        case DIVINE_BATTLEMAGE: return "DIVINE_BATTLEMAGE";

        case DEATH_CHARGE: return "DEATH_CHARGE";
        case DEATH_CHARGE_COOLDOWN: return "DEATH_CHARGE_COOLDOWN";
        case RESURRECT_THRALL: return "RESURRECT_THRALL";
        case SHADOW_VEIL: return "SHADOW_VEIL";
        case SHADOW_VEIL_COOLDOWN: return "SHADOW_VEIL_COOLDOWN";

        case NMZ_OVERLOAD_REFRESHES_REMAINING: return "NMZ_OVERLOAD_REFRESHES_REMAINING";
        case COX_OVERLOAD_REFRESHES_REMAINING: return "COX_OVERLOAD_REFRESHES_REMAINING";

        case MULTICOMBAT_AREA: return "MULTICOMBAT_AREA";
        case IN_WILDERNESS: return "IN_WILDERNESS";
        case PVP_SPEC_ORB: return "PVP_SPEC_ORB";

        case ACCOUNT_TYPE: return "ACCOUNT_TYPE";

        case EQUIPPED_WEAPON_TYPE: return "EQUIPPED_WEAPON_TYPE";

        case BOSS_HEALTH_CURRENT: return "BOSS_HEALTH_CURRENT";
        case BOSS_HEALTH_MAXIMUM: return "BOSS_HEALTH_MAXIMUM";
        case BOSS_HEALTH_OVERLAY: return "BOSS_HEALTH_OVERLAY";

        case SLAYER_POINTS: return "SLAYER_POINTS";
        case SLAYER_TASK_STREAK: return "SLAYER_TASK_STREAK";
        case SLAYER_TASK_BOSS: return "SLAYER_TASK_BOSS";
        case SUPERIOR_ENABLED: return "SUPERIOR_ENABLED";

        case IN_RAID: return "IN_RAID";
        case RAID_STATE: return "RAID_STATE";
        case RAID_TOTAL_POINTS: return "RAID_TOTAL_POINTS";
        case THEATRE_OF_BLOOD: return "THEATRE_OF_BLOOD";
        case TOA_RAID_LEVEL: return "TOA_RAID_LEVEL";
        case TOA_RAID_DAMAGE: return "TOA_RAID_DAMAGE";

        case BANK_LEAVEPLACEHOLDERS: return "BANK_LEAVEPLACEHOLDERS";
        case BANK_WITHDRAWNOTES: return "BANK_WITHDRAWNOTES";
        case BANK_REARRANGE_MODE: return "BANK_REARRANGE_MODE";
        case BANK_REQUESTEDQUANTITY: return "BANK_REQUESTEDQUANTITY";
        case CURRENT_BANK_TAB: return "CURRENT_BANK_TAB";
        case BANK_QUANTITY_TYPE: return "BANK_QUANTITY_TYPE";

        case DIARY_ARDOUGNE_EASY: return "DIARY_ARDOUGNE_EASY";
        case DIARY_ARDOUGNE_MEDIUM: return "DIARY_ARDOUGNE_MEDIUM";
        case DIARY_ARDOUGNE_HARD: return "DIARY_ARDOUGNE_HARD";
        case DIARY_ARDOUGNE_ELITE: return "DIARY_ARDOUGNE_ELITE";
        case DIARY_DESERT_EASY: return "DIARY_DESERT_EASY";
        case DIARY_DESERT_MEDIUM: return "DIARY_DESERT_MEDIUM";
        case DIARY_DESERT_HARD: return "DIARY_DESERT_HARD";
        case DIARY_DESERT_ELITE: return "DIARY_DESERT_ELITE";
        case DIARY_FALADOR_EASY: return "DIARY_FALADOR_EASY";
        case DIARY_FALADOR_MEDIUM: return "DIARY_FALADOR_MEDIUM";
        case DIARY_FALADOR_HARD: return "DIARY_FALADOR_HARD";
        case DIARY_FALADOR_ELITE: return "DIARY_FALADOR_ELITE";
        case DIARY_VARROCK_EASY: return "DIARY_VARROCK_EASY";
        case DIARY_VARROCK_MEDIUM: return "DIARY_VARROCK_MEDIUM";
        case DIARY_VARROCK_HARD: return "DIARY_VARROCK_HARD";
        case DIARY_VARROCK_ELITE: return "DIARY_VARROCK_ELITE";
        case DIARY_LUMBRIDGE_EASY: return "DIARY_LUMBRIDGE_EASY";
        case DIARY_LUMBRIDGE_MEDIUM: return "DIARY_LUMBRIDGE_MEDIUM";
        case DIARY_LUMBRIDGE_HARD: return "DIARY_LUMBRIDGE_HARD";
        case DIARY_LUMBRIDGE_ELITE: return "DIARY_LUMBRIDGE_ELITE";
        case DIARY_MORYTANIA_EASY: return "DIARY_MORYTANIA_EASY";
        case DIARY_MORYTANIA_MEDIUM: return "DIARY_MORYTANIA_MEDIUM";
        case DIARY_MORYTANIA_HARD: return "DIARY_MORYTANIA_HARD";
        case DIARY_MORYTANIA_ELITE: return "DIARY_MORYTANIA_ELITE";
        case DIARY_KANDARIN_EASY: return "DIARY_KANDARIN_EASY";
        case DIARY_KANDARIN_MEDIUM: return "DIARY_KANDARIN_MEDIUM";
        case DIARY_KANDARIN_HARD: return "DIARY_KANDARIN_HARD";
        case DIARY_KANDARIN_ELITE: return "DIARY_KANDARIN_ELITE";
        case DIARY_FREMENNIK_EASY: return "DIARY_FREMENNIK_EASY";
        case DIARY_FREMENNIK_MEDIUM: return "DIARY_FREMENNIK_MEDIUM";
        case DIARY_FREMENNIK_HARD: return "DIARY_FREMENNIK_HARD";
        case DIARY_FREMENNIK_ELITE: return "DIARY_FREMENNIK_ELITE";
        case DIARY_WILDERNESS_EASY: return "DIARY_WILDERNESS_EASY";
        case DIARY_WILDERNESS_MEDIUM: return "DIARY_WILDERNESS_MEDIUM";
        case DIARY_WILDERNESS_HARD: return "DIARY_WILDERNESS_HARD";
        case DIARY_WILDERNESS_ELITE: return "DIARY_WILDERNESS_ELITE";
        case DIARY_WESTERN_EASY: return "DIARY_WESTERN_EASY";
        case DIARY_WESTERN_MEDIUM: return "DIARY_WESTERN_MEDIUM";
        case DIARY_WESTERN_HARD: return "DIARY_WESTERN_HARD";
        case DIARY_WESTERN_ELITE: return "DIARY_WESTERN_ELITE";
        case DIARY_KARAMJA_EASY: return "DIARY_KARAMJA_EASY";
        case DIARY_KARAMJA_MEDIUM: return "DIARY_KARAMJA_MEDIUM";
        case DIARY_KARAMJA_HARD: return "DIARY_KARAMJA_HARD";
        case DIARY_KARAMJA_ELITE: return "DIARY_KARAMJA_ELITE";
        case DIARY_KOUREND_EASY: return "DIARY_KOUREND_EASY";
        case DIARY_KOUREND_MEDIUM: return "DIARY_KOUREND_MEDIUM";
        case DIARY_KOUREND_HARD: return "DIARY_KOUREND_HARD";
        case DIARY_KOUREND_ELITE: return "DIARY_KOUREND_ELITE";

        case TELEBLOCK: return "TELEBLOCK";
        case NMZ_ABSORPTION: return "NMZ_ABSORPTION";
        case NMZ_POINTS: return "NMZ_POINTS";
        case DRAGONFIRE_SHIELD_COOLDOWN: return "DRAGONFIRE_SHIELD_COOLDOWN";
        case MENAPHITE_REMEDY: return "MENAPHITE_REMEDY";
        case BUFF_STAT_BOOST: return "BUFF_STAT_BOOST";
        case COLOSSEUM_DOOM: return "COLOSSEUM_DOOM";

        case TRANSPARENT_CHATBOX: return "TRANSPARENT_CHATBOX";
        case SIDE_PANELS: return "SIDE_PANELS";
        case EXPERIENCE_TRACKER_POSITION: return "EXPERIENCE_TRACKER_POSITION";

        default: return nullptr;
    }
}

}  // namespace Varbits

}  // namespace titan
