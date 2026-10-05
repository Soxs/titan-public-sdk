/// @file titan/prayer.h
/// @brief Prayer enumeration with varbit mapping and display names.
///
/// Canonical SDK location for the `Prayer` enum. Enumerates every prayer
/// from both the standard prayer book and the Ruinous Powers set.
/// `PrayerInfo` provides varbit-ID lookup (for checking active state via
/// `titan::state::vars().varbit(...)`) and human-readable name strings.
///
/// Client-internal code can include `client/game/types/prayer.h`, which
/// forwards this header into the project's top-level `Prayer` name.

#pragma once

#include "varbits.h"

namespace titan {

/// All prayers across both the standard and Ruinous Powers prayer books.
enum class Prayer {
    THICK_SKIN,
    BURST_OF_STRENGTH,
    CLARITY_OF_THOUGHT,
    SHARP_EYE,
    MYSTIC_WILL,
    ROCK_SKIN,
    SUPERHUMAN_STRENGTH,
    IMPROVED_REFLEXES,
    RAPID_RESTORE,
    RAPID_HEAL,
    PROTECT_ITEM,
    HAWK_EYE,
    MYSTIC_LORE,
    STEEL_SKIN,
    ULTIMATE_STRENGTH,
    INCREDIBLE_REFLEXES,
    PROTECT_FROM_MAGIC,
    PROTECT_FROM_MISSILES,
    PROTECT_FROM_MELEE,
    EAGLE_EYE,
    MYSTIC_MIGHT,
    RETRIBUTION,
    REDEMPTION,
    SMITE,
    CHIVALRY,
    DEADEYE,
    MYSTIC_VIGOUR,
    PIETY,
    PRESERVE,
    RIGOUR,
    AUGURY,

    RP_REJUVENATION,
    RP_ANCIENT_STRENGTH,
    RP_ANCIENT_SIGHT,
    RP_ANCIENT_WILL,
    RP_PROTECT_ITEM,
    RP_RUINOUS_GRACE,
    RP_DAMPEN_MAGIC,
    RP_DAMPEN_RANGED,
    RP_DAMPEN_MELEE,
    RP_TRINITAS,
    RP_BERSERKER,
    RP_PURGE,
    RP_METABOLISE,
    RP_REBUKE,
    RP_VINDICATION,
    RP_DECIMATE,
    RP_ANNIHILATE,
    RP_VAPORISE,
    RP_FUMUS_VOW,
    RP_UMBRA_VOW,
    RP_CRUORS_VOW,
    RP_GLACIES_VOW,
    RP_WRATH,
    RP_INTENSIFY,

