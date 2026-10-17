/// @file titan/utils/bfs.h
/// @brief Tile reachability and natural pathfinding over the scene collision map.
///
/// C++ port of the Java SDK's `net.titan.api.utils.BFS`; JS/TS
/// `titan.utils.bfs` binds to this implementation. Public coordinates are
/// world tiles (`WorldPoint` / `WorldArea`); the collision grid is indexed
/// scene-local. Movement is the game's 8-directional model with wall and
/// diagonal-corner rules. The player-anchored reachability map is cached per
/// game tick.
///
/// Fidelity: the collision step, the neighbour expansion order, the BFS FIFO,
/// the parent reconstruction and every priority-queue ordering match the Java
/// SDK exactly, as does each tie-break that walks a map (the first tile in
/// discovery order wins). Only the data structures differ: flat scene-indexed
/// arrays over one collision snapshot per tick instead of hash maps over
/// per-tile reads.
///
/// Points are compared by tile and plane only; their world view id is
/// ignored, so tiles read from actors in any view key the same map entry.
/// Results use `WorldView::CURRENT`.
///
/// `Locatable::isReachable()` is defined at the bottom of this header, so
/// include it wherever an actor, object or ground item calls it.
///
/// Threading: call from game-thread plugin callbacks, like other scene reads.
/// The per-tick caches are mutex-guarded and hand out shared immutable maps;
/// one `WeightedSearch` instance is not thread-safe.
///
/// Added in SDK 153.

#pragma once

#include "../client.h"
#include "../collision.h"
#include "../query.h"
#include "../world_area.h"
#include "../world_point.h"

#include <algorithm>
#include <climits>
#include <cstdint>
#include <cstdlib>
#include <functional>
#include <memory>
#include <mutex>
#include <optional>
#include <queue>
#include <type_traits>
#include <unordered_map>
#include <unordered_set>
#include <utility>
#include <vector>

