package net.titan.api;

import net.titan.api.internal.TitanRuntime;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

/** SDK-v112 facade for asynchronous, read-only web-path generation. */
public final class WebWalker {
    /** Maximum number of steps returned by one fixed-ABI path request. */
    public static final int MAX_STEPS = 16_384;
    /** Integer points assigned to legacy authored cost 1. */
    public static final int COST_POINTS_PER_WHOLE = 10;
    /** Integer point cost of one normal walked tile. */
    public static final int WALK_COST_POINTS = 5;

    WebWalker() {}

    public OptionalLong submit(WebPathRequest request) {
        return TitanRuntime.getWebWalkerBackend().submit(request);
    }

    public Optional<WebPathSummary> poll(long requestId) {
        return TitanRuntime.getWebWalkerBackend().poll(requestId);
    }

    public Optional<List<WebPathStep>> copySteps(long requestId) {
        return TitanRuntime.getWebWalkerBackend().copySteps(requestId);
    }

    public boolean cancel(long requestId) {
        return TitanRuntime.getWebWalkerBackend().cancel(requestId);
    }

    public boolean release(long requestId) {
        return TitanRuntime.getWebWalkerBackend().release(requestId);
    }

    /**
     * UTF-8 JSON action payload for one step of a completed, retained path
     * request (internal backend only). Empty for unknown requests, out of
     * range indices, or hosts before SDK 114.
     */
    public Optional<String> stepPayload(long requestId, int stepIndex) {
        return TitanRuntime.getWebWalkerBackend().stepPayload(requestId, stepIndex);
    }
}
