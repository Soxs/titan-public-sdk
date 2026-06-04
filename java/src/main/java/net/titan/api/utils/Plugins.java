package net.titan.api.utils;

import net.titan.api.PluginInfo;
import net.titan.api.Titan;

import java.util.List;
import java.util.Optional;

/// Plugin-manager helpers. Mirrors the C++ {@code titan::plugins()} facade.
/// All methods delegate to the live {@link net.titan.api.Client}.
public final class Plugins {
    private Plugins() {}

    public static List<PluginInfo> all() {
        return Titan.client().plugins();
    }

    public static Optional<PluginInfo> get(String pluginId) {
        return Titan.client().plugin(pluginId);
    }

    public static boolean setEnabled(String pluginId, boolean enabled) {
        return Titan.client().setPluginEnabled(pluginId, enabled);
    }

    public static boolean enable(String pluginId) {
        return setEnabled(pluginId, true);
    }

    public static boolean disable(String pluginId) {
        return setEnabled(pluginId, false);
    }

    /// Returns 1 = enabled, 0 = disabled, -1 = unknown id.
    public static int enabledState(String pluginId) {
        return Titan.client().pluginEnabledState(pluginId);
    }

    public static boolean isEnabled(String pluginId) {
        return enabledState(pluginId) == 1;
    }
}
