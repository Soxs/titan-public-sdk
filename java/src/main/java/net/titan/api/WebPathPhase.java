package net.titan.api;

/** Lifecycle state of an asynchronous web-path request. */
public enum WebPathPhase {
    NONE(0),
    QUEUED(1),
    RUNNING(2),
    COMPLETE(3),
    FAILED(4),
    CANCELLED(5),
    UNKNOWN(-1);

    private final int value;

    WebPathPhase(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean finished() {
        return this == COMPLETE || this == FAILED || this == CANCELLED;
    }

    public static WebPathPhase fromValue(int value) {
        for (WebPathPhase phase : values()) {
            if (phase.value == value) return phase;
        }
        return UNKNOWN;
    }
}
