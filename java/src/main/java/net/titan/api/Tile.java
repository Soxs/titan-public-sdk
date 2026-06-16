package net.titan.api;

import java.util.Objects;

public final class Tile {
    private int x;
    private int y;
    private int plane;
    private int worldViewId = WorldView.CURRENT;

    public Tile() {}

    public Tile(int x, int y, int plane) {
        this(x, y, plane, WorldView.CURRENT);
    }

    public Tile(int x, int y, int plane, int worldViewId) {
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.worldViewId = worldViewId;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int plane() { return plane; }
    public int worldViewId() { return worldViewId; }

    public int distanceTo(Tile other) {
        if (other == null || other.plane != plane || !WorldView.same(other.worldViewId, worldViewId)) {
            return Integer.MAX_VALUE;
        }
        return distanceTo2D(other);
    }

    public int distanceTo2D(Tile other) {
        if (other == null || !WorldView.same(other.worldViewId, worldViewId)) return Integer.MAX_VALUE;
        return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
    }

    public boolean isInScene(int sceneSizeX, int sceneSizeY) {
        return x >= 0 && x < sceneSizeX && y >= 0 && y < sceneSizeY;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Tile)) return false;
        Tile other = (Tile) object;
        return x == other.x && y == other.y && plane == other.plane
            && WorldView.same(worldViewId, other.worldViewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, plane, worldViewId);
    }

    @Override
    public String toString() {
        return "Tile{" + x + "," + y + "," + plane + ",wv=" + worldViewId + "}";
    }
}
