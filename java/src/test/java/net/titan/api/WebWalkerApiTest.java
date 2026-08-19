package net.titan.api;

import net.titan.api.internal.TitanRuntime;
import net.titan.api.internal.WebWalkerBackend;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebWalkerApiTest {
    private WebWalkerBackend backend;

    @Test
    void fixedAbiStepCapIsMirrored() {
        assertEquals(16_384, WebWalker.MAX_STEPS);
        assertEquals(10, WebWalker.COST_POINTS_PER_WHOLE);
        assertEquals(5, WebWalker.WALK_COST_POINTS);
    }

    @AfterEach
    void clearBackend() {
        if (backend != null) TitanRuntime.clearWebWalkerBackend(backend);
    }

    @Test
    void requestDefaultsMatchSdk112() {
        WorldPoint destination = new WorldPoint(3200, 3201, 0, WorldView.TOP_LEVEL);
        WebPathRequest request = new WebPathRequest(destination);

        assertSame(destination, request.destination());
        assertTrue(request.useLocalPlayer());
        assertEquals(WebPathRouteSpace.GLOBAL, request.routeSpace());
        assertEquals(Duration.ofSeconds(60), request.timeout());
        assertEquals(
            WebPathOptions.TRANSPORTS | WebPathOptions.TELEPORTS
                | WebPathOptions.EQUIPPED_ITEM_TELEPORTS | WebPathOptions.POH_ROUTES
                | WebPathOptions.AVOID_WILDERNESS,
            request.options().bits());
        assertFalse(request.options().minigameTeleports());
        assertFalse(request.options().charters());
    }

    @Test
    void stepRetainsBothEndpointDomains() {
        WorldPoint from = new WorldPoint(1200, 2200, 0, 41);
        WorldPoint to = new WorldPoint(3200, 3200, 0, WorldView.TOP_LEVEL);
        WebPathStep step = new WebPathStep(
            7L, WebPathStepKind.TELEPORT, 3,
            WebPathRouteSpace.CURRENT_INSTANCE, WebPathRouteSpace.GLOBAL,
            40L, 120L, 19, -1, from, to, "Leave instance");

        assertEquals(WebPathRouteSpace.CURRENT_INSTANCE,
            step.fromRouteSpace());
        assertEquals(19, step.fromInstanceCopyId());
        assertEquals(WebPathRouteSpace.GLOBAL, step.toRouteSpace());
        assertEquals(-1, step.toInstanceCopyId());
        assertEquals(40L, step.edgeCost());
        assertEquals(120L, step.accumulatedCost());
        assertSame(from, step.from());
        assertSame(to, step.to());
    }

    @Test
    void titanFacadeDelegatesToRuntimeBackend() {
        backend = new WebWalkerBackend() {
            @Override
            public OptionalLong submit(WebPathRequest request) {
                return OptionalLong.of(42L);
            }

            @Override
            public Optional<WebPathSummary> poll(long requestId) {
                return Optional.empty();
            }

            @Override
            public Optional<List<WebPathStep>> copySteps(long requestId) {
                return Optional.of(Collections.emptyList());
            }

            @Override
            public boolean cancel(long requestId) {
                return requestId == 42L;
            }

            @Override
            public boolean release(long requestId) {
                return requestId == 42L;
            }

            @Override
            public OptionalLong walkTo(WorldPoint destination, WebWalkOptions options) {
                return OptionalLong.of(77L);
            }

            @Override
            public Optional<WebWalkStatus> walkStatus(long walkId) {
                return Optional.empty();
            }

            @Override
            public boolean walkCancel(long walkId) {
                return walkId == 77L;
            }

            @Override
            public boolean walkRelease(long walkId) {
                return walkId == 77L;
            }

            @Override
            public boolean walkAdvance(long walkId) {
                return walkId == 77L;
            }

            @Override
            public Optional<String> stepPayload(long requestId, int stepIndex) {
                return requestId == 42L && stepIndex == 0
                    ? Optional.of("{}") : Optional.empty();
            }
        };
        TitanRuntime.setWebWalkerBackend(backend);

        assertEquals(42L, Titan.webWalker().submit(
            new WebPathRequest(new WorldPoint(1, 2, 0))).orElseThrow());
        assertTrue(Titan.webWalker().copySteps(42L).isPresent());
        assertTrue(Titan.webWalker().cancel(42L));
        assertTrue(Titan.webWalker().release(42L));
        assertEquals("{}", Titan.webWalker().stepPayload(42L, 0).orElseThrow());

        assertEquals(77L, Titan.webWalk().walkTo(
            new WorldPoint(1, 2, 0)).orElseThrow());
        assertFalse(Titan.webWalk().status(77L).isPresent());
        assertTrue(Titan.webWalk().cancel(77L));
        assertTrue(Titan.webWalk().release(77L));
        assertTrue(Titan.webWalk().advance(77L));
    }

    @Test
    void walkOptionDefaultsMatchSdk114() {
        WebWalkOptions options = new WebWalkOptions();

        assertEquals(WebWalkOptions.MANAGE_RUN, options.walkFlags());
        assertEquals(WebPathRouteSpace.GLOBAL, options.routeSpace());
        assertEquals(Duration.ofSeconds(60), options.timeout());
        assertEquals(0, options.arriveRadius());
        assertEquals(0, options.maxDurationTicks());
        assertTrue(options.forbiddenTiles().isEmpty());

        options.manageRun(false).drinkStamina(true).manualTick(true);
        assertEquals(WebWalkOptions.DRINK_STAMINA | WebWalkOptions.MANUAL_TICK,
            options.walkFlags());
    }

    @Test
    void walkPhaseTerminalStatesAreFinished() {
        assertTrue(WebWalkPhase.ARRIVED.finished());
        assertTrue(WebWalkPhase.FAILED.finished());
        assertTrue(WebWalkPhase.CANCELLED.finished());
        assertFalse(WebWalkPhase.NONE.finished());
        assertFalse(WebWalkPhase.PLANNING.finished());
        assertFalse(WebWalkPhase.WALKING.finished());
        assertFalse(WebWalkPhase.TRANSITING.finished());
        assertEquals(WebWalkPhase.UNKNOWN, WebWalkPhase.fromValue(99));
    }
}
