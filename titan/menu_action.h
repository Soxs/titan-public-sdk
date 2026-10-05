/// @file titan/menu_action.h
/// @brief Menu action opcodes used by the game's DoAction dispatch.
///
/// Canonical SDK location for the `MenuAction` opcode catalog. Every
/// right-click menu option in OSRS maps to a numeric opcode that
/// DoAction uses to determine what to do. Opcodes >= `kDeprioritizeOffset`
/// are de-prioritised duplicates (the base opcode + 2000).
///
/// Numeric values align with `net.runelite.api.MenuAction` where applicable.
/// Opcode 31 is both walk-here (deobfuscated client) and RuneLite's deprecated
/// `ITEM_USE_ON_ITEM`; both names are provided with the same underlying value.
///
/// Used by `titan::state::widgets().interact(...)` and the new
/// `titan::state::client().invokeMenuAction(...)` fluent helper; the latter
/// accepts either the `Id` enum directly or a fully-populated `Entry`
/// struct for one-shot synthetic dispatch.
///
/// Client-internal code can include `client/game/types/menu_action.h`,
/// which forwards this header into the project's top-level
/// `MenuActionDef` namespace alias to preserve existing callsites.

#pragma once

#include <cstdint>

namespace titan {

/// Menu action opcode definitions and classification helpers.
namespace MenuAction {

/// Opcodes at or above this value are de-prioritised variants (base + 2000).
constexpr uint32_t kDeprioritizeOffset = 2000;

/// Known DoAction opcodes for entity interactions, walking, and widgets.
/// All values explicit so ordering in the source matches RuneLite numbering.
enum class Id : uint32_t {
    ItemUseOnGameObject = 1,
    WidgetTargetOnGameObject = 2,

    GameObjectFirstOption = 3,
    GameObjectSecondOption = 4,
    GameObjectThirdOption = 5,
    GameObjectFourthOption = 6,
    GameObjectFifthOption = 1001,

    ItemUseOnNpc = 7,
    WidgetTargetOnNpc = 8,

    NpcFirstOption = 9,
    NpcSecondOption = 10,
    NpcThirdOption = 11,
    NpcFourthOption = 12,
    NpcFifthOption = 13,

    ItemUseOnPlayer = 14,
    WidgetTargetOnPlayer = 15,

    ItemUseOnGroundItem = 16,
    WidgetTargetOnGroundItem = 17,

    GroundItemFirstOption = 18,
    GroundItemSecondOption = 19,
    GroundItemThirdOption = 20,
    GroundItemFourthOption = 21,
    GroundItemFifthOption = 22,

    Walk = 23,
    WidgetType1 = 24,
    WidgetTarget = 25,
    WidgetClose = 26,
    WidgetType4 = 28,
    WidgetType5 = 29,
    WidgetContinue = 30,

    /// Walk-here / scene click (native client); same numeric id as RuneLite `ITEM_USE_ON_ITEM` (deprecated).
    WalkHere = 31,
    ItemUseOnItem = WalkHere,

    WidgetUseOnItem = 32,

    ItemUse = 38,
    WidgetFirstOption = 39,
    WidgetSecondOption = 40,
    WidgetThirdOption = 41,
    WidgetFourthOption = 42,
    WidgetFifthOption = 43,

    PlayerFirstOption = 44,
    PlayerSecondOption = 45,
    PlayerThirdOption = 46,
    PlayerFourthOption = 47,
    PlayerFifthOption = 48,
    PlayerSixthOption = 49,
    PlayerSeventhOption = 50,
    PlayerEighthOption = 51,

    CcOp = 57,
    WidgetTargetOnWidget = 58,

    SetHeading = 60,
    WorldEntityFirstOption = 63,
    WorldEntitySecondOption = 64,
    WorldEntityThirdOption = 65,
    WorldEntityFourthOption = 66,
    WorldEntityFifthOption = 67,

