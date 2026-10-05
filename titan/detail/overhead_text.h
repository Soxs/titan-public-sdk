#pragma once
#include "abi.h"
#include <optional>
#include <string>
#include <type_traits>

namespace titan::detail {
/// Query/retry a variable-size read; never returns a partial string. Storage
/// and allocation failures remain local to the caller's static CRT.
template<class Read>
    requires std::is_invocable_r_v<uint8_t, Read, uint64_t, char*, uint64_t, uint64_t*>
std::optional<std::string> readOverheadText(Read&& read, uint64_t entityPtr) noexcept {
    try {
        uint64_t length = 0;
        uint8_t status = read(entityPtr, nullptr, 0, &length);
        if (status == TitanPluginSdk::OverheadTextReadResult::Success && length == 0)
            return std::string{};
        if (status != TitanPluginSdk::OverheadTextReadResult::BufferTooSmall) return std::nullopt;
        for (int attempt = 0; attempt < 3; ++attempt) {
            std::string text;
            if (length > text.max_size()) return std::nullopt;
            text.resize(static_cast<size_t>(length));
            uint64_t actual = 0;
            status = read(entityPtr, text.data(), length, &actual);
            if (status == TitanPluginSdk::OverheadTextReadResult::Success) {
                if (actual > length) return std::nullopt;
                text.resize(static_cast<size_t>(actual));
                return text;
            }
            if (status != TitanPluginSdk::OverheadTextReadResult::BufferTooSmall || actual <= length)
                return std::nullopt;
            length = actual;
        }
    } catch (...) {}
    return std::nullopt;
}
inline std::optional<std::string> readOverheadText(const TitanPluginSdk::HostApi* api,
                                                 uint64_t entityPtr) noexcept {
    if (!api || api->sdkVersion < 127 || !api->getActorOverheadText) return std::nullopt;
    return readOverheadText(api->getActorOverheadText, entityPtr);
}
}
