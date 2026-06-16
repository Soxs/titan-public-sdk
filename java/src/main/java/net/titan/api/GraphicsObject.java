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
    private int worldViewId = WorldView.CURRENT;
    private long worldViewPtr;
    private long seqStateAddr;
    private long seqTypePtr;
    private long seqPtr;
    private int animationId = -1;
    private int frameCycle;
    private int currentFrame;
    private int loopCount;
    private int totalCycle;
    private Sequence animation;

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
    @Override
    public int worldViewId() { return worldViewId; }
    @Override
    public long worldViewPtr() { return worldViewPtr; }
    public long seqStateAddr() { return seqStateAddr; }
    public long seqPtr() { return seqPtr != 0 ? seqPtr : seqTypePtr; }
    public long seqTypePtr() { return seqPtr(); }
    public int animationId() { return seqPtr() != 0 ? animationId : -1; }
    public int frameCycle() { return frameCycle; }
    public int currentFrame() { return currentFrame; }
    public int loopCount() { return loopCount; }
    public int totalCycle() { return totalCycle; }
    public Sequence animation() { return seqPtr() == 0 ? null : animation; }

    @Override
    public LocalPoint localPoint() {
        return new LocalPoint(preciseX, preciseZ, worldViewId);
    }
}
