package net.titan.api;

/** Result of a guarded, game-thread actor snapshot read. */
public enum ActorDataStatus {
    UNAVAILABLE(0), INVALID_ACTOR(1), UNREADABLE(2), INACTIVE(3), ACTIVE(4),
    BUFFER_TOO_SMALL(5), WRONG_THREAD(6);

    /** For NPC customisations, INACTIVE means no active override object. */
    public static final ActorDataStatus NONE = INACTIVE;
    private final int value;
    ActorDataStatus(int value) { this.value = value; }
    public int value() { return value; }
    public static ActorDataStatus fromValue(int value) {
        for (ActorDataStatus status : values()) if (status.value == value) return status;
        return UNAVAILABLE;
    }
}
