package net.titan.api;

/** SDK 148 normalized hint-arrow kind; raw native values remain on the snapshot. */
public enum HintArrowKind {
    NONE(0), NPC(1), COORDINATE(2), PLAYER(3), WORLD_ENTITY(4), UNKNOWN(5);

    private final int value;
    HintArrowKind(int value) { this.value = value; }
    public int value() { return value; }
    public static HintArrowKind fromValue(int value) {
        for (HintArrowKind kind : values()) if (kind.value == value) return kind;
        return UNKNOWN;
    }
}
