/// @file titan/detail/backend.h
/// @brief Abstract backend interface dispatched to by the fluent SDK facades.
///
/// The public `titan::` fluent facades in `shared/titan/*.h` do not call the
/// raw `HostApi` function pointers directly. They go through this `IBackend`
/// interface, which one of two implementations satisfies:
///
///  - `detail::ExternalBackend` (defined in `backend_external.h`) forwards
///    every method to the `HostApi` function-pointer table. Plugin DLLs use
///    this -- the host fills in the fn pointers at plugin load, and the
///    backend wraps them with null-guards so facades can stay clean.
///
///  - `client::titan_backend::InternalBackend` (lives in `client.dll`) calls
///    straight into the client's `game/*`, `core/*`, `actions/*` layers with
///    zero HostApi indirection. `client.dll` installs this at init so any
///    `titan::` call from client code is as fast as the legacy direct reads.
///
/// Each DLL has its own static `backendRef()` and installs its own backend.
/// The vtable never crosses DLL boundaries -- the only cross-DLL contract is
/// still the plain-C `HostApi` struct that `ExternalBackend` uses internally.
///
/// The signatures match `HostApi` 1:1. When a new HostApi entry is added,
/// the same signature is appended here as a pure virtual, and both backends
/// get a new implementation in the same commit. See the parity checklist in
/// `.cursor/rules/adding-features.mdc`.

#pragma once

#include "abi.h"

namespace titan {
namespace detail {

/// Abstract backend interface. One pure virtual per `HostApi` fn pointer.
/// Signatures mirror `HostApi` exactly so both backends can be written by
/// direct correspondence.
struct IBackend {
    virtual ~IBackend() = default;

    // Optional data capabilities; older/local test backends remain valid.
    virtual uint8_t getWidgetModelIdAtPath(
            const TitanPluginSdk::WidgetAddressState*, int32_t* outModelId) {
        if (outModelId) *outModelId = -1;
        return 0;
    }
    virtual uint8_t copyItemWornAction(int32_t, uint32_t, char*, uint32_t,
                                      uint32_t* outRequired) {
        if (outRequired) *outRequired = 0;
        return 0;
    }
    virtual uint8_t getNpcBaseId(int32_t, int32_t, int32_t* outBaseId) {
        if (outBaseId) *outBaseId = -1;
        return 0;
    }

    // --- Logging ---
    virtual void log(const char* msg) = 0;

    // --- Game state queries ---
    virtual uint8_t getClientState(TitanPluginSdk::ClientState* outState) = 0;
    /// SDK 120+. Cheap freshness epoch (== ClientState::tickCount). The default
    /// builds a full ClientState so backends predating this keep working; both
    /// real backends override it with a single cheap read.
    virtual int32_t getLiveStateEpoch() {
        TitanPluginSdk::ClientState st{};
        return getClientState(&st) ? st.tickCount : 0;
    }
    virtual uint32_t getPlayers(TitanPluginSdk::PlayerState* outPlayers, uint32_t maxPlayers) = 0;
    virtual uint8_t getPlayerComposition(uint64_t playerEntityPtr,
                                         TitanPluginSdk::PlayerCompositionState* out) = 0;
    virtual uint32_t getNpcs(TitanPluginSdk::NpcState* outNpcs, uint32_t maxNpcs) = 0;
    virtual uint32_t getTileObjects(int32_t radius, TitanPluginSdk::TileObjectState* outObjects, uint32_t maxObjects) = 0;
    virtual uint32_t getGroundItems(int32_t radius, TitanPluginSdk::GroundItemState* outItems, uint32_t maxItems) = 0;
    virtual uint32_t getTileObjectsOnTile(int32_t plane, int32_t tileX, int32_t tileY,
                                           TitanPluginSdk::TileObjectState* outObjects, uint32_t maxObjects) = 0;
    virtual uint32_t getTileObjectsOnTileInWorldView(
        int32_t worldViewId, int32_t plane, int32_t tileX, int32_t tileY,
        TitanPluginSdk::TileObjectState* outObjects, uint32_t maxObjects) = 0;
    virtual uint32_t getGroundItemsOnTile(int32_t plane, int32_t tileX, int32_t tileY,
                                          TitanPluginSdk::GroundItemState* outItems, uint32_t maxItems) = 0;
    virtual uint32_t getProjectiles(TitanPluginSdk::ProjectileState* outProjectiles, uint32_t maxProjectiles) = 0;
    /// SDK 57+. Enumerate active `MapSpotAnim` instances (RuneLite's
    /// `GraphicsObject`) rooted at `WorldView::GraphicsObjectList`.
    virtual uint32_t getGraphicsObjects(TitanPluginSdk::GraphicsObjectState* outGraphicsObjects,
                                        uint32_t maxGraphicsObjects) = 0;
    /// SDK 59+. Enumerate valid world-point entries from an actor path queue.
    virtual uint32_t getActorPathQueue(uint64_t entityPtr,
                                       TitanPluginSdk::WorldPointState* out,
                                       uint32_t max) = 0;
    /// SDK 89+. Enumerate an actor path queue relative to the actor's WorldView.
    virtual uint32_t getActorPathQueueInWorldView(
        uint64_t entityPtr, int32_t worldViewId,
        TitanPluginSdk::WorldPointState* out, uint32_t max) = 0;
    /// SDK 76+. Enumerate active per-actor spot animations.
    virtual uint32_t getActorSpotAnims(uint64_t entityPtr,
                                       TitanPluginSdk::ActorSpotAnimState* out,
                                       uint32_t max) = 0;
    /// SDK 81+. WorldView and instance-template helpers.
    virtual uint8_t getWorldViewById(int32_t worldViewId, uint64_t* outPtr) = 0;
    virtual uint8_t getTopLevelWorldView(uint64_t* outPtr) = 0;
    virtual uint8_t getInstanceTemplateChunks(
        TitanPluginSdk::InstanceTemplateChunksState* out) = 0;
    virtual uint8_t worldPointFromLocalInstance(
        const TitanPluginSdk::WorldPointState* in,
        TitanPluginSdk::WorldPointState* out) = 0;
    virtual uint8_t worldPointToLocalInstance(
        const TitanPluginSdk::WorldPointState* in,
        TitanPluginSdk::WorldPointState* out) = 0;
    /// SDK 82+. Minimap red-flag walking destination.
    virtual uint8_t getLocalDestinationLocation(
        TitanPluginSdk::LocalPointState* out) = 0;
    virtual uint8_t getWorldDestinationLocation(
        TitanPluginSdk::WorldPointState* out) = 0;

