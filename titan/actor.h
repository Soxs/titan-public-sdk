/// @file titan/actor.h
/// @brief Value-type entity wrappers (Player, Npc, TileObject, GroundItem,
///        Item, Projectile) with fluent .interact(action) and positional helpers.
///
/// Each wrapper owns a copy of the underlying ABI state struct. Query results
/// (`titan::queries::npcs()`, `titan::queries::players()`, etc.) return vectors of these wrappers so
/// the plugin can filter / sort / interact without juggling raw structs.

#pragma once

#include "detail/abi.h"
#include "detail/account_mode.h"
#include "detail/backend.h"
#include "detail/overhead_text.h"
#include "detail/line_of_sight.h"
#include "equipment_slot.h"
#include "generated/gamevals/interface_id.h"
#include "head_icon.h"
#include "local_point.h"
#include "menu_action.h"
#include "varbits.h"
#include "world_view.h"
#include "world_point.h"

#include <algorithm>
#include <cstdint>
#include <cstdlib>
#include <functional>
#include <optional>
#include <string>
#include <utility>
#include <vector>

namespace titan {

class Player;
class Npc;
class Actor;
struct WorldArea;

/// Active actor-attached spot animation snapshot. These are per-actor
/// attachments, distinct from map-tile `GraphicsObject` spotanims.
/// Added in SDK 76.
struct ActorSpotAnim {
    int32_t slot = 0;
    int32_t id = -1;
    int32_t height = 0;
    int32_t expireCycle = 0;
};

struct PlayerCompositionSlot {
    int32_t slotIndex = -1;
    int32_t rawValue = 0;
    int32_t itemId = -1;
    uint8_t kind = TitanPluginSdk::PlayerCompositionSlotKind::Empty;

    bool isEmpty() const {
        return kind == TitanPluginSdk::PlayerCompositionSlotKind::Empty;
    }
    bool isItem() const {
        return kind == TitanPluginSdk::PlayerCompositionSlotKind::Item;
    }
};

class PlayerComposition {
public:
    PlayerComposition() = default;
    explicit PlayerComposition(const TitanPluginSdk::PlayerCompositionState& state)
        : state_(state) {}

    const TitanPluginSdk::PlayerCompositionState& raw() const { return state_; }
    uint8_t status() const { return state_.status; }
    bool available() const {
        return state_.status == TitanPluginSdk::PlayerCompositionStatus::Available;
    }
    int32_t itemIdBase() const { return state_.itemIdBase; }
    int32_t npcTransformId() const { return state_.npcTransformId; }
    uint32_t slotCount() const { return state_.slotCount; }

    std::optional<PlayerCompositionSlot> getSlot(int32_t slotIndex) const {
        const uint32_t count = (std::min)(state_.slotCount,
            TitanPluginSdk::kMaxPlayerCompositionSlots);
        for (uint32_t i = 0; i < count; ++i) {
            const auto& slot = state_.slots[i];
            if (slot.slotIndex == slotIndex) {
                return PlayerCompositionSlot{
                    slot.slotIndex,
                    slot.rawValue,
                    slot.itemId,
                    slot.kind,
                };
            }
        }
        return std::nullopt;
    }

    std::optional<PlayerCompositionSlot> getSlot(EquipmentSlot slot) const {
        return getSlot(static_cast<int32_t>(slot));
    }

