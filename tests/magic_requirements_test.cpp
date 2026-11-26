#include <titan/utils/magic.h>
#include <titan/detail/backend_external.h>
#include "check.h"

using titan::Skill;
using titan::EquipmentSlot;
using titan::InventoryID;
namespace Varbits = titan::Varbits;
namespace VarPlayerID = titan::VarPlayerID;
using titan::RunePouchContents;
namespace utils = titan::utils;
using namespace titan::utils::Magic;
using I = titan::gamevals::ItemID;
using V = titan::gamevals::VarbitID;
using P = titan::gamevals::VarPlayerID;
using Q = titan::gamevals::QuestID;
using E = titan::RuneElement;

namespace {
SpellCastState state;
bool containersAvailable = true;
int32_t lastQuestRow = -1;
int32_t boltDialogClicks = 0;
bool widgetDispatchAccepted = true;

void refreshRunes() {
    state.runeAmounts = utils::RuneElement::getCarriedRuneElementAmounts(
        state.inventory, state.equipment, state.membersWorld.value_or(false));
}

void reset(SpellBook book = SpellBook::Standard) {
    state = {};
    state.spellBook = book;
    state.membersWorld = true;
    state.boostedLevels[Skill::MAGIC] = 99;
    state.realLevels[Skill::MAGIC] = 99;
    state.realLevels[Skill::DEFENCE] = 40;
    state.quests[Q::QUEST_DESERTTREASURE] = 2;
    state.quests[Q::QUEST_LUNARDIPLOMACY] = 2;
    state.quests[Q::QUEST_KINGDOMDIVIDED] = 2;
    refreshRunes();
}
} // namespace

