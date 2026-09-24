package net.titan.api.crosstab;

/** A Cross-Tab Store key's state as this tab sees it (SDK 141). */
public final class CrossTabInfo {
    private final long version;
    private final int size;
    private final boolean secret;
    private final boolean pending;
    private final boolean redacted;

    public CrossTabInfo(long version, int size, boolean secret, boolean pending,
                        boolean redacted) {
        this.version = version;
        this.size = size;
        this.secret = secret;
        this.pending = pending;
        this.redacted = redacted;
    }

    /** Controller-assigned and rising; 0 while pending. */
    public long version() { return version; }
    /** Value bytes; 0 when redacted. */
    public int size() { return size; }
    public boolean secret() { return secret; }
    /** This tab's own put, not yet confirmed by the controller. */
    public boolean pending() { return pending; }
    /** A secret whose value this tab may not hold. */
    public boolean redacted() { return redacted; }
}
