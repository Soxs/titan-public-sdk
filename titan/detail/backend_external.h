/// @file titan/detail/backend_external.h
/// @brief `ExternalBackend` -- the plugin-side `IBackend` implementation.
///
/// Wraps this instance's local `HostApi` view imported from negotiated native
/// capability tables. Missing optional operations remain null and each method
/// checks availability before invoking them.
///
/// Header-only so plugin DLLs do not need to link a separate library; a
/// separate backend lives with each native plugin instance.

#pragma once

#include "abi.h"
#include "backend.h"
#include "native_lifetime.h"

#include <algorithm>

namespace titan {
namespace detail {

class ExternalBackend final : public IBackend {
public:
    explicit ExternalBackend(const TitanPluginSdk::HostApi* api) noexcept
        : api_(api) {}

    void setHostApi(const TitanPluginSdk::HostApi* api) noexcept { api_ = api; }
    const TitanPluginSdk::HostApi* hostApi() const noexcept { return api_; }
    void setLifetime(const TitanPluginSdk::NativeAbi::LifetimeV1* lifetime) noexcept {
        lifetime_ = lifetime;
    }

    uint32_t getHintArrows(TitanPluginSdk::HintArrowState* out, uint32_t capacity) override {
        return api_ && api_->getHintArrows
            ? api_->getHintArrows(out, capacity) : TitanPluginSdk::kHintArrowsUnavailable;
    }
    uint8_t getServerHintArrow(TitanPluginSdk::HintArrowState* out) override {
        if (out) *out = {};
        return api_ && api_->getServerHintArrow ? api_->getServerHintArrow(out)
            : static_cast<uint8_t>(TitanPluginSdk::HintArrowReadStatus::Unavailable);
    }
    uint8_t setHintArrowCoordinate(const TitanPluginSdk::HintArrowCoordinateTarget* target) override {
        return api_ && api_->setHintArrowCoordinate ? api_->setHintArrowCoordinate(target)
            : static_cast<uint8_t>(TitanPluginSdk::HintArrowUpdateResult::Unavailable);
    }
    uint8_t setHintArrowActor(const TitanPluginSdk::HintArrowActorTarget* target) override {
        return api_ && api_->setHintArrowActor ? api_->setHintArrowActor(target)
            : static_cast<uint8_t>(TitanPluginSdk::HintArrowUpdateResult::Unavailable);
    }
    uint8_t clearHintArrow() override {
        return api_ && api_->clearHintArrow ? api_->clearHintArrow()
            : static_cast<uint8_t>(TitanPluginSdk::HintArrowUpdateResult::Unavailable);
    }
    uint8_t getHintArrowWorldPoint(const TitanPluginSdk::HintArrowState* expected,
                                  TitanPluginSdk::WorldPointState* out) override {
        if (out) *out = {};
        return api_ && api_->getHintArrowWorldPoint ? api_->getHintArrowWorldPoint(expected, out) : 0;
    }

    uint8_t getWidgetModelIdAtPath(const TitanPluginSdk::WidgetAddressState* address,
                                  int32_t* outModelId) override {
        if (outModelId) *outModelId = -1;
        return api_ && api_->getWidgetModelIdAtPath
            ? api_->getWidgetModelIdAtPath(address, outModelId) : 0;
    }
    uint8_t copyItemWornAction(int32_t itemId, uint32_t actionIndex,
                              char* outUtf8, uint32_t capacity,
                              uint32_t* outRequired) override {
        if (outRequired) *outRequired = 0;
        return api_ && api_->copyItemWornAction
            ? api_->copyItemWornAction(itemId, actionIndex, outUtf8, capacity, outRequired) : 0;
    }
    uint8_t getNpcBaseId(int32_t worldViewId, int32_t hashIndex, int32_t* outBaseId) override {
        if (outBaseId) *outBaseId = -1;
        return api_ && api_->getNpcBaseId
            ? api_->getNpcBaseId(worldViewId, hashIndex, outBaseId) : 0;
    }

    // --- Logging ---
    void log(const char* msg) override {
        if (api_ && api_->log && msg) api_->log(msg);
    }

    // --- Game state queries ---
    uint8_t getClientState(TitanPluginSdk::ClientState* out) override {
        return (api_ && api_->getClientState) ? api_->getClientState(out) : 0;
    }
    int32_t getLiveStateEpoch() override {
        // Fast path when the host (SDK 120+) provides the cheap epoch; older
        // hosts leave the pointer null, so fall back to the full ClientState
        // read for byte-identical behaviour.
        if (api_ && api_->getLiveStateEpoch) return api_->getLiveStateEpoch();
        return IBackend::getLiveStateEpoch();
    }
    uint32_t getPlayers(TitanPluginSdk::PlayerState* out, uint32_t max) override {
        return (api_ && api_->getPlayers) ? api_->getPlayers(out, max) : 0;
    }
    uint8_t getPlayerComposition(uint64_t playerEntityPtr,
                                 TitanPluginSdk::PlayerCompositionState* out) override {
        return (api_ && api_->getPlayerComposition)
            ? api_->getPlayerComposition(playerEntityPtr, out) : 0;
    }
    uint32_t getNpcs(TitanPluginSdk::NpcState* out, uint32_t max) override {
        return (api_ && api_->getNpcs) ? api_->getNpcs(out, max) : 0;
    }
    uint32_t getTileObjects(int32_t radius, TitanPluginSdk::TileObjectState* out, uint32_t max) override {
        return (api_ && api_->getTileObjects) ? api_->getTileObjects(radius, out, max) : 0;
    }
    uint32_t getGroundItems(int32_t radius, TitanPluginSdk::GroundItemState* out, uint32_t max) override {
        return (api_ && api_->getGroundItems) ? api_->getGroundItems(radius, out, max) : 0;
    }
    uint32_t getTileObjectsOnTile(int32_t plane, int32_t tileX, int32_t tileY,
                                  TitanPluginSdk::TileObjectState* out, uint32_t max) override {
        return (api_ && api_->getTileObjectsOnTile)
            ? api_->getTileObjectsOnTile(plane, tileX, tileY, out, max) : 0;
    }
    uint32_t getTileObjectsOnTileInWorldView(
        int32_t worldViewId, int32_t plane, int32_t tileX, int32_t tileY,
        TitanPluginSdk::TileObjectState* out, uint32_t max) override {
        if (api_ && api_->getTileObjectsOnTileInWorldView) {
            return api_->getTileObjectsOnTileInWorldView(
                worldViewId, plane, tileX, tileY, out, max);
        }
        if (!api_ || !api_->getTileObjectsOnTile) return 0;
        TitanPluginSdk::ClientState client{};
        if (worldViewId >= 0 &&
            (!api_->getClientState || !api_->getClientState(&client) ||
             client.currentWorldViewId != worldViewId)) {
            return 0;
        }
        return api_->getTileObjectsOnTile(plane, tileX, tileY, out, max);
    }
    uint32_t getGroundItemsOnTile(int32_t plane, int32_t tileX, int32_t tileY,
                                  TitanPluginSdk::GroundItemState* out, uint32_t max) override {
        return (api_ && api_->getGroundItemsOnTile)
            ? api_->getGroundItemsOnTile(plane, tileX, tileY, out, max) : 0;
    }
    uint32_t getProjectiles(TitanPluginSdk::ProjectileState* out, uint32_t max) override {
        return (api_ && api_->getProjectiles) ? api_->getProjectiles(out, max) : 0;
    }
    uint32_t getGraphicsObjects(TitanPluginSdk::GraphicsObjectState* out, uint32_t max) override {
        return (api_ && api_->getGraphicsObjects) ? api_->getGraphicsObjects(out, max) : 0;
    }
    uint32_t getActorPathQueue(uint64_t entityPtr,
                               TitanPluginSdk::WorldPointState* out,
                               uint32_t max) override {
        return (api_ && api_->getActorPathQueue)
            ? api_->getActorPathQueue(entityPtr, out, max) : 0;
    }
    uint32_t getActorSpotAnims(uint64_t entityPtr,
                               TitanPluginSdk::ActorSpotAnimState* out,
                               uint32_t max) override {
        return (api_ && api_->getActorSpotAnims)
            ? api_->getActorSpotAnims(entityPtr, out, max) : 0;
    }
    uint8_t getWorldViewById(int32_t worldViewId, uint64_t* outPtr) override {
        return (api_ && api_->getWorldViewById)
            ? api_->getWorldViewById(worldViewId, outPtr) : 0;
    }
    uint8_t getTopLevelWorldView(uint64_t* outPtr) override {
        return (api_ && api_->getTopLevelWorldView)
            ? api_->getTopLevelWorldView(outPtr) : 0;
    }
    uint8_t getInstanceTemplateChunks(
            TitanPluginSdk::InstanceTemplateChunksState* out) override {
        return (api_ && api_->getInstanceTemplateChunks)
            ? api_->getInstanceTemplateChunks(out) : 0;
    }
    uint8_t worldPointFromLocalInstance(
            const TitanPluginSdk::WorldPointState* in,
            TitanPluginSdk::WorldPointState* out) override {
        return (api_ && api_->worldPointFromLocalInstance)
            ? api_->worldPointFromLocalInstance(in, out) : 0;
    }
    uint8_t worldPointToLocalInstance(
            const TitanPluginSdk::WorldPointState* in,
            TitanPluginSdk::WorldPointState* out) override {
        return (api_ && api_->worldPointToLocalInstance)
            ? api_->worldPointToLocalInstance(in, out) : 0;
    }
    uint8_t getLocalDestinationLocation(
            TitanPluginSdk::LocalPointState* out) override {
        return (api_ && api_->getLocalDestinationLocation)
            ? api_->getLocalDestinationLocation(out) : 0;
    }
    uint8_t getWorldDestinationLocation(
            TitanPluginSdk::WorldPointState* out) override {
        return (api_ && api_->getWorldDestinationLocation)
            ? api_->getWorldDestinationLocation(out) : 0;
    }
    // --- Camera ---
    uint8_t getCameraState(TitanPluginSdk::CameraState* out) override {
        return (api_ && api_->getCameraState) ? api_->getCameraState(out) : 0;
    }

