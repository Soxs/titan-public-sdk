/// @file titan/skill.h
/// @brief Enumeration of OSRS skills with display name lookup.
///
/// Canonical SDK location for the `Skill` enum. Defines every trainable
/// skill as a strongly-typed enum and provides `SkillInfo::name()` for
/// converting skill values to human-readable strings. `Skill::COUNT`
/// gives the total number of skills for array sizing.
///
/// Ordinals match the game's internal stat array -- pass `static_cast<int32_t>(skill)`
/// directly to `titan::state::skills().boosted(...)` / `.real(...)` / `.experience(...)`
/// or use the enum-typed overloads that take `Skill` directly.
///
/// Client-internal code can include `client/game/types/skill.h`, which
/// forwards this header into the project's top-level `Skill` name.

#pragma once

#include <cstdint>

namespace titan {

/// All trainable skills in order matching the game's internal stat array.
enum class Skill : int {
    ATTACK = 0,
    DEFENCE,
    STRENGTH,
    HITPOINTS,
    RANGED,
    PRAYER,
    MAGIC,
    COOKING,
    WOODCUTTING,
    FLETCHING,
    FISHING,
    FIREMAKING,
    CRAFTING,
    SMITHING,
    MINING,
    HERBLORE,
    AGILITY,
    THIEVING,
    SLAYER,
    FARMING,
    RUNECRAFT,
    HUNTER,
    CONSTRUCTION,
    SAILING,
    COUNT
};

/// Utility functions for Skill metadata.
namespace SkillInfo {

/// Total number of trainable skills (value of `Skill::COUNT`). Handy for
/// sizing `int xpBaseline[SkillInfo::MAX_SKILLS]` arrays without the
/// `static_cast<int>(Skill::COUNT)` ceremony.
constexpr int MAX_SKILLS = static_cast<int>(Skill::COUNT);

/// @return Human-readable display name for the given skill.
inline const char* name(Skill s) {
    switch (s) {
        case Skill::ATTACK:       return "Attack";
        case Skill::DEFENCE:      return "Defence";
        case Skill::STRENGTH:     return "Strength";
        case Skill::HITPOINTS:    return "Hitpoints";
        case Skill::RANGED:       return "Ranged";
        case Skill::PRAYER:       return "Prayer";
        case Skill::MAGIC:        return "Magic";
        case Skill::COOKING:      return "Cooking";
        case Skill::WOODCUTTING:  return "Woodcutting";
        case Skill::FLETCHING:    return "Fletching";
        case Skill::FISHING:      return "Fishing";
        case Skill::FIREMAKING:   return "Firemaking";
        case Skill::CRAFTING:     return "Crafting";
        case Skill::SMITHING:     return "Smithing";
        case Skill::MINING:       return "Mining";
        case Skill::HERBLORE:     return "Herblore";
        case Skill::AGILITY:      return "Agility";
        case Skill::THIEVING:     return "Thieving";
        case Skill::SLAYER:       return "Slayer";
        case Skill::FARMING:      return "Farming";
        case Skill::RUNECRAFT:    return "Runecraft";
        case Skill::HUNTER:       return "Hunter";
        case Skill::CONSTRUCTION: return "Construction";
        case Skill::SAILING:      return "Sailing";
        default:                  return "Unknown";
    }
}

/// Convenience overload -- accepts the raw int ordinal as returned from
/// e.g. iteration over `0..SkillInfo::MAX_SKILLS`.
inline const char* name(int skillId) {
    if (skillId < 0 || skillId >= MAX_SKILLS) return "Unknown";
    return name(static_cast<Skill>(skillId));
}

}  // namespace SkillInfo

}  // namespace titan
