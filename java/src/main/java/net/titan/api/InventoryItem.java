package net.titan.api;

public final class InventoryItem {
    private transient Client client;
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
        Client value = client;
        if (value == null) return false;
        if (slot >= 0 && value.interactInventoryItemAtSlot(slot, id, action)) {
            return true;
        }
        return value.interactInventoryItem(id, action);
    }

    public boolean useOn(InventoryItem target) {
        if (target == null) return false;
        Client value = client;
        return value != null && value.useInventoryItemOnItem(
            slot, id, target.slot(), target.id());
    }

    public boolean useOn(Npc target) {
        if (target == null) return false;
        Client value = client;
        return value != null && value.useInventoryItemOnNpc(slot, id, target.hashIndex());
    }

    public boolean useOn(TileObject target) {
        if (target == null) return false;
        Client value = client;
        return value != null && value.useInventoryItemOnObject(
            slot, id, target.id(), target.tileX(), target.tileY());
    }
}
