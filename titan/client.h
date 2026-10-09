/// @file titan/client.h
/// @brief State / subsystem facades: client, camera, hider, cache, vars,
///        script, widgets, idle, walk, login, item containers, world list.
///
/// Each `titan::state::xxx()` factory returns a lightweight value type whose
/// methods forward to the active `IBackend`. No state is stored -- each call
/// dispatches through the per-DLL `detail::backend()` pointer installed at
/// plugin load (`ExternalBackend`) or at client init (`InternalBackend`).
///
/// SDK v41 grouped every facade factory under `namespace titan::state`.
/// Free helpers (`log`, `logf`, `addChatMessage`) stay at top-level
/// `namespace titan` since they aren't subsystem accessors.

#pragma once

#include "actor.h"
#include "collision.h"
#include "detail/abi.h"
#include "detail/account_mode.h"
#include "detail/backend.h"
#include "menu_action.h"
#include "prayer.h"
#include "skill.h"
#include "varbits.h"
#include "world_view.h"
#include "grand_exchange.h"
#include "item_prices.h"
#include "hint_arrow.h"

#include <algorithm>
#include <array>
#include <cstdarg>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <functional>
#include <limits>
#include <optional>
#include <string>
#include <vector>

namespace titan {

// ---------------------------------------------------------------------------
// titan::log() / titan::logf() -- console log passthrough.
//
// log() takes a plain string. logf() takes printf-style format args so plugin
// authors can format in a single line without juggling a scratch buffer:
//
//     titan::logf("[MyPlugin] tick=%d hp=%d", tick, hp);
//
// Both route to the host's log function, which writes through the client
// Logger (level=info) into the ring buffer, stdout, and the optional log file.
// ---------------------------------------------------------------------------
inline void log(const char* msg) {
    if (auto* b = detail::backend()) b->log(msg);
}
inline void log(const std::string& msg) { log(msg.c_str()); }

/// printf-style single-line log. Formats locally into a bounded stack buffer
/// (truncates silently past ~1 KB) and forwards to the host log function.
inline void logf(const char* fmt, ...) {
    if (!fmt) return;
    char buf[1024];
    va_list args;
    va_start(args, fmt);
    std::vsnprintf(buf, sizeof(buf), fmt, args);
    va_end(args);
    log(buf);
}

// ---------------------------------------------------------------------------
// titan::addChatMessage -- inject a local chat line into the chatbox.
//
// RuneLite-style: the message appears on the local client only, no server
// packet is sent. The host dispatches the call onto the game thread, builds
// eastl::basic_string temporaries, and forwards to the native chat pipeline
// so the line is treated identically to server-delivered text (including
// firing `onChatMessage` on every listener plugin).
//
// `type` is the native chat message type (0=PUBLIC, 2=SERVER, 3=CLAN, ...).
// Pass {@c nullptr} or empty string for `name` / `sender` when they don't
// apply to the message class (e.g. type=2 system messages typically leave
// both blank). Added in SDK 22.
// ---------------------------------------------------------------------------
inline void addChatMessage(int32_t type, const char* name,
                           const char* message, const char* sender) {
    if (auto* b = detail::backend()) b->addChatMessage(type, name, message, sender);
}
inline void addChatMessage(int32_t type, const std::string& name,
                           const std::string& message,
                           const std::string& sender) {
    addChatMessage(type, name.c_str(), message.c_str(), sender.c_str());
}

// ---------------------------------------------------------------------------
// Client
// ---------------------------------------------------------------------------

struct ClientSnapshot {
    int32_t tickCount = 0;
    int32_t plane = 0;
    int32_t currentWorldViewId = WorldView::CURRENT;
    int32_t localPlayerIndex = -1;
    int32_t playerCount = 0;
    int32_t baseX = 0;
    int32_t baseY = 0;
    int32_t topLevelBaseX = 0;
    int32_t topLevelBaseY = 0;
    int32_t topLevelPlane = 0;
    int32_t topLevelLocalPlayerTileX = 0;
    int32_t topLevelLocalPlayerTileY = 0;
    int32_t topLevelLocalPlayerPlane = 0;
    bool topLevelLocalPlayerTileValid = false;
    int32_t topLevelSceneSizeX = 0;
    int32_t topLevelSceneSizeY = 0;
    int32_t sceneSizeX = 0;
    int32_t sceneSizeY = 0;
    int32_t runEnergy = 0;
    int32_t weight = 0;
    uint64_t worldViewPtr = 0;
    uint64_t scenePtr = 0;
    uint64_t localPlayerEntity = 0;
    uint64_t clientBase = 0;
};

struct InstanceTemplateChunks {
    std::array<int32_t, TitanPluginSdk::kInstanceTemplateChunkCount> chunks{};
    bool instanced = false;
};

class ClientFacade {
public:
    bool isGrandExchangeAvailable() const { return GrandExchangeFacade{}.available(); }
    std::optional<GrandExchangeOffer> getGrandExchangeOffer(int32_t slot) const {
        return GrandExchangeFacade{}.offer(slot);
    }
    std::vector<GrandExchangeOffer> getGrandExchangeOffers() const {
        return GrandExchangeFacade{}.offers();
    }
    std::optional<ClientSnapshot> snapshot() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::ClientState s = {};
        if (!b->getClientState(&s)) return std::nullopt;
        ClientSnapshot out;
        out.tickCount = s.tickCount;
        out.plane = s.plane;
        out.currentWorldViewId = s.currentWorldViewId;
        out.localPlayerIndex = s.localPlayerIndex;
        out.playerCount = s.playerCount;
        out.baseX = s.baseX;
        out.baseY = s.baseY;
        out.topLevelBaseX = s.topLevelBaseX;
        out.topLevelBaseY = s.topLevelBaseY;
        out.topLevelPlane = s.topLevelPlane;
        out.topLevelLocalPlayerTileX = s.topLevelLocalPlayerTileX;
        out.topLevelLocalPlayerTileY = s.topLevelLocalPlayerTileY;
        out.topLevelLocalPlayerPlane = s.topLevelLocalPlayerPlane;
        out.topLevelLocalPlayerTileValid = s.topLevelLocalPlayerTileValid != 0;
        out.topLevelSceneSizeX = s.topLevelSceneSizeX;
        out.topLevelSceneSizeY = s.topLevelSceneSizeY;
        out.sceneSizeX = s.sceneSizeX;
        out.sceneSizeY = s.sceneSizeY;
        out.runEnergy = s.runEnergy;
        out.weight = s.weight;
        out.worldViewPtr = s.worldViewPtr;
        out.scenePtr = s.scenePtr;
        out.localPlayerEntity = s.localPlayerEntity;
        out.clientBase = s.clientBase;
        return out;
    }

    int32_t tick() const { return snapshot().value_or(ClientSnapshot{}).tickCount; }
    /// SDK 127: DirectAccess=1, Events=2 (all five accepted speech sources).
    uint32_t getOverheadTextCapabilities() const {
        auto* b = detail::backend(); return b ? b->getOverheadTextCapabilities() : 0;
    }
    /// Current native game cycle. This signed 32-bit clock advances at
    /// the nominal 20 ms client logic cadence (50 Hz); 30 cycles make one
    /// 600 ms server tick. It is distinct from tick(). Returns 0 when the
    /// current host or analyzer bundle does not expose the clock. SDK 125+.
    int32_t gameCycle() const {
        auto* b = detail::backend();
        return b ? b->getGameCycle() : 0;
    }
    int32_t plane() const { return snapshot().value_or(ClientSnapshot{}).plane; }
    int32_t playerCount() const { return snapshot().value_or(ClientSnapshot{}).playerCount; }
    int32_t sceneSizeX() const { return snapshot().value_or(ClientSnapshot{}).sceneSizeX; }
    int32_t sceneSizeY() const { return snapshot().value_or(ClientSnapshot{}).sceneSizeY; }
    bool loggedIn() const { return snapshot().value_or(ClientSnapshot{}).localPlayerIndex >= 0; }
    /// Run energy (0-10000). Divide by 100 for the percentage shown in the orb.
    int32_t runEnergy() const { return snapshot().value_or(ClientSnapshot{}).runEnergy; }
    /// Player weight in kg (signed; negative with weight-reducing gear).
    int32_t weight() const { return snapshot().value_or(ClientSnapshot{}).weight; }
    /// Raw RuneLite-compatible account type varbit (Varbits::ACCOUNT_TYPE).
    int32_t accountType() const {
        auto* b = detail::backend();
        return b ? b->getVarbit(Varbits::ACCOUNT_TYPE) : 0;
    }
    /// True for ironman account modes, including group variants.
    bool isIronman() const { return detail::isIronmanAccountType(accountType()); }
    bool isIronMan() const { return isIronman(); }
    /// True for GIM / HCGIM / UGIM account modes.
    bool isGroupIronman() const {
        return detail::isGroupIronmanAccountType(accountType());
    }
    bool isGroupIronMan() const { return isGroupIronman(); }

    int32_t currentWorldViewId() const {
        return snapshot().value_or(ClientSnapshot{}).currentWorldViewId;
    }

    uint64_t currentWorldViewPtr() const {
        return snapshot().value_or(ClientSnapshot{}).worldViewPtr;
    }

