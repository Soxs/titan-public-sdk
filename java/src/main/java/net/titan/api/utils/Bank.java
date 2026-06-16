package net.titan.api.utils;

import net.titan.api.BankSets;
import net.titan.api.Client;
import net.titan.api.InterfaceId;
import net.titan.api.InventoryId;
import net.titan.api.InventoryItem;
import net.titan.api.ItemContainer;
import net.titan.api.MenuAction;
import net.titan.api.Player;
import net.titan.api.TileObject;
import net.titan.api.Titan;
import net.titan.api.Varbits;
import net.titan.api.Widget;
import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.queries.Queries;

import java.util.List;
import java.util.Optional;

/// Plugin-side bank state, query, action, and PIN helpers. Mirrors the C++
/// {@code titan::utils::Bank} surface (open/close, deposit/withdraw, PIN,
/// near-bank), composing existing {@link Client}/widget/query calls.
public final class Bank {
    private Bank() {}

    private static Client client() {
        return Titan.client();
    }

    private static InteractionBackend actions() {
        return TitanRuntime.getInteractionBackend();
    }

    // --- State reads -----------------------------------------------------

    public static boolean isOpen() {
        return widgetVisible(InterfaceId.BANK_ITEM_CONTAINER);
    }

    public static boolean isGeOpen() {
        return widgetVisible(InterfaceId.GRAND_EXCHANGE_INVENTORY_ITEM_CONTAINER);
    }

    public static boolean isSearchOpen() {
        return client().widgetByText("Enter amount:").isPresent()
            || client().widgetByText("Enter name:").isPresent();
    }

    public static boolean isNotedMode() {
        return client().varbit(Varbits.BANK_WITHDRAWNOTES) == 1;
    }

    public static int getBankTab() {
        return client().varbit(Varbits.CURRENT_BANK_TAB);
    }

    public static boolean isMainTabOpen() {
        return getBankTab() == 0;
    }

    // --- Bank container queries ------------------------------------------

    public static List<InventoryItem> getAll() {
        Optional<ItemContainer> snap = client().itemContainer(InventoryId.BANK);
        return snap.map(ItemContainer::items).orElse(java.util.Collections.emptyList());
    }

    public static boolean contains(int itemId) {
        return find(itemId).isPresent();
    }

    public static boolean contains(String name) {
        return find(name).isPresent();
    }

    public static boolean contains(int itemId, int minQuantity) {
        return count(itemId) >= minQuantity;
    }

    public static int count(int itemId) {
        int total = 0;
        for (InventoryItem item : getAll()) {
            if (item.id() == itemId) total += item.quantity();
        }
        return total;
    }

    public static int count(String name) {
        int total = 0;
        for (InventoryItem item : getAll()) {
            if (containsIgnoreCase(itemName(item), name)) total += item.quantity();
        }
        return total;
    }

    public static Optional<InventoryItem> find(int itemId) {
        for (InventoryItem item : getAll()) {
            if (item.id() == itemId) return Optional.of(item);
        }
        return Optional.empty();
    }

    public static Optional<InventoryItem> find(String name) {
        for (InventoryItem item : getAll()) {
            if (containsIgnoreCase(itemName(item), name)) return Optional.of(item);
        }
        return Optional.empty();
    }

    // --- Actions ---------------------------------------------------------

    public static boolean close() {
        return actions().widgetInteract(MenuAction.CC_OP, 1, 11, InterfaceId.BANK_CLOSE);
    }

    public static boolean setNotedMode(boolean noted) {
        if (isNotedMode() == noted) return true;
        return actions().widgetInteract(MenuAction.CC_OP, 1, -1, InterfaceId.BANK_NOTE_TOGGLE);
    }

    public static boolean depositAll() {
        return actions().widgetInteract(
            MenuAction.CC_OP, 1, -1, InterfaceId.BANK_DEPOSIT_INVENTORY);
    }

