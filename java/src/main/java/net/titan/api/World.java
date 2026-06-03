package net.titan.api;

public final class World {
    private int id;
    private long flags;
    private String activity;
    private String location;

    public int id() { return id; }
    public long flags() { return flags; }
    public String activity() { return activity == null ? "" : activity; }
    public String location() { return location == null ? "" : location; }
    public boolean isMembers() { return (flags & 1L) != 0; }
    public boolean isBeta() { return (flags & (1L << 16)) != 0; }
}
