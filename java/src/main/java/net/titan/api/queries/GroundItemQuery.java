package net.titan.api.queries;

import net.titan.api.Client;
import net.titan.api.GroundItem;

public final class GroundItemQuery extends NamedLocatableQuery<GroundItem, GroundItemQuery> {
    public GroundItemQuery(Client client, int radius) {
        super(client, client == null ? null : client.groundItems(radius), GroundItem::name);
    }

    public GroundItemQuery id(int itemId) {
        return where(item -> item.id() == itemId);
    }

    public GroundItemQuery ids(int... ids) {
        return where(item -> NPCQuery.contains(ids, item.id()));
    }

    public GroundItemQuery minQuantity(int quantity) {
        return where(item -> item.quantity() >= quantity);
    }

    public GroundItemQuery maxQuantity(int quantity) {
        return where(item -> item.quantity() <= quantity);
    }

    public GroundItemQuery canLoot() {
        return where(GroundItem::canLoot);
    }
}
