#pragma once
#include <cstdint>
#include <type_traits>

namespace TitanPluginSdk {
/// Snapshot result; unavailable never means a valid frame zero or empty override.
enum class ActorDataStatus : uint8_t {
    Unavailable = 0, InvalidActor = 1, Unreadable = 2,
    Inactive = 3, None = 3, Active = 4, BufferTooSmall = 5, WrongThread = 6
};
enum class AnimationSequenceMode : uint8_t { Unknown = 0, Classic = 1, Cached = 2 };
/// Retained identity: all fields must still match the live world-view slot.
struct ActorDataIdentity {
    uint64_t entityPtr = 0;
    uint64_t worldViewPtr = 0;
    int32_t worldViewId = -1;
    int32_t hashIndex = -1;
    int32_t entityType = 0;
    uint32_t reserved = 0;
};
struct ActorAnimationProgressState {
    ActorDataStatus status = ActorDataStatus::Unavailable;
    AnimationSequenceMode mode = AnimationSequenceMode::Unknown;
    uint8_t frameCycleAvailable = 0;
    uint8_t reserved = 0;
    int32_t animationId = -1;
    int32_t frame = -1;
    int32_t frameCycle = 0;
    int32_t loopCount = 0;
    int32_t delay = 0;
};
struct NpcModelOverridesState {
    ActorDataStatus status = ActorDataStatus::Unavailable;
    uint8_t reserved[3]{};
    uint32_t modelCount = 0;
};
/// Defensive allocation limit, not a game-memory layout constant.
inline constexpr uint32_t kMaxNpcModelOverrideIds = 4096;
static_assert(sizeof(ActorDataIdentity) == 32 && alignof(ActorDataIdentity) == 8);
static_assert(sizeof(ActorAnimationProgressState) == 24 && alignof(ActorAnimationProgressState) == 4);
static_assert(sizeof(NpcModelOverridesState) == 8 && alignof(NpcModelOverridesState) == 4);
static_assert(std::is_trivially_copyable_v<ActorDataIdentity>);
static_assert(std::is_trivially_copyable_v<ActorAnimationProgressState>);
static_assert(std::is_trivially_copyable_v<NpcModelOverridesState>);
} // namespace TitanPluginSdk
