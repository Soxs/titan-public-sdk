/// @file titan/web_walker_provider.h
/// @brief Fixed-ABI provider contracts for the Titan web walker.
///
/// Normal plugins should use <titan/web_walker.h>. The host validates the
/// service owner before selecting a provider. V1 is the legacy planning-only
/// DEV override. V2 is the complete extraction boundary: planning and execution
/// belong to the same provider, eventually one public native plugin. The
/// temporary client-owned V2 adapter preserves current behaviour during that
/// extraction. V3 adds the provider's full route and walk options; plugins
/// call it directly until the host routes it. No C++ objects cross the DLL
/// boundary.

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

inline constexpr const char* kWebWalkerProviderV3ServiceId =
    "titan.web_walker.provider.v3";
inline constexpr uint32_t kWebWalkerProviderV3ApiVersion = 3;
/// Most destinations, and most starts, one V3 path request may list.
inline constexpr uint32_t kWebPathV3MaxEndpoints = 4096;
/// Largest cost, in ABI points, a V3 request may add to one start or one
/// destination. It keeps every added cost far below the provider's cost range.
inline constexpr uint32_t kWebPathV3MaxExtraCost = 1'000'000;

/// How hard a V3 route keeps out of the Wilderness. Soft and Hard also make
/// every step inside it cost extra, so a route that has to be there leaves by
/// the shortest way.
namespace WebPathWildernessAvoidance {
inline constexpr uint32_t None = 0;
/// Entering costs as much as a long detour, so a route crosses the
/// Wilderness only when that saves a lot.
inline constexpr uint32_t Soft = 1;
/// Entering is forbidden, unless a destination lies inside the Wilderness:
/// then entering costs as with Soft. A route starting inside can still leave.
inline constexpr uint32_t Hard = 2;
}  // namespace WebPathWildernessAvoidance

/// Route families a V3 path request may use, one bit each.
namespace WebPathTransport {
/// Doors, stairs, shortcuts, boats and the other catalogued objects. Without
/// it a route only walks and uses the families enabled below.
inline constexpr uint32_t CatalogTransports     = 1u << 0;
inline constexpr uint32_t GnomeGliders          = 1u << 1;
inline constexpr uint32_t FairyRings            = 1u << 2;
inline constexpr uint32_t SpiritTrees           = 1u << 3;
inline constexpr uint32_t Minecarts             = 1u << 4;
inline constexpr uint32_t Canoes                = 1u << 5;
inline constexpr uint32_t ShantayPass           = 1u << 6;
inline constexpr uint32_t Mushtrees             = 1u << 7;
inline constexpr uint32_t Quetzals              = 1u << 8;
inline constexpr uint32_t CharterShips          = 1u << 9;
inline constexpr uint32_t HotAirBalloons        = 1u << 10;
inline constexpr uint32_t MinigameTeleports     = 1u << 11;
/// Teleport items in the inventory, or withdrawn on a planned bank visit.
inline constexpr uint32_t ItemTeleports         = 1u << 12;
/// Teleport items rubbed or operated while worn.
inline constexpr uint32_t EquippedItemTeleports = 1u << 13;
inline constexpr uint32_t SpellTeleports        = 1u << 14;
/// Every way into the player's own house. Without it the house furniture
/// below is unreachable.
inline constexpr uint32_t HouseEntry            = 1u << 15;
inline constexpr uint32_t HouseMountedGlory     = 1u << 16;
inline constexpr uint32_t HouseDigsitePendant   = 1u << 17;
inline constexpr uint32_t HouseXericsTalisman   = 1u << 18;
inline constexpr uint32_t HouseJewelleryBox     = 1u << 19;
inline constexpr uint32_t HousePortalRoom       = 1u << 20;
inline constexpr uint32_t HousePortalNexus      = 1u << 21;

inline constexpr uint32_t All = (1u << 22) - 1;
/// The families TitanPluginSdk::WebPathOption::Default enables.
inline constexpr uint32_t Default = All & ~(CharterShips | MinigameTeleports);
}  // namespace WebPathTransport

