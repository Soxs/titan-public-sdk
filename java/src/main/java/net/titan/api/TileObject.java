package net.titan.api;

import java.util.Collections;
import java.util.List;

public final class TileObject implements Locatable<TileObject> {
    private transient Client client;
    private int tileX;
    private int tileY;
    private int plane;
    private int id = -1;
    private int sizeX = 1;
    private int sizeY = 1;
    private long packedId;
    private long entityPtr;
    private long definitionPtr;
    private String type;
    private String name;
    private List<String> actions;
    private int layer = -1;
    private int worldX;
    private int worldY;
    private int sceneTypecode = -1;
    private int sceneObjectType = -1;
    private int orientation = -1;
    private int animation = -1;

    @Override
    public int tileX() { return tileX; }
    @Override
    public int tileY() { return tileY; }
    @Override
    public int plane() { return plane; }
    public int id() { return id; }
    public int sizeX() { return sizeX; }
    public int sizeY() { return sizeY; }
    public long packedId() { return packedId; }
    public long entityPtr() { return entityPtr; }
    public long definitionPtr() { return definitionPtr; }
    public String type() { return type == null ? "" : type; }
    public String typeName() { return type(); }
    public String name() { return name == null ? "" : name; }
    public List<String> actions() { return actions == null ? Collections.emptyList() : Collections.unmodifiableList(actions); }
    public int layer() { return layer; }
    public int sceneTypecode() { return sceneTypecode; }
    public int sceneObjectType() { return sceneObjectType; }
    public int shape() { return sceneObjectType(); }
    public int orientation() { return orientation; }
    public int animation() { return animation; }
    public int getAnimation() { return animation(); }
    @Override
    public int worldX() { return worldX; }
    @Override
    public int worldY() { return worldY; }

    @Override
    public WorldArea worldArea() {
        return new WorldArea(worldX(), worldY(), Math.max(1, sizeX()), Math.max(1, sizeY()), plane());
    }

    public boolean hasAction(String action) {
        if (action == null || action.isEmpty()) return false;
        String needle = action.toLowerCase();
        for (String value : actions()) {
            if (value != null && value.toLowerCase().contains(needle)) return true;
        }
        return false;
    }

    public boolean interact(String action) {
        if (action == null || action.isEmpty()) return false;
        Client value = client;
        if (value == null) value = Titan.client();
        return value.interactTileObject(action, this);
    }
}