    std::optional<uint64_t> worldViewPtr(int32_t worldViewId) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        uint64_t ptr = 0;
        if (!b->getWorldViewById(worldViewId, &ptr) || ptr == 0) return std::nullopt;
        return ptr;
    }

    std::optional<uint64_t> topLevelWorldViewPtr() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        uint64_t ptr = 0;
        if (!b->getTopLevelWorldView(&ptr) || ptr == 0) return std::nullopt;
        return ptr;
    }

    std::optional<InstanceTemplateChunks> instanceTemplateChunks() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::InstanceTemplateChunksState raw{};
        if (!b->getInstanceTemplateChunks(&raw)) return std::nullopt;
        InstanceTemplateChunks out;
        out.instanced = raw.instanced != 0;
        std::copy(raw.chunks,
                  raw.chunks + TitanPluginSdk::kInstanceTemplateChunkCount,
                  out.chunks.begin());
        return out;
    }

    bool isInInstance() const {
        auto chunks = instanceTemplateChunks();
        return chunks && chunks->instanced;
    }

    std::optional<LocalPoint> getLocalDestinationLocation() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::LocalPointState raw{};
        if (!b->getLocalDestinationLocation(&raw)) return std::nullopt;
        return LocalPoint{raw.x, raw.y, raw.worldViewId};
    }

    std::optional<WorldPoint> getWorldDestinationLocation() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::WorldPointState raw{};
        if (!b->getWorldDestinationLocation(&raw)) return std::nullopt;
        return WorldPoint{raw.x, raw.y, raw.z, raw.worldViewId};
    }

    std::optional<Player> localPlayer() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::PlayerState ps = {};
        if (!b->getLocalPlayer(&ps)) return std::nullopt;
        return Player{ps};
    }

    /// Game-thread snapshot; unavailable differs from a readable empty list.
    std::optional<HintArrowSnapshot> hintArrows() const {
        return HintArrowSnapshot::read();
    }

    /// Replace server slot zero now. Call on the game thread; the result is
    /// the actual update outcome. Later server packets can replace this arrow.
    HintArrowUpdateResult setHintArrow(const WorldPoint& point, int32_t subX = 64,
                                       int32_t subY = 64, int32_t height = 0) const {
        auto* backend = detail::backend();
        if (!backend) return HintArrowUpdateResult::Unavailable;
        const TitanPluginSdk::HintArrowCoordinateTarget target{
            {point.x, point.y, point.z, point.worldViewId}, subX, subY, height};
        return static_cast<HintArrowUpdateResult>(backend->setHintArrowCoordinate(&target));
    }
    HintArrowUpdateResult setHintArrow(const Actor& actor) const {
        auto* backend = detail::backend();
        if (!backend) return HintArrowUpdateResult::Unavailable;
        if (actor.isEmpty()) return HintArrowUpdateResult::InvalidArgument;
        TitanPluginSdk::HintArrowActorTarget target{};
        const auto copyIdentity = [&target](HintArrowKind kind, const auto& retained) {
            target = {kind, retained.hashIndex, retained.worldViewId, 0,
                      retained.entityPtr, retained.worldViewPtr};
        };
        if (const auto* npc = actor.asNpc()) copyIdentity(HintArrowKind::Npc, npc->state_);
        else if (const auto* player = actor.asPlayer()) copyIdentity(HintArrowKind::Player, player->state_);
        return static_cast<HintArrowUpdateResult>(backend->setHintArrowActor(&target));
    }
    HintArrowUpdateResult setHintArrow(const Npc& npc) const { return setHintArrow(Actor{npc}); }
    HintArrowUpdateResult setHintArrow(const Player& player) const { return setHintArrow(Actor{player}); }
    HintArrowUpdateResult clearHintArrow() const {
        auto* backend = detail::backend();
        return backend ? static_cast<HintArrowUpdateResult>(backend->clearHintArrow())
                       : HintArrowUpdateResult::Unavailable;
    }

    // ---- Synthetic menu dispatch (SDK v37+) ---------------------------

    /// Dispatch a synthetic menu action directly. Use the fluent wrappers
    /// on entity types (`Npc::interact`, `Item::interact`, ...) where
    /// possible; this lower-level helper is for cases where the
    /// opcode+id+params are known directly (e.g. CC_OP widget dispatch
    /// with custom parameters, replaying a captured menu entry).
    ///
    /// @param opcode       MenuAction opcode (57 = CC_OP, 1007 = CC_OP_LOW,
    ///                     25 = WIDGET_TARGET,
    ///                     58 = WIDGET_TARGET_ON_WIDGET, 3..6/1001 for
    ///                     GAME_OBJECT_*, 9..13 for NPC_*, etc).
    /// @param identifier   Menu-entry identifier -- the entity id or CC_OP
    ///                     sub-action index depending on family.
    /// @param param0       Contextual param 0 (dynamic-child slot on
    ///                     widgets, target tile on walks, etc).
    /// @param param1       Contextual param 1 (packed widget id on
    ///                     widget-family opcodes, 0 for most others).
    /// @param worldViewId  World view id to dispatch against; -1 picks
    ///                     the game's current world view (0).
    /// @param skipClick    When true, dispatch DoAction directly without
    ///                     emitting the synthetic click phase.
    /// @return true when the action was queued for the next client tick.
    ///
    /// Unspecified click coordinates are resolved by Titan: entity/world
    /// target opcodes use their native clickboxes, and other opcodes use
    /// randomized active-screen coords.
    bool invokeMenuAction(MenuAction::Id opcode,
                          int32_t identifier,
                          int32_t param0,
                          int32_t param1,
                          int32_t worldViewId = -1,
                          bool skipClick = false) const {
        TitanPluginSdk::SyntheticActionEntry entry{};
        entry.opcode = static_cast<uint32_t>(opcode);
        entry.identifier = identifier;
        entry.param0 = param0;
        entry.param1 = param1;
        entry.worldViewId = worldViewId;
        entry.clickX = -1;
        entry.clickY = -1;
        entry.skipClick = skipClick ? 1 : 0;
        auto* b = detail::backend();
        return b && b->executeSyntheticEntry(&entry) != 0;
    }

    /// SDK 128. Submit source selection and its dependent target together.
    /// Source opcode is WidgetTarget; its widget/child are param1/param0.
    /// Omit expectedSourceItem for spells so the host binds the live item.
    bool invokeSelectedMenuAction(const MenuAction::Entry& source,
                                  const MenuAction::Entry& target,
                                  std::optional<int32_t> expectedSourceItem = std::nullopt) const {
        auto copy = [](const MenuAction::Entry& e) {
            TitanPluginSdk::SyntheticActionEntry out{};
            out.opcode = static_cast<uint32_t>(e.opcode); out.identifier = e.identifier;
            out.param0 = e.param0; out.param1 = e.param1; out.worldViewId = e.worldViewId;
            out.clickX = e.clickX; out.clickY = e.clickY;
            out.actionText = e.actionText; out.targetText = e.targetText;
            out.skipClick = e.skipClick ? 1 : 0;
            out.targetPlane = e.targetPlane; out.targetSizeX = e.targetSizeX; out.targetSizeY = e.targetSizeY;
            out.targetLayer = e.targetLayer; out.targetEntityPtr = e.targetEntityPtr; out.targetPackedId = e.targetPackedId;
            return out;
        };
        TitanPluginSdk::SelectedActionPair pair{};
        pair.source = copy(source); pair.target = copy(target);
        pair.hasExpectedSourceItem = expectedSourceItem.has_value() ? 1 : 0;
        pair.expectedSourceItemId = expectedSourceItem.value_or(-1);
        auto* b = detail::backend();
        return b && b->executeSelectedActionPair(&pair) != 0;
    }

    /// Fully-specified overload. Use when you need `actionText` /
    /// `targetText` / explicit click coords -- typically for replaying a
    /// captured `MenuClickEvent`. Leave click coords at -1/-1 to randomize
    /// them on the active game screen. SDK v37+.
    bool invokeMenuAction(const MenuAction::Entry& e) const {
        TitanPluginSdk::SyntheticActionEntry entry{};
        entry.opcode         = static_cast<uint32_t>(e.opcode);
        entry.identifier     = e.identifier;
        entry.param0         = e.param0;
        entry.param1         = e.param1;
        entry.worldViewId    = e.worldViewId;
        entry.clickX         = e.clickX;
        entry.clickY         = e.clickY;
        entry.actionText     = e.actionText;
        entry.targetText     = e.targetText;
        entry.skipClick = e.skipClick ? 1 : 0;
        entry.targetPlane    = e.targetPlane;
        entry.targetSizeX    = e.targetSizeX;
        entry.targetSizeY    = e.targetSizeY;
        entry.targetLayer    = e.targetLayer;
        entry.targetEntityPtr = e.targetEntityPtr;
        entry.targetPackedId = e.targetPackedId;
        auto* b = detail::backend();
        return b && b->executeSyntheticEntry(&entry) != 0;
    }
};

namespace state { inline ::titan::ClientFacade client() { return ::titan::ClientFacade{}; } }

// ---------------------------------------------------------------------------
// Camera
// ---------------------------------------------------------------------------

struct CameraSnapshot {
    int32_t posX = 0;
    int32_t posY = 0;
    int32_t posZ = 0;
    int32_t yaw = 0;
    int32_t pitch = 0;
    int32_t viewportW = 0;
    int32_t viewportH = 0;
    int32_t zoom = 0;
    bool    valid = false;
};

/// Live UI-frame -> physical interface-scale factor derived from the game's
/// canvas coordinate transform (`physical = widget * scale + canvasOrigin`).
/// `scaleX`/`scaleY` are 1.0 at 100% in-game interface scaling, ~1.5 at 150%,
/// ~2.0 at 200%; independent of Windows display scaling. `valid` is false
/// when the analyzer did not detect the canvas transform on the bound
/// revision, in which case the fields fall back to identity (1.0 / 0).
struct InterfaceScale {
    float scaleX = 1.0f;
    float scaleY = 1.0f;
    int32_t canvasOriginX = 0;
    int32_t canvasOriginY = 0;
    bool valid = false;
};

