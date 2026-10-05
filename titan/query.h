/// @file titan/query.h
/// @brief Fluent chainable queries over live game state.
///
/// `titan::queries::npcs()`, `players()`, `objects()`, `groundItems()`,
/// `inventory()`, `projectiles()`, `graphicsObjects()`, and `widgets()` each
/// return a Query populated from the HostApi. Queries support shared terminals
/// (first, forEach, toVector, count, any). Entities inside the query carry `.interact(action)`
/// — no domain-specific verbs live on the query itself.
///
/// SDK v41 grouped every query factory under `namespace titan::queries`.
/// Query *types* (`NpcQuery`, `InventoryQuery`, ...) stay at top-level
/// `namespace titan` because they're entity wrappers, not factories.

#pragma once

#include "actor.h"
#include "client.h"
#include "detail/abi.h"
#include "detail/backend.h"
#include "local_point.h"
#include "world_area.h"

#include <algorithm>
#include <cstring>
#include <functional>
#include <initializer_list>
#include <optional>
#include <string>
#include <type_traits>
#include <vector>

namespace titan {
namespace detail {

inline bool stringContainsCI(const std::string& haystack, const std::string& needle) {
    if (needle.empty()) return true;
    if (haystack.size() < needle.size()) return false;
    auto lower = [](char c) { return (c >= 'A' && c <= 'Z') ? static_cast<char>(c + 32) : c; };
    for (size_t i = 0; i + needle.size() <= haystack.size(); ++i) {
        bool match = true;
        for (size_t j = 0; j < needle.size(); ++j) {
            if (lower(haystack[i + j]) != lower(needle[j])) { match = false; break; }
        }
        if (match) return true;
    }
    return false;
}

inline bool stringEqualsCI(const std::string& a, const std::string& b) {
    if (a.size() != b.size()) return false;
    auto lower = [](char c) { return (c >= 'A' && c <= 'Z') ? static_cast<char>(c + 32) : c; };
    for (size_t i = 0; i < a.size(); ++i) {
        if (lower(a[i]) != lower(b[i])) return false;
    }
    return true;
}

inline bool interactionTargets(int32_t idx, uint8_t type,
                               int32_t targetHash, uint8_t targetType) {
    constexpr uint8_t kTypeMissing = TitanPluginSdk::EntityType::NONE;
    constexpr int32_t kPlayerOffset = 65536;

    if (type == kTypeMissing) {
        if (targetType == TitanPluginSdk::EntityType::NPC)
            return idx == targetHash;
        if (targetType == TitanPluginSdk::EntityType::PLAYER)
            return idx == targetHash || idx == targetHash + kPlayerOffset;
        return false;
    }

    return idx == targetHash && type == targetType;
}

}  // namespace detail

/// Common chainable filters and terminals that work on *any* entity type,
/// including ones without a position (e.g. inventory items). Filters mutate
/// `items_` in place and return *this, so chaining is zero-allocation.
///
/// Name filters live on NamedQueryBaseT. Location-dependent operations
/// (`within`, `nearestTo`) live on LocatableQueryBase instead.
template <typename Derived, typename EntityT>
class QueryBase {
public:
    using Predicate = std::function<bool(const EntityT&)>;

    // --- Generic filters ---

    /// Keep only entities satisfying @p pred.
    Derived& where(const Predicate& pred) {
        items_.erase(std::remove_if(items_.begin(), items_.end(),
            [&pred](const EntityT& e) { return !pred(e); }),
            items_.end());
        return self();
    }

    /// Apply @p fn to the query only if @p cond is true.
    template <typename Fn>
    Derived& when(bool cond, Fn&& fn) {
        if (cond) fn(self());
        return self();
    }

    // --- Sorting ---

    /// Sort items using a user-supplied comparator.
    template <typename Cmp>
    Derived& sortBy(Cmp&& cmp) {
        std::sort(items_.begin(), items_.end(), std::forward<Cmp>(cmp));
        return self();
    }

    // --- Terminals (location-independent) ---

    size_t count() const { return items_.size(); }
    bool   any()   const { return !items_.empty(); }
    bool   empty() const { return items_.empty(); }

    /// First matching entity, or std::nullopt if none.
    std::optional<EntityT> first() const {
        if (items_.empty()) return std::nullopt;
        return items_.front();
    }

    template <typename Fn>
    void forEach(Fn&& fn) const { for (const auto& e : items_) fn(e); }

    std::vector<EntityT> toVector() const { return items_; }
    std::vector<EntityT>& items() { return items_; }
    const std::vector<EntityT>& items() const { return items_; }

protected:
    std::vector<EntityT> items_;

    Derived& self() { return static_cast<Derived&>(*this); }
};

/// Adds position-based filters and terminals on top of QueryBase.
///
/// Requires `EntityT::distanceTo(const Tile&)` to be callable. Used by any
/// query over world-space entities (NPCs, players, tile objects, ground
/// items, projectiles).
template <typename Derived, typename EntityT>
class LocatableQueryBase : public QueryBase<Derived, EntityT> {
public:
    Derived& worldView(int32_t worldViewId) {
        return this->where([worldViewId](const EntityT& e) {
            return e.worldViewId() == worldViewId;
        });
    }

    Derived& currentWorldView() {
        int32_t id = WorldView::CURRENT;
        if (auto local = state::client().localPlayer()) {
            id = local->worldViewId();
        } else if (auto snap = state::client().snapshot()) {
            id = snap->currentWorldViewId;
        }
        return worldView(id);
    }

    Derived& topLevelWorldView() {
        return worldView(WorldView::TOP_LEVEL);
    }

    /// Keep only entities within @p radius Chebyshev tiles of @p origin.
    Derived& within(int32_t radius, const Tile& origin) {
        return this->where([radius, origin](const EntityT& e) {
            return e.distanceTo(origin) <= radius;
        });
    }
    Derived& within(int32_t radius, const Player& p) { return within(radius, p.tile()); }
    Derived& within(int32_t radius, const Npc& n)    { return within(radius, n.tile()); }

