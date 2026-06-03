package net.titan.api;

public enum EquipmentSlot {
    HEAD(0, 15),
    CAPE(1, 16),
    AMULET(2, 17),
    WEAPON(3, 18),
    BODY(4, 19),
    SHIELD(5, 20),
    ARMS(6, -1),
    LEGS(7, 21),
    HAIR(8, -1),
    GLOVES(9, 22),
    BOOTS(10, 23),
    JAW(11, -1),
    RING(12, 24),
    AMMO(13, 25);

    public static final int MIN_ORDINAL = 0;
    public static final int MAX_ORDINAL = 13;
    public static final int SLOT_COUNT = 14;
    public static final int WORN_ITEMS_GROUP = 387;

    private final int ordinal;
    private final int widgetChild;

    EquipmentSlot(int ordinal, int widgetChild) {
        this.ordinal = ordinal;
        this.widgetChild = widgetChild;
    }

    public int id() { return ordinal; }
    public int slot() { return ordinal; }

    public int widgetPackedId() {
        return widgetChild < 0 ? 0 : InterfaceId.pack(WORN_ITEMS_GROUP, widgetChild);
    }

    public static boolean isValid(int ordinal) {
        return ordinal >= MIN_ORDINAL && ordinal <= MAX_ORDINAL;
    }

    public static EquipmentSlot fromOrdinal(int ordinal) {
        for (EquipmentSlot slot : values()) {
            if (slot.ordinal == ordinal) return slot;
        }
        return HEAD;
    }
}
