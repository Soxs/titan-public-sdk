package net.titan.api;

import net.titan.api.internal.TitanRuntime;
import net.titan.api.utils.Magic;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface Actor extends Locatable<Actor> {
    long entityPtr();
    int hashIndex();
    int tileX();
    int tileY();
    int plane();
    int worldX();
    int worldY();
    default int worldViewId() { return WorldView.CURRENT; }
    default long worldViewPtr() { return 0L; }
    int preciseX();
    int preciseY();
    int orientation();
    int animation();
    int movementPose();
    int idlePose();
    int interactingIndex();
    int interactingType();
    int interactingPhase();
    int healthRatio();
    int healthScale();
    boolean hasHealthBar();

    default boolean exists() { return TitanRuntime.liveExists(this); }

    boolean castOn(Magic.SpellInfo spell);
    boolean castOn(Magic.Standard spell);
    boolean castOn(Magic.Ancient spell);
    boolean castOn(Magic.Lunar spell);
    boolean castOn(Magic.Necromancy spell);

    @Override
    default LocalPoint localPoint() {
        return new LocalPoint(preciseX(), preciseY(), worldViewId());
    }

    default boolean isAnimating() { return animation() != -1; }

    default boolean isStationary() { return movementPose() == idlePose(); }

    // NOTE: No generic interact(String) is declared here. The canonical
    // C++/TS SDKs only expose interaction on NPC / TileObject / item
    // surfaces -- there is no Player.interact -- so Actor deliberately
    // does not advertise an interact method that would be a silent no-op
    // for Player. NPC declares its own interact(String).

    default boolean isInteracting() {
        if (interactingPhase() == 0xFF) return interactingIndex() != -1 && interactingIndex() != 0;
        return interactingPhase() == 0 && interactingType() != EntityType.NONE && interactingIndex() != -1;
    }

    default Optional<Actor> interacting() {
        return TitanRuntime.resolveInteracting(interactingIndex(), interactingType(), worldViewId());
    }

    default Actor getInteracting() {
        return interacting().orElse(null);
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

    default List<ActorSpotAnim> currentSpotAnims() {
        return Collections.emptyList();
    }
}