    // --- Camera ---
    virtual uint8_t getCameraState(TitanPluginSdk::CameraState* outState) = 0;

    // --- Interface scale (SDK 109) ---
    /// Live UI-frame -> physical interface-scale factor plus optional canvas
    /// origin. Default writes identity (1.0 / 0) and returns 0 so backends
    /// that predate this keep compiling; both real backends override it.
    virtual uint8_t getInterfaceScale(float* outScaleX, float* outScaleY,
                                      int32_t* outCanvasOriginX,
                                      int32_t* outCanvasOriginY) {
        if (outScaleX) *outScaleX = 1.0f;
        if (outScaleY) *outScaleY = 1.0f;
        if (outCanvasOriginX) *outCanvasOriginX = 0;
        if (outCanvasOriginY) *outCanvasOriginY = 0;
        return 0;
    }

    // --- Rendering (ImGui context + world-space + screen-space) ---
    virtual void setImGuiContext(void* ctx) = 0;
    virtual uint8_t worldToScreen(int32_t worldX, int32_t worldY, int32_t worldZ,
                                  int32_t* screenX, int32_t* screenY) = 0;
    virtual uint8_t tileToScreen(int32_t tileX, int32_t tileY, int32_t plane, int32_t heightOffset,
                                 int32_t* screenX, int32_t* screenY) = 0;
    virtual int32_t getTileHeight(int32_t preciseX, int32_t preciseY, int32_t plane) = 0;
    virtual void drawTileQuad(int32_t tileX, int32_t tileY, int32_t plane,
                              uint32_t fillColor, uint32_t outlineColor) = 0;
    virtual void drawTileRegion(int32_t minTX, int32_t minTY, int32_t maxTX, int32_t maxTY, int32_t plane,
                                uint32_t fillColor, uint32_t outlineColor) = 0;
    virtual void drawEntityBox(int32_t preciseX, int32_t preciseY, int32_t plane,
                               int32_t tileSize, int32_t height, uint32_t color) = 0;
    virtual void drawTextAtWorld(int32_t worldX, int32_t worldY, int32_t worldZ,
                                 const char* text, uint32_t color, uint8_t centered) = 0;
    virtual uint8_t worldToScreenInWorldView(int32_t worldViewId,
                                             int32_t preciseX, int32_t worldY,
                                             int32_t preciseY, int32_t plane,
                                             int32_t* screenX, int32_t* screenY) = 0;
    virtual int32_t getTileHeightInWorldView(int32_t worldViewId,
                                             int32_t preciseX, int32_t preciseY,
                                             int32_t plane) = 0;
    virtual void drawTileQuadInWorldView(int32_t worldViewId,
                                         int32_t tileX, int32_t tileY,
                                         int32_t plane,
                                         uint32_t fillColor,
                                         uint32_t outlineColor) = 0;
    virtual void drawTileRegionInWorldView(int32_t worldViewId,
                                           int32_t minTX, int32_t minTY,
                                           int32_t maxTX, int32_t maxTY,
                                           int32_t plane,
                                           uint32_t fillColor,
                                           uint32_t outlineColor) = 0;
    virtual void drawTextAtWorldInWorldView(int32_t worldViewId,
                                            int32_t preciseX, int32_t worldY,
                                            int32_t preciseY, int32_t plane,
                                            const char* text, uint32_t color,
                                            uint8_t centered) = 0;

