#pragma once
#include "abi.h"
#include <optional>
#include <utility>
#include <vector>

namespace titan {
using ActorDataStatus = TitanPluginSdk::ActorDataStatus;
using AnimationSequenceMode = TitanPluginSdk::AnimationSequenceMode;
class ActorAnimationProgress {
public:
    explicit ActorAnimationProgress(TitanPluginSdk::ActorAnimationProgressState state = {}) : state_(state) {}
    const auto& raw() const { return state_; }
    ActorDataStatus status() const { return state_.status; }
    bool available() const { return status() == ActorDataStatus::Active || status() == ActorDataStatus::Inactive; }
    bool active() const { return status() == ActorDataStatus::Active; }
    AnimationSequenceMode mode() const { return state_.mode; }
    int32_t animationId() const { return state_.animationId; }
    int32_t frame() const { return state_.frame; }
    std::optional<int32_t> frameCycle() const {
        return state_.frameCycleAvailable ? std::optional<int32_t>(state_.frameCycle) : std::nullopt;
    }
    int32_t loopCount() const { return state_.loopCount; }
    int32_t delay() const { return state_.delay; }
private:
    TitanPluginSdk::ActorAnimationProgressState state_;
};
class NpcModelOverrides {
public:
    explicit NpcModelOverrides(TitanPluginSdk::NpcModelOverridesState state = {}, std::vector<int32_t> ids = {})
        : state_(state), modelIds_(std::move(ids)) {}
    const auto& raw() const { return state_; }
    ActorDataStatus status() const { return state_.status; }
    bool available() const { return status() == ActorDataStatus::Active || status() == ActorDataStatus::None; }
    bool active() const { return status() == ActorDataStatus::Active; }
    const std::vector<int32_t>& modelIds() const { return modelIds_; }
private:
    TitanPluginSdk::NpcModelOverridesState state_;
    std::vector<int32_t> modelIds_;
};
namespace detail {
template<class Read>
ActorAnimationProgress readActorAnimationProgressWith(Read&& read, const TitanPluginSdk::ActorDataIdentity& identity) {
    TitanPluginSdk::ActorAnimationProgressState state{};
    read(&identity, &state);
    if (state.status != ActorDataStatus::Active) {
        const auto status = state.status;
        state = {}; state.status = status;
    }
    return ActorAnimationProgress(state);
}
inline ActorAnimationProgress readActorAnimationProgress(const TitanPluginSdk::HostApi* api,
        const TitanPluginSdk::ActorDataIdentity& identity) {
    return readActorAnimationProgressWith([api](const auto* actor, auto* out) {
        return api && api->sdkVersion >= 155 && api->getActorAnimationProgress ? api->getActorAnimationProgress(actor, out) : 0;
    }, identity);
}
template<class Read>
NpcModelOverrides readNpcModelOverridesWith(Read&& read, const TitanPluginSdk::ActorDataIdentity& identity) {
    TitanPluginSdk::NpcModelOverridesState state{};
    std::vector<int32_t> ids(256);
    for (int attempt = 0; attempt < 3; ++attempt) {
        state = {};
        read(&identity, &state, ids.data(), static_cast<uint32_t>(ids.size()));
        if (state.modelCount > TitanPluginSdk::kMaxNpcModelOverrideIds) {
            state = {}; state.status = ActorDataStatus::Unreadable; return NpcModelOverrides(state);
        }
        if (state.status == ActorDataStatus::BufferTooSmall) {
            if (state.modelCount <= ids.size()) { state.status = ActorDataStatus::Unreadable; return NpcModelOverrides(state); }
            ids.resize(state.modelCount); continue;
        }
        if (state.status != ActorDataStatus::Active) return NpcModelOverrides(state);
        if (state.modelCount > ids.size()) { state.status = ActorDataStatus::Unreadable; return NpcModelOverrides(state); }
        ids.resize(state.modelCount);
        return NpcModelOverrides(state, std::move(ids));
    }
    return NpcModelOverrides(state);
}
inline NpcModelOverrides readNpcModelOverrides(const TitanPluginSdk::HostApi* api,
        const TitanPluginSdk::ActorDataIdentity& identity) {
    return readNpcModelOverridesWith([api](const auto* actor, auto* out, auto* ids, uint32_t capacity) {
        return api && api->sdkVersion >= 155 && api->getNpcModelOverrides ? api->getNpcModelOverrides(actor, out, ids, capacity) : 0;
    }, identity);
}
} // namespace detail
} // namespace titan
