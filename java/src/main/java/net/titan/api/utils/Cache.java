package net.titan.api.utils;

import net.titan.api.ItemDefinition;
import net.titan.api.NpcDefinition;
import net.titan.api.ObjectDefinition;
import net.titan.api.Titan;
import net.titan.api.VarbitDefinition;

import java.util.Optional;

/// Cache definition lookups. Mirrors the C++ {@code titan::state::cache()} facade.
public final class Cache {
    private Cache() {}

    public static Optional<ItemDefinition> item(int itemId) {
        return Titan.client().itemDefinition(itemId);
    }

    public static Optional<NpcDefinition> npc(int npcId) {
        return Titan.client().npcDefinition(npcId);
    }

    public static Optional<ObjectDefinition> obj(int objectId) {
        return Titan.client().objectDefinition(objectId);
    }

    public static Optional<VarbitDefinition> varbit(int varbitId) {
        return Titan.client().varbitDefinition(varbitId);
    }
}
