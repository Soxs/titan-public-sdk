/// @file titan/detail/host.h
/// @brief Host/plugin lookup with per-thread native instance context.
///
/// Dispatch and owned scheduled work install a scoped override. The per-DLL
/// slots remain available for internal backends and stateless compatibility.

#pragma once

#include "abi.h"

namespace titan {
class Plugin;

namespace detail {

/// Globals installed by TITAN_REGISTER_PLUGIN's TitanCreatePlugin thunk.
/// Each plugin DLL gets its own copy of these statics.
inline const TitanPluginSdk::HostApi*& hostRef() {
    static const TitanPluginSdk::HostApi* g_host = nullptr;
    return g_host;
}

inline Plugin*& pluginRef() {
    static Plugin* g_plugin = nullptr;
    return g_plugin;
}

struct ThreadHostContext {
    bool active = false;
    const TitanPluginSdk::HostApi* host = nullptr;
    Plugin* plugin = nullptr;
};

inline ThreadHostContext& threadHostContext() noexcept {
    static thread_local ThreadHostContext context;
    return context;
}

inline const TitanPluginSdk::HostApi* host() {
    const auto& context = threadHostContext();
    return context.active ? context.host : hostRef();
}
inline Plugin* currentPlugin() {
    const auto& context = threadHostContext();
    return context.active ? context.plugin : pluginRef();
}

}  // namespace detail
}  // namespace titan
