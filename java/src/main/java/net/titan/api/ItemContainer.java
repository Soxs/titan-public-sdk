package net.titan.api;

import java.util.Collections;
import java.util.List;

public final class ItemContainer implements ClientBacked {
    private transient Client client;
    private int containerId = -1;
    private int capacity;
    private List<InventoryItem> items;

    @Override
    public void bindClient(Client client) {
        this.client = client;
        bindChildren(client);
    }

    @Override
    public Client client() { return client; }

    public int containerId() { return containerId; }
    public int capacity() { return capacity; }
    public List<InventoryItem> items() {
        return items == null ? Collections.emptyList() : Collections.unmodifiableList(items);
    }

    void bindChildren(Client client) {
        if (items == null) return;
        for (InventoryItem item : items) {
            if (item != null) item.bindClient(client);
        }
    }
}
