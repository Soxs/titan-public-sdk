#pragma once
#include <cstdint>

namespace TitanPluginSdk {
/// SDK 156. Capability/installation status; individual unsupported poses use native rendering.
enum class AnimationSmoothingError : uint32_t {
    None = 0, Unavailable = 1, InstallFailed = 2, ShuttingDown = 3
};
}
namespace titan { using AnimationSmoothingError = TitanPluginSdk::AnimationSmoothingError; }
