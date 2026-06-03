package net.titan.api.events;

import net.titan.api.Player;

public final class PlayerSpawned {
    private final Player player;

    public PlayerSpawned(Player player) {
        this.player = player;
    }

    public Player player() { return player; }
}
