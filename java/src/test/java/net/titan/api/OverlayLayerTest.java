package net.titan.api;

import net.titan.api.overlay.OverlayLayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OverlayLayerTest {
    @Test void protocolLayersRemainDistinct() {
        assertEquals(0, OverlayLayer.ABOVE_SCENE.protocolId());
        assertEquals(1, OverlayLayer.ABOVE_WIDGETS.protocolId());
        assertEquals(2, OverlayLayer.ABOVE_ALL.protocolId());
        for (OverlayLayer layer : OverlayLayer.values()) {
            assertSame(layer, OverlayLayer.fromProtocolId(layer.protocolId()));
        }
        assertThrows(IllegalArgumentException.class, () -> OverlayLayer.fromProtocolId(-1));
        assertThrows(IllegalArgumentException.class, () -> OverlayLayer.fromProtocolId(255));
    }
}