    // --- Scene queries ---
    virtual int32_t getCollisionFlag(int32_t plane, int32_t tileX, int32_t tileY) = 0;

    // --- Input ---
    virtual uint8_t getMousePos(int32_t* outX, int32_t* outY) = 0;

    // --- Screen-space rendering ---
    virtual void drawScreenText(int32_t screenX, int32_t screenY,
                                const char* text, uint32_t color) = 0;
    virtual void drawScreenRect(int32_t x, int32_t y, int32_t w, int32_t h,
                                uint32_t color) = 0;
    virtual void drawScreenLine(int32_t x1, int32_t y1, int32_t x2, int32_t y2,
                                uint32_t color, float thickness) = 0;

    // --- Var system ---
    virtual int32_t getVarbit(int32_t varbitId) = 0;
    virtual int32_t getVarp(int32_t varpId) = 0;

    // --- Prayer ---
    virtual uint8_t isPrayerActive(int32_t prayerOrdinal) = 0;

    // --- Skills ---
    virtual int32_t getBoostedSkillLevel(int32_t skillId) = 0;
    virtual int32_t getRealSkillLevel(int32_t skillId) = 0;
    virtual int32_t getSkillExperience(int32_t skillId) = 0;

    // --- Synthetic action dispatch ---
    virtual uint8_t executeSyntheticAction(uint32_t opcode, int32_t identifier,
                                           int32_t param0, int32_t param1,
                                           int32_t worldViewId,
                                           int32_t clickX, int32_t clickY,
                                           const char* actionText,
                                           const char* targetText,
                                           uint8_t skipClick) = 0;
    virtual uint8_t executeSyntheticEntry(const TitanPluginSdk::SyntheticActionEntry* entry) = 0;
    virtual uint8_t resolveActionClickPoint(
        const TitanPluginSdk::ActionClickPointSpec* action,
        int32_t* screenX,
        int32_t* screenY) = 0;

    // --- Walking ---
    virtual uint8_t walkTo(int32_t sceneX, int32_t sceneY) = 0;
    virtual uint8_t walkToWorld(int32_t worldX, int32_t worldY, int32_t plane) = 0;

    // --- Entity hiding ---
    virtual void setEntityHidden(uint8_t entityType, uint8_t hidden) = 0;
    virtual uint8_t getEntityHidden(uint8_t entityType) = 0;

    // --- Audio playback toggle ---
    virtual void setAudioPlaybackDisabled(uint8_t disabled) = 0;
    virtual uint8_t getAudioPlaybackDisabled() = 0;

    // --- Interact API ---
    virtual uint8_t interactNpc(const char* action, int32_t npcIdOrNeg1,
                                const char* nameOrNull) = 0;
    virtual uint8_t interactNpcByIndex(const char* action, int32_t hashIndex) = 0;
    virtual uint8_t interactNpcByIndexInWorldView(const char* action,
                                                  int32_t hashIndex,
                                                  int32_t worldViewId) = 0;
    virtual uint8_t interactObject(const char* action, int32_t locIdOrNeg1,
                                   const char* nameOrNull) = 0;
    virtual uint8_t interactTileObject(const char* action,
                                       const TitanPluginSdk::TileObjectState* object) = 0;
    virtual uint8_t interactGroundItem(const char* action, int32_t itemId,
                                       int32_t tileX, int32_t tileY) = 0;
    virtual uint8_t interactGroundItemInWorldView(const char* action,
                                                  int32_t itemId,
                                                  int32_t tileX,
                                                  int32_t tileY,
                                                  int32_t worldViewId) = 0;

    // --- Find API ---
    virtual uint8_t findNearestNpc(int32_t npcIdOrNeg1, const char* nameOrNull,
                                   TitanPluginSdk::NpcState* outNpc) = 0;
    virtual uint8_t findNearestObject(int32_t locIdOrNeg1, const char* nameOrNull,
                                      TitanPluginSdk::TileObjectState* outObject) = 0;

    // --- Interaction target resolution ---
    virtual uint8_t getInteracting(int32_t interactingIndex, uint8_t interactingType,
                                   TitanPluginSdk::PlayerState* outPlayer,
                                   TitanPluginSdk::NpcState* outNpc) = 0;

    // --- Thread dispatch ---
    // SDK 116 contract: cleanup(userData) runs exactly once -- after the
    // callback ran, or when the request was dropped/rejected without running.
    // The callback must not free userData.
    virtual void runOnClientTick(void (*callback)(void* userData), void* userData,
                                 void (*cleanup)(void* userData)) = 0;
    virtual void runOnRender(void (*callback)(void* userData), void* userData,
                             void (*cleanup)(void* userData)) = 0;

