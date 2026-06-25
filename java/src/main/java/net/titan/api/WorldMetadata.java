package net.titan.api;

import net.titan.api.internal.TitanRuntime;

public final class WorldMetadata {
    private boolean liveHandle = true;
    private int id;
    private long flags;
    private String host;
    private String activity;
    private int location;
    private int population;
    private int pingMs;
    private String region;

    private WorldMetadata live() { return TitanRuntime.currentLive(this); }

    public int id() { return live().id; }
    public long flags() { return live().flags; }
    public String host() {
        String value = live().host;
        return value == null ? "" : value;
    }
    public String activity() {
        String value = live().activity;
        return value == null ? "" : value;
    }
    public int location() { return live().location; }
    public int population() { return live().population; }
    public int pingMs() { return live().pingMs; }
    public String region() {
        String value = live().region;
        return value == null ? "" : value;
    }
    public boolean isMembers() { return (flags() & 1L) != 0; }
    public boolean isBeta() { return (flags() & (1L << 16)) != 0; }
    public boolean exists() { return TitanRuntime.liveExists(this); }
    public WorldMetadata snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) { return TitanRuntime.liveEquals(this, other); }

    @Override
    public int hashCode() { return TitanRuntime.liveHashCode(this); }
}