    // --- Interface scale (SDK 109) ---
    uint8_t getInterfaceScale(float* outScaleX, float* outScaleY,
                              int32_t* outCanvasOriginX,
                              int32_t* outCanvasOriginY) override {
        if (api_ && api_->getInterfaceScale) {
            return api_->getInterfaceScale(outScaleX, outScaleY,
                                           outCanvasOriginX, outCanvasOriginY);
        }
        if (outScaleX) *outScaleX = 1.0f;
        if (outScaleY) *outScaleY = 1.0f;
        if (outCanvasOriginX) *outCanvasOriginX = 0;
        if (outCanvasOriginY) *outCanvasOriginY = 0;
        return 0;
    }

    // --- Rendering ---
    void setImGuiContext(void* ctx) override {
        if (api_ && api_->setImGuiContext) api_->setImGuiContext(ctx);
    }
    uint8_t worldToScreen(int32_t wx, int32_t wy, int32_t wz,
                          int32_t* sx, int32_t* sy) override {
        return (api_ && api_->worldToScreen) ? api_->worldToScreen(wx, wy, wz, sx, sy) : 0;
    }
    uint8_t tileToScreen(int32_t tx, int32_t ty, int32_t plane, int32_t hOff,
                         int32_t* sx, int32_t* sy) override {
        return (api_ && api_->tileToScreen) ? api_->tileToScreen(tx, ty, plane, hOff, sx, sy) : 0;
    }
    int32_t getTileHeight(int32_t px, int32_t py, int32_t plane) override {
        return (api_ && api_->getTileHeight) ? api_->getTileHeight(px, py, plane) : 0;
    }
    void drawTileQuad(int32_t tx, int32_t ty, int32_t plane,
                      uint32_t fill, uint32_t outline) override {
        if (api_ && api_->drawTileQuad) api_->drawTileQuad(tx, ty, plane, fill, outline);
    }
    void drawTileRegion(int32_t minTX, int32_t minTY, int32_t maxTX, int32_t maxTY, int32_t plane,
                        uint32_t fill, uint32_t outline) override {
        if (api_ && api_->drawTileRegion) api_->drawTileRegion(minTX, minTY, maxTX, maxTY, plane, fill, outline);
    }
    void drawEntityBox(int32_t px, int32_t py, int32_t plane,
                       int32_t tileSize, int32_t height, uint32_t color) override {
        if (api_ && api_->drawEntityBox) api_->drawEntityBox(px, py, plane, tileSize, height, color);
    }
    void drawTextAtWorld(int32_t wx, int32_t wy, int32_t wz,
                         const char* text, uint32_t color, uint8_t centered) override {
        if (api_ && api_->drawTextAtWorld && text) api_->drawTextAtWorld(wx, wy, wz, text, color, centered);
    }
    uint8_t worldToScreenInWorldView(int32_t worldViewId,
                                     int32_t px, int32_t wy, int32_t py,
                                     int32_t plane,
                                     int32_t* sx, int32_t* sy) override {
        return (api_ && api_->worldToScreenInWorldView)
            ? api_->worldToScreenInWorldView(worldViewId, px, wy, py, plane, sx, sy)
            : 0;
    }
    int32_t getTileHeightInWorldView(int32_t worldViewId,
                                     int32_t px, int32_t py,
                                     int32_t plane) override {
        return (api_ && api_->getTileHeightInWorldView)
            ? api_->getTileHeightInWorldView(worldViewId, px, py, plane)
            : 0;
    }
    void drawTileQuadInWorldView(int32_t worldViewId,
                                 int32_t tx, int32_t ty, int32_t plane,
                                 uint32_t fill, uint32_t outline) override {
        if (api_ && api_->drawTileQuadInWorldView) {
            api_->drawTileQuadInWorldView(worldViewId, tx, ty, plane, fill, outline);
        }
    }
    void drawTileRegionInWorldView(int32_t worldViewId,
                                   int32_t minTX, int32_t minTY,
                                   int32_t maxTX, int32_t maxTY,
                                   int32_t plane,
                                   uint32_t fill, uint32_t outline) override {
        if (api_ && api_->drawTileRegionInWorldView) {
            api_->drawTileRegionInWorldView(
                worldViewId, minTX, minTY, maxTX, maxTY, plane, fill, outline);
        }
    }
    void drawTextAtWorldInWorldView(int32_t worldViewId,
                                    int32_t px, int32_t wy, int32_t py,
                                    int32_t plane,
                                    const char* text, uint32_t color,
                                    uint8_t centered) override {
        if (api_ && api_->drawTextAtWorldInWorldView && text) {
            api_->drawTextAtWorldInWorldView(
                worldViewId, px, wy, py, plane, text, color, centered);
        }
    }

    // --- Scene queries ---
    int32_t getCollisionFlag(int32_t plane, int32_t tx, int32_t ty) override {
        return (api_ && api_->getCollisionFlag) ? api_->getCollisionFlag(plane, tx, ty) : 0;
    }

    // --- Input ---
    uint8_t getMousePos(int32_t* ox, int32_t* oy) override {
        return (api_ && api_->getMousePos) ? api_->getMousePos(ox, oy) : 0;
    }

    // --- Screen-space rendering ---
    void drawScreenText(int32_t sx, int32_t sy, const char* text, uint32_t color) override {
        if (api_ && api_->drawScreenText && text) api_->drawScreenText(sx, sy, text, color);
    }
    void drawScreenRect(int32_t x, int32_t y, int32_t w, int32_t h, uint32_t color) override {
        if (api_ && api_->drawScreenRect) api_->drawScreenRect(x, y, w, h, color);
    }
    void drawScreenLine(int32_t x1, int32_t y1, int32_t x2, int32_t y2,
                        uint32_t color, float thickness) override {
        if (api_ && api_->drawScreenLine) api_->drawScreenLine(x1, y1, x2, y2, color, thickness);
    }

    // --- Var system ---
    int32_t getVarbit(int32_t id) override {
        return (api_ && api_->getVarbit) ? api_->getVarbit(id) : -1;
    }
    int32_t getVarp(int32_t id) override {
        return (api_ && api_->getVarp) ? api_->getVarp(id) : -1;
    }

    // --- Prayer ---
    uint8_t isPrayerActive(int32_t ordinal) override {
        return (api_ && api_->isPrayerActive) ? api_->isPrayerActive(ordinal) : 0;
    }

