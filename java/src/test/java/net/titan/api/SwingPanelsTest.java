package net.titan.api;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.swing.JComponent;
import java.util.concurrent.CompletionStage;
import javax.swing.JPanel;
import net.titan.api.internal.SwingPanelBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;
import net.titan.api.swing.SwingPanel;
import net.titan.api.swing.SwingPanels;
import net.titan.api.swing.SwingCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SwingPanelsTest {
    @AfterEach void reset() { TitanRuntime.clearSwingPanelBackend(TitanRuntime.getSwingPanelBackend()); }

    @Test void oldHostRejectsExplicitlyAndNullOwnersOrFactoriesAreInvalid() {
        reset();
        assertThrows(UnsupportedOperationException.class, () -> SwingPanels.of(new Plugin() {}).register("panel", "Panel", JPanel::new));
        assertThrows(NullPointerException.class, () -> SwingPanels.of(null));
        assertThrows(NullPointerException.class, () -> SwingPanels.of(new Plugin() {}).register("panel", "Panel", null));
    }

    @Test void exactOwnerAndOwnedIconCrossTheBackendWithoutRunningFactory() {
        Plugin owner = new Plugin() {};
        byte[] icon = {1, 2, 3};
        SwingPanel handle = new SwingPanel() {
            public CompletionStage<Void> ready() { return CompletableFuture.completedFuture(null); }
            public boolean requestOpen() { return true; }
            public CompletionStage<Void> closeAsync() { return CompletableFuture.completedFuture(null); }
            public void close() {}
        };
        SwingPanelBackend first = (actual, id, title, priority, png, factory) -> {
            assertSame(owner, actual); assertEquals("panel", id); assertEquals("Panel", title); assertEquals(7, priority);
            assertArrayEquals(icon, png); assertNotSame(icon, png); png[0] = 99;
            return handle;
        };
        TitanRuntime.setSwingPanelBackend(first);
        assertSame(handle, SwingPanels.of(owner).register("panel", "Panel", 7, icon, () -> { fail("Factory must be deferred to EDT"); return null; }));
        assertEquals(1, icon[0]);
        TitanRuntime.setSwingPanelBackend(SwingPanelBackend.UNAVAILABLE);
        TitanRuntime.clearSwingPanelBackend(first);
        assertSame(SwingPanelBackend.UNAVAILABLE, TitanRuntime.getSwingPanelBackend());
    }

    @Test void oldBackendsKeepContentPanelsButRejectOptionalCommands() {
        Plugin owner = new Plugin() {};
        AtomicInteger legacy = new AtomicInteger();
        TitanRuntime.setSwingPanelBackend((plugin, id, title, priority, icon, factory) -> { legacy.incrementAndGet(); return null; });
        SwingPanels.of(owner).register("legacy", "Legacy", JPanel::new); assertEquals(1, legacy.get());
        assertThrows(UnsupportedOperationException.class, () -> SwingPanels.of(owner).action("action", "Action", () -> fail()));
        assertThrows(UnsupportedOperationException.class, () -> SwingPanels.of(owner).register("menu", "Menu", JPanel::new, List::of));
        assertEquals(1, legacy.get());
    }

    @Test void commandApiPreservesExactOwnerCallbacksAndDeferredSupplierWithOwnedIcon() {
        Plugin owner = new Plugin() {};
        Runnable action = () -> fail("Registration must not invoke the action");
        Supplier<List<SwingCommand>> menu = () -> { fail("Menu must be evaluated only when requested"); return List.of(); };
        byte[] icon = {1, 2}; AtomicInteger forwarded = new AtomicInteger();
        TitanRuntime.setSwingPanelBackend(new SwingPanelBackend() {
            public SwingPanel register(Plugin plugin, String id, String title, int priority, byte[] png, Supplier<? extends JComponent> factory) { fail("Legacy overload"); return null; }
            public SwingPanel register(Plugin plugin, String id, String title, int priority, byte[] png, Supplier<? extends JComponent> factory,
                                       Supplier<? extends List<SwingCommand>> actualMenu) {
                assertSame(owner, plugin); assertSame(menu, actualMenu); assertEquals("content", id); forwarded.incrementAndGet(); return null;
            }
            public SwingPanel action(Plugin plugin, String id, String title, int priority, byte[] png, Runnable actual,
                                     Supplier<? extends List<SwingCommand>> actualMenu) {
                assertSame(owner, plugin); assertSame(action, actual); assertSame(menu, actualMenu);
                assertEquals("action", id); assertEquals(4, priority); assertArrayEquals(icon, png); assertNotSame(icon, png);
                png[0] = 9; forwarded.incrementAndGet(); return null;
            }
        });
        SwingPanels.of(owner).register("content", "Content", JPanel::new, menu);
        SwingPanels.of(owner).action("action", "Action", 4, icon, action, menu);
        assertEquals(2, forwarded.get()); assertEquals(1, icon[0]);
    }

    @Test void popupTitlesAreBoundedUtf8AndActionsAreExplicit() {
        AtomicInteger calls = new AtomicInteger(); SwingCommand command = new SwingCommand("Reset", calls::incrementAndGet);
        assertEquals("Reset", command.title()); assertEquals(0, calls.get()); command.run(); assertEquals(1, calls.get());
        assertDoesNotThrow(() -> new SwingCommand("\u00e9".repeat(128), () -> {}));
        for (String invalid : new String[]{"", "\u00e9".repeat(129), "line\nbreak", "nul\0", "\u007f", "\ud800"})
            assertThrows(IllegalArgumentException.class, () -> new SwingCommand(invalid, () -> {}));
        assertThrows(NullPointerException.class, () -> new SwingCommand("Command", null));
    }
}
