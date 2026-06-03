package net.titan.api;

import java.util.Objects;

public final class Tile {
    private int x;
    private int y;
    private int plane;

    public Tile() {}

    public Tile(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int plane() { return plane; }

    public int distanceTo(Tile other) {
        if (other == null || other.plane != plane) return Integer.MAX_VALUE;
        return distanceTo2D(other);
    }

    public int distanceTo2D(Tile other) {
        if (other == null) return Integer.MAX_VALUE;
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
        return x == other.x && y == other.y && plane == other.plane;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, plane);
    }

    @Override
    public String toString() {
        return "Tile{" + x + "," + y + "," + plane + "}";
    }
}
