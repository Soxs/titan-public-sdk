/// @file titan/detail/plugin_context.h
/// @brief Nested per-thread SDK dispatch context for a native plugin instance.
#pragma once

#include "backend.h"
#include "host.h"

namespace titan::detail {

/// Context carries identity, not a lifetime lease. The host must already hold
/// the instance alive for this callback, scheduled task, or scoped worker.
class ScopedBackendContext final {
public:
    ScopedBackendContext(IBackend* backend,
                         const TitanPluginSdk::HostApi* host,
                         Plugin* plugin) noexcept
        : previousBackend_(threadBackendContext()),
          previousHost_(threadHostContext()) {
        threadBackendContext() = {true, backend};
        threadHostContext() = {true, host, plugin};
    }

    ~ScopedBackendContext() {
        threadHostContext() = previousHost_;
        threadBackendContext() = previousBackend_;
    }

    ScopedBackendContext(const ScopedBackendContext&) = delete;
    ScopedBackendContext& operator=(const ScopedBackendContext&) = delete;

private:
    ThreadBackendContext previousBackend_;
    ThreadHostContext previousHost_;
};

} // namespace titan::detail