    // --- Skills ---
    int32_t getBoostedSkillLevel(int32_t skill) override {
        return (api_ && api_->getBoostedSkillLevel) ? api_->getBoostedSkillLevel(skill) : -1;
    }
    int32_t getRealSkillLevel(int32_t skill) override {
        return (api_ && api_->getRealSkillLevel) ? api_->getRealSkillLevel(skill) : -1;
    }
    int32_t getSkillExperience(int32_t skill) override {
        return (api_ && api_->getSkillExperience) ? api_->getSkillExperience(skill) : -1;
    }

    // --- Synthetic action dispatch ---
    uint8_t executeSyntheticAction(uint32_t opcode, int32_t identifier,
                                   int32_t param0, int32_t param1,
                                   int32_t worldViewId,
                                   int32_t cx, int32_t cy,
                                   const char* action, const char* target,
                                   uint8_t skipClick) override {
        if (!api_ || !api_->executeSyntheticAction) return 0;
        return api_->executeSyntheticAction(opcode, identifier, param0, param1,
                                            worldViewId, cx, cy, action, target, skipClick);
    }
    uint8_t executeSyntheticEntry(const TitanPluginSdk::SyntheticActionEntry* entry) override {
        return (api_ && api_->executeSyntheticEntry) ? api_->executeSyntheticEntry(entry) : 0;
    }

    uint8_t executeSelectedActionPair(const TitanPluginSdk::SelectedActionPair* pair) override {
        return (api_ && api_->sdkVersion >= 128 && api_->executeSelectedActionPair)
            ? api_->executeSelectedActionPair(pair) : 0;
    }
    uint8_t resolveActionClickPoint(const TitanPluginSdk::ActionClickPointSpec* action,
                                    int32_t* screenX,
                                    int32_t* screenY) override {
        return (api_ && api_->resolveActionClickPoint)
            ? api_->resolveActionClickPoint(action, screenX, screenY)
            : 0;
    }

    // --- Walking ---
    uint8_t walkTo(int32_t sx, int32_t sy) override {
        return (api_ && api_->walkTo) ? api_->walkTo(sx, sy) : 0;
    }
    uint8_t walkToWorld(int32_t wx, int32_t wy, int32_t plane) override {
        return (api_ && api_->walkToWorld) ? api_->walkToWorld(wx, wy, plane) : 0;
    }

    // --- Entity hiding ---
    void setEntityHidden(uint8_t type, uint8_t hidden) override {
        if (api_ && api_->setEntityHidden) api_->setEntityHidden(type, hidden);
    }
    uint8_t getEntityHidden(uint8_t type) override {
        return (api_ && api_->getEntityHidden) ? api_->getEntityHidden(type) : 0;
    }

    // --- Audio playback toggle ---
    void setAudioPlaybackDisabled(uint8_t disabled) override {
        if (api_ && api_->setAudioPlaybackDisabled) api_->setAudioPlaybackDisabled(disabled);
    }
    uint8_t getAudioPlaybackDisabled() override {
        return (api_ && api_->getAudioPlaybackDisabled) ? api_->getAudioPlaybackDisabled() : 0;
    }

    // --- Interact API ---
    uint8_t interactNpc(const char* action, int32_t idOrNeg1, const char* name) override {
        return (api_ && api_->interactNpc) ? api_->interactNpc(action, idOrNeg1, name) : 0;
    }
    uint8_t interactNpcByIndex(const char* action, int32_t hashIndex) override {
        return (api_ && api_->interactNpcByIndex) ? api_->interactNpcByIndex(action, hashIndex) : 0;
    }
    uint8_t interactNpcByIndexInWorldView(const char* action,
                                          int32_t hashIndex,
                                          int32_t worldViewId) override {
        if (api_ && api_->interactNpcByIndexInWorldView) {
            return api_->interactNpcByIndexInWorldView(action, hashIndex, worldViewId);
        }
        return interactNpcByIndex(action, hashIndex);
    }
    uint8_t interactObject(const char* action, int32_t idOrNeg1, const char* name) override {
        return (api_ && api_->interactObject) ? api_->interactObject(action, idOrNeg1, name) : 0;
    }
    uint8_t interactTileObject(const char* action,
                               const TitanPluginSdk::TileObjectState* object) override {
        return (api_ && api_->interactTileObject) ? api_->interactTileObject(action, object) : 0;
    }
    uint8_t interactGroundItem(const char* action, int32_t itemId, int32_t tx, int32_t ty) override {
        return (api_ && api_->interactGroundItem) ? api_->interactGroundItem(action, itemId, tx, ty) : 0;
    }
    uint8_t interactGroundItemInWorldView(const char* action,
                                          int32_t itemId,
                                          int32_t tx,
                                          int32_t ty,
                                          int32_t worldViewId) override {
        if (api_ && api_->interactGroundItemInWorldView) {
            return api_->interactGroundItemInWorldView(action, itemId, tx, ty, worldViewId);
        }
        return interactGroundItem(action, itemId, tx, ty);
    }

    // --- Find API ---
    uint8_t findNearestNpc(int32_t idOrNeg1, const char* name,
                           TitanPluginSdk::NpcState* out) override {
        return (api_ && api_->findNearestNpc) ? api_->findNearestNpc(idOrNeg1, name, out) : 0;
    }
    uint8_t findNearestObject(int32_t idOrNeg1, const char* name,
                              TitanPluginSdk::TileObjectState* out) override {
        return (api_ && api_->findNearestObject) ? api_->findNearestObject(idOrNeg1, name, out) : 0;
    }

    // --- Interaction target resolution ---
    uint8_t getInteracting(int32_t idx, uint8_t type,
                           TitanPluginSdk::PlayerState* outP,
                           TitanPluginSdk::NpcState* outN) override {
        return (api_ && api_->getInteracting) ? api_->getInteracting(idx, type, outP, outN) : 0;
    }

    // --- Thread dispatch ---
    // Every not-forwarded path must still fire cleanup exactly once, or the
    // caller's userData leaks one layer up.
    void runOnClientTick(void (*cb)(void*), void* ud, void (*cleanup)(void*)) override {
        if (lifetime_) {
            if (lifetime_->schedule) {
                // schedule owns cleanup on success AND rejection.
                lifetime_->schedule(lifetime_->context, 0, cb, ud, cleanup);
            } else if (cleanup) {
                cleanup(ud);
            }
            return;
        }
        if (api_ && api_->runOnClientTick && cb) {
            api_->runOnClientTick(cb, ud, cleanup);
            return;
        }
        if (cleanup) cleanup(ud);
    }
    void runOnRender(void (*cb)(void*), void* ud, void (*cleanup)(void*)) override {
        if (lifetime_) {
            if (lifetime_->schedule) {
                lifetime_->schedule(lifetime_->context, 1, cb, ud, cleanup);
            } else if (cleanup) {
                cleanup(ud);
            }
            return;
        }
        if (api_ && api_->runOnRender && cb) {
            api_->runOnRender(cb, ud, cleanup);
            return;
        }
        if (cleanup) cleanup(ud);
    }

    // --- Cache definition lookups ---
    uint8_t getItemDef(int32_t id, TitanPluginSdk::ItemDefSnapshot* out) override {
        return (api_ && api_->getItemDef) ? api_->getItemDef(id, out) : 0;
    }
    uint8_t getNpcDef(int32_t id, TitanPluginSdk::NpcDefSnapshot* out) override {
        return (api_ && api_->getNpcDef) ? api_->getNpcDef(id, out) : 0;
    }
    uint8_t getObjDef(int32_t id, TitanPluginSdk::ObjDefSnapshot* out) override {
        return (api_ && api_->getObjDef) ? api_->getObjDef(id, out) : 0;
    }
    uint8_t getVarbitDef(int32_t id, TitanPluginSdk::VarbitDefSnapshot* out) override {
        return (api_ && api_->getVarbitDef) ? api_->getVarbitDef(id, out) : 0;
    }

    // --- Internal developer tools ---
    void setInternalToolVisible(const char* toolId, uint8_t v) override {
        if (api_ && api_->setInternalToolVisible) api_->setInternalToolVisible(toolId, v);
    }
    uint8_t getInternalToolVisible(const char* toolId) override {
        return (api_ && api_->getInternalToolVisible)
            ? api_->getInternalToolVisible(toolId)
            : 0;
    }

