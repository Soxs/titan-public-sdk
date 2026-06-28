package net.titan.api;

import java.util.Collections;
import java.util.List;

import net.titan.api.internal.TitanRuntime;

public final class Player implements Actor {
    private boolean liveHandle = true;
    private long entityPtr;
    private int hashIndex;
    private int worldViewId = WorldView.CURRENT;
    private long worldViewPtr;
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
    private List<ActorSpotAnim> currentSpotAnims;
    private PlayerComposition composition;

    private Player live() { return TitanRuntime.currentLive(this); }

    @Override
    public long entityPtr() { return live().entityPtr; }
    @Override
    public int worldViewId() { return live().worldViewId; }
    @Override
    public long worldViewPtr() { return live().worldViewPtr; }
    public String name() {
        String value = live().name;
        return value == null ? "" : value;
    }
    @Override
    public int tileX() { return live().tileX; }
    @Override
    public int tileY() { return live().tileY; }
    @Override
    public int plane() { return live().plane; }
    @Override
    public int worldX() { return live().worldX; }
    @Override
    public int worldY() { return live().worldY; }
    @Override
    public int preciseX() { return live().preciseX; }
    @Override
    public int preciseY() { return live().preciseY; }
    @Override
    public int orientation() { return live().orientation; }
    @Override
    public int animation() { return live().animation; }
    @Override
    public int movementPose() { return live().movementPose; }
    @Override
    public int idlePose() { return live().idlePose; }
    public int combatLevel() { return live().combatLevel; }
    @Override
    public int hashIndex() { return live().hashIndex; }
    @Override
    public int interactingIndex() { return live().interactingIndex; }
    @Override
    public int interactingType() { return live().interactingType; }
    @Override
    public int interactingPhase() { return live().interactingPhase; }
    public int overheadIcon() { return live().overheadIcon; }
    public int skullIcon() { return live().skullIcon; }
    @Override
    public int healthRatio() { return live().healthRatio; }
    @Override
    public int healthScale() { return live().healthScale; }
    @Override
    public boolean hasHealthBar() { return live().hasHealthBar; }

    public boolean isOverheadActive() { return overheadIcon() >= 0; }
    public boolean isOverheadActive(HeadIcon icon) {
        return icon != null && overheadIcon() == icon.id();
    }
    public HeadIcon overheadHeadIcon() { return HeadIcon.fromId(overheadIcon()); }
    public boolean isSkulled() { return skullIcon() >= 0; }
    @Override
    public boolean isStationary() {
        Player p = live();
        return p.movementPose == p.idlePose;
    }
    @Override
    public boolean isAnimating() { return animation() != -1; }
    public boolean isIdle() { return isStationary() && !isAnimating(); }
    public boolean isHidden() { return live().hidden; }
    @Override
    public List<WorldPoint> pathQueue() {
        List<WorldPoint> value = live().pathQueue;
        return value == null ? Collections.emptyList() : Collections.unmodifiableList(value);
    }
    @Override
    public List<ActorSpotAnim> currentSpotAnims() {
        List<ActorSpotAnim> value = live().currentSpotAnims;
        return value == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(value);
    }

    public PlayerComposition getPlayerComposition() {
        PlayerComposition value = live().composition;
        return value == null ? PlayerComposition.unavailable() : value;
    }

    @Override
    public float healthPercent() {
        int ratio = healthRatio();
        int scale = healthScale();
        if (ratio < 0 || scale <= 0) return -1.0f;
        return (float) ratio / (float) scale;
    }

    @Override
    public boolean isDead() {
        return hasHealthBar() && healthScale() > 0 && healthRatio() == 0;
    }

    public Player snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) {
        return TitanRuntime.liveEquals(this, other);
    }

    @Override
    public int hashCode() {
        return TitanRuntime.liveHashCode(this);
    }
}
