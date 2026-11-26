package net.titan.api;

/** An owned result of an indexed read of the top-level world's entity table. */
public final class WorldEntityLookup {
    public enum Status { UNAVAILABLE, ABSENT, PRESENT, STALE }
    public final Status status;
    public final int index, definitionId, childWorldId;
    /** Opaque unsigned token bits. A zero child token means the child link was unavailable. */
    public final long parentIncarnation, entityIncarnation, childWorldIncarnation;

    public WorldEntityLookup(Status status, int index, int definitionId, int childWorldId,
                             long parentIncarnation, long entityIncarnation, long childWorldIncarnation) {
        this.status = java.util.Objects.requireNonNull(status);
        this.index = index;
        this.definitionId = definitionId;
        this.childWorldId = childWorldId;
        this.parentIncarnation = parentIncarnation;
        this.entityIncarnation = entityIncarnation;
        this.childWorldIncarnation = childWorldIncarnation;
    }

    public static WorldEntityLookup unavailable(int index) {
        return new WorldEntityLookup(Status.UNAVAILABLE, index, -1, -1, 0, 0, 0);
    }
}
