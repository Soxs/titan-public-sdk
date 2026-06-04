package net.titan.api;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/// Varbit ID constants for reading packed game configuration state. Mirrors
/// the C++ {@code titan::Varbits} catalog (and RuneLite's
/// {@code net.runelite.api.Varbits}). Read via
/// {@link net.titan.api.Client#varbit(int)}.
public final class Varbits {
    private Varbits() {}

    // --- Prayers -- standard prayer book ---
    public static final int QUICK_PRAYER = 4103;
    public static final int PRAYER_THICK_SKIN = 4104;
    public static final int PRAYER_BURST_OF_STRENGTH = 4105;
    public static final int PRAYER_CLARITY_OF_THOUGHT = 4106;
    public static final int PRAYER_SHARP_EYE = 4122;
    public static final int PRAYER_MYSTIC_WILL = 4123;
    public static final int PRAYER_ROCK_SKIN = 4107;
    public static final int PRAYER_SUPERHUMAN_STRENGTH = 4108;
    public static final int PRAYER_IMPROVED_REFLEXES = 4109;
    public static final int PRAYER_RAPID_RESTORE = 4110;
    public static final int PRAYER_RAPID_HEAL = 4111;
    public static final int PRAYER_PROTECT_ITEM = 4112;
    public static final int PRAYER_HAWK_EYE = 4124;
    public static final int PRAYER_MYSTIC_LORE = 4125;
    public static final int PRAYER_STEEL_SKIN = 4113;
    public static final int PRAYER_ULTIMATE_STRENGTH = 4114;
    public static final int PRAYER_INCREDIBLE_REFLEXES = 4115;
    public static final int PRAYER_PROTECT_FROM_MAGIC = 4116;
    public static final int PRAYER_PROTECT_FROM_MISSILES = 4117;
    public static final int PRAYER_PROTECT_FROM_MELEE = 4118;
    public static final int PRAYER_EAGLE_EYE = 4126;
    public static final int PRAYER_MYSTIC_MIGHT = 4127;
    public static final int PRAYER_RETRIBUTION = 4119;
    public static final int PRAYER_REDEMPTION = 4120;
    public static final int PRAYER_SMITE = 4121;
    public static final int PRAYER_CHIVALRY = 4128;
    public static final int PRAYER_PIETY = 4129;
    public static final int PRAYER_PRESERVE = 5466;
    public static final int PRAYER_RIGOUR = 5464;
    public static final int PRAYER_AUGURY = 5465;
    public static final int PRAYER_DEADEYE = 16090;
    public static final int PRAYER_MYSTIC_VIGOUR = 16091;

    // --- Ruinous Powers ---
    public static final int PRAYER_RP_REJUVENATION = 14840;
    public static final int PRAYER_RP_ANCIENT_STRENGTH = 14829;
    public static final int PRAYER_RP_ANCIENT_SIGHT = 14830;
    public static final int PRAYER_RP_ANCIENT_WILL = 14831;
    public static final int PRAYER_RP_PROTECT_ITEM = 14966;
    public static final int PRAYER_RP_RUINOUS_GRACE = 14841;
    public static final int PRAYER_RP_DAMPEN_MAGIC = 14964;
    public static final int PRAYER_RP_DAMPEN_RANGED = 14963;
    public static final int PRAYER_RP_DAMPEN_MELEE = 14962;
    public static final int PRAYER_RP_TRINITAS = 14832;
    public static final int PRAYER_RP_BERSERKER = 14844;
    public static final int PRAYER_RP_PURGE = 14839;
    public static final int PRAYER_RP_METABOLISE = 14843;
    public static final int PRAYER_RP_REBUKE = 14850;
    public static final int PRAYER_RP_VINDICATION = 14851;
    public static final int PRAYER_RP_DECIMATE = 14833;
    public static final int PRAYER_RP_ANNIHILATE = 14834;
    public static final int PRAYER_RP_VAPORISE = 14835;
    public static final int PRAYER_RP_FUMUS_VOW = 14845;
    public static final int PRAYER_RP_UMBRA_VOW = 14847;
    public static final int PRAYER_RP_CRUORS_VOW = 14846;
    public static final int PRAYER_RP_GLACIES_VOW = 14848;
    public static final int PRAYER_RP_WRATH = 14842;
    public static final int PRAYER_RP_INTENSIFY = 14965;

