package net.titan.api.queries;

import net.titan.api.Actor;
import net.titan.api.Client;
import net.titan.api.HeadIcon;
import net.titan.api.Player;

public final class PlayerQuery extends NamedLocatableQuery<Player, PlayerQuery> {
    public PlayerQuery(Client client) {
        super(client, client == null ? null : client.players(), Player::name);
    }

    public PlayerQuery interactingWith(Actor target) {
        int targetHash = NPCQuery.targetHash(target);
        int targetType = NPCQuery.targetType(target);
        if (targetHash < 0) return this;
        return where(player -> player.isInteracting()
            && NPCQuery.interactionTargets(player.interactingIndex(), player.interactingType(),
                                           targetHash, targetType));
    }

    public PlayerQuery interactingWithLocal() {
        if (client == null) return this;
        return client.localPlayer().map(this::interactingWith).orElse(this);
    }

    public PlayerQuery notInteracting() {
        return where(player -> !player.isInteracting());
    }

    public PlayerQuery isAnimating() {
        return where(Player::isAnimating);
    }

    public PlayerQuery notAnimating() {
        return where(player -> !player.isAnimating());
    }

    public PlayerQuery animation(int animationId) {
        return where(player -> player.animation() == animationId);
    }

    public PlayerQuery isStationary() {
        return where(Player::isStationary);
    }

    public PlayerQuery isIdle() {
        return where(Player::isIdle);
    }

    public PlayerQuery isSkulled() {
        return where(Player::isSkulled);
    }

    public PlayerQuery overheadActive() {
        return where(Player::isOverheadActive);
    }

    public PlayerQuery overheadActive(HeadIcon icon) {
        return where(player -> player.isOverheadActive(icon));
    }

    public PlayerQuery combatLevelAbove(int minLevel) {
        return where(player -> player.combatLevel() >= minLevel);
    }

    public PlayerQuery combatLevelBelow(int maxLevel) {
        return where(player -> player.combatLevel() <= maxLevel);
    }

    public PlayerQuery combatLevelBetween(int low, int high) {
        int min = Math.min(low, high);
        int max = Math.max(low, high);
        return where(player -> player.combatLevel() >= min && player.combatLevel() <= max);
    }

    public PlayerQuery excludingSelf() {
        if (client == null) return this;
        long localPtr = client.localPlayer().map(Player::entityPtr).orElse(0L);
        return where(player -> player.entityPtr() != localPtr);
    }

    public PlayerQuery isDead() {
        return where(Player::isDead);
    }

    public PlayerQuery isAlive() {
        return where(player -> !player.isDead());
    }

    public PlayerQuery withHealthBar() {
        return where(Player::hasHealthBar);
    }

    public PlayerQuery noHealthBar() {
        return where(player -> !player.hasHealthBar());
    }

    public PlayerQuery healthPercentBelow(float threshold) {
        return where(player -> player.healthPercent() >= 0.0f && player.healthPercent() < threshold);
    }

    public PlayerQuery healthPercentAbove(float threshold) {
        return where(player -> player.healthPercent() >= 0.0f && player.healthPercent() > threshold);
    }
}
