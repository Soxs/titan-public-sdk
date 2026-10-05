/// @file titan/utils/combat.h
/// @brief Plugin-side combat toggles: special attack orb, auto-retaliate.
///
/// Header-only inline wrappers around existing SDK primitives:
///   - `titan::state::widgets().interact(...)` for normal CC_OP clicks.
///   - `titan::state::vars().varp(...)` for the underlying state reads.
///
/// Mirrors the `Combat::*` namespace in `client/actions/combat.h`. Client
/// code uses the in-process `Widgets::interact` overload directly; plugins
/// use this header. No ABI bump is needed because everything composes
/// existing primitives.
///
/// Java reference: `theplug.utils.WidgetUtils.{enableSpecialAttack,
/// getSpecialAttackPercentage, specialAttackEnabled, isAutoRetaliateEnabled,
/// setAutoRetaliate}` -- see the matching client-side comments for the
/// exact RuneLite `LegacyMenuEntry` shapes that these wrappers reproduce.

#pragma once

#include "../client.h"
#include "../gamevals.h"
#include "../menu_action.h"
#include "../var_player.h"

#include <cstdint>

namespace titan {
namespace utils {
namespace Combat {

/// @return Special-attack energy as a percentage (0..100). Reads varp 301
///         (`SPECIAL_ATTACK`) which the game stores as percent * 10.
inline int getSpecialAttackPercentage() {
    return titan::state::vars().varp(VarPlayerID::SPECIAL_ATTACK) / 10;
}

/// @return true when the special-attack toggle is currently armed
///         (varp 300 == 1).
inline bool isSpecialAttackEnabled() {
    return titan::state::vars().varp(VarPlayerID::SPECIAL_ATTACK_ENABLED) == 1;
}

/// @return true when auto-retaliate is currently enabled. Note: the
///         underlying varp (172) is inverted -- the game stores 0 for
///         "enabled" and 1 for "disabled".
inline bool isAutoRetaliateEnabled() {
    return titan::state::vars().varp(VarPlayerID::AUTO_RETALIATE) == 0;
}

/// Click the special-attack orb (interface 160 / 34).
///
/// Reproduces the RuneLite `LegacyMenuEntry` shape
/// `{option="Use", target=<spec orb name>, identifier=1, opcode=CC_OP(57),
///  p0=-1, p1=<spec orb packed id>}`.
///
/// @param skipMovement Retained for source compatibility; special-attack
///                     toggles always use the normal synthetic click path.
/// @return true when the click was queued for the next client tick.
inline bool enableSpecialAttack(bool skipMovement = false) {
    (void)skipMovement;
    return titan::state::widgets().interact(
        static_cast<uint32_t>(MenuAction::Id::CcOp),
        /*identifier=*/1,
        /*param0=*/-1,
        /*param1 (widget packed id)=*/gamevals::InterfaceID::Orbs::ORB_SPECENERGY);
}

/// Toggle auto-retaliate to `enabled`. No-op (returns true) when the
/// player is already in the requested state.
///
/// Reproduces the RuneLite `LegacyMenuEntry` shape
/// `{option="Auto retaliate", target="", identifier=1, opcode=CC_OP(57),
///  p0=-1, p1=38862880}`.
///
/// @return true when the click was queued (or no change was needed).
inline bool setAutoRetaliate(bool enabled) {
    if (isAutoRetaliateEnabled() == enabled) return true;
    return titan::state::widgets().interact(
        static_cast<uint32_t>(MenuAction::Id::CcOp),
        /*identifier=*/1,
        /*param0=*/-1,
        /*param1 (widget packed id)=*/gamevals::InterfaceID::CombatInterface::RETALIATE);
}

}  // namespace Combat
}  // namespace utils
}  // namespace titan
