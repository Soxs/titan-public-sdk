package net.titan.api.overlay;

import java.awt.Graphics2D;
import java.util.Objects;
import java.util.function.Consumer;
import net.titan.api.internal.Graphics2DBackend;
import net.titan.api.internal.TitanRuntime;

/**
 * Owned reusable premultiplied Java2D raster, painted explicitly during the
 * owning plugin's renderOverlay callback. Each frame starts transparent.
 * Graphics2D is disposed after the callback and must not be retained.
 */
public final class Graphics2DOverlay implements AutoCloseable {
    private final Graphics2DBackend.Session session;
    private final OverlayLayer layer;
    private final boolean anchored;

    /** Canvas-positioned raster. Dimensions are 1..4096 and at most 8M pixels. */
    public Graphics2DOverlay(String stableId, int width, int height, OverlayLayer layer) {
        this(stableId, width, height, Objects.requireNonNull(layer), -1, 50);
    }

    private Graphics2DOverlay(String stableId, int width, int height, OverlayLayer layer,
                              int anchor, int priority) {
        this.layer = layer;
        this.anchored = anchor >= 0;
        session = TitanRuntime.getGraphics2DBackend().create(TitanRuntime.currentPluginId(),
            Objects.requireNonNull(stableId), width, height, layer.protocolId(), anchor, priority);
    }

    /** Above-widgets HUD with native anchor, Alt-drag and saved placement.
     * stableId is plugin-local and must remain stable between plugin updates.
     */
    public static Graphics2DOverlay anchored(String stableId, int width, int height,
                                             OverlayPanelAnchor anchor, int priority) {
        return new Graphics2DOverlay(stableId, width, height, OverlayLayer.ABOVE_WIDGETS,
            Objects.requireNonNull(anchor).protocolId(), priority);
    }

    public int width() { return session.width(); }
    public int height() { return session.height(); }
    public OverlayLayer layer() { return layer; }

    /** Resize the reusable raster; cannot run recursively during its painting. */
    public void resize(int width, int height) { session.resize(width, height); }

    /** Paint at canvas origin, or at the native layout position for an anchored HUD.
     * Wrong overlay layer returns false without painting. Off-render, foreign or
     * retired scopes throw IllegalStateException. Superseded frames or a native
     * raster that cannot currently be admitted return false.
     */
    public boolean render(Consumer<Graphics2D> painter) {
        return session.render(0, 0, Objects.requireNonNull(painter));
    }

    /** Paint a canvas raster; anchored HUD positions belong to native layout. */
    public boolean render(int x, int y, Consumer<Graphics2D> painter) {
        if (anchored) throw new IllegalStateException("Anchored Graphics2D overlays use native placement");
        return session.render(x, y, Objects.requireNonNull(painter));
    }

    /**
     * Present each frame and repaint when the supplied content revision changes.
     * The revision must cover every input to the painter, including font and
     * animation changes. Resize and a new activation also require a repaint.
     */
    public boolean renderCached(long contentRevision, Consumer<Graphics2D> painter) {
        return session.renderCached(0, 0, contentRevision, Objects.requireNonNull(painter));
    }

    /** Present cached content at the current canvas coordinates. Moving alone reuses the pixels. */
    public boolean renderCached(int x, int y, long contentRevision, Consumer<Graphics2D> painter) {
        if (anchored) throw new IllegalStateException("Anchored Graphics2D overlays use native placement");
        return session.renderCached(x, y, contentRevision, Objects.requireNonNull(painter));
    }

    /** Idempotent. Owner unload also retires this raster and its CPU/GPU resources. */
    @Override public void close() { session.close(); }
}
