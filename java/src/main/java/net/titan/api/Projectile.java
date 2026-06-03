package net.titan.api;

public final class Projectile implements Locatable<Projectile> {
    private long basePtr;
    private int plane;
    private int startX;
    private int startZ;
    private int targetX;
    private int targetZ;
    private int sourceEntity;
    private int targetEntity;
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

    @Override
    public LocalPoint localPoint() {
        return new LocalPoint(sceneX, sceneZ);
    }
}