class CameraFacade {
public:
    std::optional<CameraSnapshot> snapshot() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::CameraState s = {};
        if (!b->getCameraState(&s) || !s.valid) return std::nullopt;
        CameraSnapshot out;
        out.posX = s.posX; out.posY = s.posY; out.posZ = s.posZ;
        out.yaw  = s.yaw;  out.pitch = s.pitch;
        out.viewportW = s.viewportW; out.viewportH = s.viewportH;
        out.zoom = s.zoom; out.valid = true;
        return out;
    }
    int32_t yaw() const { return snapshot().value_or(CameraSnapshot{}).yaw; }
    int32_t pitch() const { return snapshot().value_or(CameraSnapshot{}).pitch; }
    int32_t zoom() const { return snapshot().value_or(CameraSnapshot{}).zoom; }
    /// World-space camera X position (sub-tile units). Added in SDK 39.
    int32_t posX() const { return snapshot().value_or(CameraSnapshot{}).posX; }
    /// World-space camera Y position (sub-tile units). Added in SDK 39.
    int32_t posY() const { return snapshot().value_or(CameraSnapshot{}).posY; }
    /// World-space camera Z (height) position. Added in SDK 39.
    int32_t posZ() const { return snapshot().value_or(CameraSnapshot{}).posZ; }

    /// Live UI-frame -> physical interface-scale factor plus canvas origin.
    /// Always returns a usable value: identity (1.0 / 0, valid=false) when the
    /// host predates SDK 109 or the analyzer did not detect the canvas
    /// transform. Added in SDK 109. Useful for plugins that draw their own
    /// fixed-pixel geometry (bar widths, anchor offsets) and need to scale it
    /// to match the host-positioned widget overlays at 150%/200% interface
    /// scaling.
    InterfaceScale interfaceScale() const {
        InterfaceScale out;
        auto* b = detail::backend();
        if (!b) return out;
        out.valid = b->getInterfaceScale(&out.scaleX, &out.scaleY,
                                         &out.canvasOriginX,
                                         &out.canvasOriginY) != 0;
        return out;
    }
    /// Convenience: horizontal interface-scale factor (1.0 when unavailable).
    float interfaceScaleX() const { return interfaceScale().scaleX; }
    /// Convenience: vertical interface-scale factor (1.0 when unavailable).
    float interfaceScaleY() const { return interfaceScale().scaleY; }
};

namespace state { inline ::titan::CameraFacade camera() { return ::titan::CameraFacade{}; } }

// ---------------------------------------------------------------------------
// Hider (render-function overrides for entities and the scene)
// ---------------------------------------------------------------------------

class HiderFacade {
public:
    void setPlayers(bool v) { writeChannel(0, v); }
    void setNpcs(bool v)    { writeChannel(1, v); }
    void setSelf(bool v)    { writeChannel(2, v); }
    void setScene(bool v)   { writeChannel(3, v); }

    bool isPlayersHidden() const { return readChannel(0); }
    bool isNpcsHidden()    const { return readChannel(1); }
    bool isSelfHidden()    const { return readChannel(2); }
    bool isSceneHidden()   const { return readChannel(3); }

private:
    static void writeChannel(uint8_t channel, bool v) {
        if (auto* b = detail::backend()) b->setEntityHidden(channel, v ? 1 : 0);
    }
    static bool readChannel(uint8_t channel) {
        auto* b = detail::backend();
        return b && b->getEntityHidden(channel) != 0;
    }
};

namespace state { inline ::titan::HiderFacade hider() { return ::titan::HiderFacade{}; } }

// ---------------------------------------------------------------------------
// Audio (global sound-effect playback toggle)
// ---------------------------------------------------------------------------

/// Controls the client's global audio-playback suppression. When disabled,
/// the native sound hooks still fire `onSoundPlayed` but skip the game's
/// playback call (synth entries are dropped from the queue; jingles are not
/// played). Per-sound playback suppression is not supported by the current
/// hook; `event.consume()` only affects handler ordering. Added in SDK 69.
class AudioFacade {
public:
    void setPlaybackDisabled(bool v) {
        if (auto* b = detail::backend()) b->setAudioPlaybackDisabled(v ? 1 : 0);
    }
    bool playbackDisabled() const {
        auto* b = detail::backend();
        return b && b->getAudioPlaybackDisabled() != 0;
    }
};

namespace state { inline ::titan::AudioFacade audio() { return ::titan::AudioFacade{}; } }

// ---------------------------------------------------------------------------
// Cache -- definition lookups from the JS5 disk cache.
// ---------------------------------------------------------------------------

struct ItemDef {
    using SubOps = std::array<
        std::array<std::string, TitanPluginSdk::kMaxItemSubOpCount>,
        TitanPluginSdk::kMaxActionCount>;

    int32_t id = 0;
    std::string name;
    bool members = false;
    bool stackable = false;
    bool noted = false;
    int32_t noteId = -1;
    /// Other item id in the note pair; -1 when there is no note pair.
    int32_t linkedId = -1;
    std::array<std::string, 5> inventoryActions;
    std::array<std::string, 5> groundActions;
    /// Raw cache submenu labels indexed by inventory action then submenu slot.
    /// Empty strings preserve the fixed 5 x 20 positional shape.
    SubOps subOps;
};

struct NpcDef {
    int32_t id = 0;
    std::string name;
    int32_t combatLevel = 0;
    int32_t size = 1;
    std::array<std::string, 5> actions;
    int32_t transformVarbit = -1;
    int32_t transformVarp = -1;
    int32_t transformDefault = -1;
};

struct ObjDef {
    int32_t id = 0;
    std::string name;
    std::array<std::string, 5> actions;
    int32_t sizeX = 1;
    int32_t sizeY = 1;
    bool blocksMovement = false;
    int32_t transformVarbit = -1;
    int32_t transformVarp = -1;
    int32_t transformDefault = -1;
};

struct VarbitDef {
    int32_t id = 0;
    int32_t varpIndex = 0;
    int32_t lowBit = 0;
    int32_t highBit = 0;
    /// Origin of the `{varpIndex, lowBit, highBit}` triple. Current hosts
    /// return `Disk` from Titan-owned JS5 cache data; `LiveCache` and `Native`
    /// remain valid enum values for older hosts and diagnostics.
    TitanPluginSdk::VarbitDefSource source = TitanPluginSdk::VarbitDefSource::Disk;
};

class CacheFacade {
public:
    std::optional<ItemDef> item(int32_t id) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::ItemDefSnapshot s = {};
        if (!b->getItemDef(id, &s)) return std::nullopt;
        ItemDef d;
        d.id = s.id; d.name = s.name;
        d.members = s.members != 0;
        d.stackable = s.stackable != 0;
        d.noted = s.noted != 0;
        d.noteId = s.noteId; d.linkedId = s.linkedId;
        for (int i = 0; i < 5; ++i) {
            d.inventoryActions[i] = s.inventoryActions[i];
            d.groundActions[i] = s.groundActions[i];
            for (uint32_t subOp = 0;
                 subOp < TitanPluginSdk::kMaxItemSubOpCount; ++subOp) {
                d.subOps[i][subOp] = s.subOps[i][subOp];
            }
        }
        return d;
    }

    std::optional<NpcDef> npc(int32_t id) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::NpcDefSnapshot s = {};
        if (!b->getNpcDef(id, &s)) return std::nullopt;
        NpcDef d;
        d.id = s.id; d.name = s.name;
        d.combatLevel = s.combatLevel; d.size = s.size;
        for (int i = 0; i < 5; ++i) d.actions[i] = s.actions[i];
        d.transformVarbit = s.transformVarbit;
        d.transformVarp = s.transformVarp;
        d.transformDefault = s.transformDefault;
        return d;
    }

    std::optional<ObjDef> obj(int32_t id) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::ObjDefSnapshot s = {};
        if (!b->getObjDef(id, &s)) return std::nullopt;
        ObjDef d;
        d.id = s.id; d.name = s.name;
        for (int i = 0; i < 5; ++i) d.actions[i] = s.actions[i];
        d.sizeX = s.sizeX; d.sizeY = s.sizeY;
        d.blocksMovement = s.blocksMovement != 0;
        d.transformVarbit = s.transformVarbit;
        d.transformVarp = s.transformVarp;
        d.transformDefault = s.transformDefault;
        return d;
    }

    std::optional<VarbitDef> varbit(int32_t id) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::VarbitDefSnapshot s = {};
        if (!b->getVarbitDef(id, &s)) return std::nullopt;
        VarbitDef d;
        d.id = s.id; d.varpIndex = s.varpIndex;
        d.lowBit = s.lowBit; d.highBit = s.highBit;
        d.source = static_cast<TitanPluginSdk::VarbitDefSource>(s.source);
        return d;
    }
};

namespace state { inline ::titan::CacheFacade cache() { return ::titan::CacheFacade{}; } }

// ---------------------------------------------------------------------------
// Vars (varbits + varps + client-side variables)
// ---------------------------------------------------------------------------

/// Raw var reads. Varbits are packed bit-fields inside per-player varps and
/// are resolved from Titan-owned JS5 cache definitions plus direct varp reads;
/// they do not call native GET_VARBIT or walk the game's live VarBitType cache.
/// VarClients are client-side typed values exposed by the analyzer-backed
/// runtime helper service. Prior to SDK v37 this facade also exposed skill and
/// prayer queries; those have moved to `titan::state::skills()` and
/// `titan::state::prayers()` respectively for cleaner domain separation.
class VarsFacade {
public:
    int32_t varbit(int32_t id) const {
        auto* b = detail::backend();
        return b ? b->getVarbit(id) : 0;
    }
    int32_t varp(int32_t id) const {
        auto* b = detail::backend();
        return b ? b->getVarp(id) : 0;
    }
    std::optional<int32_t> varClientInt(int32_t id) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        int32_t value = 0;
        return b->getVarClientInt(id, &value)
            ? std::optional<int32_t>{value} : std::nullopt;
    }
    bool setVarClientInt(int32_t id, int32_t value) const {
        auto* b = detail::backend();
        return b && b->setVarClientInt(id, value) != 0;
    }
    std::optional<std::string> varClientString(int32_t id) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;

        uint32_t required = b->getVarClientString(id, nullptr, 0);
        if (required == 0 ||
            required > TitanPluginSdk::kMaxVarClientStringBytes) {
            return std::nullopt;
        }

        std::vector<char> buffer(required);
        for (int attempt = 0; attempt < 2; ++attempt) {
            const uint32_t actual = b->getVarClientString(
                id, buffer.data(), static_cast<uint32_t>(buffer.size()));
            if (actual == 0 ||
                actual > TitanPluginSdk::kMaxVarClientStringBytes) {
                return std::nullopt;
            }
            if (actual <= buffer.size()) {
                return std::string(buffer.data(), actual - 1);
            }
            buffer.assign(actual, '\0');
        }
        return std::nullopt;
    }
    bool setVarClientString(int32_t id, const char* value) const {
        auto* b = detail::backend();
        return b && value && b->setVarClientString(id, value) != 0;
    }
    bool setVarClientString(int32_t id, const std::string& value) const {
        return setVarClientString(id, value.c_str());
    }
    std::optional<int64_t> varClientLong(int32_t id) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        int64_t value = 0;
        return b->getVarClientLong(id, &value)
            ? std::optional<int64_t>{value} : std::nullopt;
    }
    bool setVarClientLong(int32_t id, int64_t value) const {
        auto* b = detail::backend();
        return b && b->setVarClientLong(id, value) != 0;
    }
};

