package net.titan.api;

/** Snapshot of one active actor-attached spot animation. Added in SDK 76. */
public final class ActorSpotAnim {
    private int slot;
    private int id = -1;
    private int height;
    private int expireCycle;

    public int slot() { return slot; }
    public int id() { return id; }
    public int height() { return height; }
    public int expireCycle() { return expireCycle; }
}