    std::vector<PlayerCompositionSlot> slots() const {
        std::vector<PlayerCompositionSlot> out;
        const uint32_t count = (std::min)(state_.slotCount,
            TitanPluginSdk::kMaxPlayerCompositionSlots);
        out.reserve(count);
        for (uint32_t i = 0; i < count; ++i) {
            const auto& slot = state_.slots[i];
            out.push_back(PlayerCompositionSlot{
                slot.slotIndex,
                slot.rawValue,
                slot.itemId,
                slot.kind,
            });
        }
        return out;
    }

private:
    TitanPluginSdk::PlayerCompositionState state_{};
};

namespace detail {
template <typename T>
int32_t locatableWidth(const T& t) {
    if constexpr (requires { t.sizeX(); }) {
        return t.sizeX() > 0 ? t.sizeX() : 1;
    } else {
        return 1;
    }
}

template <typename T>
int32_t locatableHeight(const T& t) {
    if constexpr (requires { t.sizeY(); }) {
        return t.sizeY() > 0 ? t.sizeY() : 1;
    } else {
        return 1;
    }
}

template <typename T>
LineOfSightArea locatableArea(const T& t) {
    return {t.worldX(), t.worldY(), locatableWidth(t), locatableHeight(t), t.plane(), t.worldViewId()};
}

/// Case-insensitive substring match — used by hasAction helpers on entities
/// whose action list is a fixed char[][].
template <typename ActionsT>
bool containsActionCI(const ActionsT& actions, const char* needle) {
    if (!needle || !needle[0]) return false;
    auto lower = [](char c) -> char {
        return (c >= 'A' && c <= 'Z') ? static_cast<char>(c + 32) : c;
    };
    for (const auto& slot : actions) {
        if (slot[0] == '\0') continue;
        for (const char* h = slot; *h; ++h) {
            const char* hi = h;
            const char* ni = needle;
            while (*hi && *ni && lower(*hi) == lower(*ni)) { ++hi; ++ni; }
            if (!*ni) return true;
        }
    }
    return false;
}

inline std::vector<WorldPoint> actorPathQueue(uint64_t entityPtr,
                                              int32_t worldViewId) {
    std::vector<WorldPoint> out;
    auto* b = backend();
    if (!b || !entityPtr) return out;

    constexpr uint32_t kMaxActorPathQueue = 10;
    TitanPluginSdk::WorldPointState raw[kMaxActorPathQueue] = {};
    const uint32_t count = (std::min)(
        b->getActorPathQueueInWorldView(entityPtr, worldViewId, raw, kMaxActorPathQueue),
        kMaxActorPathQueue);
        out.reserve(count);
    for (uint32_t i = 0; i < count; ++i) {
        if (raw[i].worldViewId < 0) raw[i].worldViewId = worldViewId;
        out.push_back(WorldPoint{raw[i].x, raw[i].y, raw[i].z, raw[i].worldViewId});
    }
    return out;
}

inline std::optional<std::string> actorOverheadText(uint64_t entityPtr) {
    auto* b = backend();
    if (!b || !entityPtr) return std::nullopt;
    return readOverheadText([b](uint64_t ptr, char* out, uint64_t cap, uint64_t* len) {
        return b->getActorOverheadText(ptr, out, cap, len);
    }, entityPtr);
}
inline std::optional<int32_t> actorOverheadTextCycles(uint64_t entityPtr) {
    auto* b = backend();
    int32_t cycles = 0;
    if (!b || !entityPtr || !b->getActorOverheadTextCyclesRemaining(entityPtr, &cycles))
        return std::nullopt;
    return cycles;
}

inline std::vector<ActorSpotAnim> actorSpotAnims(uint64_t entityPtr) {
    std::vector<ActorSpotAnim> out;
    auto* b = backend();
    if (!b || !entityPtr) return out;

    constexpr uint32_t kMaxActorSpotAnims = 16;
    TitanPluginSdk::ActorSpotAnimState raw[kMaxActorSpotAnims] = {};
    const uint32_t count = (std::min)(
        b->getActorSpotAnims(entityPtr, raw, kMaxActorSpotAnims),
        kMaxActorSpotAnims);
    out.reserve(count);
    for (uint32_t i = 0; i < count; ++i) {
        out.push_back(ActorSpotAnim{
            raw[i].slot,
            raw[i].id,
            raw[i].height,
            raw[i].expireCycle
        });
    }
    return out;
}

inline PlayerComposition playerComposition(uint64_t entityPtr) {
    TitanPluginSdk::PlayerCompositionState raw{};
    auto* b = backend();
    if (!b) return PlayerComposition(raw);
    b->getPlayerComposition(entityPtr, &raw);
    return PlayerComposition(raw);
}

inline int32_t liveStateEpoch() {
    auto* b = backend();
    if (!b) return 0;
    // Cheap on SDK 120+ hosts (a single atomic load); the backend falls back to
    // a full getClientState()->tickCount read on older hosts, so the value is
    // identical either way. Called on every live-wrapper accessor.
    return b->getLiveStateEpoch();
}

inline bool sameActorIdentity(int32_t aWorldViewId, int32_t aHashIndex,
                              int32_t bWorldViewId, int32_t bHashIndex) {
    return aHashIndex == bHashIndex && sameWorldViewId(aWorldViewId, bWorldViewId);
}

inline bool readCurrentPlayerState(TitanPluginSdk::PlayerState& state) {
    auto* b = backend();
    if (!b || state.hashIndex < 0) return false;

    TitanPluginSdk::PlayerState fresh{};
    if (b->getPlayerByIndexInWorldView(state.hashIndex, state.worldViewId, &fresh)) {
        state = fresh;
        return true;
    }

    constexpr uint32_t kMaxPlayers = 2048;
    std::vector<TitanPluginSdk::PlayerState> buf(kMaxPlayers);
    const uint32_t count = (std::min)(b->getPlayers(buf.data(), kMaxPlayers), kMaxPlayers);
    for (uint32_t i = 0; i < count; ++i) {
        if (sameActorIdentity(state.worldViewId, state.hashIndex,
                              buf[i].worldViewId, buf[i].hashIndex)) {
            state = buf[i];
            return true;
        }
    }
    return false;
}

inline bool readCurrentNpcState(TitanPluginSdk::NpcState& state) {
    auto* b = backend();
    if (!b || state.hashIndex < 0) return false;

    TitanPluginSdk::NpcState fresh{};
    if (b->getNpcByIndexInWorldView(state.hashIndex, state.worldViewId, &fresh)) {
        state = fresh;
        return true;
    }

    constexpr uint32_t kMaxNpcs = 4096;
    std::vector<TitanPluginSdk::NpcState> buf(kMaxNpcs);
    const uint32_t count = (std::min)(b->getNpcs(buf.data(), kMaxNpcs), kMaxNpcs);
    for (uint32_t i = 0; i < count; ++i) {
        if (sameActorIdentity(state.worldViewId, state.hashIndex,
                              buf[i].worldViewId, buf[i].hashIndex)) {
            state = buf[i];
            return true;
        }
    }
    return false;
}

inline const TitanPluginSdk::PlayerState& liveState(
        TitanPluginSdk::PlayerState& state,
        int32_t& lastReadEpoch,
        bool& exists,
        bool live) {
    if (!live) return state;
    const int32_t epoch = liveStateEpoch();
    if (lastReadEpoch == epoch) return state;
    lastReadEpoch = epoch;
    exists = readCurrentPlayerState(state);
    return state;
}

inline const TitanPluginSdk::NpcState& liveState(
        TitanPluginSdk::NpcState& state,
        int32_t& lastReadEpoch,
        bool& exists,
        bool live) {
    if (!live) return state;
    const int32_t epoch = liveStateEpoch();
    if (lastReadEpoch == epoch) return state;
    lastReadEpoch = epoch;
    exists = readCurrentNpcState(state);
    return state;
}

/// Re-resolve a tile object against its tile's current occupants. A scenery
/// tile can hold multiple locs, so the match is keyed on (layer, locId) -- the
/// same disambiguation the host's light-object resolver uses. Returns false
/// (caller sets exists=false) when the slot no longer holds this loc.
inline bool readCurrentTileObjectState(TitanPluginSdk::TileObjectState& state) {
    auto* b = backend();
    if (!b) return false;
    const int32_t originalPlane = state.plane;
    const int32_t originalWorldViewId = state.worldViewId;
    constexpr uint32_t kMaxTileObjectsOnTile = 64;
    TitanPluginSdk::TileObjectState buf[kMaxTileObjectsOnTile] = {};
    const uint32_t count = (std::min)(
        b->getTileObjectsOnTileInWorldView(
            state.worldViewId, state.plane, state.tileX, state.tileY, buf,
            kMaxTileObjectsOnTile),
        kMaxTileObjectsOnTile);
    for (uint32_t i = 0; i < count; ++i) {
        const bool planeOk = buf[i].plane == originalPlane;
        const bool worldViewOk =
            sameWorldViewId(buf[i].worldViewId, originalWorldViewId);
        const bool layerOk = state.layer < 0 || buf[i].layer == state.layer;
        const bool idOk = state.locId < 0 || buf[i].locId == state.locId;
        if (planeOk && worldViewOk && layerOk && idOk) {
            state = buf[i];
            return true;
        }
    }
    return false;
}

/// Re-resolve a ground item against its tile's current stacks, matched on
/// itemId. Returns false when the stack for this item id is gone.
inline bool readCurrentGroundItemState(TitanPluginSdk::GroundItemState& state) {
    auto* b = backend();
    if (!b) return false;
    constexpr uint32_t kMaxGroundItemsOnTile = 128;
    TitanPluginSdk::GroundItemState buf[kMaxGroundItemsOnTile] = {};
    const uint32_t count = (std::min)(
        b->getGroundItemsOnTile(state.plane, state.tileX, state.tileY, buf,
                                kMaxGroundItemsOnTile),
        kMaxGroundItemsOnTile);
    for (uint32_t i = 0; i < count; ++i) {
        if (state.itemId < 0 || buf[i].itemId == state.itemId) {
            state = buf[i];
            return true;
        }
    }
    return false;
}

inline const TitanPluginSdk::TileObjectState& liveState(
        TitanPluginSdk::TileObjectState& state,
        int32_t& lastReadEpoch,
        bool& exists,
        bool live) {
    if (!live) return state;
    const int32_t epoch = liveStateEpoch();
    if (lastReadEpoch == epoch) return state;
    lastReadEpoch = epoch;
    exists = readCurrentTileObjectState(state);
    return state;
}

inline const TitanPluginSdk::GroundItemState& liveState(
        TitanPluginSdk::GroundItemState& state,
        int32_t& lastReadEpoch,
        bool& exists,
        bool live) {
    if (!live) return state;
    const int32_t epoch = liveStateEpoch();
    if (lastReadEpoch == epoch) return state;
    lastReadEpoch = epoch;
    exists = readCurrentGroundItemState(state);
    return state;
}

}  // namespace detail

// ---------------------------------------------------------------------------
// Locatable CRTP mixin -- uniform positional API for world-placed entities
// ---------------------------------------------------------------------------

/// CRTP base that provides composite position accessors for any entity that
/// lives in the game world. The derived class must expose public:
///   `tileX()`, `tileY()`, `worldX()`, `worldY()`, `plane()`
/// each returning `int32_t`. The mixin derives `tile()`, `worldPoint()`,
/// `localPoint()`, and `distanceTo()` from those primitives.
template <typename Derived>
class Locatable {
public:
    int32_t worldViewId() const {
        if constexpr (requires { self_().worldViewId(); }) {
            return self_().worldViewId();
        } else {
            return WorldView::CURRENT;
        }
    }
    Tile tile() const {
        return {self_().tileX(), self_().tileY(), self_().plane(), worldViewId()};
    }
    WorldPoint worldPoint() const {
        return {self_().worldX(), self_().worldY(), self_().plane(), worldViewId()};
    }
    std::optional<WorldPoint> fromLocalInstance() const {
        return worldPoint().fromLocalInstance();
    }
    std::optional<WorldPoint> toLocalInstance() const {
        return worldPoint().toLocalInstance();
    }
    LocalPoint localPoint() const {
        if constexpr (requires { self_().renderLocalPoint(); }) {
            return self_().renderLocalPoint();
        } else {
            return LocalPoint::fromScene(self_().tileX(), self_().tileY(), worldViewId());
        }
    }
    WorldArea worldArea() const;

    int32_t distanceTo(const Tile& t) const {
        return tile().distanceTo(t);
    }
    template <typename T>
    int32_t distanceTo(const Locatable<T>& other) const {
        return tile().distanceTo(other.tile());
    }

    bool hasLineOfSight(const WorldPoint& other) const {
        return detail::hasLineOfSight(
            detail::locatableArea(self_()),
            detail::LineOfSightArea{other.x, other.y, 1, 1, other.z, other.worldViewId});
    }
    template <typename T>
    bool hasLineOfSight(const Locatable<T>& other) const {
        return detail::hasLineOfSight(
            detail::locatableArea(self_()),
            detail::locatableArea(static_cast<const T&>(other)));
    }

