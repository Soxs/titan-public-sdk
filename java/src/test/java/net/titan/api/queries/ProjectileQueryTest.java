package net.titan.api.queries;

import net.titan.api.Actor;
import net.titan.api.Camera;
import net.titan.api.Client;
import net.titan.api.EntityType;
import net.titan.api.NPC;
import net.titan.api.Player;
import net.titan.api.Projectile;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectileQueryTest {
    @Test
    void decodedProjectileFieldsExposeRawValueAndResolvePlayerTarget() {
        Player player = player(1854);
        Projectile projectile = projectile(
            -1, EntityType.NONE, 0,
            1854, EntityType.PLAYER, -1855);
        setObj(projectile, "client", client(List.of(projectile), List.of(player), List.of()));

        assertEquals(1854, projectile.targetEntity());
        assertEquals(-1855, projectile.rawTargetEntity());
        assertEquals(EntityType.PLAYER, projectile.targetEntityType());
        Optional<Actor> target = projectile.targetActor();
        assertTrue(target.isPresent());
        assertSame(player, target.get());
    }

    @Test
    void actorQueriesMatchBothTypeAndHash() {
        Player player = player(42);
        NPC npc = npc(42);
        Projectile playerTarget = projectile(-1, EntityType.NONE, 0, 42, EntityType.PLAYER, -43);
        Projectile npcTarget = projectile(-1, EntityType.NONE, 0, 42, EntityType.NPC, 43);
        Projectile npcSource = projectile(42, EntityType.NPC, 43, -1, EntityType.NONE, 0);
        Client client = client(List.of(playerTarget, npcTarget, npcSource), List.of(player), List.of(npc));

        assertEquals(List.of(playerTarget), new ProjectileQuery(client).targetingActor(player).toList());
        assertEquals(List.of(npcTarget), new ProjectileQuery(client).targetingActor(npc).toList());
        assertEquals(List.of(npcSource), new ProjectileQuery(client).fromActor(npc).toList());
    }

    private static Player player(int hashIndex) {
        Player player = new Player();
        setInt(player, "hashIndex", hashIndex);
        return player;
    }

    private static NPC npc(int hashIndex) {
        NPC npc = new NPC();
        setInt(npc, "hashIndex", hashIndex);
        return npc;
    }

    private static Projectile projectile(int sourceHash, int sourceType, int rawSource,
                                         int targetHash, int targetType, int rawTarget) {
        Projectile projectile = new Projectile();
        setInt(projectile, "sourceEntity", sourceHash);
        setInt(projectile, "sourceEntityType", sourceType);
        setInt(projectile, "rawSourceEntity", rawSource);
        setInt(projectile, "targetEntity", targetHash);
        setInt(projectile, "targetEntityType", targetType);
        setInt(projectile, "rawTargetEntity", rawTarget);
        return projectile;
    }

    private static Client client(List<Projectile> projectiles, List<Player> players, List<NPC> npcs) {
        return (Client) Proxy.newProxyInstance(
            ProjectileQueryTest.class.getClassLoader(),
            new Class<?>[] { Client.class },
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "projectiles":
                        return projectiles;
                    case "players":
                        return players;
                    case "npcs":
                        return npcs;
                    case "localPlayer":
                        return Optional.empty();
                    case "camera":
                        return Optional.<Camera>empty();
                    default:
                        return defaultValue(method.getReturnType());
                }
            });
    }

    private static Object defaultValue(Class<?> type) {
        if (type == Void.TYPE) return null;
        if (type == Boolean.TYPE) return false;
        if (type == Byte.TYPE) return (byte) 0;
        if (type == Short.TYPE) return (short) 0;
        if (type == Integer.TYPE) return 0;
        if (type == Long.TYPE) return 0L;
        if (type == Float.TYPE) return 0.0f;
        if (type == Double.TYPE) return 0.0d;
        if (type == Optional.class) return Optional.empty();
        if (type == OptionalInt.class) return OptionalInt.empty();
        if (type == OptionalLong.class) return OptionalLong.empty();
        if (type == List.class) return Collections.emptyList();
        return null;
    }

    private static void setInt(Object target, String fieldName, int value) {
        setObj(target, fieldName, value);
    }

    private static void setObj(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
