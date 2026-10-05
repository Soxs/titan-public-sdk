/// @file titan/detail/line_of_sight.h
/// @brief Internal RuneLite-style line-of-sight helpers.

#pragma once

#include "abi.h"
#include "backend.h"

#include <algorithm>
#include <cstdint>
#include <cstdlib>
#include <vector>

namespace titan::detail {

inline int32_t resolveWorldViewIdForCompare(int32_t worldViewId) {
    if (worldViewId != -1) return worldViewId;
    auto* b = backend();
    if (!b) return worldViewId;
    TitanPluginSdk::ClientState state{};
    if (!b->getClientState(&state)) return worldViewId;
    return state.currentWorldViewId;
}

inline bool sameWorldViewForLineOfSight(int32_t a, int32_t b) {
    if (a == b) return true;
    return resolveWorldViewIdForCompare(a) == resolveWorldViewIdForCompare(b);
}

struct LineOfSightArea {
    int32_t x = 0;
    int32_t y = 0;
    int32_t width = 1;
    int32_t height = 1;
    int32_t plane = 0;
    int32_t worldViewId = -1;
};

struct LineOfSightPoint {
    int32_t x = 0;
    int32_t y = 0;
    int32_t plane = 0;
};

namespace los_detail {

// Ported from RuneLite WorldArea/CollisionDataFlag
// (Copyright (c) 2018, Woox; BSD-2-Clause).
inline constexpr int32_t BLOCK_LINE_OF_SIGHT_NORTH = 0x00000400;
inline constexpr int32_t BLOCK_LINE_OF_SIGHT_EAST  = 0x00001000;
inline constexpr int32_t BLOCK_LINE_OF_SIGHT_SOUTH = 0x00004000;
inline constexpr int32_t BLOCK_LINE_OF_SIGHT_WEST  = 0x00010000;
inline constexpr int32_t BLOCK_LINE_OF_SIGHT_FULL  = 0x00020000;

inline int32_t positiveSize(int32_t v) {
    return v > 0 ? v : 1;
}

inline LineOfSightArea normalized(LineOfSightArea a) {
    a.width = positiveSize(a.width);
    a.height = positiveSize(a.height);
    return a;
}

inline bool intersects(const LineOfSightArea& a, const LineOfSightArea& b) {
    return sameWorldViewForLineOfSight(a.worldViewId, b.worldViewId)
        && a.plane == b.plane
        && a.x < b.x + b.width && b.x < a.x + a.width
        && a.y < b.y + b.height && b.y < a.y + a.height;
}

inline int32_t chebyshev(const LineOfSightPoint& a, const LineOfSightPoint& b) {
    if (a.plane != b.plane) return 0x7FFFFFFF;
    return (std::max)(std::abs(a.x - b.x), std::abs(a.y - b.y));
}

inline LineOfSightPoint comparisonPoint(const LineOfSightArea& area,
                                        const LineOfSightArea& other) {
    int32_t cx = area.x;
    int32_t cy = area.y;

    if (other.x <= area.x) {
        cx = area.x;
    } else if (other.x >= area.x + area.width - 1) {
        cx = area.x + area.width - 1;
    } else {
        cx = other.x;
    }

    if (other.y <= area.y) {
        cy = area.y;
    } else if (other.y >= area.y + area.height - 1) {
        cy = area.y + area.height - 1;
    } else {
        cy = other.y;
    }

    return {cx, cy, area.plane};
}

inline bool isEdgePoint(const LineOfSightArea& area, const LineOfSightPoint& p) {
    return p.x == area.x || p.x == area.x + area.width - 1
        || p.y == area.y || p.y == area.y + area.height - 1;
}

inline bool isVisibleCandidate(const LineOfSightArea& viewer,
                               const LineOfSightArea& subject,
                               const LineOfSightPoint& p) {
    if (intersects(viewer, subject)) {
        return false;
    }

    const int32_t viewerMaxX = viewer.x + viewer.width - 1;
    const int32_t viewerMaxY = viewer.y + viewer.height - 1;
    const int32_t subjectMaxX = subject.x + subject.width - 1;
    const int32_t subjectMaxY = subject.y + subject.height - 1;

    if (viewerMaxX > subjectMaxX) {
        if (viewer.y < subject.y) {
            return p.x == subjectMaxX || p.y == subject.y;
        }
        if (viewerMaxY > subjectMaxY) {
            return p.x == subjectMaxX || p.y == subjectMaxY;
        }
        return p.x == subjectMaxX;
    }

    if (viewer.x < subject.x) {
        if (viewer.y < subject.y) {
            return p.x == subject.x || p.y == subject.y;
        }
        if (viewerMaxY > subjectMaxY) {
            return p.x == subject.x || p.y == subjectMaxY;
        }
        return p.x == subject.x;
    }

    if (viewer.y > subjectMaxY) {
        return p.y == subjectMaxY;
    }

    if (viewer.y < subject.y) {
        return p.y == subject.y;
    }

    return false;
}

inline std::vector<LineOfSightPoint> visibleCandidates(const LineOfSightArea& viewer,
                                                       const LineOfSightArea& subject) {
    std::vector<LineOfSightPoint> out;
    const LineOfSightPoint comp = comparisonPoint(viewer, subject);
    out.reserve(static_cast<size_t>(subject.width) * static_cast<size_t>(subject.height));

    for (int32_t dx = 0; dx < subject.width; ++dx) {
        for (int32_t dy = 0; dy < subject.height; ++dy) {
            LineOfSightPoint p{subject.x + dx, subject.y + dy, subject.plane};
            if (!isEdgePoint(subject, p)) continue;
            if (!isVisibleCandidate(viewer, subject, p)) continue;
            out.push_back(p);
        }
    }

    std::sort(out.begin(), out.end(), [&](const LineOfSightPoint& a,
                                          const LineOfSightPoint& b) {
        return chebyshev(a, comp) < chebyshev(b, comp);
    });
    return out;
}

inline bool inScene(int32_t x, int32_t y, const TitanPluginSdk::ClientState& state) {
    return x >= 0 && y >= 0 && x < state.sceneSizeX && y < state.sceneSizeY;
}

inline bool areaInScene(const LineOfSightArea& area, const TitanPluginSdk::ClientState& state) {
    const int32_t sx = area.x - state.baseX;
    const int32_t sy = area.y - state.baseY;
    return sx >= 0 && sy >= 0
        && area.width <= state.sceneSizeX - sx
        && area.height <= state.sceneSizeY - sy;
}

inline bool blockedAt(::titan::detail::IBackend* backend,
                      const TitanPluginSdk::ClientState& state,
                      int32_t plane, int32_t x, int32_t y, int32_t mask) {
    if (!inScene(x, y, state)) return true;
    return (backend->getCollisionFlag(plane, x, y) & mask) != 0;
}

inline bool hasLineOfSightTile(::titan::detail::IBackend* backend,
                               const TitanPluginSdk::ClientState& state,
                               int32_t plane,
                               int32_t fromX, int32_t fromY,
                               int32_t toX, int32_t toY) {
    if (!backend || plane < 0 || plane > 3) return false;
    if (!inScene(fromX, fromY, state) || !inScene(toX, toY, state)) return false;
    if (fromX == toX && fromY == toY) return true;

    const int32_t dx = toX - fromX;
    const int32_t dy = toY - fromY;
    const int32_t dxAbs = std::abs(dx);
    const int32_t dyAbs = std::abs(dy);

    int32_t xFlags = BLOCK_LINE_OF_SIGHT_FULL;
    int32_t yFlags = BLOCK_LINE_OF_SIGHT_FULL;

    xFlags |= dx < 0 ? BLOCK_LINE_OF_SIGHT_EAST : BLOCK_LINE_OF_SIGHT_WEST;
    yFlags |= dy < 0 ? BLOCK_LINE_OF_SIGHT_NORTH : BLOCK_LINE_OF_SIGHT_SOUTH;

    if (dxAbs > dyAbs) {
        int32_t x = fromX;
        int32_t yBig = fromY << 16;
        const int32_t slope = (dy << 16) / dxAbs;
        yBig += 0x8000;
        if (dy < 0) --yBig;

        const int32_t direction = dx < 0 ? -1 : 1;
        while (x != toX) {
            x += direction;
            const int32_t y = yBig >> 16;
            if (blockedAt(backend, state, plane, x, y, xFlags)) return false;

            yBig += slope;
            const int32_t nextY = yBig >> 16;
            if (nextY != y && blockedAt(backend, state, plane, x, nextY, yFlags)) {
                return false;
            }
        }
    } else {
        int32_t y = fromY;
        int32_t xBig = fromX << 16;
        const int32_t slope = (dx << 16) / dyAbs;
        xBig += 0x8000;
        if (dx < 0) --xBig;

        const int32_t direction = dy < 0 ? -1 : 1;
        while (y != toY) {
            y += direction;
            const int32_t x = xBig >> 16;
            if (blockedAt(backend, state, plane, x, y, yFlags)) return false;

            xBig += slope;
            const int32_t nextX = xBig >> 16;
            if (nextX != x && blockedAt(backend, state, plane, nextX, y, xFlags)) {
                return false;
            }
        }
    }

    return true;
}

}  // namespace los_detail

inline bool hasLineOfSight(const LineOfSightArea& fromRaw,
                           const LineOfSightArea& toRaw) {
    auto* b = backend();
    if (!b) return false;

    const LineOfSightArea from = los_detail::normalized(fromRaw);
    const LineOfSightArea to = los_detail::normalized(toRaw);
    if (!sameWorldViewForLineOfSight(from.worldViewId, to.worldViewId)) return false;
    if (from.plane != to.plane) return false;

    TitanPluginSdk::ClientState state = {};
    if (!b->getClientState(&state)) return false;
    if (!state.scenePtr || state.sceneSizeX <= 0 || state.sceneSizeY <= 0) return false;
    if (!los_detail::areaInScene(from, state) || !los_detail::areaInScene(to, state)) {
        return false;
    }
    if (los_detail::intersects(from, to)) return true;

    const auto fromCandidates = los_detail::visibleCandidates(to, from);
    const auto toCandidates = los_detail::visibleCandidates(from, to);

    for (const auto& source : fromCandidates) {
        const int32_t sx = source.x - state.baseX;
        const int32_t sy = source.y - state.baseY;
        if (!los_detail::inScene(sx, sy, state)) continue;

        for (const auto& target : toCandidates) {
            const int32_t tx = target.x - state.baseX;
            const int32_t ty = target.y - state.baseY;
            if (!los_detail::inScene(tx, ty, state)) continue;

            if (los_detail::hasLineOfSightTile(b, state, from.plane, sx, sy, tx, ty)) {
                return true;
            }
        }
    }

    return false;
}

}  // namespace titan::detail