namespace titan {
namespace utils {
namespace BFS {

class ReachabilityMap;

namespace detail {

// --- scene collision flags (standard values) -------------------------------
// Fully-blocked composite: a tile no actor may stand on.
inline constexpr int32_t kBlockFull = 0x1000000;
inline constexpr int32_t kBlockFloor = 0x200000;
inline constexpr int32_t kBlockObject = 0x100;
inline constexpr int32_t kBlockFloorDecoration = 0x40000;
inline constexpr int32_t kFullyBlocked =
    kBlockFull | kBlockFloor | kBlockObject | kBlockFloorDecoration;

// Per-edge movement blocks, in RuneLite's CollisionDataFlag naming. The raw
// bits are titan::CollisionFlag's WALL_* values named from the other side.
inline constexpr int32_t kNW = 0x1, kN = 0x2, kNE = 0x4, kE = 0x8;
inline constexpr int32_t kSE = 0x10, kS = 0x20, kSW = 0x40, kW = 0x80;

// Line-of-sight blocks (separate bits in the same flag word).
inline constexpr int32_t kLosFull = 0x20000, kLosN = 0x400, kLosE = 0x1000,
                         kLosS = 0x4000, kLosW = 0x10000;

// NPCs that occupy their tiles as hard collision (routed around like objects).
inline constexpr int32_t kSolidBlockerNpcs[] = {14152, 14153};

// The outermost ring of the loaded scene has unreliable collision (its flags
// depend on neighbouring chunks that may not be loaded), so the flood stays
// one tile inside the scene edge.
inline constexpr int32_t kEdgeInset = 1;

inline constexpr int32_t kCardinals[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
inline constexpr int32_t kStepDirs[8][2] = {
    {0, -1}, {0, 1}, {-1, 0}, {1, 0}, {-1, -1}, {1, -1}, {-1, 1}, {1, 1}};

inline WorldPoint point(int32_t x, int32_t y, int32_t z) {
    return WorldPoint{x, y, z, WorldView::CURRENT};
}

inline WorldPoint normalized(const WorldPoint& p) { return point(p.x, p.y, p.z); }

inline bool samePoint(const WorldPoint& a, const WorldPoint& b) {
    return a.x == b.x && a.y == b.y && a.z == b.z;
}

inline uint64_t tileKey(int32_t x, int32_t y, int32_t z) {
    return (static_cast<uint64_t>(static_cast<uint16_t>(z)) << 48)
        | (static_cast<uint64_t>(static_cast<uint32_t>(x) & 0xFFFFFFu) << 24)
        | (static_cast<uint64_t>(static_cast<uint32_t>(y) & 0xFFFFFFu));
}

inline uint64_t tileKey(const WorldPoint& p) { return tileKey(p.x, p.y, p.z); }

inline std::vector<WorldPoint> areaTiles(const WorldArea& area) {
    std::vector<WorldPoint> tiles;
    if (area.width > 0 && area.height > 0) {
        tiles.reserve(static_cast<size_t>(area.width) * static_cast<size_t>(area.height));
    }
    for (int32_t x = 0; x < area.width; ++x) {
        for (int32_t y = 0; y < area.height; ++y) {
            tiles.push_back(point(area.x + x, area.y + y, area.plane));
        }
    }
    return tiles;
}

inline int32_t clamp(int32_t v, int32_t lo, int32_t hi) {
    return v < lo ? lo : (std::min)(v, hi);
}

// Area tests by tile and plane only. WorldArea's own contains/distanceTo also
// compare world views, which C++ and Java resolve differently; the BFS keys
// every tile by coordinates, so it uses these in both SDKs.
inline bool areaContains(const WorldArea& area, const WorldPoint& p) {
    return p.z == area.plane && p.x >= area.x && p.x < area.x + area.width
        && p.y >= area.y && p.y < area.y + area.height;
}

inline int32_t areaDistanceTo(const WorldArea& area, const WorldPoint& p) {
    if (p.z != area.plane) return INT_MAX;
    int32_t dx = 0, dy = 0;
    if (p.x < area.x) dx = area.x - p.x;
    else if (p.x >= area.x + area.width) dx = p.x - (area.x + area.width - 1);
    if (p.y < area.y) dy = area.y - p.y;
    else if (p.y >= area.y + area.height) dy = p.y - (area.y + area.height - 1);
    return (std::max)(dx, dy);
}

inline bool noneRemovable(const WorldPoint&) { return false; }

/// One consistent view of the scene for a game tick: geometry, collision
/// flags (a snapshot, or live per-tile reads when no snapshot is available)
/// and the tiles held by solid-blocker NPCs.
struct Scene {
    bool valid = false;
    int32_t tick = 0;
    int32_t baseX = 0;
    int32_t baseY = 0;
    int32_t sizeX = 0;
    int32_t sizeY = 0;
    int32_t plane = 0;
    bool snapshot = false;
    std::vector<int32_t> flags;          ///< `[plane][sceneX][sceneY]`
    std::unordered_set<uint64_t> solid;  ///< solid-blocker NPC tiles

    bool sameKey(const ClientSnapshot& c) const {
        return valid && tick == c.tickCount && baseX == c.baseX && baseY == c.baseY
            && plane == c.plane && sizeX == c.sceneSizeX && sizeY == c.sceneSizeY;
    }

    int32_t flag(int32_t p, int32_t lx, int32_t ly) const {
        if (snapshot) {
            if (p < 0 || p > 3 || lx < 0 || ly < 0 || lx >= sizeX || ly >= sizeY) return 0;
            const size_t index = (static_cast<size_t>(p) * static_cast<size_t>(sizeX)
                + static_cast<size_t>(lx)) * static_cast<size_t>(sizeY) + static_cast<size_t>(ly);
            return index < flags.size() ? flags[index] : 0;
        }
        auto* b = ::titan::detail::backend();
        return b ? b->getCollisionFlag(p, lx, ly) : 0;
    }

    bool isSolid(int32_t x, int32_t y, int32_t z) const {
        return !solid.empty() && solid.count(tileKey(x, y, z)) != 0;
    }

    bool blocked(const WorldPoint& wp, int32_t f) const {
        return (f & kFullyBlocked) != 0 || isSolid(wp.x, wp.y, wp.z);
    }

    bool fullyBlocked(int32_t p, int32_t worldX, int32_t worldY) const {
        const int32_t lx = worldX - baseX, ly = worldY - baseY;
        if (lx < 0 || lx >= sizeX || ly < 0 || ly >= sizeY) return true;
        return (flag(p, lx, ly) & kFullyBlocked) != 0 || isSolid(worldX, worldY, p);
    }

    int32_t sightFlag(int32_t p, int32_t worldX, int32_t worldY) const {
        const int32_t lx = worldX - baseX, ly = worldY - baseY;
        if (lx < 0 || lx >= sizeX || ly < 0 || ly >= sizeY) return kLosFull;
        return flag(p, lx, ly);
    }

    /// Both intermediate corner tiles of a diagonal step must be clear.
    bool corners(int32_t p, int32_t ax, int32_t ay, int32_t bx, int32_t by) const {
        return !fullyBlocked(p, ax, ay) && !fullyBlocked(p, bx, by);
    }

    /// The collision step (fidelity-critical; do not reorder or relax).
    template <typename Permeable>
    bool walkable(const WorldPoint& current, const WorldPoint& target,
                  bool includeCurrent, const Permeable& permeable) const {
        if (current.z != target.z) return false;
        const int32_t p = target.z;
        const int32_t clx = current.x - baseX, cly = current.y - baseY;
        const int32_t tlx = target.x - baseX, tly = target.y - baseY;
        const int32_t lo = kEdgeInset, hix = sizeX - kEdgeInset, hiy = sizeY - kEdgeInset;
        if (clx < lo || clx >= hix || cly < lo || cly >= hiy
            || tlx < lo || tlx >= hix || tly < lo || tly >= hiy) {
            return false;
        }
        const int32_t dx = target.x - current.x, dy = target.y - current.y;
        const int32_t cur = flag(p, clx, cly);
        const int32_t tgt = flag(p, tlx, tly);
        if (includeCurrent) {
            if ((!permeable(current) && blocked(current, cur))
                || (!permeable(target) && blocked(target, tgt))) {
                return false;
            }
        } else if (!permeable(target) && blocked(target, tgt)) {
            return false;
        }
        if (dx == -1) {
            if ((cur & kW) != 0 || (tgt & kE) != 0) return false;
        } else if (dx == 1) {
            if ((cur & kE) != 0 || (tgt & kW) != 0) return false;
        }
        if (dy == -1) {
            if ((cur & kS) != 0 || (tgt & kN) != 0) return false;
        } else if (dy == 1) {
            if ((cur & kN) != 0 || (tgt & kS) != 0) return false;
        }
        if (dx == -1 && dy == 1) {  // NW
            if ((cur & kNW) != 0 || (cur & kW) != 0 || (cur & kN) != 0
                || (tgt & kSE) != 0 || (tgt & kE) != 0 || (tgt & kS) != 0) return false;
            return corners(p, target.x, current.y, current.x, target.y);
        } else if (dx == 1 && dy == 1) {  // NE
            if ((cur & kNE) != 0 || (cur & kE) != 0 || (cur & kN) != 0
                || (tgt & kSW) != 0 || (tgt & kW) != 0 || (tgt & kS) != 0) return false;
            return corners(p, target.x, current.y, current.x, target.y);
        } else if (dx == -1 && dy == -1) {  // SW
            if ((cur & kSW) != 0 || (cur & kW) != 0 || (cur & kS) != 0
                || (tgt & kNE) != 0 || (tgt & kE) != 0 || (tgt & kN) != 0) return false;
            return corners(p, target.x, current.y, current.x, target.y);
        } else if (dx == 1 && dy == -1) {  // SE
            if ((cur & kSE) != 0 || (cur & kE) != 0 || (cur & kS) != 0
                || (tgt & kNW) != 0 || (tgt & kW) != 0 || (tgt & kN) != 0) return false;
            return corners(p, target.x, current.y, current.x, target.y);
        }
        return true;
    }

    bool walkable(const WorldPoint& current, const WorldPoint& target, bool includeCurrent) const {
        return walkable(current, target, includeCurrent, noneRemovable);
    }

    /// Calls @p fn with each walkable neighbour of @p p, in the
    /// fidelity-critical order S, N, W, E, SW, SE, NW, NE.
    template <typename Fn>
    void forEachNeighbor(const WorldPoint& p, Fn&& fn) const {
        for (const auto& d : kStepDirs) {
            const WorldPoint nb = point(p.x + d[0], p.y + d[1], p.z);
            if (walkable(p, nb, true)) fn(nb);
        }
    }

    std::vector<WorldPoint> neighbors(const WorldPoint& p) const {
        std::vector<WorldPoint> out;
        out.reserve(8);
        forEachNeighbor(p, [&](const WorldPoint& nb) { out.push_back(nb); });
        return out;
    }

    /// Line of sight between two tiles (the BFS variant: stepped tiles
    /// outside the scene block sight).
    bool hasLineOfSight(const WorldPoint& from, const WorldPoint& to) const {
        if (from.z != to.z) return false;
        const int32_t p = from.z;
        const int32_t x1 = from.x, y1 = from.y, x2 = to.x, y2 = to.y;
        if (x1 == x2 && y1 == y2) return true;
        const int32_t dx = x2 - x1, dy = y2 - y1;
        const int32_t dxa = std::abs(dx), dya = std::abs(dy);
        const int32_t xFlags = kLosFull | (dx < 0 ? kLosE : kLosW);
        const int32_t yFlags = kLosFull | (dy < 0 ? kLosN : kLosS);
        // Unsigned arithmetic reproduces Java's wrapping int and `>>>`.
        if (dxa > dya) {
            int32_t x = x1;
            uint32_t yBig = (static_cast<uint32_t>(y1) << 16) + 0x8000u
                + static_cast<uint32_t>(dy < 0 ? -1 : 0);
            const uint32_t slope = static_cast<uint32_t>((dy * 65536) / dxa);
            const int32_t dir = dx < 0 ? -1 : 1;
            while (x != x2) {
                x += dir;
                const int32_t y = static_cast<int32_t>(yBig >> 16);
                if ((sightFlag(p, x, y) & xFlags) != 0) return false;
                yBig += slope;
                const int32_t ny = static_cast<int32_t>(yBig >> 16);
                if (ny != y && (sightFlag(p, x, ny) & yFlags) != 0) return false;
            }
        } else {
            int32_t y = y1;
            uint32_t xBig = (static_cast<uint32_t>(x1) << 16) + 0x8000u
                + static_cast<uint32_t>(dx < 0 ? -1 : 0);
            const uint32_t slope = static_cast<uint32_t>((dx * 65536) / dya);
            const int32_t dir = dy < 0 ? -1 : 1;
            while (y != y2) {
                y += dir;
                const int32_t x = static_cast<int32_t>(xBig >> 16);
                if ((sightFlag(p, x, y) & yFlags) != 0) return false;
                xBig += slope;
                const int32_t nx = static_cast<int32_t>(xBig >> 16);
                if (nx != x && (sightFlag(p, nx, y) & xFlags) != 0) return false;
            }
        }
        return true;
    }

    bool hasLineOfSight(const WorldPoint& from, const WorldArea& area) const {
        const WorldPoint nearest = point(
            clamp(from.x, area.x, area.x + area.width - 1),
            clamp(from.y, area.y, area.y + area.height - 1),
            area.plane);
        return hasLineOfSight(from, nearest);
    }
};

struct Caches {
    std::mutex mutex;
    std::shared_ptr<const Scene> scene;
    std::shared_ptr<const ReachabilityMap> map;
    // Held, not just compared: a freed scene's address can be reused by a
    // later tick's scene, which would make a raw-pointer match stale.
    std::shared_ptr<const Scene> mapScene;
};

inline Caches& caches() {
    static Caches instance;
    return instance;
}

inline std::shared_ptr<const Scene> buildScene(const ClientSnapshot& client) {
    auto scene = std::make_shared<Scene>();
    scene->valid = true;
    scene->tick = client.tickCount;
    scene->baseX = client.baseX;
    scene->baseY = client.baseY;
    scene->sizeX = client.sceneSizeX;
    scene->sizeY = client.sceneSizeY;
    scene->plane = client.plane;
    if (client.sceneSizeX > 0 && client.sceneSizeY > 0
        && client.sceneSizeX <= 256 && client.sceneSizeY <= 256) {
        auto live = ::titan::state::collisions().currentScene(client.sceneSizeX, client.sceneSizeY);
        if (live.ready() && live.baseX == client.baseX && live.baseY == client.baseY
            && live.width == client.sceneSizeX && live.height == client.sceneSizeY) {
            scene->flags = std::move(live.flags);
            scene->snapshot = true;
        }
    }
    // Keep the query alive: items() returns a reference into it.
    auto npcs = ::titan::queries::npcs();
    for (const auto& npc : npcs.items()) {
        const int32_t id = npc.id();
        bool solid = false;
        for (int32_t blocker : kSolidBlockerNpcs) solid = solid || blocker == id;
        if (!solid) continue;
        for (const auto& tile : areaTiles(npc.worldArea())) scene->solid.insert(tileKey(tile));
    }
    return scene;
}

/// The scene for the current tick, rebuilt when the tick or the loaded scene
/// changes. An invalid scene (no client state) blocks every step.
inline std::shared_ptr<const Scene> currentScene() {
    const auto client = ::titan::state::client().snapshot();
    if (!client) return std::make_shared<const Scene>();
    auto& c = caches();
    {
        std::lock_guard<std::mutex> lock(c.mutex);
        if (c.scene && c.scene->sameKey(*client)) return c.scene;
    }
    auto scene = buildScene(*client);
    std::lock_guard<std::mutex> lock(c.mutex);
    c.scene = scene;
    return scene;
}

inline std::optional<WorldPoint> localPlayerPoint() {
    const auto player = ::titan::state::client().localPlayer();
    if (!player) return std::nullopt;
    return normalized(player->worldPoint());
}

}  // namespace detail

// ---------------------------------------------------------------------------
// Reachability map type
// ---------------------------------------------------------------------------

/// BFS flood from one source tile: every tile reachable on the source's
/// plane, its path length, and the parent that rebuilds the natural path.
class ReachabilityMap {
public:
    ReachabilityMap() = default;