    // --- Cache definition lookups ---
    virtual uint8_t getItemDef(int32_t id, TitanPluginSdk::ItemDefSnapshot* out) = 0;
    virtual uint8_t getNpcDef(int32_t id, TitanPluginSdk::NpcDefSnapshot* out) = 0;
    virtual uint8_t getObjDef(int32_t id, TitanPluginSdk::ObjDefSnapshot* out) = 0;
    virtual uint8_t getVarbitDef(int32_t id, TitanPluginSdk::VarbitDefSnapshot* out) = 0;

    // --- Internal developer tools ---
    virtual void setInternalToolVisible(const char* toolId, uint8_t visible) = 0;
    virtual uint8_t getInternalToolVisible(const char* toolId) = 0;

    // --- Worlds (SDK 28) ---
    virtual uint8_t getCurrentWorld(int32_t* outWorld) = 0;
    virtual uint32_t getWorldList(TitanPluginSdk::WorldState* out, uint32_t cap) = 0;
    virtual uint8_t hopToWorldId(int32_t worldId) = 0;
    virtual uint8_t hopToListIndex(uint32_t idx) = 0;
    /// SDK 31 -- in-game CC_OP footer hop sequence. See abi.h for
    /// semantics. Returns 0 when pre-SDK-31 clients lack the vtable
    /// entry.
    virtual uint8_t hopToWorldIngame(int32_t worldId) = 0;
    /// SDK 83 -- SLR-backed world metadata and manual refresh.
    virtual uint32_t getWorldMetadata(TitanPluginSdk::WorldMetadataState* out,
                                      uint32_t cap) = 0;
    virtual uint8_t refreshWorldMetadata() = 0;

    // --- Inventory ---
    virtual uint8_t containsInventoryItem(int32_t itemId) = 0;
    virtual uint8_t interactInventoryItemAtSlot(int32_t slot, int32_t itemId, const char* action) = 0;
    virtual uint8_t interactInventoryItem(int32_t itemId, const char* action) = 0;
    virtual uint32_t getInventoryItems(TitanPluginSdk::InventoryItemState* out, uint32_t max) = 0;

    // --- CS2 script execution ---
    virtual uint8_t runClientScript(int32_t scriptId,
                                    const int32_t* intArgs, uint32_t intArgCount,
                                    TitanPluginSdk::Cs2ScriptResult* outResult) = 0;
    virtual int32_t getQuestState(int32_t questId) = 0;

    // --- Local player ---
    virtual uint8_t getLocalPlayer(TitanPluginSdk::PlayerState* outPlayer) = 0;

    // --- Idle timer ---
    virtual int32_t getIdleTimeRemaining() = 0;
    virtual void resetIdleTimer() = 0;

    // --- Widget queries ---
    virtual uint8_t getWidget(uint32_t packedId, TitanPluginSdk::WidgetState* outState) = 0;

    // --- Plugin manager ---
    virtual uint32_t listPlugins(TitanPluginSdk::PluginInfo* out, uint32_t max) = 0;
    /// Upper-bound plugin count used to size enumeration buffers (SDK 95).
    /// Default returns 0 so backends that predate this keep compiling; the
    /// real backends override it.
    virtual uint32_t getPluginCount() { return 0; }
    virtual uint8_t getPlugin(const char* pluginId, TitanPluginSdk::PluginInfo* out) = 0;
    virtual uint8_t setPluginEnabled(const char* pluginId, uint8_t enabled) = 0;
    virtual int8_t isPluginEnabled(const char* pluginId) = 0;

    // --- Login / account switch ---
    virtual uint8_t getLoginAccountState(TitanPluginSdk::LoginAccountState* out) = 0;
    virtual void setLoginUsername(const char* username) = 0;
    virtual void setLoginPassword(const char* password) = 0;
    virtual void setLoginAuthenticator(const char* code) = 0;
    virtual void setLoginIndex(int32_t loginIndex) = 0;
    virtual void setLoginDisplayName(const char* displayName) = 0;
    virtual void setLoginOAuth2Credentials(const char* accessToken,
                                           const char* refreshToken) = 0;
    virtual void setLoginGameSessionCredentials(const char* sessionId,
                                                const char* characterId) = 0;
    virtual void setLoginCharacter(const char* displayName,
                                   const char* characterId,
                                   const char* sessionId) = 0;
    virtual void resetLoginCharacter() = 0;
    virtual uint8_t submitLoginLauncherCredentials() = 0;
    virtual uint8_t submitLoginStandardCredentials() = 0;
    virtual uint8_t acknowledgeStandardLogin() = 0;
    virtual uint8_t advanceLoginClickToPlay() = 0;
    virtual uint8_t getLoginFlowState(TitanPluginSdk::LoginFlowState* out) = 0;
    virtual uint8_t advanceLoginLauncherCredentials() = 0;
    virtual uint8_t advanceLoginLogout() = 0;
    virtual void cancelLoginProfileOperations() = 0;
    virtual void cancelLoginLogoutOperation() = 0;
    virtual uint8_t stageLoginCredentials(const char* profileLabel) = 0;
    virtual uint8_t submitLoginCredentials() = 0;

