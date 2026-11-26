#include <titan/utils/tools/axe.h>
#include <titan/detail/backend_external.h>

#include "check.h"

using namespace titan::utils::Axe;

namespace {

int32_t woodcutting = 1;
int32_t questStatus = 2;
bool members = true;
bool worldAvailable = true;
bool inventoryAvailable = true;
bool equipmentAvailable = true;
std::vector<titan::ItemContainerSlot> inventory;
std::vector<titan::ItemContainerSlot> equipment;

}  // namespace

int main() {
    CHECK(!canUse(1351));
    CHECK(!hasUsableAxe());

    TitanPluginSdk::HostApi host{};
    host.getRealSkillLevel = [](int32_t skill) {
        CHECK(skill == static_cast<int32_t>(titan::Skill::WOODCUTTING));
        return woodcutting;
    };
    host.getBoostedSkillLevel = [](int32_t) { return 99; };
    host.getQuestState = [](int32_t rowId) {
        // Song of the Elves uses DB row 137, not native quest ordinal 156.
        CHECK(rowId == 137);
        return questStatus;
    };
    host.getCurrentWorld = [](int32_t* out) -> uint8_t {
        *out = 301;
        return worldAvailable;
    };
    host.getWorldList = [](TitanPluginSdk::WorldState* out, uint32_t cap) -> uint32_t {
        if (!cap || !worldAvailable) return 0;
        out[0].id = 301;
        out[0].flags = members ? 1 : 0;
        return 1;
    };
    host.getItemContainer = [](int32_t id, TitanPluginSdk::ItemContainerState* out) -> uint8_t {
        const bool isInventory = id == static_cast<int32_t>(titan::InventoryID::INVENTORY);
        CHECK(isInventory || id == static_cast<int32_t>(titan::InventoryID::EQUIPMENT));
        if (!(isInventory ? inventoryAvailable : equipmentAvailable)) return 0;
        const auto& items = isInventory ? inventory : equipment;
        out->writtenCount = static_cast<int32_t>(items.size());
        for (int32_t i = 0; i < out->writtenCount; ++i) {
            out->slots[i] = items[i].slot;
            out->itemIds[i] = items[i].itemId;
            out->quantities[i] = items[i].quantity;
        }
        return 1;
    };
    titan::detail::ExternalBackend backend{&host};
    auto* previousBackend = titan::detail::backendRef();
    titan::detail::backendRef() = &backend;

    // RuneLite item IDs exercise each source variant's skill boundary and access restrictions.
    struct Requirement { int32_t id; int32_t level; bool membersOnly; bool questRequired; };
    const Requirement axes[] = {
        {1351, 1, false, false}, {1349, 1, false, false}, {1353, 6, false, false},
        {1361, 11, false, false}, {1355, 21, false, false}, {1357, 31, false, false},
        {1359, 41, false, false}, {28214, 41, true, false},
        {6739, 61, true, false}, {25378, 61, true, false}, {30352, 61, true, false},
        {13241, 61, true, false}, {25066, 61, true, false}, {30347, 61, true, false},
        {28217, 61, true, false}, {23673, 71, true, true}, {23862, 71, true, true},
        {23675, 71, true, true}, {28223, 71, true, true}, {28220, 71, true, true},
    };
    for (const auto& axe : axes) {
        members = true;
        questStatus = 2;
        woodcutting = axe.level - 1;
        CHECK(!canUse(axe.id)); // Boosted Woodcutting must not satisfy a real-level gate.
        woodcutting = axe.level;
        CHECK(canUse(axe.id));
        woodcutting++;
        CHECK(canUse(axe.id));
        members = false;
        CHECK(canUse(axe.id) == !axe.membersOnly);
        members = true;
        for (int status : {-1, 1, 0}) {
            questStatus = status;
            CHECK(canUse(axe.id) == !axe.questRequired);
        }
    }

    woodcutting = 99;
    questStatus = 2;
    // Notes, broken tools, battleaxes, pickaxes, and unsupported uncharged variants must not qualify.
    for (int id : {-1, 0, 1352, 494, 1375, 1265, 13242}) CHECK(!canUse(id));
    worldAvailable = false;
    CHECK(!canUse(6739));
    CHECK(canUse(1351));
    worldAvailable = true;

    CHECK(!hasUsableAxe()); // Eligibility alone must not establish possession.
    inventory = {{0, 1351, 1}};
    CHECK(hasUsableAxe());
    for (int quantity : {0, -1}) {
        inventory[0].quantity = quantity;
        CHECK(!hasUsableAxe());
    }
    inventory.clear();
    equipment = {{3, 1351, 1}};
    CHECK(hasUsableAxe());
    inventoryAvailable = false;
    CHECK(hasUsableAxe());
    equipmentAvailable = false;
    CHECK(!hasUsableAxe());
    inventoryAvailable = true;
    equipmentAvailable = true;
    equipment[0].itemId = 1359;
    woodcutting = 30;
    CHECK(!hasUsableAxe());
    inventory = {{0, 1351, 1}};
    CHECK(hasUsableAxe());

    titan::detail::backendRef() = previousBackend;
}
