/// @file titan/collision.h
/// @brief Collision map reads + RouteFindSize1 step blocking (composition over
///        `IBackend::getCollisionFlag` — no extra HostApi entries).

#pragma once

#include "detail/backend.h"

#include <array>
#include <cstdint>
#include <optional>
#include <vector>

namespace titan {

/// Bit masks on the per-tile int32 collision map. Matches
/// `client/game/scene.h` `CollisionFlag` and the game's BFS pathfinder.
namespace CollisionFlag {
inline constexpr int32_t WALL_SE_CORNER         = 0x00000001;
inline constexpr int32_t WALL_SOUTH             = 0x00000002;
inline constexpr int32_t WALL_SW_CORNER         = 0x00000004;
inline constexpr int32_t WALL_WEST              = 0x00000008;
inline constexpr int32_t WALL_NW_CORNER         = 0x00000010;
inline constexpr int32_t WALL_NORTH             = 0x00000020;
inline constexpr int32_t WALL_NE_CORNER         = 0x00000040;
inline constexpr int32_t WALL_EAST              = 0x00000080;
inline constexpr int32_t BLOCK_OBJECT           = 0x00000100;
inline constexpr int32_t BLOCK_FLOOR_DECORATION = 0x00040000;
inline constexpr int32_t BLOCK_FLOOR            = 0x00200000;
inline constexpr int32_t BLOCK_FULL             = 0x01000000;

inline constexpr int32_t BLOCK_MOVE = BLOCK_FULL | BLOCK_OBJECT
    | BLOCK_FLOOR_DECORATION | BLOCK_FLOOR;

inline constexpr int32_t BLOCKED_WEST  = BLOCK_MOVE | WALL_WEST;
inline constexpr int32_t BLOCKED_EAST  = BLOCK_MOVE | WALL_EAST;
inline constexpr int32_t BLOCKED_SOUTH = BLOCK_MOVE | WALL_SOUTH;
inline constexpr int32_t BLOCKED_NORTH = BLOCK_MOVE | WALL_NORTH;

inline constexpr int32_t BLOCKED_SW = BLOCK_MOVE | WALL_SOUTH | WALL_SW_CORNER | WALL_WEST;
inline constexpr int32_t BLOCKED_SE = BLOCK_MOVE | WALL_EAST | WALL_SOUTH | WALL_SE_CORNER;
inline constexpr int32_t BLOCKED_NW = BLOCK_MOVE | WALL_NORTH | WALL_NW_CORNER | WALL_WEST;
inline constexpr int32_t BLOCKED_NE = BLOCK_MOVE | WALL_EAST | WALL_NE_CORNER | WALL_NORTH;
}  // namespace CollisionFlag

enum class CollisionSnapshotStatus : uint8_t {
    Unavailable = TitanPluginSdk::CollisionSnapshotStatus::Unavailable,
    Ready = TitanPluginSdk::CollisionSnapshotStatus::Ready,
    MissingRegion = TitanPluginSdk::CollisionSnapshotStatus::MissingRegion,
    BufferTooSmall = TitanPluginSdk::CollisionSnapshotStatus::BufferTooSmall,
    InvalidRequest = TitanPluginSdk::CollisionSnapshotStatus::InvalidRequest,
};

struct CachedCollisionRegion {
    CollisionSnapshotStatus status = CollisionSnapshotStatus::Unavailable;
    uint32_t regionId = 0;
    std::vector<int32_t> flags;

    bool ready() const { return status == CollisionSnapshotStatus::Ready; }

    std::optional<int32_t> flag(int32_t plane, int32_t x, int32_t y) const {
        if (!ready() || plane < 0 || plane >= 4 || x < 0 || x >= 64
            || y < 0 || y >= 64) {
            return std::nullopt;
        }
        const size_t index = (static_cast<size_t>(plane) * 64u
            + static_cast<size_t>(x)) * 64u + static_cast<size_t>(y);
        if (index >= flags.size()) return std::nullopt;
        return flags[index];
    }
};

struct LiveCollisionScene {
    CollisionSnapshotStatus status = CollisionSnapshotStatus::Unavailable;
    int32_t baseX = 0;
    int32_t baseY = 0;
    int32_t width = 0;
    int32_t height = 0;
    int32_t worldViewId = -1;
    bool instanced = false;
    std::array<int32_t, TitanPluginSdk::kInstanceTemplateChunkCount>
        templateChunks{};
    std::vector<int32_t> flags;