namespace state { inline ::titan::VarsFacade vars() { return ::titan::VarsFacade{}; } }

// ---------------------------------------------------------------------------
// Skills (boosted / real / experience) -- SDK v37+
// ---------------------------------------------------------------------------

/// Skill level and XP reads. Both int-ordinal and `Skill`-typed overloads
/// exist; prefer the typed form in new code. All methods route to the
/// existing `IBackend::getBoostedSkillLevel` / `getRealSkillLevel` /
/// `getSkillExperience` virtuals -- no ABI change from the previous
/// VarsFacade versions, just a clean domain split.
class SkillsFacade {
public:
    int32_t boosted(int32_t skillId) const {
        auto* b = detail::backend();
        return b ? b->getBoostedSkillLevel(skillId) : 0;
    }
    int32_t real(int32_t skillId) const {
        auto* b = detail::backend();
        return b ? b->getRealSkillLevel(skillId) : 0;
    }
    int32_t experience(int32_t skillId) const {
        auto* b = detail::backend();
        return b ? b->getSkillExperience(skillId) : 0;
    }

    int32_t boosted(Skill s) const    { return boosted(static_cast<int32_t>(s)); }
    int32_t real(Skill s) const       { return real(static_cast<int32_t>(s)); }
    int32_t experience(Skill s) const { return experience(static_cast<int32_t>(s)); }
};

namespace state { inline ::titan::SkillsFacade skills() { return ::titan::SkillsFacade{}; } }

// ---------------------------------------------------------------------------
// Prayers (active-state predicate) -- SDK v37+
// ---------------------------------------------------------------------------

/// Per-prayer active-state queries. Both int-ordinal (for when callers
/// iterate raw varbit IDs) and `Prayer`-typed overloads are supported.
/// Routes to the existing `IBackend::isPrayerActive` virtual.
class PrayersFacade {
public:
    bool isActive(int32_t prayerOrdinal) const {
        auto* b = detail::backend();
        return b && b->isPrayerActive(prayerOrdinal) != 0;
    }
    bool isActive(Prayer p) const { return isActive(static_cast<int32_t>(p)); }
};

namespace state { inline ::titan::PrayersFacade prayers() { return ::titan::PrayersFacade{}; } }

// ---------------------------------------------------------------------------
// Script (CS2 script runner + quest state)
// ---------------------------------------------------------------------------

struct Cs2Result {
    bool success = false;
    std::vector<int32_t> ints;
};

class ScriptFacade {
public:
    std::optional<Cs2Result> run(int32_t scriptId, std::vector<int32_t> intArgs = {}) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::Cs2ScriptResult out = {};
        if (!b->runClientScript(scriptId, intArgs.data(),
                                static_cast<uint32_t>(intArgs.size()), &out)) {
            return std::nullopt;
        }
        Cs2Result r;
        r.success = out.success != 0;
        const uint32_t n = (std::min)(out.intCount,
            TitanPluginSdk::kMaxCs2IntResults);
        r.ints.assign(out.ints, out.ints + n);
        return r;
    }

    /// Convenience wrapper returning the first integer result.
    std::optional<int32_t> runAndGetInt(int32_t scriptId, std::vector<int32_t> intArgs = {}) const {
        auto res = run(scriptId, std::move(intArgs));
        if (!res || !res->success || res->ints.empty()) return std::nullopt;
        return res->ints.front();
    }

    /// Returns: 0 = in progress, 1 = not started, 2 = finished, -1 = error.
    int32_t questState(int32_t questId) const {
        auto* b = detail::backend();
        return b ? b->getQuestState(questId) : -1;
    }
};

namespace state { inline ::titan::ScriptFacade script() { return ::titan::ScriptFacade{}; } }

// ---------------------------------------------------------------------------
// Widgets
// ---------------------------------------------------------------------------

class WidgetQuery;
class Widget;

/// Explicit frozen widget value. Use `Widget::snapshot()` when a plugin needs
/// a stable copy instead of a live handle.
struct WidgetSnapshot {
    /// Packed widget id `(groupId << 16) | childId`. Populated by the host
    /// inside `TitanPluginSdk::WidgetState::packedId`; falls back to the
    /// lookup key when talking to a pre-SDK-36 client.
    int32_t packedId = 0;
    /// Dynamic-child address context. Populated only by
    /// `WidgetsFacade::children(parentPackedId)`.
    int32_t dynamicParentPackedId = 0;
    int32_t dynamicChildSlot = -1;
    /// Root flat-table widget plus recursively retained dynamic-child slots.
    /// Query snapshots use this path so interactions and text writes resolve
    /// the exact live widget again at dispatch time.
    int32_t rootPackedId = 0;
    std::vector<int32_t> dynamicPath;
    int32_t screenX = 0, screenY = 0;
    int32_t width = 0, height = 0;
    int32_t relativeX = 0, relativeY = 0;
    int32_t scrollX = 0, scrollY = 0;
    int32_t type = 0;
    int32_t contentType = 0;
    int32_t spriteId = -1;
    /// Model source kind and the id it selects (SDK v146+). See
    /// `TitanPluginSdk::WidgetState::modelType`; -1 when unavailable.
    int32_t modelType = -1;
    int32_t modelId = -1;
    int32_t opacity = 0;
    int32_t itemId = -1;
    int32_t itemQuantity = 0;
    int32_t parentId = -1;
    bool hidden = false;
    bool selfHidden = false;
    bool visible = false;
    std::string text;

    /// Dispatch against one exact direct dynamic child beneath this snapshot.
    ///
    /// @param opcode    MenuAction opcode (57 = CC_OP, 1007 = CC_OP_LOW,
    ///                  25 = WIDGET_TARGET, 58 = WIDGET_TARGET_ON_WIDGET).
    /// @param identifier Menu-entry identifier -- the CC_OP sub-action index,
    ///                   0 for non-CC_OP opcodes. Not a widget packed id.
    /// @param param0    Native dynamic-child slot beneath this snapshot.
    /// @return true when the action was queued.
    bool interact(uint32_t opcode, int32_t identifier, int32_t param0) const;

    /// Dispatch against this exact snapshot. Flat widgets emit param0=-1;
    /// dynamic snapshots emit their retained immediate-parent slot.
    bool interact(uint32_t opcode, int32_t identifier) const;

    /// Replace this widget's live display text. Dynamic snapshots returned by
    /// `children(parentPackedId)` route through their remembered parent and
    /// exact native slot. Ordinary snapshots retain packed-id lookup behavior.
    /// Current hosts accept up to 256 UTF-8 bytes through native EASTL range
    /// assignment. Older offset bundles retain the <= 22-byte inline fallback.
    bool setText(const char* text) const;
    bool setText(const std::string& text) const { return setText(text.c_str()); }
    template <typename SpellT>
    bool castOn(SpellT spell) const;

    TitanPluginSdk::WidgetAddressState addressState() const {
        TitanPluginSdk::WidgetAddressState address = {};
        address.rootPackedId = rootPackedId != 0
            ? static_cast<uint32_t>(rootPackedId)
            : static_cast<uint32_t>(packedId);
        address.depth = dynamicPath.size() > TitanPluginSdk::kMaxWidgetAddressDepth
            ? TitanPluginSdk::kMaxWidgetAddressDepth + 1
            : static_cast<uint32_t>(dynamicPath.size());
        const uint32_t copiedDepth = (std::min)(
            address.depth, TitanPluginSdk::kMaxWidgetAddressDepth);
        for (uint32_t i = 0; i < copiedDepth; ++i) {
            address.slots[i] = dynamicPath[i];
        }
        return address;
    }
};

