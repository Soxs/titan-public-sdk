package net.titan.api.utils;

import net.titan.api.EquipmentSlot;
import net.titan.api.InventoryId;
import net.titan.api.InventoryItem;
import net.titan.api.ItemComposition;
import net.titan.api.MenuAction;
import net.titan.api.Titan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Equipment {
    private Equipment() {}

    public static final class EquippedItem {
        private final EquipmentSlot slot;
        private final int itemId;
        private final int quantity;
        private final String name;

        private EquippedItem(EquipmentSlot slot, int itemId, int quantity, String name) {
            this.slot = slot;
            this.itemId = itemId;
            this.quantity = quantity;
            this.name = name == null ? "" : name;
        }

        public EquipmentSlot slot() { return slot; }
        public int itemId() { return itemId; }
        public int id() { return itemId; }
        public int quantity() { return quantity; }
        public String name() { return name; }
        public boolean isValid() { return itemId > 0; }

        public boolean unequip() {
            int packedId = slot.widgetPackedId();
            return packedId != 0 && Titan.client().widgetInteract(
                MenuAction.CC_OP, 1, -1, packedId);
        }
    }

    public static List<EquippedItem> getAll() {
        return Titan.client().itemContainer(InventoryId.EQUIPMENT)
            .map(container -> {
                List<EquippedItem> out = new ArrayList<>();
                for (InventoryItem item : container.items()) {
                    if (item.id() <= 0 || !EquipmentSlot.isValid(item.slot())) continue;
                    EquipmentSlot slot = EquipmentSlot.fromOrdinal(item.slot());
                    out.add(new EquippedItem(
                        slot,
                        item.id(),
                        Math.max(1, item.quantity()),
                        resolveName(item.id())));
                }
                return Collections.unmodifiableList(out);
            })
            .orElse(Collections.emptyList());
    }

    public static Optional<EquippedItem> find(EquipmentSlot slot) {
        if (slot == null) return Optional.empty();
        return getAll().stream().filter(item -> item.slot() == slot).findFirst();
    }

    public static Optional<EquippedItem> find(int itemId) {
        return getAll().stream().filter(item -> item.itemId() == itemId).findFirst();
    }

    public static Optional<EquippedItem> find(String name) {
        return getAll().stream()
            .filter(item -> containsIgnoreCase(item.name(), name))
            .findFirst();
    }

    public static List<EquippedItem> getByIds(int... ids) {
        if (ids == null || ids.length == 0) return Collections.emptyList();
        return getAll().stream()
            .filter(item -> contains(ids, item.itemId()))
            .collect(Collectors.toList());
    }

    public static List<EquippedItem> getByNames(String... names) {
        if (names == null || names.length == 0) return Collections.emptyList();
        return getAll().stream()
            .filter(item -> containsAnyIgnoreCase(item.name(), names))
            .collect(Collectors.toList());
    }

    public static boolean contains(EquipmentSlot slot) {
        return find(slot).isPresent();
    }

    public static boolean contains(int itemId) {
        return find(itemId).isPresent();
    }

    public static boolean contains(int itemId, int minQuantity) {
        return find(itemId).map(item -> item.quantity() >= minQuantity).orElse(false);
    }

    public static boolean contains(String name) {
        return find(name).isPresent();
    }

    public static boolean containsAny(int... ids) {
        if (ids == null) return false;
        for (int id : ids) {
            if (contains(id)) return true;
        }
        return false;
    }

    public static boolean containsAll(int... ids) {
        if (ids == null) return true;
        for (int id : ids) {
            if (!contains(id)) return false;
        }
        return true;
    }

    public static boolean containsAnyName(String... names) {
        if (names == null) return false;
        for (String name : names) {
            if (contains(name)) return true;
        }
        return false;
    }

    public static boolean containsAllNames(String... names) {
        if (names == null) return true;
        for (String name : names) {
            if (!contains(name)) return false;
        }
        return true;
    }

    public static int count(int itemId) {
        int total = 0;
        for (EquippedItem item : getAll()) {
            if (item.itemId() == itemId) total += item.quantity();
        }
        return total;
    }

    public static int count(int... ids) {
        if (ids == null || ids.length == 0) return 0;
        int total = 0;
        for (EquippedItem item : getAll()) {
            if (contains(ids, item.itemId())) total += item.quantity();
        }
        return total;
    }

    public static boolean unequip(EquipmentSlot slot) {
        return find(slot).map(EquippedItem::unequip).orElse(false);
    }

    public static boolean unequip(int itemId) {
        return find(itemId).map(EquippedItem::unequip).orElse(false);
    }

    public static boolean unequip(String name) {
        return find(name).map(EquippedItem::unequip).orElse(false);
    }

    private static String resolveName(int itemId) {
        return Titan.client().itemComposition(itemId)
            .map(ItemComposition::name)
            .orElse("");
    }

    private static boolean contains(int[] ids, int id) {
        for (int value : ids) {
            if (value == id) return true;
        }
        return false;
    }

    private static boolean containsIgnoreCase(String haystack, String needle) {
        if (needle == null || needle.isEmpty()) return true;
        return haystack != null &&
            haystack.toLowerCase().contains(needle.toLowerCase());
    }

    private static boolean containsAnyIgnoreCase(String haystack, String... needles) {
        if (needles == null) return false;
        for (String needle : needles) {
            if (containsIgnoreCase(haystack, needle)) return true;
        }
        return false;
    }
}
