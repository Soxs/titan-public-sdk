/// @file titan/cache_files.h
/// @brief Owned copies of files in the game's own local cache.
#pragma once

#include "detail/cache_file_abi.h"
#include "detail/host.h"

#include <cstdint>
#include <utility>
#include <vector>

namespace titan {

using CacheFileStatus = TitanPluginSdk::CacheFileStatus;

struct CacheFileResult {
    CacheFileStatus status = CacheFileStatus::Unsupported;
    std::vector<uint8_t> bytes;
    bool ready() const { return status == CacheFileStatus::Ready; }
};

struct CacheFileIdsResult {
    CacheFileStatus status = CacheFileStatus::Unsupported;
    std::vector<int32_t> ids;
    bool ready() const { return status == CacheFileStatus::Ready; }
};

namespace cacheFiles {

inline bool available() {
    const auto* host = detail::host();
    return host && host->copyCacheFile && host->copyCacheFileIds;
}

inline CacheFileResult read(const TitanPluginSdk::HostApi* host, int32_t index, int32_t group, int32_t file) {
    if (index < 0 || index > TitanPluginSdk::kCacheMaxIndex || group < 0 || file < 0) return {CacheFileStatus::Invalid, {}};
    if (!host || !host->copyCacheFile) return {};
    uint32_t size = 0;
    auto status = host->copyCacheFile(index, group, file, nullptr, 0, &size);
    for (int attempt = 0; attempt < 2 && status == CacheFileStatus::BufferTooSmall; ++attempt) {
        if (!size || size > TitanPluginSdk::kCacheFileMaxBytes) return {CacheFileStatus::Failed, {}};
        std::vector<uint8_t> bytes(size);
        size = 0;
        status = host->copyCacheFile(index, group, file, bytes.data(), static_cast<uint32_t>(bytes.size()), &size);
        if (status != CacheFileStatus::Ready) continue;
        if (size > bytes.size()) return {CacheFileStatus::Failed, {}};
        bytes.resize(size);
        return {status, std::move(bytes)};
    }
    return {status, {}};
}

/// The decompressed bytes of one file, such as index 8 for sprites, 12 for
/// client scripts or 2 for configuration. Any thread may call this. It reads
/// the disk, so keep what you reuse. NotReady and Failed may succeed later;
/// Absent means that the game's reference table lists no such file.
inline CacheFileResult read(int32_t index, int32_t group, int32_t file = 0) {
    return read(detail::host(), index, group, file);
}

inline CacheFileIdsResult fileIds(const TitanPluginSdk::HostApi* host, int32_t index, int32_t group) {
    if (index < 0 || index > TitanPluginSdk::kCacheMaxIndex || group < 0) return {CacheFileStatus::Invalid, {}};
    if (!host || !host->copyCacheFileIds) return {};
    uint32_t count = 0;
    auto status = host->copyCacheFileIds(index, group, nullptr, 0, &count);
    for (int attempt = 0; attempt < 2 && status == CacheFileStatus::BufferTooSmall; ++attempt) {
        if (!count || count > TitanPluginSdk::kCacheGroupMaxFiles) return {CacheFileStatus::Failed, {}};
        std::vector<int32_t> ids(count);
        count = 0;
        status = host->copyCacheFileIds(index, group, ids.data(), static_cast<uint32_t>(ids.size()), &count);
        if (status != CacheFileStatus::Ready) continue;
        if (count > ids.size()) return {CacheFileStatus::Failed, {}};
        ids.resize(count);
        return {status, std::move(ids)};
    }
    return {status, {}};
}

/// The file IDs in one group, in ascending order.
inline CacheFileIdsResult fileIds(int32_t index, int32_t group) { return fileIds(detail::host(), index, group); }

} // namespace cacheFiles
} // namespace titan
