/// @file titan/world_map.h
/// @brief Read-only native world-map state and projection helpers (SDK 113).

#pragma once

#include "detail/abi.h"
#include "detail/backend.h"
#include "render.h"
#include "world_point.h"

#include <cmath>
#include <cstdint>
#include <limits>
#include <optional>

namespace titan {

/// One immutable snapshot of the visible native world map. `viewport*` uses
/// physical screen pixels; `logicalViewport*` uses the game's widget-frame
/// coordinates before `interfaceScale*` and `canvasOrigin*` are applied.
/// `globalCenterY` is Titan's public world Y coordinate (the native client
/// calls the same axis Z).
struct WorldMapSnapshot {
    int32_t globalCenterX = 0;
    int32_t globalCenterY = 0;
    float currentZoom = 0.0f;
    float targetZoom = 0.0f;
    float pixelsPerTile = 0.0f;
    int32_t viewportX = 0;
    int32_t viewportY = 0;
    int32_t viewportWidth = 0;
    int32_t viewportHeight = 0;
    int32_t logicalViewportX = 0;
    int32_t logicalViewportY = 0;
    int32_t logicalViewportWidth = 0;
    int32_t logicalViewportHeight = 0;
    float interfaceScaleX = 0.0f;
    float interfaceScaleY = 0.0f;
    int32_t canvasOriginX = 0;
    int32_t canvasOriginY = 0;

    /// Convert a world tile to its physical-pixel centre in the map viewport.
    /// Plane and WorldView identity are intentionally ignored: the native
    /// world map is a global 2D surface. The rounding order matches RLPL's
    /// RuneLite world-map projection, including Java's truncation toward zero.
    std::optional<ScreenPoint> worldToScreen(
            int32_t worldX, int32_t worldY) const noexcept {
        if (!valid()) return std::nullopt;

        const auto logical = worldToLogicalScreen(worldX, worldY);
        if (!logical) return std::nullopt;

        // Match W2S::widgetToWindow exactly: promote the analyzer-provided
        // float scale to double, apply origin, then lround (half away from
        // zero). The public result is always a physical screen pixel.
        const auto physicalX = scaleLogicalCoordinate(
            logical->x, interfaceScaleX, canvasOriginX);
        const auto physicalY = scaleLogicalCoordinate(
            logical->y, interfaceScaleY, canvasOriginY);
        if (!physicalX || !physicalY) return std::nullopt;
        return ScreenPoint{*physicalX, *physicalY};
    }

    std::optional<ScreenPoint> worldToScreen(
            const WorldPoint& point) const noexcept {
        return worldToScreen(point.x, point.y);
    }

    /// Convert a physical screen pixel to the nearest map tile using the same
    /// centre point and Java-style truncation as RLPL. The returned point is a
    /// canonical global, ground-plane coordinate.
    std::optional<WorldPoint> screenToWorld(
            int32_t screenX, int32_t screenY) const noexcept {
        if (!valid()) return std::nullopt;
        const auto middle = worldToLogicalScreen(globalCenterX, globalCenterY);
        if (!middle) return std::nullopt;

        // Invert the UI-frame -> physical transform before applying RLPL's
        // map delta calculation. Preserve the fractional logical coordinate;
        // rounding it first would select the wrong tile near scaled edges.
        const double logicalX =
            (static_cast<double>(screenX) - canvasOriginX) / interfaceScaleX;
        const double logicalY =
            (static_cast<double>(screenY) - canvasOriginY) / interfaceScaleY;
        const auto dx = checkedTruncate(
            (logicalX - static_cast<double>(middle->x)) / pixelsPerTile);
        const auto dy = checkedTruncate(
            -(logicalY - static_cast<double>(middle->y)) / pixelsPerTile);
        if (!dx || !dy) return std::nullopt;

        const int64_t worldX = static_cast<int64_t>(globalCenterX) + *dx;
        const int64_t worldY = static_cast<int64_t>(globalCenterY) + *dy;
        if (!fitsInt32(worldX) || !fitsInt32(worldY)) return std::nullopt;
        return WorldPoint{static_cast<int32_t>(worldX),
                          static_cast<int32_t>(worldY), 0,
                          WorldView::TOP_LEVEL};
    }

    /// Convert logical world-map pixels to tiles. Physical screen pixels are
    /// axis-dependent under interface scaling and must first be inverse-
    /// transformed through screenToWorld or the published scale fields.
    std::optional<float> pixelsToTiles(float pixels) const noexcept {
        return checkedFloatResult(
            static_cast<double>(pixels) / static_cast<double>(pixelsPerTile));
    }

