package net.titan.api;

public final class GraphicsObject implements Locatable<GraphicsObject> {
    private long basePtr;
    private int plane;
    private int spotAnimId;
    private int startCycle;
    private int height;
    private int preciseX;
    private int preciseZ;
    private int sceneX;
    private int sceneZ;
    private int tileX;
    private int tileY;
    private int worldX;
    private int worldY;
    private long worldViewPtr;
    private long seqStateAddr;
    private long seqTypePtr;

    public long basePtr() { return basePtr; }
    public int plane() { return plane; }
    public int spotAnimId() { return spotAnimId; }
    public int startCycle() { return startCycle; }
    public int height() { return height; }
    public int preciseX() { return preciseX; }
    public int preciseZ() { return preciseZ; }
    public int sceneX() { return sceneX; }
    public int sceneZ() { return sceneZ; }
    @Override
    public int tileX() { return tileX; }
    @Override
    public int tileY() { return tileY; }
    @Override
    public int worldX() { return worldX; }
    @Override
    public int worldY() { return worldY; }
    public long worldViewPtr() { return worldViewPtr; }
    public long seqStateAddr() { return seqStateAddr; }
    public long seqTypePtr() { return seqTypePtr; }

    @Override
    public LocalPoint localPoint() {
        return new LocalPoint(preciseX, preciseZ);
    }
}
