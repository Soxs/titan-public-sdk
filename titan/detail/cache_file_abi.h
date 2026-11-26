#pragma once

#include <cstdint>

namespace TitanPluginSdk {

// Files of the running game's own local cache. Every byte returned was checked
// against that cache's reference table when it was read.
enum class CacheFileStatus : uint32_t {
    Ready = 0,
    Absent = 1,      // The reference table lists no such group or file.
    NotReady = 2,    // The cache is not open yet. Ask again later.
    Invalid = 3,
    Unsupported = 4,
    Failed = 5,      // Listed, but not stored, damaged, encrypted or too large.
    BufferTooSmall = 6
};

inline constexpr int32_t kCacheMaxIndex = 254;
inline constexpr uint32_t kCacheFileMaxBytes = 16u * 1024u * 1024u;
inline constexpr uint32_t kCacheGroupMaxFiles = 1024u * 1024u;
static_assert(sizeof(CacheFileStatus) == 4);

} // namespace TitanPluginSdk
