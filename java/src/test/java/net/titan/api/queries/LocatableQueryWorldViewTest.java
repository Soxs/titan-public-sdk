package net.titan.api.queries;

import net.titan.api.Client;
import net.titan.api.Player;
import net.titan.api.Tile;
import net.titan.api.TileObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocatableQueryWorldViewTest {
    @Test
    void selectsNearbyDisembarkAcrossViewsIndependentlyOfEnumerationOrder() {
        Fixture f = new Fixture();
        assertEquals(59835, f.query().nearest().orElseThrow().id());
        assertTrue(f.query().nearest().orElseThrow().hasAction("Disembark"));
        Collections.reverse(f.objects);
        assertEquals(59835, f.query().nearest().orElseThrow().id());
    }

    @Test
    void comparesSameViewAndProjectedTopLevelDistances() {
        Fixture f = new Fixture();
        f.objects.add(object(90000, 3, 4, 1, 1080, "Open"));
        assertEquals(90000, f.query().nearest().orElseThrow().id());
    }

    @Test
    void unavailableProjectionSkipsTopLevelAndKeepsSameView() {
        Fixture f = new Fixture();
        f.valid = false;
        assertFalse(f.query().nearest().isPresent());
        f.objects.add(object(90000, 3, 4, 1, 1080, "Open"));
        assertEquals(90000, f.query().nearest().orElseThrow().id());
    }

    @Test
    void rejectsStaleProjectionBeforeOrDuringRead() {
        Fixture f = new Fixture();
        f.currentView = 1081;
        assertFalse(f.query().nearest().isPresent());
        f.currentView = 1080;
        f.changeViewDuringProjection = true;
        assertFalse(f.query().nearest().isPresent());
    }

    @Test
    void rejectsOtherViewsAndBothKindsOfPlaneMismatch() {
        Fixture f = new Fixture();
        f.projectedPlane = 1;
        assertFalse(f.query().nearest().isPresent());
        f.projectedPlane = -1;
        assertFalse(f.query().nearest().isPresent());
        f.projectedPlane = 4;
        assertFalse(f.query().nearest().isPresent());
        f.objects.clear();
        f.objects.add(object(90000, 3, 4, 1, 1081, "Open"));
        assertFalse(f.query().nearest().isPresent());
        f.objects.clear();
        f.objects.add(object(90000, 3, 4, 0, 1080, "Open"));
        assertFalse(f.query().nearest().isPresent());
    }

    @Test
    void topLevelPlayerNeedsNoProjectionAndExplicitNearestToIsUnchanged() {
        Fixture f = new Fixture();
        set(f.local, "tileX", 51); set(f.local, "tileY", 48);
        set(f.local, "plane", 0); set(f.local, "worldViewId", 0);
        f.valid = false;
        assertEquals(59835, f.query().nearest().orElseThrow().id());
        assertEquals(17404, f.query().nearestTo(new Tile(38, 47, 0, 0)).orElseThrow().id());
        assertFalse(f.query().nearestTo(new Tile(3, 4, 1, 1080)).isPresent());
    }

    @Test
    void missingLocalOrEmptyQueryReturnsEmpty() {
        Fixture f = new Fixture();
        f.hasLocal = false;
        assertFalse(f.query().nearest().isPresent());
        f.hasLocal = true;
        f.objects.clear();
        assertFalse(f.query().nearest().isPresent());
    }

    private static final class Fixture {
        final Player local = new Player();
        // Captured Gangplank identities/order; projected center (51,48,0) is test input.
        final List<TileObject> objects = new ArrayList<>(List.of(
            object(17404, 38, 47, 0, 0, "Cross"), object(14304, 41, 57, 0, 0, "Cross"),
            object(2593, 47, 61, 0, 0, "Cross"), object(59835, 51, 49, 0, 0, "Disembark")));
        int currentView = 1080, projectedPlane = 0;
        boolean valid = true, hasLocal = true, changeViewDuringProjection;
        final Client client = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(),
            new Class<?>[]{Client.class}, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "localPlayer": return hasLocal ? Optional.of(local) : Optional.empty();
                    case "tileObjects": return objects;
                    case "currentWorldViewId": return currentView;
                    case "topLevelLocalPlayerTileValid": return valid;
                    case "topLevelLocalPlayerTileX": return 51;
                    case "topLevelLocalPlayerTileY": return 48;
                    case "topLevelLocalPlayerPlane":
                        if (changeViewDuringProjection) currentView = 1081;
                        return projectedPlane;
                    default: throw new AssertionError("Unexpected Client call: " + method.getName());
                }
            });
        Fixture() {
            set(local, "liveHandle", false); set(local, "tileX", 3); set(local, "tileY", 4);
            set(local, "plane", 1); set(local, "worldViewId", 1080);
        }
        ObjectQuery query() { return new ObjectQuery(client, 15); }
    }

    private static TileObject object(int id, int x, int y, int plane, int view, String action) {
        TileObject object = new TileObject();
        set(object, "liveHandle", false); set(object, "id", id); set(object, "tileX", x);
        set(object, "tileY", y); set(object, "plane", plane); set(object, "worldViewId", view);
        set(object, "name", "Gangplank"); set(object, "actions", List.of(action));
        return object;
    }

    private static void set(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true); field.set(object, value);
        } catch (ReflectiveOperationException ex) { throw new AssertionError(ex); }
    }
}
