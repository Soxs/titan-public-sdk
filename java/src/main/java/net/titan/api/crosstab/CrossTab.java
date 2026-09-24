package net.titan.api.crosstab;

import net.titan.api.internal.CrossTabBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * Cross-Tab Store (SDK 141): small values every tab launched by the same
 * controller sees, for the life of the controller session.
 *
 * <p>Each plugin owns one namespace. The runtime stamps it from the exact
 * plugin instance it loaded, so a plugin reads and writes only its own keys.
 * Helper classes may hold that instance and call from anywhere:</p>
 *
 * <pre>{@code
 * CrossTab ct = CrossTab.of(this);
 * ct.putString("route.last", "lumbridge");          // every tab sees it
 * ct.get("route.last").ifPresent(v -> use(v.text()));
 * ct.erase("route.last");                            // every tab loses it
 * }</pre>
 *
 * <p>Values are raw bytes, at most {@value #MAX_VALUE_BYTES} bytes, under keys
 * of 1-{@value #MAX_KEY_LENGTH} characters of {@code [A-Za-z0-9._:/-]}. A
 * namespace holds at most 64 keys, 128 KiB and 16 secrets. Values live in the
 * controller's memory for one controller sign-in session and outlive plugin
 * reloads and tab restarts; they are never written to disk. Changes arrive as
 * {@link CrossTabChanged} events, whether or not the plugin is enabled.</p>
 *
 * <p>Every call is safe from any thread. Calls from the plugin's constructor
 * fail: the runtime binds the namespace only once it holds the instance.</p>
 */
public final class CrossTab {
    /** Longest key, in characters. */
    public static final int MAX_KEY_LENGTH = 63;
    /** Largest value, secret or not. Zero-length values are values. */
    public static final int MAX_VALUE_BYTES = 16 * 1024;

    private static final byte[] NO_VALUE = new byte[0];

    private final Plugin plugin;

    private CrossTab(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * The namespace of {@code plugin}, the exact instance the runtime loaded.
     * Holds only that reference; make one per use or keep it.
     */
    public static CrossTab of(Plugin plugin) {
        return new CrossTab(Objects.requireNonNull(plugin, "plugin"));
    }

    /**
     * Set {@code key}. Shown in this tab at once (pending, version 0) and
     * sent to every tab; the last write the controller receives wins. No
     * change event reaches this instance unless the controller rejects it.
     *
     * @return false when refused locally: a bad key, a value over 16 KiB, a
     *     namespace limit, a secret in a tab this controller did not launch,
     *     or a full outbox of unacknowledged writes
     */
    public boolean put(String key, byte[] value, CrossTabOption... options) {
        return write(key, value, flags(options), 0L) != 0L;
    }

    /**
     * {@link #put} of {@code value} encoded as UTF-8. The temporary bytes are
     * zeroed afterwards, but a String cannot be: keep secrets in a byte array
     * you zero yourself and use {@link #put}.
     */
    public boolean putString(String key, String value, CrossTabOption... options) {
        byte[] bytes = Objects.requireNonNull(value, "value").getBytes(StandardCharsets.UTF_8);
        try {
            return put(key, bytes, options);
        } finally {
            Arrays.fill(bytes, (byte) 0);
        }
    }

    /**
     * Erase {@code key}, even if it is absent: the erase reaches every tab,
     * including tabs that never held the key.
     */
    public boolean erase(String key) {
        return write(key, NO_VALUE, CrossTabBackend.FLAG_ERASE, 0L) != 0L;
    }

    /**
     * Propose setting {@code key} only if its version is still
     * {@code expectedVersion} (0 = only if absent). Nothing changes locally;
     * the controller decides, and a {@link CrossTabChanged} carrying the
     * returned write id reports an {@link CrossTabChanged.Origin#OUTCOME} of
     * kind SET, or kind REJECTED (conflict or another cause).
     *
     * @return the write id, or empty when refused locally as {@link #put} is
     */
    public OptionalLong putIf(String key, byte[] value, long expectedVersion,
                              CrossTabOption... options) {
        return writeId(write(key, value,
            flags(options) | CrossTabBackend.FLAG_CONDITIONAL, expectedVersion));
    }

    /**
     * Propose erasing {@code key} only if its version is still
     * {@code expectedVersion}; as {@link #putIf}, with an outcome of kind
     * ERASED.
     */
    public OptionalLong eraseIf(String key, long expectedVersion) {
        return writeId(write(key, NO_VALUE,
            CrossTabBackend.FLAG_ERASE | CrossTabBackend.FLAG_CONDITIONAL, expectedVersion));
    }

    /** The value of a non-secret key; empty when it is absent or secret. */
    public Optional<CrossTabValue> get(String key) {
        return isValidKey(key) ? backend().crossTabGet(plugin, key) : Optional.empty();
    }

    /**
     * A key's value, secret or not, in a fresh array: zero it
     * ({@code Arrays.fill(bytes, (byte) 0)}) once done. The runtime zeroes
     * its own native buffers, but copies the JVM makes cannot be scrubbed.
     *
     * @return null when the key is absent, or its value is withheld in this
     *     tab ({@link CrossTabInfo#redacted()})
     */
    public byte[] getSecret(String key) {
        return isValidKey(key) ? backend().crossTabGetSecret(plugin, key) : null;
    }

    /** A key's state without its value; empty when absent. */
    public Optional<CrossTabInfo> info(String key) {
        return isValidKey(key) ? backend().crossTabInfo(plugin, key) : Optional.empty();
    }

    private long write(String key, byte[] value, int flags, long expectedVersion) {
        Objects.requireNonNull(value, "value");
        if (!isValidKey(key) || value.length > MAX_VALUE_BYTES || expectedVersion < 0L) {
            return 0L;
        }
        return backend().crossTabWrite(plugin, key, value, flags, expectedVersion);
    }

    private static int flags(CrossTabOption[] options) {
        int flags = 0;
        if (options == null) return flags;
        for (CrossTabOption option : options) {
            if (option == CrossTabOption.SECRET) flags |= CrossTabBackend.FLAG_SECRET;
        }
        return flags;
    }

    private static OptionalLong writeId(long id) {
        return id == 0L ? OptionalLong.empty() : OptionalLong.of(id);
    }

    private static boolean isValidKey(String key) {
        Objects.requireNonNull(key, "key");
        if (key.isEmpty() || key.length() > MAX_KEY_LENGTH) return false;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            boolean allowed = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == ':'
                || c == '/' || c == '-';
            if (!allowed) return false;
        }
        return true;
    }

    private static CrossTabBackend backend() {
        return TitanRuntime.getCrossTabBackend();
    }
}
