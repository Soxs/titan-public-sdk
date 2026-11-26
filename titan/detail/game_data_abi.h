#pragma once

#include <cstdint>

namespace TitanPluginSdk {

// Named reads and changes of the game. titan/game_data.h has the ones with a type of their own.
enum class GameDataStatus : uint32_t {
    Ready = 0,
    NotReady = 1,     // Nothing to answer with now, such as before the account is logged in.
    Invalid = 2,      // Not a request of this name.
    Unsupported = 3,  // This client or this build of the game has no such operation.
    WrongThread = 4,  // Not the game thread.
    BufferTooSmall = 5
};

inline constexpr uint32_t kGameDataMaxNumbers = 4096;
inline constexpr uint32_t kGameDataMaxTexts = 64;
inline constexpr uint32_t kGameDataMaxTextBytes = 4096;
inline constexpr uint32_t kGameDataMaxBytes = 4u * 1024u * 1024u;
static_assert(sizeof(GameDataStatus) == 4);

} // namespace TitanPluginSdk
