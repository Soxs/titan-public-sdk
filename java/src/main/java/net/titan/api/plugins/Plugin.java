package net.titan.api.plugins;

import net.titan.api.overlay.OverlayLayer;
import net.titan.api.panel.Panel;
import net.titan.api.panel.PanelValue;

public interface Plugin {
    default void onLoad() {}
    default void onUnload() {}
    default void onEnable() {}
    default void onDisable() {}
    default void renderOverlay(OverlayLayer layer) {}

    /// Build the content for the side panel identified by {@code panelId}
    /// (declared via {@link SidePanel}). Called every frame the panel is
    /// visible; append commands to {@code panel} using its fluent builder.
    default void buildPanel(String panelId, Panel panel) {}

    /// Handle a control interaction (button click, checkbox toggle, slider
    /// change, text submit, ...) inside the panel identified by
    /// {@code panelId}. {@code actionId} matches the id supplied when the
    /// control was appended in {@link #buildPanel}.
    default void onPanelAction(String panelId, int actionId, PanelValue value) {}
}
