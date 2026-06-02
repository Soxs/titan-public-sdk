package net.titan.api.plugins;

public interface Plugin {
    default void onLoad() {}
    default void onUnload() {}
    default void onEnable() {}
    default void onDisable() {}
}
