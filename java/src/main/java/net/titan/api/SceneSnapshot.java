package net.titan.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Complete owned capture of one WorldView's four scene planes. Tile and object
 * getters are Java-only. Retaining this value retains that captured generation;
 * obtain another snapshot to observe changes. Array views are borrowed and must
 * not be modified. The unique object list is independent of those array views.
 */
public final class SceneSnapshot {
    private final Client client;
    private final long generation;
    private final long sceneIncarnation;
    private final int liveStateEpoch;
    private final WorldView worldView;
    private final long worldViewPtr;
    private final long scenePtr;
    private final int baseX, baseY, plane, sizeX, sizeY;
    private final Tile[][][] tiles;
    private final List<TileObject> objects;
    private final int[] tileRows;
    private final int[] indices;

    /** Runtime capture factory. Tile rows are (plane,x,y,firstIndex,indexCount). */
    public SceneSnapshot(Client client, long generation, long sceneIncarnation, int liveStateEpoch,
                         int worldViewId, long worldViewPtr, long scenePtr,
                         int baseX, int baseY, int plane, int sizeX, int sizeY,
                         int[] tileRows, List<TileObject> sourceObjects, int[] indices) {
        this(client, generation, sceneIncarnation, liveStateEpoch, worldViewId, worldViewPtr, scenePtr,
            baseX, baseY, plane, sizeX, sizeY, tileRows, sourceObjects, indices, true);
    }

    private SceneSnapshot(Client client, long generation, long sceneIncarnation, int liveStateEpoch,
                          int worldViewId, long worldViewPtr, long scenePtr,
                          int baseX, int baseY, int plane, int sizeX, int sizeY,
                          int[] tileRows, List<TileObject> sourceObjects, int[] indices,
                          boolean detachObjects) {
        Objects.requireNonNull(client, "client");
        Objects.requireNonNull(tileRows, "tileRows");
        Objects.requireNonNull(sourceObjects, "sourceObjects");
        Objects.requireNonNull(indices, "indices");
        if (generation == 0 || sceneIncarnation == 0 || worldViewId < 0 || worldViewPtr == 0 || scenePtr == 0
                || plane < 0 || plane > 3 || sizeX <= 0 || sizeX > 256
                || sizeY <= 0 || sizeY > 256 || tileRows.length % 5 != 0
                || tileRows.length / 5 > 4 * sizeX * sizeY
                || sourceObjects.size() > indices.length) {
            throw new IllegalArgumentException("Invalid scene snapshot metadata");
        }
        this.client = client;
        this.generation = generation;
        this.sceneIncarnation = sceneIncarnation;
        this.liveStateEpoch = liveStateEpoch;
        this.worldView = new WorldView(client, worldViewId);
        this.worldViewPtr = worldViewPtr;
        this.scenePtr = scenePtr;
        this.baseX = baseX;
        this.baseY = baseY;
        this.plane = plane;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.tileRows = tileRows.clone();
        this.indices = indices.clone();
        List<TileObject> owned = new ArrayList<>(sourceObjects.size());
        for (TileObject object : sourceObjects) {
            owned.add(detachObjects ? Objects.requireNonNull(object, "object").capturedCopy(worldView)
                : Objects.requireNonNull(object, "object"));
        }
        this.objects = Collections.unmodifiableList(owned);
        this.tiles = new Tile[4][sizeX][sizeY];
        boolean[] referenced = new boolean[owned.size()];
        int expectedIndex = 0;
        for (int row = 0; row < tileRows.length; row += 5) {
            int p = tileRows[row], x = tileRows[row + 1], y = tileRows[row + 2];
            int first = tileRows[row + 3], count = tileRows[row + 4];
            if (p < 0 || p > 3 || x < 0 || x >= sizeX || y < 0 || y >= sizeY
                    || tiles[p][x][y] != null || first != expectedIndex || count < 0
                    || count > indices.length - first) {
                throw new IllegalArgumentException("Invalid scene tile row");
            }
            TileObject[] members = count == 0 ? Tile.EMPTY_OBJECTS : new TileObject[count];
            for (int i = 0; i < count; ++i) {
                int index = indices[first + i];
                if (index < 0 || index >= owned.size()) {
                    throw new IllegalArgumentException("Invalid scene object membership");
                }
                TileObject object = owned.get(index);
                if (detachObjects && (object.worldViewId() != worldViewId || object.worldViewPtr() != worldViewPtr
                        || object.plane() != p || object.tileX() < 0 || object.tileX() >= sizeX
                        || object.tileY() < 0 || object.tileY() >= sizeY)) {
                    throw new IllegalArgumentException("Scene object belongs to another capture");
                }
                for (int prior = 0; prior < i; ++prior) {
                    if (members[prior] == object) throw new IllegalArgumentException("Duplicate tile member");
                }
                members[i] = object;
                referenced[index] = true;
            }
            tiles[p][x][y] = new Tile(worldView, generation, p, x, y, baseX, baseY, members);
            expectedIndex += count;
        }
        if (expectedIndex != indices.length) throw new IllegalArgumentException("Unused tile memberships");
        for (boolean member : referenced) {
            if (!member) throw new IllegalArgumentException("Unreferenced scene object");
        }
    }

    /** Runtime-only binding step: membership is unchanged; objects are scene-qualified live handles. */
    public SceneSnapshot withLiveObjects(List<TileObject> liveObjects) {
        if (liveObjects.size() != objects.size()) throw new IllegalArgumentException("Object count changed");
        return new SceneSnapshot(client, generation, sceneIncarnation, liveStateEpoch, worldView.getId(), worldViewPtr,
            scenePtr, baseX, baseY, plane, sizeX, sizeY, tileRows, liveObjects, indices, false);
    }

    public long getGeneration() { return generation; }
    public long getSceneIncarnation() { return sceneIncarnation; }
    public int getLiveStateEpoch() { return liveStateEpoch; }
    public WorldView getWorldView() { return worldView; }
    public long getWorldViewPtr() { return worldViewPtr; }
    public long getScenePtr() { return scenePtr; }
    public int getBaseX() { return baseX; }
    public int getBaseY() { return baseY; }
    public int getPlane() { return plane; }
    public int getSizeX() { return sizeX; }
    public int getSizeY() { return sizeY; }
    public Tile[][][] getTiles() { return tiles; }
    public List<TileObject> getObjects() { return objects; }
    public Tile getTile(int plane, int x, int y) {
        return plane < 0 || plane > 3 || x < 0 || x >= sizeX || y < 0 || y >= sizeY
            ? null : tiles[plane][x][y];
    }
}