    // --- Sanitized proxy routing (SDK 97) ---
    virtual uint32_t listSanitizedProxyRoutes(
        TitanPluginSdk::SanitizedProxyRouteState* out, uint32_t capacity) = 0;
    virtual uint8_t setProxyRoute(
        const char* proxyId, TitanPluginSdk::ProxyRouteStatusState* outStatus) = 0;
    virtual uint8_t getProxyRouteStatus(
        TitanPluginSdk::ProxyRouteStatusState* outStatus) = 0;

    // --- Chat injection ---
    virtual void addChatMessage(int32_t type, const char* name,
                                const char* message, const char* sender) = 0;

    // --- Item containers + runtime ItemDef (SDK 26) ---
    virtual uint8_t getItemContainer(int32_t containerId,
                                     TitanPluginSdk::ItemContainerState* outState) = 0;
    virtual uint8_t getItemComposition(int32_t itemId,
                                       TitanPluginSdk::ItemCompositionState* outState) = 0;

    // --- Use-on item API (SDK 32) ---
    virtual uint8_t useInventoryItemOnItem(int32_t srcSlot, int32_t srcItemId,
                                           int32_t tgtSlot, int32_t tgtItemId) = 0;
    virtual uint8_t useInventoryItemOnNpc(int32_t srcSlot, int32_t srcItemId,
                                          int32_t npcHashIndex) = 0;
    virtual uint8_t useInventoryItemOnObject(int32_t srcSlot, int32_t srcItemId,
                                             int32_t locId, int32_t tileX,
                                             int32_t tileY) = 0;

    // --- Entity clickbox + hull overlays (SDK 33) ---
    /// Project the entity's cached world-space AABB and draw wireframe
    /// edges + optional translucent face fills. No-op when the analyzer
    /// did not detect the supporting offsets on this revision.
    virtual void drawEntityClickbox(uint64_t entityPtr, uint64_t typecode,
                                    uint32_t outline, uint32_t fill) = 0;
    virtual void drawTileObjectClickbox(uint64_t locPtr, uint64_t typecode,
                                        uint32_t outline, uint32_t fill) = 0;
    /// Draw the 2D convex hull of the AABB's 8 projected corners -- a
    /// clean closed silhouette with no interior edges. Same data source
    /// as the clickbox but reduced to the outer outline.
    virtual void drawEntityHull(uint64_t entityPtr, uint64_t typecode,
                                uint32_t outline, uint32_t fill) = 0;
    virtual void drawTileObjectHull(uint64_t locPtr, uint64_t typecode,
                                    uint32_t outline, uint32_t fill) = 0;

    // --- True model-vertex outlines (SDK 122; mode added SDK 123) ---
    /// Draw the silhouette of the entity's actual projected model vertices
    /// -- following the real mesh, versus drawEntityHull's 8-corner AABB
    /// hull. @p mode: 0 = convex hull, 1 = concave hull. No-op when the
    /// model handle isn't bound this frame or the host lacks Model geometry.
    virtual void drawEntityOutline(uint64_t entityPtr, uint64_t typecode,
                                   uint32_t outline, uint32_t fill,
                                   uint32_t mode) = 0;
    virtual void drawTileObjectOutline(uint64_t locPtr, uint64_t typecode,
                                       uint32_t outline, uint32_t fill,
                                       uint32_t mode) = 0;

    // --- Generic widget interaction (SDK 36) ---
    virtual uint8_t widgetInteract(uint32_t opcode, int32_t identifier,
                                   int32_t param0, int32_t param1) = 0;

    // --- Widget child enumeration (SDK 38) ---
    virtual uint32_t getWidgetChildren(uint32_t parentPackedId,
                                       TitanPluginSdk::WidgetState* outStates,
                                       uint32_t maxOut) = 0;

    // --- Widget text search (SDK 39) ---
    virtual uint8_t getWidgetByText(const char* query,
                                    TitanPluginSdk::WidgetState* outState) = 0;

    // --- Keyboard (SDK 44) ---
    virtual uint8_t sendKeyboardString(const char* utf8) = 0;
    virtual uint8_t sendKeyboardKey(int32_t key, uint32_t modMask) = 0;
    virtual uint8_t typeKeyboardString(const char* utf8, int32_t minDelayMs, int32_t maxDelayMs) = 0;
    virtual void cancelKeyboardType() = 0;
    virtual uint8_t isKeyboardTyping() = 0;

    // --- OverlayPanels (SDK 46) ---
    virtual int32_t overlayPanelRegister(const char* pluginId,
                                         const char* panelName,
                                         uint8_t defaultAnchor,
                                         int32_t defaultPriority) = 0;
    virtual void overlayPanelUnregister(int32_t handle) = 0;
    virtual void overlayPanelBegin(int32_t handle, int32_t preferredWidth) = 0;
    virtual void overlayPanelEnd(int32_t handle) = 0;
    virtual void overlayPanelSetStyle(int32_t handle,
                                      const TitanPluginSdk::OverlayPanelStyleAbi* style) = 0;
    virtual void overlayPanelTitle(int32_t handle, const char* text,
                                   uint32_t color) = 0;
    virtual void overlayPanelLine(int32_t handle, const char* left,
                                  const char* right,
                                  uint32_t leftColor,
                                  uint32_t rightColor) = 0;
    virtual void overlayPanelProgressBar(int32_t handle,
                                         int32_t value, int32_t minVal,
                                         int32_t maxVal,
                                         uint32_t fillColor,
                                         uint32_t bgColor) = 0;

