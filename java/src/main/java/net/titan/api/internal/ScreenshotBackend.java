package net.titan.api.internal;

import net.titan.api.ScreenshotStatus;

import java.util.Optional;
import java.util.OptionalLong;

/**
 * Internal runtime bridge behind the public {@code Titan.screenshot()} facade
 * (SDK 131). Method names carry the "Screenshot" suffix because the one
 * runtime bridge class implements this next to {@link WebWalkerBackend}, whose
 * {@code poll(long)} / {@code release(long)} would otherwise clash.
 */
public interface ScreenshotBackend {
    OptionalLong submitScreenshot();
    Optional<ScreenshotStatus> pollScreenshot(long requestId);
    Optional<byte[]> copyScreenshotPng(long requestId);
    boolean releaseScreenshot(long requestId);
}
