package net.titan.api.crosstab;

import java.util.Objects;

/**
 * One change to a key of a plugin's Cross-Tab Store namespace (SDK 141),
 * posted to that plugin's {@code @Subscribe} methods on the game thread
 * whether or not the plugin is enabled. It never carries the value: read it
 * with {@link CrossTab}. Handlers must be cheap: set a flag, wake a worker.
 *
 * <p>When the runtime loads (or reloads) the plugin, it hears one
 * {@link Origin#REPLAY} event per key it can see. A subscriber registered
 * outside the plugin's own injection hears every plugin's changes;
 * {@link #pluginId()} says whose namespace changed.</p>
 */
public final class CrossTabChanged {
    /** What happened to the key. Values match the native CROSS_TAB_CHANGE_*. */
    public enum Kind {
        /** The key is present at {@link #version()}. */
        SET(0),
        /** The key is absent. */
        ERASED(1),
        /** Write {@link #writeId()} was refused; the key shows the controller's state again. */
        REJECTED(2);

        private final int value;

        Kind(int value) { this.value = value; }

        public int value() { return value; }

        public static Kind fromValue(int value) {
            for (Kind kind : values()) {
                if (kind.value == value) return kind;
            }
            throw new IllegalArgumentException("Unknown Cross-Tab change kind: " + value);
        }
    }

    /** Why this plugin is hearing about it. Values match the native CROSS_TAB_ORIGIN_*. */
    public enum Origin {
        /** Another tab or another plugin instance changed it. */
        REMOTE(0),
        /** This instance was just loaded: one event per key it can see. */
        REPLAY(1),
        /** The result of this instance's own putIf/eraseIf ({@link #writeId()} set). */
        OUTCOME(2);

        private final int value;

        Origin(int value) { this.value = value; }

        public int value() { return value; }

        public static Origin fromValue(int value) {
            for (Origin origin : values()) {
                if (origin.value == value) return origin;
            }
            throw new IllegalArgumentException("Unknown Cross-Tab change origin: " + value);
        }
    }

    /** Why the key changed or a write was refused. Values match the native CROSS_TAB_CAUSE_*. */
    public enum Cause {
        /** An ordinary write. */
        WRITER(0),
        /** The controller session ended (sign-out, sign-in, restart). */
        SESSION_RESET(1),
        /** A namespace or store limit. */
        LIMIT(2),
        /** A conditional write's expected version did not match. */
        CONFLICT(3),
        /** A secret written from a tab this controller did not launch, or signed out. */
        NOT_PERMITTED(4);

        private final int value;

        Cause(int value) { this.value = value; }

        public int value() { return value; }

        public static Cause fromValue(int value) {
            for (Cause cause : values()) {
                if (cause.value == value) return cause;
            }
            throw new IllegalArgumentException("Unknown Cross-Tab change cause: " + value);
        }
    }

    private final String pluginId;
    private final String key;
    private final Kind kind;
    private final Origin origin;
    private final Cause cause;
    private final boolean secret;
    private final boolean redacted;
    private final long version;
    private final long writeId;

    public CrossTabChanged(String pluginId, String key, Kind kind, Origin origin, Cause cause,
                           boolean secret, boolean redacted, long version, long writeId) {
        this.pluginId = Objects.requireNonNull(pluginId, "pluginId");
        this.key = Objects.requireNonNull(key, "key");
        this.kind = Objects.requireNonNull(kind, "kind");
        this.origin = Objects.requireNonNull(origin, "origin");
        this.cause = Objects.requireNonNull(cause, "cause");
        this.secret = secret;
        this.redacted = redacted;
        this.version = version;
        this.writeId = writeId;
    }

    /** The plugin whose namespace changed. */
    public String pluginId() { return pluginId; }
    public String key() { return key; }
    public Kind kind() { return kind; }
    public Origin origin() { return origin; }
    public Cause cause() { return cause; }
    public boolean secret() { return secret; }
    /** A secret whose value this tab may not hold. */
    public boolean redacted() { return redacted; }
    /**
     * SET/ERASED: the version it happened at. OUTCOME: the version the write
     * got. REJECTED: the key's version after the revert; re-read the key.
     * 0 for a pending value, a session reset, and a key a resync left out.
     */
    public long version() { return version; }
    /**
     * OUTCOME and REJECTED: the write's id, as putIf/eraseIf returned it
     * (put and erase do not hand theirs out). Otherwise 0.
     */
    public long writeId() { return writeId; }
}