    bool reachable(const WorldPoint& wp) const { return distanceOf(wp) >= 0; }

    std::optional<int32_t> pathLength(const WorldPoint& wp) const {
        const int32_t d = distanceOf(wp);
        if (d < 0) return std::nullopt;
        return d;
    }

    /// Path from the source to @p wp, excluding the start tile; empty if
    /// unreachable (or @p wp is the source).
    std::vector<WorldPoint> pathTo(const WorldPoint& wp) const {
        std::vector<WorldPoint> path;
        if (!reachable(wp) || detail::samePoint(wp, *source_)) return path;
        for (int32_t i = indexOf(wp); i >= 0 && i != sourceIndex_; i = parent_[static_cast<size_t>(i)]) {
            path.push_back(pointOf(i));
        }
        std::reverse(path.begin(), path.end());
        return path;
    }

    /// Every reachable tile, the source first, in BFS discovery order.
    std::vector<WorldPoint> getWorldPoints() const {
        std::vector<WorldPoint> out;
        forEachTile([&](const WorldPoint& wp, int32_t) { out.push_back(wp); });
        return out;
    }

    std::optional<WorldPoint> source() const { return source_; }

    /// The tile @p wp is entered from on its natural path; nullopt for the
    /// source and for unreachable tiles.
    std::optional<WorldPoint> parentOf(const WorldPoint& wp) const {
        if (!reachable(wp) || detail::samePoint(wp, *source_)) return std::nullopt;
        const int32_t parent = parent_[static_cast<size_t>(indexOf(wp))];
        if (parent < 0) return std::nullopt;
        return pointOf(parent);
    }

    /// Calls @p fn(tile, pathLength) for every reachable tile in BFS
    /// discovery order (the source first).
    template <typename Fn>
    void forEachTile(Fn&& fn) const {
        if (!source_) return;
        if (sourceIndex_ < 0) {
            fn(*source_, 0);
            return;
        }
        for (int32_t index : order_) fn(pointOf(index), dist_[static_cast<size_t>(index)]);
    }