    bool isInMeleeDistance(const WorldPoint& other) const;
    bool isInMeleeDistance(const WorldArea& other) const;
    template <typename T>
    bool isInMeleeDistance(const Locatable<T>& other) const;

private:
    const Derived& self_() const { return static_cast<const Derived&>(*this); }
};

// ---------------------------------------------------------------------------
// Player
// ---------------------------------------------------------------------------

class Player : public Locatable<Player> {
public:
    Player() = default;
    explicit Player(const TitanPluginSdk::PlayerState& s) : state_(s) {}

    const TitanPluginSdk::PlayerState& raw() const { return state(); }
    /// Construct an immutable identity snapshot without resolving its slot.
    static Player fromSnapshot(const TitanPluginSdk::PlayerState& state,
                               std::optional<std::string> text = std::nullopt) {
        Player out(state); out.live_ = false; out.exists_ = true;
        out.overheadTextSnapshot_ = std::move(text); return out;
    }
    Player snapshot() const {
        Player copy;
        copy.state_ = state();
        copy.overheadTextSnapshot_ = overheadTextSnapshot_;
        copy.live_ = false;
        copy.exists_ = true;
        return copy;
    }
    bool exists() const {
        (void) state();
        return exists_;
    }
    bool operator==(const Player& other) const {
        return hashIndex() >= 0 && other.hashIndex() >= 0
            && detail::sameActorIdentity(worldViewId(), hashIndex(),
                                         other.worldViewId(), other.hashIndex());
    }
    bool operator!=(const Player& other) const { return !(*this == other); }
    std::size_t identityHash() const {
        std::size_t h = std::hash<int32_t>{}(TitanPluginSdk::EntityType::PLAYER);
        h ^= std::hash<int32_t>{}(worldViewId()) + 0x9e3779b9u + (h << 6) + (h >> 2);
        h ^= std::hash<int32_t>{}(hashIndex()) + 0x9e3779b9u + (h << 6) + (h >> 2);
        return h;
    }
    template <typename SpellT>
    bool castOn(SpellT spell) const;

    std::string name() const { return state().name; }
    int32_t tileX() const { return state().tileX; }
    int32_t tileY() const { return state().tileY; }
    int32_t plane() const { return state().plane; }
    int32_t worldX() const { return state().worldX; }
    int32_t worldY() const { return state().worldY; }
    int32_t preciseX() const { return state().preciseX; }
    int32_t preciseY() const { return state().preciseY; }
    LocalPoint renderLocalPoint() const { return {state().preciseX, state().preciseY, worldViewId()}; }
    int32_t worldViewId() const { return state().worldViewId; }
    uint64_t worldViewPtr() const { return state().worldViewPtr; }
    int32_t orientation() const { return state().orientation; }
    int32_t animation() const { return state().animation; }
    int32_t movementPose() const { return state().movementPose; }
    int32_t idlePose() const { return state().idlePose; }
    int32_t combatLevel() const { return state().combatLevel; }
    int32_t hashIndex() const { return state().hashIndex; }
    int32_t interactingIndex() const { return state().interactingIndex; }
    uint8_t interactingType() const { return state().interactingType; }
    /// Interaction lifecycle phase (0 = active, non-zero = stale). SDK v44+.
    uint8_t interactingPhase() const { return state().interactingPhase; }
    /// True when this entity is actively interacting with a target, matching
    /// the game's own predicate (phase == 0 && type != 0 && index != -1).
    /// Falls back to index-only check when the phase offset is unavailable.
    bool isInteracting() const {
        if (state().interactingPhase == 0xFF)
            return state().interactingIndex != -1 && state().interactingIndex != 0;
        return state().interactingPhase == 0 && state().interactingType != 0 && state().interactingIndex != -1;
    }
    uint64_t entityPtr() const { return state().entityPtr; }
    /// Live overhead prayer icon index, or `-1` when no icon is active.
    /// Sourced from `ClientPlayer + PLAYER_OVERHEAD_ICON_OFFSET`. Returns
    /// `-1` transparently when the analyzer hasn't detected the offset
    /// on the loaded revision. SDK v35+.
    int32_t overheadIcon() const { return state().overheadIcon; }
    /// Live skull icon index, or `-1` when unskulled. SDK v35+.
    int32_t skullIcon() const { return state().skullIcon; }
    /// True when any overhead prayer/curse icon is currently active on
    /// this player. Derived from `overheadIcon() >= 0` -- no extra host
    /// call. Returns `false` transparently when the analyzer hasn't
    /// detected `PLAYER_OVERHEAD_ICON_OFFSET` on the loaded revision.
    /// SDK v35+.
    bool isOverheadActive() const { return state().overheadIcon >= 0; }
    /// True when the player is currently displaying the exact @p icon
    /// (e.g. `isOverheadActive(HeadIcon::MELEE)` answers "is this player
    /// praying Protect from Melee right now"). Returns `false` when no
    /// icon is active, when the ordinal doesn't match, or when the
    /// analyzer hasn't detected the offset on this revision. SDK v35+.
    bool isOverheadActive(HeadIcon icon) const {
        return state().overheadIcon == static_cast<int32_t>(icon);
    }
    /// True when the player currently has a PvP/Wilderness skull icon.
    /// Matches RuneLite `Player.getSkullIcon() >= 0`. Returns `false`
    /// transparently when the analyzer hasn't detected
    /// `PLAYER_SKULL_ICON_OFFSET` on the loaded revision. SDK v35+.
    bool isSkulled() const { return state().skullIcon >= 0; }
    /// True when the player has no pending movement
    /// (`movementPose == idlePose`). A stationary player can still be
    /// animating -- see `isAnimating()` / `isIdle()`.
    bool isStationary() const { return state().movementPose == state().idlePose; }
    /// True when the player is currently playing any animation (spot anim,
    /// skilling pose, etc.).
    bool isAnimating() const { return animation() != -1; }
    /// True when the player is fully idle: not moving *and* not animating.
    bool isIdle() const { return isStationary() && !isAnimating(); }
    bool isHidden() const { return state().hidden != 0; }
    /// Current headbar fill value in [0, healthScale()], or `-1` when no
    /// health bar is active. Populated from the entity's headbar vector.
    /// SDK v48+.
    int32_t healthRatio() const { return state().healthRatio; }
    /// Maximum headbar width (from HeadbarType config), or `-1` when no
    /// health bar is active. SDK v48+.
    int32_t healthScale() const { return state().healthScale; }
    /// Health as a percentage [0.0, 1.0], or `-1.0f` when no bar is
    /// active. Computed as `healthRatio / healthScale`. SDK v48+.
    float healthPercent() const {
        if (state().healthRatio < 0 || state().healthScale <= 0) return -1.0f;
        return static_cast<float>(state().healthRatio) / static_cast<float>(state().healthScale);
    }
    /// True when the entity has at least one active headbar. SDK v48+.
    bool hasHealthBar() const { return state().hasHealthBar != 0; }
    /// True when the entity has an active health bar at exactly 0%.
    /// SDK v48+.
    bool isDead() const {
        return state().hasHealthBar != 0 && state().healthScale > 0 && state().healthRatio == 0;
    }

    // tile(), worldPoint(), localPoint(), distanceTo() inherited from Locatable.

    std::vector<WorldPoint> pathQueue() const {
        const auto& s = state();
        return detail::actorPathQueue(s.entityPtr, s.worldViewId);
    }

    /// Complete UTF-8 text; nullopt means unavailable/invalid, empty is valid.
    std::optional<std::string> getOverheadText() const {
        if (overheadTextSnapshot_) return overheadTextSnapshot_;
        return live_ ? detail::actorOverheadText(entityPtr()) : std::nullopt;
    }
    std::optional<int32_t> getOverheadTextCyclesRemaining() const {
        return live_ ? detail::actorOverheadTextCycles(entityPtr()) : std::nullopt;
    }

    std::vector<ActorSpotAnim> currentSpotAnims() const {
        return detail::actorSpotAnims(state().entityPtr);
    }

    PlayerComposition composition() const {
        return detail::playerComposition(state().entityPtr);
    }

