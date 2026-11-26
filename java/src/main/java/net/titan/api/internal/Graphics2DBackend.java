package net.titan.api.internal;

import java.awt.Graphics2D;
import java.util.function.Consumer;

/** Host-owned mutable raster transport; independent of the immutable sprite API. */
public interface Graphics2DBackend {
    Session create(String pluginId, String stableId, int width, int height,
                   int layer, int anchor, int priority);

    interface Session extends AutoCloseable {
        int width();
        int height();
        void resize(int width, int height);
        boolean render(int x, int y, Consumer<Graphics2D> painter);
        default boolean renderCached(int x, int y, long contentRevision, Consumer<Graphics2D> painter) {
            return render(x, y, painter);
        }
        @Override void close();
    }
}