    public static boolean depositEquipment() {
        return actions().widgetInteract(
            MenuAction.CC_OP, 1, -1, InterfaceId.BANK_DEPOSIT_EQUIPMENT);
    }

    public static boolean depositAllOfSlot(int slot) {
        return actions().widgetInteract(
            MenuAction.CC_OP_LOW_PRIORITY, 8, slot,
            InterfaceId.BANK_INVENTORY_ITEM_CONTAINER);
    }

    public static boolean depositOneOfSlot(int slot) {
        int qty = bankQuantityType();
        int identifier = (qty == 0) ? 2 : 3;
        return actions().widgetInteract(
            MenuAction.CC_OP, identifier, slot,
            InterfaceId.BANK_INVENTORY_ITEM_CONTAINER);
    }

    public static boolean depositAllOfItem(int itemId) {
        return inventorySlotOf(itemId).map(Bank::depositAllOfSlot).orElse(false);
    }

    public static boolean depositOneOfItem(int itemId) {
        return inventorySlotOf(itemId).map(Bank::depositOneOfSlot).orElse(false);
    }

    public static boolean depositAllExcept(int... keepIds) {
        Optional<ItemContainer> inv = client().itemContainer(InventoryId.INVENTORY);
        if (inv.isEmpty()) return false;
        for (InventoryItem item : inv.get().items()) {
            boolean keep = false;
            if (keepIds != null) {
                for (int kid : keepIds) {
                    if (item.id() == kid) { keep = true; break; }
                }
            }
            if (!keep) return depositAllOfSlot(item.slot());
        }
        return false;
    }

    public static boolean withdrawItem(int itemId) {
        Optional<InventoryItem> slot = find(itemId);
        if (slot.isEmpty()) return false;
        int qty = bankQuantityType();
        int identifier = (qty == 0) ? 1 : 2;
        return actions().widgetInteract(
            MenuAction.CC_OP, identifier, slot.get().slot(),
            InterfaceId.BANK_ITEM_CONTAINER);
    }

    public static boolean withdrawAllItem(int itemId) {
        Optional<InventoryItem> slot = find(itemId);
        if (slot.isEmpty()) return false;
        int qty = bankQuantityType();
        int identifier = (qty == 4) ? 1 : 7;
        int op = (qty == 4) ? MenuAction.CC_OP : MenuAction.CC_OP_LOW_PRIORITY;
        return actions().widgetInteract(op, identifier, slot.get().slot(),
            InterfaceId.BANK_ITEM_CONTAINER);
    }

    /// Withdraw a specific amount. For 1/5/10 the matching menu option is used.
    /// For other amounts: if BANK_REQUESTEDQUANTITY already equals {@code amount}
    /// the fast path is clicked; otherwise Withdraw-X is clicked and {@code true}
    /// is returned so the caller can type the amount once {@link #isSearchOpen()}.
    public static boolean withdrawItemAmount(int itemId, int amount) {
        Optional<InventoryItem> found = find(itemId);
        if (found.isEmpty()) return false;
        int slot = found.get().slot();
        int qty = bankQuantityType();

        if (amount == 1) {
            int id = (qty == 0) ? 1 : 2;
            return actions().widgetInteract(MenuAction.CC_OP, id, slot,
                InterfaceId.BANK_ITEM_CONTAINER);
        }
        if (amount == 5) {
            int id = (qty == 1) ? 1 : 3;
            return actions().widgetInteract(MenuAction.CC_OP, id, slot,
                InterfaceId.BANK_ITEM_CONTAINER);
        }
        if (amount == 10) {
            int id = (qty == 2) ? 1 : 4;
            return actions().widgetInteract(MenuAction.CC_OP, id, slot,
                InterfaceId.BANK_ITEM_CONTAINER);
        }

        int lastQty = client().varbit(Varbits.BANK_REQUESTEDQUANTITY);
        if (lastQty == amount) {
            int id = (qty == 3) ? 1 : 5;
            return actions().widgetInteract(MenuAction.CC_OP, id, slot,
                InterfaceId.BANK_ITEM_CONTAINER);
        }

        int id = (qty == 3) ? 1 : 6;
        int op = (qty == 3) ? MenuAction.CC_OP : MenuAction.CC_OP_LOW_PRIORITY;
        return actions().widgetInteract(op, id, slot, InterfaceId.BANK_ITEM_CONTAINER);
    }

