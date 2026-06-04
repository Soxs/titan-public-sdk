package net.titan.api.overlay;

public final class Overlay {
    private Overlay() {}

    public static OverlayDraw draw() {
        return OverlayDraw.current();
    }
}