    bool ready() const { return status == CollisionSnapshotStatus::Ready; }

    std::optional<int32_t> flag(
            int32_t plane, int32_t sceneX, int32_t sceneY) const {
        if (!ready() || plane < 0 || plane >= 4 || sceneX < 0
            || sceneY < 0 || sceneX >= width || sceneY >= height) {
            return std::nullopt;
        }
        const size_t index = (static_cast<size_t>(plane)
            * static_cast<size_t>(width) + static_cast<size_t>(sceneX))
            * static_cast<size_t>(height) + static_cast<size_t>(sceneY);
        if (index >= flags.size()) return std::nullopt;
        return flags[index];
    }
};

class CollisionsFacade {
public:
    /// Nonblocking source readiness, independent of region existence. Any
    /// thread; nullopt on hosts without the optional capability. A ready
    /// source can still have missing/unreadable individual regions.
    std::optional<bool> sourceReady() const {
        auto* b = detail::backend();
        uint8_t ready = 0;
        if (!b || !b->getCollisionSourceReady(&ready) || ready > 1)
            return std::nullopt;
        return ready != 0;
    }

    int32_t flag(int32_t plane, int32_t tileX, int32_t tileY) const {
        auto* b = detail::backend();
        return b ? b->getCollisionFlag(plane, tileX, tileY) : 0;
    }

    /// True when a 1-tile entity at (x, y) cannot step to (x+dx, y+dy).
    /// dx, dy each in {-1, 0, 1}. Mirrors `SceneReader::isBlocked`.
    bool isBlocked(int32_t plane, int32_t x, int32_t y, int32_t dx, int32_t dy) const {
        using namespace CollisionFlag;
        if (dx == 0 && dy == 0) return false;

        int32_t nx = x + dx, ny = y + dy;

        auto getF = [&](int32_t fx, int32_t fy) -> int32_t {
            // A zero collision flag is a valid open tile, not a failed read.
            return flag(plane, fx, fy);
        };

        if (dx != 0 && dy == 0) {
            int32_t dest = getF(nx, ny);
            return (dest & (dx < 0 ? BLOCKED_WEST : BLOCKED_EAST)) != 0;
        }
        if (dx == 0 && dy != 0) {
            int32_t dest = getF(nx, ny);
            return (dest & (dy < 0 ? BLOCKED_SOUTH : BLOCKED_NORTH)) != 0;
        }

        int32_t diag = getF(nx, ny);
        int32_t cardX = getF(nx, y);
        int32_t cardY = getF(x, ny);

        int32_t diagMask = 0, cardXMask = 0, cardYMask = 0;
        if (dx < 0 && dy < 0) {
            diagMask = BLOCKED_SW;
            cardXMask = BLOCKED_WEST;
            cardYMask = BLOCKED_SOUTH;
        } else if (dx > 0 && dy < 0) {
            diagMask = BLOCKED_SE;
            cardXMask = BLOCKED_EAST;
            cardYMask = BLOCKED_SOUTH;
        } else if (dx < 0 && dy > 0) {
            diagMask = BLOCKED_NW;
            cardXMask = BLOCKED_WEST;
            cardYMask = BLOCKED_NORTH;
        } else {
            diagMask = BLOCKED_NE;
            cardXMask = BLOCKED_EAST;
            cardYMask = BLOCKED_NORTH;
        }

        return (diag & diagMask) != 0
            || (cardX & cardXMask) != 0
            || (cardY & cardYMask) != 0;
    }

