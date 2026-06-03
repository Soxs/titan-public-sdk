package net.titan.api.events;

import net.titan.api.Npc;

public final class NpcSpawned {
    private final Npc npc;

    public NpcSpawned(Npc npc) {
        this.npc = npc;
    }

    public Npc npc() { return npc; }
}