    /// Absolute world-coord overload. WorldView identity is part of
    /// WorldPoint, so cross-view points do not compare as nearby.
    Derived& within(int32_t radius, const WorldPoint& origin) {
        return this->where([radius, origin](const EntityT& e) {
            return e.worldPoint().distanceTo(origin) <= radius;
        });
    }

    /// Sub-tile-precise scene-local overload. Round-down via `>> 7`
    /// (matches `LocalPoint::sceneX` / `sceneY`) before delegating;
    /// plane is left at `0` because entity-level `distanceTo(Tile)`
    /// ignores plane in any case.
    Derived& within(int32_t radius, const LocalPoint& origin) {
        return within(radius, Tile{origin.x >> 7, origin.y >> 7, 0, origin.worldViewId});
    }

    /// Keep only entities whose absolute world point is inside @p worldArea.
    Derived& within(const WorldArea& worldArea) {
        return this->where([worldArea](const EntityT& e) {
            return worldArea.contains(e.worldPoint());
        });
    }

    /// Nearest entity to @p origin by Chebyshev tile distance, or nullopt.
    std::optional<EntityT> nearestTo(const Tile& origin) const {
        std::optional<EntityT> best;
        int32_t bestDist = INT32_MAX;
        for (const auto& e : this->items_) {
            const int32_t d = e.distanceTo(origin);
            if (d < bestDist) { bestDist = d; best = e; }
        }
        return best;
    }
    std::optional<EntityT> nearestTo(const Player& p) const { return nearestTo(p.tile()); }
    std::optional<EntityT> nearestTo(const Npc& n) const    { return nearestTo(n.tile()); }

    /// Absolute world-coord overload. WorldView identity is part of
    /// WorldPoint, so cross-view points do not compare as nearby.
    std::optional<EntityT> nearestTo(const WorldPoint& origin) const {
        std::optional<EntityT> best;
        int32_t bestDist = INT32_MAX;
        for (const auto& e : this->items_) {
            const int32_t d = e.worldPoint().distanceTo(origin);
            if (d < bestDist) { bestDist = d; best = e; }
        }
        return best;
    }

    /// Sub-tile-precise scene-local overload. Round-down via `>> 7`
    /// (matches `LocalPoint::sceneX` / `sceneY`) before delegating;
    /// plane is left at `0` because entity-level `distanceTo(Tile)`
    /// ignores plane in any case.
    std::optional<EntityT> nearestTo(const LocalPoint& origin) const {
        return nearestTo(Tile{origin.x >> 7, origin.y >> 7, 0, origin.worldViewId});
    }

    /// Nearest entity to the local player. The host's projected player center
    /// also permits comparison with top-level entities while aboard a child view.
    std::optional<EntityT> nearest() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::PlayerState lp = {};
        if (!b->getLocalPlayer(&lp)) return std::nullopt;
        TitanPluginSdk::ClientState snapshot{};
        const bool hasTopLevelCenter = lp.worldViewId > WorldView::TOP_LEVEL && b->getClientState(&snapshot) &&
            snapshot.currentWorldViewId == lp.worldViewId && snapshot.topLevelLocalPlayerTileValid &&
            snapshot.topLevelLocalPlayerPlane >= 0 && snapshot.topLevelLocalPlayerPlane <= 3;
        const Tile current{lp.tileX, lp.tileY, lp.plane, lp.worldViewId};
        const Tile topLevel{snapshot.topLevelLocalPlayerTileX, snapshot.topLevelLocalPlayerTileY,
            snapshot.topLevelLocalPlayerPlane, WorldView::TOP_LEVEL};
        std::optional<EntityT> best;
        int32_t bestDist = INT32_MAX;
        for (const auto& e : this->items_) {
            const bool sameView = detail::sameWorldViewId(e.worldViewId(), lp.worldViewId);
            if (!sameView && (!hasTopLevelCenter || e.worldViewId() != WorldView::TOP_LEVEL)) continue;
            const int32_t distance = e.distanceTo(sameView ? current : topLevel);
            if (distance < bestDist) { bestDist = distance; best = e; }
        }
        return best;
    }

    // --- Exact-position filters ---

    /// Keep only entities on the exact scene tile.
    Derived& onTile(const Tile& t) {
        return this->where([t](const EntityT& e) {
            return e.tile() == t;
        });
    }
    Derived& onTile(int32_t x, int32_t y) {
        return this->where([x, y](const EntityT& e) {
            return e.tileX() == x && e.tileY() == y;
        });
    }

    /// Keep only entities at the exact absolute world coordinate and WorldView.
    Derived& atWorldPoint(const WorldPoint& wp) {
        return this->where([wp](const EntityT& e) {
            return e.worldPoint() == wp;
        });
    }

    // --- Sorting ---

    /// Sort ascending by Chebyshev tile distance from @p origin.
    Derived& sortedByDistanceTo(const Tile& origin) {
        std::sort(this->items_.begin(), this->items_.end(),
            [origin](const EntityT& a, const EntityT& b) {
                return a.distanceTo(origin) < b.distanceTo(origin);
            });
        return this->self();
    }
    Derived& sortedByDistanceTo(const Player& p) { return sortedByDistanceTo(p.tile()); }
    Derived& sortedByDistanceTo(const Npc& n)    { return sortedByDistanceTo(n.tile()); }
    Derived& sortedByDistanceTo(const WorldPoint& origin) {
        std::sort(this->items_.begin(), this->items_.end(),
            [origin](const EntityT& a, const EntityT& b) {
                return a.worldPoint().distanceTo(origin) < b.worldPoint().distanceTo(origin);
            });
        return this->self();
    }
    Derived& sortedByDistanceTo(const LocalPoint& origin) {
        return sortedByDistanceTo(Tile{origin.x >> 7, origin.y >> 7, 0, origin.worldViewId});
    }
};

/// Adds name-specific filters only to entity queries that expose `.name()`.
template <typename Derived, typename EntityT, typename BaseT>
class NamedQueryBaseT : public BaseT {
public:
    /// Case-insensitive substring match against .name().
    Derived& nameContains(const std::string& needle) {
        return this->where([needle](const EntityT& e) {
            return detail::stringContainsCI(e.name(), needle);
        });
    }