    PlayerComposition getPlayerComposition() const {
        return composition();
    }

    /// Resolve the target this player is interacting with, if any.
    Actor interacting() const;

private:
    friend class ClientFacade; // Copy retained setter identity without a live slot refresh.
    const TitanPluginSdk::PlayerState& state() const {
        return detail::liveState(state_, lastReadEpoch_, exists_, live_);
    }

    mutable TitanPluginSdk::PlayerState state_ = {};
    mutable int32_t lastReadEpoch_ = INT32_MIN;
    mutable bool exists_ = true;
    std::optional<std::string> overheadTextSnapshot_;
    bool live_ = true;
};

// ---------------------------------------------------------------------------
// Npc
// ---------------------------------------------------------------------------

class Npc : public Locatable<Npc> {
public:
    Npc() = default;
    explicit Npc(const TitanPluginSdk::NpcState& s) : state_(s) {}

    const TitanPluginSdk::NpcState& raw() const { return state(); }
    /// Construct an immutable identity snapshot without resolving its slot.
    static Npc fromSnapshot(const TitanPluginSdk::NpcState& state,
                               std::optional<std::string> text = std::nullopt) {
        Npc out(state); out.live_ = false; out.exists_ = true;
        out.overheadTextSnapshot_ = std::move(text); return out;
    }
    Npc snapshot() const {
        Npc copy;
        copy.state_ = state();
        copy.overheadTextSnapshot_ = overheadTextSnapshot_;
        copy.live_ = false;
        copy.exists_ = true;
        return copy;
    }
    bool exists() const {
        (void) state();
        return exists_;
    }
    bool operator==(const Npc& other) const {
        return hashIndex() >= 0 && other.hashIndex() >= 0
            && detail::sameActorIdentity(worldViewId(), hashIndex(),
                                         other.worldViewId(), other.hashIndex());
    }
    bool operator!=(const Npc& other) const { return !(*this == other); }
    std::size_t identityHash() const {
        std::size_t h = std::hash<int32_t>{}(TitanPluginSdk::EntityType::NPC);
        h ^= std::hash<int32_t>{}(worldViewId()) + 0x9e3779b9u + (h << 6) + (h >> 2);
        h ^= std::hash<int32_t>{}(hashIndex()) + 0x9e3779b9u + (h << 6) + (h >> 2);
        return h;
    }

    std::string name() const { return state().name; }
    int32_t id() const { return state().npcId; }
    int32_t tileX() const { return state().tileX; }
    int32_t tileY() const { return state().tileY; }
    int32_t plane() const { return state().plane; }
    int32_t worldX() const { return state().worldX; }
    int32_t worldY() const { return state().worldY; }
    int32_t preciseX() const { return state().preciseX; }
    int32_t preciseY() const { return state().preciseY; }
    LocalPoint renderLocalPoint() const { return {state().preciseX, state().preciseY, worldViewId()}; }
    int32_t worldViewId() const { return state().worldViewId; }
    uint64_t worldViewPtr() const { return state().worldViewPtr; }
    int32_t animation() const { return state().animation; }
    int32_t orientation() const { return state().orientation; }
    int32_t movementPose() const { return state().movementPose; }
    int32_t idlePose() const { return state().idlePose; }
    int32_t hashIndex() const { return state().hashIndex; }
    int32_t interactingIndex() const { return state().interactingIndex; }
    uint8_t interactingType() const { return state().interactingType; }
    /// Interaction lifecycle phase (0 = active, non-zero = stale). SDK v44+.
    uint8_t interactingPhase() const { return state().interactingPhase; }
    /// True when this entity is actively interacting with a target. SDK v44+.
    bool isInteracting() const {
        if (state().interactingPhase == 0xFF)
            return state().interactingIndex != -1 && state().interactingIndex != 0;
        return state().interactingPhase == 0 && state().interactingType != 0 && state().interactingIndex != -1;
    }
    int32_t sizeX() const { return state().sizeX; }
    int32_t sizeY() const { return state().sizeY; }
    /// Footprint as an absolute world-tile area, anchored at the NPC's
    /// south-west world point.
    WorldArea toWorldArea() const;
    uint64_t entityPtr() const { return state().entityPtr; }
    /// Primary overhead icon index for this NPC, or `-1` when none.
    /// Returns the first entry of the NpcType cache-default head icon
    /// graphics vector. For bosses like Bandos this yields the
    /// protect-melee icon. Dynamic per-instance overrides are surfaced
    /// via `hasHeadIconOverride()`. SDK v35+.
    int32_t overheadIcon() const { return state().overheadIcon; }
    /// True when a per-instance runtime override is currently set via
    /// `ClientNpc::SetHeadIcon` (distinct from the cache default that
    /// `overheadIcon()` returns). SDK v35+.
    bool hasHeadIconOverride() const { return state().hasHeadIconOverride != 0; }
    /// True when this NPC is currently displaying any overhead icon --
    /// either the cache-default `overheadIcon() >= 0` (e.g. Bandos with
    /// protect-melee) or a dynamic per-instance override set via
    /// `ClientNpc::SetHeadIcon` (mid-encounter icon swaps). Covers both
    /// cases uniformly. SDK v35+.
    bool isOverheadActive() const {
        return state().overheadIcon >= 0 || state().hasHeadIconOverride != 0;
    }
    /// True when this NPC is currently displaying the exact cache-default
    /// @p icon (compares against `overheadIcon()` = `headIconGraphics[0]`).
    /// A runtime override set via `ClientNpc::SetHeadIcon` is NOT matched
    /// by this overload because the override's icon value isn't surfaced
    /// in SDK state; use `hasHeadIconOverride()` if you want to detect
    /// any override regardless of value. SDK v35+.
    bool isOverheadActive(HeadIcon icon) const {
        return state().overheadIcon == static_cast<int32_t>(icon);
    }

    /// True when the NPC is currently playing any animation.
    bool isAnimating() const { return animation() != -1; }
    /// True when the NPC has no pending movement (`movementPose == idlePose`).
    /// A stationary NPC can still be animating.
    bool isStationary() const { return state().movementPose == state().idlePose; }
    bool isDead() const {
        return state().hasHealthBar != 0 && state().healthScale > 0 && state().healthRatio == 0;
    }
    int32_t healthRatio() const { return state().healthRatio; }
    int32_t healthScale() const { return state().healthScale; }
    float healthPercent() const {
        if (state().healthRatio < 0 || state().healthScale <= 0) return -1.0f;
        return static_cast<float>(state().healthRatio) / static_cast<float>(state().healthScale);
    }
    bool hasHealthBar() const { return state().hasHealthBar != 0; }

    // tile(), worldPoint(), localPoint(), distanceTo() inherited from Locatable.

    std::vector<WorldPoint> pathQueue() const {
        const auto& s = state();
        return detail::actorPathQueue(s.entityPtr, s.worldViewId);
    }

    /// Complete UTF-8 text; nullopt means unavailable/invalid, empty is valid.
    std::optional<std::string> getOverheadText() const {
        if (overheadTextSnapshot_) return overheadTextSnapshot_;
        return live_ ? detail::actorOverheadText(entityPtr()) : std::nullopt;
    }
    std::optional<int32_t> getOverheadTextCyclesRemaining() const {
        return live_ ? detail::actorOverheadTextCycles(entityPtr()) : std::nullopt;
    }

    std::vector<ActorSpotAnim> currentSpotAnims() const {
        return detail::actorSpotAnims(state().entityPtr);
    }

    bool hasAction(const char* action) const { return state().hasAction(action); }
    bool hasAction(const std::string& action) const { return state().hasAction(action.c_str()); }

    /// Dispatch a named action (case-insensitive substring match) against
    /// this exact NPC by its hash index. Returns true if the action was queued,
    /// or false when this snapshot does not expose the requested action.
    bool interact(const char* action) const {
        auto* b = detail::backend();
        if (!b || !action) return false;
        if (!hasAction(action)) return false;
        return b->interactNpcByIndexInWorldView(
            action, state().hashIndex, state().worldViewId) != 0;
    }
    bool interact(const std::string& action) const { return interact(action.c_str()); }
    template <typename SpellT>
    bool castOn(SpellT spell) const;