namespace detail {

inline WidgetSnapshot widgetSnapshotFromState(const TitanPluginSdk::WidgetState& s,
                                              uint32_t fallbackPackedId) {
    WidgetSnapshot w;
    w.packedId = s.packedId != 0 ? s.packedId
                                 : static_cast<int32_t>(fallbackPackedId);
    w.screenX = s.screenX; w.screenY = s.screenY;
    w.width = s.width; w.height = s.height;
    w.relativeX = s.relativeX; w.relativeY = s.relativeY;
    w.scrollX = s.scrollX; w.scrollY = s.scrollY;
    w.type = s.type; w.contentType = s.contentType;
    w.spriteId = s.spriteId;
    w.modelType = s.modelType; w.modelId = s.modelId;
    w.opacity = s.opacity; w.itemId = s.itemId;
    w.itemQuantity = s.itemQuantity; w.parentId = s.parentId;
    w.hidden = s.hidden != 0;
    w.selfHidden = s.selfHidden != 0;
    w.visible = s.visible != 0;
    w.text = s.text;
    return w;
}

inline WidgetSnapshot widgetSnapshotFromQueryState(
        const TitanPluginSdk::WidgetQueryState& state) {
    WidgetSnapshot widget = widgetSnapshotFromState(
        state.widget, static_cast<uint32_t>(state.widget.packedId));
    widget.rootPackedId = static_cast<int32_t>(state.address.rootPackedId);
    const uint32_t depth = (std::min)(
        state.address.depth, TitanPluginSdk::kMaxWidgetAddressDepth);
    widget.dynamicPath.assign(state.address.slots, state.address.slots + depth);
    widget.dynamicChildSlot = depth > 0 ? state.address.slots[depth - 1] : -1;
    widget.dynamicParentPackedId = depth == 1
        ? static_cast<int32_t>(state.address.rootPackedId) : 0;
    return widget;
}

inline bool sameWidgetAddress(const WidgetSnapshot& a, const WidgetSnapshot& b) {
    const int32_t aRoot = a.rootPackedId != 0 ? a.rootPackedId : a.packedId;
    const int32_t bRoot = b.rootPackedId != 0 ? b.rootPackedId : b.packedId;
    return aRoot == bRoot && a.dynamicPath == b.dynamicPath;
}

inline bool readCurrentWidgetSnapshot(WidgetSnapshot& widget) {
    auto* b = backend();
    if (!b) return false;

    const uint32_t root = static_cast<uint32_t>(
        widget.rootPackedId != 0 ? widget.rootPackedId : widget.packedId);
    if (root == 0) return false;

    TitanPluginSdk::WidgetAddressState address = {};
    address.rootPackedId = root;
    if (widget.dynamicPath.size() <= TitanPluginSdk::kMaxWidgetAddressDepth) {
        address.depth = static_cast<uint32_t>(widget.dynamicPath.size());
        for (uint32_t i = 0; i < address.depth; ++i) {
            address.slots[i] = widget.dynamicPath[i];
        }
        TitanPluginSdk::WidgetState raw = {};
        if (b->getWidgetAtPath(&address, &raw)) {
            WidgetSnapshot fresh = widgetSnapshotFromState(raw, root);
            fresh.rootPackedId = static_cast<int32_t>(root);
            fresh.dynamicPath = widget.dynamicPath;
            fresh.dynamicChildSlot = fresh.dynamicPath.empty()
                ? -1 : fresh.dynamicPath.back();
            fresh.dynamicParentPackedId = fresh.dynamicPath.size() == 1
                ? static_cast<int32_t>(root) : 0;
            widget = std::move(fresh);
            return true;
        }
    }

    if (widget.dynamicPath.empty()) {
        TitanPluginSdk::WidgetState raw = {};
        if (!b->getWidget(root, &raw)) return false;
        WidgetSnapshot fresh = widgetSnapshotFromState(raw, root);
        fresh.rootPackedId = fresh.packedId;
        widget = std::move(fresh);
        return true;
    }

    if (widget.dynamicPath.size() > TitanPluginSdk::kMaxWidgetAddressDepth) return false;

    TitanPluginSdk::WidgetAddressState parent = {};
    parent.rootPackedId = root;
    parent.depth = static_cast<uint32_t>(widget.dynamicPath.size() - 1);
    for (uint32_t i = 0; i < parent.depth; ++i) {
        parent.slots[i] = widget.dynamicPath[i];
    }

    const uint32_t reported = b->getWidgetChildrenAtPath(&parent, nullptr, 0);
    const uint32_t count = (std::min)(reported, TitanPluginSdk::kMaxWidgetDynamicChildren);
    if (count == 0) return false;

    std::vector<TitanPluginSdk::WidgetQueryState> raw(count);
    const uint32_t actual =
        b->getWidgetChildrenAtPath(&parent, raw.data(), static_cast<uint32_t>(raw.size()));
    const uint32_t n = (std::min)(actual, static_cast<uint32_t>(raw.size()));
    for (uint32_t i = 0; i < n; ++i) {
        WidgetSnapshot fresh = widgetSnapshotFromQueryState(raw[i]);
        if (sameWidgetAddress(fresh, widget)) {
            widget = std::move(fresh);
            return true;
        }
    }
    return false;
}

inline const WidgetSnapshot& liveWidgetState(WidgetSnapshot& state,
                                             int32_t& lastReadEpoch,
                                             bool& exists,
                                             bool live) {
    if (!live) return state;
    const int32_t epoch = liveStateEpoch();
    if (lastReadEpoch == epoch) return state;
    lastReadEpoch = epoch;
    exists = readCurrentWidgetSnapshot(state);
    return state;
}

inline std::size_t widgetAddressHash(const WidgetSnapshot& state) {
    std::size_t h = std::hash<int32_t>{}(
        state.rootPackedId != 0 ? state.rootPackedId : state.packedId);
    for (int32_t slot : state.dynamicPath) {
        h ^= std::hash<int32_t>{}(slot) + 0x9e3779b9u + (h << 6) + (h >> 2);
    }
    return h;
}

}  // namespace detail

class Widget {
public:
    Widget() = default;
    explicit Widget(WidgetSnapshot state, bool live = true)
        : state_(std::move(state)),
          lastReadEpoch_(live ? detail::liveStateEpoch()
                              : (std::numeric_limits<int32_t>::min)()),
          live_(live) {}

    int32_t packedId() const { return state().packedId; }
    int32_t dynamicParentPackedId() const { return state().dynamicParentPackedId; }
    int32_t dynamicChildSlot() const { return state().dynamicChildSlot; }
    int32_t rootPackedId() const { return state().rootPackedId; }
    const std::vector<int32_t>& dynamicPath() const { return state().dynamicPath; }
    int32_t screenX() const { return state().screenX; }
    int32_t screenY() const { return state().screenY; }
    int32_t width() const { return state().width; }
    int32_t height() const { return state().height; }
    int32_t relativeX() const { return state().relativeX; }
    int32_t relativeY() const { return state().relativeY; }
    int32_t scrollX() const { return state().scrollX; }
    int32_t scrollY() const { return state().scrollY; }
    int32_t type() const { return state().type; }
    int32_t contentType() const { return state().contentType; }
    int32_t spriteId() const { return state().spriteId; }
    int32_t modelType() const { return state().modelType; }
    int32_t modelId() const { return state().modelId; }
    int32_t opacity() const { return state().opacity; }
    int32_t itemId() const { return state().itemId; }
    int32_t itemQuantity() const { return state().itemQuantity; }
    int32_t parentId() const { return state().parentId; }
    bool hidden() const { return state().hidden; }
    bool selfHidden() const { return state().selfHidden; }
    bool visible() const { return state().visible; }
    const std::string& text() const { return state().text; }

    bool exists() const {
        (void)state();
        return exists_;
    }

    WidgetSnapshot snapshot() const {
        return state();
    }

    TitanPluginSdk::WidgetAddressState addressState() const {
        return state().addressState();
    }

    bool interact(uint32_t opcode, int32_t identifier, int32_t param0) const {
        return state().interact(opcode, identifier, param0);
    }

    bool interact(uint32_t opcode, int32_t identifier) const {
        return state().interact(opcode, identifier);
    }

    bool setText(const char* text) const {
        const bool ok = state().setText(text);
        if (ok) {
            state_.text = text ? text : "";
            lastReadEpoch_ = detail::liveStateEpoch();
            exists_ = true;
        }
        return ok;
    }
    bool setText(const std::string& text) const { return setText(text.c_str()); }
    template <typename SpellT>
    bool castOn(SpellT spell) const;

    bool operator==(const Widget& other) const {
        return detail::sameWidgetAddress(state_, other.state_);
    }
    bool operator!=(const Widget& other) const { return !(*this == other); }

    std::size_t hash() const { return detail::widgetAddressHash(state_); }

private:
    const WidgetSnapshot& state() const {
        return detail::liveWidgetState(state_, lastReadEpoch_, exists_, live_);
    }

    mutable WidgetSnapshot state_;
    mutable int32_t lastReadEpoch_ = (std::numeric_limits<int32_t>::min)();
    mutable bool exists_ = true;
    bool live_ = true;
};

