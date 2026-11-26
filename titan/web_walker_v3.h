/// @file titan/web_walker_v3.h
/// @brief Typed calls into the web walker provider's V3 service: several
/// destinations or starts, banking, every route family and Wilderness
/// avoidance. Native C++ plugins only; declare a dependency on
/// kWebWalkerProviderPluginId.

#pragma once

#include "plugins.h"
#include "runescape_bank.h"
#include "web_walker.h"
#include "web_walker_provider.h"

#include <algorithm>
#include <chrono>
#include <cstdint>
#include <optional>
#include <string>
#include <vector>

namespace titan {

enum class WildernessAvoidance : uint32_t {
    None = WebPathWildernessAvoidance::None,
    Soft = WebPathWildernessAvoidance::Soft,
    Hard = WebPathWildernessAvoidance::Hard,
};

/// A route start or destination.
struct WebPathEndpoint {
    WorldPoint tile{};
    /// Cost points added when the route starts or ends here, so callers can
    /// rank endpoints. At most kWebPathV3MaxExtraCost.
    uint32_t costOffset = 0;
};

/// An area a route pays extra to be in; see WebPathCostZoneState. The area's
/// worldViewId is not sent.
struct WebPathCostZone {
    WorldArea area{};
    /// Cost points per tile entered inside. kWebPathCostZoneForbidden or more
    /// forbids entering the area.
    uint32_t costPerTile = 0;
};

/// Defaults match WebPathRequestV3State's.
struct WebPathV3Options {
    /// WebPathTransport bits.
    uint32_t transports = WebPathTransport::Default;
    bool allowBanking = false;
    bool partialWithinThreeTiles = true;
    WildernessAvoidance wilderness = WildernessAvoidance::Hard;
    WebPathRouteSpace routeSpace = WebPathRouteSpace::Global;
    std::chrono::milliseconds timeout{TitanPluginSdk::kWebPathDefaultTimeoutMs};
    /// At most kWebPathV3MaxCostZones.
    std::vector<WebPathCostZone> costZones;
};

/// Defaults match WebWalkRequestV3State's.
struct WebWalkV3Options {
    WebPathV3Options path{};
    bool manageRun = true;
    bool drinkStamina = false;
    /// Drive the session yourself by calling advance() once per game tick.
    bool manualTick = false;
    /// Chebyshev arrival tolerance around any destination; zero requires the
    /// exact tile.
    uint32_t arriveRadius = 0;
    /// Whole-walk budget in game ticks; zero = unlimited.
    uint32_t maxDurationTicks = 0;
    /// Run energy percent, at most 100, held back under manageRun for
    /// outrunning attackers. Zero never turns run off.
    uint32_t runEnergyReserve = 0;
};

namespace detail {

/// Empty when no live provider publishes a complete V3 table. Hold the lease
/// only for one call.
inline ServiceRef<WebWalkerProviderV3> webWalkerProviderV3() {
    auto provider = service<WebWalkerProviderV3>(kWebWalkerProviderV3ServiceId);
    if (!validWebWalkerProviderV3(provider.get())) return nullptr;
    return provider;
}

template <typename State>
std::vector<State> webPathEndpointStates(
        const std::vector<WebPathEndpoint>& endpoints) {
    std::vector<State> states;
    states.reserve(endpoints.size());
    for (const auto& endpoint : endpoints) {
        states.push_back({toWebPointState(endpoint.tile), endpoint.costOffset});
    }
    return states;
}

inline std::vector<WebPathCostZoneState> webPathCostZoneStates(
        const std::vector<WebPathCostZone>& zones) {
    std::vector<WebPathCostZoneState> states;
    states.reserve(zones.size());
    for (const auto& zone : zones) {
        states.push_back({zone.area.x, zone.area.y, zone.area.width,
                          zone.area.height, zone.area.plane,
                          zone.costPerTile});
    }
    return states;
}

/// Plans from the local player. The request borrows `destinations` and
/// `costZones`.
inline WebPathRequestV3State webPathRequestV3(
        const WebPathV3Options& options,
        const std::vector<WebPathDestinationState>& destinations,
        const std::vector<WebPathCostZoneState>& costZones) {
    WebPathRequestV3State request;
    request.destinations = destinations.data();
    request.destinationCount = static_cast<uint32_t>(destinations.size());
    request.costZones = costZones.empty() ? nullptr : costZones.data();
    request.costZoneCount = static_cast<uint32_t>(costZones.size());
    request.routeSpace = static_cast<uint32_t>(options.routeSpace);
    request.flags = WebPathV3Flag::UseLocalPlayer
        | (options.allowBanking ? WebPathV3Flag::AllowBanking : 0u)
        | (options.partialWithinThreeTiles
               ? WebPathV3Flag::PartialWithinThreeTiles : 0u);
    request.transports = options.transports;
    request.timeoutMs = static_cast<uint32_t>(std::clamp<int64_t>(
        options.timeout.count(), 1, TitanPluginSdk::kWebPathMaxTimeoutMs));
    request.wildernessAvoidance = static_cast<uint32_t>(options.wilderness);
    return request;
}

}  // namespace detail

/// Route planning through the provider's V3 service.
class WebWalkerV3Facade {
public:
    std::optional<WebPathHandle> submit(
            const WorldPoint& destination,
            const WebPathV3Options& options = {}) const {
        return submit(std::vector<WebPathEndpoint>{{destination}}, options);
    }

