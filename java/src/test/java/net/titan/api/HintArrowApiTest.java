package net.titan.api;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Proxy;
import static org.junit.jupiter.api.Assertions.*;

class HintArrowApiTest {
    private HintArrow arrow(int slot, HintArrowKind kind, boolean resolved, Actor actor) {
        return new HintArrow(slot, kind, kind.value(), 42, 3201, 3202, 64, 128, 30,
            20, 10, true, resolved, 7, 0x1234L, 0x5678L, actor);
    }
    private NPC npc(long ptr) {
        NPC npc = new NPC();
        try {
            for (var entry : java.util.Map.<String, Object>of("hashIndex", 42, "worldViewId", 7,
                    "entityPtr", ptr, "worldViewPtr", 0x5678L, "liveHandle", false).entrySet()) {
                var field = NPC.class.getDeclaredField(entry.getKey());
                field.setAccessible(true); field.set(npc, entry.getValue());
            }
        } catch (ReflectiveOperationException error) { throw new AssertionError(error); }
        return npc;
    }
    @Test void emptyNoneAndUnresolvedRemainDistinct() {
        assertFalse(new HintArrowSnapshot(List.of()).server().isPresent());
        HintArrow none = arrow(0, HintArrowKind.NONE, false, null);
        assertEquals(HintArrowKind.NONE, new HintArrowSnapshot(List.of(none)).server().orElseThrow().kind());
        HintArrow unresolved = arrow(0, HintArrowKind.NPC, false, null);
        assertEquals(42, unresolved.targetIndex());
        assertFalse(unresolved.targetResolved());
        assertFalse(unresolved.target().isPresent());
        assertFalse(unresolved.location().isPresent());
        assertEquals(0L, unresolved.actorEntityPtr());
    }
    @Test void locationHasOnlyProvenCoordinates() {
        HintArrowLocation location = arrow(0, HintArrowKind.COORDINATE, false, null).location().orElseThrow();
        assertEquals(3201, location.tileX()); assertEquals(3202, location.tileY());
        assertEquals(64, location.subX()); assertEquals(128, location.subY()); assertEquals(30, location.height());
        assertFalse(location.worldPoint().isPresent());
        assertThrows(NoSuchMethodException.class, () -> HintArrowLocation.class.getMethod("plane"));
    }
    @Test void resolvedLocationCachesAnOwnedWorldPointAndKeepsOldConstructor() {
        assertFalse(new HintArrowLocation(3201, 3202, 64, 128, 30).worldPoint().isPresent());
        WorldPoint original = new WorldPoint(3201, 3202, 3, 7);
        HintArrowLocation location = new HintArrowLocation(3201, 3202, 64, 128, 30, original);
        WorldPoint point = location.worldPoint().orElseThrow();
        assertNotSame(original, point);
        assertSame(point, location.worldPoint().orElseThrow());
        assertEquals(3201, point.x()); assertEquals(3202, point.y());
        assertEquals(3, point.z()); assertEquals(7, point.worldViewId());
        HintArrow arrow = new HintArrow(0, HintArrowKind.COORDINATE, 2, -1,
            3201, 3202, 64, 128, 30, 20, 10, true, false, -1, 0, 0, null, original);
        assertEquals(point, arrow.withTarget(null).location().orElseThrow().worldPoint().orElseThrow());
    }
    @Test void incompatibleWorldPointDoesNotHideRawLocation() {
        for (WorldPoint point : List.of(new WorldPoint(3200, 3202, 1, 7),
                new WorldPoint(3201, 3202, 4, 7), new WorldPoint(3201, 3202, 1, -1))) {
            HintArrowLocation location = new HintArrowLocation(3201, 3202, 64, 128, 30, point);
            assertFalse(location.worldPoint().isPresent());
            assertEquals(3201, location.tileX()); assertEquals(30, location.height());
        }
    }
    @Test void recycledActorIdentityDoesNotBecomeTarget() {
        HintArrow hint = arrow(0, HintArrowKind.NPC, true, npc(0x1234L));
        assertTrue(hint.target().isPresent());
        assertFalse(hint.withTarget(npc(0x9999L)).target().isPresent());
        assertFalse(arrow(0, HintArrowKind.WORLD_ENTITY, true, npc(0x1234L)).target().isPresent());
        HintArrow unknownView = new HintArrow(0, HintArrowKind.NPC, 1, 42,
            0, 0, 0, 0, 0, 20, 10, true, true, -1, 0x1234L, 0x5678L, npc(0x1234L));
        assertFalse(unknownView.targetResolved());
        assertFalse(unknownView.target().isPresent());
    }
    @Test void collectionOwnsItsEntriesAndServerUsesSlotNumber() {
        List<HintArrow> source = new ArrayList<>(List.of(arrow(4, HintArrowKind.NPC, false, null), arrow(0, HintArrowKind.NONE, false, null)));
        HintArrowSnapshot snapshot = new HintArrowSnapshot(source);
        source.clear();
        assertEquals(2, snapshot.entries().size()); assertEquals(0, snapshot.server().orElseThrow().slot());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.entries().clear());
    }
    @Test void existingClientImplementersInheritUnavailableDefault() throws Exception {
        assertTrue(Client.class.getMethod("hintArrows").isDefault());
        assertTrue(Client.class.getMethod("setHintArrow", WorldPoint.class).isDefault());
        assertTrue(Client.class.getMethod("setHintArrow", Actor.class).isDefault());
        assertTrue(Client.class.getMethod("clearHintArrow").isDefault());
    }
    @Test void coordinateConveniencePassesPlaneWorldViewAndCenterDefaults() {
        Object[][] captured = new Object[1][];
        Client client = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(), new Class<?>[]{Client.class}, (proxy, method, args) -> {
            if (method.getName().equals("setHintArrow") && method.getParameterCount() == 4) {
                captured[0] = args;
                return HintArrowUpdateResult.APPLIED;
            }
            return MethodHandles.privateLookupIn(Client.class, MethodHandles.lookup())
                .unreflectSpecial(method, Client.class).bindTo(proxy)
                .invokeWithArguments(args == null ? new Object[0] : args);
        });
        WorldPoint point = new WorldPoint(3201, 3202, 3, 7);
        assertEquals(HintArrowUpdateResult.APPLIED, client.setHintArrow(point));
        assertSame(point, captured[0][0]);
        assertEquals(3, ((WorldPoint) captured[0][0]).z());
        assertEquals(7, ((WorldPoint) captured[0][0]).worldViewId());
        assertArrayEquals(new Object[]{point, 64, 64, 0}, captured[0]);
        assertEquals(HintArrowUpdateResult.UNAVAILABLE, client.clearHintArrow());
        assertEquals(HintArrowUpdateResult.UNAVAILABLE, client.setHintArrow((Actor) npc(0x1234L)));
    }
    @Test void mutationResultsAreExactAndUnknownCodesFailClosed() {
        for (HintArrowUpdateResult result : HintArrowUpdateResult.values())
            assertEquals(result, HintArrowUpdateResult.fromValue(result.value()));
        assertEquals(HintArrowUpdateResult.UNAVAILABLE, HintArrowUpdateResult.fromValue(99));
    }
}
