package net.titan.api.crosstab;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** A non-secret Cross-Tab Store value, as {@link CrossTab#get} returns it (SDK 141). */
public final class CrossTabValue {
    private final byte[] bytes;
    private final long version;
    private final boolean pending;

    public CrossTabValue(byte[] bytes, long version, boolean pending) {
        this.bytes = Objects.requireNonNull(bytes, "bytes").clone();
        this.version = version;
        this.pending = pending;
    }

    /** The raw bytes, as a copy. */
    public byte[] bytes() { return bytes.clone(); }
    /** The bytes decoded as UTF-8, for values stored with {@link CrossTab#putString}. */
    public String text() { return new String(bytes, StandardCharsets.UTF_8); }
    /** Controller-assigned and rising; 0 while pending. */
    public long version() { return version; }
    /** This tab's own put, not yet confirmed by the controller. */
    public boolean pending() { return pending; }
}
