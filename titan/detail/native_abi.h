#pragma once

// Native DLL ABI v1. This is the public binary contract, independent of the
// client/controller transport protocol. Existing table members are frozen.
// New optional functions may only be appended; incompatible table changes
// need a new interface major. Referenced payload records change layout only
// together with kMinSupportedSdkVersion: hosts refuse modules whose
// sdkRelease is below it, and plugins refuse hosts whose
// HostCoreV1::sdkRelease is below it. Do not regenerate existing
// declarations from HostApi.
// The supported ABI is the Windows x64 C calling convention and natural
// alignment. C++ facades and the local HostApi/PluginApi views are not ABI.
#include "abi.h"
#include "native_payload_v1.h"

#include <cstddef>
#include <cstdint>
#include <cstring>
#include <type_traits>

namespace TitanPluginSdk::NativeAbi {

inline constexpr uint32_t kAbiVersion = 1;
inline constexpr const char* kQueryExportName = "TitanPlugin_QueryNative";

struct InterfaceHeader {
    uint32_t structSize = sizeof(InterfaceHeader);
    uint32_t majorVersion = 1;
};

using QueryInterfaceFn = const InterfaceHeader* (*)(
    const void* context, uint64_t interfaceId, uint32_t majorVersion);

// The provider and every returned table remain valid until its owning host
// context or plugin descriptor is destroyed. queryInterface must not throw.
struct InterfaceProviderV1 {
    uint32_t structSize = sizeof(InterfaceProviderV1);
    uint32_t abiVersion = kAbiVersion;
    const void* context = nullptr;
    QueryInterfaceFn queryInterface = nullptr;
};

struct InterfaceRequirement {
    uint64_t interfaceId = 0;
    uint32_t majorVersion = 1;
    uint32_t minStructSize = sizeof(InterfaceHeader);
};

struct PluginDescriptorV1 {
    uint32_t structSize = sizeof(PluginDescriptorV1);
    uint32_t abiVersion = kAbiVersion;
    uint32_t sdkRelease = kSdkVersion; // Diagnostics only; the module's release is the gate.
    uint32_t reserved = 0;
    void* userData = nullptr;
    InterfaceProviderV1 interfaces{};
    // Exactly once, in the owning DLL, after the host has drained all calls.
    // Frees the instance AND this descriptor. No access is legal afterwards.
    void (*destroy)(const PluginDescriptorV1* descriptor) = nullptr;
};

using CreatePluginFn = uint8_t (*)(
    uint32_t index, const InterfaceProviderV1* host,
    const PluginDescriptorV1** outPlugin, char* error, uint32_t errorCapacity);

struct ModuleDescriptorV1 {
    uint32_t structSize = sizeof(ModuleDescriptorV1);
    uint32_t abiVersion = kAbiVersion;
    // Hosts refuse modules built below their kMinSupportedSdkVersion.
    uint32_t sdkRelease = kSdkVersion;
    uint32_t pluginCount = 0;
    CreatePluginFn createPlugin = nullptr;
    // Borrowed immutable requirements, valid for the DLL lifetime. A null
    // function means no required capabilities. Missing optional capabilities
    // always import as null operations, regardless of the SDK release.
    const InterfaceRequirement* (*getRequirements)(
        uint32_t index, uint32_t* outCount) = nullptr;
};

// Export with C linkage. Returns nullptr for an unsupported bootstrap ABI.
// Querying a module must not instantiate plugins or call game operations.
using QueryNativePluginFn = const ModuleDescriptorV1* (*)(uint32_t abiVersion);

inline bool isValidProvider(const InterfaceProviderV1* provider) noexcept {
    return provider && provider->structSize >= sizeof(InterfaceProviderV1)
        && provider->abiVersion == kAbiVersion && provider->queryInterface;
}

inline bool isValidModule(const ModuleDescriptorV1* module) noexcept {
    return module && module->structSize >= sizeof(ModuleDescriptorV1)
        && module->abiVersion == kAbiVersion && module->createPlugin
        && module->pluginCount != 0;
}

inline bool isValidPluginDescriptor(const PluginDescriptorV1* plugin) noexcept {
    return plugin && plugin->structSize >= sizeof(PluginDescriptorV1)
        && plugin->abiVersion == kAbiVersion && plugin->reserved == 0
        && plugin->destroy && isValidProvider(&plugin->interfaces);
}

inline const InterfaceHeader* queryInterface(
    const InterfaceProviderV1* provider, uint64_t id, uint32_t major = 1) {
    if (!isValidProvider(provider)) return nullptr;
    const auto* table = provider->queryInterface(provider->context, id, major);
    if (!table || table->structSize < sizeof(InterfaceHeader)
        || table->majorVersion != major) return nullptr;
    return table;
}

inline bool requirementsSatisfied(const InterfaceProviderV1* provider,
                                  const InterfaceRequirement* requirements,
                                  uint32_t count,
                                  uint64_t* missingInterfaceId = nullptr) {
    if (missingInterfaceId) *missingInterfaceId = 0;
    if (!isValidProvider(provider) || (count && !requirements)) return false;
    for (uint32_t i = 0; i < count; ++i) {
        const auto& requirement = requirements[i];
        const auto* table = queryInterface(provider, requirement.interfaceId,
                                          requirement.majorVersion);
        if (requirement.minStructSize < sizeof(InterfaceHeader) || !table
            || table->structSize < requirement.minStructSize) {
            if (missingInterfaceId) *missingInterfaceId = requirement.interfaceId;
            return false;
        }
    }
    return true;
}

// Byte copies deliberately avoid forming a reference to a trailing member
// outside an older producer's allocation. Even a partially present pointer
// imports as unavailable; a producer never writes into a consumer's table.
template <typename Member>
inline Member readMember(const InterfaceHeader* table, size_t offset) noexcept {
    static_assert(std::is_trivially_copyable_v<Member>);
    Member value{};
    if (table && offset <= table->structSize
        && sizeof(Member) <= table->structSize - offset) {
        std::memcpy(&value, reinterpret_cast<const unsigned char*>(table) + offset,
                    sizeof(Member));
    }
    return value;
}

inline constexpr uint64_t kHostCoreId = 0x1001;
inline constexpr uint64_t kHostGameId = 0x1002;
inline constexpr uint64_t kHostActionsId = 0x1003;
inline constexpr uint64_t kHostRenderId = 0x1004;
inline constexpr uint64_t kHostNavigationId = 0x1005;
inline constexpr uint64_t kHostDefinitionExtrasId = 0x1006;
inline constexpr uint64_t kHostHtmlUiId = 0x1007;
inline constexpr uint64_t kPluginCoreId = 0x2001;
inline constexpr uint64_t kPluginUiId = 0x2002;
inline constexpr uint64_t kPluginEventsId = 0x2003;
inline constexpr uint64_t kPluginHtmlPanelsId = 0x2004;
inline constexpr uint64_t kPluginHtmlOverlaysId = 0x2005;

// HTML is additive. Absence of either optional side never rejects a plugin.
// Each interface has one required prefix, imported atomically. Future tail
// extensions require independent size checks; records above never grow.
struct HostHtmlUiV1 {
    InterfaceHeader header{sizeof(HostHtmlUiV1), 1};
    uint32_t (*capabilities)() = nullptr;
};
struct PluginHtmlPanelsV1 {
    InterfaceHeader header{sizeof(PluginHtmlPanelsV1), 1};
    HtmlUiCallbacks callbacks{};
};
struct PluginHtmlOverlaysV1 {
    InterfaceHeader header{sizeof(PluginHtmlOverlaysV1), 1};
    HtmlUiCallbacks callbacks{};
};
inline HtmlUiCallbacks importHtmlCallbacks(const InterfaceProviderV1* provider, uint64_t id) {
    const auto* table = queryInterface(provider, id);
    // This check must precede forming any reference to trailing fields: an
    // older provider may allocate only its physically short prefix.
    if (!table || table->structSize < sizeof(PluginHtmlPanelsV1)) return {};
    auto callbacks = readMember<HtmlUiCallbacks>(table, offsetof(PluginHtmlPanelsV1, callbacks));
    return callbacks.complete() ? callbacks : HtmlUiCallbacks{};
}
static_assert(std::is_standard_layout_v<HostHtmlUiV1>);
static_assert(std::is_standard_layout_v<PluginHtmlPanelsV1>);
static_assert(std::is_standard_layout_v<PluginHtmlOverlaysV1>);
static_assert(sizeof(HostHtmlUiV1) == 16);
static_assert(sizeof(PluginHtmlPanelsV1) == 88 && sizeof(PluginHtmlOverlaysV1) == 88);
static_assert(offsetof(PluginHtmlPanelsV1, callbacks) == 8 && offsetof(PluginHtmlOverlaysV1, callbacks) == 8);

// Separately negotiated optional data reads, added without a payload layout
// break. Since SDK 146 WidgetState::modelId carries the same model id as
// getWidgetModelIdAtPath, which remains for existing callers.
struct HostDefinitionExtrasV1 {
    InterfaceHeader header{sizeof(HostDefinitionExtrasV1), 1};
    uint8_t (*getWidgetModelIdAtPath)(const WidgetAddressState* address,
                                     int32_t* outModelId) = nullptr;
    uint8_t (*copyItemWornAction)(int32_t itemId, uint32_t actionIndex,
                                 char* outUtf8, uint32_t capacity,
                                 uint32_t* outRequired) = nullptr;
    uint8_t (*getNpcBaseId)(int32_t worldViewId, int32_t hashIndex,
                           int32_t* outBaseId) = nullptr;
};
static_assert(std::is_standard_layout_v<HostDefinitionExtrasV1>);
static_assert(sizeof(HostDefinitionExtrasV1) == 32);
static_assert(offsetof(HostDefinitionExtrasV1, header) == 0);
static_assert(offsetof(HostDefinitionExtrasV1, getWidgetModelIdAtPath) == 8);
static_assert(offsetof(HostDefinitionExtrasV1, copyItemWornAction) == 16);
static_assert(offsetof(HostDefinitionExtrasV1, getNpcBaseId) == 24);

struct HostCoreV1 {
    InterfaceHeader header{sizeof(HostCoreV1), 1};
    void (*log)(const char* msg) = nullptr;
    void (*setInternalToolVisible)(const char* toolId, uint8_t visible) = nullptr;
    uint8_t (*getInternalToolVisible)(const char* toolId) = nullptr;
    uint32_t (*listPlugins)(PluginInfo* out, uint32_t max) = nullptr;
    uint8_t (*getPlugin)(const char* pluginId, PluginInfo* out) = nullptr;
    uint8_t (*setPluginEnabled)(const char* pluginId, uint8_t enabled) = nullptr;
    int8_t (*isPluginEnabled)(const char* pluginId) = nullptr;
    uint32_t (*getPluginCount)() = nullptr;
    uint8_t (*breakHandlerRegisterPlugin)( const void* pluginInstance, const BreakRegistrationState* registration) = nullptr;
    uint8_t (*breakHandlerStart)(const void* pluginInstance, const char* pluginId) = nullptr;
    uint8_t (*breakHandlerStop)(const void* pluginInstance, const char* pluginId) = nullptr;
    uint8_t (*breakHandlerUnregisterPlugin)(const void* pluginInstance, const char* pluginId) = nullptr;
    uint8_t (*breakHandlerPoll)(const void* pluginInstance, const char* pluginId, BreakCommandState* outCommand) = nullptr;
    uint8_t (*breakHandlerReport)( const void* pluginInstance, const BreakReportState* report) = nullptr;
    uint32_t (*breakHandlerCoordinatorSnapshot)( const void* coordinatorInstance, const char* coordinatorId, BreakParticipantState* out, uint32_t capacity) = nullptr;
    uint8_t (*breakHandlerCoordinatorPublish)( const void* coordinatorInstance, const char* coordinatorId, const BreakCommandState* command) = nullptr;
    uint8_t (*breakHandlerCoordinatorClear)( const void* coordinatorInstance, const char* coordinatorId, uint64_t expectedEpoch) = nullptr;
    void (*markSettingChanged)(const char* pluginId, const char* settingKey, const TitanNativeRecords::Value* value, const uint8_t* hidden) = nullptr;
    uint8_t (*crossTabWrite)(const void* pluginInstance, const char* pluginId, const CrossTabWrite* write, uint64_t* outWriteId) = nullptr;
    uint32_t (*crossTabRead)(const void* pluginInstance, const char* pluginId, const char* key, uint8_t* out, uint32_t capacity, CrossTabEntryInfo* info) = nullptr;
    uint8_t (*previewPillWrite)(const void* pluginInstance, const char* pluginId, const PreviewPillWrite* write) = nullptr;
    uint8_t (*breakHandlerObserve)(const void* pluginInstance, const char* pluginId, BreakCommandState* outCommand) = nullptr;
    // Only permanent host-resident tables; never the plugin service registry.
    void* (*getHostService)(const char* serviceId) = nullptr;
    // The host's source SDK release (SDK 146+). An SDK-built plugin refuses a
    // host whose release is below its kMinSupportedSdkVersion; a table too
    // short to hold this field means a host older than SDK 146.
    uint32_t sdkRelease = kSdkVersion;
};
static_assert(std::is_standard_layout_v<HostCoreV1>);
static_assert(offsetof(HostCoreV1, header) == 0);

struct HostGameV1 {
    InterfaceHeader header{sizeof(HostGameV1), 1};
    uint8_t (*isPrayerActive)(int32_t prayerOrdinal) = nullptr;
    uint8_t (*findNearestNpc)(int32_t npcIdOrNeg1, const char* nameOrNull, NpcState* outNpc) = nullptr;
    uint8_t (*findNearestObject)(int32_t locIdOrNeg1, const char* nameOrNull, TileObjectState* outObject) = nullptr;
    uint8_t (*containsInventoryItem)(int32_t itemId) = nullptr;
    bool (*isGrandExchangeAvailable)() = nullptr;
    uint8_t (*getClientState)(ClientState* outState) = nullptr;
    uint32_t (*getPlayers)(PlayerState* outPlayers, uint32_t maxPlayers) = nullptr;
    uint8_t (*getPlayerComposition)(uint64_t playerEntityPtr, PlayerCompositionState* outComposition) = nullptr;
    uint32_t (*getNpcs)(NpcState* outNpcs, uint32_t maxNpcs) = nullptr;
    uint32_t (*getTileObjects)(int32_t radius, TileObjectState* outObjects, uint32_t maxObjects) = nullptr;
    uint32_t (*getGroundItems)(int32_t radius, GroundItemState* outItems, uint32_t maxItems) = nullptr;
    uint32_t (*getTileObjectsOnTile)(int32_t plane, int32_t tileX, int32_t tileY, TileObjectState* outObjects, uint32_t maxObjects) = nullptr;
    uint32_t (*getGroundItemsOnTile)(int32_t plane, int32_t tileX, int32_t tileY, GroundItemState* outItems, uint32_t maxItems) = nullptr;
    uint32_t (*getProjectiles)(ProjectileState* outProjectiles, uint32_t maxProjectiles) = nullptr;
    uint32_t (*getGraphicsObjects)(GraphicsObjectState* outGraphicsObjects, uint32_t maxGraphicsObjects) = nullptr;
    uint8_t (*getCameraState)(CameraState* outState) = nullptr;
    uint8_t (*getMousePos)(int32_t* outX, int32_t* outY) = nullptr;
    int32_t (*getVarbit)(int32_t varbitId) = nullptr;
    int32_t (*getVarp)(int32_t varpId) = nullptr;
    int32_t (*getBoostedSkillLevel)(int32_t skillId) = nullptr;
    int32_t (*getRealSkillLevel)(int32_t skillId) = nullptr;
    int32_t (*getSkillExperience)(int32_t skillId) = nullptr;
    uint8_t (*getInteracting)(int32_t interactingIndex, uint8_t interactingType, PlayerState* outPlayer, NpcState* outNpc) = nullptr;
    uint8_t (*getItemDef)(int32_t id, ItemDefSnapshot* out) = nullptr;
    uint8_t (*getNpcDef)(int32_t id, NpcDefSnapshot* out) = nullptr;
    uint8_t (*getObjDef)(int32_t id, ObjDefSnapshot* out) = nullptr;
    uint8_t (*getVarbitDef)(int32_t id, VarbitDefSnapshot* out) = nullptr;
    uint8_t (*getCurrentWorld)(int32_t* outWorld) = nullptr;
    uint32_t (*getWorldList)(WorldState* out, uint32_t cap) = nullptr;
    uint32_t (*getInventoryItems)(InventoryItemState* out, uint32_t max) = nullptr;
    int32_t (*getQuestState)(int32_t questId) = nullptr;
    uint8_t (*getLocalPlayer)(PlayerState* outPlayer) = nullptr;
    int32_t (*getIdleTimeRemaining)() = nullptr;
    uint8_t (*getWidget)(uint32_t packedId, WidgetState* outState) = nullptr;
    uint8_t (*getItemContainer)(int32_t containerId, ItemContainerState* outState) = nullptr;
    uint8_t (*getItemComposition)(int32_t itemId, ItemCompositionState* outState) = nullptr;
    uint32_t (*getWidgetChildren)(uint32_t parentPackedId, WidgetState* outStates, uint32_t maxOut) = nullptr;
    uint8_t (*getWidgetByText)(const char* query, WidgetState* outState) = nullptr;
    uint32_t (*getActorPathQueue)(uint64_t entityPtr, WorldPointState* out, uint32_t max) = nullptr;
    uint8_t (*getVarClientInt)(int32_t id, int32_t* outValue) = nullptr;
    uint32_t (*getVarClientString)(int32_t id, char* out, uint32_t capacity) = nullptr;
    uint8_t (*getVarClientLong)(int32_t id, int64_t* outValue) = nullptr;
    uint32_t (*getWidgets)(uint32_t groupId, WidgetQueryState* outStates, uint32_t maxOut, uint8_t* outTruncated) = nullptr;
    uint32_t (*getWidgetChildrenAtPath)(const WidgetAddressState* parent, WidgetQueryState* outStates, uint32_t maxOut) = nullptr;
    uint32_t (*getActorSpotAnims)(uint64_t entityPtr, ActorSpotAnimState* out, uint32_t max) = nullptr;
    uint8_t (*getWorldViewById)(int32_t worldViewId, uint64_t* outPtr) = nullptr;
    uint8_t (*getTopLevelWorldView)(uint64_t* outPtr) = nullptr;
    uint32_t (*getWorldMetadata)(WorldMetadataState* out, uint32_t cap) = nullptr;
    uint8_t (*getPlayerByIndexInWorldView)(int32_t hashIndex, int32_t worldViewId, PlayerState* outPlayer) = nullptr;
    uint8_t (*getNpcByIndexInWorldView)(int32_t hashIndex, int32_t worldViewId, NpcState* outNpc) = nullptr;
    uint8_t (*getWidgetAtPath)(const WidgetAddressState* address, WidgetState* outState) = nullptr;
    uint32_t (*getActorPathQueueInWorldView)(uint64_t entityPtr, int32_t worldViewId, WorldPointState* out, uint32_t max) = nullptr;
    uint32_t (*getTileObjectsOnTileInWorldView)( int32_t worldViewId, int32_t plane, int32_t tileX, int32_t tileY, TileObjectState* outObjects, uint32_t maxObjects) = nullptr;
    uint8_t (*getWorldMapState)(WorldMapState* outState) = nullptr;
    int32_t (*getLiveStateEpoch)() = nullptr;
    int32_t (*getGameCycle)() = nullptr;
    uint8_t (*getActorOverheadText)(uint64_t entityPtr, char* out, uint64_t capacity, uint64_t* outLength) = nullptr;
    uint8_t (*getActorOverheadTextCyclesRemaining)(uint64_t entityPtr, int32_t* out) = nullptr;
    uint32_t (*getOverheadTextCapabilities)() = nullptr;
    bool (*getGrandExchangeOffer)(int32_t slot, GrandExchangeOffer* out) = nullptr;
    int32_t (*getGrandExchangeOffers)(GrandExchangeOffer* out, int32_t capacity) = nullptr;
    bool (*getItemPriceStatus)(ItemPriceStatus* out) = nullptr;
    bool (*getItemPriceMetadata)(int32_t id, ItemPriceMetadata* out) = nullptr;
    int32_t (*getItemPriceItemIds)(int32_t* out, int32_t capacity) = nullptr;
    bool (*getItemPrice)(int32_t id, ItemPrice* out) = nullptr;
    bool (*getItemCacheBank)(BankCacheState* out) = nullptr;
    uint32_t (*getCurrentSceneTileObjects)(TileObjectState* out, uint32_t capacity) = nullptr;
    // SDK 148 optional tail; older tables import these operations as null.
    uint32_t (*getHintArrows)(HintArrowState* out, uint32_t capacity) = nullptr;
    uint8_t (*getServerHintArrow)(HintArrowState* out) = nullptr;
    // SDK 149 optional tail; HintArrowState retains its original layout.
    uint8_t (*getHintArrowWorldPoint)(const HintArrowState* expected, WorldPointState* out) = nullptr;
};
static_assert(std::is_standard_layout_v<HostGameV1>);
static_assert(offsetof(HostGameV1, header) == 0);

struct HostActionsV1 {
    InterfaceHeader header{sizeof(HostActionsV1), 1};
    uint8_t (*executeSyntheticAction)(uint32_t opcode, int32_t identifier, int32_t param0, int32_t param1, int32_t worldViewId, int32_t clickX, int32_t clickY, const char* actionText, const char* targetText, uint8_t skipClick) = nullptr;
    uint8_t (*executeSyntheticEntry)(const SyntheticActionEntry* entry) = nullptr;
    uint8_t (*interactNpc)(const char* action, int32_t npcIdOrNeg1, const char* nameOrNull) = nullptr;
    uint8_t (*interactNpcByIndex)(const char* action, int32_t hashIndex) = nullptr;
    uint8_t (*interactObject)(const char* action, int32_t locIdOrNeg1, const char* nameOrNull) = nullptr;
    uint8_t (*interactGroundItem)(const char* action, int32_t itemId, int32_t tileX, int32_t tileY) = nullptr;
    uint8_t (*hopToWorldId)(int32_t worldId) = nullptr;
    uint8_t (*hopToListIndex)(uint32_t idx) = nullptr;
    uint8_t (*hopToWorldIngame)(int32_t worldId) = nullptr;
    uint8_t (*interactInventoryItem)(int32_t itemId, const char* action) = nullptr;
    uint8_t (*runClientScript)(int32_t scriptId, const int32_t* intArgs, uint32_t intArgCount, Cs2ScriptResult* outResult) = nullptr;
    void (*resetIdleTimer)() = nullptr;
    uint8_t (*getLoginAccountState)(LoginAccountState* out) = nullptr;
    void (*setLoginUsername)(const char* username) = nullptr;
    void (*setLoginPassword)(const char* password) = nullptr;
    void (*setLoginAuthenticator)(const char* code) = nullptr;
    void (*setLoginIndex)(int32_t loginIndex) = nullptr;
    void (*setLoginDisplayName)(const char* displayName) = nullptr;
    void (*setLoginOAuth2Credentials)(const char* accessToken, const char* refreshToken) = nullptr;
    void (*setLoginGameSessionCredentials)(const char* sessionId, const char* characterId) = nullptr;
    void (*setLoginCharacter)(const char* displayName, const char* characterId, const char* sessionId) = nullptr;
    void (*resetLoginCharacter)() = nullptr;
    void (*addChatMessage)(int32_t type, const char* name, const char* message, const char* sender) = nullptr;
    uint8_t (*useInventoryItemOnItem)(int32_t srcSlot, int32_t srcItemId, int32_t tgtSlot, int32_t tgtItemId) = nullptr;
    uint8_t (*useInventoryItemOnNpc)(int32_t srcSlot, int32_t srcItemId, int32_t npcHashIndex) = nullptr;
    uint8_t (*useInventoryItemOnObject)(int32_t srcSlot, int32_t srcItemId, int32_t locId, int32_t tileX, int32_t tileY) = nullptr;
    uint8_t (*widgetInteract)(uint32_t opcode, int32_t identifier, int32_t param0, int32_t param1) = nullptr;
    uint8_t (*sendKeyboardString)(const char* utf8) = nullptr;
    uint8_t (*sendKeyboardKey)(int32_t key, uint32_t modMask) = nullptr;
    uint8_t (*runClientScriptTyped)(int32_t scriptId, const TitanHookArg* args, uint32_t argCount, Cs2ScriptResult* outResult) = nullptr;
    // Native ABI v1 reserves onDone/userData: callers must pass nullptr.
    // Completion callbacks need an owner-scoped scheduling extension.
    uint8_t (*typeKeyboardString)(const char* utf8, int32_t minDelayMs, int32_t maxDelayMs, int32_t callbackPhase, void (*onDone)(uint8_t completed, void* userData), void* userData) = nullptr;
    void (*cancelKeyboardType)() = nullptr;
    uint8_t (*isKeyboardTyping)() = nullptr;
    uint8_t (*setWidgetText)(uint32_t packedId, const char* text) = nullptr;
    uint8_t (*interactInventoryItemAtSlot)(int32_t slot, int32_t itemId, const char* action) = nullptr;
    uint8_t (*interactTileObject)(const char* action, const TileObjectState* object) = nullptr;
    uint8_t (*setVarClientInt)(int32_t id, int32_t value) = nullptr;
    uint8_t (*setVarClientString)(int32_t id, const char* value) = nullptr;
    uint8_t (*setVarClientLong)(int32_t id, int64_t value) = nullptr;
    uint8_t (*setWidgetTextAtSlot)(uint32_t parentPackedId, int32_t slot, const char* text) = nullptr;
    uint8_t (*setWidgetTextAtPath)(const WidgetAddressState* address, const char* text) = nullptr;
    uint8_t (*widgetInteractAtPath)(const WidgetAddressState* address, uint32_t opcode, int32_t identifier, int32_t childSlot) = nullptr;
    uint8_t (*refreshWorldMetadata)() = nullptr;
    uint8_t (*interactNpcByIndexInWorldView)(const char* action, int32_t hashIndex, int32_t worldViewId) = nullptr;
    uint8_t (*interactGroundItemInWorldView)(const char* action, int32_t itemId, int32_t tileX, int32_t tileY, int32_t worldViewId) = nullptr;
    uint8_t (*resolveActionClickPoint)(const ActionClickPointSpec* action, int32_t* screenX, int32_t* screenY) = nullptr;
    uint32_t (*listSanitizedProxyRoutes)(SanitizedProxyRouteState* out, uint32_t capacity) = nullptr;
    uint8_t (*setProxyRoute)(const char* proxyId, ProxyRouteStatusState* outStatus) = nullptr;
    uint8_t (*getProxyRouteStatus)(ProxyRouteStatusState* outStatus) = nullptr;
    uint8_t (*submitLoginLauncherCredentials)() = nullptr;
    uint8_t (*submitLoginStandardCredentials)() = nullptr;
    uint8_t (*acknowledgeStandardLogin)() = nullptr;
    uint8_t (*advanceLoginClickToPlay)() = nullptr;
    uint8_t (*getLoginFlowState)(LoginFlowState* out) = nullptr;
    uint8_t (*advanceLoginLauncherCredentials)() = nullptr;
    uint8_t (*advanceLoginLogout)() = nullptr;
    void (*cancelLoginProfileOperations)() = nullptr;
    void (*cancelLoginLogoutOperation)() = nullptr;
    uint8_t (*stageLoginCredentials)(const char* profileLabel) = nullptr;
    uint8_t (*submitLoginCredentials)() = nullptr;
    uint8_t (*executeSelectedActionPair)(const SelectedActionPair* pair) = nullptr;
    bool (*requestItemPriceCatalog)() = nullptr;
    bool (*requestItemPrice)(int32_t id) = nullptr;
    uint64_t (*geSubmitBuy)(const char* legacyUnused, const GeBuyOptions* options) = nullptr;
    bool (*geGetRequest)(const char* legacyUnused, uint64_t id, GeRequestState* out) = nullptr;
    int32_t (*geGetRequests)(const char* legacyUnused, GeRequestState* out, int32_t capacity) = nullptr;
    bool (*geCancelRequest)(const char* legacyUnused, uint64_t id) = nullptr;
    bool (*geReleaseRequest)(const char* legacyUnused, uint64_t id) = nullptr;
    // SDK 148 optional tail. Return HintArrowUpdateResult, synchronously.
    uint8_t (*setHintArrowCoordinate)(const HintArrowCoordinateTarget* target) = nullptr;
    uint8_t (*setHintArrowActor)(const HintArrowActorTarget* target) = nullptr;
    uint8_t (*clearHintArrow)() = nullptr;
};
static_assert(std::is_standard_layout_v<HostActionsV1>);
static_assert(offsetof(HostActionsV1, header) == 0);

struct HostRenderV1 {
    InterfaceHeader header{sizeof(HostRenderV1), 1};
    void (*setImGuiContext)(void* ctx) = nullptr;
    uint8_t (*worldToScreen)(int32_t worldX, int32_t worldY, int32_t worldZ, int32_t* screenX, int32_t* screenY) = nullptr;
    uint8_t (*tileToScreen)(int32_t tileX, int32_t tileY, int32_t plane, int32_t heightOffset, int32_t* screenX, int32_t* screenY) = nullptr;
    int32_t (*getTileHeight)(int32_t preciseX, int32_t preciseY, int32_t plane) = nullptr;
    void (*drawTileQuad)(int32_t tileX, int32_t tileY, int32_t plane, uint32_t fillColor, uint32_t outlineColor) = nullptr;
    void (*drawTileRegion)(int32_t minTileX, int32_t minTileY, int32_t maxTileX, int32_t maxTileY, int32_t plane, uint32_t fillColor, uint32_t outlineColor) = nullptr;
    void (*drawEntityBox)(int32_t preciseX, int32_t preciseY, int32_t plane, int32_t tileSize, int32_t height, uint32_t color) = nullptr;
    void (*drawTextAtWorld)(int32_t worldX, int32_t worldY, int32_t worldZ, const char* text, uint32_t color, uint8_t centered) = nullptr;
    void (*drawScreenText)(int32_t screenX, int32_t screenY, const char* text, uint32_t color) = nullptr;
    void (*drawScreenRect)(int32_t x, int32_t y, int32_t w, int32_t h, uint32_t color) = nullptr;
    void (*drawScreenLine)(int32_t x1, int32_t y1, int32_t x2, int32_t y2, uint32_t color, float thickness) = nullptr;
    void (*setEntityHidden)(uint8_t entityType, uint8_t hidden) = nullptr;
    uint8_t (*getEntityHidden)(uint8_t entityType) = nullptr;
    void (*drawEntityClickbox)(uint64_t entityPtr, uint64_t typecode, uint32_t outline, uint32_t fill) = nullptr;
    void (*drawTileObjectClickbox)(uint64_t locPtr, uint64_t typecode, uint32_t outline, uint32_t fill) = nullptr;
    void (*drawEntityHull)(uint64_t entityPtr, uint64_t typecode, uint32_t outline, uint32_t fill) = nullptr;
    void (*drawTileObjectHull)(uint64_t locPtr, uint64_t typecode, uint32_t outline, uint32_t fill) = nullptr;
    int32_t (*overlayPanelRegister)(const char* pluginId, const char* panelName, uint8_t defaultAnchor, int32_t defaultPriority) = nullptr;
    void (*overlayPanelUnregister)(int32_t handle) = nullptr;
    void (*overlayPanelBegin)(int32_t handle, int32_t preferredWidth) = nullptr;
    void (*overlayPanelEnd)(int32_t handle) = nullptr;
    void (*overlayPanelSetStyle)(int32_t handle, const OverlayPanelStyleAbi* style) = nullptr;
    void (*overlayPanelTitle)(int32_t handle, const char* text, uint32_t color) = nullptr;
    void (*overlayPanelLine)(int32_t handle, const char* left, const char* right, uint32_t leftColor, uint32_t rightColor) = nullptr;
    void (*overlayPanelProgressBar)(int32_t handle, int32_t value, int32_t minVal, int32_t maxVal, uint32_t fillColor, uint32_t bgColor) = nullptr;
    void (*setAudioPlaybackDisabled)(uint8_t disabled) = nullptr;
    uint8_t (*getAudioPlaybackDisabled)() = nullptr;
    uint8_t (*worldToScreenInWorldView)(int32_t worldViewId, int32_t preciseX, int32_t worldY, int32_t preciseY, int32_t plane, int32_t* screenX, int32_t* screenY) = nullptr;
    int32_t (*getTileHeightInWorldView)(int32_t worldViewId, int32_t preciseX, int32_t preciseY, int32_t plane) = nullptr;
    void (*drawTileQuadInWorldView)(int32_t worldViewId, int32_t tileX, int32_t tileY, int32_t plane, uint32_t fillColor, uint32_t outlineColor) = nullptr;
    void (*drawTileRegionInWorldView)(int32_t worldViewId, int32_t minTileX, int32_t minTileY, int32_t maxTileX, int32_t maxTileY, int32_t plane, uint32_t fillColor, uint32_t outlineColor) = nullptr;
    void (*drawTextAtWorldInWorldView)(int32_t worldViewId, int32_t preciseX, int32_t worldY, int32_t preciseY, int32_t plane, const char* text, uint32_t color, uint8_t centered) = nullptr;
    uint8_t (*getInterfaceScale)(float* outScaleX, float* outScaleY, int32_t* outCanvasOriginX, int32_t* outCanvasOriginY) = nullptr;
    void (*drawEntityOutline)(uint64_t entityPtr, uint64_t typecode, uint32_t outline, uint32_t fill, uint32_t mode) = nullptr;
    void (*drawTileObjectOutline)(uint64_t locPtr, uint64_t typecode, uint32_t outline, uint32_t fill, uint32_t mode) = nullptr;
    uint8_t (*screenshotSubmit)(uint64_t* outRequestId) = nullptr;
    uint8_t (*screenshotPoll)(uint64_t requestId, ScreenshotStatusState* outStatus) = nullptr;
    uint8_t (*screenshotCopyPng)(uint64_t requestId, uint8_t* out, uint32_t capacity, uint32_t* outRequired) = nullptr;
    uint8_t (*screenshotRelease)(uint64_t requestId) = nullptr;
};
static_assert(std::is_standard_layout_v<HostRenderV1>);
static_assert(offsetof(HostRenderV1, header) == 0);

struct HostNavigationV1 {
    InterfaceHeader header{sizeof(HostNavigationV1), 1};
    int32_t (*getCollisionFlag)(int32_t plane, int32_t tileX, int32_t tileY) = nullptr;
    uint8_t (*walkTo)(int32_t sceneX, int32_t sceneY) = nullptr;
    uint8_t (*walkToWorld)(int32_t worldX, int32_t worldY, int32_t plane) = nullptr;
    uint8_t (*getInstanceTemplateChunks)(InstanceTemplateChunksState* out) = nullptr;
    uint8_t (*worldPointFromLocalInstance)(const WorldPointState* in, WorldPointState* out) = nullptr;
    uint8_t (*worldPointToLocalInstance)(const WorldPointState* in, WorldPointState* out) = nullptr;
    uint8_t (*getLocalDestinationLocation)(LocalPointState* out) = nullptr;
    uint8_t (*getWorldDestinationLocation)(WorldPointState* out) = nullptr;
    uint8_t (*copyCachedCollisionRegion)(uint32_t regionId, int32_t* outFlags, uint32_t capacity, uint32_t* outCount) = nullptr;
    uint8_t (*copyCurrentCollisionScene)(CollisionSceneSnapshotState* outScene, int32_t* outFlags, uint32_t capacity, uint32_t* outCount) = nullptr;
    uint8_t (*webPathSubmit)(const WebPathRequestState* request, const WorldPointState* forbiddenTiles, uint32_t forbiddenTileCount, uint64_t* outRequestId) = nullptr;
    uint8_t (*webPathPoll)(uint64_t requestId, WebPathSummaryState* outSummary) = nullptr;
    uint8_t (*webPathCopySteps)(uint64_t requestId, WebPathStepState* outSteps, uint32_t capacity, uint32_t* outCount) = nullptr;
    uint8_t (*webPathCancel)(uint64_t requestId) = nullptr;
    uint8_t (*webPathRelease)(uint64_t requestId) = nullptr;
    uint8_t (*webWalkStart)(const WebWalkRequestState* request, const WorldPointState* forbiddenTiles, uint32_t forbiddenTileCount, uint64_t* outWalkId) = nullptr;
    uint8_t (*webWalkStatus)(uint64_t walkId, WebWalkStatusState* outStatus) = nullptr;
    uint8_t (*webWalkCancel)(uint64_t walkId) = nullptr;
    uint8_t (*webWalkRelease)(uint64_t walkId) = nullptr;
    uint8_t (*webWalkAdvance)(uint64_t walkId) = nullptr;
    uint8_t (*webPathCopyStepPayload)(uint64_t requestId, uint32_t stepIndex, char* outUtf8, uint32_t capacity, uint32_t* outRequired) = nullptr;
    uint8_t (*getCollisionSourceReady)(uint8_t* outReady) = nullptr;
};
static_assert(std::is_standard_layout_v<HostNavigationV1>);
static_assert(offsetof(HostNavigationV1, header) == 0);

struct PluginCoreV1 {
    InterfaceHeader header{sizeof(PluginCoreV1), 1};
    const char* (*getId)(void* userData) = nullptr;
    const char* (*getName)(void* userData) = nullptr;
    const char* (*getDescription)(void* userData) = nullptr;
    const char* (*getAuthor)(void* userData) = nullptr;
    const char* (*getVersion)(void* userData) = nullptr;
    uint8_t (*getDefaultEnabled)(void* userData) = nullptr;
    uint8_t (*getEnabled)(void* userData) = nullptr;
    void (*setEnabled)(void* userData, uint8_t enabled) = nullptr;
    uint32_t (*getSettings)(void* userData, TitanNativeRecords::Setting* outSettings, uint32_t maxSettings) = nullptr;
    uint8_t (*setSetting)(void* userData, const char* settingKey, const TitanNativeRecords::Value* value, char* errOut, uint32_t errOutLen) = nullptr;
    uint32_t (*getSections)(void* userData, TitanNativeRecords::Section* outSections, uint32_t maxSections) = nullptr;
    void (*onEnable)(void* userData) = nullptr;
    void (*onDisable)(void* userData) = nullptr;
    // Reserved, always null. Only PluginDescriptorV1::destroy owns deletion;
    // publishing the local PluginApi destructor here would permit double-free.
    void (*reservedDestroy)(void* userData) = nullptr;
    uint32_t (*getDependencies)(void* userData, char outIds[][TitanNativeRecords::kMaxIdLen], uint32_t maxIds) = nullptr;
    void (*prepareUnload)(void* userData) = nullptr;
};
static_assert(std::is_standard_layout_v<PluginCoreV1>);
static_assert(offsetof(PluginCoreV1, header) == 0);

struct PluginUiV1 {
    InterfaceHeader header{sizeof(PluginUiV1), 1};
    uint32_t (*getPanels)(void* userData, TitanNativeRecords::PanelDescriptor* outPanels, uint32_t maxPanels) = nullptr;
    void (*renderOverlay)(void* userData, uint8_t layer) = nullptr;
    uint32_t (*getPanelElements)(void* userData, const char* panelId, TitanNativeRecords::PanelElement* outElements, uint32_t maxElements) = nullptr;
    void (*onPanelAction)(void* userData, const char* panelId, int32_t actionId, const TitanNativeRecords::Value* value) = nullptr;
    uint32_t (*getPanelIcon)(void* userData, const char* panelId, uint8_t* outBytes, uint32_t maxBytes) = nullptr;
};
static_assert(std::is_standard_layout_v<PluginUiV1>);
static_assert(offsetof(PluginUiV1, header) == 0);

struct PluginEventsV1 {
    InterfaceHeader header{sizeof(PluginEventsV1), 1};
    void (*onGameTick)(void* userData, int32_t tickCount) = nullptr;
    void (*onClientTick)(void* userData) = nullptr;
    void (*onProjectileSpawned)(void* userData, const ProjectileState* proj) = nullptr;
    void (*onProjectileDespawned)(void* userData, const ProjectileState* proj) = nullptr;
    void (*onProjectileMoved)(void* userData, const ProjectileState* proj) = nullptr;
    void (*onGraphicsObjectSpawned)(void* userData, const GraphicsObjectState* obj) = nullptr;
    void (*onGraphicsObjectDespawned)(void* userData, const GraphicsObjectState* obj) = nullptr;
    void (*onGraphicsObjectMoved)(void* userData, const GraphicsObjectState* obj) = nullptr;
    void (*onNpcSpawned)(void* userData, const NpcState* npc) = nullptr;
    void (*onNpcDespawned)(void* userData, const NpcState* npc) = nullptr;
    void (*onPlayerSpawned)(void* userData, const PlayerState* player) = nullptr;
    void (*onPlayerDespawned)(void* userData, const PlayerState* player) = nullptr;
    void (*onTileObjectSpawned)(void* userData, const TileObjectState* obj) = nullptr;
    void (*onTileObjectDespawned)(void* userData, const TileObjectState* obj) = nullptr;
    void (*onMenuOptionClicked)(void* userData, MenuOptionClickedEvent* event) = nullptr;
    void (*onScriptFired)(void* userData, const ScriptFiredEvent* event) = nullptr;
    void (*onVarbitChanged)(void* userData, const VarbitChangedEvent* event) = nullptr;
    void (*onChatMessage)(void* userData, const ChatMessageEvent* event) = nullptr;
    void (*onItemContainerChanged)(void* userData, const ItemContainerChangedEvent* event) = nullptr;
    void (*onSettingChanged)(void* userData, const char* settingKey) = nullptr;
    void (*onSoundPlayed)(void* userData, SoundPlayedEvent* event) = nullptr;
    void (*onHitsplatApplied)(void* userData, const HitsplatAppliedEvent* event) = nullptr;
    void (*onActorSpotAnim)(void* userData, const ActorSpotAnimEvent* event) = nullptr;
    void (*onAnimationChanged)(void* userData, const AnimationChangedEvent* event) = nullptr;
    void (*onGameStateChanged)(void* userData, const GameStateChangedEvent* event) = nullptr;
    void (*onMousePressed)(void* userData, MouseButtonEvent* event) = nullptr;
    void (*onMouseReleased)(void* userData, MouseButtonEvent* event) = nullptr;
    void (*onMainLoop)(void* userData) = nullptr;
    void (*onOverheadTextChanged)(void* userData, const OverheadTextChangedEvent* event) = nullptr;
    void (*onGrandExchangeOfferChanged)(void* userData, const GrandExchangeOfferChangedEvent* event) = nullptr;
    void (*onCrossTabChanged)(void* userData, const CrossTabChangeEvent* event) = nullptr;
};
static_assert(std::is_standard_layout_v<PluginEventsV1>);
static_assert(offsetof(PluginEventsV1, header) == 0);

// Minimal mandatory bootstrap operations. Optional domain tables can be
// required at a larger fixed prefix through InterfaceRequirement.
inline constexpr uint32_t kHostCoreMinimumSize =
    static_cast<uint32_t>(offsetof(HostCoreV1, log) + sizeof(HostCoreV1::log));
inline constexpr uint32_t kPluginCoreMinimumSize =
    static_cast<uint32_t>(offsetof(PluginCoreV1, getName) + sizeof(PluginCoreV1::getName));
inline constexpr InterfaceRequirement kHostCoreRequirement{
    kHostCoreId, 1, kHostCoreMinimumSize};
inline constexpr InterfaceRequirement kPluginCoreRequirement{
    kPluginCoreId, 1, kPluginCoreMinimumSize};

// ABI v1 golden offsets: append only; never update an existing offset.
#if INTPTR_MAX == INT64_MAX
static_assert(sizeof(HostCoreV1) == 200);
static_assert(offsetof(HostCoreV1, log) == 8);
static_assert(offsetof(HostCoreV1, setInternalToolVisible) == 16);
static_assert(offsetof(HostCoreV1, getInternalToolVisible) == 24);
static_assert(offsetof(HostCoreV1, listPlugins) == 32);
static_assert(offsetof(HostCoreV1, getPlugin) == 40);
static_assert(offsetof(HostCoreV1, setPluginEnabled) == 48);
static_assert(offsetof(HostCoreV1, isPluginEnabled) == 56);
static_assert(offsetof(HostCoreV1, getPluginCount) == 64);
static_assert(offsetof(HostCoreV1, breakHandlerRegisterPlugin) == 72);
static_assert(offsetof(HostCoreV1, breakHandlerStart) == 80);
static_assert(offsetof(HostCoreV1, breakHandlerStop) == 88);
static_assert(offsetof(HostCoreV1, breakHandlerUnregisterPlugin) == 96);
static_assert(offsetof(HostCoreV1, breakHandlerPoll) == 104);
static_assert(offsetof(HostCoreV1, breakHandlerReport) == 112);
static_assert(offsetof(HostCoreV1, breakHandlerCoordinatorSnapshot) == 120);
static_assert(offsetof(HostCoreV1, breakHandlerCoordinatorPublish) == 128);
static_assert(offsetof(HostCoreV1, breakHandlerCoordinatorClear) == 136);
static_assert(offsetof(HostCoreV1, markSettingChanged) == 144);
static_assert(offsetof(HostCoreV1, crossTabWrite) == 152);
static_assert(offsetof(HostCoreV1, crossTabRead) == 160);
static_assert(offsetof(HostCoreV1, previewPillWrite) == 168);
static_assert(offsetof(HostCoreV1, breakHandlerObserve) == 176);
static_assert(offsetof(HostCoreV1, getHostService) == 184);
static_assert(offsetof(HostCoreV1, sdkRelease) == 192);
#endif

// ABI v1 golden offsets: append only; never update an existing offset.
#if INTPTR_MAX == INT64_MAX
static_assert(sizeof(HostGameV1) == 568);
static_assert(offsetof(HostGameV1, isPrayerActive) == 8);
static_assert(offsetof(HostGameV1, findNearestNpc) == 16);
static_assert(offsetof(HostGameV1, findNearestObject) == 24);
static_assert(offsetof(HostGameV1, containsInventoryItem) == 32);
static_assert(offsetof(HostGameV1, isGrandExchangeAvailable) == 40);
static_assert(offsetof(HostGameV1, getClientState) == 48);
static_assert(offsetof(HostGameV1, getPlayers) == 56);
static_assert(offsetof(HostGameV1, getPlayerComposition) == 64);
static_assert(offsetof(HostGameV1, getNpcs) == 72);
static_assert(offsetof(HostGameV1, getTileObjects) == 80);
static_assert(offsetof(HostGameV1, getGroundItems) == 88);
static_assert(offsetof(HostGameV1, getTileObjectsOnTile) == 96);
static_assert(offsetof(HostGameV1, getGroundItemsOnTile) == 104);
static_assert(offsetof(HostGameV1, getProjectiles) == 112);
static_assert(offsetof(HostGameV1, getGraphicsObjects) == 120);
static_assert(offsetof(HostGameV1, getCameraState) == 128);
static_assert(offsetof(HostGameV1, getMousePos) == 136);
static_assert(offsetof(HostGameV1, getVarbit) == 144);
static_assert(offsetof(HostGameV1, getVarp) == 152);
static_assert(offsetof(HostGameV1, getBoostedSkillLevel) == 160);
static_assert(offsetof(HostGameV1, getRealSkillLevel) == 168);
static_assert(offsetof(HostGameV1, getSkillExperience) == 176);
static_assert(offsetof(HostGameV1, getInteracting) == 184);
static_assert(offsetof(HostGameV1, getItemDef) == 192);
static_assert(offsetof(HostGameV1, getNpcDef) == 200);
static_assert(offsetof(HostGameV1, getObjDef) == 208);
static_assert(offsetof(HostGameV1, getVarbitDef) == 216);
static_assert(offsetof(HostGameV1, getCurrentWorld) == 224);
static_assert(offsetof(HostGameV1, getWorldList) == 232);
static_assert(offsetof(HostGameV1, getInventoryItems) == 240);
static_assert(offsetof(HostGameV1, getQuestState) == 248);
static_assert(offsetof(HostGameV1, getLocalPlayer) == 256);
static_assert(offsetof(HostGameV1, getIdleTimeRemaining) == 264);
static_assert(offsetof(HostGameV1, getWidget) == 272);
static_assert(offsetof(HostGameV1, getItemContainer) == 280);
static_assert(offsetof(HostGameV1, getItemComposition) == 288);
static_assert(offsetof(HostGameV1, getWidgetChildren) == 296);
static_assert(offsetof(HostGameV1, getWidgetByText) == 304);
static_assert(offsetof(HostGameV1, getActorPathQueue) == 312);
static_assert(offsetof(HostGameV1, getVarClientInt) == 320);
static_assert(offsetof(HostGameV1, getVarClientString) == 328);
static_assert(offsetof(HostGameV1, getVarClientLong) == 336);
static_assert(offsetof(HostGameV1, getWidgets) == 344);
static_assert(offsetof(HostGameV1, getWidgetChildrenAtPath) == 352);
static_assert(offsetof(HostGameV1, getActorSpotAnims) == 360);
static_assert(offsetof(HostGameV1, getWorldViewById) == 368);
static_assert(offsetof(HostGameV1, getTopLevelWorldView) == 376);
static_assert(offsetof(HostGameV1, getWorldMetadata) == 384);
static_assert(offsetof(HostGameV1, getPlayerByIndexInWorldView) == 392);
static_assert(offsetof(HostGameV1, getNpcByIndexInWorldView) == 400);
static_assert(offsetof(HostGameV1, getWidgetAtPath) == 408);
static_assert(offsetof(HostGameV1, getActorPathQueueInWorldView) == 416);
static_assert(offsetof(HostGameV1, getTileObjectsOnTileInWorldView) == 424);
static_assert(offsetof(HostGameV1, getWorldMapState) == 432);
static_assert(offsetof(HostGameV1, getLiveStateEpoch) == 440);
static_assert(offsetof(HostGameV1, getGameCycle) == 448);
static_assert(offsetof(HostGameV1, getActorOverheadText) == 456);
static_assert(offsetof(HostGameV1, getActorOverheadTextCyclesRemaining) == 464);
static_assert(offsetof(HostGameV1, getOverheadTextCapabilities) == 472);
static_assert(offsetof(HostGameV1, getGrandExchangeOffer) == 480);
static_assert(offsetof(HostGameV1, getGrandExchangeOffers) == 488);
static_assert(offsetof(HostGameV1, getItemPriceStatus) == 496);
static_assert(offsetof(HostGameV1, getItemPriceMetadata) == 504);
static_assert(offsetof(HostGameV1, getItemPriceItemIds) == 512);
static_assert(offsetof(HostGameV1, getItemPrice) == 520);
static_assert(offsetof(HostGameV1, getItemCacheBank) == 528);
static_assert(offsetof(HostGameV1, getCurrentSceneTileObjects) == 536);
static_assert(offsetof(HostGameV1, getHintArrows) == 544);
static_assert(offsetof(HostGameV1, getServerHintArrow) == 552);
static_assert(offsetof(HostGameV1, getHintArrowWorldPoint) == 560);
static_assert(sizeof(HintArrowState) == 72);
static_assert(offsetof(HintArrowState, actorWorldViewId) == 48);
static_assert(offsetof(HintArrowState, actorEntityPtr) == 56);
static_assert(offsetof(HintArrowState, actorWorldViewPtr) == 64);
#endif

// ABI v1 golden offsets: append only; never update an existing offset.
#if INTPTR_MAX == INT64_MAX
static_assert(sizeof(HostActionsV1) == 576);
static_assert(offsetof(HostActionsV1, executeSyntheticAction) == 8);
static_assert(offsetof(HostActionsV1, executeSyntheticEntry) == 16);
static_assert(offsetof(HostActionsV1, interactNpc) == 24);
static_assert(offsetof(HostActionsV1, interactNpcByIndex) == 32);
static_assert(offsetof(HostActionsV1, interactObject) == 40);
static_assert(offsetof(HostActionsV1, interactGroundItem) == 48);
static_assert(offsetof(HostActionsV1, hopToWorldId) == 56);
static_assert(offsetof(HostActionsV1, hopToListIndex) == 64);
static_assert(offsetof(HostActionsV1, hopToWorldIngame) == 72);
static_assert(offsetof(HostActionsV1, interactInventoryItem) == 80);
static_assert(offsetof(HostActionsV1, runClientScript) == 88);
static_assert(offsetof(HostActionsV1, resetIdleTimer) == 96);
static_assert(offsetof(HostActionsV1, getLoginAccountState) == 104);
static_assert(offsetof(HostActionsV1, setLoginUsername) == 112);
static_assert(offsetof(HostActionsV1, setLoginPassword) == 120);
static_assert(offsetof(HostActionsV1, setLoginAuthenticator) == 128);
static_assert(offsetof(HostActionsV1, setLoginIndex) == 136);
static_assert(offsetof(HostActionsV1, setLoginDisplayName) == 144);
static_assert(offsetof(HostActionsV1, setLoginOAuth2Credentials) == 152);
static_assert(offsetof(HostActionsV1, setLoginGameSessionCredentials) == 160);
static_assert(offsetof(HostActionsV1, setLoginCharacter) == 168);
static_assert(offsetof(HostActionsV1, resetLoginCharacter) == 176);
static_assert(offsetof(HostActionsV1, addChatMessage) == 184);
static_assert(offsetof(HostActionsV1, useInventoryItemOnItem) == 192);
static_assert(offsetof(HostActionsV1, useInventoryItemOnNpc) == 200);
static_assert(offsetof(HostActionsV1, useInventoryItemOnObject) == 208);
static_assert(offsetof(HostActionsV1, widgetInteract) == 216);
static_assert(offsetof(HostActionsV1, sendKeyboardString) == 224);
static_assert(offsetof(HostActionsV1, sendKeyboardKey) == 232);
static_assert(offsetof(HostActionsV1, runClientScriptTyped) == 240);
static_assert(offsetof(HostActionsV1, typeKeyboardString) == 248);
static_assert(offsetof(HostActionsV1, cancelKeyboardType) == 256);
static_assert(offsetof(HostActionsV1, isKeyboardTyping) == 264);
static_assert(offsetof(HostActionsV1, setWidgetText) == 272);
static_assert(offsetof(HostActionsV1, interactInventoryItemAtSlot) == 280);
static_assert(offsetof(HostActionsV1, interactTileObject) == 288);
static_assert(offsetof(HostActionsV1, setVarClientInt) == 296);
static_assert(offsetof(HostActionsV1, setVarClientString) == 304);
static_assert(offsetof(HostActionsV1, setVarClientLong) == 312);
static_assert(offsetof(HostActionsV1, setWidgetTextAtSlot) == 320);
static_assert(offsetof(HostActionsV1, setWidgetTextAtPath) == 328);
static_assert(offsetof(HostActionsV1, widgetInteractAtPath) == 336);
static_assert(offsetof(HostActionsV1, refreshWorldMetadata) == 344);
static_assert(offsetof(HostActionsV1, interactNpcByIndexInWorldView) == 352);
static_assert(offsetof(HostActionsV1, interactGroundItemInWorldView) == 360);
static_assert(offsetof(HostActionsV1, resolveActionClickPoint) == 368);
static_assert(offsetof(HostActionsV1, listSanitizedProxyRoutes) == 376);
static_assert(offsetof(HostActionsV1, setProxyRoute) == 384);
static_assert(offsetof(HostActionsV1, getProxyRouteStatus) == 392);
static_assert(offsetof(HostActionsV1, submitLoginLauncherCredentials) == 400);
static_assert(offsetof(HostActionsV1, submitLoginStandardCredentials) == 408);
static_assert(offsetof(HostActionsV1, acknowledgeStandardLogin) == 416);
static_assert(offsetof(HostActionsV1, advanceLoginClickToPlay) == 424);
static_assert(offsetof(HostActionsV1, getLoginFlowState) == 432);
static_assert(offsetof(HostActionsV1, advanceLoginLauncherCredentials) == 440);
static_assert(offsetof(HostActionsV1, advanceLoginLogout) == 448);
static_assert(offsetof(HostActionsV1, cancelLoginProfileOperations) == 456);
static_assert(offsetof(HostActionsV1, cancelLoginLogoutOperation) == 464);
static_assert(offsetof(HostActionsV1, stageLoginCredentials) == 472);
static_assert(offsetof(HostActionsV1, submitLoginCredentials) == 480);
static_assert(offsetof(HostActionsV1, executeSelectedActionPair) == 488);
static_assert(offsetof(HostActionsV1, requestItemPriceCatalog) == 496);
static_assert(offsetof(HostActionsV1, requestItemPrice) == 504);
static_assert(offsetof(HostActionsV1, geSubmitBuy) == 512);
static_assert(offsetof(HostActionsV1, geGetRequest) == 520);
static_assert(offsetof(HostActionsV1, geGetRequests) == 528);
static_assert(offsetof(HostActionsV1, geCancelRequest) == 536);
static_assert(offsetof(HostActionsV1, geReleaseRequest) == 544);
static_assert(offsetof(HostActionsV1, setHintArrowCoordinate) == 552);
static_assert(offsetof(HostActionsV1, setHintArrowActor) == 560);
static_assert(offsetof(HostActionsV1, clearHintArrow) == 568);
static_assert(sizeof(HintArrowCoordinateTarget) == 28);
static_assert(sizeof(HintArrowActorTarget) == 32);
static_assert(offsetof(HintArrowActorTarget, entityPtr) == 16);
static_assert(offsetof(HintArrowActorTarget, worldViewPtr) == 24);
#endif

// ABI v1 golden offsets: append only; never update an existing offset.
#if INTPTR_MAX == INT64_MAX
static_assert(sizeof(HostRenderV1) == 320);
static_assert(offsetof(HostRenderV1, setImGuiContext) == 8);
static_assert(offsetof(HostRenderV1, worldToScreen) == 16);
static_assert(offsetof(HostRenderV1, tileToScreen) == 24);
static_assert(offsetof(HostRenderV1, getTileHeight) == 32);
static_assert(offsetof(HostRenderV1, drawTileQuad) == 40);
static_assert(offsetof(HostRenderV1, drawTileRegion) == 48);
static_assert(offsetof(HostRenderV1, drawEntityBox) == 56);
static_assert(offsetof(HostRenderV1, drawTextAtWorld) == 64);
static_assert(offsetof(HostRenderV1, drawScreenText) == 72);
static_assert(offsetof(HostRenderV1, drawScreenRect) == 80);
static_assert(offsetof(HostRenderV1, drawScreenLine) == 88);
static_assert(offsetof(HostRenderV1, setEntityHidden) == 96);
static_assert(offsetof(HostRenderV1, getEntityHidden) == 104);
static_assert(offsetof(HostRenderV1, drawEntityClickbox) == 112);
static_assert(offsetof(HostRenderV1, drawTileObjectClickbox) == 120);
static_assert(offsetof(HostRenderV1, drawEntityHull) == 128);
static_assert(offsetof(HostRenderV1, drawTileObjectHull) == 136);
static_assert(offsetof(HostRenderV1, overlayPanelRegister) == 144);
static_assert(offsetof(HostRenderV1, overlayPanelUnregister) == 152);
static_assert(offsetof(HostRenderV1, overlayPanelBegin) == 160);
static_assert(offsetof(HostRenderV1, overlayPanelEnd) == 168);
static_assert(offsetof(HostRenderV1, overlayPanelSetStyle) == 176);
static_assert(offsetof(HostRenderV1, overlayPanelTitle) == 184);
static_assert(offsetof(HostRenderV1, overlayPanelLine) == 192);
static_assert(offsetof(HostRenderV1, overlayPanelProgressBar) == 200);
static_assert(offsetof(HostRenderV1, setAudioPlaybackDisabled) == 208);
static_assert(offsetof(HostRenderV1, getAudioPlaybackDisabled) == 216);
static_assert(offsetof(HostRenderV1, worldToScreenInWorldView) == 224);
static_assert(offsetof(HostRenderV1, getTileHeightInWorldView) == 232);
static_assert(offsetof(HostRenderV1, drawTileQuadInWorldView) == 240);
static_assert(offsetof(HostRenderV1, drawTileRegionInWorldView) == 248);
static_assert(offsetof(HostRenderV1, drawTextAtWorldInWorldView) == 256);
static_assert(offsetof(HostRenderV1, getInterfaceScale) == 264);
static_assert(offsetof(HostRenderV1, drawEntityOutline) == 272);
static_assert(offsetof(HostRenderV1, drawTileObjectOutline) == 280);
static_assert(offsetof(HostRenderV1, screenshotSubmit) == 288);
static_assert(offsetof(HostRenderV1, screenshotPoll) == 296);
static_assert(offsetof(HostRenderV1, screenshotCopyPng) == 304);
static_assert(offsetof(HostRenderV1, screenshotRelease) == 312);
#endif

// ABI v1 golden offsets: append only; never update an existing offset.
#if INTPTR_MAX == INT64_MAX
static_assert(sizeof(HostNavigationV1) == 184);
static_assert(offsetof(HostNavigationV1, getCollisionFlag) == 8);
static_assert(offsetof(HostNavigationV1, walkTo) == 16);
static_assert(offsetof(HostNavigationV1, walkToWorld) == 24);
static_assert(offsetof(HostNavigationV1, getInstanceTemplateChunks) == 32);
static_assert(offsetof(HostNavigationV1, worldPointFromLocalInstance) == 40);
static_assert(offsetof(HostNavigationV1, worldPointToLocalInstance) == 48);
static_assert(offsetof(HostNavigationV1, getLocalDestinationLocation) == 56);
static_assert(offsetof(HostNavigationV1, getWorldDestinationLocation) == 64);
static_assert(offsetof(HostNavigationV1, copyCachedCollisionRegion) == 72);
static_assert(offsetof(HostNavigationV1, copyCurrentCollisionScene) == 80);
static_assert(offsetof(HostNavigationV1, webPathSubmit) == 88);
static_assert(offsetof(HostNavigationV1, webPathPoll) == 96);
static_assert(offsetof(HostNavigationV1, webPathCopySteps) == 104);
static_assert(offsetof(HostNavigationV1, webPathCancel) == 112);
static_assert(offsetof(HostNavigationV1, webPathRelease) == 120);
static_assert(offsetof(HostNavigationV1, webWalkStart) == 128);
static_assert(offsetof(HostNavigationV1, webWalkStatus) == 136);
static_assert(offsetof(HostNavigationV1, webWalkCancel) == 144);
static_assert(offsetof(HostNavigationV1, webWalkRelease) == 152);
static_assert(offsetof(HostNavigationV1, webWalkAdvance) == 160);
static_assert(offsetof(HostNavigationV1, webPathCopyStepPayload) == 168);
static_assert(offsetof(HostNavigationV1, getCollisionSourceReady) == 176);
#endif

// ABI v1 golden offsets: append only; never update an existing offset.
#if INTPTR_MAX == INT64_MAX
static_assert(sizeof(PluginCoreV1) == 136);
static_assert(offsetof(PluginCoreV1, getId) == 8);
static_assert(offsetof(PluginCoreV1, getName) == 16);
static_assert(offsetof(PluginCoreV1, getDescription) == 24);
static_assert(offsetof(PluginCoreV1, getAuthor) == 32);
static_assert(offsetof(PluginCoreV1, getVersion) == 40);
static_assert(offsetof(PluginCoreV1, getDefaultEnabled) == 48);
static_assert(offsetof(PluginCoreV1, getEnabled) == 56);
static_assert(offsetof(PluginCoreV1, setEnabled) == 64);
static_assert(offsetof(PluginCoreV1, getSettings) == 72);
static_assert(offsetof(PluginCoreV1, setSetting) == 80);
static_assert(offsetof(PluginCoreV1, getSections) == 88);
static_assert(offsetof(PluginCoreV1, onEnable) == 96);
static_assert(offsetof(PluginCoreV1, onDisable) == 104);
static_assert(offsetof(PluginCoreV1, reservedDestroy) == 112);
static_assert(offsetof(PluginCoreV1, getDependencies) == 120);
static_assert(offsetof(PluginCoreV1, prepareUnload) == 128);
#endif

// ABI v1 golden offsets: append only; never update an existing offset.
#if INTPTR_MAX == INT64_MAX
static_assert(sizeof(PluginUiV1) == 48);
static_assert(offsetof(PluginUiV1, getPanels) == 8);
static_assert(offsetof(PluginUiV1, renderOverlay) == 16);
static_assert(offsetof(PluginUiV1, getPanelElements) == 24);
static_assert(offsetof(PluginUiV1, onPanelAction) == 32);
static_assert(offsetof(PluginUiV1, getPanelIcon) == 40);
#endif

// ABI v1 golden offsets: append only; never update an existing offset.
#if INTPTR_MAX == INT64_MAX
static_assert(sizeof(PluginEventsV1) == 256);
static_assert(offsetof(PluginEventsV1, onGameTick) == 8);
static_assert(offsetof(PluginEventsV1, onClientTick) == 16);
static_assert(offsetof(PluginEventsV1, onProjectileSpawned) == 24);
static_assert(offsetof(PluginEventsV1, onProjectileDespawned) == 32);
static_assert(offsetof(PluginEventsV1, onProjectileMoved) == 40);
static_assert(offsetof(PluginEventsV1, onGraphicsObjectSpawned) == 48);
static_assert(offsetof(PluginEventsV1, onGraphicsObjectDespawned) == 56);
static_assert(offsetof(PluginEventsV1, onGraphicsObjectMoved) == 64);
static_assert(offsetof(PluginEventsV1, onNpcSpawned) == 72);
static_assert(offsetof(PluginEventsV1, onNpcDespawned) == 80);
static_assert(offsetof(PluginEventsV1, onPlayerSpawned) == 88);
static_assert(offsetof(PluginEventsV1, onPlayerDespawned) == 96);
static_assert(offsetof(PluginEventsV1, onTileObjectSpawned) == 104);
static_assert(offsetof(PluginEventsV1, onTileObjectDespawned) == 112);
static_assert(offsetof(PluginEventsV1, onMenuOptionClicked) == 120);
static_assert(offsetof(PluginEventsV1, onScriptFired) == 128);
static_assert(offsetof(PluginEventsV1, onVarbitChanged) == 136);
static_assert(offsetof(PluginEventsV1, onChatMessage) == 144);
static_assert(offsetof(PluginEventsV1, onItemContainerChanged) == 152);
static_assert(offsetof(PluginEventsV1, onSettingChanged) == 160);
static_assert(offsetof(PluginEventsV1, onSoundPlayed) == 168);
static_assert(offsetof(PluginEventsV1, onHitsplatApplied) == 176);
static_assert(offsetof(PluginEventsV1, onActorSpotAnim) == 184);
static_assert(offsetof(PluginEventsV1, onAnimationChanged) == 192);
static_assert(offsetof(PluginEventsV1, onGameStateChanged) == 200);
static_assert(offsetof(PluginEventsV1, onMousePressed) == 208);
static_assert(offsetof(PluginEventsV1, onMouseReleased) == 216);
static_assert(offsetof(PluginEventsV1, onMainLoop) == 224);
static_assert(offsetof(PluginEventsV1, onOverheadTextChanged) == 232);
static_assert(offsetof(PluginEventsV1, onGrandExchangeOfferChanged) == 240);
static_assert(offsetof(PluginEventsV1, onCrossTabChanged) == 248);
#endif

#define TITAN_NATIVE_HOSTCOREV1_MEMBERS(X) \
    X(log) \
    X(setInternalToolVisible) \
    X(getInternalToolVisible) \
    X(listPlugins) \
    X(getPlugin) \
    X(setPluginEnabled) \
    X(isPluginEnabled) \
    X(getPluginCount) \
    X(breakHandlerRegisterPlugin) \
    X(breakHandlerStart) \
    X(breakHandlerStop) \
    X(breakHandlerUnregisterPlugin) \
    X(breakHandlerPoll) \
    X(breakHandlerReport) \
    X(breakHandlerCoordinatorSnapshot) \
    X(breakHandlerCoordinatorPublish) \
    X(breakHandlerCoordinatorClear) \
    X(markSettingChanged) \
    X(crossTabWrite) \
    X(crossTabRead) \
    X(previewPillWrite) \
    X(breakHandlerObserve) \
    X(getHostService)

#define TITAN_NATIVE_HOSTGAMEV1_MEMBERS(X) \
    X(isPrayerActive) \
    X(findNearestNpc) \
    X(findNearestObject) \
    X(containsInventoryItem) \
    X(isGrandExchangeAvailable) \
    X(getClientState) \
    X(getPlayers) \
    X(getPlayerComposition) \
    X(getNpcs) \
    X(getTileObjects) \
    X(getGroundItems) \
    X(getTileObjectsOnTile) \
    X(getGroundItemsOnTile) \
    X(getProjectiles) \
    X(getGraphicsObjects) \
    X(getCameraState) \
    X(getMousePos) \
    X(getVarbit) \
    X(getVarp) \
    X(getBoostedSkillLevel) \
    X(getRealSkillLevel) \
    X(getSkillExperience) \
    X(getInteracting) \
    X(getItemDef) \
    X(getNpcDef) \
    X(getObjDef) \
    X(getVarbitDef) \
    X(getCurrentWorld) \
    X(getWorldList) \
    X(getInventoryItems) \
    X(getQuestState) \
    X(getLocalPlayer) \
    X(getIdleTimeRemaining) \
    X(getWidget) \
    X(getItemContainer) \
    X(getItemComposition) \
    X(getWidgetChildren) \
    X(getWidgetByText) \
    X(getActorPathQueue) \
    X(getVarClientInt) \
    X(getVarClientString) \
    X(getVarClientLong) \
    X(getWidgets) \
    X(getWidgetChildrenAtPath) \
    X(getActorSpotAnims) \
    X(getWorldViewById) \
    X(getTopLevelWorldView) \
    X(getWorldMetadata) \
    X(getPlayerByIndexInWorldView) \
    X(getNpcByIndexInWorldView) \
    X(getWidgetAtPath) \
    X(getActorPathQueueInWorldView) \
    X(getTileObjectsOnTileInWorldView) \
    X(getWorldMapState) \
    X(getLiveStateEpoch) \
    X(getGameCycle) \
    X(getActorOverheadText) \
    X(getActorOverheadTextCyclesRemaining) \
    X(getOverheadTextCapabilities) \
    X(getGrandExchangeOffer) \
    X(getGrandExchangeOffers) \
    X(getItemPriceStatus) \
    X(getItemPriceMetadata) \
    X(getItemPriceItemIds) \
    X(getItemPrice) \
    X(getItemCacheBank) \
    X(getCurrentSceneTileObjects) \
    X(getHintArrows) \
    X(getServerHintArrow) \
    X(getHintArrowWorldPoint)

#define TITAN_NATIVE_HOSTACTIONSV1_MEMBERS(X) \
    X(executeSyntheticAction) \
    X(executeSyntheticEntry) \
    X(interactNpc) \
    X(interactNpcByIndex) \
    X(interactObject) \
    X(interactGroundItem) \
    X(hopToWorldId) \
    X(hopToListIndex) \
    X(hopToWorldIngame) \
    X(interactInventoryItem) \
    X(runClientScript) \
    X(resetIdleTimer) \
    X(getLoginAccountState) \
    X(setLoginUsername) \
    X(setLoginPassword) \
    X(setLoginAuthenticator) \
    X(setLoginIndex) \
    X(setLoginDisplayName) \
    X(setLoginOAuth2Credentials) \
    X(setLoginGameSessionCredentials) \
    X(setLoginCharacter) \
    X(resetLoginCharacter) \
    X(addChatMessage) \
    X(useInventoryItemOnItem) \
    X(useInventoryItemOnNpc) \
    X(useInventoryItemOnObject) \
    X(widgetInteract) \
    X(sendKeyboardString) \
    X(sendKeyboardKey) \
    X(runClientScriptTyped) \
    X(typeKeyboardString) \
    X(cancelKeyboardType) \
    X(isKeyboardTyping) \
    X(setWidgetText) \
    X(interactInventoryItemAtSlot) \
    X(interactTileObject) \
    X(setVarClientInt) \
    X(setVarClientString) \
    X(setVarClientLong) \
    X(setWidgetTextAtSlot) \
    X(setWidgetTextAtPath) \
    X(widgetInteractAtPath) \
    X(refreshWorldMetadata) \
    X(interactNpcByIndexInWorldView) \
    X(interactGroundItemInWorldView) \
    X(resolveActionClickPoint) \
    X(listSanitizedProxyRoutes) \
    X(setProxyRoute) \
    X(getProxyRouteStatus) \
    X(submitLoginLauncherCredentials) \
    X(submitLoginStandardCredentials) \
    X(acknowledgeStandardLogin) \
    X(advanceLoginClickToPlay) \
    X(getLoginFlowState) \
    X(advanceLoginLauncherCredentials) \
    X(advanceLoginLogout) \
    X(cancelLoginProfileOperations) \
    X(cancelLoginLogoutOperation) \
    X(stageLoginCredentials) \
    X(submitLoginCredentials) \
    X(executeSelectedActionPair) \
    X(requestItemPriceCatalog) \
    X(requestItemPrice) \
    X(geSubmitBuy) \
    X(geGetRequest) \
    X(geGetRequests) \
    X(geCancelRequest) \
    X(geReleaseRequest) \
    X(setHintArrowCoordinate) \
    X(setHintArrowActor) \
    X(clearHintArrow)

#define TITAN_NATIVE_HOSTRENDERV1_MEMBERS(X) \
    X(setImGuiContext) \
    X(worldToScreen) \
    X(tileToScreen) \
    X(getTileHeight) \
    X(drawTileQuad) \
    X(drawTileRegion) \
    X(drawEntityBox) \
    X(drawTextAtWorld) \
    X(drawScreenText) \
    X(drawScreenRect) \
    X(drawScreenLine) \
    X(setEntityHidden) \
    X(getEntityHidden) \
    X(drawEntityClickbox) \
    X(drawTileObjectClickbox) \
    X(drawEntityHull) \
    X(drawTileObjectHull) \
    X(overlayPanelRegister) \
    X(overlayPanelUnregister) \
    X(overlayPanelBegin) \
    X(overlayPanelEnd) \
    X(overlayPanelSetStyle) \
    X(overlayPanelTitle) \
    X(overlayPanelLine) \
    X(overlayPanelProgressBar) \
    X(setAudioPlaybackDisabled) \
    X(getAudioPlaybackDisabled) \
    X(worldToScreenInWorldView) \
    X(getTileHeightInWorldView) \
    X(drawTileQuadInWorldView) \
    X(drawTileRegionInWorldView) \
    X(drawTextAtWorldInWorldView) \
    X(getInterfaceScale) \
    X(drawEntityOutline) \
    X(drawTileObjectOutline) \
    X(screenshotSubmit) \
    X(screenshotPoll) \
    X(screenshotCopyPng) \
    X(screenshotRelease)

#define TITAN_NATIVE_HOSTNAVIGATIONV1_MEMBERS(X) \
    X(getCollisionFlag) \
    X(walkTo) \
    X(walkToWorld) \
    X(getInstanceTemplateChunks) \
    X(worldPointFromLocalInstance) \
    X(worldPointToLocalInstance) \
    X(getLocalDestinationLocation) \
    X(getWorldDestinationLocation) \
    X(copyCachedCollisionRegion) \
    X(copyCurrentCollisionScene) \
    X(webPathSubmit) \
    X(webPathPoll) \
    X(webPathCopySteps) \
    X(webPathCancel) \
    X(webPathRelease) \
    X(webWalkStart) \
    X(webWalkStatus) \
    X(webWalkCancel) \
    X(webWalkRelease) \
    X(webWalkAdvance) \
    X(webPathCopyStepPayload) \
    X(getCollisionSourceReady)

#define TITAN_NATIVE_PLUGINCOREV1_MEMBERS(X) \
    X(getId) \
    X(getName) \
    X(getDescription) \
    X(getAuthor) \
    X(getVersion) \
    X(getDefaultEnabled) \
    X(getEnabled) \
    X(setEnabled) \
    X(getSettings) \
    X(setSetting) \
    X(getSections) \
    X(onEnable) \
    X(onDisable) \
    X(getDependencies) \
    X(prepareUnload)

#define TITAN_NATIVE_PLUGINUIV1_MEMBERS(X) \
    X(getPanels) \
    X(renderOverlay) \
    X(getPanelElements) \
    X(onPanelAction) \
    X(getPanelIcon)

#define TITAN_NATIVE_PLUGINEVENTSV1_MEMBERS(X) \
    X(onGameTick) \
    X(onClientTick) \
    X(onProjectileSpawned) \
    X(onProjectileDespawned) \
    X(onProjectileMoved) \
    X(onGraphicsObjectSpawned) \
    X(onGraphicsObjectDespawned) \
    X(onGraphicsObjectMoved) \
    X(onNpcSpawned) \
    X(onNpcDespawned) \
    X(onPlayerSpawned) \
    X(onPlayerDespawned) \
    X(onTileObjectSpawned) \
    X(onTileObjectDespawned) \
    X(onMenuOptionClicked) \
    X(onScriptFired) \
    X(onVarbitChanged) \
    X(onChatMessage) \
    X(onItemContainerChanged) \
    X(onSettingChanged) \
    X(onSoundPlayed) \
    X(onHitsplatApplied) \
    X(onActorSpotAnim) \
    X(onAnimationChanged) \
    X(onGameStateChanged) \
    X(onMousePressed) \
    X(onMouseReleased) \
    X(onMainLoop) \
    X(onOverheadTextChanged) \
    X(onGrandExchangeOfferChanged) \
    X(onCrossTabChanged)

// Owns immutable capability tables. The provider contains this object's
// address, so keep the object alive and stationary while anyone can call it.
class HostInterfaceSet {
public:
    HostCoreV1 core{};
    HostGameV1 game{};
    HostActionsV1 actions{};
    HostRenderV1 render{};
    HostNavigationV1 navigation{};
    HostDefinitionExtrasV1 definitionExtras{};
    HostHtmlUiV1 htmlUi{};