class WidgetsFacade {
public:
    std::optional<Widget> find(uint32_t packedId) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::WidgetState s = {};
        if (!b->getWidget(packedId, &s)) return std::nullopt;
        WidgetSnapshot widget = detail::widgetSnapshotFromState(s, packedId);
        widget.rootPackedId = widget.packedId;
        return Widget{std::move(widget)};
    }

    /// Enumerate the dynamic children of the widget at @p parentPackedId.
    /// Returns one snapshot per native slot, ordered by the underlying dynamic-
    /// children array. Empty placeholders are preserved so slot indexes do not
    /// drift. Empty vector when the parent is missing, has no
    /// dynamic children, or the host is pre-SDK-38 (fn pointer null).
    /// Sized to the parent's true child count via the SDK-124 sizing probe,
    /// bounded at `kMaxWidgetDynamicChildren` (2048) per call.
    ///
    /// The child's position in the returned vector is the slot index that
    /// `WIDGET_CONTINUE` / dynamic-child `CC_OP` dispatches expect as
    /// `param0`. Most common use: iterating dialog-option children at
    /// `pack(219, 1)` to locate an option by text, then firing
    /// `interact(WIDGET_CONTINUE, 0, <slot>, pack(219, 1))`.
    std::vector<Widget> children(uint32_t parentPackedId) const {
        auto* b = detail::backend();
        if (!b) return {};

        // Count-then-fill. The (nullptr, 0) sizing probe is safe to rely on
        // because the loader rejects plugins built against a newer SDK than
        // the host, so a v124+ header never runs on a host whose
        // getWidgetChildren still returns 0 for the probe form.
        const uint32_t reported = b->getWidgetChildren(parentPackedId, nullptr, 0);
        const uint32_t cap = (std::min)(
            reported, TitanPluginSdk::kMaxWidgetDynamicChildren);
        if (cap == 0) return {};

        std::vector<TitanPluginSdk::WidgetState> raw(cap);
        const uint32_t n = (std::min)(
            b->getWidgetChildren(parentPackedId, raw.data(), cap), cap);
        if (n == 0) return {};

        std::vector<Widget> out;
        out.reserve(n);
        const uint32_t parentGroup = (parentPackedId >> 16) & 0xFFFFu;
        for (uint32_t i = 0; i < n; ++i) {
            // Fall back to `(parentGroup << 16) | i` so callers always see a
            // non-zero id even on hosts where the analyzer skipped
            // packedComponentId population for dynamic children.
            const uint32_t fallback = (parentGroup << 16) | i;
            WidgetSnapshot child = detail::widgetSnapshotFromState(raw[i], fallback);
            child.dynamicParentPackedId = static_cast<int32_t>(parentPackedId);
            child.dynamicChildSlot = static_cast<int32_t>(i);
            child.rootPackedId = static_cast<int32_t>(parentPackedId);
            child.dynamicPath.push_back(static_cast<int32_t>(i));
            out.emplace_back(std::move(child));
        }
        return out;
    }

    /// First widget whose *primary* text contains @p query (case-sensitive
    /// substring; same walk as `WidgetReader::findByText`). Nullopt when
    /// @p query is empty, nothing matched, or the host is pre-SDK-39.
    std::optional<Widget> findByText(const char* query) const {
        auto* b = detail::backend();
        if (!b || !query || !query[0]) return std::nullopt;
        TitanPluginSdk::WidgetState s = {};
        if (!b->getWidgetByText(query, &s)) return std::nullopt;
        const uint32_t fb = s.packedId != 0
            ? static_cast<uint32_t>(s.packedId) : 0u;
        WidgetSnapshot widget = detail::widgetSnapshotFromState(s, fb);
        widget.rootPackedId = widget.packedId;
        return Widget{std::move(widget)};
    }

    /// Packed widget ID helper.
    static constexpr uint32_t pack(uint32_t group, uint32_t child) {
        return (group << 16) | child;
    }

    /// Dispatch a widget-family action (CC_OP, CC_OP_LOW,
    /// WIDGET_TARGET, WIDGET_TARGET_ON_WIDGET, ...) against a widget slot.
    ///
    /// @param opcode   MenuAction opcode (57 = CC_OP, 1007 = CC_OP_LOW,
    ///                 25 = WIDGET_TARGET,
    ///                 58 = WIDGET_TARGET_ON_WIDGET, ...).
    /// @param identifier Menu-entry identifier -- the CC_OP sub-action index,
    ///                   or 0 for non-CC_OP opcodes. Not a widget packed id.
    /// @param param0   Dynamic-child slot index on the target widget, or -1
    ///                 for "whole widget / no slot". Inventory-style
    ///                 containers use this to identify the item slot.
    /// @param param1   Packed widget id `(groupId << 16) | childId` -- the
    ///                 parent component the action targets.
    /// @return true when the action was queued for the next client tick.
    ///         Does not guarantee the game accepted it (wrong combos may
    ///         silently no-op on the native side).
    bool interact(uint32_t opcode, int32_t identifier,
                  int32_t param0, int32_t param1) const {
        auto* b = detail::backend();
        return b && b->widgetInteract(opcode, identifier, param0, param1) != 0;
    }

    /// Replace the live display text on the widget identified by @p packedId.
    /// Returns true when the host applied or accepted the write for the game
    /// thread. Current hosts accept up to 256 UTF-8 bytes; older offset bundles
    /// retain the <= 22-byte inline fallback.
    bool setText(uint32_t packedId, const char* text) const {
        auto* b = detail::backend();
        return b && b->setWidgetText(packedId, text ? text : "") != 0;
    }
    bool setText(uint32_t packedId, const std::string& text) const {
        return setText(packedId, text.c_str());
    }

    /// Replace an exact dynamic child's live display text.
    bool setText(uint32_t parentPackedId, int32_t slot, const char* text) const {
        auto* b = detail::backend();
        return b && b->setWidgetTextAtSlot(parentPackedId, slot, text ? text : "") != 0;
    }
    bool setText(uint32_t parentPackedId, int32_t slot,
                 const std::string& text) const {
        return setText(parentPackedId, slot, text.c_str());
    }

private:
    friend class WidgetQuery;
};

namespace state { inline ::titan::WidgetsFacade widgets() { return ::titan::WidgetsFacade{}; } }

inline bool WidgetSnapshot::interact(uint32_t opcode, int32_t identifier,
                                     int32_t param0) const {
    auto* b = detail::backend();
    if (!b || dynamicPath.size() > TitanPluginSdk::kMaxWidgetAddressDepth) return false;
    const auto address = addressState();
    return b->widgetInteractAtPath(&address, opcode, identifier, param0) != 0;
}

inline bool WidgetSnapshot::interact(uint32_t opcode, int32_t identifier) const {
    auto* b = detail::backend();
    if (!b || dynamicPath.size() > TitanPluginSdk::kMaxWidgetAddressDepth) return false;
    auto address = addressState();
    int32_t childSlot = -1;
    // Keep the final dynamic slot explicit so it becomes native param0.
    if (dynamicChildSlot >= 0) {
        if (address.depth == 0 ||
                address.slots[address.depth - 1] != dynamicChildSlot) {
            return false;
        }
        childSlot = dynamicChildSlot;
        --address.depth;
    }
    return b->widgetInteractAtPath(&address, opcode, identifier, childSlot) != 0;
}

inline bool WidgetSnapshot::setText(const char* text) const {
    auto* b = detail::backend();
    if (!b || dynamicPath.size() > TitanPluginSdk::kMaxWidgetAddressDepth) return false;
    const auto address = addressState();
    return b->setWidgetTextAtPath(&address, text ? text : "") != 0;
}

// ---------------------------------------------------------------------------
// Idle timer
// ---------------------------------------------------------------------------

class IdleFacade {
public:
    /// Milliseconds remaining before idle logout. -1 if unavailable.
    int32_t remaining() const {
        auto* b = detail::backend();
        return b ? b->getIdleTimeRemaining() : -1;
    }
    /// Reset the idle timer to prevent AFK logout.
    void reset() {
        if (auto* b = detail::backend()) b->resetIdleTimer();
    }
};

namespace state { inline ::titan::IdleFacade idle() { return ::titan::IdleFacade{}; } }

// ---------------------------------------------------------------------------
// Sanitized proxy route control
// ---------------------------------------------------------------------------

enum class ProxyRouteKind : uint32_t {
    Direct = TitanPluginSdk::PROXY_ROUTE_DIRECT,
    Proxy = TitanPluginSdk::PROXY_ROUTE_PROXY,
    Blocked = TitanPluginSdk::PROXY_ROUTE_BLOCKED,
};

struct SanitizedProxyRoute {
    std::string id;
    std::string label;
};

struct ProxyRouteStatus {
    uint64_t generation = 0;
    ProxyRouteKind kind = ProxyRouteKind::Direct;
    int32_t failureCode = 0;
    std::string proxyId;
    std::string failureStage;
    bool egressReady = false;
};

class ProxyFacade {
public:
    std::vector<SanitizedProxyRoute> list() const {
        std::vector<SanitizedProxyRoute> result;
        auto* backend = detail::backend();
        if (!backend) return result;
        uint32_t capacity = backend->listSanitizedProxyRoutes(nullptr, 0);
        if (!capacity) return result;
        for (int attempt = 0; attempt < 3; ++attempt) {
            std::vector<TitanPluginSdk::SanitizedProxyRouteState> raw(capacity);
            for (auto& row : raw) {
                row.structSize = sizeof(row);
                row.apiVersion = TitanPluginSdk::kProxyRouteApiVersion;
            }
            const uint32_t count = backend->listSanitizedProxyRoutes(raw.data(), capacity);
            if (count > capacity) {
                capacity = count;
                continue;
            }
            result.reserve(count);
            for (uint32_t i = 0; i < count; ++i) {
                result.push_back({raw[i].proxyId, raw[i].label});
            }
            break;
        }
        return result;
    }

    std::optional<ProxyRouteStatus> setRoute(const std::string& proxyId) const {
        auto* backend = detail::backend();
        if (!backend) return std::nullopt;
        TitanPluginSdk::ProxyRouteStatusState raw{};
        raw.structSize = sizeof(raw);
        raw.apiVersion = TitanPluginSdk::kProxyRouteApiVersion;
        if (!backend->setProxyRoute(proxyId.c_str(), &raw)) return std::nullopt;
        return fromAbi(raw);
    }

    std::optional<ProxyRouteStatus> status() const {
        auto* backend = detail::backend();
        if (!backend) return std::nullopt;
        TitanPluginSdk::ProxyRouteStatusState raw{};
        raw.structSize = sizeof(raw);
        raw.apiVersion = TitanPluginSdk::kProxyRouteApiVersion;
        if (!backend->getProxyRouteStatus(&raw)) return std::nullopt;
        return fromAbi(raw);
    }

private:
    static ProxyRouteStatus fromAbi(const TitanPluginSdk::ProxyRouteStatusState& raw) {
        ProxyRouteStatus out;
        out.generation = raw.generation;
        out.kind = static_cast<ProxyRouteKind>(raw.kind);
        out.failureCode = raw.failureCode;
        out.proxyId = raw.proxyId;
        out.failureStage = raw.failureStage;
        out.egressReady = raw.egressReady != 0;
        return out;
    }
};

namespace state { inline ::titan::ProxyFacade proxy() { return ::titan::ProxyFacade{}; } }

// ---------------------------------------------------------------------------
// Login / account switch (Super Profiles style)
// ---------------------------------------------------------------------------

/// Native client game state values exposed by `Client.getGameState()`.
enum class LoginGameState {
    Unknown = TitanPluginSdk::LoginGameStateAbi::UNKNOWN,
    LoginScreen = TitanPluginSdk::LoginGameStateAbi::LOGIN_SCREEN,
    LoginAuthenticator = TitanPluginSdk::LoginGameStateAbi::LOGIN_AUTHENTICATOR,
    LoggingIn = TitanPluginSdk::LoginGameStateAbi::LOGGING_IN,
    Loading = TitanPluginSdk::LoginGameStateAbi::LOADING,
    LoggedIn = TitanPluginSdk::LoginGameStateAbi::LOGGED_IN,
    HoppingWorld = TitanPluginSdk::LoginGameStateAbi::HOPPING,
};

enum class LoginUiAdvance : uint8_t {
    notFound = 0,
    pending = 1,
    dispatched = 2,
};

