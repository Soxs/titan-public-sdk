package net.titan.api;

import net.titan.api.internal.TitanRuntime;

public final class World {
    private boolean liveHandle = true;
    private int id;
    private long flags;
    private String activity;
    private String location;

    private World live() { return TitanRuntime.currentLive(this); }

    public int id() { return live().id; }
    public long flags() { return live().flags; }
    public String activity() {
        String value = live().activity;
        return value == null ? "" : value;
    }
    public String location() {
        String value = live().location;
        return value == null ? "" : value;
    }
    public boolean isMembers() { return (flags() & 1L) != 0; }
    public boolean isBeta() { return (flags() & (1L << 16)) != 0; }
    public boolean exists() { return TitanRuntime.liveExists(this); }
    public World snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) { return TitanRuntime.liveEquals(this, other); }

    @Override
    public int hashCode() { return TitanRuntime.liveHashCode(this); }
}
