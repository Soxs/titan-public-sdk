#pragma once

// Native ABI v1 payload golden contract (Windows x64).
// Captured from the pre-release v1 baseline, never regenerated at build time.
// These constants protect record stride, field types/array extents,
// alignment, enum representations, and numeric meanings. Do not update an
// existing expectation to accommodate a mutation. A deliberate layout break
// updates its expectations only in the same change that raises
// kMinSupportedSdkVersion (SDK 146: WidgetState modelType/modelId), so the
// host refuses every DLL built against the old layout. UI payloads have
// equivalent checks in native_records.h; native_abi.h freezes function-table
// layouts.
#include "abi.h"

#include <cstddef>
#include <cstdint>
#include <type_traits>

namespace TitanPluginSdk::NativeAbi::PayloadV1 {

static_assert(sizeof(void*) == 8, "Native ABI v1 supports Windows x64");
static_assert(sizeof(bool) == 1 && sizeof(float) == 4);

#define TITAN_NATIVE_RECORD(type, bytes, alignment) \
    static_assert(std::is_standard_layout_v<type> && std::is_trivially_copyable_v<type>); \
    static_assert(sizeof(type) == bytes && alignof(type) == alignment, "Native ABI v1 record layout changed")
#define TITAN_NATIVE_FIELD(type, field, offset, fieldType) \
    static_assert(offsetof(type, field) == offset, "Native ABI v1 field offset changed"); \
    static_assert(std::is_same_v<decltype(type::field), fieldType>, "Native ABI v1 field type changed")
#define TITAN_NATIVE_ENUM(type, underlying) \
    static_assert(std::is_same_v<std::underlying_type_t<type>, underlying>, "Native ABI v1 enum representation changed")
#define TITAN_NATIVE_VALUE(name, value) \
    static_assert(static_cast<int64_t>(name) == value, "Native ABI v1 numeric contract changed")

TITAN_NATIVE_RECORD(ProjectileActorRefAbi, 8, 4);
TITAN_NATIVE_FIELD(ProjectileActorRefAbi, hashIndex, 0, int32_t);
TITAN_NATIVE_FIELD(ProjectileActorRefAbi, entityType, 4, int32_t);

TITAN_NATIVE_RECORD(OverlayPanelStyleAbi, 48, 4);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, background, 0, uint32_t);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, borderColor, 4, uint32_t);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, borderThickness, 8, float);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, cornerRadius, 12, float);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, padHorizontal, 16, int32_t);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, padVertical, 20, int32_t);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, lineGap, 24, int32_t);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, titleColor, 28, uint32_t);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, lineLeftColor, 32, uint32_t);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, lineRightColor, 36, uint32_t);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, barFillColor, 40, uint32_t);
TITAN_NATIVE_FIELD(OverlayPanelStyleAbi, barBgColor, 44, uint32_t);

TITAN_NATIVE_RECORD(ClientState, 112, 8);
TITAN_NATIVE_FIELD(ClientState, clientBase, 0, uint64_t);
TITAN_NATIVE_FIELD(ClientState, worldViewPtr, 8, uint64_t);
TITAN_NATIVE_FIELD(ClientState, scenePtr, 16, uint64_t);
TITAN_NATIVE_FIELD(ClientState, localPlayerEntity, 24, uint64_t);
TITAN_NATIVE_FIELD(ClientState, tickCount, 32, int32_t);
TITAN_NATIVE_FIELD(ClientState, plane, 36, int32_t);
TITAN_NATIVE_FIELD(ClientState, localPlayerIndex, 40, int32_t);
TITAN_NATIVE_FIELD(ClientState, playerCount, 44, int32_t);
TITAN_NATIVE_FIELD(ClientState, baseX, 48, int32_t);
TITAN_NATIVE_FIELD(ClientState, baseY, 52, int32_t);
TITAN_NATIVE_FIELD(ClientState, runEnergy, 56, int32_t);
TITAN_NATIVE_FIELD(ClientState, weight, 60, int32_t);
TITAN_NATIVE_FIELD(ClientState, sceneSizeX, 64, int32_t);
TITAN_NATIVE_FIELD(ClientState, sceneSizeY, 68, int32_t);
TITAN_NATIVE_FIELD(ClientState, currentWorldViewId, 72, int32_t);
TITAN_NATIVE_FIELD(ClientState, topLevelBaseX, 76, int32_t);
TITAN_NATIVE_FIELD(ClientState, topLevelBaseY, 80, int32_t);
TITAN_NATIVE_FIELD(ClientState, topLevelPlane, 84, int32_t);
TITAN_NATIVE_FIELD(ClientState, topLevelSceneSizeX, 88, int32_t);
TITAN_NATIVE_FIELD(ClientState, topLevelSceneSizeY, 92, int32_t);
TITAN_NATIVE_FIELD(ClientState, topLevelLocalPlayerTileX, 96, int32_t);
TITAN_NATIVE_FIELD(ClientState, topLevelLocalPlayerTileY, 100, int32_t);
TITAN_NATIVE_FIELD(ClientState, topLevelLocalPlayerPlane, 104, int32_t);
TITAN_NATIVE_FIELD(ClientState, topLevelLocalPlayerTileValid, 108, uint8_t);

TITAN_NATIVE_RECORD(WorldPointState, 16, 4);
TITAN_NATIVE_FIELD(WorldPointState, x, 0, int32_t);
TITAN_NATIVE_FIELD(WorldPointState, y, 4, int32_t);
TITAN_NATIVE_FIELD(WorldPointState, z, 8, int32_t);
TITAN_NATIVE_FIELD(WorldPointState, worldViewId, 12, int32_t);

TITAN_NATIVE_RECORD(LocalPointState, 12, 4);
TITAN_NATIVE_FIELD(LocalPointState, x, 0, int32_t);
TITAN_NATIVE_FIELD(LocalPointState, y, 4, int32_t);
TITAN_NATIVE_FIELD(LocalPointState, worldViewId, 8, int32_t);

