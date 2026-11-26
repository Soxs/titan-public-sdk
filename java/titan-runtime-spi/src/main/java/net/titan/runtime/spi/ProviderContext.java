package net.titan.runtime.spi;

import java.nio.file.Path;
import java.util.Objects;

/** Distribution-owned services that outlive individual plugin generations.
 * The host permits passive reads, input observation and qualified render-pass
 * raster composition. Actions, configuration,
 * panels and other plugin resources require the relevant {@link PluginContext}.
 * Inspection contexts expose no live capabilities. */
public final class ProviderContext {
    private final String providerId;
    private final long runtimeGeneration;
    private final Path storageDirectory;
    private final HostServices host;
    private final Runnable installedChanged;

    public ProviderContext(String providerId, long runtimeGeneration, Path storageDirectory, HostServices host) {
        this(providerId, runtimeGeneration, storageDirectory, host, () -> {});
    }

    public ProviderContext(String providerId, long runtimeGeneration, Path storageDirectory, HostServices host,
                           Runnable installedChanged) {
        if (providerId == null || !providerId.matches("[a-z][a-z0-9_-]{0,63}"))
            throw new IllegalArgumentException("Invalid provider ID");
        if (runtimeGeneration <= 0) throw new IllegalArgumentException("Invalid provider runtime generation");
        Objects.requireNonNull(storageDirectory, "storageDirectory");
        if (!storageDirectory.isAbsolute()) throw new IllegalArgumentException("Provider storage must be absolute");
        this.providerId = providerId;
        this.runtimeGeneration = runtimeGeneration;
        this.storageDirectory = storageDirectory.normalize();
        this.host = Objects.requireNonNull(host, "host");
        this.installedChanged = Objects.requireNonNull(installedChanged, "installedChanged");
    }
    public String providerId() { return providerId; }
    public long runtimeGeneration() { return runtimeGeneration; }
    public Path storageDirectory() { return storageDirectory; }
    public HostServices host() { return host; }
    /** Signal that fresh verified packages are available from installedPlugins().
     * Existing package versions stay pinned for the lifetime of the tab. */
    public void installedPluginsChanged() { installedChanged.run(); }
}