namespace WebPathV3Flag {
/// Ignore `starts` and plan from the local player's tile.
inline constexpr uint32_t UseLocalPlayer          = 1u << 0;
/// Plan bank visits that withdraw what a route needs, such as fares, passes
/// and teleport items. Only bank contents the client has seen count.
inline constexpr uint32_t AllowBanking            = 1u << 1;
/// Also accept routes ending within three tiles of a destination, reported as
/// PartialWithinThreeTiles. Each tile short of it ranks the route as if it
/// walked 5,000 more tiles, so one wins only when no destination is reachable
/// without such a detour, counting costOffsets. Reported costs leave that out.
inline constexpr uint32_t PartialWithinThreeTiles = 1u << 2;

inline constexpr uint32_t Default = UseLocalPlayer | PartialWithinThreeTiles;
}  // namespace WebPathV3Flag

struct WebPathDestinationState {
    TitanPluginSdk::WorldPointState tile{};
    /// Points added when the route ends here, so a caller can rank
    /// destinations. At most kWebPathV3MaxExtraCost.
    uint32_t costOffset = 0;
    uint32_t reserved0 = 0;
};

struct WebPathStartState {
    TitanPluginSdk::WorldPointState tile{};
    /// Points added when the route starts here, such as the cost of getting
    /// there, so a caller can rank starts. At most kWebPathV3MaxExtraCost.
    uint32_t costOffset = 0;
    uint32_t reserved0 = 0;
};

/// Most cost zones one V3 path request may list.
inline constexpr uint32_t kWebPathV3MaxCostZones = 32;
/// A costPerTile at or above this forbids the zone: no walking step,
/// transport or teleport may enter it from outside. A route that starts
/// inside may still move within it, at this cost per tile, so it leaves by
/// the fewest tiles. It is also the most that overlapping zones add for one
/// tile.
inline constexpr uint32_t kWebPathCostZoneForbidden = 100'000;

/// An area a route pays extra to be in, so it goes round the area when that is
/// cheaper and crosses it when it is not, or an area it must not enter.
/// Overlapping zones add up.
struct WebPathCostZoneState {
    /// South-west tile, in the request's route space; the area runs `width`
    /// tiles east and `height` tiles north, each at least one.
    int32_t x = 0;
    int32_t y = 0;
    int32_t width = 0;
    int32_t height = 0;
    int32_t plane = 0;
    /// Points added for each walking step, transport or teleport that ends on
    /// a tile inside, so crossing more of the area costs more. From
    /// kWebPathCostZoneForbidden up, the zone is forbidden instead.
    uint32_t costPerTile = 0;
};

/// A V3 route request. The route runs between whichever start and destination
/// are cheapest, counting their costOffsets. Costs are ABI points, so a walked
/// tile costs kWebPathWalkCostPoints. Array pointers are borrowed for the call.
struct WebPathRequestV3State {
    uint32_t structSize = sizeof(WebPathRequestV3State);
    uint32_t apiVersion = kWebWalkerProviderV3ApiVersion;
    const WebPathDestinationState* destinations = nullptr;
    uint32_t destinationCount = 0;
    /// Alternative starts, read only without UseLocalPlayer.
    uint32_t startCount = 0;
    const WebPathStartState* starts = nullptr;
    uint32_t routeSpace = TitanPluginSdk::WebPathRouteSpace::Global;
    uint32_t flags = WebPathV3Flag::Default;
    uint32_t transports = WebPathTransport::Default;
    uint32_t timeoutMs = TitanPluginSdk::kWebPathDefaultTimeoutMs;
    uint32_t wildernessAvoidance = WebPathWildernessAvoidance::Hard;
    /// At most kWebPathV3MaxCostZones.
    uint32_t costZoneCount = 0;
    const WebPathCostZoneState* costZones = nullptr;
};

/// A V3 walk. Walks always plan from the local player: the path's starts must
/// be empty, and UseLocalPlayer is implied.
struct WebWalkRequestV3State {
    uint32_t structSize = sizeof(WebWalkRequestV3State);
    uint32_t apiVersion = kWebWalkerProviderV3ApiVersion;
    WebPathRequestV3State path{};
    uint32_t walkFlags = TitanPluginSdk::WebWalkFlag::Default;
    /// Chebyshev arrival tolerance around any destination; zero = exact tile.
    uint32_t arriveRadius = 0;
    /// Hard budget for the whole walk in game ticks; zero = unlimited.
    uint32_t maxDurationTicks = 0;
    /// Run energy percent, at most 100, held back for outrunning attackers
    /// under ManageRun. Run turns off near it and back on some points above
    /// it, and an attacked player runs regardless. Zero never turns run off.
    uint32_t runEnergyReserve = 0;
};

