package net.titan.api;

import net.titan.api.internal.TitanRuntime;

import java.util.Optional;
import java.util.OptionalLong;

/**
 * SDK-v131 facade for asynchronous full-frame game screenshots.
 *
 * <p>Returns the same image the controller's {@code /tabs} command does: the
 * game's presented backbuffer after the AboveWidgets overlay pass, encoded as
 * PNG. Capture happens on the next presented frame and encoding on a worker
 * thread, so {@link #submit()} hands back a handle to {@link #poll(long)} for
 * {@link ScreenshotPhase#READY}, then {@link #copyPng(long)} and
 * {@link #release(long)}. Callable from any plugin callback thread.
 */
public final class Screenshot {
    /** Upper bound on unreleased requests; submit() fails once it is reached. */
    public static final int MAX_UNRELEASED = 4;

    Screenshot() {}

    /** Queue one capture, or empty when the host cannot accept another request. */
    public OptionalLong submit() {
        return TitanRuntime.getScreenshotBackend().submitScreenshot();
    }

    public Optional<ScreenshotStatus> poll(long requestId) {
        return TitanRuntime.getScreenshotBackend().pollScreenshot(requestId);
    }

    /** PNG bytes of a {@link ScreenshotPhase#READY} request; empty for anything else. */
    public Optional<byte[]> copyPng(long requestId) {
        return TitanRuntime.getScreenshotBackend().copyScreenshotPng(requestId);
    }

    /** Release retained state; the handle must not be used after this succeeds. */
    public boolean release(long requestId) {
        return TitanRuntime.getScreenshotBackend().releaseScreenshot(requestId);
    }
}
