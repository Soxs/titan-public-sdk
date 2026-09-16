package net.titan.api;

import net.titan.api.internal.ScreenshotBackend;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScreenshotApiTest {
    private ScreenshotBackend backend;

    @AfterEach
    void clearBackend() {
        if (backend != null) TitanRuntime.clearScreenshotBackend(backend);
    }

    @Test
    void fixedAbiCapIsMirrored() {
        assertEquals(4, Screenshot.MAX_UNRELEASED);
    }

    @Test
    void phaseMappingMatchesAbi() {
        assertEquals(ScreenshotPhase.NONE, ScreenshotPhase.fromValue(0));
        assertEquals(ScreenshotPhase.PENDING, ScreenshotPhase.fromValue(1));
        assertEquals(ScreenshotPhase.READY, ScreenshotPhase.fromValue(2));
        assertEquals(ScreenshotPhase.FAILED, ScreenshotPhase.fromValue(3));
        assertEquals(ScreenshotPhase.UNKNOWN, ScreenshotPhase.fromValue(99));
        assertTrue(ScreenshotPhase.READY.finished());
        assertTrue(ScreenshotPhase.FAILED.finished());
        assertFalse(ScreenshotPhase.PENDING.finished());
    }

    @Test
    void missingBackendFailsClosed() {
        assertThrows(IllegalStateException.class, () -> Titan.screenshot().submit());
    }

    @Test
    void facadeForwardsEveryCall() {
        byte[] png = {(byte) 0x89, 'P', 'N', 'G'};
        ScreenshotStatus ready = new ScreenshotStatus(
            7L, ScreenshotPhase.READY, 1920, 1080, png.length, "");
        backend = new ScreenshotBackend() {
            @Override public OptionalLong submitScreenshot() { return OptionalLong.of(7L); }
            @Override public Optional<ScreenshotStatus> pollScreenshot(long requestId) {
                return requestId == 7L ? Optional.of(ready) : Optional.empty();
            }
            @Override public Optional<byte[]> copyScreenshotPng(long requestId) {
                return requestId == 7L ? Optional.of(png) : Optional.empty();
            }
            @Override public boolean releaseScreenshot(long requestId) { return requestId == 7L; }
        };
        TitanRuntime.setScreenshotBackend(backend);

        long handle = Titan.screenshot().submit().orElseThrow();
        assertEquals(7L, handle);

        ScreenshotStatus status = Titan.screenshot().poll(handle).orElseThrow();
        assertEquals(ScreenshotPhase.READY, status.phase());
        assertEquals(1920, status.width());
        assertEquals(1080, status.height());
        assertEquals(png.length, status.pngBytes());
        assertTrue(status.finished());
        assertEquals("", status.message());

        assertArrayEquals(png, Titan.screenshot().copyPng(handle).orElseThrow());
        assertFalse(Titan.screenshot().copyPng(8L).isPresent());
        assertFalse(Titan.screenshot().poll(8L).isPresent());
        assertTrue(Titan.screenshot().release(handle));
        assertFalse(Titan.screenshot().release(8L));
    }

    @Test
    void statusNormalisesNullMessage() {
        ScreenshotStatus failed = new ScreenshotStatus(
            1L, ScreenshotPhase.FAILED, 0, 0, 0, null);
        assertEquals("", failed.message());
        assertTrue(failed.finished());
    }
}
