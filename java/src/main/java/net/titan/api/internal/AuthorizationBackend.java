package net.titan.api.internal;

import net.titan.api.plugins.Plugin;

/** Raw, owner-bound transport. Authority is verified by the plugin-bundled SDK. */
public interface AuthorizationBackend {
    interface Listener {
        void pulse();
        void tick(int tick);
        void stopped(String reason);
    }
    boolean register(Plugin owner, Listener listener);
    void unregister(Plugin owner, Listener listener);
    String context(Plugin owner);
    long submit(Plugin owner, String operation, String requestJson);
    String poll(Plugin owner, long requestId);
    void cancel(Plugin owner, long requestId);
    void authorizationStop(Plugin owner);
    /** Relative monotonic deadline for the host's independent admission fence. */
    default void deadline(Plugin owner,long remainingMillis) {}
}