    /// Case-insensitive full-name match.
    Derived& nameEquals(const std::string& name) {
        return this->where([name](const EntityT& e) {
            return detail::stringEqualsCI(e.name(), name);
        });
    }

    /// Case-insensitive substring match against any of @p names.
    Derived& namesAnyOf(std::initializer_list<std::string> names) {
        std::vector<std::string> copy(names.begin(), names.end());
        return this->where([copy](const EntityT& e) {
            const std::string n = e.name();
            for (const auto& c : copy) if (detail::stringContainsCI(n, c)) return true;
            return false;
        });
    }
};

template <typename Derived, typename EntityT>
using NamedQueryBase = NamedQueryBaseT<Derived, EntityT, QueryBase<Derived, EntityT>>;

template <typename Derived, typename EntityT>
using NamedLocatableQueryBase =
    NamedQueryBaseT<Derived, EntityT, LocatableQueryBase<Derived, EntityT>>;

// ---------------------------------------------------------------------------
// NpcQuery
// ---------------------------------------------------------------------------

class NpcQuery : public NamedLocatableQueryBase<NpcQuery, Npc> {
public:
    NpcQuery() {
        auto* b = detail::backend();
        if (!b) return;
        constexpr uint32_t kMax = 512;
        std::vector<TitanPluginSdk::NpcState> buf(kMax);
        const uint32_t n = (std::min)(b->getNpcs(buf.data(), kMax), kMax);
        items_.reserve(n);
        for (uint32_t i = 0; i < n; ++i) items_.emplace_back(buf[i]);
    }

    NpcQuery& id(int32_t npcId) {
        return where([npcId](const Npc& n) { return n.id() == npcId; });
    }
    NpcQuery& ids(std::initializer_list<int32_t> ids) {
        std::vector<int32_t> copy(ids.begin(), ids.end());
        return where([copy](const Npc& n) {
            for (int32_t i : copy) if (n.id() == i) return true;
            return false;
        });
    }
    NpcQuery& hasAction(const std::string& action) {
        return where([action](const Npc& n) { return n.hasAction(action); });
    }
    /// Keep NPCs exposing at least one of @p actions.
    NpcQuery& hasAction(std::initializer_list<std::string> actions) {
        std::vector<std::string> copy(actions.begin(), actions.end());
        return where([copy](const Npc& n) {
            for (const auto& action : copy) {
                if (n.hasAction(action)) return true;
            }
            return false;
        });
    }
    /// Keep only NPCs whose definition combat level is at least @p minLevel.
    NpcQuery& combatLevelAbove(int32_t minLevel) {
        return where([minLevel](const Npc& n) {
            const auto definition = state::cache().npc(n.id());
            return definition && definition->combatLevel >= minLevel;
        });
    }
    /// Keep only NPCs whose definition combat level is at most @p maxLevel.
    NpcQuery& combatLevelBelow(int32_t maxLevel) {
        return where([maxLevel](const Npc& n) {
            const auto definition = state::cache().npc(n.id());
            return definition && definition->combatLevel <= maxLevel;
        });
    }
    /// Keep only NPCs whose definition combat level is within the inclusive range.
    NpcQuery& combatLevelBetween(int32_t low, int32_t high) {
        const int32_t minLevel = (std::min)(low, high);
        const int32_t maxLevel = (std::max)(low, high);
        return where([minLevel, maxLevel](const Npc& n) {
            const auto definition = state::cache().npc(n.id());
            return definition
                && definition->combatLevel >= minLevel
                && definition->combatLevel <= maxLevel;
        });
    }
    /// Exclude one exact NPC identity (WorldView + hash index).
    NpcQuery& exclude(const Npc& npc) {
        return where([npc](const Npc& candidate) { return candidate != npc; });
    }
    /// Exclude any exact NPC identity in @p npcs.
    NpcQuery& exclude(std::initializer_list<Npc> npcs) {
        std::vector<Npc> copy(npcs.begin(), npcs.end());
        return where([copy](const Npc& candidate) {
            for (const auto& npc : copy) {
                if (candidate == npc) return false;
            }
            return true;
        });
    }
    /// Keep only NPCs not actively being targeted by any other player.
    NpcQuery& notTargetedByOtherPlayers();

    /// Keep only NPCs actively interacting with a specific actor.
    NpcQuery& interactingWith(const Actor& target);
    /// Keep only NPCs actively interacting with the local player.
    NpcQuery& interactingWithLocal();
    /// Keep only NPCs that have no active interaction target.
    NpcQuery& notInteracting() {
        return where([](const Npc& n) { return !n.isInteracting(); });
    }
    /// Keep only NPCs currently playing an animation.
    NpcQuery& isAnimating() {
        return where([](const Npc& n) { return n.animation() != -1; });
    }
    /// Keep only NPCs not currently animating.
    NpcQuery& notAnimating() {
        return where([](const Npc& n) { return n.animation() == -1; });
    }
    /// Keep only NPCs playing the exact animation ID.
    NpcQuery& animation(int32_t animId) {
        return where([animId](const Npc& n) { return n.animation() == animId; });
    }
    /// Keep only NPCs with no pending movement. SDK v73+.
    NpcQuery& isStationary() {
        return where([](const Npc& n) { return n.isStationary(); });
    }
    /// Keep only NPCs with any active overhead icon.
    NpcQuery& overheadActive() {
        return where([](const Npc& n) { return n.isOverheadActive(); });
    }
    /// Keep only NPCs with the exact overhead icon.
    NpcQuery& overheadActive(HeadIcon icon) {
        return where([icon](const Npc& n) { return n.isOverheadActive(icon); });
    }
    /// Keep only NPCs whose overrideTransform matches (morphing NPCs).
    NpcQuery& overrideTransform(int32_t transformId) {
        return where([transformId](const Npc& n) { return n.raw().overrideTransform == transformId; });
    }
    /// Keep only NPCs whose tile footprint equals @p s on both axes.
    NpcQuery& sizeEquals(int32_t s) {
        return where([s](const Npc& n) { return n.sizeX() == s && n.sizeY() == s; });
    }
    /// Keep only NPCs that are dead (health bar at 0%). SDK v48+.
    NpcQuery& isDead() {
        return where([](const Npc& n) { return n.isDead(); });
    }
    /// Keep only NPCs that are alive (no health bar, or ratio > 0). SDK v48+.
    NpcQuery& isAlive() {
        return where([](const Npc& n) { return !n.isDead(); });
    }
    /// Keep only NPCs with an active health bar. SDK v48+.
    NpcQuery& withHealthBar() {
        return where([](const Npc& n) { return n.hasHealthBar(); });
    }
    /// Keep only NPCs without an active health bar. SDK v48+.
    NpcQuery& noHealthBar() {
        return where([](const Npc& n) { return !n.hasHealthBar(); });
    }
    /// Keep only NPCs whose health percent is below @p threshold [0.0, 1.0]. SDK v48+.
    NpcQuery& healthPercentBelow(float threshold) {
        return where([threshold](const Npc& n) {
            float pct = n.healthPercent();
            return pct >= 0.0f && pct < threshold;
        });
    }
    /// Keep only NPCs whose health percent is above @p threshold [0.0, 1.0]. SDK v48+.
    NpcQuery& healthPercentAbove(float threshold) {
        return where([threshold](const Npc& n) {
            float pct = n.healthPercent();
            return pct >= 0.0f && pct > threshold;
        });
    }
};