    // --- Widget text setter (SDK 51) ---
    virtual uint8_t setWidgetText(uint32_t packedId, const char* text) = 0;

    // --- VarClient values (SDK 62) ---
    virtual uint8_t getVarClientInt(int32_t id, int32_t* outValue) = 0;
    virtual uint8_t setVarClientInt(int32_t id, int32_t value) = 0;
    virtual uint32_t getVarClientString(int32_t id, char* out,
                                        uint32_t capacity) = 0;
    virtual uint8_t setVarClientString(int32_t id, const char* value) = 0;
    virtual uint8_t getVarClientLong(int32_t id, int64_t* outValue) = 0;
    virtual uint8_t setVarClientLong(int32_t id, int64_t value) = 0;

    // --- Slot-addressed dynamic widget text writes (SDK 63) ---
    virtual uint8_t setWidgetTextAtSlot(uint32_t parentPackedId, int32_t slot,
                                        const char* text) = 0;

    // --- Slot-aware recursive widget queries (SDK 64) ---
    virtual uint32_t getWidgets(uint32_t groupId,
                                TitanPluginSdk::WidgetQueryState* outStates,
                                uint32_t maxOut, uint8_t* outTruncated) = 0;
    virtual uint32_t getWidgetChildrenAtPath(
        const TitanPluginSdk::WidgetAddressState* parent,
        TitanPluginSdk::WidgetQueryState* outStates, uint32_t maxOut) = 0;
    virtual uint8_t setWidgetTextAtPath(
        const TitanPluginSdk::WidgetAddressState* address,
        const char* text) = 0;
    virtual uint8_t widgetInteractAtPath(
        const TitanPluginSdk::WidgetAddressState* address,
        uint32_t opcode, int32_t identifier, int32_t childSlot) = 0;

    // --- Direct identity resolvers for live handles (SDK 89) ---
    virtual uint8_t getPlayerByIndexInWorldView(int32_t hashIndex,
                                                int32_t worldViewId,
                                                TitanPluginSdk::PlayerState* outPlayer) = 0;
    virtual uint8_t getNpcByIndexInWorldView(int32_t hashIndex,
                                             int32_t worldViewId,
                                             TitanPluginSdk::NpcState* outNpc) = 0;
    virtual uint8_t getWidgetAtPath(
        const TitanPluginSdk::WidgetAddressState* address,
        TitanPluginSdk::WidgetState* outState) = 0;

    // --- Cross-plugin service registry (SDK 66) ---
    virtual void registerPluginService(const char* serviceId, void* service) = 0;
    virtual uint8_t registerPluginServiceOwned(const void* pluginInstance,
                                               const char* pluginId,
                                               const char* serviceId,
                                               void* service) = 0;
    virtual void* getPluginService(const char* serviceId) = 0;
    /// Borrow a native service under a host-held provider lifetime lease.
    /// Defaults fail closed for internal/test backends without native leases.
    virtual uint8_t acquirePluginService(const char* serviceId,
                                         void** outService, void** outLease) {
        (void)serviceId;
        if (outService) *outService = nullptr;
        if (outLease) *outLease = nullptr;
        return 0;
    }
    virtual void releasePluginLease(void* lease) { (void)lease; }
    virtual uint8_t acquirePluginWork(void** outLease) {
        if (outLease) *outLease = nullptr;
        return 0;
    }

    // --- Break Handler registry (SDK 97) ---
    virtual uint8_t breakHandlerRegisterPlugin(
        const void* pluginInstance,
        const TitanPluginSdk::BreakRegistrationState* registration) = 0;
    virtual uint8_t breakHandlerStart(const void* pluginInstance,
                                      const char* pluginId) = 0;
    virtual uint8_t breakHandlerStop(const void* pluginInstance,
                                     const char* pluginId) = 0;
    virtual uint8_t breakHandlerUnregisterPlugin(const void* pluginInstance,
                                                 const char* pluginId) = 0;
    virtual uint8_t breakHandlerPoll(
        const void* pluginInstance, const char* pluginId,
        TitanPluginSdk::BreakCommandState* outCommand) = 0;
    virtual uint8_t breakHandlerReport(
        const void* pluginInstance,
        const TitanPluginSdk::BreakReportState* report) = 0;
    virtual uint32_t breakHandlerCoordinatorSnapshot(
        const void* coordinatorInstance, const char* coordinatorId,
        TitanPluginSdk::BreakParticipantState* out, uint32_t capacity) = 0;
    virtual uint8_t breakHandlerCoordinatorPublish(
        const void* coordinatorInstance, const char* coordinatorId,
        const TitanPluginSdk::BreakCommandState* command) = 0;
    virtual uint8_t breakHandlerCoordinatorClear(
        const void* coordinatorInstance, const char* coordinatorId,
        uint64_t expectedEpoch) = 0;

