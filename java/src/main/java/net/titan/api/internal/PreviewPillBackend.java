package net.titan.api.internal;

import net.titan.api.plugins.Plugin;

/**
 * Runtime bridge behind the public {@code PreviewPills} facade (SDK 142). The
 * runtime resolves the exact plugin instance to the pills it owns; a plugin id
 * is never taken from the caller. The method name carries the "previewPill"
 * prefix because the one runtime bridge class implements every backend.
 */
public interface PreviewPillBackend {
    /** Write flags; the values are the native PREVIEW_PILL_* flags. */
    int FLAG_CLEAR = 1;
    int FLAG_COUNTDOWN = 2;

    /**
     * Set or clear one pill. A clear with an empty key clears every pill the
     * plugin shows. {@code tone} is a {@code PanelTone} protocol id and
     * {@code countdownMs} counts only with {@link #FLAG_COUNTDOWN}.
     *
     * @return false when refused
     */
    boolean previewPillWrite(Plugin plugin, String key, String text, int tone,
                             int flags, long countdownMs);
}
