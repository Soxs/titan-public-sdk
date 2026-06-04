package net.titan.api.plugins;

import net.titan.api.overlay.OverlayLayer;

public interface Plugin {
    default void onLoad() {}
    default void onUnload() {}
    default void onEnable() {}
    default void onDisable() {}
    default void renderOverlay(OverlayLayer layer) {}
}