    // --- Spellbook & prayer book ---
    public static final int PRAYERBOOK = 14826;
    public static final int SPELLBOOK = 4070;
    public static final int SPELLBOOK_SUBMENU = 9730;

    // --- Buff timers ---
    public static final int RUN_SLOWED_DEPLETION_ACTIVE = 25;
    public static final int STAMINA_EFFECT = 24;
    public static final int ANTIFIRE = 3981;
    public static final int SUPER_ANTIFIRE = 6101;
    public static final int MAGIC_IMBUE = 5438;
    public static final int VENGEANCE_ACTIVE = 2450;
    public static final int VENGEANCE_COOLDOWN = 2451;
    public static final int IMBUED_HEART_COOLDOWN = 5361;
    public static final int RING_OF_ENDURANCE_EFFECT = 10385;

    // --- Divine potions ---
    public static final int DIVINE_SUPER_ATTACK = 8429;
    public static final int DIVINE_SUPER_STRENGTH = 8430;
    public static final int DIVINE_SUPER_DEFENCE = 8431;
    public static final int DIVINE_RANGING = 8432;
    public static final int DIVINE_MAGIC = 8433;
    public static final int DIVINE_SUPER_COMBAT = 13663;
    public static final int DIVINE_BASTION = 13664;
    public static final int DIVINE_BATTLEMAGE = 13665;

    // --- Spell activeness / cooldowns ---
    public static final int DEATH_CHARGE = 12411;
    public static final int DEATH_CHARGE_COOLDOWN = 12138;
    public static final int RESURRECT_THRALL = 12413;
    public static final int SHADOW_VEIL = 12414;
    public static final int SHADOW_VEIL_COOLDOWN = 12291;

    // --- Overloads ---
    public static final int NMZ_OVERLOAD_REFRESHES_REMAINING = 3955;
    public static final int COX_OVERLOAD_REFRESHES_REMAINING = 5418;

    // --- Combat area flags ---
    public static final int MULTICOMBAT_AREA = 4605;
    public static final int IN_WILDERNESS = 5963;
    public static final int PVP_SPEC_ORB = 8121;

    // --- Account ---
    public static final int ACCOUNT_TYPE = 1777;

    // --- Equipped weapon type ---
    public static final int EQUIPPED_WEAPON_TYPE = 357;

    // --- Boss health ---
    public static final int BOSS_HEALTH_CURRENT = 6099;
    public static final int BOSS_HEALTH_MAXIMUM = 6100;
    public static final int BOSS_HEALTH_OVERLAY = 12389;

    // --- Slayer ---
    public static final int SLAYER_POINTS = 4068;
    public static final int SLAYER_TASK_STREAK = 4069;
    public static final int SLAYER_TASK_BOSS = 4723;
    public static final int SUPERIOR_ENABLED = 5362;

    // --- Raids ---
    public static final int IN_RAID = 5432;
    public static final int RAID_STATE = 5425;
    public static final int RAID_TOTAL_POINTS = 5431;
    public static final int THEATRE_OF_BLOOD = 6440;
    public static final int TOA_RAID_LEVEL = 14380;
    public static final int TOA_RAID_DAMAGE = 14325;

    // --- Bank ---
    public static final int BANK_LEAVEPLACEHOLDERS = 3755;
    public static final int BANK_WITHDRAWNOTES = 3958;
    public static final int BANK_REARRANGE_MODE = 3959;
    public static final int BANK_REQUESTEDQUANTITY = 3960;
    public static final int CURRENT_BANK_TAB = 4150;
    public static final int BANK_QUANTITY_TYPE = 6590;

