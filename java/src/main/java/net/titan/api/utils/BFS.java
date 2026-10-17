package net.titan.api.utils;

import net.titan.api.Client;
import net.titan.api.NPC;
import net.titan.api.Player;
import net.titan.api.Titan;
import net.titan.api.WorldArea;
import net.titan.api.WorldPoint;
import net.titan.api.WorldView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Queue;
import java.util.Set;

/**
 * Tile reachability and natural pathfinding over the scene collision map.
 *
 * <p>Public coordinates are world tiles ({@link WorldPoint}/{@link WorldArea}); the
 * collision grid is indexed scene-local and converted at the lookup. Movement is the
 * game's 8-directional model with wall and diagonal-corner rules; the player
 * reachability map is a per-tick flood-fill cache. Mirrors the C++
 * {@code titan::utils::BFS} (SDK 153), which JS/TS {@code titan.utils.bfs} binds to.
 *
 * <p><b>Fidelity:</b> the collision step ({@link #isWalkable}) and neighbour
 * expansion order ({@link #getNeighbors}) reproduce in-game movement exactly. The
 * C++ port swaps the data structures (flat scene-indexed arrays) for speed but
 * preserves that step logic, the neighbour order, the BFS FIFO, and the parent
 * reconstruction unchanged -- those decide which path is returned and must stay
 * identical. Maps iterate in insertion (discovery) order, so every tie that walks a
 * map resolves the same way in both languages.
 *
 * <p>Points are keyed by tile and plane only: a point carrying a concrete world view
 * id keys the same entry as its {@link WorldView#CURRENT} twin (WorldPoint's hash
 * includes the view id, which its equality treats as a wildcard). Results use
 * {@code CURRENT}. One scene snapshot -- geometry, collision flags read once per
 * plane, and solid-blocker NPC tiles -- serves every query in a game tick.
 */
public final class BFS {

    private BFS() {
    }

    // --- scene collision flags (standard values) -------------------------------
    // Fully-blocked composite: a tile no actor may stand on.
    private static final int BLOCK_FULL = 0x1000000;
    private static final int BLOCK_FLOOR = 0x200000;
    private static final int BLOCK_OBJECT = 0x100;
    private static final int BLOCK_FLOOR_DECORATION = 0x40000;
    private static final int FULLY_BLOCKED = BLOCK_FULL | BLOCK_FLOOR | BLOCK_OBJECT | BLOCK_FLOOR_DECORATION;

    // Per-edge movement blocks.
    private static final int M_NW = 0x1, M_N = 0x2, M_NE = 0x4, M_E = 0x8;
    private static final int M_SE = 0x10, M_S = 0x20, M_SW = 0x40, M_W = 0x80;

    // Line-of-sight blocks (separate bits in the same flag word).
    private static final int LOS_FULL = 0x20000, LOS_N = 0x400, LOS_E = 0x1000, LOS_S = 0x4000, LOS_W = 0x10000;

    // NPCs that occupy their tiles as hard collision (routed around like objects).
    private static final int[] SOLID_BLOCKER_NPCS = {14152, 14153};

    // The outermost ring of the loaded scene has unreliable collision (its flags
    // depend on neighbouring chunks that may not be loaded), so the flood stays one
    // tile inside the scene edge.
    private static final int EDGE_INSET = 1;

    // "Nothing is removable" — the default removable set for the Set-overload null case.
    private static final java.util.function.Predicate<WorldPoint> NONE_REMOVABLE = w -> false;

    // Per-tick caches.
    private static Scene cachedScene;
    private static ReachabilityMap cachedMap;
    private static Scene cachedMapScene;

    private static Client client() {
        return Titan.client();
    }

    /** Tile-and-plane key: drops a concrete world view id (see the class comment). */
    private static WorldPoint key(WorldPoint p) {
        return p == null || p.worldViewId() == WorldView.CURRENT ? p : new WorldPoint(p.x(), p.y(), p.z());
    }

    private static WorldPoint playerPoint() {
        return key(client().localPlayer().map(Player::worldPoint).orElse(null));
    }

    // ---------------------------------------------------------------------------
    // Scene snapshot (one per tick and loaded scene)
    // ---------------------------------------------------------------------------

    private static final class Scene {
        final int tick;
        final long key;
        final int baseX, baseY, sizeX, sizeY;
        final Set<WorldPoint> solid;
        private final int[][] flags = new int[4][];

        Scene(Client c, int tick, long key) {
            this.tick = tick;
            this.key = key;
            this.baseX = c.baseX();
            this.baseY = c.baseY();
            this.sizeX = c.sceneSizeX();
            this.sizeY = c.sceneSizeY();
            Set<WorldPoint> s = new HashSet<>();
            for (NPC npc : c.npcs()) {
                if (isSolidBlocker(npc.id())) {
                    s.addAll(areaTiles(npc.worldArea()));
                }
            }
            this.solid = s;
        }

        /** Scene-local flag; each plane is read from the client once per scene. */
        int flag(int plane, int lx, int ly) {
            if (plane < 0 || plane > 3 || lx < 0 || ly < 0 || lx >= sizeX || ly >= sizeY) {
                return 0;
            }
            int[] grid = flags[plane];
            if (grid == null) {
                Client c = client();
                grid = new int[sizeX * sizeY];
                for (int x = 0; x < sizeX; x++) {
                    for (int y = 0; y < sizeY; y++) {
                        grid[x * sizeY + y] = c.collisionFlag(plane, x, y);
                    }
                }
                flags[plane] = grid;
            }
            return grid[lx * sizeY + ly];
        }
    }

    private static Scene scene() {
        Client c = client();
        int tick = c.tick();
        long key = sceneKey(c);
        Scene s = cachedScene;
        // Key by (tick, scene), not tick alone: a scene reload within the same tick
        // moves the collision and blockers, so a tick-only guard could keep stale data.
        if (s != null && s.tick == tick && s.key == key
                && s.sizeX == c.sceneSizeX() && s.sizeY == c.sceneSizeY()) {
            return s;
        }
        s = new Scene(c, tick, key);
        cachedScene = s;
        return s;
    }

