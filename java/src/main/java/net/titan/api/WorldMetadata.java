package net.titan.api;

public final class WorldMetadata {
    private int id;
    private long flags;
    private String host;
    private String activity;
    private int location;
    private int population;
    private int pingMs;
    private String region;

    public int id() { return id; }
    public long flags() { return flags; }
    public String host() { return host == null ? "" : host; }
    public String activity() { return activity == null ? "" : activity; }
    public int location() { return location; }
    public int population() { return population; }
    public int pingMs() { return pingMs; }
    public String region() { return region == null ? "" : region; }
    public boolean isMembers() { return (flags & 1L) != 0; }
    public boolean isBeta() { return (flags & (1L << 16)) != 0; }
}
