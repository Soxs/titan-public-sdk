package net.titan.api.events;

import net.titan.api.GraphicsObject;

public final class GraphicsObjectSpawned {
    private final GraphicsObject graphicsObject;

    public GraphicsObjectSpawned(GraphicsObject graphicsObject) {
        this.graphicsObject = graphicsObject;
    }

    public GraphicsObject graphicsObject() { return graphicsObject; }
}