    Actor interacting() const;

private:
    friend class ClientFacade; // Copy retained setter identity without a live slot refresh.
    const TitanPluginSdk::NpcState& state() const {
        return detail::liveState(state_, lastReadEpoch_, exists_, live_);
    }

    mutable TitanPluginSdk::NpcState state_ = {};
    mutable int32_t lastReadEpoch_ = INT32_MIN;
    mutable bool exists_ = true;
    std::optional<std::string> overheadTextSnapshot_;
    bool live_ = true;
};

// ---------------------------------------------------------------------------
// TileObject (loc)
// ---------------------------------------------------------------------------

class TileObject : public Locatable<TileObject> {
public:
    TileObject() = default;
    explicit TileObject(const TitanPluginSdk::TileObjectState& s) : state_(s) {}

    const TitanPluginSdk::TileObjectState& raw() const { return state(); }

    /// Detach a frozen copy at the current values. The copy is non-live: its
    /// accessors keep returning capture-time values and never re-resolve.
    TileObject snapshot() const {
        TileObject copy;
        copy.state_ = state();
        copy.live_ = false;
        copy.exists_ = true;
        return copy;
    }
    /// True while a loc matching this handle's (layer, id) still occupies the
    /// tile. Flips to false once it despawns or the slot changes to another loc.
    bool exists() const {
        (void) state();
        return exists_;
    }

    std::string name() const { return state().name; }
    std::string typeName() const { return state().type; }
    int32_t id() const { return state().locId; }
    int32_t tileX() const { return state().tileX; }
    int32_t tileY() const { return state().tileY; }
    int32_t plane() const { return state().plane; }
    int32_t worldX() const { return state().worldX; }
    int32_t worldY() const { return state().worldY; }
    int32_t worldViewId() const { return state().worldViewId; }
    uint64_t worldViewPtr() const { return state().worldViewPtr; }
    int32_t sizeX() const { return state().sizeX; }
    int32_t sizeY() const { return state().sizeY; }
    uint64_t packedId() const { return state().packedId; }
    int32_t sceneTypecode() const { return state().sceneTypecode; }
    int32_t sceneObjectType() const { return state().sceneObjectType; }
    int32_t shape() const { return sceneObjectType(); }
    int32_t orientation() const { return state().orientation; }
    int32_t animation() const { return state().animation; }
    int32_t getAnimation() const { return animation(); }
    /// Raw `jag::oldscape::dash3d::Loc*` pointer for advanced clients that
    /// feed it back into clickbox / overlay calls. Zero when the host did
    /// not populate it (pre-SDK-33 builds).
    uint64_t entityPtr() const { return state().entityPtr; }
    /// Scene layer this loc was picked up from (0=Wall, 1=Decor,
    /// 2=Scenery, 3=GroundDecor), or -1 when the host couldn't
    /// classify it. The native loc picking key is the raw packed scene tag;
    /// this layer value is metadata for filtering/debug display.
    int32_t layer() const { return state().layer; }

    // tile(), worldPoint(), localPoint(), distanceTo() inherited from Locatable.

    bool hasAction(const char* action) const {
        return detail::containsActionCI(state().actions, action);
    }
    bool hasAction(const std::string& action) const { return hasAction(action.c_str()); }

    /// Dispatch a named action against the loc currently occupying this slot.
    /// Re-resolves first (live handle), so a despawned object is a safe no-op
    /// rather than an action into stale scene memory.
    bool interact(const char* action) const {
        auto* b = detail::backend();
        if (!b || !action) return false;
        const TitanPluginSdk::TileObjectState& s = state();
        if (!exists_) return false;
        if (!detail::containsActionCI(s.actions, action)) return false;
        return b->interactTileObject(action, &s) != 0;
    }
    bool interact(const std::string& action) const { return interact(action.c_str()); }
    template <typename SpellT>
    bool castOn(SpellT spell) const;

    bool operator==(const TileObject& other) const {
        const auto& a = state();
        const auto& b = other.state();
        return a.locId >= 0 && b.locId >= 0 && a.locId == b.locId
            && a.layer == b.layer && a.plane == b.plane
            && a.tileX == b.tileX && a.tileY == b.tileY
            && detail::sameWorldViewId(a.worldViewId, b.worldViewId);
    }
    bool operator!=(const TileObject& other) const { return !(*this == other); }

private:
    const TitanPluginSdk::TileObjectState& state() const {
        return detail::liveState(state_, lastReadEpoch_, exists_, live_);
    }

    mutable TitanPluginSdk::TileObjectState state_ = {};
    mutable int32_t lastReadEpoch_ = INT32_MIN;
    mutable bool exists_ = true;
    bool live_ = true;
};

// ---------------------------------------------------------------------------
// GroundItem
// ---------------------------------------------------------------------------

enum class GroundItemOwnership : uint32_t {
    None = TitanPluginSdk::GroundItemOwnershipAbi::NONE,
    SelfPlayer = TitanPluginSdk::GroundItemOwnershipAbi::SELF_PLAYER,
    OtherPlayer = TitanPluginSdk::GroundItemOwnershipAbi::OTHER_PLAYER,
    GroupIronman = TitanPluginSdk::GroundItemOwnershipAbi::GROUP_IRONMAN,
};

class GroundItem : public Locatable<GroundItem> {
public:
    GroundItem() = default;
    explicit GroundItem(const TitanPluginSdk::GroundItemState& s) : state_(s) {}

    const TitanPluginSdk::GroundItemState& raw() const { return state(); }

    /// Detach a frozen copy at the current values (non-live: never re-resolves).
    GroundItem snapshot() const {
        GroundItem copy;
        copy.state_ = state();
        copy.live_ = false;
        copy.exists_ = true;
        return copy;
    }
    /// True while a stack of this item id still lies on the tile.
    bool exists() const {
        (void) state();
        return exists_;
    }

    std::string name() const { return state().name; }
    int32_t id() const { return state().itemId; }
    int32_t quantity() const { return state().quantity; }
    /// Raw ownershipType value from the ClientObj.
    uint32_t ownershipType() const { return state().ownershipType; }
    GroundItemOwnership ownership() const {
        return static_cast<GroundItemOwnership>(state().ownershipType);
    }
    bool canLoot() const {
        auto* b = detail::backend();
        return b ? detail::groundItemOwnershipLootableForAccount(
                       state().ownershipType, b->getVarbit(Varbits::ACCOUNT_TYPE))
                 : false;
    }
    int32_t tileX() const { return state().tileX; }
    int32_t tileY() const { return state().tileY; }
    int32_t plane() const { return state().plane; }
    int32_t worldX() const { return state().worldX; }
    int32_t worldY() const { return state().worldY; }
    int32_t worldViewId() const { return state().worldViewId; }
    uint64_t worldViewPtr() const { return state().worldViewPtr; }

    // tile(), worldPoint(), localPoint(), distanceTo() inherited from Locatable.

    /// Dispatch a named ground-item action (e.g. "Take", "Examine").
    /// Re-resolves against the live tile first.
    bool interact(const char* action) const {
        auto* b = detail::backend();
        if (!b || !action) return false;
        const TitanPluginSdk::GroundItemState& s = state();
        return b->interactGroundItemInWorldView(
            action, s.itemId, s.tileX, s.tileY, s.worldViewId) != 0;
    }
    bool interact(const std::string& action) const { return interact(action.c_str()); }
    template <typename SpellT>
    bool castOn(SpellT spell) const;

    bool operator==(const GroundItem& other) const {
        const auto& a = state();
        const auto& b = other.state();
        return a.itemId >= 0 && b.itemId >= 0 && a.itemId == b.itemId
            && a.plane == b.plane && a.tileX == b.tileX && a.tileY == b.tileY
            && detail::sameWorldViewId(a.worldViewId, b.worldViewId);
    }
    bool operator!=(const GroundItem& other) const { return !(*this == other); }

private:
    const TitanPluginSdk::GroundItemState& state() const {
        return detail::liveState(state_, lastReadEpoch_, exists_, live_);
    }

