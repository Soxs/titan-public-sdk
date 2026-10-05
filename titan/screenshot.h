/// @file titan/screenshot.h
/// @brief Full-frame game screenshot facade (SDK 131).
///
/// The same image the controller's `/tabs` command returns: the game's
/// presented backbuffer after the AboveWidgets overlay pass, PNG-encoded.
/// Capture is asynchronous -- the pixels are read back on the next presented
/// frame and encoded on a worker thread -- so the facade is a handle you
/// submit, poll, copy from, and release, like `titan::webWalker()`.

#pragma once

#include "detail/backend.h"
#include "detail/screenshot.h"

#include <cstdint>
#include <optional>
#include <string>
#include <vector>

namespace titan {

using ScreenshotHandle = uint64_t;

enum class ScreenshotPhase : uint32_t {
    None = TitanPluginSdk::ScreenshotPhase::None,
    Pending = TitanPluginSdk::ScreenshotPhase::Pending,
    Ready = TitanPluginSdk::ScreenshotPhase::Ready,
    Failed = TitanPluginSdk::ScreenshotPhase::Failed,
};

struct ScreenshotStatus {
    ScreenshotHandle requestId = 0;
    ScreenshotPhase phase = ScreenshotPhase::None;
    /// Captured frame size in pixels; zero until Ready.
    uint32_t width = 0;
    uint32_t height = 0;
    /// Encoded PNG size; zero until Ready.
    uint32_t pngBytes = 0;
    /// Failure reason, or empty.
    std::string message;

    bool finished() const {
        return phase == ScreenshotPhase::Ready || phase == ScreenshotPhase::Failed;
    }
};

namespace detail {
template <size_t N>
inline std::string fixedScreenshotString(const char (&value)[N]) {
    size_t length = 0;
    while (length < N && value[length] != '\0') ++length;
    return std::string(value, length);
}
}  // namespace detail

/// Callable from any plugin callback thread. At most
/// `TitanPluginSdk::kScreenshotMaxUnreleased` handles may be outstanding, so
/// release() every handle you finish with; a request that never sees a
/// presented frame fails on its own after a few seconds.
class ScreenshotFacade {
public:
    /// Queue one capture of the next presented frame. Empty when the host
    /// predates SDK 131 or the unreleased-request cap is reached.
    std::optional<ScreenshotHandle> submit() const {
        auto* backend = detail::backend();
        if (!backend) return std::nullopt;
        ScreenshotHandle handle = 0;
        if (!backend->screenshotSubmit(&handle) || handle == 0) return std::nullopt;
        return handle;
    }

    std::optional<ScreenshotStatus> poll(ScreenshotHandle handle) const {
        auto* backend = detail::backend();
        if (!backend || handle == 0) return std::nullopt;
        TitanPluginSdk::ScreenshotStatusState state{};
        if (!backend->screenshotPoll(handle, &state)) return std::nullopt;
        ScreenshotStatus status;
        status.requestId = state.requestId;
        status.phase = static_cast<ScreenshotPhase>(state.phase);
        status.width = state.width;
        status.height = state.height;
        status.pngBytes = state.pngBytes;
        status.message = detail::fixedScreenshotString(state.message);
        return status;
    }

    /// The PNG bytes of a Ready request; empty for anything else.
    std::optional<std::vector<uint8_t>> copyPng(ScreenshotHandle handle) const {
        auto* backend = detail::backend();
        if (!backend || handle == 0) return std::nullopt;
        return detail::copyScreenshotPng(
            [backend](uint64_t id, uint8_t* out, uint32_t capacity, uint32_t* required) {
                return backend->screenshotCopyPng(id, out, capacity, required);
            },
            handle);
    }

    bool release(ScreenshotHandle handle) const {
        auto* backend = detail::backend();
        return backend && handle != 0 && backend->screenshotRelease(handle) != 0;
    }
};

inline ScreenshotFacade screenshot() { return ScreenshotFacade{}; }
namespace state {
inline ::titan::ScreenshotFacade screenshot() { return ::titan::ScreenshotFacade{}; }
}  // namespace state

}  // namespace titan
