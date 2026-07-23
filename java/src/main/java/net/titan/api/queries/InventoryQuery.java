package net.titan.api.queries;

import net.titan.api.Client;
import net.titan.api.InventoryItem;

public final class InventoryQuery extends NamedQuery<InventoryItem, InventoryQuery> {
    private final Client client;

    public InventoryQuery(Client client) {
        super(client == null ? null : client.inventoryItems(), InventoryItem::name);
        this.client = client;
    }

    public InventoryQuery id(int itemId) {
        return where(item -> item.id() == itemId);
    }

    public InventoryQuery ids(int... ids) {
        return where(item -> NPCQuery.contains(ids, item.id()));
    }

    public InventoryQuery slot(int index) {
        return where(item -> item.slot() == index);
    }

    public InventoryQuery slotsAnyOf(int... slots) {
        return where(item -> NPCQuery.contains(slots, item.slot()));
    }

    public InventoryQuery slotsBetween(int minSlot, int maxSlot) {
        int min = Math.min(minSlot, maxSlot);
        int max = Math.max(minSlot, maxSlot);
        return where(item -> item.slot() >= min && item.slot() <= max);
    }

    public InventoryQuery minQuantity(int quantity) {
        return where(item -> item.quantity() >= quantity);
    }

    public InventoryQuery maxQuantity(int quantity) {
        return where(item -> item.quantity() <= quantity);
    }

    public InventoryQuery hasAction(String action) {
        if (client == null || action == null || action.isEmpty()) {
            return where(item -> false);
        }
        return where(item -> client.itemComposition(item.id())
            .map(definition -> {
                for (String candidate : definition.inventoryActions()) {
                    if (containsIgnoreCase(candidate, action)) return true;
                }
                return false;
            })
            .orElse(false));
    }

    public InventoryQuery isNoted() {
        if (client == null) return where(item -> false);
        return where(item -> client.itemDefinition(item.id())
            .map(definition -> definition.isNoted())
            .orElse(false));
    }

    public InventoryQuery excludeIds(int... ids) {
        return where(item -> !NPCQuery.contains(ids, item.id()));
    }

    public InventoryQuery excludeNames(String... names) {
        return where(item -> {
            if (names == null) return true;
            for (String name : names) {
                if (containsIgnoreCase(item.name(), name)) return false;
            }
            return true;
        });
    }

    public int totalQuantity() {
        int total = 0;
        for (InventoryItem item : items) total += item.quantity();
        return total;
    }

    public boolean exists() {
        return any();
    }
}
