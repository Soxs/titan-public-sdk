package net.titan.api;

import java.util.Objects;

/** Immutable walk, transport, or teleport edge in a generated route. */
public final class WebPathStep {
    private final long edgeId;
    private final WebPathStepKind kind;
    private final int subtype;
    private final WebPathRouteSpace fromRouteSpace;
    private final WebPathRouteSpace toRouteSpace;
    private final long edgeCost;
    private final long accumulatedCost;
    private final int fromInstanceCopyId;
    private final int toInstanceCopyId;
    private final WorldPoint from;
    private final WorldPoint to;
    private final String name;

    public WebPathStep(long edgeId, WebPathStepKind kind, int subtype,
                       WebPathRouteSpace fromRouteSpace,
                       WebPathRouteSpace toRouteSpace, long edgeCost,
                       long accumulatedCost, int fromInstanceCopyId,
                       int toInstanceCopyId,
                       WorldPoint from, WorldPoint to, String name) {
        this.edgeId = edgeId;
        this.kind = Objects.requireNonNull(kind, "kind");
        this.subtype = subtype;
        this.fromRouteSpace = Objects.requireNonNull(
            fromRouteSpace, "fromRouteSpace");
        this.toRouteSpace = Objects.requireNonNull(
            toRouteSpace, "toRouteSpace");
        this.edgeCost = edgeCost;
        this.accumulatedCost = accumulatedCost;
        this.fromInstanceCopyId = fromInstanceCopyId;
        this.toInstanceCopyId = toInstanceCopyId;
        this.from = Objects.requireNonNull(from, "from");
        this.to = Objects.requireNonNull(to, "to");
        this.name = name == null ? "" : name;
    }

    public long edgeId() { return edgeId; }
    public WebPathStepKind kind() { return kind; }
    public int subtype() { return subtype; }
    public WebPathRouteSpace fromRouteSpace() { return fromRouteSpace; }
    public WebPathRouteSpace toRouteSpace() { return toRouteSpace; }
    /** Integer edge cost points. */
    public long edgeCost() { return edgeCost; }
    /** Integer accumulated cost points. */
    public long accumulatedCost() { return accumulatedCost; }
    public int fromInstanceCopyId() { return fromInstanceCopyId; }
    public int toInstanceCopyId() { return toInstanceCopyId; }
    public WorldPoint from() { return from; }
    public WorldPoint to() { return to; }
    public String name() { return name; }
}
