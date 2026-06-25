package net.titan.api;

import net.titan.api.internal.TitanRuntime;

/// Identity + lifecycle snapshot for an installed plugin. Mirrors the C++
/// {@code titan::PluginInfo} returned by the plugin-manager queries.
public final class PluginInfo {
    private boolean liveHandle = true;
    private String id;
    private String name;
    private boolean enabled;
    private boolean hasPanel;
    private String description;
    private String author;
    private String version;

    private PluginInfo live() { return TitanRuntime.currentLive(this); }

    public String id() {
        String value = live().id;
        return value == null ? "" : value;
    }
    public String name() {
        String value = live().name;
        return value == null ? "" : value;
    }
    public boolean enabled() { return live().enabled; }
    public boolean hasPanel() { return live().hasPanel; }
    public String description() {
        String value = live().description;
        return value == null ? "" : value;
    }
    public String author() {
        String value = live().author;
        return value == null ? "" : value;
    }
    public String version() {
        String value = live().version;
        return value == null ? "" : value;
    }
    public boolean exists() { return TitanRuntime.liveExists(this); }
    public PluginInfo snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) { return TitanRuntime.liveEquals(this, other); }

    @Override
    public int hashCode() { return TitanRuntime.liveHashCode(this); }
}
