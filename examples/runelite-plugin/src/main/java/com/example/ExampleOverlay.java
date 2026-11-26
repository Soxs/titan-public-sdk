package com.example;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

class ExampleOverlay extends OverlayPanel {
    private final Client client;
    /** Titan's own client, beside RuneLite's: walking, banking, interaction and the rest of Titan's SDK. */
    private final net.titan.api.Client titan;
    private final ExamplePlugin plugin;
    private final ExampleConfig config;

    @Inject
    ExampleOverlay(Client client, net.titan.api.Client titan, ExamplePlugin plugin, ExampleConfig config) {
        this.client = client;
        this.titan = titan;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        panelComponent.setPreferredSize(new Dimension(170, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        line("Ticks here", Integer.toString(plugin.ticksLoggedIn()));
        Player player = client.getLocalPlayer();
        if (config.showLocation() && player != null) {
            WorldPoint at = player.getWorldLocation();
            line("Location", at.getX() + ", " + at.getY() + ", " + at.getPlane());
        }
        if (config.showTitan()) line("Titan tick", Integer.toString(titan.tick()));
        return super.render(graphics);
    }

    private void line(String left, String right) {
        panelComponent.getChildren().add(LineComponent.builder().left(left).right(right).build());
    }
}
