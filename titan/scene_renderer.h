#pragma once

#include "scene_renderer_types.h"
#include "detail/host.h"

namespace titan::scene {
using namespace TitanPluginSdk;

inline bool available() {
    const auto* h = detail::host();
    return h && h->sceneRendererRegister && h->sceneRendererUnregister
        && h->sceneRendererSetEnabled && h->sceneRendererGetStatus;
}
inline bool registerRenderer(const void* owner, const SceneRendererCallbacks& callbacks, uint64_t& token) {
    const auto* h = detail::host();
    token = 0;
    return owner && h && h->sceneRendererRegister && h->sceneRendererRegister(owner, &callbacks, &token);
}
// Revokes admission and drains callbacks. Never call from render().
inline bool unregisterRenderer(const void* owner, uint64_t token) {
    const auto* h = detail::host();
    return h && h->sceneRendererUnregister && h->sceneRendererUnregister(owner, token);
}
inline bool setEnabled(const void* owner, uint64_t token, bool enabled) {
    const auto* h = detail::host();
    return h && h->sceneRendererSetEnabled && h->sceneRendererSetEnabled(owner, token, enabled ? 1 : 0);
}
inline bool status(SceneRendererStatus& out) {
    const auto* h = detail::host();
    return h && h->sceneRendererGetStatus && h->sceneRendererGetStatus(&out);
}
// Valid only within render(), for that callback's exact frame and context.
inline bool environment(uint64_t frameId,uint64_t contextKey,SceneFrameEnvironment& out) {
    const auto* h=detail::host();
    return h && h->sceneRendererGetFrameEnvironment && h->sceneRendererGetFrameEnvironment(frameId,contextKey,&out);
}
// Subscribe outside render(). Host copies at most 64 variable identities and
// reads them on the producer thread; values arrive with the captured frame.
inline bool worldVariables(const void* owner,uint64_t token,const SceneWorldVariable* values,uint32_t count) {
    const auto* h=detail::host();
    return h && h->sceneRendererSetWorldVariables && h->sceneRendererSetWorldVariables(owner,token,values,count);
}
inline bool worldData(uint64_t frameId,uint64_t contextKey,SceneFrameWorldData& out) {
    const auto* h=detail::host();
    return h && h->sceneRendererGetFrameWorldData && h->sceneRendererGetFrameWorldData(frameId,contextKey,&out);
}
// Host copies the IDs and deletes on the owning GL context. No plugin code is
// retained. Unknown/destroyed context keys may be discarded with that context.
inline bool retireResources(uint64_t contextKey, const SceneGlResource* resources, uint32_t count) {
    const auto* h = detail::host();
    return h && h->sceneRendererRetireResources && h->sceneRendererRetireResources(contextKey, resources, count);
}
} // namespace titan::scene
