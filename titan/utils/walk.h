/// @file titan/utils/walk.h
/// @brief Plugin-side run toggle helpers.
///
/// Header-only inline wrappers around existing SDK primitives:
///   - `titan::state::vars().varp(...)` for the run-enabled state read.
///   - `titan::state::widgets().interact(...)` for the orb click.
///
/// Mirrors the `Walk::*` namespace pattern established by `combat.h`.
/// No ABI bump -- everything composes existing primitives.
///
/// Java reference: `theplug.utils.core.api.WalkUtils.{isRunning, toggleRun,
/// setRunning}`.

#pragma once

#include "../client.h"
#include "../gamevals.h"
#include "../menu_action.h"
#include "../var_player.h"

#include <cstdint>
#include <limits>

namespace titan {
namespace utils {
namespace Walk {

/// @return true when run mode is currently toggled on (varp 173 == 1).
inline bool isRunEnabled() {
    return titan::state::vars().varp(VarPlayerID::RUN_ENABLED) == 1;
}

/// Click the run orb to toggle run on/off.
///
/// The orb lives in a different interface group per layout -- `Orbs` (160)
/// in fixed mode, `OrbsNomap` (895), `OrbsOsm` (897) and `OrbsOsmNomap`
/// (898) otherwise -- and the component that carries the op is `RUNBUTTON`
/// (matching `PRAYERBUTTON` and `SPECBUTTON`), not the `RUNENERGY_BACKING`
/// sprite drawn behind it. Addressing `Orbs::RUNENERGY_BACKING` alone made
/// this a no-op in every layout but fixed (CallbackV1 refuses an entry whose
/// component is not loaded) and sampled the click point from the backing
/// rect rather than the button in fixed mode. Resolve the live orb for the
/// player's current layout instead -- note RLPL's `Movement.toggleRun` does
/// NOT do this (it clicks one hardcoded group-160 child) and carries the
/// same orb-spam bug because of it.
///
/// @param skipMovement Retained for source compatibility; run toggles always
///                     use the normal synthetic click path.
/// @return true when the click was queued for the next client tick; false
///         when no layout's orb is currently visible.
inline bool toggleRun(bool skipMovement = false) {
    (void)skipMovement;
    for (const int32_t packed : {gamevals::InterfaceID::Orbs::RUNBUTTON,
                                 gamevals::InterfaceID::OrbsNomap::RUNBUTTON,
                                 gamevals::InterfaceID::OrbsOsm::RUNBUTTON,
                                 gamevals::InterfaceID::OrbsOsmNomap::RUNBUTTON}) {
        const auto orb = titan::state::widgets().find(
            static_cast<uint32_t>(packed));
        if (!orb || !orb->visible()) continue;
        return orb->interact(static_cast<uint32_t>(MenuAction::Id::CcOp),
                             /*identifier=*/1);
    }
    return false;
}

/// Idempotent setter: enables or disables run mode. No-op (returns true)
/// when the player is already in the requested state.
///
/// @return true when the click was queued (or no change was needed).
inline bool setRunEnabled(bool enabled) {
    if (isRunEnabled() == enabled) return true;
    return toggleRun();
}

/// @return Run energy in the 0-10000 range (divide by 100 for the orb %).
///         Delegates to `titan::state::client().runEnergy()`. SDK v45+.
inline int32_t runEnergy() {
    return titan::state::client().runEnergy();
}

/// @return Run energy as a percentage (0-100), matching the orb display.
inline int32_t runEnergyPercent() {
    return runEnergy() / 100;
}

/// Ticks a sent toggle is given to land before another is allowed. Varp 173
/// is server-authoritative, so it still reads "off" on the tick after the
/// click; a caller polling `ensureRunEnabled` every tick would otherwise
/// re-click mid-flight and toggle run straight back off.
inline constexpr int32_t kRunToggleSettleTicks = 3;

/// Enable run only when the player has enough energy. Safe to call every
/// tick: a sent toggle is given `kRunToggleSettleTicks` to land before
/// another click goes out.
/// @param minPercent  Minimum energy percentage (0-100) required to
///                    toggle run on. Defaults to 1 (any energy).
/// @return true when run was already on or a click went out; false when no
///         click was sent (not enough energy, a toggle still settling, or
///         no visible orb).
inline bool ensureRunEnabled(int32_t minPercent = 1) {
    // One instance per plugin module, which is the right granularity: a
    // module has exactly one run orb to contend for.
    static int32_t lastToggleTick = (std::numeric_limits<int32_t>::min)() / 2;
    if (isRunEnabled()) return true;
    if (runEnergyPercent() < minPercent) return false;
    const int32_t now = titan::state::client().tick();
    // `now < lastToggleTick` means the tick counter restarted under us
    // (fresh client); treat that as "window elapsed" rather than locking
    // the toggle out until the counter climbs back.
    if (now >= lastToggleTick && now - lastToggleTick < kRunToggleSettleTicks) {
        return false;
    }
    lastToggleTick = now;
    return toggleRun();
}

}  // namespace Walk
}  // namespace utils
}  // namespace titan
