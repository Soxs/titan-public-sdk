# Rune-element and rune-pouch utilities

The two rune modules live under `titan/utils/runes/`:

- `rune_pouch.h`: pouch types, capacity, possession, advisory ownership, and
  decoded contents as rune item IDs and quantities.
- `rune_element.h`: `titan::RuneElement`, rune and equipment catalogs, and
  element quantities from inventory, equipment, and carried pouch contents.

RuneElement consumes RunePouch data; RunePouch has no dependency on RuneElement.

```cpp
#include <titan/utils/runes/rune_pouch.h>
#include <titan/utils/runes/rune_element.h>

auto contents = titan::utils::RunePouch::getRunePouchContents();
auto carriedType = titan::utils::RunePouch::getCarriedRunePouchType();
bool ownsPouch = titan::utils::RunePouch::accountOwnsRunePouch();

if (ownsPouch) {
    // Inventory, the open bank, or remembered bank contents contain a pouch.
}

auto pouchAmounts = titan::utils::RuneElement::getAvailableRuneElementAmountsFromCarriedRunePouch();
auto totalAmounts = titan::utils::RuneElement::getCarriedRuneElementAmounts();
```

## Contracts

- `RunePouchType` distinguishes `REGULAR` (three slots) and `DIVINE` (four).
  Locked variants have the same capacity. If supplied inventory data contains
  both types, divine wins; stored contents are counted only once.
  `RunePouch::capacity(type)` and `isValidSlot(type, slot)` own these rules;
  invalid types have no usable slots.
- `RunePouchContents` is a vector of `ItemContainerSlot`, containing occupied
  slots with their original zero-based pouch indices, decoded rune item IDs,
  and positive quantities. Contents do not establish possession. All four
  stored slots are read even without a carried pouch or on an F2P world.
- `accountOwnsRunePouch()` returns a plain bool. A positive observation in
  inventory, the open bank, or the host's remembered bank is sufficient, even
  when a refresh is in progress or the open bank is empty. False means no pouch
  was observed, not proven absence. Remembered positives can remain stale until
  the host refreshes its account cache. Placeholders and nonpositive quantities
  do not establish ownership.
- `getCarriedRunePouchType()` returns nullopt when no pouch is detected, including
  unavailable inventory. Possession and ownership do not require membership.
- Rune-element availability requires a members world and a carried pouch.
  Regular pouches ignore stored slot four. Combination runes contribute to each
  represented element; finite supplies are added before equipment's infinite
  amounts override them.

Supplied-item overloads accept `std::span<const ItemContainerSlot>` and perform
no SDK reads, so request-only pathfinding can use them without a client host.
Ownership's two-span overload checks the supplied inventory and bank snapshots.
The pouch-amount overload accepts `(inventoryItems, pouchContents, membersWorld)`.
The combined overload accepts `(inventoryItems, equippedItems, pouchContents,
membersWorld)`. The three-argument combined overload has no pouch snapshot
and counts only loose inventory runes and equipped providers.

## Source migration

The former top-level rune headers and `utils/runes.h` / `utils/rune_pouch.h`
are replaced by the two headers above. Rune catalog, equipment catalog, and
quantity functions now share `titan::utils::RuneElement`; their function names
are unchanged. `titan::RuneElement`, `RunePouchType`, and `RunePouchContents`
retain their type names. Pouch functions remain under `titan::utils::RunePouch`,
with `accountOwnsRunePouch()` returning bool instead of optional bool.

## Encoding and limits

The type values 1–23 follow the Java reference's rune ordering, cross-checked
against [RuneLite's pinned rune-image ordering and enum-982 usage][1]. Rune
items and the eight slot varbits use the SDK's generated named constants.
The SDK has no runtime cache-enum lookup, so decoding uses a static table.
Zero, unknown type values, and nonpositive quantities are omitted.

The SDK's varbit reader has no success flag: unreadable contents may appear
empty, including through the optional live amount getters. These reads are
sequential, not an atomic inventory/world/varbit snapshot. The three/four-slot
model does not include RuneLite's additional fifth and sixth slot varbits.
No live game-client execution has been verified.

[1]: https://github.com/runelite/runelite/blob/42a6f17a6a2e8e478aa763890ecd0181a59dad38/runelite-client/src/main/java/net/runelite/client/plugins/runepouch/RunepouchOverlay.java
