package net.titan.api;

import java.util.Collections;
import java.util.List;

public final class NPC implements Actor, Locatable<NPC> {
    private transient Client client;
    private long entityPtr;
    private long definitionPtr;
    private int hashIndex;
    private int id = -1;
    private int tileX;
    private int tileY;
    private int plane;
    private int worldX;
    private int worldY;
    private int preciseX;
    private int preciseY;
    private int orientation;
    private int animation;
    private int interactingIndex = -1;
    private int interactingType = EntityType.NONE;
    private int interactingPhase = 0xFF;
    private int overrideTransform;
    private int sizeX = 1;
    private int sizeY = 1;
    private int overheadIcon = -1;
    private boolean hasHeadIconOverride;
    private int healthRatio = -1;
    private int healthScale = -1;
    private boolean hasHealthBar;
    private String name;
    private List<String> actions;
    private List<WorldPoint> pathQueue;

    @Override public long entityPtr() { return entityPtr; }
    public long definitionPtr() { return definitionPtr; }
    @Override public int hashIndex() { return hashIndex; }
    public int id() { return id; }
    @Override public int tileX() { return tileX; }
    @Override public int tileY() { return tileY; }
    @Override public int plane() { return plane; }
    @Override public int worldX() { return worldX; }
    @Override public int worldY() { return worldY; }
    @Override public int preciseX() { return preciseX; }
    @Override public int preciseY() { return preciseY; }
    @Override public LocalPoint localPoint() { return new LocalPoint(preciseX, preciseY); }
    @Override public Tile tile() { return Locatable.super.tile(); }
    @Override public WorldPoint worldPoint() { return Locatable.super.worldPoint(); }
    @Override public int orientation() { return orientation; }
    @Override public int animation() { return animation; }
    @Override public int interactingIndex() { return interactingIndex; }
    @Override public int interactingType() { return interactingType; }
    @Override public int interactingPhase() { return interactingPhase; }
    public int overrideTransform() { return overrideTransform; }
    public int sizeX() { return sizeX; }
    public int sizeY() { return sizeY; }
    public int overheadIcon() { return overheadIcon; }
    public HeadIcon overheadHeadIcon() { return HeadIcon.fromId(overheadIcon); }
    public boolean hasHeadIconOverride() { return hasHeadIconOverride; }
    public boolean isOverheadActive() {
        return overheadIcon >= 0 || hasHeadIconOverride;
    }
    public boolean isOverheadActive(HeadIcon icon) {
        return icon != null && overheadIcon == icon.id();
    }
    @Override public int healthRatio() { return healthRatio; }
    @Override public int healthScale() { return healthScale; }
    @Override public boolean hasHealthBar() { return hasHealthBar; }
    public String name() { return name == null ? "" : name; }
    public List<String> actions() { return actions == null ? Collections.emptyList() : Collections.unmodifiableList(actions); }
    @Override public List<WorldPoint> pathQueue() {
        return pathQueue == null ? Collections.emptyList() : Collections.unmodifiableList(pathQueue);
    }

    @Override
    public WorldArea worldArea() {
        return new WorldArea(worldX(), worldY(), Math.max(1, sizeX()), Math.max(1, sizeY()), plane());
    }

    @Override public int distanceTo(Tile other) { return Locatable.super.distanceTo(other); }
    @Override public int distanceTo(WorldPoint other) { return Locatable.super.distanceTo(other); }
    @Override public int distanceTo(Locatable<?> other) { return Locatable.super.distanceTo(other); }

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
        return value.interactNpcByIndex(action, hashIndex);
    }
}