    // --- Bulk collision snapshots + web walker (SDK 112) ---
    virtual bool supportsCurrentSceneTileObjects() const { return false; }
    virtual uint32_t getCurrentSceneTileObjects(
        TitanPluginSdk::TileObjectState*, uint32_t) { return UINT32_MAX; }
    virtual uint8_t getCollisionSourceReady(uint8_t* outReady) {
        if (outReady) *outReady = 0;
        return 0;
    }
    virtual uint8_t copyCachedCollisionRegion(
        uint32_t regionId, int32_t* outFlags, uint32_t capacity,
        uint32_t* outCount) {
        if (outCount) *outCount = 0;
        return TitanPluginSdk::CollisionSnapshotStatus::Unavailable;
    }
    virtual uint8_t copyCurrentCollisionScene(
        TitanPluginSdk::CollisionSceneSnapshotState* outScene,
        int32_t* outFlags, uint32_t capacity, uint32_t* outCount) {
        if (outCount) *outCount = 0;
        return TitanPluginSdk::CollisionSnapshotStatus::Unavailable;
    }
    virtual uint8_t webPathSubmit(
        const TitanPluginSdk::WebPathRequestState* request,
        const TitanPluginSdk::WorldPointState* forbiddenTiles,
        uint32_t forbiddenTileCount, uint64_t* outRequestId) {
        if (outRequestId) *outRequestId = 0;
        return 0;
    }
    virtual uint8_t webPathPoll(
        uint64_t requestId,
        TitanPluginSdk::WebPathSummaryState* outSummary) {
        return 0;
    }
    virtual uint8_t webPathCopySteps(
        uint64_t requestId, TitanPluginSdk::WebPathStepState* outSteps,
        uint32_t capacity, uint32_t* outCount) {
        if (outCount) *outCount = 0;
        return 0;
    }
    virtual uint8_t webPathCancel(uint64_t requestId) { return 0; }
    virtual uint8_t webPathRelease(uint64_t requestId) { return 0; }

    // --- Web walk executor (SDK 114) ---
    virtual uint8_t webWalkStart(
        const TitanPluginSdk::WebWalkRequestState* request,
        const TitanPluginSdk::WorldPointState* forbiddenTiles,
        uint32_t forbiddenTileCount, uint64_t* outWalkId) {
        if (outWalkId) *outWalkId = 0;
        (void)request; (void)forbiddenTiles; (void)forbiddenTileCount;
        return 0;
    }
    virtual uint8_t webWalkStatus(
        uint64_t walkId, TitanPluginSdk::WebWalkStatusState* outStatus) {
        (void)walkId; (void)outStatus;
        return 0;
    }
    virtual uint8_t webWalkCancel(uint64_t walkId) {
        (void)walkId;
        return 0;
    }
    virtual uint8_t webWalkRelease(uint64_t walkId) {
        (void)walkId;
        return 0;
    }
    virtual uint8_t webWalkAdvance(uint64_t walkId) {
        (void)walkId;
        return 0;
    }
    virtual uint8_t webPathCopyStepPayload(
        uint64_t requestId, uint32_t stepIndex, char* outUtf8,
        uint32_t capacity, uint32_t* outRequired) {
        if (outRequired) *outRequired = 0;
        (void)requestId; (void)stepIndex; (void)outUtf8; (void)capacity;
        return 0;
    }

    // --- Native world-map display state (SDK 113) ---
    virtual uint8_t getWorldMapState(
        TitanPluginSdk::WorldMapState* outState) {
        if (outState) *outState = TitanPluginSdk::WorldMapState{};
        return 0;
    }

    // --- Current native client game cycle (SDK 125) ---
    virtual int32_t getGameCycle() = 0;

    // --- Actor overhead text (SDK 127) ---
    virtual uint8_t getActorOverheadText(uint64_t entityPtr, char* out,
                                         uint64_t capacity, uint64_t* outLength) = 0;
    virtual uint8_t getActorOverheadTextCyclesRemaining(uint64_t entityPtr, int32_t* out) = 0;
    virtual uint32_t getOverheadTextCapabilities() = 0;
    // SDK 128. Default preserves source compatibility of other backend adapters.
    virtual uint8_t executeSelectedActionPair(const TitanPluginSdk::SelectedActionPair*) { return 0; }

    // --- Grand Exchange owned snapshots (SDK 136) ---
    virtual bool getGrandExchangeOffer(int32_t, TitanPluginSdk::GrandExchangeOffer*) { return false; }
    virtual int32_t getGrandExchangeOffers(TitanPluginSdk::GrandExchangeOffer*, int32_t) { return 0; }
    virtual bool isGrandExchangeAvailable() { return false; }

