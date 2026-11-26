#include <titan/utils/tools/pickaxe.h>
#include <titan/detail/backend_external.h>

#include "check.h"
#include <utility>

using namespace titan::utils::Pickaxe;

namespace {

int32_t mining = 1;
int32_t questStatus = 2;
bool members = true;
bool worldAvailable = true;
bool containersAvailable = true;
std::vector<titan::ItemContainerSlot> inventory;
std::vector<titan::ItemContainerSlot> equipment;

}  // namespace

int main() {
    CHECK(!canUse(1265));
    CHECK(!hasUsablePickaxe());

    TitanPluginSdk::HostApi host{};
    host.getRealSkillLevel = [](int32_t skill) {
        CHECK(skill == static_cast<int32_t>(titan::Skill::MINING));
        return mining;
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
        if (!containersAvailable) return 0;
        const auto& items = id == static_cast<int32_t>(titan::InventoryID::INVENTORY) ? inventory : equipment;
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

    // Every supported pickaxe variant must retain its Mining, membership, and quest gates.
    const std::pair<int32_t, int32_t> pickaxes[] = {
        {1265, 1}, {1267, 1}, {1269, 6}, {12297, 11}, {1273, 21}, {1271, 31},
        {1275, 41}, {23276, 41}, {11920, 61}, {23677, 61}, {25376, 61},
        {13243, 61}, {25063, 61}, {30345, 61}, {13244, 61}, {25369, 61}, {30346, 61},
        {23680, 71}, {23863, 71}, {23682, 71},
    };
    for (const auto& [id, requiredMining] : pickaxes) {
        members = true;
        questStatus = 2;
        mining = requiredMining - 1;
        CHECK(!canUse(id)); // Boosted Mining must not satisfy a real-level gate.
        mining = requiredMining;
        CHECK(canUse(id));
        mining++;
        CHECK(canUse(id));
        members = false;
        CHECK(canUse(id) == (id != 23276 && requiredMining < 61));
        members = true;
        for (int status : {-1, 1, 0}) {
            questStatus = status;
            CHECK(canUse(id) == (requiredMining != 71));
        }
    }

    mining = 99;
    questStatus = 2;
    for (int id : {-1, 0, 1266, 468}) { // Unknown, noted, and broken tools cannot qualify.
        CHECK(!canUse(id));
    }
    worldAvailable = false;
    CHECK(!canUse(11920));
    CHECK(canUse(1265));
    worldAvailable = true;

    CHECK(!hasUsablePickaxe()); // Eligibility alone must not establish possession.
    inventory = {{0, 1265, 1}};
    CHECK(hasUsablePickaxe());
    for (int quantity : {0, -1}) {
        inventory[0].quantity = quantity;
        CHECK(!hasUsablePickaxe());
    }
    inventory.clear();
    equipment = {{3, 1265, 1}};
    CHECK(hasUsablePickaxe());
    equipment[0].itemId = 1275;
    mining = 30;
    CHECK(!hasUsablePickaxe());
    inventory = {{0, 1265, 1}};
    CHECK(hasUsablePickaxe());
    containersAvailable = false;
    CHECK(!hasUsablePickaxe());

    titan::detail::backendRef() = previousBackend;
}
