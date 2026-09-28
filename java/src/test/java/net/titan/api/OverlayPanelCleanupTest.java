package net.titan.api;

import net.titan.api.internal.OverlayBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.overlay.OverlayPanel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OverlayPanelCleanupTest {
    private final List<Integer> closed = new ArrayList<>();
    private final RuntimeException first = new IllegalStateException("first close");
    private final Error second = new AssertionError("second close");
    private int next;
    private final OverlayBackend backend = (OverlayBackend) Proxy.newProxyInstance(
        OverlayBackend.class.getClassLoader(), new Class<?>[] {OverlayBackend.class},
        (proxy, method, args) -> {
            if (method.getName().equals("overlayPanelRegister")) return ++next;
            if (method.getName().equals("overlayPanelUnregister")) {
                int handle = (Integer) args[0];
                closed.add(handle);
                if (handle == 1) throw first;
                if (handle == 2) throw second;
            }
            return null;
        });

    @AfterEach void cleanup() {
        try { TitanRuntime.closeAllOverlayPanels(); }
        finally { TitanRuntime.clearOverlayBackend(backend); }
    }

    @Test void ownerCleanupAttemptsEveryPanelAndPreservesFailures() {
        panels("owner", 3);
        assertSame(first, assertThrows(RuntimeException.class,
            () -> TitanRuntime.closeOverlayPanels("owner")));
        assertArrayEquals(new Throwable[] {second}, first.getSuppressed());
        assertEquals(Arrays.asList(1, 2, 3), closed);
        assertDoesNotThrow(() -> TitanRuntime.closeOverlayPanels("owner"));
        assertEquals(3, closed.size());
    }

    @Test void runtimeCleanupContinuesAcrossOwners() {
        panels("a", 1);
        panels("b", 2);
        assertThrows(Throwable.class, TitanRuntime::closeAllOverlayPanels);
        assertEquals(3, closed.size());
        assertTrue(closed.containsAll(Arrays.asList(1, 2, 3)));
        assertDoesNotThrow(TitanRuntime::closeAllOverlayPanels);
        assertEquals(3, closed.size());
    }

    private void panels(String owner, int count) {
        TitanRuntime.setOverlayBackend(backend);
        TitanRuntime.enterPlugin(owner);
        try {
            for (int i = 0; i < count; i++) new OverlayPanel("panel" + i);
        } finally {
            TitanRuntime.leavePlugin();
        }
    }
}
