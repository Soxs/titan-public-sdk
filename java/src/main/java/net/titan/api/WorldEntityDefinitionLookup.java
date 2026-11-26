package net.titan.api;

/** An owned definition observation through a particular top-level world entity. */
public final class WorldEntityDefinitionLookup {
    public final WorldEntityLookup.Status status;
    public final int index, definitionId;
    /** Opaque unsigned token bits, valid only for their original game. */
    public final long parentIncarnation, entityIncarnation, definitionIncarnation;

    public WorldEntityDefinitionLookup(WorldEntityLookup.Status status, int index, int definitionId,
                                       long parentIncarnation, long entityIncarnation, long definitionIncarnation) {
        this.status = java.util.Objects.requireNonNull(status);
        this.index = index;
        this.definitionId = definitionId;
        this.parentIncarnation = parentIncarnation;
        this.entityIncarnation = entityIncarnation;
        this.definitionIncarnation = definitionIncarnation;
    }
    public static WorldEntityDefinitionLookup unavailable(int index) {
        return new WorldEntityDefinitionLookup(WorldEntityLookup.Status.UNAVAILABLE, index, -1, 0, 0, 0);
    }
}