    // --- Worlds (SDK 28) ---
    uint8_t getCurrentWorld(int32_t* outWorld) override {
        return (api_ && api_->getCurrentWorld) ? api_->getCurrentWorld(outWorld) : 0;
    }
    uint32_t getWorldList(TitanPluginSdk::WorldState* out, uint32_t cap) override {
        return (api_ && api_->getWorldList) ? api_->getWorldList(out, cap) : 0;
    }
    uint8_t hopToWorldId(int32_t worldId) override {
        return (api_ && api_->hopToWorldId) ? api_->hopToWorldId(worldId) : 0;
    }
    uint8_t hopToListIndex(uint32_t idx) override {
        return (api_ && api_->hopToListIndex) ? api_->hopToListIndex(idx) : 0;
    }
    uint8_t hopToWorldIngame(int32_t worldId) override {
        return (api_ && api_->hopToWorldIngame) ? api_->hopToWorldIngame(worldId) : 0;
    }
    uint32_t getWorldMetadata(TitanPluginSdk::WorldMetadataState* out, uint32_t cap) override {
        return (api_ && api_->getWorldMetadata) ? api_->getWorldMetadata(out, cap) : 0;
    }
    uint8_t refreshWorldMetadata() override {
        return (api_ && api_->refreshWorldMetadata) ? api_->refreshWorldMetadata() : 0;
    }

    // --- Inventory ---
    uint8_t containsInventoryItem(int32_t itemId) override {
        return (api_ && api_->containsInventoryItem) ? api_->containsInventoryItem(itemId) : 0;
    }
    uint8_t interactInventoryItemAtSlot(int32_t slot, int32_t itemId, const char* action) override {
        if (!api_ || !action || !action[0]) return 0;
        // SDK 55+: forward directly to the host, which resolves the slot's
        // widget child for click-bounds — identical to the InternalBackend /
        // JS path (hostInteractInventoryItemAtSlot → Inventory::interact →
        // Widgets::interact).
        if (api_->interactInventoryItemAtSlot) {
            return api_->interactInventoryItemAtSlot(slot, itemId, action);
        }
        // Pre-55 host fallback: by-id interact (no slot awareness).
        return api_->interactInventoryItem ? api_->interactInventoryItem(itemId, action) : 0;
    }
    uint8_t interactInventoryItem(int32_t itemId, const char* action) override {
        return (api_ && api_->interactInventoryItem) ? api_->interactInventoryItem(itemId, action) : 0;
    }
    uint32_t getInventoryItems(TitanPluginSdk::InventoryItemState* out, uint32_t max) override {
        return (api_ && api_->getInventoryItems) ? api_->getInventoryItems(out, max) : 0;
    }

    // --- CS2 script execution ---
    uint8_t runClientScript(int32_t scriptId,
                            const int32_t* args, uint32_t argCount,
                            TitanPluginSdk::Cs2ScriptResult* out) override {
        return (api_ && api_->runClientScript) ? api_->runClientScript(scriptId, args, argCount, out) : 0;
    }
    int32_t getQuestState(int32_t questId) override {
        return (api_ && api_->getQuestState) ? api_->getQuestState(questId) : -1;
    }

    // --- Local player ---
    uint8_t getLocalPlayer(TitanPluginSdk::PlayerState* out) override {
        return (api_ && api_->getLocalPlayer) ? api_->getLocalPlayer(out) : 0;
    }

    // --- Idle timer ---
    int32_t getIdleTimeRemaining() override {
        return (api_ && api_->getIdleTimeRemaining) ? api_->getIdleTimeRemaining() : -1;
    }
    void resetIdleTimer() override {
        if (api_ && api_->resetIdleTimer) api_->resetIdleTimer();
    }

    // --- Widget queries ---
    uint8_t getWidget(uint32_t packedId, TitanPluginSdk::WidgetState* out) override {
        return (api_ && api_->getWidget) ? api_->getWidget(packedId, out) : 0;
    }

    // --- Plugin manager ---
    uint32_t getPluginCount() override {
        return (api_ && api_->getPluginCount) ? api_->getPluginCount() : 0;
    }
    uint32_t listPlugins(TitanPluginSdk::PluginInfo* out, uint32_t max) override {
        return (api_ && api_->listPlugins) ? api_->listPlugins(out, max) : 0;
    }
    uint8_t getPlugin(const char* id, TitanPluginSdk::PluginInfo* out) override {
        return (api_ && api_->getPlugin) ? api_->getPlugin(id, out) : 0;
    }
    uint8_t setPluginEnabled(const char* id, uint8_t enabled) override {
        return (api_ && api_->setPluginEnabled) ? api_->setPluginEnabled(id, enabled) : 0;
    }
    void markSettingChanged(const char* pluginId, const char* settingKey,
                            const TitanNativeRecords::Value* value,
                            const uint8_t* hidden) override {
        if (api_ && api_->sdkVersion >= 140 && api_->markSettingChanged) {
            api_->markSettingChanged(pluginId, settingKey, value, hidden);
        }
    }
    int8_t isPluginEnabled(const char* id) override {
        return (api_ && api_->isPluginEnabled) ? api_->isPluginEnabled(id) : -1;
    }

    // --- Login / account switch ---
    uint8_t getLoginAccountState(TitanPluginSdk::LoginAccountState* out) override {
        return (api_ && api_->getLoginAccountState) ? api_->getLoginAccountState(out) : 0;
    }
    void setLoginUsername(const char* u) override {
        if (api_ && api_->setLoginUsername) api_->setLoginUsername(u);
    }
    void setLoginPassword(const char* p) override {
        if (api_ && api_->setLoginPassword) api_->setLoginPassword(p);
    }
    void setLoginAuthenticator(const char* c) override {
        if (api_ && api_->setLoginAuthenticator) api_->setLoginAuthenticator(c);
    }
    void setLoginIndex(int32_t i) override {
        if (api_ && api_->setLoginIndex) api_->setLoginIndex(i);
    }
    void setLoginDisplayName(const char* d) override {
        if (api_ && api_->setLoginDisplayName) api_->setLoginDisplayName(d);
    }
    void setLoginOAuth2Credentials(const char* a, const char* r) override {
        if (api_ && api_->setLoginOAuth2Credentials) api_->setLoginOAuth2Credentials(a, r);
    }
    void setLoginGameSessionCredentials(const char* s, const char* c) override {
        if (api_ && api_->setLoginGameSessionCredentials) api_->setLoginGameSessionCredentials(s, c);
    }
    void setLoginCharacter(const char* d, const char* c, const char* s) override {
        if (api_ && api_->setLoginCharacter) api_->setLoginCharacter(d, c, s);
    }
    void resetLoginCharacter() override {
        if (api_ && api_->resetLoginCharacter) api_->resetLoginCharacter();
    }
    uint8_t submitLoginLauncherCredentials() override {
        return (api_ && api_->submitLoginLauncherCredentials)
            ? api_->submitLoginLauncherCredentials() : 0;
    }
    uint8_t submitLoginStandardCredentials() override {
        return (api_ && api_->submitLoginStandardCredentials)
            ? api_->submitLoginStandardCredentials() : 0;
    }
    uint8_t acknowledgeStandardLogin() override {
        return (api_ && api_->acknowledgeStandardLogin)
            ? api_->acknowledgeStandardLogin() : 0;
    }
    uint8_t advanceLoginClickToPlay() override {
        return (api_ && api_->advanceLoginClickToPlay)
            ? api_->advanceLoginClickToPlay() : 0;
    }
    uint8_t getLoginFlowState(TitanPluginSdk::LoginFlowState* out) override {
        return (api_ && api_->getLoginFlowState) ? api_->getLoginFlowState(out) : 0;
    }
    uint8_t advanceLoginLauncherCredentials() override {
        return (api_ && api_->advanceLoginLauncherCredentials)
            ? api_->advanceLoginLauncherCredentials() : 0;
    }
    uint8_t advanceLoginLogout() override {
        return (api_ && api_->advanceLoginLogout) ? api_->advanceLoginLogout() : 0;
    }
    void cancelLoginProfileOperations() override {
        if (api_ && api_->cancelLoginProfileOperations)
            api_->cancelLoginProfileOperations();
    }
    void cancelLoginLogoutOperation() override {
        if (api_ && api_->cancelLoginLogoutOperation)
            api_->cancelLoginLogoutOperation();
    }
    uint8_t stageLoginCredentials(const char* profileLabel) override {
        return (api_ && api_->stageLoginCredentials)
            ? api_->stageLoginCredentials(profileLabel) : 0;
    }
    uint8_t submitLoginCredentials() override {
        return (api_ && api_->submitLoginCredentials)
            ? api_->submitLoginCredentials() : 0;
    }
    uint32_t listSanitizedProxyRoutes(
            TitanPluginSdk::SanitizedProxyRouteState* out,
            uint32_t capacity) override {
        return (api_ && api_->listSanitizedProxyRoutes)
            ? api_->listSanitizedProxyRoutes(out, capacity) : 0;
    }
    uint8_t setProxyRoute(
            const char* proxyId,
            TitanPluginSdk::ProxyRouteStatusState* outStatus) override {
        return (api_ && api_->setProxyRoute)
            ? api_->setProxyRoute(proxyId ? proxyId : "", outStatus) : 0;
    }
    uint8_t getProxyRouteStatus(
            TitanPluginSdk::ProxyRouteStatusState* outStatus) override {
        return (api_ && api_->getProxyRouteStatus)
            ? api_->getProxyRouteStatus(outStatus) : 0;
    }

