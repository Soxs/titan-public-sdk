#pragma once
#include <cstdint>
#include <type_traits>

namespace titan::authorization::detail {
inline constexpr const char* kServiceId = "titan.plugin-authorization.v1";
inline constexpr std::uint32_t kMaxReplyBytes = 1024 * 1024;
// Optional permanent host capability, acquired through HostCoreV1::getHostService.
// No existing native record or function-table layout changes.
struct TransportV1 {
    std::uint32_t size = sizeof(TransportV1);
    std::uint32_t version = 1;
    std::uint64_t (*submit)(const void* owner, const char* operation, const char* json) = nullptr;
    std::uint32_t (*poll)(const void* owner, std::uint64_t ticket, char* out, std::uint32_t capacity) = nullptr;
    std::uint32_t (*context)(const void* owner, char* out, std::uint32_t capacity) = nullptr;
    void (*cancel)(const void* owner, std::uint64_t ticket) = nullptr;
};
static_assert(std::is_standard_layout_v<TransportV1>);
}