    /// Convert tiles to logical world-map pixels (before interface scaling).
    std::optional<float> tilesToPixels(float tiles) const noexcept {
        return checkedFloatResult(
            static_cast<double>(tiles) * static_cast<double>(pixelsPerTile));
    }

private:
    friend class WorldMapFacade;

    std::optional<ScreenPoint> worldToLogicalScreen(
            int32_t worldX, int32_t worldY) const noexcept {
        if (!valid()) return std::nullopt;

        const auto widthTiles = checkedTruncate(
            std::ceil(static_cast<double>(logicalViewportWidth)
                / static_cast<double>(pixelsPerTile)));
        const auto heightTiles = checkedTruncate(
            std::ceil(static_cast<double>(logicalViewportHeight)
                / static_cast<double>(pixelsPerTile)));
        if (!widthTiles || !heightTiles || *widthTiles <= 0 || *heightTiles <= 0) {
            return std::nullopt;
        }

        const int64_t yTileMax = static_cast<int64_t>(globalCenterY)
            - static_cast<int64_t>(*heightTiles / 2);
        const int64_t yTileOffset = -(yTileMax
            - static_cast<int64_t>(worldY) - 1);
        const int64_t xTileOffset = static_cast<int64_t>(worldX)
            + static_cast<int64_t>(*widthTiles / 2)
            - static_cast<int64_t>(globalCenterX);

        const auto xBase = checkedTruncate(
            static_cast<float>(xTileOffset) * pixelsPerTile);
        const auto yBase = checkedTruncate(
            static_cast<float>(yTileOffset) * pixelsPerTile);
        if (!xBase || !yBase) return std::nullopt;

        const double halfTileAdjustment = static_cast<double>(pixelsPerTile)
            - std::ceil(static_cast<double>(pixelsPerTile) / 2.0);
        const auto xGraph = checkedTruncate(
            static_cast<double>(*xBase) + halfTileAdjustment);
        const auto yGraph = checkedTruncate(
            static_cast<double>(*yBase) - halfTileAdjustment);
        if (!xGraph || !yGraph) return std::nullopt;

        const int64_t screenX = static_cast<int64_t>(logicalViewportX) + *xGraph;
        const int64_t screenY = static_cast<int64_t>(logicalViewportY)
            + static_cast<int64_t>(logicalViewportHeight) - *yGraph;
        if (!fitsInt32(screenX) || !fitsInt32(screenY)) return std::nullopt;
        return ScreenPoint{static_cast<int32_t>(screenX),
                           static_cast<int32_t>(screenY)};
    }

    bool valid() const noexcept {
        return viewportWidth > 0 && viewportHeight > 0
            && logicalViewportWidth > 0 && logicalViewportHeight > 0
            && std::isfinite(currentZoom) && currentZoom > 0.0f
            && std::isfinite(targetZoom) && targetZoom > 0.0f
            && std::isfinite(pixelsPerTile) && pixelsPerTile > 0.0f
            && std::isfinite(interfaceScaleX) && interfaceScaleX > 0.0f
            && std::isfinite(interfaceScaleY) && interfaceScaleY > 0.0f
            && viewportTransformMatches();
    }

    bool viewportTransformMatches() const noexcept {
        const int64_t logicalRight = static_cast<int64_t>(logicalViewportX)
            + logicalViewportWidth;
        const int64_t logicalBottom = static_cast<int64_t>(logicalViewportY)
            + logicalViewportHeight;
        if (!fitsInt32(logicalRight) || !fitsInt32(logicalBottom)) return false;

        const auto left = scaleLogicalCoordinate(
            logicalViewportX, interfaceScaleX, canvasOriginX);
        const auto top = scaleLogicalCoordinate(
            logicalViewportY, interfaceScaleY, canvasOriginY);
        const auto right = scaleLogicalCoordinate(
            static_cast<int32_t>(logicalRight), interfaceScaleX, canvasOriginX);
        const auto bottom = scaleLogicalCoordinate(
            static_cast<int32_t>(logicalBottom), interfaceScaleY, canvasOriginY);
        if (!left || !top || !right || !bottom) return false;

        return *left == viewportX && *top == viewportY
            && static_cast<int64_t>(*right) - *left == viewportWidth
            && static_cast<int64_t>(*bottom) - *top == viewportHeight;
    }

    static bool fitsInt32(int64_t value) noexcept {
        return value >= (std::numeric_limits<int32_t>::min)()
            && value <= (std::numeric_limits<int32_t>::max)();
    }

