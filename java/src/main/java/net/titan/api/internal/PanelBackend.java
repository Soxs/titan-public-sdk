package net.titan.api.internal;

import net.titan.api.plugins.Plugin;

/** Runtime implementation detail for owner-bound side-panel requests. */
public interface PanelBackend {
    boolean requestOpen(Plugin owner, String panelId);

    PanelBackend UNAVAILABLE = (owner, panelId) -> false;
}