    mutable TitanPluginSdk::GroundItemState state_ = {};
    mutable int32_t lastReadEpoch_ = INT32_MIN;
    mutable bool exists_ = true;
    bool live_ = true;
};

// ---------------------------------------------------------------------------
// Item (inventory slot)
// ---------------------------------------------------------------------------

class Item {
public:
    Item() = default;
    explicit Item(const TitanPluginSdk::InventoryItemState& s) : state_(s) {}

    const TitanPluginSdk::InventoryItemState& raw() const { return state_; }

    std::string name() const { return state_.name; }
    int32_t id() const { return state_.itemId; }
    int32_t quantity() const { return state_.quantity; }
    int32_t slot() const { return state_.slot; }

    /// Dispatch a named inventory action (e.g. "Eat", "Drop", "Bury") or a
    /// live opcode-43 submenu label. Ordinary actions take precedence over
    /// same-named sub-operations. Returns true when the action was accepted /
    /// queued; observe onItemContainerChanged to confirm inventory or
    /// equipment state changes.
    bool interact(const char* action) const {
        auto* b = detail::backend();
        if (!b || !action) return false;
        if (state_.slot >= 0) {
            if (b->interactInventoryItemAtSlot(state_.slot, state_.itemId, action) != 0) return true;
        }
        return b->interactInventoryItem(state_.itemId, action) != 0;
    }
    bool interact(const std::string& action) const { return interact(action.c_str()); }

    /// Use this item on another inventory item (e.g. knife on logs). The
    /// WIDGET_TARGET and WIDGET_TARGET_ON_WIDGET actions are queued together
    /// in order; the input backend spaces their execution. Returns true when
    /// the complete two-action sequence was accepted.
    bool useOn(const Item& target) const {
        auto* b = detail::backend();
        if (!b) return false;
        return b->useInventoryItemOnItem(
            state_.slot, state_.itemId,
            target.state_.slot, target.state_.itemId) != 0;
    }

    /// Use this item on an NPC. Both actions are queued together in order;
    /// the input backend spaces their execution.
    bool useOn(const Npc& target) const {
        auto* b = detail::backend();
        if (!b) return false;
        return b->useInventoryItemOnNpc(
            state_.slot, state_.itemId, target.hashIndex()) != 0;
    }

    /// Select this inventory item and use it on the exact live player as one queued pair.
    bool useOn(const Player& target) const {
        auto* b = detail::backend();
        if (!b || state_.slot < 0 || state_.itemId < 0 || target.hashIndex() < 0) return false;
        TitanPluginSdk::SelectedActionPair pair{};
        pair.source.opcode = static_cast<uint32_t>(MenuAction::Id::WidgetTarget);
        pair.source.param0 = state_.slot;
        pair.source.param1 = gamevals::InterfaceID::Inventory::ITEMS;
        pair.source.actionText = "Use";
        pair.source.targetText = "";
        pair.target.opcode = static_cast<uint32_t>(MenuAction::Id::ItemUseOnPlayer);
        pair.target.identifier = target.hashIndex();
        pair.target.worldViewId = target.worldViewId();
        pair.target.targetPlane = target.plane();
        pair.target.targetEntityPtr = target.entityPtr();
        pair.target.actionText = "Use";
        pair.target.targetText = "";
        pair.hasExpectedSourceItem = 1;
        pair.expectedSourceItemId = state_.itemId;
        return b->executeSelectedActionPair(&pair) != 0;
    }

    /// Use this item on a scene object / loc. Both actions are queued together
    /// in order; the input backend spaces their execution.
    bool useOn(const TileObject& target) const {
        auto* b = detail::backend();
        if (!b) return false;
        return b->useInventoryItemOnObject(
            state_.slot, state_.itemId,
            target.id(), target.tileX(), target.tileY()) != 0;
    }

    /// Use this item on a ground item stack (SDK 129 selected pair). Composes
    /// `WidgetTarget` on this inventory slot followed by `ItemUseOnGroundItem`
    /// at the stack's tile and queues both through the host together, the
    /// same way `utils::Magic::castOn` queues a spell on a ground item. The
    /// host pins this item id as the expected source and binds the unique
    /// matching stack (including its quantity) on the game thread. Requires
    /// a live slot; returns true when the pair was queued, not when the game
    /// acted on it.
    bool useOn(const GroundItem& target) const {
        auto* b = detail::backend();
        if (!b || state_.slot < 0 || state_.itemId < 0) return false;
        const TitanPluginSdk::GroundItemState& t = target.raw();
        if (t.itemId < 0) return false;
        TitanPluginSdk::SelectedActionPair pair{};
        pair.source.opcode = static_cast<uint32_t>(MenuAction::Id::WidgetTarget);
        pair.source.identifier = 0;
        pair.source.param0 = state_.slot;
        pair.source.param1 = gamevals::InterfaceID::Inventory::ITEMS;
        pair.source.actionText = "Use";
        pair.source.targetText = "";
        pair.target.opcode = static_cast<uint32_t>(MenuAction::Id::ItemUseOnGroundItem);
        pair.target.identifier = t.itemId;
        pair.target.param0 = t.tileX;
        pair.target.param1 = t.tileY;
        pair.target.worldViewId = t.worldViewId;
        pair.target.targetPlane = t.plane;
        pair.target.actionText = "Use";
        pair.target.targetText = "";
        pair.hasExpectedSourceItem = 1;
        pair.expectedSourceItemId = state_.itemId;
        return b->executeSelectedActionPair(&pair) != 0;
    }
    template <typename SpellT>
    bool castOn(SpellT spell) const;

private:
    TitanPluginSdk::InventoryItemState state_ = {};
};

// ---------------------------------------------------------------------------
// Projectile
// ---------------------------------------------------------------------------

class Projectile : public Locatable<Projectile> {
public:
    Projectile() = default;
    explicit Projectile(const TitanPluginSdk::ProjectileState& s) : state_(s) {}

    const TitanPluginSdk::ProjectileState& raw() const { return state_; }

    int32_t spotAnimId() const { return state_.spotAnimId; }
    int32_t startTick() const { return state_.startTick; }
    int32_t endTick() const { return state_.endTick; }
    int32_t startX() const { return state_.startX; }
    int32_t startY() const { return state_.startY; }
    int32_t targetX() const { return state_.targetX; }
    int32_t targetY() const { return state_.targetY; }
    int32_t sourceEntity() const { return state_.sourceEntity; }
    int32_t targetEntity() const { return state_.targetEntity; }
    int32_t rawSourceEntity() const { return state_.rawSourceEntity; }
    int32_t rawTargetEntity() const { return state_.rawTargetEntity; }
    int32_t sourceEntityType() const { return state_.sourceEntityType; }
    int32_t targetEntityType() const { return state_.targetEntityType; }
    Actor sourceActor() const;
    Actor targetActor() const;
    int32_t plane() const { return state_.plane; }
    int32_t worldX() const { return state_.worldX; }
    int32_t worldY() const { return state_.worldY; }
    int32_t worldViewId() const { return WorldView::CURRENT; }
    uint64_t worldViewPtr() const { return 0; }
    /// Scene tile X derived from the current interpolated position.
    int32_t tileX() const { return state_.sceneX >> 7; }
    /// Scene tile Y derived from the current interpolated position.
    int32_t tileY() const { return state_.sceneY >> 7; }
    int32_t sceneX() const { return state_.sceneX; }
    int32_t height() const { return state_.height; }
    int32_t sceneY() const { return state_.sceneY; }
    uint64_t basePtr() const { return state_.basePtr; }
    bool hasMoved() const { return state_.moved != 0; }

    // tile(), worldPoint(), localPoint(), distanceTo() inherited from Locatable.

private:
    TitanPluginSdk::ProjectileState state_ = {};
};

// ---------------------------------------------------------------------------
// GraphicsObject (map-tile spot anim) -- SDK 57+
// ---------------------------------------------------------------------------

/// Runtime animation sequence snapshot. Pointer fields refer to live game
/// memory and are intended for advanced timing/introspection only.
class Sequence {
public:
    Sequence() = default;
    explicit Sequence(const TitanPluginSdk::SequenceState& s) : state_(s) {}

    const TitanPluginSdk::SequenceState& raw() const { return state_; }

