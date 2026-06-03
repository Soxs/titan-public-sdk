package net.titan.api.events;

import net.titan.api.InventoryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemContainerChanged {
    private final int containerId;
    private final int capacity;
    private final int gameTick;
    private final List<InventoryItem> items;

    public ItemContainerChanged(int containerId, int capacity, int gameTick,
                                List<InventoryItem> items) {
        this.containerId = containerId;
        this.capacity = capacity;
        this.gameTick = gameTick;
        this.items = items == null ? Collections.emptyList() :
            Collections.unmodifiableList(new ArrayList<>(items));
    }

    public int containerId() { return containerId; }
    public int capacity() { return capacity; }
    public int gameTick() { return gameTick; }
    public List<InventoryItem> items() { return items; }
}
