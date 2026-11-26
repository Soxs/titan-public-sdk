package net.titan.runtime.spi;

import java.util.Objects;

/** A plugin supplied by a provider's own versioned distribution, not a downloaded
 * plugin JAR. The hash identifies the actual artifact supplying the plugin classes,
 * including adaptations; it must not be the hash of a different upstream artifact. */
public final class BundledPlugin {
    private final PluginDescriptor descriptor;
    private final String packageId, compatibilityBaseline, artifactSha256;
    private final boolean wantsLifecycleEvents, wantsItemContainerEvents;

    public BundledPlugin(PluginDescriptor descriptor, String packageId,
                         String compatibilityBaseline, String artifactSha256) {
        this(descriptor, packageId, compatibilityBaseline, artifactSha256, true, true);
    }

    /** Event-interest hints allow a UI-only entry to avoid enabling native entity
     * and item-container scans. Older four-argument callers retain full delivery. */
    public BundledPlugin(PluginDescriptor descriptor, String packageId,
                         String compatibilityBaseline, String artifactSha256,
                         boolean wantsLifecycleEvents, boolean wantsItemContainerEvents) {
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
        this.packageId = required(packageId, "packageId", 127);
        this.compatibilityBaseline = required(compatibilityBaseline, "compatibilityBaseline", 63);
        this.artifactSha256 = Objects.requireNonNull(artifactSha256, "artifactSha256");
        if (!artifactSha256.matches("[0-9a-f]{64}"))
            throw new IllegalArgumentException("Bundled artifactSha256 must be 64 lowercase hexadecimal characters");
        this.wantsLifecycleEvents = wantsLifecycleEvents;
        this.wantsItemContainerEvents = wantsItemContainerEvents;
    }

    private static String required(String value, String field, int maximumBytes) {
        Objects.requireNonNull(value, field);
        if (value.isBlank() || value.indexOf('\0') >= 0
            || value.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > maximumBytes)
            throw new IllegalArgumentException("Invalid bundled plugin " + field);
        return value;
    }

    public PluginDescriptor descriptor() { return descriptor; }
    public String packageId() { return packageId; }
    public String compatibilityBaseline() { return compatibilityBaseline; }
    public String artifactSha256() { return artifactSha256; }
    public boolean wantsLifecycleEvents() { return wantsLifecycleEvents; }
    public boolean wantsItemContainerEvents() { return wantsItemContainerEvents; }
}
