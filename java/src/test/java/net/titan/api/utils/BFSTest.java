package net.titan.api.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.titan.api.Client;
import net.titan.api.NPC;
import net.titan.api.Player;
import net.titan.api.WorldArea;
import net.titan.api.WorldPoint;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Golden fixture for BFS. tests/bfs_sdk_tests.cpp builds the same scene and asserts
 * the same GOLDEN text, which keeps the C++ port (and JS, which binds to it) at
 * parity with this implementation.
 */
class BFSTest {
    static final int BASE = 3200;
    static final int SIZE = 24;

    /** Must match kGolden in tests/bfs_sdk_tests.cpp line for line. */
    static final String GOLDEN = String.join("\n",
        "map size=448 first=3205,3205,0 hash=8c6105386da57743",
        "path1 [3205,3206,0;3205,3207,0;3205,3208,0;3206,3209,0;3207,3210,0;3208,3210,0;3209,3210,0;3210,3210,0;"
            + "3211,3210,0;3212,3210,0;3213,3210,0;3214,3210,0;3215,3210,0;3216,3210,0;3217,3210,0;3218,3210,0]",
        "partial [3205,3206,0;3205,3207,0;3205,3208,0;3205,3209,0;3205,3210,0;3206,3211,0;3207,3212,0;3208,3213,0;"
            + "3209,3214,0;3210,3215,0;3211,3216,0;3212,3217,0;3213,3218,0;3214,3219,0;3215,3219,0;3216,3219,0] | []",
        "fromStart [3210,3204,0;3210,3205,0;3210,3206,0;3210,3207,0;3210,3208,0;3210,3209,0;3209,3210,0;3208,3210,0;"
            + "3207,3210,0;3206,3211,0]",
        "len 16,10,none",
        "tiles reach=false,true walk=false,true step=false,false,false,true",
        "cost3 [3205,3202,0;3204,3202,0;3206,3202,0;3203,3202,0;3207,3202,0;3205,3208,0;3204,3208,0;3206,3208,0;"
            + "3203,3208,0;3207,3207,0;3207,3208,0;3202,3205,0;3202,3204,0;3202,3206,0;3202,3203,0;3202,3207,0;"
            + "3208,3204,0;3208,3203,0;3202,3202,0;3208,3202,0;3202,3208,0] within1 [3205,3205,0;3205,3204,0;"
            + "3205,3206,0;3204,3205,0;3206,3205,0;3204,3204,0;3206,3204,0;3204,3206,0;3206,3206,0]",
        "adj [3209,3205,0;3207,3205,0;3208,3204,0;3209,3206,0;3207,3206,0;3209,3207,0;3207,3207,0;3209,3208,0;"
            + "3207,3208,0;3209,3209,0;3207,3209,0;3208,3210,0] melee=true,false range=true,false,false,true",
        "areaPath [3206,3205,0;3207,3205,0] len=2 pocket=[] len=none",
        "los false,true,true,true,true,false",
        "block [3217,3219,0] | null | [3217,3219,0] | []",
        "wwalk [3206,3205,0;3207,3204,0;3208,3204,0;3209,3204,0;3210,3203,0;3211,3202,0;3212,3201,0;3213,3201,0;"
            + "3213,3202,0;3214,3203,0] len=10 click=3214,3203,0 safe=3205,3205,0 goals=[3205,3206,0;3205,3207,0;"
            + "3205,3208,0;3205,3209,0;3205,3210,0;3204,3211,0;3203,3212,0] reach=false partial=[3205,3206,0;"
            + "3205,3207,0;3205,3208,0;3205,3209,0;3205,3210,0;3206,3211,0;3207,3212,0;3208,3213,0;3209,3214,0;"
            + "3210,3215,0;3211,3216,0;3212,3217,0;3213,3218,0;3214,3219,0;3215,3219,0;3216,3219,0]",
        "wrun [3207,3204,0;3209,3204,0;3211,3202,0;3213,3201,0;3214,3203,0] click=3214,3203,0 len=10 "
            + "partial=[3205,3207,0;3205,3209,0;3207,3211,0;3209,3213,0;3211,3215,0;3212,3217,0;3214,3219,0;"
            + "3216,3219,0] far=[3207,3204,0;3209,3204,0;3209,3206,0;3211,3208,0;3212,3210,0;3214,3210,0;"
            + "3216,3210,0;3218,3212,0]",
        "runTiles [3205,3207,0;3206,3209,0;3208,3210,0;3210,3210,0;3212,3210,0;3214,3210,0;3216,3210,0;3218,3210,0]"
            + " | [3206,3203,0]",
        "isReachable true,true,false,true,false",
        "escape [3206,3205,0;3207,3204,0;3208,3204,0;3209,3204,0;3210,3205,0] safe=3207,3205,0",
        "timed [3206,3204,0;3207,3204,0;3208,3204,0] | [3207,3204,0;3208,3204,0]",
        "view true,1,[3206,3205,0;3207,3205,0]");

