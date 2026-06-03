package net.titan.api;

public final class InventoryId {
    public static final int INVENTORY = 93;
    public static final int EQUIPMENT = 94;
    public static final int BANK = 95;

    private InventoryId() {}

    public static String nameOf(int id) {
        switch (id) {
        case INVENTORY: return "INVENTORY";
        case EQUIPMENT: return "EQUIPMENT";
        case BANK: return "BANK";
        default: return null;
        }
    }
}
