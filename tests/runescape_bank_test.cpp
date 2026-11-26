#include <titan/runescape_bank.h>
#include <titan/detail/backend_external.h>

#include "check.h"
#include <cstring>
#include <map>
#include <set>

using titan::RunescapeBank;
using titan::Skill;
using titan::gamevals::VarbitID;
using titan::gamevals::VarPlayerID;
using namespace titan::RunescapeBankInfo;

namespace {

std::map<int32_t, int32_t> quests;
std::map<int32_t, int32_t> varbits;
std::map<int32_t, int32_t> varps;
std::map<int32_t, int32_t> skills;
std::vector<TitanPluginSdk::InventoryItemState> inventory;
std::vector<titan::ItemContainerSlot> equipment;
TitanPluginSdk::PlayerState player;
bool playerAvailable = false;
bool worldAvailable = true;
bool membersWorld = true;
bool instanceLocationAvailable = true;
std::optional<titan::WorldPoint> instanceLocation;

void reset() {
    quests.clear();
    varbits.clear();
    varps.clear();
    skills.clear();
    inventory.clear();
    equipment.clear();
    player = {};
    player.worldViewId = titan::WorldView::TOP_LEVEL;
    playerAvailable = false;
    worldAvailable = true;
    membersWorld = true;
    instanceLocationAvailable = true;
    instanceLocation.reset();
}

}  // namespace

