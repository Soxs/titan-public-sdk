#include <titan/web_walker_v3.h>
#include <titan/detail/backend_external.h>

#include "check.h"
#include <algorithm>
#include <cstring>
#include <vector>

using namespace titan;

namespace {

// The provider borrows request arrays only for the call, so the fake copies them.
WebPathRequestV3State pathRequest;
WebWalkRequestV3State walkRequest;
std::vector<WebPathDestinationState> destinations;
std::vector<WebPathStartState> starts;
std::vector<WebPathCostZoneState> costZones;
bool providerAvailable = true;
uint64_t cancelledWalk = ~uint64_t{0};
int openLeases = 0;

void capture(const WebPathRequestV3State& request) {
    pathRequest = request;
    destinations.assign(request.destinations, request.destinations + request.destinationCount);
    starts.assign(request.starts, request.starts + request.startCount);
    costZones.assign(request.costZones, request.costZones + request.costZoneCount);
}

uint8_t fillSteps(TitanPluginSdk::WebPathStepState* out, uint32_t capacity, uint32_t* outCount) {
    *outCount = 2;
    if (!out) return 1;
    if (capacity < 2) return 0;
    out[0] = {};
    std::strcpy(out[0].name, "Walk");
    out[1] = {};
    out[1].kind = TitanPluginSdk::WebPathStepKind::Teleport;
    std::strcpy(out[1].name, "Varrock Teleport");
    return 1;
}

WebWalkerProviderV3 makeProvider() {
    WebWalkerProviderV3 provider;
    provider.pathSubmit = [](void*, const WebPathRequestV3State* request, uint64_t* out) -> uint8_t {
        capture(*request);
        *out = 7;
        return 1;
    };
    provider.pathPoll = [](void*, uint64_t, TitanPluginSdk::WebPathSummaryState*) -> uint8_t { return 0; };
    provider.pathCopySteps = [](void*, uint64_t, TitanPluginSdk::WebPathStepState* out, uint32_t capacity,
                                uint32_t* outCount) { return fillSteps(out, capacity, outCount); };
    provider.pathCopyStepPayload = [](void*, uint64_t, uint32_t, char*, uint32_t, uint32_t*) -> uint8_t { return 0; };
    provider.pathCancel = [](void*, uint64_t) -> uint8_t { return 1; };
    provider.pathRelease = [](void*, uint64_t) -> uint8_t { return 1; };
    provider.walkStart = [](void*, const WebWalkRequestV3State* request, uint64_t* out) -> uint8_t {
        walkRequest = *request;
        capture(request->path);
        *out = 9;
        return 1;
    };
    provider.walkStatus = [](void*, uint64_t, TitanPluginSdk::WebWalkStatusState*) -> uint8_t { return 0; };
    provider.walkCancel = [](void*, uint64_t walkId) -> uint8_t {
        cancelledWalk = walkId;
        return 1;
    };
    provider.walkRelease = [](void*, uint64_t) -> uint8_t { return 1; };
    provider.walkAdvance = [](void*, uint64_t) -> uint8_t { return 1; };
    provider.walkCopySteps = [](void*, uint64_t, TitanPluginSdk::WebPathStepState* out, uint32_t capacity,
                                uint32_t* outCount) { return fillSteps(out, capacity, outCount); };
    return provider;
}

WebWalkerProviderV3 provider = makeProvider();

bool hasDestination(const WorldPoint& tile) {
    return std::ranges::any_of(destinations, [&](const WebPathDestinationState& destination) {
        return destination.tile.x == tile.x && destination.tile.y == tile.y && destination.tile.z == tile.z;
    });
}

}  // namespace

