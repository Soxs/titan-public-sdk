#pragma once

#include "abi.h"
#include <optional>

namespace titan::detail {
inline std::optional<bool> readFrameFocus(const TitanPluginSdk::HostApi* api) {
    if (!api || !api->frameFocus) return std::nullopt;
    const int32_t value = api->frameFocus();
    if (value != 0 && value != 1) return std::nullopt;
    return value == 1;
}
}
