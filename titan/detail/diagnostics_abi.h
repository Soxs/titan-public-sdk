#pragma once
#include <cstdint>
#include <type_traits>

namespace titan::diagnostics {
enum class Status : std::uint32_t { Unavailable = 0, Pending = 1, Saved = 2, Retry = 3,
                                    Auth = 4, Rejected = 5, Cancelled = 6 };
namespace detail {
inline constexpr const char* kServiceId = "titan.plugin-diagnostics.v1";
inline constexpr std::uint32_t kMaxGzipBytes = 16 * 1024 * 1024;
// Permanent host-owned optional capability; no existing native ABI records change.
// Calls are thread-safe and never throw across the DLL boundary. The host copies submit's
// bounded inputs before returning. owner is the SDK Plugin identity, never a chosen slug.
struct TransportV1 {
    std::uint32_t size = sizeof(TransportV1);
    std::uint32_t version = 1;
    std::uint64_t (*submit)(const void* owner, const char* name, const char* sha256,
                            const std::uint8_t* gzip, std::uint32_t bytes) = nullptr;
    std::uint32_t (*poll)(const void* owner, std::uint64_t ticket) = nullptr;
    void (*cancel)(const void* owner, std::uint64_t ticket) = nullptr;
    void (*release)(const void* owner, std::uint64_t ticket) = nullptr;
};
static_assert(std::is_standard_layout_v<TransportV1>);
static_assert(sizeof(TransportV1) == 40);
} // namespace detail
} // namespace titan::diagnostics
