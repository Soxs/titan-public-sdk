/// @file titan/web_walker.h
/// @brief Read-only asynchronous web-path generation facade (SDK 112).

#pragma once

#include "detail/backend.h"
#include "web_walker_areas.h"
#include "world_area.h"
#include "world_point.h"

#include <algorithm>
#include <chrono>
#include <cstdint>
#include <optional>
#include <string>
#include <vector>

namespace titan {

using WebPathHandle = uint64_t;

enum class WebPathRouteSpace : uint32_t {
    Global = TitanPluginSdk::WebPathRouteSpace::Global,
    CurrentInstance = TitanPluginSdk::WebPathRouteSpace::CurrentInstance,
};

enum class WebPathPhase : uint32_t {
    None = TitanPluginSdk::WebPathPhase::None,
    Queued = TitanPluginSdk::WebPathPhase::Queued,
    Running = TitanPluginSdk::WebPathPhase::Running,
    Complete = TitanPluginSdk::WebPathPhase::Complete,
    Failed = TitanPluginSdk::WebPathPhase::Failed,
    Cancelled = TitanPluginSdk::WebPathPhase::Cancelled,
};

enum class WebPathResult : uint32_t {
    None = TitanPluginSdk::WebPathResult::None,
    Exact = TitanPluginSdk::WebPathResult::Exact,
    PartialWithinThreeTiles =
        TitanPluginSdk::WebPathResult::PartialWithinThreeTiles,
    NoPath = TitanPluginSdk::WebPathResult::NoPath,
    Timeout = TitanPluginSdk::WebPathResult::Timeout,
    Cancelled = TitanPluginSdk::WebPathResult::Cancelled,
    InvalidRequest = TitanPluginSdk::WebPathResult::InvalidRequest,
    NotLoggedIn = TitanPluginSdk::WebPathResult::NotLoggedIn,
    CollisionUnavailable = TitanPluginSdk::WebPathResult::CollisionUnavailable,
    ProviderUnavailable = TitanPluginSdk::WebPathResult::ProviderUnavailable,
    Busy = TitanPluginSdk::WebPathResult::Busy,
    InternalError = TitanPluginSdk::WebPathResult::InternalError,
};

enum class WebPathStepKind : uint32_t {
    Walk = TitanPluginSdk::WebPathStepKind::Walk,
    Transport = TitanPluginSdk::WebPathStepKind::Transport,
    Teleport = TitanPluginSdk::WebPathStepKind::Teleport,
};

struct WebPathOptions {
    bool transports = true;
    bool teleports = true;
    bool equippedItemTeleports = true;
    bool minigameTeleports = false;
    bool pohRoutes = true;
    bool charters = false;
    bool avoidWilderness = true;

    uint32_t bits() const {
        uint32_t value = 0;
        if (transports) value |= TitanPluginSdk::WebPathOption::Transports;
        if (teleports) value |= TitanPluginSdk::WebPathOption::Teleports;
        if (equippedItemTeleports) {
            value |= TitanPluginSdk::WebPathOption::EquippedItemTeleports;
        }
        if (minigameTeleports) {
            value |= TitanPluginSdk::WebPathOption::MinigameTeleports;
        }
        if (pohRoutes) value |= TitanPluginSdk::WebPathOption::PohRoutes;
        if (charters) value |= TitanPluginSdk::WebPathOption::Charters;
        if (avoidWilderness) {
            value |= TitanPluginSdk::WebPathOption::AvoidWilderness;
        }
        return value;
    }
};

struct WebPathRequest {
    /// Ignored when `useLocalPlayer` is true (the default).
    WorldPoint start{};
    WorldPoint destination{};
    WebPathRouteSpace routeSpace = WebPathRouteSpace::Global;
    WebPathOptions options{};
    std::chrono::milliseconds timeout{TitanPluginSdk::kWebPathDefaultTimeoutMs};
    bool useLocalPlayer = true;
    std::vector<WorldPoint> forbiddenTiles;
    /// Whole areas the route may not enter (canonical world tiles; an
    /// area's world view is not consulted). To the walker an area is every
    /// one of its tiles: no walking step, transport or teleport may end
    /// inside it. A request that starts inside an area can only leave across
    /// its edge, and a destination inside one has no route, so leave out any
    /// area the trip must begin or end in. Each area takes two of the
    /// kWebPathMaxForbiddenTiles entries a request may send, whatever its
    /// size (see <titan/web_walker_areas.h>).
    std::vector<WorldArea> forbiddenAreas;
    /// Also send each area's outline as ordinary forbidden tiles, as far as
    /// the entry limit allows. A WebWalker Engine that predates areas does
    /// not avoid them, but cannot walk across an outline. Turn this off once
    /// every engine the plugin runs with honours areas.
    bool outlineForbiddenAreas = true;
};

struct WebPathSummary {
    WebPathHandle requestId = 0;
    WebPathPhase phase = WebPathPhase::None;
    WebPathResult result = WebPathResult::None;
    /// Integer cost points. A normal walked tile contributes 5 points.
    uint32_t totalCost = 0;
    uint32_t stepCount = 0;
    uint32_t exploredNodes = 0;
    std::chrono::milliseconds elapsed{};
    WorldPoint start{};
    WorldPoint requestedDestination{};
    WorldPoint reachedDestination{};
    std::string message;

