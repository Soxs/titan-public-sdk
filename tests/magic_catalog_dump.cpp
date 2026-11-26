#include <titan/utils/magic.h>
#include "check.h"
#include <iostream>
#include <map>

using namespace titan::utils::Magic;

template <SpellEnum Spell>
void dump(const char* book) {
    constexpr const char* elements[] = {
        "AIR", "WATER", "EARTH", "FIRE", "MIND", "BODY", "COSMIC", "CHAOS",
        "NATURE", "LAW", "DEATH", "ASTRAL", "BLOOD", "SOUL", "WRATH"
    };
    for (int i = 0; i < static_cast<int>(Spell::COUNT); ++i) {
        const auto spell = static_cast<Spell>(i);
        const auto metadata = info(spell);
        const auto required = requirements(spell);
        CHECK(required.known);
        std::map<int, int> costs;
        for (const auto cost : required.runes) {
            if (cost.quantity == 0) continue;
            CHECK(cost.quantity > 0);
            CHECK(costs.emplace(static_cast<int>(cost.element), cost.quantity).second);
        }
        std::cout << book << '\t' << metadata.name << '\t' << metadata.level << '\t' << metadata.members << '\t';
        bool first = true;
        for (const auto [element, amount] : costs) {
            if (!first) std::cout << ',';
            std::cout << elements[element] << ':' << amount;
            first = false;
        }
        std::cout << '\n';
    }
}

int main() {
    dump<Standard>("Standard");
    dump<Ancient>("Ancient");
    dump<Lunar>("Lunar");
    dump<Necromancy>("Necromancy");
    dump<BoltEnchantment>("Standard");
}