    /// Flood from @p source over @p scene (the Java `buildMap`).
    static ReachabilityMap build(const detail::Scene& scene, const WorldPoint& source) {
        ReachabilityMap m;
        m.source_ = detail::normalized(source);
        m.baseX_ = scene.baseX;
        m.baseY_ = scene.baseY;
        m.width_ = scene.sizeX;
        m.height_ = scene.sizeY;
        m.plane_ = m.source_->z;
        if (m.width_ <= 0 || m.height_ <= 0) return m;
        const size_t cells = static_cast<size_t>(m.width_) * static_cast<size_t>(m.height_);
        m.dist_.assign(cells, -1);
        m.parent_.assign(cells, -1);
        const int32_t si = m.indexOf(*m.source_);
        if (si < 0) {
            m.dist_.clear();
            m.parent_.clear();
            return m;
        }
        m.sourceIndex_ = si;
        m.dist_[static_cast<size_t>(si)] = 0;
        m.order_.push_back(si);
        // FIFO: order_ is the queue, popped in insertion order.
        for (size_t head = 0; head < m.order_.size(); ++head) {
            const int32_t ci = m.order_[head];
            const int32_t nd = m.dist_[static_cast<size_t>(ci)] + 1;
            scene.forEachNeighbor(m.pointOf(ci), [&](const WorldPoint& nb) {
                const int32_t ni = m.indexOf(nb);
                if (ni < 0 || m.dist_[static_cast<size_t>(ni)] >= 0) return;
                m.dist_[static_cast<size_t>(ni)] = nd;
                m.parent_[static_cast<size_t>(ni)] = ci;
                m.order_.push_back(ni);
            });
        }
        return m;
    }

private:
    int32_t indexOf(const WorldPoint& wp) const {
        if (dist_.empty() || wp.z != plane_) return -1;
        const int32_t lx = wp.x - baseX_, ly = wp.y - baseY_;
        if (lx < 0 || ly < 0 || lx >= width_ || ly >= height_) return -1;
        return lx * height_ + ly;
    }

    WorldPoint pointOf(int32_t index) const {
        return detail::point(baseX_ + index / height_, baseY_ + index % height_, plane_);
    }

    int32_t distanceOf(const WorldPoint& wp) const {
        if (!source_) return -1;
        if (detail::samePoint(wp, *source_)) return 0;
        const int32_t index = indexOf(wp);
        return index < 0 ? -1 : dist_[static_cast<size_t>(index)];
    }

