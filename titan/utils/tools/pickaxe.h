/// @file titan/utils/tools/pickaxe.h
/// @brief Live pickaxe eligibility and possession checks ported from PaistiUtils Pickaxe.

#pragma once

#include "../../client.h"
#include "../../generated/gamevals/item_id.h"
#include "../../generated/gamevals/quest_id.h"
#include "../../generated/gamevals/quest_id_table.h"
#include "../../inventory_id.h"

#include <cstdint>
#include <initializer_list>

namespace titan::utils::Pickaxe {

namespace detail {

inline bool membersWorld() {
    const auto current = state::world::current();
    if (!current) return false;
    for (const auto& world : state::world::list()) {
        if (world.id == *current) return world.isMembers();
    }
    return false;
}

}  // namespace detail

/// Checks real Mining level, world membership, and the crystal-pickaxe quest gate, not possession.
/// Call with world-ready SDK state. Unknown, noted, and broken item IDs return false.
/// Unavailable state cannot satisfy a requirement. Supported variants follow PaistiUtils Pickaxe.
inline bool canUse(int32_t itemId) {
    using I = gamevals::ItemID;
    const auto mining = state::skills().real(Skill::MINING);
    // Explicit unnoted IDs preserve PaistiUtils Pickaxe's supported variants and exclude broken tools.
    switch (itemId) {
    case I::BRONZE_PICKAXE:
    case I::IRON_PICKAXE: return mining >= 1;
    case I::STEEL_PICKAXE: return mining >= 6;
    case I::BLACK_PICKAXE: return mining >= 11;
    case I::MITHRIL_PICKAXE: return mining >= 21;
    case I::ADAMANT_PICKAXE: return mining >= 31;
    case I::RUNE_PICKAXE: return mining >= 41;
    case I::TRAIL_GILDED_PICKAXE: return mining >= 41 && detail::membersWorld();
    case I::DRAGON_PICKAXE:
    case I::ZALCANO_PICKAXE:
    case I::TRAILBLAZER_PICKAXE_NO_INFERNAL:
    case I::INFERNAL_PICKAXE:
    case I::TRAILBLAZER_PICKAXE:
    case I::TRAILBLAZER_RELOADED_PICKAXE:
    case I::INFERNAL_PICKAXE_EMPTY:
    case I::TRAILBLAZER_PICKAXE_EMPTY:
    case I::TRAILBLAZER_RELOADED_PICKAXE_EMPTY:
        return mining >= 61 && detail::membersWorld();
    case I::CRYSTAL_PICKAXE:
    case I::GAUNTLET_PICKAXE:
    case I::CRYSTAL_PICKAXE_INACTIVE: {
        if (mining < 71 || !detail::membersWorld()) return false;
        // The script API takes the quest's DB row ID, not the generated native quest ID.
        const auto* quest = gamevals::QuestID::byQuestId(gamevals::QuestID::QUEST_SONGOFTHEELVES);
        return quest && state::script().questState(quest->rowId) == 2;
    }
    default: return false;
    }
}

/// Whether inventory or equipment contains a positive-quantity pickaxe satisfying canUse().
/// Reads current SDK containers; unavailable containers cannot establish possession.
inline bool hasUsablePickaxe() {
    for (const auto containerId : {InventoryID::INVENTORY, InventoryID::EQUIPMENT}) {
        const auto container = state::itemContainer(static_cast<int32_t>(containerId));
        if (!container) continue;
        for (const auto& item : container->items) {
            if (item.quantity > 0 && canUse(item.itemId)) return true;
        }
    }
    return false;
}

}  // namespace titan::utils::Pickaxe