    /// Copy all four 64x64 planes for a mapsquare. The returned snapshot owns
    /// its storage and is safe to retain or use from a worker thread.
    CachedCollisionRegion cachedRegion(uint32_t regionId) const {
        CachedCollisionRegion result;
        result.regionId = regionId;
        auto* b = detail::backend();
        if (!b) return result;

        uint32_t required = 0;
        auto status = static_cast<CollisionSnapshotStatus>(
            b->copyCachedCollisionRegion(regionId, nullptr, 0, &required));
        if (status != CollisionSnapshotStatus::Ready
            && status != CollisionSnapshotStatus::BufferTooSmall) {
            result.status = status;
            return result;
        }
        if (required != TitanPluginSdk::kCollisionRegionFlagCount) {
            result.status = CollisionSnapshotStatus::InvalidRequest;
            return result;
        }
        result.flags.resize(TitanPluginSdk::kCollisionRegionFlagCount);
        status = static_cast<CollisionSnapshotStatus>(
            b->copyCachedCollisionRegion(
                regionId, result.flags.data(),
                TitanPluginSdk::kCollisionRegionFlagCount, &required));
        result.status = status;
        if (!result.ready()
            || required != TitanPluginSdk::kCollisionRegionFlagCount) {
            result.status = result.ready()
                ? CollisionSnapshotStatus::InvalidRequest
                : result.status;
            result.flags.clear();
        }
        return result;
    }

    /// Copy the entire live scene, including the canonical instance mapping,
    /// into immutable caller-owned storage. Supplying dimensions from a
    /// ClientSnapshot lets the facade allocate the exact flag buffer and call
    /// the fixed ABI only once; zero dimensions retain the general two-pass
    /// form for callers without a matching snapshot.
    LiveCollisionScene currentScene(
            int32_t expectedWidth = 0,
            int32_t expectedHeight = 0) const {
        LiveCollisionScene result;
        auto* b = detail::backend();
        if (!b) return result;

        TitanPluginSdk::CollisionSceneSnapshotState state{};
        const auto validShape = [](const TitanPluginSdk::CollisionSceneSnapshotState& value,
                                   uint32_t count) {
            if (value.structSize < sizeof(TitanPluginSdk::CollisionSceneSnapshotState)
                || value.apiVersion != TitanPluginSdk::kCollisionSnapshotApiVersion
                || value.width <= 0 || value.width > 256
                || value.height <= 0 || value.height > 256) {
                return false;
            }
            const uint64_t expected = 4ull * static_cast<uint64_t>(value.width)
                * static_cast<uint64_t>(value.height);
            return expected == count && value.flagCount == count;
        };
        const bool dimensionsSupplied = expectedWidth != 0 || expectedHeight != 0;
        if (dimensionsSupplied
            && (expectedWidth <= 0 || expectedWidth > 256
                || expectedHeight <= 0 || expectedHeight > 256)) {
            result.status = CollisionSnapshotStatus::InvalidRequest;
            return result;
        }

        uint32_t required = 0;
        CollisionSnapshotStatus status = CollisionSnapshotStatus::Unavailable;
        if (dimensionsSupplied) {
            required = 4u * static_cast<uint32_t>(expectedWidth)
                * static_cast<uint32_t>(expectedHeight);
            result.flags.resize(required);
            status = static_cast<CollisionSnapshotStatus>(
                b->copyCurrentCollisionScene(
                    &state, result.flags.data(), required, &required));
        } else {
            status = static_cast<CollisionSnapshotStatus>(
                b->copyCurrentCollisionScene(&state, nullptr, 0, &required));
            if (status != CollisionSnapshotStatus::Ready
                && status != CollisionSnapshotStatus::BufferTooSmall) {
                result.status = status;
                return result;
            }
            if (!validShape(state, required)) {
                result.status = CollisionSnapshotStatus::InvalidRequest;
                return result;
            }
            result.flags.resize(required);
            status = static_cast<CollisionSnapshotStatus>(
                b->copyCurrentCollisionScene(
                    &state, result.flags.data(), required, &required));
        }
        result.status = status;
        if (!result.ready() || !validShape(state, required)
            || result.flags.size() != required
            || (dimensionsSupplied
                && (state.width != expectedWidth
                    || state.height != expectedHeight))) {
            result.status = result.ready()
                ? CollisionSnapshotStatus::InvalidRequest
                : result.status;
            result.flags.clear();
            return result;
        }
        result.baseX = state.baseX;
        result.baseY = state.baseY;
        result.width = state.width;
        result.height = state.height;
        result.worldViewId = state.worldViewId;
        result.instanced = state.templates.instanced != 0;
        for (size_t i = 0; i < result.templateChunks.size(); ++i) {
            result.templateChunks[i] = state.templates.chunks[i];
        }
        return result;
    }
};

namespace state {
inline ::titan::CollisionsFacade collisions() { return ::titan::CollisionsFacade{}; }
}  // namespace state

}  // namespace titan
