/// @file titan/account_profiles.h
/// @brief Versioned, POD-only Account Profiles service contract.
///
/// The service is owned by the native `account_profiles` plugin and is
/// published for that plugin instance's complete load lifetime.  Consumers
/// provide all output storage; no STL value, exception, callback, credential,
/// or language-runtime object crosses the DLL boundary.

#pragma once

#include "detail/account_profiles_credentials.h"
#include "plugins.h"

#include <cstddef>
#include <cstdint>
#include <type_traits>

namespace titan::account_profiles {

inline constexpr const char* kServiceId = "titan.account_profiles.v1";
inline constexpr std::uint32_t kApiVersion = 1;
inline constexpr const char* kSelectionServiceId =
    "titan.account_profiles.selection.v1";
inline constexpr std::uint32_t kSelectionApiVersion = 1;
inline constexpr std::size_t kProfileIdCapacity = 64;
inline constexpr std::size_t kProfileLabelCapacity = 128;
inline constexpr std::size_t kProxyLabelCapacity = 128;
inline constexpr std::size_t kActivationMessageCapacity = 192;

enum class VaultState : std::uint32_t {
    Missing = 0,
    Locked,
    Ready,
    Busy,
    Corrupt,
    UnknownVersion,
    IoError,
};

enum class ProfileKind : std::uint32_t {
    Standard = 0,
    Jagex = 1,
};

enum ProfileReadiness : std::uint32_t {
    CredentialsPresent = 1u << 0,
    ProxyResolved       = 1u << 1,
    LoginSupported      = 1u << 2,
};

enum class ActivationPhase : std::uint32_t {
    Pending = 0,
    CredentialsApplied,
    Submitted,
    Succeeded,
    Failed,
    Cancelled,
};

/// Sanitized terminal result. `None` is used while an operation is pending.
enum class ActivationResult : std::uint32_t {
    None = 0,
    Succeeded,
    VaultLocked,
    ProfileMissing,
    RevisionChanged,
    MissingProxy,
    ProxyFailed,
    CredentialsRejectedOrExpired,
    Requires2FA,
    RequiresUserAction,
    Unsupported,
    TimedOut,
    Cancelled,
};

struct Status {
    std::uint32_t structSize = sizeof(Status);
    std::uint32_t apiVersion = kApiVersion;
    VaultState state = VaultState::Missing;
    std::uint32_t revision = 0;
    std::uint32_t profileCount = 0;
    std::uint32_t reserved = 0;
    char activeProfileId[kProfileIdCapacity]{};
    char stagedProfileId[kProfileIdCapacity]{};
};

/// One non-secret row in an immutable snapshot copied into caller storage.
struct ProfileSnapshot {
    std::uint32_t structSize = sizeof(ProfileSnapshot);
    std::uint32_t apiVersion = kApiVersion;
    ProfileKind kind = ProfileKind::Standard;
    std::uint32_t readiness = 0;
    char profileId[kProfileIdCapacity]{};
    char label[kProfileLabelCapacity]{};
    char proxyId[kProfileIdCapacity]{};
    char proxyLabel[kProxyLabelCapacity]{};
};

struct ActivationSnapshot {
    std::uint32_t structSize = sizeof(ActivationSnapshot);
    std::uint32_t apiVersion = kApiVersion;
    std::uint64_t token = 0;
    std::uint32_t vaultRevision = 0;
    ActivationPhase phase = ActivationPhase::Pending;
    ActivationResult result = ActivationResult::None;
    std::uint32_t reserved = 0;
    char profileId[kProfileIdCapacity]{};
    char message[kActivationMessageCapacity]{};
};

/// Sanitized UI selection state. This is a separate additive service so the
/// established activation service keeps its exact v1 ABI. `generation`
/// advances for every accepted Account Profiles "Use" action, including when
/// the same profile is selected again.
struct SelectionSnapshot {
    std::uint32_t structSize = sizeof(SelectionSnapshot);
    std::uint32_t apiVersion = kSelectionApiVersion;
    std::uint64_t generation = 0;
    char profileId[kProfileIdCapacity]{};
};

/// Versioned service table. Every function is noexcept at the ABI boundary and
/// returns zero when the request or output record is invalid.
struct ServiceV1 {
    std::uint32_t structSize = sizeof(ServiceV1);
    std::uint32_t apiVersion = kApiVersion;
    void* context = nullptr;

    std::uint8_t (*getStatus)(void* context, Status* out) = nullptr;
    std::uint8_t (*unlockExisting)(void* context, const char* password,
                                   std::uint32_t passwordLength,
                                   Status* out) = nullptr;
    std::uint8_t (*getSnapshot)(void* context, ProfileSnapshot* outProfiles,
                                std::uint32_t capacity,
                                std::uint32_t* outCount,
                                std::uint32_t* outRevision) = nullptr;
    std::uint8_t (*beginActivation)(void* context, const char* profileId,
                                    std::uint64_t* outToken) = nullptr;
    std::uint8_t (*pollActivation)(void* context, std::uint64_t token,
                                   ActivationSnapshot* out) = nullptr;
    std::uint8_t (*submitActivation)(void* context, std::uint64_t token) = nullptr;
    std::uint8_t (*cancelActivation)(void* context, std::uint64_t token) = nullptr;
};

struct SelectionServiceV1 {
    std::uint32_t structSize = sizeof(SelectionServiceV1);
    std::uint32_t apiVersion = kSelectionApiVersion;
    void* context = nullptr;

    std::uint8_t (*getSelection)(void* context,
                                 SelectionSnapshot* out) = nullptr;
};

static_assert(std::is_standard_layout_v<Status> &&
              std::is_trivially_copyable_v<Status>);
static_assert(std::is_standard_layout_v<ProfileSnapshot> &&
              std::is_trivially_copyable_v<ProfileSnapshot>);
static_assert(std::is_standard_layout_v<ActivationSnapshot> &&
              std::is_trivially_copyable_v<ActivationSnapshot>);
static_assert(std::is_standard_layout_v<ServiceV1> &&
              std::is_trivially_copyable_v<ServiceV1>);
static_assert(std::is_standard_layout_v<SelectionSnapshot> &&
              std::is_trivially_copyable_v<SelectionSnapshot>);
static_assert(std::is_standard_layout_v<SelectionServiceV1> &&
              std::is_trivially_copyable_v<SelectionServiceV1>);

inline titan::ServiceRef<ServiceV1> service() {
    auto value = titan::service<ServiceV1>(kServiceId);
    if (!value || value->apiVersion != kApiVersion ||
        value->structSize < sizeof(ServiceV1)) {
        return nullptr;
    }
    return value;
}

inline titan::ServiceRef<SelectionServiceV1> selectionService() {
    auto value = titan::service<SelectionServiceV1>(kSelectionServiceId);
    if (!value || value->apiVersion != kSelectionApiVersion ||
        value->structSize < sizeof(SelectionServiceV1)) {
        return nullptr;
    }
    return value;
}

inline titan::ServiceRef<CredentialServiceV1> credentialService() {
    auto value = titan::service<CredentialServiceV1>(kCredentialServiceId);
    if (!value || value->apiVersion != kCredentialApiVersion ||
        value->structSize < sizeof(CredentialServiceV1)) {
        return nullptr;
    }
    return value;
}

}  // namespace titan::account_profiles
