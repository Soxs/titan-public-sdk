package net.titan.api;

import java.util.Collections;
import java.util.List;

/// Cache NPC definition snapshot. Mirrors the C++ {@code titan::NpcDef}
/// returned by {@code state::cache().npc(id)}.
public final class NpcDefinition {
    private int id;
    private String name;
    private int combatLevel;
    private int size = 1;
    private List<String> actions;
    private int transformVarbit = -1;
    private int transformVarp = -1;
    private int transformDefault = -1;

    public int id() { return id; }
    public String name() { return name == null ? "" : name; }
    public int combatLevel() { return combatLevel; }
    public int size() { return size; }
    public List<String> actions() {
        return actions == null ? Collections.emptyList() : Collections.unmodifiableList(actions);
    }
    public int transformVarbit() { return transformVarbit; }
    public int transformVarp() { return transformVarp; }
    public int transformDefault() { return transformDefault; }
}