    std::optional<WorldPoint> source_;
    int32_t baseX_ = 0;
    int32_t baseY_ = 0;
    int32_t width_ = 0;
    int32_t height_ = 0;
    int32_t plane_ = 0;
    int32_t sourceIndex_ = -1;
    std::vector<int32_t> dist_;
    std::vector<int32_t> parent_;
    std::vector<int32_t> order_;
};

// ---------------------------------------------------------------------------
// Reachability map (player-anchored, cached per tick)
// ---------------------------------------------------------------------------

/// The reachability map from the local player's tile, cached until the tick,
/// the loaded scene or the player's tile changes. Empty with no player.
inline std::shared_ptr<const ReachabilityMap> getReachabilityMap() {
    const auto scene = detail::currentScene();
    const auto source = detail::localPlayerPoint();
    auto& c = detail::caches();
    {
        std::lock_guard<std::mutex> lock(c.mutex);
        if (c.map && c.mapScene == scene) {
            const auto cachedSource = c.map->source();
            const bool same = (!cachedSource && !source)
                || (cachedSource && source && detail::samePoint(*cachedSource, *source));
            if (same) return c.map;
        }
    }
    auto map = std::make_shared<const ReachabilityMap>(
        source ? ReachabilityMap::build(*scene, *source) : ReachabilityMap{});
    std::lock_guard<std::mutex> lock(c.mutex);
    c.map = map;
    c.mapScene = scene;
    return map;
}

namespace detail {

inline std::shared_ptr<const ReachabilityMap> mapFrom(const WorldPoint& start) {
    const auto player = localPlayerPoint();
    if (player && samePoint(start, *player)) return getReachabilityMap();
    const auto scene = currentScene();
    return std::make_shared<const ReachabilityMap>(ReachabilityMap::build(*scene, start));
}

/// end reachable -> path; else best reachable cardinal neighbour; else the
/// closest reachable tile by Manhattan distance (ties: lower path length,
/// then discovery order).
inline std::vector<WorldPoint> resolve(const ReachabilityMap& map,
                                       const WorldPoint& end, bool partial) {
    if (map.reachable(end)) return map.pathTo(end);
    if (!partial) return {};
    std::optional<WorldPoint> best;
    int32_t bestLen = INT_MAX;
    for (const auto& d : kCardinals) {
        const WorldPoint adj = point(end.x + d[0], end.y + d[1], end.z);
        const auto len = map.pathLength(adj);
        if (len && *len < bestLen) {
            bestLen = *len;
            best = adj;
        }
    }
    if (best) return map.pathTo(*best);
    int32_t bestDist = INT_MAX, bestCost = INT_MAX;
    map.forEachTile([&](const WorldPoint& wp, int32_t cost) {
        const int32_t md = std::abs(wp.x - end.x) + std::abs(wp.y - end.y);
        if (md < bestDist || (md == bestDist && cost < bestCost)) {
            bestDist = md;
            bestCost = cost;
            best = wp;
        }
    });
    return best ? map.pathTo(*best) : std::vector<WorldPoint>{};
}

template <typename IsGoal, typename Removable, typename Step>
std::optional<std::vector<WorldPoint>> minBlockerRoute(
        const WorldPoint& source, const IsGoal& isGoal,
        const Removable& removable, const Step& step) {
    if (isGoal(source)) return std::vector<WorldPoint>{};
    struct Best {
        int32_t blockers = 0;
        int32_t steps = 0;
        bool hasParent = false;
        WorldPoint parent{};
    };
    struct Node {
        WorldPoint wp;
        int32_t blockers;
        int32_t steps;
        int32_t ins;
    };
    // std::priority_queue pops the greatest; "greater" means lower priority.
    const auto lowerPriority = [](const Node& a, const Node& b) {
        if (a.blockers != b.blockers) return a.blockers > b.blockers;
        if (a.steps != b.steps) return a.steps > b.steps;
        return a.ins > b.ins;
    };
    std::unordered_map<uint64_t, Best> best;
    std::priority_queue<Node, std::vector<Node>, decltype(lowerPriority)> queue(lowerPriority);
    int32_t ins = 0;
    best[tileKey(source)] = Best{};
    queue.push(Node{source, 0, 0, ins++});
    while (!queue.empty()) {
        const Node cur = queue.top();
        queue.pop();
        const Best& curBest = best.at(tileKey(cur.wp));
        if (cur.blockers != curBest.blockers || cur.steps != curBest.steps) continue;
        if (isGoal(cur.wp)) {
            std::vector<WorldPoint> blockers;
            WorldPoint p = cur.wp;
            while (!samePoint(p, source)) {
                if (removable(p)) blockers.push_back(p);
                const Best& link = best.at(tileKey(p));
                if (!link.hasParent) break;
                p = link.parent;
            }
            std::reverse(blockers.begin(), blockers.end());
            return blockers;
        }
        for (const auto& d : kStepDirs) {
            const WorldPoint nb = point(cur.wp.x + d[0], cur.wp.y + d[1], cur.wp.z);
            if (!step(cur.wp, nb)) continue;
            const int32_t nbBlockers = cur.blockers + (removable(nb) ? 1 : 0);
            const int32_t nbSteps = cur.steps + 1;
            const auto it = best.find(tileKey(nb));
            if (it == best.end() || nbBlockers < it->second.blockers
                || (nbBlockers == it->second.blockers && nbSteps < it->second.steps)) {
                best[tileKey(nb)] = Best{nbBlockers, nbSteps, true, cur.wp};
                queue.push(Node{nb, nbBlockers, nbSteps, ins++});
            }
        }
    }
    return std::nullopt;
}

}  // namespace detail

// ---------------------------------------------------------------------------
// Paths and distances
// ---------------------------------------------------------------------------

/// Natural path from the local player to @p end (start tile excluded). With
/// @p partial, an unreachable end yields the path to its best reachable
/// cardinal neighbour, else to the closest reachable tile.
inline std::vector<WorldPoint> findPath(const WorldPoint& end, bool partial = true) {
    return detail::resolve(*getReachabilityMap(), end, partial);
}

inline std::vector<WorldPoint> findPath(const WorldPoint& start, const WorldPoint& end,
                                        bool partial = true) {
    return detail::resolve(*detail::mapFrom(start), end, partial);
}

inline std::optional<int32_t> pathLength(const WorldPoint& end) {
    return getReachabilityMap()->pathLength(end);
}

inline std::optional<int32_t> pathLength(const WorldPoint& start, const WorldPoint& end) {
    return detail::mapFrom(start)->pathLength(end);
}

// ---------------------------------------------------------------------------
// Tile tests
// ---------------------------------------------------------------------------

inline bool reachable(const WorldPoint& wp) {
    return getReachabilityMap()->reachable(wp);
}

/// True when @p wp is inside the scene and not fully blocked.
inline bool walkable(const WorldPoint& wp) {
    return !detail::currentScene()->fullyBlocked(wp.z, wp.x, wp.y);
}

inline bool canStep(const WorldPoint& from, const WorldPoint& to) {
    return detail::currentScene()->walkable(from, to, true);
}

inline bool canStep(const WorldPoint& from, int32_t dx, int32_t dy) {
    return canStep(from, detail::point(from.x + dx, from.y + dy, from.z));
}

inline std::vector<WorldPoint> reachableTilesAtCost(int32_t n) {
    std::vector<WorldPoint> out;
    getReachabilityMap()->forEachTile([&](const WorldPoint& wp, int32_t cost) {
        if (cost == n) out.push_back(wp);
    });
    return out;
}

inline std::vector<WorldPoint> reachableTilesWithinCost(int32_t n) {
    std::vector<WorldPoint> out;
    getReachabilityMap()->forEachTile([&](const WorldPoint& wp, int32_t cost) {
        if (cost <= n) out.push_back(wp);
    });
    return out;
}

// ---------------------------------------------------------------------------
// Area tests (engine for the entity sugar)
// ---------------------------------------------------------------------------

/// Reachable cardinal-adjacent tiles of the area's footprint (where you stand
/// to interact or melee).
inline std::vector<WorldPoint> reachableAdjacentTiles(const WorldArea& area) {
    const auto map = getReachabilityMap();
    const auto scene = detail::currentScene();
    std::unordered_set<uint64_t> seen;
    std::vector<WorldPoint> out;
    for (const auto& t : detail::areaTiles(area)) {
        for (const auto& d : detail::kCardinals) {
            const WorldPoint adj = detail::point(t.x + d[0], t.y + d[1], t.z);
            if (detail::areaContains(area, adj) || !seen.insert(detail::tileKey(adj)).second) continue;
            if (scene->walkable(t, adj, false) && map->reachable(adj)) out.push_back(adj);
        }
    }
    return out;
}

/// Can the player reach a tile cardinally adjacent to the area (interact or melee).
inline bool meleeable(const WorldArea& area) {
    return !reachableAdjacentTiles(area).empty();
}

/// Can the player reach a tile within Chebyshev @p range of the area,
/// optionally with line of sight to it.
inline bool reachableWithinRange(const WorldArea& area, int32_t range, bool requireLoS) {
    const auto map = getReachabilityMap();
    const auto scene = detail::currentScene();
    const int32_t plane = area.plane;
    for (int32_t x = area.x - range; x < area.x + area.width + range; ++x) {
        for (int32_t y = area.y - range; y < area.y + area.height + range; ++y) {
            const WorldPoint t = detail::point(x, y, plane);
            if (detail::areaDistanceTo(area, t) > range || !map->reachable(t)) continue;
            if (!requireLoS || scene->hasLineOfSight(t, area)) return true;
        }
    }
    return false;
}

namespace detail {

inline std::optional<WorldPoint> nearestAdjacent(const WorldArea& area) {
    const auto m = getReachabilityMap();
    std::optional<WorldPoint> best;
    int32_t bestLen = INT_MAX;
    for (const auto& t : reachableAdjacentTiles(area)) {
        const int32_t len = m->pathLength(t).value_or(INT_MAX);
        if (len < bestLen) {
            bestLen = len;
            best = t;
        }
    }
    return best;
}

}  // namespace detail

/// Natural path to the nearest reachable tile adjacent to the area (the
/// interact tile).
inline std::vector<WorldPoint> findPath(const WorldArea& area) {
    const auto t = detail::nearestAdjacent(area);
    return t ? findPath(*t) : std::vector<WorldPoint>{};
}

inline std::optional<int32_t> pathLength(const WorldArea& area) {
    const auto t = detail::nearestAdjacent(area);
    return t ? pathLength(*t) : std::nullopt;
}

/// True when the player can stand on a tile of the area or on a tile
/// cardinally adjacent to it (see `reachableAdjacentTiles`). This is the
/// engine behind `Locatable::isReachable()`.
inline bool isReachable(const WorldArea& area) {
    const auto map = getReachabilityMap();
    for (const auto& t : detail::areaTiles(area)) {
        if (map->reachable(t)) return true;
    }
    return meleeable(area);
}

inline bool isReachable(const WorldPoint& wp) {
    return isReachable(WorldArea{wp.x, wp.y, 1, 1, wp.z, WorldView::CURRENT});
}

// ---------------------------------------------------------------------------
// Line of sight
// ---------------------------------------------------------------------------

/// Line of sight from the local player to @p to.
inline bool hasLineOfSight(const WorldPoint& to) {
    const auto from = detail::localPlayerPoint();
    return from && detail::currentScene()->hasLineOfSight(*from, to);
}

inline bool hasLineOfSight(const WorldPoint& from, const WorldPoint& to) {
    return detail::currentScene()->hasLineOfSight(from, to);
}

// ---------------------------------------------------------------------------
// Minimum-blocker route (removable obstacle tiles to clear toward a goal)
// ---------------------------------------------------------------------------

using TilePredicate = std::function<bool(const WorldPoint&)>;

/// Player-anchored: removable obstacle tiles to clear to reach @p target, in
/// walk order. Empty if already reachable or no removable tile is on the
/// route; nullopt if unreachable even clearing every removable tile (or no
/// player / no predicate).
inline std::optional<std::vector<WorldPoint>> blockingTiles(
        const WorldPoint& target, const TilePredicate& removable) {
    if (!removable) return std::nullopt;
    const auto source = detail::localPlayerPoint();
    if (!source) return std::nullopt;
    if (getReachabilityMap()->reachable(target)) return std::vector<WorldPoint>{};
    const auto scene = detail::currentScene();
    const WorldPoint goal = detail::normalized(target);
    return detail::minBlockerRoute(
        *source,
        [&](const WorldPoint& p) { return detail::samePoint(p, goal); },
        removable,
        [&](const WorldPoint& from, const WorldPoint& to) {
            return scene->walkable(from, to, true, removable);
        });
}

/// Set convenience: an empty set means no tile is removable.
inline std::optional<std::vector<WorldPoint>> blockingTiles(
        const WorldPoint& target, const std::vector<WorldPoint>& removable) {
    std::unordered_set<uint64_t> keys;
    for (const auto& p : removable) keys.insert(detail::tileKey(p));
    return blockingTiles(target, TilePredicate([keys](const WorldPoint& p) {
        return keys.count(detail::tileKey(p)) != 0;
    }));
}

/// Area-target form: the removable tiles to clear, in walk order, to reach
/// ANY tile of @p target. Empty if any footprint tile is already reachable;
/// nullopt if none is reachable even after clearing every removable tile.
inline std::optional<std::vector<WorldPoint>> blockingTiles(
        const WorldArea& target, const TilePredicate& removable) {
    if (!removable) return std::nullopt;
    const auto source = detail::localPlayerPoint();
    if (!source) return std::nullopt;
    const auto map = getReachabilityMap();
    for (const auto& t : detail::areaTiles(target)) {
        if (map->reachable(t)) return std::vector<WorldPoint>{};
    }
    const auto scene = detail::currentScene();
    return detail::minBlockerRoute(
        *source,
        [&](const WorldPoint& p) { return detail::areaContains(target, p); },
        removable,
        [&](const WorldPoint& from, const WorldPoint& to) {
            return scene->walkable(from, to, true, removable);
        });
}

inline std::optional<std::vector<WorldPoint>> blockingTiles(
        const WorldArea& target, const std::vector<WorldPoint>& removable) {
    std::unordered_set<uint64_t> keys;
    for (const auto& p : removable) keys.insert(detail::tileKey(p));
    return blockingTiles(target, TilePredicate([keys](const WorldPoint& p) {
        return keys.count(detail::tileKey(p)) != 0;
    }));
}

// ---------------------------------------------------------------------------
// Weighted / constrained search
// ---------------------------------------------------------------------------

/// Extra cost (>= 0) for entering a tile at a given step from the source.
using WeightFn = std::function<int32_t(const WorldPoint& tile, int32_t stepsFromStart)>;
/// Whether a tile is impassable to enter at a given step from the source.
using BlockFn = std::function<bool(const WorldPoint& tile, int32_t stepsFromStart)>;

/// The tiles you stand on if you run @p path (start excluded) as one
/// continuous click: every other tile, plus the destination. Valid for a
/// single-click run (e.g. a natural path). For a weighted run route use
/// `WeightedSearch::findPath(end, partial, true)` instead.
inline std::vector<WorldPoint> runTiles(const std::vector<WorldPoint>& path) {
    std::vector<WorldPoint> landed;
    if (path.empty()) return landed;
    landed.reserve(path.size() / 2 + 1);
    for (size_t i = 1; i < path.size(); i += 2) landed.push_back(path[i]);
    const size_t last = path.size() - 1;
    if ((last & 1u) == 0) landed.push_back(path[last]);
    return landed;
}

/// A configured constrained search: a per-step weight added to the base cost
/// of 1, and a per-step block predicate. Blocks act as walls (the path stops
/// before them) when the player starts on a safe tile; when the player starts
/// on a blocked tile the search escapes through the least-blocked route.
class WeightedSearch {
public:
    /// Weight per entered tile: `int(tile)` or `int(tile, stepsFromStart)`.
    template <typename F>
    WeightedSearch& weight(F f) {
        if constexpr (std::is_invocable_v<F&, const WorldPoint&, int32_t>) {
            weight_ = WeightFn(std::move(f));
        } else {
            weight_ = [g = std::move(f)](const WorldPoint& t, int32_t) {
                return static_cast<int32_t>(g(t));
            };
        }
        map_.reset();
        return *this;
    }

