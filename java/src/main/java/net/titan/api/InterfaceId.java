package net.titan.api;

public final class InterfaceId {
    public static final int MAKE_BUTTON = 17694734;
    public static final int AUTO_RETALIATE = 38862880;
    public static final int QUESTSCROLL_CLOSE = 10027024;
    public static final int QUESTSCROLL_CONTENT = pack(153, 1);
    public static final int DIALOG_OPTIONS = pack(219, 1);
    public static final int SPEC_ORB = pack(160, 34);
    public static final int RUN_ORB = pack(160, 27);

    public static final int BANK_CLOSE = pack(12, 2);
    public static final int BANK_ITEM_CONTAINER = pack(12, 12);
    public static final int BANK_NOTE_TOGGLE = pack(12, 19);
    public static final int BANK_DEPOSIT_INVENTORY = pack(12, 41);
    public static final int BANK_DEPOSIT_EQUIPMENT = pack(12, 43);
    public static final int BANK_INVENTORY_ITEM_CONTAINER = pack(15, 3);

    public static final int DEPOSIT_BOX_INVENTORY_ITEM_CONTAINER = pack(192, 2);
    public static final int DEPOSIT_BOX_DEPOSIT_BUTTON = pack(192, 4);
    public static final int GRAND_EXCHANGE_INVENTORY_ITEM_CONTAINER = pack(467, 0);
    public static final int BANK_PIN_CONTAINER = pack(213, 0);
    public static final int BANK_PIN_INSTRUCTION = pack(213, 1);

    private InterfaceId() {}

    public static int pack(int group, int child) {
        return (group << 16) | child;
    }

    public static int group(int packedId) {
        return (packedId >>> 16) & 0xffff;
    }

    public static int child(int packedId) {
        return packedId & 0xffff;
    }
}