    // ---------------------------------------------------------------------------
    // Reachability map (player-anchored, cached per tick)
    // ---------------------------------------------------------------------------

    public static ReachabilityMap getReachabilityMap() {
        Scene s = scene();
        WorldPoint src = playerPoint();
        ReachabilityMap map = cachedMap;
        if (map != null && cachedMapScene == s && java.util.Objects.equals(map.source, src)) {
            return map;
        }
        map = src == null ? new ReachabilityMap(null) : buildMap(s, src);
        cachedMap = map;
        cachedMapScene = s;
        return map;
    }

    // ---------------------------------------------------------------------------
    // Paths and distances
    // ---------------------------------------------------------------------------

    public static List<WorldPoint> findPath(WorldPoint end) {
        return findPath(end, true);
    }

    public static List<WorldPoint> findPath(WorldPoint end, boolean partial) {
        return resolve(getReachabilityMap(), key(end), partial);
    }

    public static List<WorldPoint> findPath(WorldPoint start, WorldPoint end) {
        return findPath(start, end, true);
    }

    public static List<WorldPoint> findPath(WorldPoint start, WorldPoint end, boolean partial) {
        return resolve(mapFrom(start), key(end), partial);
    }

    public static OptionalInt pathLength(WorldPoint end) {
        return getReachabilityMap().pathLength(end);
    }

    public static OptionalInt pathLength(WorldPoint start, WorldPoint end) {
        return mapFrom(start).pathLength(end);
    }

    // ---------------------------------------------------------------------------
    // Tile tests
    // ---------------------------------------------------------------------------

    public static boolean reachable(WorldPoint wp) {
        return getReachabilityMap().reachable(wp);
    }

    public static boolean walkable(WorldPoint wp) {
        return wp != null && !fullyBlocked(scene(), wp.z(), wp.x(), wp.y());
    }

    public static boolean canStep(WorldPoint from, WorldPoint to) {
        return isWalkable(scene(), key(from), key(to), true);
    }

    public static boolean canStep(WorldPoint from, int dx, int dy) {
        return isWalkable(scene(), key(from), new WorldPoint(from.x() + dx, from.y() + dy, from.z()), true);
    }

    public static List<WorldPoint> reachableTilesAtCost(int n) {
        List<WorldPoint> out = new ArrayList<>();
        for (Map.Entry<WorldPoint, Integer> e : getReachabilityMap().dist.entrySet()) {
            if (e.getValue() == n) {
                out.add(e.getKey());
            }
        }
        return out;
    }

    public static List<WorldPoint> reachableTilesWithinCost(int n) {
        List<WorldPoint> out = new ArrayList<>();
        for (Map.Entry<WorldPoint, Integer> e : getReachabilityMap().dist.entrySet()) {
            if (e.getValue() <= n) {
                out.add(e.getKey());
            }
        }
        return out;
    }

    // ---------------------------------------------------------------------------
    // Area tests (engine for the entity sugar)
    // ---------------------------------------------------------------------------

    /** Reachable cardinal-adjacent tiles of the area's footprint (where you stand to interact/melee). */
    public static List<WorldPoint> reachableAdjacentTiles(WorldArea area) {
        ReachabilityMap map = getReachabilityMap();
        Scene s = scene();
        Set<WorldPoint> seen = new HashSet<>();
        List<WorldPoint> out = new ArrayList<>();
        for (WorldPoint t : areaTiles(area)) {
            for (int[] d : CARDINALS) {
                WorldPoint adj = new WorldPoint(t.x() + d[0], t.y() + d[1], t.z());
                if (areaContains(area, adj) || !seen.add(adj)) {
                    continue;
                }
                if (isWalkable(s, t, adj, false) && map.reachable(adj)) {
                    out.add(adj);
                }
            }
        }
        return out;
    }

    /** Can the player reach a tile cardinally adjacent to the area (interact/melee). */
    public static boolean meleeable(WorldArea area) {
        return !reachableAdjacentTiles(area).isEmpty();
    }

