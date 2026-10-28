/// @file titan/sprites.h
/// @brief Owned item sprites, alpha-mask outlines, and overlay image drawing.
#pragma once

#include "detail/host.h"
#include "detail/item_sprite_abi.h"
#include "detail/sprite_pixels.h"

#include <cmath>
#include <cstdint>
#include <optional>
#include <span>
#include <utility>
#include <vector>

namespace titan {

using ItemSpriteStatus = TitanPluginSdk::ItemSpriteStatus;
using ItemSpriteQuantityText = TitanPluginSdk::ItemSpriteQuantityText;
using ItemSpriteRequest = TitanPluginSdk::ItemSpriteRequest;
using SpriteImageInfo = TitanPluginSdk::SpriteImageInfo;

struct ItemSpriteOptions {
    int32_t border = 0;
    uint32_t shadowRgb = 0;
    ItemSpriteQuantityText quantityText = ItemSpriteQuantityText::Hidden;
};

/// Row-major owned 0xAARRGGBB pixels. The host's item sprite has binary alpha;
/// derived images may use any alpha. A zero alpha pixel is transparent.
/// Offsets place this stored rectangle inside its logical canvas and may be
/// negative for padded outlines. Keep the image for as long as it is useful.
struct SpriteImage {
    int32_t width = 0, height = 0;
    int32_t xOffset = 0, yOffset = 0;
    int32_t canvasWidth = 0, canvasHeight = 0;
    std::vector<uint32_t> pixels;

    TitanPluginSdk::SpriteImageInfo info() const {
        return {width, height, xOffset, yOffset, canvasWidth, canvasHeight,
            pixels.size() <= TitanPluginSdk::kSpriteImageMaxPixels
                ? static_cast<uint32_t>(pixels.size()) : 0u, 0};
    }
    bool valid() const {
        return pixels.size() <= TitanPluginSdk::kSpriteImageMaxPixels
            && TitanPluginSdk::validSpriteImage(info(), static_cast<uint32_t>(pixels.size()));
    }
};

struct ItemSpriteResult {
    ItemSpriteStatus status = ItemSpriteStatus::Unsupported;
    std::optional<SpriteImage> image;
    bool ready() const { return status == ItemSpriteStatus::Ready && image.has_value(); }
};

namespace detail {

inline SpriteImage spriteImage(TitanPluginSdk::SpriteImageInfo info, std::vector<uint32_t> pixels) {
    return {info.width, info.height, info.xOffset, info.yOffset,
        info.canvasWidth, info.canvasHeight, std::move(pixels)};
}

inline bool validItemSpriteRequest(const ItemSpriteRequest& request) {
    return request.itemId >= 0 && request.quantity > 0 && request.border >= 0 && request.border <= 2
        && request.shadowRgb <= 0x00ffffffu && request.quantityText >= 0 && request.quantityText <= 1
        && request.reserved == 0;
}

/// Shared bounded copy path for native and managed adapters. Getter has the
/// HostApi::getItemSprite signature and must never retain the output buffer.
template <typename Getter>
inline ItemSpriteResult copyItemSprite(Getter getter, const ItemSpriteRequest& request) {
    if (!validItemSpriteRequest(request)) return {ItemSpriteStatus::Invalid, {}};
    TitanPluginSdk::SpriteImageInfo info{};
    auto status = getter(&request, &info, nullptr, 0);
    if (status != ItemSpriteStatus::BufferTooSmall)
        return {status == ItemSpriteStatus::Ready ? ItemSpriteStatus::Failed : status, {}};
    for (int attempt = 0; attempt < 2; ++attempt) {
        if (!TitanPluginSdk::validSpriteImage(info, info.pixelCount)) return {ItemSpriteStatus::Failed, {}};
        std::vector<uint32_t> pixels(info.pixelCount);
        info = {};
        status = getter(&request, &info, pixels.data(), static_cast<uint32_t>(pixels.size()));
        if (status == ItemSpriteStatus::BufferTooSmall) continue;
        if (status != ItemSpriteStatus::Ready) return {status, {}};
        if (info.pixelCount > pixels.size() || !TitanPluginSdk::validSpriteImage(info, info.pixelCount))
            return {ItemSpriteStatus::Failed, {}};
        pixels.resize(info.pixelCount);
        return {ItemSpriteStatus::Ready, spriteImage(info, std::move(pixels))};
    }
    return {ItemSpriteStatus::BufferTooSmall, {}};
}

} // namespace detail

namespace sprites {

/// Reports the negotiated API, not whether a particular item's resources are
/// loaded. item() reports current readiness and revision support separately.
inline bool available() {
    const auto* host = detail::host();
    return host && host->getItemSprite;
}

/// Nonblocking. Once the native renderer has been observed and resources are
/// loaded, an accepted miss queues host-owned work and returns Pending.
/// NotReady covers bootstrap/resource readiness and temporary queue pressure. Poll on a
/// later callback; never spin or wait on the game/render thread. No native
/// handle is retained by the SDK and no release call is needed for the copy.
inline ItemSpriteResult item(const ItemSpriteRequest& request) {
    if (!detail::validItemSpriteRequest(request)) return {ItemSpriteStatus::Invalid, {}};
    const auto* host = detail::host();
    if (!host || !host->getItemSprite) return {};
    return detail::copyItemSprite(host->getItemSprite, request);
}

inline ItemSpriteResult item(int32_t itemId, int32_t quantity = 1, const ItemSpriteOptions& options = {}) {
    return item({itemId, quantity, options.border, options.shadowRgb, static_cast<int32_t>(options.quantityText), 0});
}

/// Four-neighbour dilation of the nonzero alpha mask. The result contains only
/// the outer ring, excluding original occupied pixels. Padding extends stored
/// bounds and offsets while retaining the canvas origin. Cache this pure CPU
/// result when reusing it; invalid images or thickness outside 1..8 return empty.
inline std::optional<SpriteImage> outline(const SpriteImage& image, uint32_t argb, uint32_t thickness = 1) {
    if (!image.valid()) return std::nullopt;
    TitanPluginSdk::SpriteImageInfo info{};
    std::vector<uint32_t> pixels;
    if (!TitanPluginSdk::outlineSpritePixels(image.info(), std::span<const uint32_t>(image.pixels),
            argb, thickness, info, pixels)) return std::nullopt;
    return detail::spriteImage(info, std::move(pixels));
}

/// Draw inside an overlay callback. x/y are screen-space logical canvas origin;
/// the host applies stored offsets and scale (finite, greater than 0, at most 16).
/// Pixels are copied synchronously.
inline bool draw(const SpriteImage& image, int32_t x, int32_t y, float scale = 1.0f) {
    if (!image.valid() || !std::isfinite(scale) || scale <= 0) return false;
    const auto* host = detail::host();
    const auto info = image.info();
    return host && host->drawSpriteImage && host->drawSpriteImage(&info, image.pixels.data(),
        static_cast<uint32_t>(image.pixels.size()), x, y, scale) != 0;
}

} // namespace sprites
} // namespace titan