    bool finished() const {
        return phase == WebPathPhase::Complete
            || phase == WebPathPhase::Failed
            || phase == WebPathPhase::Cancelled;
    }
};

struct WebPathStep {
    uint64_t edgeId = 0;
    WebPathStepKind kind = WebPathStepKind::Walk;
    uint32_t subtype = 0;
    /// Route domains are endpoint-specific because a teleport may leave an
    /// instance for the canonical global graph.
    WebPathRouteSpace fromRouteSpace = WebPathRouteSpace::Global;
    WebPathRouteSpace toRouteSpace = WebPathRouteSpace::Global;
    /// Integer cost points, matching the fixed ABI directly.
    uint32_t edgeCost = 0;
    uint32_t accumulatedCost = 0;
    /// Exact instance-copy identities; -1 when the corresponding endpoint is
    /// in global route space.
    int32_t fromInstanceCopyId = -1;
    int32_t toInstanceCopyId = -1;
    WorldPoint from{};
    WorldPoint to{};
    std::string name;
};

namespace detail {
inline TitanPluginSdk::WorldPointState toWebPointState(const WorldPoint& point) {
    return {point.x, point.y, point.z, point.worldViewId};
}

inline WorldPoint fromWebPointState(
        const TitanPluginSdk::WorldPointState& point) {
    return {point.x, point.y, point.z, point.worldViewId};
}

/// The forbidden array of a request: its tiles, then its areas (see
/// <titan/web_walker_areas.h>), then, when asked for, as much of the areas'
/// outlines as still fits. Empty optional when the tiles and areas alone
/// are more than a request may send.
inline std::optional<std::vector<TitanPluginSdk::WorldPointState>>
webForbiddenStates(const std::vector<WorldPoint>& tiles,
                   const std::vector<WorldArea>& areas, bool outlines) {
    std::vector<TitanPluginSdk::WorldPointState> forbidden;
    forbidden.reserve(tiles.size() + areas.size() * 2);
    for (const auto& point : tiles) {
        forbidden.push_back(toWebPointState(point));
    }
    for (const auto& area : areas) {
        appendWebPathForbiddenArea(forbidden, area.x, area.y, area.width,
                                   area.height, area.plane);
    }
    if (forbidden.size() > TitanPluginSdk::kWebPathMaxForbiddenTiles) {
        return std::nullopt;
    }
    if (outlines) {
        for (const auto& area : areas) {
            if (!appendWebPathForbiddenOutline(
                    forbidden, area.x, area.y, area.width, area.height,
                    area.plane, TitanPluginSdk::kWebPathMaxForbiddenTiles)) {
                break;
            }
        }
    }
    return forbidden;
}

template <size_t N>
inline std::string fixedWebString(const char (&value)[N]) {
    size_t length = 0;
    while (length < N && value[length] != '\0') ++length;
    return std::string(value, length);
}

// The host and the walker provider's services return the same records, so
// each conversion takes the call that fills them.

/// `poll(TitanPluginSdk::WebPathSummaryState*)` returns nonzero on success.
template <typename Poll>
std::optional<WebPathSummary> pollWebPath(Poll poll) {
    TitanPluginSdk::WebPathSummaryState state{};
    if (!poll(&state)) return std::nullopt;
    if (state.stepCount > TitanPluginSdk::kWebPathMaxSteps) {
        return std::nullopt;
    }

    WebPathSummary result;
    result.requestId = state.requestId;
    result.phase = static_cast<WebPathPhase>(state.phase);
    result.result = static_cast<WebPathResult>(state.result);
    result.totalCost = state.totalCost;
    result.stepCount = state.stepCount;
    result.exploredNodes = state.exploredNodes;
    result.elapsed = std::chrono::milliseconds(state.elapsedMs);
    result.start = fromWebPointState(state.start);
    result.requestedDestination = fromWebPointState(state.requestedDestination);
    result.reachedDestination = fromWebPointState(state.reachedDestination);
    result.message = fixedWebString(state.message);
    return result;
}

/// `copy(TitanPluginSdk::WebPathStepState* out, uint32_t capacity,
/// uint32_t* outCount)` returns nonzero on success; a null output is a size
/// query.
template <typename Copy>
std::optional<std::vector<WebPathStep>> copyWebPathSteps(Copy copy) {
    uint32_t required = 0;
    if (!copy(nullptr, 0, &required)) return std::nullopt;
    if (required > TitanPluginSdk::kWebPathMaxSteps) return std::nullopt;
    std::vector<TitanPluginSdk::WebPathStepState> states(required);
    uint32_t copied = required;
    if (required != 0 && !copy(states.data(), required, &copied)) {
        return std::nullopt;
    }
    if (copied > required || copied > TitanPluginSdk::kWebPathMaxSteps) {
        return std::nullopt;
    }
    states.resize(copied);

    std::vector<WebPathStep> result;
    result.reserve(states.size());
    for (const auto& state : states) {
        result.push_back(WebPathStep{
            state.edgeId,
            static_cast<WebPathStepKind>(state.kind),
            state.subtype,
            static_cast<WebPathRouteSpace>(state.fromRouteSpace),
            static_cast<WebPathRouteSpace>(state.toRouteSpace),
            state.edgeCost,
            state.accumulatedCost,
            TitanPluginSdk::webPathAbiInstanceCopyId(
                state.fromRouteSpace, state.fromInstanceCopyId),
            TitanPluginSdk::webPathAbiInstanceCopyId(
                state.toRouteSpace, state.toInstanceCopyId),
            fromWebPointState(state.from),
            fromWebPointState(state.to),
            fixedWebString(state.name),
        });
    }
    return result;
}

/// `copy(char* out, uint32_t capacity, uint32_t* outRequired)` returns
/// nonzero on success; a null output is a size query.
template <typename Copy>
std::optional<std::string> copyWebPathStepPayload(Copy copy) {
    uint32_t required = 0;
    if (!copy(nullptr, 0, &required) || required == 0) return std::nullopt;
    std::string payload(required - 1, '\0');
    if (!copy(payload.data(), required, &required)) return std::nullopt;
    return payload;
}
}  // namespace detail

class WebWalkerFacade {
public:
    std::optional<WebPathHandle> submit(const WebPathRequest& request) const {
        auto* backend = detail::backend();
        const auto states = detail::webForbiddenStates(
            request.forbiddenTiles, request.forbiddenAreas,
            request.outlineForbiddenAreas);
        if (!backend || !states) return std::nullopt;
        const auto& forbidden = *states;

        TitanPluginSdk::WebPathRequestState state{};
        state.start = detail::toWebPointState(request.start);
        state.destination = detail::toWebPointState(request.destination);
        state.routeSpace = static_cast<uint32_t>(request.routeSpace);
        state.options = request.options.bits();
        const auto timeout = std::clamp<int64_t>(
            request.timeout.count(), 1,
            TitanPluginSdk::kWebPathMaxTimeoutMs);
        state.timeoutMs = static_cast<uint32_t>(timeout);
        state.requestFlags = request.useLocalPlayer
            ? TitanPluginSdk::WebPathRequestFlag::UseLocalPlayer : 0;

        WebPathHandle handle = 0;
        if (!backend->webPathSubmit(
                &state, forbidden.empty() ? nullptr : forbidden.data(),
                static_cast<uint32_t>(forbidden.size()), &handle)
            || handle == 0) {
            return std::nullopt;
        }
        return handle;
    }

