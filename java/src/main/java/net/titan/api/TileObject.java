package net.titan.api;

import java.util.Collections;
import java.util.List;

public final class TileObject {
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

    public int tileX() { return tileX; }
    public int tileY() { return tileY; }
    public int plane() { return plane; }
    public int id() { return id; }
    public int sizeX() { return sizeX; }
    public int sizeY() { return sizeY; }
    public long packedId() { return packedId; }
    public long entityPtr() { return entityPtr; }
    public long definitionPtr() { return definitionPtr; }
    public String type() { return type == null ? "" : type; }
    public String name() { return name == null ? "" : name; }
    public List<String> actions() { return actions == null ? Collections.emptyList() : Collections.unmodifiableList(actions); }
    public int layer() { return layer; }
    public int worldX() { return worldX; }
    public int worldY() { return worldY; }

    public boolean hasAction(String action) {
        if (action == null || action.isEmpty()) return false;
        String needle = action.toLowerCase();
        for (String value : actions()) {
            if (value != null && value.toLowerCase().contains(needle)) return true;
        }
        return false;
    }
}
