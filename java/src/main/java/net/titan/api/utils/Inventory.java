package net.titan.api.utils;

import net.titan.api.InventoryItem;
import net.titan.api.InterfaceId;
import net.titan.api.Titan;
import net.titan.api.Widget;
import net.titan.api.queries.Queries;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Inventory {
    public static final int CAPACITY = 28;
    public static final int INVENTORY_PACKED_ID = InterfaceId.pack(149, 0);

    private Inventory() {}

    public static boolean isOpen() {
        return Titan.client().widget(INVENTORY_PACKED_ID)
            .map(Widget::isVisible)
            .orElse(false);
    }

    public static int size() {
        return Queries.inventory().count();
    }

    public static int emptySlots() {
        return Math.max(0, CAPACITY - size());
    }

    public static boolean isFull() {
        return size() >= CAPACITY;
    }

    public static boolean isEmpty() {
        return size() == 0;
    }

    public static List<InventoryItem> getAll() {
        return Titan.client().inventoryItems();
    }

    public static Optional<InventoryItem> find(int itemId) {
        return Queries.inventory().id(itemId).first();
    }

    public static Optional<InventoryItem> find(String name) {
        return Queries.inventory().nameContains(name).first();
    }

    public static Optional<InventoryItem> getSlot(int slot) {
        if (slot < 0 || slot >= CAPACITY) return Optional.empty();
        return Queries.inventory().slot(slot).first();
    }

    public static List<InventoryItem> getByIds(int... ids) {
        if (ids == null || ids.length == 0) return Collections.emptyList();
        return Queries.inventory().ids(ids).toList();
    }

    public static List<InventoryItem> getByNames(String... names) {
        if (names == null || names.length == 0) return Collections.emptyList();
        return getAll().stream()
            .filter(item -> containsAnyIgnoreCase(item.name(), names))
            .collect(Collectors.toList());
    }

    public static boolean contains(int itemId) {
        return find(itemId).isPresent();
    }

    public static boolean contains(int itemId, int minQuantity) {
        return count(itemId) >= minQuantity;
    }

    public static boolean contains(String name) {
        return find(name).isPresent();
    }

    public static int count(int itemId) {
        return Queries.inventory().id(itemId).totalQuantity();
    }

    public static int count(String name) {
        return Queries.inventory().nameContains(name).totalQuantity();
    }

    public static boolean containsAny(int... ids) {
        return ids != null && Queries.inventory().ids(ids).any();
    }

    public static boolean containsAll(int... ids) {
        if (ids == null) return true;
        for (int id : ids) {
            if (!contains(id)) return false;
        }
        return true;
    }

    public static boolean drop(int itemId) {
        return find(itemId).map(item -> item.interact("Drop") || item.interact("Destroy"))
            .orElse(false);
    }

    public static boolean drop(String name) {
        return find(name).map(item -> item.interact("Drop") || item.interact("Destroy"))
            .orElse(false);
    }

    private static boolean containsAnyIgnoreCase(String haystack, String... needles) {
        if (haystack == null) haystack = "";
        String lower = haystack.toLowerCase();
        for (String needle : needles) {
            if (needle != null && lower.contains(needle.toLowerCase())) return true;
        }
        return false;
    }
}
