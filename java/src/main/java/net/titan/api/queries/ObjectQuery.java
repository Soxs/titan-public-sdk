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

    /**
     * Keeps tile objects exposing at least one of the supplied actions.
     */
    public ObjectQuery hasAction(String... actions) {
        return where(object -> {
            if (actions == null) return false;
            for (String action : actions) {
                if (object.hasAction(action)) return true;
            }
            return false;
        });
    }

    public ObjectQuery ofType(String typeName) {
        return where(object -> equalsIgnoreCase(object.typeName(), typeName));
    }

    public ObjectQuery layer(int layerId) {
        return where(object -> object.layer() == layerId);
    }
}
