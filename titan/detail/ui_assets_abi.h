#pragma once

#include <cstdint>

namespace TitanPluginSdk {

enum class UiAssetStatus : uint32_t {
    Ready = 0, Unsupported = 1, Invalid = 2, Failed = 3, BufferTooSmall = 4
};

// A resource bound, independent of the size of a particular bundled font.
inline constexpr uint32_t kUiAssetMaxFontBytes = 1024u * 1024u;
static_assert(sizeof(UiAssetStatus) == 4);

} // namespace TitanPluginSdk
