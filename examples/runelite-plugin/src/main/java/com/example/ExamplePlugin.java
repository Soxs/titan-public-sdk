package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
    name = "Example",
    description = "Shows where you are and how long you have been there",
    tags = {"example"}
)
public class ExamplePlugin extends Plugin {
    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private ExampleOverlay overlay;

    private int ticksLoggedIn;

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
        log.info("Example started");
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        ticksLoggedIn = 0;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN) ticksLoggedIn = 0;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getGameState() == GameState.LOGGED_IN) ticksLoggedIn++;
    }

    int ticksLoggedIn() {
        return ticksLoggedIn;
    }

    @Provides
    ExampleConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ExampleConfig.class);
    }
}