    private static int nextTick = 1000;

    private Injector injector;
    private final int[] flags = new int[4 * SIZE * SIZE];
    private Player player;
    private final List<NPC> npcs = new ArrayList<>();
    private int tick;

    @AfterEach
    void clearRuntime() {
        if (injector != null) {
            TitanRuntime.clearInjector(injector);
            injector = null;
        }
    }

    // --- fixture (mirrors buildFixture in tests/bfs_sdk_tests.cpp) ------------

    private void set(int x, int y, int flag) {
        flags[x * SIZE + y] |= flag;
    }

    private void buildFixture() {
        for (int y = 5; y <= 9; y++) set(8, y, 0x100);                 // pillar
        for (int y = 2; y <= 14; y++) {                                 // wall, gap at y=10
            if (y == 10) continue;
            set(12, y, 0x8);
            set(13, y, 0x80);
        }
        set(6, 6, 0x2);                                                 // one-sided north edge
        set(5, 12, 0x1000000);                                          // corner block
        set(10, 15, 0x20000);                                           // sight-only block
        set(15, 18, 0x40000);                                           // floor decoration
        for (int x = 17; x <= 21; x++) {                                // sealed pocket ring
            for (int y = 17; y <= 21; y++) {
                if (x == 17 || x == 21 || y == 17 || y == 21) set(x, y, 0x100);
            }
        }
        player = setField(setField(setField(setField(setField(setField(new Player(),
            "worldX", 3205), "worldY", 3205), "plane", 0), "tileX", 5), "tileY", 5), "worldViewId", 0);
        npcs.add(npc(14152, 3202, 3215, 2));                            // solid blocker
        npcs.add(npc(1, 3210, 3210, 1));                                // ordinary NPC
    }

    private static NPC npc(int id, int x, int y, int size) {
        return setField(setField(setField(setField(setField(setField(setField(setField(new NPC(),
            "id", id), "worldX", x), "worldY", y), "plane", 0), "tileX", x - BASE), "tileY", y - BASE),
            "sizeX", size), "sizeY", size);
    }

