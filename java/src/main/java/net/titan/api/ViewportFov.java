package net.titan.api;

/**
 * The viewport_setfov (CS2 6200) projection scales (SDK 162). The game stores
 * {@code 128 * 2^(x / 256)} of the script zoom x, so larger is closer and 0x80
 * is twice as far out as 0x100. {@link #smallViewport()} applies at viewport
 * heights up to 334, {@link #largeViewport()} from 434, blended between.
 */
public final class ViewportFov {
    private final int smallViewport;
    private final int largeViewport;

    public ViewportFov(int smallViewport, int largeViewport) {
        this.smallViewport = smallViewport;
        this.largeViewport = largeViewport;
    }

    public int smallViewport() { return smallViewport; }
    public int largeViewport() { return largeViewport; }

    @Override public boolean equals(Object other) {
        if (!(other instanceof ViewportFov)) return false;
        ViewportFov that = (ViewportFov) other;
        return smallViewport == that.smallViewport && largeViewport == that.largeViewport;
    }
    @Override public int hashCode() { return 31 * smallViewport + largeViewport; }
    @Override public String toString() {
        return "ViewportFov[smallViewport=" + smallViewport + ", largeViewport=" + largeViewport + "]";
    }
}
