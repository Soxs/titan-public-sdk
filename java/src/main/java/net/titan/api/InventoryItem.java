package net.titan.api;

public final class InventoryItem {
    private int slot = -1;
    private int id = -1;
    private int quantity;
    private String name;

    public int slot() { return slot; }
    public int id() { return id; }
    public int quantity() { return quantity; }
    public String name() { return name == null ? "" : name; }
}
