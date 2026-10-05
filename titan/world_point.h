/// @file titan/world_point.h
/// @brief Lightweight scene-tile and absolute-world coordinate value types.

#pragma once

#include "detail/abi.h"
#include "detail/backend.h"
#include "detail/line_of_sight.h"
#include "world_view.h"

#include <algorithm>
#include <cstdint>
#include <cstdlib>
#include <optional>

namespace titan {

struct WorldArea;

namespace detail {
inline int32_t currentWorldViewIdForCompare() {
    auto* b = backend();
    if (!b) return WorldView::CURRENT;
    TitanPluginSdk::ClientState state{};
    if (!b->getClientState(&state)) return WorldView::CURRENT;
    return state.currentWorldViewId;
}

inline bool sameWorldViewId(int32_t a, int32_t b) {
    if (a == b) return true;
    if (a == WorldView::CURRENT) a = currentWorldViewIdForCompare();
    if (b == WorldView::CURRENT) b = currentWorldViewIdForCompare();
    return a == b;
}
}  // namespace detail

/// Tile coordinate on the loaded scene (local to the WorldView base).
struct Tile {
    int32_t x = 0;
    int32_t y = 0;
    int32_t plane = 0;
    int32_t worldViewId = WorldView::CURRENT;

    /// Chebyshev tile distance -- INT_MAX across planes, matching
    /// `WorldPos::distanceTo` semantics. SDK v37+.
    int32_t distanceTo(const Tile& other) const {
        if (!detail::sameWorldViewId(other.worldViewId, worldViewId)) return 0x7FFFFFFF;
        if (other.plane != plane) return 0x7FFFFFFF;
        return (std::max)(std::abs(x - other.x), std::abs(y - other.y));
    }
    /// Manhattan / Chebyshev 2D distance ignoring plane (useful for
    /// crude range checks where plane mismatch should still give a
    /// numeric answer). SDK v37+.
    int32_t distanceTo2D(const Tile& other) const {
        if (!detail::sameWorldViewId(other.worldViewId, worldViewId)) return 0x7FFFFFFF;
        return (std::max)(std::abs(x - other.x), std::abs(y - other.y));
    }
    /// True when this tile lies within `[0, sceneSizeX) x [0, sceneSizeY)`.
    /// SDK v37+.
    bool isInScene(int32_t sceneSizeX, int32_t sceneSizeY) const {
        return x >= 0 && x < sceneSizeX && y >= 0 && y < sceneSizeY;
    }

    bool operator==(const Tile& o) const {
        return x == o.x && y == o.y && plane == o.plane
            && detail::sameWorldViewId(worldViewId, o.worldViewId);
    }
    bool operator!=(const Tile& o) const { return !(*this == o); }
};

/// Absolute world coordinate (independent of scene base).
/// `x` / `y` are the absolute world tile coords; `z` is the plane.
struct WorldPos {
    int32_t x = 0;
    int32_t y = 0;
    int32_t z = 0;   ///< plane (0=ground, 1..3 upper floors)
    int32_t worldViewId = WorldView::CURRENT;

    /// Chebyshev distance. Returns INT_MAX across planes. SDK v37+.
    int32_t distanceTo(const WorldPos& other) const {
        if (!detail::sameWorldViewId(other.worldViewId, worldViewId)) return 0x7FFFFFFF;
        if (other.z != z) return 0x7FFFFFFF;
        return distanceTo2D(other);
    }
    /// Chebyshev distance ignoring plane differences. SDK v37+.
    int32_t distanceTo2D(const WorldPos& other) const {
        if (!detail::sameWorldViewId(other.worldViewId, worldViewId)) return 0x7FFFFFFF;
        return (std::max)(std::abs(x - other.x), std::abs(y - other.y));
    }

    /// Jagex region id -- `(chunkX << 8) | chunkY` where `chunk = tile >> 6`.
    /// SDK v37+.
    int32_t regionId() const { return ((x >> 6) << 8) | (y >> 6); }
    /// Tile X coordinate within the containing 64x64 region. SDK v37+.
    int32_t regionX()  const { return x & 63; }
    /// Tile Y coordinate within the containing 64x64 region. SDK v37+.
    int32_t regionY()  const { return y & 63; }

