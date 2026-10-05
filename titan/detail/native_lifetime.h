/// Native ABI v1 instance ownership. This layout and its semantics are frozen.
#pragma once

#include "native_abi.h"

namespace TitanPluginSdk::NativeAbi {

inline constexpr uint64_t kLifetimeInterfaceId = 0x544954414E4C0001ULL;

// All callbacks belong to the instance that received this table. A lease pins
// both the provider instance and its module until releaseLease. Do not retain
// the borrowed service pointer beyond its lease. Cleanup runs exactly once,
// including rejected/cancelled scheduling, before the owner may be destroyed.
struct LifetimeV1 {
    InterfaceHeader header{sizeof(LifetimeV1), 1};
    void* context = nullptr;
    uint8_t (*schedule)(void* context, uint32_t phase,
                        void (*run)(void*), void* userData,
                        void (*cleanup)(void*)) = nullptr;
    uint8_t (*publishService)(void* context, const void* instance,
                              const char* pluginId, const char* serviceId,
                              void* service) = nullptr;
    uint8_t (*acquireService)(void* context, const char* serviceId,
                              void** outService, void** outLease) = nullptr;
    void (*releaseLease)(void* context, void* lease) = nullptr;
    uint8_t (*acquireWork)(void* context, void** outLease) = nullptr;
};

static_assert(std::is_standard_layout_v<LifetimeV1>);
static_assert(sizeof(LifetimeV1) == 56);
static_assert(offsetof(LifetimeV1, header) == 0);
static_assert(offsetof(LifetimeV1, context) == 8);
static_assert(offsetof(LifetimeV1, schedule) == 16);
static_assert(offsetof(LifetimeV1, publishService) == 24);
static_assert(offsetof(LifetimeV1, acquireService) == 32);
static_assert(offsetof(LifetimeV1, releaseLease) == 40);
static_assert(offsetof(LifetimeV1, acquireWork) == 48);

inline const LifetimeV1* lifetime(const InterfaceProviderV1* provider) noexcept {
    if (!provider || provider->structSize < sizeof(InterfaceProviderV1) ||
        provider->abiVersion != kAbiVersion || !provider->queryInterface) return nullptr;
    const auto* header = provider->queryInterface(provider->context, kLifetimeInterfaceId, 1);
    if (!header || header->majorVersion != 1 || header->structSize < sizeof(LifetimeV1)) return nullptr;
    return reinterpret_cast<const LifetimeV1*>(header);
}

} // namespace TitanPluginSdk::NativeAbi
