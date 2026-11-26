#include <titan/utils/runes/rune_element.h>
#include <titan/detail/backend_external.h>

#include "check.h"
#include <array>
#include <limits>
#include <vector>

using titan::EquipmentSlot;
using titan::InventoryID;
using titan::ItemContainerSlot;
using titan::RuneElement;
using titan::gamevals::ItemID;
using namespace titan::utils::RuneElement;

namespace {

constexpr int32_t weaponSlot = static_cast<int32_t>(EquipmentSlot::WEAPON);
constexpr int32_t shieldSlot = static_cast<int32_t>(EquipmentSlot::SHIELD);
const std::vector<ItemContainerSlot> inventory{
    {0, ItemID::WATERRUNE, 10}, {1, ItemID::MISTRUNE, 5}, {2, ItemID::STEAMRUNE, 7},
    {3, ItemID::SUNFIRERUNE, 11}, {4, ItemID::AETHERRUNE, 13}, {5, ItemID::BLOODRUNE, 17},
    {6, ItemID::LAWRUNE, (std::numeric_limits<int32_t>::max)()},
    {7, ItemID::LAWRUNE, (std::numeric_limits<int32_t>::max)()},
};
const std::vector<ItemContainerSlot> equipment{
    {weaponSlot, ItemID::STAFF_OF_FIRE, 1}, {shieldSlot, ItemID::TOME_OF_WATER, 1},
};

bool membersWorld = true;
bool worldAvailable = true;
bool worldListed = true;
bool inventoryAvailable = true;
bool equipmentAvailable = true;
bool containersEmpty = false;

}  // namespace

