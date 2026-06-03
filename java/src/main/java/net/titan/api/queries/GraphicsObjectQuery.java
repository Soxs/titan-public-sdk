package net.titan.api.queries;

import net.titan.api.Client;
import net.titan.api.GraphicsObject;

public final class GraphicsObjectQuery extends LocatableQuery<GraphicsObject, GraphicsObjectQuery> {
    public GraphicsObjectQuery(Client client) {
        super(client, client == null ? null : client.graphicsObjects());
    }

    public GraphicsObjectQuery spotAnim(int animationId) {
        return where(object -> object.spotAnimId() == animationId);
    }

    public GraphicsObjectQuery onPlane(int plane) {
        return where(object -> object.plane() == plane);
    }

    public GraphicsObjectQuery startedAfterTick(int tick) {
        return where(object -> object.startCycle() >= tick);
    }

    public GraphicsObjectQuery startedBeforeTick(int tick) {
        return where(object -> object.startCycle() <= tick);
    }
}
