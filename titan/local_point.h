/// @file titan/local_point.h
/// @brief Scene-local coordinate in sub-tile precision.
///
/// Canonical SDK location for `LocalPoint`. Represents a position within
/// the current scene using the game's internal coordinate system where
/// each tile spans 128 units. Converting between tile indices and
/// sub-tile coordinates is done via bit-shift by 7.
/// The center of a tile at scene index (sx, sy) is at
/// `((sx << 7) + 64, (sy << 7) + 64)`.
///
/// Distinct from `titan::Tile` (which carries a plane field and uses
/// tile-granularity Chebyshev distance); `LocalPoint` is plane-less and
/// sub-tile-precise with Euclidean distance semantics. Useful for
/// precise-coord math where plane is tracked separately (e.g. projectile
/// trajectory interpolation, clickbox centres).
///
/// Client-internal code can include `client/game/types/local_point.h`,
/// which forwards this header into the project's top-level `LocalPoint`
/// name.

#pragma once

#include "detail/abi.h"
#include "detail/backend.h"
#include "world_view.h"

#include <cmath>
#include <cstdint>

namespace titan {

namespace detail {
inline int32_t resolveLocalPointWorldViewId(int32_t worldViewId) {
    if (worldViewId != WorldView::CURRENT) return worldViewId;
    auto* b = backend();
    if (!b) return worldViewId;
    TitanPluginSdk::ClientState state{};
    if (!b->getClientState(&state)) return worldViewId;
    return state.currentWorldViewId;
}

inline bool sameLocalPointWorldViewId(int32_t a, int32_t b) {
    if (a == b) return true;
    return resolveLocalPointWorldViewId(a) == resolveLocalPointWorldViewId(b);
}
}  // namespace detail

/// A position in scene-local space at sub-tile (1/128th tile) granularity.
struct LocalPoint {
    int32_t x = 0;
    int32_t y = 0;
    int32_t worldViewId = WorldView::CURRENT;

    /// @return Scene tile X index (x >> 7).
    int32_t sceneX() const { return x >> 7; }
    /// @return Scene tile Y index (y >> 7).
    int32_t sceneY() const { return y >> 7; }

    LocalPoint dx(int32_t d) const { return {x + d, y, worldViewId}; }
    LocalPoint dy(int32_t d) const { return {x, y + d, worldViewId}; }
    LocalPoint plus(int32_t ddx, int32_t ddy) const { return {x + ddx, y + ddy, worldViewId}; }

    /// @return Euclidean distance in sub-tile units. Matches RuneLite's
    ///         `LocalPoint.distanceTo` and the client's historical
    ///         `LocalPoint::distanceTo`.
    int32_t distanceTo(const LocalPoint& other) const {
        if (!detail::sameLocalPointWorldViewId(worldViewId, other.worldViewId)) return 0x7FFFFFFF;
        return static_cast<int32_t>(
            std::hypot(static_cast<double>(x - other.x),
                       static_cast<double>(y - other.y)));
    }

    /// @return True if this point falls within the scene bounds.
    bool isInScene(int32_t sceneSizeX, int32_t sceneSizeY) const {
        return x >= 0 && x < (sceneSizeX << 7)
            && y >= 0 && y < (sceneSizeY << 7);
    }

    /// Build a LocalPoint centered on the given scene tile.
    static LocalPoint fromScene(int32_t sceneX, int32_t sceneY,
                                int32_t worldViewId = WorldView::CURRENT) {
        return {(sceneX << 7) + 64, (sceneY << 7) + 64, worldViewId};
    }

    bool operator==(const LocalPoint& o) const {
        return x == o.x && y == o.y
            && detail::sameLocalPointWorldViewId(worldViewId, o.worldViewId);
    }
    bool operator!=(const LocalPoint& o) const { return !(*this == o); }
};

}  // namespace titan
