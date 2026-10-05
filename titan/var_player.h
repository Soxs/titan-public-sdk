/// @file titan/var_player.h
/// @brief VarPlayer (varp) index constants for core player state.
///
/// Canonical SDK location for the `VarPlayerID::*` catalog. VarPlayer
/// values are the game's primary per-player configuration store -- each
/// index maps to a 32-bit integer that the client reads for things like
/// special attack percentage, poison state, and the run-mode toggle.
/// Varbits (see `titan/varbits.h`) are packed sub-fields within these
/// varp slots. Note: actual run energy (0-10000) is a direct Client
/// field, not a varp -- use `titan::state::client().runEnergy()`.
///
/// Client-internal code can include `client/game/types/var_player.h`,
/// which forwards this header into the project's top-level `VarPlayerID`
/// namespace to match the convention used by `prayer.h` / `skill.h` /
/// `varbits.h` / etc.

#pragma once

#include "generated/gamevals/var_player_id.h"

namespace titan {

/// Known VarPlayer indices for commonly queried player state.
namespace VarPlayerID {

constexpr int ATTACK_STYLE          = titan::gamevals::VarPlayerID::COM_MODE;
constexpr int SPECIAL_ATTACK        = titan::gamevals::VarPlayerID::SA_ENERGY;
/// Whether the special-attack toggle is currently armed (1) or off (0).
constexpr int SPECIAL_ATTACK_ENABLED = titan::gamevals::VarPlayerID::SA_ATTACK;
/// Whether run mode is toggled on (1) or off (0). In the native Steam
/// client, varp 173 stores the run/walk toggle only. The actual energy
/// value (0-10000) is a direct Client struct field exposed via
/// `titan::state::client().runEnergy()` (SDK v45+).
constexpr int RUN_ENABLED           = titan::gamevals::VarPlayerID::OPTION_RUN;
constexpr int POISON                = titan::gamevals::VarPlayerID::POISON;
/// Auto-retaliate toggle. Inverted: 0 == enabled, 1 == disabled.
constexpr int AUTO_RETALIATE        = titan::gamevals::VarPlayerID::OPTION_NODEF;
constexpr int DISEASE               = titan::gamevals::VarPlayerID::DISEASE;
/// @deprecated In the native Steam client, weight is a direct Client field.
/// Use `titan::state::client().weight()` (SDK v45+) instead.
constexpr int HP_HUD_1              = 3209;
constexpr int HP_HUD_2              = 3210;
constexpr int PRAYER_POINTS         = 2382;
/// Last home-teleport use, stored as minutes since Unix epoch.
constexpr int LAST_HOME_TELEPORT    = titan::gamevals::VarPlayerID::AIDE_TELE_TIMER;

/// Return the catalog identifier for @p id, or nullptr when the id is not
/// named in this header.
inline const char* nameOf(int id) {
    switch (id) {
        case ATTACK_STYLE: return "ATTACK_STYLE";
        case SPECIAL_ATTACK: return "SPECIAL_ATTACK";
        case SPECIAL_ATTACK_ENABLED: return "SPECIAL_ATTACK_ENABLED";
        case RUN_ENABLED: return "RUN_ENABLED";
        case POISON: return "POISON";
        case AUTO_RETALIATE: return "AUTO_RETALIATE";
        case DISEASE: return "DISEASE";
        case HP_HUD_1: return "HP_HUD_1";
        case HP_HUD_2: return "HP_HUD_2";
        case PRAYER_POINTS: return "PRAYER_POINTS";
        case LAST_HOME_TELEPORT: return "LAST_HOME_TELEPORT";
        default: return nullptr;
    }
}

}  // namespace VarPlayerID

}  // namespace titan
