/// @file titan/web_walker_via.h
/// @brief Fixed-ABI contract for the web walker's "via" request.
///
/// "Which of these places should I stop at on my way there?" The request
/// names a start, a destination and a list of via points (banks, say), and
/// the walker answers with the via point that makes the cheapest whole trip:
/// start -> via point -> destination. It plans the trip in two searches,
/// whatever the number of via points, where asking `webWalker().submit` about
/// each leg of each point takes two requests a point.
///
/// The WebWalker Engine (`web_walker_provider`) publishes this as an ordinary
/// plugin service. It is optional: an engine that predates it does not
/// publish it, and callers fall back to pricing the legs themselves.
///
/// @code
///   if (auto via = titan::service<titan::WebWalkerViaV1>(titan::kWebWalkerViaServiceId);
///       titan::validWebWalkerViaV1(via.get())) {
///       TitanPluginSdk::WebPathRequestState request{};   // start: local player
///       request.destination = {2559, 3445, 0, titan::WorldView::TOP_LEVEL};
///       uint64_t id = 0;
///       via->submit(via->userData, &request, banks.data(), bankCount, &id);
///   }   // the lease ends here: resolve the service again to poll
/// @endcode
///
/// No C++ objects cross the DLL boundary. Request ids are local to the engine
/// generation that issued them; after an engine reload `poll` fails and the
/// caller starts again.

#pragma once

#include "detail/abi.h"

#include <cstddef>
#include <cstdint>
#include <type_traits>

namespace titan {

inline constexpr const char* kWebWalkerViaServiceId = "titan.web_walker.via.v1";
inline constexpr uint32_t kWebWalkerViaApiVersion = 1;
/// The most via points one request may name.
inline constexpr uint32_t kWebWalkerViaMaxPoints = 32;

/// Outcome of one via request. Costs are integer cost points, as in
/// WebPathSummaryState (a walked tile is kWebPathWalkCostPoints).
struct WebWalkerViaResult {
    uint32_t structSize = sizeof(WebWalkerViaResult);
    uint32_t apiVersion = kWebWalkerViaApiVersion;
    uint64_t requestId = 0;
    uint32_t phase = TitanPluginSdk::WebPathPhase::None;
    /// WebPathResult. Exact and PartialWithinThreeTiles both carry an answer.
    uint32_t result = TitanPluginSdk::WebPathResult::None;
    /// Index into the request's via points; -1 when there is no answer.
    int32_t viaIndex = -1;
    uint32_t costToVia = 0;
    uint32_t costOnward = 0;
    /// costToVia + costOnward.
    uint32_t totalCost = 0;
    uint32_t exploredNodes = 0;
    uint32_t elapsedMs = 0;
    /// The tile the first leg ends on: the via point itself, or the nearest
    /// tile to it that can be stood on (within three tiles).
    TitanPluginSdk::WorldPointState reachedVia{};
    char message[TitanPluginSdk::kWebPathMessageCapacity] = {};
};

/// The service table. Every callback returns 1 on success and 0 on rejection.
/// They may be called from any thread. Input pointers are borrowed for the
/// duration of the call; output storage belongs to the caller.
struct WebWalkerViaV1 {
    uint32_t structSize = sizeof(WebWalkerViaV1);
    uint32_t apiVersion = kWebWalkerViaApiVersion;
    void* userData = nullptr;

    /// Queue one request. `request` is an ordinary path request: its start
    /// (or the local player), destination and options mean what they mean to
    /// `webPathSubmit`; only global route space is supported. `viaPoints`
    /// names between 1 and kWebWalkerViaMaxPoints canonical tiles.
    uint8_t (*submit)(
        void* userData,
        const TitanPluginSdk::WebPathRequestState* request,
        const TitanPluginSdk::WorldPointState* viaPoints,
        uint32_t viaPointCount,
        uint64_t* outRequestId) = nullptr;
    uint8_t (*poll)(
        void* userData, uint64_t requestId,
        WebWalkerViaResult* outResult) = nullptr;
    uint8_t (*cancel)(void* userData, uint64_t requestId) = nullptr;
    /// Forget a request. Every submitted request must be released.
    uint8_t (*release)(void* userData, uint64_t requestId) = nullptr;
};

static_assert(std::is_standard_layout_v<WebWalkerViaResult>);
static_assert(std::is_trivially_copyable_v<WebWalkerViaResult>);
static_assert(std::is_standard_layout_v<WebWalkerViaV1>);
static_assert(std::is_trivially_copyable_v<WebWalkerViaV1>);
static_assert(sizeof(WebWalkerViaResult) == 64 + TitanPluginSdk::kWebPathMessageCapacity);
static_assert(offsetof(WebWalkerViaResult, viaIndex) == 24);
static_assert(offsetof(WebWalkerViaResult, reachedVia) == 48);
static_assert(sizeof(WebWalkerViaV1) == 48);
static_assert(offsetof(WebWalkerViaV1, submit) == 16);

/// Validate a resolved table before use. Appended fields are allowed; the
/// complete V1 prefix is mandatory.
inline bool validWebWalkerViaV1(const WebWalkerViaV1* service) noexcept {
    return service
        && service->structSize >= sizeof(WebWalkerViaV1)
        && service->apiVersion == kWebWalkerViaApiVersion
        && service->submit && service->poll
        && service->cancel && service->release;
}

}  // namespace titan
