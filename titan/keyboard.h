/// @file titan/keyboard.h
/// @brief SDK facade for keyboard input injection.
///
/// Thin inline wrapper over `IBackend` keyboard virtuals. Lets SDK-level
/// code (`shared/titan/utils/*.h`) send keyboard input without depending
/// on `client/actions/keyboard.h`.
///
/// JS plugins already have `titan.keyboard` wired through HostApi; this
/// header is the C++ equivalent for native plugin DLLs and SDK utilities.
///
/// Added in SDK 44.

#pragma once

#include "detail/backend.h"
#include <cstdint>

namespace titan {
namespace keyboard {

inline bool sendString(const char* utf8) {
    auto* b = detail::backend();
    return b ? b->sendKeyboardString(utf8) != 0 : false;
}

inline bool sendKey(int32_t key, uint32_t mods = 0) {
    auto* b = detail::backend();
    return b ? b->sendKeyboardKey(key, mods) != 0 : false;
}

inline bool typeString(const char* utf8, int32_t minDelayMs = 60, int32_t maxDelayMs = 120) {
    auto* b = detail::backend();
    return b ? b->typeKeyboardString(utf8, minDelayMs, maxDelayMs) != 0 : false;
}

inline void cancelTypeString() {
    auto* b = detail::backend();
    if (b) b->cancelKeyboardType();
}

inline bool isTyping() {
    auto* b = detail::backend();
    return b ? b->isKeyboardTyping() != 0 : false;
}

}  // namespace keyboard
}  // namespace titan