    /// Block per entered tile: `bool(tile)` or `bool(tile, stepsFromStart)`.
    template <typename F>
    WeightedSearch& block(F f) {
        if constexpr (std::is_invocable_v<F&, const WorldPoint&, int32_t>) {
            block_ = BlockFn(std::move(f));
        } else {
            block_ = [g = std::move(f)](const WorldPoint& t, int32_t) {
                return static_cast<bool>(g(t));
            };
        }
        map_.reset();
        return *this;
    }

    /// Run-aware costing: weight is charged only on the tiles you actually
    /// stand on (every other tile, plus the destination), so the search may
    /// route a 2-tile run through a weighted tile it merely passes over.
    /// Blockers still block every tile, and a skipped tile still costs 1.
    WeightedSearch& run(bool run) {
        if (run_ != run) {
            run_ = run;
            map_.reset();
        }
        return *this;
    }

    std::vector<WorldPoint> findPath(const WorldPoint& end, bool partial = true) {
        return map().resolve(end, partial);
    }

    /// One-shot run-aware path; returns the tiles you land on.
    std::vector<WorldPoint> findPath(const WorldPoint& end, bool partial, bool run) {
        return this->run(run).map().resolve(end, partial);
    }

    bool reachable(const WorldPoint& wp) { return map().nodeOf(wp) >= 0; }

    std::optional<int32_t> pathLength(const WorldPoint& wp) {
        const Map& m = map();
        const int32_t n = m.nodeOf(wp);
        if (n < 0) return std::nullopt;
        return m.nodes[static_cast<size_t>(n)].steps;
    }

    /// Farthest tile of the weighted route to @p end whose natural path
    /// matches the route (full geometry, run-skipped tiles included).
    std::optional<WorldPoint> clickTile(const WorldPoint& end) {
        return clickTile(map().pathToFull(end));
    }

    /// Farthest tile on @p intended whose natural path matches the intended prefix.
    std::optional<WorldPoint> clickTile(const std::vector<WorldPoint>& intended) const {
        if (intended.empty()) return std::nullopt;
        const auto natural = getReachabilityMap();
        std::optional<WorldPoint> best;
        for (size_t i = 0; i < intended.size(); ++i) {
            const auto nat = natural->pathTo(intended[i]);
            bool matches = nat.size() == i + 1;
            for (size_t k = 0; matches && k <= i; ++k) {
                matches = detail::samePoint(nat[k], intended[k]);
            }
            if (!matches) break;
            best = detail::normalized(intended[i]);
        }
        return best;
    }

    /// Closest tile of the natural map (by path length) that the block
    /// predicate allows at its path length.
    std::optional<WorldPoint> nearestSafeTile() const {
        const auto natural = getReachabilityMap();
        std::optional<WorldPoint> best;
        int32_t bestLen = INT_MAX;
        natural->forEachTile([&](const WorldPoint& wp, int32_t len) {
            if (len < bestLen && !block_(wp, len)) {
                bestLen = len;
                best = wp;
            }
        });
        return best;
    }