TITAN_NATIVE_RECORD(WorldMapState, 76, 4);
TITAN_NATIVE_FIELD(WorldMapState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(WorldMapState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(WorldMapState, globalCenterX, 8, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, globalCenterY, 12, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, currentZoom, 16, float);
TITAN_NATIVE_FIELD(WorldMapState, targetZoom, 20, float);
TITAN_NATIVE_FIELD(WorldMapState, pixelsPerTile, 24, float);
TITAN_NATIVE_FIELD(WorldMapState, viewportX, 28, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, viewportY, 32, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, viewportWidth, 36, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, viewportHeight, 40, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, logicalViewportX, 44, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, logicalViewportY, 48, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, logicalViewportWidth, 52, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, logicalViewportHeight, 56, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, interfaceScaleX, 60, float);
TITAN_NATIVE_FIELD(WorldMapState, interfaceScaleY, 64, float);
TITAN_NATIVE_FIELD(WorldMapState, canvasOriginX, 68, int32_t);
TITAN_NATIVE_FIELD(WorldMapState, canvasOriginY, 72, int32_t);

TITAN_NATIVE_RECORD(InstanceTemplateChunksState, 2708, 4);
TITAN_NATIVE_FIELD(InstanceTemplateChunksState, chunks, 0, int32_t[676]);
TITAN_NATIVE_FIELD(InstanceTemplateChunksState, instanced, 2704, uint8_t);

TITAN_NATIVE_RECORD(CollisionSceneSnapshotState, 2740, 4);
TITAN_NATIVE_FIELD(CollisionSceneSnapshotState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(CollisionSceneSnapshotState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(CollisionSceneSnapshotState, baseX, 8, int32_t);
TITAN_NATIVE_FIELD(CollisionSceneSnapshotState, baseY, 12, int32_t);
TITAN_NATIVE_FIELD(CollisionSceneSnapshotState, width, 16, int32_t);
TITAN_NATIVE_FIELD(CollisionSceneSnapshotState, height, 20, int32_t);
TITAN_NATIVE_FIELD(CollisionSceneSnapshotState, worldViewId, 24, int32_t);
TITAN_NATIVE_FIELD(CollisionSceneSnapshotState, flagCount, 28, uint32_t);
TITAN_NATIVE_FIELD(CollisionSceneSnapshotState, templates, 32, InstanceTemplateChunksState);

TITAN_NATIVE_RECORD(WebPathRequestState, 56, 4);
TITAN_NATIVE_FIELD(WebPathRequestState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(WebPathRequestState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(WebPathRequestState, start, 8, WorldPointState);
TITAN_NATIVE_FIELD(WebPathRequestState, destination, 24, WorldPointState);
TITAN_NATIVE_FIELD(WebPathRequestState, routeSpace, 40, uint32_t);
TITAN_NATIVE_FIELD(WebPathRequestState, options, 44, uint32_t);
TITAN_NATIVE_FIELD(WebPathRequestState, timeoutMs, 48, uint32_t);
TITAN_NATIVE_FIELD(WebPathRequestState, requestFlags, 52, uint32_t);

TITAN_NATIVE_RECORD(WebPathSummaryState, 280, 8);
TITAN_NATIVE_FIELD(WebPathSummaryState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(WebPathSummaryState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(WebPathSummaryState, requestId, 8, uint64_t);
TITAN_NATIVE_FIELD(WebPathSummaryState, phase, 16, uint32_t);
TITAN_NATIVE_FIELD(WebPathSummaryState, result, 20, uint32_t);
TITAN_NATIVE_FIELD(WebPathSummaryState, totalCost, 24, uint32_t);
TITAN_NATIVE_FIELD(WebPathSummaryState, stepCount, 28, uint32_t);
TITAN_NATIVE_FIELD(WebPathSummaryState, exploredNodes, 32, uint32_t);
TITAN_NATIVE_FIELD(WebPathSummaryState, elapsedMs, 36, uint32_t);
TITAN_NATIVE_FIELD(WebPathSummaryState, start, 40, WorldPointState);
TITAN_NATIVE_FIELD(WebPathSummaryState, requestedDestination, 56, WorldPointState);
TITAN_NATIVE_FIELD(WebPathSummaryState, reachedDestination, 72, WorldPointState);
TITAN_NATIVE_FIELD(WebPathSummaryState, message, 88, char[192]);

TITAN_NATIVE_RECORD(WebPathStepState, 176, 8);
TITAN_NATIVE_FIELD(WebPathStepState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(WebPathStepState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(WebPathStepState, edgeId, 8, uint64_t);
TITAN_NATIVE_FIELD(WebPathStepState, kind, 16, uint32_t);
TITAN_NATIVE_FIELD(WebPathStepState, subtype, 20, uint32_t);
TITAN_NATIVE_FIELD(WebPathStepState, fromRouteSpace, 24, uint32_t);
TITAN_NATIVE_FIELD(WebPathStepState, toRouteSpace, 28, uint32_t);
TITAN_NATIVE_FIELD(WebPathStepState, edgeCost, 32, uint32_t);
TITAN_NATIVE_FIELD(WebPathStepState, accumulatedCost, 36, uint32_t);
TITAN_NATIVE_FIELD(WebPathStepState, fromInstanceCopyId, 40, int32_t);
TITAN_NATIVE_FIELD(WebPathStepState, toInstanceCopyId, 44, int32_t);
TITAN_NATIVE_FIELD(WebPathStepState, from, 48, WorldPointState);
TITAN_NATIVE_FIELD(WebPathStepState, to, 64, WorldPointState);
TITAN_NATIVE_FIELD(WebPathStepState, name, 80, char[96]);

TITAN_NATIVE_RECORD(WebWalkRequestState, 76, 4);
TITAN_NATIVE_FIELD(WebWalkRequestState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(WebWalkRequestState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(WebWalkRequestState, path, 8, WebPathRequestState);
TITAN_NATIVE_FIELD(WebWalkRequestState, walkFlags, 64, uint32_t);
TITAN_NATIVE_FIELD(WebWalkRequestState, arriveRadius, 68, uint32_t);
TITAN_NATIVE_FIELD(WebWalkRequestState, maxDurationTicks, 72, uint32_t);

TITAN_NATIVE_RECORD(WebWalkStatusState, 352, 8);
TITAN_NATIVE_FIELD(WebWalkStatusState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, walkId, 8, uint64_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, phase, 16, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, pathResult, 20, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, currentStepIndex, 24, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, stepCount, 28, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, ticksActive, 32, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, replanCount, 36, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, lastDecision, 40, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, reserved0, 44, uint32_t);
TITAN_NATIVE_FIELD(WebWalkStatusState, destination, 48, WorldPointState);
TITAN_NATIVE_FIELD(WebWalkStatusState, currentStepName, 64, char[96]);
TITAN_NATIVE_FIELD(WebWalkStatusState, message, 160, char[192]);

TITAN_NATIVE_RECORD(ActorSpotAnimState, 16, 4);
TITAN_NATIVE_FIELD(ActorSpotAnimState, slot, 0, int32_t);
TITAN_NATIVE_FIELD(ActorSpotAnimState, id, 4, int32_t);
TITAN_NATIVE_FIELD(ActorSpotAnimState, height, 8, int32_t);
TITAN_NATIVE_FIELD(ActorSpotAnimState, expireCycle, 12, int32_t);

TITAN_NATIVE_RECORD(PlayerCompositionSlotState, 16, 4);
TITAN_NATIVE_FIELD(PlayerCompositionSlotState, slotIndex, 0, int32_t);
TITAN_NATIVE_FIELD(PlayerCompositionSlotState, rawValue, 4, int32_t);
TITAN_NATIVE_FIELD(PlayerCompositionSlotState, itemId, 8, int32_t);
TITAN_NATIVE_FIELD(PlayerCompositionSlotState, kind, 12, uint8_t);

TITAN_NATIVE_RECORD(PlayerCompositionState, 528, 4);
TITAN_NATIVE_FIELD(PlayerCompositionState, status, 0, uint8_t);
TITAN_NATIVE_FIELD(PlayerCompositionState, itemIdBase, 4, int32_t);
TITAN_NATIVE_FIELD(PlayerCompositionState, npcTransformId, 8, int32_t);
TITAN_NATIVE_FIELD(PlayerCompositionState, slotCount, 12, uint32_t);
TITAN_NATIVE_FIELD(PlayerCompositionState, slots, 16, PlayerCompositionSlotState[32]);

TITAN_NATIVE_RECORD(PlayerState, 176, 8);
TITAN_NATIVE_FIELD(PlayerState, entityPtr, 0, uint64_t);
TITAN_NATIVE_FIELD(PlayerState, hashIndex, 8, int32_t);
TITAN_NATIVE_FIELD(PlayerState, tileX, 12, int32_t);
TITAN_NATIVE_FIELD(PlayerState, tileY, 16, int32_t);
TITAN_NATIVE_FIELD(PlayerState, plane, 20, int32_t);
TITAN_NATIVE_FIELD(PlayerState, worldX, 24, int32_t);
TITAN_NATIVE_FIELD(PlayerState, worldY, 28, int32_t);
TITAN_NATIVE_FIELD(PlayerState, preciseX, 32, int32_t);
TITAN_NATIVE_FIELD(PlayerState, preciseY, 36, int32_t);
TITAN_NATIVE_FIELD(PlayerState, orientation, 40, int32_t);
TITAN_NATIVE_FIELD(PlayerState, animation, 44, int32_t);
TITAN_NATIVE_FIELD(PlayerState, interactingIndex, 48, int32_t);
TITAN_NATIVE_FIELD(PlayerState, interactingType, 52, uint8_t);
TITAN_NATIVE_FIELD(PlayerState, combatLevel, 56, int32_t);
TITAN_NATIVE_FIELD(PlayerState, hidden, 60, uint8_t);
TITAN_NATIVE_FIELD(PlayerState, stationary, 61, uint8_t);
TITAN_NATIVE_FIELD(PlayerState, unknownPlayerFlag, 62, uint8_t);
TITAN_NATIVE_FIELD(PlayerState, name, 63, char[64]);
TITAN_NATIVE_FIELD(PlayerState, overheadIcon, 128, int32_t);
TITAN_NATIVE_FIELD(PlayerState, skullIcon, 132, int32_t);
TITAN_NATIVE_FIELD(PlayerState, interactingPhase, 136, uint8_t);
TITAN_NATIVE_FIELD(PlayerState, healthRatio, 140, int32_t);
TITAN_NATIVE_FIELD(PlayerState, healthScale, 144, int32_t);
TITAN_NATIVE_FIELD(PlayerState, hasHealthBar, 148, uint8_t);
TITAN_NATIVE_FIELD(PlayerState, movementPose, 152, int32_t);
TITAN_NATIVE_FIELD(PlayerState, idlePose, 156, int32_t);
TITAN_NATIVE_FIELD(PlayerState, worldViewId, 160, int32_t);
TITAN_NATIVE_FIELD(PlayerState, worldViewPtr, 168, uint64_t);

TITAN_NATIVE_RECORD(NpcState, 512, 8);
TITAN_NATIVE_FIELD(NpcState, entityPtr, 0, uint64_t);
TITAN_NATIVE_FIELD(NpcState, definitionPtr, 8, uint64_t);
TITAN_NATIVE_FIELD(NpcState, hashIndex, 16, int32_t);
TITAN_NATIVE_FIELD(NpcState, npcId, 20, int32_t);
TITAN_NATIVE_FIELD(NpcState, tileX, 24, int32_t);
TITAN_NATIVE_FIELD(NpcState, tileY, 28, int32_t);
TITAN_NATIVE_FIELD(NpcState, plane, 32, int32_t);
TITAN_NATIVE_FIELD(NpcState, worldX, 36, int32_t);
TITAN_NATIVE_FIELD(NpcState, worldY, 40, int32_t);
TITAN_NATIVE_FIELD(NpcState, preciseX, 44, int32_t);
TITAN_NATIVE_FIELD(NpcState, preciseY, 48, int32_t);
TITAN_NATIVE_FIELD(NpcState, orientation, 52, int32_t);
TITAN_NATIVE_FIELD(NpcState, animation, 56, int32_t);
TITAN_NATIVE_FIELD(NpcState, interactingIndex, 60, int32_t);
TITAN_NATIVE_FIELD(NpcState, interactingType, 64, uint8_t);
TITAN_NATIVE_FIELD(NpcState, overrideTransform, 68, int32_t);
TITAN_NATIVE_FIELD(NpcState, sizeX, 72, int32_t);
TITAN_NATIVE_FIELD(NpcState, sizeY, 76, int32_t);
TITAN_NATIVE_FIELD(NpcState, name, 80, char[64]);
TITAN_NATIVE_FIELD(NpcState, actions, 144, char[5][64]);
TITAN_NATIVE_FIELD(NpcState, overheadIcon, 464, int32_t);
TITAN_NATIVE_FIELD(NpcState, hasHeadIconOverride, 468, uint8_t);
TITAN_NATIVE_FIELD(NpcState, interactingPhase, 469, uint8_t);
TITAN_NATIVE_FIELD(NpcState, healthRatio, 472, int32_t);
TITAN_NATIVE_FIELD(NpcState, healthScale, 476, int32_t);
TITAN_NATIVE_FIELD(NpcState, hasHealthBar, 480, uint8_t);
TITAN_NATIVE_FIELD(NpcState, movementPose, 484, int32_t);
TITAN_NATIVE_FIELD(NpcState, idlePose, 488, int32_t);
TITAN_NATIVE_FIELD(NpcState, stationary, 492, uint8_t);
TITAN_NATIVE_FIELD(NpcState, worldViewId, 496, int32_t);
TITAN_NATIVE_FIELD(NpcState, worldViewPtr, 504, uint64_t);

TITAN_NATIVE_RECORD(TileObjectState, 536, 8);
TITAN_NATIVE_FIELD(TileObjectState, tileX, 0, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, tileY, 4, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, plane, 8, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, locId, 12, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, sizeX, 16, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, sizeY, 20, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, packedId, 24, uint64_t);
TITAN_NATIVE_FIELD(TileObjectState, entityPtr, 32, uint64_t);
TITAN_NATIVE_FIELD(TileObjectState, definitionPtr, 40, uint64_t);
TITAN_NATIVE_FIELD(TileObjectState, type, 48, char[64]);
TITAN_NATIVE_FIELD(TileObjectState, name, 112, char[64]);
TITAN_NATIVE_FIELD(TileObjectState, actions, 176, char[5][64]);
TITAN_NATIVE_FIELD(TileObjectState, layer, 496, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, worldX, 500, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, worldY, 504, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, sceneTypecode, 508, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, sceneObjectType, 512, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, orientation, 516, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, animation, 520, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, worldViewId, 524, int32_t);
TITAN_NATIVE_FIELD(TileObjectState, worldViewPtr, 528, uint64_t);

TITAN_NATIVE_RECORD(GroundItemState, 112, 8);
TITAN_NATIVE_FIELD(GroundItemState, tileX, 0, int32_t);
TITAN_NATIVE_FIELD(GroundItemState, tileY, 4, int32_t);
TITAN_NATIVE_FIELD(GroundItemState, plane, 8, int32_t);
TITAN_NATIVE_FIELD(GroundItemState, itemId, 12, int32_t);
TITAN_NATIVE_FIELD(GroundItemState, quantity, 16, int32_t);
TITAN_NATIVE_FIELD(GroundItemState, name, 20, char[64]);
TITAN_NATIVE_FIELD(GroundItemState, worldX, 84, int32_t);
TITAN_NATIVE_FIELD(GroundItemState, worldY, 88, int32_t);
TITAN_NATIVE_FIELD(GroundItemState, ownershipType, 92, uint32_t);
TITAN_NATIVE_FIELD(GroundItemState, worldViewId, 96, int32_t);
TITAN_NATIVE_FIELD(GroundItemState, worldViewPtr, 104, uint64_t);

TITAN_NATIVE_RECORD(ProjectileState, 96, 8);
TITAN_NATIVE_FIELD(ProjectileState, basePtr, 0, uint64_t);
TITAN_NATIVE_FIELD(ProjectileState, plane, 8, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, startX, 12, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, startY, 16, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, targetX, 20, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, targetY, 24, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, sourceEntity, 28, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, targetEntity, 32, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, spotAnimId, 36, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, startTick, 40, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, endTick, 44, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, sceneX, 48, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, height, 52, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, sceneY, 56, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, yaw, 60, uint32_t);
TITAN_NATIVE_FIELD(ProjectileState, pitch, 64, uint32_t);
TITAN_NATIVE_FIELD(ProjectileState, moved, 68, uint8_t);
TITAN_NATIVE_FIELD(ProjectileState, worldX, 72, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, worldY, 76, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, rawSourceEntity, 80, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, rawTargetEntity, 84, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, sourceEntityType, 88, int32_t);
TITAN_NATIVE_FIELD(ProjectileState, targetEntityType, 92, int32_t);

TITAN_NATIVE_RECORD(SequenceState, 56, 8);
TITAN_NATIVE_FIELD(SequenceState, ptr, 0, uint64_t);
TITAN_NATIVE_FIELD(SequenceState, id, 8, int32_t);
TITAN_NATIVE_FIELD(SequenceState, flags, 12, uint32_t);
TITAN_NATIVE_FIELD(SequenceState, frameCount, 16, int32_t);
TITAN_NATIVE_FIELD(SequenceState, frameIds, 24, uint64_t);
TITAN_NATIVE_FIELD(SequenceState, frameLengths, 32, uint64_t);
TITAN_NATIVE_FIELD(SequenceState, totalDuration, 40, int32_t);
TITAN_NATIVE_FIELD(SequenceState, frameStep, 44, int32_t);
TITAN_NATIVE_FIELD(SequenceState, repeatLimit, 48, uint16_t);

TITAN_NATIVE_RECORD(GraphicsObjectState, 160, 8);
TITAN_NATIVE_FIELD(GraphicsObjectState, basePtr, 0, uint64_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, plane, 8, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, spotAnimId, 12, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, startCycle, 16, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, height, 20, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, preciseX, 24, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, preciseY, 28, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, sceneX, 32, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, sceneY, 36, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, worldX, 40, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, worldY, 44, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, worldViewPtr, 48, uint64_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, seqStateAddr, 56, uint64_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, seqTypePtr, 64, uint64_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, seqPtr, 72, uint64_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, animationId, 80, int32_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, frameCycle, 84, uint16_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, currentFrame, 86, uint16_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, loopCount, 88, uint16_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, totalCycle, 90, uint16_t);
TITAN_NATIVE_FIELD(GraphicsObjectState, animation, 96, SequenceState);
TITAN_NATIVE_FIELD(GraphicsObjectState, worldViewId, 152, int32_t);

TITAN_NATIVE_RECORD(CameraState, 36, 4);
TITAN_NATIVE_FIELD(CameraState, posX, 0, int32_t);
TITAN_NATIVE_FIELD(CameraState, posY, 4, int32_t);
TITAN_NATIVE_FIELD(CameraState, posZ, 8, int32_t);
TITAN_NATIVE_FIELD(CameraState, yaw, 12, int32_t);
TITAN_NATIVE_FIELD(CameraState, pitch, 16, int32_t);
TITAN_NATIVE_FIELD(CameraState, viewportW, 20, int32_t);
TITAN_NATIVE_FIELD(CameraState, viewportH, 24, int32_t);
TITAN_NATIVE_FIELD(CameraState, zoom, 28, int32_t);
TITAN_NATIVE_FIELD(CameraState, valid, 32, uint8_t);

TITAN_NATIVE_RECORD(ItemDefSnapshot, 7128, 4);
TITAN_NATIVE_FIELD(ItemDefSnapshot, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(ItemDefSnapshot, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(ItemDefSnapshot, id, 8, int32_t);
TITAN_NATIVE_FIELD(ItemDefSnapshot, name, 12, char[64]);
TITAN_NATIVE_FIELD(ItemDefSnapshot, members, 76, uint8_t);
TITAN_NATIVE_FIELD(ItemDefSnapshot, stackable, 77, uint8_t);
TITAN_NATIVE_FIELD(ItemDefSnapshot, noted, 78, uint8_t);
TITAN_NATIVE_FIELD(ItemDefSnapshot, reserved0, 79, uint8_t);
TITAN_NATIVE_FIELD(ItemDefSnapshot, noteId, 80, int32_t);
TITAN_NATIVE_FIELD(ItemDefSnapshot, linkedId, 84, int32_t);
TITAN_NATIVE_FIELD(ItemDefSnapshot, inventoryActions, 88, char[5][64]);
TITAN_NATIVE_FIELD(ItemDefSnapshot, groundActions, 408, char[5][64]);
TITAN_NATIVE_FIELD(ItemDefSnapshot, subOps, 728, char[5][20][64]);

TITAN_NATIVE_RECORD(NpcDefSnapshot, 416, 4);
TITAN_NATIVE_FIELD(NpcDefSnapshot, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(NpcDefSnapshot, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(NpcDefSnapshot, id, 8, int32_t);
TITAN_NATIVE_FIELD(NpcDefSnapshot, name, 12, char[64]);
TITAN_NATIVE_FIELD(NpcDefSnapshot, combatLevel, 76, int32_t);
TITAN_NATIVE_FIELD(NpcDefSnapshot, size, 80, int32_t);
TITAN_NATIVE_FIELD(NpcDefSnapshot, actions, 84, char[5][64]);
TITAN_NATIVE_FIELD(NpcDefSnapshot, transformVarbit, 404, int32_t);
TITAN_NATIVE_FIELD(NpcDefSnapshot, transformVarp, 408, int32_t);
TITAN_NATIVE_FIELD(NpcDefSnapshot, transformDefault, 412, int32_t);

TITAN_NATIVE_RECORD(ObjDefSnapshot, 420, 4);
TITAN_NATIVE_FIELD(ObjDefSnapshot, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(ObjDefSnapshot, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(ObjDefSnapshot, id, 8, int32_t);
TITAN_NATIVE_FIELD(ObjDefSnapshot, name, 12, char[64]);
TITAN_NATIVE_FIELD(ObjDefSnapshot, actions, 76, char[5][64]);
TITAN_NATIVE_FIELD(ObjDefSnapshot, sizeX, 396, int32_t);
TITAN_NATIVE_FIELD(ObjDefSnapshot, sizeY, 400, int32_t);
TITAN_NATIVE_FIELD(ObjDefSnapshot, blocksMovement, 404, uint8_t);
TITAN_NATIVE_FIELD(ObjDefSnapshot, reserved0, 405, uint8_t[3]);
TITAN_NATIVE_FIELD(ObjDefSnapshot, transformVarbit, 408, int32_t);
TITAN_NATIVE_FIELD(ObjDefSnapshot, transformVarp, 412, int32_t);
TITAN_NATIVE_FIELD(ObjDefSnapshot, transformDefault, 416, int32_t);

TITAN_NATIVE_RECORD(Cs2ScriptResult, 136, 4);
TITAN_NATIVE_FIELD(Cs2ScriptResult, success, 0, uint8_t);
TITAN_NATIVE_FIELD(Cs2ScriptResult, intCount, 4, uint32_t);
TITAN_NATIVE_FIELD(Cs2ScriptResult, ints, 8, int32_t[32]);

TITAN_NATIVE_RECORD(TitanHookArg, 32, 4);
TITAN_NATIVE_FIELD(TitanHookArg, type, 0, uint8_t);
TITAN_NATIVE_FIELD(TitanHookArg, intVal, 4, int32_t);
TITAN_NATIVE_FIELD(TitanHookArg, stringVal, 8, char[23]);

TITAN_NATIVE_RECORD(WidgetState, 332, 4);
TITAN_NATIVE_FIELD(WidgetState, screenX, 0, int32_t);
TITAN_NATIVE_FIELD(WidgetState, screenY, 4, int32_t);
TITAN_NATIVE_FIELD(WidgetState, width, 8, int32_t);
TITAN_NATIVE_FIELD(WidgetState, height, 12, int32_t);
TITAN_NATIVE_FIELD(WidgetState, relativeX, 16, int32_t);
TITAN_NATIVE_FIELD(WidgetState, relativeY, 20, int32_t);
TITAN_NATIVE_FIELD(WidgetState, scrollX, 24, int32_t);
TITAN_NATIVE_FIELD(WidgetState, scrollY, 28, int32_t);
TITAN_NATIVE_FIELD(WidgetState, type, 32, int32_t);
TITAN_NATIVE_FIELD(WidgetState, contentType, 36, int32_t);
TITAN_NATIVE_FIELD(WidgetState, opacity, 40, int32_t);
TITAN_NATIVE_FIELD(WidgetState, itemId, 44, int32_t);
TITAN_NATIVE_FIELD(WidgetState, itemQuantity, 48, int32_t);
TITAN_NATIVE_FIELD(WidgetState, parentId, 52, int32_t);
TITAN_NATIVE_FIELD(WidgetState, hidden, 56, uint8_t);
TITAN_NATIVE_FIELD(WidgetState, selfHidden, 57, uint8_t);
TITAN_NATIVE_FIELD(WidgetState, visible, 58, uint8_t);
TITAN_NATIVE_FIELD(WidgetState, text, 59, char[256]);
TITAN_NATIVE_FIELD(WidgetState, packedId, 316, int32_t);
TITAN_NATIVE_FIELD(WidgetState, spriteId, 320, int32_t);
TITAN_NATIVE_FIELD(WidgetState, modelType, 324, int32_t);
TITAN_NATIVE_FIELD(WidgetState, modelId, 328, int32_t);

TITAN_NATIVE_RECORD(WidgetAddressState, 56, 4);
TITAN_NATIVE_FIELD(WidgetAddressState, rootPackedId, 0, uint32_t);
TITAN_NATIVE_FIELD(WidgetAddressState, depth, 4, uint32_t);
TITAN_NATIVE_FIELD(WidgetAddressState, slots, 8, int32_t[12]);

TITAN_NATIVE_RECORD(WidgetQueryState, 388, 4);
TITAN_NATIVE_FIELD(WidgetQueryState, widget, 0, WidgetState);
TITAN_NATIVE_FIELD(WidgetQueryState, address, 332, WidgetAddressState);

TITAN_NATIVE_RECORD(VarbitDefSnapshot, 28, 4);
TITAN_NATIVE_FIELD(VarbitDefSnapshot, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(VarbitDefSnapshot, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(VarbitDefSnapshot, id, 8, int32_t);
TITAN_NATIVE_FIELD(VarbitDefSnapshot, varpIndex, 12, int32_t);
TITAN_NATIVE_FIELD(VarbitDefSnapshot, lowBit, 16, int32_t);
TITAN_NATIVE_FIELD(VarbitDefSnapshot, highBit, 20, int32_t);
TITAN_NATIVE_FIELD(VarbitDefSnapshot, source, 24, uint8_t);
TITAN_NATIVE_FIELD(VarbitDefSnapshot, reserved0, 25, uint8_t[3]);

TITAN_NATIVE_RECORD(PluginInfo, 434, 1);
TITAN_NATIVE_FIELD(PluginInfo, id, 0, char[32]);
TITAN_NATIVE_FIELD(PluginInfo, name, 32, char[64]);
TITAN_NATIVE_FIELD(PluginInfo, enabled, 96, uint8_t);
TITAN_NATIVE_FIELD(PluginInfo, hasPanel, 97, uint8_t);
TITAN_NATIVE_FIELD(PluginInfo, description, 98, char[256]);
TITAN_NATIVE_FIELD(PluginInfo, author, 354, char[64]);
TITAN_NATIVE_FIELD(PluginInfo, version, 418, char[16]);

TITAN_NATIVE_RECORD(InventoryItemState, 76, 4);
TITAN_NATIVE_FIELD(InventoryItemState, slot, 0, int32_t);
TITAN_NATIVE_FIELD(InventoryItemState, itemId, 4, int32_t);
TITAN_NATIVE_FIELD(InventoryItemState, quantity, 8, int32_t);
TITAN_NATIVE_FIELD(InventoryItemState, name, 12, char[64]);

TITAN_NATIVE_RECORD(ItemContainerState, 24588, 4);
TITAN_NATIVE_FIELD(ItemContainerState, containerId, 0, int32_t);
TITAN_NATIVE_FIELD(ItemContainerState, capacity, 4, int32_t);
TITAN_NATIVE_FIELD(ItemContainerState, writtenCount, 8, int32_t);
TITAN_NATIVE_FIELD(ItemContainerState, slots, 12, int32_t[2048]);
TITAN_NATIVE_FIELD(ItemContainerState, itemIds, 8204, int32_t[2048]);
TITAN_NATIVE_FIELD(ItemContainerState, quantities, 16396, int32_t[2048]);

TITAN_NATIVE_RECORD(ItemContainerChangedEvent, 24592, 4);
TITAN_NATIVE_FIELD(ItemContainerChangedEvent, containerId, 0, int32_t);
TITAN_NATIVE_FIELD(ItemContainerChangedEvent, capacity, 4, int32_t);
TITAN_NATIVE_FIELD(ItemContainerChangedEvent, writtenCount, 8, int32_t);
TITAN_NATIVE_FIELD(ItemContainerChangedEvent, slots, 12, int32_t[2048]);
TITAN_NATIVE_FIELD(ItemContainerChangedEvent, itemIds, 8204, int32_t[2048]);
TITAN_NATIVE_FIELD(ItemContainerChangedEvent, quantities, 16396, int32_t[2048]);
TITAN_NATIVE_FIELD(ItemContainerChangedEvent, gameTick, 24588, int32_t);

TITAN_NATIVE_RECORD(ItemCompositionState, 8532, 4);
TITAN_NATIVE_FIELD(ItemCompositionState, id, 0, int32_t);
TITAN_NATIVE_FIELD(ItemCompositionState, name, 4, char[64]);
TITAN_NATIVE_FIELD(ItemCompositionState, stackable, 68, uint8_t);
TITAN_NATIVE_FIELD(ItemCompositionState, linkedNoteId, 72, int32_t);
TITAN_NATIVE_FIELD(ItemCompositionState, inventoryActionsCount, 76, uint32_t);
TITAN_NATIVE_FIELD(ItemCompositionState, inventoryActions, 80, char[32][64]);
TITAN_NATIVE_FIELD(ItemCompositionState, runtimeResolved, 2128, uint8_t);
TITAN_NATIVE_FIELD(ItemCompositionState, subOps, 2129, char[5][20][64]);

TITAN_NATIVE_RECORD(WorldState, 136, 4);
TITAN_NATIVE_FIELD(WorldState, id, 0, int32_t);
TITAN_NATIVE_FIELD(WorldState, flags, 4, uint32_t);
TITAN_NATIVE_FIELD(WorldState, string0, 8, char[64]);
TITAN_NATIVE_FIELD(WorldState, string1, 72, char[64]);

TITAN_NATIVE_RECORD(WorldMetadataState, 208, 4);
TITAN_NATIVE_FIELD(WorldMetadataState, id, 0, int32_t);
TITAN_NATIVE_FIELD(WorldMetadataState, flags, 4, uint32_t);
TITAN_NATIVE_FIELD(WorldMetadataState, host, 8, char[64]);
TITAN_NATIVE_FIELD(WorldMetadataState, activity, 72, char[64]);
TITAN_NATIVE_FIELD(WorldMetadataState, location, 136, uint8_t);
TITAN_NATIVE_FIELD(WorldMetadataState, padding1, 137, uint8_t);
TITAN_NATIVE_FIELD(WorldMetadataState, population, 138, int16_t);
TITAN_NATIVE_FIELD(WorldMetadataState, pingMs, 140, int32_t);
TITAN_NATIVE_FIELD(WorldMetadataState, region, 144, char[64]);

TITAN_NATIVE_RECORD(LoginAccountState, 144, 4);
TITAN_NATIVE_FIELD(LoginAccountState, loginIndex, 0, int32_t);
TITAN_NATIVE_FIELD(LoginAccountState, gameState, 4, int32_t);
TITAN_NATIVE_FIELD(LoginAccountState, fieldToggle, 8, int32_t);
TITAN_NATIVE_FIELD(LoginAccountState, oauthSwitchAvailable, 12, uint8_t);
TITAN_NATIVE_FIELD(LoginAccountState, credentialSetAvailable, 13, uint8_t);
TITAN_NATIVE_FIELD(LoginAccountState, displayNameAvailable, 14, uint8_t);
TITAN_NATIVE_FIELD(LoginAccountState, username, 15, char[64]);
TITAN_NATIVE_FIELD(LoginAccountState, displayName, 79, char[64]);

TITAN_NATIVE_RECORD(LoginFlowState, 56, 8);
TITAN_NATIVE_FIELD(LoginFlowState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(LoginFlowState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(LoginFlowState, gameState, 8, int32_t);
TITAN_NATIVE_FIELD(LoginFlowState, loginIndex, 12, int32_t);
TITAN_NATIVE_FIELD(LoginFlowState, jagexLauncherIndex, 16, int32_t);
TITAN_NATIVE_FIELD(LoginFlowState, oauthSetterAvailable, 20, uint8_t);
TITAN_NATIVE_FIELD(LoginFlowState, sessionSetterAvailable, 21, uint8_t);
TITAN_NATIVE_FIELD(LoginFlowState, launcherSubmitAvailable, 22, uint8_t);
TITAN_NATIVE_FIELD(LoginFlowState, standardAcknowledgeAvailable, 23, uint8_t);
TITAN_NATIVE_FIELD(LoginFlowState, worldReady, 24, uint8_t);
TITAN_NATIVE_FIELD(LoginFlowState, reserved, 25, uint8_t[3]);
TITAN_NATIVE_FIELD(LoginFlowState, transitionGeneration, 32, uint64_t);
TITAN_NATIVE_FIELD(LoginFlowState, loggingInGeneration, 40, uint64_t);
TITAN_NATIVE_FIELD(LoginFlowState, failedLoginGeneration, 48, uint64_t);

TITAN_NATIVE_RECORD(ScriptFiredEvent, 268, 4);
TITAN_NATIVE_FIELD(ScriptFiredEvent, scriptId, 0, int32_t);
TITAN_NATIVE_FIELD(ScriptFiredEvent, intArgCount, 4, int32_t);
TITAN_NATIVE_FIELD(ScriptFiredEvent, intArgs, 8, int32_t[32]);
TITAN_NATIVE_FIELD(ScriptFiredEvent, intResultCount, 136, int32_t);
TITAN_NATIVE_FIELD(ScriptFiredEvent, intResults, 140, int32_t[32]);

TITAN_NATIVE_RECORD(VarbitChangedEvent, 16, 4);
TITAN_NATIVE_FIELD(VarbitChangedEvent, varbitId, 0, int32_t);
TITAN_NATIVE_FIELD(VarbitChangedEvent, oldValue, 4, int32_t);
TITAN_NATIVE_FIELD(VarbitChangedEvent, newValue, 8, int32_t);
TITAN_NATIVE_FIELD(VarbitChangedEvent, gameTick, 12, int32_t);

TITAN_NATIVE_RECORD(GameStateChangedEvent, 12, 4);
TITAN_NATIVE_FIELD(GameStateChangedEvent, oldState, 0, int32_t);
TITAN_NATIVE_FIELD(GameStateChangedEvent, newState, 4, int32_t);
TITAN_NATIVE_FIELD(GameStateChangedEvent, tickCount, 8, int32_t);

TITAN_NATIVE_RECORD(ChatMessageEvent, 648, 4);
TITAN_NATIVE_FIELD(ChatMessageEvent, type, 0, int32_t);
TITAN_NATIVE_FIELD(ChatMessageEvent, name, 4, char[64]);
TITAN_NATIVE_FIELD(ChatMessageEvent, message, 68, char[512]);
TITAN_NATIVE_FIELD(ChatMessageEvent, sender, 580, char[64]);
TITAN_NATIVE_FIELD(ChatMessageEvent, gameTick, 644, int32_t);

TITAN_NATIVE_RECORD(MenuOptionClickedEvent, 316, 4);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, opcode, 0, uint32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, identifier, 4, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, param0, 8, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, param1, 12, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, worldViewId, 16, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, clickX, 20, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, clickY, 24, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, actionText, 28, char[64]);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, targetText, 92, char[64]);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, consumed, 156, uint8_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replaced, 157, uint8_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replacementOpcode, 160, uint32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replacementIdentifier, 164, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replacementParam0, 168, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replacementParam1, 172, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replacementWorldViewId, 176, uint32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replacementClickX, 180, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replacementClickY, 184, int32_t);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replacementActionText, 188, char[64]);
TITAN_NATIVE_FIELD(MenuOptionClickedEvent, replacementTargetText, 252, char[64]);

TITAN_NATIVE_RECORD(SoundPlayedEvent, 28, 4);
TITAN_NATIVE_FIELD(SoundPlayedEvent, kind, 0, int32_t);
TITAN_NATIVE_FIELD(SoundPlayedEvent, soundId, 4, int32_t);
TITAN_NATIVE_FIELD(SoundPlayedEvent, loops, 8, int32_t);
TITAN_NATIVE_FIELD(SoundPlayedEvent, durationMs, 12, int32_t);
TITAN_NATIVE_FIELD(SoundPlayedEvent, packedPos, 16, int32_t);
TITAN_NATIVE_FIELD(SoundPlayedEvent, gameTick, 20, int32_t);
TITAN_NATIVE_FIELD(SoundPlayedEvent, consumed, 24, uint8_t);

TITAN_NATIVE_RECORD(MouseButtonEvent, 24, 4);
TITAN_NATIVE_FIELD(MouseButtonEvent, x, 0, int32_t);
TITAN_NATIVE_FIELD(MouseButtonEvent, y, 4, int32_t);
TITAN_NATIVE_FIELD(MouseButtonEvent, button, 8, int32_t);
TITAN_NATIVE_FIELD(MouseButtonEvent, modifiers, 12, int32_t);
TITAN_NATIVE_FIELD(MouseButtonEvent, gameTick, 16, int32_t);
TITAN_NATIVE_FIELD(MouseButtonEvent, consumed, 20, uint8_t);

TITAN_NATIVE_RECORD(HitsplatAppliedEvent, 720, 8);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, actorType, 0, uint8_t);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, reserved, 1, uint8_t[7]);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, player, 8, PlayerState);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, npc, 184, NpcState);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, type, 696, int32_t);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, value, 700, int32_t);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, limit, 704, int32_t);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, delay, 708, int32_t);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, cycle, 712, int32_t);
TITAN_NATIVE_FIELD(HitsplatAppliedEvent, gameTick, 716, int32_t);

TITAN_NATIVE_RECORD(ActorSpotAnimEvent, 720, 8);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, actorType, 0, uint8_t);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, reserved, 1, uint8_t[7]);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, player, 8, PlayerState);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, npc, 184, NpcState);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, slot, 696, int32_t);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, id, 700, int32_t);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, height, 704, int32_t);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, delay, 708, int32_t);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, cycle, 712, int32_t);
TITAN_NATIVE_FIELD(ActorSpotAnimEvent, gameTick, 716, int32_t);

