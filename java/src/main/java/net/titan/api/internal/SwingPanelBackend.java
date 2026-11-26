package net.titan.api.internal;

import java.util.function.Supplier;
import java.util.List;
import javax.swing.JComponent;
import net.titan.api.plugins.Plugin;
import net.titan.api.swing.SwingPanel;
import net.titan.api.swing.SwingCommand;

/** Internal bridge; plugin authors use SwingPanels. */
public interface SwingPanelBackend {
    SwingPanel register(Plugin owner, String id, String title, int priority, byte[] iconPng,
                        Supplier<? extends JComponent> factory);

    default SwingPanel register(Plugin owner, String id, String title, int priority, byte[] iconPng,
                                Supplier<? extends JComponent> factory, Supplier<? extends List<SwingCommand>> menu) {
        throw new UnsupportedOperationException("This Titan host does not support Swing toolbar commands");
    }

    default SwingPanel action(Plugin owner, String id, String title, int priority, byte[] iconPng,
                              Runnable action, Supplier<? extends List<SwingCommand>> menu) {
        throw new UnsupportedOperationException("This Titan host does not support Swing toolbar commands");
    }

    SwingPanelBackend UNAVAILABLE = (owner, id, title, priority, icon, factory) -> {
        throw new UnsupportedOperationException("This Titan host does not support Java Swing side panels");
    };
}