// ---------------------------------------------------------------------------
// PlayerQuery
// ---------------------------------------------------------------------------

class PlayerQuery : public NamedLocatableQueryBase<PlayerQuery, Player> {
public:
    PlayerQuery() {
        auto* b = detail::backend();
        if (!b) return;
        constexpr uint32_t kMax = 256;
        std::vector<TitanPluginSdk::PlayerState> buf(kMax);
        const uint32_t n = (std::min)(b->getPlayers(buf.data(), kMax), kMax);
        items_.reserve(n);
        for (uint32_t i = 0; i < n; ++i) items_.emplace_back(buf[i]);
    }

    /// Keep only players actively interacting with a specific actor.
    PlayerQuery& interactingWith(const Actor& target);
    /// Keep only players actively interacting with the local player.
    PlayerQuery& interactingWithLocal();
    /// Keep only players that have no active interaction target.
    PlayerQuery& notInteracting() {
        return where([](const Player& p) { return !p.isInteracting(); });
    }
    /// Keep only players currently playing an animation.
    PlayerQuery& isAnimating() {
        return where([](const Player& p) { return p.animation() != -1; });
    }
    /// Keep only players not currently animating.
    PlayerQuery& notAnimating() {
        return where([](const Player& p) { return p.animation() == -1; });
    }
    /// Keep only players playing the exact animation ID.
    PlayerQuery& animation(int32_t animId) {
        return where([animId](const Player& p) { return p.animation() == animId; });
    }
    /// Keep only players with no pending movement. SDK v73+.
    PlayerQuery& isStationary() {
        return where([](const Player& p) { return p.isStationary(); });
    }
    /// Keep only fully idle players (stationary and not animating).
    PlayerQuery& isIdle() {
        return where([](const Player& p) { return p.isIdle(); });
    }
    /// Keep only skulled players.
    PlayerQuery& isSkulled() {
        return where([](const Player& p) { return p.isSkulled(); });
    }
    /// Keep only players with any active overhead icon.
    PlayerQuery& overheadActive() {
        return where([](const Player& p) { return p.isOverheadActive(); });
    }
    /// Keep only players with the exact overhead icon.
    PlayerQuery& overheadActive(HeadIcon icon) {
        return where([icon](const Player& p) { return p.isOverheadActive(icon); });
    }
    /// Keep only players with combat level >= @p minLevel.
    PlayerQuery& combatLevelAbove(int32_t minLevel) {
        return where([minLevel](const Player& p) { return p.combatLevel() >= minLevel; });
    }
    /// Keep only players with combat level <= @p maxLevel.
    PlayerQuery& combatLevelBelow(int32_t maxLevel) {
        return where([maxLevel](const Player& p) { return p.combatLevel() <= maxLevel; });
    }
    /// Keep only players with combat level in [lo, hi] inclusive.
    PlayerQuery& combatLevelBetween(int32_t lo, int32_t hi) {
        return where([lo, hi](const Player& p) {
            return p.combatLevel() >= lo && p.combatLevel() <= hi;
        });
    }

    /// Keep only players that are dead (health bar at 0%). SDK v48+.
    PlayerQuery& isDead() {
        return where([](const Player& p) { return p.isDead(); });
    }
    /// Keep only players that are alive (no health bar, or ratio > 0). SDK v48+.
    PlayerQuery& isAlive() {
        return where([](const Player& p) { return !p.isDead(); });
    }
    /// Keep only players with an active health bar. SDK v48+.
    PlayerQuery& withHealthBar() {
        return where([](const Player& p) { return p.hasHealthBar(); });
    }
    /// Keep only players without an active health bar. SDK v48+.
    PlayerQuery& noHealthBar() {
        return where([](const Player& p) { return !p.hasHealthBar(); });
    }
    /// Keep only players whose health percent is below @p threshold [0.0, 1.0]. SDK v48+.
    PlayerQuery& healthPercentBelow(float threshold) {
        return where([threshold](const Player& p) {
            float pct = p.healthPercent();
            return pct >= 0.0f && pct < threshold;
        });
    }
    /// Keep only players whose health percent is above @p threshold [0.0, 1.0]. SDK v48+.
    PlayerQuery& healthPercentAbove(float threshold) {
        return where([threshold](const Player& p) {
            float pct = p.healthPercent();
            return pct >= 0.0f && pct > threshold;
        });
    }

    /// Exclude the local player (resolved via HostApi::getLocalPlayer).
    PlayerQuery& excludingSelf();
};

// ---------------------------------------------------------------------------
// ObjectQuery (tile objects / locs)
// ---------------------------------------------------------------------------