    /** Can the player reach a tile within Chebyshev {@code range} of the area, optionally with line of sight. */
    public static boolean reachableWithinRange(WorldArea area, int range, boolean requireLoS) {
        ReachabilityMap map = getReachabilityMap();
        Scene s = scene();
        int plane = area.plane();
        for (int x = area.x() - range; x < area.x() + area.width() + range; x++) {
            for (int y = area.y() - range; y < area.y() + area.height() + range; y++) {
                WorldPoint t = new WorldPoint(x, y, plane);
                if (areaDistanceTo(area, t) > range || !map.reachable(t)) {
                    continue;
                }
                if (!requireLoS || hasLineOfSight(s, t, area)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Natural path to the nearest reachable tile adjacent to the area (the interact tile). */
    public static List<WorldPoint> findPath(WorldArea area) {
        WorldPoint t = nearestAdjacent(area);
        return t == null ? Collections.emptyList() : findPath(t);
    }

    public static OptionalInt pathLength(WorldArea area) {
        WorldPoint t = nearestAdjacent(area);
        return t == null ? OptionalInt.empty() : pathLength(t);
    }

    private static WorldPoint nearestAdjacent(WorldArea area) {
        ReachabilityMap m = getReachabilityMap();
        WorldPoint best = null;
        int bestLen = Integer.MAX_VALUE;
        for (WorldPoint t : reachableAdjacentTiles(area)) {
            int len = m.pathLength(t).orElse(Integer.MAX_VALUE);
            if (len < bestLen) {
                bestLen = len;
                best = t;
            }
        }
        return best;
    }

    /**
     * True when the player can stand on a tile of the area or on a tile cardinally
     * adjacent to it (see {@link #reachableAdjacentTiles}). The engine behind
     * {@link net.titan.api.Locatable#isReachable()}.
     */
    public static boolean isReachable(WorldArea area) {
        if (area == null) {
            return false;
        }
        ReachabilityMap map = getReachabilityMap();
        for (WorldPoint t : areaTiles(area)) {
            if (map.reachable(t)) {
                return true;
            }
        }
        return meleeable(area);
    }

    public static boolean isReachable(WorldPoint wp) {
        return wp != null && isReachable(new WorldArea(wp.x(), wp.y(), 1, 1, wp.z(), WorldView.CURRENT));
    }

    // ---------------------------------------------------------------------------
    // Line of sight
    // ---------------------------------------------------------------------------

    /** Line of sight from the local player to {@code to}. */
    public static boolean hasLineOfSight(WorldPoint to) {
        WorldPoint from = playerPoint();
        return from != null && hasLineOfSight(from, to);
    }

    public static boolean hasLineOfSight(WorldPoint from, WorldPoint to) {
        return hasLineOfSight(scene(), from, to);
    }

    private static boolean hasLineOfSight(Scene s, WorldPoint from, WorldPoint to) {
        if (from == null || to == null || from.z() != to.z()) {
            return false;
        }
        int plane = from.z();
        int x1 = from.x(), y1 = from.y(), x2 = to.x(), y2 = to.y();
        if (x1 == x2 && y1 == y2) {
            return true;
        }
        int dx = x2 - x1, dy = y2 - y1, dxa = Math.abs(dx), dya = Math.abs(dy);
        int xFlags = LOS_FULL | (dx < 0 ? LOS_E : LOS_W);
        int yFlags = LOS_FULL | (dy < 0 ? LOS_N : LOS_S);
        if (dxa > dya) {
            int x = x1, yBig = (y1 << 16) + 0x8000 + (dy < 0 ? -1 : 0);
            int slope = (dy << 16) / dxa, dir = dx < 0 ? -1 : 1;
            while (x != x2) {
                x += dir;
                int y = yBig >>> 16;
                if ((sightFlag(s, plane, x, y) & xFlags) != 0) {
                    return false;
                }
                yBig += slope;
                int ny = yBig >>> 16;
                if (ny != y && (sightFlag(s, plane, x, ny) & yFlags) != 0) {
                    return false;
                }
            }
        } else {
            int y = y1, xBig = (x1 << 16) + 0x8000 + (dx < 0 ? -1 : 0);
            int slope = (dx << 16) / dya, dir = dy < 0 ? -1 : 1;
            while (y != y2) {
                y += dir;
                int x = xBig >>> 16;
                if ((sightFlag(s, plane, x, y) & yFlags) != 0) {
                    return false;
                }
                xBig += slope;
                int nx = xBig >>> 16;
                if (nx != x && (sightFlag(s, plane, nx, y) & xFlags) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean hasLineOfSight(Scene s, WorldPoint from, WorldArea area) {
        WorldPoint nearest = new WorldPoint(
                clamp(from.x(), area.x(), area.x() + area.width() - 1),
                clamp(from.y(), area.y(), area.y() + area.height() - 1),
                area.plane());
        return hasLineOfSight(s, from, nearest);
    }

    // ---------------------------------------------------------------------------
    // Reachability map type
    // ---------------------------------------------------------------------------

    public static final class ReachabilityMap {
        private final WorldPoint source;
        private final Map<WorldPoint, Integer> dist = new LinkedHashMap<>();
        private final Map<WorldPoint, WorldPoint> parent = new HashMap<>();

        private ReachabilityMap(WorldPoint source) {
            this.source = source;
        }

        public boolean reachable(WorldPoint wp) {
            return dist.containsKey(key(wp));
        }

        public OptionalInt pathLength(WorldPoint wp) {
            Integer d = dist.get(key(wp));
            return d == null ? OptionalInt.empty() : OptionalInt.of(d);
        }

        /** Path from the source to {@code wp}, excluding the start tile; empty if unreachable. */
        public List<WorldPoint> pathTo(WorldPoint wp) {
            wp = key(wp);
            if (!dist.containsKey(wp)) {
                return Collections.emptyList();
            }
            LinkedList<WorldPoint> path = new LinkedList<>();
            WorldPoint cur = wp;
            while (cur != null && !cur.equals(source)) {
                path.addFirst(cur);
                cur = parent.get(cur);
            }
            return path;
        }

        /** Every reachable tile, the source first, in BFS discovery order. */
        public List<WorldPoint> getWorldPoints() {
            return new ArrayList<>(dist.keySet());
        }

        public WorldPoint source() {
            return source;
        }

        /** The tile {@code wp} is entered from on its natural path; empty for the source and unreachable tiles. */
        public Optional<WorldPoint> parentOf(WorldPoint wp) {
            return Optional.ofNullable(parent.get(key(wp)));
        }
    }

    // ---------------------------------------------------------------------------
    // Flood-fill + partial-path resolution
    // ---------------------------------------------------------------------------

    private static ReachabilityMap mapFrom(WorldPoint start) {
        start = key(start);
        WorldPoint player = playerPoint();
        if (start != null && start.equals(player)) {
            return getReachabilityMap();
        }
        return start == null ? new ReachabilityMap(null) : buildMap(scene(), start);
    }

    private static ReachabilityMap buildMap(Scene s, WorldPoint source) {
        ReachabilityMap map = new ReachabilityMap(source);
        Queue<WorldPoint> queue = new ArrayDeque<>();
        map.dist.put(source, 0);
        queue.add(source);
        while (!queue.isEmpty()) {
            WorldPoint cur = queue.poll();
            int nd = map.dist.get(cur) + 1;
            for (WorldPoint nb : getNeighbors(s, cur)) {
                if (map.dist.containsKey(nb)) {
                    continue;
                }
                map.dist.put(nb, nd);
                map.parent.put(nb, cur);
                queue.add(nb);
            }
        }
        return map;
    }

    /** end reachable -> path; else best reachable cardinal neighbour; else closest reachable by Manhattan. */
    private static List<WorldPoint> resolve(ReachabilityMap map, WorldPoint end, boolean partial) {
        if (end != null && map.reachable(end)) {
            return map.pathTo(end);
        }
        if (!partial || end == null) {
            return Collections.emptyList();
        }
        WorldPoint best = null;
        int bestLen = Integer.MAX_VALUE;
        for (int[] d : CARDINALS) {
            WorldPoint adj = new WorldPoint(end.x() + d[0], end.y() + d[1], end.z());
            Integer len = map.dist.get(adj);
            if (len != null && len < bestLen) {
                bestLen = len;
                best = adj;
            }
        }
        if (best != null) {
            return map.pathTo(best);
        }
        int bestDist = Integer.MAX_VALUE, bestCost = Integer.MAX_VALUE;
        for (Map.Entry<WorldPoint, Integer> e : map.dist.entrySet()) {
            WorldPoint wp = e.getKey();
            int md = Math.abs(wp.x() - end.x()) + Math.abs(wp.y() - end.y());
            int cost = e.getValue();
            if (md < bestDist || (md == bestDist && cost < bestCost)) {
                bestDist = md;
                bestCost = cost;
                best = wp;
            }
        }
        return best == null ? Collections.emptyList() : map.pathTo(best);
    }

    // ---------------------------------------------------------------------------
    // Neighbours + collision step (fidelity-critical; do not reorder/relax)
    // ---------------------------------------------------------------------------

    private static List<WorldPoint> getNeighbors(Scene s, WorldPoint p) {
        int x = p.x(), y = p.y(), z = p.z();
        List<WorldPoint> n = new ArrayList<>(8);
        if (isWalkable(s, p, 0, -1)) n.add(new WorldPoint(x, y - 1, z)); // S
        if (isWalkable(s, p, 0, 1)) n.add(new WorldPoint(x, y + 1, z));  // N
        if (isWalkable(s, p, -1, 0)) n.add(new WorldPoint(x - 1, y, z)); // W
        if (isWalkable(s, p, 1, 0)) n.add(new WorldPoint(x + 1, y, z));  // E
        if (isWalkable(s, p, -1, -1)) n.add(new WorldPoint(x - 1, y - 1, z)); // SW
        if (isWalkable(s, p, 1, -1)) n.add(new WorldPoint(x + 1, y - 1, z));  // SE
        if (isWalkable(s, p, -1, 1)) n.add(new WorldPoint(x - 1, y + 1, z));  // NW
        if (isWalkable(s, p, 1, 1)) n.add(new WorldPoint(x + 1, y + 1, z));   // NE
        return n;
    }

    private static boolean isWalkable(Scene s, WorldPoint p, int dx, int dy) {
        return isWalkable(s, p, new WorldPoint(p.x() + dx, p.y() + dy, p.z()), true);
    }

    private static boolean isWalkable(Scene s, WorldPoint current, WorldPoint target, boolean includeCurrent) {
        return isWalkable(s, current, target, includeCurrent, NONE_REMOVABLE);
    }

    private static boolean isWalkable(Scene s, WorldPoint current, WorldPoint target, boolean includeCurrent,
                                      java.util.function.Predicate<WorldPoint> permeable) {
        if (current == null || target == null || current.z() != target.z()) {
            return false;
        }
        int plane = target.z(), bx = s.baseX, by = s.baseY, sx = s.sizeX, sy = s.sizeY;
        int clx = current.x() - bx, cly = current.y() - by, tlx = target.x() - bx, tly = target.y() - by;
        int lo = EDGE_INSET, hix = sx - EDGE_INSET, hiy = sy - EDGE_INSET;
        if (clx < lo || clx >= hix || cly < lo || cly >= hiy || tlx < lo || tlx >= hix || tly < lo || tly >= hiy) {
            return false;
        }
        int dx = target.x() - current.x(), dy = target.y() - current.y();
        int cur = s.flag(plane, clx, cly);
        int tgt = s.flag(plane, tlx, tly);
        if (includeCurrent) {
            if ((!permeable.test(current) && blocked(s, current, cur))
                    || (!permeable.test(target) && blocked(s, target, tgt))) {
                return false;
            }
        } else if (!permeable.test(target) && blocked(s, target, tgt)) {
            return false;
        }
        if (dx == -1) {
            if ((cur & M_W) != 0 || (tgt & M_E) != 0) return false;
        } else if (dx == 1) {
            if ((cur & M_E) != 0 || (tgt & M_W) != 0) return false;
        }
        if (dy == -1) {
            if ((cur & M_S) != 0 || (tgt & M_N) != 0) return false;
        } else if (dy == 1) {
            if ((cur & M_N) != 0 || (tgt & M_S) != 0) return false;
        }
        if (dx == -1 && dy == 1) { // NW
            if ((cur & M_NW) != 0 || (cur & M_W) != 0 || (cur & M_N) != 0
                    || (tgt & M_SE) != 0 || (tgt & M_E) != 0 || (tgt & M_S) != 0) return false;
            return corners(s, plane, target.x(), current.y(), current.x(), target.y());
        } else if (dx == 1 && dy == 1) { // NE
            if ((cur & M_NE) != 0 || (cur & M_E) != 0 || (cur & M_N) != 0
                    || (tgt & M_SW) != 0 || (tgt & M_W) != 0 || (tgt & M_S) != 0) return false;
            return corners(s, plane, target.x(), current.y(), current.x(), target.y());
        } else if (dx == -1 && dy == -1) { // SW
            if ((cur & M_SW) != 0 || (cur & M_W) != 0 || (cur & M_S) != 0
                    || (tgt & M_NE) != 0 || (tgt & M_E) != 0 || (tgt & M_N) != 0) return false;
            return corners(s, plane, target.x(), current.y(), current.x(), target.y());
        } else if (dx == 1 && dy == -1) { // SE
            if ((cur & M_SE) != 0 || (cur & M_E) != 0 || (cur & M_S) != 0
                    || (tgt & M_NW) != 0 || (tgt & M_W) != 0 || (tgt & M_N) != 0) return false;
            return corners(s, plane, target.x(), current.y(), current.x(), target.y());
        }
        return true;
    }

    /** Both intermediate corner tiles of a diagonal step must be clear. */
    private static boolean corners(Scene s, int plane, int ax, int ay, int bx, int by) {
        return !fullyBlocked(s, plane, ax, ay) && !fullyBlocked(s, plane, bx, by);
    }

    private static boolean blocked(Scene s, WorldPoint wp, int flags) {
        return (flags & FULLY_BLOCKED) != 0 || s.solid.contains(wp);
    }

    private static boolean fullyBlocked(Scene s, int plane, int worldX, int worldY) {
        int lx = worldX - s.baseX, ly = worldY - s.baseY;
        if (lx < 0 || lx >= s.sizeX || ly < 0 || ly >= s.sizeY) {
            return true;
        }
        return (s.flag(plane, lx, ly) & FULLY_BLOCKED) != 0
                || s.solid.contains(new WorldPoint(worldX, worldY, plane));
    }

    private static int sightFlag(Scene s, int plane, int worldX, int worldY) {
        int lx = worldX - s.baseX, ly = worldY - s.baseY;
        if (lx < 0 || lx >= s.sizeX || ly < 0 || ly >= s.sizeY) {
            return LOS_FULL;
        }
        return s.flag(plane, lx, ly);
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private static final int[][] CARDINALS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private static boolean isSolidBlocker(int id) {
        for (int b : SOLID_BLOCKER_NPCS) {
            if (b == id) {
                return true;
            }
        }
        return false;
    }

    private static List<WorldPoint> areaTiles(WorldArea area) {
        List<WorldPoint> tiles = new ArrayList<>(Math.max(0, area.width()) * Math.max(0, area.height()));
        for (int x = 0; x < area.width(); x++) {
            for (int y = 0; y < area.height(); y++) {
                tiles.add(new WorldPoint(area.x() + x, area.y() + y, area.plane()));
            }
        }
        return tiles;
    }

    // Area tests by tile and plane only. WorldArea's own contains/distanceTo also
    // compare world views, which Java and C++ resolve differently; the BFS keys
    // every tile by coordinates, so it uses these in both SDKs.
    private static boolean areaContains(WorldArea area, WorldPoint p) {
        return p.z() == area.plane() && p.x() >= area.x() && p.x() < area.x() + area.width()
                && p.y() >= area.y() && p.y() < area.y() + area.height();
    }

    private static int areaDistanceTo(WorldArea area, WorldPoint p) {
        if (p.z() != area.plane()) {
            return Integer.MAX_VALUE;
        }
        int dx = 0, dy = 0;
        if (p.x() < area.x()) dx = area.x() - p.x();
        else if (p.x() >= area.x() + area.width()) dx = p.x() - (area.x() + area.width() - 1);
        if (p.y() < area.y()) dy = area.y() - p.y();
        else if (p.y() >= area.y() + area.height()) dy = p.y() - (area.y() + area.height() - 1);
        return Math.max(dx, dy);
    }

    private static long sceneKey(Client c) {
        return (((long) c.baseX()) << 32) ^ (((long) c.baseY()) << 8) ^ c.plane();
    }

    private static int clamp(int v, int lo, int hi) {
        return v < lo ? lo : Math.min(v, hi);
    }

    // ---------------------------------------------------------------------------
    // Minimum-blocker route (removable obstacle tiles to clear toward a goal)
    // ---------------------------------------------------------------------------

    private static final int[][] STEP_DIRS = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}, {-1, -1}, {1, -1}, {-1, 1}, {1, 1}
    };

    private static final class BNode {
        final WorldPoint wp;
        final int blockers, steps, ins;

        BNode(WorldPoint wp, int blockers, int steps, int ins) {
            this.wp = wp;
            this.blockers = blockers;
            this.steps = steps;
            this.ins = ins;
        }
    }

    /** Player-anchored: removable obstacle tiles to clear to reach {@code target}, in walk order. Empty if
     *  already reachable / no removable on the route; null if unreachable even clearing every removable. */
    public static List<WorldPoint> blockingTiles(WorldPoint target, java.util.function.Predicate<WorldPoint> removable) {
        if (target == null || removable == null) {
            return null;
        }
        WorldPoint source = playerPoint();
        if (source == null) {
            return null;
        }
        WorldPoint goal = key(target);
        if (getReachabilityMap().reachable(goal)) {
            return Collections.emptyList();
        }
        Scene s = scene();
        return minBlockerRoute(source, goal::equals, removable,
                (from, to) -> isWalkable(s, from, to, true, removable));
    }

    /** {@code Set} convenience for {@link #blockingTiles(WorldPoint, java.util.function.Predicate)};
     *  a {@code null} set means no tile is removable. */
    public static List<WorldPoint> blockingTiles(WorldPoint target, Set<WorldPoint> removable) {
        return blockingTiles(target, removableSet(removable));
    }

    /**
     * Area-target form of {@link #blockingTiles(WorldPoint, java.util.function.Predicate)}: the removable tiles
     * to clear, in walk order, to reach ANY tile of {@code target}. Empty if any footprint tile of {@code target}
     * is already reachable; {@code null} if no footprint tile is reachable even after clearing every removable
     * tile (or the player is off-scene).
     */
    public static List<WorldPoint> blockingTiles(WorldArea target, java.util.function.Predicate<WorldPoint> removable) {
        if (target == null || removable == null) {
            return null;
        }
        WorldPoint source = playerPoint();
        if (source == null) {
            return null;
        }
        ReachabilityMap map = getReachabilityMap();
        for (WorldPoint t : areaTiles(target)) {
            if (map.reachable(t)) {
                return Collections.emptyList();
            }
        }
        Scene s = scene();
        return minBlockerRoute(source, p -> areaContains(target, p), removable,
                (from, to) -> isWalkable(s, from, to, true, removable));
    }

    /** {@code Set} convenience for {@link #blockingTiles(WorldArea, java.util.function.Predicate)};
     *  a {@code null} set means no tile is removable. */
    public static List<WorldPoint> blockingTiles(WorldArea target, Set<WorldPoint> removable) {
        return blockingTiles(target, removableSet(removable));
    }

    private static java.util.function.Predicate<WorldPoint> removableSet(Set<WorldPoint> removable) {
        if (removable == null) {
            return NONE_REMOVABLE;
        }
        Set<WorldPoint> keys = new HashSet<>();
        for (WorldPoint p : removable) {
            if (p != null) {
                keys.add(key(p));
            }
        }
        return keys::contains;
    }

    static List<WorldPoint> minBlockerRoute(
            WorldPoint source,
            java.util.function.Predicate<WorldPoint> isGoal,
            java.util.function.Predicate<WorldPoint> removable,
            java.util.function.BiPredicate<WorldPoint, WorldPoint> step) {
        if (source == null) {
            return null;
        }
        if (isGoal.test(source)) {
            return Collections.emptyList();
        }
        Map<WorldPoint, WorldPoint> parent = new HashMap<>();
        Map<WorldPoint, Integer> bestBlockers = new HashMap<>();
        Map<WorldPoint, Integer> bestSteps = new HashMap<>();
        int[] ins = {0};
        java.util.PriorityQueue<BNode> queue = new java.util.PriorityQueue<>(
                java.util.Comparator.<BNode>comparingInt(n -> n.blockers)
                        .thenComparingInt(n -> n.steps)
                        .thenComparingInt(n -> n.ins));
        bestBlockers.put(source, 0);
        bestSteps.put(source, 0);
        queue.add(new BNode(source, 0, 0, ins[0]++));
        while (!queue.isEmpty()) {
            BNode cur = queue.poll();
            if (cur.blockers != bestBlockers.get(cur.wp) || cur.steps != bestSteps.get(cur.wp)) {
                continue;
            }
            if (isGoal.test(cur.wp)) {
                LinkedList<WorldPoint> blockers = new LinkedList<>();
                for (WorldPoint p = cur.wp; p != null && !p.equals(source); p = parent.get(p)) {
                    if (removable.test(p)) {
                        blockers.addFirst(p);
                    }
                }
                return blockers;
            }
            for (int[] d : STEP_DIRS) {
                WorldPoint nb = new WorldPoint(cur.wp.x() + d[0], cur.wp.y() + d[1], cur.wp.z());
                if (!step.test(cur.wp, nb)) {
                    continue;
                }
                int nbBlockers = cur.blockers + (removable.test(nb) ? 1 : 0);
                int nbSteps = cur.steps + 1;
                Integer exB = bestBlockers.get(nb);
                if (exB == null || nbBlockers < exB || (nbBlockers == exB && nbSteps < bestSteps.get(nb))) {
                    bestBlockers.put(nb, nbBlockers);
                    bestSteps.put(nb, nbSteps);
                    parent.put(nb, cur.wp);
                    queue.add(new BNode(nb, nbBlockers, nbSteps, ins[0]++));
                }
            }
        }
        return null;
    }

    // ---------------------------------------------------------------------------
    // Weighted / constrained search
    // ---------------------------------------------------------------------------

    /** Extra cost ({@code >= 0}) for entering a tile at a given step from the source. */
    public interface WeightFn {
        int weight(WorldPoint tile, int stepsFromStart);
    }

    /** Whether a tile is impassable to enter at a given step from the source. */
    public interface BlockFn {
        boolean blocked(WorldPoint tile, int stepsFromStart);
    }

    public static WeightedSearch weighted() {
        return new WeightedSearch();
    }

    /**
     * The tiles you stand on if you run {@code path} (start excluded) as one continuous click:
     * every other tile, plus the destination. Valid for a single-click run (e.g. a natural path).
     * For a weighted run route (which mixes 1- and 2-tile moves across clicks), use
     * {@link WeightedSearch#findPath(WorldPoint, boolean, boolean)} with {@code run=true} instead --
     * its landed tiles follow the move structure, not a positional skip.
     */
    public static List<WorldPoint> runTiles(List<WorldPoint> path) {
        if (path == null || path.isEmpty()) {
            return Collections.emptyList();
        }
        List<WorldPoint> landed = new ArrayList<>(path.size() / 2 + 1);
        for (int i = 1; i < path.size(); i += 2) {     // every other tile: positions 2, 4, 6, ...
            landed.add(path.get(i));
        }
        int last = path.size() - 1;
        if ((last & 1) == 0) {                          // odd-length path: destination not yet added
            landed.add(path.get(last));
        }
        return landed;
    }

    /**
     * A configured constrained search: a per-step weight added to the base cost of 1,
     * and a per-step block predicate. Blocks act as walls (path stops before them) when
     * the player starts on a safe tile; when the player starts on a blocked tile the
     * search escapes through the least-blocked route instead of getting stuck.
     */
    public static final class WeightedSearch {
        private WeightFn weight = (t, s) -> 0;
        private BlockFn block = (t, s) -> false;
        private WeightedMap map;
        private Scene builtScene;
        private boolean run = false;
        private boolean builtRun = false;

        private WeightedSearch() {
        }

        public WeightedSearch weight(java.util.function.ToIntFunction<WorldPoint> f) {
            this.weight = (t, s) -> f.applyAsInt(t);
            this.map = null;
            return this;
        }

        public WeightedSearch weight(WeightFn f) {
            this.weight = f;
            this.map = null;
            return this;
        }

        public WeightedSearch block(java.util.function.Predicate<WorldPoint> f) {
            this.block = (t, s) -> f.test(t);
            this.map = null;
            return this;
        }

        public WeightedSearch block(BlockFn f) {
            this.block = f;
            this.map = null;
            return this;
        }

        public List<WorldPoint> findPath(WorldPoint end) {
            return findPath(end, true);
        }

        public List<WorldPoint> findPath(WorldPoint end, boolean partial) {
            return map().resolve(key(end), partial);
        }

        /**
         * Run-aware costing: weight is charged only on the tiles you actually stand on (every
         * other tile, plus the destination), so the search may route a 2-tile run <i>through</i>
         * a weighted tile it merely passes over. Blockers still block every tile. Base step cost
         * is unchanged (a skipped tile still costs 1), so running only saves weight, never steps.
         */
        public WeightedSearch run(boolean run) {
            if (this.run != run) {
                this.run = run;
                this.map = null;
            }
            return this;
        }

        /** One-shot run-aware path; returns the tiles you land on. (2-arg slot is taken by {@code partial}.) */
        public List<WorldPoint> findPath(WorldPoint end, boolean partial, boolean run) {
            return run(run).map().resolve(key(end), partial);
        }

        public boolean reachable(WorldPoint wp) {
            return map().reachable(key(wp));
        }

        public OptionalInt pathLength(WorldPoint wp) {
            return map().pathLength(key(wp));
        }

        public Optional<WorldPoint> clickTile(WorldPoint end) {
            // Full tile geometry (incl. run-skipped tiles): clickTile compares against natural
            // paths, which are full and speed-independent. For walk this equals findPath(end).
            return clickTile(map().pathToFull(key(end)));
        }

        /** Farthest tile on {@code intended} whose natural path matches the intended prefix. */
        public Optional<WorldPoint> clickTile(List<WorldPoint> intended) {
            if (intended == null || intended.isEmpty()) {
                return Optional.empty();
            }
            ReachabilityMap natural = getReachabilityMap();
            WorldPoint best = null;
            for (int i = 0; i < intended.size(); i++) {
                List<WorldPoint> nat = natural.pathTo(intended.get(i));
                if (nat.size() == i + 1 && samePrefix(nat, intended, i + 1)) {
                    best = key(intended.get(i));
                } else {
                    break;
                }
            }
            return Optional.ofNullable(best);
        }

        public Optional<WorldPoint> nearestSafeTile() {
            ReachabilityMap natural = getReachabilityMap();
            WorldPoint best = null;
            int bestLen = Integer.MAX_VALUE;
            for (Map.Entry<WorldPoint, Integer> e : natural.dist.entrySet()) {
                int len = e.getValue();
                if (len < bestLen && !block.blocked(e.getKey(), len)) {
                    bestLen = len;
                    best = e.getKey();
                }
            }
            return Optional.ofNullable(best);
        }

        public List<WorldPoint> safePathTo(Collection<WorldPoint> goals) {
            WeightedMap m = map();
            WorldPoint best = null;
            int bestCost = Integer.MAX_VALUE;
            for (WorldPoint g : goals) {
                int c = m.cost(key(g));   // weighted cost, so a cheaper/safer longer route wins over a short costly one
                if (c < bestCost) {
                    bestCost = c;
                    best = key(g);
                }
            }
            return best == null ? Collections.emptyList() : m.resolve(best, false);
        }

        private WeightedMap map() {
            Scene s = scene();
            // Key by the scene snapshot (tick + loaded scene), not tick alone: a same-tick scene reload
            // changes the collision the map was built against (matches getReachabilityMap).
            if (map == null || builtScene != s || builtRun != run) {
                map = build(s);
                builtScene = s;
                builtRun = run;
            }
            return map;
        }

        private WeightedMap build(Scene s) {
            return run ? buildRun(s) : buildWalk(s);
        }

        private WeightedMap buildWalk(Scene s) {
            WorldPoint src = playerPoint();
            WeightedMap wm = new WeightedMap(src);
            if (src == null) {
                return wm;
            }
            boolean escape = block.blocked(src, 0);
            java.util.PriorityQueue<WNode> pq = new java.util.PriorityQueue<>(
                    java.util.Comparator.<WNode>comparingInt(n -> n.blockedEntered)
                            .thenComparingInt(n -> n.cost).thenComparingInt(n -> n.ins));
            int[] ins = {0};
            WNode start = new WNode(src, null, 0, 0, 0, 0, null, ins[0]++);
            wm.nodes.put(src, start);
            pq.add(start);
            while (!pq.isEmpty()) {
                WNode cur = pq.poll();
                if (wm.nodes.get(cur.wp) != cur) {
                    continue;
                }
                for (WorldPoint nb : getNeighbors(s, cur.wp)) {
                    int steps = cur.steps + 1;
                    boolean blk = block.blocked(nb, steps);
                    if (blk && !escape) {
                        continue;
                    }
                    int newBlocked = cur.blockedEntered + (blk ? 1 : 0);
                    int newCost = cur.cost + 1 + Math.max(0, weight.weight(nb, steps));
                    WNode ex = wm.nodes.get(nb);
                    if (ex == null || newBlocked < ex.blockedEntered
                            || (newBlocked == ex.blockedEntered && newCost < ex.cost)) {
                        WNode n = new WNode(nb, null, newBlocked, newCost, steps, steps, cur, ins[0]++);
                        wm.nodes.put(nb, n);
                        pq.add(n);
                    }
                }
            }
            return wm;
        }

        /**
         * Run-aware Dijkstra over <i>landed</i> tiles. Each move is a 1-tile step (cost 1) or a
         * 2-tile run A-&gt;B-&gt;C where B is skipped (cost 1, no weight) and C is landed (cost 1 +
         * weight). Nodes are only the tiles you stand on, so weight is charged only there; the
         * time-aware weight/block functions get the tick (move count), since B and C share a tick.
         */
        private WeightedMap buildRun(Scene s) {
            WorldPoint src = playerPoint();
            WeightedMap wm = new WeightedMap(src);
            if (src == null) {
                return wm;
            }
            boolean escape = block.blocked(src, 0);
            java.util.PriorityQueue<WNode> pq = new java.util.PriorityQueue<>(
                    java.util.Comparator.<WNode>comparingInt(n -> n.blockedEntered)
                            .thenComparingInt(n -> n.cost).thenComparingInt(n -> n.ins));
            int[] ins = {0};
            WNode start = new WNode(src, null, 0, 0, 0, 0, null, ins[0]++);
            wm.nodes.put(src, start);
            pq.add(start);
            while (!pq.isEmpty()) {
                WNode cur = pq.poll();
                if (wm.nodes.get(cur.wp) != cur) {
                    continue;
                }
                int tick = cur.tick + 1;
                // A node reached by a 1-tile move ends its run (see WNode.terminal); don't run on from it.
                // This forces an odd-length run to spend its single 1-tile move on the FINAL tile, keeping the
                // landed cadence at 2,4,6,... + the trailing odd tile instead of the spurious 1,3,5... phase.
                if (cur.terminal) {
                    continue;
                }
                // 1-tile move (sidestep / trailing-odd / click-adjacent): land on c.
                for (WorldPoint c : getNeighbors(s, cur.wp)) {
                    relaxRun(wm, pq, cur, null, c, 1, tick, escape, ins);
                }
                // 2-tile run: pass over b (no weight), land on c.
                for (WorldPoint b : getNeighbors(s, cur.wp)) {
                    for (WorldPoint c : getNeighbors(s, b)) {
                        if (c.equals(cur.wp)) {
                            continue;   // running back onto the current tile is a no-op
                        }
                        relaxRun(wm, pq, cur, b, c, 2, tick, escape, ins);
                    }
                }
            }
            return wm;
        }

        private void relaxRun(WeightedMap wm, java.util.PriorityQueue<WNode> pq, WNode cur,
                              WorldPoint b, WorldPoint c, int tiles, int tick, boolean escape, int[] ins) {
            int entered = 0;
            if (b != null) {
                boolean blkB = block.blocked(b, tick);
                if (blkB && !escape) {
                    return;             // can't run over a blocker
                }
                entered += blkB ? 1 : 0;
            }
            boolean blkC = block.blocked(c, tick);
            if (blkC && !escape) {
                return;
            }
            entered += blkC ? 1 : 0;
            int newBlocked = cur.blockedEntered + entered;
            int newSteps = cur.steps + tiles;
            int newCost = cur.cost + tiles + Math.max(0, weight.weight(c, tick));   // weight on landed c only
            WNode ex = wm.nodes.get(c);
            if (ex == null || newBlocked < ex.blockedEntered
                    || (newBlocked == ex.blockedEntered && newCost < ex.cost)) {
                WNode n = new WNode(c, b, newBlocked, newCost, newSteps, tick, cur, ins[0]++, tiles == 1);
                wm.nodes.put(c, n);
                pq.add(n);
            }
        }
    }

    private static boolean samePrefix(List<WorldPoint> a, List<WorldPoint> b, int n) {
        for (int k = 0; k < n; k++) {
            if (!java.util.Objects.equals(key(a.get(k)), key(b.get(k)))) {
                return false;
            }
        }
        return true;
    }

    private static final class WNode {
        final WorldPoint wp;
        final WorldPoint skipped;   // run mode: tile run over to reach wp this move (2-tile move), else null
        final int blockedEntered, cost, steps, tick, ins;
        final WNode parent;
        // Run mode only: reached by a 1-tile move. A continuous run covers 2 tiles/tick and only drops to a
        // single tile as its FINAL move (an odd-length run), so a 1-tile move is never a through-step -- it
        // terminates the run. buildRun does not expand terminal nodes, which keeps the landed tiles on the
        // correct 2-tile cadence (positions 2,4,6,... + the trailing odd tile) instead of the spurious 1,3,5...
        // chain a free 1-tile hop from the source would otherwise win on cost ties.
        final boolean terminal;

        WNode(WorldPoint wp, WorldPoint skipped, int blockedEntered, int cost, int steps, int tick, WNode parent, int ins) {
            this(wp, skipped, blockedEntered, cost, steps, tick, parent, ins, false);
        }

        WNode(WorldPoint wp, WorldPoint skipped, int blockedEntered, int cost, int steps, int tick, WNode parent, int ins, boolean terminal) {
            this.wp = wp;
            this.skipped = skipped;
            this.blockedEntered = blockedEntered;
            this.cost = cost;
            this.steps = steps;
            this.tick = tick;
            this.parent = parent;
            this.ins = ins;
            this.terminal = terminal;
        }
    }

    private static final class WeightedMap {
        final WorldPoint source;
        final Map<WorldPoint, WNode> nodes = new LinkedHashMap<>();

        WeightedMap(WorldPoint source) {
            this.source = source;
        }

        boolean reachable(WorldPoint wp) {
            return nodes.containsKey(wp);
        }

        OptionalInt pathLength(WorldPoint wp) {
            WNode n = nodes.get(wp);
            return n == null ? OptionalInt.empty() : OptionalInt.of(n.steps);
        }

        /** Weighted cost to {@code wp} (base steps + summed weights); the goal-selection key. */
        int cost(WorldPoint wp) {
            WNode n = nodes.get(wp);
            return n == null ? Integer.MAX_VALUE : n.cost;
        }

        List<WorldPoint> pathTo(WorldPoint wp) {
            if (!nodes.containsKey(wp)) {
                return Collections.emptyList();
            }
            LinkedList<WorldPoint> path = new LinkedList<>();
            for (WNode cur = nodes.get(wp); cur != null && cur.parent != null; cur = cur.parent) {
                path.addFirst(cur.wp);
            }
            return path;
        }

        /** Full tile geometry including run-skipped tiles (walk: identical to {@link #pathTo}). */
        List<WorldPoint> pathToFull(WorldPoint wp) {
            if (!nodes.containsKey(wp)) {
                return Collections.emptyList();
            }
            LinkedList<WorldPoint> path = new LinkedList<>();
            for (WNode cur = nodes.get(wp); cur != null && cur.parent != null; cur = cur.parent) {
                path.addFirst(cur.wp);
                if (cur.skipped != null) {
                    path.addFirst(cur.skipped);
                }
            }
            return path;
        }

        List<WorldPoint> resolve(WorldPoint end, boolean partial) {
            if (end != null && reachable(end)) {
                return pathTo(end);
            }
            if (!partial || end == null) {
                return Collections.emptyList();
            }
            WorldPoint best = null;
            int bestLen = Integer.MAX_VALUE;
            for (int[] d : CARDINALS) {
                WNode n = nodes.get(new WorldPoint(end.x() + d[0], end.y() + d[1], end.z()));
                if (n != null && n.cost < bestLen) {
                    bestLen = n.cost;
                    best = n.wp;
                }
            }
            if (best != null) {
                return pathTo(best);
            }
            int bestDist = Integer.MAX_VALUE, bestCost = Integer.MAX_VALUE;
            for (WNode n : nodes.values()) {
                int md = Math.abs(n.wp.x() - end.x()) + Math.abs(n.wp.y() - end.y());
                if (md < bestDist || (md == bestDist && n.cost < bestCost)) {
                    bestDist = md;
                    bestCost = n.cost;
                    best = n.wp;
                }
            }
            return best == null ? Collections.emptyList() : pathTo(best);
        }
    }
}
