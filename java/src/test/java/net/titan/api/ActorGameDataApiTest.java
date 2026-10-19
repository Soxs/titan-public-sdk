package net.titan.api;

import net.titan.api.internal.LiveStateBackend;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ActorGameDataApiTest {
    @Test void classicCachedAndSameIdRestartSnapshotsRetainIndependentValues() {
        ActorAnimationProgress classic = new ActorAnimationProgress(ActorDataStatus.ACTIVE,
            AnimationSequenceMode.CLASSIC, 5061, 4, true, 7, 2, 3);
        ActorAnimationProgress restarted = new ActorAnimationProgress(ActorDataStatus.ACTIVE,
            AnimationSequenceMode.CLASSIC, 5061, 0, true, 0, 0, 0);
        assertTrue(classic.active()); assertTrue(classic.available());
        assertEquals(4, classic.frame()); assertEquals(7, classic.frameCycle());
        assertTrue(classic.frameCycleAvailable()); assertEquals(2, classic.loopCount()); assertEquals(3, classic.delay());
        assertEquals(classic.animationId(), restarted.animationId()); assertEquals(0, restarted.frame());
        assertEquals(4, classic.frame());
        ActorAnimationProgress cached = new ActorAnimationProgress(ActorDataStatus.ACTIVE,
            AnimationSequenceMode.CACHED, 8123, 73, true, 999, 1, 0);
        assertEquals(AnimationSequenceMode.CACHED, cached.mode()); assertEquals(73, cached.frame());
        assertFalse(cached.frameCycleAvailable()); assertEquals(0, cached.frameCycle());
    }

    @Test void unavailableInvalidUnreadableIdleAndWrongThreadCannotImpersonateFrameZero() {
        for (ActorDataStatus status : ActorDataStatus.values()) {
            assertSame(status, ActorDataStatus.fromValue(status.value()));
            if (status == ActorDataStatus.ACTIVE) continue;
            ActorAnimationProgress snapshot = new ActorAnimationProgress(status, AnimationSequenceMode.CLASSIC,
                5061, 0, true, 0, 2, 3);
            assertEquals(status, snapshot.status()); assertFalse(snapshot.active());
            assertEquals(-1, snapshot.frame()); assertEquals(-1, snapshot.animationId());
            assertEquals(AnimationSequenceMode.UNKNOWN, snapshot.mode()); assertFalse(snapshot.frameCycleAvailable());
            assertEquals(status == ActorDataStatus.INACTIVE, snapshot.available());
        }
        assertSame(ActorDataStatus.INACTIVE, ActorDataStatus.NONE);
        assertSame(ActorDataStatus.UNAVAILABLE, ActorDataStatus.fromValue(999));
        assertSame(AnimationSequenceMode.UNKNOWN, AnimationSequenceMode.fromValue(999));
    }

    @Test void modelIdsAreOwnedImmutableSignedValuesWithExplicitEmptyStates() {
        List<Integer> original = new ArrayList<>(List.of(-1, 12345, Integer.MAX_VALUE));
        NpcModelOverrides snapshot = new NpcModelOverrides(ActorDataStatus.ACTIVE, original);
        original.set(1, 99); original.clear();
        assertEquals(List.of(-1, 12345, Integer.MAX_VALUE), snapshot.modelIds());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.modelIds().set(0, 123));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.modelIds().add(9));
        NpcModelOverrides absent = NpcModelOverrides.failure(ActorDataStatus.NONE);
        NpcModelOverrides empty = new NpcModelOverrides(ActorDataStatus.ACTIVE, List.of());
        assertTrue(absent.available()); assertFalse(absent.active()); assertTrue(absent.modelIds().isEmpty());
        assertTrue(empty.available()); assertTrue(empty.active()); assertTrue(empty.modelIds().isEmpty());
        assertNotEquals(absent.status(), empty.status());
        for (ActorDataStatus status : List.of(ActorDataStatus.UNAVAILABLE, ActorDataStatus.INVALID_ACTOR,
                ActorDataStatus.UNREADABLE, ActorDataStatus.WRONG_THREAD, ActorDataStatus.BUFFER_TOO_SMALL)) {
            NpcModelOverrides failure = new NpcModelOverrides(status, List.of(12345));
            assertFalse(failure.available()); assertFalse(failure.active()); assertTrue(failure.modelIds().isEmpty());
        }
    }

    @Test void actorAccessorsPassTheHandleWithoutInvokingLiveIdentityGetters() {
        class Backend extends PassiveBackend {
            Actor seenActor;
            NPC seenNpc;
            @Override public <T> T current(T actor) { throw new AssertionError("new query must not refresh actor fields"); }
            @Override public ActorAnimationProgress actorAnimationProgress(Actor actor) {
                seenActor = actor; return ActorAnimationProgress.failure(ActorDataStatus.WRONG_THREAD);
            }
            @Override public NpcModelOverrides npcModelOverrides(NPC npc) {
                seenNpc = npc; return NpcModelOverrides.failure(ActorDataStatus.NONE);
            }
        }
        Backend backend = new Backend();
        TitanRuntime.setLiveStateBackend(backend);
        try {
            Player player = new Player(); NPC npc = new NPC();
            assertEquals(ActorDataStatus.WRONG_THREAD, player.animationProgress().status());
            assertSame(player, backend.seenActor);
            assertEquals(ActorDataStatus.WRONG_THREAD, npc.animationProgress().status()); assertSame(npc, backend.seenActor);
            assertEquals(ActorDataStatus.NONE, npc.modelOverrides().status()); assertSame(npc, backend.seenNpc);
            set(npc, "liveHandle", false); set(player, "liveHandle", false);
            backend.seenActor = null; backend.seenNpc = null;
            assertEquals(ActorDataStatus.INVALID_ACTOR, player.animationProgress().status());
            assertEquals(ActorDataStatus.INVALID_ACTOR, npc.animationProgress().status());
            assertEquals(ActorDataStatus.INVALID_ACTOR, npc.modelOverrides().status());
            assertNull(backend.seenActor); assertNull(backend.seenNpc);
        } finally { TitanRuntime.clearLiveStateBackend(backend); }
    }

    @Test void olderBackendImplementationsInheritUnavailableQueries() {
        PassiveBackend backend = new PassiveBackend();
        TitanRuntime.setLiveStateBackend(backend);
        try {
            assertEquals(ActorDataStatus.UNAVAILABLE, new Player().animationProgress().status());
            assertEquals(ActorDataStatus.UNAVAILABLE, new NPC().modelOverrides().status());
        } finally { TitanRuntime.clearLiveStateBackend(backend); }
    }

    private static class PassiveBackend implements LiveStateBackend {
        public <T> T current(T value) { return value; }
        public boolean exists(Object value) { return true; }
        public String identity(Object value) { return null; }
        public <T> T snapshot(T value) { return value; }
    }
    private static void set(Object target, String name, Object value) {
        try { Field field = target.getClass().getDeclaredField(name); field.setAccessible(true); field.set(target, value); }
        catch (ReflectiveOperationException error) { throw new AssertionError(error); }
    }
}
