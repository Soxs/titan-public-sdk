package net.titan.api.events;

public final class GameTick {
    private final int tickCount;

    public GameTick(int tickCount) {
        this.tickCount = tickCount;
    }

    public int tickCount() { return tickCount; }
}
