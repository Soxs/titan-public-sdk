package net.titan.api.events;

import net.titan.api.LoginGameState;

public final class GameStateChanged {
    private final LoginGameState oldState;
    private final LoginGameState newState;
    private final int tickCount;

    public GameStateChanged(int oldState, int newState, int tickCount) {
        this.oldState = LoginGameState.fromId(oldState);
        this.newState = LoginGameState.fromId(newState);
        this.tickCount = tickCount;
    }

    public LoginGameState oldState() { return oldState; }
    public LoginGameState newState() { return newState; }
    public int tickCount() { return tickCount; }
}