    // --- Chat injection ---
    void addChatMessage(int32_t type, const char* name,
                        const char* message, const char* sender) override {
        if (api_ && api_->addChatMessage) api_->addChatMessage(type, name, message, sender);
    }

    // --- Item containers + runtime ItemDef (SDK 26) ---
    uint8_t getItemContainer(int32_t id,
                             TitanPluginSdk::ItemContainerState* out) override {
        if (!api_ || !api_->getItemContainer) return 0;
        return api_->getItemContainer(id, out);
    }
    uint8_t getItemComposition(int32_t id,
                               TitanPluginSdk::ItemCompositionState* out) override {
        if (!api_ || !api_->getItemComposition) return 0;
        return api_->getItemComposition(id, out);
    }

    // --- Use-on item API (SDK 32) ---
    uint8_t useInventoryItemOnItem(int32_t srcSlot, int32_t srcItemId,
                                   int32_t tgtSlot, int32_t tgtItemId) override {
        return (api_ && api_->useInventoryItemOnItem)
            ? api_->useInventoryItemOnItem(srcSlot, srcItemId, tgtSlot, tgtItemId) : 0;
    }
    uint8_t useInventoryItemOnNpc(int32_t srcSlot, int32_t srcItemId,
                                  int32_t npcHashIndex) override {
        return (api_ && api_->useInventoryItemOnNpc)
            ? api_->useInventoryItemOnNpc(srcSlot, srcItemId, npcHashIndex) : 0;
    }
    uint8_t useInventoryItemOnObject(int32_t srcSlot, int32_t srcItemId,
                                     int32_t locId, int32_t tileX,
                                     int32_t tileY) override {
        return (api_ && api_->useInventoryItemOnObject)
            ? api_->useInventoryItemOnObject(srcSlot, srcItemId, locId, tileX, tileY) : 0;
    }

    // --- Entity clickbox + hull overlays (SDK 34 typecode-keyed) ---
    void drawEntityClickbox(uint64_t entityPtr, uint64_t typecode,
                            uint32_t outline, uint32_t fill) override {
        if (api_ && api_->drawEntityClickbox)
            api_->drawEntityClickbox(entityPtr, typecode, outline, fill);
    }
    void drawTileObjectClickbox(uint64_t locPtr, uint64_t typecode,
                                uint32_t outline, uint32_t fill) override {
        if (api_ && api_->drawTileObjectClickbox)
            api_->drawTileObjectClickbox(locPtr, typecode, outline, fill);
    }
    void drawEntityHull(uint64_t entityPtr, uint64_t typecode,
                        uint32_t outline, uint32_t fill) override {
        if (api_ && api_->drawEntityHull)
            api_->drawEntityHull(entityPtr, typecode, outline, fill);
    }
    void drawTileObjectHull(uint64_t locPtr, uint64_t typecode,
                            uint32_t outline, uint32_t fill) override {
        if (api_ && api_->drawTileObjectHull)
            api_->drawTileObjectHull(locPtr, typecode, outline, fill);
    }
    void drawEntityOutline(uint64_t entityPtr, uint64_t typecode,
                           uint32_t outline, uint32_t fill,
                           uint32_t mode) override {
        if (api_ && api_->drawEntityOutline)
            api_->drawEntityOutline(entityPtr, typecode, outline, fill, mode);
    }
    void drawTileObjectOutline(uint64_t locPtr, uint64_t typecode,
                               uint32_t outline, uint32_t fill,
                               uint32_t mode) override {
        if (api_ && api_->drawTileObjectOutline)
            api_->drawTileObjectOutline(locPtr, typecode, outline, fill, mode);
    }

    // --- Generic widget interaction (SDK 36) ---
    uint8_t widgetInteract(uint32_t opcode, int32_t identifier,
                           int32_t param0, int32_t param1) override {
        return (api_ && api_->widgetInteract)
            ? api_->widgetInteract(opcode, identifier, param0, param1) : 0;
    }

    // --- Widget child enumeration (SDK 38) ---
    uint32_t getWidgetChildren(uint32_t parentPackedId,
                               TitanPluginSdk::WidgetState* outStates,
                               uint32_t maxOut) override {
        return (api_ && api_->getWidgetChildren)
            ? api_->getWidgetChildren(parentPackedId, outStates, maxOut) : 0;
    }

    uint8_t getWidgetByText(const char* query,
                            TitanPluginSdk::WidgetState* outState) override {
        return (api_ && api_->getWidgetByText)
            ? api_->getWidgetByText(query, outState) : 0;
    }

    // --- Keyboard (SDK 44) ---
    uint8_t sendKeyboardString(const char* utf8) override {
        return (api_ && api_->sendKeyboardString)
            ? api_->sendKeyboardString(utf8) : 0;
    }
    uint8_t sendKeyboardKey(int32_t key, uint32_t modMask) override {
        return (api_ && api_->sendKeyboardKey)
            ? api_->sendKeyboardKey(key, modMask) : 0;
    }
    uint8_t typeKeyboardString(const char* utf8, int32_t minDelayMs, int32_t maxDelayMs) override {
        return (api_ && api_->typeKeyboardString)
            ? api_->typeKeyboardString(utf8, minDelayMs, maxDelayMs, 0, nullptr, nullptr) : 0;
    }
    void cancelKeyboardType() override {
        if (api_ && api_->cancelKeyboardType) api_->cancelKeyboardType();
    }
    uint8_t isKeyboardTyping() override {
        return (api_ && api_->isKeyboardTyping)
            ? api_->isKeyboardTyping() : 0;
    }

    // --- OverlayPanels (SDK 46) ---
    int32_t overlayPanelRegister(const char* pluginId, const char* panelName,
                                 uint8_t defaultAnchor,
                                 int32_t defaultPriority) override {
        return (api_ && api_->overlayPanelRegister)
            ? api_->overlayPanelRegister(pluginId, panelName,
                                         defaultAnchor, defaultPriority)
            : -1;
    }
    void overlayPanelUnregister(int32_t handle) override {
        if (api_ && api_->overlayPanelUnregister) api_->overlayPanelUnregister(handle);
    }
    void overlayPanelBegin(int32_t handle, int32_t preferredWidth) override {
        if (api_ && api_->overlayPanelBegin) api_->overlayPanelBegin(handle, preferredWidth);
    }
    void overlayPanelEnd(int32_t handle) override {
        if (api_ && api_->overlayPanelEnd) api_->overlayPanelEnd(handle);
    }
    void overlayPanelSetStyle(int32_t handle,
                              const TitanPluginSdk::OverlayPanelStyleAbi* style) override {
        if (api_ && api_->overlayPanelSetStyle) api_->overlayPanelSetStyle(handle, style);
    }
    void overlayPanelTitle(int32_t handle, const char* text,
                           uint32_t color) override {
        if (api_ && api_->overlayPanelTitle) api_->overlayPanelTitle(handle, text, color);
    }
    void overlayPanelLine(int32_t handle, const char* left, const char* right,
                          uint32_t leftColor, uint32_t rightColor) override {
        if (api_ && api_->overlayPanelLine)
            api_->overlayPanelLine(handle, left, right, leftColor, rightColor);
    }
    void overlayPanelProgressBar(int32_t handle, int32_t value, int32_t minVal,
                                 int32_t maxVal, uint32_t fillColor,
                                 uint32_t bgColor) override {
        if (api_ && api_->overlayPanelProgressBar)
            api_->overlayPanelProgressBar(handle, value, minVal, maxVal, fillColor, bgColor);
    }

