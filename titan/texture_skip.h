#pragma once

#include <cstdint>

namespace TitanPluginSdk {
// SDK 150. Scalar result; no native payload record layout changes.
enum class TextureSkippingError : uint32_t {
    None = 0, Unavailable, InstallFailed, RemoveFailed,
    ProviderReadFailed, CacheResetFailed, DirtyWriteFailed, ShuttingDown
};
}
namespace titan { using TextureSkippingError = TitanPluginSdk::TextureSkippingError; }