    private void install() {
        tick = nextTick++;
        Client client = (Client) Proxy.newProxyInstance(
            Client.class.getClassLoader(), new Class<?>[] {Client.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "tick": return tick;
                    case "baseX":
                    case "baseY": return BASE;
                    case "sceneSizeX":
                    case "sceneSizeY": return SIZE;
                    case "plane": return 0;
                    case "collisionFlag":
                        if (args != null && args.length == 3) {
                            int plane = (Integer) args[0], x = (Integer) args[1], y = (Integer) args[2];
                            if (plane < 0 || plane > 3 || x < 0 || y < 0 || x >= SIZE || y >= SIZE) return 0;
                            return plane == 0 ? flags[x * SIZE + y] : 0;
                        }
                        return 0;
                    case "localPlayer": return Optional.ofNullable(player);
                    case "npcs": return npcs;
                    case "hashCode": return System.identityHashCode(proxy);
                    case "equals": return proxy == args[0];
                    case "toString": return "BFSTestClient";
                    default: return defaultValue(method.getReturnType());
                }
            });
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Client.class).toInstance(client);
            }
        });
        TitanRuntime.setInjector(injector);
    }

    // --- formatting (mirrors the C++ helpers) ---------------------------------

    private static WorldPoint p(int x, int y) {
        return new WorldPoint(x, y, 0);
    }

    private static String fmt(WorldPoint wp) {
        return wp.x() + "," + wp.y() + "," + wp.z();
    }

    private static String fmt(List<WorldPoint> path) {
        if (path == null) return "null";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(';');
            sb.append(fmt(path.get(i)));
        }
        return sb.append(']').toString();
    }

    private static String fmt(Optional<WorldPoint> wp) {
        return wp.map(BFSTest::fmt).orElse("none");
    }

    private static String fmt(OptionalInt value) {
        return value.isPresent() ? Integer.toString(value.getAsInt()) : "none";
    }

    static String run() {
        StringBuilder out = new StringBuilder();
        BFS.ReachabilityMap map = BFS.getReachabilityMap();
        long h = 0xcbf29ce484222325L;
        for (WorldPoint wp : map.getWorldPoints()) {
            long v = ((long) wp.x() * 10000L + wp.y()) * 1000L + map.pathLength(wp).getAsInt();
            h ^= v;
            h *= 0x100000001b3L;
        }
        out.append("map size=").append(map.getWorldPoints().size())
            .append(" first=").append(fmt(map.getWorldPoints().get(0)))
            .append(" hash=").append(Long.toHexString(h)).append('\n');
        out.append("path1 ").append(fmt(BFS.findPath(p(3218, 3210)))).append('\n');
        out.append("partial ").append(fmt(BFS.findPath(p(3219, 3219))))
            .append(" | ").append(fmt(BFS.findPath(p(3219, 3219), false))).append('\n');
        out.append("fromStart ").append(fmt(BFS.findPath(p(3210, 3203), p(3206, 3211)))).append('\n');
        out.append("len ").append(fmt(BFS.pathLength(p(3218, 3210))))
            .append(',').append(fmt(BFS.pathLength(p(3210, 3203), p(3206, 3211))))
            .append(',').append(fmt(BFS.pathLength(p(3219, 3219)))).append('\n');
        out.append("tiles reach=").append(BFS.reachable(p(3219, 3219)))
            .append(',').append(BFS.reachable(p(3206, 3207)))
            .append(" walk=").append(BFS.walkable(p(3208, 3206)))
            .append(',').append(BFS.walkable(p(3206, 3207)))
            .append(" step=").append(BFS.canStep(p(3206, 3206), p(3206, 3207)))
            .append(',').append(BFS.canStep(p(3206, 3207), p(3206, 3206)))
            .append(',').append(BFS.canStep(p(3204, 3212), 1, 1))
            .append(',').append(BFS.canStep(p(3204, 3211), 1, -1)).append('\n');
        out.append("cost3 ").append(fmt(BFS.reachableTilesAtCost(3)))
            .append(" within1 ").append(fmt(BFS.reachableTilesWithinCost(1))).append('\n');
        WorldArea pillar = new WorldArea(3208, 3205, 1, 5, 0);
        WorldArea pocket = new WorldArea(3218, 3218, 3, 3, 0);
        WorldArea solidNpc = new WorldArea(3202, 3215, 2, 2, 0);
        out.append("adj ").append(fmt(BFS.reachableAdjacentTiles(pillar)))
            .append(" melee=").append(BFS.meleeable(pillar))
            .append(',').append(BFS.meleeable(pocket))
            .append(" range=").append(BFS.reachableWithinRange(pillar, 3, true))
            .append(',').append(BFS.reachableWithinRange(pillar, 0, false))
            .append(',').append(BFS.reachableWithinRange(pocket, 1, false))
            .append(',').append(BFS.reachableWithinRange(pocket, 2, true)).append('\n');
        out.append("areaPath ").append(fmt(BFS.findPath(pillar)))
            .append(" len=").append(fmt(BFS.pathLength(pillar)))
            .append(" pocket=").append(fmt(BFS.findPath(pocket)))
            .append(" len=").append(fmt(BFS.pathLength(pocket))).append('\n');
        out.append("los ").append(BFS.hasLineOfSight(p(3210, 3216)))
            .append(',').append(BFS.hasLineOfSight(p(3210, 3214)))
            .append(',').append(BFS.hasLineOfSight(p(3214, 3215)))
            .append(',').append(BFS.hasLineOfSight(p(3205, 3220)))
            .append(',').append(BFS.hasLineOfSight(p(3210, 3203), p(3216, 3203)))
            .append(',').append(BFS.hasLineOfSight(p(3207, 3212), p(3213, 3218))).append('\n');
        out.append("block ").append(fmt(BFS.blockingTiles(p(3219, 3219), Set.of(p(3217, 3219)))))
            .append(" | ").append(fmt(BFS.blockingTiles(p(3219, 3219), Collections.<WorldPoint>emptySet())))
            .append(" | ").append(fmt(BFS.blockingTiles(pocket, Set.of(p(3217, 3219)))))
            .append(" | ").append(fmt(BFS.blockingTiles(p(3206, 3207), w -> false))).append('\n');
        BFS.WeightedSearch w = BFS.weighted()
            .weight(t -> t.x() == 3210 ? 5 : 0)
            .block(t -> t.equals(p(3207, 3209)) || t.equals(p(3207, 3210)));
        out.append("wwalk ").append(fmt(w.findPath(p(3214, 3203))))
            .append(" len=").append(fmt(w.pathLength(p(3214, 3203))))
            .append(" click=").append(fmt(w.clickTile(p(3214, 3203))))
            .append(" safe=").append(fmt(w.nearestSafeTile()))
            .append(" goals=").append(fmt(w.safePathTo(List.of(p(3214, 3203), p(3203, 3212)))))
            .append(" reach=").append(w.reachable(p(3207, 3209)))
            .append(" partial=").append(fmt(w.findPath(p(3219, 3219)))).append('\n');
        out.append("wrun ").append(fmt(w.findPath(p(3214, 3203), true, true)))
            .append(" click=").append(fmt(w.clickTile(p(3214, 3203))))
            .append(" len=").append(fmt(w.pathLength(p(3214, 3203))))
            .append(" partial=").append(fmt(w.findPath(p(3219, 3219), true, true)))
            .append(" far=").append(fmt(w.findPath(p(3218, 3212), true, true))).append('\n');
        out.append("runTiles ").append(fmt(BFS.runTiles(BFS.findPath(p(3218, 3210)))))
            .append(" | ").append(fmt(BFS.runTiles(BFS.findPath(p(3206, 3203))))).append('\n');
        out.append("isReachable ").append(BFS.isReachable(p(3218, 3210)))
            .append(',').append(BFS.isReachable(pillar))
            .append(',').append(BFS.isReachable(pocket))
            .append(',').append(BFS.isReachable(solidNpc))
            .append(',').append(BFS.isReachable(p(3219, 3219))).append('\n');
        BFS.WeightedSearch escape = BFS.weighted().block(t -> t.x() <= 3206);
        out.append("escape ").append(fmt(escape.findPath(p(3210, 3205))))
            .append(" safe=").append(fmt(escape.nearestSafeTile())).append('\n');
        BFS.WeightedSearch timed = BFS.weighted()
            .block((t, s) -> t.equals(p(3206, 3205)) && s == 1)
            .weight((t, s) -> s > 3 ? 1 : 0);
        out.append("timed ").append(fmt(timed.findPath(p(3208, 3204))))
            .append(" | ").append(fmt(timed.findPath(p(3208, 3204), true, true))).append('\n');
        out.append("view ").append(BFS.reachable(new WorldPoint(3206, 3205, 0, 0)))
            .append(',').append(fmt(BFS.pathLength(new WorldPoint(3206, 3205, 0, 7))))
            .append(',').append(fmt(BFS.findPath(new WorldPoint(3207, 3205, 0, 0))));
        return out.toString();
    }

    @Test
    void goldenFixtureMatchesTheCppPort() {
        buildFixture();
        install();
        String actual = run();
        System.out.println("BFS_GOLDEN_BEGIN\n" + actual + "\nBFS_GOLDEN_END");
        assertEquals(GOLDEN, actual);
    }

    @Test
    void locatableIsReachableUsesTheMap() {
        buildFixture();
        install();
        NPC solid = npcs.get(0);
        NPC ordinary = npcs.get(1);
        assertTrue(solid.isReachable(), "a solid NPC is reachable from an adjacent tile");
        assertTrue(ordinary.isReachable());
        assertTrue(player.isReachable(), "the player's own tile is the source");
        NPC sealed = npc(2, 3219, 3219, 1);
        assertFalse(sealed.isReachable(), "nothing reaches the sealed pocket");
    }

    @Test
    void sceneSnapshotRefreshesOnANewTick() {
        buildFixture();
        install();
        assertFalse(BFS.reachable(p(3219, 3219)));
        flags[17 * SIZE + 19] = 0;   // open the pocket ring
        assertFalse(BFS.reachable(p(3219, 3219)), "the same tick keeps its snapshot");
        tick = nextTick++;
        assertTrue(BFS.reachable(p(3219, 3219)), "a new tick re-reads collision");
    }

    @SuppressWarnings("unchecked")
    private static <T> T setField(T target, String name, Object value) {
        try {
            Class<?> type = target.getClass();
            while (type != null) {
                try {
                    Field field = type.getDeclaredField(name);
                    field.setAccessible(true);
                    field.set(target, value);
                    return target;
                } catch (NoSuchFieldException ignored) {
                    type = type.getSuperclass();
                }
            }
            throw new IllegalArgumentException(name);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == short.class) return (short) 0;
        if (type == byte.class) return (byte) 0;
        if (type == char.class) return '\0';
        if (type == Optional.class) return Optional.empty();
        if (type == OptionalInt.class) return OptionalInt.empty();
        if (type == List.class) return Collections.emptyList();
        return null;
    }
}