int main() {
    reset();
    CHECK(!canCast(Standard::VARROCK_TELEPORT));
    CHECK(!haveRunesForSpell(Standard::VARROCK_TELEPORT, state));
    CHECK(additionalRequirementsSatisfied(Standard::VARROCK_TELEPORT, state));
    state.inventory = {{0, I::AIRRUNE, 3}, {1, I::FIRERUNE, 1}, {2, I::LAWRUNE, 1}};
    refreshRunes();
    state.boostedLevels[Skill::MAGIC] = 25;
    state.realLevels[Skill::MAGIC] = 20;
    state.membersWorld = false;
    CHECK(canCast(Standard::VARROCK_TELEPORT, state)); // A current boost is sufficient for Standard spells.
    state.boostedLevels[Skill::MAGIC] = 24;
    CHECK(!canCast(Standard::VARROCK_TELEPORT, state));
    state.boostedLevels[Skill::MAGIC] = 25;
    state.inventory[0].quantity = 2;
    refreshRunes();
    CHECK(!canCast(Standard::VARROCK_TELEPORT, state));

    state.membersWorld = true;
    state.inventory = {{0, I::SMOKERUNE, 3}, {1, I::LAWRUNE, 1}};
    refreshRunes();
    CHECK(canCast(Standard::VARROCK_TELEPORT, state));
    state.inventory = {{0, I::STAFF_OF_AIR, 1}, {1, I::FIRERUNE, 1}, {2, I::LAWRUNE, 1}};
    refreshRunes();
    CHECK(!haveRunesForSpell(Standard::VARROCK_TELEPORT, state));
    state.equipment = {{static_cast<int>(EquipmentSlot::WEAPON), I::STAFF_OF_AIR, 1}};
    refreshRunes();
    CHECK(canCast(Standard::VARROCK_TELEPORT, state));
    state.spellBook = SpellBook::Ancient;
    CHECK(!canCast(Standard::VARROCK_TELEPORT, state));

    reset();
    state.inventory = {{0, I::BH_RUNE_POUCH, 1}};
    const RunePouchContents pouch{{0, I::AIRRUNE, 3}, {1, I::FIRERUNE, 1}, {2, I::LAWRUNE, 1}};
    state.runeAmounts = utils::RuneElement::getCarriedRuneElementAmounts(state.inventory, {}, pouch, true);
    CHECK(canCast(Standard::VARROCK_TELEPORT, state));
    state.inventory.clear();
    state.runeAmounts = utils::RuneElement::getCarriedRuneElementAmounts(state.inventory, {}, pouch, true);
    CHECK(!canCast(Standard::VARROCK_TELEPORT, state));

    reset();
    state.inventory = {{0, I::EARTHRUNE, 2}, {1, I::LAWRUNE, 2}};
    refreshRunes();
    state.varps[P::ITWATCHTOWER] = 13;
    CHECK(haveRunesForSpell(Standard::WATCHTOWER_TELEPORT, state));
    CHECK(!additionalRequirementsSatisfied(Standard::WATCHTOWER_TELEPORT, state));
    state.varps[P::ITWATCHTOWER] = 14;
    CHECK(canCast(Standard::WATCHTOWER_TELEPORT, state));
    state.membersWorld = false;
    CHECK(!canCast(Standard::WATCHTOWER_TELEPORT, state));
    state.membersWorld.reset();
    CHECK(!canCast(Standard::WATCHTOWER_TELEPORT, state));

    reset(SpellBook::Lunar);
    state.inventory = {{0, I::EARTHRUNE, 6}, {1, I::ASTRALRUNE, 2}, {2, I::LAWRUNE, 1}};
    refreshRunes();
    CHECK(!canCast(Lunar::OURANIA_TELEPORT, state));
    state.varbits[V::RC_ZMI_LUNAR_SPELL] = 1;
    CHECK(canCast(Lunar::OURANIA_TELEPORT, state));
    state.realLevels[Skill::DEFENCE] = 39;
    CHECK(!canCast(Lunar::OURANIA_TELEPORT, state));
    state.realLevels[Skill::DEFENCE] = 40;
    state.quests[Q::QUEST_LUNARDIPLOMACY] = -1;
    CHECK(!canCast(Lunar::OURANIA_TELEPORT, state));

    reset(SpellBook::Ancient);
    state.inventory = {{0, I::BLIGHTED_SACK_ICEBARRAGE, 1}};
    CHECK(!haveRunesForSpell(Ancient::ICE_BARRAGE, state));
    state.varbits[V::BLIGHTED_ITEMS_ALLOWED] = 1;
    CHECK(canCast(Ancient::ICE_BARRAGE, state));
    CHECK(!haveRunesForSpell(Ancient::BLOOD_BARRAGE, state));
    state.inventory[0].quantity = 0;
    CHECK(!canCast(Ancient::ICE_BARRAGE, state));

    reset();
    state.varbits[V::BLIGHTED_ITEMS_ALLOWED] = 1;
    state.inventory = {{0, I::BLIGHTED_SACK_SURGE, 1}};
    CHECK(canCast(Standard::FIRE_WAVE, state));
    CHECK(canCast(Standard::FIRE_SURGE, state));
    state.inventory = {{0, I::BLIGHTED_SACK_ENTANGLE, 1}};
    CHECK(canCast(Standard::BIND, state));
    CHECK(canCast(Standard::SNARE, state));
    CHECK(canCast(Standard::ENTANGLE, state));
    CHECK(!canCast(Standard::FIRE_WAVE, state));

    reset();
    state.varps[VarPlayerID::LAST_HOME_TELEPORT] = 100;
    state.nowMinutes = 129;
    CHECK(haveRunesForSpell(Standard::HOME_TELEPORT, state));
    CHECK(!canCast(Standard::HOME_TELEPORT, state));
    state.nowMinutes = 130;
    CHECK(canCast(Standard::HOME_TELEPORT, state));
    state.varps.clear();
    CHECK(!canCast(Standard::HOME_TELEPORT, state));

    reset(SpellBook::Necromancy);
    state.inventory = {{0, I::BOOK_OF_THE_DEAD, 1}, {1, I::FIRERUNE, 10}, {2, I::BLOODRUNE, 5}, {3, I::COSMICRUNE, 1}};
    refreshRunes();
    state.varbits[V::ARCEUUS_RESURRECTION_COOLDOWN] = 0;
    state.boostedLevels[Skill::PRAYER] = 5;
    CHECK(!canCast(Necromancy::RESURRECT_GREATER_GHOST, state));
    state.boostedLevels[Skill::PRAYER] = 6;
    CHECK(canCast(Necromancy::RESURRECT_GREATER_GHOST, state));
    state.inventory[0].quantity = 0;
    CHECK(!canCast(Necromancy::RESURRECT_GREATER_GHOST, state));
    state.equipment = {{static_cast<int>(EquipmentSlot::SHIELD), I::BOOK_OF_THE_DEAD, 1}};
    CHECK(canCast(Necromancy::RESURRECT_GREATER_GHOST, state));
    state.varbits[V::ARCEUUS_RESURRECTION_COOLDOWN] = 1;
    CHECK(!canCast(Necromancy::RESURRECT_GREATER_GHOST, state));

    reset();
    state.quests[Q::SUBQUEST_RFD_MONKEY] = 2;
    CHECK(!additionalRequirementsSatisfied(Standard::TELEPORT_TO_APE_ATOLL, state));
    state.inventory = {{0, I::BANANA, 1}};
    CHECK(additionalRequirementsSatisfied(Standard::TELEPORT_TO_APE_ATOLL, state));
    state.inventory = {{0, I::CERT_BANANA, 1}};
    CHECK(!additionalRequirementsSatisfied(Standard::TELEPORT_TO_APE_ATOLL, state));
    state.quests[Q::QUEST_UNDERGROUNDPASS] = 2;
    CHECK(!additionalRequirementsSatisfied(Standard::IBAN_BLAST, state));
    state.equipment = {{static_cast<int>(EquipmentSlot::WEAPON), I::IBANSTAFF, 1}};
    CHECK(additionalRequirementsSatisfied(Standard::IBAN_BLAST, state));
    state.equipment.clear();
    CHECK(!additionalRequirementsSatisfied(Standard::IBAN_BLAST, state));

    for (const auto spell : {static_cast<Standard>(-1), static_cast<Standard>(10000)}) {
        CHECK(!requirements(spell).known);
        CHECK(!haveRunesForSpell(spell, state));
        CHECK(!additionalRequirementsSatisfied(spell, state));
        CHECK(!canCast(spell, state));
    }
    reset();
    state.inventory = {{0, I::AIRRUNE, 2}, {1, I::COSMICRUNE, 1}};
    refreshRunes();
    CHECK(canCast(BoltEnchantment::OPAL, state));
    CHECK(!haveRunesForSpell(BoltEnchantment::EMERALD, state));

    // Exercise live SDK reads, including native quest ID -> script DB row conversion.
    TitanPluginSdk::HostApi host{};
    host.widgetInteract = [](uint32_t opcode, int32_t identifier, int32_t param0, int32_t param1) -> uint8_t {
        CHECK(opcode == static_cast<uint32_t>(titan::MenuAction::Id::CcOp));
        CHECK(identifier == 1);
        CHECK(param0 == -1);
        CHECK(param1 == titan::gamevals::InterfaceID::MagicSpellbook::XBOWS_ENCHANT);
        ++boltDialogClicks;
        return widgetDispatchAccepted;
    };
    host.getCurrentWorld = [](int32_t* out) -> uint8_t { *out = 301; return 1; };
    host.getWorldList = [](TitanPluginSdk::WorldState* out, uint32_t cap) -> uint32_t {
        if (!cap) return 0;
        out[0].id = 301;
        out[0].flags = state.membersWorld.value_or(false) ? 1u : 0u;
        return 1;
    };
    host.getVarbit = [](int32_t id) -> int32_t {
        return id == Varbits::SPELLBOOK ? static_cast<int32_t>(state.spellBook.value_or(static_cast<SpellBook>(-1)))
            : utils::Magic::detail::valueOrUnknown(state.varbits, id);
    };
    host.getVarp = [](int32_t id) -> int32_t { return utils::Magic::detail::valueOrUnknown(state.varps, id); };
    host.getBoostedSkillLevel = [](int32_t id) -> int32_t { return utils::Magic::detail::valueOrUnknown(state.boostedLevels, static_cast<Skill>(id)); };
    host.getRealSkillLevel = [](int32_t id) -> int32_t { return utils::Magic::detail::valueOrUnknown(state.realLevels, static_cast<Skill>(id)); };
    host.getQuestState = [](int32_t row) -> int32_t {
        lastQuestRow = row;
        const auto* quest = Q::byRowId(row);
        return quest ? utils::Magic::detail::valueOrUnknown(state.quests, quest->questId) : -1;
    };
    host.getItemContainer = [](int32_t id, TitanPluginSdk::ItemContainerState* out) -> uint8_t {
        if (!containersAvailable) return 0;
        const auto& items = id == static_cast<int32_t>(InventoryID::INVENTORY) ? state.inventory : state.equipment;
        out->writtenCount = static_cast<int32_t>(items.size());
        out->capacity = 28;
        for (int i = 0; i < out->writtenCount; ++i) {
            out->slots[i] = items[i].slot;
            out->itemIds[i] = items[i].itemId;
            out->quantities[i] = items[i].quantity;
        }
        return 1;
    };
    titan::detail::ExternalBackend backend{&host};
    auto* previous = titan::detail::backendRef();
    titan::detail::backendRef() = &backend;
    reset();
    // Opening the dialog does not require the selected variant's runes or perform its final selection.
    CHECK(!canCast(BoltEnchantment::OPAL));
    for (int i = 0; i < static_cast<int>(BoltEnchantment::COUNT); ++i) {
        CHECK(cast(static_cast<BoltEnchantment>(i)));
    }
    CHECK(boltDialogClicks == static_cast<int>(BoltEnchantment::COUNT));
    CHECK(!cast(BoltEnchantment::COUNT));
    CHECK(boltDialogClicks == static_cast<int>(BoltEnchantment::COUNT));
    widgetDispatchAccepted = false;
    CHECK(!cast(BoltEnchantment::OPAL));
    widgetDispatchAccepted = true;
    state.inventory = {{0, I::FIRERUNE, 2}, {1, I::LAWRUNE, 2}};
    state.quests[Q::QUEST_EADGARSRUSE] = 2;
    CHECK(canCast(Standard::TROLLHEIM_TELEPORT));
    CHECK(lastQuestRow == Q::byQuestId(Q::QUEST_EADGARSRUSE)->rowId);
    CHECK(lastQuestRow != Q::QUEST_EADGARSRUSE);
    state.quests[Q::QUEST_EADGARSRUSE] = -1;
    CHECK(!canCast(Standard::TROLLHEIM_TELEPORT));
    state.quests[Q::QUEST_EADGARSRUSE] = 2;
    containersAvailable = false;
    CHECK(!canCast(Standard::TROLLHEIM_TELEPORT));
    containersAvailable = true;
    state.membersWorld = false;
    CHECK(!canCast(Standard::TROLLHEIM_TELEPORT));

    // Unverified charge/timer/focus state must not reject otherwise eligible casts in either overload.
    reset();
    state.inventory = {{0, I::AIRRUNE, 3}, {1, I::FIRERUNE, 5}, {2, I::DEATHRUNE, 1},
        {3, I::BLOODRUNE, 3}, {4, I::EARTHRUNE, 2}, {5, I::WATERRUNE, 2}, {6, I::LAWRUNE, 2}};
    state.equipment = {{static_cast<int>(EquipmentSlot::WEAPON), I::IBANSTAFF, 1}};
    refreshRunes();
    for (const auto spell : {Standard::IBAN_BLAST, Standard::CHARGE, Standard::TELEPORT_TO_BOAT}) {
        const auto quest = requirements(spell).questId;
        state.quests[quest] = 2;
        CHECK(canCast(spell, state));
        CHECK(canCast(spell));
        state.quests[quest] = 1;
        CHECK(!canCast(spell, state));
        CHECK(!canCast(spell));
        state.quests[quest] = 2;
        state.boostedLevels[Skill::MAGIC] = 0;
        CHECK(!canCast(spell, state));
        CHECK(!canCast(spell));
        state.boostedLevels[Skill::MAGIC] = 99;
    }
    state.equipment.clear();
    CHECK(!canCast(Standard::IBAN_BLAST, state));
    CHECK(!canCast(Standard::IBAN_BLAST));
    titan::detail::backendRef() = previous;
}
