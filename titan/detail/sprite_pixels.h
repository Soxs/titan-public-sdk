#pragma once

#include "item_sprite_abi.h"

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <span>
#include <vector>

namespace TitanPluginSdk {

// The bitmap can extend beyond its logical canvas: outlines intentionally use
// negative offsets. Limits also bound work for caller-supplied images.
inline bool validSpriteImage(const SpriteImageInfo& image, size_t count) noexcept {
    if (image.reserved || image.width <= 0 || image.height <= 0
        || image.width > kSpriteImageMaxDimension || image.height > kSpriteImageMaxDimension
        || image.canvasWidth <= 0 || image.canvasHeight <= 0
        || image.canvasWidth > kSpriteImageMaxDimension || image.canvasHeight > kSpriteImageMaxDimension
        || image.xOffset < -kSpriteImageMaxDimension || image.xOffset > kSpriteImageMaxDimension
        || image.yOffset < -kSpriteImageMaxDimension || image.yOffset > kSpriteImageMaxDimension) return false;
    const auto required = static_cast<uint32_t>(image.width) * static_cast<uint32_t>(image.height);
    return required <= kSpriteImageMaxPixels && image.pixelCount == required && count == required;
}

// Four-neighbour (Manhattan-distance) outer outline of the alpha silhouette.
// Original occupied pixels are excluded, including partially transparent ones.
// Pixels are owned straight-alpha 0xAARRGGBB. Canvas origin is preserved, while
// the expanded bitmap's offsets move outwards by thickness.
inline bool outlineSpritePixels(const SpriteImageInfo& image, std::span<const uint32_t> pixels,
                                uint32_t argb, uint32_t thickness, SpriteImageInfo& outImage,
                                std::vector<uint32_t>& outPixels) noexcept {
    if (!validSpriteImage(image, pixels.size()) || thickness == 0 || thickness > kSpriteOutlineMaxThickness) return false;
    SpriteImageInfo expanded = image;
    expanded.width += static_cast<int32_t>(thickness * 2);
    expanded.height += static_cast<int32_t>(thickness * 2);
    expanded.xOffset -= static_cast<int32_t>(thickness);
    expanded.yOffset -= static_cast<int32_t>(thickness);
    expanded.pixelCount = static_cast<uint32_t>(expanded.width) * static_cast<uint32_t>(expanded.height);
    if (!validSpriteImage(expanded, expanded.pixelCount)) return false;
    try {
        const int width = expanded.width, height = expanded.height;
        const auto outsideRadius = static_cast<uint16_t>(thickness + 1);
        std::vector<uint16_t> distance(expanded.pixelCount, outsideRadius);
        for (int y = 0; y < image.height; ++y) {
            for (int x = 0; x < image.width; ++x) {
                if (pixels[static_cast<size_t>(y) * image.width + x] >> 24) {
                    distance[(static_cast<size_t>(y) + thickness) * width + x + thickness] = 0;
                }
            }
        }
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                const auto at = static_cast<size_t>(y) * width + x;
                if (x) distance[at] = (std::min)(distance[at], static_cast<uint16_t>(distance[at - 1] + 1));
                if (y) distance[at] = (std::min)(distance[at], static_cast<uint16_t>(distance[at - width] + 1));
            }
        }
        for (int y = height - 1; y >= 0; --y) {
            for (int x = width - 1; x >= 0; --x) {
                const auto at = static_cast<size_t>(y) * width + x;
                if (x + 1 < width) distance[at] = (std::min)(distance[at], static_cast<uint16_t>(distance[at + 1] + 1));
                if (y + 1 < height) distance[at] = (std::min)(distance[at], static_cast<uint16_t>(distance[at + width] + 1));
            }
        }
        std::vector<uint32_t> output(expanded.pixelCount);
        for (size_t at = 0; at < output.size(); ++at) {
            if (distance[at] != 0 && distance[at] <= thickness) output[at] = argb;
        }
        outImage = expanded;
        outPixels.swap(output);
        return true;
    } catch (...) {
        return false;
    }
}

} // namespace TitanPluginSdk
