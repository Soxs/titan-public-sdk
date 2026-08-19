package net.titan.api.internal;

import net.titan.api.WorldMapSnapshot;

import java.util.Optional;

/** Internal runtime bridge behind the public {@code Titan.worldMap()} facade. */
public interface WorldMapBackend {
    Optional<WorldMapSnapshot> snapshot();
}
