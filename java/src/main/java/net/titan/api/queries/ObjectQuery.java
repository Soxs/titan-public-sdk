package net.titan.api.queries;

import net.titan.api.Client;
import net.titan.api.TileObject;

public final class ObjectQuery extends NamedLocatableQuery<TileObject, ObjectQuery> {
    public ObjectQuery(Client client, int radius) {
        super(client, client == null ? null : client.tileObjects(radius), TileObject::name);
    }

    public ObjectQuery id(int locId) {
        return where(object -> object.id() == locId);
    }

    public ObjectQuery ids(int... ids) {
        return where(object -> NPCQuery.contains(ids, object.id()));
    }

    public ObjectQuery hasAction(String action) {
        return where(object -> object.hasAction(action));
    }

    public ObjectQuery ofType(String typeName) {
        return where(object -> equalsIgnoreCase(object.typeName(), typeName));
    }

    public ObjectQuery layer(int layerId) {
        return where(object -> object.layer() == layerId);
    }
}
