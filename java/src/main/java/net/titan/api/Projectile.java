package net.titan.api;

import java.util.List;
import java.util.Optional;

public final class Projectile implements Locatable<Projectile> {
    private transient Client client;
    private long basePtr;
    private int plane;
    private int startX;
    private int startZ;
    private int targetX;
    private int targetZ;
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
    private int sceneY;
    private int sceneZ;
    private int tileX;
    private int tileY;
    private int worldX;
    private int worldY;
    private long yaw;
    private long pitch;
    private boolean hasMoved;

    public long basePtr() { return basePtr; }
    public int plane() { return plane; }
    public int startX() { return startX; }
    public int startZ() { return startZ; }
    public int targetX() { return targetX; }
    public int targetZ() { return targetZ; }
    public int sourceEntity() { return sourceEntity; }
    public int targetEntity() { return targetEntity; }
    public int rawSourceEntity() { return rawSourceEntity; }
    public int rawTargetEntity() { return rawTargetEntity; }
    public int sourceEntityType() { return sourceEntityType; }
    public int targetEntityType() { return targetEntityType; }
    public int spotAnimId() { return spotAnimId; }
    public int startTick() { return startTick; }
    public int endTick() { return endTick; }
    public int sceneX() { return sceneX; }
    public int sceneY() { return sceneY; }
    public int sceneZ() { return sceneZ; }
    @Override
    public int tileX() { return tileX; }
    @Override
    public int tileY() { return tileY; }
    @Override
    public int worldX() { return worldX; }
    @Override
    public int worldY() { return worldY; }
    public long yaw() { return yaw; }
    public long pitch() { return pitch; }
    public boolean hasMoved() { return hasMoved; }

    public Optional<Actor> sourceActor() {
        return resolveActor(sourceEntity, sourceEntityType);
    }

    public Optional<Actor> targetActor() {
        return resolveActor(targetEntity, targetEntityType);
    }

    @Override
    public LocalPoint localPoint() {
        return new LocalPoint(sceneX, sceneZ);
    }

    private Optional<Actor> resolveActor(int hashIndex, int entityType) {
        if (hashIndex < 0 || entityType == EntityType.NONE) return Optional.empty();
        Client value = client;
        if (value == null) {
            try {
                value = Titan.client();
            } catch (RuntimeException ignored) {
                return Optional.empty();
            }
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
}
