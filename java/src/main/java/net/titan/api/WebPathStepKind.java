package net.titan.api;

/** Kind of edge selected for a generated web path. */
public enum WebPathStepKind {
    WALK(0),
    TRANSPORT(1),
    TELEPORT(2),
    UNKNOWN(-1);

    private final int value;

    WebPathStepKind(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static WebPathStepKind fromValue(int value) {
        for (WebPathStepKind kind : values()) {
            if (kind.value == value) return kind;
        }
        return UNKNOWN;
    }
}