    uint64_t ptr() const { return state_.ptr; }
    int32_t id() const { return state_.id; }
    uint32_t flags() const { return state_.flags; }
    int32_t numFrames() const { return state_.frameCount; }
    int32_t frameCount() const { return state_.frameCount; }
    uint64_t frameIDs() const { return state_.frameIds; }
    uint64_t frameIds() const { return state_.frameIds; }
    uint64_t frameLengths() const { return state_.frameLengths; }
    int32_t totalDuration() const { return state_.totalDuration; }
    int32_t frameStep() const { return state_.frameStep; }
    uint16_t repeatLimit() const { return state_.repeatLimit; }

private:
    TitanPluginSdk::SequenceState state_ = {};
};

/// Active map-tile spot animation (RuneLite's `GraphicsObject`). Surfaced as
/// the in-flight contents of `WorldView::GraphicsObjectList`. Same Locatable
/// shape as Projectile: scene/world tile + plane drive `tile()` /
/// `worldPoint()` / `localPoint()`. Returned by
/// `titan::queries::graphicsObjects()` and dispatched through
/// `Plugin::onGraphicsObjectSpawned / Despawned / Moved`.
class GraphicsObject : public Locatable<GraphicsObject> {
public:
    GraphicsObject() = default;
    explicit GraphicsObject(const TitanPluginSdk::GraphicsObjectState& s) : state_(s) {}

    const TitanPluginSdk::GraphicsObjectState& raw() const { return state_; }

    int32_t spotAnimId() const { return state_.spotAnimId; }
    int32_t startCycle() const { return state_.startCycle; }
    int32_t plane() const { return state_.plane; }
    /// Signed vertical height offset (added to terrain height by the
    /// renderer). NOT a scene Y axis -- spot anims aren't billboarded
    /// vertically; this is the engine's `MapSpotAnim::Height` field.
    int32_t height() const { return state_.height; }
    /// Precise scene X (1/128 subtile units).
    int32_t preciseX() const { return state_.preciseX; }
    /// Precise scene Y (1/128 subtile units).
    int32_t preciseY() const { return state_.preciseY; }
    /// Scene tile X (`preciseX >> 7`). Used by the Locatable mixin as `tileX()`.
    int32_t sceneX() const { return state_.sceneX; }
    /// Scene tile Y (`preciseY >> 7`). Used by the Locatable mixin as `tileY()`.
    int32_t sceneY() const { return state_.sceneY; }
    /// Absolute world tile X (`sceneX + WorldView::BaseX`).
    int32_t worldX() const { return state_.worldX; }
    /// Absolute world tile Y (`sceneY + WorldView::BaseY`).
    int32_t worldY() const { return state_.worldY; }
    int32_t worldViewId() const { return state_.worldViewId; }
    /// Locatable primitive: scene tile X.
    int32_t tileX() const { return state_.sceneX; }
    /// Locatable primitive: scene tile Y.
    int32_t tileY() const { return state_.sceneY; }
    uint64_t basePtr() const { return state_.basePtr; }
    /// Owning WorldView pointer captured at construction. Plugins running
    /// across multiple WorldViews can use this to filter.
    uint64_t worldViewPtr() const { return state_.worldViewPtr; }
    /// Address of the inline SeqState subobject. Zero when the analyzer
    /// didn't detect the field. Advanced; the wrapper does not deref it.
    uint64_t seqStateAddr() const { return state_.seqStateAddr; }
    /// Active `Sequence*`. Zero means the animation has finished/despawned.
    uint64_t seqPtr() const { return state_.seqPtr ? state_.seqPtr : state_.seqTypePtr; }
    /// Legacy alias retained for SDK compatibility.
    uint64_t seqTypePtr() const { return seqPtr(); }
    /// Active sequence id, or -1 when `seqPtr() == 0`.
    int32_t animationId() const { return seqPtr() ? state_.animationId : -1; }
    uint16_t frameCycle() const { return state_.frameCycle; }
    uint16_t currentFrame() const { return state_.currentFrame; }
    uint16_t loopCount() const { return state_.loopCount; }
    uint16_t totalCycle() const { return state_.totalCycle; }
    std::optional<Sequence> animation() const {
        if (!seqPtr()) return std::nullopt;
        return Sequence{state_.animation};
    }

    /// Number of game cycles since this spot anim started. Negative when
    /// the live tick is before `startCycle` (shouldn't happen in practice).
    int32_t ageAtTick(int32_t tick) const { return tick - state_.startCycle; }

    // tile(), worldPoint(), localPoint(), distanceTo() inherited from Locatable.

private:
    TitanPluginSdk::GraphicsObjectState state_ = {};
};

// ---------------------------------------------------------------------------
// Actor (Player | Npc variant, used for interaction-target resolution)
// ---------------------------------------------------------------------------

class Actor : public Locatable<Actor> {
public:
    Actor() = default;
    explicit Actor(Player p) : kind_(Kind::player), player_(std::move(p)) {}
    explicit Actor(Npc n)    : kind_(Kind::npc),    npc_(std::move(n)) {}

    bool isPlayer() const { return kind_ == Kind::player; }
    bool isNpc()    const { return kind_ == Kind::npc; }
    bool isEmpty()  const { return kind_ == Kind::none; }
    bool exists() const {
        if (isPlayer()) return player_.exists();
        if (isNpc()) return npc_.exists();
        return false;
    }
    Actor snapshot() const {
        if (isPlayer()) return Actor{player_.snapshot()};
        if (isNpc()) return Actor{npc_.snapshot()};
        return Actor{};
    }
    bool operator==(const Actor& other) const {
        return !isEmpty() && !other.isEmpty()
            && entityType() == other.entityType()
            && detail::sameActorIdentity(worldViewId(), hashIndex(),
                                         other.worldViewId(), other.hashIndex());
    }
    bool operator!=(const Actor& other) const { return !(*this == other); }
    std::size_t identityHash() const {
        std::size_t h = std::hash<int32_t>{}(entityType());
        h ^= std::hash<int32_t>{}(worldViewId()) + 0x9e3779b9u + (h << 6) + (h >> 2);
        h ^= std::hash<int32_t>{}(hashIndex()) + 0x9e3779b9u + (h << 6) + (h >> 2);
        return h;
    }

    const Player* asPlayer() const { return kind_ == Kind::player ? &player_ : nullptr; }
    const Npc*    asNpc()    const { return kind_ == Kind::npc    ? &npc_    : nullptr; }

    int32_t hashIndex() const {
        if (isPlayer()) return player_.hashIndex();
        if (isNpc())    return npc_.hashIndex();
        return -1;
    }
    int32_t entityType() const {
        if (isPlayer()) return TitanPluginSdk::EntityType::PLAYER;
        if (isNpc())    return TitanPluginSdk::EntityType::NPC;
        return TitanPluginSdk::EntityType::NONE;
    }
    uint64_t entityPtr() const {
        if (isPlayer()) return player_.entityPtr();
        if (isNpc())    return npc_.entityPtr();
        return 0;
    }

    std::string name() const {
        if (isPlayer()) return player_.name();
        if (isNpc())    return npc_.name();
        return "";
    }

    /// Forward to the underlying entity's interact (no-op for players).
    bool interact(const char* action) const {
        if (isNpc()) return npc_.interact(action);
        return false;
    }
    bool interact(const std::string& a) const { return interact(a.c_str()); }

