#pragma once
#include "plugin.h"
#include <cstdint>
#include <functional>
#include <future>
#include <memory>
#include <optional>
#include <stop_token>
#include <string>
#include <vector>

namespace titan {
namespace authorization {
enum class Decision { Entitled, NotEntitled, Unavailable, ScopeDenied };
struct ExtraInstances { std::uint32_t quantity = 0; std::optional<std::int64_t> expiresAt; };
struct Entitlement {
    std::string key, pluginId, canonicalPluginId, runtimePluginId, name, accessMode;
    bool entitled = false, owned = false, runtimeEligible = false, deliveryEligible = false;
    std::optional<std::int64_t> accessExpiresAt, nextChangeAt;
    std::optional<std::uint32_t> includedInstances, totalInstances; // null = unlimited
    std::uint32_t extraInstances = 0;
    std::uint32_t activeInstances = 0, retiringInstances = 0;
    std::int64_t usageObservedAt = 0;
    std::vector<std::string> sources;
    std::vector<ExtraInstances> extraExpiries;
};
struct Result {
    Decision decision = Decision::Unavailable;
    std::string reason;
    std::optional<Entitlement> entitlement;
    std::int64_t verifiedAt = 0, validUntil = 0;
};
struct Snapshot {
    bool complete = false, stale = true;
    std::string reason, tier;
    std::vector<std::string> features;
    std::vector<Entitlement> plugins;
    std::int64_t verifiedAt = 0, validUntil = 0;
};
enum class RunState { Stopped, Starting, Running, Reconnecting, Stopping };
struct Status {
    RunState state = RunState::Stopped;
    std::string reason;
    std::int64_t validUntil = 0, releasePendingUntil = 0;
    bool active() const { return state == RunState::Running || state == RunState::Reconnecting; }
};
struct StartResult { bool started = false; std::string reason; };
struct Bot {
    std::function<void()> start, tick, stop;
};
namespace detail { class InstanceState; class EntitlementState; }
}

// Futures complete on the plugin's MainLoop. Never block a game callback on get().
class ManagedInstance {
public:
    ManagedInstance() = default;
    explicit ManagedInstance(std::shared_ptr<authorization::detail::InstanceState> state) : state_(std::move(state)) {}
    std::shared_future<authorization::StartResult> start() const;
    void stop() const;
    authorization::Status status() const;
    bool active() const { return status().active(); }
    // Optional standard panel. Call once during the plugin constructor.
    SidePanel& controls(const char* panelId = "run", const char* title = "Run") const;
    std::shared_future<authorization::StartResult> run(std::function<void(std::stop_token)> task) const;
private:
    std::shared_ptr<authorization::detail::InstanceState> state_;
};
class Entitlements {
public:
    explicit Entitlements(std::shared_ptr<authorization::detail::EntitlementState> state) : state_(std::move(state)) {}
    std::shared_future<authorization::Result> check(std::string key = {}) const;
    std::shared_future<authorization::Snapshot> list() const;
    authorization::Snapshot snapshot() const;
    // Returns an unsubscribe function. Unload also removes the subscription.
    std::function<void()> onChanged(std::function<void(const authorization::Snapshot&)> callback) const;
private:
    std::shared_ptr<authorization::detail::EntitlementState> state_;
};
ManagedInstance instance(Plugin& owner, authorization::Bot bot = {});
Entitlements entitlements(Plugin& owner);
template<class BotType>
ManagedInstance instance(Plugin& owner, std::shared_ptr<BotType> bot) {
    if (!bot) throw std::invalid_argument("instance requires an owned bot");
    authorization::Bot callbacks;
    callbacks.start = [bot] {
        if constexpr (std::is_same_v<decltype(bot->start()), bool>) {
            if (!bot->start()) throw std::runtime_error("Bot startup failed");
        } else bot->start();
    };
    if constexpr (requires { bot->tick(); }) callbacks.tick = [bot] { bot->tick(); };
    callbacks.stop = [bot] { bot->stop(); };
    return instance(owner, std::move(callbacks));
}
}