    explicit HostInterfaceSet(const HostApi& api,
                              const void* extensionContext = nullptr,
                              QueryInterfaceFn extensionQuery = nullptr)
        : extensionContext_(extensionContext), extensionQuery_(extensionQuery) {
#define TITAN_NATIVE_ASSIGN(name) core.name = api.name;
        TITAN_NATIVE_HOSTCOREV1_MEMBERS(TITAN_NATIVE_ASSIGN)
#undef TITAN_NATIVE_ASSIGN
        core.sdkRelease = api.sdkVersion;
#define TITAN_NATIVE_ASSIGN(name) game.name = api.name;
        TITAN_NATIVE_HOSTGAMEV1_MEMBERS(TITAN_NATIVE_ASSIGN)
#undef TITAN_NATIVE_ASSIGN
#define TITAN_NATIVE_ASSIGN(name) actions.name = api.name;
        TITAN_NATIVE_HOSTACTIONSV1_MEMBERS(TITAN_NATIVE_ASSIGN)
#undef TITAN_NATIVE_ASSIGN
#define TITAN_NATIVE_ASSIGN(name) render.name = api.name;
        TITAN_NATIVE_HOSTRENDERV1_MEMBERS(TITAN_NATIVE_ASSIGN)
#undef TITAN_NATIVE_ASSIGN
#define TITAN_NATIVE_ASSIGN(name) navigation.name = api.name;
        TITAN_NATIVE_HOSTNAVIGATIONV1_MEMBERS(TITAN_NATIVE_ASSIGN)
#undef TITAN_NATIVE_ASSIGN
        definitionExtras.getWidgetModelIdAtPath = api.getWidgetModelIdAtPath;
        definitionExtras.copyItemWornAction = api.copyItemWornAction;
        definitionExtras.getNpcBaseId = api.getNpcBaseId;
        htmlUi.capabilities = api.htmlUiCapabilities;
    }
    HostInterfaceSet(const HostInterfaceSet&) = delete;
    HostInterfaceSet& operator=(const HostInterfaceSet&) = delete;
    HostInterfaceSet(HostInterfaceSet&&) = delete;
    HostInterfaceSet& operator=(HostInterfaceSet&&) = delete;

