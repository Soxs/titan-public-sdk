package net.titan.api.events;

/** The game's drawable client area changed size, in physical pixels. */
public final class CanvasSizeChanged {
    private final int width, height;

    public CanvasSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width() { return width; }
    public int height() { return height; }
}
