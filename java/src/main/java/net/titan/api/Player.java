package net.titan.api;

import java.util.Collections;
import java.util.List;

public final class Player implements Actor, Locatable<Player> {
    private long entityPtr;
    private int hashIndex;
    private int tileX;
    private int tileY;
    private int plane;
    private int worldX;
    private int worldY;
    private int preciseX;
    private int preciseY;
    private int orientation;
    private int animation;
    private int movementPose;
    private int idlePose;
    private int interactingIndex;
    private int interactingType = EntityType.NONE;
    private int interactingPhase = 0xFF;
    private int combatLevel;
    private boolean hidden;
    private boolean stationary;
    private String name;
    private int overheadIcon;
    private int skullIcon;
    private int healthRatio;
    private int healthScale;
    private boolean hasHealthBar;
    private List<WorldPoint> pathQueue;

    @Override
    public long entityPtr() { return entityPtr; }
    public String name() { return name == null ? "" : name; }
    @Override
    public int tileX() { return tileX; }
    @Override
    public int tileY() { return tileY; }
    @Override
    public int plane() { return plane; }
    @Override
    public int worldX() { return worldX; }
    @Override
    public int worldY() { return worldY; }
    @Override
    public int preciseX() { return preciseX; }
    @Override
    public int preciseY() { return preciseY; }
    @Override
    public LocalPoint localPoint() { return new LocalPoint(preciseX, preciseY); }
    @Override
    public Tile tile() { return Locatable.super.tile(); }
    @Override
    public WorldPoint worldPoint() { return Locatable.super.worldPoint(); }
    @Override
    public WorldArea worldArea() { return Locatable.super.worldArea(); }
    @Override
    public int distanceTo(Tile other) { return Locatable.super.distanceTo(other); }
    @Override
    public int distanceTo(WorldPoint other) { return Locatable.super.distanceTo(other); }
    @Override
    public int distanceTo(Locatable<?> other) { return Locatable.super.distanceTo(other); }
    @Override
    public int orientation() { return orientation; }
    @Override
    public int animation() { return animation; }
    @Override
    public int movementPose() { return movementPose; }
    @Override
    public int idlePose() { return idlePose; }
    public int combatLevel() { return combatLevel; }
    @Override
    public int hashIndex() { return hashIndex; }
    @Override
    public int interactingIndex() { return interactingIndex; }
    @Override
    public int interactingType() { return interactingType; }
    @Override
    public int interactingPhase() { return interactingPhase; }
    public int overheadIcon() { return overheadIcon; }
    public int skullIcon() { return skullIcon; }
    @Override
    public int healthRatio() { return healthRatio; }
    @Override
    public int healthScale() { return healthScale; }
    @Override
    public boolean hasHealthBar() { return hasHealthBar; }

    public boolean isOverheadActive() { return overheadIcon >= 0; }
    public boolean isOverheadActive(HeadIcon icon) {
        return icon != null && overheadIcon == icon.id();
    }
    public HeadIcon overheadHeadIcon() { return HeadIcon.fromId(overheadIcon); }
    public boolean isSkulled() { return skullIcon >= 0; }
    @Override
    public boolean isStationary() { return movementPose == idlePose; }
    @Override
    public boolean isAnimating() { return animation != -1; }
    public boolean isIdle() { return isStationary() && !isAnimating(); }
    public boolean isHidden() { return hidden; }
    @Override
    public List<WorldPoint> pathQueue() {
        return pathQueue == null ? Collections.emptyList() : Collections.unmodifiableList(pathQueue);
    }

    @Override
    public float healthPercent() {
        if (healthRatio < 0 || healthScale <= 0) return -1.0f;
        return (float) healthRatio / (float) healthScale;
    }

    @Override
    public boolean isDead() {
        return hasHealthBar && healthScale > 0 && healthRatio == 0;
    }
}
