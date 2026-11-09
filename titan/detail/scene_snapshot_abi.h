#pragma once

#include <cstdint>

namespace TitanPluginSdk {

// SDK 174. New records; existing native payloads keep their layouts.
enum class SceneSnapshotStatus : uint8_t {
    Unavailable = 0,
    Ready = 1,
    BufferTooSmall = 2,
    Stale = 3,
};

constexpr uint32_t kMaxSceneSnapshotTiles = 4u * 256u * 256u;
constexpr uint32_t kMaxSceneSnapshotObjects = 262144u;
constexpr uint32_t kMaxSceneSnapshotReferences = 1048576u;

struct SceneSnapshotInfo {
    uint64_t generation = 0;
    uint64_t worldViewPtr = 0;
    uint64_t scenePtr = 0;
    int32_t worldViewId = -1;
    int32_t plane = 0;
    int32_t baseX = 0;
    int32_t baseY = 0;
    int32_t sizeX = 0;
    int32_t sizeY = 0;
    int32_t liveStateEpoch = 0;
    uint32_t tileCount = 0;
    uint32_t objectCount = 0;
    uint32_t referenceCount = 0;
    // Changes across observed unload/reload even when native addresses and
    // scene coordinates are reused; stable while the same scene remains live.
    uint64_t sceneIncarnation = 0;
};

// One actual non-null native square, including squares with no objects.
// Its objects are indices into the snapshot's unique TileObjectState array;
// references preserve native slot order and multi-tile membership exactly.
struct SceneTileState {
    int32_t plane = 0;
    int32_t x = 0;
    int32_t y = 0;
    uint32_t firstObjectIndex = 0;
    uint32_t objectCount = 0;
};

static_assert(sizeof(SceneSnapshotInfo) == 72);
static_assert(sizeof(SceneTileState) == 20);

} // namespace TitanPluginSdk
