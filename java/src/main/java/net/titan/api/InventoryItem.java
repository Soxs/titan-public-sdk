package net.titan.api;

import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.utils.Magic;

public final class InventoryItem {
    private boolean liveHandle = true;
    private boolean actionable = true;

    /** Owned bank-memory value. Historical slots cannot dispatch inventory actions. */
    public static InventoryItem rememberedBankItem(int slot, int id, int quantity) {
        InventoryItem item = new InventoryItem();
        item.liveHandle = false; item.actionable = false; item.containerId = InventoryId.BANK;
        item.slot = slot; item.id = id; item.quantity = quantity;
        return item;
    }
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
        if (!actionable) return false;
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
        if (!actionable) return false;
        if (target == null || !target.actionable) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnItem(
            slot(), id(), target.slot(), target.id());
    }

    public boolean useOn(NPC target) {
        if (!actionable) return false;
        if (target == null) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnNpc(
            slot(), id(), target.hashIndex());
    }

    /** Select this inventory item and use it on the exact live player as one queued pair. */
    public boolean useOn(Player target) {
        if (!actionable) return false;
        if (target == null || slot() < 0 || id() < 0 || target.hashIndex() < 0) return false;
        MenuActionRequest source = new MenuActionRequest(MenuAction.WIDGET_TARGET, 0, slot(),
            net.titan.gamevals.InterfaceID.Inventory.ITEMS, -1, -1, -1, "Use", "", false, -1, 1, 1, -1, 0, 0);
        MenuActionRequest selectedTarget = new MenuActionRequest(MenuAction.ITEM_USE_ON_PLAYER, target.hashIndex(), 0, 0,
            target.worldViewId(), -1, -1, "Use", "", false, target.plane(), 1, 1, -1, target.entityPtr(), 0);
        return Titan.client().invokeSelectedMenuAction(source, selectedTarget, id());
    }

    public boolean useOn(TileObject target) {
        if (!actionable) return false;
        if (target == null) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnObject(
            slot(), id(), target.id(), target.tileX(), target.tileY());
    }

    /**
     * SDK 129. Use this item on a ground item stack: {@code WIDGET_TARGET} on
     * this inventory slot and {@code ITEM_USE_ON_GROUND_ITEM} at the stack's
     * tile are queued together as one selected pair (the same path
     * {@code Magic.castOn} uses for ground items). The host pins this item id
     * and binds the unique matching stack on the game thread. Requires a live
     * slot; returns {@code true} when the pair was queued.
     */
    public boolean useOn(GroundItem target) {
        if (!actionable) return false;
        if (target == null) return false;
        return TitanRuntime.getInteractionBackend().useInventoryItemOnGroundItem(
            slot(), id(), target.id(), target.tileX(), target.tileY(),
            target.plane(), target.worldViewId());
    }

    public boolean castOn(Magic.SpellInfo spell) { if (!actionable) return false; return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Standard spell) { if (!actionable) return false; return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Ancient spell) { if (!actionable) return false; return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Lunar spell) { if (!actionable) return false; return Magic.castOn(spell, this); }
    public boolean castOn(Magic.Necromancy spell) { if (!actionable) return false; return Magic.castOn(spell, this); }

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