    // --- Widget text setter (SDK 51) ---
    uint8_t setWidgetText(uint32_t packedId, const char* text) override {
        return (api_ && api_->setWidgetText)
            ? api_->setWidgetText(packedId, text) : 0;
    }

    // --- VarClient values (SDK 62) ---
    uint8_t getVarClientInt(int32_t id, int32_t* outValue) override {
        return (api_ && api_->getVarClientInt)
            ? api_->getVarClientInt(id, outValue) : 0;
    }
    uint8_t setVarClientInt(int32_t id, int32_t value) override {
        return (api_ && api_->setVarClientInt)
            ? api_->setVarClientInt(id, value) : 0;
    }
    uint32_t getVarClientString(int32_t id, char* out,
                                uint32_t capacity) override {
        return (api_ && api_->getVarClientString)
            ? api_->getVarClientString(id, out, capacity) : 0;
    }
    uint8_t setVarClientString(int32_t id, const char* value) override {
        return (api_ && api_->setVarClientString)
            ? api_->setVarClientString(id, value) : 0;
    }
    uint8_t getVarClientLong(int32_t id, int64_t* outValue) override {
        return (api_ && api_->getVarClientLong)
            ? api_->getVarClientLong(id, outValue) : 0;
    }
    uint8_t setVarClientLong(int32_t id, int64_t value) override {
        return (api_ && api_->setVarClientLong)
            ? api_->setVarClientLong(id, value) : 0;
    }

    // --- Slot-addressed dynamic widget text writes (SDK 63) ---
    uint8_t setWidgetTextAtSlot(uint32_t parentPackedId, int32_t slot,
                                const char* text) override {
        return (api_ && api_->setWidgetTextAtSlot)
            ? api_->setWidgetTextAtSlot(parentPackedId, slot, text) : 0;
    }

    // --- Slot-aware recursive widget queries (SDK 64) ---
    uint32_t getWidgets(uint32_t groupId,
                        TitanPluginSdk::WidgetQueryState* outStates,
                        uint32_t maxOut, uint8_t* outTruncated) override {
        return (api_ && api_->getWidgets)
            ? api_->getWidgets(groupId, outStates, maxOut, outTruncated) : 0;
    }
    uint32_t getWidgetChildrenAtPath(
            const TitanPluginSdk::WidgetAddressState* parent,
            TitanPluginSdk::WidgetQueryState* outStates,
            uint32_t maxOut) override {
        return (api_ && api_->getWidgetChildrenAtPath)
            ? api_->getWidgetChildrenAtPath(parent, outStates, maxOut) : 0;
    }
    uint8_t setWidgetTextAtPath(
            const TitanPluginSdk::WidgetAddressState* address,
            const char* text) override {
        return (api_ && api_->setWidgetTextAtPath)
            ? api_->setWidgetTextAtPath(address, text) : 0;
    }
    uint8_t widgetInteractAtPath(
            const TitanPluginSdk::WidgetAddressState* address,
            uint32_t opcode, int32_t identifier, int32_t childSlot) override {
        return (api_ && api_->widgetInteractAtPath)
            ? api_->widgetInteractAtPath(address, opcode, identifier, childSlot) : 0;
    }

    // --- Direct identity resolvers for live handles (SDK 89) ---
    uint8_t getPlayerByIndexInWorldView(int32_t hashIndex,
                                        int32_t worldViewId,
                                        TitanPluginSdk::PlayerState* outPlayer) override {
        return (api_ && api_->getPlayerByIndexInWorldView)
            ? api_->getPlayerByIndexInWorldView(hashIndex, worldViewId, outPlayer) : 0;
    }
    uint8_t getNpcByIndexInWorldView(int32_t hashIndex,
                                     int32_t worldViewId,
                                     TitanPluginSdk::NpcState* outNpc) override {
        return (api_ && api_->getNpcByIndexInWorldView)
            ? api_->getNpcByIndexInWorldView(hashIndex, worldViewId, outNpc) : 0;
    }
    uint8_t getWidgetAtPath(
            const TitanPluginSdk::WidgetAddressState* address,
            TitanPluginSdk::WidgetState* outState) override {
        return (api_ && api_->getWidgetAtPath)
            ? api_->getWidgetAtPath(address, outState) : 0;
    }
    uint32_t getActorPathQueueInWorldView(
            uint64_t entityPtr, int32_t worldViewId,
            TitanPluginSdk::WorldPointState* out, uint32_t max) override {
        if (api_ && api_->getActorPathQueueInWorldView) {
            return api_->getActorPathQueueInWorldView(entityPtr, worldViewId, out, max);
        }
        return (api_ && api_->getActorPathQueue)
            ? api_->getActorPathQueue(entityPtr, out, max) : 0;
    }

    // --- Cross-plugin service registry (SDK 66) ---
    void registerPluginService(const char* serviceId, void* service) override {
        // A native owner must use the instance-bound publication path.
        if (lifetime_) return;
        if (api_ && api_->registerPluginService && serviceId) {
            api_->registerPluginService(serviceId, service);
        }
    }
    uint8_t registerPluginServiceOwned(const void* pluginInstance,
                                       const char* pluginId,
                                       const char* serviceId,
                                       void* service) override {
        if (lifetime_) {
            return lifetime_->publishService && pluginInstance && pluginId && serviceId
                ? lifetime_->publishService(lifetime_->context, pluginInstance,
                                              pluginId, serviceId, service) : 0;
        }
        if (api_ && api_->sdkVersion >= 97 &&
            api_->registerPluginServiceOwned && pluginInstance &&
            pluginId && serviceId) {
            return api_->registerPluginServiceOwned(
                pluginInstance, pluginId, serviceId, service);
        }
        // Compatibility with a pre-97 host: publish through the legacy
        // unowned registry. Such a host cannot provide generation cleanup.
        if (api_ && api_->registerPluginService && serviceId) {
            api_->registerPluginService(serviceId, service);
            return 1;
        }
        return 0;
    }
    void* getPluginService(const char* serviceId) override {
        // Native callers can cache only whitelisted host-resident tables.
        // Plugin-owned services always require acquirePluginService + a lease.
        if (lifetime_) {
            return api_ && api_->getHostService && serviceId
                ? api_->getHostService(serviceId) : nullptr;
        }
        return (api_ && api_->getPluginService && serviceId)
            ? api_->getPluginService(serviceId) : nullptr;
    }

    uint8_t acquirePluginService(const char* serviceId,
                                 void** outService, void** outLease) override {
        if (outService) *outService = nullptr;
        if (outLease) *outLease = nullptr;
        return lifetime_ && lifetime_->acquireService && lifetime_->releaseLease
                && serviceId && outService && outLease
            ? lifetime_->acquireService(lifetime_->context, serviceId,
                                         outService, outLease) : 0;
    }

    void releasePluginLease(void* lease) override {
        if (lifetime_ && lifetime_->releaseLease && lease) {
            lifetime_->releaseLease(lifetime_->context, lease);
        }
    }

    uint8_t acquirePluginWork(void** outLease) override {
        if (outLease) *outLease = nullptr;
        return lifetime_ && lifetime_->acquireWork && lifetime_->releaseLease && outLease
            ? lifetime_->acquireWork(lifetime_->context, outLease) : 0;
    }