    /// Path to the goal with the lowest weighted cost (so a cheaper, safer
    /// longer route wins over a short costly one); empty if none is reachable.
    std::vector<WorldPoint> safePathTo(const std::vector<WorldPoint>& goals) {
        const Map& m = map();
        std::optional<WorldPoint> best;
        int32_t bestCost = INT_MAX;
        for (const auto& g : goals) {
            const int32_t c = m.cost(g);
            if (c < bestCost) {
                bestCost = c;
                best = g;
            }
        }
        return best ? m.resolve(*best, false) : std::vector<WorldPoint>{};
    }

private:
    struct Node {
        WorldPoint wp;
        bool hasSkipped = false;  ///< run mode: 2-tile move over `skipped`
        WorldPoint skipped{};
        int32_t blockedEntered = 0;
        int32_t cost = 0;
        int32_t steps = 0;
        int32_t tick = 0;
        int32_t ins = 0;
        int32_t parent = -1;      ///< node index, -1 for the start node
        // Run mode only: reached by a 1-tile move. A continuous run covers 2
        // tiles per tick and only drops to a single tile as its final move,
        // so a 1-tile move terminates the run and is never expanded. That
        // keeps landed tiles on the 2-tile cadence (2, 4, 6, ... plus the
        // trailing odd tile) instead of a spurious 1, 3, 5... chain that a
        // free 1-tile hop from the source would otherwise win on cost ties.
        bool terminal = false;
    };

    struct Map {
        std::optional<WorldPoint> source;
        int32_t baseX = 0, baseY = 0, width = 0, height = 0, plane = 0;
        std::vector<Node> nodes;      ///< arena; index 0 is the start node
        std::vector<int32_t> nodeAt;  ///< scene cell -> current node index
        std::vector<int32_t> order;   ///< cells in first-insertion order
        bool sourceInGrid = false;

        int32_t cellOf(const WorldPoint& wp) const {
            if (nodeAt.empty() || wp.z != plane) return -1;
            const int32_t lx = wp.x - baseX, ly = wp.y - baseY;
            if (lx < 0 || ly < 0 || lx >= width || ly >= height) return -1;
            return lx * height + ly;
        }

        int32_t nodeOf(const WorldPoint& wp) const {
            if (!source) return -1;
            if (!sourceInGrid) return detail::samePoint(wp, *source) ? 0 : -1;
            const int32_t cell = cellOf(wp);
            return cell < 0 ? -1 : nodeAt[static_cast<size_t>(cell)];
        }

        int32_t cost(const WorldPoint& wp) const {
            const int32_t n = nodeOf(wp);
            return n < 0 ? INT_MAX : nodes[static_cast<size_t>(n)].cost;
        }

        void put(const WorldPoint& wp, int32_t nodeIndex) {
            const int32_t cell = cellOf(wp);
            if (cell < 0) return;
            if (nodeAt[static_cast<size_t>(cell)] < 0) order.push_back(cell);
            nodeAt[static_cast<size_t>(cell)] = nodeIndex;
        }

        std::vector<WorldPoint> pathTo(const WorldPoint& wp) const {
            std::vector<WorldPoint> path;
            for (int32_t i = nodeOf(wp); i >= 0 && nodes[static_cast<size_t>(i)].parent >= 0;
                 i = nodes[static_cast<size_t>(i)].parent) {
                path.push_back(nodes[static_cast<size_t>(i)].wp);
            }
            std::reverse(path.begin(), path.end());
            return path;
        }

        /// Full tile geometry including run-skipped tiles (walk: identical to pathTo).
        std::vector<WorldPoint> pathToFull(const WorldPoint& wp) const {
            std::vector<WorldPoint> path;
            for (int32_t i = nodeOf(wp); i >= 0 && nodes[static_cast<size_t>(i)].parent >= 0;
                 i = nodes[static_cast<size_t>(i)].parent) {
                const Node& n = nodes[static_cast<size_t>(i)];
                path.push_back(n.wp);
                if (n.hasSkipped) path.push_back(n.skipped);
            }
            std::reverse(path.begin(), path.end());
            return path;
        }

        template <typename Fn>
        void forEachNode(Fn&& fn) const {
            if (!source) return;
            if (!sourceInGrid) {
                fn(nodes.front());
                return;
            }
            for (int32_t cell : order) fn(nodes[static_cast<size_t>(nodeAt[static_cast<size_t>(cell)])]);
        }

        std::vector<WorldPoint> resolve(const WorldPoint& end, bool partial) const {
            if (nodeOf(end) >= 0) return pathTo(end);
            if (!partial) return {};
            std::optional<WorldPoint> best;
            int32_t bestLen = INT_MAX;
            for (const auto& d : detail::kCardinals) {
                const int32_t n = nodeOf(detail::point(end.x + d[0], end.y + d[1], end.z));
                if (n >= 0 && nodes[static_cast<size_t>(n)].cost < bestLen) {
                    bestLen = nodes[static_cast<size_t>(n)].cost;
                    best = nodes[static_cast<size_t>(n)].wp;
                }
            }
            if (best) return pathTo(*best);
            int32_t bestDist = INT_MAX, bestCost = INT_MAX;
            forEachNode([&](const Node& n) {
                const int32_t md = std::abs(n.wp.x - end.x) + std::abs(n.wp.y - end.y);
                if (md < bestDist || (md == bestDist && n.cost < bestCost)) {
                    bestDist = md;
                    bestCost = n.cost;
                    best = n.wp;
                }
            });
            return best ? pathTo(*best) : std::vector<WorldPoint>{};
        }
    };

    struct NodeOrder {
        const std::vector<Node>* nodes;
        // std::priority_queue pops the greatest; "greater" means lower priority.
        bool operator()(int32_t a, int32_t b) const {
            const Node& x = (*nodes)[static_cast<size_t>(a)];
            const Node& y = (*nodes)[static_cast<size_t>(b)];
            if (x.blockedEntered != y.blockedEntered) return x.blockedEntered > y.blockedEntered;
            if (x.cost != y.cost) return x.cost > y.cost;
            return x.ins > y.ins;
        }
    };
    using Queue = std::priority_queue<int32_t, std::vector<int32_t>, NodeOrder>;

    const Map& map() {
        const auto scene = detail::currentScene();
        if (!map_ || builtTick_ != scene->tick || builtValid_ != scene->valid
            || builtBaseX_ != scene->baseX || builtBaseY_ != scene->baseY
            || builtPlane_ != scene->plane || builtSizeX_ != scene->sizeX
            || builtSizeY_ != scene->sizeY || builtRun_ != run_) {
            map_ = std::make_unique<Map>(build(*scene));
            builtTick_ = scene->tick;
            builtValid_ = scene->valid;
            builtBaseX_ = scene->baseX;
            builtBaseY_ = scene->baseY;
            builtPlane_ = scene->plane;
            builtSizeX_ = scene->sizeX;
            builtSizeY_ = scene->sizeY;
            builtRun_ = run_;
        }
        return *map_;
    }