TITAN_NATIVE_RECORD(AnimationChangedEvent, 712, 8);
TITAN_NATIVE_FIELD(AnimationChangedEvent, actorType, 0, uint8_t);
TITAN_NATIVE_FIELD(AnimationChangedEvent, reserved, 1, uint8_t[7]);
TITAN_NATIVE_FIELD(AnimationChangedEvent, player, 8, PlayerState);
TITAN_NATIVE_FIELD(AnimationChangedEvent, npc, 184, NpcState);
TITAN_NATIVE_FIELD(AnimationChangedEvent, oldAnimation, 696, int32_t);
TITAN_NATIVE_FIELD(AnimationChangedEvent, newAnimation, 700, int32_t);
TITAN_NATIVE_FIELD(AnimationChangedEvent, gameTick, 704, int32_t);

TITAN_NATIVE_RECORD(OverheadTextChangedEvent, 720, 8);
TITAN_NATIVE_FIELD(OverheadTextChangedEvent, actorType, 0, uint8_t);
TITAN_NATIVE_FIELD(OverheadTextChangedEvent, reserved, 1, uint8_t[7]);
TITAN_NATIVE_FIELD(OverheadTextChangedEvent, player, 8, PlayerState);
TITAN_NATIVE_FIELD(OverheadTextChangedEvent, npc, 184, NpcState);
TITAN_NATIVE_FIELD(OverheadTextChangedEvent, overheadText, 696, const char *);
TITAN_NATIVE_FIELD(OverheadTextChangedEvent, overheadTextLength, 704, uint64_t);
TITAN_NATIVE_FIELD(OverheadTextChangedEvent, gameTick, 712, int32_t);