    virtual bool requestItemPriceCatalog() { return false; }
    virtual bool requestItemPrice(int32_t) { return false; }
    virtual bool getItemPriceStatus(TitanPluginSdk::ItemPriceStatus*) { return false; }
    virtual bool getItemPriceMetadata(int32_t, TitanPluginSdk::ItemPriceMetadata*) { return false; }
    virtual int32_t getItemPriceItemIds(int32_t*, int32_t) { return 0; }
    virtual bool getItemPrice(int32_t, TitanPluginSdk::ItemPrice*) { return false; }
    virtual uint64_t geSubmitBuy(const TitanPluginSdk::GeBuyOptions*) { return 0; }
    virtual bool geGetRequest(uint64_t, TitanPluginSdk::GeRequestState*) { return false; }
    virtual int32_t geGetRequests(TitanPluginSdk::GeRequestState*, int32_t) { return 0; }
    virtual bool geCancelRequest(uint64_t) { return false; }
    virtual bool geReleaseRequest(uint64_t) { return false; }
    virtual bool getItemCacheBank(TitanPluginSdk::BankCacheState*) { return false; }

    // --- Full-frame game screenshot (SDK 131) ---
    // Defaults fail closed so backend adapters that predate the feature still compile.
    virtual uint8_t screenshotSubmit(uint64_t* outRequestId) {
        if (outRequestId) *outRequestId = 0;
        return 0;
    }
    virtual uint8_t screenshotPoll(uint64_t requestId,
                                   TitanPluginSdk::ScreenshotStatusState* outStatus) {
        (void)requestId; (void)outStatus;
        return 0;
    }
    virtual uint8_t screenshotCopyPng(uint64_t requestId, uint8_t* out,
                                      uint32_t capacity, uint32_t* outRequired) {
        if (outRequired) *outRequired = 0;
        (void)requestId; (void)out; (void)capacity;
        return 0;
    }
    virtual uint8_t screenshotRelease(uint64_t requestId) {
        (void)requestId;
        return 0;
    }

    // --- Plugin-authored setting changes (SDK 140) -----------------
    // Default is a no-op so a backend adapter that predates the feature
    // still compiles; the change then simply is not persisted, which is
    // the pre-140 behaviour.
    virtual void markSettingChanged(const char* pluginId,
                                    const char* settingKey,
                                    const TitanNativeRecords::Value* value,
                                    const uint8_t* hidden) {
        (void)pluginId; (void)settingKey; (void)value; (void)hidden;
    }

    // --- Cross-Tab Store (SDK 141) ---------------------------------
    // Defaults fail closed so backend adapters that predate the feature
    // still compile.
    virtual uint8_t crossTabWrite(const void* pluginInstance,
                                  const char* pluginId,
                                  const TitanPluginSdk::CrossTabWrite* write,
                                  uint64_t* outWriteId) {
        (void)pluginInstance; (void)pluginId; (void)write;
        if (outWriteId) *outWriteId = 0;
        return 0;
    }
    virtual uint32_t crossTabRead(const void* pluginInstance,
                                  const char* pluginId, const char* key,
                                  uint8_t* out, uint32_t capacity,
                                  TitanPluginSdk::CrossTabEntryInfo* info) {
        (void)pluginInstance; (void)pluginId; (void)key; (void)out;
        (void)capacity; (void)info;
        return TitanPluginSdk::kCrossTabAbsent;
    }

    // --- Preview pills (SDK 142) -----------------------------------
    // Default fails closed so backend adapters that predate the feature
    // still compile.
    virtual uint8_t previewPillWrite(const void* pluginInstance,
                                     const char* pluginId,
                                     const TitanPluginSdk::PreviewPillWrite* write) {
        (void)pluginInstance; (void)pluginId; (void)write;
        return 0;
    }

    // --- Break Handler observation (SDK 144) -----------------------
    // Default fails closed so backend adapters that predate the feature
    // still compile.
    virtual uint8_t breakHandlerObserve(
            const void* pluginInstance, const char* pluginId,
            TitanPluginSdk::BreakCommandState* outCommand) {
        (void)pluginInstance; (void)pluginId; (void)outCommand;
        return 0;
    }
};

/// Per-DLL static slot. Each plugin DLL and `client.dll` has its own copy;
/// both install their own backend and the fluent facades dispatch through
/// whichever one is live in the current DLL.
inline IBackend*& backendRef() {
    static IBackend* g_backend = nullptr;
    return g_backend;
}

struct ThreadBackendContext {
    bool active = false;
    IBackend* backend = nullptr;
};

inline ThreadBackendContext& threadBackendContext() noexcept {
    static thread_local ThreadBackendContext context;
    return context;
}

inline IBackend* backend() {
    const auto& context = threadBackendContext();
    return context.active ? context.backend : backendRef();
}

}  // namespace detail
}  // namespace titan
