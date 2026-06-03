package net.titan.api;

import java.util.Objects;

public final class LocalPoint {
    private int x;
    private int y;

    public LocalPoint() {}

    public LocalPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int sceneX() { return x >> 7; }
    public int sceneY() { return y >> 7; }

    public LocalPoint dx(int delta) { return new LocalPoint(x + delta, y); }
    public LocalPoint dy(int delta) { return new LocalPoint(x, y + delta); }
    public LocalPoint plus(int dx, int dy) { return new LocalPoint(x + dx, y + dy); }

    public int distanceTo(LocalPoint other) {
        if (other == null) return Integer.MAX_VALUE;
        return (int) Math.hypot(x - other.x, y - other.y);
    }

    public boolean isInScene(int sceneSizeX, int sceneSizeY) {
        return x >= 0 && x < (sceneSizeX << 7)
            && y >= 0 && y < (sceneSizeY << 7);
    }

    public static LocalPoint fromScene(int sceneX, int sceneY) {
        return new LocalPoint((sceneX << 7) + 64, (sceneY << 7) + 64);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof LocalPoint)) return false;
        LocalPoint other = (LocalPoint) object;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "LocalPoint{" + x + "," + y + "}";
    }
}
