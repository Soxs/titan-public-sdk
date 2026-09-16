package net.titan.api;

/** Lifecycle state of an asynchronous full-frame screenshot request (SDK 131). */
public enum ScreenshotPhase {
    NONE(0),
    /** Waiting for the next presented frame, or encoding it. */
    PENDING(1),
    /** PNG bytes are available through {@link Screenshot#copyPng(long)}. */
    READY(2),
    FAILED(3),
    UNKNOWN(-1);

    private final int value;

    ScreenshotPhase(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean finished() {
        return this == READY || this == FAILED;
    }

    public static ScreenshotPhase fromValue(int value) {
        for (ScreenshotPhase phase : values()) {
            if (phase.value == value) return phase;
        }
        return UNKNOWN;
    }
}
