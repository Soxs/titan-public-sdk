package net.titan.api;

import java.util.Objects;

/** Immutable status snapshot for a started web-walk session (SDK 114). */
public final class WebWalkStatus {
    private final long walkId;
    private final WebWalkPhase phase;
    private final WebPathResult pathResult;
    private final long currentStepIndex;
    private final long stepCount;
    private final long ticksActive;
    private final long replanCount;
    private final long lastDecision;
    private final WorldPoint destination;
    private final String currentStepName;
    private final String message;

    public WebWalkStatus(long walkId, WebWalkPhase phase, WebPathResult pathResult,
                         long currentStepIndex, long stepCount, long ticksActive,
                         long replanCount, long lastDecision,
                         WorldPoint destination, String currentStepName,
                         String message) {
        this.walkId = walkId;
        this.phase = Objects.requireNonNull(phase, "phase");
        this.pathResult = Objects.requireNonNull(pathResult, "pathResult");
        this.currentStepIndex = currentStepIndex;
        this.stepCount = stepCount;
        this.ticksActive = ticksActive;
        this.replanCount = replanCount;
        this.lastDecision = lastDecision;
        this.destination = Objects.requireNonNull(destination, "destination");
        this.currentStepName = currentStepName == null ? "" : currentStepName;
        this.message = message == null ? "" : message;
    }

    public long walkId() { return walkId; }
    public WebWalkPhase phase() { return phase; }
    /** Planning failure detail when the underlying route request failed. */
    public WebPathResult pathResult() { return pathResult; }
    public long currentStepIndex() { return currentStepIndex; }
    public long stepCount() { return stepCount; }
    public long ticksActive() { return ticksActive; }
    public long replanCount() { return replanCount; }
    /** Stable code of the follower's most recent decision, for overlays. */
    public long lastDecision() { return lastDecision; }
    public WorldPoint destination() { return destination; }
    public String currentStepName() { return currentStepName; }
    public String message() { return message; }
    public boolean finished() { return phase.finished(); }
}
