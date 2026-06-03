package net.titan.api.events;

import net.titan.api.GraphicsObject;

public final class GraphicsObjectMoved {
    private final GraphicsObject graphicsObject;

    public GraphicsObjectMoved(GraphicsObject graphicsObject) {
        this.graphicsObject = graphicsObject;
    }

    public GraphicsObject graphicsObject() { return graphicsObject; }
}