    static std::optional<int32_t> checkedTruncate(double value) noexcept {
        if (!std::isfinite(value)
            || value < static_cast<double>((std::numeric_limits<int32_t>::min)())
            || value > static_cast<double>((std::numeric_limits<int32_t>::max)())) {
            return std::nullopt;
        }
        return static_cast<int32_t>(value);
    }

    static std::optional<int32_t> checkedRoundAwayFromZero(
            double value) noexcept {
        if (!std::isfinite(value)) return std::nullopt;
        const double rounded = std::round(value);
        if (rounded < static_cast<double>((std::numeric_limits<int32_t>::min)())
            || rounded > static_cast<double>((std::numeric_limits<int32_t>::max)())) {
            return std::nullopt;
        }
        return static_cast<int32_t>(rounded);
    }

    static std::optional<int32_t> scaleLogicalCoordinate(
            int32_t logical, float scale, int32_t origin) noexcept {
        return checkedRoundAwayFromZero(
            static_cast<double>(logical) * static_cast<double>(scale)
            + static_cast<double>(origin));
    }

    static std::optional<float> checkedFloatResult(double value) noexcept {
        if (!std::isfinite(value)
            || value < -static_cast<double>((std::numeric_limits<float>::max)())
            || value > static_cast<double>((std::numeric_limits<float>::max)())) {
            return std::nullopt;
        }
        const float result = static_cast<float>(value);
        return std::isfinite(result) ? std::optional<float>{result}
                                     : std::nullopt;
    }
};

/// Read-only facade over the current native world-map display state.
class WorldMapFacade {
public:
    std::optional<WorldMapSnapshot> snapshot() const noexcept {
        auto* b = detail::backend();
        if (!b) return std::nullopt;

        TitanPluginSdk::WorldMapState state{};
        if (!b->getWorldMapState(&state)
            || state.structSize < sizeof(TitanPluginSdk::WorldMapState)
            || state.apiVersion != TitanPluginSdk::kWorldMapApiVersion
            || state.viewportWidth <= 0 || state.viewportHeight <= 0
            || state.logicalViewportWidth <= 0
            || state.logicalViewportHeight <= 0
            || !std::isfinite(state.currentZoom) || state.currentZoom <= 0.0f
            || !std::isfinite(state.targetZoom) || state.targetZoom <= 0.0f
            || !std::isfinite(state.pixelsPerTile) || state.pixelsPerTile <= 0.0f
            || !std::isfinite(state.interfaceScaleX)
            || state.interfaceScaleX <= 0.0f
            || !std::isfinite(state.interfaceScaleY)
            || state.interfaceScaleY <= 0.0f
            || state.pixelsPerTile != state.currentZoom) {
            return std::nullopt;
        }

        WorldMapSnapshot snapshot{
            state.globalCenterX,
            state.globalCenterY,
            state.currentZoom,
            state.targetZoom,
            state.pixelsPerTile,
            state.viewportX,
            state.viewportY,
            state.viewportWidth,
            state.viewportHeight,
            state.logicalViewportX,
            state.logicalViewportY,
            state.logicalViewportWidth,
            state.logicalViewportHeight,
            state.interfaceScaleX,
            state.interfaceScaleY,
            state.canvasOriginX,
            state.canvasOriginY,
        };
        return snapshot.valid() ? std::optional<WorldMapSnapshot>{snapshot}
                                : std::nullopt;
    }

    std::optional<ScreenPoint> worldToScreen(
            int32_t worldX, int32_t worldY) const noexcept {
        const auto state = snapshot();
        return state ? state->worldToScreen(worldX, worldY) : std::nullopt;
    }

    std::optional<ScreenPoint> worldToScreen(
            const WorldPoint& point) const noexcept {
        const auto state = snapshot();
        return state ? state->worldToScreen(point) : std::nullopt;
    }

    std::optional<WorldPoint> screenToWorld(
            int32_t screenX, int32_t screenY) const noexcept {
        const auto state = snapshot();
        return state ? state->screenToWorld(screenX, screenY) : std::nullopt;
    }

    std::optional<float> pixelsToTiles(float pixels) const noexcept {
        const auto state = snapshot();
        if (!state) return std::nullopt;
        return state->pixelsToTiles(pixels);
    }

    std::optional<float> tilesToPixels(float tiles) const noexcept {
        const auto state = snapshot();
        if (!state) return std::nullopt;
        return state->tilesToPixels(tiles);
    }
};

namespace state {
inline ::titan::WorldMapFacade worldMap() { return {}; }
}  // namespace state

}  // namespace titan
