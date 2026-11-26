/// @file titan/utils/runes/rune_element.h
/// @brief Rune-element catalogs and quantities supplied by inventory, equipment, and carried pouches.

#pragma once

#include "../../client.h"
#include "../../equipment_slot.h"
#include "../../generated/gamevals/item_id.h"
#include "../../inventory_id.h"
#include "rune_pouch.h"

#include <algorithm>
#include <array>
#include <cstdint>
#include <limits>
#include <map>
#include <optional>
#include <span>
#include <vector>

namespace titan {

/// Spell-resource kinds, independent of the item supplying them.
/// A combination rune supplies multiple kinds; sunfire supplies FIRE rather than a separate kind.
/// Enum values are not item IDs or rune-pouch type encodings.
enum class RuneElement : int32_t {
    AIR,
    WATER,
    EARTH,
    FIRE,
    MIND,
    BODY,
    COSMIC,
    CHAOS,
    NATURE,
    LAW,
    DEATH,
    ASTRAL,
    BLOOD,
    SOUL,
    WRATH,
};

}  // namespace titan

/// Catalog lookups and supplied-snapshot calculations do not read SDK state; no-argument overloads do.
/// Element supplies overlap for combination runes and are not independent spell budgets.
namespace titan::utils::RuneElement {

namespace detail {

// Explicit static arrays avoid MSVC's runtime initialization issues with nested constexpr initializer_lists.
template <::titan::RuneElement... Elements>
inline constexpr std::array<::titan::RuneElement, sizeof...(Elements)> kProvidedElements{Elements...};

// Reverse-lookup spans need the same static backing lifetime as forward element spans.
template <int32_t... ItemIds>
inline constexpr std::array<int32_t, sizeof...(ItemIds)> kRuneIds{ItemIds...};

}  // namespace detail

/// One rune item's element coverage and source-item membership restriction.
/// Catalog entries borrow immutable, static-lifetime element storage.
struct RuneDefinition {
    int32_t itemId;
    std::span<const ::titan::RuneElement> elements;
    bool membersOnly = false; ///< Exclude this source item when calculating supply on an F2P world.
};

/// Reverse mapping from an element to its primary rune and alternative rune items.
/// Catalog spans borrow immutable, static-lifetime item-ID storage.
struct ElementDefinition {
    ::titan::RuneElement element;
    int32_t primitiveRuneId; ///< Ordinary single-element rune, excluding variants such as sunfire.
    std::span<const int32_t> combinationRuneIds; ///< Substitutes that also supply other elements.
    // Single-element substitutes, such as sunfire, are not combination runes.
    std::span<const int32_t> variantRuneIds;
};

/// Supported rune items, including combinations and single-element variants; item IDs are unique.
inline constexpr std::array<RuneDefinition, 23> kRunes = {{
    {gamevals::ItemID::AIRRUNE,     detail::kProvidedElements<::titan::RuneElement::AIR>},
    {gamevals::ItemID::WATERRUNE,   detail::kProvidedElements<::titan::RuneElement::WATER>},
    {gamevals::ItemID::EARTHRUNE,   detail::kProvidedElements<::titan::RuneElement::EARTH>},
    {gamevals::ItemID::FIRERUNE,    detail::kProvidedElements<::titan::RuneElement::FIRE>},
    {gamevals::ItemID::MINDRUNE,    detail::kProvidedElements<::titan::RuneElement::MIND>},
    {gamevals::ItemID::BODYRUNE,    detail::kProvidedElements<::titan::RuneElement::BODY>},
    {gamevals::ItemID::COSMICRUNE,  detail::kProvidedElements<::titan::RuneElement::COSMIC>},
    {gamevals::ItemID::CHAOSRUNE,   detail::kProvidedElements<::titan::RuneElement::CHAOS>},
    {gamevals::ItemID::NATURERUNE,  detail::kProvidedElements<::titan::RuneElement::NATURE>},
    {gamevals::ItemID::LAWRUNE,     detail::kProvidedElements<::titan::RuneElement::LAW>},
    {gamevals::ItemID::DEATHRUNE,   detail::kProvidedElements<::titan::RuneElement::DEATH>},
    {gamevals::ItemID::ASTRALRUNE,  detail::kProvidedElements<::titan::RuneElement::ASTRAL>, true},
    {gamevals::ItemID::BLOODRUNE,   detail::kProvidedElements<::titan::RuneElement::BLOOD>, true},
    {gamevals::ItemID::SOULRUNE,    detail::kProvidedElements<::titan::RuneElement::SOUL>, true},
    {gamevals::ItemID::WRATHRUNE,   detail::kProvidedElements<::titan::RuneElement::WRATH>, true},
    {gamevals::ItemID::MISTRUNE,    detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::WATER>, true},
    {gamevals::ItemID::DUSTRUNE,    detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::EARTH>, true},
    {gamevals::ItemID::SMOKERUNE,   detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::FIRE>, true},
    {gamevals::ItemID::MUDRUNE,     detail::kProvidedElements<::titan::RuneElement::WATER, ::titan::RuneElement::EARTH>, true},
    {gamevals::ItemID::STEAMRUNE,   detail::kProvidedElements<::titan::RuneElement::WATER, ::titan::RuneElement::FIRE>, true},
    {gamevals::ItemID::LAVARUNE,    detail::kProvidedElements<::titan::RuneElement::EARTH, ::titan::RuneElement::FIRE>, true},
    {gamevals::ItemID::AETHERRUNE,  detail::kProvidedElements<::titan::RuneElement::COSMIC, ::titan::RuneElement::SOUL>, true},
    {gamevals::ItemID::SUNFIRERUNE, detail::kProvidedElements<::titan::RuneElement::FIRE>, true},
}};

/// One reverse-lookup entry per supported RuneElement; alternatives reference kRunes items.
inline constexpr std::array kElements = {
    ElementDefinition{::titan::RuneElement::AIR, gamevals::ItemID::AIRRUNE,
        detail::kRuneIds<gamevals::ItemID::MISTRUNE, gamevals::ItemID::DUSTRUNE, gamevals::ItemID::SMOKERUNE>, {}},
    ElementDefinition{::titan::RuneElement::WATER, gamevals::ItemID::WATERRUNE,
        detail::kRuneIds<gamevals::ItemID::MISTRUNE, gamevals::ItemID::MUDRUNE, gamevals::ItemID::STEAMRUNE>, {}},
    ElementDefinition{::titan::RuneElement::EARTH, gamevals::ItemID::EARTHRUNE,
        detail::kRuneIds<gamevals::ItemID::DUSTRUNE, gamevals::ItemID::MUDRUNE, gamevals::ItemID::LAVARUNE>, {}},
    ElementDefinition{::titan::RuneElement::FIRE, gamevals::ItemID::FIRERUNE,
        detail::kRuneIds<gamevals::ItemID::SMOKERUNE, gamevals::ItemID::STEAMRUNE, gamevals::ItemID::LAVARUNE>,
        detail::kRuneIds<gamevals::ItemID::SUNFIRERUNE>},
    ElementDefinition{::titan::RuneElement::MIND, gamevals::ItemID::MINDRUNE, {}, {}},
    ElementDefinition{::titan::RuneElement::BODY, gamevals::ItemID::BODYRUNE, {}, {}},
    ElementDefinition{::titan::RuneElement::COSMIC, gamevals::ItemID::COSMICRUNE, detail::kRuneIds<gamevals::ItemID::AETHERRUNE>, {}},
    ElementDefinition{::titan::RuneElement::CHAOS, gamevals::ItemID::CHAOSRUNE, {}, {}},
    ElementDefinition{::titan::RuneElement::NATURE, gamevals::ItemID::NATURERUNE, {}, {}},
    ElementDefinition{::titan::RuneElement::LAW, gamevals::ItemID::LAWRUNE, {}, {}},
    ElementDefinition{::titan::RuneElement::DEATH, gamevals::ItemID::DEATHRUNE, {}, {}},
    ElementDefinition{::titan::RuneElement::ASTRAL, gamevals::ItemID::ASTRALRUNE, {}, {}},
    ElementDefinition{::titan::RuneElement::BLOOD, gamevals::ItemID::BLOODRUNE, {}, {}},
    ElementDefinition{::titan::RuneElement::SOUL, gamevals::ItemID::SOULRUNE, detail::kRuneIds<gamevals::ItemID::AETHERRUNE>, {}},
    ElementDefinition{::titan::RuneElement::WRATH, gamevals::ItemID::WRATHRUNE, {}, {}},
};

/// Returns every element supplied by a catalogued rune item, without membership filtering.
/// The view refers to static catalog data; unknown item IDs produce an empty view.
inline constexpr std::span<const ::titan::RuneElement> getRuneElementsProvidedByRune(int32_t itemId) {
    for (const auto& rune : kRunes) {
        if (rune.itemId == itemId) return rune.elements;
    }
    return {};
}

/// Whether an item ID is in the rune catalog, including combinations and single-element variants.
/// Unknown IDs return false; current world membership and possession are irrelevant.
inline constexpr bool isRune(int32_t itemId) {
    return !getRuneElementsProvidedByRune(itemId).empty();
}

/// Returns the ordinary single-element rune's item ID, not a combination or variant ID.
/// Invalid element values have no primary rune and return nullopt.
inline constexpr std::optional<int32_t> getPrimaryRuneForElement(::titan::RuneElement element) {
    for (const auto& entry : kElements) {
        if (entry.element == element) return entry.primitiveRuneId;
    }
    return std::nullopt;
}

/// Returns an owned list of all catalogued rune item IDs supplying the element, without membership filtering.
/// Primary rune first, then combinations and variants; invalid elements produce an empty list.
inline constexpr std::vector<int32_t> getAllRunesProvidingElement(::titan::RuneElement element) {
    for (const auto& entry : kElements) {
        if (entry.element != element) continue;
        std::vector<int32_t> result{entry.primitiveRuneId};
        result.insert(result.end(), entry.combinationRuneIds.begin(), entry.combinationRuneIds.end());
        result.insert(result.end(), entry.variantRuneIds.begin(), entry.variantRuneIds.end());
        return result;
    }
    return {};
}

/// Potential infinite-rune provider with the slot and world eligibility required to activate it.
/// Catalog element spans borrow immutable, static-lifetime storage.
struct EquipmentDefinition {
    int32_t itemId;
    EquipmentSlot slot; ///< Matching the item ID in a different equipment slot does not supply runes.
    std::span<const ::titan::RuneElement> elements;
    bool membersOnly = true; ///< Exclude this provider when calculating supply on an F2P world.
};

// TODO: Read weapon mappings once from cache enums 988/989/996/997; the SDK lacks runtime enum access.
// shortcut: revision-241 weapon data can miss new equipment, refresh until runtime enum access is available.
/// Supported weapon providers and charged tomes, with one entry per item ID; empty tomes are excluded.
inline constexpr std::array kEquipment = {
    EquipmentDefinition{gamevals::ItemID::STAFF_OF_AIR, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::AIR>, false},
    EquipmentDefinition{gamevals::ItemID::AIR_BATTLESTAFF, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::AIR>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_AIR_STAFF, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::AIR>},
    EquipmentDefinition{gamevals::ItemID::BR_AIR_STAFF, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::AIR>, false},
    EquipmentDefinition{gamevals::ItemID::STAFF_OF_WATER, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::WATER>, false},
    EquipmentDefinition{gamevals::ItemID::WATER_BATTLESTAFF, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::WATER>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_WATER_STAFF, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::WATER>},
    EquipmentDefinition{gamevals::ItemID::KODAI_WAND, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::WATER>},
    EquipmentDefinition{gamevals::ItemID::STAFF_OF_EARTH, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::EARTH>, false},
    EquipmentDefinition{gamevals::ItemID::EARTH_BATTLESTAFF, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::EARTH>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_EARTH_STAFF, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::EARTH>},
    EquipmentDefinition{gamevals::ItemID::STAFF_OF_FIRE, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::FIRE>, false},
    EquipmentDefinition{gamevals::ItemID::FIRE_BATTLESTAFF, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_FIRE_STAFF, EquipmentSlot::WEAPON, detail::kProvidedElements<::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::MIST_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::WATER>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_MIST_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::WATER>},
    EquipmentDefinition{gamevals::ItemID::DUST_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::EARTH>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_DUST_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::EARTH>},
    EquipmentDefinition{gamevals::ItemID::SMOKE_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_SMOKE_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::MUD_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::WATER, ::titan::RuneElement::EARTH>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_MUD_STAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::WATER, ::titan::RuneElement::EARTH>},
    EquipmentDefinition{gamevals::ItemID::STEAM_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::WATER, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_STEAM_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::WATER, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::STEAM_BATTLESTAFF_PRETTY, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::WATER, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_STEAM_BATTLESTAFF_PRETTY, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::WATER, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::TWINFLAME_STAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::WATER, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::LAVA_BATTLESTAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::EARTH, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_LAVA_STAFF, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::EARTH, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::LAVA_BATTLESTAFF_PRETTY, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::EARTH, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::MYSTIC_LAVA_STAFF_PRETTY, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::EARTH, ::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::SHADOWFLAME_QUADRANT, EquipmentSlot::WEAPON,
        detail::kProvidedElements<::titan::RuneElement::AIR, ::titan::RuneElement::WATER, ::titan::RuneElement::EARTH, ::titan::RuneElement::FIRE>},
    // magic_runecount checks charged tomes in the off-hand separately from the weapon enums.
    EquipmentDefinition{gamevals::ItemID::TOME_OF_FIRE, EquipmentSlot::SHIELD, detail::kProvidedElements<::titan::RuneElement::FIRE>},
    EquipmentDefinition{gamevals::ItemID::TOME_OF_WATER, EquipmentSlot::SHIELD, detail::kProvidedElements<::titan::RuneElement::WATER>},
    EquipmentDefinition{gamevals::ItemID::TOME_OF_EARTH, EquipmentSlot::SHIELD, detail::kProvidedElements<::titan::RuneElement::EARTH>},
};

/// Returns the elements an equipment item can supply as infinite rune amounts.
/// Static catalog membership only; callers must check the equipped slot and world restrictions.
/// The view has static lifetime; unknown IDs, including empty tomes, produce an empty view.
/// This lookup does not model spell-specific tome page consumption or damage bonuses.
inline constexpr std::span<const ::titan::RuneElement> getRuneElementsProvidedByEquipment(int32_t itemId) {
    for (const auto& equipment : kEquipment) {
        if (equipment.itemId == itemId) return equipment.elements;
    }
    return {};
}

/// Sentinel for equipment-provided infinite supply; ordinary available >= required comparisons work.
/// Arithmetic must preserve infinity explicitly: adding can overflow and subtracting loses the sentinel.
inline constexpr int64_t kInfiniteRuneAmount = (std::numeric_limits<int64_t>::max)();

/// Missing elements have zero supply. Combination-rune totals overlap and are not independent budgets.
/// Owned map of positive finite quantities or kInfiniteRuneAmount; finite sums saturate at one below infinity.
using RuneElementAmounts = std::map<::titan::RuneElement, int64_t>;

/// Counts loose rune stacks, crediting the full stack quantity to each represented element.
/// Ignores unknown items, nonpositive quantities, providers, and pouch contents; inventory slot indices are unused.
/// When membersWorld is false, members-only source runes are excluded even if their elements are F2P-usable.
inline RuneElementAmounts getAvailableRuneElementAmountsFromInventoryRunes(
        std::span<const ItemContainerSlot> inventoryItems, bool membersWorld) {
    RuneElementAmounts result;
    for (const auto& item : inventoryItems) {
        if (item.quantity <= 0) continue;
        for (const auto& rune : kRunes) {
            if (rune.itemId != item.itemId || (rune.membersOnly && !membersWorld)) continue;
            for (auto element : rune.elements) {
                auto& amount = result[element];
                // Finite stacks must never overflow or become the infinity sentinel.
                amount += (std::min)(int64_t{item.quantity}, kInfiniteRuneAmount - 1 - amount);
            }
            break;
        }
    }
    return result;
}

/// Returns kInfiniteRuneAmount for each element supplied by eligible equipped providers.
/// Item slot indices must match EquipmentSlot values and the provider's required slot; quantity must be positive.
/// Unknown IDs and empty tomes are ignored; members-only providers are excluded when membersWorld is false.
inline RuneElementAmounts getAvailableRuneElementAmountsFromEquippedItems(
        std::span<const ItemContainerSlot> equippedItems, bool membersWorld) {
    RuneElementAmounts result;
    for (const auto& item : equippedItems) {
        if (item.quantity <= 0) continue;
        for (const auto& equipment : kEquipment) {
            if (equipment.itemId != item.itemId || item.slot != static_cast<int32_t>(equipment.slot)
                || (equipment.membersOnly && !membersWorld)) continue;
            for (auto element : equipment.elements) result[element] = kInfiniteRuneAmount;
            break;
        }
    }
    return result;
}

/// Counts supplied pouch contents only when inventory contains a supported pouch and membersWorld is true.
/// pouchContents must use decoded rune item IDs and original zero-based pouch slot indices, not inventory indices.
/// Regular pouches accept slots 0-2, divine 0-3; divine wins if both types are carried and contents count only once.
/// Out-of-range slots, unknown rune IDs, and nonpositive quantities are ignored; combinations credit each element.
inline RuneElementAmounts getAvailableRuneElementAmountsFromCarriedRunePouch(
        std::span<const ItemContainerSlot> inventoryItems,
        std::span<const ItemContainerSlot> pouchContents, bool membersWorld) {
    if (!membersWorld) return {};
    const auto type = RunePouch::getCarriedRunePouchType(inventoryItems);
    if (!type) return {};
    RunePouchContents available;
    for (const auto& item : pouchContents) {
        if (RunePouch::isValidSlot(*type, item.slot)) available.push_back(item);
    }
    return getAvailableRuneElementAmountsFromInventoryRunes(available, membersWorld);
}

/// Combines supplied loose runes and usable pouch contents, then overrides elements supplied by equipment with infinity.
/// Equipment entries use EquipmentSlot indices; pouch entries use original zero-based pouch slot indices.
/// membersWorld filters member-only sources, including the entire pouch; finite totals saturate below infinity.
inline RuneElementAmounts getCarriedRuneElementAmounts(
        std::span<const ItemContainerSlot> inventoryItems,
        std::span<const ItemContainerSlot> equippedItems,
        std::span<const ItemContainerSlot> pouchContents, bool membersWorld) {
    auto result = getAvailableRuneElementAmountsFromInventoryRunes(inventoryItems, membersWorld);
    for (const auto& [element, amount] : getAvailableRuneElementAmountsFromCarriedRunePouch(inventoryItems, pouchContents, membersWorld)) {
        auto& total = result[element];
        total += (std::min)(amount, kInfiniteRuneAmount - 1 - total);
    }
    for (const auto& [element, amount] : getAvailableRuneElementAmountsFromEquippedItems(equippedItems, membersWorld)) {
        result[element] = amount;
    }
    return result;
}

/// Without a supplied pouch snapshot, only loose inventory runes and equipped providers contribute.
/// Performs no SDK reads; membersWorld filters source eligibility and equipped providers override finite totals.
inline RuneElementAmounts getCarriedRuneElementAmounts(
        std::span<const ItemContainerSlot> inventoryItems,
        std::span<const ItemContainerSlot> equippedItems, bool membersWorld) {
    return getCarriedRuneElementAmounts(inventoryItems, equippedItems, {}, membersWorld);
}

namespace detail {

// A missing current-world list entry is unknown, not evidence of an F2P world.
inline std::optional<bool> currentWorldIsMembers() {
    const auto currentWorld = state::world::current();
    if (!currentWorld) return std::nullopt;
    for (const auto& world : state::world::list()) {
        if (world.id == *currentWorld) return world.isMembers();
    }
    return std::nullopt;
}

}  // namespace detail

/// Counts loose runes using the SDK's current inventory and world membership; pouch supplies are excluded.
/// Returns nullopt when world membership or inventory is unavailable; a readable inventory with no eligible runes is empty.
inline std::optional<RuneElementAmounts> getAvailableRuneElementAmountsFromInventoryRunes() {
    const auto membersWorld = detail::currentWorldIsMembers();
    if (!membersWorld) return std::nullopt;
    const auto inventory = state::itemContainer(static_cast<int32_t>(InventoryID::INVENTORY));
    if (!inventory) return std::nullopt;
    return getAvailableRuneElementAmountsFromInventoryRunes(inventory->items, *membersWorld);
}

/// Resolves current equipment and world membership through the SDK, returning infinite amounts for eligible providers.
/// Returns nullopt when world membership or equipment is unavailable; no eligible providers produces an empty map.
inline std::optional<RuneElementAmounts> getAvailableRuneElementAmountsFromEquippedItems() {
    const auto membersWorld = detail::currentWorldIsMembers();
    if (!membersWorld) return std::nullopt;
    const auto equipment = state::itemContainer(static_cast<int32_t>(InventoryID::EQUIPMENT));
    if (!equipment) return std::nullopt;
    return getAvailableRuneElementAmountsFromEquippedItems(equipment->items, *membersWorld);
}

/// Resolves current inventory, pouch varbits, and world membership through the SDK.
/// A known F2P world returns an empty map without requiring inventory; a readable inventory without a pouch is also empty.
/// Returns nullopt for unknown world membership, or unavailable inventory on a members world.
/// Pouch contents inherit the varbit reader's inability to distinguish unreadable values from zero.
inline std::optional<RuneElementAmounts> getAvailableRuneElementAmountsFromCarriedRunePouch() {
    const auto membersWorld = detail::currentWorldIsMembers();
    if (!membersWorld) return std::nullopt;
    if (!*membersWorld) return RuneElementAmounts{};
    const auto inventory = state::itemContainer(static_cast<int32_t>(InventoryID::INVENTORY));
    if (!inventory) return std::nullopt;
    if (!RunePouch::getCarriedRunePouchType(inventory->items)) return RuneElementAmounts{};
    return getAvailableRuneElementAmountsFromCarriedRunePouch(inventory->items, RunePouch::getRunePouchContents(), true);
}

/// Includes carried pouch contents; unreadable pouch varbits may appear empty.
/// Reads current inventory, equipment, world membership, and eligible pouch contents through the SDK.
/// Returns nullopt if membership, inventory, or equipment is unavailable; known absence of supplies gives an empty map.
/// Eligible equipment overrides finite totals with infinity. Sequential SDK reads are not an atomic snapshot.
inline std::optional<RuneElementAmounts> getCarriedRuneElementAmounts() {
    const auto membersWorld = detail::currentWorldIsMembers();
    if (!membersWorld) return std::nullopt;
    const auto inventory = state::itemContainer(static_cast<int32_t>(InventoryID::INVENTORY));
    const auto equipment = state::itemContainer(static_cast<int32_t>(InventoryID::EQUIPMENT));
    if (!inventory || !equipment) return std::nullopt;
    const auto pouch = *membersWorld && RunePouch::getCarriedRunePouchType(inventory->items)
        ? RunePouch::getRunePouchContents() : RunePouchContents{};
    return getCarriedRuneElementAmounts(inventory->items, equipment->items, pouch, *membersWorld);
}

}  // namespace titan::utils::RuneElement
