package net.titan.runtime.spi;

import java.util.Objects;

/** Host-owned, generation-scoped services. A retired context cannot regain privileges. */
public final class PluginContext {
    private final String pluginId, initialStateJson;
    private final long loadGeneration;
    private final HostServices host;
    public PluginContext(String pluginId, long loadGeneration, String initialStateJson, HostServices host) {
        if (loadGeneration <= 0) throw new IllegalArgumentException("Invalid load generation");
        this.pluginId = Objects.requireNonNull(pluginId);
        this.loadGeneration = loadGeneration;
        this.initialStateJson = initialStateJson == null ? "{}" : initialStateJson;
        this.host = Objects.requireNonNull(host);
    }
    public String pluginId() { return pluginId; }
    public long loadGeneration() { return loadGeneration; }
    public String initialStateJson() { return initialStateJson; }
    public HostServices host() { return host; }
}
