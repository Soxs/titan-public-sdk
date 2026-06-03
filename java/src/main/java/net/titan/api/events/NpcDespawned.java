package net.titan.api.events;

import net.titan.api.NPC;

public final class NpcDespawned {
    private final NPC npc;

    public NpcDespawned(NPC npc) {
        this.npc = npc;
    }

    public NPC npc() { return npc; }
}