int main() {
    using B = RunescapeBank;
    static_assert(kBanks.size() == 102);
    static_assert(getLocation(B::GRAND_EXCHANGE)->x == 3164);
    static_assert(getLocation(B::GREAT_KOUREND_CASTLE)->z == 2);
    static_assert(!getLocation(static_cast<B>(-1)));
    std::set<B> uniqueBanks;
    for (const auto& entry : kBanks) {
        CHECK(uniqueBanks.insert(entry.bank).second);
        CHECK(getLocation(entry.bank) == entry.location);
        CHECK(entry.location.worldViewId == titan::WorldView::TOP_LEVEL);
    }
    CHECK(!canUse(static_cast<B>(-1)));
    CHECK(!canUse(static_cast<B>(999)));
    CHECK(canUse(B::GRAND_EXCHANGE));
    CHECK(!canUse(B::BLAST_FURNACE_BANK));
    CHECK(!canUse(B::JATIZSO));

    TitanPluginSdk::HostApi host{};
    host.getQuestState = [](int32_t rowId) {
        const auto found = quests.find(rowId);
        return found == quests.end() ? -1 : found->second;
    };
    host.getVarbit = [](int32_t id) { return varbits[id]; };
    host.getVarp = [](int32_t id) { return varps[id]; };
    host.getRealSkillLevel = [](int32_t id) { return skills[id]; };
    host.getBoostedSkillLevel = [](int32_t) { return 99; };
    host.getCurrentWorld = [](int32_t* out) -> uint8_t { *out = 301; return worldAvailable; };
    host.getWorldList = [](TitanPluginSdk::WorldState* out, uint32_t cap) -> uint32_t {
        if (!cap || !worldAvailable) return 0;
        out[0].id = 301;
        out[0].flags = membersWorld ? 1 : 0;
        return 1;
    };
    host.getLocalPlayer = [](TitanPluginSdk::PlayerState* out) -> uint8_t {
        *out = player;
        return playerAvailable;
    };
    host.worldPointFromLocalInstance = [](const TitanPluginSdk::WorldPointState* in,
                                         TitanPluginSdk::WorldPointState* out) -> uint8_t {
        if (!instanceLocationAvailable) return 0;
        *out = instanceLocation
            ? TitanPluginSdk::WorldPointState{instanceLocation->x, instanceLocation->y,
                                             instanceLocation->z, instanceLocation->worldViewId}
            : *in;
        return 1;
    };
    host.getInventoryItems = [](TitanPluginSdk::InventoryItemState* out, uint32_t cap) -> uint32_t {
        CHECK(inventory.size() <= cap);
        std::copy(inventory.begin(), inventory.end(), out);
        return static_cast<uint32_t>(inventory.size());
    };
    host.getItemContainer = [](int32_t id, TitanPluginSdk::ItemContainerState* out) -> uint8_t {
        if (id == static_cast<int32_t>(titan::InventoryID::INVENTORY)) {
            out->writtenCount = static_cast<int32_t>(inventory.size());
            for (int32_t i = 0; i < out->writtenCount; ++i) {
                out->slots[i] = i;
                out->itemIds[i] = inventory[i].itemId;
                out->quantities[i] = inventory[i].quantity;
            }
            return 1;
        }
        CHECK(id == static_cast<int32_t>(titan::InventoryID::EQUIPMENT));
        out->writtenCount = static_cast<int32_t>(equipment.size());
        for (int32_t i = 0; i < out->writtenCount; ++i) {
            out->slots[i] = equipment[i].slot;
            out->itemIds[i] = equipment[i].itemId;
            out->quantities[i] = equipment[i].quantity;
        }
        return 1;
    };
    titan::detail::ExternalBackend backend{&host};
    auto* previousBackend = titan::detail::backendRef();
    titan::detail::backendRef() = &backend;

    // These are script DB row IDs from RuneLite's quest catalog, not native QuestID ordinals.
    const std::pair<B, int32_t> finishedQuests[] = {
        {B::CORSAIR_COVE, 18}, {B::FOSSIL_ISLAND_WATER, 11}, {B::GIANTS_FOUNDRY, 169},
        {B::HALLOWED_SEPULCHRE, 134}, {B::MISCELLANIA, 147}, {B::SHILO_VILLAGE, 133},
        {B::SOPHANEM, 16}, {B::VER_SINHAZA, 22}, {B::ZANARIS, 86}, {B::MYTHS_GUILD, 32},
        {B::PRIFDDINAS, 137},
        {B::GUARDIANS_OF_THE_RIFT, 167}, {B::VARLAMORE_EAST_BANK, 3450},
        {B::VARLAMORE_WEST_BANK, 3450}, {B::QUETZAL_GORGE_BANK, 3450},
    };
    for (const auto& [bank, rowId] : finishedQuests) {
        reset();
        for (int status : {-1, 1, 0, 2}) {
            quests[rowId] = status;
            CHECK(canUse(bank) == (status == 2));
        }
    }
    for (const auto& [bank, rowId] : {std::pair{B::BLAST_FURNACE_BANK, 63},
                                    std::pair{B::JATIZSO, 56}, std::pair{B::NEITIZNOT, 56}}) {
        reset();
        for (int status : {-1, 1, 0, 2, 3}) {
            quests[rowId] = status;
            CHECK(canUse(bank) == (status == 0 || status == 2));
        }
    }

    struct SkillRequirement { B bank; Skill skill; int minimum; };
    const SkillRequirement levels[] = {
        {B::BLAST_MINE, Skill::MINING, 43}, {B::CRAFTING_GUILD, Skill::CRAFTING, 99},
        {B::FARMING_GUILD, Skill::FARMING, 45}, {B::FARMING_GUILD_NORTH, Skill::FARMING, 85},
        {B::FISHING_GUILD, Skill::FISHING, 68}, {B::MINING_GUILD, Skill::MINING, 60},
        {B::WOODCUTTING_GUILD, Skill::WOODCUTTING, 60}, {B::PORT_ROBERTS_BANK, Skill::SAILING, 50},
        {B::DEEPFIN_POINT, Skill::SAILING, 67},
    };
    for (const auto& [bank, skill, minimum] : levels) {
        reset();
        skills[static_cast<int32_t>(skill)] = minimum - 1;
        CHECK(!canUse(bank)); // Boosted 99 must not satisfy a real-level requirement.
        skills[static_cast<int32_t>(skill)] = minimum;
        CHECK(canUse(bank));
    }
    for (const auto diary : {VarbitID::FALADOR_DIARY_HARD_COMPLETE, VarbitID::FALADOR_DIARY_ELITE_COMPLETE}) {
        reset();
        skills[static_cast<int32_t>(Skill::CRAFTING)] = 1;
        varbits[diary] = 1;
        CHECK(canUse(B::CRAFTING_GUILD));
    }
    struct SailingStage { B bank; int level; int32_t quest; int stage; };
    for (const auto& [bank, level, quest, stage] : {SailingStage{B::GREAT_CONCH_BANK, 45, VarbitID::TT, 8},
                                                    SailingStage{B::RED_ROCK_BANK, 52, VarbitID::TRR, 10}}) {
        reset();
        skills[static_cast<int32_t>(Skill::SAILING)] = level;
        varbits[quest] = stage - 1;
        CHECK(!canUse(bank));
        varbits[quest] = stage;
        CHECK(canUse(bank));
        skills[static_cast<int32_t>(Skill::SAILING)] = level - 1;
        CHECK(!canUse(bank));
    }

    reset();
    skills[0] = 65;
    skills[2] = 64;
    CHECK(!canUse(B::WARRIORS_GUILD));
    skills[2] = 65;
    CHECK(canUse(B::WARRIORS_GUILD));
    skills[0] = 1;
    skills[2] = 99;
    CHECK(canUse(B::WARRIORS_GUILD));
    skills[0] = 99;
    skills[2] = 1;
    CHECK(canUse(B::WARRIORS_GUILD));

    for (auto bank : {B::ALDARIN_SOUTH, B::ALDARIN_CENTRAL, B::NEMUS_RETREAT,
                      B::AUBURNVALE_WEST_SIDE, B::AUBURNVALE_EAST_SIDE, B::TAL_TEKLAN}) {
        reset();
        quests[3450] = 2;
        varbits[VarbitID::VMQ2_FIRST_TRAVEL] = 2;
        CHECK(!canUse(bank));
        varbits[VarbitID::VMQ2_FIRST_TRAVEL] = 3;
        CHECK(canUse(bank));
        membersWorld = false;
        CHECK(!canUse(bank));
        membersWorld = true;
        worldAvailable = false;
        CHECK(!canUse(bank));
        worldAvailable = true;
        quests[3450] = 0;
        CHECK(!canUse(bank));
    }
    reset();
    quests[3450] = 2;
    varbits[VarbitID::VMQ2_FIRST_TRAVEL] = 3;
    skills[static_cast<int32_t>(Skill::HUNTER)] = 45;
    CHECK(!canUse(B::VARLAMORE_HUNTERS_GUILD_BANK));
    skills[static_cast<int32_t>(Skill::HUNTER)] = 46;
    CHECK(canUse(B::VARLAMORE_HUNTERS_GUILD_BANK));
    varbits[VarbitID::VMQ2_FIRST_TRAVEL] = 2;
    CHECK(!canUse(B::VARLAMORE_HUNTERS_GUILD_BANK)); // Like Aldarin, the guild needs Varlamore's travel unlocked.

    for (auto bank : {B::ARCEUUS, B::GREAT_KOUREND_CASTLE, B::HOSIDIUS, B::LANDS_END,
                      B::LOVAKENGJ, B::MOUNT_QUIDAMORTEM, B::VINERY_BANK, B::WINTERTODT, B::ZEAH_SAND_BANK}) {
        reset();
        quests[162] = 2;
        CHECK(!canUse(bank));
        varbits[VarbitID::ZEAH_PLAYERHASVISITED] = 1;
        CHECK(canUse(bank));
        quests[162] = 1;
        CHECK(!canUse(bank));
    }

    reset();
    quests[117] = 2; // The full RFD quest must not stand in for its intro subquest.
    CHECK(!canUse(B::LUMBRIDGE_BASEMENT));
    quests[2307] = 2;
    CHECK(canUse(B::LUMBRIDGE_BASEMENT));
    membersWorld = false;
    CHECK(!canUse(B::LUMBRIDGE_BASEMENT));
    membersWorld = true;
    worldAvailable = false;
    CHECK(!canUse(B::LUMBRIDGE_BASEMENT));

    reset();
    quests[11] = 2;
    CHECK(!canUse(B::FOSSIL_ISLAND));
    varbits[VarbitID::FOSSIL_BANK] = 1;
    CHECK(canUse(B::FOSSIL_ISLAND));
    quests[11] = 0;
    CHECK(!canUse(B::FOSSIL_ISLAND));

    reset();
    CHECK(!canUse(B::LUNAR_ISLE));
    quests[33] = 2;
    CHECK(canUse(B::LUNAR_ISLE));
    quests[33] = 1;
    TitanPluginSdk::InventoryItemState item{};
    item.itemId = 9083;
    item.quantity = 1;
    inventory = {item};
    CHECK(canUse(B::LUNAR_ISLE));
    inventory[0].quantity = 0;
    CHECK(!canUse(B::LUNAR_ISLE));

    reset();
    skills[static_cast<int32_t>(Skill::MINING)] = 30;
    playerAvailable = true;
    player.worldX = 3756 + 9;
    player.worldY = 5666 + 9;
    CHECK(canUse(B::MOTHERLODE_MINE));
    player.worldX++;
    CHECK(!canUse(B::MOTHERLODE_MINE));
    player.worldX--;
    player.plane = 1;
    CHECK(!canUse(B::MOTHERLODE_MINE));
    player.plane = 0;
    player.worldViewId = 7;
    CHECK(!canUse(B::MOTHERLODE_MINE));
    playerAvailable = false;
    item.itemId = 1265;
    item.quantity = 1;
    std::strcpy(item.name, "Bronze pickaxe");
    inventory = {item};
    CHECK(canUse(B::MOTHERLODE_MINE));
    inventory[0].itemId = 1266; // Noted bronze pickaxe.
    CHECK(!canUse(B::MOTHERLODE_MINE));
    inventory[0].itemId = 468; // Broken bronze pickaxe.
    CHECK(!canUse(B::MOTHERLODE_MINE));
    inventory[0].itemId = 1275; // A rune pickaxe cannot be used at 30 Mining.
    CHECK(!canUse(B::MOTHERLODE_MINE));
    inventory.clear();
    equipment = {{static_cast<int32_t>(titan::EquipmentSlot::WEAPON), 1265, 1}};
    CHECK(canUse(B::MOTHERLODE_MINE));
    skills[static_cast<int32_t>(Skill::MINING)] = 29;
    CHECK(!canUse(B::MOTHERLODE_MINE));

    struct VarRequirement { B bank; int32_t id; int minimum; };
    for (const auto& [bank, id, minimum] : {
            VarRequirement{B::BURGH_DE_ROTT, VarbitID::MYREQUE_2_QUEST, 200},
            VarRequirement{B::THE_PANDEMONIUM, VarbitID::SAILING_INTRO, 16},
            VarRequirement{B::INFERNO, VarbitID::INFERNO_SACRIFICED_FIRECAPE, 1},
            VarRequirement{B::CHARRED_ISLAND_BANK, VarbitID::AMENITY_BANKCHEST_CHARRED_ISLAND, 1},
            VarRequirement{B::WYRMSCRAIG_BANK, VarbitID::AMENITY_BANKCHEST_WYRMSCRAIG, 1},
            VarRequirement{B::SUNBLEAK_ISLAND_BANK, VarbitID::AMENITY_BANKCHEST_SUNBLEAK, 1},
            VarRequirement{B::BUCCANEERS_HAVEN_BANK, VarbitID::AMENITY_BANKCHEST_BUCCANEERS, 1},
            VarRequirement{B::DEEPFIN_MINE_WEST_BANK, VarbitID::AMENITY_BANKCHEST_DEEPFIN_1, 1},
            VarRequirement{B::DEEPFIN_MINE_EAST_BANK, VarbitID::AMENITY_BANKCHEST_DEEPFIN_2, 1}}) {
        reset();
        varbits[id] = minimum - 1;
        CHECK(!canUse(bank));
        varbits[id] = minimum;
        CHECK(canUse(bank));
    }
    for (const auto& [bank, id, minimum] : {
            VarRequirement{B::LLETYA, VarPlayerID::MOURNING_QUEST, 3},
            VarRequirement{B::FORTIS_COLOSSEUM, VarPlayerID::COLOSSEUM_GLORY, 2000}}) {
        reset();
        varps[id] = minimum - 1;
        CHECK(!canUse(bank));
        varps[id] = minimum;
        CHECK(canUse(bank));
    }

    reset();
    varbits[VarbitID::SAILING_BOARDED_BOAT] = 1;
    CHECK(!canUse(B::ONYX_CREST_BANK));
    varbits[VarbitID::AMENITY_BANKCHEST_ONYX_CREST] = 1;
    CHECK(canUse(B::ONYX_CREST_BANK));
    varbits[VarbitID::SAILING_BOARDED_BOAT] = 0;
    CHECK(!canUse(B::ONYX_CREST_BANK));
    playerAvailable = true;
    player.worldX = 46 * 64;
    player.worldY = 35 * 64;
    CHECK(canUse(B::ONYX_CREST_BANK));
    player.worldX--;
    CHECK(!canUse(B::ONYX_CREST_BANK));
    // Instanced coordinates must be mapped back to the template region.
    instanceLocation = titan::WorldPoint{46 * 64, 35 * 64, 0};
    CHECK(canUse(B::ONYX_CREST_BANK));
    instanceLocationAvailable = false;
    CHECK(!canUse(B::ONYX_CREST_BANK));

    reset();
    CHECK(!canUse(B::WINTERTODT));
    CHECK(!canUse(B::PRIFDDINAS));
    auto usable = getUsableBanks();
    std::vector<B> visited;
    const auto filtered = getUsableBanks([&](B bank) {
        visited.push_back(bank);
        return bank == B::GRAND_EXCHANGE;
    });
    CHECK(visited == usable);
    CHECK(filtered == std::vector{B::GRAND_EXCHANGE});
    CHECK(getUsableBanks([](B) { return false; }).empty());
    titan::detail::backendRef() = previousBackend;
}
