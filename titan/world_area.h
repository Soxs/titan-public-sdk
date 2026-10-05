/// @file titan/world_area.h
/// @brief Axis-aligned rectangular region in absolute world-tile space.
///
/// Canonical SDK location for `WorldArea`. Describes a bounding box on
/// the world map, defined by its south-west corner (`x`, `y`),
/// dimensions (`width`, `height`), and `plane`. Used for containment
/// tests, Chebyshev distance calculations to/from `titan::WorldPos`
/// (== `titan::WorldPoint`) values, and spatial queries.
///
/// Interoperates with `titan::WorldPos` (aliased as `titan::WorldPoint`
/// in `titan/world_point.h`); `WorldArea` field `x` corresponds to
/// `WorldPos::x` (world-tile east), `y` to `WorldPos::y`
/// (world-tile north), and `plane` to `WorldPos::z`.

#pragma once

#include "actor.h"   // titan::WorldPos
#include "detail/line_of_sight.h"

#include <algorithm>
#include <climits>
#include <cstdint>
#include <cstdlib>

namespace titan {

/// An axis-aligned rectangle in absolute world-tile space.
struct WorldArea {
    int32_t x = 0;
    int32_t y = 0;
    int32_t width = 1;
    int32_t height = 1;
    int32_t plane = 0;
    int32_t worldViewId = WorldView::CURRENT;

    /// @return True if @p p is inside the area (plane-aware; `WorldPos::z`
    ///         is the plane).
    bool contains(const WorldPos& p) const {
        return detail::sameWorldViewId(p.worldViewId, worldViewId)
            && p.z == plane
            && p.x >= x && p.x < x + width
            && p.y >= y && p.y < y + height;
    }

    /// @return True if @p p is inside the area ignoring plane.
    bool contains2D(const WorldPos& p) const {
        return detail::sameWorldViewId(p.worldViewId, worldViewId)
            && p.x >= x && p.x < x + width
            && p.y >= y && p.y < y + height;
    }

    /// @return Chebyshev distance from the closest edge of this area to
    ///         @p p; `INT_MAX` when planes differ.
    int32_t distanceTo(const WorldPos& p) const {
        if (!detail::sameWorldViewId(p.worldViewId, worldViewId)) return INT_MAX;
        if (p.z != plane) return INT_MAX;
        int32_t dx = 0, dy = 0;
        if (p.x < x)                  dx = x - p.x;
        else if (p.x >= x + width)    dx = p.x - (x + width - 1);
        if (p.y < y)                  dy = y - p.y;
        else if (p.y >= y + height)   dy = p.y - (y + height - 1);
        return (std::max)(dx, dy);
    }

    /// @return Chebyshev distance between the closest edges of two areas;
    ///         `INT_MAX` when planes differ.
    int32_t distanceTo(const WorldArea& other) const {
        if (!detail::sameWorldViewId(other.worldViewId, worldViewId)) return INT_MAX;
        if (other.plane != plane) return INT_MAX;
        int32_t dx = 0, dy = 0;
        if (other.x + other.width <= x)      dx = x - (other.x + other.width - 1);
        else if (other.x >= x + width)       dx = other.x - (x + width - 1);
        if (other.y + other.height <= y)     dy = y - (other.y + other.height - 1);
        else if (other.y >= y + height)      dy = other.y - (y + height - 1);
        return (std::max)(dx, dy);
    }

    bool isInMeleeDistance(const WorldPos& p) const {
        return isInMeleeDistance(WorldArea{p.x, p.y, 1, 1, p.z, p.worldViewId});
    }

    bool isInMeleeDistance(const WorldArea& other) const {
        if (!detail::sameWorldViewId(other.worldViewId, worldViewId)) return false;
        if (other.plane != plane) return false;
        int32_t dx = 0, dy = 0;
        if (other.x + other.width <= x)      dx = x - (other.x + other.width - 1);
        else if (other.x >= x + width)       dx = other.x - (x + width - 1);
        if (other.y + other.height <= y)     dy = y - (other.y + other.height - 1);
        else if (other.y >= y + height)      dy = other.y - (y + height - 1);
        return dx + dy == 1;
    }

    template <typename T>
    bool isInMeleeDistance(const Locatable<T>& other) const {
        return isInMeleeDistance(static_cast<const T&>(other).worldArea());
    }

    /// @return The centre tile of this area (as a `WorldPos` / `WorldPoint`).
    WorldPos center() const {
        return {x + width / 2, y + height / 2, plane, worldViewId};
    }

    bool hasLineOfSight(const WorldArea& other) const {
        return detail::hasLineOfSight(
            detail::LineOfSightArea{x, y, width, height, plane, worldViewId},
            detail::LineOfSightArea{other.x, other.y, other.width, other.height, other.plane, other.worldViewId});
    }

    bool hasLineOfSight(const WorldPos& p) const {
        return detail::hasLineOfSight(
            detail::LineOfSightArea{x, y, width, height, plane, worldViewId},
            detail::LineOfSightArea{p.x, p.y, 1, 1, p.z, p.worldViewId});
    }

    bool operator==(const WorldArea& o) const {
        return x == o.x && y == o.y
            && width == o.width && height == o.height
            && plane == o.plane && detail::sameWorldViewId(worldViewId, o.worldViewId);
    }
    bool operator!=(const WorldArea& o) const { return !(*this == o); }
};

inline WorldArea WorldPos::toWorldArea() const {
    return {x, y, 1, 1, z, worldViewId};
}

inline bool WorldPos::isInMeleeDistance(const WorldPos& other) const {
    return toWorldArea().isInMeleeDistance(other);
}

inline bool WorldPos::isInMeleeDistance(const WorldArea& other) const {
    return other.isInMeleeDistance(*this);
}

template <typename Derived>
inline WorldArea Locatable<Derived>::worldArea() const {
    return {self_().worldX(), self_().worldY(),
            detail::locatableWidth(self_()), detail::locatableHeight(self_()),
            self_().plane(), worldViewId()};
}

template <typename Derived>
inline bool Locatable<Derived>::isInMeleeDistance(const WorldPoint& other) const {
    return worldArea().isInMeleeDistance(other);
}

template <typename Derived>
inline bool Locatable<Derived>::isInMeleeDistance(const WorldArea& other) const {
    return worldArea().isInMeleeDistance(other);
}

template <typename Derived>
template <typename T>
inline bool Locatable<Derived>::isInMeleeDistance(const Locatable<T>& other) const {
    return worldArea().isInMeleeDistance(static_cast<const T&>(other).worldArea());
}

inline WorldArea Npc::toWorldArea() const {
    return {worldX(), worldY(),
            detail::locatableWidth(*this), detail::locatableHeight(*this),
            plane(), worldViewId()};
}

}  // namespace titan
