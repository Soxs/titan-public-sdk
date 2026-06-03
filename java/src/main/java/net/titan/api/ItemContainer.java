package net.titan.api;

import java.util.Collections;
import java.util.List;

public final class ItemContainer {
    private int containerId = -1;
    private int capacity;
    private List<InventoryItem> items;

    public int containerId() { return containerId; }
    public int capacity() { return capacity; }
    public List<InventoryItem> items() {
        return items == null ? Collections.emptyList() : Collections.unmodifiableList(items);
    }
}
