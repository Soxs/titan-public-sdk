package net.titan.api;

import java.util.Collections;
import java.util.List;

public final class ItemComposition {
    private int id;
    private String name;
    private boolean stackable;
    private int linkedNoteId = -1;
    private List<String> inventoryActions;
    private boolean runtimeResolved;

    public int id() { return id; }
    public String name() { return name == null ? "" : name; }
    public boolean isStackable() { return stackable; }
    public int linkedNoteId() { return linkedNoteId; }
    public List<String> inventoryActions() {
        return inventoryActions == null ? Collections.emptyList() : Collections.unmodifiableList(inventoryActions);
    }
    public boolean isRuntimeResolved() { return runtimeResolved; }
}