    std::optional<WebPathSummary> poll(WebPathHandle handle) const {
        auto* backend = detail::backend();
        if (!backend || handle == 0) return std::nullopt;
        return detail::pollWebPath([&](auto* out) {
            return backend->webPathPoll(handle, out);
        });
    }

    std::optional<std::vector<WebPathStep>> copySteps(
            WebPathHandle handle) const {
        auto* backend = detail::backend();
        if (!backend || handle == 0) return std::nullopt;
        return detail::copyWebPathSteps(
            [&](TitanPluginSdk::WebPathStepState* out, uint32_t capacity,
                uint32_t* outCount) {
                return backend->webPathCopySteps(
                    handle, out, capacity, outCount);
            });
    }

    bool cancel(WebPathHandle handle) const {
        auto* backend = detail::backend();
        return backend && handle != 0 && backend->webPathCancel(handle) != 0;
    }

    bool release(WebPathHandle handle) const {
        auto* backend = detail::backend();
        return backend && handle != 0 && backend->webPathRelease(handle) != 0;
    }

    /// SDK 114: the raw JSON action payload of one step of a completed
    /// internal request — object ids, actions, dialog options, widget ids —
    /// for plugins building custom route executors. Empty optional when the
    /// request is unknown, provider-backed, still running, or predates the
    /// executor ABI.
    std::optional<std::string> stepPayload(WebPathHandle handle,
                                           uint32_t stepIndex) const {
        auto* backend = detail::backend();
        if (!backend || handle == 0) return std::nullopt;
        return detail::copyWebPathStepPayload(
            [&](char* out, uint32_t capacity, uint32_t* outRequired) {
                return backend->webPathCopyStepPayload(
                    handle, stepIndex, out, capacity, outRequired);
            });
    }
};

// --- SDK 114: web walk executor ------------------------------------------

using WebWalkHandle = uint64_t;

enum class WebWalkPhase : uint32_t {
    None = TitanPluginSdk::WebWalkPhase::None,
    Planning = TitanPluginSdk::WebWalkPhase::Planning,
    Walking = TitanPluginSdk::WebWalkPhase::Walking,
    Transiting = TitanPluginSdk::WebWalkPhase::Transiting,
    Arrived = TitanPluginSdk::WebWalkPhase::Arrived,
    Failed = TitanPluginSdk::WebWalkPhase::Failed,
    Cancelled = TitanPluginSdk::WebWalkPhase::Cancelled,
};

inline bool isTerminal(WebWalkPhase phase) {
    return TitanPluginSdk::isTerminalWebWalkPhase(
        static_cast<uint32_t>(phase));
}

struct WebWalkOptions {
    /// Route generation options for every plan and replan.
    WebPathOptions pathOptions{};
    std::chrono::milliseconds planTimeout{
        TitanPluginSdk::kWebPathDefaultTimeoutMs};
    std::vector<WorldPoint> forbiddenTiles;
    /// Whole areas the walk may not enter, on every plan and replan; see
    /// WebPathRequest::forbiddenAreas. Leave out any area the walk begins or
    /// ends in.
    std::vector<WorldArea> forbiddenAreas;
    /// See WebPathRequest::outlineForbiddenAreas.
    bool outlineForbiddenAreas = true;
    bool manageRun = true;
    bool drinkStamina = false;
    /// Drive the session yourself by calling advance() once per game tick
    /// instead of the host's automatic pump.
    bool manualTick = false;
    /// Chebyshev arrival tolerance; zero requires the exact tile.
    uint32_t arriveRadius = 0;
    /// Whole-walk budget in game ticks; zero = unlimited.
    uint32_t maxDurationTicks = 0;
};

struct WebWalkStatus {
    WebWalkHandle walkId = 0;
    WebWalkPhase phase = WebWalkPhase::None;
    WebPathResult pathResult = WebPathResult::None;
    uint32_t currentStepIndex = 0;
    uint32_t stepCount = 0;
    uint32_t ticksActive = 0;
    uint32_t replanCount = 0;
    uint32_t lastDecision = 0;
    WorldPoint destination{};
    std::string currentStepName;
    std::string message;
};

namespace detail {
/// `status(TitanPluginSdk::WebWalkStatusState*)` returns nonzero on success.
template <typename Status>
std::optional<WebWalkStatus> webWalkStatus(Status status) {
    TitanPluginSdk::WebWalkStatusState state{};
    state.structSize = sizeof(state);
    state.apiVersion = TitanPluginSdk::kWebWalkApiVersion;
    if (!status(&state)) return std::nullopt;
    WebWalkStatus result;
    result.walkId = state.walkId;
    result.phase = static_cast<WebWalkPhase>(state.phase);
    result.pathResult = static_cast<WebPathResult>(state.pathResult);
    result.currentStepIndex = state.currentStepIndex;
    result.stepCount = state.stepCount;
    result.ticksActive = state.ticksActive;
    result.replanCount = state.replanCount;
    result.lastDecision = state.lastDecision;
    result.destination = fromWebPointState(state.destination);
    result.currentStepName = fixedWebString(state.currentStepName);
    result.message = fixedWebString(state.message);
    return result;
}
}  // namespace detail

/// Autonomous path following: the client walks the local player to the
/// destination, using the same route engine as submit(), and (as executor
/// milestones land) its transports and teleports. One session per client; a
/// new walkTo supersedes the previous session.
class WebWalkFacade {
public:
    std::optional<WebWalkHandle> walkTo(
            const WorldPoint& destination,
            const WebWalkOptions& options = {}) const {
        auto* backend = detail::backend();
        const auto states = detail::webForbiddenStates(
            options.forbiddenTiles, options.forbiddenAreas,
            options.outlineForbiddenAreas);
        if (!backend || !states) return std::nullopt;
        const auto& forbidden = *states;

        TitanPluginSdk::WebWalkRequestState state{};
        state.path.destination = detail::toWebPointState(destination);
        state.path.routeSpace = static_cast<uint32_t>(
            TitanPluginSdk::WebPathRouteSpace::Global);
        state.path.options = options.pathOptions.bits();
        const auto timeout = std::clamp<int64_t>(
            options.planTimeout.count(), 1,
            TitanPluginSdk::kWebPathMaxTimeoutMs);
        state.path.timeoutMs = static_cast<uint32_t>(timeout);
        state.path.requestFlags =
            TitanPluginSdk::WebPathRequestFlag::UseLocalPlayer;
        state.walkFlags =
            (options.manageRun ? TitanPluginSdk::WebWalkFlag::ManageRun : 0u)
            | (options.drinkStamina
                   ? TitanPluginSdk::WebWalkFlag::DrinkStamina : 0u)
            | (options.manualTick
                   ? TitanPluginSdk::WebWalkFlag::ManualTick : 0u);
        state.arriveRadius = options.arriveRadius;
        state.maxDurationTicks = options.maxDurationTicks;

        uint64_t walkId = 0;
        if (backend->webWalkStart(
                &state, forbidden.empty() ? nullptr : forbidden.data(),
                static_cast<uint32_t>(forbidden.size()), &walkId) == 0
            || walkId == 0) {
            return std::nullopt;
        }
        return walkId;
    }

