package net.titan.runtime.spi;

import java.util.Objects;

/** A locally installed artifact admitted by a provider-managed source. Catalog
 * metadata is a provenance claim, not a host entitlement or permission grant.
 * The provider must retain and revalidate the exact artifact before creation. */
public final class InstalledPlugin {
    private final PluginDescriptor descriptor;
    private final String sourceId, packageId, compatibilityBaseline, artifactSha256;
    private final boolean wantsLifecycleEvents, wantsItemContainerEvents;

    public InstalledPlugin(PluginDescriptor descriptor, String sourceId, String packageId,
                           String compatibilityBaseline, String artifactSha256) {
        this(descriptor, sourceId, packageId, compatibilityBaseline, artifactSha256, true, true);
    }

    public InstalledPlugin(PluginDescriptor descriptor, String sourceId, String packageId,
                           String compatibilityBaseline, String artifactSha256,
                           boolean wantsLifecycleEvents, boolean wantsItemContainerEvents) {
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
        this.sourceId = Objects.requireNonNull(sourceId, "sourceId");
        if (!sourceId.matches("[a-z][a-z0-9_-]{0,63}"))
            throw new IllegalArgumentException("Invalid installed plugin sourceId");
        this.packageId = required(packageId, "packageId", 127);
        this.compatibilityBaseline = required(compatibilityBaseline, "compatibilityBaseline", 63);
        this.artifactSha256 = Objects.requireNonNull(artifactSha256, "artifactSha256");
        if (!artifactSha256.matches("[0-9a-f]{64}"))
            throw new IllegalArgumentException("Installed artifactSha256 must be 64 lowercase hexadecimal characters");
        this.wantsLifecycleEvents = wantsLifecycleEvents;
        this.wantsItemContainerEvents = wantsItemContainerEvents;
    }

    private static String required(String value, String field, int maximumBytes) {
        Objects.requireNonNull(value, field);
        if (value.isBlank() || value.indexOf('\0') >= 0
            || value.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > maximumBytes)
            throw new IllegalArgumentException("Invalid installed plugin " + field);
        return value;
    }

    public PluginDescriptor descriptor() { return descriptor; }
    public String sourceId() { return sourceId; }
    public String packageId() { return packageId; }
    public String compatibilityBaseline() { return compatibilityBaseline; }
    public String artifactSha256() { return artifactSha256; }
    public boolean wantsLifecycleEvents() { return wantsLifecycleEvents; }
    public boolean wantsItemContainerEvents() { return wantsItemContainerEvents; }
}
