package net.titan.api;

import java.util.Objects;
import java.util.Optional;

/** Immutable SDK 148 arrow fields. An attached actor is a live handle whose state may change. */
public final class HintArrow {
    private final int slot, rawKind, targetIndex, flashPeriod, flashThreshold;
    private final HintArrowKind kind;
    private final HintArrowLocation coordinates;
    private final boolean drawInWorld, targetResolved;
    private final int actorWorldViewId;
    private final long actorEntityPtr, actorWorldViewPtr;
    private final Actor target;

    public HintArrow(int slot, HintArrowKind kind, int rawKind, int targetIndex,
            int tileX, int tileY, int subX, int subY, int height, int flashPeriod,
            int flashThreshold, boolean drawInWorld, boolean targetResolved,
            int actorWorldViewId, long actorEntityPtr, long actorWorldViewPtr, Actor target) {
        this(slot, kind, rawKind, targetIndex, tileX, tileY, subX, subY, height, flashPeriod,
            flashThreshold, drawInWorld, targetResolved, actorWorldViewId, actorEntityPtr, actorWorldViewPtr, target, null);
    }

    public HintArrow(int slot, HintArrowKind kind, int rawKind, int targetIndex,
            int tileX, int tileY, int subX, int subY, int height, int flashPeriod,
            int flashThreshold, boolean drawInWorld, boolean targetResolved,
            int actorWorldViewId, long actorEntityPtr, long actorWorldViewPtr, Actor target, WorldPoint worldPoint) {
        if (slot < 0) throw new IllegalArgumentException("negative hint-arrow slot");
        this.slot = slot; this.kind = Objects.requireNonNull(kind, "kind");
        this.rawKind = rawKind; this.targetIndex = targetIndex;
        this.coordinates = new HintArrowLocation(tileX, tileY, subX, subY, height,
            kind == HintArrowKind.COORDINATE ? worldPoint : null);
        this.flashPeriod = flashPeriod; this.flashThreshold = flashThreshold;
        this.drawInWorld = drawInWorld;
        this.targetResolved = targetResolved && (kind == HintArrowKind.NPC || kind == HintArrowKind.PLAYER)
            && targetIndex >= 0 && actorWorldViewId >= 0 && actorEntityPtr != 0 && actorWorldViewPtr != 0;
        this.actorWorldViewId = this.targetResolved ? actorWorldViewId : -1;
        this.actorEntityPtr = this.targetResolved ? actorEntityPtr : 0;
        this.actorWorldViewPtr = this.targetResolved ? actorWorldViewPtr : 0;
        this.target = matches(target) ? target : null;
    }

    public int slot() { return slot; }
    public HintArrowKind kind() { return kind; }
    public int rawKind() { return rawKind; }
    /** Index retained even when the actor is outside the loaded scene. */
    public int targetIndex() { return targetIndex; }
    public int flashPeriod() { return flashPeriod; }
    public int flashThreshold() { return flashThreshold; }
    public boolean drawInWorld() { return drawInWorld; }
    /** The native read found an NPC/player identity; target() may still be empty after a later refresh. */
    public boolean targetResolved() { return targetResolved; }
    public int actorWorldViewId() { return actorWorldViewId; }
    public long actorEntityPtr() { return actorEntityPtr; }
    public long actorWorldViewPtr() { return actorWorldViewPtr; }
    public Optional<HintArrowLocation> location() {
        return kind == HintArrowKind.COORDINATE ? Optional.of(coordinates) : Optional.empty();
    }
    /** A matching actor handle, never a fabricated actor for an unresolved index. */
    public Optional<Actor> target() { return Optional.ofNullable(target); }

    /** Runtime adapter: attach only an actor whose entire identity still matches this snapshot. */
    public HintArrow withTarget(Actor actor) {
        return new HintArrow(slot, kind, rawKind, targetIndex, coordinates.tileX(), coordinates.tileY(),
            coordinates.subX(), coordinates.subY(), coordinates.height(), flashPeriod, flashThreshold,
            drawInWorld, targetResolved, actorWorldViewId, actorEntityPtr, actorWorldViewPtr, actor,
            coordinates.worldPoint().orElse(null));
    }

    private boolean matches(Actor actor) {
        return targetResolved && actor != null
            && ((kind == HintArrowKind.NPC && actor instanceof NPC) || (kind == HintArrowKind.PLAYER && actor instanceof Player))
            && actor.hashIndex() == targetIndex && actor.worldViewId() == actorWorldViewId
            && actor.entityPtr() == actorEntityPtr && actor.worldViewPtr() == actorWorldViewPtr;
    }
}