    // --- Diary ---
    public static final int DIARY_ARDOUGNE_EASY = 4458;
    public static final int DIARY_ARDOUGNE_MEDIUM = 4459;
    public static final int DIARY_ARDOUGNE_HARD = 4460;
    public static final int DIARY_ARDOUGNE_ELITE = 4461;
    public static final int DIARY_DESERT_EASY = 4483;
    public static final int DIARY_DESERT_MEDIUM = 4484;
    public static final int DIARY_DESERT_HARD = 4485;
    public static final int DIARY_DESERT_ELITE = 4486;
    public static final int DIARY_FALADOR_EASY = 4462;
    public static final int DIARY_FALADOR_MEDIUM = 4463;
    public static final int DIARY_FALADOR_HARD = 4464;
    public static final int DIARY_FALADOR_ELITE = 4465;
    public static final int DIARY_VARROCK_EASY = 4479;
    public static final int DIARY_VARROCK_MEDIUM = 4480;
    public static final int DIARY_VARROCK_HARD = 4481;
    public static final int DIARY_VARROCK_ELITE = 4482;
    public static final int DIARY_LUMBRIDGE_EASY = 4495;
    public static final int DIARY_LUMBRIDGE_MEDIUM = 4496;
    public static final int DIARY_LUMBRIDGE_HARD = 4497;
    public static final int DIARY_LUMBRIDGE_ELITE = 4498;
    public static final int DIARY_MORYTANIA_EASY = 4487;
    public static final int DIARY_MORYTANIA_MEDIUM = 4488;
    public static final int DIARY_MORYTANIA_HARD = 4489;
    public static final int DIARY_MORYTANIA_ELITE = 4490;
    public static final int DIARY_KANDARIN_EASY = 4475;
    public static final int DIARY_KANDARIN_MEDIUM = 4476;
    public static final int DIARY_KANDARIN_HARD = 4477;
    public static final int DIARY_KANDARIN_ELITE = 4478;
    public static final int DIARY_FREMENNIK_EASY = 4491;
    public static final int DIARY_FREMENNIK_MEDIUM = 4492;
    public static final int DIARY_FREMENNIK_HARD = 4493;
    public static final int DIARY_FREMENNIK_ELITE = 4494;
    public static final int DIARY_WILDERNESS_EASY = 4466;
    public static final int DIARY_WILDERNESS_MEDIUM = 4467;
    public static final int DIARY_WILDERNESS_HARD = 4468;
    public static final int DIARY_WILDERNESS_ELITE = 4469;
    public static final int DIARY_WESTERN_EASY = 4471;
    public static final int DIARY_WESTERN_MEDIUM = 4472;
    public static final int DIARY_WESTERN_HARD = 4473;
    public static final int DIARY_WESTERN_ELITE = 4474;
    public static final int DIARY_KARAMJA_EASY = 3578;
    public static final int DIARY_KARAMJA_MEDIUM = 3599;
    public static final int DIARY_KARAMJA_HARD = 3611;
    public static final int DIARY_KARAMJA_ELITE = 4566;
    public static final int DIARY_KOUREND_EASY = 7925;
    public static final int DIARY_KOUREND_MEDIUM = 7926;
    public static final int DIARY_KOUREND_HARD = 7927;
    public static final int DIARY_KOUREND_ELITE = 7928;

    // --- Misc gameplay ---
    public static final int TELEBLOCK = 4163;
    public static final int NMZ_ABSORPTION = 3956;
    public static final int NMZ_POINTS = 3949;
    public static final int DRAGONFIRE_SHIELD_COOLDOWN = 6539;
    public static final int MENAPHITE_REMEDY = 14448;
    public static final int BUFF_STAT_BOOST = 14344;
    public static final int COLOSSEUM_DOOM = 9801;

    // --- UI ---
    public static final int TRANSPARENT_CHATBOX = 4608;
    public static final int SIDE_PANELS = 4607;
    public static final int EXPERIENCE_TRACKER_POSITION = 4692;

    /// Returns the identifier name (e.g. "QUICK_PRAYER") for the given varbit
    /// id, or {@code null} when the id has no named constant in this class.
    public static String nameOf(int id) {
        for (Field field : Varbits.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                try {
                    if (field.getInt(null) == id) return field.getName();
                } catch (IllegalAccessException ignored) {
                    // Skip inaccessible fields.
                }
            }
        }
        return null;
    }
}
