/// @file titan/utils/tools/axe.h
/// @brief Live woodcutting-axe eligibility and possession checks ported from PaistiUtils Axe.

#pragma once

#include "../../client.h"
#include "../../generated/gamevals/item_id.h"
#include "../../generated/gamevals/quest_id.h"
#include "../../generated/gamevals/quest_id_table.h"
#include "../../inventory_id.h"

#include <cstdint>
#include <initializer_list>

namespace titan::utils::Axe {

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

/// Checks real Woodcutting level, world membership, and the crystal-axe quest gate, not possession.
/// Call with world-ready SDK state. Unknown, noted, and broken item IDs return false.
/// Supported variants follow PaistiUtils Axe, including felling axes but excluding uncharged infernal axes.
/// Unavailable state cannot satisfy a requirement; canUse does not check wielding levels or felling bonuses.
inline bool canUse(int32_t itemId) {
    using I = gamevals::ItemID;
    const auto woodcutting = state::skills().real(Skill::WOODCUTTING);
    // Explicit unnoted IDs distinguish woodcutting axes from battleaxes, broken tools, and notes.
    switch (itemId) {
    case I::BRONZE_AXE:
    case I::IRON_AXE: return woodcutting >= 1;
    case I::STEEL_AXE: return woodcutting >= 6;
    case I::BLACK_AXE: return woodcutting >= 11;
    case I::MITHRIL_AXE: return woodcutting >= 21;
    case I::ADAMANT_AXE: return woodcutting >= 31;
    case I::RUNE_AXE: return woodcutting >= 41;
    case I::RUNE_AXE_2H: return woodcutting >= 41 && detail::membersWorld();
    case I::DRAGON_AXE:
    case I::TRAILBLAZER_AXE_NO_INFERNAL:
    case I::TRAILBLAZER_RELOADED_AXE_NO_INFERNAL:
    case I::INFERNAL_AXE:
    case I::TRAILBLAZER_AXE:
    case I::TRAILBLAZER_RELOADED_AXE:
    case I::DRAGON_AXE_2H:
        return woodcutting >= 61 && detail::membersWorld();
    case I::CRYSTAL_AXE:
    case I::GAUNTLET_AXE:
    case I::CRYSTAL_AXE_INACTIVE:
    case I::CRYSTAL_AXE_2H:
    case I::CRYSTAL_AXE_2H_INACTIVE: {
        if (woodcutting < 71 || !detail::membersWorld()) return false;
        // The script API takes the quest's DB row ID, not the generated native quest ID.
        const auto* quest = gamevals::QuestID::byQuestId(gamevals::QuestID::QUEST_SONGOFTHEELVES);
        return quest && state::script().questState(quest->rowId) == 2;
    }
    default: return false;
    }
}

/// Whether inventory or equipment contains a positive-quantity axe satisfying canUse().
/// Reads current SDK containers; unavailable containers cannot establish possession.
inline bool hasUsableAxe() {
    for (const auto containerId : {InventoryID::INVENTORY, InventoryID::EQUIPMENT}) {
        const auto container = state::itemContainer(static_cast<int32_t>(containerId));
        if (!container) continue;
        for (const auto& item : container->items) {
            if (item.quantity > 0 && canUse(item.itemId)) return true;
        }
    }
    return false;
}

}  // namespace titan::utils::Axe
