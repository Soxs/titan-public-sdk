package net.titan.api;

import net.titan.api.internal.TitanRuntime;
import net.titan.api.internal.WorldMapBackend;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldMapApiTest {
    private WorldMapBackend backend;

    @AfterEach
    void clearBackend() {
        if (backend != null) TitanRuntime.clearWorldMapBackend(backend);
    }

    @Test
    void unavailableBackendSnapshotFailsClosed() {
        backend = Optional::empty;
        TitanRuntime.setWorldMapBackend(backend);

        assertFalse(Titan.worldMap().snapshot().isPresent());
        assertFalse(Titan.worldMap().worldToScreen(3200, 3200).isPresent());
        assertFalse(Titan.worldMap().screenToWorld(400, 300).isPresent());
        assertFalse(Titan.worldMap().pixelsToTiles(12.0f).isPresent());
    }

    @Test
    void facadeUsesOneValidImmutableSnapshot() {
        WorldMapSnapshot state = new WorldMapSnapshot(
            3200, 3200, 4.0f, 8.0f, 4.0f,
            100, 200, 800, 600);
        backend = () -> Optional.of(state);
        TitanRuntime.setWorldMapBackend(backend);

        assertEquals(3200, Titan.worldMap().snapshot().orElseThrow().globalCenterX());
        assertEquals(8.0f, state.targetZoom());
        assertEquals(4.0f, state.currentZoom());

        ScreenPoint center = Titan.worldMap().worldToScreen(3200, 3200).orElseThrow();
        assertEquals(502, center.x());
        assertEquals(498, center.y());

        ScreenPoint projected = state.worldToScreen(
            new WorldPoint(3205, 3197, 0)).orElseThrow();
        assertEquals(522, projected.x());
        assertEquals(510, projected.y());

        WorldPoint restored = state.screenToWorld(projected).orElseThrow();
        assertEquals(new WorldPoint(3205, 3197, 0, WorldView.TOP_LEVEL), restored);
        assertEquals(3.0, Titan.worldMap().pixelsToTiles(12.0f).orElseThrow(), 0.0001);
        assertEquals(12.0, Titan.worldMap().tilesToPixels(3.0f).orElseThrow(), 0.0001);
        assertFalse(state.tilesToPixels(Float.MAX_VALUE).isPresent());
    }

    @Test
    void panningChangesProjectionWithoutChangingWorldPoint() {
        WorldMapSnapshot before = new WorldMapSnapshot(
            3200, 3200, 4.0f, 4.0f, 4.0f,
            0, 0, 800, 600);
        WorldMapSnapshot after = new WorldMapSnapshot(
            3201, 3198, 4.0f, 4.0f, 4.0f,
            0, 0, 800, 600);

        ScreenPoint first = before.worldToScreen(3200, 3200).orElseThrow();
        ScreenPoint second = after.worldToScreen(3200, 3200).orElseThrow();
        assertEquals(first.x() - 4, second.x());
        assertEquals(first.y() - 8, second.y());
    }

    @Test
    void viewportTileCeilRetainsRlplDoubleDivisionOrder() {
        // 7 / 1.4f rounds to exactly 5 when evaluated as float, but is
        // slightly greater than 5 when Java promotes the float zoom to double.
        // RLPL uses Rectangle.getWidth() (double), so the correct ceil is 6.
        WorldMapSnapshot state = new WorldMapSnapshot(
            100, 100, 1.4f, 1.4f, 1.4f,
            0, 0, 7, 7);

        ScreenPoint center = state.worldToScreen(100, 100).orElseThrow();
        assertEquals(4, center.x());
        assertEquals(3, center.y());
    }

    @Test
    void fractionalProjectionMatchesNativeSdkContract() {
        WorldMapSnapshot state = new WorldMapSnapshot(
            3200, 3200, 2.5f, 4.0f, 2.5f,
            100, 200, 1000, 800);

        ScreenPoint center = state.worldToScreen(3200, 3200).orElseThrow();
        assertEquals(600, center.x());
        assertEquals(599, center.y());
        assertEquals(
            new WorldPoint(3202, 3202, 0, WorldView.TOP_LEVEL),
            state.screenToWorld(605, 594).orElseThrow());
    }

    @Test
    void projectionAppliesOnePointFiveInterfaceScale() {
        WorldMapSnapshot state = scaledSnapshot(1.5f, 1.5f, 10, 20);

        ScreenPoint center = state.worldToScreen(3200, 3200).orElseThrow();
        assertEquals(910, center.x());
        assertEquals(919, center.y());
        assertEquals(
            new WorldPoint(3202, 3202, 0, WorldView.TOP_LEVEL),
            state.screenToWorld(918, 911).orElseThrow());

        // logical Y=599 -> physical -1.5; C++ lround is half-away from zero.
        WorldMapSnapshot negativeHalf = scaledSnapshot(1.5f, 1.5f, 0, -900);
        assertEquals(-2,
            negativeHalf.worldToScreen(3200, 3200).orElseThrow().y());
    }

    @Test
    void projectionAppliesTwoTimesInterfaceScale() {
        WorldMapSnapshot state = scaledSnapshot(2.0f, 2.0f, -50, 30);

        ScreenPoint center = state.worldToScreen(3200, 3200).orElseThrow();
        assertEquals(1150, center.x());
        assertEquals(1228, center.y());
        assertEquals(
            new WorldPoint(3202, 3202, 0, WorldView.TOP_LEVEL),
            state.screenToWorld(1160, 1218).orElseThrow());
    }

    @Test
    void projectionSupportsNonUniformInterfaceScale() {
        WorldMapSnapshot state = scaledSnapshot(1.5f, 2.0f, 10, -20);

        ScreenPoint center = state.worldToScreen(3200, 3200).orElseThrow();
        assertEquals(910, center.x());
        assertEquals(1178, center.y());
        assertEquals(
            new WorldPoint(3202, 3202, 0, WorldView.TOP_LEVEL),
            state.screenToWorld(918, 1168).orElseThrow());

        // Pixel/tile helpers deliberately stay in logical CurrentZoom units.
        assertEquals(4.0, state.pixelsToTiles(10.0f).orElseThrow(), 0.0001);
        assertEquals(10.0, state.tilesToPixels(4.0f).orElseThrow(), 0.0001);
    }

    @Test
    void malformedSnapshotsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new WorldMapSnapshot(
            3200, 3200, Float.NaN, 4.0f, 4.0f,
            0, 0, 800, 600));
        assertThrows(IllegalArgumentException.class, () -> new WorldMapSnapshot(
            3200, 3200, 4.0f, 4.0f, 0.0f,
            0, 0, 800, 600));
        assertThrows(IllegalArgumentException.class, () -> new WorldMapSnapshot(
            3200, 3200, 4.0f, 4.0f, 8.0f,
            0, 0, 800, 600));
        assertThrows(IllegalArgumentException.class, () -> new WorldMapSnapshot(
            3200, 3200, 4.0f, 4.0f, 4.0f,
            0, 0, 0, 600));
        assertThrows(IllegalArgumentException.class, () -> new WorldMapSnapshot(
            3200, 3200, 4.0f, 4.0f, 4.0f,
            0, 0, 800, 600,
            0, 0, 0, 600,
            1.0f, 1.0f, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new WorldMapSnapshot(
            3200, 3200, 4.0f, 4.0f, 4.0f,
            0, 0, 800, 600,
            0, 0, 800, 600,
            0.0f, 1.0f, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new WorldMapSnapshot(
            3200, 3200, 4.0f, 4.0f, 4.0f,
            1, 0, 800, 600,
            0, 0, 800, 600,
            1.0f, 1.0f, 0, 0));
    }

    private static WorldMapSnapshot scaledSnapshot(
            float scaleX, float scaleY, int originX, int originY) {
        return new WorldMapSnapshot(
            3200, 3200, 2.5f, 4.0f, 2.5f,
            Math.round(100 * scaleX + originX),
            Math.round(200 * scaleY + originY),
            Math.round(1000 * scaleX),
            Math.round(800 * scaleY),
            100, 200, 1000, 800,
            scaleX, scaleY, originX, originY);
    }
}