    std::optional<WebWalkStatus> status(WebWalkHandle handle) const {
        auto* backend = detail::backend();
        if (!backend || handle == 0) return std::nullopt;
        return detail::webWalkStatus([&](auto* out) {
            return backend->webWalkStatus(handle, out);
        });
    }

    bool cancel(WebWalkHandle handle) const {
        auto* backend = detail::backend();
        return backend && handle != 0
            && backend->webWalkCancel(handle) != 0;
    }

    bool release(WebWalkHandle handle) const {
        auto* backend = detail::backend();
        return backend && handle != 0
            && backend->webWalkRelease(handle) != 0;
    }

    /// Manual-mode sessions only: advance one follower tick. Call once per
    /// game tick from onGameTick.
    bool advance(WebWalkHandle handle) const {
        auto* backend = detail::backend();
        return backend && handle != 0
            && backend->webWalkAdvance(handle) != 0;
    }
};

inline WebWalkFacade webWalk() { return WebWalkFacade{}; }

inline WebWalkerFacade webWalker() { return WebWalkerFacade{}; }
namespace state {
inline ::titan::WebWalkerFacade webWalker() { return ::titan::WebWalkerFacade{}; }
inline ::titan::WebWalkFacade webWalk() { return ::titan::WebWalkFacade{}; }
}  // namespace state

}  // namespace titan
