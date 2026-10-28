#pragma once

#include <cstddef>
#include <cstdint>
#include <type_traits>

namespace TitanPluginSdk {

enum class ItemSpriteStatus : uint32_t {
    Ready = 0, Pending = 1, Unsupported = 2, Invalid = 3,
    NotReady = 4, Failed = 5, BufferTooSmall = 6
};

enum class ItemSpriteQuantityText : int32_t { Hidden = 0, Always = 1 };

// Frozen v1 records. Every pointer used by this interface is caller-owned and
// borrowed only for the call. No native client pointer or allocator crosses it.
struct ItemSpriteRequest {
    int32_t itemId = -1;
    int32_t quantity = 1;
    int32_t border = 0; // Native border treatment: 0, 1 or 2.
    uint32_t shadowRgb = 0; // 0 disables the native shadow; otherwise 0x00RRGGBB.
    int32_t quantityText = static_cast<int32_t>(ItemSpriteQuantityText::Hidden);
    uint32_t reserved = 0;
};

struct SpriteImageInfo {
    int32_t width = 0;
    int32_t height = 0;
    int32_t xOffset = 0;
    int32_t yOffset = 0;
    int32_t canvasWidth = 0;
    int32_t canvasHeight = 0;
    uint32_t pixelCount = 0;
    uint32_t reserved = 0;
};

// Bounds apply to owned pixels and derived outlines, independently of the
// native factory's default canvas dimensions obtained from its verified schema.
inline constexpr uint32_t kSpriteImageMaxPixels = 512u * 512u;
inline constexpr int32_t kSpriteImageMaxDimension = 512;
inline constexpr uint32_t kSpriteOutlineMaxThickness = 8;

static_assert(sizeof(ItemSpriteStatus) == 4 && sizeof(ItemSpriteQuantityText) == 4);
static_assert(sizeof(ItemSpriteRequest) == 24 && alignof(ItemSpriteRequest) == 4);
static_assert(offsetof(ItemSpriteRequest, quantityText) == 16);
static_assert(sizeof(SpriteImageInfo) == 32 && alignof(SpriteImageInfo) == 4);
static_assert(offsetof(SpriteImageInfo, pixelCount) == 24);
static_assert(std::is_standard_layout_v<ItemSpriteRequest> && std::is_trivially_copyable_v<ItemSpriteRequest>);
static_assert(std::is_standard_layout_v<SpriteImageInfo> && std::is_trivially_copyable_v<SpriteImageInfo>);

} // namespace TitanPluginSdk
