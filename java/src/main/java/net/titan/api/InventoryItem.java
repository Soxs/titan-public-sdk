package net.titan.api;

import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.utils.Magic;

public final class InventoryItem {
    private boolean liveHandle = true;
    private int containerId = InventoryId.INVENTORY;
    private int slot = -1;
    private int id = -1;
    private int quantity;
    private String name;

    private InventoryItem live() { return TitanRuntime.currentLive(this); }

    public int containerId() { return live().containerId; }
    public int slot() { return live().slot; }
    public int id() { return live().id; }
    public int quantity() { return live().quantity; }
    public String name() {
        String value = live().name;
        return value == null ? "" : value;
    }

    /**
     * Dispatches an ordinary inventory action or a live opcode-43 submenu
     * label. Ordinary actions take precedence when labels collide.
     */
    public boolean interact(String action) {
        if (action == null || action.isEmpty()) return false;
        InteractionBackend actions = TitanRuntime.getInteractionBackend();
        int itemSlot = slot();
        int itemId = id();
        if (itemSlot >= 0 && actions.interactInventoryItemAtSlot(itemSlot, itemId, action)) {
            return true;
        }
        return actions.interactInventoryItem(itemId, action);
    }

    public boolean useOn(InventoryItem target) {
        if (target == null) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnItem(
            slot(), id(), target.slot(), target.id());
    }

    public boolean useOn(NPC target) {
        if (target == null) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnNpc(
            slot(), id(), target.hashIndex());
    }

    public boolean useOn(TileObject target) {
        if (target == null) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnObject(
            slot(), id(), target.id(), target.tileX(), target.tileY());
    }

    public boolean castOn(Magic.SpellInfo spell) { return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Standard spell) { return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Ancient spell) { return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Lunar spell) { return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Necromancy spell) { return Magic.castOn(spell, this); }

    public boolean exists() { return TitanRuntime.liveExists(this); }

    public InventoryItem snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) {
        return TitanRuntime.liveEquals(this, other);
    }

    @Override
    public int hashCode() {
        return TitanRuntime.liveHashCode(this);
    }
}
