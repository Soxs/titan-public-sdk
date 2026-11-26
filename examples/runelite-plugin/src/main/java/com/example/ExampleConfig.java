package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface ExampleConfig extends Config {
    @ConfigItem(keyName = "showLocation", name = "Show location", description = "Show the tile you are on", position = 1)
    default boolean showLocation() {
        return true;
    }

    @ConfigItem(keyName = "showTitan", name = "Show Titan's tick", description = "Show the tick Titan's own SDK counts", position = 2)
    default boolean showTitan() {
        return true;
    }
}
