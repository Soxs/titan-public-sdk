/// @file titan/utils/mouse.h
/// @brief Mouse and click-point helpers for action dispatch.

#pragma once

#include "../detail/abi.h"
#include "../detail/backend.h"
#include "../menu_action.h"
#include "../render.h"

#include <cstdint>
#include <optional>

namespace titan {
namespace utils {
namespace Mouse {

inline std::optional<ScreenPoint> resolveActionClickPoint(
        MenuAction::Id opcode,
        int32_t identifier,
        int32_t param0,
        int32_t param1,
        int32_t worldViewId = -1,
        int32_t targetPlane = -1,
        int32_t targetSizeX = 1,
        int32_t targetSizeY = 1,
        int32_t targetLayer = -1,
        uint64_t targetEntityPtr = 0,
        uint64_t targetPackedId = 0) {
    auto* b = ::titan::detail::backend();
    if (!b) return std::nullopt;

    TitanPluginSdk::ActionClickPointSpec action{};
    action.opcode = static_cast<uint32_t>(opcode);
    action.identifier = identifier;
    action.param0 = param0;
    action.param1 = param1;
    action.worldViewId = worldViewId;
    action.targetPlane = targetPlane;
    action.targetSizeX = targetSizeX;
    action.targetSizeY = targetSizeY;
    action.targetLayer = targetLayer;
    action.targetEntityPtr = targetEntityPtr;
    action.targetPackedId = targetPackedId;

    int32_t x = 0;
    int32_t y = 0;
    if (!b->resolveActionClickPoint(&action, &x, &y)) {
        return std::nullopt;
    }
    return ScreenPoint{x, y};
}

inline std::optional<ScreenPoint> resolveActionClickPoint(
        const MenuAction::Entry& entry) {
    return resolveActionClickPoint(
        entry.opcode,
        entry.identifier,
        entry.param0,
        entry.param1,
        entry.worldViewId,
        entry.targetPlane,
        entry.targetSizeX,
        entry.targetSizeY,
        entry.targetLayer,
        entry.targetEntityPtr,
        entry.targetPackedId);
}

}  // namespace Mouse
}  // namespace utils
}  // namespace titan