    const InterfaceProviderV1& provider() const noexcept { return provider_; }

private:
    static const InterfaceHeader* query(const void* context, uint64_t id,
                                        uint32_t majorVersion) {
        if (!context) return nullptr;
        const auto& self = *static_cast<const HostInterfaceSet*>(context);
        if (majorVersion == 1) {
            switch (id) {
            case kHostCoreId: return &self.core.header;
            case kHostGameId: return &self.game.header;
            case kHostActionsId: return &self.actions.header;
            case kHostRenderId: return &self.render.header;
            case kHostNavigationId: return &self.navigation.header;
            case kHostDefinitionExtrasId:
                return self.definitionExtras.getWidgetModelIdAtPath
                        || self.definitionExtras.copyItemWornAction
                        || self.definitionExtras.getNpcBaseId
                    ? &self.definitionExtras.header : nullptr;
            case kHostHtmlUiId: return self.htmlUi.capabilities ? &self.htmlUi.header : nullptr;
            default: break;
            }
        }
        return self.extensionQuery_
            ? self.extensionQuery_(self.extensionContext_, id, majorVersion)
            : nullptr;
    }
    const void* extensionContext_ = nullptr;
    QueryInterfaceFn extensionQuery_ = nullptr;
    InterfaceProviderV1 provider_{sizeof(InterfaceProviderV1), kAbiVersion, this, &query};
};

// Owns immutable capability tables. The provider contains this object's
// address, so keep the object alive and stationary while anyone can call it.
class PluginInterfaceSet {
public:
    PluginCoreV1 core{};
    PluginUiV1 ui{};
    PluginEventsV1 events{};
    PluginHtmlPanelsV1 htmlPanels{};
    PluginHtmlOverlaysV1 htmlOverlays{};

