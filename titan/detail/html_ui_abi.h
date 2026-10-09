#pragma once
#include <cstdint>
#include <type_traits>

namespace TitanPluginSdk {
// Immutable v1 records. Never append fields or change their array strides.
namespace HtmlUiRecords {
struct DescriptorV1 {
    uint32_t version = 1, kind = 1;
    char id[32]{}, title[64]{}, icon[64]{}, entrypoint[256]{};
    uint32_t iconColor = 0, iconBytes = 0, resourceCount = 0, bundleBytes = 0;
    uint32_t width = 220, height = 160;
    int32_t priority = 50;
    uint8_t anchor = 0, input = 0, visible = 1, reserved = 0;
};
struct ResourceV1 { char path[256]{}, mime[64]{}; uint32_t bytes = 0; };
struct SnapshotV1 {
    uint64_t activation = 0, lastSequence = 0, stateRevision = 0;
    uint32_t stateBytes = 0, messageCount = 0;
    uint8_t visible = 1, reserved[7]{};
    uint32_t width = 220, height = 160;
};
static_assert(sizeof(DescriptorV1) == 456);
static_assert(sizeof(ResourceV1) == 324);
static_assert(sizeof(SnapshotV1) == 48);
static_assert(std::is_standard_layout_v<DescriptorV1> && std::is_trivially_copyable_v<DescriptorV1>);
}
// Consumer-local view, copied as one complete required prefix from a separately
// negotiated native interface. Strings are UTF-8; byte counts exclude NUL.
struct HtmlUiCallbacks {
    uint32_t (*getDescriptors)(void*, HtmlUiRecords::DescriptorV1*, uint32_t) = nullptr;
    uint8_t (*getResource)(void*, const char*, uint32_t, HtmlUiRecords::ResourceV1*) = nullptr;
    uint32_t (*copyResource)(void*, const char*, uint32_t, uint32_t, uint8_t*, uint32_t) = nullptr;
    uint8_t (*getSnapshot)(void*, const char*, uint64_t, HtmlUiRecords::SnapshotV1*) = nullptr;
    uint32_t (*copyState)(void*, const char*, uint64_t, char*, uint32_t) = nullptr;
    uint32_t (*copyMessage)(void*, const char*, uint64_t, uint32_t, char*, uint32_t, uint64_t*) = nullptr;
    uint8_t (*acknowledge)(void*, const char*, uint64_t, uint64_t) = nullptr;
    uint8_t (*reset)(void*, const char*, uint64_t) = nullptr;
    // Host MUST marshal dispatchMessage to MainLoop under the plugin lifecycle
    // gate. Other operations access synchronized framework data while lifetime
    // admission pins the DLL; they must not invoke user handlers or HostApi.
    // Passive reads/reset/acknowledgements may overlap overlay rendering.
    uint8_t (*dispatchMessage)(void*, const char*, uint64_t, const char*, uint32_t) = nullptr;
    uint32_t (*copyIcon)(void*, const char*, uint32_t, uint8_t*, uint32_t) = nullptr;
    bool complete() const noexcept {
        return getDescriptors && getResource && copyResource && getSnapshot && copyState &&
            copyMessage && acknowledge && reset && dispatchMessage && copyIcon;
    }
};
inline constexpr uint32_t kHtmlUiSidePanels = 1, kHtmlUiOverlays = 2;
// Support describes the client protocol. RuntimeAvailable means a renderer has
// been observed for a live activation; a clear bit also includes unobserved.
// Do not gate registration or initial visibility on this observational bit.
// A missing renderer is nonfatal and does not require loading a helper at idle.
inline constexpr uint32_t kHtmlUiRuntimeAvailable = uint32_t{1} << 31;
} // namespace TitanPluginSdk