    // --- Break Handler registry (SDK 97) ---
    uint8_t breakHandlerRegisterPlugin(
            const void* pluginInstance,
            const TitanPluginSdk::BreakRegistrationState* registration) override {
        return (api_ && api_->breakHandlerRegisterPlugin)
            ? api_->breakHandlerRegisterPlugin(pluginInstance, registration)
            : 0;
    }
    uint8_t breakHandlerStart(const void* pluginInstance,
                              const char* pluginId) override {
        return (api_ && api_->breakHandlerStart)
            ? api_->breakHandlerStart(pluginInstance, pluginId) : 0;
    }
    uint8_t breakHandlerStop(const void* pluginInstance,
                             const char* pluginId) override {
        return (api_ && api_->breakHandlerStop)
            ? api_->breakHandlerStop(pluginInstance, pluginId) : 0;
    }
    uint8_t breakHandlerUnregisterPlugin(const void* pluginInstance,
                                         const char* pluginId) override {
        return (api_ && api_->breakHandlerUnregisterPlugin)
            ? api_->breakHandlerUnregisterPlugin(pluginInstance, pluginId) : 0;
    }
    uint8_t breakHandlerPoll(
            const void* pluginInstance, const char* pluginId,
            TitanPluginSdk::BreakCommandState* outCommand) override {
        return (api_ && api_->breakHandlerPoll)
            ? api_->breakHandlerPoll(pluginInstance, pluginId, outCommand) : 0;
    }
    uint8_t breakHandlerReport(
            const void* pluginInstance,
            const TitanPluginSdk::BreakReportState* report) override {
        return (api_ && api_->breakHandlerReport)
            ? api_->breakHandlerReport(pluginInstance, report)
            : 0;
    }
    uint32_t breakHandlerCoordinatorSnapshot(
            const void* coordinatorInstance, const char* coordinatorId,
            TitanPluginSdk::BreakParticipantState* out,
            uint32_t capacity) override {
        return (api_ && api_->breakHandlerCoordinatorSnapshot)
            ? api_->breakHandlerCoordinatorSnapshot(
                coordinatorInstance, coordinatorId, out, capacity)
            : 0;
    }
    uint8_t breakHandlerCoordinatorPublish(
            const void* coordinatorInstance, const char* coordinatorId,
            const TitanPluginSdk::BreakCommandState* command) override {
        return (api_ && api_->breakHandlerCoordinatorPublish)
            ? api_->breakHandlerCoordinatorPublish(
                coordinatorInstance, coordinatorId, command)
            : 0;
    }
    uint8_t breakHandlerCoordinatorClear(
            const void* coordinatorInstance, const char* coordinatorId,
            uint64_t expectedEpoch) override {
        return (api_ && api_->breakHandlerCoordinatorClear)
            ? api_->breakHandlerCoordinatorClear(
                coordinatorInstance, coordinatorId, expectedEpoch)
            : 0;
    }

    bool supportsCurrentSceneTileObjects() const override {
        return api_ && api_->getCurrentSceneTileObjects;
    }
    uint32_t getCurrentSceneTileObjects(
            TitanPluginSdk::TileObjectState* out, uint32_t capacity) override {
        return api_ && api_->getCurrentSceneTileObjects
            ? api_->getCurrentSceneTileObjects(out, capacity) : UINT32_MAX;
    }
    uint8_t getCollisionSourceReady(uint8_t* outReady) override {
        if (outReady) *outReady = 0;
        return api_ && api_->getCollisionSourceReady
            ? api_->getCollisionSourceReady(outReady) : 0;
    }
    uint8_t copyCachedCollisionRegion(
            uint32_t regionId, int32_t* outFlags, uint32_t capacity,
            uint32_t* outCount) override {
        if (outCount) *outCount = 0;
        return (api_ && api_->sdkVersion >= 112 && api_->copyCachedCollisionRegion)
            ? api_->copyCachedCollisionRegion(regionId, outFlags, capacity, outCount)
            : TitanPluginSdk::CollisionSnapshotStatus::Unavailable;
    }
    uint8_t copyCurrentCollisionScene(
            TitanPluginSdk::CollisionSceneSnapshotState* outScene,
            int32_t* outFlags, uint32_t capacity,
            uint32_t* outCount) override {
        if (outCount) *outCount = 0;
        return (api_ && api_->sdkVersion >= 112 && api_->copyCurrentCollisionScene)
            ? api_->copyCurrentCollisionScene(outScene, outFlags, capacity, outCount)
            : TitanPluginSdk::CollisionSnapshotStatus::Unavailable;
    }
    uint8_t webPathSubmit(
            const TitanPluginSdk::WebPathRequestState* request,
            const TitanPluginSdk::WorldPointState* forbiddenTiles,
            uint32_t forbiddenTileCount, uint64_t* outRequestId) override {
        if (outRequestId) *outRequestId = 0;
        return (api_ && api_->sdkVersion >= 112 && api_->webPathSubmit)
            ? api_->webPathSubmit(
                request, forbiddenTiles, forbiddenTileCount, outRequestId)
            : 0;
    }
    uint8_t webPathPoll(
            uint64_t requestId,
            TitanPluginSdk::WebPathSummaryState* outSummary) override {
        return (api_ && api_->sdkVersion >= 112 && api_->webPathPoll)
            ? api_->webPathPoll(requestId, outSummary) : 0;
    }
    uint8_t webPathCopySteps(
            uint64_t requestId, TitanPluginSdk::WebPathStepState* outSteps,
            uint32_t capacity, uint32_t* outCount) override {
        if (outCount) *outCount = 0;
        return (api_ && api_->sdkVersion >= 112 && api_->webPathCopySteps)
            ? api_->webPathCopySteps(requestId, outSteps, capacity, outCount)
            : 0;
    }
    uint8_t webPathCancel(uint64_t requestId) override {
        return (api_ && api_->sdkVersion >= 112 && api_->webPathCancel)
            ? api_->webPathCancel(requestId) : 0;
    }
    uint8_t webPathRelease(uint64_t requestId) override {
        return (api_ && api_->sdkVersion >= 112 && api_->webPathRelease)
            ? api_->webPathRelease(requestId) : 0;
    }

    uint8_t webWalkStart(
            const TitanPluginSdk::WebWalkRequestState* request,
            const TitanPluginSdk::WorldPointState* forbiddenTiles,
            uint32_t forbiddenTileCount, uint64_t* outWalkId) override {
        if (outWalkId) *outWalkId = 0;
        return (api_ && api_->sdkVersion >= 114 && api_->webWalkStart)
            ? api_->webWalkStart(request, forbiddenTiles, forbiddenTileCount,
                                 outWalkId)
            : 0;
    }
    uint8_t webWalkStatus(
            uint64_t walkId,
            TitanPluginSdk::WebWalkStatusState* outStatus) override {
        return (api_ && api_->sdkVersion >= 114 && api_->webWalkStatus)
            ? api_->webWalkStatus(walkId, outStatus) : 0;
    }
    uint8_t webWalkCancel(uint64_t walkId) override {
        return (api_ && api_->sdkVersion >= 114 && api_->webWalkCancel)
            ? api_->webWalkCancel(walkId) : 0;
    }
    uint8_t webWalkRelease(uint64_t walkId) override {
        return (api_ && api_->sdkVersion >= 114 && api_->webWalkRelease)
            ? api_->webWalkRelease(walkId) : 0;
    }
    uint8_t webWalkAdvance(uint64_t walkId) override {
        return (api_ && api_->sdkVersion >= 114 && api_->webWalkAdvance)
            ? api_->webWalkAdvance(walkId) : 0;
    }
    uint8_t webPathCopyStepPayload(
            uint64_t requestId, uint32_t stepIndex, char* outUtf8,
            uint32_t capacity, uint32_t* outRequired) override {
        if (outRequired) *outRequired = 0;
        return (api_ && api_->sdkVersion >= 114
                && api_->webPathCopyStepPayload)
            ? api_->webPathCopyStepPayload(requestId, stepIndex, outUtf8,
                                           capacity, outRequired)
            : 0;
    }

    uint8_t getWorldMapState(
            TitanPluginSdk::WorldMapState* outState) override {
        if (!outState) return 0;
        if (api_ && api_->sdkVersion >= 113 && api_->getWorldMapState) {
            return api_->getWorldMapState(outState);
        }
        *outState = TitanPluginSdk::WorldMapState{};
        return 0;
    }

