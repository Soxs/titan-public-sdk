package net.titan.api;

public final class GraphicsObject {
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
    public int tileX() { return tileX; }
    public int tileY() { return tileY; }
    public int worldX() { return worldX; }
    public int worldY() { return worldY; }
    public long worldViewPtr() { return worldViewPtr; }
    public long seqStateAddr() { return seqStateAddr; }
    public long seqTypePtr() { return seqTypePtr; }
}
