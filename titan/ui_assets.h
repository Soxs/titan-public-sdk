/// @file titan/ui_assets.h
/// @brief Owned copies of host-bundled skill icons and font files.
#pragma once

#include "skill.h"
#include "sprites.h"

namespace titan {
namespace skillIcons {

inline bool available() {
    const auto* host = detail::host();
    return host && host->copySkillIcon;
}

/// Exact bundled artwork, with owned 0xAARRGGBB pixels. No game, cache or JVM
/// is needed. An older host, invalid skill or unavailable resource returns
/// nullopt. This does not enable arbitrary game-cache sprite retrieval.
inline std::optional<SpriteImage> get(int32_t skillId, bool smallIcon = false) {
    if (skillId < 0 || skillId >= static_cast<int32_t>(Skill::COUNT)) return std::nullopt;
    const auto* host = detail::host();
    if (!host || !host->copySkillIcon) return std::nullopt;
    SpriteImageInfo info{};
    if (host->copySkillIcon(skillId, smallIcon ? 1 : 0, &info, nullptr, 0)
            != TitanPluginSdk::UiAssetStatus::BufferTooSmall
        || !TitanPluginSdk::validSpriteImage(info, info.pixelCount)) return std::nullopt;
    std::vector<uint32_t> pixels(info.pixelCount);
    info = {};
    if (host->copySkillIcon(skillId, smallIcon ? 1 : 0, &info, pixels.data(), static_cast<uint32_t>(pixels.size()))
            != TitanPluginSdk::UiAssetStatus::Ready
        || info.pixelCount > pixels.size()
        || !TitanPluginSdk::validSpriteImage(info, info.pixelCount)) return std::nullopt;
    pixels.resize(info.pixelCount);
    return detail::spriteImage(info, std::move(pixels));
}

inline std::optional<SpriteImage> get(Skill skill, bool smallIcon = false) {
    return get(static_cast<int32_t>(skill), smallIcon);
}
} // namespace skillIcons

namespace fonts {
enum class Face : uint32_t { Regular = 0, Bold = 1, Small = 2 };

inline bool available() {
    const auto* host = detail::host();
    return host && host->copyFontData;
}

/// Exact bundled font file bytes, suitable for an HTML panel's font resource
/// or a caller-owned decoder. This adds no overlay font-rendering API. The
/// returned copy remains valid independently of the host's resource storage.
inline std::optional<std::vector<uint8_t>> data(Face face) {
    const auto value = static_cast<uint32_t>(face);
    if (value > static_cast<uint32_t>(Face::Small)) return std::nullopt;
    const auto* host = detail::host();
    if (!host || !host->copyFontData) return std::nullopt;
    uint32_t size = 0;
    if (host->copyFontData(value, nullptr, 0, &size) != TitanPluginSdk::UiAssetStatus::BufferTooSmall
        || !size || size > TitanPluginSdk::kUiAssetMaxFontBytes) return std::nullopt;
    std::vector<uint8_t> bytes(size);
    uint32_t copied = 0;
    if (host->copyFontData(value, bytes.data(), size, &copied) != TitanPluginSdk::UiAssetStatus::Ready
        || copied != size) return std::nullopt;
    return bytes;
}
} // namespace fonts
} // namespace titan