enum class LoginOperationAdvance : uint8_t {
    unavailable = TitanPluginSdk::LoginOperationAdvanceAbi::UNAVAILABLE,
    pending = TitanPluginSdk::LoginOperationAdvanceAbi::PENDING,
    completed = TitanPluginSdk::LoginOperationAdvanceAbi::COMPLETED,
    failed = TitanPluginSdk::LoginOperationAdvanceAbi::FAILED,
};

/// Credential-free orchestration view. Safe for Break Handler and other
/// coordinators that must never receive account identifiers or secrets.
struct LoginFlowSnapshot {
    int32_t loginIndex = -1;
    LoginGameState gameState = LoginGameState::Unknown;
    bool worldReady = false;
    bool oauthSetterAvailable = false;
    bool sessionSetterAvailable = false;
    bool launcherSubmitAvailable = false;
    bool standardAcknowledgeAvailable = false;
    int32_t jagexLauncherIndex = 0;
    uint64_t transitionGeneration = 0;
    uint64_t loggingInGeneration = 0;
    uint64_t failedLoginGeneration = 0;
};

struct LoginSnapshot {
    int32_t loginIndex = -1;
    LoginGameState gameState = LoginGameState::Unknown;
    int32_t fieldToggle = -1;           ///< 0=username, 1=password, -1=unknown
    bool oauthSwitchAvailable = false;
    bool credentialSetAvailable = false;
    bool displayNameAvailable = false;
    bool oauthSetterAvailable = false;
    bool sessionSetterAvailable = false;
    bool launcherSubmitAvailable = false;
    bool standardAcknowledgeAvailable = false;
    int32_t jagexLauncherIndex = 0;
    uint64_t transitionGeneration = 0;
    uint64_t loggingInGeneration = 0;
    uint64_t failedLoginGeneration = 0;
    std::string username;
    std::string displayName;
};

class LoginFacade {
public:
    std::optional<LoginFlowSnapshot> flow() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::LoginFlowState raw{};
        if (!b->getLoginFlowState(&raw)) return std::nullopt;
        LoginFlowSnapshot out;
        out.loginIndex = raw.loginIndex;
        out.gameState = static_cast<LoginGameState>(raw.gameState);
        out.worldReady = raw.worldReady != 0;
        out.oauthSetterAvailable = raw.oauthSetterAvailable != 0;
        out.sessionSetterAvailable = raw.sessionSetterAvailable != 0;
        out.launcherSubmitAvailable = raw.launcherSubmitAvailable != 0;
        out.standardAcknowledgeAvailable =
            raw.standardAcknowledgeAvailable != 0;
        out.jagexLauncherIndex = raw.jagexLauncherIndex;
        out.transitionGeneration = raw.transitionGeneration;
        out.loggingInGeneration = raw.loggingInGeneration;
        out.failedLoginGeneration = raw.failedLoginGeneration;
        return out;
    }

    std::optional<LoginSnapshot> snapshot() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::LoginAccountState s{};
        if (!b->getLoginAccountState(&s)) return std::nullopt;
        LoginSnapshot out;
        out.loginIndex = s.loginIndex;
        out.gameState = static_cast<LoginGameState>(s.gameState);
        out.fieldToggle = s.fieldToggle;
        out.oauthSwitchAvailable = s.oauthSwitchAvailable != 0;
        out.credentialSetAvailable = s.credentialSetAvailable != 0;
        out.displayNameAvailable = s.displayNameAvailable != 0;
        out.username = s.username;
        out.displayName = s.displayName;
        if (const auto sanitized = flow()) {
            out.oauthSetterAvailable = sanitized->oauthSetterAvailable;
            out.sessionSetterAvailable = sanitized->sessionSetterAvailable;
            out.launcherSubmitAvailable = sanitized->launcherSubmitAvailable;
            out.standardAcknowledgeAvailable =
                sanitized->standardAcknowledgeAvailable;
            out.jagexLauncherIndex = sanitized->jagexLauncherIndex;
            out.transitionGeneration = sanitized->transitionGeneration;
            out.loggingInGeneration = sanitized->loggingInGeneration;
            out.failedLoginGeneration = sanitized->failedLoginGeneration;
        } else {
            // SDK-96/legacy host fallback: the aggregate flag required both
            // Jagex setters and a live wrapper.
            out.oauthSetterAvailable = out.oauthSwitchAvailable;
            out.sessionSetterAvailable = out.oauthSwitchAvailable;
            out.launcherSubmitAvailable = out.oauthSwitchAvailable;
        }
        return out;
    }

    LoginGameState state() const {
        return flow().value_or(LoginFlowSnapshot{}).gameState;
    }
    int32_t index() const {
        return flow().value_or(LoginFlowSnapshot{}).loginIndex;
    }
    bool isWorldReady() const {
        return flow().value_or(LoginFlowSnapshot{}).worldReady;
    }
    bool isLoggedIn() const { return state() == LoginGameState::LoggedIn; }

    void setUsername(const std::string& v) const {
        if (auto* b = detail::backend()) b->setLoginUsername(v.c_str());
    }
    void setPassword(const std::string& v) const {
        if (auto* b = detail::backend()) b->setLoginPassword(v.c_str());
    }
    void setAuthenticator(const std::string& v) const {
        if (auto* b = detail::backend()) b->setLoginAuthenticator(v.c_str());
    }
    void setIndex(int32_t v) const {
        if (auto* b = detail::backend()) b->setLoginIndex(v);
    }
    void setDisplayName(const std::string& v) const {
        if (auto* b = detail::backend()) b->setLoginDisplayName(v.c_str());
    }
    void setOAuth2Credentials(const std::string& access, const std::string& refresh) const {
        if (auto* b = detail::backend()) b->setLoginOAuth2Credentials(access.c_str(), refresh.c_str());
    }
    void setGameSessionCredentials(const std::string& session, const std::string& character) const {
        if (auto* b = detail::backend()) b->setLoginGameSessionCredentials(session.c_str(), character.c_str());
    }
    /// Composite setter matching the RuneLite `ReflectionMethods.setCharacter`.
    void setCharacter(const std::string& displayName,
                      const std::string& characterId,
                      const std::string& sessionId) const {
        if (auto* b = detail::backend())
            b->setLoginCharacter(displayName.c_str(), characterId.c_str(), sessionId.c_str());
    }
    /// Clear every Jagex token and flip back to the standard login screen.
    void resetCharacter() const {
        if (auto* b = detail::backend()) b->resetLoginCharacter();
    }
    /// Resolve an exact Account Profiles label and queue its credentials.
    /// The encrypted profile secrets remain owned by Account Profiles.
    bool stageCredentials(const std::string& profileName) const {
        if (auto* b = detail::backend()) {
            return b->stageLoginCredentials(profileName.c_str()) != 0;
        }
        return false;
    }
    /// Press Enter on either supported credential screen, holding it through
    /// one native MainLoop update before the host releases it.
    bool submitCredentials() const {
        if (auto* b = detail::backend()) return b->submitLoginCredentials() != 0;
        return false;
    }
    /// Queue a guarded Enter keypress on the Jagex launcher screen. The
    /// historical method name and ABI are retained for compatibility.
    bool submitLauncherCredentials() const {
        if (auto* b = detail::backend()) return b->submitLoginLauncherCredentials() != 0;
        return false;
    }
    /// Start or poll the launcher Enter submission. Post-authentication
    /// click-to-play remains a separate operation.
    LoginOperationAdvance advanceLauncherCredentials() const {
        if (auto* b = detail::backend()) {
            return static_cast<LoginOperationAdvance>(
                b->advanceLoginLauncherCredentials());
        }
        return LoginOperationAdvance::unavailable;
    }
    bool submitStandardCredentials() const {
        if (auto* b = detail::backend()) return b->submitLoginStandardCredentials() != 0;
        return false;
    }
    bool acknowledgeStandardLogin() const {
        if (auto* b = detail::backend()) return b->acknowledgeStandardLogin() != 0;
        return false;
    }
    LoginUiAdvance advanceClickToPlay() const {
        if (auto* b = detail::backend()) {
            return static_cast<LoginUiAdvance>(b->advanceLoginClickToPlay());
        }
        return LoginUiAdvance::notFound;
    }
    LoginOperationAdvance advanceLogout() const {
        if (auto* b = detail::backend()) {
            return static_cast<LoginOperationAdvance>(b->advanceLoginLogout());
        }
        return LoginOperationAdvance::unavailable;
    }
    void cancelProfileOperations() const {
        if (auto* b = detail::backend()) b->cancelLoginProfileOperations();
    }
    void cancelLogoutOperation() const {
        if (auto* b = detail::backend()) b->cancelLoginLogoutOperation();
    }
};

namespace state { inline ::titan::LoginFacade login() { return ::titan::LoginFacade{}; } }

// ---------------------------------------------------------------------------
// Walk (scene or world-space)
// ---------------------------------------------------------------------------

class WalkFacade {
public:
    bool toScene(int32_t sceneX, int32_t sceneY) const {
        auto* b = detail::backend();
        return b && b->walkTo(sceneX, sceneY) != 0;
    }
    bool toWorld(int32_t worldX, int32_t worldY, int32_t plane) const {
        auto* b = detail::backend();
        return b && b->walkToWorld(worldX, worldY, plane) != 0;
    }
    bool to(const Tile& t) const {
        auto* b = detail::backend();
        TitanPluginSdk::ClientState state{};
        if (!b || !b->getClientState(&state) || t.plane != state.plane ||
            (t.worldViewId != WorldView::CURRENT &&
             t.worldViewId != state.currentWorldViewId)) return false;
        return b->walkTo(t.x, t.y) != 0;
    }
    bool to(const WorldPos& p) const {
        if (p.worldViewId != WorldView::CURRENT &&
            !detail::sameWorldViewId(p.worldViewId, WorldView::CURRENT)) return false;
        return toWorld(p.x, p.y, p.z);
    }
};

namespace state { inline ::titan::WalkFacade walk() { return ::titan::WalkFacade{}; } }

// ---------------------------------------------------------------------------
// Item containers (SDK 26) -- backed by the native ClientInvCache hashtable.
// ---------------------------------------------------------------------------

