package net.titan.api;

public enum AnimationSequenceMode {
    UNKNOWN(0), CLASSIC(1), CACHED(2);
    private final int value;
    AnimationSequenceMode(int value) { this.value = value; }
    public int value() { return value; }
    public static AnimationSequenceMode fromValue(int value) {
        for (AnimationSequenceMode mode : values()) if (mode.value == value) return mode;
        return UNKNOWN;
    }
}
