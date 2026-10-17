package net.titan.api.utils;

import net.titan.api.Client;
import net.titan.api.ItemDefinition;
import net.titan.api.MenuAction;
import net.titan.api.Titan;
import net.titan.api.Widget;
import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.gamevals.InterfaceID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.function.IntPredicate;

/// Plugin-side shop state and action helpers. Mirrors the C++
/// {@code titan::utils::Shop} surface (SDK 153). Every shop NPC opens the shared
/// {@code Shopmain} stock grid beside the {@code Shopside} inventory panel.
///
/// Buying and selling fire the slot's own component operation (CC_OP) for the
/// largest quantity button that does not exceed the request -- Buy/Sell 50, 10, 5
/// or 1 -- once per call, so a caller repeats until it holds what it needs. Actions
/// report queue acceptance, not gameplay success. The close X is a component
/// operation on a dynamic child of the frame, not a {@code WIDGET_CLOSE} request.
public final class Shop {
    private Shop() {}

    /** Dynamic child slot of {@code Shopmain.FRAME} carrying the close X. */
    private static final int CLOSE_CHILD_SLOT = 11;
    /** CC_OP identifiers on a stock or inventory slot: 2-5 are quantity 1/5/10/50. */
    private static final int QUANTITY_1_OP = 2;
    private static final int QUANTITY_5_OP = 3;
    private static final int QUANTITY_10_OP = 4;
    private static final int QUANTITY_50_OP = 5;
    /** Placeholder item id the client shows in empty inventory slots. */
    private static final int EMPTY_SLOT_ITEM_ID = 6512;

    /** One stocked slot of the shop's grid; {@code quantity} is 0 when sold out. */
    public static final class ShopItem {
        private final int itemId;
        private final int quantity;
        private final int slot;

        ShopItem(int itemId, int quantity, int slot) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.slot = slot;
        }

        public int itemId() { return itemId; }
        public int quantity() { return quantity; }
        /** Dynamic child slot of {@code Shopmain.ITEMS} (the CC_OP param0). */
        public int slot() { return slot; }

        @Override
        public String toString() {
            return "ShopItem{itemId=" + itemId + ", quantity=" + quantity + ", slot=" + slot + "}";
        }
    }

    private static Client client() {
        return Titan.client();
    }

    private static InteractionBackend actions() {
        return TitanRuntime.getInteractionBackend();
    }

    private static int quantityOp(int quantity) {
        if (quantity >= 50) return QUANTITY_50_OP;
        if (quantity >= 10) return QUANTITY_10_OP;
        if (quantity >= 5) return QUANTITY_5_OP;
        return QUANTITY_1_OP;
    }

    private static boolean hasItem(int itemId) {
        return itemId >= 0 && itemId != EMPTY_SLOT_ITEM_ID;
    }

    /** Case-insensitive substring match against the item's cache name. */
    private static boolean nameContains(int itemId, String needleLower) {
        if (needleLower.isEmpty() || !hasItem(itemId)) return false;
        return client().itemDefinition(itemId)
            .map(ItemDefinition::name)
            .map(name -> name.toLowerCase(Locale.ROOT).contains(needleLower))
            .orElse(false);
    }

    private static int inventorySlot(IntPredicate match) {
        List<Widget> children = client().widgetChildren(InterfaceID.Shopside.ITEMS);
        for (int i = 0; i < children.size(); i++) {
            Widget child = children.get(i);
            if (child != null && match.test(child.itemId())) return i;
        }
        return -1;
    }

    private static boolean sellSlot(int slot, int quantity) {
        return slot >= 0
            && actions().widgetInteract(MenuAction.CC_OP, quantityOp(quantity), slot, InterfaceID.Shopside.ITEMS);
    }

    // --- State reads -----------------------------------------------------

    /** True while a shop interface is open (its stock grid is visible). */
    public static boolean isOpen() {
        return client().widget(InterfaceID.Shopmain.ITEMS).map(Widget::isVisible).orElse(false);
    }

    /** Every stocked slot, in grid order. Empty when the shop is closed. */
    public static List<ShopItem> items() {
        if (!isOpen()) return Collections.emptyList();
        List<Widget> children = client().widgetChildren(InterfaceID.Shopmain.ITEMS);
        List<ShopItem> out = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            Widget child = children.get(i);
            if (child == null || !hasItem(child.itemId())) continue;
            out.add(new ShopItem(child.itemId(), child.itemQuantity(), i));
        }
        return Collections.unmodifiableList(out);
    }

    /** Grid slot (the CC_OP param0) of {@code itemId}; empty when closed or unlisted. */
    public static OptionalInt slotOf(int itemId) {
        for (ShopItem item : items()) {
            if (item.itemId() == itemId) return OptionalInt.of(item.slot());
        }
        return OptionalInt.empty();
    }

    /** The shop's stock of {@code itemId}; 0 when closed, unlisted or sold out. */
    public static int stock(int itemId) {
        for (ShopItem item : items()) {
            if (item.itemId() == itemId) return item.quantity();
        }
        return 0;
    }

    /** True when the shop has at least one {@code itemId} in stock. */
    public static boolean has(int itemId) {
        return has(itemId, 1);
    }

    /** True when the shop has at least {@code quantity} of {@code itemId} in stock. */
    public static boolean has(int itemId, int quantity) {
        return stock(itemId) >= quantity;
    }

    /**
     * The item's general store value ({@link ItemDefinition#storeValue()}); 0 when the
     * definition is unknown. Shops scale their actual price from this by stock.
     */
    public static int storeValue(int itemId) {
        return client().itemDefinition(itemId).map(ItemDefinition::storeValue).orElse(0);
    }

    /** True while the shop is open and an inventory item's name contains {@code name}. */
    public static boolean hasItemToSell(String name) {
        if (!isOpen() || name == null) return false;
        String needle = name.toLowerCase(Locale.ROOT);
        return inventorySlot(id -> nameContains(id, needle)) >= 0;
    }

    // --- Actions (queue acceptance, not gameplay success) ----------------

    /**
     * Buy {@code itemId} with the largest quantity button not above {@code quantity}
     * (50, 10, 5 or 1). False when the shop is closed or holds fewer than {@code quantity}.
     */
    public static boolean buy(int itemId, int quantity) {
        if (!isOpen() || quantity <= 0 || !has(itemId, quantity)) return false;
        OptionalInt slot = slotOf(itemId);
        return slot.isPresent()
            && actions().widgetInteract(MenuAction.CC_OP, quantityOp(quantity), slot.getAsInt(),
                InterfaceID.Shopmain.ITEMS);
    }

    /** Sell the first inventory slot holding {@code itemId} (quantity button 50, 10, 5 or 1). */
    public static boolean sell(int itemId, int quantity) {
        if (!isOpen() || quantity <= 0 || !hasItem(itemId)) return false;
        return sellSlot(inventorySlot(id -> id == itemId), quantity);
    }

    /** Sell the first inventory item whose name contains {@code name} (case-insensitive). */
    public static boolean sellByName(String name, int quantity) {
        if (!isOpen() || quantity <= 0 || name == null) return false;
        String needle = name.toLowerCase(Locale.ROOT);
        return sellSlot(inventorySlot(id -> nameContains(id, needle)), quantity);
    }

    /** Close the shop through its frame's close X; true immediately when no shop is open. */
    public static boolean close() {
        if (!isOpen()) return true;
        return actions().widgetInteract(MenuAction.CC_OP, 1, CLOSE_CHILD_SLOT, InterfaceID.Shopmain.FRAME);
    }
}
