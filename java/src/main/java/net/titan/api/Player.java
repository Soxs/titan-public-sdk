package net.titan.api;

public final class Player {
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
    private int interactingIndex;
    private int interactingType;
    private int combatLevel;
    private boolean hidden;
    private boolean stationary;
    private String name;
    private int overheadIcon;
    private int skullIcon;
    private int healthRatio;
    private int healthScale;
    private boolean hasHealthBar;

    public String name() { return name == null ? "" : name; }
    public int tileX() { return tileX; }
    public int tileY() { return tileY; }
    public int plane() { return plane; }
    public int worldX() { return worldX; }
    public int worldY() { return worldY; }
    public int preciseX() { return preciseX; }
    public int preciseY() { return preciseY; }
    public int orientation() { return orientation; }
    public int animation() { return animation; }
    public int combatLevel() { return combatLevel; }
    public int hashIndex() { return hashIndex; }
    public int interactingIndex() { return interactingIndex; }
    public int interactingType() { return interactingType; }
    public int overheadIcon() { return overheadIcon; }
    public int skullIcon() { return skullIcon; }
    public int healthRatio() { return healthRatio; }
    public int healthScale() { return healthScale; }
    public boolean hasHealthBar() { return hasHealthBar; }

    public boolean isOverheadActive() { return overheadIcon >= 0; }
    public boolean isSkulled() { return skullIcon >= 0; }
    public boolean isStationary() { return stationary; }
    public boolean isAnimating() { return animation != -1; }
    public boolean isIdle() { return isStationary() && !isAnimating(); }
    public boolean isHidden() { return hidden; }

    public float healthPercent() {
        if (healthRatio < 0 || healthScale <= 0) return -1.0f;
        return (float) healthRatio / (float) healthScale;
    }

    public boolean isDead() {
        return hasHealthBar && healthScale > 0 && healthRatio == 0;
    }
}
