#include <titan/utils/runes/rune_element.h>

#include <algorithm>
#include <array>

#include "check.h"

using titan::EquipmentSlot;
using titan::RuneElement;
using titan::gamevals::ItemID;
using namespace titan::utils::RuneElement;

static_assert(std::ranges::equal(getRuneElementsProvidedByEquipment(ItemID::STAFF_OF_FIRE),
    std::array{RuneElement::FIRE}));
static_assert(std::ranges::equal(getRuneElementsProvidedByEquipment(ItemID::KODAI_WAND),
    std::array{RuneElement::WATER}));
static_assert(std::ranges::equal(getRuneElementsProvidedByEquipment(ItemID::MYSTIC_STEAM_BATTLESTAFF_PRETTY),
    std::array{RuneElement::WATER, RuneElement::FIRE}));
static_assert(std::ranges::equal(getRuneElementsProvidedByEquipment(ItemID::TWINFLAME_STAFF),
    std::array{RuneElement::WATER, RuneElement::FIRE}));
static_assert(std::ranges::equal(getRuneElementsProvidedByEquipment(ItemID::SHADOWFLAME_QUADRANT),
    std::array{RuneElement::AIR, RuneElement::WATER, RuneElement::EARTH, RuneElement::FIRE}));
static_assert(std::ranges::equal(getRuneElementsProvidedByEquipment(ItemID::TOME_OF_FIRE),
    std::array{RuneElement::FIRE}));
static_assert(std::ranges::equal(getRuneElementsProvidedByEquipment(ItemID::TOME_OF_WATER),
    std::array{RuneElement::WATER}));
static_assert(std::ranges::equal(getRuneElementsProvidedByEquipment(ItemID::TOME_OF_EARTH),
    std::array{RuneElement::EARTH}));

// Empty, noted and placeholder items must not inherit the charged/equippable item's provision.
static_assert([] {
    for (auto id : {ItemID::TOME_OF_FIRE_UNCHARGED, ItemID::TOME_OF_WATER_UNCHARGED,
                   ItemID::TOME_OF_EARTH_UNCHARGED, ItemID::CERT_STAFF_OF_FIRE,
                   ItemID::PLACEHOLDER_TOME_OF_WATER, ItemID::FIRERUNE, -1, 0}) {
        if (!getRuneElementsProvidedByEquipment(id).empty()) return false;
    }
    return true;
}());

static_assert([] {
    for (const auto& equipment : kEquipment) {
        if (equipment.elements.size() == 0) return false;
        if (std::ranges::count(kEquipment, equipment.itemId, &EquipmentDefinition::itemId) != 1) return false;
        for (auto element : equipment.elements) {
            if (std::ranges::count(equipment.elements, element) != 1) return false;
        }
        const bool tome = equipment.itemId == ItemID::TOME_OF_FIRE
            || equipment.itemId == ItemID::TOME_OF_WATER || equipment.itemId == ItemID::TOME_OF_EARTH;
        if (equipment.slot != (tome ? EquipmentSlot::SHIELD : EquipmentSlot::WEAPON)) return false;
    }
    return true;
}());

int main() {
    volatile int32_t tomeId = ItemID::TOME_OF_WATER;
    CHECK(std::ranges::equal(getRuneElementsProvidedByEquipment(tomeId), std::array{RuneElement::WATER}));
    volatile int32_t staffId = ItemID::SHADOWFLAME_QUADRANT;
    CHECK(std::ranges::equal(getRuneElementsProvidedByEquipment(staffId),
        std::array{RuneElement::AIR, RuneElement::WATER, RuneElement::EARTH, RuneElement::FIRE}));
}