    ExamineObject = 1002,
    ExamineNpc = 1003,
    ExamineItemGround = 1004,
    ExamineItem = 1005,
    Cancel = 1006,
    CcOpLowPriority = 1007,
    ExamineWorldEntity = 1013
};

/// Strip the de-prioritise offset if present, returning the base opcode.
inline uint32_t normalize(uint32_t opcode) {
    return opcode >= kDeprioritizeOffset ? (opcode - kDeprioritizeOffset) : opcode;
}

/// @return True if the opcode belongs to the widget / CC_OP family --
///         i.e. an opcode whose `param1` is a packed widget id and whose
///         native handler dereferences `arg11[0]` (a widget pointer)
///         and reads `arg11[1]` (an aux struct where `aux[+4] = param1`).
///
/// This is the gate the synthetic-dispatch router uses to decide whether
/// to call the game's native arg11 builder (which resolves the widget
/// from `param1` and populates the aux ctx) vs. pass a zeroed ctx.
/// Captured `DoAction` traces confirm every opcode below requires the
/// builder path -- omitting any of them lands a `{0,0}` arg11 in the
/// game's handler and the action becomes a silent no-op.
inline bool isWidgetCcFamily(uint32_t opcode) {
    const uint32_t n = normalize(opcode);
    return n == static_cast<uint32_t>(Id::WidgetType1)
        || n == static_cast<uint32_t>(Id::WidgetTarget)
        || n == static_cast<uint32_t>(Id::WidgetClose)
        || n == static_cast<uint32_t>(Id::WidgetType4)
        || n == static_cast<uint32_t>(Id::WidgetType5)
        || n == static_cast<uint32_t>(Id::WidgetContinue)
        || n == static_cast<uint32_t>(Id::WidgetUseOnItem)
        || n == static_cast<uint32_t>(Id::ItemUse)
        || n == static_cast<uint32_t>(Id::WidgetFirstOption)
        || n == static_cast<uint32_t>(Id::WidgetSecondOption)
        || n == static_cast<uint32_t>(Id::WidgetThirdOption)
        || n == static_cast<uint32_t>(Id::WidgetFourthOption)
        || n == static_cast<uint32_t>(Id::WidgetFifthOption)
        || n == static_cast<uint32_t>(Id::CcOp)
        || n == static_cast<uint32_t>(Id::WidgetTargetOnWidget)
        || n == static_cast<uint32_t>(Id::CcOpLowPriority);
}

/// @return True if the opcode is a CC_OP or CC_OP-adjacent action.
inline bool isCcOpFamily(uint32_t opcode) {
    const uint32_t n = normalize(opcode);
    return n == static_cast<uint32_t>(Id::CcOp)
        || n == static_cast<uint32_t>(Id::CcOpLowPriority)
        || n == static_cast<uint32_t>(Id::WidgetTargetOnWidget);
}

/// @return Debug-friendly string name for the given opcode.
inline const char* nameFor(uint32_t opcode) {
    switch (normalize(opcode)) {
        case static_cast<uint32_t>(Id::ItemUseOnGameObject): return "ITEM_USE_ON_GAME_OBJECT";
        case static_cast<uint32_t>(Id::WidgetTargetOnGameObject): return "WIDGET_TARGET_ON_GAME_OBJECT";
        case static_cast<uint32_t>(Id::GameObjectFirstOption): return "GAME_OBJECT_FIRST_OPTION";
        case static_cast<uint32_t>(Id::GameObjectSecondOption): return "GAME_OBJECT_SECOND_OPTION";
        case static_cast<uint32_t>(Id::GameObjectThirdOption): return "GAME_OBJECT_THIRD_OPTION";
        case static_cast<uint32_t>(Id::GameObjectFourthOption): return "GAME_OBJECT_FOURTH_OPTION";
        case static_cast<uint32_t>(Id::GameObjectFifthOption): return "GAME_OBJECT_FIFTH_OPTION";
        case static_cast<uint32_t>(Id::ItemUseOnNpc): return "ITEM_USE_ON_NPC";
        case static_cast<uint32_t>(Id::WidgetTargetOnNpc): return "WIDGET_TARGET_ON_NPC";
        case static_cast<uint32_t>(Id::NpcFirstOption): return "NPC_FIRST_OPTION";
        case static_cast<uint32_t>(Id::NpcSecondOption): return "NPC_SECOND_OPTION";
        case static_cast<uint32_t>(Id::NpcThirdOption): return "NPC_THIRD_OPTION";
        case static_cast<uint32_t>(Id::NpcFourthOption): return "NPC_FOURTH_OPTION";
        case static_cast<uint32_t>(Id::NpcFifthOption): return "NPC_FIFTH_OPTION";
        case static_cast<uint32_t>(Id::ItemUseOnPlayer): return "ITEM_USE_ON_PLAYER";
        case static_cast<uint32_t>(Id::WidgetTargetOnPlayer): return "WIDGET_TARGET_ON_PLAYER";
        case static_cast<uint32_t>(Id::ItemUseOnGroundItem): return "ITEM_USE_ON_GROUND_ITEM";
        case static_cast<uint32_t>(Id::WidgetTargetOnGroundItem): return "WIDGET_TARGET_ON_GROUND_ITEM";
        case static_cast<uint32_t>(Id::GroundItemFirstOption): return "GROUND_ITEM_FIRST_OPTION";
        case static_cast<uint32_t>(Id::GroundItemSecondOption): return "GROUND_ITEM_SECOND_OPTION";
        case static_cast<uint32_t>(Id::GroundItemThirdOption): return "GROUND_ITEM_THIRD_OPTION";
        case static_cast<uint32_t>(Id::GroundItemFourthOption): return "GROUND_ITEM_FOURTH_OPTION";
        case static_cast<uint32_t>(Id::GroundItemFifthOption): return "GROUND_ITEM_FIFTH_OPTION";
        case static_cast<uint32_t>(Id::Walk): return "WALK";
        case static_cast<uint32_t>(Id::WidgetType1): return "WIDGET_TYPE_1";
        case static_cast<uint32_t>(Id::WidgetTarget): return "WIDGET_TARGET";
        case static_cast<uint32_t>(Id::WidgetClose): return "WIDGET_CLOSE";
        case static_cast<uint32_t>(Id::WidgetType4): return "WIDGET_TYPE_4";
        case static_cast<uint32_t>(Id::WidgetType5): return "WIDGET_TYPE_5";
        case static_cast<uint32_t>(Id::WidgetContinue): return "WIDGET_CONTINUE";
        case static_cast<uint32_t>(Id::WalkHere): return "WALK_HERE";
        case static_cast<uint32_t>(Id::WidgetUseOnItem): return "WIDGET_USE_ON_ITEM";
        case static_cast<uint32_t>(Id::ItemUse): return "ITEM_USE";
        case static_cast<uint32_t>(Id::WidgetFirstOption): return "WIDGET_FIRST_OPTION";
        case static_cast<uint32_t>(Id::WidgetSecondOption): return "WIDGET_SECOND_OPTION";
        case static_cast<uint32_t>(Id::WidgetThirdOption): return "WIDGET_THIRD_OPTION";
        case static_cast<uint32_t>(Id::WidgetFourthOption): return "WIDGET_FOURTH_OPTION";
        case static_cast<uint32_t>(Id::WidgetFifthOption): return "WIDGET_FIFTH_OPTION";
        case static_cast<uint32_t>(Id::PlayerFirstOption): return "PLAYER_FIRST_OPTION";
        case static_cast<uint32_t>(Id::PlayerSecondOption): return "PLAYER_SECOND_OPTION";
        case static_cast<uint32_t>(Id::PlayerThirdOption): return "PLAYER_THIRD_OPTION";
        case static_cast<uint32_t>(Id::PlayerFourthOption): return "PLAYER_FOURTH_OPTION";
        case static_cast<uint32_t>(Id::PlayerFifthOption): return "PLAYER_FIFTH_OPTION";
        case static_cast<uint32_t>(Id::PlayerSixthOption): return "PLAYER_SIXTH_OPTION";
        case static_cast<uint32_t>(Id::PlayerSeventhOption): return "PLAYER_SEVENTH_OPTION";
        case static_cast<uint32_t>(Id::PlayerEighthOption): return "PLAYER_EIGHTH_OPTION";
        case static_cast<uint32_t>(Id::CcOp): return "CC_OP";
        case static_cast<uint32_t>(Id::WidgetTargetOnWidget): return "WIDGET_TARGET_ON_WIDGET";
        case static_cast<uint32_t>(Id::SetHeading): return "SET_HEADING";
        case static_cast<uint32_t>(Id::WorldEntityFirstOption): return "WORLD_ENTITY_FIRST_OPTION";
        case static_cast<uint32_t>(Id::WorldEntitySecondOption): return "WORLD_ENTITY_SECOND_OPTION";
        case static_cast<uint32_t>(Id::WorldEntityThirdOption): return "WORLD_ENTITY_THIRD_OPTION";
        case static_cast<uint32_t>(Id::WorldEntityFourthOption): return "WORLD_ENTITY_FOURTH_OPTION";
        case static_cast<uint32_t>(Id::WorldEntityFifthOption): return "WORLD_ENTITY_FIFTH_OPTION";
        case static_cast<uint32_t>(Id::ExamineObject): return "EXAMINE_OBJECT";
        case static_cast<uint32_t>(Id::ExamineNpc): return "EXAMINE_NPC";
        case static_cast<uint32_t>(Id::ExamineItemGround): return "EXAMINE_ITEM_GROUND";
        case static_cast<uint32_t>(Id::ExamineItem): return "EXAMINE_ITEM";
        case static_cast<uint32_t>(Id::Cancel): return "CANCEL";
        case static_cast<uint32_t>(Id::CcOpLowPriority): return "CC_OP_LOW_PRIORITY";
        case static_cast<uint32_t>(Id::ExamineWorldEntity): return "EXAMINE_WORLD_ENTITY";
        default: return "UNKNOWN";
    }
}

/// Fully-specified synthetic menu entry -- direct mirror of
/// `TitanPluginSdk::SyntheticActionEntry` but expressed with the strong
/// `Id` enum and plugin-friendly defaults. Pass to
/// `titan::state::client().invokeMenuAction(entry)` for one-shot dispatch.
/// `identifier` is the menu-entry identity field.
struct Entry {
    Id opcode = Id::Cancel;
    int32_t identifier = 0;
    int32_t param0 = 0;
    int32_t param1 = 0;
    int32_t worldViewId = -1;   ///< -1 -> use the game's current world view (0)
    /// -1/-1 lets Titan resolve click coords. Entity/world target opcodes use
    /// their native clickboxes; other opcodes use randomized active-screen coords.
    int32_t clickX = -1;
    int32_t clickY = -1;
    const char* actionText = "";
    const char* targetText = "";
    /// Skip the synthetic click phase and dispatch DoAction directly.
    bool skipClick = false;
    /// Optional target metadata for native clickbox resolution. Fill these
    /// when the caller already has the entity/object snapshot.
    int32_t targetPlane = -1;
    int32_t targetSizeX = 1;
    int32_t targetSizeY = 1;
    int32_t targetLayer = -1;
    uint64_t targetEntityPtr = 0;
    uint64_t targetPackedId = 0;
};

}  // namespace MenuAction

}  // namespace titan
