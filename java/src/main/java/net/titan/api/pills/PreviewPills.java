package net.titan.api.pills;

import net.titan.api.internal.PreviewPillBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * Preview pills (SDK 142): short labels a plugin pins over its tab's
 * thumbnail on the controller's Home grid.
 *
 * <p>Each plugin owns its own pills. The runtime stamps them from the exact
 * plugin instance it loaded, so a plugin sets and clears only its own. Helper
 * classes may hold that instance and call from anywhere:</p>
 *
 * <pre>{@code
 * PreviewPills pills = PreviewPills.of(this);
 * pills.set("break", PreviewPill.of("Breaking:")
 *     .withTone(PanelTone.INFO)
 *     .withCountdown(Duration.ofMinutes(12)));   // "Breaking: 00:12:00", ticking down
 * pills.set("task", PreviewPill.of("Mining iron"));
 * pills.clear("break");
 * }</pre>
 *
 * <p>The controller ticks a countdown itself, so set it once: it stops at
 * 00:00:00 and stays there until the plugin clears or replaces the pill.
 * Keys are 1-{@value #MAX_KEY_LENGTH} characters of {@code [A-Za-z0-9._:/-]}.
 * A plugin shows at most {@value #MAX_PER_PLUGIN} pills and a tab at most
 * {@value #MAX_PER_TAB}; a new pill past either limit is refused, never
 * swapped for another. Text becomes one line, cut to
 * {@value #MAX_TEXT_BYTES} bytes of UTF-8. Pills keep the order they were
 * first set in; replacing one keeps its place.</p>
 *
 * <p>Only an enabled plugin may set a pill, and disabling, unloading or
 * reloading the plugin drops every pill it set. Every call is safe from any
 * thread. Calls from the plugin's constructor fail: the runtime binds the
 * plugin only once it holds the instance.</p>
 */
public final class PreviewPills {
    /** Longest key, in characters. */
    public static final int MAX_KEY_LENGTH = 32;
    /** Text past this many UTF-8 bytes is cut at a character boundary. */
    public static final int MAX_TEXT_BYTES = 63;
    /** Most pills one plugin shows at once. */
    public static final int MAX_PER_PLUGIN = 2;
    /** Most pills one tab shows at once, across every plugin. */
    public static final int MAX_PER_TAB = 8;

    private final Plugin plugin;

    private PreviewPills(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * The pills of {@code plugin}, the exact instance the runtime loaded.
     * Holds only that reference; make one per use or keep it.
     */
    public static PreviewPills of(Plugin plugin) {
        return new PreviewPills(Objects.requireNonNull(plugin, "plugin"));
    }

    /**
     * Show pill {@code key}, replacing the one already under that key.
     *
     * @return false when refused: the plugin is disabled or not yet bound, the
     *     key is invalid, the text is empty with no countdown, or a new pill
     *     would pass the per-plugin or per-tab limit
     */
    public boolean set(String key, PreviewPill pill) {
        Objects.requireNonNull(pill, "pill");
        if (!isValidKey(key)) return false;
        int flags = 0;
        long countdownMs = 0L;
        Optional<Duration> countdown = pill.countdown();
        if (countdown.isPresent()) {
            flags |= PreviewPillBackend.FLAG_COUNTDOWN;
            countdownMs = toMillis(countdown.get());
        }
        return backend().previewPillWrite(plugin, key, pill.text(),
            pill.tone().protocolId(), flags, countdownMs);
    }

    /**
     * Remove pill {@code key}.
     *
     * @return true once it is gone, including when it was never shown; false
     *     for an invalid key or an unbound plugin
     */
    public boolean clear(String key) {
        if (!isValidKey(key)) return false;
        return backend().previewPillWrite(plugin, key, "", 0,
            PreviewPillBackend.FLAG_CLEAR, 0L);
    }

    /** Remove every pill this plugin shows. */
    public boolean clearAll() {
        return backend().previewPillWrite(plugin, "", "", 0,
            PreviewPillBackend.FLAG_CLEAR, 0L);
    }

    private static long toMillis(Duration remaining) {
        if (remaining.isNegative()) return 0L;
        try {
            return remaining.toMillis();
        } catch (ArithmeticException tooLong) {
            // The host shortens anything past 999:59:59 anyway.
            return Long.MAX_VALUE;
        }
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

    private static PreviewPillBackend backend() {
        return TitanRuntime.getPreviewPillBackend();
    }
}