    // --- Locatable primitives (delegate to underlying variant) ----------
    int32_t tileX() const {
        if (isPlayer()) return player_.tileX();
        if (isNpc())    return npc_.tileX();
        return 0;
    }
    int32_t tileY() const {
        if (isPlayer()) return player_.tileY();
        if (isNpc())    return npc_.tileY();
        return 0;
    }
    int32_t worldX() const {
        if (isPlayer()) return player_.worldX();
        if (isNpc())    return npc_.worldX();
        return 0;
    }
    int32_t worldY() const {
        if (isPlayer()) return player_.worldY();
        if (isNpc())    return npc_.worldY();
        return 0;
    }
    int32_t preciseX() const {
        if (isPlayer()) return player_.preciseX();
        if (isNpc())    return npc_.preciseX();
        return 0;
    }
    int32_t preciseY() const {
        if (isPlayer()) return player_.preciseY();
        if (isNpc())    return npc_.preciseY();
        return 0;
    }
    LocalPoint renderLocalPoint() const {
        return {preciseX(), preciseY(), worldViewId()};
    }
    int32_t plane() const {
        if (isPlayer()) return player_.plane();
        if (isNpc())    return npc_.plane();
        return 0;
    }
    int32_t worldViewId() const {
        if (isPlayer()) return player_.worldViewId();
        if (isNpc())    return npc_.worldViewId();
        return WorldView::CURRENT;
    }
    uint64_t worldViewPtr() const {
        if (isPlayer()) return player_.worldViewPtr();
        if (isNpc())    return npc_.worldViewPtr();
        return 0;
    }
    /// Locatable size hint -- NPCs report their live runtime footprint,
    /// players are 1x1. Empty actors are 1x1.
    int32_t sizeX() const {
        if (isNpc()) return npc_.sizeX();
        return 1;
    }
    int32_t sizeY() const {
        if (isNpc()) return npc_.sizeY();
        return 1;
    }
    // tile(), worldPoint(), localPoint(), distanceTo(Tile), distanceTo(Locatable),
    // hasLineOfSight(WorldPoint), hasLineOfSight(Locatable) all inherited from Locatable.

    std::vector<WorldPoint> pathQueue() const {
        if (isPlayer()) return player_.pathQueue();
        if (isNpc())    return npc_.pathQueue();
        return {};
    }

    /// Complete UTF-8 text; nullopt means unavailable/invalid, empty is valid.
    std::optional<std::string> getOverheadText() const {
        if (isPlayer()) return player_.getOverheadText();
        if (isNpc()) return npc_.getOverheadText();
        return std::nullopt;
    }
    std::optional<int32_t> getOverheadTextCyclesRemaining() const {
        if (isPlayer()) return player_.getOverheadTextCyclesRemaining();
        if (isNpc()) return npc_.getOverheadTextCyclesRemaining();
        return std::nullopt;
    }

    std::vector<ActorSpotAnim> currentSpotAnims() const {
        if (isPlayer()) return player_.currentSpotAnims();
        if (isNpc())    return npc_.currentSpotAnims();
        return {};
    }

    int32_t movementPose() const {
        if (isPlayer()) return player_.movementPose();
        if (isNpc())    return npc_.movementPose();
        return 0;
    }
    int32_t idlePose() const {
        if (isPlayer()) return player_.idlePose();
        if (isNpc())    return npc_.idlePose();
        return 0;
    }
    bool isStationary() const {
        if (isPlayer()) return player_.isStationary();
        if (isNpc())    return npc_.isStationary();
        return false;
    }

    int32_t healthRatio() const {
        if (isPlayer()) return player_.healthRatio();
        if (isNpc())    return npc_.healthRatio();
        return -1;
    }
    int32_t healthScale() const {
        if (isPlayer()) return player_.healthScale();
        if (isNpc())    return npc_.healthScale();
        return -1;
    }
    float healthPercent() const {
        if (isPlayer()) return player_.healthPercent();
        if (isNpc())    return npc_.healthPercent();
        return -1.0f;
    }
    bool hasHealthBar() const {
        if (isPlayer()) return player_.hasHealthBar();
        if (isNpc())    return npc_.hasHealthBar();
        return false;
    }
    bool isDead() const {
        if (isPlayer()) return player_.isDead();
        if (isNpc())    return npc_.isDead();
        return false;
    }
    /// True when the resolved underlying player or NPC is actively
    /// interacting with a target. Returns `false` for an empty Actor.
    bool isInteracting() const {
        if (isPlayer()) return player_.isInteracting();
        if (isNpc())    return npc_.isInteracting();
        return false;
    }

    /// True when the underlying Player or Npc has an overhead icon active.
    /// Delegates to the type-specific predicate; returns `false` for an
    /// empty Actor (no player or npc resolved).
    bool isOverheadActive() const {
        if (isPlayer()) return player_.isOverheadActive();
        if (isNpc())    return npc_.isOverheadActive();
        return false;
    }
    /// True when the underlying Player or Npc is displaying the exact
    /// @p icon. Delegates to the type-specific overload; returns `false`
    /// for an empty Actor. See `Npc::isOverheadActive(HeadIcon)` for the
    /// NPC-specific caveat about runtime overrides.
    bool isOverheadActive(HeadIcon icon) const {
        if (isPlayer()) return player_.isOverheadActive(icon);
        if (isNpc())    return npc_.isOverheadActive(icon);
        return false;
    }

private:
    enum class Kind : uint8_t { none, player, npc };
    Kind kind_ = Kind::none;
    Player player_;
    Npc npc_;
};

inline bool matchesPlayerInteractingIndex(int32_t interactingIndex, int32_t hashIndex) {
    return interactingIndex == hashIndex || interactingIndex == hashIndex + 65536;
}

inline Actor resolveActorByHash(int32_t hashIndex, int32_t entityType,
                                int32_t worldViewId = WorldView::CURRENT) {
    if (hashIndex < 0) return Actor{};
    auto* b = detail::backend();
    if (!b) return Actor{};

    if (entityType == TitanPluginSdk::EntityType::PLAYER) {
        constexpr uint32_t kMaxPlayers = 2048;
        std::vector<TitanPluginSdk::PlayerState> buf(kMaxPlayers);
        const uint32_t n = (std::min)(b->getPlayers(buf.data(), kMaxPlayers), kMaxPlayers);
        for (uint32_t i = 0; i < n; ++i) {
            if (matchesPlayerInteractingIndex(hashIndex, buf[i].hashIndex)
                    && detail::sameWorldViewId(buf[i].worldViewId, worldViewId)) {
                return Actor{Player{buf[i]}};
            }
        }
        return Actor{};
    }

    if (entityType == TitanPluginSdk::EntityType::NPC) {
        constexpr uint32_t kMaxNpcs = 4096;
        std::vector<TitanPluginSdk::NpcState> buf(kMaxNpcs);
        const uint32_t n = (std::min)(b->getNpcs(buf.data(), kMaxNpcs), kMaxNpcs);
        for (uint32_t i = 0; i < n; ++i) {
            if (buf[i].hashIndex == hashIndex
                    && detail::sameWorldViewId(buf[i].worldViewId, worldViewId)) {
                return Actor{Npc{buf[i]}};
            }
        }
    }

    return Actor{};
}

inline Actor Projectile::sourceActor() const {
    return resolveActorByHash(state_.sourceEntity, state_.sourceEntityType);
}

inline Actor Projectile::targetActor() const {
    return resolveActorByHash(state_.targetEntity, state_.targetEntityType);
}

inline Actor resolveInteracting(int32_t idx, uint8_t type) {
    auto* b = detail::backend();
    if (!b) return Actor{};
    TitanPluginSdk::PlayerState ps = {};
    TitanPluginSdk::NpcState   ns = {};
    const uint8_t kind = b->getInteracting(idx, type, &ps, &ns);
    if (kind == TitanPluginSdk::EntityType::PLAYER) return Actor{Player{ps}};
    if (kind == TitanPluginSdk::EntityType::NPC)    return Actor{Npc{ns}};
    return Actor{};
}

inline Actor Player::interacting() const {
    const auto& s = raw();
    Actor actor = resolveActorByHash(s.interactingIndex, s.interactingType, s.worldViewId);
    return actor.isEmpty()
        ? resolveInteracting(s.interactingIndex, s.interactingType)
        : actor;
}
inline Actor Npc::interacting() const {
    const auto& s = raw();
    Actor actor = resolveActorByHash(s.interactingIndex, s.interactingType, s.worldViewId);
    return actor.isEmpty()
        ? resolveInteracting(s.interactingIndex, s.interactingType)
        : actor;
}

}  // namespace titan

namespace std {
template <> struct hash<titan::Player> {
    std::size_t operator()(const titan::Player& value) const noexcept {
        return value.identityHash();
    }
};

template <> struct hash<titan::Npc> {
    std::size_t operator()(const titan::Npc& value) const noexcept {
        return value.identityHash();
    }
};

template <> struct hash<titan::Actor> {
    std::size_t operator()(const titan::Actor& value) const noexcept {
        return value.identityHash();
    }
};
}  // namespace std