    public static boolean interactItemInBank(int itemId) {
        return interactItemInBank(itemId, 9);
    }

    public static boolean interactItemInBank(int itemId, int identifier) {
        Optional<InventoryItem> slot = find(itemId);
        if (slot.isEmpty()) return false;
        return actions().widgetInteract(MenuAction.CC_OP, identifier, slot.get().slot(),
            InterfaceId.BANK_ITEM_CONTAINER);
    }

    // --- Opening a bank --------------------------------------------------

    public static boolean isNearBank() {
        return isNearBank(15);
    }

    public static boolean isNearBank(int distance) {
        return Queries.objects(distance)
            .where(o -> BankSets.isBankObject(o.id()))
            .first()
            .isPresent();
    }

    public static boolean open() {
        Optional<Player> local = client().localPlayer();
        Optional<TileObject> nearest = Queries.objects(15)
            .where(o -> BankSets.isBankObject(o.id()))
            .nearest();
        if (nearest.isPresent()) {
            TileObject obj = nearest.get();
            if (obj.interact("Bank")) return true;
            if (obj.interact("Use")) return true;
        }
        if (local.isEmpty()) return false;
        return Queries.npcs()
            .hasAction("Bank")
            .nearestTo(local.get())
            .map(npc -> npc.interact("Bank"))
            .orElse(false);
    }

    // --- PIN helpers -----------------------------------------------------

    public static boolean isPinVisible() {
        return client().widgetByText("Please enter your PIN").isPresent();
    }

    public static int pinRequestedDigitIndex() {
        Optional<Widget> w = client().widget(InterfaceId.BANK_PIN_INSTRUCTION);
        if (w.isEmpty() || !w.get().isVisible()) return -1;
        String txt = w.get().text();
        if (containsIgnoreCase(txt, "FIRST")) return 0;
        if (containsIgnoreCase(txt, "SECOND")) return 1;
        if (containsIgnoreCase(txt, "THIRD")) return 2;
        if (containsIgnoreCase(txt, "FOURTH")) return 3;
        return -1;
    }

    public static boolean typePin(String pin) {
        if (pin == null || pin.length() != 4) return false;
        for (int i = 0; i < pin.length(); i++) {
            char c = pin.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        int idx = pinRequestedDigitIndex();
        if (idx < 0 || idx > 3) return false;
        return client().sendKeyboardString(String.valueOf(pin.charAt(idx)));
    }

    // --- Internal helpers ------------------------------------------------

    private static boolean widgetVisible(int packedId) {
        return client().widget(packedId).map(Widget::isVisible).orElse(false);
    }

    private static int bankQuantityType() {
        return client().varbit(Varbits.BANK_QUANTITY_TYPE);
    }

    private static Optional<Integer> inventorySlotOf(int itemId) {
        return client().itemContainer(InventoryId.INVENTORY)
            .flatMap(inv -> inv.items().stream()
                .filter(item -> item.id() == itemId)
                .map(InventoryItem::slot)
                .findFirst());
    }

    private static String itemName(InventoryItem item) {
        String name = item.name();
        if (name != null && !name.isEmpty()) return name;
        return client().itemComposition(item.id())
            .map(comp -> comp.name())
            .orElse("");
    }

    private static boolean containsIgnoreCase(String haystack, String needle) {
        if (needle == null || needle.isEmpty()) return true;
        if (haystack == null) return false;
        return haystack.toLowerCase().contains(needle.toLowerCase());
    }
}
