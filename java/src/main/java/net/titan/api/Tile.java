package net.titan.api;

import java.util.Objects;
import java.util.function.Supplier;

public final class Tile {
    static final TileObject[] EMPTY_OBJECTS = new TileObject[0];
    private int x;
    private int y;
    private int plane;
    private int worldViewId = WorldView.CURRENT;
    private WorldView capturedWorldView;
    private long sceneGeneration;
    private int baseX, baseY;
    private TileObject[] gameObjects;
    private TileObject wallObject, decorativeObject, groundObject;
    private ScenePoint sceneLocation;
    private Supplier<Tile> liveSceneTile;

    public Tile() {}

    public Tile(int x, int y, int plane) {
        this(x, y, plane, WorldView.CURRENT);
    }

    public Tile(int x, int y, int plane, int worldViewId) {
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.worldViewId = worldViewId;
    }

    Tile(WorldView worldView, long generation, int plane, int x, int y,
         int baseX, int baseY, TileObject[] objects) {
        this(x, y, plane, worldView.getId());
        this.capturedWorldView = worldView;
        this.sceneGeneration = generation;
        this.baseX = baseX;
        this.baseY = baseY;
        this.sceneLocation = new ScenePoint(x, y);
        int gameCount = 0;
        for (TileObject object : objects) if (object.capturedLayer() == 2) ++gameCount;
        this.gameObjects = gameCount == 0 ? EMPTY_OBJECTS : new TileObject[gameCount];
        int next = 0;
        for (TileObject object : objects) {
            switch (object.capturedLayer()) {
                case 0: if (wallObject == null) wallObject = object; break;
                case 1: if (decorativeObject == null) decorativeObject = object; break;
                case 2: gameObjects[next++] = object; break;
                case 3: if (groundObject == null) groundObject = object; break;
                default: break;
            }
        }
    }

    /** Runtime facade factory. The resolver must reject a replaced/unavailable scene. */
    public static Tile sceneView(WorldView worldView, int plane, int x, int y, Supplier<Tile> resolver) {
        Tile tile = new Tile(x, y, plane, worldView.getId());
        tile.capturedWorldView = worldView;
        tile.sceneLocation = new ScenePoint(x, y);
        tile.liveSceneTile = Objects.requireNonNull(resolver, "resolver");
        return tile;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int plane() { return plane; }
    public int worldViewId() { return worldViewId; }

    public int getPlane() { return plane; }
    public WorldView getWorldView() {
        return capturedWorldView != null ? capturedWorldView : Titan.client().getWorldView(worldViewId);
    }
    public long getSceneGeneration() {
        if (liveSceneTile == null) return sceneGeneration;
        Tile tile = liveSceneTile.get(); return tile == null ? 0 : tile.getSceneGeneration();
    }
    public ScenePoint getSceneLocation() {
        if (sceneLocation == null) sceneLocation = new ScenePoint(x, y);
        return sceneLocation;
    }
    public LocalPoint getLocalLocation() { return LocalPoint.fromScene(x, y, worldViewId); }
    public WorldPoint getWorldLocation() {
        if (liveSceneTile != null) {
            Tile tile = liveSceneTile.get(); return tile == null ? null : tile.getWorldLocation();
        }
        if (capturedWorldView != null) return new WorldPoint(baseX + x, baseY + y, plane, worldViewId);
        Tile captured = currentSceneTile();
        return captured == null ? null : captured.getWorldLocation();
    }
    private Tile currentSceneTile() {
        Tile[][][] tiles = Titan.client().sceneTiles(worldViewId);
        return plane < 0 || plane >= tiles.length || x < 0 || x >= tiles[plane].length
            || y < 0 || y >= tiles[plane][x].length ? null : tiles[plane][x][y];
    }
    /** Borrowed read-only layer-2 objects; captured scene tiles perform no JNI calls. */
    public TileObject[] getGameObjects() {
        if (liveSceneTile != null) {
            Tile tile = liveSceneTile.get(); return tile == null ? EMPTY_OBJECTS : tile.getGameObjects();
        }
        if (capturedWorldView != null) return gameObjects;
        Tile tile = currentSceneTile();
        return tile == null ? EMPTY_OBJECTS : tile.getGameObjects();
    }
    public TileObject getWallObject() {
        if (liveSceneTile != null) {
            Tile tile = liveSceneTile.get(); return tile == null ? null : tile.getWallObject();
        }
        if (capturedWorldView != null) return wallObject;
        Tile tile = currentSceneTile(); return tile == null ? null : tile.getWallObject();
    }
    public TileObject getDecorativeObject() {
        if (liveSceneTile != null) {
            Tile tile = liveSceneTile.get(); return tile == null ? null : tile.getDecorativeObject();
        }
        if (capturedWorldView != null) return decorativeObject;
        Tile tile = currentSceneTile(); return tile == null ? null : tile.getDecorativeObject();
    }
    public TileObject getGroundObject() {
        if (liveSceneTile != null) {
            Tile tile = liveSceneTile.get(); return tile == null ? null : tile.getGroundObject();
        }
        if (capturedWorldView != null) return groundObject;
        Tile tile = currentSceneTile(); return tile == null ? null : tile.getGroundObject();
    }

    public int distanceTo(Tile other) {
        if (other == null || other.plane != plane || !WorldView.same(other.worldViewId, worldViewId)) {
            return Integer.MAX_VALUE;
        }
        return distanceTo2D(other);
    }

    public int distanceTo2D(Tile other) {
        if (other == null || !WorldView.same(other.worldViewId, worldViewId)) return Integer.MAX_VALUE;
        return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
    }

    public boolean isInScene(int sceneSizeX, int sceneSizeY) {
        return x >= 0 && x < sceneSizeX && y >= 0 && y < sceneSizeY;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Tile)) return false;
        Tile other = (Tile) object;
        return x == other.x && y == other.y && plane == other.plane
            && WorldView.same(worldViewId, other.worldViewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, plane, worldViewId);
    }

    @Override
    public String toString() {
        return "Tile{" + x + "," + y + "," + plane + ",wv=" + worldViewId + "}";
    }
}
