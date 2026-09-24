package net.titan.api.internal;

import net.titan.api.crosstab.CrossTabInfo;
import net.titan.api.crosstab.CrossTabValue;
import net.titan.api.plugins.Plugin;

import java.util.Optional;

/**
 * Runtime bridge behind the public {@code CrossTab} facade (SDK 141). The
 * runtime resolves the exact plugin instance to its namespace; a plugin id is
 * never taken from the caller. Method names carry the "crossTab" prefix
 * because the one runtime bridge class implements every backend.
 */
public interface CrossTabBackend {
    /** Write flags; the values are the native CROSS_TAB_* flags. */
    int FLAG_SECRET = 1;
    int FLAG_ERASE = 2;
    int FLAG_CONDITIONAL = 4;

    /** Queue one write; returns its write id, or 0 when refused. */
    long crossTabWrite(Plugin plugin, String key, byte[] value, int flags, long expectedVersion);
    /** A non-secret key's value; empty when absent or secret, which is never decrypted for it. */
    Optional<CrossTabValue> crossTabGet(Plugin plugin, String key);
    /** Any value this tab holds, or null when the key is absent or redacted here. */
    byte[] crossTabGetSecret(Plugin plugin, String key);
    /** A key's state without its value; empty when absent. */
    Optional<CrossTabInfo> crossTabInfo(Plugin plugin, String key);
}
