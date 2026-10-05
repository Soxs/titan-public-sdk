/// @file titan/web_walker_provider.h
/// @brief Fixed-ABI provider contracts for the Titan web walker.
///
/// Normal plugins should use <titan/web_walker.h>. The host validates the
/// service owner before selecting a provider. V1 is the legacy planning-only
/// DEV override. V2 is the complete extraction boundary: planning and execution
/// belong to the same provider, eventually one public native plugin. The
/// temporary client-owned V2 adapter preserves current behaviour during that
/// extraction. No C++ objects cross the DLL boundary.

#pragma once

#include "detail/abi.h"

#include <cstddef>
#include <cstdint>
#include <type_traits>

namespace titan {

inline constexpr const char* kWebWalkerProviderServiceId =
    "titan.web_walker.provider.v1";
inline constexpr const char* kWebWalkerProviderPluginId =
    "web_walker_provider";
inline constexpr uint32_t kWebWalkerProviderApiVersion = 1;

struct WebWalkerProviderV1 {
    uint32_t structSize = sizeof(WebWalkerProviderV1);
    uint32_t apiVersion = kWebWalkerProviderApiVersion;
    void* userData = nullptr;

    uint8_t (*submit)(
        void* userData,
        const TitanPluginSdk::WebPathRequestState* request,
        const TitanPluginSdk::WorldPointState* forbiddenTiles,
        uint32_t forbiddenTileCount,
        uint64_t* outProviderRequestId) = nullptr;
    uint8_t (*poll)(
        void* userData, uint64_t providerRequestId,
        TitanPluginSdk::WebPathSummaryState* outSummary) = nullptr;
    /// Size queries and populated copies must report no more than
    /// TitanPluginSdk::kWebPathMaxSteps records. Summary and step cost fields
    /// are integer points; see kWebPathCostPointsPerWhole.
    uint8_t (*copySteps)(
        void* userData, uint64_t providerRequestId,
        TitanPluginSdk::WebPathStepState* outSteps,
        uint32_t capacity, uint32_t* outCount) = nullptr;
    uint8_t (*cancel)(void* userData, uint64_t providerRequestId) = nullptr;
    uint8_t (*release)(void* userData, uint64_t providerRequestId) = nullptr;
};

inline constexpr const char* kWebWalkerProviderV2ServiceId =
    "titan.web_walker.provider.v2";
inline constexpr uint32_t kWebWalkerProviderV2ApiVersion = 2;

/// Complete walker service. Providers publish every callback together; a
/// planning-only implementation must continue to use V1. The host holds a
/// service lease while calling these functions and routes each retained handle
/// back to the generation that created it. IDs are opaque, provider-local
/// values: callers must not mix handles from different provider generations.
///
/// Requests and output records use the existing versioned WebPath/WebWalk POD
/// layouts. Input pointers are borrowed for the duration of the call; providers
/// copy any input they retain. Output storage belongs to the caller. Neither
/// allocator ownership nor exceptions may cross this boundary. Callbacks return
/// 1 on success and 0 on rejection/failure, matching the existing HostApi calls.
///
/// Planning calls and walk start/status/cancel/release may be called from any
/// thread. walkAdvance is game-thread-only and advances a ManualTick session.
/// The host calls walkTick once per game tick AFTER normal plugin game-tick
/// callbacks have finished, preserving the order in which callers can start or
/// cancel a walk before execution. The provider owns execution entirely and
/// must not also advance automatic sessions from its normal onGameTick hook.
struct WebWalkerProviderV2 {
    uint32_t structSize = sizeof(WebWalkerProviderV2);
    uint32_t apiVersion = kWebWalkerProviderV2ApiVersion;
    void* userData = nullptr;

    uint8_t (*pathSubmit)(
        void* userData,
        const TitanPluginSdk::WebPathRequestState* request,
        const TitanPluginSdk::WorldPointState* forbiddenTiles,
        uint32_t forbiddenTileCount,
        uint64_t* outProviderRequestId) = nullptr;
    uint8_t (*pathPoll)(
        void* userData, uint64_t providerRequestId,
        TitanPluginSdk::WebPathSummaryState* outSummary) = nullptr;
    /// Size queries and populated copies must report no more than
    /// TitanPluginSdk::kWebPathMaxSteps records. Costs are integer points.
    uint8_t (*pathCopySteps)(
        void* userData, uint64_t providerRequestId,
        TitanPluginSdk::WebPathStepState* outSteps,
        uint32_t capacity, uint32_t* outCount) = nullptr;
    /// UTF-8 JSON for one step. outRequired includes the NUL terminator. A
    /// null or short output buffer is a size query and must not be written.
    uint8_t (*pathCopyStepPayload)(
        void* userData, uint64_t providerRequestId, uint32_t stepIndex,
        char* outUtf8, uint32_t capacity, uint32_t* outRequired) = nullptr;
    uint8_t (*pathCancel)(
        void* userData, uint64_t providerRequestId) = nullptr;
    uint8_t (*pathRelease)(
        void* userData, uint64_t providerRequestId) = nullptr;

    uint8_t (*walkStart)(
        void* userData,
        const TitanPluginSdk::WebWalkRequestState* request,
        const TitanPluginSdk::WorldPointState* forbiddenTiles,
        uint32_t forbiddenTileCount,
        uint64_t* outProviderWalkId) = nullptr;
    /// Handle 0 addresses the latest retained walk, as in HostApi.
    uint8_t (*walkStatus)(
        void* userData, uint64_t providerWalkId,
        TitanPluginSdk::WebWalkStatusState* outStatus) = nullptr;
    /// Handle 0 addresses the active walk.
    uint8_t (*walkCancel)(void* userData, uint64_t providerWalkId) = nullptr;
    uint8_t (*walkRelease)(void* userData, uint64_t providerWalkId) = nullptr;
    /// Handle 0 addresses the active walk. Game thread only.
    uint8_t (*walkAdvance)(void* userData, uint64_t providerWalkId) = nullptr;
    /// Host-driven automatic pump, game thread only, after plugin tick fanout.
    /// Advance automatic sessions only; ManualTick sessions remain controlled
    /// by walkAdvance. An idle/manual-only provider still returns success.
    uint8_t (*walkTick)(void* userData, int32_t tick) = nullptr;
};

static_assert(std::is_standard_layout_v<WebWalkerProviderV2>);
static_assert(std::is_trivially_copyable_v<WebWalkerProviderV2>);
static_assert(sizeof(WebWalkerProviderV1) == 56);
static_assert(sizeof(WebWalkerProviderV2) == 112);
static_assert(offsetof(WebWalkerProviderV2, pathSubmit) == 16);
static_assert(offsetof(WebWalkerProviderV2, walkStart) == 64);
static_assert(offsetof(WebWalkerProviderV2, walkTick) == 104);

/// Validate a readable service table before dispatch. Owner authorization and
/// the generation lease are the caller's responsibility. Future appended fields
/// are allowed, but the complete V2 prefix is mandatory.
inline bool validWebWalkerProviderV2(
        const WebWalkerProviderV2* provider) noexcept {
    return provider
        && provider->structSize >= sizeof(WebWalkerProviderV2)
        && provider->apiVersion == kWebWalkerProviderV2ApiVersion
        && provider->pathSubmit && provider->pathPoll && provider->pathCopySteps
        && provider->pathCopyStepPayload && provider->pathCancel
        && provider->pathRelease && provider->walkStart && provider->walkStatus
        && provider->walkCancel && provider->walkRelease && provider->walkAdvance
        && provider->walkTick;
}

}  // namespace titan