TITAN_NATIVE_RECORD(ScreenshotStatusState, 160, 8);
TITAN_NATIVE_FIELD(ScreenshotStatusState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(ScreenshotStatusState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(ScreenshotStatusState, requestId, 8, uint64_t);
TITAN_NATIVE_FIELD(ScreenshotStatusState, phase, 16, uint32_t);
TITAN_NATIVE_FIELD(ScreenshotStatusState, width, 20, uint32_t);
TITAN_NATIVE_FIELD(ScreenshotStatusState, height, 24, uint32_t);
TITAN_NATIVE_FIELD(ScreenshotStatusState, pngBytes, 28, uint32_t);
TITAN_NATIVE_FIELD(ScreenshotStatusState, message, 32, char[128]);

TITAN_NATIVE_RECORD(SyntheticActionEntry, 88, 8);
TITAN_NATIVE_FIELD(SyntheticActionEntry, opcode, 0, uint32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, identifier, 4, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, param0, 8, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, param1, 12, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, worldViewId, 16, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, clickX, 20, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, clickY, 24, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, actionText, 32, const char *);
TITAN_NATIVE_FIELD(SyntheticActionEntry, targetText, 40, const char *);
TITAN_NATIVE_FIELD(SyntheticActionEntry, skipClick, 48, uint8_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, targetPlane, 52, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, targetSizeX, 56, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, targetSizeY, 60, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, targetLayer, 64, int32_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, targetEntityPtr, 72, uint64_t);
TITAN_NATIVE_FIELD(SyntheticActionEntry, targetPackedId, 80, uint64_t);

TITAN_NATIVE_RECORD(SelectedActionPair, 184, 8);
TITAN_NATIVE_FIELD(SelectedActionPair, source, 0, SyntheticActionEntry);
TITAN_NATIVE_FIELD(SelectedActionPair, target, 88, SyntheticActionEntry);
TITAN_NATIVE_FIELD(SelectedActionPair, hasExpectedSourceItem, 176, uint8_t);
TITAN_NATIVE_FIELD(SelectedActionPair, expectedSourceItemId, 180, int32_t);

TITAN_NATIVE_RECORD(ActionClickPointSpec, 56, 8);
TITAN_NATIVE_FIELD(ActionClickPointSpec, opcode, 0, uint32_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, identifier, 4, int32_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, param0, 8, int32_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, param1, 12, int32_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, worldViewId, 16, int32_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, targetPlane, 20, int32_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, targetSizeX, 24, int32_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, targetSizeY, 28, int32_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, targetLayer, 32, int32_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, targetEntityPtr, 40, uint64_t);
TITAN_NATIVE_FIELD(ActionClickPointSpec, targetPackedId, 48, uint64_t);

TITAN_NATIVE_RECORD(BreakRegistrationState, 176, 4);
TITAN_NATIVE_FIELD(BreakRegistrationState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(BreakRegistrationState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(BreakRegistrationState, role, 8, uint8_t);
TITAN_NATIVE_FIELD(BreakRegistrationState, reserved, 9, uint8_t[7]);
TITAN_NATIVE_FIELD(BreakRegistrationState, pluginId, 16, char[64]);
TITAN_NATIVE_FIELD(BreakRegistrationState, displayName, 80, char[96]);

TITAN_NATIVE_RECORD(BreakReportState, 288, 8);
TITAN_NATIVE_FIELD(BreakReportState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(BreakReportState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(BreakReportState, epoch, 8, uint64_t);
TITAN_NATIVE_FIELD(BreakReportState, state, 16, uint8_t);
TITAN_NATIVE_FIELD(BreakReportState, reserved, 17, uint8_t[7]);
TITAN_NATIVE_FIELD(BreakReportState, code, 24, uint32_t);
TITAN_NATIVE_FIELD(BreakReportState, retryAfterMs, 28, uint32_t);
TITAN_NATIVE_FIELD(BreakReportState, pluginId, 32, char[64]);
TITAN_NATIVE_FIELD(BreakReportState, reason, 96, char[192]);

TITAN_NATIVE_RECORD(BreakCommandState, 88, 8);
TITAN_NATIVE_FIELD(BreakCommandState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(BreakCommandState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(BreakCommandState, epoch, 8, uint64_t);
TITAN_NATIVE_FIELD(BreakCommandState, phase, 16, uint8_t);
TITAN_NATIVE_FIELD(BreakCommandState, mode, 17, uint8_t);
TITAN_NATIVE_FIELD(BreakCommandState, reserved, 18, uint8_t[6]);
TITAN_NATIVE_FIELD(BreakCommandState, triggeringOwnerId, 24, char[64]);

TITAN_NATIVE_RECORD(BreakParticipantState, 496, 8);
TITAN_NATIVE_FIELD(BreakParticipantState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(BreakParticipantState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(BreakParticipantState, token, 8, uint64_t);
TITAN_NATIVE_FIELD(BreakParticipantState, generation, 16, uint64_t);
TITAN_NATIVE_FIELD(BreakParticipantState, activityGeneration, 24, uint64_t);
TITAN_NATIVE_FIELD(BreakParticipantState, lastObservedEpoch, 32, uint64_t);
TITAN_NATIVE_FIELD(BreakParticipantState, runtimeKind, 40, uint8_t);
TITAN_NATIVE_FIELD(BreakParticipantState, configurable, 41, uint8_t);
TITAN_NATIVE_FIELD(BreakParticipantState, active, 42, uint8_t);
TITAN_NATIVE_FIELD(BreakParticipantState, enabled, 43, uint8_t);
TITAN_NATIVE_FIELD(BreakParticipantState, reportState, 44, uint8_t);
TITAN_NATIVE_FIELD(BreakParticipantState, reserved, 45, uint8_t[3]);
TITAN_NATIVE_FIELD(BreakParticipantState, reportCode, 48, uint32_t);
TITAN_NATIVE_FIELD(BreakParticipantState, retryAfterMs, 52, uint32_t);
TITAN_NATIVE_FIELD(BreakParticipantState, pluginId, 56, char[64]);
TITAN_NATIVE_FIELD(BreakParticipantState, displayName, 120, char[96]);
TITAN_NATIVE_FIELD(BreakParticipantState, reportReason, 216, char[192]);
TITAN_NATIVE_FIELD(BreakParticipantState, command, 408, BreakCommandState);

TITAN_NATIVE_RECORD(SanitizedProxyRouteState, 200, 4);
TITAN_NATIVE_FIELD(SanitizedProxyRouteState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(SanitizedProxyRouteState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(SanitizedProxyRouteState, proxyId, 8, char[64]);
TITAN_NATIVE_FIELD(SanitizedProxyRouteState, label, 72, char[128]);

TITAN_NATIVE_RECORD(ProxyRouteStatusState, 160, 8);
TITAN_NATIVE_FIELD(ProxyRouteStatusState, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(ProxyRouteStatusState, apiVersion, 4, uint32_t);
TITAN_NATIVE_FIELD(ProxyRouteStatusState, generation, 8, uint64_t);
TITAN_NATIVE_FIELD(ProxyRouteStatusState, kind, 16, uint32_t);
TITAN_NATIVE_FIELD(ProxyRouteStatusState, failureCode, 20, int32_t);
TITAN_NATIVE_FIELD(ProxyRouteStatusState, proxyId, 24, char[64]);
TITAN_NATIVE_FIELD(ProxyRouteStatusState, failureStage, 88, char[64]);
TITAN_NATIVE_FIELD(ProxyRouteStatusState, egressReady, 152, uint8_t);
TITAN_NATIVE_FIELD(ProxyRouteStatusState, reserved, 153, uint8_t[7]);

TITAN_NATIVE_RECORD(GrandExchangeOffer, 40, 8);
TITAN_NATIVE_FIELD(GrandExchangeOffer, slot, 0, int32_t);
TITAN_NATIVE_FIELD(GrandExchangeOffer, itemId, 4, int32_t);
TITAN_NATIVE_FIELD(GrandExchangeOffer, totalQuantity, 8, int32_t);
TITAN_NATIVE_FIELD(GrandExchangeOffer, quantitySold, 12, int32_t);
TITAN_NATIVE_FIELD(GrandExchangeOffer, price, 16, int64_t);
TITAN_NATIVE_FIELD(GrandExchangeOffer, spent, 24, int64_t);
TITAN_NATIVE_FIELD(GrandExchangeOffer, state, 32, GrandExchangeOfferState);
TITAN_NATIVE_FIELD(GrandExchangeOffer, status, 36, uint8_t);
TITAN_NATIVE_FIELD(GrandExchangeOffer, type, 37, uint8_t);

TITAN_NATIVE_RECORD(BankCacheState, 24944, 8);
TITAN_NATIVE_FIELD(BankCacheState, bank, 0, ItemContainerState);
TITAN_NATIVE_FIELD(BankCacheState, lastObservedAt, 24592, int64_t);
TITAN_NATIVE_FIELD(BankCacheState, known, 24600, uint8_t);
TITAN_NATIVE_FIELD(BankCacheState, live, 24601, uint8_t);
TITAN_NATIVE_FIELD(BankCacheState, loading, 24602, uint8_t);
TITAN_NATIVE_FIELD(BankCacheState, reserved, 24603, uint8_t[5]);
TITAN_NATIVE_FIELD(BankCacheState, account, 24608, char[80]);
TITAN_NATIVE_FIELD(BankCacheState, error, 24688, char[256]);

TITAN_NATIVE_RECORD(GeBuyOptions, 32, 8);
TITAN_NATIVE_FIELD(GeBuyOptions, itemId, 0, int32_t);
TITAN_NATIVE_FIELD(GeBuyOptions, quantity, 4, int32_t);
TITAN_NATIVE_FIELD(GeBuyOptions, maxAttempts, 8, int32_t);
TITAN_NATIVE_FIELD(GeBuyOptions, waitPerAttemptMs, 12, int32_t);
TITAN_NATIVE_FIELD(GeBuyOptions, timeoutMs, 16, int32_t);
TITAN_NATIVE_FIELD(GeBuyOptions, toInventory, 20, uint8_t);
TITAN_NATIVE_FIELD(GeBuyOptions, noted, 21, uint8_t);
TITAN_NATIVE_FIELD(GeBuyOptions, autoOpen, 22, uint8_t);
TITAN_NATIVE_FIELD(GeBuyOptions, reserved, 23, uint8_t);
TITAN_NATIVE_FIELD(GeBuyOptions, maxUnitPrice, 24, int64_t);

TITAN_NATIVE_RECORD(GeRequestState, 312, 8);
TITAN_NATIVE_FIELD(GeRequestState, requestId, 0, uint64_t);
TITAN_NATIVE_FIELD(GeRequestState, itemId, 8, int32_t);
TITAN_NATIVE_FIELD(GeRequestState, quantity, 12, int32_t);
TITAN_NATIVE_FIELD(GeRequestState, filled, 16, int32_t);
TITAN_NATIVE_FIELD(GeRequestState, remaining, 20, int32_t);
TITAN_NATIVE_FIELD(GeRequestState, attempts, 24, int32_t);
TITAN_NATIVE_FIELD(GeRequestState, slot, 28, int32_t);
TITAN_NATIVE_FIELD(GeRequestState, phase, 32, GeRequestPhase);
TITAN_NATIVE_FIELD(GeRequestState, unitPrice, 40, int64_t);
TITAN_NATIVE_FIELD(GeRequestState, spent, 48, int64_t);
TITAN_NATIVE_FIELD(GeRequestState, message, 56, char[256]);

TITAN_NATIVE_RECORD(GrandExchangeOfferChangedEvent, 48, 8);
TITAN_NATIVE_FIELD(GrandExchangeOfferChangedEvent, offer, 0, GrandExchangeOffer);
TITAN_NATIVE_FIELD(GrandExchangeOfferChangedEvent, slot, 40, int32_t);

TITAN_NATIVE_RECORD(ItemPriceMetadata, 4392, 8);
TITAN_NATIVE_FIELD(ItemPriceMetadata, id, 0, int32_t);
TITAN_NATIVE_FIELD(ItemPriceMetadata, present, 4, uint32_t);
TITAN_NATIVE_FIELD(ItemPriceMetadata, buyLimit, 8, int64_t);
TITAN_NATIVE_FIELD(ItemPriceMetadata, highAlch, 16, int64_t);
TITAN_NATIVE_FIELD(ItemPriceMetadata, members, 24, uint8_t);
TITAN_NATIVE_FIELD(ItemPriceMetadata, reserved, 25, uint8_t[7]);
TITAN_NATIVE_FIELD(ItemPriceMetadata, name, 32, char[257]);
TITAN_NATIVE_FIELD(ItemPriceMetadata, examine, 289, char[4097]);

TITAN_NATIVE_RECORD(ItemPrice, 576, 8);
TITAN_NATIVE_FIELD(ItemPrice, id, 0, int32_t);
TITAN_NATIVE_FIELD(ItemPrice, present, 4, uint32_t);
TITAN_NATIVE_FIELD(ItemPrice, high, 8, int64_t);
TITAN_NATIVE_FIELD(ItemPrice, low, 16, int64_t);
TITAN_NATIVE_FIELD(ItemPrice, highTime, 24, int64_t);
TITAN_NATIVE_FIELD(ItemPrice, lowTime, 32, int64_t);
TITAN_NATIVE_FIELD(ItemPrice, fetchedAt, 40, int64_t);
TITAN_NATIVE_FIELD(ItemPrice, lastAttemptAt, 48, int64_t);
TITAN_NATIVE_FIELD(ItemPrice, loading, 56, uint8_t);
TITAN_NATIVE_FIELD(ItemPrice, pending, 57, uint8_t);
TITAN_NATIVE_FIELD(ItemPrice, reserved, 58, uint8_t[6]);
TITAN_NATIVE_FIELD(ItemPrice, error, 64, char[512]);

TITAN_NATIVE_RECORD(ItemPriceStatus, 552, 8);
TITAN_NATIVE_FIELD(ItemPriceStatus, catalogRevision, 0, int64_t);
TITAN_NATIVE_FIELD(ItemPriceStatus, catalogFetchedAt, 8, int64_t);
TITAN_NATIVE_FIELD(ItemPriceStatus, catalogLastAttemptAt, 16, int64_t);
TITAN_NATIVE_FIELD(ItemPriceStatus, pendingCount, 24, int32_t);
TITAN_NATIVE_FIELD(ItemPriceStatus, loadingItem, 28, int32_t);
TITAN_NATIVE_FIELD(ItemPriceStatus, available, 32, uint8_t);
TITAN_NATIVE_FIELD(ItemPriceStatus, catalogLoading, 33, uint8_t);
TITAN_NATIVE_FIELD(ItemPriceStatus, catalogPending, 34, uint8_t);
TITAN_NATIVE_FIELD(ItemPriceStatus, reserved, 35, uint8_t[5]);
TITAN_NATIVE_FIELD(ItemPriceStatus, error, 40, char[512]);

TITAN_NATIVE_RECORD(CrossTabWrite, 40, 8);
TITAN_NATIVE_FIELD(CrossTabWrite, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(CrossTabWrite, flags, 4, uint32_t);
TITAN_NATIVE_FIELD(CrossTabWrite, key, 8, const char *);
TITAN_NATIVE_FIELD(CrossTabWrite, data, 16, const uint8_t *);
TITAN_NATIVE_FIELD(CrossTabWrite, size, 24, uint32_t);
TITAN_NATIVE_FIELD(CrossTabWrite, reserved, 28, uint32_t);
TITAN_NATIVE_FIELD(CrossTabWrite, expectedVersion, 32, uint64_t);

TITAN_NATIVE_RECORD(CrossTabEntryInfo, 24, 8);
TITAN_NATIVE_FIELD(CrossTabEntryInfo, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(CrossTabEntryInfo, flags, 4, uint32_t);
TITAN_NATIVE_FIELD(CrossTabEntryInfo, version, 8, uint64_t);
TITAN_NATIVE_FIELD(CrossTabEntryInfo, size, 16, uint32_t);
TITAN_NATIVE_FIELD(CrossTabEntryInfo, pending, 20, uint8_t);
TITAN_NATIVE_FIELD(CrossTabEntryInfo, redacted, 21, uint8_t);
TITAN_NATIVE_FIELD(CrossTabEntryInfo, reserved, 22, uint8_t[2]);

TITAN_NATIVE_RECORD(CrossTabChangeEvent, 96, 8);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, key, 4, char[64]);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, kind, 68, uint8_t);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, origin, 69, uint8_t);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, cause, 70, uint8_t);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, secret, 71, uint8_t);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, redacted, 72, uint8_t);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, reserved, 73, uint8_t[7]);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, version, 80, uint64_t);
TITAN_NATIVE_FIELD(CrossTabChangeEvent, writeId, 88, uint64_t);

TITAN_NATIVE_RECORD(PreviewPillWrite, 40, 8);
TITAN_NATIVE_FIELD(PreviewPillWrite, structSize, 0, uint32_t);
TITAN_NATIVE_FIELD(PreviewPillWrite, flags, 4, uint32_t);
TITAN_NATIVE_FIELD(PreviewPillWrite, key, 8, const char *);
TITAN_NATIVE_FIELD(PreviewPillWrite, text, 16, const char *);
TITAN_NATIVE_FIELD(PreviewPillWrite, tone, 24, int32_t);
TITAN_NATIVE_FIELD(PreviewPillWrite, reserved, 28, uint32_t);
TITAN_NATIVE_FIELD(PreviewPillWrite, countdownMs, 32, uint64_t);

TITAN_NATIVE_ENUM(VarbitDefSource, uint8_t);
TITAN_NATIVE_VALUE(VarbitDefSource::LiveCache, 0);
TITAN_NATIVE_VALUE(VarbitDefSource::Native, 1);
TITAN_NATIVE_VALUE(VarbitDefSource::Disk, 2);

TITAN_NATIVE_ENUM(SoundKind, int32_t);
TITAN_NATIVE_VALUE(SoundKind::SOUND_KIND_SYNTH, 0);
TITAN_NATIVE_VALUE(SoundKind::SOUND_KIND_JINGLE, 1);

TITAN_NATIVE_ENUM(BreakPhaseAbi, uint8_t);
TITAN_NATIVE_VALUE(BreakPhaseAbi::BREAK_PHASE_NONE, 0);
TITAN_NATIVE_VALUE(BreakPhaseAbi::BREAK_PHASE_PREPARE, 1);
TITAN_NATIVE_VALUE(BreakPhaseAbi::BREAK_PHASE_ACTIVE, 2);
TITAN_NATIVE_VALUE(BreakPhaseAbi::BREAK_PHASE_RESUME, 3);

TITAN_NATIVE_ENUM(BreakModeAbi, uint8_t);
TITAN_NATIVE_VALUE(BreakModeAbi::BREAK_MODE_AFK, 0);
TITAN_NATIVE_VALUE(BreakModeAbi::BREAK_MODE_LOGOUT, 1);

TITAN_NATIVE_ENUM(BreakRegistrationRoleAbi, uint8_t);
TITAN_NATIVE_VALUE(BreakRegistrationRoleAbi::BREAK_REGISTRATION_PARTICIPATES, 0);
TITAN_NATIVE_VALUE(BreakRegistrationRoleAbi::BREAK_REGISTRATION_OWNS_SCHEDULE, 1);

TITAN_NATIVE_ENUM(BreakReportStateAbi, uint8_t);
TITAN_NATIVE_VALUE(BreakReportStateAbi::BREAK_REPORT_NONE, 0);
TITAN_NATIVE_VALUE(BreakReportStateAbi::BREAK_REPORT_RUNNING, 1);
TITAN_NATIVE_VALUE(BreakReportStateAbi::BREAK_REPORT_PREPARING, 2);
TITAN_NATIVE_VALUE(BreakReportStateAbi::BREAK_REPORT_SAFE_PAUSED, 3);
TITAN_NATIVE_VALUE(BreakReportStateAbi::BREAK_REPORT_DEFERRED, 4);
TITAN_NATIVE_VALUE(BreakReportStateAbi::BREAK_REPORT_ERROR, 5);

TITAN_NATIVE_ENUM(ProxyRouteKindAbi, uint32_t);
TITAN_NATIVE_VALUE(ProxyRouteKindAbi::PROXY_ROUTE_DIRECT, 0);
TITAN_NATIVE_VALUE(ProxyRouteKindAbi::PROXY_ROUTE_PROXY, 1);
TITAN_NATIVE_VALUE(ProxyRouteKindAbi::PROXY_ROUTE_BLOCKED, 2);

TITAN_NATIVE_ENUM(GrandExchangeOfferState, int32_t);
TITAN_NATIVE_VALUE(GrandExchangeOfferState::Unknown, -1);
TITAN_NATIVE_VALUE(GrandExchangeOfferState::Empty, 0);
TITAN_NATIVE_VALUE(GrandExchangeOfferState::CancelledBuy, 1);
TITAN_NATIVE_VALUE(GrandExchangeOfferState::CancelledSell, 2);
TITAN_NATIVE_VALUE(GrandExchangeOfferState::Buying, 3);
TITAN_NATIVE_VALUE(GrandExchangeOfferState::Bought, 4);
TITAN_NATIVE_VALUE(GrandExchangeOfferState::Selling, 5);
TITAN_NATIVE_VALUE(GrandExchangeOfferState::Sold, 6);

TITAN_NATIVE_ENUM(GeRequestPhase, int32_t);
TITAN_NATIVE_VALUE(GeRequestPhase::Queued, 0);
TITAN_NATIVE_VALUE(GeRequestPhase::Opening, 1);
TITAN_NATIVE_VALUE(GeRequestPhase::Selecting, 2);
TITAN_NATIVE_VALUE(GeRequestPhase::Quantity, 3);
TITAN_NATIVE_VALUE(GeRequestPhase::Pricing, 4);
TITAN_NATIVE_VALUE(GeRequestPhase::Confirming, 5);
TITAN_NATIVE_VALUE(GeRequestPhase::Buying, 6);
TITAN_NATIVE_VALUE(GeRequestPhase::Cancelling, 7);
TITAN_NATIVE_VALUE(GeRequestPhase::Collecting, 8);
TITAN_NATIVE_VALUE(GeRequestPhase::Completed, 9);
TITAN_NATIVE_VALUE(GeRequestPhase::Cancelled, 10);
TITAN_NATIVE_VALUE(GeRequestPhase::Failed, 11);

TITAN_NATIVE_ENUM(CrossTabFlagAbi, uint32_t);
TITAN_NATIVE_VALUE(CrossTabFlagAbi::CROSS_TAB_SECRET, 1);
TITAN_NATIVE_VALUE(CrossTabFlagAbi::CROSS_TAB_ERASE, 2);
TITAN_NATIVE_VALUE(CrossTabFlagAbi::CROSS_TAB_CONDITIONAL, 4);

TITAN_NATIVE_ENUM(CrossTabChangeKindAbi, uint8_t);
TITAN_NATIVE_VALUE(CrossTabChangeKindAbi::CROSS_TAB_CHANGE_SET, 0);
TITAN_NATIVE_VALUE(CrossTabChangeKindAbi::CROSS_TAB_CHANGE_ERASED, 1);
TITAN_NATIVE_VALUE(CrossTabChangeKindAbi::CROSS_TAB_CHANGE_REJECTED, 2);

TITAN_NATIVE_ENUM(CrossTabChangeOriginAbi, uint8_t);
TITAN_NATIVE_VALUE(CrossTabChangeOriginAbi::CROSS_TAB_ORIGIN_REMOTE, 0);
TITAN_NATIVE_VALUE(CrossTabChangeOriginAbi::CROSS_TAB_ORIGIN_REPLAY, 1);
TITAN_NATIVE_VALUE(CrossTabChangeOriginAbi::CROSS_TAB_ORIGIN_OUTCOME, 2);

TITAN_NATIVE_ENUM(CrossTabChangeCauseAbi, uint8_t);
TITAN_NATIVE_VALUE(CrossTabChangeCauseAbi::CROSS_TAB_CAUSE_WRITER, 0);
TITAN_NATIVE_VALUE(CrossTabChangeCauseAbi::CROSS_TAB_CAUSE_SESSION_RESET, 1);
TITAN_NATIVE_VALUE(CrossTabChangeCauseAbi::CROSS_TAB_CAUSE_LIMIT, 2);
TITAN_NATIVE_VALUE(CrossTabChangeCauseAbi::CROSS_TAB_CAUSE_CONFLICT, 3);
TITAN_NATIVE_VALUE(CrossTabChangeCauseAbi::CROSS_TAB_CAUSE_NOT_PERMITTED, 4);

TITAN_NATIVE_ENUM(PreviewPillFlagAbi, uint32_t);
TITAN_NATIVE_VALUE(PreviewPillFlagAbi::PREVIEW_PILL_CLEAR, 1);
TITAN_NATIVE_VALUE(PreviewPillFlagAbi::PREVIEW_PILL_COUNTDOWN, 2);

TITAN_NATIVE_VALUE(kMaxVarClientStringBytes, 16777217);
TITAN_NATIVE_VALUE(kMaxErrorLen, 160);
TITAN_NATIVE_VALUE(kMaxLabelLen, 64);
TITAN_NATIVE_VALUE(kMaxActionCount, 5);
TITAN_NATIVE_VALUE(kMaxItemSubOpCount, 20);
TITAN_NATIVE_VALUE(kMaxDescriptionLen, 256);
TITAN_NATIVE_VALUE(kMaxAuthorLen, 64);
TITAN_NATIVE_VALUE(kMaxVersionLen, 16);
TITAN_NATIVE_VALUE(EntityType::LOCATION, 0);
TITAN_NATIVE_VALUE(EntityType::NPC, 1);
TITAN_NATIVE_VALUE(EntityType::PLAYER, 2);
TITAN_NATIVE_VALUE(EntityType::NONE, 127);
TITAN_NATIVE_VALUE(RenderLayerAbi::ABOVE_SCENE, 0);
TITAN_NATIVE_VALUE(RenderLayerAbi::ABOVE_WIDGETS, 1);
TITAN_NATIVE_VALUE(AnchorAbi::DYNAMIC, 0);
TITAN_NATIVE_VALUE(AnchorAbi::TOP_CENTER, 1);
TITAN_NATIVE_VALUE(AnchorAbi::LEFT_CENTER, 2);
TITAN_NATIVE_VALUE(AnchorAbi::RIGHT_CENTER, 3);
TITAN_NATIVE_VALUE(AnchorAbi::ABOVE_CHATBOX_RIGHT, 4);
TITAN_NATIVE_VALUE(AnchorAbi::TOOLTIP, 5);
TITAN_NATIVE_VALUE(kWorldMapApiVersion, 1);
TITAN_NATIVE_VALUE(kInstanceTemplatePlaneCount, 4);
TITAN_NATIVE_VALUE(kInstanceTemplateChunkXCount, 13);
TITAN_NATIVE_VALUE(kInstanceTemplateChunkYCount, 13);
TITAN_NATIVE_VALUE(kInstanceTemplateChunkCount, 676);
TITAN_NATIVE_VALUE(kWebWalkerApiVersion, 1);
TITAN_NATIVE_VALUE(kCollisionSnapshotApiVersion, 1);
TITAN_NATIVE_VALUE(kCollisionRegionSize, 64);
TITAN_NATIVE_VALUE(kCollisionPlaneCount, 4);
TITAN_NATIVE_VALUE(kCollisionRegionFlagCount, 16384);
TITAN_NATIVE_VALUE(kWebPathMaxForbiddenTiles, 4096);
TITAN_NATIVE_VALUE(kWebPathMaxSteps, 16384);
TITAN_NATIVE_VALUE(kWebPathDefaultTimeoutMs, 60000);
TITAN_NATIVE_VALUE(kWebPathMaxTimeoutMs, 600000);
TITAN_NATIVE_VALUE(kWebPathMessageCapacity, 192);
TITAN_NATIVE_VALUE(kWebPathStepNameCapacity, 96);
TITAN_NATIVE_VALUE(kWebPathCostPointsPerWhole, 10);
TITAN_NATIVE_VALUE(kWebPathWalkCostPoints, 5);
TITAN_NATIVE_VALUE(CollisionSnapshotStatus::Unavailable, 0);
TITAN_NATIVE_VALUE(CollisionSnapshotStatus::Ready, 1);
TITAN_NATIVE_VALUE(CollisionSnapshotStatus::MissingRegion, 2);
TITAN_NATIVE_VALUE(CollisionSnapshotStatus::BufferTooSmall, 3);
TITAN_NATIVE_VALUE(CollisionSnapshotStatus::InvalidRequest, 4);
TITAN_NATIVE_VALUE(WebPathRouteSpace::Global, 0);
TITAN_NATIVE_VALUE(WebPathRouteSpace::CurrentInstance, 1);
TITAN_NATIVE_VALUE(WebPathRequestFlag::UseLocalPlayer, 1);
TITAN_NATIVE_VALUE(WebPathOption::Transports, 1);
TITAN_NATIVE_VALUE(WebPathOption::Teleports, 2);
TITAN_NATIVE_VALUE(WebPathOption::EquippedItemTeleports, 4);
TITAN_NATIVE_VALUE(WebPathOption::MinigameTeleports, 8);
TITAN_NATIVE_VALUE(WebPathOption::PohRoutes, 16);
TITAN_NATIVE_VALUE(WebPathOption::Charters, 32);
TITAN_NATIVE_VALUE(WebPathOption::AvoidWilderness, 64);
TITAN_NATIVE_VALUE(WebPathOption::Default, 87);
TITAN_NATIVE_VALUE(WebPathPhase::None, 0);
TITAN_NATIVE_VALUE(WebPathPhase::Queued, 1);
TITAN_NATIVE_VALUE(WebPathPhase::Running, 2);
TITAN_NATIVE_VALUE(WebPathPhase::Complete, 3);
TITAN_NATIVE_VALUE(WebPathPhase::Failed, 4);
TITAN_NATIVE_VALUE(WebPathPhase::Cancelled, 5);
TITAN_NATIVE_VALUE(WebPathResult::None, 0);
TITAN_NATIVE_VALUE(WebPathResult::Exact, 1);
TITAN_NATIVE_VALUE(WebPathResult::PartialWithinThreeTiles, 2);
TITAN_NATIVE_VALUE(WebPathResult::NoPath, 3);
TITAN_NATIVE_VALUE(WebPathResult::Timeout, 4);
TITAN_NATIVE_VALUE(WebPathResult::Cancelled, 5);
TITAN_NATIVE_VALUE(WebPathResult::InvalidRequest, 6);
TITAN_NATIVE_VALUE(WebPathResult::NotLoggedIn, 7);
TITAN_NATIVE_VALUE(WebPathResult::CollisionUnavailable, 8);
TITAN_NATIVE_VALUE(WebPathResult::ProviderUnavailable, 9);
TITAN_NATIVE_VALUE(WebPathResult::Busy, 10);
TITAN_NATIVE_VALUE(WebPathResult::InternalError, 11);
TITAN_NATIVE_VALUE(WebPathStepKind::Walk, 0);
TITAN_NATIVE_VALUE(WebPathStepKind::Transport, 1);
TITAN_NATIVE_VALUE(WebPathStepKind::Teleport, 2);
TITAN_NATIVE_VALUE(kWebWalkApiVersion, 1);
TITAN_NATIVE_VALUE(WebWalkFlag::ManageRun, 1);
TITAN_NATIVE_VALUE(WebWalkFlag::DrinkStamina, 2);
TITAN_NATIVE_VALUE(WebWalkFlag::ManualTick, 4);
TITAN_NATIVE_VALUE(WebWalkFlag::Default, 1);
TITAN_NATIVE_VALUE(WebWalkPhase::None, 0);
TITAN_NATIVE_VALUE(WebWalkPhase::Planning, 1);
TITAN_NATIVE_VALUE(WebWalkPhase::Walking, 2);
TITAN_NATIVE_VALUE(WebWalkPhase::Transiting, 3);
TITAN_NATIVE_VALUE(WebWalkPhase::Arrived, 4);
TITAN_NATIVE_VALUE(WebWalkPhase::Failed, 5);
TITAN_NATIVE_VALUE(WebWalkPhase::Cancelled, 6);
TITAN_NATIVE_VALUE(PlayerCompositionStatus::Unavailable, 0);
TITAN_NATIVE_VALUE(PlayerCompositionStatus::Available, 1);
TITAN_NATIVE_VALUE(PlayerCompositionStatus::MissingOffsets, 2);
TITAN_NATIVE_VALUE(PlayerCompositionStatus::NullPlayer, 3);
TITAN_NATIVE_VALUE(PlayerCompositionStatus::NullModel, 4);
TITAN_NATIVE_VALUE(PlayerCompositionStatus::NpcTransform, 5);
TITAN_NATIVE_VALUE(PlayerCompositionStatus::BadVector, 6);
TITAN_NATIVE_VALUE(PlayerCompositionSlotKind::Empty, 0);
TITAN_NATIVE_VALUE(PlayerCompositionSlotKind::Item, 1);
TITAN_NATIVE_VALUE(PlayerCompositionSlotKind::NonItem, 2);
TITAN_NATIVE_VALUE(PlayerCompositionSlotKind::UnknownRaw, 3);
TITAN_NATIVE_VALUE(kMaxPlayerCompositionSlots, 32);
TITAN_NATIVE_VALUE(GroundItemOwnershipAbi::NONE, 0);
TITAN_NATIVE_VALUE(GroundItemOwnershipAbi::SELF_PLAYER, 1);
TITAN_NATIVE_VALUE(GroundItemOwnershipAbi::OTHER_PLAYER, 2);
TITAN_NATIVE_VALUE(GroundItemOwnershipAbi::GROUP_IRONMAN, 3);
TITAN_NATIVE_VALUE(kMaxCs2IntResults, 32);
TITAN_NATIVE_VALUE(kMaxHookArgStringLen, 22);
TITAN_NATIVE_VALUE(kMaxHookArgs, 16);
TITAN_NATIVE_VALUE(KeyboardMods::SHIFT, 1);
TITAN_NATIVE_VALUE(KeyboardMods::CTRL, 2);
TITAN_NATIVE_VALUE(KeyboardMods::ALT, 4);
TITAN_NATIVE_VALUE(KeyboardKey::ENTER, 0);
TITAN_NATIVE_VALUE(KeyboardKey::ESCAPE, 1);
TITAN_NATIVE_VALUE(KeyboardKey::BACKSPACE, 2);
TITAN_NATIVE_VALUE(KeyboardKey::DELETE_KEY, 3);
TITAN_NATIVE_VALUE(KeyboardKey::TAB, 4);
TITAN_NATIVE_VALUE(KeyboardKey::SPACE, 5);
TITAN_NATIVE_VALUE(KeyboardKey::HOME, 6);
TITAN_NATIVE_VALUE(KeyboardKey::END_KEY, 7);
TITAN_NATIVE_VALUE(KeyboardKey::PAGE_UP, 8);
TITAN_NATIVE_VALUE(KeyboardKey::PAGE_DOWN, 9);
TITAN_NATIVE_VALUE(KeyboardKey::INSERT, 10);
TITAN_NATIVE_VALUE(KeyboardKey::ARROW_UP, 11);
TITAN_NATIVE_VALUE(KeyboardKey::ARROW_DOWN, 12);
TITAN_NATIVE_VALUE(KeyboardKey::ARROW_LEFT, 13);
TITAN_NATIVE_VALUE(KeyboardKey::ARROW_RIGHT, 14);
TITAN_NATIVE_VALUE(KeyboardKey::F1, 15);
TITAN_NATIVE_VALUE(KeyboardKey::F2, 16);
TITAN_NATIVE_VALUE(KeyboardKey::F3, 17);
TITAN_NATIVE_VALUE(KeyboardKey::F4, 18);
TITAN_NATIVE_VALUE(KeyboardKey::F5, 19);
TITAN_NATIVE_VALUE(KeyboardKey::F6, 20);
TITAN_NATIVE_VALUE(KeyboardKey::F7, 21);
TITAN_NATIVE_VALUE(KeyboardKey::F8, 22);
TITAN_NATIVE_VALUE(KeyboardKey::F9, 23);
TITAN_NATIVE_VALUE(KeyboardKey::F10, 24);
TITAN_NATIVE_VALUE(KeyboardKey::F11, 25);
TITAN_NATIVE_VALUE(KeyboardKey::F12, 26);
TITAN_NATIVE_VALUE(KeyboardKey::SHIFT, 27);
TITAN_NATIVE_VALUE(KeyboardKey::CONTROL, 28);
TITAN_NATIVE_VALUE(KeyboardKey::ALT, 29);
TITAN_NATIVE_VALUE(KeyboardTypeCallbackPhase::PUMP_THREAD, 0);
TITAN_NATIVE_VALUE(KeyboardTypeCallbackPhase::CLIENT_TICK, 1);
TITAN_NATIVE_VALUE(KeyboardTypeCallbackPhase::PRE_GAME_LOOP, 2);
TITAN_NATIVE_VALUE(kMaxWidgetTextLen, 256);
TITAN_NATIVE_VALUE(kMaxWidgetAddressDepth, 12);
TITAN_NATIVE_VALUE(kMaxWidgetDynamicChildren, 2048);
TITAN_NATIVE_VALUE(kMaxWidgetQueryResults, 50000);
TITAN_NATIVE_VALUE(kAllWidgetGroups, 4294967295);
TITAN_NATIVE_VALUE(kMaxItemContainerSlots, 2048);
TITAN_NATIVE_VALUE(kMaxItemCompositionActions, 32);
TITAN_NATIVE_VALUE(kMaxItemCompositionActionLen, 64);
TITAN_NATIVE_VALUE(kMaxWorldListEntries, 2048);
TITAN_NATIVE_VALUE(LoginGameStateAbi::UNKNOWN, -1);
TITAN_NATIVE_VALUE(LoginGameStateAbi::LOGIN_SCREEN, 10);
TITAN_NATIVE_VALUE(LoginGameStateAbi::LOGIN_AUTHENTICATOR, 11);
TITAN_NATIVE_VALUE(LoginGameStateAbi::LOGGING_IN, 20);
TITAN_NATIVE_VALUE(LoginGameStateAbi::LOADING, 25);
TITAN_NATIVE_VALUE(LoginGameStateAbi::LOGGED_IN, 30);
TITAN_NATIVE_VALUE(LoginGameStateAbi::HOPPING, 45);
TITAN_NATIVE_VALUE(kLoginFlowApiVersion, 1);
TITAN_NATIVE_VALUE(LoginOperationAdvanceAbi::UNAVAILABLE, 0);
TITAN_NATIVE_VALUE(LoginOperationAdvanceAbi::PENDING, 1);
TITAN_NATIVE_VALUE(LoginOperationAdvanceAbi::COMPLETED, 2);
TITAN_NATIVE_VALUE(LoginOperationAdvanceAbi::FAILED, 3);
TITAN_NATIVE_VALUE(kMaxChatMessageLen, 512);
TITAN_NATIVE_VALUE(MouseButton::LEFT, 0);
TITAN_NATIVE_VALUE(MouseButton::RIGHT, 1);
TITAN_NATIVE_VALUE(MouseButton::MIDDLE, 2);
TITAN_NATIVE_VALUE(OverheadTextReadResult::Invalid, 0);
TITAN_NATIVE_VALUE(OverheadTextReadResult::Success, 1);
TITAN_NATIVE_VALUE(OverheadTextReadResult::BufferTooSmall, 2);
TITAN_NATIVE_VALUE(OverheadTextCapability::DirectAccess, 1);
TITAN_NATIVE_VALUE(OverheadTextCapability::Events, 2);
TITAN_NATIVE_VALUE(kScreenshotApiVersion, 1);
TITAN_NATIVE_VALUE(kScreenshotMaxUnreleased, 4);
TITAN_NATIVE_VALUE(kScreenshotMaxPngBytes, 16777216);
TITAN_NATIVE_VALUE(kScreenshotMessageCapacity, 128);
TITAN_NATIVE_VALUE(ScreenshotPhase::None, 0);
TITAN_NATIVE_VALUE(ScreenshotPhase::Pending, 1);
TITAN_NATIVE_VALUE(ScreenshotPhase::Ready, 2);
TITAN_NATIVE_VALUE(ScreenshotPhase::Failed, 3);
TITAN_NATIVE_VALUE(kBreakRegistryApiVersion, 1);
TITAN_NATIVE_VALUE(kBreakPluginIdCapacity, 64);
TITAN_NATIVE_VALUE(kBreakDisplayNameCapacity, 96);
TITAN_NATIVE_VALUE(kBreakReasonCapacity, 192);
TITAN_NATIVE_VALUE(kProxyRouteApiVersion, 1);
TITAN_NATIVE_VALUE(kProxyRouteIdCapacity, 64);
TITAN_NATIVE_VALUE(kProxyRouteLabelCapacity, 128);
TITAN_NATIVE_VALUE(kProxyFailureStageCapacity, 64);
TITAN_NATIVE_VALUE(kCrossTabMaxKeyLen, 63);
TITAN_NATIVE_VALUE(kCrossTabKeyCapacity, 64);
TITAN_NATIVE_VALUE(kCrossTabMaxValueBytes, 16384);
TITAN_NATIVE_VALUE(kCrossTabAbsent, 4294967295);
TITAN_NATIVE_VALUE(kPreviewPillMaxKeyLen, 32);
TITAN_NATIVE_VALUE(kPreviewPillMaxTextBytes, 63);
TITAN_NATIVE_VALUE(kPreviewPillsPerPlugin, 2);
TITAN_NATIVE_VALUE(kPreviewPillsPerTab, 8);
TITAN_NATIVE_VALUE(kPreviewPillMaxCountdownMs, 3599999000);

// Payload tags are immutable contract versions, independent of SDK releases.
TITAN_NATIVE_VALUE(kNativePayloadVersion, 1);
TITAN_NATIVE_VALUE(WorldMapState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(CollisionSceneSnapshotState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(WebPathRequestState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(WebPathSummaryState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(WebPathStepState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(WebWalkRequestState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(WebWalkStatusState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(ItemDefSnapshot{}.apiVersion, 1);
TITAN_NATIVE_VALUE(NpcDefSnapshot{}.apiVersion, 1);
TITAN_NATIVE_VALUE(ObjDefSnapshot{}.apiVersion, 1);
TITAN_NATIVE_VALUE(VarbitDefSnapshot{}.apiVersion, 1);
TITAN_NATIVE_VALUE(LoginFlowState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(ScreenshotStatusState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(BreakRegistrationState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(BreakReportState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(BreakCommandState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(BreakParticipantState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(SanitizedProxyRouteState{}.apiVersion, 1);
TITAN_NATIVE_VALUE(ProxyRouteStatusState{}.apiVersion, 1);

#undef TITAN_NATIVE_RECORD
#undef TITAN_NATIVE_FIELD
#undef TITAN_NATIVE_ENUM
#undef TITAN_NATIVE_VALUE

} // namespace TitanPluginSdk::NativeAbi::PayloadV1
