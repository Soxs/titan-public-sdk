package net.titan.api.events;

import net.titan.api.NPC;

public final class NpcSpawned {
    private final NPC npc;

    public NpcSpawned(NPC npc) {
        this.npc = npc;
    }

    public NPC npc() { return npc; }
}
