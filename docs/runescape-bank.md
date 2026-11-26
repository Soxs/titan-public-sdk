# Bank destinations (C++)

`<titan/runescape_bank.h>` provides the 102 named destinations from PaistiUtils
`PathFinding/Locations/RunescapeBank.java`, in their original order.

```cpp
#include <titan/runescape_bank.h>

using titan::RunescapeBank;
namespace Banks = titan::RunescapeBankInfo;

const auto location = Banks::getLocation(RunescapeBank::GRAND_EXCHANGE);
// With world-ready client state:
const bool eligible = Banks::canUse(RunescapeBank::FARMING_GUILD);
const auto usable = Banks::getUsableBanks();
const auto groundFloor = Banks::getUsableBanks([](RunescapeBank bank) {
    return Banks::getLocation(bank)->z == 0;
});

for (const auto& entry : Banks::kBanks) {
    // entry.bank identifies the destination; entry.location is its WorldPoint.
}
```

## Contract

- `kBanks` is immutable catalog data. Coordinates explicitly belong to
  `WorldView::TOP_LEVEL`, not a current sailing WorldView. Enum ordinals are
  not stable storage or ABI identifiers.
- `getLocation(bank)` returns `std::optional<WorldPoint>`; invalid enum values
  return `nullopt`. Coordinate lookup needs no client state.
- `canUse(bank)` evaluates current real skill levels, quests, varbits/varps,
  world membership, inventory/equipment, and player position as required by
  that entry. Results are not cached. Call with world-ready client state.
- `getUsableBanks()` returns enum values in catalog order. Its predicate
  overload applies an additional caller filter only to eligible entries.
- Invalid enum values return false. Unavailable quest/world/item/player data
  cannot satisfy the corresponding check. Quest errors do not count as a
  started quest. Var/skill readers retain their existing SDK fallback behavior.

Quest checks translate generated native `gamevals::QuestID` values to the
DB row IDs consumed by `state::script().questState()`. Lumbridge basement
checks the **RFD intro subquest**, not completion of all Recipe for Disaster.

## Source limitations

This is a port of the supplied predicates, not a verified exhaustive list of
every bank or every access requirement. In particular:

- Entries without predicates always return true, even without a backend.
  Members-world checks are not universal.
- Crafting Guild needs 99 Crafting or the hard or elite Falador diary. Skill
  gates use real levels, not boosts.
- Motherlode Mine delegates to `titan::utils::Pickaxe::hasUsablePickaxe()` in
  `<titan/utils/tools/pickaxe.h>` for supported IDs, Mining levels, membership, and
  the crystal-pickaxe quest gate, or allows being
  within nine tiles of its entrance point on the same plane. The pickaxe list
  is not an exhaustive catalog of all tools in the game.
- Passing a predicate does not prove that a route exists, the bank is in the
  loaded scene, or a banking interaction will succeed.

`BankSets` remains the bank **object-ID** catalog. The destination catalog
does not change `utils::Bank::open()` or automatically walk to a bank.
This implementation is header-only C++; it adds no host ABI or Java/JS binding.

Run the standalone SDK tests with CMake:

```sh
cmake -S tests -B build/sdk-tests -A x64
cmake --build build/sdk-tests --config Debug
ctest --test-dir build/sdk-tests -C Debug --output-on-failure
```
