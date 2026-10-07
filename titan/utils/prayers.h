/// @file titan/utils/prayers.h
/// @brief Plugin-side prayer state and toggle helpers.
///
/// Header-only wrappers around existing SDK primitives:
///   - `titan::state::vars().varbit(...)` for active-state reads.
///   - `titan::state::widgets().find(...)` for safe widget resolution.
///   - `Widget::interact(...)` for the prayer button's CC_OP action.
///
/// The widget table follows the live prayer-definition
/// `OC_PRAYER_COMPONENT` values. Several prayers intentionally share a
/// component (Eagle Eye / Deadeye and Mystic Might / Mystic Vigour), while
/// standard prayers and Ruinous Powers reuse the same prayer-book slots.

#pragma once

#include "../client.h"
#include "../generated/gamevals/interface_id.h"
#include "../menu_action.h"
#include "../prayer.h"

#include <array>
#include <cstddef>
#include <cstdint>

namespace titan {
namespace utils {
namespace Prayers {
namespace detail {

inline constexpr std::array<uint32_t, 55> kPrayerWidgets = {
    // Standard prayer book.
    gamevals::InterfaceID::Prayerbook::PRAYER1,   // THICK_SKIN
    gamevals::InterfaceID::Prayerbook::PRAYER2,   // BURST_OF_STRENGTH
    gamevals::InterfaceID::Prayerbook::PRAYER3,   // CLARITY_OF_THOUGHT
    gamevals::InterfaceID::Prayerbook::PRAYER19,  // SHARP_EYE
    gamevals::InterfaceID::Prayerbook::PRAYER22,  // MYSTIC_WILL
    gamevals::InterfaceID::Prayerbook::PRAYER4,   // ROCK_SKIN
    gamevals::InterfaceID::Prayerbook::PRAYER5,   // SUPERHUMAN_STRENGTH
    gamevals::InterfaceID::Prayerbook::PRAYER6,   // IMPROVED_REFLEXES
    gamevals::InterfaceID::Prayerbook::PRAYER7,   // RAPID_RESTORE
    gamevals::InterfaceID::Prayerbook::PRAYER8,   // RAPID_HEAL
    gamevals::InterfaceID::Prayerbook::PRAYER9,   // PROTECT_ITEM
    gamevals::InterfaceID::Prayerbook::PRAYER20,  // HAWK_EYE
    gamevals::InterfaceID::Prayerbook::PRAYER23,  // MYSTIC_LORE
    gamevals::InterfaceID::Prayerbook::PRAYER10,  // STEEL_SKIN
    gamevals::InterfaceID::Prayerbook::PRAYER11,  // ULTIMATE_STRENGTH
    gamevals::InterfaceID::Prayerbook::PRAYER12,  // INCREDIBLE_REFLEXES
    gamevals::InterfaceID::Prayerbook::PRAYER13,  // PROTECT_FROM_MAGIC
    gamevals::InterfaceID::Prayerbook::PRAYER14,  // PROTECT_FROM_MISSILES
    gamevals::InterfaceID::Prayerbook::PRAYER15,  // PROTECT_FROM_MELEE
    gamevals::InterfaceID::Prayerbook::PRAYER21,  // EAGLE_EYE
    gamevals::InterfaceID::Prayerbook::PRAYER24,  // MYSTIC_MIGHT
    gamevals::InterfaceID::Prayerbook::PRAYER16,  // RETRIBUTION
    gamevals::InterfaceID::Prayerbook::PRAYER17,  // REDEMPTION
    gamevals::InterfaceID::Prayerbook::PRAYER18,  // SMITE
    gamevals::InterfaceID::Prayerbook::PRAYER26,  // CHIVALRY
    gamevals::InterfaceID::Prayerbook::PRAYER21,  // DEADEYE
    gamevals::InterfaceID::Prayerbook::PRAYER24,  // MYSTIC_VIGOUR
    gamevals::InterfaceID::Prayerbook::PRAYER27,  // PIETY
    gamevals::InterfaceID::Prayerbook::PRAYER29,  // PRESERVE
    gamevals::InterfaceID::Prayerbook::PRAYER25,  // RIGOUR
    gamevals::InterfaceID::Prayerbook::PRAYER28,  // AUGURY

    // Ruinous Powers. These are ordered by Prayer enum ordinal, not by
    // component number.
    gamevals::InterfaceID::Prayerbook::PRAYER12,  // RP_REJUVENATION
    gamevals::InterfaceID::Prayerbook::PRAYER1,   // RP_ANCIENT_STRENGTH
    gamevals::InterfaceID::Prayerbook::PRAYER2,   // RP_ANCIENT_SIGHT
    gamevals::InterfaceID::Prayerbook::PRAYER3,   // RP_ANCIENT_WILL
    gamevals::InterfaceID::Prayerbook::PRAYER24,  // RP_PROTECT_ITEM
    gamevals::InterfaceID::Prayerbook::PRAYER13,  // RP_RUINOUS_GRACE
    gamevals::InterfaceID::Prayerbook::PRAYER10,  // RP_DAMPEN_MAGIC
    gamevals::InterfaceID::Prayerbook::PRAYER9,   // RP_DAMPEN_RANGED
    gamevals::InterfaceID::Prayerbook::PRAYER8,   // RP_DAMPEN_MELEE
    gamevals::InterfaceID::Prayerbook::PRAYER4,   // RP_TRINITAS
    gamevals::InterfaceID::Prayerbook::PRAYER16,  // RP_BERSERKER
    gamevals::InterfaceID::Prayerbook::PRAYER11,  // RP_PURGE
    gamevals::InterfaceID::Prayerbook::PRAYER15,  // RP_METABOLISE
    gamevals::InterfaceID::Prayerbook::PRAYER22,  // RP_REBUKE
    gamevals::InterfaceID::Prayerbook::PRAYER23,  // RP_VINDICATION
    gamevals::InterfaceID::Prayerbook::PRAYER5,   // RP_DECIMATE
    gamevals::InterfaceID::Prayerbook::PRAYER6,   // RP_ANNIHILATE
    gamevals::InterfaceID::Prayerbook::PRAYER7,   // RP_VAPORISE
    gamevals::InterfaceID::Prayerbook::PRAYER17,  // RP_FUMUS_VOW
    gamevals::InterfaceID::Prayerbook::PRAYER19,  // RP_UMBRA_VOW
    gamevals::InterfaceID::Prayerbook::PRAYER18,  // RP_CRUORS_VOW
    gamevals::InterfaceID::Prayerbook::PRAYER20,  // RP_GLACIES_VOW
    gamevals::InterfaceID::Prayerbook::PRAYER14,  // RP_WRATH
    gamevals::InterfaceID::Prayerbook::PRAYER21,  // RP_INTENSIFY
};

static_assert(static_cast<int32_t>(Prayer::COUNT) == 55,
              "Update the prayer widget table when Prayer changes");

inline bool valid(Prayer prayer) {
    const auto ordinal = static_cast<int32_t>(prayer);
    return ordinal >= 0 &&
           ordinal < static_cast<int32_t>(kPrayerWidgets.size());
}

inline uint32_t widgetPackedId(Prayer prayer) {
    return kPrayerWidgets[static_cast<std::size_t>(
        static_cast<int32_t>(prayer))];
}

inline int32_t activeState(Prayer prayer) {
    if (!valid(prayer) || !titan::detail::backend()) return -1;
    return titan::state::vars().varbit(PrayerInfo::varbitId(prayer));
}

}  // namespace detail

/// @return true when @p prayer is currently active. Invalid enum values,
///         including `Prayer::COUNT`, return false.
inline bool isActive(Prayer prayer) {
    return detail::activeState(prayer) > 0;
}

/// Click the widget associated with @p prayer.
///
/// The widget must be present in the live prayer-book interface. The exact
/// synthetic menu shape is `{opcode=CC_OP, identifier=1, param0=-1}`.
///
/// @return true when the interaction was queued; false for invalid prayers,
///         missing widgets, or a rejected interaction.
inline bool toggle(Prayer prayer) {
    if (!detail::valid(prayer)) return false;

    auto widget = titan::state::widgets().find(
        detail::widgetPackedId(prayer));
    if (!widget || !widget->exists()) return false;

    return widget->interact(
        static_cast<uint32_t>(MenuAction::Id::CcOp),
        /*identifier=*/1,
        /*param0=*/-1);
}

/// Idempotently set @p prayer to the requested active state.
///
/// @return true when no change was needed or the toggle interaction was
///         queued; false for invalid prayers or failed interaction setup.
inline bool setActive(Prayer prayer, bool enabled) {
    if (!detail::valid(prayer)) return false;
    const int32_t active = detail::activeState(prayer);
    if (active < 0) return false;
    if ((active != 0) == enabled) return true;
    return toggle(prayer);
}

/// Idempotently enable @p prayer.
inline bool enable(Prayer prayer) {
    return setActive(prayer, true);
}

/// Idempotently disable @p prayer.
inline bool disable(Prayer prayer) {
    return setActive(prayer, false);
}

}  // namespace Prayers
}  // namespace utils
}  // namespace titan
