/// @file titan/utils/magic.h
/// @brief Spellbook metadata and magic action helpers for plugins.
///
/// This header ports the TP_RL magic API catalog shape into Titan. Spell
/// metadata and state predicates are live; spell selection/casts dispatch
/// through the widget interaction backend. Targeted casts queue spell
/// selection and the follow-up target action together as one ordered input
/// sequence; the backend provides the required frame spacing.

#pragma once

#include "../actor.h"
#include "../client.h"
#include "../gamevals.h"
#include "../skill.h"
#include "../var_player.h"
#include "../varbits.h"

#include <chrono>
#include <cstdint>
#include <type_traits>

namespace titan {
namespace utils {
namespace Magic {

namespace detail {
constexpr int32_t kNoParam = -1;
constexpr const char* kCastActionText = "Cast";
constexpr const char* kBlankTargetText = "";

inline bool spellWidgetInteract(uint32_t widget, int32_t identifier, MenuAction::Id opcode) {
    if (widget == 0u) return false;
    return ::titan::state::widgets().interact(
        static_cast<uint32_t>(opcode), identifier, kNoParam,
        static_cast<int32_t>(widget));
}

inline bool dispatchTarget(MenuAction::Entry entry) {
    entry.actionText = kCastActionText;
    entry.targetText = kBlankTargetText;
    return ::titan::state::client().invokeMenuAction(entry);
}

inline bool dispatchWidgetTarget(int32_t childIndex, int32_t packedId) {
    if (packedId == 0) return false;
    return ::titan::state::widgets().interact(
        static_cast<uint32_t>(MenuAction::Id::WidgetTargetOnWidget),
        0,
        childIndex,
        packedId);
}

inline MenuAction::Entry targetEntry(MenuAction::Id opcode, int32_t identifier,
                                     int32_t param0, int32_t param1,
                                     int32_t worldViewId = -1) {
    MenuAction::Entry entry{};
    entry.opcode = opcode;
    entry.identifier = identifier;
    entry.param0 = param0;
    entry.param1 = param1;
    entry.worldViewId = worldViewId;
    return entry;
}

}

enum class SpellBook : int32_t {
    Standard = 0,
    Ancient = 1,
    Lunar = 2,
    Necromancy = 3
};

struct SpellInfo {
    const char* name;
    int level;
    uint32_t widget;
    SpellBook book;
    bool members;
    int menuEntryId;
};

struct WidgetTarget {
    uint32_t packedId = 0;
    int32_t childIndex = -1;
    int32_t itemId = -1;
};

enum class Standard : int32_t {
    HOME_TELEPORT = 0,
    VARROCK_TELEPORT,
    LUMBRIDGE_TELEPORT,
    FALADOR_TELEPORT,
    TELEPORT_TO_HOUSE,
    CAMELOT_TELEPORT,
    ARDOUGNE_TELEPORT,
    WATCHTOWER_TELEPORT,
    TROLLHEIM_TELEPORT,
    TELEPORT_TO_APE_ATOLL,
    TELEPORT_TO_KOUREND,
    TELEOTHER_LUMBRIDGE,
    TELEOTHER_FALADOR,
    TELEPORT_TO_BOUNTY_TARGET,
    TELEOTHER_CAMELOT,
    WIND_STRIKE,
    WATER_STRIKE,
    EARTH_STRIKE,
    FIRE_STRIKE,
    WIND_BOLT,
    WATER_BOLT,
    EARTH_BOLT,
    FIRE_BOLT,
    WIND_BLAST,
    WATER_BLAST,
    EARTH_BLAST,
    FIRE_BLAST,
    WIND_WAVE,
    WATER_WAVE,
    EARTH_WAVE,
    FIRE_WAVE,
    WIND_SURGE,
    WATER_SURGE,
    EARTH_SURGE,
    FIRE_SURGE,
    SARADOMIN_STRIKE,
    CLAWS_OF_GUTHIX,
    FLAMES_OF_ZAMORAK,
    CRUMBLE_UNDEAD,
    IBAN_BLAST,
    MAGIC_DART,
    CONFUSE,
    WEAKEN,
    CURSE,
    BIND,
    SNARE,
    VULNERABILITY,
    ENFEEBLE,
    ENTANGLE,
    STUN,
    TELE_BLOCK,
    CHARGE,
    BONES_TO_BANANAS,
    LOW_LEVEL_ALCHEMY,
    SUPERHEAT_ITEM,
    HIGH_LEVEL_ALCHEMY,
    BONES_TO_PEACHES,
    LVL_1_ENCHANT,
    LVL_2_ENCHANT,
    LVL_3_ENCHANT,
    CHARGE_WATER_ORB,
    LVL_4_ENCHANT,
    CHARGE_EARTH_ORB,
    CHARGE_FIRE_ORB,
    CHARGE_AIR_ORB,
    LVL_5_ENCHANT,
    LVL_6_ENCHANT,
    LVL_7_ENCHANT,
    TELEKINETIC_GRAB
};

enum class Ancient : int32_t {
    EDGEVILLE_HOME_TELEPORT = 0,
    PADDEWWA_TELEPORT,
    SENNTISTEN_TELEPORT,
    KHARYRLL_TELEPORT,
    LASSAR_TELEPORT,
    DAREEYAK_TELEPORT,
    CARRALLANGER_TELEPORT,
    BOUNTY_TARGET_TELEPORT,
    ANNAKARL_TELEPORT,
    GHORROCK_TELEPORT,
    SMOKE_RUSH,
    SHADOW_RUSH,
    BLOOD_RUSH,
    ICE_RUSH,
    SMOKE_BURST,
    SHADOW_BURST,
    BLOOD_BURST,
    ICE_BURST,
    SMOKE_BLITZ,
    SHADOW_BLITZ,
    BLOOD_BLITZ,
    ICE_BLITZ,
    SMOKE_BARRAGE,
    SHADOW_BARRAGE,
    BLOOD_BARRAGE,
    ICE_BARRAGE
};

enum class Lunar : int32_t {
    LUNAR_HOME_TELEPORT = 0,
    MOONCLAN_TELEPORT,
    TELE_GROUP_MOONCLAN,
    OURANIA_TELEPORT,
    WATERBIRTH_TELEPORT,
    TELE_GROUP_WATERBIRTH,
    BARBARIAN_TELEPORT,
    TELE_GROUP_BARBARIAN,
    KHAZARD_TELEPORT,
    TELE_GROUP_KHAZARD,
    FISHING_GUILD_TELEPORT,
    TELE_GROUP_FISHING_GUILD,
    CATHERBY_TELEPORT,
    TELE_GROUP_CATHERBY,
    ICE_PLATEAU_TELEPORT,
    TELE_GROUP_ICE_PLATEAU,
    MONSTER_EXAMINE,
    CURE_OTHER,
    CURE_ME,
    CURE_GROUP,
    STAT_SPY,
    DREAM,
    STAT_RESTORE_POT_SHARE,
    BOOST_POTION_SHARE,
    ENERGY_TRANSFER,
    HEAL_OTHER,
    VENGEANCE_OTHER,
    VENGEANCE,
    HEAL_GROUP,
    BAKE_PIE,
    GEOMANCY,
    CURE_PLANT,
    NPC_CONTACT,
    HUMIDIFY,
    HUNTER_KIT,
    SPIN_FLAX,
    SUPERGLASS_MAKE,
    TAN_LEATHER,
    STRING_JEWELLERY,
    MAGIC_IMBUE,
    FERTILE_SOIL,
    PLANK_MAKE,
    RECHARGE_DRAGONSTONE,
    SPELLBOOK_SWAP
};

enum class Necromancy : int32_t {
    ARCEUUS_HOME_TELEPORT = 0,
    ARCEUUS_LIBRARY_TELEPORT,
    DRAYNOR_MANOR_TELEPORT,
    BATTLEFRONT_TELEPORT,
    MIND_ALTAR_TELEPORT,
    RESPAWN_TELEPORT,
    SALVE_GRAVEYARD_TELEPORT,
    FENKENSTRAINS_CASTLE_TELEPORT,
    WEST_ARDOUGNE_TELEPORT,
    HARMONY_ISLAND_TELEPORT,
    CEMETERY_TELEPORT,
    BARROWS_TELEPORT,
    APE_ATOLL_TELEPORT,
    GHOSTLY_GRASP,
    SKELETAL_GRASP,
    UNDEAD_GRASP,
    INFERIOR_DEMONBANE,
    SUPERIOR_DEMONBANE,
    DARK_DEMONBANE,
    LESSER_CORRUPTION,
    GREATER_CORRUPTION,
    RESURRECT_LESSER_GHOST,
    RESURRECT_LESSER_SKELETON,
    RESURRECT_LESSER_ZOMBIE,
    RESURRECT_SUPERIOR_GHOST,
    RESURRECT_SUPERIOR_SKELETON,
    RESURRECT_SUPERIOR_ZOMBIE,
    RESURRECT_GREATER_GHOST,
    RESURRECT_GREATER_SKELETON,
    RESURRECT_GREATER_ZOMBIE,
    DARK_LURE,
    MARK_OF_DARKNESS,
    WARD_OF_ARCEUUS,
    BASIC_REANIMATION,
    ADEPT_REANIMATION,
    EXPERT_REANIMATION,
    MASTER_REANIMATION,
    DEMONIC_OFFERING,
    SINISTER_OFFERING,
    SHADOW_VEIL,
    VILE_VIGOUR,
    DEGRIME,
    RESURRECT_CROPS,
    DEATH_CHARGE
};

template <typename T>
concept SpellEnum = std::is_same_v<T, Standard> || std::is_same_v<T, Ancient> ||
                    std::is_same_v<T, Lunar> || std::is_same_v<T, Necromancy>;

inline constexpr SpellInfo info(Standard spell) {
    switch (spell) {
        case Standard::HOME_TELEPORT: return {"HOME_TELEPORT", 0, gamevals::InterfaceID::MagicSpellbook::TELEPORT_HOME_STANDARD, SpellBook::Standard, false, 0};
        case Standard::VARROCK_TELEPORT: return {"VARROCK_TELEPORT", 25, gamevals::InterfaceID::MagicSpellbook::VARROCK_TELEPORT, SpellBook::Standard, false, 0};
        case Standard::LUMBRIDGE_TELEPORT: return {"LUMBRIDGE_TELEPORT", 31, gamevals::InterfaceID::MagicSpellbook::LUMBRIDGE_TELEPORT, SpellBook::Standard, false, 0};
        case Standard::FALADOR_TELEPORT: return {"FALADOR_TELEPORT", 37, gamevals::InterfaceID::MagicSpellbook::FALADOR_TELEPORT, SpellBook::Standard, false, 0};
        case Standard::TELEPORT_TO_HOUSE: return {"TELEPORT_TO_HOUSE", 40, gamevals::InterfaceID::MagicSpellbook::TELEPORT_TO_YOUR_HOUSE, SpellBook::Standard, true, 0};
        case Standard::CAMELOT_TELEPORT: return {"CAMELOT_TELEPORT", 45, gamevals::InterfaceID::MagicSpellbook::CAMELOT_TELEPORT, SpellBook::Standard, true, 0};
        case Standard::ARDOUGNE_TELEPORT: return {"ARDOUGNE_TELEPORT", 51, gamevals::InterfaceID::MagicSpellbook::ARDOUGNE_TELEPORT, SpellBook::Standard, true, 0};
        case Standard::WATCHTOWER_TELEPORT: return {"WATCHTOWER_TELEPORT", 58, gamevals::InterfaceID::MagicSpellbook::WATCHTOWER_TELEPORT, SpellBook::Standard, true, 0};
        case Standard::TROLLHEIM_TELEPORT: return {"TROLLHEIM_TELEPORT", 61, gamevals::InterfaceID::MagicSpellbook::TROLLHEIM_TELEPORT, SpellBook::Standard, true, 0};
        case Standard::TELEPORT_TO_APE_ATOLL: return {"TELEPORT_TO_APE_ATOLL", 64, gamevals::InterfaceID::MagicSpellbook::APE_TELEPORT, SpellBook::Standard, true, 0};
        case Standard::TELEPORT_TO_KOUREND: return {"TELEPORT_TO_KOUREND", 69, gamevals::InterfaceID::MagicSpellbook::KOUREND_TELEPORT, SpellBook::Standard, true, 0};
        case Standard::TELEOTHER_LUMBRIDGE: return {"TELEOTHER_LUMBRIDGE", 74, gamevals::InterfaceID::MagicSpellbook::TELEOTHER_LUMBRIDGE, SpellBook::Standard, true, 0};
        case Standard::TELEOTHER_FALADOR: return {"TELEOTHER_FALADOR", 82, gamevals::InterfaceID::MagicSpellbook::TELEOTHER_FALADOR, SpellBook::Standard, true, 0};
        case Standard::TELEPORT_TO_BOUNTY_TARGET: return {"TELEPORT_TO_BOUNTY_TARGET", 85, gamevals::InterfaceID::MagicSpellbook::BOUNTY_TARGET, SpellBook::Standard, true, 0};
        case Standard::TELEOTHER_CAMELOT: return {"TELEOTHER_CAMELOT", 90, gamevals::InterfaceID::MagicSpellbook::TELEOTHER_CAMELOT, SpellBook::Standard, true, 0};
        case Standard::WIND_STRIKE: return {"WIND_STRIKE", 1, gamevals::InterfaceID::MagicSpellbook::WIND_STRIKE, SpellBook::Standard, false, 0};
        case Standard::WATER_STRIKE: return {"WATER_STRIKE", 5, gamevals::InterfaceID::MagicSpellbook::WATER_STRIKE, SpellBook::Standard, false, 0};
        case Standard::EARTH_STRIKE: return {"EARTH_STRIKE", 9, gamevals::InterfaceID::MagicSpellbook::EARTH_STRIKE, SpellBook::Standard, false, 0};
        case Standard::FIRE_STRIKE: return {"FIRE_STRIKE", 13, gamevals::InterfaceID::MagicSpellbook::FIRE_STRIKE, SpellBook::Standard, false, 0};
        case Standard::WIND_BOLT: return {"WIND_BOLT", 17, gamevals::InterfaceID::MagicSpellbook::WIND_BOLT, SpellBook::Standard, false, 0};
        case Standard::WATER_BOLT: return {"WATER_BOLT", 23, gamevals::InterfaceID::MagicSpellbook::WATER_BOLT, SpellBook::Standard, false, 0};
        case Standard::EARTH_BOLT: return {"EARTH_BOLT", 29, gamevals::InterfaceID::MagicSpellbook::EARTH_BOLT, SpellBook::Standard, false, 0};
        case Standard::FIRE_BOLT: return {"FIRE_BOLT", 35, gamevals::InterfaceID::MagicSpellbook::FIRE_BOLT, SpellBook::Standard, false, 0};
        case Standard::WIND_BLAST: return {"WIND_BLAST", 41, gamevals::InterfaceID::MagicSpellbook::WIND_BLAST, SpellBook::Standard, false, 0};
        case Standard::WATER_BLAST: return {"WATER_BLAST", 47, gamevals::InterfaceID::MagicSpellbook::WATER_BLAST, SpellBook::Standard, false, 0};
        case Standard::EARTH_BLAST: return {"EARTH_BLAST", 53, gamevals::InterfaceID::MagicSpellbook::EARTH_BLAST, SpellBook::Standard, false, 0};
        case Standard::FIRE_BLAST: return {"FIRE_BLAST", 59, gamevals::InterfaceID::MagicSpellbook::FIRE_BLAST, SpellBook::Standard, false, 0};
        case Standard::WIND_WAVE: return {"WIND_WAVE", 62, gamevals::InterfaceID::MagicSpellbook::WIND_WAVE, SpellBook::Standard, true, 0};
        case Standard::WATER_WAVE: return {"WATER_WAVE", 65, gamevals::InterfaceID::MagicSpellbook::WATER_WAVE, SpellBook::Standard, true, 0};
        case Standard::EARTH_WAVE: return {"EARTH_WAVE", 70, gamevals::InterfaceID::MagicSpellbook::EARTH_WAVE, SpellBook::Standard, true, 0};
        case Standard::FIRE_WAVE: return {"FIRE_WAVE", 75, gamevals::InterfaceID::MagicSpellbook::FIRE_WAVE, SpellBook::Standard, true, 0};
        case Standard::WIND_SURGE: return {"WIND_SURGE", 81, gamevals::InterfaceID::MagicSpellbook::WIND_SURGE, SpellBook::Standard, true, 0};
        case Standard::WATER_SURGE: return {"WATER_SURGE", 85, gamevals::InterfaceID::MagicSpellbook::WATER_SURGE, SpellBook::Standard, true, 0};
        case Standard::EARTH_SURGE: return {"EARTH_SURGE", 90, gamevals::InterfaceID::MagicSpellbook::EARTH_SURGE, SpellBook::Standard, true, 0};
        case Standard::FIRE_SURGE: return {"FIRE_SURGE", 95, gamevals::InterfaceID::MagicSpellbook::FIRE_SURGE, SpellBook::Standard, true, 0};
        case Standard::SARADOMIN_STRIKE: return {"SARADOMIN_STRIKE", 60, gamevals::InterfaceID::MagicSpellbook::SARADOMIN_STRIKE, SpellBook::Standard, true, 0};
        case Standard::CLAWS_OF_GUTHIX: return {"CLAWS_OF_GUTHIX", 60, gamevals::InterfaceID::MagicSpellbook::CLAWS_OF_GUTHIX, SpellBook::Standard, true, 0};
        case Standard::FLAMES_OF_ZAMORAK: return {"FLAMES_OF_ZAMORAK", 60, gamevals::InterfaceID::MagicSpellbook::FLAMES_OF_ZAMORAK, SpellBook::Standard, true, 0};
        case Standard::CRUMBLE_UNDEAD: return {"CRUMBLE_UNDEAD", 39, gamevals::InterfaceID::MagicSpellbook::CRUMBLE_UNDEAD, SpellBook::Standard, false, 0};
        case Standard::IBAN_BLAST: return {"IBAN_BLAST", 50, gamevals::InterfaceID::MagicSpellbook::IBAN_BLAST, SpellBook::Standard, true, 0};
        case Standard::MAGIC_DART: return {"MAGIC_DART", 50, gamevals::InterfaceID::MagicSpellbook::MAGIC_DART, SpellBook::Standard, true, 0};
        case Standard::CONFUSE: return {"CONFUSE", 3, gamevals::InterfaceID::MagicSpellbook::CONFUSE, SpellBook::Standard, false, 0};
        case Standard::WEAKEN: return {"WEAKEN", 11, gamevals::InterfaceID::MagicSpellbook::WEAKEN, SpellBook::Standard, false, 0};
        case Standard::CURSE: return {"CURSE", 19, gamevals::InterfaceID::MagicSpellbook::CURSE, SpellBook::Standard, false, 0};
        case Standard::BIND: return {"BIND", 20, gamevals::InterfaceID::MagicSpellbook::BIND, SpellBook::Standard, false, 0};
        case Standard::SNARE: return {"SNARE", 50, gamevals::InterfaceID::MagicSpellbook::SNARE, SpellBook::Standard, false, 0};
        case Standard::VULNERABILITY: return {"VULNERABILITY", 66, gamevals::InterfaceID::MagicSpellbook::VULNERABILITY, SpellBook::Standard, true, 0};
        case Standard::ENFEEBLE: return {"ENFEEBLE", 73, gamevals::InterfaceID::MagicSpellbook::ENFEEBLE, SpellBook::Standard, true, 0};
        case Standard::ENTANGLE: return {"ENTANGLE", 79, gamevals::InterfaceID::MagicSpellbook::ENTANGLE, SpellBook::Standard, true, 0};
        case Standard::STUN: return {"STUN", 80, gamevals::InterfaceID::MagicSpellbook::STUN, SpellBook::Standard, true, 0};
        case Standard::TELE_BLOCK: return {"TELE_BLOCK", 85, gamevals::InterfaceID::MagicSpellbook::TELEPORT_BLOCK, SpellBook::Standard, false, 0};
        case Standard::CHARGE: return {"CHARGE", 80, gamevals::InterfaceID::MagicSpellbook::CHARGE, SpellBook::Standard, true, 0};
        case Standard::BONES_TO_BANANAS: return {"BONES_TO_BANANAS", 15, gamevals::InterfaceID::MagicSpellbook::BONES_BANANAS, SpellBook::Standard, false, 0};
        case Standard::LOW_LEVEL_ALCHEMY: return {"LOW_LEVEL_ALCHEMY", 21, gamevals::InterfaceID::MagicSpellbook::LOW_ALCHEMY, SpellBook::Standard, false, 0};
        case Standard::SUPERHEAT_ITEM: return {"SUPERHEAT_ITEM", 43, gamevals::InterfaceID::MagicSpellbook::SUPERHEAT, SpellBook::Standard, false, 0};
        case Standard::HIGH_LEVEL_ALCHEMY: return {"HIGH_LEVEL_ALCHEMY", 55, gamevals::InterfaceID::MagicSpellbook::HIGH_ALCHEMY, SpellBook::Standard, false, 0};
        case Standard::BONES_TO_PEACHES: return {"BONES_TO_PEACHES", 60, gamevals::InterfaceID::MagicSpellbook::BONES_PEACHES, SpellBook::Standard, true, 0};
        case Standard::LVL_1_ENCHANT: return {"LVL_1_ENCHANT", 7, gamevals::InterfaceID::MagicSpellbook::ENCHANT_1, SpellBook::Standard, false, 0};
        case Standard::LVL_2_ENCHANT: return {"LVL_2_ENCHANT", 27, gamevals::InterfaceID::MagicSpellbook::ENCHANT_2, SpellBook::Standard, false, 0};
        case Standard::LVL_3_ENCHANT: return {"LVL_3_ENCHANT", 49, gamevals::InterfaceID::MagicSpellbook::ENCHANT_3, SpellBook::Standard, false, 0};
        case Standard::CHARGE_WATER_ORB: return {"CHARGE_WATER_ORB", 56, gamevals::InterfaceID::MagicSpellbook::CHARGE_WATER_ORB, SpellBook::Standard, true, 0};
        case Standard::LVL_4_ENCHANT: return {"LVL_4_ENCHANT", 57, gamevals::InterfaceID::MagicSpellbook::ENCHANT_4, SpellBook::Standard, false, 0};
        case Standard::CHARGE_EARTH_ORB: return {"CHARGE_EARTH_ORB", 60, gamevals::InterfaceID::MagicSpellbook::CHARGE_EARTH_ORB, SpellBook::Standard, true, 0};
        case Standard::CHARGE_FIRE_ORB: return {"CHARGE_FIRE_ORB", 63, gamevals::InterfaceID::MagicSpellbook::CHARGE_FIRE_ORB, SpellBook::Standard, true, 0};
        case Standard::CHARGE_AIR_ORB: return {"CHARGE_AIR_ORB", 66, gamevals::InterfaceID::MagicSpellbook::CHARGE_AIR_ORB, SpellBook::Standard, true, 0};
        case Standard::LVL_5_ENCHANT: return {"LVL_5_ENCHANT", 68, gamevals::InterfaceID::MagicSpellbook::ENCHANT_5, SpellBook::Standard, true, 0};
        case Standard::LVL_6_ENCHANT: return {"LVL_6_ENCHANT", 87, gamevals::InterfaceID::MagicSpellbook::ENCHANT_6, SpellBook::Standard, true, 0};
        case Standard::LVL_7_ENCHANT: return {"LVL_7_ENCHANT", 93, gamevals::InterfaceID::MagicSpellbook::ENCHANT_7, SpellBook::Standard, true, 0};
        case Standard::TELEKINETIC_GRAB: return {"TELEKINETIC_GRAB", 31, gamevals::InterfaceID::MagicSpellbook::TELEGRAB, SpellBook::Standard, false, 0};
    }
    return {"", 0, 0u, SpellBook::Standard, false, 0};
}

inline constexpr SpellInfo info(Ancient spell) {
    switch (spell) {
        case Ancient::EDGEVILLE_HOME_TELEPORT: return {"EDGEVILLE_HOME_TELEPORT", 0, gamevals::InterfaceID::MagicSpellbook::TELEPORT_HOME_ZAROS, SpellBook::Ancient, true, 0};
        case Ancient::PADDEWWA_TELEPORT: return {"PADDEWWA_TELEPORT", 54, gamevals::InterfaceID::MagicSpellbook::ZAROSTELEPORT1, SpellBook::Ancient, true, 0};
        case Ancient::SENNTISTEN_TELEPORT: return {"SENNTISTEN_TELEPORT", 60, gamevals::InterfaceID::MagicSpellbook::ZAROSTELEPORT2, SpellBook::Ancient, true, 0};
        case Ancient::KHARYRLL_TELEPORT: return {"KHARYRLL_TELEPORT", 66, gamevals::InterfaceID::MagicSpellbook::ZAROSTELEPORT3, SpellBook::Ancient, true, 0};
        case Ancient::LASSAR_TELEPORT: return {"LASSAR_TELEPORT", 72, gamevals::InterfaceID::MagicSpellbook::ZAROSTELEPORT4, SpellBook::Ancient, true, 0};
        case Ancient::DAREEYAK_TELEPORT: return {"DAREEYAK_TELEPORT", 78, gamevals::InterfaceID::MagicSpellbook::ZAROSTELEPORT5, SpellBook::Ancient, true, 0};
        case Ancient::CARRALLANGER_TELEPORT: return {"CARRALLANGER_TELEPORT", 84, gamevals::InterfaceID::MagicSpellbook::ZAROSTELEPORT6, SpellBook::Ancient, true, 0};
        case Ancient::BOUNTY_TARGET_TELEPORT: return {"BOUNTY_TARGET_TELEPORT", 85, gamevals::InterfaceID::MagicSpellbook::BOUNTY_TARGET, SpellBook::Ancient, true, 0};
        case Ancient::ANNAKARL_TELEPORT: return {"ANNAKARL_TELEPORT", 90, gamevals::InterfaceID::MagicSpellbook::ZAROSTELEPORT7, SpellBook::Ancient, true, 0};
        case Ancient::GHORROCK_TELEPORT: return {"GHORROCK_TELEPORT", 96, gamevals::InterfaceID::MagicSpellbook::ZAROSTELEPORT8, SpellBook::Ancient, true, 0};
        case Ancient::SMOKE_RUSH: return {"SMOKE_RUSH", 50, gamevals::InterfaceID::MagicSpellbook::SMOKE_RUSH, SpellBook::Ancient, true, 0};
        case Ancient::SHADOW_RUSH: return {"SHADOW_RUSH", 52, gamevals::InterfaceID::MagicSpellbook::SHADOW_RUSH, SpellBook::Ancient, true, 0};
        case Ancient::BLOOD_RUSH: return {"BLOOD_RUSH", 56, gamevals::InterfaceID::MagicSpellbook::BLOOD_RUSH, SpellBook::Ancient, true, 0};
        case Ancient::ICE_RUSH: return {"ICE_RUSH", 58, gamevals::InterfaceID::MagicSpellbook::ICE_RUSH, SpellBook::Ancient, true, 0};
        case Ancient::SMOKE_BURST: return {"SMOKE_BURST", 62, gamevals::InterfaceID::MagicSpellbook::SMOKE_BURST, SpellBook::Ancient, true, 0};
        case Ancient::SHADOW_BURST: return {"SHADOW_BURST", 64, gamevals::InterfaceID::MagicSpellbook::SHADOW_BURST, SpellBook::Ancient, true, 0};
        case Ancient::BLOOD_BURST: return {"BLOOD_BURST", 68, gamevals::InterfaceID::MagicSpellbook::BLOOD_BURST, SpellBook::Ancient, true, 0};
        case Ancient::ICE_BURST: return {"ICE_BURST", 70, gamevals::InterfaceID::MagicSpellbook::ICE_BURST, SpellBook::Ancient, true, 0};
        case Ancient::SMOKE_BLITZ: return {"SMOKE_BLITZ", 74, gamevals::InterfaceID::MagicSpellbook::SMOKE_BLITZ, SpellBook::Ancient, true, 0};
        case Ancient::SHADOW_BLITZ: return {"SHADOW_BLITZ", 76, gamevals::InterfaceID::MagicSpellbook::SHADOW_BLITZ, SpellBook::Ancient, true, 0};
        case Ancient::BLOOD_BLITZ: return {"BLOOD_BLITZ", 80, gamevals::InterfaceID::MagicSpellbook::BLOOD_BLITZ, SpellBook::Ancient, true, 0};
        case Ancient::ICE_BLITZ: return {"ICE_BLITZ", 82, gamevals::InterfaceID::MagicSpellbook::ICE_BLITZ, SpellBook::Ancient, true, 0};
        case Ancient::SMOKE_BARRAGE: return {"SMOKE_BARRAGE", 86, gamevals::InterfaceID::MagicSpellbook::SMOKE_BARRAGE, SpellBook::Ancient, true, 0};
        case Ancient::SHADOW_BARRAGE: return {"SHADOW_BARRAGE", 88, gamevals::InterfaceID::MagicSpellbook::SHADOW_BARRAGE, SpellBook::Ancient, true, 0};
        case Ancient::BLOOD_BARRAGE: return {"BLOOD_BARRAGE", 92, gamevals::InterfaceID::MagicSpellbook::BLOOD_BARRAGE, SpellBook::Ancient, true, 0};
        case Ancient::ICE_BARRAGE: return {"ICE_BARRAGE", 94, gamevals::InterfaceID::MagicSpellbook::ICE_BARRAGE, SpellBook::Ancient, true, 0};
    }
    return {"", 0, 0u, SpellBook::Ancient, false, 0};
}

inline constexpr SpellInfo info(Lunar spell) {
    switch (spell) {
        case Lunar::LUNAR_HOME_TELEPORT: return {"LUNAR_HOME_TELEPORT", 0, gamevals::InterfaceID::MagicSpellbook::TELEPORT_HOME_LUNAR, SpellBook::Lunar, true, 0};
        case Lunar::MOONCLAN_TELEPORT: return {"MOONCLAN_TELEPORT", 69, gamevals::InterfaceID::MagicSpellbook::TELE_MOONCLAN, SpellBook::Lunar, true, 0};
        case Lunar::TELE_GROUP_MOONCLAN: return {"TELE_GROUP_MOONCLAN", 70, gamevals::InterfaceID::MagicSpellbook::TELE_GROUP_MOONCLAN, SpellBook::Lunar, true, 0};
        case Lunar::OURANIA_TELEPORT: return {"OURANIA_TELEPORT", 71, gamevals::InterfaceID::MagicSpellbook::OURANIA_TELEPORT, SpellBook::Lunar, true, 0};
        case Lunar::WATERBIRTH_TELEPORT: return {"WATERBIRTH_TELEPORT", 72, gamevals::InterfaceID::MagicSpellbook::TELE_WATERBIRTH, SpellBook::Lunar, true, 0};
        case Lunar::TELE_GROUP_WATERBIRTH: return {"TELE_GROUP_WATERBIRTH", 73, gamevals::InterfaceID::MagicSpellbook::TELE_GROUP_WATERBIRTH, SpellBook::Lunar, true, 0};
        case Lunar::BARBARIAN_TELEPORT: return {"BARBARIAN_TELEPORT", 75, gamevals::InterfaceID::MagicSpellbook::TELE_BARB_OUT, SpellBook::Lunar, true, 0};
        case Lunar::TELE_GROUP_BARBARIAN: return {"TELE_GROUP_BARBARIAN", 76, gamevals::InterfaceID::MagicSpellbook::TELE_GROUP_BARBARIAN, SpellBook::Lunar, true, 0};
        case Lunar::KHAZARD_TELEPORT: return {"KHAZARD_TELEPORT", 78, gamevals::InterfaceID::MagicSpellbook::TELE_KHAZARD, SpellBook::Lunar, true, 0};
        case Lunar::TELE_GROUP_KHAZARD: return {"TELE_GROUP_KHAZARD", 79, gamevals::InterfaceID::MagicSpellbook::TELE_GROUP_KHAZARD, SpellBook::Lunar, true, 0};
        case Lunar::FISHING_GUILD_TELEPORT: return {"FISHING_GUILD_TELEPORT", 85, gamevals::InterfaceID::MagicSpellbook::TELE_FISH, SpellBook::Lunar, true, 0};
        case Lunar::TELE_GROUP_FISHING_GUILD: return {"TELE_GROUP_FISHING_GUILD", 86, gamevals::InterfaceID::MagicSpellbook::TELE_GROUP_FISHING_GUILD, SpellBook::Lunar, true, 0};
        case Lunar::CATHERBY_TELEPORT: return {"CATHERBY_TELEPORT", 87, gamevals::InterfaceID::MagicSpellbook::TELE_CATHER, SpellBook::Lunar, true, 0};
        case Lunar::TELE_GROUP_CATHERBY: return {"TELE_GROUP_CATHERBY", 88, gamevals::InterfaceID::MagicSpellbook::TELE_GROUP_CATHERBY, SpellBook::Lunar, true, 0};
        case Lunar::ICE_PLATEAU_TELEPORT: return {"ICE_PLATEAU_TELEPORT", 89, gamevals::InterfaceID::MagicSpellbook::TELE_GHORROCK, SpellBook::Lunar, true, 0};
        case Lunar::TELE_GROUP_ICE_PLATEAU: return {"TELE_GROUP_ICE_PLATEAU", 90, gamevals::InterfaceID::MagicSpellbook::TELE_GROUP_GHORROCK, SpellBook::Lunar, true, 0};
        case Lunar::MONSTER_EXAMINE: return {"MONSTER_EXAMINE", 66, gamevals::InterfaceID::MagicSpellbook::MONSTER_EXAMINE, SpellBook::Lunar, true, 0};
        case Lunar::CURE_OTHER: return {"CURE_OTHER", 66, gamevals::InterfaceID::MagicSpellbook::CURE_OTHER, SpellBook::Lunar, true, 0};
        case Lunar::CURE_ME: return {"CURE_ME", 66, gamevals::InterfaceID::MagicSpellbook::CURE_ME, SpellBook::Lunar, true, 0};
        case Lunar::CURE_GROUP: return {"CURE_GROUP", 66, gamevals::InterfaceID::MagicSpellbook::CURE_GROUP, SpellBook::Lunar, true, 0};
        case Lunar::STAT_SPY: return {"STAT_SPY", 66, gamevals::InterfaceID::MagicSpellbook::STAT_SPY, SpellBook::Lunar, true, 0};
        case Lunar::DREAM: return {"DREAM", 66, gamevals::InterfaceID::MagicSpellbook::DREAM, SpellBook::Lunar, true, 0};
        case Lunar::STAT_RESTORE_POT_SHARE: return {"STAT_RESTORE_POT_SHARE", 66, gamevals::InterfaceID::MagicSpellbook::REST_POT_SHARE, SpellBook::Lunar, true, 0};
        case Lunar::BOOST_POTION_SHARE: return {"BOOST_POTION_SHARE", 66, gamevals::InterfaceID::MagicSpellbook::STREN_POT_SHARE, SpellBook::Lunar, true, 0};
        case Lunar::ENERGY_TRANSFER: return {"ENERGY_TRANSFER", 66, gamevals::InterfaceID::MagicSpellbook::ENERGY_TRANS, SpellBook::Lunar, true, 0};
        case Lunar::HEAL_OTHER: return {"HEAL_OTHER", 66, gamevals::InterfaceID::MagicSpellbook::HEAL_OTHER, SpellBook::Lunar, true, 0};
        case Lunar::VENGEANCE_OTHER: return {"VENGEANCE_OTHER", 66, gamevals::InterfaceID::MagicSpellbook::VENGEANCE_OTHER, SpellBook::Lunar, true, 0};
        case Lunar::VENGEANCE: return {"VENGEANCE", 66, gamevals::InterfaceID::MagicSpellbook::VENGEANCE, SpellBook::Lunar, true, 0};
        case Lunar::HEAL_GROUP: return {"HEAL_GROUP", 66, gamevals::InterfaceID::MagicSpellbook::HEAL_GROUP, SpellBook::Lunar, true, 0};
        case Lunar::BAKE_PIE: return {"BAKE_PIE", 66, gamevals::InterfaceID::MagicSpellbook::BAKE_PIE, SpellBook::Lunar, true, 0};
        case Lunar::GEOMANCY: return {"GEOMANCY", 66, gamevals::InterfaceID::MagicSpellbook::GEOMANCY, SpellBook::Lunar, true, 0};
        case Lunar::CURE_PLANT: return {"CURE_PLANT", 66, gamevals::InterfaceID::MagicSpellbook::CURE_PLANT, SpellBook::Lunar, true, 0};
        case Lunar::NPC_CONTACT: return {"NPC_CONTACT", 66, gamevals::InterfaceID::MagicSpellbook::NPC_CONTACT, SpellBook::Lunar, true, 0};
        case Lunar::HUMIDIFY: return {"HUMIDIFY", 66, gamevals::InterfaceID::MagicSpellbook::HUMIDIFY, SpellBook::Lunar, true, 0};
        case Lunar::HUNTER_KIT: return {"HUNTER_KIT", 66, gamevals::InterfaceID::MagicSpellbook::HUNTER_KIT, SpellBook::Lunar, true, 0};
        case Lunar::SPIN_FLAX: return {"SPIN_FLAX", 66, gamevals::InterfaceID::MagicSpellbook::SPIN_FLAX, SpellBook::Lunar, true, 0};
        case Lunar::SUPERGLASS_MAKE: return {"SUPERGLASS_MAKE", 66, gamevals::InterfaceID::MagicSpellbook::SUPERGLASS, SpellBook::Lunar, true, 0};
        case Lunar::TAN_LEATHER: return {"TAN_LEATHER", 66, gamevals::InterfaceID::MagicSpellbook::TAN_LEATHER, SpellBook::Lunar, true, 0};
        case Lunar::STRING_JEWELLERY: return {"STRING_JEWELLERY", 66, gamevals::InterfaceID::MagicSpellbook::STRING_JEWEL, SpellBook::Lunar, true, 0};
        case Lunar::MAGIC_IMBUE: return {"MAGIC_IMBUE", 66, gamevals::InterfaceID::MagicSpellbook::MAGIC_IMBUE, SpellBook::Lunar, true, 0};
        case Lunar::FERTILE_SOIL: return {"FERTILE_SOIL", 66, gamevals::InterfaceID::MagicSpellbook::FERTILE_SOIL, SpellBook::Lunar, true, 0};
        case Lunar::PLANK_MAKE: return {"PLANK_MAKE", 66, gamevals::InterfaceID::MagicSpellbook::PLANK_MAKE, SpellBook::Lunar, true, 0};
        case Lunar::RECHARGE_DRAGONSTONE: return {"RECHARGE_DRAGONSTONE", 66, gamevals::InterfaceID::MagicSpellbook::RECHARGE_DRAGONSTONE, SpellBook::Lunar, true, 0};
        case Lunar::SPELLBOOK_SWAP: return {"SPELLBOOK_SWAP", 66, gamevals::InterfaceID::MagicSpellbook::SPELLBOOK_SWAP, SpellBook::Lunar, true, 0};
    }
    return {"", 0, 0u, SpellBook::Lunar, false, 0};
}

inline constexpr SpellInfo info(Necromancy spell) {
    switch (spell) {
        case Necromancy::ARCEUUS_HOME_TELEPORT: return {"ARCEUUS_HOME_TELEPORT", 1, gamevals::InterfaceID::MagicSpellbook::TELEPORT_HOME_ARCEUUS, SpellBook::Necromancy, true, 0};
        case Necromancy::ARCEUUS_LIBRARY_TELEPORT: return {"ARCEUUS_LIBRARY_TELEPORT", 6, gamevals::InterfaceID::MagicSpellbook::TELEPORT_ARCEUUS_LIBRARY, SpellBook::Necromancy, true, 0};
        case Necromancy::DRAYNOR_MANOR_TELEPORT: return {"DRAYNOR_MANOR_TELEPORT", 17, gamevals::InterfaceID::MagicSpellbook::TELEPORT_DRAYNOR_MANOR, SpellBook::Necromancy, true, 0};
        case Necromancy::BATTLEFRONT_TELEPORT: return {"BATTLEFRONT_TELEPORT", 23, gamevals::InterfaceID::MagicSpellbook::TELEPORT_BATTLEFRONT, SpellBook::Necromancy, true, 0};
        case Necromancy::MIND_ALTAR_TELEPORT: return {"MIND_ALTAR_TELEPORT", 28, gamevals::InterfaceID::MagicSpellbook::TELEPORT_MIND_ALTAR, SpellBook::Necromancy, true, 0};
        case Necromancy::RESPAWN_TELEPORT: return {"RESPAWN_TELEPORT", 34, gamevals::InterfaceID::MagicSpellbook::TELEPORT_RESPAWN, SpellBook::Necromancy, true, 0};
        case Necromancy::SALVE_GRAVEYARD_TELEPORT: return {"SALVE_GRAVEYARD_TELEPORT", 40, gamevals::InterfaceID::MagicSpellbook::TELEPORT_SALVE_GRAVEYARD, SpellBook::Necromancy, true, 0};
        case Necromancy::FENKENSTRAINS_CASTLE_TELEPORT: return {"FENKENSTRAINS_CASTLE_TELEPORT", 48, gamevals::InterfaceID::MagicSpellbook::TELEPORT_FENKENSTRAIN_CASTLE, SpellBook::Necromancy, true, 0};
        case Necromancy::WEST_ARDOUGNE_TELEPORT: return {"WEST_ARDOUGNE_TELEPORT", 61, gamevals::InterfaceID::MagicSpellbook::TELEPORT_WEST_ARDOUGNE, SpellBook::Necromancy, true, 0};
        case Necromancy::HARMONY_ISLAND_TELEPORT: return {"HARMONY_ISLAND_TELEPORT", 65, gamevals::InterfaceID::MagicSpellbook::TELEPORT_HARMONY_ISLAND, SpellBook::Necromancy, true, 0};
        case Necromancy::CEMETERY_TELEPORT: return {"CEMETERY_TELEPORT", 71, gamevals::InterfaceID::MagicSpellbook::TELEPORT_CEMETERY, SpellBook::Necromancy, true, 0};
        case Necromancy::BARROWS_TELEPORT: return {"BARROWS_TELEPORT", 83, gamevals::InterfaceID::MagicSpellbook::TELEPORT_BARROWS, SpellBook::Necromancy, true, 0};
        case Necromancy::APE_ATOLL_TELEPORT: return {"APE_ATOLL_TELEPORT", 90, gamevals::InterfaceID::MagicSpellbook::TELEPORT_APE_ATOLL_DUNGEON, SpellBook::Necromancy, true, 0};
        case Necromancy::GHOSTLY_GRASP: return {"GHOSTLY_GRASP", 35, gamevals::InterfaceID::MagicSpellbook::GHOSTLY_GRASP, SpellBook::Necromancy, true, 0};
        case Necromancy::SKELETAL_GRASP: return {"SKELETAL_GRASP", 56, gamevals::InterfaceID::MagicSpellbook::SKELETAL_GRASP, SpellBook::Necromancy, true, 0};
        case Necromancy::UNDEAD_GRASP: return {"UNDEAD_GRASP", 79, gamevals::InterfaceID::MagicSpellbook::UNDEAD_GRASP, SpellBook::Necromancy, true, 0};
        case Necromancy::INFERIOR_DEMONBANE: return {"INFERIOR_DEMONBANE", 44, gamevals::InterfaceID::MagicSpellbook::INFERIOR_DEMONBANE, SpellBook::Necromancy, true, 0};
        case Necromancy::SUPERIOR_DEMONBANE: return {"SUPERIOR_DEMONBANE", 62, gamevals::InterfaceID::MagicSpellbook::SUPERIOR_DEMONBANE, SpellBook::Necromancy, true, 0};
        case Necromancy::DARK_DEMONBANE: return {"DARK_DEMONBANE", 82, gamevals::InterfaceID::MagicSpellbook::DARK_DEMONBANE, SpellBook::Necromancy, true, 0};
        case Necromancy::LESSER_CORRUPTION: return {"LESSER_CORRUPTION", 64, gamevals::InterfaceID::MagicSpellbook::LESSER_CORRUPTION, SpellBook::Necromancy, true, 0};
        case Necromancy::GREATER_CORRUPTION: return {"GREATER_CORRUPTION", 85, gamevals::InterfaceID::MagicSpellbook::GREATER_CORRUPTION, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_LESSER_GHOST: return {"RESURRECT_LESSER_GHOST", 38, gamevals::InterfaceID::MagicSpellbook::RESURRECT_LESSER_GHOST, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_LESSER_SKELETON: return {"RESURRECT_LESSER_SKELETON", 38, gamevals::InterfaceID::MagicSpellbook::RESURRECT_LESSER_SKELETON, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_LESSER_ZOMBIE: return {"RESURRECT_LESSER_ZOMBIE", 38, gamevals::InterfaceID::MagicSpellbook::RESURRECT_LESSER_ZOMBIE, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_SUPERIOR_GHOST: return {"RESURRECT_SUPERIOR_GHOST", 57, gamevals::InterfaceID::MagicSpellbook::RESURRECT_SUPERIOR_GHOST, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_SUPERIOR_SKELETON: return {"RESURRECT_SUPERIOR_SKELETON", 57, gamevals::InterfaceID::MagicSpellbook::RESURRECT_SUPERIOR_SKELETON, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_SUPERIOR_ZOMBIE: return {"RESURRECT_SUPERIOR_ZOMBIE", 57, gamevals::InterfaceID::MagicSpellbook::RESURRECT_SUPERIOR_ZOMBIE, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_GREATER_GHOST: return {"RESURRECT_GREATER_GHOST", 76, gamevals::InterfaceID::MagicSpellbook::RESURRECT_GREATER_GHOST, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_GREATER_SKELETON: return {"RESURRECT_GREATER_SKELETON", 76, gamevals::InterfaceID::MagicSpellbook::RESURRECT_GREATER_SKELETON, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_GREATER_ZOMBIE: return {"RESURRECT_GREATER_ZOMBIE", 76, gamevals::InterfaceID::MagicSpellbook::RESURRECT_GREATER_ZOMBIE, SpellBook::Necromancy, true, 0};
        case Necromancy::DARK_LURE: return {"DARK_LURE", 50, gamevals::InterfaceID::MagicSpellbook::DARK_LURE, SpellBook::Necromancy, true, 0};
        case Necromancy::MARK_OF_DARKNESS: return {"MARK_OF_DARKNESS", 59, gamevals::InterfaceID::MagicSpellbook::MARK_OF_DARKNESS, SpellBook::Necromancy, true, 0};
        case Necromancy::WARD_OF_ARCEUUS: return {"WARD_OF_ARCEUUS", 73, gamevals::InterfaceID::MagicSpellbook::WARD_OF_ARCEUUS, SpellBook::Necromancy, true, 0};
        case Necromancy::BASIC_REANIMATION: return {"BASIC_REANIMATION", 16, gamevals::InterfaceID::MagicSpellbook::REANIMATION_BASIC, SpellBook::Necromancy, true, 0};
        case Necromancy::ADEPT_REANIMATION: return {"ADEPT_REANIMATION", 41, gamevals::InterfaceID::MagicSpellbook::REANIMATION_ADEPT, SpellBook::Necromancy, true, 0};
        case Necromancy::EXPERT_REANIMATION: return {"EXPERT_REANIMATION", 72, gamevals::InterfaceID::MagicSpellbook::REANIMATION_EXPERT, SpellBook::Necromancy, true, 0};
        case Necromancy::MASTER_REANIMATION: return {"MASTER_REANIMATION", 90, gamevals::InterfaceID::MagicSpellbook::REANIMATION_MASTER, SpellBook::Necromancy, true, 0};
        case Necromancy::DEMONIC_OFFERING: return {"DEMONIC_OFFERING", 84, gamevals::InterfaceID::MagicSpellbook::DEMONIC_OFFERING, SpellBook::Necromancy, true, 0};
        case Necromancy::SINISTER_OFFERING: return {"SINISTER_OFFERING", 92, gamevals::InterfaceID::MagicSpellbook::SINISTER_OFFERING, SpellBook::Necromancy, true, 0};
        case Necromancy::SHADOW_VEIL: return {"SHADOW_VEIL", 47, gamevals::InterfaceID::MagicSpellbook::SHADOW_VEIL, SpellBook::Necromancy, true, 0};
        case Necromancy::VILE_VIGOUR: return {"VILE_VIGOUR", 66, gamevals::InterfaceID::MagicSpellbook::VILE_VIGOUR, SpellBook::Necromancy, true, 0};
        case Necromancy::DEGRIME: return {"DEGRIME", 70, gamevals::InterfaceID::MagicSpellbook::DEGRIME, SpellBook::Necromancy, true, 0};
        case Necromancy::RESURRECT_CROPS: return {"RESURRECT_CROPS", 78, gamevals::InterfaceID::MagicSpellbook::RESURRECT_CROPS, SpellBook::Necromancy, true, 0};
        case Necromancy::DEATH_CHARGE: return {"DEATH_CHARGE", 80, gamevals::InterfaceID::MagicSpellbook::DEATH_CHARGE, SpellBook::Necromancy, true, 0};
    }
    return {"", 0, 0u, SpellBook::Necromancy, false, 0};
}

inline SpellBook currentSpellBook() {
    switch (titan::state::vars().varbit(Varbits::SPELLBOOK)) {
        case 0: return SpellBook::Standard;
        case 1: return SpellBook::Ancient;
        case 2: return SpellBook::Lunar;
        case 3: return SpellBook::Necromancy;
        default: return SpellBook::Standard;
    }
}

inline bool isAutoCasting() {
    constexpr int kAutocastVarp = 108;
    return titan::state::vars().varp(kAutocastVarp) != 0;
}

inline std::chrono::system_clock::time_point lastHomeTeleportUsage() {
    const auto minutes = std::chrono::minutes{titan::state::vars().varp(VarPlayerID::LAST_HOME_TELEPORT)};
    return std::chrono::system_clock::time_point{minutes};
}

inline bool isHomeTeleportOnCooldown() {
    return lastHomeTeleportUsage() + std::chrono::minutes{30} > std::chrono::system_clock::now();
}

inline bool canCast(const SpellInfo& spell) {
    if (!spell.name || spell.name[0] == '\0' || spell.widget == 0u) return false;
    if (spell.book != currentSpellBook()) return false;
    const int magic = static_cast<int>(Skill::MAGIC);
    if (spell.level > titan::state::skills().real(magic) ||
        spell.level > titan::state::skills().boosted(magic)) {
        return false;
    }
    if (spell.book == SpellBook::Standard) {
        if (spell.widget == gamevals::InterfaceID::MagicSpellbook::ARDOUGNE_TELEPORT &&
            titan::state::vars().varp(165) < 30) {
            return false;
        }
        if (spell.widget == gamevals::InterfaceID::MagicSpellbook::TROLLHEIM_TELEPORT &&
            titan::state::vars().varp(335) < 110) {
            return false;
        }
    }
    return true;
}

template <SpellEnum SpellT>
inline bool canCast(SpellT spell) {
    return canCast(info(spell));
}

/// Perform the spell's own Cast option: CC_OP against the catalog's menu
/// entry. This is the plain "click the spell" action, and it is what a
/// non-targeted spell such as a teleport needs. Use select() for the
/// "cast on ..." source selection that leaves the client awaiting a target.
template <SpellEnum SpellT>
inline bool cast(SpellT spell) {
    const SpellInfo i = info(spell);
    if (!i.name || i.name[0] == '\0' || i.widget == 0u || i.menuEntryId < 0) return false;

    return detail::spellWidgetInteract(i.widget, i.menuEntryId + 1, MenuAction::Id::CcOp);
}

/// Select the spell as a targeting source, leaving the client awaiting a
/// target. On its own this casts nothing -- pair it with a target through
/// castOn(), or dispatch the target leg yourself.
template <SpellEnum SpellT>
inline bool select(SpellT spell) {
    const SpellInfo i = info(spell);
    if (!i.name || i.name[0] == '\0' || i.widget == 0u) return false;

    return detail::spellWidgetInteract(i.widget, 0, MenuAction::Id::WidgetTarget);
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, int actionIndex, MenuAction::Id opcode) {
    if (actionIndex < 0) return false;

    const SpellInfo i = info(spell);
    if (!i.name || i.name[0] == '\0' || i.widget == 0u) return false;

    return detail::spellWidgetInteract(i.widget, actionIndex + 1, opcode);
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, int actionIndex) {
    return cast(spell, actionIndex, MenuAction::Id::CcOp);
}

namespace detail {
inline bool dispatchSelected(const SpellInfo& spell, MenuAction::Entry target) {
    if (!spell.name || !spell.name[0] || !spell.widget) return false;
    auto source = targetEntry(MenuAction::Id::WidgetTarget, 0, -1, static_cast<int32_t>(spell.widget));
    source.actionText = kCastActionText;
    source.skipClick = target.skipClick;
    target.actionText = kCastActionText; target.targetText = kBlankTargetText;
    return ::titan::state::client().invokeSelectedMenuAction(source, target);
}
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, const Item& target) {
    const int32_t slot = target.slot();
    if (slot < 0 || target.id() < 0) return false;
    return detail::dispatchSelected(info(spell), detail::targetEntry(
        MenuAction::Id::WidgetTargetOnWidget, 0, slot, gamevals::InterfaceID::Inventory::ITEMS));
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, const Npc& target) {
    const int32_t hashIndex = target.hashIndex();
    if (hashIndex < 0) return false;
    auto entry = detail::targetEntry(
        MenuAction::Id::WidgetTargetOnNpc, hashIndex, 0, 0, target.worldViewId());
    entry.targetPlane = target.plane();
    entry.targetSizeX = target.sizeX();
    entry.targetSizeY = target.sizeY();
    entry.targetEntityPtr = target.entityPtr();
    return detail::dispatchSelected(info(spell), entry);
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, const Player& target) {
    const int32_t hashIndex = target.hashIndex();
    if (hashIndex < 0) return false;
    auto entry = detail::targetEntry(
        MenuAction::Id::WidgetTargetOnPlayer, hashIndex, 0, 0, target.worldViewId());
    entry.targetPlane = target.plane();
    entry.targetEntityPtr = target.entityPtr();
    return detail::dispatchSelected(info(spell), entry);
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, const GroundItem& target) {
    const int32_t itemId = target.id();
    if (itemId < 0) return false;
    auto entry = detail::targetEntry(
        MenuAction::Id::WidgetTargetOnGroundItem,
        itemId,
        target.tileX(),
        target.tileY(),
        target.worldViewId());
    entry.targetPlane = target.plane();
    return detail::dispatchSelected(info(spell), entry);
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, const TileObject& target) {
    const int32_t objectId = target.id();
    if (objectId < 0) return false;
    auto entry = detail::targetEntry(
        MenuAction::Id::WidgetTargetOnGameObject,
        objectId,
        target.tileX(),
        target.tileY(),
        target.worldViewId());
    entry.targetPlane = target.plane();
    entry.targetSizeX = target.sizeX();
    entry.targetSizeY = target.sizeY();
    entry.targetLayer = target.layer();
    entry.targetEntityPtr = target.entityPtr();
    entry.targetPackedId = target.packedId();
    return detail::dispatchSelected(info(spell), entry);
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, const Widget& target) {
    const int32_t packedId = target.packedId();
    if (packedId == 0) return false;
    return detail::dispatchSelected(info(spell), detail::targetEntry(
        MenuAction::Id::WidgetTargetOnWidget, 0, target.dynamicChildSlot(), packedId));
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, const WidgetSnapshot& target) {
    if (target.packedId == 0) return false;
    return detail::dispatchSelected(info(spell), detail::targetEntry(
        MenuAction::Id::WidgetTargetOnWidget, 0, target.dynamicChildSlot, target.packedId));
}

template <SpellEnum SpellT>
inline bool cast(SpellT spell, WidgetTarget target) {
    if (target.packedId == 0u) return false;
    return detail::dispatchSelected(info(spell), detail::targetEntry(
        MenuAction::Id::WidgetTargetOnWidget, 0, target.childIndex, static_cast<int32_t>(target.packedId)));
}

template <SpellEnum SpellT> inline bool castOn(SpellT spell, const Item& target) { return cast(spell, target); }
template <SpellEnum SpellT> inline bool castOn(SpellT spell, const Npc& target) { return cast(spell, target); }
template <SpellEnum SpellT> inline bool castOn(SpellT spell, const Player& target) { return cast(spell, target); }
template <SpellEnum SpellT> inline bool castOn(SpellT spell, const GroundItem& target) { return cast(spell, target); }
template <SpellEnum SpellT> inline bool castOn(SpellT spell, const TileObject& target) { return cast(spell, target); }
template <SpellEnum SpellT> inline bool castOn(SpellT spell, const Widget& target) { return cast(spell, target); }
template <SpellEnum SpellT> inline bool castOn(SpellT spell, const WidgetSnapshot& target) { return cast(spell, target); }
template <SpellEnum SpellT> inline bool castOn(SpellT spell, WidgetTarget target) { return cast(spell, target); }

}  // namespace Magic
}  // namespace utils

template <typename SpellT>
inline bool Player::castOn(SpellT spell) const {
    return ::titan::utils::Magic::castOn(spell, *this);
}

template <typename SpellT>
inline bool Npc::castOn(SpellT spell) const {
    return ::titan::utils::Magic::castOn(spell, *this);
}

template <typename SpellT>
inline bool Item::castOn(SpellT spell) const {
    return ::titan::utils::Magic::castOn(spell, *this);
}

template <typename SpellT>
inline bool GroundItem::castOn(SpellT spell) const {
    return ::titan::utils::Magic::castOn(spell, *this);
}

template <typename SpellT>
inline bool TileObject::castOn(SpellT spell) const {
    return ::titan::utils::Magic::castOn(spell, *this);
}

template <typename SpellT>
inline bool Widget::castOn(SpellT spell) const {
    return ::titan::utils::Magic::castOn(spell, *this);
}

template <typename SpellT>
inline bool WidgetSnapshot::castOn(SpellT spell) const {
    return ::titan::utils::Magic::castOn(spell, *this);
}

}  // namespace titan
