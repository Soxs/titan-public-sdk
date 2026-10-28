package net.titan.api.panel;

import java.util.Objects;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;

/**
 * Owner-bound requests for declared native and HTML side panels.
 * Store {@code Panels.of(this)} on the plugin or pass it to helpers.
 * Calls are safe from any thread and never use a thread-local plugin ID.
 */
public final class Panels {
    private final Plugin owner;

    private Panels(Plugin owner) { this.owner = owner; }

    public static Panels of(Plugin owner) {
        return new Panels(Objects.requireNonNull(owner, "owner"));
    }

    /**
     * Request that this plugin's panel be selected and the sidebar expanded.
     * Returns false for unknown IDs, disabled or retired owners, and older
     * runtimes. Acceptance does not guarantee the controller has shown it.
     * The request belongs to the owning game client and does not switch tabs.
     */
    public boolean requestOpen(String panelId) {
        return TitanRuntime.getPanelBackend().requestOpen(owner, panelId);
    }
}
