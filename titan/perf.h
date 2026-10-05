#pragma once

#include "perf_probe.h"
#include "detail/backend.h"

#include <atomic>

namespace titan::perf {
namespace detail {

inline std::atomic<const ServiceV1*> service{nullptr};

} // namespace detail

/// Refresh once at a plugin callback boundary, never inside a hot helper.
/// Missing/older hosts remain no-ops and can be retried at the next boundary.
inline void bind() {
    auto* backend = titan::detail::backend();
    const auto* service = backend
        ? static_cast<const ServiceV1*>(backend->getPluginService(kServiceId))
        : nullptr;
    if (service && (service->structSize < sizeof(ServiceV1) ||
                    service->apiVersion != kApiVersion ||
                    !service->begin || !service->end || !service->add)) {
        service = nullptr;
    }
    detail::service.store(service, std::memory_order_release);
}

inline void add(Metric metric, uint64_t value) noexcept {
    if (const auto* service = detail::service.load(std::memory_order_acquire)) {
        service->add(static_cast<uint32_t>(metric), value);
    }
}

/// The host returns a zero token when profiling is disabled. Keep the same
/// service for end() even if a later callback refreshes the cached binding.
class Scope {
public:
    explicit Scope(Metric metric) noexcept
        : service_(detail::service.load(std::memory_order_acquire)),
          token_(service_ ? service_->begin(static_cast<uint32_t>(metric)) : 0) {}

    ~Scope() {
        if (token_) service_->end(token_);
    }

    Scope(const Scope&) = delete;
    Scope& operator=(const Scope&) = delete;

    // Whether construction opened a span. Used once at a search boundary to
    // gate optional sampling; reset/disable may invalidate the token later.
    bool recording() const noexcept { return token_ != 0; }

private:
    const ServiceV1* service_;
    uint64_t token_;
};

} // namespace titan::perf