class ObjectQuery : public NamedLocatableQueryBase<ObjectQuery, TileObject> {
public:
    explicit ObjectQuery(int32_t radius = 20) {
        auto* b = detail::backend();
        if (!b) return;
        constexpr uint32_t kMax = 1024;
        std::vector<TitanPluginSdk::TileObjectState> buf(kMax);
        const uint32_t n = (std::min)(b->getTileObjects(radius, buf.data(), kMax), kMax);
        items_.reserve(n);
        for (uint32_t i = 0; i < n; ++i) items_.emplace_back(buf[i]);
    }

    ObjectQuery& id(int32_t locId) {
        return where([locId](const TileObject& o) { return o.id() == locId; });
    }
    ObjectQuery& ids(std::initializer_list<int32_t> ids) {
        std::vector<int32_t> copy(ids.begin(), ids.end());
        return where([copy](const TileObject& o) {
            for (int32_t i : copy) if (o.id() == i) return true;
            return false;
        });
    }
    ObjectQuery& hasAction(const std::string& action) {
        return where([action](const TileObject& o) { return o.hasAction(action); });
    }
    /// Keep tile objects exposing at least one of @p actions.
    ObjectQuery& hasAction(std::initializer_list<std::string> actions) {
        std::vector<std::string> copy(actions.begin(), actions.end());
        return where([copy](const TileObject& o) {
            for (const auto& action : copy) {
                if (o.hasAction(action)) return true;
            }
            return false;
        });
    }
    ObjectQuery& ofType(const std::string& typeName) {
        return where([typeName](const TileObject& o) {
            return detail::stringEqualsCI(o.typeName(), typeName);
        });
    }
    /// Keep only objects on the given scene layer (0=Wall, 1=Decor, 2=Scenery, 3=GroundDecor).
    ObjectQuery& layer(int32_t layerId) {
        return where([layerId](const TileObject& o) { return o.layer() == layerId; });
    }
};

// ---------------------------------------------------------------------------
// GroundItemQuery
// ---------------------------------------------------------------------------

class GroundItemQuery : public NamedLocatableQueryBase<GroundItemQuery, GroundItem> {
public:
    explicit GroundItemQuery(int32_t radius = 20) {
        auto* b = detail::backend();
        if (!b) return;
        constexpr uint32_t kMax = 512;
        std::vector<TitanPluginSdk::GroundItemState> buf(kMax);
        const uint32_t n = (std::min)(b->getGroundItems(radius, buf.data(), kMax), kMax);
        items_.reserve(n);
        for (uint32_t i = 0; i < n; ++i) items_.emplace_back(buf[i]);
    }

    GroundItemQuery& id(int32_t itemId) {
        return where([itemId](const GroundItem& g) { return g.id() == itemId; });
    }
    GroundItemQuery& ids(std::initializer_list<int32_t> ids) {
        std::vector<int32_t> copy(ids.begin(), ids.end());
        return where([copy](const GroundItem& g) {
            for (int32_t i : copy) if (g.id() == i) return true;
            return false;
        });
    }
    GroundItemQuery& minQuantity(int32_t n) {
        return where([n](const GroundItem& g) { return g.quantity() >= n; });
    }
    GroundItemQuery& maxQuantity(int32_t n) {
        return where([n](const GroundItem& g) { return g.quantity() <= n; });
    }
    GroundItemQuery& canLoot() {
        return where([](const GroundItem& g) { return g.canLoot(); });
    }
};

// ---------------------------------------------------------------------------
// InventoryQuery
// ---------------------------------------------------------------------------

class InventoryQuery : public NamedQueryBase<InventoryQuery, Item> {
public:
    InventoryQuery() {
        auto* b = detail::backend();
        if (!b) return;
        constexpr uint32_t kMax = 32;
        std::vector<TitanPluginSdk::InventoryItemState> buf(kMax);
        const uint32_t n = (std::min)(b->getInventoryItems(buf.data(), kMax), kMax);
        items_.reserve(n);
        for (uint32_t i = 0; i < n; ++i) items_.emplace_back(buf[i]);
    }

    InventoryQuery& id(int32_t itemId) {
        return where([itemId](const Item& it) { return it.id() == itemId; });
    }
    InventoryQuery& ids(std::initializer_list<int32_t> ids) {
        std::vector<int32_t> copy(ids.begin(), ids.end());
        return where([copy](const Item& it) {
            for (int32_t i : copy) if (it.id() == i) return true;
            return false;
        });
    }
    /// Keep only items in the exact inventory slot.
    InventoryQuery& slot(int32_t index) {
        return where([index](const Item& it) { return it.slot() == index; });
    }
    /// Keep items whose slot matches any of @p slots.
    InventoryQuery& slotsAnyOf(std::initializer_list<int32_t> slots) {
        std::vector<int32_t> copy(slots.begin(), slots.end());
        return where([copy](const Item& it) {
            for (int32_t slot : copy) if (it.slot() == slot) return true;
            return false;
        });
    }
    /// Keep only items whose slot is within [minSlot, maxSlot], inclusive.
    InventoryQuery& slotsBetween(int32_t minSlot, int32_t maxSlot) {
        const int32_t lo = (std::min)(minSlot, maxSlot);
        const int32_t hi = (std::max)(minSlot, maxSlot);
        return where([lo, hi](const Item& it) {
            const int32_t slot = it.slot();
            return slot >= lo && slot <= hi;
        });
    }

