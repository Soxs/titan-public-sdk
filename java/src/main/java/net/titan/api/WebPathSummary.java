package net.titan.api;

import java.time.Duration;
import java.util.Objects;

/** Immutable status snapshot for a submitted web-path request. */
public final class WebPathSummary {
    private final long requestId;
    private final WebPathPhase phase;
    private final WebPathResult result;
    private final long totalCost;
    private final long stepCount;
    private final long exploredNodes;
    private final Duration elapsed;
    private final WorldPoint start;
    private final WorldPoint requestedDestination;
    private final WorldPoint reachedDestination;
    private final String message;

    public WebPathSummary(long requestId, WebPathPhase phase, WebPathResult result,
                          long totalCost, long stepCount, long exploredNodes,
                          Duration elapsed, WorldPoint start,
                          WorldPoint requestedDestination,
                          WorldPoint reachedDestination, String message) {
        this.requestId = requestId;
        this.phase = Objects.requireNonNull(phase, "phase");
        this.result = Objects.requireNonNull(result, "result");
        this.totalCost = totalCost;
        this.stepCount = stepCount;
        this.exploredNodes = exploredNodes;
        this.elapsed = Objects.requireNonNull(elapsed, "elapsed");
        this.start = Objects.requireNonNull(start, "start");
        this.requestedDestination = Objects.requireNonNull(
            requestedDestination, "requestedDestination");
        this.reachedDestination = Objects.requireNonNull(
            reachedDestination, "reachedDestination");
        this.message = message == null ? "" : message;
    }

    public long requestId() { return requestId; }
    public WebPathPhase phase() { return phase; }
    public WebPathResult result() { return result; }
    /** Integer route cost points; normal walking contributes 5 per tile. */
    public long totalCost() { return totalCost; }
    public long stepCount() { return stepCount; }
    public long exploredNodes() { return exploredNodes; }
    public Duration elapsed() { return elapsed; }
    public WorldPoint start() { return start; }
    public WorldPoint requestedDestination() { return requestedDestination; }
    public WorldPoint reachedDestination() { return reachedDestination; }
    public String message() { return message; }
    public boolean finished() { return phase.finished(); }
}
