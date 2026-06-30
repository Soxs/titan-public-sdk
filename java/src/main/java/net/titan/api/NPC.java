package net.titan.api;

import net.titan.api.internal.TitanRuntime;
import net.titan.api.utils.Magic;

import java.util.Collections;
import java.util.List;

public final class NPC implements Actor {
    private boolean liveHandle = true;
    private long entityPtr;
    private long definitionPtr;
    private int hashIndex;
    private int worldViewId = WorldView.CURRENT;
    private long worldViewPtr;
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
    private int movementPose;
    private int idlePose;
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
    private List<ActorSpotAnim> currentSpotAnims;

    private NPC live() { return TitanRuntime.currentLive(this); }

    @Override public long entityPtr() { return live().entityPtr; }
    public long definitionPtr() { return live().definitionPtr; }
    @Override public int hashIndex() { return live().hashIndex; }
    @Override public int worldViewId() { return live().worldViewId; }
    @Override public long worldViewPtr() { return live().worldViewPtr; }
    public int id() { return live().id; }
    @Override public int tileX() { return live().tileX; }
    @Override public int tileY() { return live().tileY; }
    @Override public int plane() { return live().plane; }
    @Override public int worldX() { return live().worldX; }
    @Override public int worldY() { return live().worldY; }
    @Override public int preciseX() { return live().preciseX; }
    @Override public int preciseY() { return live().preciseY; }
    @Override public int orientation() { return live().orientation; }
    @Override public int animation() { return live().animation; }
    @Override public int movementPose() { return live().movementPose; }
    @Override public int idlePose() { return live().idlePose; }
    @Override public int interactingIndex() { return live().interactingIndex; }
    @Override public int interactingType() { return live().interactingType; }
    @Override public int interactingPhase() { return live().interactingPhase; }
    public int overrideTransform() { return live().overrideTransform; }
    public int sizeX() { return live().sizeX; }
    public int sizeY() { return live().sizeY; }
    public int overheadIcon() { return live().overheadIcon; }
    public HeadIcon overheadHeadIcon() { return HeadIcon.fromId(overheadIcon()); }
    public boolean hasHeadIconOverride() { return live().hasHeadIconOverride; }
    public boolean isOverheadActive() {
        return overheadIcon() >= 0 || hasHeadIconOverride();
    }
    public boolean isOverheadActive(HeadIcon icon) {
        return icon != null && overheadIcon() == icon.id();
    }
    @Override public int healthRatio() { return live().healthRatio; }
    @Override public int healthScale() { return live().healthScale; }
    @Override public boolean hasHealthBar() { return live().hasHealthBar; }
    public String name() {
        String value = live().name;
        return value == null ? "" : value;
    }
    public List<String> actions() {
        List<String> value = live().actions;
        return value == null ? Collections.emptyList() : Collections.unmodifiableList(value);
    }
    @Override public List<WorldPoint> pathQueue() {
        List<WorldPoint> value = live().pathQueue;
        return value == null ? Collections.emptyList() : Collections.unmodifiableList(value);
    }
    @Override public List<ActorSpotAnim> currentSpotAnims() {
        List<ActorSpotAnim> value = live().currentSpotAnims;
        return value == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(value);
    }

    @Override
    public WorldArea worldArea() {
        return new WorldArea(worldX(), worldY(), Math.max(1, sizeX()), Math.max(1, sizeY()),
            plane(), worldViewId());
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
        return TitanRuntime.getInteractionBackend().interactNpcByIndex(
            action, hashIndex(), worldViewId());
    }

    @Override
    public boolean castOn(Magic.SpellInfo spell) { return Magic.castOn(spell, this); }
    @Override
    public boolean castOn(Magic.Standard spell) { return Magic.castOn(spell, this); }
    @Override
    public boolean castOn(Magic.Ancient spell) { return Magic.castOn(spell, this); }
    @Override
    public boolean castOn(Magic.Lunar spell) { return Magic.castOn(spell, this); }
    @Override
    public boolean castOn(Magic.Necromancy spell) { return Magic.castOn(spell, this); }

    public NPC snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) {
        return TitanRuntime.liveEquals(this, other);
    }

    @Override
    public int hashCode() {
        return TitanRuntime.liveHashCode(this);
    }
}