    /// Keep only items with quantity >= @p n (per-slot).
    InventoryQuery& minQuantity(int32_t n) {
        return where([n](const Item& it) { return it.quantity() >= n; });
    }
    /// Keep only items with quantity <= @p n (per-slot).
    InventoryQuery& maxQuantity(int32_t n) {
        return where([n](const Item& it) { return it.quantity() <= n; });
    }
    /// Keep only items exposing @p action in their runtime inventory actions.
    InventoryQuery& hasAction(const std::string& action) {
        return where([action](const Item& it) {
            if (action.empty()) return false;
            const auto definition = state::itemDef(it.id());
            if (!definition) return false;
            for (const auto& candidate : definition->inventoryActions) {
                if (detail::stringContainsCI(candidate, action)) return true;
            }
            return false;
        });
    }
    /// Keep only noted item variants.
    InventoryQuery& isNoted() {
        return where([](const Item& it) {
            const auto definition = state::cache().item(it.id());
            return definition && definition->noted;
        });
    }
    /// Remove items whose id matches any in @p ids.
    InventoryQuery& excludeIds(std::initializer_list<int32_t> ids) {
        std::vector<int32_t> copy(ids.begin(), ids.end());
        return where([copy](const Item& it) {
            for (int32_t i : copy) if (it.id() == i) return false;
            return true;
        });
    }
    /// Remove items whose name matches any in @p names (CI substring).
    InventoryQuery& excludeNames(std::initializer_list<std::string> names) {
        std::vector<std::string> copy(names.begin(), names.end());
        return where([copy](const Item& it) {
            const std::string n = it.name();
            for (const auto& c : copy) if (detail::stringContainsCI(n, c)) return false;
            return true;
        });
    }

    /// Total quantity across all matching stacks.
    int32_t totalQuantity() const {
        int32_t total = 0;
        for (const auto& it : items_) total += it.quantity();
        return total;
    }

    /// True when the inventory has at least one matching item.
    bool exists() const { return !items_.empty(); }
};

// ---------------------------------------------------------------------------
// ProjectileQuery
// ---------------------------------------------------------------------------

class ProjectileQuery : public LocatableQueryBase<ProjectileQuery, Projectile> {
public:
    ProjectileQuery() {
        auto* b = detail::backend();
        if (!b) return;
        constexpr uint32_t kMax = 256;
        std::vector<TitanPluginSdk::ProjectileState> buf(kMax);
        const uint32_t n = (std::min)(b->getProjectiles(buf.data(), kMax), kMax);
        items_.reserve(n);
        for (uint32_t i = 0; i < n; ++i) items_.emplace_back(buf[i]);
    }

    ProjectileQuery& spotAnim(int32_t animId) {
        return this->where([animId](const Projectile& p) { return p.spotAnimId() == animId; });
    }
    /// Keep only projectiles targeting the given decoded actor hash index.
    ProjectileQuery& targetingEntity(int32_t entityIndex) {
        return this->where([entityIndex](const Projectile& p) { return p.targetEntity() == entityIndex; });
    }
    /// Keep only projectiles from the given decoded actor hash index.
    ProjectileQuery& fromEntity(int32_t entityIndex) {
        return this->where([entityIndex](const Projectile& p) { return p.sourceEntity() == entityIndex; });
    }
    /// Keep only projectiles targeting the exact player/NPC actor.
    ProjectileQuery& targetingActor(const Actor& actor) {
        return targetingActorRef(actor.hashIndex(), actor.entityType());
    }
    ProjectileQuery& targetingActor(const Player& player) {
        return targetingActorRef(player.hashIndex(), TitanPluginSdk::EntityType::PLAYER);
    }
    ProjectileQuery& targetingActor(const Npc& npc) {
        return targetingActorRef(npc.hashIndex(), TitanPluginSdk::EntityType::NPC);
    }
    /// Keep only projectiles from the exact player/NPC actor.
    ProjectileQuery& fromActor(const Actor& actor) {
        return fromActorRef(actor.hashIndex(), actor.entityType());
    }
    ProjectileQuery& fromActor(const Player& player) {
        return fromActorRef(player.hashIndex(), TitanPluginSdk::EntityType::PLAYER);
    }
    ProjectileQuery& fromActor(const Npc& npc) {
        return fromActorRef(npc.hashIndex(), TitanPluginSdk::EntityType::NPC);
    }
    /// Keep only projectiles that started on or after @p tick.
    ProjectileQuery& startedAfterTick(int32_t tick) {
        return this->where([tick](const Projectile& p) { return p.startTick() >= tick; });
    }
    /// Keep only projectiles that end on or before @p tick.
    ProjectileQuery& endsBeforeTick(int32_t tick) {
        return this->where([tick](const Projectile& p) { return p.endTick() <= tick; });
    }
    /// Keep only projectiles active during @p tick (startTick <= tick <= endTick).
    ProjectileQuery& activeDuring(int32_t tick) {
        return this->where([tick](const Projectile& p) {
            return p.startTick() <= tick && p.endTick() >= tick;
        });
    }

private:
    ProjectileQuery& targetingActorRef(int32_t hashIndex, int32_t entityType) {
        return this->where([hashIndex, entityType](const Projectile& p) {
            return hashIndex >= 0
                && entityType != TitanPluginSdk::EntityType::NONE
                && p.targetEntity() == hashIndex
                && p.targetEntityType() == entityType;
        });
    }
    ProjectileQuery& fromActorRef(int32_t hashIndex, int32_t entityType) {
        return this->where([hashIndex, entityType](const Projectile& p) {
            return hashIndex >= 0
                && entityType != TitanPluginSdk::EntityType::NONE
                && p.sourceEntity() == hashIndex
                && p.sourceEntityType() == entityType;
        });
    }
};

// ---------------------------------------------------------------------------
// GraphicsObjectQuery (SDK 57+)
// ---------------------------------------------------------------------------

/// Filterable collection of active map-tile spot animations. Sourced from
/// `IBackend::getGraphicsObjects`, which walks `WorldView::GraphicsObjectList`
/// on the bound revision. Returns an empty query when the analyzer hasn't
/// detected the list head.
class GraphicsObjectQuery : public LocatableQueryBase<GraphicsObjectQuery, GraphicsObject> {
public:
    GraphicsObjectQuery() {
        auto* b = detail::backend();
        if (!b) return;
        constexpr uint32_t kMax = 256;
        std::vector<TitanPluginSdk::GraphicsObjectState> buf(kMax);
        const uint32_t n = (std::min)(b->getGraphicsObjects(buf.data(), kMax), kMax);
        items_.reserve(n);
        for (uint32_t i = 0; i < n; ++i) items_.emplace_back(buf[i]);
    }

