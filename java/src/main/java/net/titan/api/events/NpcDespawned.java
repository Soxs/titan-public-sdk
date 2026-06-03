package net.titan.api.events;

import net.titan.api.Npc;

public final class NpcDespawned {
    private final Npc npc;

    public NpcDespawned(Npc npc) {
        this.npc = npc;
    }

    public Npc npc() { return npc; }
}
