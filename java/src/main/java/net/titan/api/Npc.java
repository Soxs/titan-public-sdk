package net.titan.api;

import java.util.Collections;
import java.util.List;

public final class Npc implements Actor {
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
    @Override public int orientation() { return orientation; }
    @Override public int animation() { return animation; }
    @Override public int interactingIndex() { return interactingIndex; }
    @Override public int interactingType() { return interactingType; }
    @Override public int interactingPhase() { return interactingPhase; }
    public int overrideTransform() { return overrideTransform; }
    public int sizeX() { return sizeX; }
    public int sizeY() { return sizeY; }
    public int overheadIcon() { return overheadIcon; }
    public boolean hasHeadIconOverride() { return hasHeadIconOverride; }
    @Override public int healthRatio() { return healthRatio; }
    @Override public int healthScale() { return healthScale; }
    @Override public boolean hasHealthBar() { return hasHealthBar; }
    public String name() { return name == null ? "" : name; }
    public List<String> actions() { return actions == null ? Collections.emptyList() : Collections.unmodifiableList(actions); }
    @Override public List<WorldPoint> pathQueue() {
        return pathQueue == null ? Collections.emptyList() : Collections.unmodifiableList(pathQueue);
    }

    public boolean hasAction(String action) {
        if (action == null || action.isEmpty()) return false;
        String needle = action.toLowerCase();
        for (String value : actions()) {
            if (value != null && value.toLowerCase().contains(needle)) return true;
        }
        return false;
    }
}
