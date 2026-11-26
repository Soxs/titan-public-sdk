package net.titan.api.events;

/** Fired after a write to the string client variable at this index. */
public final class VarClientStrChanged {
    private final int index;

    public VarClientStrChanged(int index) { this.index = index; }

    public int index() { return index; }
}
