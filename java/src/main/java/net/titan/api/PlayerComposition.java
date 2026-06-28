package net.titan.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class PlayerComposition {
    public static final int STATUS_UNAVAILABLE = 0;
    public static final int STATUS_AVAILABLE = 1;
    public static final int STATUS_MISSING_OFFSETS = 2;
    public static final int STATUS_NULL_PLAYER = 3;
    public static final int STATUS_NULL_MODEL = 4;
    public static final int STATUS_NPC_TRANSFORM = 5;
    public static final int STATUS_BAD_VECTOR = 6;

    private static final PlayerComposition UNAVAILABLE =
        new PlayerComposition(STATUS_UNAVAILABLE, -1, -1, Collections.emptyList());

    private final int status;
    private final int itemIdBase;
    private final int npcTransformId;
    private final List<PlayerCompositionSlot> slots;

    public PlayerComposition(int status, int itemIdBase, int npcTransformId,
                             List<PlayerCompositionSlot> slots) {
        this.status = status;
        this.itemIdBase = itemIdBase;
        this.npcTransformId = npcTransformId;
        this.slots = slots == null ? Collections.emptyList() : List.copyOf(slots);
    }

    public static PlayerComposition unavailable() { return UNAVAILABLE; }

    public int status() { return status; }
    public boolean available() { return status == STATUS_AVAILABLE; }
    public int itemIdBase() { return itemIdBase; }
    public int npcTransformId() { return npcTransformId; }
    public int slotCount() { return slots.size(); }
    public List<PlayerCompositionSlot> slots() { return slots; }
    public Optional<PlayerCompositionSlot> getSlot(int slotIndex) {
        for (PlayerCompositionSlot slot : slots) {
            if (slot.slotIndex() == slotIndex) return Optional.of(slot);
        }
        return Optional.empty();
    }
    public Optional<PlayerCompositionSlot> getSlot(EquipmentSlot slot) {
        return slot == null ? Optional.empty() : getSlot(slot.id());
    }

    public String statusName() {
        switch (status) {
            case STATUS_UNAVAILABLE: return "Unavailable";
            case STATUS_AVAILABLE: return "Available";
            case STATUS_MISSING_OFFSETS: return "MissingOffsets";
            case STATUS_NULL_PLAYER: return "NullPlayer";
            case STATUS_NULL_MODEL: return "NullModel";
            case STATUS_NPC_TRANSFORM: return "NpcTransform";
            case STATUS_BAD_VECTOR: return "BadVector";
            default: return "Unknown";
        }
    }
}
