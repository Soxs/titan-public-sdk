package net.titan.api;

import java.util.Collections;
import java.util.List;

public interface Actor {
    long entityPtr();
    int hashIndex();
    int tileX();
    int tileY();
    int plane();
    int worldX();
    int worldY();
    int preciseX();
    int preciseY();
    int orientation();
    int animation();
    int interactingIndex();
    int interactingType();
    int interactingPhase();
    int healthRatio();
    int healthScale();
    boolean hasHealthBar();

    default boolean isAnimating() { return animation() != -1; }

    default boolean isInteracting() {
        if (interactingPhase() == 0xFF) return interactingIndex() != -1 && interactingIndex() != 0;
        return interactingPhase() == 0 && interactingType() != EntityType.NONE && interactingIndex() != -1;
    }

    default float healthPercent() {
        if (healthRatio() < 0 || healthScale() <= 0) return -1.0f;
        return (float) healthRatio() / (float) healthScale();
    }

    default boolean isDead() {
        return hasHealthBar() && healthScale() > 0 && healthRatio() == 0;
    }

    default List<WorldPoint> pathQueue() {
        return Collections.emptyList();
    }
}
