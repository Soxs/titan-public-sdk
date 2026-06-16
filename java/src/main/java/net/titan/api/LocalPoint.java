package net.titan.api;

import java.util.Objects;

public final class LocalPoint {
    private int x;
    private int y;
    private int worldViewId = WorldView.CURRENT;

    public LocalPoint() {}

    public LocalPoint(int x, int y) {
        this(x, y, WorldView.CURRENT);
    }

    public LocalPoint(int x, int y, int worldViewId) {
        this.x = x;
        this.y = y;
        this.worldViewId = worldViewId;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int worldViewId() { return worldViewId; }
    public int sceneX() { return x >> 7; }
    public int sceneY() { return y >> 7; }

    public LocalPoint dx(int delta) { return new LocalPoint(x + delta, y, worldViewId); }
    public LocalPoint dy(int delta) { return new LocalPoint(x, y + delta, worldViewId); }
    public LocalPoint plus(int dx, int dy) { return new LocalPoint(x + dx, y + dy, worldViewId); }

    public int distanceTo(LocalPoint other) {
        if (other == null || !WorldView.same(other.worldViewId, worldViewId)) return Integer.MAX_VALUE;
        return (int) Math.hypot(x - other.x, y - other.y);
    }

    public boolean isInScene(int sceneSizeX, int sceneSizeY) {
        return x >= 0 && x < (sceneSizeX << 7)
            && y >= 0 && y < (sceneSizeY << 7);
    }

    public static LocalPoint fromScene(int sceneX, int sceneY) {
        return fromScene(sceneX, sceneY, WorldView.CURRENT);
    }

    public static LocalPoint fromScene(int sceneX, int sceneY, int worldViewId) {
        return new LocalPoint((sceneX << 7) + 64, (sceneY << 7) + 64, worldViewId);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof LocalPoint)) return false;
        LocalPoint other = (LocalPoint) object;
        return x == other.x && y == other.y && WorldView.same(worldViewId, other.worldViewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, worldViewId);
    }

    @Override
    public String toString() {
        return "LocalPoint{" + x + "," + y + ",wv=" + worldViewId + "}";
    }
}
