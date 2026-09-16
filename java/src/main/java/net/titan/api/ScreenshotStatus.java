package net.titan.api;

import java.util.Objects;

/** Immutable status snapshot for a submitted screenshot request (SDK 131). */
public final class ScreenshotStatus {
    private final long requestId;
    private final ScreenshotPhase phase;
    private final int width;
    private final int height;
    private final long pngBytes;
    private final String message;

    public ScreenshotStatus(long requestId, ScreenshotPhase phase, int width, int height,
                            long pngBytes, String message) {
        this.requestId = requestId;
        this.phase = Objects.requireNonNull(phase, "phase");
        this.width = width;
        this.height = height;
        this.pngBytes = pngBytes;
        this.message = message == null ? "" : message;
    }

    public long requestId() { return requestId; }
    public ScreenshotPhase phase() { return phase; }
    /** Captured frame width in pixels; zero until {@link ScreenshotPhase#READY}. */
    public int width() { return width; }
    /** Captured frame height in pixels; zero until {@link ScreenshotPhase#READY}. */
    public int height() { return height; }
    /** Encoded PNG size in bytes; zero until {@link ScreenshotPhase#READY}. */
    public long pngBytes() { return pngBytes; }
    /** Failure reason, or empty. */
    public String message() { return message; }
    public boolean finished() { return phase.finished(); }
}
