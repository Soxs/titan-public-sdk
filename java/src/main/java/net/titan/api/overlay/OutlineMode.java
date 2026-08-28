package net.titan.api.overlay;

/**
 * Polygon shape for the model-outline overlays
 * ({@link OverlayDraw#entityOutline} / {@link OverlayDraw#tileObjectOutline}).
 *
 * <p>{@link #CONVEX} is the clean, tight bounding silhouette; {@link #CONCAVE}
 * hugs the projected model vertices and follows the model's concavities.
 * Added in SDK 123; hosts that predate it render both as convex.
 */
public enum OutlineMode {
    CONVEX(0),
    CONCAVE(1);

    private final int value;

    OutlineMode(int value) {
        this.value = value;
    }

    /** Wire value passed across the host boundary. */
    public int value() {
        return value;
    }
}