    WorldPos dx(int32_t d) const { return {x + d, y,     z,     worldViewId}; }
    WorldPos dy(int32_t d) const { return {x,     y + d, z,     worldViewId}; }
    WorldPos dz(int32_t d) const { return {x,     y,     z + d, worldViewId}; }

    WorldArea toWorldArea() const;
    bool isInMeleeDistance(const WorldPos& other) const;
    bool isInMeleeDistance(const WorldArea& other) const;

    /// True when this world tile lies inside the given loaded-scene window.
    /// Pure overload for callers that already hold a client snapshot (or are
    /// testing), mirroring `Tile::isInScene`. SDK v117+.
    bool isInScene(int32_t sceneBaseX, int32_t sceneBaseY,
                   int32_t sceneSizeX, int32_t sceneSizeY) const {
        const int32_t sceneX = x - sceneBaseX;
        const int32_t sceneY = y - sceneBaseY;
        return sceneX >= 0 && sceneX < sceneSizeX
            && sceneY >= 0 && sceneY < sceneSizeY;
    }

    /// True when this world tile is inside the scene the client currently
    /// has loaded, i.e. when its objects, collision and clickable tiles can
    /// be read at all. A tile outside it can be walked TOWARD but never
    /// interacted with, which is what makes this the standard guard before
    /// clicking a tile, resolving an object, or reading collision.
    ///
    /// Tested against the CURRENT WorldView's scene window. Returns false
    /// when the client state is unavailable (not logged in / no world).
    /// SDK v117+.
    bool isInScene() const {
        auto* b = detail::backend();
        if (!b) return false;
        TitanPluginSdk::ClientState state = {};
        if (!b->getClientState(&state)) return false;
        // Nothing is cached: the scene window is re-read on every call, so a
        // point held across a region change answers against wherever the
        // client is NOW. A point tagged TOP_LEVEL keeps being measured
        // against the top-level scene even while an instanced WorldView is
        // loaded, which is the window its coordinates belong to.
        if (worldViewId == WorldView::TOP_LEVEL
            && state.currentWorldViewId != WorldView::TOP_LEVEL) {
            return isInScene(state.topLevelBaseX, state.topLevelBaseY,
                             state.topLevelSceneSizeX,
                             state.topLevelSceneSizeY);
        }
        return isInScene(state.baseX, state.baseY,
                         state.sceneSizeX, state.sceneSizeY);
    }

    std::optional<WorldPos> fromLocalInstance() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::WorldPointState in{x, y, z, worldViewId};
        TitanPluginSdk::WorldPointState out{};
        if (!b->worldPointFromLocalInstance(&in, &out)) return std::nullopt;
        return WorldPos{out.x, out.y, out.z, out.worldViewId};
    }

    std::optional<WorldPos> toLocalInstance() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::WorldPointState in{x, y, z, worldViewId};
        TitanPluginSdk::WorldPointState out{};
        if (!b->worldPointToLocalInstance(&in, &out)) return std::nullopt;
        return WorldPos{out.x, out.y, out.z, out.worldViewId};
    }

    bool hasLineOfSight(const WorldPos& other) const {
        return detail::hasLineOfSight(
            detail::LineOfSightArea{x, y, 1, 1, z, worldViewId},
            detail::LineOfSightArea{other.x, other.y, 1, 1, other.z, other.worldViewId});
    }

    bool operator==(const WorldPos& o) const {
        return x == o.x && y == o.y && z == o.z
            && detail::sameWorldViewId(worldViewId, o.worldViewId);
    }
    bool operator!=(const WorldPos& o) const { return !(*this == o); }
};

/// Naming alias -- `titan::WorldPoint` is the same struct as `WorldPos`,
/// provided for parity with the RuneLite / client-side naming convention
/// that plugin authors often reach for. Use whichever reads better.
/// SDK v37+.
using WorldPoint = WorldPos;

}  // namespace titan
