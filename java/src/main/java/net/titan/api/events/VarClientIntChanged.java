package net.titan.api.events;

/** Fired after a write to the integer client variable at this index. */
public final class VarClientIntChanged {
    private final int index;

    public VarClientIntChanged(int index) { this.index = index; }

    public int index() { return index; }
}
