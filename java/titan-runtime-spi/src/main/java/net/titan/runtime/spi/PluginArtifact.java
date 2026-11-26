package net.titan.runtime.spi;

import java.util.Objects;

/** Immutable original JAR bytes. No filesystem path or trust is implied by its id. */
public final class PluginArtifact {
    public static final int MAX_BYTES = 128 * 1024 * 1024;
    private final String id;
    private final byte[] bytes;
    public PluginArtifact(String id, byte[] bytes) {
        this.id = Objects.requireNonNull(id, "id");
        Objects.requireNonNull(bytes, "bytes");
        if (id.isEmpty() || bytes.length == 0 || bytes.length > MAX_BYTES)
            throw new IllegalArgumentException("Invalid plugin artifact");
        this.bytes = bytes.clone();
    }
    public String id() { return id; }
    public byte[] bytes() { return bytes.clone(); }
}
