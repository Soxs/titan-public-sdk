/// @file titan/head_icon.h
/// @brief Overhead icon enumeration for prayers, curses, and protection variants.
///
/// Canonical SDK location for the `HeadIcon` enum that mirrors RuneLite's
/// `net.runelite.api.HeadIcon` ordinal layout. Values returned by
/// `titan::Player::overheadIcon()` and `titan::Npc::overheadIcon()` (SDK
/// v35+) resolve directly to a `titan::HeadIcon` ordinal when they lie
/// in the 0..COUNT-1 range.
///
/// `titan::Player::skullIcon()` returns a separate skull-sprite index
/// (PvP/Wilderness skulls, forinthry surge, etc.) and is NOT indexed by
/// this enum -- use `titan::Player::isSkulled()` for the active/inactive
/// predicate.
///
/// Client-internal code can include `client/game/types/head_icon.h`, which
/// forwards this header into the project's top-level `HeadIcon` /
/// `HeadIconInfo` names to match the convention used by `prayer.h`,
/// `skill.h`, `menu_action.h`, etc.

#pragma once

#include <cstdint>

namespace titan {

/// All overhead prayer/curse icons displayed above players and NPCs.
/// Ordinals match the raw int read from `ClientPlayer +
/// PLAYER_OVERHEAD_ICON_OFFSET` and the first slot of
/// `NpcType::headIconGraphics`.
enum class HeadIcon : int {
    MELEE = 0,
    RANGED,
    MAGIC,
    RETRIBUTION,
    SMITE,
    REDEMPTION,
    RANGE_MAGE,
    RANGE_MELEE,
    MAGE_MELEE,
    RANGE_MAGE_MELEE,
    WRATH,
    SOUL_SPLIT,
    DEFLECT_MELEE,
    DEFLECT_RANGE,
    DEFLECT_MAGE,

    COUNT
};

/// Utility functions for HeadIcon metadata.
namespace HeadIconInfo {

/// True when @p raw is a valid HeadIcon ordinal (0..COUNT-1).
inline bool isValid(int raw) {
    return raw >= 0 && raw < static_cast<int>(HeadIcon::COUNT);
}

/// Convert a raw int (as returned by `Player::overheadIcon()` /
/// `Npc::overheadIcon()`) into a strongly-typed HeadIcon. Returns
/// `HeadIcon::COUNT` as a sentinel when @p raw is out of range.
inline HeadIcon fromRaw(int raw) {
    return isValid(raw) ? static_cast<HeadIcon>(raw) : HeadIcon::COUNT;
}

/// @return Human-readable display name (e.g. "Protect from Melee", "Soul Split").
inline const char* name(HeadIcon h) {
    switch (h) {
        case HeadIcon::MELEE:            return "Protect from Melee";
        case HeadIcon::RANGED:           return "Protect from Ranged";
        case HeadIcon::MAGIC:            return "Protect from Magic";
        case HeadIcon::RETRIBUTION:      return "Retribution";
        case HeadIcon::SMITE:            return "Smite";
        case HeadIcon::REDEMPTION:       return "Redemption";
        case HeadIcon::RANGE_MAGE:       return "Protect Range+Magic";
        case HeadIcon::RANGE_MELEE:      return "Protect Range+Melee";
        case HeadIcon::MAGE_MELEE:       return "Protect Magic+Melee";
        case HeadIcon::RANGE_MAGE_MELEE: return "Protect All";
        case HeadIcon::WRATH:            return "Wrath";
        case HeadIcon::SOUL_SPLIT:       return "Soul Split";
        case HeadIcon::DEFLECT_MELEE:    return "Deflect Melee";
        case HeadIcon::DEFLECT_RANGE:    return "Deflect Ranged";
        case HeadIcon::DEFLECT_MAGE:     return "Deflect Magic";
        default:                         return "Unknown";
    }
}

/// Convenience overload for raw ints (e.g. direct output of `overheadIcon()`).
inline const char* name(int raw) { return name(fromRaw(raw)); }

/// Short 3-to-6-char abbreviation for dense overlay tags ("MELEE", "SOUL", "D-Mag").
inline const char* shortName(HeadIcon h) {
    switch (h) {
        case HeadIcon::MELEE:            return "MELEE";
        case HeadIcon::RANGED:           return "RANGE";
        case HeadIcon::MAGIC:            return "MAGE";
        case HeadIcon::RETRIBUTION:      return "RETRI";
        case HeadIcon::SMITE:            return "SMITE";
        case HeadIcon::REDEMPTION:       return "REDEM";
        case HeadIcon::RANGE_MAGE:       return "R+M";
        case HeadIcon::RANGE_MELEE:      return "R+Me";
        case HeadIcon::MAGE_MELEE:       return "M+Me";
        case HeadIcon::RANGE_MAGE_MELEE: return "R+M+Me";
        case HeadIcon::WRATH:            return "WRATH";
        case HeadIcon::SOUL_SPLIT:       return "SOUL";
        case HeadIcon::DEFLECT_MELEE:    return "D-Mel";
        case HeadIcon::DEFLECT_RANGE:    return "D-Rng";
        case HeadIcon::DEFLECT_MAGE:     return "D-Mag";
        default:                         return "?";
    }
}

inline const char* shortName(int raw) { return shortName(fromRaw(raw)); }

/// Standard prayer book icons (MELEE..RANGE_MAGE_MELEE).
inline bool isPrayer(HeadIcon h) {
    return h >= HeadIcon::MELEE && h <= HeadIcon::RANGE_MAGE_MELEE;
}

/// Ancient curses (WRATH..DEFLECT_MAGE).
inline bool isCurse(HeadIcon h) {
    return h >= HeadIcon::WRATH && h <= HeadIcon::DEFLECT_MAGE;
}

/// Suggested ARGB color for the icon, grouped by damage type
/// (melee = red, ranged = green, magic = blue, mixed/curses = purple).
/// Alpha is left at 0xE0 so the caller can mask it to change opacity.
inline uint32_t color(HeadIcon h) {
    switch (h) {
        case HeadIcon::MELEE:
        case HeadIcon::DEFLECT_MELEE:
        case HeadIcon::RETRIBUTION:      return 0xE0FF4040u;  // red
        case HeadIcon::RANGED:
        case HeadIcon::DEFLECT_RANGE:    return 0xE040FF60u;  // green
        case HeadIcon::MAGIC:
        case HeadIcon::DEFLECT_MAGE:     return 0xE04080FFu;  // blue
        case HeadIcon::SMITE:
        case HeadIcon::REDEMPTION:       return 0xE0FFD040u;  // yellow
        case HeadIcon::RANGE_MAGE:
        case HeadIcon::RANGE_MELEE:
        case HeadIcon::MAGE_MELEE:
        case HeadIcon::RANGE_MAGE_MELEE: return 0xE0C060FFu;  // purple (mixed)
        case HeadIcon::WRATH:            return 0xE0FF8030u;  // orange
        case HeadIcon::SOUL_SPLIT:       return 0xE0E0E0E0u;  // white
        default:                         return 0xE0C0C0C0u;  // grey
    }
}

inline uint32_t color(int raw) { return color(fromRaw(raw)); }

}  // namespace HeadIconInfo

}  // namespace titan
