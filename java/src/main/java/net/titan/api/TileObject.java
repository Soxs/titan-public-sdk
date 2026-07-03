package net.titan.api;

import net.titan.api.internal.TitanRuntime;
import net.titan.api.utils.Magic;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class TileObject implements Locatable<TileObject> {
    private boolean liveHandle = true;
    private int worldViewId = WorldView.CURRENT;
    private long worldViewPtr;
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

    private TileObject live() { return TitanRuntime.currentLive(this); }

    @Override
    public int tileX() { return live().tileX; }
    @Override
    public int tileY() { return live().tileY; }
    @Override
    public int plane() { return live().plane; }
    @Override
    public int worldViewId() { return live().worldViewId; }
    @Override
    public long worldViewPtr() { return live().worldViewPtr; }
    public int id() { return live().id; }
    public int sizeX() { return live().sizeX; }
    public int sizeY() { return live().sizeY; }
    public long packedId() { return live().packedId; }
    public long entityPtr() { return live().entityPtr; }
    public long definitionPtr() { return live().definitionPtr; }
    public String type() {
        String value = live().type;
        return value == null ? "" : value;
    }
    public String typeName() { return type(); }
    public String name() {
        String value = live().name;
        return value == null ? "" : value;
    }
    public List<String> actions() {
        List<String> value = live().actions;
        return value == null ? Collections.emptyList() : Collections.unmodifiableList(value);
    }
    public int layer() { return live().layer; }
    public int sceneTypecode() { return live().sceneTypecode; }
    public int sceneObjectType() { return live().sceneObjectType; }
    public int shape() { return sceneObjectType(); }
    public int orientation() { return live().orientation; }
    public int animation() { return live().animation; }
    public int getAnimation() { return animation(); }
    @Override
    public int worldX() { return live().worldX; }
    @Override
    public int worldY() { return live().worldY; }

    @Override
    public WorldArea worldArea() {
        return new WorldArea(worldX(), worldY(), Math.max(1, sizeX()), Math.max(1, sizeY()),
            plane(), worldViewId());
    }

    public boolean hasAction(String action) {
        if (action == null || action.isEmpty()) return false;
        String needle = action.toLowerCase(Locale.ROOT);
        for (String value : actions()) {
            if (value != null && value.toLowerCase(Locale.ROOT).contains(needle)) return true;
        }
        return false;
    }

    public boolean interact(String action) {
        if (action == null || action.isEmpty()) return false;
        if (!hasAction(action)) return false;
        return TitanRuntime.getInteractionBackend().interactTileObject(action, this);
    }

    public boolean castOn(Magic.SpellInfo spell) { return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Standard spell) { return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Ancient spell) { return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Lunar spell) { return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Necromancy spell) { return Magic.castOn(spell, this); }

    public boolean exists() { return TitanRuntime.liveExists(this); }

    public TileObject snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) {
        return TitanRuntime.liveEquals(this, other);
    }

    @Override
    public int hashCode() {
        return TitanRuntime.liveHashCode(this);
    }
}
