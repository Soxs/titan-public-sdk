package net.titan.api;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Immutable snapshot of the currently displayed in-game world map (SDK 113).
 *
 * <p>Coordinates are canonical global tiles. {@code pixelsPerTile} and the
 * logical viewport use the game's logical widget pixels, while the legacy
 * {@code viewport*} fields and projected points use physical canvas pixels.
 * A snapshot is exposed only while the map widget and its native map area are
 * both available.</p>
 */
public final class WorldMapSnapshot {
    private final int globalCenterX;
    private final int globalCenterY;
    private final float currentZoom;
    private final float targetZoom;
    private final float pixelsPerTile;
    private final int viewportX;
    private final int viewportY;
    private final int viewportWidth;
    private final int viewportHeight;
    private final int logicalViewportX;
    private final int logicalViewportY;
    private final int logicalViewportWidth;
    private final int logicalViewportHeight;
    private final float interfaceScaleX;
    private final float interfaceScaleY;
    private final int canvasOriginX;
    private final int canvasOriginY;

    public WorldMapSnapshot(int globalCenterX, int globalCenterY,
                            float currentZoom, float targetZoom,
                            float pixelsPerTile, int viewportX, int viewportY,
                            int viewportWidth, int viewportHeight) {
        this(globalCenterX, globalCenterY, currentZoom, targetZoom,
            pixelsPerTile, viewportX, viewportY, viewportWidth, viewportHeight,
            viewportX, viewportY, viewportWidth, viewportHeight,
            1.0f, 1.0f, 0, 0);
    }

    public WorldMapSnapshot(int globalCenterX, int globalCenterY,
                            float currentZoom, float targetZoom,
                            float pixelsPerTile, int viewportX, int viewportY,
                            int viewportWidth, int viewportHeight,
                            int logicalViewportX, int logicalViewportY,
                            int logicalViewportWidth, int logicalViewportHeight,
                            float interfaceScaleX, float interfaceScaleY,
                            int canvasOriginX, int canvasOriginY) {
        long logicalRight = (long) logicalViewportX + logicalViewportWidth;
        long logicalBottom = (long) logicalViewportY + logicalViewportHeight;
        Integer expectedViewportX = transformedCoordinate(
            logicalViewportX, interfaceScaleX, canvasOriginX);
        Integer expectedViewportY = transformedCoordinate(
            logicalViewportY, interfaceScaleY, canvasOriginY);
        Integer expectedViewportRight = fitsInt(logicalRight)
            ? transformedCoordinate(logicalRight, interfaceScaleX, canvasOriginX)
            : null;
        Integer expectedViewportBottom = fitsInt(logicalBottom)
            ? transformedCoordinate(logicalBottom, interfaceScaleY, canvasOriginY)
            : null;

        if (!Float.isFinite(currentZoom) || currentZoom <= 0.0f
                || !Float.isFinite(targetZoom) || targetZoom <= 0.0f
                || !Float.isFinite(pixelsPerTile) || pixelsPerTile <= 0.0f
                || pixelsPerTile != currentZoom
                || viewportWidth <= 0 || viewportHeight <= 0
                || logicalViewportWidth <= 0 || logicalViewportHeight <= 0
                || !Float.isFinite(interfaceScaleX) || interfaceScaleX <= 0.0f
                || !Float.isFinite(interfaceScaleY) || interfaceScaleY <= 0.0f
                || expectedViewportX == null || expectedViewportY == null
                || expectedViewportRight == null || expectedViewportBottom == null
                || viewportX != expectedViewportX || viewportY != expectedViewportY
                || (long) viewportWidth
                    != (long) expectedViewportRight - expectedViewportX
                || (long) viewportHeight
                    != (long) expectedViewportBottom - expectedViewportY) {
            throw new IllegalArgumentException("Invalid world-map snapshot");
        }
        this.globalCenterX = globalCenterX;
        this.globalCenterY = globalCenterY;
        this.currentZoom = currentZoom;
        this.targetZoom = targetZoom;
        this.pixelsPerTile = pixelsPerTile;
        this.viewportX = viewportX;
        this.viewportY = viewportY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.logicalViewportX = logicalViewportX;
        this.logicalViewportY = logicalViewportY;
        this.logicalViewportWidth = logicalViewportWidth;
        this.logicalViewportHeight = logicalViewportHeight;
        this.interfaceScaleX = interfaceScaleX;
        this.interfaceScaleY = interfaceScaleY;
        this.canvasOriginX = canvasOriginX;
        this.canvasOriginY = canvasOriginY;
    }

    public int globalCenterX() { return globalCenterX; }
    public int globalCenterY() { return globalCenterY; }
    public float currentZoom() { return currentZoom; }
    public float targetZoom() { return targetZoom; }
    public float pixelsPerTile() { return pixelsPerTile; }
    public int viewportX() { return viewportX; }
    public int viewportY() { return viewportY; }
    public int viewportWidth() { return viewportWidth; }
    public int viewportHeight() { return viewportHeight; }
    public int logicalViewportX() { return logicalViewportX; }
    public int logicalViewportY() { return logicalViewportY; }
    public int logicalViewportWidth() { return logicalViewportWidth; }
    public int logicalViewportHeight() { return logicalViewportHeight; }
    public float interfaceScaleX() { return interfaceScaleX; }
    public float interfaceScaleY() { return interfaceScaleY; }
    public int canvasOriginX() { return canvasOriginX; }
    public int canvasOriginY() { return canvasOriginY; }

