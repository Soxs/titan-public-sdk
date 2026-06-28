package net.titan.api;

public final class PlayerCompositionSlot {
    public static final int KIND_EMPTY = 0;
    public static final int KIND_ITEM = 1;
    public static final int KIND_NON_ITEM = 2;
    public static final int KIND_UNKNOWN_RAW = 3;

    private final int slotIndex;
    private final int rawValue;
    private final int itemId;
    private final int kind;

    public PlayerCompositionSlot(int slotIndex, int rawValue, int itemId, int kind) {
        this.slotIndex = slotIndex;
        this.rawValue = rawValue;
        this.itemId = itemId;
        this.kind = kind;
    }

    public int slotIndex() { return slotIndex; }
    public int rawValue() { return rawValue; }
    public int itemId() { return itemId; }
    public int kind() { return kind; }
    public boolean isEmpty() { return kind == KIND_EMPTY; }
    public boolean isItem() { return kind == KIND_ITEM; }

    public String kindName() {
        switch (kind) {
            case KIND_EMPTY: return "Empty";
            case KIND_ITEM: return "Item";
            case KIND_NON_ITEM: return "NonItem";
            case KIND_UNKNOWN_RAW: return "UnknownRaw";
            default: return "Unknown";
        }
    }
}