/// The provider's full planning and walking options, called by plugins
/// directly: the host routes only V2. Acquire it for each operation with
/// `titan::service<WebWalkerProviderV3>(*this, kWebWalkerProviderV3ServiceId)`,
/// check validWebWalkerProviderV3, and depend on kWebWalkerProviderPluginId.
/// Native C++ plugins only.
///
/// Callbacks keep V2's threading, ownership and return conventions, and
/// handles are shared with V2: one walk drives the player whichever version
/// started it. The host advances automatic walks of both versions through
/// V2's walkTick, so a V3 provider must also publish V2. As in V2, walk handle
/// 0 addresses the latest walk for walkStatus and walkCopySteps, and the
/// active walk for walkCancel and walkAdvance, whichever plugin started it.
struct WebWalkerProviderV3 {
    uint32_t structSize = sizeof(WebWalkerProviderV3);
    uint32_t apiVersion = kWebWalkerProviderV3ApiVersion;
    void* userData = nullptr;

    uint8_t (*pathSubmit)(
        void* userData, const WebPathRequestV3State* request,
        uint64_t* outProviderRequestId) = nullptr;
    /// A route that reaches any listed destination reports Exact.
    uint8_t (*pathPoll)(
        void* userData, uint64_t providerRequestId,
        TitanPluginSdk::WebPathSummaryState* outSummary) = nullptr;
    uint8_t (*pathCopySteps)(
        void* userData, uint64_t providerRequestId,
        TitanPluginSdk::WebPathStepState* outSteps,
        uint32_t capacity, uint32_t* outCount) = nullptr;
    uint8_t (*pathCopyStepPayload)(
        void* userData, uint64_t providerRequestId, uint32_t stepIndex,
        char* outUtf8, uint32_t capacity, uint32_t* outRequired) = nullptr;
    uint8_t (*pathCancel)(
        void* userData, uint64_t providerRequestId) = nullptr;
    uint8_t (*pathRelease)(
        void* userData, uint64_t providerRequestId) = nullptr;

    uint8_t (*walkStart)(
        void* userData, const WebWalkRequestV3State* request,
        uint64_t* outProviderWalkId) = nullptr;
    uint8_t (*walkStatus)(
        void* userData, uint64_t providerWalkId,
        TitanPluginSdk::WebWalkStatusState* outStatus) = nullptr;
    uint8_t (*walkCancel)(void* userData, uint64_t providerWalkId) = nullptr;
    uint8_t (*walkRelease)(void* userData, uint64_t providerWalkId) = nullptr;
    /// Game thread only.
    uint8_t (*walkAdvance)(void* userData, uint64_t providerWalkId) = nullptr;
    /// The route the walk follows, as pathCopySteps reports one; the status's
    /// currentStepIndex indexes it. While a walk plans its first route this is
    /// empty, and while it replans it keeps the previous route.
    uint8_t (*walkCopySteps)(
        void* userData, uint64_t providerWalkId,
        TitanPluginSdk::WebPathStepState* outSteps,
        uint32_t capacity, uint32_t* outCount) = nullptr;
};

static_assert(std::is_standard_layout_v<WebPathRequestV3State>);
static_assert(std::is_standard_layout_v<WebWalkRequestV3State>);
static_assert(std::is_standard_layout_v<WebWalkerProviderV3>);
static_assert(std::is_trivially_copyable_v<WebWalkerProviderV3>);
static_assert(sizeof(WebPathDestinationState) == 24);
static_assert(sizeof(WebPathStartState) == 24);
static_assert(sizeof(WebPathCostZoneState) == 24);
static_assert(sizeof(WebPathRequestV3State) == 64);
static_assert(sizeof(WebWalkRequestV3State) == 88);
static_assert(sizeof(WebWalkerProviderV3) == 112);
static_assert(offsetof(WebWalkerProviderV3, walkStart) == 64);

inline bool validWebWalkerProviderV3(
        const WebWalkerProviderV3* provider) noexcept {
    return provider
        && provider->structSize >= sizeof(WebWalkerProviderV3)
        && provider->apiVersion == kWebWalkerProviderV3ApiVersion
        && provider->pathSubmit && provider->pathPoll && provider->pathCopySteps
        && provider->pathCopyStepPayload && provider->pathCancel
        && provider->pathRelease && provider->walkStart && provider->walkStatus
        && provider->walkCancel && provider->walkRelease && provider->walkAdvance
        && provider->walkCopySteps;
}

}  // namespace titan
