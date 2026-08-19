package net.titan.api;

/** Lifecycle phase of a host-driven web-walk session (SDK 114). */
public enum WebWalkPhase {
    NONE(0),
    PLANNING(1),
    WALKING(2),
    TRANSITING(3),
    ARRIVED(4),
    FAILED(5),
    CANCELLED(6),
    UNKNOWN(-1);

    private final int value;

    WebWalkPhase(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean finished() {
        return this == ARRIVED || this == FAILED || this == CANCELLED;
    }

    public static WebWalkPhase fromValue(int value) {
        for (WebWalkPhase phase : values()) {
            if (phase.value == value) return phase;
        }
        return UNKNOWN;
    }
}
