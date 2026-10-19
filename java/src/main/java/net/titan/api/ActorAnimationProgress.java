package net.titan.api;

import java.util.Objects;

/** Owned primary/action sequence snapshot. Only ACTIVE makes frame zero meaningful. */
public final class ActorAnimationProgress {
    private final ActorDataStatus status;
    private final AnimationSequenceMode mode;
    private final int animationId, frame, frameCycle, loopCount, delay;
    private final boolean frameCycleAvailable;

    public ActorAnimationProgress(ActorDataStatus status, AnimationSequenceMode mode,
            int animationId, int frame, boolean frameCycleAvailable, int frameCycle,
            int loopCount, int delay) {
        this.status = Objects.requireNonNull(status);
        boolean active = status == ActorDataStatus.ACTIVE;
        this.mode = active ? Objects.requireNonNull(mode) : AnimationSequenceMode.UNKNOWN;
        this.animationId = active ? animationId : -1;
        this.frame = active ? frame : -1;
        this.frameCycleAvailable = active && this.mode == AnimationSequenceMode.CLASSIC && frameCycleAvailable;
        this.frameCycle = this.frameCycleAvailable ? frameCycle : 0;
        this.loopCount = active ? loopCount : 0;
        this.delay = active ? delay : 0;
    }
    public static ActorAnimationProgress failure(ActorDataStatus status) {
        return new ActorAnimationProgress(status, AnimationSequenceMode.UNKNOWN, -1, -1, false, 0, 0, 0);
    }
    public ActorDataStatus status() { return status; }
    public boolean active() { return status == ActorDataStatus.ACTIVE; }
    public boolean available() { return active() || status == ActorDataStatus.INACTIVE; }
    public AnimationSequenceMode mode() { return mode; }
    public int animationId() { return animationId; }
    /** Classic keyframe index, or cached animation progress in client ticks. */
    public int frame() { return frame; }
    public boolean frameCycleAvailable() { return frameCycleAvailable; }
    public int frameCycle() { return frameCycle; }
    public int loopCount() { return loopCount; }
    public int delay() { return delay; }
}
