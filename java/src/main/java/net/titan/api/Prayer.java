package net.titan.api;

/// All prayers across both the standard and Ruinous Powers prayer books.
/// Mirrors the C++ {@code titan::Prayer} enum + {@code PrayerInfo}. Each
/// constant carries the varbit id used to read its active state via
/// {@link net.titan.api.Client#varbit(int)} / {@link net.titan.api.Client#prayerActive(int)}.
public enum Prayer {
    THICK_SKIN(Varbits.PRAYER_THICK_SKIN, "Thick Skin"),
    BURST_OF_STRENGTH(Varbits.PRAYER_BURST_OF_STRENGTH, "Burst of Strength"),
    CLARITY_OF_THOUGHT(Varbits.PRAYER_CLARITY_OF_THOUGHT, "Clarity of Thought"),
    SHARP_EYE(Varbits.PRAYER_SHARP_EYE, "Sharp Eye"),
    MYSTIC_WILL(Varbits.PRAYER_MYSTIC_WILL, "Mystic Will"),
    ROCK_SKIN(Varbits.PRAYER_ROCK_SKIN, "Rock Skin"),
    SUPERHUMAN_STRENGTH(Varbits.PRAYER_SUPERHUMAN_STRENGTH, "Superhuman Strength"),
    IMPROVED_REFLEXES(Varbits.PRAYER_IMPROVED_REFLEXES, "Improved Reflexes"),
    RAPID_RESTORE(Varbits.PRAYER_RAPID_RESTORE, "Rapid Restore"),
    RAPID_HEAL(Varbits.PRAYER_RAPID_HEAL, "Rapid Heal"),
    PROTECT_ITEM(Varbits.PRAYER_PROTECT_ITEM, "Protect Item"),
    HAWK_EYE(Varbits.PRAYER_HAWK_EYE, "Hawk Eye"),
    MYSTIC_LORE(Varbits.PRAYER_MYSTIC_LORE, "Mystic Lore"),
    STEEL_SKIN(Varbits.PRAYER_STEEL_SKIN, "Steel Skin"),
    ULTIMATE_STRENGTH(Varbits.PRAYER_ULTIMATE_STRENGTH, "Ultimate Strength"),
    INCREDIBLE_REFLEXES(Varbits.PRAYER_INCREDIBLE_REFLEXES, "Incredible Reflexes"),
    PROTECT_FROM_MAGIC(Varbits.PRAYER_PROTECT_FROM_MAGIC, "Protect from Magic"),
    PROTECT_FROM_MISSILES(Varbits.PRAYER_PROTECT_FROM_MISSILES, "Protect from Missiles"),
    PROTECT_FROM_MELEE(Varbits.PRAYER_PROTECT_FROM_MELEE, "Protect from Melee"),
    EAGLE_EYE(Varbits.PRAYER_EAGLE_EYE, "Eagle Eye"),
    MYSTIC_MIGHT(Varbits.PRAYER_MYSTIC_MIGHT, "Mystic Might"),
    RETRIBUTION(Varbits.PRAYER_RETRIBUTION, "Retribution"),
    REDEMPTION(Varbits.PRAYER_REDEMPTION, "Redemption"),
    SMITE(Varbits.PRAYER_SMITE, "Smite"),
    CHIVALRY(Varbits.PRAYER_CHIVALRY, "Chivalry"),
    DEADEYE(Varbits.PRAYER_DEADEYE, "Deadeye"),
    MYSTIC_VIGOUR(Varbits.PRAYER_MYSTIC_VIGOUR, "Mystic Vigour"),
    PIETY(Varbits.PRAYER_PIETY, "Piety"),
    PRESERVE(Varbits.PRAYER_PRESERVE, "Preserve"),
    RIGOUR(Varbits.PRAYER_RIGOUR, "Rigour"),
    AUGURY(Varbits.PRAYER_AUGURY, "Augury"),

    RP_REJUVENATION(Varbits.PRAYER_RP_REJUVENATION, "Rejuvenation"),
    RP_ANCIENT_STRENGTH(Varbits.PRAYER_RP_ANCIENT_STRENGTH, "Ancient Strength"),
    RP_ANCIENT_SIGHT(Varbits.PRAYER_RP_ANCIENT_SIGHT, "Ancient Sight"),
    RP_ANCIENT_WILL(Varbits.PRAYER_RP_ANCIENT_WILL, "Ancient Will"),
    RP_PROTECT_ITEM(Varbits.PRAYER_RP_PROTECT_ITEM, "Protect Item (RP)"),
    RP_RUINOUS_GRACE(Varbits.PRAYER_RP_RUINOUS_GRACE, "Ruinous Grace"),
    RP_DAMPEN_MAGIC(Varbits.PRAYER_RP_DAMPEN_MAGIC, "Dampen Magic"),
    RP_DAMPEN_RANGED(Varbits.PRAYER_RP_DAMPEN_RANGED, "Dampen Ranged"),
    RP_DAMPEN_MELEE(Varbits.PRAYER_RP_DAMPEN_MELEE, "Dampen Melee"),
    RP_TRINITAS(Varbits.PRAYER_RP_TRINITAS, "Trinitas"),
    RP_BERSERKER(Varbits.PRAYER_RP_BERSERKER, "Berserker"),
    RP_PURGE(Varbits.PRAYER_RP_PURGE, "Purge"),
    RP_METABOLISE(Varbits.PRAYER_RP_METABOLISE, "Metabolise"),
    RP_REBUKE(Varbits.PRAYER_RP_REBUKE, "Rebuke"),
    RP_VINDICATION(Varbits.PRAYER_RP_VINDICATION, "Vindication"),
    RP_DECIMATE(Varbits.PRAYER_RP_DECIMATE, "Decimate"),
    RP_ANNIHILATE(Varbits.PRAYER_RP_ANNIHILATE, "Annihilate"),
    RP_VAPORISE(Varbits.PRAYER_RP_VAPORISE, "Vaporise"),
    RP_FUMUS_VOW(Varbits.PRAYER_RP_FUMUS_VOW, "Fumus' Vow"),
    RP_UMBRA_VOW(Varbits.PRAYER_RP_UMBRA_VOW, "Umbra's Vow"),
    RP_CRUORS_VOW(Varbits.PRAYER_RP_CRUORS_VOW, "Cruor's Vow"),
    RP_GLACIES_VOW(Varbits.PRAYER_RP_GLACIES_VOW, "Glacies' Vow"),
    RP_WRATH(Varbits.PRAYER_RP_WRATH, "Wrath"),
    RP_INTENSIFY(Varbits.PRAYER_RP_INTENSIFY, "Intensify");

    private final int varbitId;
    private final String displayName;

    Prayer(int varbitId, String displayName) {
        this.varbitId = varbitId;
        this.displayName = displayName;
    }

    public int varbitId() { return varbitId; }
    public String displayName() { return displayName; }
}
