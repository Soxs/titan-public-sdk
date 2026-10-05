#pragma once

#include <cstddef>
#include <cstdint>

#ifndef TITAN_PROFILE_PERFORMANCE
#define TITAN_PROFILE_PERFORMANCE 0
#endif

// Erase optional instrumentation and its arguments even in Debug /Od builds.
// Declarations retain their enclosing scope when instrumentation is enabled.
#if TITAN_PROFILE_PERFORMANCE
#define TITAN_PERF_ONLY(...) __VA_ARGS__
#else
#define TITAN_PERF_ONLY(...) ((void)0);
#endif

// Optional service protocol. This header has no runtime or SDK dependency.
namespace titan::perf {

inline constexpr bool probesCompiled = TITAN_PROFILE_PERFORMANCE != 0;

enum class Metric : uint32_t {
    Frame, NativeGameLoop, ClientTick, GameTick, Lifecycle, LifecycleObjects,
    ItemContainerDiff, SepulchreCallback, SepulchreTick, SceneCapture,
    CollisionCapture, ObjectCapture, ObjectMaterialize, NpcCapture,
    GraphicsCapture, HazardUpdate, MovingUpdate, LightningUpdate, DebugData,
    RouteSelection, ActionExecution, PathFind, BonusDetours, TimedPath,
    DetourSelection, WeightedSearch, BfsPlayer, BfsOther, Projection, WalkAction,
    InventoryMaintenance, Trace, PathOverlay, DebugOverlay, HostTileObjects,
    LightObjectsScan, ObjectResolve, LocDefinition, TemplateCapture,
    TemplateConvert, HostCollision, HostNpcs, HostNpcRefresh, HostPlayers,
    HostGraphics, HostLocalPlayer,
    LightCacheHit, LightCacheMiss, ObjectsScanned, ObjectsExported,
    ObjectsRetained, ObjectResolveHit, ObjectResolveMiss, DefinitionHit,
    DefinitionMiss, TemplateCells, CollisionBytes, ObjectBytes, ObjectBufferRetry,
    NpcsExported, NpcRefreshes, GraphicsExported, BfsBuilds, BfsNodes,
    WeightedBuilds, WeightedNodes, WeightedEdges, WeightedAccepted,
    ProjectionPrefixes, SafetyHits, SafetyMisses, Floor, RouteIndex, PlayerX,
    PlayerY, IsRunning, DiagnosticsEnabled, TraceEnabled,
    // Append only: existing plugin DLLs send these numeric IDs via ServiceV1.
    ResolveCacheRead, ResolveLiveSlot, ResolveClickbox, ResolvePack,
    ResolveAnimation, ResolveStrings, ResolveCacheWrite,
    // Timings cover sampled operations only; counter counts all operations.
    WeightedNeighborSample, WeightedPrimarySample, WeightedSecondarySample,
    WeightedWeightSample, WeightedOfferSample, WeightedPopSample,
    // Completed distance outcomes and whole BFS work; flushed once per findPath.
    DistanceOtherExact, DistanceOtherFallback, DistanceOtherUnavailable,
    DistanceOtherBfsBuilds, DistanceOtherBfsNodes,
    DistanceDirectTargetExact, DistanceDirectTargetFallback, DistanceDirectTargetUnavailable,
    DistanceDirectTargetBfsBuilds, DistanceDirectTargetBfsNodes,
    DistanceBonusScoringExact, DistanceBonusScoringFallback, DistanceBonusScoringUnavailable,
    DistanceBonusScoringBfsBuilds, DistanceBonusScoringBfsNodes,
    DistanceImmunityFallbackExact, DistanceImmunityFallbackFallback, DistanceImmunityFallbackUnavailable,
    DistanceImmunityFallbackBfsBuilds, DistanceImmunityFallbackBfsNodes,
    DistanceNearestFallbackExact, DistanceNearestFallbackFallback, DistanceNearestFallbackUnavailable,
    DistanceNearestFallbackBfsBuilds, DistanceNearestFallbackBfsNodes,
    DistanceSegmentPathsExact, DistanceSegmentPathsFallback, DistanceSegmentPathsUnavailable,
    DistanceSegmentPathsBfsBuilds, DistanceSegmentPathsBfsNodes,
    DistanceCacheHit, DistanceCacheMiss, DistanceCacheEviction,
    DistanceCacheEvictedNodes, DistanceNearestScanned, DistanceProbeCalls,
    DistanceTargetX, DistanceTargetY, DistanceTargetPlane,
    DistancePlayerPlane, DistanceTargetExact, DistanceTargetFallback,
    DistanceTargetUnavailable, DistanceImmunityActive, DistanceObstacleOnPlayer,
    DistanceCrossesMoving, DistanceImmunityFallbackEntered, DistanceNearestFallbackEntered,
    Count
};

inline constexpr size_t metricCount = static_cast<size_t>(Metric::Count);
inline constexpr uint32_t kApiVersion = 1;
inline constexpr char kServiceId[] = "titan.performance-probe.v1";
inline constexpr uint32_t kWeightedSamplePeriod = 64;

constexpr const char* metricName(Metric metric) noexcept {
    constexpr const char* names[] = {
        "Frame", "NativeGameLoop", "ClientTick", "GameTick", "Lifecycle",
        "LifecycleObjects", "ItemContainerDiff", "SepulchreCallback",
        "SepulchreTick", "SceneCapture", "CollisionCapture", "ObjectCapture",
        "ObjectMaterialize", "NpcCapture", "GraphicsCapture", "HazardUpdate",
        "MovingUpdate", "LightningUpdate", "DebugData", "RouteSelection",
        "ActionExecution", "PathFind", "BonusDetours", "TimedPath",
        "DetourSelection", "WeightedSearch", "BfsPlayer", "BfsOther",
        "Projection", "WalkAction", "InventoryMaintenance", "Trace",
        "PathOverlay", "DebugOverlay", "HostTileObjects", "LightObjectsScan",
        "ObjectResolve", "LocDefinition", "TemplateCapture", "TemplateConvert",
        "HostCollision", "HostNpcs", "HostNpcRefresh", "HostPlayers",
        "HostGraphics", "HostLocalPlayer", "LightCacheHit", "LightCacheMiss",
        "ObjectsScanned", "ObjectsExported", "ObjectsRetained",
        "ObjectResolveHit", "ObjectResolveMiss", "DefinitionHit",
        "DefinitionMiss", "TemplateCells", "CollisionBytes", "ObjectBytes",
        "ObjectBufferRetry", "NpcsExported", "NpcRefreshes", "GraphicsExported",
        "BfsBuilds", "BfsNodes", "WeightedBuilds", "WeightedNodes",
        "WeightedEdges", "WeightedAccepted", "ProjectionPrefixes", "SafetyHits",
        "SafetyMisses", "Floor", "RouteIndex", "PlayerX", "PlayerY",
        "IsRunning", "DiagnosticsEnabled", "TraceEnabled",
        "ResolveCacheRead", "ResolveLiveSlot", "ResolveClickbox", "ResolvePack",
        "ResolveAnimation", "ResolveStrings", "ResolveCacheWrite",
        "WeightedNeighborSample", "WeightedPrimarySample", "WeightedSecondarySample",
        "WeightedWeightSample", "WeightedOfferSample", "WeightedPopSample",
        "DistanceOtherExact", "DistanceOtherFallback", "DistanceOtherUnavailable",
        "DistanceOtherBfsBuilds", "DistanceOtherBfsNodes",
        "DistanceDirectTargetExact", "DistanceDirectTargetFallback", "DistanceDirectTargetUnavailable",
        "DistanceDirectTargetBfsBuilds", "DistanceDirectTargetBfsNodes",
        "DistanceBonusScoringExact", "DistanceBonusScoringFallback", "DistanceBonusScoringUnavailable",
        "DistanceBonusScoringBfsBuilds", "DistanceBonusScoringBfsNodes",
        "DistanceImmunityFallbackExact", "DistanceImmunityFallbackFallback", "DistanceImmunityFallbackUnavailable",
        "DistanceImmunityFallbackBfsBuilds", "DistanceImmunityFallbackBfsNodes",
        "DistanceNearestFallbackExact", "DistanceNearestFallbackFallback", "DistanceNearestFallbackUnavailable",
        "DistanceNearestFallbackBfsBuilds", "DistanceNearestFallbackBfsNodes",
        "DistanceSegmentPathsExact", "DistanceSegmentPathsFallback", "DistanceSegmentPathsUnavailable",
        "DistanceSegmentPathsBfsBuilds", "DistanceSegmentPathsBfsNodes",
        "DistanceCacheHit", "DistanceCacheMiss", "DistanceCacheEviction",
        "DistanceCacheEvictedNodes", "DistanceNearestScanned", "DistanceProbeCalls",
        "DistanceTargetX", "DistanceTargetY", "DistanceTargetPlane",
        "DistancePlayerPlane", "DistanceTargetExact", "DistanceTargetFallback",
        "DistanceTargetUnavailable", "DistanceImmunityActive", "DistanceObstacleOnPlayer",
        "DistanceCrossesMoving", "DistanceImmunityFallbackEntered", "DistanceNearestFallbackEntered"
    };
    static_assert(sizeof(names) / sizeof(names[0]) == metricCount);
    const auto index = static_cast<size_t>(metric);
    return index < metricCount ? names[index] : "Unknown";
}

struct ServiceV1 {
    uint32_t structSize;
    uint32_t apiVersion;
    uint64_t (*begin)(uint32_t metric) noexcept;
    void (*end)(uint64_t token) noexcept;
    void (*add)(uint32_t metric, uint64_t value) noexcept;
};

} // namespace titan::perf
