/// @file titan/world_view.h
/// @brief Public WorldView identifiers used by WorldView-aware APIs.

#pragma once

#include <cstdint>

namespace titan {

struct WorldView {
    static constexpr int32_t CURRENT = -1;
    static constexpr int32_t TOP_LEVEL = 0;
};

}  // namespace titan