    /// The route runs from whichever start to whichever destination is
    /// cheapest, counting their costOffsets. No starts plans from the local
    /// player.
    std::optional<WebPathHandle> submit(
            const std::vector<WebPathEndpoint>& destinations,
            const WebPathV3Options& options = {},
            const std::vector<WebPathEndpoint>& starts = {}) const {
        if (destinations.empty()
            || destinations.size() > kWebPathV3MaxEndpoints
            || starts.size() > kWebPathV3MaxEndpoints
            || options.costZones.size() > kWebPathV3MaxCostZones) {
            return std::nullopt;
        }
        auto provider = detail::webWalkerProviderV3();
        if (!provider) return std::nullopt;
        const auto destinationStates =
            detail::webPathEndpointStates<WebPathDestinationState>(destinations);
        const auto startStates =
            detail::webPathEndpointStates<WebPathStartState>(starts);
        const auto zoneStates = detail::webPathCostZoneStates(options.costZones);
        auto request =
            detail::webPathRequestV3(options, destinationStates, zoneStates);
        if (!starts.empty()) {
            request.flags &= ~WebPathV3Flag::UseLocalPlayer;
            request.starts = startStates.data();
            request.startCount = static_cast<uint32_t>(startStates.size());
        }
        WebPathHandle handle = 0;
        if (!provider->pathSubmit(provider->userData, &request, &handle)
            || handle == 0) {
            return std::nullopt;
        }
        return handle;
    }

    std::optional<WebPathSummary> poll(WebPathHandle handle) const {
        auto provider = detail::webWalkerProviderV3();
        if (!provider || handle == 0) return std::nullopt;
        return detail::pollWebPath([&](auto* out) {
            return provider->pathPoll(provider->userData, handle, out);
        });
    }

    std::optional<std::vector<WebPathStep>> copySteps(
            WebPathHandle handle) const {
        auto provider = detail::webWalkerProviderV3();
        if (!provider || handle == 0) return std::nullopt;
        return detail::copyWebPathSteps(
            [&](TitanPluginSdk::WebPathStepState* out, uint32_t capacity,
                uint32_t* outCount) {
                return provider->pathCopySteps(
                    provider->userData, handle, out, capacity, outCount);
            });
    }

    /// UTF-8 JSON describing one step of a completed route.
    std::optional<std::string> stepPayload(WebPathHandle handle,
                                           uint32_t stepIndex) const {
        auto provider = detail::webWalkerProviderV3();
        if (!provider || handle == 0) return std::nullopt;
        return detail::copyWebPathStepPayload(
            [&](char* out, uint32_t capacity, uint32_t* outRequired) {
                return provider->pathCopyStepPayload(
                    provider->userData, handle, stepIndex, out, capacity,
                    outRequired);
            });
    }

    bool cancel(WebPathHandle handle) const {
        auto provider = detail::webWalkerProviderV3();
        return provider && handle != 0
            && provider->pathCancel(provider->userData, handle) != 0;
    }

    bool release(WebPathHandle handle) const {
        auto provider = detail::webWalkerProviderV3();
        return provider && handle != 0
            && provider->pathRelease(provider->userData, handle) != 0;
    }
};

/// Walks the local player through the provider's V3 service. Walk handles are
/// shared with webWalk(): one session per client, and a new walk supersedes
/// the previous one whichever version started it. Handle 0 addresses the
/// latest walk for status() and copySteps(), and the active walk for cancel()
/// and advance(), whichever plugin started it.
class WebWalkV3Facade {
public:
    std::optional<WebWalkHandle> walkTo(
            const WorldPoint& destination,
            const WebWalkV3Options& options = {}) const {
        return walkTo(std::vector<WebPathEndpoint>{{destination}}, options);
    }

