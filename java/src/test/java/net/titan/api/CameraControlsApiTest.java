package net.titan.api;

import net.titan.api.internal.LiveStateBackend;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class CameraControlsApiTest {
    @Test void pitchUnlockRoundTripsThroughTheHost() {
        RecordingBackend backend = new RecordingBackend();
        TitanRuntime.setLiveStateBackend(backend);
        try {
            Camera camera = new Camera();
            assertEquals(Optional.of(false), camera.pitchUnlocked());
            assertTrue(camera.setPitchUnlocked(true));
            assertEquals(Optional.of(true), camera.pitchUnlocked());
            backend.rejectWrites = true;
            assertFalse(camera.setPitchUnlocked(false), "a rejected host write reports false");
            assertEquals(Optional.of(true), camera.pitchUnlocked());
        } finally { TitanRuntime.clearLiveStateBackend(backend); }
    }

    @Test void pitchTargetRoundTripsThroughTheHost() {
        RecordingBackend backend = new RecordingBackend();
        TitanRuntime.setLiveStateBackend(backend);
        try {
            Camera camera = new Camera();
            assertEquals(Optional.of(2832), camera.pitchTarget());
            assertTrue(camera.setPitchTarget(1024));
            assertEquals(Optional.of(1024), camera.pitchTarget());
            backend.pitchTarget = null;
            assertEquals(Optional.empty(), camera.pitchTarget());
        } finally { TitanRuntime.clearLiveStateBackend(backend); }

        LiveStateBackend passive = new PassiveBackend();
        TitanRuntime.setLiveStateBackend(passive);
        try {
            assertEquals(Optional.empty(), new Camera().pitchTarget());
            assertFalse(new Camera().setPitchTarget(1024));
        } finally { TitanRuntime.clearLiveStateBackend(passive); }
    }

    @Test void fovAndClampForwardEveryArgumentInOrder() {
        RecordingBackend backend = new RecordingBackend();
        TitanRuntime.setLiveStateBackend(backend);
        try {
            Camera camera = new Camera();
            assertEquals(Optional.of(new ViewportFov(256, 256)), camera.fov());
            assertTrue(camera.setFov(new ViewportFov(90, 181)));
            assertArrayEquals(new int[]{90, 181}, backend.fov);
            assertEquals(Optional.of(new ViewportFov(90, 181)), camera.fov());
            assertFalse(camera.setFov(null));

            assertEquals(Optional.of(new ViewportFovClamp(1, 0x7FFF, 1, 0x7FFF)), camera.fovClamp());
            assertTrue(camera.setFovClamp(new ViewportFovClamp(448, 560, 448, 480)));
            assertArrayEquals(new int[]{448, 560, 448, 480}, backend.clamp);
            assertFalse(camera.setFovClamp(null));
        } finally { TitanRuntime.clearLiveStateBackend(backend); }
    }

    @Test void snapshotsReadTheLiveClientControls() {
        RecordingBackend backend = new RecordingBackend();
        TitanRuntime.setLiveStateBackend(backend);
        try {
            Camera snapshot = new Camera().snapshot();
            backend.unlocked = 1;
            backend.fov = new int[]{128, 181};
            assertEquals(Optional.of(true), snapshot.pitchUnlocked());
            assertEquals(Optional.of(new ViewportFov(128, 181)), snapshot.fov());
        } finally { TitanRuntime.clearLiveStateBackend(backend); }
    }

    @Test void unavailableHostsAndMalformedRepliesReportEmpty() {
        RecordingBackend backend = new RecordingBackend();
        TitanRuntime.setLiveStateBackend(backend);
        try {
            backend.unlocked = -1;
            backend.fov = new int[]{1};
            backend.clamp = null;
            Camera camera = new Camera();
            assertEquals(Optional.empty(), camera.pitchUnlocked());
            assertEquals(Optional.empty(), camera.fov(), "a short reply is not a value");
            assertEquals(Optional.empty(), camera.fovClamp());
        } finally { TitanRuntime.clearLiveStateBackend(backend); }

        LiveStateBackend passive = new PassiveBackend();
        TitanRuntime.setLiveStateBackend(passive);
        try {
            Camera camera = new Camera();
            assertEquals(Optional.empty(), camera.pitchUnlocked());
            assertFalse(camera.setPitchUnlocked(true));
            assertEquals(Optional.empty(), camera.fov());
            assertFalse(camera.setFov(new ViewportFov(256, 256)));
            assertEquals(Optional.empty(), camera.fovClamp());
            assertFalse(camera.setFovClamp(new ViewportFovClamp(1, 1, 1, 1)));
        } finally { TitanRuntime.clearLiveStateBackend(passive); }
    }

    @Test void valueClassesCompareByValue() {
        assertEquals(new ViewportFov(1, 2), new ViewportFov(1, 2));
        assertNotEquals(new ViewportFov(1, 2), new ViewportFov(2, 1));
        assertEquals(new ViewportFovClamp(1, 2, 3, 4).hashCode(), new ViewportFovClamp(1, 2, 3, 4).hashCode());
        assertNotEquals(new ViewportFovClamp(1, 2, 3, 4), new ViewportFovClamp(1, 2, 4, 3));
    }

    private static class PassiveBackend implements LiveStateBackend {
        public <T> T current(T value) { return value; }
        public boolean exists(Object value) { return true; }
        public String identity(Object value) { return null; }
        public <T> T snapshot(T value) { return value; }
    }

    private static final class RecordingBackend extends PassiveBackend {
        int unlocked;
        Integer pitchTarget = 2832;
        int[] fov = {256, 256};
        int[] clamp = {1, 0x7FFF, 1, 0x7FFF};
        boolean rejectWrites;

        @Override public Boolean cameraPitchUnlocked() { return unlocked < 0 ? null : unlocked != 0; }
        @Override public boolean setCameraPitchUnlocked(boolean value) {
            if (rejectWrites) return false;
            unlocked = value ? 1 : 0;
            return true;
        }
        @Override public Integer cameraPitchTarget() { return pitchTarget; }
        @Override public boolean setCameraPitchTarget(int pitch) {
            pitchTarget = pitch;
            return !rejectWrites;
        }
        @Override public int[] viewportFov() { return fov == null ? null : fov.clone(); }
        @Override public boolean setViewportFov(int smallViewport, int largeViewport) {
            fov = new int[]{smallViewport, largeViewport};
            return !rejectWrites;
        }
        @Override public int[] viewportFovClamp() { return clamp == null ? null : clamp.clone(); }
        @Override public boolean setViewportFovClamp(int fovMin, int fovMax, int zoomMin, int zoomMax) {
            clamp = new int[]{fovMin, fovMax, zoomMin, zoomMax};
            return !rejectWrites;
        }
    }
}