int main() {
    const RuneElementAmounts memberInventory{
        {RuneElement::AIR, 5}, {RuneElement::WATER, 22}, {RuneElement::FIRE, 18},
        {RuneElement::COSMIC, 13}, {RuneElement::SOUL, 13}, {RuneElement::BLOOD, 17},
        {RuneElement::LAW, 4294967294LL},
    };
    const RuneElementAmounts freeInventory{{RuneElement::WATER, 10}, {RuneElement::LAW, 4294967294LL}};
    const RuneElementAmounts memberEquipment{
        {RuneElement::WATER, kInfiniteRuneAmount}, {RuneElement::FIRE, kInfiniteRuneAmount},
    };
    const RuneElementAmounts freeEquipment{{RuneElement::FIRE, kInfiniteRuneAmount}};
    CHECK(getAvailableRuneElementAmountsFromInventoryRunes(inventory, true) == memberInventory);
    CHECK(getAvailableRuneElementAmountsFromInventoryRunes(inventory, false) == freeInventory);
    CHECK(getAvailableRuneElementAmountsFromEquippedItems(equipment, true) == memberEquipment);
    CHECK(getAvailableRuneElementAmountsFromEquippedItems(equipment, false) == freeEquipment);

    auto memberCarried = memberInventory;
    memberCarried[RuneElement::WATER] = kInfiniteRuneAmount;
    memberCarried[RuneElement::FIRE] = kInfiniteRuneAmount;
    auto freeCarried = freeInventory;
    freeCarried[RuneElement::FIRE] = kInfiniteRuneAmount;
    CHECK(getCarriedRuneElementAmounts(inventory, equipment, true) == memberCarried);
    CHECK(getCarriedRuneElementAmounts(inventory, equipment, false) == freeCarried);
    CHECK(getCarriedRuneElementAmounts({}, {}, false).empty());

    // Carrying a provider or rune pouch in inventory does not expose its supplies.
    const std::array ignoredInventory{
        ItemContainerSlot{0, ItemID::STAFF_OF_FIRE, 1}, ItemContainerSlot{1, ItemID::TOME_OF_WATER, 1},
        ItemContainerSlot{2, ItemID::BH_RUNE_POUCH, 1}, ItemContainerSlot{3, ItemID::FIRERUNE, 0},
        ItemContainerSlot{4, ItemID::WATERRUNE, -1}, ItemContainerSlot{5, -1, 100},
    };
    CHECK(getAvailableRuneElementAmountsFromInventoryRunes(ignoredInventory, true).empty());
    const std::array ignoredEquipment{
        ItemContainerSlot{shieldSlot, ItemID::STAFF_OF_FIRE, 1},
        ItemContainerSlot{weaponSlot, ItemID::TOME_OF_WATER, 1},
        ItemContainerSlot{shieldSlot, ItemID::TOME_OF_EARTH_UNCHARGED, 1},
        ItemContainerSlot{weaponSlot, ItemID::STAFF_OF_AIR, 0},
    };
    CHECK(getAvailableRuneElementAmountsFromEquippedItems(ignoredEquipment, true).empty());
    const std::array steamStaff{ItemContainerSlot{weaponSlot, ItemID::MYSTIC_STEAM_BATTLESTAFF, 1}};
    CHECK(getAvailableRuneElementAmountsFromEquippedItems(steamStaff, true) == memberEquipment);
    CHECK(getAvailableRuneElementAmountsFromEquippedItems(steamStaff, false).empty());
    const std::array brAirStaff{ItemContainerSlot{weaponSlot, ItemID::BR_AIR_STAFF, 1}};
    CHECK((getAvailableRuneElementAmountsFromEquippedItems(brAirStaff, false)
        == RuneElementAmounts{{RuneElement::AIR, kInfiniteRuneAmount}}));

    CHECK(!getCarriedRuneElementAmounts());
    CHECK(!getAvailableRuneElementAmountsFromInventoryRunes());
    CHECK(!getAvailableRuneElementAmountsFromEquippedItems());

    // Exercise SDK reads through the real external adapter with deterministic host snapshots.
    TitanPluginSdk::HostApi host{};
    host.getCurrentWorld = [](int32_t* out) -> uint8_t {
        *out = 301;
        return worldAvailable;
    };
    host.getWorldList = [](TitanPluginSdk::WorldState* out, uint32_t cap) -> uint32_t {
        if (!cap) return 0;
        out[0].id = worldListed ? 301 : 302;
        out[0].flags = membersWorld ? 1u : 0u;
        return 1;
    };
    host.getItemContainer = [](int32_t id, TitanPluginSdk::ItemContainerState* out) -> uint8_t {
        const bool isInventory = id == static_cast<int32_t>(InventoryID::INVENTORY);
        CHECK(isInventory || id == static_cast<int32_t>(InventoryID::EQUIPMENT));
        if (!(isInventory ? inventoryAvailable : equipmentAvailable)) return 0;
        const auto& items = isInventory ? inventory : equipment;
        out->containerId = id;
        out->writtenCount = containersEmpty ? 0 : static_cast<int32_t>(items.size());
        out->capacity = isInventory ? 28 : 14;
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
    CHECK(getCarriedRuneElementAmounts() == memberCarried);
    CHECK(getAvailableRuneElementAmountsFromInventoryRunes() == memberInventory);
    CHECK(getAvailableRuneElementAmountsFromEquippedItems() == memberEquipment);
    membersWorld = false;
    CHECK(getCarriedRuneElementAmounts() == freeCarried);
    CHECK(getAvailableRuneElementAmountsFromInventoryRunes() == freeInventory);
    CHECK(getAvailableRuneElementAmountsFromEquippedItems() == freeEquipment);

    inventoryAvailable = false;
    CHECK(!getCarriedRuneElementAmounts());
    CHECK(!getAvailableRuneElementAmountsFromInventoryRunes());
    CHECK(getAvailableRuneElementAmountsFromEquippedItems() == freeEquipment);
    inventoryAvailable = true;
    equipmentAvailable = false;
    CHECK(!getCarriedRuneElementAmounts());
    CHECK(!getAvailableRuneElementAmountsFromEquippedItems());
    CHECK(getAvailableRuneElementAmountsFromInventoryRunes() == freeInventory);
    equipmentAvailable = true;
    worldAvailable = false;
    CHECK(!getCarriedRuneElementAmounts());
    worldAvailable = true;
    worldListed = false;
    CHECK(!getCarriedRuneElementAmounts());
    CHECK(!getAvailableRuneElementAmountsFromInventoryRunes());
    CHECK(!getAvailableRuneElementAmountsFromEquippedItems());
    worldListed = true;
    containersEmpty = true;
    CHECK(getCarriedRuneElementAmounts() == RuneElementAmounts{});
    titan::detail::backendRef() = previousBackend;
}