    uint8_t getActorOverheadText(uint64_t ptr, char* out, uint64_t cap, uint64_t* len) override {
        if (len) *len = 0;
        return api_ && api_->sdkVersion >= 127 && api_->getActorOverheadText
            ? api_->getActorOverheadText(ptr, out, cap, len) : 0;
    }
    uint8_t getActorOverheadTextCyclesRemaining(uint64_t ptr, int32_t* out) override {
        return api_ && api_->sdkVersion >= 127 && api_->getActorOverheadTextCyclesRemaining
            ? api_->getActorOverheadTextCyclesRemaining(ptr, out) : 0;
    }
    uint32_t getOverheadTextCapabilities() override {
        return api_ && api_->sdkVersion >= 127 && api_->getOverheadTextCapabilities
            ? api_->getOverheadTextCapabilities() : 0;
    }
    int32_t getGameCycle() override {
        return (api_ && api_->sdkVersion >= 125 && api_->getGameCycle)
            ? api_->getGameCycle() : 0;
    }

    // --- Full-frame game screenshot (SDK 131) ---
    uint8_t screenshotSubmit(uint64_t* outRequestId) override {
        if (outRequestId) *outRequestId = 0;
        return (api_ && api_->sdkVersion >= 131 && api_->screenshotSubmit)
            ? api_->screenshotSubmit(outRequestId) : 0;
    }
    uint8_t screenshotPoll(uint64_t requestId,
                           TitanPluginSdk::ScreenshotStatusState* outStatus) override {
        return (api_ && api_->sdkVersion >= 131 && api_->screenshotPoll)
            ? api_->screenshotPoll(requestId, outStatus) : 0;
    }
    uint8_t screenshotCopyPng(uint64_t requestId, uint8_t* out,
                              uint32_t capacity, uint32_t* outRequired) override {
        if (outRequired) *outRequired = 0;
        return (api_ && api_->sdkVersion >= 131 && api_->screenshotCopyPng)
            ? api_->screenshotCopyPng(requestId, out, capacity, outRequired) : 0;
    }
    uint8_t screenshotRelease(uint64_t requestId) override {
        return (api_ && api_->sdkVersion >= 131 && api_->screenshotRelease)
            ? api_->screenshotRelease(requestId) : 0;
    }

    bool getGrandExchangeOffer(int32_t slot, TitanPluginSdk::GrandExchangeOffer* out) override {
        return api_ && api_->sdkVersion >= 136 && api_->getGrandExchangeOffer
            && api_->getGrandExchangeOffer(slot, out);
    }
    int32_t getGrandExchangeOffers(TitanPluginSdk::GrandExchangeOffer* out, int32_t capacity) override {
        return api_ && api_->sdkVersion >= 136 && api_->getGrandExchangeOffers
            ? api_->getGrandExchangeOffers(out, capacity) : 0;
    }
    bool isGrandExchangeAvailable() override {
        return api_ && api_->sdkVersion >= 136 && api_->isGrandExchangeAvailable
            && api_->isGrandExchangeAvailable();
    }

    bool requestItemPriceCatalog() override {
        return api_ && api_->sdkVersion >= 137 && api_->requestItemPriceCatalog && api_->requestItemPriceCatalog();
    }
    bool requestItemPrice(int32_t id) override {
        return api_ && api_->sdkVersion >= 137 && api_->requestItemPrice && api_->requestItemPrice(id);
    }
    bool getItemPriceStatus(TitanPluginSdk::ItemPriceStatus* out) override {
        return api_ && api_->sdkVersion >= 137 && api_->getItemPriceStatus && api_->getItemPriceStatus(out);
    }
    bool getItemPriceMetadata(int32_t id, TitanPluginSdk::ItemPriceMetadata* out) override {
        return api_ && api_->sdkVersion >= 137 && api_->getItemPriceMetadata && api_->getItemPriceMetadata(id, out);
    }
    int32_t getItemPriceItemIds(int32_t* out, int32_t capacity) override {
        return api_ && api_->sdkVersion >= 137 && api_->getItemPriceItemIds ? api_->getItemPriceItemIds(out, capacity) : 0;
    }
    uint64_t geSubmitBuy(const TitanPluginSdk::GeBuyOptions* options) override {
        return api_ && api_->sdkVersion >= 139 && api_->geSubmitBuy ? api_->geSubmitBuy(nullptr, options) : 0;
    }
    bool geGetRequest(uint64_t id, TitanPluginSdk::GeRequestState* out) override {
        return api_ && api_->sdkVersion >= 139 && api_->geGetRequest && api_->geGetRequest(nullptr, id, out);
    }
    int32_t geGetRequests(TitanPluginSdk::GeRequestState* out, int32_t capacity) override {
        return api_ && api_->sdkVersion >= 139 && api_->geGetRequests ? api_->geGetRequests(nullptr, out, capacity) : 0;
    }
    bool geCancelRequest(uint64_t id) override {
        return api_ && api_->sdkVersion >= 139 && api_->geCancelRequest && api_->geCancelRequest(nullptr, id);
    }
    bool geReleaseRequest(uint64_t id) override {
        return api_ && api_->sdkVersion >= 139 && api_->geReleaseRequest && api_->geReleaseRequest(nullptr, id);
    }
    bool getItemCacheBank(TitanPluginSdk::BankCacheState* out) override {
        return api_ && api_->sdkVersion >= 138 && api_->getItemCacheBank && api_->getItemCacheBank(out);
    }
    bool getItemPrice(int32_t id, TitanPluginSdk::ItemPrice* out) override {
        return api_ && api_->sdkVersion >= 137 && api_->getItemPrice && api_->getItemPrice(id, out);
    }

    // --- Cross-Tab Store (SDK 141) ---
    uint8_t crossTabWrite(const void* pluginInstance, const char* pluginId,
                          const TitanPluginSdk::CrossTabWrite* write,
                          uint64_t* outWriteId) override {
        if (outWriteId) *outWriteId = 0;
        return (api_ && api_->sdkVersion >= 141 && api_->crossTabWrite)
            ? api_->crossTabWrite(pluginInstance, pluginId, write, outWriteId) : 0;
    }
    uint32_t crossTabRead(const void* pluginInstance, const char* pluginId,
                          const char* key, uint8_t* out, uint32_t capacity,
                          TitanPluginSdk::CrossTabEntryInfo* info) override {
        return (api_ && api_->sdkVersion >= 141 && api_->crossTabRead)
            ? api_->crossTabRead(pluginInstance, pluginId, key, out, capacity, info)
            : TitanPluginSdk::kCrossTabAbsent;
    }

    // --- Preview pills (SDK 142) ---
    uint8_t previewPillWrite(const void* pluginInstance, const char* pluginId,
                             const TitanPluginSdk::PreviewPillWrite* write) override {
        return (api_ && api_->sdkVersion >= 142 && api_->previewPillWrite)
            ? api_->previewPillWrite(pluginInstance, pluginId, write) : 0;
    }

    // --- Break Handler observation (SDK 144) ---
    uint8_t breakHandlerObserve(
            const void* pluginInstance, const char* pluginId,
            TitanPluginSdk::BreakCommandState* outCommand) override {
        return (api_ && api_->sdkVersion >= 144 && api_->breakHandlerObserve)
            ? api_->breakHandlerObserve(pluginInstance, pluginId, outCommand)
            : 0;
    }

private:
    static constexpr uint32_t kInventoryPackedId = (149u << 16) | 0u;

    static bool containsCI(const char* haystack, const char* needle) {
        if (!haystack || !needle || !needle[0]) return false;
        const auto lower = [](char c) {
            return (c >= 'A' && c <= 'Z') ? static_cast<char>(c + 32) : c;
        };
        for (const char* h = haystack; *h; ++h) {
            const char* hi = h;
            const char* ni = needle;
            while (*hi && *ni && lower(*hi) == lower(*ni)) { ++hi; ++ni; }
            if (!*ni) return true;
        }
        return false;
    }

    static int32_t actionIndexToIdentifier(int index) {
        return (index <= 2) ? (index + 2) : (index + 3);
    }

    static uint32_t actionIndexToOpcode(int index) {
        return (index <= 2) ? 57u : 1007u;
    }

    const TitanPluginSdk::HostApi* api_;
    const TitanPluginSdk::NativeAbi::LifetimeV1* lifetime_ = nullptr;
};

}  // namespace detail
}  // namespace titan
