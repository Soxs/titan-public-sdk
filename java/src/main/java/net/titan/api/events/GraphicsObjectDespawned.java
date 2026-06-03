package net.titan.api.events;

import net.titan.api.GraphicsObject;

public final class GraphicsObjectDespawned {
    private final GraphicsObject graphicsObject;

    public GraphicsObjectDespawned(GraphicsObject graphicsObject) {
        this.graphicsObject = graphicsObject;
    }

    public GraphicsObject graphicsObject() { return graphicsObject; }
}
