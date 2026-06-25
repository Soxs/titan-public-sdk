package net.titan.api;

import net.titan.api.internal.TitanRuntime;

import java.util.Collections;
import java.util.List;

public final class ItemContainer {
    private boolean liveHandle = true;
    private int containerId = -1;
    private int capacity;
    private List<InventoryItem> items;

    private ItemContainer live() { return TitanRuntime.currentLive(this); }

    public int containerId() { return live().containerId; }
    public int capacity() { return live().capacity; }
    public List<InventoryItem> items() {
        List<InventoryItem> value = live().items;
        return value == null ? Collections.emptyList() : Collections.unmodifiableList(value);
    }

    public boolean exists() { return TitanRuntime.liveExists(this); }

    public ItemContainer snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) {
        return TitanRuntime.liveEquals(this, other);
    }

    @Override
    public int hashCode() {
        return TitanRuntime.liveHashCode(this);
    }
}