    Map start(const detail::Scene& scene, const WorldPoint& src) const {
        Map wm;
        wm.source = src;
        wm.baseX = scene.baseX;
        wm.baseY = scene.baseY;
        wm.width = scene.sizeX;
        wm.height = scene.sizeY;
        wm.plane = src.z;
        Node first;
        first.wp = src;
        wm.nodes.push_back(first);
        if (wm.width > 0 && wm.height > 0) {
            wm.nodeAt.assign(static_cast<size_t>(wm.width) * static_cast<size_t>(wm.height), -1);
            const int32_t cell = wm.cellOf(src);
            if (cell >= 0) {
                wm.sourceInGrid = true;
                wm.put(src, 0);
            } else {
                wm.nodeAt.clear();
            }
        }
        return wm;
    }

    bool isCurrent(const Map& wm, int32_t nodeIndex) const {
        return wm.nodeOf(wm.nodes[static_cast<size_t>(nodeIndex)].wp) == nodeIndex;
    }

    Map build(const detail::Scene& scene) const {
        const auto src = detail::localPlayerPoint();
        if (!src) return Map{};
        return run_ ? buildRun(scene, *src) : buildWalk(scene, *src);
    }

    Map buildWalk(const detail::Scene& scene, const WorldPoint& src) const {
        Map wm = start(scene, src);
        const bool escape = block_(src, 0);
        Queue pq(NodeOrder{&wm.nodes});
        int32_t ins = 1;
        pq.push(0);
        while (!pq.empty()) {
            const int32_t curIndex = pq.top();
            pq.pop();
            if (!isCurrent(wm, curIndex)) continue;
            const Node cur = wm.nodes[static_cast<size_t>(curIndex)];
            scene.forEachNeighbor(cur.wp, [&](const WorldPoint& nb) {
                const int32_t steps = cur.steps + 1;
                const bool blk = block_(nb, steps);
                if (blk && !escape) return;
                const int32_t newBlocked = cur.blockedEntered + (blk ? 1 : 0);
                const int32_t newCost = cur.cost + 1 + (std::max)(0, weight_(nb, steps));
                const int32_t ex = wm.nodeOf(nb);
                if (ex < 0 || newBlocked < wm.nodes[static_cast<size_t>(ex)].blockedEntered
                    || (newBlocked == wm.nodes[static_cast<size_t>(ex)].blockedEntered
                        && newCost < wm.nodes[static_cast<size_t>(ex)].cost)) {
                    Node n;
                    n.wp = nb;
                    n.blockedEntered = newBlocked;
                    n.cost = newCost;
                    n.steps = steps;
                    n.tick = steps;
                    n.parent = curIndex;
                    n.ins = ins++;
                    wm.nodes.push_back(n);
                    const int32_t index = static_cast<int32_t>(wm.nodes.size() - 1);
                    wm.put(nb, index);
                    pq.push(index);
                }
            });
        }
        return wm;
    }

    /// Run-aware Dijkstra over landed tiles. Each move is a 1-tile step
    /// (cost 1) or a 2-tile run A->B->C where B is skipped (cost 1, no
    /// weight) and C is landed (cost 1 + weight). The weight and block
    /// functions receive the tick (move count), since B and C share a tick.
    Map buildRun(const detail::Scene& scene, const WorldPoint& src) const {
        Map wm = start(scene, src);
        const bool escape = block_(src, 0);
        Queue pq(NodeOrder{&wm.nodes});
        int32_t ins = 1;
        pq.push(0);
        while (!pq.empty()) {
            const int32_t curIndex = pq.top();
            pq.pop();
            if (!isCurrent(wm, curIndex)) continue;
            const Node cur = wm.nodes[static_cast<size_t>(curIndex)];
            const int32_t tick = cur.tick + 1;
            if (cur.terminal) continue;
            // 1-tile move (sidestep / trailing-odd / click-adjacent): land on c.
            scene.forEachNeighbor(cur.wp, [&](const WorldPoint& c) {
                relaxRun(wm, pq, curIndex, cur, nullptr, c, 1, tick, escape, ins);
            });
            // 2-tile run: pass over b (no weight), land on c.
            for (const auto& b : scene.neighbors(cur.wp)) {
                scene.forEachNeighbor(b, [&](const WorldPoint& c) {
                    if (detail::samePoint(c, cur.wp)) return;  // running back is a no-op
                    relaxRun(wm, pq, curIndex, cur, &b, c, 2, tick, escape, ins);
                });
            }
        }
        return wm;
    }

    void relaxRun(Map& wm, Queue& pq, int32_t curIndex, const Node& cur,
                  const WorldPoint* b, const WorldPoint& c, int32_t tiles,
                  int32_t tick, bool escape, int32_t& ins) const {
        int32_t entered = 0;
        if (b) {
            const bool blkB = block_(*b, tick);
            if (blkB && !escape) return;  // can't run over a blocker
            entered += blkB ? 1 : 0;
        }
        const bool blkC = block_(c, tick);
        if (blkC && !escape) return;
        entered += blkC ? 1 : 0;
        const int32_t newBlocked = cur.blockedEntered + entered;
        const int32_t newSteps = cur.steps + tiles;
        const int32_t newCost = cur.cost + tiles + (std::max)(0, weight_(c, tick));  // weight on landed c only
        const int32_t ex = wm.nodeOf(c);
        if (ex < 0 || newBlocked < wm.nodes[static_cast<size_t>(ex)].blockedEntered
            || (newBlocked == wm.nodes[static_cast<size_t>(ex)].blockedEntered
                && newCost < wm.nodes[static_cast<size_t>(ex)].cost)) {
            Node n;
            n.wp = c;
            n.hasSkipped = b != nullptr;
            if (b) n.skipped = *b;
            n.blockedEntered = newBlocked;
            n.cost = newCost;
            n.steps = newSteps;
            n.tick = tick;
            n.parent = curIndex;
            n.ins = ins++;
            n.terminal = tiles == 1;
            wm.nodes.push_back(n);
            const int32_t index = static_cast<int32_t>(wm.nodes.size() - 1);
            wm.put(c, index);
            pq.push(index);
        }
    }

    WeightFn weight_ = [](const WorldPoint&, int32_t) { return 0; };
    BlockFn block_ = [](const WorldPoint&, int32_t) { return false; };
    std::unique_ptr<Map> map_;
    int32_t builtTick_ = -1;
    bool builtValid_ = false;
    int32_t builtBaseX_ = 0;
    int32_t builtBaseY_ = 0;
    int32_t builtPlane_ = 0;
    int32_t builtSizeX_ = 0;
    int32_t builtSizeY_ = 0;
    bool run_ = false;
    bool builtRun_ = false;
};

inline WeightedSearch weighted() { return WeightedSearch{}; }

}  // namespace BFS
}  // namespace utils

// ---------------------------------------------------------------------------
// Locatable::isReachable (declared in actor.h)
// ---------------------------------------------------------------------------

template <typename Derived>
inline bool Locatable<Derived>::isReachable() const {
    return utils::BFS::isReachable(worldArea());
}

}  // namespace titan
