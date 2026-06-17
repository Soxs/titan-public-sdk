package net.titan.api;

import java.util.Objects;
import java.util.Optional;

public final class WorldPoint {
    private int x;
    private int y;
    private int z;
    private int worldViewId = WorldView.CURRENT;

    public WorldPoint() {}

    public WorldPoint(int x, int y, int z) {
        this(x, y, z, WorldView.CURRENT);
    }

    public WorldPoint(int x, int y, int z, int worldViewId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldViewId = worldViewId;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int z() { return z; }
    public int worldViewId() { return worldViewId; }

    public int distanceTo(WorldPoint other) {
        if (other == null || other.z != z || !WorldView.same(other.worldViewId, worldViewId)) {
            return Integer.MAX_VALUE;
        }
        return distanceTo2D(other);
    }

    public int distanceTo2D(WorldPoint other) {
        if (other == null || !WorldView.same(other.worldViewId, worldViewId)) return Integer.MAX_VALUE;
        return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
    }

    public int regionId() { return ((x >> 6) << 8) | (y >> 6); }
    public int regionX() { return x & 63; }
    public int regionY() { return y & 63; }

    public WorldArea worldArea() {
        return new WorldArea(x, y, 1, 1, z, worldViewId);
    }

    public boolean isInMeleeDistance(WorldPoint other) {
        return worldArea().isInMeleeDistance(other);
    }

    public boolean isInMeleeDistance(WorldArea other) {
        return other != null && other.isInMeleeDistance(this);
    }

    public boolean isInMeleeDistance(Locatable<?> other) {
        return other != null && isInMeleeDistance(other.worldArea());
    }

    public WorldPoint dx(int delta) { return new WorldPoint(x + delta, y, z, worldViewId); }
    public WorldPoint dy(int delta) { return new WorldPoint(x, y + delta, z, worldViewId); }
    public WorldPoint dz(int delta) { return new WorldPoint(x, y, z + delta, worldViewId); }

    public Optional<WorldPoint> fromLocalInstance() {
        try {
            return Titan.client().fromLocalInstance(this);
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    public Optional<WorldPoint> toLocalInstance() {
        try {
            return Titan.client().toLocalInstance(this);
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof WorldPoint)) return false;
        WorldPoint other = (WorldPoint) object;
        return x == other.x && y == other.y && z == other.z
            && WorldView.same(worldViewId, other.worldViewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, worldViewId);
    }

    @Override
    public String toString() {
        return "WorldPoint{" + x + "," + y + "," + z + ",wv=" + worldViewId + "}";
    }
}
