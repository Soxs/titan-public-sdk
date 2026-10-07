package net.titan.api.html;

import net.titan.api.internal.HtmlUiBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.HtmlOverlayPanel;
import net.titan.api.plugins.HtmlSidePanel;
import net.titan.api.plugins.Plugin;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HtmlPanelsTest {
    @Test void helpersRetainExactOwnerAndKeepSurfaceKindsDistinct() {
        Plugin owner = new Plugin() {};
        HtmlUiBackend backend = new HtmlUiBackend() {
            @Override public HtmlUiResult setState(Plugin received, int kind, String id, String json) {
                assertSame(owner, received); assertEquals(SIDE_PANEL, kind);
                assertEquals("stats", id); assertEquals("{\"x\":1}", json);
                return HtmlUiResult.OK;
            }
            @Override public HtmlUiResult postMessage(Plugin received, int kind, String id, String type,
                                                     String payload, String correlationId) {
                assertSame(owner, received); assertEquals(OVERLAY, kind);
                assertEquals("selected", type); assertEquals("[1,2]", payload); assertEquals("request", correlationId);
                return HtmlUiResult.OVERFLOW;
            }
        };
        TitanRuntime.setHtmlUiBackend(backend);
        try {
            assertTrue(HtmlPanels.of(owner).setState("stats", "{\"x\":1}").accepted());
            assertEquals(HtmlUiResult.OVERFLOW, HtmlOverlays.of(owner).postMessage("stats", "selected", "[1,2]", "request"));
            assertThrows(NullPointerException.class, () -> HtmlPanels.of(null));
        } finally { TitanRuntime.clearHtmlUiBackend(backend); }
        assertEquals(HtmlUiResult.UNAVAILABLE, HtmlPanels.of(owner).setState("stats", "{}"));
    }

    @Test void repeatableDeclarationsExposePackagedEntrypointsAndOverlayDefaults() {
        assertEquals(2, Declared.class.getAnnotationsByType(HtmlSidePanel.class).length);
        HtmlOverlayPanel overlay = Declared.class.getAnnotationsByType(HtmlOverlayPanel.class)[0];
        assertEquals("ui", overlay.resourceRoot()); assertEquals("index.html", overlay.entrypoint());
        assertEquals(220, overlay.width()); assertEquals(160, overlay.height());
        assertFalse(overlay.interactive()); assertTrue(overlay.visible());
    }
    @HtmlSidePanel(id="a",title="A",resourceRoot="ui") @HtmlSidePanel(id="b",title="B",resourceRoot="ui")
    @HtmlOverlayPanel(id="a",resourceRoot="ui")
    static class Declared implements Plugin {}
}
