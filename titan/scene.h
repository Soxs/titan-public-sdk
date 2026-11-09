/// @file titan/scene.h
/// @brief Complete owned world-view scene snapshots, reused within a native generation.
#pragma once

#include "detail/backend.h"
#include "world_view.h"

#include <memory>
#include <mutex>
#include <span>
#include <unordered_map>
#include <vector>

namespace titan {

/// An immutable capture, not a live game-memory view. Retaining this object
/// intentionally retains capture-time values. Ask Client::sceneSnapshot again
/// for current data; a stale/unavailable capture is never returned as current.
class SceneSnapshot {
public:
    const TitanPluginSdk::SceneSnapshotInfo& info() const { return info_; }
    const std::vector<TitanPluginSdk::SceneTileState>& tiles() const { return tiles_; }
    const std::vector<TitanPluginSdk::TileObjectState>& objects() const { return objects_; }
    const std::vector<uint32_t>& objectIndices() const { return objectIndices_; }

    /// Null is a genuinely absent native square; a present square may have
    /// zero objects. Coordinates are local to this snapshot's world view.
    const TitanPluginSdk::SceneTileState* tile(int plane, int x, int y) const {
        if (plane < 0 || plane >= 4 || x < 0 || x >= info_.sizeX || y < 0 || y >= info_.sizeY)
            return nullptr;
        const uint32_t index = tileLookup_[(static_cast<size_t>(plane) * info_.sizeX + x) * info_.sizeY + y];
        return index == UINT32_MAX ? nullptr : &tiles_[index];
    }

    /// Indices into objects(), in native slot order. A multi-tile object has
    /// one unique state and is referenced by every square actually containing it.
    std::span<const uint32_t> objectIndices(const TitanPluginSdk::SceneTileState& tile) const {
        if (tile.firstObjectIndex > objectIndices_.size()
            || tile.objectCount > objectIndices_.size() - tile.firstObjectIndex) return {};
        if (!tile.objectCount) return {};
        return {objectIndices_.data() + tile.firstObjectIndex, tile.objectCount};
    }

    static std::shared_ptr<const SceneSnapshot> current(int32_t worldViewId = WorldView::CURRENT) {
        using namespace TitanPluginSdk;
        auto* backend = detail::backend();
        if (!backend) return {};
        struct Cache {
            std::mutex mutex;
            detail::IBackend* backend = nullptr;
            std::unordered_map<int32_t, std::shared_ptr<const SceneSnapshot>> views;
        };
        static Cache cache;
        std::lock_guard<std::mutex> lock(cache.mutex);
        const uint64_t generation = backend->getSceneSnapshotGeneration();
        if (!generation) return {};
        if (cache.backend != backend) {
            cache.views.clear();
            cache.backend = backend;
        }
        const auto found = cache.views.find(worldViewId);
        if (found != cache.views.end() && found->second->info_.generation == generation)
            return found->second;
        cache.views.erase(worldViewId);
        SceneSnapshotInfo info;
        const auto status = backend->getSceneSnapshot(worldViewId, 0, &info,
            nullptr, 0, nullptr, 0, nullptr, 0);
        if ((status != SceneSnapshotStatus::Ready && status != SceneSnapshotStatus::BufferTooSmall)
            || info.generation != generation || !info.scenePtr || !info.worldViewPtr || !info.sceneIncarnation
            || info.sizeX <= 0 || info.sizeX > 256 || info.sizeY <= 0 || info.sizeY > 256
            || info.tileCount > kMaxSceneSnapshotTiles || info.objectCount > kMaxSceneSnapshotObjects
            || info.referenceCount > kMaxSceneSnapshotReferences) return {};
        auto next = std::make_shared<SceneSnapshot>();
        next->tiles_.resize(info.tileCount);
        next->objects_.resize(info.objectCount);
        next->objectIndices_.resize(info.referenceCount);
        if (backend->getSceneSnapshot(worldViewId, generation, &next->info_,
                next->tiles_.data(), info.tileCount, next->objects_.data(), info.objectCount,
                next->objectIndices_.data(), info.referenceCount) != SceneSnapshotStatus::Ready
            || backend->getSceneSnapshotGeneration() != generation) return {};
        if (next->info_.generation != generation || next->info_.tileCount != info.tileCount
            || next->info_.objectCount != info.objectCount || next->info_.referenceCount != info.referenceCount
            || next->info_.sceneIncarnation != info.sceneIncarnation) return {};
        next->tileLookup_.assign(static_cast<size_t>(4) * info.sizeX * info.sizeY, UINT32_MAX);
        for (uint32_t i = 0; i < info.tileCount; ++i) {
            const auto& tile = next->tiles_[i];
            if (tile.plane < 0 || tile.plane >= 4 || tile.x < 0 || tile.x >= info.sizeX
                || tile.y < 0 || tile.y >= info.sizeY || tile.firstObjectIndex > info.referenceCount
                || tile.objectCount > info.referenceCount - tile.firstObjectIndex) return {};
            const size_t flat = (static_cast<size_t>(tile.plane) * info.sizeX + tile.x) * info.sizeY + tile.y;
            if (next->tileLookup_[flat] != UINT32_MAX) return {};
            next->tileLookup_[flat] = i;
        }
        for (const uint32_t index : next->objectIndices_) if (index >= info.objectCount) return {};
        if (backend->getSceneSnapshotGeneration() != generation) return {};
        if (cache.views.size() >= 16) cache.views.erase(cache.views.begin());
        cache.views[worldViewId] = next;
        return next;
    }

private:
    TitanPluginSdk::SceneSnapshotInfo info_{};
    std::vector<TitanPluginSdk::SceneTileState> tiles_;
    std::vector<TitanPluginSdk::TileObjectState> objects_;
    std::vector<uint32_t> objectIndices_;
    std::vector<uint32_t> tileLookup_;
};

} // namespace titan
