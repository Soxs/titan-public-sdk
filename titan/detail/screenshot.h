#pragma once
#include "abi.h"
#include <cstdint>
#include <optional>
#include <type_traits>
#include <vector>

namespace titan::detail {
/// Size-query then copy of one Ready screenshot's PNG bytes. Never returns a
/// partial image: a size that moves between the two calls is retried, and
/// anything past the ABI cap fails closed. Shared by the C++ facade, the
/// QuickJS binding, and the Java bridge so the three cannot disagree about the
/// copy contract. Storage stays in the caller's CRT.
template<class Copy>
    requires std::is_invocable_r_v<uint8_t, Copy, uint64_t, uint8_t*, uint32_t, uint32_t*>
std::optional<std::vector<uint8_t>> copyScreenshotPng(Copy&& copy, uint64_t requestId) noexcept {
    if (requestId == 0) return std::nullopt;
    try {
        uint32_t required = 0;
        if (!copy(requestId, nullptr, 0, &required)) return std::nullopt;
        for (int attempt = 0; attempt < 3; ++attempt) {
            if (required == 0 || required > TitanPluginSdk::kScreenshotMaxPngBytes) return std::nullopt;
            std::vector<uint8_t> png(required);
            uint32_t written = 0;
            if (!copy(requestId, png.data(), required, &written)) return std::nullopt;
            if (written == required) return png;
            // The host reports the true size when the buffer was short; try
            // once more at that size rather than trusting a truncated copy.
            if (written < required) return std::nullopt;
            required = written;
        }
    } catch (...) {}
    return std::nullopt;
}

inline std::optional<std::vector<uint8_t>> copyScreenshotPng(const TitanPluginSdk::HostApi* api,
                                                             uint64_t requestId) noexcept {
    if (!api || api->sdkVersion < 131 || !api->screenshotCopyPng) return std::nullopt;
    return copyScreenshotPng(api->screenshotCopyPng, requestId);
}
}  // namespace titan::detail
