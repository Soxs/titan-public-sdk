/// @file titan/utils/magic/spell_requirements.h
/// @brief Spell resources and player prerequisites, independent of location and target validity.
#pragma once

#include "../runes/rune_element.h"
#include "../../generated/gamevals/interface_id.h"
#include "../../generated/gamevals/quest_id.h"
#include "../../generated/gamevals/quest_id_table.h"
#include "../../generated/gamevals/var_player_id.h"
#include "../../skill.h"
#include "../../var_player.h"

#include <chrono>
#include <initializer_list>

namespace titan::utils::Magic {

enum class SpellBook : int32_t {
    Standard = 0, Ancient = 1, Lunar = 2, Necromancy = 3
};

/// Costs are per batch of up to ten bolts; casting uses the enchant-bolts dialog.
enum class BoltEnchantment : int32_t {
    OPAL, SAPPHIRE, JADE, PEARL, EMERALD, RED_TOPAZ, RUBY, DIAMOND, DRAGONSTONE, ONYX, COUNT
};

struct RuneCost {
    ::titan::RuneElement element{};
    int32_t quantity = 0;
};

/// Named exceptions to the common quest, unlock, and cooldown checks.
enum class AdditionalRequirement {
    None, HomeTeleport, House, Ardougne, Watchtower, ApeAtoll,
    IbanStaff, MagicDart, SaradominStaff, GuthixStaff, ZamorakStaff,
    ThrallLesser, ThrallSuperior, ThrallGreater, DeathCharge, VileVigour,
    EnergyTransfer, HealOther
};

struct SpellRequirements {
    bool known = false; ///< Unknown catalogue entries must not be treated as free, unlocked spells.
    std::array<RuneCost, 4> runes{}; ///< Zero-quantity entries are unused.
    int32_t questId = 0; ///< Native QuestID, not a quest DB row or RuneLite enum ordinal.
    int32_t unlockVarbit = 0; ///< Must be positive when present.
    int32_t cooldownVarbit = 0; ///< Must be zero when present.
    AdditionalRequirement additional = AdditionalRequirement::None;
    int32_t spellSackId = 0; ///< Alternative to the entire rune cost when blighted items are allowed.
};

/// Owned inputs for deterministic/off-thread checks. Missing scalar values are unknown (-1).
/// Rune amounts should come from RuneElement::getCarriedRuneElementAmounts with the same item/world snapshot.
/// This is an availability snapshot, not a consumable budget for a sequence of casts.
struct SpellCastState {
    std::optional<SpellBook> spellBook;
    std::optional<bool> membersWorld;
    std::optional<RuneElement::RuneElementAmounts> runeAmounts;
    std::vector<ItemContainerSlot> inventory;
    std::vector<ItemContainerSlot> equipment;
    std::map<int32_t, int32_t> varbits;
    std::map<int32_t, int32_t> varps;
    std::map<int32_t, int32_t> quests; ///< Native QuestID keys; values are 0=in progress, 1=not started, 2=finished.
    std::map<Skill, int32_t> boostedLevels;
    std::map<Skill, int32_t> realLevels;
    int64_t nowMinutes = -1; ///< Unix minutes, matching the home-teleport varp.
};

namespace detail {

template <typename Map, typename Key>
inline int32_t valueOrUnknown(const Map& values, Key key) {
    const auto found = values.find(key);
    return found == values.end() ? -1 : found->second;
}

inline bool containsItem(std::span<const ItemContainerSlot> items, int32_t id, int32_t slot = -1) {
    for (const auto& item : items) {
        if (item.itemId == id && item.quantity > 0 && (slot < 0 || item.slot == slot)) return true;
    }
    return false;
}

struct SnapshotSpellState {
    const SpellCastState& state;
    int32_t varbit(int32_t id) const { return valueOrUnknown(state.varbits, id); }
    int32_t varp(int32_t id) const { return valueOrUnknown(state.varps, id); }
    int32_t quest(int32_t id) const { return valueOrUnknown(state.quests, id); }
    int32_t boosted(Skill skill) const { return valueOrUnknown(state.boostedLevels, skill); }
    int32_t real(Skill skill) const { return valueOrUnknown(state.realLevels, skill); }
    int64_t minutes() const { return state.nowMinutes; }
    bool inventory(int32_t id) const { return containsItem(state.inventory, id); }
    bool equipment(int32_t id, int32_t slot = -1) const { return containsItem(state.equipment, id, slot); }
};

struct LiveSpellState {
    int32_t varbit(int32_t id) const { return state::vars().varbit(id); }
    int32_t varp(int32_t id) const { return state::vars().varp(id); }
    int32_t quest(int32_t id) const {
        // The public script reader consumes DB row IDs, whereas the catalogue uses native quest IDs.
        const auto* quest = gamevals::QuestID::byQuestId(id);
        return quest ? state::script().questState(quest->rowId) : -1;
    }
    int32_t boosted(Skill skill) const { return state::skills().boosted(static_cast<int32_t>(skill)); }
    int32_t real(Skill skill) const { return state::skills().real(static_cast<int32_t>(skill)); }
    int64_t minutes() const {
        return std::chrono::duration_cast<std::chrono::minutes>(
            std::chrono::system_clock::now().time_since_epoch()).count();
    }
    bool inventory(int32_t id) const {
        const auto items = state::itemContainer(static_cast<int32_t>(InventoryID::INVENTORY));
        return items && containsItem(items->items, id);
    }
    bool equipment(int32_t id, int32_t slot = -1) const {
        const auto items = state::itemContainer(static_cast<int32_t>(InventoryID::EQUIPMENT));
        return items && containsItem(items->items, id, slot);
    }
};

template <typename State>
inline bool wielding(const State& state, std::initializer_list<int32_t> ids) {
    for (const auto id : ids) {
        if (state.equipment(id, static_cast<int32_t>(EquipmentSlot::WEAPON))) return true;
    }
    return false;
}

inline bool enoughRunes(const SpellRequirements& requirements, const RuneElement::RuneElementAmounts& amounts) {
    if (!requirements.known) return false;
    for (const auto& cost : requirements.runes) {
        if (cost.quantity == 0) continue;
        const auto found = amounts.find(cost.element);
        if (found == amounts.end() || found->second < cost.quantity) return false;
    }
    return true;
}

template <typename State>
inline bool hasSpellSack(const SpellRequirements& requirements, const State& state) {
    return requirements.known && requirements.spellSackId > 0
        && state.varbit(gamevals::VarbitID::BLIGHTED_ITEMS_ALLOWED) > 0
        && state.inventory(requirements.spellSackId);
}

template <typename State>
inline bool additionalSatisfied(SpellBook book, const SpellRequirements& requirements, const State& state) {
    using Q = gamevals::QuestID;
    using V = gamevals::VarbitID;
    using P = gamevals::VarPlayerID;
    using I = gamevals::ItemID;
    using A = AdditionalRequirement;
    if (!requirements.known) return false;
    if (book == SpellBook::Ancient && state.quest(Q::QUEST_DESERTTREASURE) != 2) return false;
    if (book == SpellBook::Lunar) {
        if (state.quest(Q::QUEST_LUNARDIPLOMACY) != 2) return false;
        if (state.real(Skill::MAGIC) < 65 || state.real(Skill::DEFENCE) < 40) return false;
    }
    if (requirements.questId && state.quest(requirements.questId) != 2) return false;
    if (requirements.unlockVarbit && state.varbit(requirements.unlockVarbit) <= 0) return false;
    if (requirements.cooldownVarbit && state.varbit(requirements.cooldownVarbit) != 0) return false;

    switch (requirements.additional) {
    case A::None: return true;
    case A::HomeTeleport: {
        const auto last = state.varp(::titan::VarPlayerID::LAST_HOME_TELEPORT);
        return last >= 0 && state.minutes() >= static_cast<int64_t>(last) + 30;
    }
    case A::House: return state.varbit(V::POH_HOUSE_LOCATION) > 0;
    case A::Ardougne: return state.varp(P::ELENAQUEST) >= 30;
    case A::Watchtower: return state.varp(P::ITWATCHTOWER) >= 14;
    case A::ApeAtoll: return state.inventory(I::BANANA);
    case A::IbanStaff:
        // shortcut: remaining staff charges are not checked, add when reliable charge data is available.
        return wielding(state,
            {I::IBANSTAFF, I::IBANSTAFF_UPGRADED, I::IBAN_STAFF_ORN, I::IBAN_STAFF_UPGRADED_ORN});
    case A::MagicDart:
        return state.boosted(Skill::SLAYER) >= 55 && wielding(state,
            {I::SLAYER_STAFF, I::SLAYER_STAFF_ENCHANTED, I::SOTD, I::TOXIC_SOTD,
             I::TOXIC_SOTD_CHARGED, I::STAFF_OF_LIGHT, I::STAFF_OF_BALANCE});
    case A::SaradominStaff:
        return state.quest(Q::MINIQUEST_MAGEARENA1) == 2
            && wielding(state, {I::SARADOMIN_STAFF, I::STAFF_OF_LIGHT});
    case A::GuthixStaff:
        return state.quest(Q::MINIQUEST_MAGEARENA1) == 2 && wielding(state,
            {I::GUTHIX_STAFF, I::STAFF_OF_BALANCE, I::PEST_VOID_KNIGHT_MACE, I::PEST_VOID_KNIGHT_MACE_TROUVER});
    case A::ZamorakStaff:
        return state.quest(Q::MINIQUEST_MAGEARENA1) == 2 && wielding(state,
            {I::ZAMORAK_STAFF, I::SOTD, I::TOXIC_SOTD, I::TOXIC_SOTD_CHARGED,
             I::WILD_CAVE_ACCURSED_UNCHARGED_RECOL, I::WILD_CAVE_ACCURSED_CHARGED_RECOL,
             I::WILD_CAVE_SCEPTRE_UNCHARGED_RECOL, I::WILD_CAVE_SCEPTRE_CHARGED_RECOL});
    case A::ThrallLesser:
    case A::ThrallSuperior:
    case A::ThrallGreater: {
        const auto prayer = requirements.additional == A::ThrallLesser ? 2
            : requirements.additional == A::ThrallSuperior ? 4 : 6;
        return state.boosted(Skill::PRAYER) >= prayer
            && (state.inventory(I::BOOK_OF_THE_DEAD)
                || state.equipment(I::BOOK_OF_THE_DEAD, static_cast<int32_t>(EquipmentSlot::SHIELD)));
    }
    case A::DeathCharge: return state.varbit(V::ARCEUUS_DEATH_CHARGE_ACTIVE) == 0;
    case A::VileVigour: return state.boosted(Skill::PRAYER) > 0;
    case A::EnergyTransfer:
        return state.varp(::titan::VarPlayerID::SPECIAL_ATTACK) >= 1000 && state.boosted(Skill::HITPOINTS) > 10;
    case A::HealOther:
        return state.real(Skill::HITPOINTS) > 0
            && static_cast<int64_t>(state.boosted(Skill::HITPOINTS)) * 100 >= static_cast<int64_t>(state.real(Skill::HITPOINTS)) * 11;
    }
    return false;
}

} // namespace detail

} // namespace titan::utils::Magic