    /// Keep only graphics objects with the given spot-anim definition id.
    GraphicsObjectQuery& spotAnim(int32_t animId) {
        return this->where([animId](const GraphicsObject& g) { return g.spotAnimId() == animId; });
    }
    /// Keep only graphics objects on the given floor plane.
    GraphicsObjectQuery& onPlane(int32_t plane) {
        return this->where([plane](const GraphicsObject& g) { return g.plane() == plane; });
    }
    /// Keep only graphics objects that started on or after @p tick.
    GraphicsObjectQuery& startedAfterTick(int32_t tick) {
        return this->where([tick](const GraphicsObject& g) { return g.startCycle() >= tick; });
    }
    /// Keep only graphics objects that started on or before @p tick.
    GraphicsObjectQuery& startedBeforeTick(int32_t tick) {
        return this->where([tick](const GraphicsObject& g) { return g.startCycle() <= tick; });
    }
};

// ---------------------------------------------------------------------------
// WidgetQuery (SDK 64+)
// ---------------------------------------------------------------------------

/// Filterable loaded-widget handles. Initial materialization includes loaded
/// flat widgets plus recursively reachable dynamic descendants. The result
/// membership is fixed at query time; each `Widget` inside it remains live.
/// `children()` replaces the working set with each match's direct non-null
/// dynamic children.
class WidgetQuery : public QueryBase<WidgetQuery, Widget> {
public:
    explicit WidgetQuery(uint32_t groupId = TitanPluginSdk::kAllWidgetGroups) {
        auto* b = detail::backend();
        if (!b) return;

        uint8_t countTruncated = 0;
        const uint32_t reported = b->getWidgets(groupId, nullptr, 0, &countTruncated);
        truncated_ = countTruncated != 0;
        const uint32_t count = (std::min)(
            reported, TitanPluginSdk::kMaxWidgetQueryResults);
        truncated_ = truncated_ || reported > count;
        if (count == 0) return;

        std::vector<TitanPluginSdk::WidgetQueryState> raw(count);
        uint8_t fillTruncated = 0;
        const uint32_t actual = b->getWidgets(
            groupId, raw.data(), static_cast<uint32_t>(raw.size()), &fillTruncated);
        truncated_ = truncated_ || fillTruncated != 0 || actual > raw.size();

        const uint32_t n = (std::min)(actual, static_cast<uint32_t>(raw.size()));
        items_.reserve(n);
        for (uint32_t i = 0; i < n; ++i) {
            items_.emplace_back(detail::widgetSnapshotFromQueryState(raw[i]));
        }
    }

    /// Keep widgets with @p id. A flat-table match wins over dynamic fallback
    /// ids so `.packedId(parent).children()` expands the intended root.
    WidgetQuery& packedId(int32_t id) {
        where([id](const Widget& widget) {
            return widget.packedId() == id;
        });
        const bool hasFlat = std::any_of(
            items_.begin(), items_.end(),
            [](const Widget& widget) { return widget.dynamicPath().empty(); });
        if (hasFlat) {
            items_.erase(
                std::remove_if(
                    items_.begin(), items_.end(),
                    [](const Widget& widget) {
                        return !widget.dynamicPath().empty();
                    }),
                items_.end());
        }
        return *this;
    }
    WidgetQuery& group(uint32_t id) {
        return where([id](const Widget& widget) {
            return ((static_cast<uint32_t>(widget.packedId()) >> 16) & 0xFFFFu) == id;
        });
    }
    /// Keep widgets whose packed component id matches @p id.
    WidgetQuery& child(uint32_t id) {
        return where([id](const Widget& widget) {
            return (static_cast<uint32_t>(widget.packedId()) & 0xFFFFu) == id;
        });
    }
    /// Replace each match with its direct dynamic child at native @p index.
    /// Missing and null slots are omitted. May be chained for nested paths.
    WidgetQuery& slot(int32_t index) {
        children();
        return where([index](const Widget& widget) {
            return widget.dynamicChildSlot() == index;
        });
    }
    WidgetQuery& textContains(const std::string& needle) {
        return where([needle](const Widget& widget) {
            return detail::stringContainsCI(widget.text(), needle);
        });
    }
    /// Keep widgets whose text contains at least one supplied needle.
    WidgetQuery& textContains(std::initializer_list<std::string> needles) {
        std::vector<std::string> copy(needles.begin(), needles.end());
        return where([copy](const Widget& widget) {
            const std::string text = widget.text();
            for (const auto& needle : copy) {
                if (detail::stringContainsCI(text, needle)) return true;
            }
            return false;
        });
    }
    WidgetQuery& textEquals(const std::string& text) {
        return where([text](const Widget& widget) {
            return detail::stringEqualsCI(widget.text(), text);
        });
    }
    WidgetQuery& isVisible() {
        return where([](const Widget& widget) { return widget.visible(); });
    }
    WidgetQuery& isHidden() {
        return where([](const Widget& widget) { return !widget.visible(); });
    }
    WidgetQuery& type(int32_t id) {
        return where([id](const Widget& widget) { return widget.type() == id; });
    }
    WidgetQuery& contentType(int32_t id) {
        return where([id](const Widget& widget) {
            return widget.contentType() == id;
        });
    }
    WidgetQuery& itemId(int32_t id) {
        return where([id](const Widget& widget) { return widget.itemId() == id; });
    }

