package net.titan.runtime.spi;

/** An optional game API supplied by an isolated provider to ordinary Titan plugins.
 * Only the host-selected API package and its client binding cross the realm boundary. */
public interface GameApi {
    Class<?> clientType();
    Object client();
    Session open(PluginContext context, ClassLoader pluginLoader);
    Scope enterUnattributed();
    /** Establish attribution only if this callback actually accesses the API, including through a helper. */
    Scope defer(Callback firstUse);
    /** Host-scheduled callbacks inherit only the ordinary plugin currently being dispatched. */
    Callback currentCallback();

    /** Owned by one ordinary plugin load. A new activation invalidates earlier callbacks. */
    interface Session extends AutoCloseable {
        Callback activate(boolean enabling);
        /** Native code has admitted externally dispatched callbacks for this activation. */
        default void ready() {}
        void deactivate();
        @Override void close();
    }
    interface Callback {
        Scope enter();
    }
    interface Scope extends AutoCloseable {
        @Override void close();
    }
}
