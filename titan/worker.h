/// @file titan/worker.h
/// @brief Joinable native workers with explicit plugin identity and lifetime.
#pragma once

#include "detail/plugin_context.h"
#include "plugin.h"

#include <functional>
#include <memory>
#include <stdexcept>
#include <thread>
#include <utility>

namespace titan {

/// A plugin-owned worker. Signal cancellation and join from onUnload (and
/// from onDisable for workers whose lifetime follows the enabled state).
/// There is deliberately no detach: the DLL remains pinned through join,
/// including the thread trampoline and destruction of callable captures.
/// Construction throws if admission is closed or the thread cannot start;
/// captures are still destroyed under the owner's SDK context. The owner
/// must outlive this handle, and must join it from a different thread.
class PluginWorker final {
    struct State {
        detail::IBackend* backend;
        const TitanPluginSdk::HostApi* host;
        Plugin* owner;
        std::function<void()> callable;
        void* lease = nullptr;

        State(Plugin& plugin, std::function<void()> fn)
            : backend(plugin.backend()), host(plugin.host()), owner(&plugin),
              callable(std::move(fn)) {
            if (!backend || !backend->acquirePluginWork(&lease) || !lease) {
                throw std::runtime_error("Plugin worker admission is unavailable or stopping");
            }
        }
        ~State() {
            detail::ScopedBackendContext context(backend, host, owner);
            // Also covers a failed std::thread launch, before run() exists.
            callable = {};
            if (lease) backend->releasePluginLease(std::exchange(lease, nullptr));
        }
        void run() noexcept {
            detail::ScopedBackendContext context(backend, host, owner);
            try { if (callable) callable(); }
            catch (...) {
                try {
                    if (host && host->log) host->log("[Native SDK] Plugin worker threw a C++ exception");
                } catch (...) {}
            }
            // Plugin-owned captures are released with identity installed and
            // the lease still held. The caller releases that lease after join.
            callable = {};
        }
    };

public:
    PluginWorker() = default;
    PluginWorker(Plugin& owner, std::function<void()> fn) {
        detail::ScopedBackendContext context(owner.backend(), owner.host(), &owner);
        state_ = std::make_unique<State>(owner, std::move(fn));
        thread_ = std::thread([state = state_.get()] { state->run(); });
    }
    ~PluginWorker() { join(); }
    PluginWorker(const PluginWorker&) = delete;
    PluginWorker& operator=(const PluginWorker&) = delete;
    PluginWorker(PluginWorker&&) noexcept = default;
    PluginWorker& operator=(PluginWorker&& other) {
        if (this != &other) {
            join();
            state_ = std::move(other.state_);
            thread_ = std::move(other.thread_);
        }
        return *this;
    }

    bool joinable() const noexcept { return thread_.joinable(); }
    void join() {
        if (thread_.joinable()) thread_.join();
        state_.reset();
    }

private:
    std::unique_ptr<State> state_;
    std::thread thread_;
};

inline PluginWorker startPluginWorker(Plugin& owner, std::function<void()> fn) {
    return PluginWorker(owner, std::move(fn));
}

} // namespace titan
