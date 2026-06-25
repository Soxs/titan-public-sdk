package net.titan.api;

import net.titan.api.internal.TitanRuntime;

public final class GraphicsObject implements Locatable<GraphicsObject> {
    private boolean liveHandle = true;
    private long basePtr;
    private int plane;
    private int spotAnimId;
    private int startCycle;
    private int height;
    private int preciseX;
    private int preciseY;
    private int sceneX;
    private int sceneY;
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

    private GraphicsObject live() { return TitanRuntime.currentLive(this); }

    public long basePtr() { return live().basePtr; }
    public int plane() { return live().plane; }
    public int spotAnimId() { return live().spotAnimId; }
    public int startCycle() { return live().startCycle; }
    public int height() { return live().height; }
    public int preciseX() { return live().preciseX; }
    public int preciseY() { return live().preciseY; }
    public int sceneX() { return live().sceneX; }
    public int sceneY() { return live().sceneY; }
    @Override
    public int tileX() { return live().tileX; }
    @Override
    public int tileY() { return live().tileY; }
    @Override
    public int worldX() { return live().worldX; }
    @Override
    public int worldY() { return live().worldY; }
    @Override
    public int worldViewId() { return live().worldViewId; }
    @Override
    public long worldViewPtr() { return live().worldViewPtr; }
    public long seqStateAddr() { return live().seqStateAddr; }
    public long seqPtr() {
        GraphicsObject g = live();
        return g.seqPtr != 0 ? g.seqPtr : g.seqTypePtr;
    }
    public long seqTypePtr() { return seqPtr(); }
    public int animationId() { return seqPtr() != 0 ? live().animationId : -1; }
    public int frameCycle() { return live().frameCycle; }
    public int currentFrame() { return live().currentFrame; }
    public int loopCount() { return live().loopCount; }
    public int totalCycle() { return live().totalCycle; }
    public Sequence animation() { return seqPtr() == 0 ? null : live().animation; }

    @Override
    public LocalPoint localPoint() {
        return new LocalPoint(preciseX(), preciseY(), worldViewId());
    }

    public boolean exists() { return TitanRuntime.liveExists(this); }

    public GraphicsObject snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) {
        return TitanRuntime.liveEquals(this, other);
    }

    @Override
    public int hashCode() {
        return TitanRuntime.liveHashCode(this);
    }
}
