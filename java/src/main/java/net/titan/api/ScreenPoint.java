package net.titan.api;

public final class ScreenPoint {
    private int x;
    private int y;

    public ScreenPoint() {}

    public ScreenPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }
}
