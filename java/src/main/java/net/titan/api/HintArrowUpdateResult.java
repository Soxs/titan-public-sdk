package net.titan.api;

/** Actual result of a synchronous, game-thread hint-arrow mutation (SDK 148). */
public enum HintArrowUpdateResult {
    UNAVAILABLE(0), APPLIED(1), WRONG_THREAD(2), INVALID_ARGUMENT(3),
    TARGET_UNAVAILABLE(4), NO_SERVER_ARROW(5), WRITE_FAILED(6);

    private final int value;
    HintArrowUpdateResult(int value) { this.value = value; }
    public int value() { return value; }
    public static HintArrowUpdateResult fromValue(int value) {
        for (HintArrowUpdateResult result : values()) if (result.value == value) return result;
        return UNAVAILABLE;
    }
}