    COUNT
};

/// Lookup functions for Prayer metadata.
namespace PrayerInfo {

/// @return The varbit ID used to read whether @p p is currently active.
inline int varbitId(Prayer p) {
    switch (p) {
        case Prayer::THICK_SKIN:            return Varbits::PRAYER_THICK_SKIN;
        case Prayer::BURST_OF_STRENGTH:     return Varbits::PRAYER_BURST_OF_STRENGTH;
        case Prayer::CLARITY_OF_THOUGHT:    return Varbits::PRAYER_CLARITY_OF_THOUGHT;
        case Prayer::SHARP_EYE:             return Varbits::PRAYER_SHARP_EYE;
        case Prayer::MYSTIC_WILL:           return Varbits::PRAYER_MYSTIC_WILL;
        case Prayer::ROCK_SKIN:             return Varbits::PRAYER_ROCK_SKIN;
        case Prayer::SUPERHUMAN_STRENGTH:   return Varbits::PRAYER_SUPERHUMAN_STRENGTH;
        case Prayer::IMPROVED_REFLEXES:     return Varbits::PRAYER_IMPROVED_REFLEXES;
        case Prayer::RAPID_RESTORE:         return Varbits::PRAYER_RAPID_RESTORE;
        case Prayer::RAPID_HEAL:            return Varbits::PRAYER_RAPID_HEAL;
        case Prayer::PROTECT_ITEM:          return Varbits::PRAYER_PROTECT_ITEM;
        case Prayer::HAWK_EYE:              return Varbits::PRAYER_HAWK_EYE;
        case Prayer::MYSTIC_LORE:           return Varbits::PRAYER_MYSTIC_LORE;
        case Prayer::STEEL_SKIN:            return Varbits::PRAYER_STEEL_SKIN;
        case Prayer::ULTIMATE_STRENGTH:     return Varbits::PRAYER_ULTIMATE_STRENGTH;
        case Prayer::INCREDIBLE_REFLEXES:   return Varbits::PRAYER_INCREDIBLE_REFLEXES;
        case Prayer::PROTECT_FROM_MAGIC:    return Varbits::PRAYER_PROTECT_FROM_MAGIC;
        case Prayer::PROTECT_FROM_MISSILES: return Varbits::PRAYER_PROTECT_FROM_MISSILES;
        case Prayer::PROTECT_FROM_MELEE:    return Varbits::PRAYER_PROTECT_FROM_MELEE;
        case Prayer::EAGLE_EYE:             return Varbits::PRAYER_EAGLE_EYE;
        case Prayer::MYSTIC_MIGHT:          return Varbits::PRAYER_MYSTIC_MIGHT;
        case Prayer::RETRIBUTION:           return Varbits::PRAYER_RETRIBUTION;
        case Prayer::REDEMPTION:            return Varbits::PRAYER_REDEMPTION;
        case Prayer::SMITE:                 return Varbits::PRAYER_SMITE;
        case Prayer::CHIVALRY:              return Varbits::PRAYER_CHIVALRY;
        case Prayer::DEADEYE:               return Varbits::PRAYER_DEADEYE;
        case Prayer::MYSTIC_VIGOUR:         return Varbits::PRAYER_MYSTIC_VIGOUR;
        case Prayer::PIETY:                 return Varbits::PRAYER_PIETY;
        case Prayer::PRESERVE:              return Varbits::PRAYER_PRESERVE;
        case Prayer::RIGOUR:                return Varbits::PRAYER_RIGOUR;
        case Prayer::AUGURY:                return Varbits::PRAYER_AUGURY;
        case Prayer::RP_REJUVENATION:       return Varbits::PRAYER_RP_REJUVENATION;
        case Prayer::RP_ANCIENT_STRENGTH:   return Varbits::PRAYER_RP_ANCIENT_STRENGTH;
        case Prayer::RP_ANCIENT_SIGHT:      return Varbits::PRAYER_RP_ANCIENT_SIGHT;
        case Prayer::RP_ANCIENT_WILL:       return Varbits::PRAYER_RP_ANCIENT_WILL;
        case Prayer::RP_PROTECT_ITEM:       return Varbits::PRAYER_RP_PROTECT_ITEM;
        case Prayer::RP_RUINOUS_GRACE:      return Varbits::PRAYER_RP_RUINOUS_GRACE;
        case Prayer::RP_DAMPEN_MAGIC:       return Varbits::PRAYER_RP_DAMPEN_MAGIC;
        case Prayer::RP_DAMPEN_RANGED:      return Varbits::PRAYER_RP_DAMPEN_RANGED;
        case Prayer::RP_DAMPEN_MELEE:       return Varbits::PRAYER_RP_DAMPEN_MELEE;
        case Prayer::RP_TRINITAS:           return Varbits::PRAYER_RP_TRINITAS;
        case Prayer::RP_BERSERKER:          return Varbits::PRAYER_RP_BERSERKER;
        case Prayer::RP_PURGE:              return Varbits::PRAYER_RP_PURGE;
        case Prayer::RP_METABOLISE:         return Varbits::PRAYER_RP_METABOLISE;
        case Prayer::RP_REBUKE:             return Varbits::PRAYER_RP_REBUKE;
        case Prayer::RP_VINDICATION:        return Varbits::PRAYER_RP_VINDICATION;
        case Prayer::RP_DECIMATE:           return Varbits::PRAYER_RP_DECIMATE;
        case Prayer::RP_ANNIHILATE:         return Varbits::PRAYER_RP_ANNIHILATE;
        case Prayer::RP_VAPORISE:           return Varbits::PRAYER_RP_VAPORISE;
        case Prayer::RP_FUMUS_VOW:          return Varbits::PRAYER_RP_FUMUS_VOW;
        case Prayer::RP_UMBRA_VOW:          return Varbits::PRAYER_RP_UMBRA_VOW;
        case Prayer::RP_CRUORS_VOW:         return Varbits::PRAYER_RP_CRUORS_VOW;
        case Prayer::RP_GLACIES_VOW:        return Varbits::PRAYER_RP_GLACIES_VOW;
        case Prayer::RP_WRATH:              return Varbits::PRAYER_RP_WRATH;
        case Prayer::RP_INTENSIFY:          return Varbits::PRAYER_RP_INTENSIFY;
        default:                            return -1;
    }
}

/// @return Human-readable display name for the given prayer.
inline const char* name(Prayer p) {
    switch (p) {
        case Prayer::THICK_SKIN:            return "Thick Skin";
        case Prayer::BURST_OF_STRENGTH:     return "Burst of Strength";
        case Prayer::CLARITY_OF_THOUGHT:    return "Clarity of Thought";
        case Prayer::SHARP_EYE:             return "Sharp Eye";
        case Prayer::MYSTIC_WILL:           return "Mystic Will";
        case Prayer::ROCK_SKIN:             return "Rock Skin";
        case Prayer::SUPERHUMAN_STRENGTH:   return "Superhuman Strength";
        case Prayer::IMPROVED_REFLEXES:     return "Improved Reflexes";
        case Prayer::RAPID_RESTORE:         return "Rapid Restore";
        case Prayer::RAPID_HEAL:            return "Rapid Heal";
        case Prayer::PROTECT_ITEM:          return "Protect Item";
        case Prayer::HAWK_EYE:              return "Hawk Eye";
        case Prayer::MYSTIC_LORE:           return "Mystic Lore";
        case Prayer::STEEL_SKIN:            return "Steel Skin";
        case Prayer::ULTIMATE_STRENGTH:     return "Ultimate Strength";
        case Prayer::INCREDIBLE_REFLEXES:   return "Incredible Reflexes";
        case Prayer::PROTECT_FROM_MAGIC:    return "Protect from Magic";
        case Prayer::PROTECT_FROM_MISSILES: return "Protect from Missiles";
        case Prayer::PROTECT_FROM_MELEE:    return "Protect from Melee";
        case Prayer::EAGLE_EYE:             return "Eagle Eye";
        case Prayer::MYSTIC_MIGHT:          return "Mystic Might";
        case Prayer::RETRIBUTION:           return "Retribution";
        case Prayer::REDEMPTION:            return "Redemption";
        case Prayer::SMITE:                 return "Smite";
        case Prayer::CHIVALRY:              return "Chivalry";
        case Prayer::DEADEYE:               return "Deadeye";
        case Prayer::MYSTIC_VIGOUR:         return "Mystic Vigour";
        case Prayer::PIETY:                 return "Piety";
        case Prayer::PRESERVE:              return "Preserve";
        case Prayer::RIGOUR:                return "Rigour";
        case Prayer::AUGURY:                return "Augury";
        case Prayer::RP_REJUVENATION:       return "Rejuvenation";
        case Prayer::RP_ANCIENT_STRENGTH:   return "Ancient Strength";
        case Prayer::RP_ANCIENT_SIGHT:      return "Ancient Sight";
        case Prayer::RP_ANCIENT_WILL:       return "Ancient Will";
        case Prayer::RP_PROTECT_ITEM:       return "Protect Item (RP)";
        case Prayer::RP_RUINOUS_GRACE:      return "Ruinous Grace";
        case Prayer::RP_DAMPEN_MAGIC:       return "Dampen Magic";
        case Prayer::RP_DAMPEN_RANGED:      return "Dampen Ranged";
        case Prayer::RP_DAMPEN_MELEE:       return "Dampen Melee";
        case Prayer::RP_TRINITAS:           return "Trinitas";
        case Prayer::RP_BERSERKER:          return "Berserker";
        case Prayer::RP_PURGE:              return "Purge";
        case Prayer::RP_METABOLISE:         return "Metabolise";
        case Prayer::RP_REBUKE:             return "Rebuke";
        case Prayer::RP_VINDICATION:        return "Vindication";
        case Prayer::RP_DECIMATE:           return "Decimate";
        case Prayer::RP_ANNIHILATE:         return "Annihilate";
        case Prayer::RP_VAPORISE:           return "Vaporise";
        case Prayer::RP_FUMUS_VOW:          return "Fumus' Vow";
        case Prayer::RP_UMBRA_VOW:          return "Umbra's Vow";
        case Prayer::RP_CRUORS_VOW:         return "Cruor's Vow";
        case Prayer::RP_GLACIES_VOW:        return "Glacies' Vow";
        case Prayer::RP_WRATH:              return "Wrath";
        case Prayer::RP_INTENSIFY:          return "Intensify";
        default:                            return "Unknown";
    }
}

}  // namespace PrayerInfo

}  // namespace titan
