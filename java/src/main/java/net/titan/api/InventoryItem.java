package net.titan.api;

import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;

public final class InventoryItem {
    private int slot = -1;
    private int id = -1;
    private int quantity;
    private String name;

    public int slot() { return slot; }
    public int id() { return id; }
    public int quantity() { return quantity; }
    public String name() { return name == null ? "" : name; }

    public boolean interact(String action) {
        if (action == null || action.isEmpty()) return false;
        InteractionBackend actions = TitanRuntime.getInteractionBackend();
        if (slot >= 0 && actions.interactInventoryItemAtSlot(slot, id, action)) {
            return true;
        }
        return actions.interactInventoryItem(id, action);
    }

    public boolean useOn(InventoryItem target) {
        if (target == null) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnItem(
            slot, id, target.slot(), target.id());
    }

    public boolean useOn(NPC target) {
        if (target == null) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnNpc(
            slot, id, target.hashIndex());
    }

    public boolean useOn(TileObject target) {
        if (target == null) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnObject(
            slot, id, target.id(), target.tileX(), target.tileY());
    }
}
