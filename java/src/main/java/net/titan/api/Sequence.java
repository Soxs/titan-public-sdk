package net.titan.api;

public final class Sequence {
    private long ptr;
    private int id;
    private long flags;
    private int frameCount;
    private long frameIds;
    private long frameLengths;
    private int totalDuration;
    private int frameStep;
    private int repeatLimit;

    public long ptr() { return ptr; }
    public int id() { return id; }
    public long flags() { return flags; }
    public int numFrames() { return frameCount; }
    public int frameCount() { return frameCount; }
    public long frameIDs() { return frameIds; }
    public long frameIds() { return frameIds; }
    public long frameLengths() { return frameLengths; }
    public int totalDuration() { return totalDuration; }
    public int frameStep() { return frameStep; }
    public int repeatLimit() { return repeatLimit; }
}
