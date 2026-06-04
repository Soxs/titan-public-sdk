package net.titan.api;

import java.util.Collections;
import java.util.List;

/// Cache object (loc) definition snapshot. Mirrors the C++ {@code titan::ObjDef}
/// returned by {@code state::cache().obj(id)}.
public final class ObjectDefinition {
    private int id;
    private String name;
    private List<String> actions;
    private int sizeX = 1;
    private int sizeY = 1;
    private boolean blocksMovement;
    private int transformVarbit = -1;
    private int transformVarp = -1;
    private int transformDefault = -1;

    public int id() { return id; }
    public String name() { return name == null ? "" : name; }
    public List<String> actions() {
        return actions == null ? Collections.emptyList() : Collections.unmodifiableList(actions);
    }
    public int sizeX() { return sizeX; }
    public int sizeY() { return sizeY; }
    public boolean blocksMovement() { return blocksMovement; }
    public int transformVarbit() { return transformVarbit; }
    public int transformVarp() { return transformVarp; }
    public int transformDefault() { return transformDefault; }
}
