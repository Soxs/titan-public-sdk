package net.titan.api;

import java.util.Collections;
import java.util.List;

/// Cache item definition snapshot. Mirrors the C++ {@code titan::ItemDef}
/// returned by {@code state::cache().item(id)}. Distinct from
/// {@link ItemComposition}, which is the runtime ITEM_DEF_LOOKUP result.
public final class ItemDefinition {
    private int id;
    private String name;
    private boolean members;
    private boolean stackable;
    private boolean noted;
    private int noteId = -1;
    /** Other item id in the note pair, or -1 when there is no note pair. */
    private int linkedId = -1;
    private List<String> inventoryActions;
    private List<String> groundActions;

    public int id() { return id; }
    public String name() { return name == null ? "" : name; }
    public boolean isMembers() { return members; }
    public boolean isStackable() { return stackable; }
    public boolean isNoted() { return noted; }
    public int noteId() { return noteId; }
    public int linkedId() { return linkedId; }
    public List<String> inventoryActions() {
        return inventoryActions == null ? Collections.emptyList() : Collections.unmodifiableList(inventoryActions);
    }
    public List<String> groundActions() {
        return groundActions == null ? Collections.emptyList() : Collections.unmodifiableList(groundActions);
    }
}