    /** Convert a logical widget-pixel distance to world-map tile units. */
    public OptionalDouble pixelsToTiles(float pixels) {
        return checkedFloatResult((double) pixels / pixelsPerTile);
    }

    /** Convert world-map tile units to logical widget pixels. */
    public OptionalDouble tilesToPixels(float tiles) {
        return checkedFloatResult((double) tiles * pixelsPerTile);
    }

    /**
     * Project a canonical global tile using the same truncation and centering
     * rules as the native world-map renderer facade.
     */
    public Optional<ScreenPoint> worldToScreen(int worldX, int worldY) {
        Optional<ScreenPoint> logical = worldToLogicalScreen(worldX, worldY);
        if (!logical.isPresent()) return Optional.empty();

        Integer physicalX = checkedRoundAwayFromZero(
            (double) logical.get().x() * interfaceScaleX + canvasOriginX);
        Integer physicalY = checkedRoundAwayFromZero(
            (double) logical.get().y() * interfaceScaleY + canvasOriginY);
        if (physicalX == null || physicalY == null) return Optional.empty();
        return Optional.of(new ScreenPoint(physicalX, physicalY));
    }

    private Optional<ScreenPoint> worldToLogicalScreen(int worldX, int worldY) {
        Integer widthInTiles = checkedTruncate(
            Math.ceil((double) logicalViewportWidth / pixelsPerTile));
        Integer heightInTiles = checkedTruncate(
            Math.ceil((double) logicalViewportHeight / pixelsPerTile));
        if (widthInTiles == null || heightInTiles == null
                || widthInTiles <= 0 || heightInTiles <= 0) {
            return Optional.empty();
        }

        long yTileMax = (long) globalCenterY - heightInTiles / 2L;
        long yTileOffset = -(yTileMax - (long) worldY - 1L);
        long xTileOffset = (long) worldX + widthInTiles / 2L - globalCenterX;

        Integer xBase = checkedTruncate((float) xTileOffset * pixelsPerTile);
        Integer yBase = checkedTruncate((float) yTileOffset * pixelsPerTile);
        if (xBase == null || yBase == null) return Optional.empty();

        double halfTileAdjustment = (double) pixelsPerTile
            - Math.ceil((double) pixelsPerTile / 2.0);
        Integer xGraph = checkedTruncate((double) xBase + halfTileAdjustment);
        Integer yGraph = checkedTruncate((double) yBase - halfTileAdjustment);
        if (xGraph == null || yGraph == null) return Optional.empty();

        long screenX = (long) logicalViewportX + xGraph;
        long screenY = (long) logicalViewportY + logicalViewportHeight - yGraph;
        if (!fitsInt(screenX) || !fitsInt(screenY)) return Optional.empty();
        return Optional.of(new ScreenPoint((int) screenX, (int) screenY));
    }

    public Optional<ScreenPoint> worldToScreen(WorldPoint point) {
        if (point == null) return Optional.empty();
        return worldToScreen(point.x(), point.y());
    }

    /** Convert a physical map pixel to a canonical global plane-zero tile. */
    public Optional<WorldPoint> screenToWorld(int screenX, int screenY) {
        Optional<ScreenPoint> projectedMiddle = worldToLogicalScreen(
            globalCenterX, globalCenterY);
        if (!projectedMiddle.isPresent()) return Optional.empty();
        ScreenPoint middle = projectedMiddle.get();

        double logicalX = ((double) screenX - canvasOriginX) / interfaceScaleX;
        double logicalY = ((double) screenY - canvasOriginY) / interfaceScaleY;
        Integer dx = checkedTruncate(
            (logicalX - middle.x()) / pixelsPerTile);
        Integer dy = checkedTruncate(
            -(logicalY - middle.y()) / pixelsPerTile);
        if (dx == null || dy == null) return Optional.empty();

        long worldX = (long) globalCenterX + dx;
        long worldY = (long) globalCenterY + dy;
        if (!fitsInt(worldX) || !fitsInt(worldY)) return Optional.empty();
        return Optional.of(new WorldPoint(
            (int) worldX, (int) worldY, 0, WorldView.TOP_LEVEL));
    }

    public Optional<WorldPoint> screenToWorld(ScreenPoint point) {
        if (point == null) return Optional.empty();
        return screenToWorld(point.x(), point.y());
    }

    private static Integer checkedTruncate(double value) {
        if (!Double.isFinite(value)
                || value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            return null;
        }
        return (int) value;
    }

    private static boolean fitsInt(long value) {
        return value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
    }

    private static Integer checkedRoundAwayFromZero(double value) {
        if (!Double.isFinite(value)) return null;
        double rounded = value < 0.0
            ? Math.ceil(value - 0.5)
            : Math.floor(value + 0.5);
        return rounded < Integer.MIN_VALUE || rounded > Integer.MAX_VALUE
            ? null
            : (int) rounded;
    }

    private static Integer transformedCoordinate(
            long logical, float scale, int origin) {
        return checkedRoundAwayFromZero((double) logical * scale + origin);
    }

    private static OptionalDouble checkedFloatResult(double value) {
        if (!Double.isFinite(value)
                || value < -Float.MAX_VALUE || value > Float.MAX_VALUE) {
            return OptionalDouble.empty();
        }
        float result = (float) value;
        return Float.isFinite(result)
            ? OptionalDouble.of(result)
            : OptionalDouble.empty();
    }
}
