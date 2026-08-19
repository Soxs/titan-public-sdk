package net.titan.api;

import net.titan.api.internal.TitanRuntime;

import java.util.Optional;
import java.util.OptionalLong;

/**
 * SDK-v114 facade for the host-driven web-walk executor. Walking is
 * centralized: at most one walk is live per client, and a new start from any
 * plugin instantly cancels the walk in progress. The no-arg accessors target
 * the current walk, so the common single-driver plugin never needs to keep
 * the returned handle; handles remain available to tell "my walk" apart from
 * a superseding one (a superseded handle polls as CANCELLED until released).
 */
public final class WebWalk {
    WebWalk() {}

    /** Start walking the local player with default options. */
    public OptionalLong walkTo(WorldPoint destination) {
        return walkTo(destination, null);
    }

    /** Start walking the local player; null options mean the defaults. */
    public OptionalLong walkTo(WorldPoint destination, WebWalkOptions options) {
        return TitanRuntime.getWebWalkerBackend().walkTo(destination, options);
    }

    /**
     * Status of the current walk: the most recently started session, which
     * stays readable through its terminal state until released or evicted.
     * Empty when no walk has been started.
     */
    public Optional<WebWalkStatus> status() {
        return status(0L);
    }

    public Optional<WebWalkStatus> status(long walkId) {
        return TitanRuntime.getWebWalkerBackend().walkStatus(walkId);
    }

    /** Cancel the live walk; false when none is in progress. */
    public boolean cancel() {
        return cancel(0L);
    }

    public boolean cancel(long walkId) {
        return TitanRuntime.getWebWalkerBackend().walkCancel(walkId);
    }

    /** Release retained session state; handles must not be used afterwards. */
    public boolean release(long walkId) {
        return TitanRuntime.getWebWalkerBackend().walkRelease(walkId);
    }

    /**
     * Advance the live {@link WebWalkOptions#manualTick} walk by one
     * follower tick. Call once per game tick from the owning plugin's
     * onGameTick. Returns false for terminal or auto-ticked sessions.
     */
    public boolean advance() {
        return advance(0L);
    }

    /**
     * Advance a {@link WebWalkOptions#manualTick} session by one follower
     * tick. Call once per game tick from the owning plugin's onGameTick.
     * Returns false for unknown, terminal, or auto-ticked sessions.
     */
    public boolean advance(long walkId) {
        return TitanRuntime.getWebWalkerBackend().walkAdvance(walkId);
    }
}