int main() {
    TitanPluginSdk::HostApi host{};
    host.webPathCopySteps = [](uint64_t, TitanPluginSdk::WebPathStepState* out, uint32_t capacity,
                               uint32_t* outCount) { return fillSteps(out, capacity, outCount); };
    TitanPluginSdk::NativeAbi::LifetimeV1 lifetime;
    lifetime.acquireService = [](void*, const char* id, void** outService, void** outLease) -> uint8_t {
        if (!providerAvailable || std::strcmp(id, kWebWalkerProviderV3ServiceId) != 0) return 0;
        *outService = &provider;
        *outLease = &provider;
        ++openLeases;
        return 1;
    };
    lifetime.releaseLease = [](void*, void*) { --openLeases; };
    detail::ExternalBackend backend{&host};
    backend.setLifetime(&lifetime);
    detail::backendRef() = &backend;

    const WorldPoint varrock{3213, 3424, 0};

    // Leaving every option at its default must send the raw request's defaults.
    CHECK(webWalkV3().walkTo(varrock) == 9u);
    const WebWalkRequestV3State rawWalk{};
    const auto& rawPath = rawWalk.path;
    CHECK(walkRequest.walkFlags == rawWalk.walkFlags);
    CHECK(walkRequest.arriveRadius == rawWalk.arriveRadius);
    CHECK(walkRequest.maxDurationTicks == rawWalk.maxDurationTicks);
    CHECK(walkRequest.runEnergyReserve == rawWalk.runEnergyReserve);
    CHECK(pathRequest.routeSpace == rawPath.routeSpace);
    CHECK(pathRequest.flags == rawPath.flags);
    CHECK(pathRequest.transports == rawPath.transports);
    CHECK(pathRequest.timeoutMs == rawPath.timeoutMs);
    CHECK(pathRequest.wildernessAvoidance == rawPath.wildernessAvoidance);
    CHECK(destinations.size() == 1 && hasDestination(varrock) && destinations[0].costOffset == 0);
    CHECK(costZones.empty());

    // Explicit starts replace the local player; each option reaches its own field.
    WebPathV3Options options;
    options.allowBanking = true;
    options.partialWithinThreeTiles = false;
    options.wilderness = WildernessAvoidance::Soft;
    options.transports = WebPathTransport::FairyRings;
    options.costZones = {{WorldArea{3200, 3400, 5, 3, 1}, 250}};
    const WorldPoint lumbridge{3222, 3218, 0};
    CHECK(webWalkerV3().submit({{varrock}, {lumbridge, 40}}, options, {{lumbridge, 15}}) == 7u);
    CHECK(pathRequest.flags == WebPathV3Flag::AllowBanking);
    CHECK(pathRequest.wildernessAvoidance == WebPathWildernessAvoidance::Soft);
    CHECK(pathRequest.transports == WebPathTransport::FairyRings);
    CHECK(destinations.size() == 2 && destinations[1].costOffset == 40);
    CHECK(starts.size() == 1 && starts[0].tile.x == lumbridge.x && starts[0].costOffset == 15);
    CHECK(costZones.size() == 1 && costZones[0].x == 3200 && costZones[0].y == 3400 && costZones[0].width == 5
          && costZones[0].height == 3 && costZones[0].plane == 1 && costZones[0].costPerTile == 250);
    options.costZones.resize(kWebPathV3MaxCostZones + 1);
    CHECK(!webWalkerV3().submit(varrock, options));

    // With no quests or diaries done, only banks without requirements are candidates.
    CHECK(webWalkV3().walkToNearestBank());
    CHECK(hasDestination(*RunescapeBankInfo::getLocation(RunescapeBank::GRAND_EXCHANGE)));
    CHECK(!hasDestination(*RunescapeBankInfo::getLocation(RunescapeBank::BLAST_FURNACE_BANK)));

    // The V3 provider and the host fill the same step records, for paths and for the latest walk alike.
    for (const auto& steps : {webWalkerV3().copySteps(7), webWalker().copySteps(7), webWalkV3().copySteps(0)}) {
        CHECK(steps && steps->size() == 2);
        CHECK((*steps)[1].kind == WebPathStepKind::Teleport && (*steps)[1].name == "Varrock Teleport");
    }

    // Handle 0 reaches the provider, which reads it as the active walk whoever started it.
    CHECK(webWalkV3().cancel(0) && cancelledWalk == 0);

    CHECK(openLeases == 0);
    providerAvailable = false;
    CHECK(!webWalkV3().walkTo(varrock));
    return 0;
}
