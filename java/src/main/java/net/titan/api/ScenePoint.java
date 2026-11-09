package net.titan.api;

/** A two-dimensional scene tile coordinate (not a 1/128-tile LocalPoint). */
public final class ScenePoint {
    private final int x;
    private final int y;

    public ScenePoint(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int x() { return x; }
    public int y() { return y; }
    @Override public boolean equals(Object value) {
        return value instanceof ScenePoint && ((ScenePoint) value).x == x
            && ((ScenePoint) value).y == y;
    }
    @Override public int hashCode() { return 31 * x + y; }
    @Override public String toString() { return "ScenePoint{" + x + "," + y + "}"; }
}
