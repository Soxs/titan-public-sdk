package net.titan.api;

import java.util.List;
import java.util.Optional;

import net.titan.api.internal.TitanRuntime;

public final class Projectile implements Locatable<Projectile> {
    private boolean liveHandle = true;
    private long basePtr;
    private int plane;
    private int startX;
    private int startY;
    private int targetX;
    private int targetY;
    private int sourceEntity = -1;
    private int targetEntity = -1;
    private int rawSourceEntity;
    private int rawTargetEntity;
    private int sourceEntityType = EntityType.NONE;
    private int targetEntityType = EntityType.NONE;
    private int spotAnimId;
    private int startTick;
    private int endTick;
    private int sceneX;
    private int height;
    private int sceneY;
    private int tileX;
    private int tileY;
    private int worldX;
    private int worldY;
    private long yaw;
    private long pitch;
    private boolean hasMoved;

    private Projectile live() { return TitanRuntime.currentLive(this); }

    public long basePtr() { return live().basePtr; }
    public int plane() { return live().plane; }
    public int startX() { return live().startX; }
    public int startY() { return live().startY; }
    public int targetX() { return live().targetX; }
    public int targetY() { return live().targetY; }
    public int sourceEntity() { return live().sourceEntity; }
    public int targetEntity() { return live().targetEntity; }
    public int rawSourceEntity() { return live().rawSourceEntity; }
    public int rawTargetEntity() { return live().rawTargetEntity; }
    public int sourceEntityType() { return live().sourceEntityType; }
    public int targetEntityType() { return live().targetEntityType; }
    public int spotAnimId() { return live().spotAnimId; }
    public int startTick() { return live().startTick; }
    public int endTick() { return live().endTick; }
    public int sceneX() { return live().sceneX; }
    public int height() { return live().height; }
    public int sceneY() { return live().sceneY; }
    @Override
    public int tileX() { return live().tileX; }
    @Override
    public int tileY() { return live().tileY; }
    @Override
    public int worldX() { return live().worldX; }
    @Override
    public int worldY() { return live().worldY; }
    public long yaw() { return live().yaw; }
    public long pitch() { return live().pitch; }
    public boolean hasMoved() { return live().hasMoved; }

    public Optional<Actor> sourceActor() {
        return resolveActor(sourceEntity(), sourceEntityType());
    }

    public Optional<Actor> targetActor() {
        return resolveActor(targetEntity(), targetEntityType());
    }

    @Override
    public LocalPoint localPoint() {
        return new LocalPoint(sceneX(), sceneY());
    }

    private Optional<Actor> resolveActor(int hashIndex, int entityType) {
        if (hashIndex < 0 || entityType == EntityType.NONE) return Optional.empty();
        Client value;
        try {
            value = Titan.client();
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
        if (entityType == EntityType.PLAYER) {
            List<Player> players = value.players();
            if (players == null) return Optional.empty();
            for (Player player : players) {
                if (player != null && player.hashIndex() == hashIndex) return Optional.of(player);
            }
        } else if (entityType == EntityType.NPC) {
            List<NPC> npcs = value.npcs();
            if (npcs == null) return Optional.empty();
            for (NPC npc : npcs) {
                if (npc != null && npc.hashIndex() == hashIndex) return Optional.of(npc);
            }
        }
        return Optional.empty();
    }

    public boolean exists() { return TitanRuntime.liveExists(this); }

    public Projectile snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) {
        return TitanRuntime.liveEquals(this, other);
    }

    @Override
    public int hashCode() {
        return TitanRuntime.liveHashCode(this);
    }
}