    explicit PluginInterfaceSet(const PluginApi& api) {
#define TITAN_NATIVE_ASSIGN(name) core.name = api.name;
        TITAN_NATIVE_PLUGINCOREV1_MEMBERS(TITAN_NATIVE_ASSIGN)
#undef TITAN_NATIVE_ASSIGN
#define TITAN_NATIVE_ASSIGN(name) ui.name = api.name;
        TITAN_NATIVE_PLUGINUIV1_MEMBERS(TITAN_NATIVE_ASSIGN)
#undef TITAN_NATIVE_ASSIGN
#define TITAN_NATIVE_ASSIGN(name) events.name = api.name;
        TITAN_NATIVE_PLUGINEVENTSV1_MEMBERS(TITAN_NATIVE_ASSIGN)
#undef TITAN_NATIVE_ASSIGN
        htmlPanels.callbacks = api.htmlPanels;
        htmlOverlays.callbacks = api.htmlOverlays;
    }
    PluginInterfaceSet(const PluginInterfaceSet&) = delete;
    PluginInterfaceSet& operator=(const PluginInterfaceSet&) = delete;
    PluginInterfaceSet(PluginInterfaceSet&&) = delete;
    PluginInterfaceSet& operator=(PluginInterfaceSet&&) = delete;

    const InterfaceProviderV1& provider() const noexcept { return provider_; }

private:
    static const InterfaceHeader* query(const void* context, uint64_t id,
                                        uint32_t majorVersion) {
        if (!context) return nullptr;
        const auto& self = *static_cast<const PluginInterfaceSet*>(context);
        if (majorVersion == 1) {
            switch (id) {
            case kPluginCoreId: return &self.core.header;
            case kPluginUiId: return &self.ui.header;
            case kPluginEventsId: return &self.events.header;
            case kPluginHtmlPanelsId: return self.htmlPanels.callbacks.complete() ? &self.htmlPanels.header : nullptr;
            case kPluginHtmlOverlaysId: return self.htmlOverlays.callbacks.complete() ? &self.htmlOverlays.header : nullptr;
            default: break;
            }
        }
        return nullptr;
    }
    InterfaceProviderV1 provider_{sizeof(InterfaceProviderV1), kAbiVersion, this, &query};
};

// Import into a consumer-owned local view. SDK release numbers do not
// participate in negotiation; absent and short optional tables stay null.
inline bool importHostApi(const InterfaceProviderV1* provider, HostApi& out) {
    out = {};
    if (!isValidProvider(provider)) return false;
    {
        const auto* table = queryInterface(provider, kHostCoreId);
#define TITAN_NATIVE_IMPORT(name) out.name = readMember<decltype(out.name)>(table, offsetof(HostCoreV1, name));
        TITAN_NATIVE_HOSTCOREV1_MEMBERS(TITAN_NATIVE_IMPORT)
#undef TITAN_NATIVE_IMPORT
        // A host older than SDK 146 publishes no release and imports as 0.
        out.sdkVersion = readMember<uint32_t>(table, offsetof(HostCoreV1, sdkRelease));
    }
    {
        const auto* table = queryInterface(provider, kHostGameId);
#define TITAN_NATIVE_IMPORT(name) out.name = readMember<decltype(out.name)>(table, offsetof(HostGameV1, name));
        TITAN_NATIVE_HOSTGAMEV1_MEMBERS(TITAN_NATIVE_IMPORT)
#undef TITAN_NATIVE_IMPORT
    }
    {
        const auto* table = queryInterface(provider, kHostActionsId);
#define TITAN_NATIVE_IMPORT(name) out.name = readMember<decltype(out.name)>(table, offsetof(HostActionsV1, name));
        TITAN_NATIVE_HOSTACTIONSV1_MEMBERS(TITAN_NATIVE_IMPORT)
#undef TITAN_NATIVE_IMPORT
    }
    {
        const auto* table = queryInterface(provider, kHostRenderId);
#define TITAN_NATIVE_IMPORT(name) out.name = readMember<decltype(out.name)>(table, offsetof(HostRenderV1, name));
        TITAN_NATIVE_HOSTRENDERV1_MEMBERS(TITAN_NATIVE_IMPORT)
#undef TITAN_NATIVE_IMPORT
    }
    {
        const auto* table = queryInterface(provider, kHostNavigationId);
#define TITAN_NATIVE_IMPORT(name) out.name = readMember<decltype(out.name)>(table, offsetof(HostNavigationV1, name));
        TITAN_NATIVE_HOSTNAVIGATIONV1_MEMBERS(TITAN_NATIVE_IMPORT)
#undef TITAN_NATIVE_IMPORT
    }
    {
        const auto* table = queryInterface(provider, kHostDefinitionExtrasId);
        out.getWidgetModelIdAtPath = readMember<decltype(out.getWidgetModelIdAtPath)>(
            table, offsetof(HostDefinitionExtrasV1, getWidgetModelIdAtPath));
        out.copyItemWornAction = readMember<decltype(out.copyItemWornAction)>(
            table, offsetof(HostDefinitionExtrasV1, copyItemWornAction));
        out.getNpcBaseId = readMember<decltype(out.getNpcBaseId)>(
            table, offsetof(HostDefinitionExtrasV1, getNpcBaseId));
    }
    {
        const auto* table = queryInterface(provider, kHostHtmlUiId);
        out.htmlUiCapabilities = readMember<decltype(out.htmlUiCapabilities)>(table, offsetof(HostHtmlUiV1, capabilities));
    }
    return true;
}

// Import into a consumer-owned local view. SDK release numbers do not
// participate in negotiation; absent and short optional tables stay null.
inline bool importPluginApi(const PluginDescriptorV1* descriptor, PluginApi& out) {
    out = {};
    if (!isValidPluginDescriptor(descriptor)) return false;
    const auto* provider = &descriptor->interfaces;
    out.userData = descriptor->userData;
    {
        const auto* table = queryInterface(provider, kPluginCoreId);
#define TITAN_NATIVE_IMPORT(name) out.name = readMember<decltype(out.name)>(table, offsetof(PluginCoreV1, name));
        TITAN_NATIVE_PLUGINCOREV1_MEMBERS(TITAN_NATIVE_IMPORT)
#undef TITAN_NATIVE_IMPORT
    }
    {
        const auto* table = queryInterface(provider, kPluginUiId);
#define TITAN_NATIVE_IMPORT(name) out.name = readMember<decltype(out.name)>(table, offsetof(PluginUiV1, name));
        TITAN_NATIVE_PLUGINUIV1_MEMBERS(TITAN_NATIVE_IMPORT)
#undef TITAN_NATIVE_IMPORT
    }
    {
        const auto* table = queryInterface(provider, kPluginEventsId);
#define TITAN_NATIVE_IMPORT(name) out.name = readMember<decltype(out.name)>(table, offsetof(PluginEventsV1, name));
        TITAN_NATIVE_PLUGINEVENTSV1_MEMBERS(TITAN_NATIVE_IMPORT)
#undef TITAN_NATIVE_IMPORT
    }
    out.htmlPanels = importHtmlCallbacks(provider, kPluginHtmlPanelsId);
    out.htmlOverlays = importHtmlCallbacks(provider, kPluginHtmlOverlaysId);
    return out.getId && out.getName;
}

#undef TITAN_NATIVE_HOSTCOREV1_MEMBERS
#undef TITAN_NATIVE_HOSTGAMEV1_MEMBERS
#undef TITAN_NATIVE_HOSTACTIONSV1_MEMBERS
#undef TITAN_NATIVE_HOSTRENDERV1_MEMBERS
#undef TITAN_NATIVE_HOSTNAVIGATIONV1_MEMBERS
#undef TITAN_NATIVE_PLUGINCOREV1_MEMBERS
#undef TITAN_NATIVE_PLUGINUIV1_MEMBERS
#undef TITAN_NATIVE_PLUGINEVENTSV1_MEMBERS

static_assert(std::is_standard_layout_v<InterfaceHeader>);
static_assert(std::is_standard_layout_v<InterfaceProviderV1>);
static_assert(std::is_standard_layout_v<PluginDescriptorV1>);
static_assert(std::is_standard_layout_v<ModuleDescriptorV1>);
static_assert(sizeof(InterfaceHeader) == 8);
static_assert(sizeof(InterfaceRequirement) == 16);
#if INTPTR_MAX == INT64_MAX
static_assert(sizeof(InterfaceProviderV1) == 24);
static_assert(sizeof(PluginDescriptorV1) == 56);
static_assert(sizeof(ModuleDescriptorV1) == 32);
static_assert(offsetof(InterfaceProviderV1, queryInterface) == 16);
static_assert(offsetof(PluginDescriptorV1, interfaces) == 24);
static_assert(offsetof(ModuleDescriptorV1, createPlugin) == 16);
#endif

} // namespace TitanPluginSdk::NativeAbi