    /// Replace the working set with direct non-null dynamic children while
    /// retaining each child's exact root-plus-slot path.
    WidgetQuery& children() {
        auto* b = detail::backend();
        if (!b) {
            items_.clear();
            return *this;
        }

        std::vector<Widget> children;
        bool full = false;
        for (const auto& parent : items_) {
            if (parent.dynamicPath().size() > TitanPluginSdk::kMaxWidgetAddressDepth) {
                truncated_ = true;
                continue;
            }
            const auto address = parent.addressState();
            const uint32_t reported =
                b->getWidgetChildrenAtPath(&address, nullptr, 0);
            const uint32_t count = (std::min)(
                reported, TitanPluginSdk::kMaxWidgetDynamicChildren);
            if (reported > count) truncated_ = true;
            if (count == 0) continue;

            std::vector<TitanPluginSdk::WidgetQueryState> raw(count);
            const uint32_t actual = b->getWidgetChildrenAtPath(
                &address, raw.data(), static_cast<uint32_t>(raw.size()));
            if (actual > raw.size()) truncated_ = true;
            const uint32_t n = (std::min)(actual, static_cast<uint32_t>(raw.size()));
            for (uint32_t i = 0; i < n; ++i) {
                Widget child{detail::widgetSnapshotFromQueryState(raw[i])};
                bool duplicate = false;
                for (const auto& existing : children) {
                    if (existing == child) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    if (children.size() >= TitanPluginSdk::kMaxWidgetQueryResults) {
                        truncated_ = true;
                        full = true;
                        break;
                    }
                    children.push_back(std::move(child));
                }
            }
            if (full) break;
        }
        items_ = std::move(children);
        return *this;
    }

    bool isTruncated() const { return truncated_; }

private:
    bool truncated_ = false;
};

// ---------------------------------------------------------------------------
// Factories -- live in namespace titan::queries (SDK v41+).
// ---------------------------------------------------------------------------

namespace queries {

inline ::titan::NpcQuery            npcs()                          { return ::titan::NpcQuery{}; }
inline ::titan::PlayerQuery         players()                       { return ::titan::PlayerQuery{}; }
inline ::titan::ObjectQuery         objects(int32_t radius = 20)    { return ::titan::ObjectQuery{radius}; }
inline ::titan::GroundItemQuery     groundItems(int32_t radius = 20){ return ::titan::GroundItemQuery{radius}; }
inline ::titan::InventoryQuery      inventory()                     { return ::titan::InventoryQuery{}; }
inline ::titan::ProjectileQuery     projectiles()                   { return ::titan::ProjectileQuery{}; }
inline ::titan::GraphicsObjectQuery graphicsObjects()               { return ::titan::GraphicsObjectQuery{}; }
inline ::titan::WidgetQuery         widgets()                        { return ::titan::WidgetQuery{}; }
inline ::titan::WidgetQuery         widgets(uint32_t groupId)        { return ::titan::WidgetQuery{groupId}; }

}  // namespace queries

// ---------------------------------------------------------------------------
// NpcQuery / PlayerQuery tail-end definitions
// ---------------------------------------------------------------------------

inline NpcQuery& NpcQuery::notTargetedByOtherPlayers() {
    auto* b = detail::backend();
    if (!b) return *this;
    constexpr uint32_t kMax = 256;
    std::vector<TitanPluginSdk::PlayerState> players(kMax);
    const uint32_t playerCount = (std::min)(b->getPlayers(players.data(), kMax), kMax);

    // Resolve local player's entity pointer so we can exclude self.
    uint64_t localPtr = 0;
    TitanPluginSdk::PlayerState lp = {};
    if (b->getLocalPlayer(&lp)) localPtr = lp.entityPtr;

    return where([&players, playerCount, localPtr](const Npc& n) {
        for (uint32_t i = 0; i < playerCount; ++i) {
            if (players[i].entityPtr == localPtr) continue;
            const Player player{players[i]};
            if (player.isInteracting() &&
                detail::interactionTargets(player.interactingIndex(),
                                           player.interactingType(),
                                           n.hashIndex(),
                                           TitanPluginSdk::EntityType::NPC)) {
                return false;
            }
        }
        return true;
    });
}

inline PlayerQuery& PlayerQuery::excludingSelf() {
    auto* b = detail::backend();
    uint64_t localPtr = 0;
    if (b) {
        TitanPluginSdk::PlayerState lp = {};
        if (b->getLocalPlayer(&lp)) localPtr = lp.entityPtr;
    }
    return where([localPtr](const Player& p) {
        return p.entityPtr() != localPtr;
    });
}

inline NpcQuery& NpcQuery::interactingWith(const Actor& target) {
    const int32_t targetHash = target.isPlayer() ? target.asPlayer()->hashIndex()
                             : target.isNpc()    ? target.asNpc()->hashIndex()
                             : -1;
    const uint8_t targetType = target.isPlayer() ? TitanPluginSdk::EntityType::PLAYER
                             : target.isNpc()    ? TitanPluginSdk::EntityType::NPC
                             : TitanPluginSdk::EntityType::NONE;
    if (targetHash < 0) return *this;
    return where([targetHash, targetType](const Npc& n) {
        return n.isInteracting() &&
            detail::interactionTargets(n.interactingIndex(), n.interactingType(),
                                       targetHash, targetType);
    });
}

inline NpcQuery& NpcQuery::interactingWithLocal() {
    auto* b = detail::backend();
    if (!b) return *this;
    TitanPluginSdk::PlayerState lp = {};
    if (!b->getLocalPlayer(&lp)) return *this;
    return interactingWith(Actor{Player{lp}});
}

inline PlayerQuery& PlayerQuery::interactingWith(const Actor& target) {
    const int32_t targetHash = target.isPlayer() ? target.asPlayer()->hashIndex()
                             : target.isNpc()    ? target.asNpc()->hashIndex()
                             : -1;
    const uint8_t targetType = target.isPlayer() ? TitanPluginSdk::EntityType::PLAYER
                             : target.isNpc()    ? TitanPluginSdk::EntityType::NPC
                             : TitanPluginSdk::EntityType::NONE;
    if (targetHash < 0) return *this;
    return where([targetHash, targetType](const Player& p) {
        return p.isInteracting() &&
            detail::interactionTargets(p.interactingIndex(), p.interactingType(),
                                       targetHash, targetType);
    });
}

inline PlayerQuery& PlayerQuery::interactingWithLocal() {
    auto* b = detail::backend();
    if (!b) return *this;
    TitanPluginSdk::PlayerState lp = {};
    if (!b->getLocalPlayer(&lp)) return *this;
    return interactingWith(Actor{Player{lp}});
}

}  // namespace titan
