package net.titan.api.internal;

import net.titan.api.WebPathRequest;
import net.titan.api.WebPathStep;
import net.titan.api.WebPathSummary;
import net.titan.api.WebWalkOptions;
import net.titan.api.WebWalkStatus;
import net.titan.api.WorldPoint;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * Internal runtime bridge behind the public {@code Titan.webWalker()} and
 * {@code Titan.webWalk()} facades.
 */
public interface WebWalkerBackend {
    OptionalLong submit(WebPathRequest request);
    Optional<WebPathSummary> poll(long requestId);
    Optional<List<WebPathStep>> copySteps(long requestId);
    boolean cancel(long requestId);
    boolean release(long requestId);

    // --- SDK 114 web walk executor + per-step payload access ---
    OptionalLong walkTo(WorldPoint destination, WebWalkOptions options);
    Optional<WebWalkStatus> walkStatus(long walkId);
    boolean walkCancel(long walkId);
    boolean walkRelease(long walkId);
    boolean walkAdvance(long walkId);
    Optional<String> stepPayload(long requestId, int stepIndex);
}
