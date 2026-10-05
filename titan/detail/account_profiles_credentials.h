#pragma once

#include <cstdint>
#include <type_traits>

namespace titan::account_profiles {

inline constexpr const char* kCredentialServiceId =
    "titan.account_profiles.credentials.v1";
inline constexpr std::uint32_t kCredentialApiVersion = 1;

struct CredentialServiceV1 {
    std::uint32_t structSize = sizeof(CredentialServiceV1);
    std::uint32_t apiVersion = kCredentialApiVersion;
    void* context = nullptr;

    std::uint8_t (*stageCredentials)(void* context,
                                     const char* profileLabel) = nullptr;
};

static_assert(std::is_standard_layout_v<CredentialServiceV1> &&
              std::is_trivially_copyable_v<CredentialServiceV1>);

}  // namespace titan::account_profiles
