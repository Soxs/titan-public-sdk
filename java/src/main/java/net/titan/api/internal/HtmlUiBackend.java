package net.titan.api.internal;

import net.titan.api.html.HtmlUiResult;
import net.titan.api.plugins.Plugin;

/** Runtime implementation detail. Each write resolves the exact loaded owner. */
public interface HtmlUiBackend {
    int SIDE_PANEL = 1;
    int OVERLAY = 2;
    HtmlUiResult setState(Plugin owner, int kind, String id, String json);
    HtmlUiResult postMessage(Plugin owner, int kind, String id, String type,
                             String payloadJson, String correlationId);
    default HtmlUiResult setSize(Plugin owner, String id, int width, int height) { return HtmlUiResult.UNAVAILABLE; }
    default HtmlUiResult setVisible(Plugin owner, String id, boolean visible) { return HtmlUiResult.UNAVAILABLE; }
    default void checkNativeOverlayName(String pluginId, String name) {}

    HtmlUiBackend UNAVAILABLE = new HtmlUiBackend() {
        @Override public HtmlUiResult setState(Plugin owner, int kind, String id, String json) {
            return HtmlUiResult.UNAVAILABLE;
        }
        @Override public HtmlUiResult postMessage(Plugin owner, int kind, String id,
                String type, String payloadJson, String correlationId) {
            return HtmlUiResult.UNAVAILABLE;
        }
    };
}
