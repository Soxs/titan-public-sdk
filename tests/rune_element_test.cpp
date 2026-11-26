#include <titan/utils/runes/rune_element.h>

#include <algorithm>
#include <array>
#include <limits>

#include "check.h"

using titan::RuneElement;
using titan::gamevals::ItemID;
using namespace titan::utils::RuneElement;

static_assert(isRune(ItemID::WATERRUNE));
static_assert(isRune(ItemID::STEAMRUNE));
static_assert(isRune(ItemID::SUNFIRERUNE));
static_assert(isRune(ItemID::AETHERRUNE));

static_assert(std::ranges::equal(getRuneElementsProvidedByRune(ItemID::WATERRUNE),
    std::array{RuneElement::WATER}));
static_assert(std::ranges::equal(getRuneElementsProvidedByRune(ItemID::STEAMRUNE),
    std::array{RuneElement::WATER, RuneElement::FIRE}));
static_assert(std::ranges::equal(getRuneElementsProvidedByRune(ItemID::AETHERRUNE),
    std::array{RuneElement::COSMIC, RuneElement::SOUL}));
static_assert(std::ranges::equal(getRuneElementsProvidedByRune(ItemID::SUNFIRERUNE),
    std::array{RuneElement::FIRE}));

static_assert(getPrimaryRuneForElement(RuneElement::WATER) == ItemID::WATERRUNE);
static_assert(getPrimaryRuneForElement(RuneElement::FIRE) == ItemID::FIRERUNE);
static_assert(getPrimaryRuneForElement(RuneElement::COSMIC) == ItemID::COSMICRUNE);

static_assert(std::ranges::equal(getAllRunesProvidingElement(RuneElement::WATER),
    std::array{ItemID::WATERRUNE, ItemID::MISTRUNE, ItemID::MUDRUNE, ItemID::STEAMRUNE}));
static_assert(std::ranges::equal(getAllRunesProvidingElement(RuneElement::FIRE),
    std::array{ItemID::FIRERUNE, ItemID::SMOKERUNE, ItemID::STEAMRUNE, ItemID::LAVARUNE, ItemID::SUNFIRERUNE}));
static_assert(std::ranges::equal(getAllRunesProvidingElement(RuneElement::SOUL),
    std::array{ItemID::SOULRUNE, ItemID::AETHERRUNE}));
static_assert(std::ranges::equal(getAllRunesProvidingElement(RuneElement::LAW),
    std::array{ItemID::LAWRUNE}));

static_assert([] {
    for (int32_t id : {-1, 0, 995, std::numeric_limits<int32_t>::max()}) {
        if (isRune(id) || !getRuneElementsProvidedByRune(id).empty()) return false;
    }
    for (int32_t value : {-1, 15, std::numeric_limits<int32_t>::max()}) {
        const auto element = static_cast<RuneElement>(value);
        if (getPrimaryRuneForElement(element) || !getAllRunesProvidingElement(element).empty()) return false;
    }
    return true;
}());

// The two catalog directions must agree, including elements not covered by the examples above.
static_assert([] {
    for (const auto& element : kElements) {
        const auto providers = getAllRunesProvidingElement(element.element);
        if (providers.empty() || providers.front() != getPrimaryRuneForElement(element.element)) return false;
        for (const auto& rune : kRunes) {
            const auto provided = getRuneElementsProvidedByRune(rune.itemId);
            if (std::ranges::count(provided, element.element) != std::ranges::count(providers, rune.itemId)) return false;
        }
    }
    return true;
}());

int main() {
    volatile int32_t waterId = ItemID::WATERRUNE;
    const auto elements = getRuneElementsProvidedByRune(waterId);
    CHECK(elements.size() == 1);
    CHECK(elements[0] == RuneElement::WATER);
    volatile RuneElement waterElement = RuneElement::WATER;
    CHECK(std::ranges::equal(getAllRunesProvidingElement(waterElement),
        std::array{ItemID::WATERRUNE, ItemID::MISTRUNE, ItemID::MUDRUNE, ItemID::STEAMRUNE}));
}
