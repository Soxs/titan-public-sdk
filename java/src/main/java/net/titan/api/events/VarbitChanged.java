package net.titan.api.events;

public final class VarbitChanged {
    private final int varbitId;
    private final int oldValue;
    private final int newValue;
    private final int gameTick;

    public VarbitChanged(int varbitId, int oldValue, int newValue, int gameTick) {
        this.varbitId = varbitId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.gameTick = gameTick;
    }

    public int varbitId() { return varbitId; }
    public int oldValue() { return oldValue; }
    public int newValue() { return newValue; }
    public int gameTick() { return gameTick; }
    public int delta() { return newValue - oldValue; }
}
