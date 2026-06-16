package net.titan.api;

import net.titan.api.internal.ActorResolver;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActorInteractingTest {
    private ActorResolver resolver;

    @AfterEach
    void clearResolver() {
        if (resolver != null) {
            TitanRuntime.clearActorResolver(resolver);
            resolver = null;
        }
    }

    @Test
    void interactingResolvesExplicitNpcTarget() {
        NPC target = npc(42);
        install(resolver(Collections.emptyList(), Collections.singletonList(target)));

        Player source = playerSource(42, EntityType.NPC);

        assertSame(target, source.interacting().orElseThrow(AssertionError::new));
        assertSame(target, source.getInteracting());
    }

    @Test
    void interactingResolvesExplicitPlayerTarget() {
        Player target = player(7);
        install(resolver(Collections.singletonList(target), Collections.emptyList()));

        NPC source = npcSource(7, EntityType.PLAYER);

        assertSame(target, source.interacting().orElseThrow(AssertionError::new));
        assertSame(target, source.getInteracting());
    }

    @Test
    void interactingResolvesLegacyMissingTypeNpcTarget() {
        NPC target = npc(19);
        install(resolver(Collections.emptyList(), Collections.singletonList(target)));

        Player source = playerSource(19, EntityType.NONE);

        assertSame(target, source.interacting().orElseThrow(AssertionError::new));
    }

    @Test
    void interactingResolvesLegacyMissingTypePlayerOffsetTarget() {
        Player target = player(88);
        install(resolver(Collections.singletonList(target), Collections.emptyList()));

        NPC source = npcSource(88 + 65536, EntityType.NONE);

        assertSame(target, source.interacting().orElseThrow(AssertionError::new));
    }

    @Test
    void interactingReturnsEmptyAndNullableAliasReturnsNullWhenUnresolved() {
        install((index, type) -> Optional.empty());

        Player source = playerSource(123, EntityType.NPC);

        assertTrue(source.interacting().isEmpty());
        assertNull(source.getInteracting());
    }

    private void install(ActorResolver value) {
        resolver = value;
        TitanRuntime.setActorResolver(value);
    }

    private static ActorResolver resolver(List<Player> players, List<NPC> npcs) {
        return (index, type) -> {
            if (type == EntityType.NPC) return findNpc(npcs, index).map(Actor.class::cast);
            if (type == EntityType.PLAYER) return findPlayer(players, index).map(Actor.class::cast);
            if (type != EntityType.NONE || index <= 0) return Optional.empty();
            Optional<NPC> npc = findNpc(npcs, index);
            if (npc.isPresent()) return npc.map(Actor.class::cast);
            return findPlayer(players, index)
                .or(() -> index >= 65536 ? findPlayer(players, index - 65536) : Optional.empty())
                .map(Actor.class::cast);
        };
    }

    private static Optional<Player> findPlayer(List<Player> players, int hashIndex) {
        for (Player player : players) {
            if (player.hashIndex() == hashIndex) return Optional.of(player);
        }
        return Optional.empty();
    }

    private static Optional<NPC> findNpc(List<NPC> npcs, int hashIndex) {
        for (NPC npc : npcs) {
            if (npc.hashIndex() == hashIndex) return Optional.of(npc);
        }
        return Optional.empty();
    }

    private static Player player(int hashIndex) {
        Player player = new Player();
        setInt(player, "hashIndex", hashIndex);
        return player;
    }

    private static Player playerSource(int interactingIndex, int interactingType) {
        Player player = new Player();
        setInt(player, "interactingIndex", interactingIndex);
        setInt(player, "interactingType", interactingType);
        return player;
    }

    private static NPC npc(int hashIndex) {
        NPC npc = new NPC();
        setInt(npc, "hashIndex", hashIndex);
        return npc;
    }

    private static NPC npcSource(int interactingIndex, int interactingType) {
        NPC npc = new NPC();
        setInt(npc, "interactingIndex", interactingIndex);
        setInt(npc, "interactingType", interactingType);
        return npc;
    }

    private static void setInt(Object target, String name, int value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.setInt(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(
                "Could not set " + target.getClass().getSimpleName() + "." + name, ex);
        }
    }
}
