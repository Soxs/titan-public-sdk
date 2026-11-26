package net.titan.api.swing;

import java.util.Objects;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JComponent;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;

/** Native sidebar hosting for ordinary Titan Java plugins' Swing components.
 * Register from each onEnable; the factory runs on the EDT when selected.
 * Never wait for ready/closeAsync on the EDT or from a plugin lifecycle hook.
 * Swing callbacks are not game-thread callbacks: schedule game work explicitly. */
public final class SwingPanels {
    private final Plugin owner;
    private SwingPanels(Plugin owner) { this.owner = owner; }

    public static SwingPanels of(Plugin owner) {
        return new SwingPanels(Objects.requireNonNull(owner, "owner"));
    }

    public SwingPanel register(String id, String title, Supplier<? extends JComponent> factory) {
        return register(id, title, 0, null, factory);
    }

    /** The optional icon is PNG (at most 128 KiB, 512 pixels per axis).
     * Each factory must return an unparented, exclusively owned component tree.
     * Disabled/retired/foreign instances and duplicate panel IDs are rejected.
     * Older hosts throw UnsupportedOperationException; no external window is substituted. */
    public SwingPanel register(String id, String title, int priority, byte[] iconPng,
                               Supplier<? extends JComponent> factory) {
        return TitanRuntime.getSwingPanelBackend().register(owner, id, title, priority,
            iconPng == null ? null : iconPng.clone(), Objects.requireNonNull(factory, "factory"));
    }

    public SwingPanel register(String id, String title, Supplier<? extends JComponent> factory,
                               Supplier<? extends List<SwingCommand>> menu) {
        return register(id, title, 0, null, factory, menu);
    }

    /** Menu suppliers run on the owned EDT for each right-click. Return at most
     * 32 commands in display order; searching/opening a menu invokes no command.
     * The selected callback comes from that snapshot, not a later supplier call. */
    public SwingPanel register(String id, String title, int priority, byte[] iconPng,
                               Supplier<? extends JComponent> factory, Supplier<? extends List<SwingCommand>> menu) {
        return TitanRuntime.getSwingPanelBackend().register(owner, id, title, priority,
            iconPng == null ? null : iconPng.clone(), Objects.requireNonNull(factory, "factory"),
            Objects.requireNonNull(menu, "menu"));
    }

    public SwingPanel action(String id, String title, Runnable action) {
        return action(id, title, 0, null, action, null);
    }

    public SwingPanel action(String id, String title, Runnable action, Supplier<? extends List<SwingCommand>> menu) {
        return action(id, title, 0, null, action, menu);
    }

    public SwingPanel action(String id, String title, int priority, byte[] iconPng, Runnable action) {
        return action(id, title, priority, iconPng, action, null);
    }

    /** Register a toolbar button without a side panel. Its primary click runs on
     * the owned EDT; requestOpen() returns false. Optional popup commands follow
     * the same snapshot rules as content panels. Requires a command-capable host. */
    public SwingPanel action(String id, String title, int priority, byte[] iconPng,
                             Runnable action, Supplier<? extends List<SwingCommand>> menu) {
        return TitanRuntime.getSwingPanelBackend().action(owner, id, title, priority,
            iconPng == null ? null : iconPng.clone(), Objects.requireNonNull(action, "action"), menu);
    }
}
