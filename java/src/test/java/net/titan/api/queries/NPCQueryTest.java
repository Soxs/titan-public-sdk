package net.titan.api.queries;

import net.titan.api.EntityType;
import net.titan.api.NPC;
import net.titan.api.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NPCQueryTest {
    private static final int TARGET_HASH = 42;

    @Test
    void entityTypesMatchNativeInteractionAbi() {
        assertEquals(0, EntityType.LOCATION);
        assertEquals(1, EntityType.NPC);
        assertEquals(2, EntityType.PLAYER);
        assertEquals(EntityType.LOCATION, EntityType.TILE_OBJECT);
        assertEquals(0x7F, EntityType.NONE);
    }

    @Test
    void targetTypeClassifiesActors() {
        assertEquals(EntityType.PLAYER, NPCQuery.targetType(new Player()));
        assertEquals(EntityType.NPC, NPCQuery.targetType(new NPC()));
        assertEquals(EntityType.NONE, NPCQuery.targetType(null));
    }

    @Test
    void interactionTargetsMatchesModernSplitPlayerEncoding() {
        assertTrue(NPCQuery.interactionTargets(
            TARGET_HASH, EntityType.PLAYER, TARGET_HASH, EntityType.PLAYER));
        assertFalse(NPCQuery.interactionTargets(
            TARGET_HASH, EntityType.NPC, TARGET_HASH, EntityType.PLAYER));
    }

    @Test
    void interactionTargetsMatchesLegacyMissingTypePlayerEncoding() {
        assertTrue(NPCQuery.interactionTargets(
            TARGET_HASH, EntityType.NONE, TARGET_HASH, EntityType.PLAYER));
        assertTrue(NPCQuery.interactionTargets(
            TARGET_HASH + 65536, EntityType.NONE, TARGET_HASH, EntityType.PLAYER));
        assertFalse(NPCQuery.interactionTargets(
            TARGET_HASH + 1, EntityType.NONE, TARGET_HASH, EntityType.PLAYER));
    }
}
