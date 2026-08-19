package net.titan.api;

/** Terminal result reported by the web walker. */
public enum WebPathResult {
    NONE(0),
    EXACT(1),
    PARTIAL_WITHIN_THREE_TILES(2),
    NO_PATH(3),
    TIMEOUT(4),
    CANCELLED(5),
    INVALID_REQUEST(6),
    NOT_LOGGED_IN(7),
    COLLISION_UNAVAILABLE(8),
    PROVIDER_UNAVAILABLE(9),
    BUSY(10),
    INTERNAL_ERROR(11),
    UNKNOWN(-1);

    private final int value;

    WebPathResult(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static WebPathResult fromValue(int value) {
        for (WebPathResult result : values()) {
            if (result.value == value) return result;
        }
        return UNKNOWN;
    }
}
