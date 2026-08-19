package net.titan.api;

import net.titan.api.internal.TitanRuntime;

import java.util.Optional;
import java.util.OptionalDouble;

/** SDK-v113 facade for the currently displayed in-game world map. */
public final class WorldMap {
    WorldMap() {}

    /** Return one immutable state snapshot, or empty when the map is unavailable. */
    public Optional<WorldMapSnapshot> snapshot() {
        return TitanRuntime.getWorldMapBackend().snapshot();
    }

    public Optional<ScreenPoint> worldToScreen(int worldX, int worldY) {
        return snapshot().flatMap(state -> state.worldToScreen(worldX, worldY));
    }

    public Optional<ScreenPoint> worldToScreen(WorldPoint point) {
        if (point == null) return Optional.empty();
        return worldToScreen(point.x(), point.y());
    }

    public Optional<WorldPoint> screenToWorld(int screenX, int screenY) {
        return snapshot().flatMap(state -> state.screenToWorld(screenX, screenY));
    }

    public Optional<WorldPoint> screenToWorld(ScreenPoint point) {
        if (point == null) return Optional.empty();
        return screenToWorld(point.x(), point.y());
    }

    /** Convert logical world-map widget pixels to tiles. */
    public OptionalDouble pixelsToTiles(float pixels) {
        Optional<WorldMapSnapshot> state = snapshot();
        return state.isPresent()
            ? state.get().pixelsToTiles(pixels)
            : OptionalDouble.empty();
    }

    /** Convert tiles to logical world-map widget pixels before UI scaling. */
    public OptionalDouble tilesToPixels(float tiles) {
        Optional<WorldMapSnapshot> state = snapshot();
        return state.isPresent()
            ? state.get().tilesToPixels(tiles)
            : OptionalDouble.empty();
    }
}
