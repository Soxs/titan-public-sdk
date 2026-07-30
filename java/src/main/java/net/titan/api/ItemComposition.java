package net.titan.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemComposition {
    private int id;
    private String name;
    private boolean stackable;
    private int linkedNoteId = -1;
    private List<String> inventoryActions;
    private boolean runtimeResolved;
    private List<List<String>> subOps;

    public int id() { return id; }
    public String name() { return name == null ? "" : name; }
    public boolean isStackable() { return stackable; }
    public int linkedNoteId() { return linkedNoteId; }
    public List<String> inventoryActions() {
        return inventoryActions == null ? Collections.emptyList() : Collections.unmodifiableList(inventoryActions);
    }
    /** Opcode-43 submenu labels. The outer list has five inventory-action
     * slots and each inner list has twenty submenu slots. Empty strings
     * preserve positional gaps. */
    public List<List<String>> subOps() {
        if (subOps == null || subOps.isEmpty()) return Collections.emptyList();
        List<List<String>> result = new ArrayList<>(subOps.size());
        for (List<String> row : subOps) {
            result.add(row == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(row));
        }
        return Collections.unmodifiableList(result);
    }
    public boolean isRuntimeResolved() { return runtimeResolved; }
}
