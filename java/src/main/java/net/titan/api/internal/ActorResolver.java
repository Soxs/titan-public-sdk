package net.titan.api.internal;

import net.titan.api.Actor;

import java.util.Optional;

public interface ActorResolver {
    Optional<Actor> resolveInteracting(int interactingIndex, int interactingType);

    default Optional<Actor> resolveInteracting(int interactingIndex,
                                               int interactingType,
                                               int worldViewId) {
        return resolveInteracting(interactingIndex, interactingType);
    }
}
