package net.titan.api.panel;

import net.titan.api.internal.PanelBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PanelsTest {
    @Test void facadeRetainsExactOwnerAndDefaultsToUnavailable() {
        Plugin owner = new Plugin() {};
        Panels panels = Panels.of(owner);
        assertFalse(panels.requestOpen("stats"));
        assertThrows(NullPointerException.class, () -> Panels.of(null));
        PanelBackend backend = (received, id) -> {
            assertSame(owner, received);
            return "stats".equals(id);
        };
        TitanRuntime.setPanelBackend(backend);
        try {
            assertTrue(panels.requestOpen("stats"));
            assertFalse(panels.requestOpen("missing"));
            assertFalse(panels.requestOpen(null));
            TitanRuntime.clearPanelBackend((received, id) -> false);
            assertTrue(panels.requestOpen("stats"), "unrelated backend cannot clear current registration");
        } finally { TitanRuntime.clearPanelBackend(backend); }
        assertFalse(panels.requestOpen("stats"));
    }
}