struct ItemContainerSlot {
    int32_t slot = -1;
    int32_t itemId = -1;
    int32_t quantity = 0;
};

struct ItemContainerSnapshot {
    int32_t containerId = -1;
    int32_t capacity = 0;
    std::vector<ItemContainerSlot> items;  ///< occupied slots only
};

namespace state {

/// Read a snapshot of the requested container. Returns nullopt when the
/// native cache has no matching entry or its analyzer-provided layout fails
/// validation. Plugin code should treat nullopt as "unavailable", not "empty".
inline std::optional<::titan::ItemContainerSnapshot> itemContainer(int32_t containerId) {
    auto* b = ::titan::detail::backend();
    if (!b) return std::nullopt;
    TitanPluginSdk::ItemContainerState s{};
    if (!b->getItemContainer(containerId, &s)) return std::nullopt;
    ::titan::ItemContainerSnapshot out;
    out.containerId = s.containerId;
    out.capacity = s.capacity;
    const int32_t n = s.writtenCount > 0
        ? (std::min)(s.writtenCount,
            static_cast<int32_t>(TitanPluginSdk::kMaxItemContainerSlots))
        : 0;
    out.items.reserve(n);
    for (int32_t i = 0; i < n; ++i) {
        out.items.push_back({ s.slots[i], s.itemIds[i], s.quantities[i] });
    }
    return out;
}

}  // namespace state

// ---------------------------------------------------------------------------
// Runtime ItemDef (SDK 26) -- mirrors RuneLite's Client.getItemDefinition.
// Game-thread calls may invoke ITEM_DEF_LOOKUP on a live-table miss. Off-thread
// calls use only the read-only live table (a rate-limited warning fires only
// when that table misses), then fall back to raw JS5 cache metadata when
// absent. Check `runtimeResolved` to distinguish the sources.
// ---------------------------------------------------------------------------

struct ItemComposition {
    using SubOps = std::array<
        std::array<std::string, TitanPluginSdk::kMaxItemSubOpCount>,
        TitanPluginSdk::kMaxActionCount>;

    int32_t id = 0;
    std::string name;
    bool stackable = false;
    /// The other item id in the note pair, in either direction:
    /// unnoted -> noted, noted -> unnoted. -1 when there is no note pair.
    int32_t linkedNoteId = -1;
    /// Runtime inventory-action slots with positional gaps preserved. The
    /// cache fallback returns the raw 5-slot cache encoding.
    std::vector<std::string> inventoryActions;
    /// Opcode-43 submenu labels indexed by inventory action then submenu
    /// slot. Empty strings preserve the fixed 5 x 20 positional shape.
    SubOps subOps;
    /// True when the snapshot came from the live table/native resolver; false
    /// when it came from raw cache metadata.
    bool runtimeResolved = false;
};

namespace state {

/// Resolve a runtime ItemDef. Off-thread calls are limited to a read-only
/// live-table read (warned only on a miss) followed by raw cache fallback; they
/// never invoke native resolution.
/// Returns nullopt when neither source contains the id.
inline std::optional<::titan::ItemComposition> itemDef(int32_t itemId) {
    auto* b = ::titan::detail::backend();
    if (!b) return std::nullopt;
    TitanPluginSdk::ItemCompositionState s{};
    if (!b->getItemComposition(itemId, &s)) return std::nullopt;
    ::titan::ItemComposition out;
    out.id = s.id;
    out.name = s.name;
    out.stackable = s.stackable != 0;
    out.linkedNoteId = s.linkedNoteId;
    out.runtimeResolved = s.runtimeResolved != 0;
    out.inventoryActions.reserve(s.inventoryActionsCount);
    for (uint32_t i = 0; i < s.inventoryActionsCount; ++i) {
        out.inventoryActions.emplace_back(s.inventoryActions[i]);
    }
    for (uint32_t parent = 0;
         parent < TitanPluginSdk::kMaxActionCount; ++parent) {
        for (uint32_t child = 0;
             child < TitanPluginSdk::kMaxItemSubOpCount; ++child) {
            out.subOps[parent][child] = s.subOps[parent][child];
        }
    }
    return out;
}

}  // namespace state

// ---------------------------------------------------------------------
// Worlds (SDK 28)
// ---------------------------------------------------------------------

/// Snapshot of a single world entry from the game's native m_list.
/// Members:
/// - `id`: the world number shown in the title-screen world switcher.
/// - `flags`: raw GameWorld flags int (bit 0 = members, bit 16 = beta).
/// - `string0` / `string1`: the two eastl::basic_string fields on the
///   native entry. Which one is "activity" vs "location" depends on
///   revision; we deliberately don't guess.
struct World {
    int32_t id = 0;
    uint32_t flags = 0;
    std::string string0;
    std::string string1;

    bool isMembers() const { return (flags & 0x1u) != 0u; }
    bool isBeta()    const { return (flags & 0x10000u) != 0u; }
};

/// SLR-backed world metadata from Jagex's official world list. Unlike the
/// legacy native `World`, these fields have stable meanings.
struct WorldMetadata {
    int32_t id = 0;
    uint32_t flags = 0;
    std::string host;
    std::string activity;
    uint8_t location = 0;
    int16_t population = 0;
    int32_t pingMs = -1;
    std::string region;

    bool isMembers() const { return (flags & 0x1u) != 0u; }
    bool isBeta()    const { return (flags & 0x10000u) != 0u; }
};

namespace state {
namespace world {

/// Live current-world id. Returns `std::nullopt` when the analyzer
/// didn't emit `CURRENT_WORLD_OFFSET` on this revision.
inline std::optional<int32_t> current() {
    auto* b = ::titan::detail::backend();
    if (!b) return std::nullopt;
    int32_t w = 0;
    if (!b->getCurrentWorld(&w)) return std::nullopt;
    return w;
}

/// Full snapshot of the runtime world list. Returns an empty vector
/// when the analyzer didn't emit the list globals or GameWorld field
/// offsets (level-3 unavailable). Plugin can still use
/// `hopByListIndex()` if the current world is known by position.
inline std::vector<::titan::World> list() {
    std::vector<::titan::World> out;
    auto* b = ::titan::detail::backend();
    if (!b) return out;
    static constexpr uint32_t kCap = TitanPluginSdk::kMaxWorldListEntries;
    std::vector<TitanPluginSdk::WorldState> buf(kCap);
    uint32_t n = b->getWorldList(buf.data(), kCap);
    if (n == 0 || n > kCap) return out;
    out.reserve(n);
    for (uint32_t i = 0; i < n; ++i) {
        ::titan::World w;
        w.id       = buf[i].id;
        w.flags    = buf[i].flags;
        w.string0  = buf[i].string0;
        w.string1  = buf[i].string1;
        out.push_back(std::move(w));
    }
    return out;
}

/// SLR-backed metadata from Jagex's official world list, including host,
/// activity, region code, population, and measured ping cache.
inline std::vector<::titan::WorldMetadata> metadata() {
    std::vector<::titan::WorldMetadata> out;
    auto* b = ::titan::detail::backend();
    if (!b) return out;
    static constexpr uint32_t kCap = TitanPluginSdk::kMaxWorldListEntries;
    std::vector<TitanPluginSdk::WorldMetadataState> buf(kCap);
    uint32_t n = b->getWorldMetadata(buf.data(), kCap);
    if (n == 0 || n > kCap) return out;
    out.reserve(n);
    for (uint32_t i = 0; i < n; ++i) {
        ::titan::WorldMetadata w;
        w.id = buf[i].id;
        w.flags = buf[i].flags;
        w.host = buf[i].host;
        w.activity = buf[i].activity;
        w.location = buf[i].location;
        w.population = buf[i].population;
        w.pingMs = buf[i].pingMs;
        w.region = buf[i].region;
        out.push_back(std::move(w));
    }
    return out;
}

/// Force an asynchronous SLR metadata refresh and ping probe queue.
inline bool refreshMetadata() {
    auto* b = ::titan::detail::backend();
    return b && b->refreshWorldMetadata() != 0;
}

/// Dispatch a **title-screen** hop to @p id. Calls the native
/// `changeWorld` function synchronously on the MainLoop phase.
/// Returns false when the id isn't in the live list or the hop
/// function is unmapped, or when SLR metadata is missing / reports the
/// target offline or near capacity. Use this only when the player is NOT
/// logged in -- see `hopIngame` for the logged-in path. The call is
/// marshalled onto the game thread by the host.
inline bool hop(int32_t id) {
    auto* b = ::titan::detail::backend();
    if (!b) return false;
    return b->hopToWorldId(id) != 0;
}

/// Dispatch a **title-screen** hop by position in the native
/// `m_list`. Useful when the full list snapshot is unavailable
/// (level 2 only). Returns false on out-of-range or unmapped hop fn.
/// When the target id is resolvable, the same SLR capacity guard as
/// `hop()` applies.
inline bool hopByListIndex(size_t idx) {
    auto* b = ::titan::detail::backend();
    if (!b) return false;
    return b->hopToListIndex(static_cast<uint32_t>(idx)) != 0;
}

/// Dispatch an **in-game** hop to @p id via the native 3x `CC_OP`
/// footer-click sequence (opens logout tab, opens switcher, selects
/// world, confirms). Progresses asynchronously across several
/// `ClientTick`s; the return value is "accepted?" -- true when the
/// request was queued, false when the state machine is already busy,
/// the world id isn't in the live list, or the analyzer data needed
/// to drive the widget clicks is missing. It also returns false when SLR
/// metadata is unavailable or reports the target offline / near capacity. Use this when
/// `titan::state::login().isLoggedIn()` is true. SDK 31+.
inline bool hopIngame(int32_t id) {
    auto* b = ::titan::detail::backend();
    if (!b) return false;
    return b->hopToWorldIngame(id) != 0;
}

}  // namespace world
}  // namespace state

}  // namespace titan

namespace std {
template <> struct hash<titan::Widget> {
    std::size_t operator()(const titan::Widget& widget) const noexcept {
        return widget.hash();
    }
};
}  // namespace std
