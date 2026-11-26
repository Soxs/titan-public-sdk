/// @file titan/utils/magic/spell_catalog.h
/// @brief Per-cast costs and unlocks. Wiki provenance and validation: titan/PUBLIC_API.md.
#pragma once

#include "spell_requirements.h"

namespace titan::utils::Magic {
namespace detail {

constexpr SpellRequirements spellRequirements(std::initializer_list<RuneCost> runes,
                                              SpellRequirements requirements = {}) {
    if (runes.size() > requirements.runes.size()) return {};
    requirements.known = true;
    size_t index = 0;
    for (const auto cost : runes) requirements.runes[index++] = cost;
    return requirements;
}

} // namespace detail

inline constexpr SpellRequirements requirements(BoltEnchantment spell) {
    using enum ::titan::RuneElement;
    using detail::spellRequirements;
    switch (spell) {
    case BoltEnchantment::OPAL: return spellRequirements({{AIR, 2}, {COSMIC, 1}});
    case BoltEnchantment::SAPPHIRE: return spellRequirements({{WATER, 1}, {COSMIC, 1}, {MIND, 1}});
    case BoltEnchantment::JADE: return spellRequirements({{EARTH, 2}, {COSMIC, 1}});
    case BoltEnchantment::PEARL: return spellRequirements({{WATER, 2}, {COSMIC, 1}});
    case BoltEnchantment::EMERALD: return spellRequirements({{AIR, 3}, {COSMIC, 1}, {NATURE, 1}});
    case BoltEnchantment::RED_TOPAZ: return spellRequirements({{FIRE, 2}, {COSMIC, 1}});
    case BoltEnchantment::RUBY: return spellRequirements({{FIRE, 5}, {BLOOD, 1}, {COSMIC, 1}});
    case BoltEnchantment::DIAMOND: return spellRequirements({{EARTH, 10}, {COSMIC, 1}, {LAW, 2}});
    case BoltEnchantment::DRAGONSTONE: return spellRequirements({{EARTH, 15}, {COSMIC, 1}, {SOUL, 1}});
    case BoltEnchantment::ONYX: return spellRequirements({{FIRE, 20}, {COSMIC, 1}, {DEATH, 1}});
    }
    return {};
}

/// Unknown book/widget pairs return known=false, not an empty-cost spell.
inline constexpr SpellRequirements requirements(SpellBook book, uint32_t widget) {
    using W = gamevals::InterfaceID::MagicSpellbook;
    using Q = gamevals::QuestID;
    using V = gamevals::VarbitID;
    using I = gamevals::ItemID;
    using A = AdditionalRequirement;
    using enum ::titan::RuneElement;
    using detail::spellRequirements;

    // This widget is shared by all four books, with the same unlock and rune cost.
    if (widget == W::BOUNTY_TARGET && book >= SpellBook::Standard && book <= SpellBook::Necromancy) {
        return spellRequirements({{CHAOS, 1}, {DEATH, 1}, {LAW, 1}},
            {.unlockVarbit = V::BOUNTY_TARGET_TELEPORT, .spellSackId = I::BLIGHTED_SACK_TELEBLOCK});
    }

    switch (book) {
    case SpellBook::Standard:
        switch (widget) {
        case W::TELEPORT_HOME_STANDARD: return spellRequirements({}, {.additional = A::HomeTeleport});
        case W::VARROCK_TELEPORT: return spellRequirements({{AIR, 3}, {FIRE, 1}, {LAW, 1}});
        case W::LUMBRIDGE_TELEPORT: return spellRequirements({{AIR, 3}, {EARTH, 1}, {LAW, 1}});
        case W::FALADOR_TELEPORT: return spellRequirements({{AIR, 3}, {WATER, 1}, {LAW, 1}});
        case W::TELEPORT_TO_YOUR_HOUSE: return spellRequirements({{AIR, 1}, {EARTH, 1}, {LAW, 1}}, {.additional = A::House});
        case W::CAMELOT_TELEPORT: return spellRequirements({{AIR, 5}, {LAW, 1}});
        case W::ARDOUGNE_TELEPORT: return spellRequirements({{WATER, 2}, {LAW, 2}}, {.additional = A::Ardougne});
        case W::WATCHTOWER_TELEPORT: return spellRequirements({{EARTH, 2}, {LAW, 2}}, {.additional = A::Watchtower});
        case W::TROLLHEIM_TELEPORT: return spellRequirements({{FIRE, 2}, {LAW, 2}}, {.questId = Q::QUEST_EADGARSRUSE});
        case W::APE_TELEPORT: return spellRequirements({{FIRE, 2}, {WATER, 2}, {LAW, 2}}, {.questId = Q::SUBQUEST_RFD_MONKEY, .additional = A::ApeAtoll});
        case W::KOUREND_TELEPORT: return spellRequirements({{FIRE, 1}, {WATER, 1}, {LAW, 2}}, {.questId = Q::QUEST_CLIENTOFKOUREND});
        case W::FORTIS_TELEPORT: return spellRequirements({{EARTH, 1}, {FIRE, 1}, {LAW, 2}}, {.questId = Q::QUEST_TWILIGHTSPROMISE});
        // shortcut: boat-focus ownership is not checked, add when its state encoding is verified.
        case W::TELEPORT_ME_TO_BOAT: return spellRequirements({{EARTH, 2}, {WATER, 2}, {LAW, 2}}, {.questId = Q::QUEST_PANDEMONIUM});
        case W::TELEOTHER_LUMBRIDGE: return spellRequirements({{EARTH, 1}, {LAW, 1}, {SOUL, 1}});
        case W::TELEOTHER_FALADOR: return spellRequirements({{WATER, 1}, {LAW, 1}, {SOUL, 1}});
        case W::TELEOTHER_CAMELOT: return spellRequirements({{LAW, 1}, {SOUL, 2}});
        case W::WIND_STRIKE: return spellRequirements({{AIR, 1}, {MIND, 1}});
        case W::WATER_STRIKE: return spellRequirements({{AIR, 1}, {WATER, 1}, {MIND, 1}});
        case W::EARTH_STRIKE: return spellRequirements({{AIR, 1}, {EARTH, 2}, {MIND, 1}});
        case W::FIRE_STRIKE: return spellRequirements({{AIR, 2}, {FIRE, 3}, {MIND, 1}});
        case W::WIND_BOLT: return spellRequirements({{AIR, 2}, {CHAOS, 1}});
        case W::WATER_BOLT: return spellRequirements({{AIR, 2}, {WATER, 2}, {CHAOS, 1}});
        case W::EARTH_BOLT: return spellRequirements({{AIR, 2}, {EARTH, 3}, {CHAOS, 1}});
        case W::FIRE_BOLT: return spellRequirements({{AIR, 3}, {FIRE, 4}, {CHAOS, 1}});
        case W::WIND_BLAST: return spellRequirements({{AIR, 3}, {DEATH, 1}});
        case W::WATER_BLAST: return spellRequirements({{AIR, 3}, {WATER, 3}, {DEATH, 1}});
        case W::EARTH_BLAST: return spellRequirements({{AIR, 3}, {EARTH, 4}, {DEATH, 1}});
        case W::FIRE_BLAST: return spellRequirements({{AIR, 4}, {FIRE, 5}, {DEATH, 1}});
        case W::WIND_WAVE: return spellRequirements({{AIR, 5}, {BLOOD, 1}}, {.spellSackId = I::BLIGHTED_SACK_SURGE});
        case W::WATER_WAVE: return spellRequirements({{AIR, 5}, {WATER, 7}, {BLOOD, 1}}, {.spellSackId = I::BLIGHTED_SACK_SURGE});
        case W::EARTH_WAVE: return spellRequirements({{AIR, 5}, {EARTH, 7}, {BLOOD, 1}}, {.spellSackId = I::BLIGHTED_SACK_SURGE});
        case W::FIRE_WAVE: return spellRequirements({{AIR, 5}, {FIRE, 7}, {BLOOD, 1}}, {.spellSackId = I::BLIGHTED_SACK_SURGE});
        case W::WIND_SURGE: return spellRequirements({{AIR, 7}, {WRATH, 1}}, {.spellSackId = I::BLIGHTED_SACK_SURGE});
        case W::WATER_SURGE: return spellRequirements({{AIR, 7}, {WATER, 10}, {WRATH, 1}}, {.spellSackId = I::BLIGHTED_SACK_SURGE});
        case W::EARTH_SURGE: return spellRequirements({{AIR, 7}, {EARTH, 10}, {WRATH, 1}}, {.spellSackId = I::BLIGHTED_SACK_SURGE});
        case W::FIRE_SURGE: return spellRequirements({{AIR, 7}, {FIRE, 10}, {WRATH, 1}}, {.spellSackId = I::BLIGHTED_SACK_SURGE});
        case W::SARADOMIN_STRIKE: return spellRequirements({{AIR, 4}, {FIRE, 2}, {BLOOD, 2}}, {.additional = A::SaradominStaff});
        case W::CLAWS_OF_GUTHIX: return spellRequirements({{AIR, 4}, {FIRE, 1}, {BLOOD, 2}}, {.additional = A::GuthixStaff});
        case W::FLAMES_OF_ZAMORAK: return spellRequirements({{AIR, 1}, {FIRE, 4}, {BLOOD, 2}}, {.additional = A::ZamorakStaff});
        case W::CRUMBLE_UNDEAD: return spellRequirements({{AIR, 2}, {EARTH, 2}, {CHAOS, 1}});
        case W::IBAN_BLAST: return spellRequirements({{FIRE, 5}, {DEATH, 1}}, {.questId = Q::QUEST_UNDERGROUNDPASS, .additional = A::IbanStaff});
        case W::MAGIC_DART: return spellRequirements({{MIND, 4}, {DEATH, 1}}, {.additional = A::MagicDart});
        case W::CONFUSE: return spellRequirements({{EARTH, 2}, {WATER, 3}, {BODY, 1}});
        case W::WEAKEN: return spellRequirements({{EARTH, 2}, {WATER, 3}, {BODY, 1}});
        case W::CURSE: return spellRequirements({{EARTH, 3}, {WATER, 2}, {BODY, 1}});
        case W::BIND: return spellRequirements({{EARTH, 3}, {WATER, 3}, {NATURE, 2}}, {.spellSackId = I::BLIGHTED_SACK_ENTANGLE});
        case W::SNARE: return spellRequirements({{EARTH, 4}, {WATER, 4}, {NATURE, 3}}, {.spellSackId = I::BLIGHTED_SACK_ENTANGLE});
        case W::VULNERABILITY: return spellRequirements({{EARTH, 5}, {WATER, 5}, {SOUL, 1}});
        case W::ENFEEBLE: return spellRequirements({{EARTH, 8}, {WATER, 8}, {SOUL, 1}});
        case W::ENTANGLE: return spellRequirements({{EARTH, 5}, {WATER, 5}, {NATURE, 4}}, {.spellSackId = I::BLIGHTED_SACK_ENTANGLE});
        case W::STUN: return spellRequirements({{EARTH, 12}, {WATER, 12}, {SOUL, 1}});
        case W::TELEPORT_BLOCK: return spellRequirements({{CHAOS, 1}, {DEATH, 1}, {LAW, 1}}, {.spellSackId = I::BLIGHTED_SACK_TELEBLOCK});
        // shortcut: Charge's recast timer is not checked, add when reliable cooldown data is available.
        case W::CHARGE: return spellRequirements({{AIR, 3}, {FIRE, 3}, {BLOOD, 3}}, {.questId = Q::MINIQUEST_MAGEARENA1});
        case W::BONES_BANANAS: return spellRequirements({{EARTH, 2}, {WATER, 2}, {NATURE, 1}});
        case W::LOW_ALCHEMY: return spellRequirements({{FIRE, 3}, {NATURE, 1}});
        case W::SUPERHEAT: return spellRequirements({{FIRE, 4}, {NATURE, 1}});
        case W::HIGH_ALCHEMY: return spellRequirements({{FIRE, 5}, {NATURE, 1}});
        case W::BONES_PEACHES: return spellRequirements({{EARTH, 4}, {WATER, 4}, {NATURE, 2}}, {.unlockVarbit = V::MAGICTRAINING_BONESPEACHES});
        case W::MONSTER_INSPECT: return spellRequirements({{BODY, 2}, {MIND, 2}});
        case W::ENCHANT_1: return spellRequirements({{WATER, 1}, {COSMIC, 1}});
        case W::ENCHANT_2: return spellRequirements({{AIR, 3}, {COSMIC, 1}});
        case W::ENCHANT_3: return spellRequirements({{FIRE, 5}, {COSMIC, 1}});
        case W::ENCHANT_4: return spellRequirements({{EARTH, 10}, {COSMIC, 1}});
        case W::ENCHANT_5: return spellRequirements({{EARTH, 15}, {WATER, 15}, {COSMIC, 1}});
        case W::ENCHANT_6: return spellRequirements({{EARTH, 20}, {FIRE, 20}, {COSMIC, 1}});
        case W::ENCHANT_7: return spellRequirements({{BLOOD, 20}, {COSMIC, 1}, {SOUL, 20}});
        case W::CHARGE_WATER_ORB: return spellRequirements({{WATER, 30}, {COSMIC, 3}});
        case W::CHARGE_EARTH_ORB: return spellRequirements({{EARTH, 30}, {COSMIC, 3}});
        case W::CHARGE_FIRE_ORB: return spellRequirements({{FIRE, 30}, {COSMIC, 3}});
        case W::CHARGE_AIR_ORB: return spellRequirements({{AIR, 30}, {COSMIC, 3}});
        case W::TELEGRAB: return spellRequirements({{AIR, 1}, {LAW, 1}});
        }
        break;
    case SpellBook::Ancient:
        switch (widget) {
        case W::TELEPORT_HOME_ZAROS: return spellRequirements({}, {.additional = A::HomeTeleport});
        case W::ZAROSTELEPORT1: return spellRequirements({{AIR, 1}, {FIRE, 1}, {LAW, 2}});
        case W::ZAROSTELEPORT2: return spellRequirements({{LAW, 2}, {SOUL, 1}});
        case W::ZAROSTELEPORT3: return spellRequirements({{BLOOD, 1}, {LAW, 2}});
        case W::ZAROSTELEPORT4: return spellRequirements({{WATER, 4}, {LAW, 2}});
        case W::ZAROSTELEPORT5: return spellRequirements({{AIR, 2}, {FIRE, 3}, {LAW, 2}});
        case W::ZAROSTELEPORT6: return spellRequirements({{LAW, 2}, {SOUL, 2}});
        case W::ZAROSTELEPORT7: return spellRequirements({{BLOOD, 2}, {LAW, 2}});
        case W::ZAROSTELEPORT8: return spellRequirements({{WATER, 8}, {LAW, 2}});
        case W::SMOKE_RUSH: return spellRequirements({{AIR, 1}, {FIRE, 1}, {CHAOS, 2}, {DEATH, 2}});
        case W::SHADOW_RUSH: return spellRequirements({{AIR, 1}, {CHAOS, 2}, {DEATH, 2}, {SOUL, 1}});
        case W::BLOOD_RUSH: return spellRequirements({{BLOOD, 1}, {CHAOS, 2}, {DEATH, 2}});
        case W::ICE_RUSH: return spellRequirements({{WATER, 2}, {CHAOS, 2}, {DEATH, 2}}, {.spellSackId = I::BLIGHTED_SACK_ICEBARRAGE});
        case W::SMOKE_BURST: return spellRequirements({{AIR, 2}, {FIRE, 2}, {CHAOS, 4}, {DEATH, 2}});
        case W::SHADOW_BURST: return spellRequirements({{AIR, 1}, {CHAOS, 4}, {DEATH, 2}, {SOUL, 2}});
        case W::BLOOD_BURST: return spellRequirements({{BLOOD, 2}, {CHAOS, 4}, {DEATH, 2}});
        case W::ICE_BURST: return spellRequirements({{WATER, 4}, {CHAOS, 4}, {DEATH, 2}}, {.spellSackId = I::BLIGHTED_SACK_ICEBARRAGE});
        case W::SMOKE_BLITZ: return spellRequirements({{AIR, 2}, {FIRE, 2}, {BLOOD, 2}, {DEATH, 2}});
        case W::SHADOW_BLITZ: return spellRequirements({{AIR, 2}, {BLOOD, 2}, {DEATH, 2}, {SOUL, 2}});
        case W::BLOOD_BLITZ: return spellRequirements({{BLOOD, 4}, {DEATH, 2}});
        case W::ICE_BLITZ: return spellRequirements({{WATER, 3}, {BLOOD, 2}, {DEATH, 2}}, {.spellSackId = I::BLIGHTED_SACK_ICEBARRAGE});
        case W::SMOKE_BARRAGE: return spellRequirements({{AIR, 4}, {FIRE, 4}, {BLOOD, 2}, {DEATH, 4}});
        case W::SHADOW_BARRAGE: return spellRequirements({{AIR, 4}, {BLOOD, 2}, {DEATH, 4}, {SOUL, 3}});
        case W::BLOOD_BARRAGE: return spellRequirements({{BLOOD, 4}, {DEATH, 4}, {SOUL, 1}});
        case W::ICE_BARRAGE: return spellRequirements({{WATER, 6}, {BLOOD, 2}, {DEATH, 4}}, {.spellSackId = I::BLIGHTED_SACK_ICEBARRAGE});
        }
        break;
    case SpellBook::Lunar:
        switch (widget) {
        case W::TELEPORT_HOME_LUNAR: return spellRequirements({}, {.additional = A::HomeTeleport});
        case W::TELE_MOONCLAN: return spellRequirements({{EARTH, 2}, {ASTRAL, 2}, {LAW, 1}});
        case W::TELE_GROUP_MOONCLAN: return spellRequirements({{EARTH, 4}, {ASTRAL, 2}, {LAW, 1}});
        case W::OURANIA_TELEPORT: return spellRequirements({{EARTH, 6}, {ASTRAL, 2}, {LAW, 1}}, {.unlockVarbit = V::RC_ZMI_LUNAR_SPELL});
        case W::TELE_WATERBIRTH: return spellRequirements({{WATER, 1}, {ASTRAL, 2}, {LAW, 1}});
        case W::TELE_GROUP_WATERBIRTH: return spellRequirements({{WATER, 5}, {ASTRAL, 2}, {LAW, 1}});
        case W::TELE_BARB_OUT: return spellRequirements({{FIRE, 3}, {ASTRAL, 2}, {LAW, 2}});
        case W::TELE_GROUP_BARBARIAN: return spellRequirements({{FIRE, 6}, {ASTRAL, 2}, {LAW, 2}});
        case W::TELE_KHAZARD: return spellRequirements({{WATER, 4}, {ASTRAL, 2}, {LAW, 2}});
        case W::TELE_GROUP_KHAZARD: return spellRequirements({{WATER, 8}, {ASTRAL, 2}, {LAW, 2}});
        case W::TELE_FISH: return spellRequirements({{WATER, 10}, {ASTRAL, 3}, {LAW, 3}});
        case W::TELE_GROUP_FISHING_GUILD: return spellRequirements({{WATER, 14}, {ASTRAL, 3}, {LAW, 3}});
        case W::TELE_CATHER: return spellRequirements({{WATER, 10}, {ASTRAL, 3}, {LAW, 3}});
        case W::TELE_GROUP_CATHERBY: return spellRequirements({{WATER, 15}, {ASTRAL, 3}, {LAW, 3}});
        case W::TELE_GHORROCK: return spellRequirements({{WATER, 8}, {ASTRAL, 3}, {LAW, 3}});
        case W::TELE_GROUP_GHORROCK: return spellRequirements({{WATER, 16}, {ASTRAL, 3}, {LAW, 3}});
        case W::MONSTER_EXAMINE: return spellRequirements({{ASTRAL, 1}, {COSMIC, 1}, {MIND, 1}}, {.questId = Q::QUEST_DREAMMENTOR});
        case W::CURE_OTHER: return spellRequirements({{EARTH, 10}, {ASTRAL, 1}, {LAW, 1}});
        case W::CURE_ME: return spellRequirements({{ASTRAL, 2}, {COSMIC, 2}, {LAW, 1}});
        case W::CURE_GROUP: return spellRequirements({{ASTRAL, 2}, {COSMIC, 2}, {LAW, 2}});
        case W::STAT_SPY: return spellRequirements({{ASTRAL, 2}, {BODY, 5}, {COSMIC, 2}}, {.questId = Q::QUEST_DREAMMENTOR});
        case W::DREAM: return spellRequirements({{ASTRAL, 2}, {BODY, 5}, {COSMIC, 1}}, {.questId = Q::QUEST_DREAMMENTOR});
        case W::REST_POT_SHARE: return spellRequirements({{EARTH, 10}, {WATER, 10}, {ASTRAL, 2}});
        case W::STREN_POT_SHARE: return spellRequirements({{EARTH, 12}, {WATER, 10}, {ASTRAL, 3}});
        case W::ENERGY_TRANS: return spellRequirements({{ASTRAL, 3}, {LAW, 2}, {NATURE, 1}}, {.additional = A::EnergyTransfer});
        case W::HEAL_OTHER: return spellRequirements({{ASTRAL, 3}, {BLOOD, 1}, {LAW, 3}}, {.additional = A::HealOther});
        case W::VENGEANCE_OTHER: return spellRequirements({{EARTH, 10}, {ASTRAL, 3}, {DEATH, 2}}, {.cooldownVarbit = V::VENGEANCE_TIMELIMIT, .spellSackId = I::BLIGHTED_SACK_VENGEANCE});
        case W::VENGEANCE: return spellRequirements({{EARTH, 10}, {ASTRAL, 4}, {DEATH, 2}}, {.cooldownVarbit = V::VENGEANCE_TIMELIMIT, .spellSackId = I::BLIGHTED_SACK_VENGEANCE});
        case W::HEAL_GROUP: return spellRequirements({{ASTRAL, 4}, {BLOOD, 3}, {LAW, 6}});
        case W::BAKE_PIE: return spellRequirements({{FIRE, 5}, {WATER, 4}, {ASTRAL, 1}});
        case W::GEOMANCY: return spellRequirements({{EARTH, 8}, {ASTRAL, 3}, {NATURE, 3}});
        case W::CURE_PLANT: return spellRequirements({{EARTH, 8}, {ASTRAL, 1}});
        case W::NPC_CONTACT: return spellRequirements({{AIR, 2}, {ASTRAL, 1}, {COSMIC, 1}});
        case W::HUMIDIFY: return spellRequirements({{FIRE, 1}, {WATER, 3}, {ASTRAL, 1}}, {.questId = Q::QUEST_DREAMMENTOR});
        case W::HUNTER_KIT: return spellRequirements({{EARTH, 2}, {ASTRAL, 2}}, {.questId = Q::QUEST_DREAMMENTOR});
        case W::SPIN_FLAX: return spellRequirements({{AIR, 5}, {ASTRAL, 1}, {NATURE, 2}});
        case W::SUPERGLASS: return spellRequirements({{AIR, 10}, {FIRE, 6}, {ASTRAL, 2}});
        case W::TAN_LEATHER: return spellRequirements({{FIRE, 5}, {ASTRAL, 2}, {NATURE, 1}}, {.unlockVarbit = V::FREMENNIK_DIARY_HARD_COMPLETE});
        case W::STRING_JEWEL: return spellRequirements({{EARTH, 10}, {WATER, 5}, {ASTRAL, 2}});
        case W::MAGIC_IMBUE: return spellRequirements({{FIRE, 7}, {WATER, 7}, {ASTRAL, 2}});
        case W::FERTILE_SOIL: return spellRequirements({{EARTH, 15}, {ASTRAL, 3}, {NATURE, 2}});
        case W::PLANK_MAKE: return spellRequirements({{EARTH, 15}, {ASTRAL, 2}, {NATURE, 1}}, {.questId = Q::QUEST_DREAMMENTOR});
        case W::RECHARGE_DRAGONSTONE: return spellRequirements({{WATER, 4}, {ASTRAL, 1}, {SOUL, 1}}, {.unlockVarbit = V::FREMENNIK_DIARY_HARD_COMPLETE});
        case W::SPELLBOOK_SWAP: return spellRequirements({{ASTRAL, 3}, {COSMIC, 2}, {LAW, 1}}, {.questId = Q::QUEST_DREAMMENTOR});
        }
        break;
    case SpellBook::Necromancy:
        switch (widget) {
        case W::TELEPORT_HOME_ARCEUUS: return spellRequirements({}, {.additional = A::HomeTeleport});
        case W::TELEPORT_ARCEUUS_LIBRARY: return spellRequirements({{EARTH, 2}, {LAW, 1}});
        case W::TELEPORT_DRAYNOR_MANOR: return spellRequirements({{EARTH, 1}, {WATER, 1}, {LAW, 1}});
        case W::TELEPORT_BATTLEFRONT: return spellRequirements({{EARTH, 1}, {FIRE, 1}, {LAW, 1}});
        case W::TELEPORT_MIND_ALTAR: return spellRequirements({{LAW, 1}, {MIND, 2}});
        case W::TELEPORT_RESPAWN: return spellRequirements({{LAW, 1}, {SOUL, 1}});
        case W::TELEPORT_SALVE_GRAVEYARD: return spellRequirements({{LAW, 1}, {SOUL, 2}}, {.questId = Q::QUEST_PRIESTINPERIL});
        case W::TELEPORT_FENKENSTRAIN_CASTLE: return spellRequirements({{EARTH, 1}, {LAW, 1}, {SOUL, 1}}, {.questId = Q::QUEST_PRIESTINPERIL});
        case W::TELEPORT_WEST_ARDOUGNE: return spellRequirements({{LAW, 2}, {SOUL, 2}}, {.questId = Q::QUEST_BIOHAZARD});
        case W::TELEPORT_HARMONY_ISLAND: return spellRequirements({{LAW, 1}, {NATURE, 1}, {SOUL, 1}}, {.questId = Q::QUEST_GREATBRAINROBBERY});
        case W::TELEPORT_CEMETERY: return spellRequirements({{BLOOD, 1}, {LAW, 1}, {SOUL, 1}});
        case W::TELEPORT_BARROWS: return spellRequirements({{BLOOD, 1}, {LAW, 2}, {SOUL, 2}}, {.questId = Q::QUEST_PRIESTINPERIL});
        case W::TELEPORT_APE_ATOLL_DUNGEON: return spellRequirements({{BLOOD, 2}, {LAW, 2}, {SOUL, 2}}, {.questId = Q::QUEST_MONKEYMADNESS1});
        case W::GHOSTLY_GRASP: return spellRequirements({{AIR, 4}, {CHAOS, 1}});
        case W::SKELETAL_GRASP: return spellRequirements({{EARTH, 8}, {DEATH, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED});
        case W::UNDEAD_GRASP: return spellRequirements({{FIRE, 12}, {BLOOD, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED});
        case W::INFERIOR_DEMONBANE: return spellRequirements({{FIRE, 3}, {CHAOS, 1}});
        case W::SUPERIOR_DEMONBANE: return spellRequirements({{FIRE, 5}, {SOUL, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED});
        case W::DARK_DEMONBANE: return spellRequirements({{FIRE, 7}, {SOUL, 2}}, {.questId = Q::QUEST_KINGDOMDIVIDED});
        case W::LESSER_CORRUPTION: return spellRequirements({{DEATH, 1}, {SOUL, 2}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_CORRUPTION_COOLDOWN});
        case W::GREATER_CORRUPTION: return spellRequirements({{BLOOD, 1}, {SOUL, 3}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_CORRUPTION_COOLDOWN});
        case W::RESURRECT_LESSER_GHOST:
        case W::RESURRECT_LESSER_SKELETON:
        case W::RESURRECT_LESSER_ZOMBIE: return spellRequirements({{AIR, 10}, {COSMIC, 1}, {MIND, 5}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_RESURRECTION_COOLDOWN, .additional = A::ThrallLesser});
        case W::RESURRECT_SUPERIOR_GHOST:
        case W::RESURRECT_SUPERIOR_SKELETON:
        case W::RESURRECT_SUPERIOR_ZOMBIE: return spellRequirements({{EARTH, 10}, {COSMIC, 1}, {DEATH, 5}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_RESURRECTION_COOLDOWN, .additional = A::ThrallSuperior});
        case W::RESURRECT_GREATER_GHOST:
        case W::RESURRECT_GREATER_SKELETON:
        case W::RESURRECT_GREATER_ZOMBIE: return spellRequirements({{FIRE, 10}, {BLOOD, 5}, {COSMIC, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_RESURRECTION_COOLDOWN, .additional = A::ThrallGreater});
        case W::DARK_LURE: return spellRequirements({{DEATH, 1}, {NATURE, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_DARK_LURE_COOLDOWN});
        case W::MARK_OF_DARKNESS: return spellRequirements({{COSMIC, 1}, {SOUL, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED});
        case W::WARD_OF_ARCEUUS: return spellRequirements({{COSMIC, 1}, {NATURE, 2}, {SOUL, 4}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_WARD_COOLDOWN});
        case W::REANIMATION_BASIC: return spellRequirements({{BODY, 4}, {NATURE, 2}});
        case W::REANIMATION_ADEPT: return spellRequirements({{BODY, 4}, {NATURE, 3}, {SOUL, 1}});
        case W::REANIMATION_EXPERT: return spellRequirements({{BLOOD, 1}, {NATURE, 3}, {SOUL, 2}});
        case W::REANIMATION_MASTER: return spellRequirements({{BLOOD, 2}, {NATURE, 4}, {SOUL, 4}});
        case W::DEMONIC_OFFERING: return spellRequirements({{SOUL, 1}, {WRATH, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_OFFERING_COOLDOWN});
        case W::SINISTER_OFFERING: return spellRequirements({{BLOOD, 1}, {WRATH, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_OFFERING_COOLDOWN});
        case W::SHADOW_VEIL: return spellRequirements({{EARTH, 5}, {FIRE, 5}, {COSMIC, 5}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_SHADOW_VEIL_COOLDOWN});
        case W::VILE_VIGOUR: return spellRequirements({{AIR, 3}, {SOUL, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_VILE_VIGOUR_COOLDOWN, .additional = A::VileVigour});
        case W::DEGRIME: return spellRequirements({{EARTH, 4}, {NATURE, 2}}, {.questId = Q::QUEST_KINGDOMDIVIDED});
        case W::RESURRECT_CROPS: return spellRequirements({{EARTH, 25}, {BLOOD, 8}, {NATURE, 12}, {SOUL, 8}});
        case W::DEATH_CHARGE: return spellRequirements({{BLOOD, 1}, {DEATH, 1}, {SOUL, 1}}, {.questId = Q::QUEST_KINGDOMDIVIDED, .cooldownVarbit = V::ARCEUUS_DEATH_CHARGE_COOLDOWN, .additional = A::DeathCharge});
        }
        break;
    }
    return {};
}

} // namespace titan::utils::Magic
