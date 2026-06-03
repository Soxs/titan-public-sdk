package net.titan.api;

public enum GroundItemOwnership {
    NONE(0),
    SELF_PLAYER(1),
    OTHER_PLAYER(2),
    GROUP_IRONMAN(3);

    private final long id;

    GroundItemOwnership(long id) {
        this.id = id;
    }

    public long id() { return id; }

    public static GroundItemOwnership fromId(long id) {
        for (GroundItemOwnership value : values()) {
            if (value.id == id) return value;
        }
        return NONE;
    }
}
