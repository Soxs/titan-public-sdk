package net.titan.api.queries;

import net.titan.api.Actor;
import net.titan.api.Client;
import net.titan.api.EntityType;
import net.titan.api.HeadIcon;
import net.titan.api.NPC;
import net.titan.api.Player;

public final class NPCQuery extends NamedLocatableQuery<NPC, NPCQuery> {
    public NPCQuery(Client client) {
        super(client, client == null ? null : client.npcs(), NPC::name);
    }

    public NPCQuery id(int npcId) {
        return where(npc -> npc.id() == npcId);
    }

    public NPCQuery ids(int... ids) {
        return where(npc -> contains(ids, npc.id()));
    }

    public NPCQuery hasAction(String action) {
        return where(npc -> npc.hasAction(action));
    }

    public NPCQuery notTargetedByOtherPlayers() {
        if (client == null) return this;
        long localPtr = client.localPlayer().map(Player::entityPtr).orElse(0L);
        return where(npc -> {
            for (Player player : client.players()) {
                if (player.entityPtr() == localPtr) continue;
                if (player.isInteracting()
                        && interactionTargets(player.interactingIndex(), player.interactingType(),
                                              npc.hashIndex(), EntityType.NPC)) {
                    return false;
                }
            }
            return true;
        });
    }

    public NPCQuery interactingWith(Actor target) {
        int targetHash = targetHash(target);
        int targetType = targetType(target);
        if (targetHash < 0) return this;
        return where(npc -> npc.isInteracting()
            && interactionTargets(npc.interactingIndex(), npc.interactingType(), targetHash, targetType));
    }

    public NPCQuery interactingWithLocal() {
        if (client == null) return this;
        return client.localPlayer().map(this::interactingWith).orElse(this);
    }

    public NPCQuery notInteracting() {
        return where(npc -> !npc.isInteracting());
    }

    public NPCQuery isAnimating() {
        return where(NPC::isAnimating);
    }

    public NPCQuery notAnimating() {
        return where(npc -> !npc.isAnimating());
    }

    public NPCQuery animation(int animationId) {
        return where(npc -> npc.animation() == animationId);
    }

    public NPCQuery isStationary() {
        return where(NPC::isStationary);
    }

    public NPCQuery overheadActive() {
        return where(NPC::isOverheadActive);
    }

    public NPCQuery overheadActive(HeadIcon icon) {
        return where(npc -> npc.isOverheadActive(icon));
    }

    public NPCQuery overrideTransform(int transformId) {
        return where(npc -> npc.overrideTransform() == transformId);
    }

    public NPCQuery sizeEquals(int size) {
        return where(npc -> npc.sizeX() == size && npc.sizeY() == size);
    }

    public NPCQuery isDead() {
        return where(NPC::isDead);
    }

    public NPCQuery isAlive() {
        return where(npc -> !npc.isDead());
    }

    public NPCQuery withHealthBar() {
        return where(NPC::hasHealthBar);
    }

    public NPCQuery noHealthBar() {
        return where(npc -> !npc.hasHealthBar());
    }

    public NPCQuery healthPercentBelow(float threshold) {
        return where(npc -> npc.healthPercent() >= 0.0f && npc.healthPercent() < threshold);
    }

    public NPCQuery healthPercentAbove(float threshold) {
        return where(npc -> npc.healthPercent() >= 0.0f && npc.healthPercent() > threshold);
    }

    static boolean contains(int[] values, int needle) {
        if (values == null) return false;
        for (int value : values) {
            if (value == needle) return true;
        }
        return false;
    }

    static boolean interactionTargets(int index, int type, int targetHash, int targetType) {
        if (type == EntityType.NONE) {
            if (targetType == EntityType.NPC) return index == targetHash;
            if (targetType == EntityType.PLAYER) return index == targetHash || index == targetHash + 65536;
            return false;
        }
        return index == targetHash && type == targetType;
    }

    static int targetHash(Actor target) {
        return target == null ? -1 : target.hashIndex();
    }

    static int targetType(Actor target) {
        if (target instanceof NPC) return EntityType.NPC;
        if (target instanceof Player) return EntityType.PLAYER;
        return EntityType.NONE;
    }
}
