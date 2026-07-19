package net.titan.api;

import java.util.Objects;

/** Immutable command returned by {@link BreakHandler#poll}. */
public final class BreakCommand {
    private static final BreakCommand NONE =
        new BreakCommand(false, 0L, BreakPhase.NONE, BreakMode.AFK, "");

    private final boolean available;
    private final long epoch;
    private final BreakPhase phase;
    private final BreakMode mode;
    private final String triggeringOwnerId;

    public BreakCommand(boolean available, long epoch, BreakPhase phase,
                        BreakMode mode, String triggeringOwnerId) {
        this.available = available;
        this.epoch = epoch;
        this.phase = Objects.requireNonNull(phase, "phase");
        this.mode = Objects.requireNonNull(mode, "mode");
        this.triggeringOwnerId = triggeringOwnerId == null ? "" : triggeringOwnerId;
    }

    /**
     * A command used when the registry is unavailable or the plugin is unregistered.
     *
     * @return the shared immutable unavailable command
     */
    public static BreakCommand none() {
        return NONE;
    }

    public boolean available() {
        return available;
    }

    public long epoch() {
        return epoch;
    }

    public BreakPhase phase() {
        return phase;
    }

    public BreakMode mode() {
        return mode;
    }

    public String triggeringOwnerId() {
        return triggeringOwnerId;
    }

    public boolean shouldBreak() {
        return available && phase == BreakPhase.PREPARE;
    }

    public boolean isBreakActive() {
        return available && phase == BreakPhase.BREAK_ACTIVE;
    }

    public boolean shouldResume() {
        return available && phase == BreakPhase.RESUME;
    }
}
