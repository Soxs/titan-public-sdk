/// @file titan/runescape_bank.h
/// @brief Named bank destinations and live eligibility checks ported from PaistiUtils RunescapeBank.

#pragma once

#include "client.h"
#include "generated/gamevals/item_id.h"
#include "generated/gamevals/quest_id.h"
#include "generated/gamevals/quest_id_table.h"
#include "generated/gamevals/var_player_id.h"
#include "generated/gamevals/varbit_id.h"
#include "query.h"
#include "utils/tools/pickaxe.h"

#include <array>
#include <optional>
#include <vector>

namespace titan {

/// Named destinations, not bank object IDs. Ordinals are not a persistence or ABI contract.
enum class RunescapeBank {
    ALDARIN_SOUTH,
    ALDARIN_CENTRAL,
    AL_KHARID,
    ARCEUUS,
    ARDOUGNE_NORTH,
    ARDOUGNE_SOUTH,
    NEMUS_RETREAT,
    AUBURNVALE_WEST_SIDE,
    AUBURNVALE_EAST_SIDE,
    BARBARIAN_OUTPOST,
    BLAST_FURNACE_BANK,
    BLAST_MINE,
    BURGH_DE_ROTT,
    CAMELOT,
    CANIFIS,
    CASTLE_WARS,
    CATHERBY,
    CORSAIR_COVE,
    CRAFTING_GUILD,
    DRAYNOR,
    DUEL_ARENA,
    DWARF_MINE_BANK,
    EDGEVILLE,
    FALADOR_EAST,
    FALADOR_WEST,
    FARMING_GUILD,
    FARMING_GUILD_NORTH,
    FEROX_ENCLAVE,
    FISHING_GUILD,
    FOSSIL_ISLAND,
    FOSSIL_ISLAND_WATER,
    GNOME_BANK,
    GNOME_TREE_BANK_SOUTH,
    GNOME_TREE_BANK_WEST,
    GRAND_EXCHANGE,
    GREAT_KOUREND_CASTLE,
    GIANTS_FOUNDRY,
    HOSIDIUS,
    HOSIDIUS_KITCHEN,
    HALLOWED_SEPULCHRE,
    JATIZSO,
    ISLE_OF_SOULS,
    LANDS_END,
    LOVAKENGJ,
    LUMBRIDGE_BASEMENT,
    LUMBRIDGE_TOP,
    LUNAR_ISLE,
    LLETYA,
    MAGE_TRAINING_ARENA,
    MOTHERLODE_MINE,
    MINING_GUILD,
    MISCELLANIA,
    MOUNT_KARUULM,
    MOUNT_QUIDAMORTEM,
    MAGE_ARENA_WILDERNESS,
    NARDAH,
    NEITIZNOT,
    THE_PANDEMONIUM,
    PEST_CONTROL,
    PISCARILIUS,
    PRIFDDINAS,
    ROGUES_DEN,
    RUINS_OF_UNKAH,
    SHANTY_PASS,
    SHAYZIEN_CHEST,
    SHAYZIEN_BANK,
    SHILO_VILLAGE,
    SOPHANEM,
    SULPHUR_MINE,
    TZHAAR,
    TAL_TEKLAN,
    VARROCK_EAST,
    VARROCK_WEST,
    VER_SINHAZA,
    VINERY_BANK,
    VOLCANO_BANK,
    WINTERTODT,
    WOODCUTTING_GUILD,
    WARRIORS_GUILD,
    YANILLE,
    ZANARIS,
    ZEAH_SAND_BANK,
    MYTHS_GUILD,
    FORTIS_COLOSSEUM,
    VARLAMORE_EAST_BANK,
    VARLAMORE_WEST_BANK,
    QUETZAL_GORGE_BANK,
    VARLAMORE_HUNTERS_GUILD_BANK,
    PORT_KHAZARD_BANK,
    ONYX_CREST_BANK,
    CHARRED_ISLAND_BANK,
    GREAT_CONCH_BANK,
    PORT_ROBERTS_BANK,
    RED_ROCK_BANK,
    WYRMSCRAIG_BANK,
    SUNBLEAK_ISLAND_BANK,
    BUCCANEERS_HAVEN_BANK,
    DEEPFIN_POINT,
    DEEPFIN_MINE_WEST_BANK,
    DEEPFIN_MINE_EAST_BANK,
    GUARDIANS_OF_THE_RIFT,
    INFERNO,
};

namespace RunescapeBankInfo {

struct BankDefinition {
    RunescapeBank bank;
    WorldPoint location;
};

/// Static top-level world destinations in source order.
inline constexpr std::array kBanks = {
    BankDefinition{RunescapeBank::ALDARIN_SOUTH, {1380, 2866, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::ALDARIN_CENTRAL, {1399, 2925, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::AL_KHARID, {3269, 3167, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::ARCEUUS, {1624, 3745, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::ARDOUGNE_NORTH, {2616, 3332, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::ARDOUGNE_SOUTH, {2655, 3283, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::NEMUS_RETREAT, {1386, 3309, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::AUBURNVALE_WEST_SIDE, {1413, 3353, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::AUBURNVALE_EAST_SIDE, {1419, 3353, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::BARBARIAN_OUTPOST, {2536, 3574, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::BLAST_FURNACE_BANK, {1948, 4957, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::BLAST_MINE, {1502, 3856, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::BURGH_DE_ROTT, {3495, 3212, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::CAMELOT, {2725, 3493, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::CANIFIS, {3512, 3480, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::CASTLE_WARS, {2443, 3083, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::CATHERBY, {2808, 3441, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::CORSAIR_COVE, {2569, 2864, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::CRAFTING_GUILD, {2936, 3281, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::DRAYNOR, {3092, 3243, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::DUEL_ARENA, {3381, 3268, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::DWARF_MINE_BANK, {2837, 10207, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::EDGEVILLE, {3094, 3492, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::FALADOR_EAST, {3013, 3355, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::FALADOR_WEST, {2946, 3368, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::FARMING_GUILD, {1253, 3741, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::FARMING_GUILD_NORTH, {1248, 3758, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::FEROX_ENCLAVE, {3130, 3631, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::FISHING_GUILD, {2586, 3420, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::FOSSIL_ISLAND, {3739, 3804, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::FOSSIL_ISLAND_WATER, {3766, 3899, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::GNOME_BANK, {2445, 3425, 1, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::GNOME_TREE_BANK_SOUTH, {2449, 3482, 1, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::GNOME_TREE_BANK_WEST, {2442, 3488, 1, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::GRAND_EXCHANGE, {3164, 3487, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::GREAT_KOUREND_CASTLE, {1610, 3681, 2, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::GIANTS_FOUNDRY, {3374, 11493, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::HOSIDIUS, {1749, 3599, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::HOSIDIUS_KITCHEN, {1676, 3617, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::HALLOWED_SEPULCHRE, {2396, 5985, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::JATIZSO, {2416, 3801, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::ISLE_OF_SOULS, {2212, 2859, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::LANDS_END, {1512, 3421, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::LOVAKENGJ, {1526, 3739, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::LUMBRIDGE_BASEMENT, {3218, 9623, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::LUMBRIDGE_TOP, {3208, 3220, 2, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::LUNAR_ISLE, {2099, 3919, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::LLETYA, {2352, 3164, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::MAGE_TRAINING_ARENA, {3365, 3318, 1, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::MOTHERLODE_MINE, {3760, 5666, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::MINING_GUILD, {3014, 9718, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::MISCELLANIA, {2618, 3895, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::MOUNT_KARUULM, {1324, 3824, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::MOUNT_QUIDAMORTEM, {1254, 3571, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::MAGE_ARENA_WILDERNESS, {2534, 4712, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::NARDAH, {3428, 2892, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::NEITIZNOT, {2337, 3807, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::THE_PANDEMONIUM, {3039, 2999, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::PEST_CONTROL, {2667, 2653, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::PISCARILIUS, {1803, 3790, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::PRIFDDINAS, {3257, 6106, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::ROGUES_DEN, {3043, 4972, 1, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::RUINS_OF_UNKAH, {3156, 2835, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::SHANTY_PASS, {3308, 3120, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::SHAYZIEN_CHEST, {1486, 3646, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::SHAYZIEN_BANK, {1488, 3592, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::SHILO_VILLAGE, {2852, 2954, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::SOPHANEM, {2799, 5169, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::SULPHUR_MINE, {1453, 3858, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::TZHAAR, {2446, 5178, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::TAL_TEKLAN, {1243, 3121, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::VARROCK_EAST, {3253, 3420, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::VARROCK_WEST, {3185, 3441, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::VER_SINHAZA, {3652, 3208, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::VINERY_BANK, {1809, 3565, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::VOLCANO_BANK, {3819, 3809, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::WINTERTODT, {1640, 3944, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::WOODCUTTING_GUILD, {1591, 3479, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::WARRIORS_GUILD, {2843, 3543, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::YANILLE, {2613, 3093, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::ZANARIS, {2383, 4458, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::ZEAH_SAND_BANK, {1719, 3465, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::MYTHS_GUILD, {2465, 2848, 1, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::FORTIS_COLOSSEUM, {1804, 9501, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::VARLAMORE_EAST_BANK, {1780, 3096, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::VARLAMORE_WEST_BANK, {1647, 3117, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::QUETZAL_GORGE_BANK, {1519, 3228, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::VARLAMORE_HUNTERS_GUILD_BANK, {1542, 3040, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::PORT_KHAZARD_BANK, {2661, 3162, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::ONYX_CREST_BANK, {2466, 5232, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::CHARRED_ISLAND_BANK, {2654, 2387, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::GREAT_CONCH_BANK, {3182, 2419, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::PORT_ROBERTS_BANK, {1883, 3315, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::RED_ROCK_BANK, {2808, 2521, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::WYRMSCRAIG_BANK, {2587, 2259, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::SUNBLEAK_ISLAND_BANK, {2195, 2314, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::BUCCANEERS_HAVEN_BANK, {2097, 3686, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::DEEPFIN_POINT, {1936, 2755, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::DEEPFIN_MINE_WEST_BANK, {2011, 9186, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::DEEPFIN_MINE_EAST_BANK, {2094, 9197, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::GUARDIANS_OF_THE_RIFT, {3618, 9473, 0, WorldView::TOP_LEVEL}},
    BankDefinition{RunescapeBank::INFERNO, {2542, 5141, 0, WorldView::TOP_LEVEL}},
};

/// Pure coordinate lookup; invalid enum values return nullopt.
inline constexpr std::optional<WorldPoint> getLocation(RunescapeBank bank) {
    for (const auto& entry : kBanks) {
        if (entry.bank == bank) return entry.location;
    }
    return std::nullopt;
}

namespace detail {

inline int32_t questState(int32_t nativeQuestId) {
    // The script API takes the quest's DB row ID, not the generated native quest ID.
    const auto* quest = gamevals::QuestID::byQuestId(nativeQuestId);
    return quest ? state::script().questState(quest->rowId) : -1;
}

inline bool questFinished(int32_t nativeQuestId) {
    return questState(nativeQuestId) == 2;
}

inline bool questStarted(int32_t nativeQuestId) {
    const auto status = questState(nativeQuestId);
    return status == 0 || status == 2;
}

inline bool membersWorld() {
    const auto current = state::world::current();
    if (!current) return false;
    for (const auto& world : state::world::list()) {
        if (world.id == *current) return world.isMembers();
    }
    return false;
}

}  // namespace detail

/// Evaluates the source catalog's predicates against current SDK state, without caching.
/// Call with world-ready state. This is not a reachability or comprehensive access check:
/// entries without a source predicate return true, even without a backend; invalid values return false.
/// Missing quest/world/item/player data cannot satisfy the corresponding requirement.
inline bool canUse(RunescapeBank bank) {
    using Q = gamevals::QuestID;
    using V = gamevals::VarbitID;
    using P = gamevals::VarPlayerID;
    switch (bank) {
    case RunescapeBank::ALDARIN_SOUTH:
    case RunescapeBank::ALDARIN_CENTRAL:
    case RunescapeBank::NEMUS_RETREAT:
    case RunescapeBank::AUBURNVALE_WEST_SIDE:
    case RunescapeBank::AUBURNVALE_EAST_SIDE:
    case RunescapeBank::TAL_TEKLAN:
        return detail::questFinished(Q::QUEST_CHILDRENOFTHESUN) && detail::membersWorld()
            && state::vars().varbit(V::VMQ2_FIRST_TRAVEL) >= 3;
    // The bank stands inside the Hunter Guild, which needs 46 Hunter to enter.
    case RunescapeBank::VARLAMORE_HUNTERS_GUILD_BANK:
        return detail::questFinished(Q::QUEST_CHILDRENOFTHESUN) && detail::membersWorld()
            && state::vars().varbit(V::VMQ2_FIRST_TRAVEL) >= 3 && state::skills().real(Skill::HUNTER) >= 46;
    case RunescapeBank::ARCEUUS:
    case RunescapeBank::GREAT_KOUREND_CASTLE:
    case RunescapeBank::HOSIDIUS:
    case RunescapeBank::LANDS_END:
    case RunescapeBank::LOVAKENGJ:
    case RunescapeBank::MOUNT_QUIDAMORTEM:
    case RunescapeBank::VINERY_BANK:
    case RunescapeBank::WINTERTODT:
    case RunescapeBank::ZEAH_SAND_BANK:
        return state::vars().varbit(V::ZEAH_PLAYERHASVISITED) > 0
            && detail::questFinished(Q::QUEST_XMARKSTHESPOT);
    case RunescapeBank::BLAST_FURNACE_BANK:
        return detail::questStarted(Q::QUEST_GIANTDWARF);
    case RunescapeBank::BLAST_MINE:
        return state::skills().real(Skill::MINING) >= 43;
    case RunescapeBank::BURGH_DE_ROTT:
        return state::vars().varbit(V::MYREQUE_2_QUEST) >= 200;
    case RunescapeBank::CORSAIR_COVE:
        return detail::questFinished(Q::QUEST_CORSAIRCURSE);
    case RunescapeBank::CRAFTING_GUILD:
        return state::skills().real(Skill::CRAFTING) >= 99
            || state::vars().varbit(V::FALADOR_DIARY_HARD_COMPLETE) > 0
            || state::vars().varbit(V::FALADOR_DIARY_ELITE_COMPLETE) > 0;
    case RunescapeBank::FARMING_GUILD:
        return state::skills().real(Skill::FARMING) >= 45;
    case RunescapeBank::FARMING_GUILD_NORTH:
        return state::skills().real(Skill::FARMING) >= 85;
    case RunescapeBank::FISHING_GUILD:
        return state::skills().real(Skill::FISHING) >= 68;
    case RunescapeBank::FOSSIL_ISLAND:
        return detail::questFinished(Q::QUEST_BONEVOYAGE) && state::vars().varbit(V::FOSSIL_BANK) >= 1;
    case RunescapeBank::FOSSIL_ISLAND_WATER:
        return detail::questFinished(Q::QUEST_BONEVOYAGE);
    case RunescapeBank::GIANTS_FOUNDRY:
        return detail::questFinished(Q::QUEST_SLEEPINGGIANTS);
    case RunescapeBank::HALLOWED_SEPULCHRE:
        return detail::questFinished(Q::QUEST_SINSOFTHEFATHER);
    case RunescapeBank::JATIZSO:
    case RunescapeBank::NEITIZNOT:
        return detail::questStarted(Q::QUEST_FREMENNIKISLES);
    case RunescapeBank::LUMBRIDGE_BASEMENT:
        return detail::questFinished(Q::SUBQUEST_RFD_INTRO) && detail::membersWorld();
    case RunescapeBank::LUNAR_ISLE:
        return queries::inventory().id(gamevals::ItemID::LUNAR_SEAL_OF_PASSAGE).minQuantity(1).exists()
            || detail::questFinished(Q::QUEST_DREAMMENTOR);
    case RunescapeBank::LLETYA:
        return state::vars().varp(P::MOURNING_QUEST) >= 3;
    case RunescapeBank::MOTHERLODE_MINE: {
        if (state::skills().real(Skill::MINING) < 30) return false;
        const auto player = state::client().localPlayer();
        if (player && player->worldPoint().distanceTo({3756, 5666, 0, WorldView::TOP_LEVEL}) <= 9)
            return true;
        return utils::Pickaxe::hasUsablePickaxe();
    }
    case RunescapeBank::MINING_GUILD:
        return state::skills().real(Skill::MINING) >= 60;
    case RunescapeBank::MISCELLANIA:
        return detail::questFinished(Q::QUEST_THRONEOFMISCELLANIA);
    case RunescapeBank::THE_PANDEMONIUM:
        return state::vars().varbit(V::SAILING_INTRO) >= 16;
    case RunescapeBank::PRIFDDINAS:
        return detail::questFinished(Q::QUEST_SONGOFTHEELVES);
    case RunescapeBank::SHILO_VILLAGE:
        return detail::questFinished(Q::QUEST_SHILOVILLAGE);
    case RunescapeBank::SOPHANEM:
        return detail::questFinished(Q::QUEST_CONTACT);
    case RunescapeBank::VER_SINHAZA:
        return detail::questFinished(Q::QUEST_DARKNESSOFHALLOWVALE);
    case RunescapeBank::WOODCUTTING_GUILD:
        return state::skills().real(Skill::WOODCUTTING) >= 60;
    case RunescapeBank::WARRIORS_GUILD: {
        const auto attack = state::skills().real(Skill::ATTACK);
        const auto strength = state::skills().real(Skill::STRENGTH);
        return attack + strength >= 130 || strength == 99 || attack == 99;
    }
    case RunescapeBank::ZANARIS:
        return detail::questFinished(Q::QUEST_LOSTCITY);
    case RunescapeBank::MYTHS_GUILD:
        return detail::questFinished(Q::QUEST_DRAGONSLAYER2);
    case RunescapeBank::FORTIS_COLOSSEUM:
        return state::vars().varp(P::COLOSSEUM_GLORY) >= 2000;
    case RunescapeBank::VARLAMORE_EAST_BANK:
    case RunescapeBank::VARLAMORE_WEST_BANK:
    case RunescapeBank::QUETZAL_GORGE_BANK:
        return detail::questFinished(Q::QUEST_CHILDRENOFTHESUN);
    case RunescapeBank::ONYX_CREST_BANK: {
        if (state::vars().varbit(V::AMENITY_BANKCHEST_ONYX_CREST) <= 0) return false;
        if (state::vars().varbit(V::SAILING_BOARDED_BOAT) > 0) return true;
        const auto player = state::client().localPlayer();
        const auto location = player ? player->fromLocalInstance() : std::nullopt;
        return location && location->regionId() == 11811;
    }
    case RunescapeBank::CHARRED_ISLAND_BANK:
        return state::vars().varbit(V::AMENITY_BANKCHEST_CHARRED_ISLAND) > 0;
    case RunescapeBank::GREAT_CONCH_BANK:
        // The Great Conch becomes reachable once the bandage has been given to Floopa in Troubled Tortugans.
        return state::skills().real(Skill::SAILING) >= 45 && state::vars().varbit(V::TT) >= 8;
    case RunescapeBank::PORT_ROBERTS_BANK:
        return state::skills().real(Skill::SAILING) >= 50;
    case RunescapeBank::RED_ROCK_BANK:
        // Red Rock becomes reachable at the stage of The Red Reef that sends the player there.
        return state::skills().real(Skill::SAILING) >= 52 && state::vars().varbit(V::TRR) >= 10;
    case RunescapeBank::WYRMSCRAIG_BANK:
        return state::vars().varbit(V::AMENITY_BANKCHEST_WYRMSCRAIG) > 0;
    case RunescapeBank::SUNBLEAK_ISLAND_BANK:
        return state::vars().varbit(V::AMENITY_BANKCHEST_SUNBLEAK) > 0;
    case RunescapeBank::BUCCANEERS_HAVEN_BANK:
        return state::vars().varbit(V::AMENITY_BANKCHEST_BUCCANEERS) > 0;
    case RunescapeBank::DEEPFIN_POINT:
        return state::skills().real(Skill::SAILING) >= 67;
    case RunescapeBank::DEEPFIN_MINE_WEST_BANK:
        return state::vars().varbit(V::AMENITY_BANKCHEST_DEEPFIN_1) > 0;
    case RunescapeBank::DEEPFIN_MINE_EAST_BANK:
        return state::vars().varbit(V::AMENITY_BANKCHEST_DEEPFIN_2) > 0;
    case RunescapeBank::GUARDIANS_OF_THE_RIFT:
        return detail::questFinished(Q::QUEST_TEMPLEOFTHEEYE);
    case RunescapeBank::INFERNO:
        return state::vars().varbit(V::INFERNO_SACRIFICED_FIRECAPE) > 0;
    default:
        return getLocation(bank).has_value();
    }
}

/// Owned enum values in catalog order; the additional filter runs only for eligible entries.
template <typename Predicate>
inline std::vector<RunescapeBank> getUsableBanks(Predicate additionalFilter) {
    std::vector<RunescapeBank> result;
    for (const auto& entry : kBanks) {
        if (canUse(entry.bank) && additionalFilter(entry.bank)) result.push_back(entry.bank);
    }
    return result;
}

inline std::vector<RunescapeBank> getUsableBanks() {
    return getUsableBanks([](RunescapeBank) { return true; });
}

}  // namespace RunescapeBankInfo
}  // namespace titan
