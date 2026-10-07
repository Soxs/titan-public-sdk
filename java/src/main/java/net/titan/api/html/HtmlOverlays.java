package net.titan.api.html;

import net.titan.api.internal.HtmlUiBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;
import java.util.Objects;

/** Owner-bound HTML overlays with the same state and queue rules as {@link HtmlPanels}. */
public final class HtmlOverlays {
    private final Plugin owner;
    private HtmlOverlays(Plugin owner) { this.owner = owner; }
    public static HtmlOverlays of(Plugin owner) {
        return new HtmlOverlays(Objects.requireNonNull(owner, "owner"));
    }
    public HtmlUiResult setState(String id, String json) {
        return TitanRuntime.getHtmlUiBackend().setState(owner, HtmlUiBackend.OVERLAY, id, json);
    }
    /** Set the viewport in CSS pixels; width 80–600 and height 24–600. */
    public HtmlUiResult setSize(String id, int width, int height) {
        return TitanRuntime.getHtmlUiBackend().setSize(owner, id, width, height);
    }
    /**
     * Change visibility without discarding canonical state. Choose visibility
     * from plugin state, not an observed renderer-availability flag: the first
     * visible activation is what establishes that observation.
     */
    public HtmlUiResult setVisible(String id, boolean visible) {
        return TitanRuntime.getHtmlUiBackend().setVisible(owner, id, visible);
    }
    public HtmlUiResult postMessage(String id, String type, String payloadJson) {
        return postMessage(id, type, payloadJson, null);
    }
    public HtmlUiResult postMessage(String id, String type, String payloadJson, String correlationId) {
        return TitanRuntime.getHtmlUiBackend().postMessage(owner, HtmlUiBackend.OVERLAY,
            id, type, payloadJson, correlationId);
    }
}
