#include <titan/utils/runes/rune_pouch.h>
#include <titan/utils/runes/rune_element.h>
#include <titan/detail/backend_external.h>

#include "check.h"
#include <array>
#include <map>

using titan::ItemContainerSlot;
using titan::InventoryID;
using titan::RuneElement;
using titan::RunePouchType;
using titan::gamevals::ItemID;
using titan::gamevals::VarbitID;
using namespace titan::utils::RunePouch;
using namespace titan::utils::RuneElement;

namespace {

std::vector<ItemContainerSlot> inventory;
std::vector<ItemContainerSlot> liveBank;
TitanPluginSdk::BankCacheState rememberedBank;
std::map<int32_t, int32_t> varbits;
bool inventoryAvailable = true;
bool bankOpen = false;
bool bankAvailable = true;
bool bankCacheAvailable = true;
bool membersWorld = true;
bool worldAvailable = true;

}  // namespace

int main() {
    const std::array regular{ItemContainerSlot{7, ItemID::BH_RUNE_POUCH, 1}};
    const std::array divine{ItemContainerSlot{7, ItemID::DIVINE_RUNE_POUCH_TROUVER, 1}};
    CHECK(getCarriedRunePouchType(regular) == RunePouchType::REGULAR);
    CHECK(getCarriedRunePouchType(divine) == RunePouchType::DIVINE);
    CHECK(!getCarriedRunePouchType({}));
    for (auto id : {ItemID::BH_RUNE_POUCH, ItemID::BH_RUNE_POUCH_TROUVER,
                    ItemID::DIVINE_RUNE_POUCH, ItemID::DIVINE_RUNE_POUCH_TROUVER}) {
        const std::array items{ItemContainerSlot{0, id, 1}};
        const auto expected = id == ItemID::BH_RUNE_POUCH || id == ItemID::BH_RUNE_POUCH_TROUVER
            ? RunePouchType::REGULAR : RunePouchType::DIVINE;
        CHECK(getCarriedRunePouchType(items) == expected);
        CHECK(accountOwnsRunePouch({}, items));
        CHECK(accountOwnsRunePouch(items, {}));
    }
    const std::array both{regular[0], divine[0]};
    const std::array bothReversed{divine[0], regular[0]};
    CHECK(getCarriedRunePouchType(both) == RunePouchType::DIVINE);
    CHECK(getCarriedRunePouchType(bothReversed) == RunePouchType::DIVINE);
    const std::array ignored{
        ItemContainerSlot{0, ItemID::PLACEHOLDER_BH_RUNE_POUCH, 1},
        ItemContainerSlot{1, ItemID::PLACEHOLDER_DIVINE_RUNE_POUCH, 1},
        ItemContainerSlot{2, ItemID::BH_RUNE_POUCH, 0},
        ItemContainerSlot{3, ItemID::DIVINE_RUNE_POUCH, -1},
        ItemContainerSlot{4, -1, 1},
    };
    CHECK(!accountOwnsRunePouch(ignored, ignored));

    CHECK(!isValidSlot(RunePouchType::REGULAR, -1));
    CHECK(isValidSlot(RunePouchType::REGULAR, 2));
    CHECK(!isValidSlot(RunePouchType::REGULAR, 3));
    CHECK(isValidSlot(RunePouchType::DIVINE, 3));
    CHECK(!isValidSlot(RunePouchType::DIVINE, 4));
    CHECK(!isValidSlot(static_cast<RunePouchType>(-1), 0));

    // Slot four can remain populated in stored data even when only a regular pouch is carried.
    const titan::RunePouchContents contents{
        {0, ItemID::MISTRUNE, 10}, {2, ItemID::STEAMRUNE, 20}, {3, ItemID::AETHERRUNE, 30},
        {-1, ItemID::LAWRUNE, 99}, {4, ItemID::LAWRUNE, 99}, {1, ItemID::LAWRUNE, -1},
    };
    const RuneElementAmounts regularAmounts{
        {RuneElement::AIR, 10}, {RuneElement::WATER, 30}, {RuneElement::FIRE, 20},
    };
    auto divineAmounts = regularAmounts;
    divineAmounts[RuneElement::COSMIC] = 30;
    divineAmounts[RuneElement::SOUL] = 30;
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch(regular, contents, true) == regularAmounts);
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch(divine, contents, true) == divineAmounts);
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch({}, contents, true).empty());
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch(divine, contents, false).empty());
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch(divine, {}, true).empty());
    const std::array inventoryWithRunes{divine[0], ItemContainerSlot{8, ItemID::WATERRUNE, 7}};
    const std::array equipment{ItemContainerSlot{
        static_cast<int32_t>(titan::EquipmentSlot::WEAPON), ItemID::STAFF_OF_FIRE, 1}};
    auto carriedAmounts = divineAmounts;
    carriedAmounts[RuneElement::WATER] += 7;
    carriedAmounts[RuneElement::FIRE] = kInfiniteRuneAmount;
    CHECK(getCarriedRuneElementAmounts(inventoryWithRunes, equipment, contents, true) == carriedAmounts);

    CHECK(!accountOwnsRunePouch());
    CHECK(!getCarriedRunePouchType());
    CHECK(getRunePouchContents().empty());
    CHECK(!getAvailableRuneElementAmountsFromCarriedRunePouch());

    TitanPluginSdk::HostApi host{};
    host.getVarbit = [](int32_t id) { return varbits[id]; };
    host.getWidget = [](uint32_t id, TitanPluginSdk::WidgetState* out) -> uint8_t {
        CHECK(id == titan::gamevals::InterfaceID::Bankmain::ITEMS);
        out->visible = bankOpen;
        return 1;
    };
    host.getItemContainer = [](int32_t id, TitanPluginSdk::ItemContainerState* out) -> uint8_t {
        if (id == static_cast<int32_t>(InventoryID::EQUIPMENT)) return 1;
        const bool isInventory = id == static_cast<int32_t>(InventoryID::INVENTORY);
        CHECK(isInventory || id == static_cast<int32_t>(InventoryID::BANK));
        if (!(isInventory ? inventoryAvailable : bankAvailable)) return 0;
        const auto& items = isInventory ? inventory : liveBank;
        out->writtenCount = static_cast<int32_t>(items.size());
        for (int32_t i = 0; i < out->writtenCount; ++i) {
            out->slots[i] = items[i].slot;
            out->itemIds[i] = items[i].itemId;
            out->quantities[i] = items[i].quantity;
        }
        return 1;
    };
    host.getItemCacheBank = [](TitanPluginSdk::BankCacheState* out) {
        *out = rememberedBank;
        return bankCacheAvailable;
    };
    host.getCurrentWorld = [](int32_t* out) -> uint8_t { *out = 301; return worldAvailable; };
    host.getWorldList = [](TitanPluginSdk::WorldState* out, uint32_t cap) -> uint32_t {
        if (!cap) return 0;
        out[0].id = 301;
        out[0].flags = membersWorld ? 1 : 0;
        return 1;
    };
    titan::detail::ExternalBackend backend{&host};
    auto* previousBackend = titan::detail::backendRef();
    titan::detail::backendRef() = &backend;

    CHECK(!accountOwnsRunePouch());
    rememberedBank.known = 1;
    CHECK(accountOwnsRunePouch() == false);
    rememberedBank.bank.writtenCount = 1;
    rememberedBank.bank.itemIds[0] = ItemID::BH_RUNE_POUCH_TROUVER;
    rememberedBank.bank.quantities[0] = 1;
    CHECK(accountOwnsRunePouch() == true);
    inventoryAvailable = false;
    CHECK(accountOwnsRunePouch() == true);
    rememberedBank.bank.quantities[0] = 0;
    CHECK(!accountOwnsRunePouch());
    inventoryAvailable = true;
    CHECK(accountOwnsRunePouch() == false);
    rememberedBank.loading = 1;
    CHECK(!accountOwnsRunePouch());
    // A refresh in progress must not discard a remembered positive observation.
    rememberedBank.bank.quantities[0] = 1;
    CHECK(accountOwnsRunePouch());
    rememberedBank.bank.quantities[0] = 0;
    rememberedBank.loading = 0;
    bankCacheAvailable = false;
    CHECK(!accountOwnsRunePouch());
    inventory.assign(regular.begin(), regular.end());
    CHECK(accountOwnsRunePouch() == true);
    CHECK(getCarriedRunePouchType() == RunePouchType::REGULAR);
    inventory.clear();
    bankCacheAvailable = true;

    // A closed native container is not a present observation of the current account's bank.
    liveBank.assign(divine.begin(), divine.end());
    CHECK(accountOwnsRunePouch() == false);
    bankOpen = true;
    CHECK(accountOwnsRunePouch() == true);
    liveBank.clear();
    rememberedBank.bank.quantities[0] = 1;
    // An empty open bank must not veto remembered positive evidence.
    CHECK(accountOwnsRunePouch() == true);
    bankAvailable = false;
    CHECK(accountOwnsRunePouch() == true);
    bankOpen = false;

    // Sparse decoded contents retain their physical slot, not their vector index.
    varbits = {{VarbitID::RUNE_POUCH_TYPE_1, 15}, {VarbitID::RUNE_POUCH_QUANTITY_1, 10},
               {VarbitID::RUNE_POUCH_TYPE_3, 19}, {VarbitID::RUNE_POUCH_QUANTITY_3, 20},
               {VarbitID::RUNE_POUCH_TYPE_4, 23}, {VarbitID::RUNE_POUCH_QUANTITY_4, 30}};
    auto decoded = getRunePouchContents();
    CHECK(decoded.size() == 3);
    CHECK(decoded[0].slot == 0 && decoded[0].itemId == ItemID::MISTRUNE && decoded[0].quantity == 10);
    CHECK(decoded[1].slot == 2 && decoded[1].itemId == ItemID::STEAMRUNE && decoded[1].quantity == 20);
    CHECK(decoded[2].slot == 3 && decoded[2].itemId == ItemID::AETHERRUNE && decoded[2].quantity == 30);
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch() == RuneElementAmounts{});
    inventory.assign(regular.begin(), regular.end());
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch() == regularAmounts);
    inventory.assign(divine.begin(), divine.end());
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch() == divineAmounts);
    CHECK(getCarriedRuneElementAmounts() == divineAmounts);
    membersWorld = false;
    CHECK(getCarriedRunePouchType() == RunePouchType::DIVINE);
    CHECK(accountOwnsRunePouch() == true);
    CHECK(getRunePouchContents().size() == 3);
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch() == RuneElementAmounts{});
    CHECK(getCarriedRuneElementAmounts() == RuneElementAmounts{});
    inventoryAvailable = false;
    CHECK(getAvailableRuneElementAmountsFromCarriedRunePouch() == RuneElementAmounts{});
    membersWorld = true;
    CHECK(!getAvailableRuneElementAmountsFromCarriedRunePouch());
    inventoryAvailable = true;
    worldAvailable = false;
    CHECK(!getAvailableRuneElementAmountsFromCarriedRunePouch());
    worldAvailable = true;

    varbits.clear();
    const std::array expectedIds{
        ItemID::AIRRUNE, ItemID::WATERRUNE, ItemID::EARTHRUNE, ItemID::FIRERUNE,
        ItemID::MINDRUNE, ItemID::CHAOSRUNE, ItemID::DEATHRUNE, ItemID::BLOODRUNE,
        ItemID::COSMICRUNE, ItemID::NATURERUNE, ItemID::LAWRUNE, ItemID::BODYRUNE,
        ItemID::SOULRUNE, ItemID::ASTRALRUNE, ItemID::MISTRUNE, ItemID::MUDRUNE,
        ItemID::DUSTRUNE, ItemID::LAVARUNE, ItemID::STEAMRUNE, ItemID::SMOKERUNE,
        ItemID::WRATHRUNE, ItemID::SUNFIRERUNE, ItemID::AETHERRUNE,
    };
    varbits[VarbitID::RUNE_POUCH_QUANTITY_2] = 123;
    for (int32_t type = 1; type <= static_cast<int32_t>(expectedIds.size()); ++type) {
        varbits[VarbitID::RUNE_POUCH_TYPE_2] = type;
        decoded = getRunePouchContents();
        CHECK(decoded.size() == 1);
        CHECK(decoded[0].slot == 1 && decoded[0].itemId == expectedIds[type - 1] && decoded[0].quantity == 123);
    }
    for (auto type : {-1, 0, 24, 999}) {
        varbits[VarbitID::RUNE_POUCH_TYPE_2] = type;
        CHECK(getRunePouchContents().empty());
    }
    varbits[VarbitID::RUNE_POUCH_TYPE_2] = 1;
    for (auto quantity : {0, -1}) {
        varbits[VarbitID::RUNE_POUCH_QUANTITY_2] = quantity;
        CHECK(getRunePouchContents().empty());
    }
    titan::detail::backendRef() = previousBackend;
}
