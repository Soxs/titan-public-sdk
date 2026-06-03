package net.titan.api.events;

import net.titan.api.Player;

public final class PlayerDespawned {
    private final Player player;

    public PlayerDespawned(Player player) {
        this.player = player;
    }

    public Player player() { return player; }
}
