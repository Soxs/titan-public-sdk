/// @file titan/plugins.h
/// @brief Plugin manager facade: enumerate plugins, toggle enabled state.
///
/// Use @ref titan::plugins() to reach the registry. The returned PluginHandle
/// exposes identity, enabled state, and enable/disable/toggle. All state
/// changes are queued on the host side and applied after the current dispatch
/// pass completes so self-disable from inside a callback is safe.

#pragma once

#include "detail/abi.h"
#include "detail/backend.h"
#include "detail/host.h"
#include "plugin.h"

#include <optional>
#include <string>
#include <vector>

namespace titan {

/// Lightweight handle to a plugin known to the host. Storing a handle is
/// cheap; all calls re-query the host so the values are always current.
class PluginHandle {
public:
    PluginHandle() = default;
    explicit PluginHandle(std::string id) : id_(std::move(id)) {}

    /// Check that the host still knows this plugin id.
    bool isValid() const {
        return refresh().has_value();
    }

    const std::string& id() const { return id_; }

    std::string name() const {
        auto s = refresh();
        return s ? std::string(s->name) : std::string();
    }
    bool isEnabled() const {
        auto s = refresh();
        return s && s->enabled != 0;
    }
    bool hasPanel() const {
        auto s = refresh();
        return s && s->hasPanel != 0;
    }

    /// Queue an enable. Takes effect after the current dispatch pass drains.
    bool enable() const { return setEnabled(true); }
    bool disable() const { return setEnabled(false); }
    bool toggle() const { return setEnabled(!isEnabled()); }

    /// Direct setter.
    bool setEnabled(bool v) const {
        auto* b = detail::backend();
        if (!b) return false;
        return b->setPluginEnabled(id_.c_str(), v ? 1 : 0) != 0;
    }

private:
    std::optional<TitanPluginSdk::PluginInfo> refresh() const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::PluginInfo info = {};
        if (!b->getPlugin(id_.c_str(), &info)) return std::nullopt;
        return info;
    }

    std::string id_;
};

/// Plugin manager singleton. Callable as @c titan::plugins().
class PluginsFacade {
public:
    /// Enumerate all plugins known to the host. The buffer is sized from the
    /// host's plugin count (SDK 95); there is no fixed cap. Older hosts that
    /// don't expose getPluginCount report 0, so we start from a floor and
    /// grow-and-retry if the buffer fills exactly.
    std::vector<PluginHandle> all() const {
        std::vector<PluginHandle> out;
        auto* b = detail::backend();
        if (!b) return out;
        uint32_t cap = b->getPluginCount();
        if (cap == 0) cap = 128;
        for (int attempt = 0; attempt < 5; ++attempt) {
            std::vector<TitanPluginSdk::PluginInfo> buf(cap);
            const uint32_t n = b->listPlugins(buf.data(), cap);
            if (n < cap) {
                out.reserve(n);
                for (uint32_t i = 0; i < n; ++i) out.emplace_back(std::string(buf[i].id));
                return out;
            }
            // Buffer filled exactly; more may exist. Grow and retry.
            cap *= 2;
        }
        return out;
    }

    /// Return a handle for @p pluginId, regardless of whether it exists. Call
    /// .isValid() on the result if you need to distinguish.
    PluginHandle get(const std::string& pluginId) const {
        return PluginHandle{pluginId};
    }

    /// Like get() but returns std::nullopt for unknown ids.
    std::optional<PluginHandle> find(const std::string& pluginId) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        TitanPluginSdk::PluginInfo info = {};
        if (!b->getPlugin(pluginId.c_str(), &info)) return std::nullopt;
        return PluginHandle{pluginId};
    }

    /// Handle for the plugin in the current thread's callback context.
    PluginHandle self() const {
        auto* p = detail::currentPlugin();
        return PluginHandle{p ? std::string(p->id()) : std::string()};
    }
};

inline PluginsFacade plugins() { return PluginsFacade{}; }

// --- Cross-plugin service registry (SDK 66) ---------------------------------
//
// A dependency publishes a versioned C-compatible table under a string id.
// Declare the frozen table in a shared header included by both plugins. A
// consumer acquires a lease for each operation; it keeps the provider and DLL
// alive until released. Dependencies establish load order, not pointer lifetime.
//
// @code
//   // shared header
//   struct CoreServiceV1 {
//       uint32_t structSize = sizeof(CoreServiceV1);
//       uint32_t apiVersion = 1;
//       void* context = nullptr;
//       int32_t (*compute)(void* context, int32_t input) = nullptr;
//   };
//
//   // provider plugin (constructor / onEnable)
//   titan::registerService(*this, "core.service.v1", &service_);
//
//   // dependent plugin
//   if (auto svc = titan::service<CoreServiceV1>(*this, "core.service.v1")) {
//       if (svc->apiVersion == 1 && svc->structSize >= sizeof(CoreServiceV1)
//           && svc->compute) {
//           int32_t result = svc->compute(svc->context, 7);
//       }
//   } // release the lease here; never retain svc.get() beyond the lease
// @endcode

/// Publish a load-lifetime service for an explicit owning plugin instance.
/// This overload is safe to use from a plugin constructor/member helper where
/// the ambient `currentPlugin()` has not been installed yet. A constructor
/// call is staged by opaque address until adapter creation; the host never
/// dereferences that address and then keeps only stable identity/generation.
inline bool registerService(Plugin& owner, const char* serviceId, void* service) {
    auto* b = owner.backend();
    return b && serviceId &&
        b->registerPluginServiceOwned(&owner, owner.id(), serviceId, service) != 0;
}

/// A resolved service pointer is valid only while this move-only lease is alive.
/// An empty reference means no compatible live provider could be acquired.
/// Keep it for the duration of one operation, not for the plugin lifetime.
template <typename T>
class ServiceRef final {
public:
    ServiceRef() = default;
    ServiceRef(std::nullptr_t) noexcept {}
    ServiceRef(detail::IBackend* backend, const char* id) : backend_(backend) {
        void* value = nullptr;
        if (backend_ && id && backend_->acquirePluginService(id, &value, &lease_))
            value_ = static_cast<T*>(value);
    }
    ~ServiceRef() { reset(); }
    ServiceRef(const ServiceRef&) = delete;
    ServiceRef& operator=(const ServiceRef&) = delete;
    ServiceRef(ServiceRef&& other) noexcept
        : backend_(other.backend_), value_(std::exchange(other.value_, nullptr)),
          lease_(std::exchange(other.lease_, nullptr)) {}
    ServiceRef& operator=(ServiceRef&& other) noexcept {
        if (this != &other) {
            reset(); backend_ = other.backend_;
            value_ = std::exchange(other.value_, nullptr);
            lease_ = std::exchange(other.lease_, nullptr);
        }
        return *this;
    }
    explicit operator bool() const noexcept { return value_ != nullptr; }
    T* operator->() const noexcept { return value_; }
    T* get() const noexcept { return value_; }
    void reset() noexcept {
        value_ = nullptr;
        if (auto* lease = std::exchange(lease_, nullptr)) backend_->releasePluginLease(lease);
    }
private:
    detail::IBackend* backend_ = nullptr;
    T* value_ = nullptr;
    void* lease_ = nullptr;
};

template <typename T>
inline ServiceRef<T> service(const char* serviceId) {
    return {detail::backend(), serviceId};
}

template <typename T>
inline ServiceRef<T> service(Plugin& owner, const char* serviceId) {
    return {owner.backend(), serviceId};
}

}  // namespace titan