    /// Walks to whichever destination is cheapest to reach, counting their
    /// costOffsets, and arrives on reaching any of them. status() reports the
    /// first destination, not the one being walked to.
    std::optional<WebWalkHandle> walkTo(
            const std::vector<WebPathEndpoint>& destinations,
            const WebWalkV3Options& options = {}) const {
        if (destinations.empty()
            || destinations.size() > kWebPathV3MaxEndpoints
            || options.path.costZones.size() > kWebPathV3MaxCostZones) {
            return std::nullopt;
        }
        auto provider = detail::webWalkerProviderV3();
        if (!provider) return std::nullopt;
        const auto destinationStates =
            detail::webPathEndpointStates<WebPathDestinationState>(destinations);
        const auto zoneStates =
            detail::webPathCostZoneStates(options.path.costZones);
        WebWalkRequestV3State request;
        request.path = detail::webPathRequestV3(options.path, destinationStates,
                                                zoneStates);
        request.walkFlags =
            (options.manageRun ? TitanPluginSdk::WebWalkFlag::ManageRun : 0u)
            | (options.drinkStamina
                   ? TitanPluginSdk::WebWalkFlag::DrinkStamina : 0u)
            | (options.manualTick
                   ? TitanPluginSdk::WebWalkFlag::ManualTick : 0u);
        request.arriveRadius = options.arriveRadius;
        request.maxDurationTicks = options.maxDurationTicks;
        request.runEnergyReserve = options.runEnergyReserve;
        WebWalkHandle walkId = 0;
        if (!provider->walkStart(provider->userData, &request, &walkId)
            || walkId == 0) {
            return std::nullopt;
        }
        return walkId;
    }

    /// Walks to the bank the account can use that is cheapest to reach, which
    /// may be by teleport; clear the teleport bits of options.path.transports
    /// to walk there. Reads quest, varbit and world state, so call it with
    /// world-ready state.
    std::optional<WebWalkHandle> walkToNearestBank(
            const WebWalkV3Options& options = {}) const {
        std::vector<WebPathEndpoint> banks;
        for (const auto& entry : RunescapeBankInfo::kBanks) {
            if (RunescapeBankInfo::canUse(entry.bank)) {
                banks.push_back({entry.location});
            }
        }
        return walkTo(banks, options);
    }

    std::optional<WebWalkStatus> status(WebWalkHandle handle) const {
        auto provider = detail::webWalkerProviderV3();
        if (!provider) return std::nullopt;
        return detail::webWalkStatus([&](auto* out) {
            return provider->walkStatus(provider->userData, handle, out);
        });
    }

    /// The route the walk follows; status().currentStepIndex indexes it. Empty
    /// while the walk plans its first route.
    std::optional<std::vector<WebPathStep>> copySteps(
            WebWalkHandle handle) const {
        auto provider = detail::webWalkerProviderV3();
        if (!provider) return std::nullopt;
        return detail::copyWebPathSteps(
            [&](TitanPluginSdk::WebPathStepState* out, uint32_t capacity,
                uint32_t* outCount) {
                return provider->walkCopySteps(
                    provider->userData, handle, out, capacity, outCount);
            });
    }

    bool cancel(WebWalkHandle handle) const {
        auto provider = detail::webWalkerProviderV3();
        return provider
            && provider->walkCancel(provider->userData, handle) != 0;
    }

    bool release(WebWalkHandle handle) const {
        auto provider = detail::webWalkerProviderV3();
        return provider && handle != 0
            && provider->walkRelease(provider->userData, handle) != 0;
    }

    /// manualTick sessions only: advance one follower tick. Call once per
    /// game tick from onGameTick.
    bool advance(WebWalkHandle handle) const {
        auto provider = detail::webWalkerProviderV3();
        return provider
            && provider->walkAdvance(provider->userData, handle) != 0;
    }
};

inline WebWalkerV3Facade webWalkerV3() { return WebWalkerV3Facade{}; }
inline WebWalkV3Facade webWalkV3() { return WebWalkV3Facade{}; }

}  // namespace titan
