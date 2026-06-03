package net.titan.api;

import java.util.Objects;

public final class WorldPoint {
    private int x;
    private int y;
    private int z;

    public WorldPoint() {}

    public WorldPoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int z() { return z; }

    public int distanceTo(WorldPoint other) {
        if (other == null || other.z != z) return Integer.MAX_VALUE;
        return distanceTo2D(other);
    }

    public int distanceTo2D(WorldPoint other) {
        if (other == null) return Integer.MAX_VALUE;
        return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
    }

    public int regionId() { return ((x >> 6) << 8) | (y >> 6); }
    public int regionX() { return x & 63; }
    public int regionY() { return y & 63; }

    public WorldPoint dx(int delta) { return new WorldPoint(x + delta, y, z); }
    public WorldPoint dy(int delta) { return new WorldPoint(x, y + delta, z); }
    public WorldPoint dz(int delta) { return new WorldPoint(x, y, z + delta); }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof WorldPoint)) return false;
        WorldPoint other = (WorldPoint) object;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "WorldPoint{" + x + "," + y + "," + z + "}";
    }
}
