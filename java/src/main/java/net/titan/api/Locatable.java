package net.titan.api;

import java.util.Optional;

public interface Locatable<T extends Locatable<T>> {
    int tileX();
    int tileY();
    int plane();
    int worldX();
    int worldY();
    default int worldViewId() { return WorldView.CURRENT; }
    default long worldViewPtr() { return 0L; }

    default Tile tile() {
        return new Tile(tileX(), tileY(), plane(), worldViewId());
    }

    default WorldPoint worldPoint() {
        return new WorldPoint(worldX(), worldY(), plane(), worldViewId());
    }

    default Optional<WorldPoint> fromLocalInstance() {
        return worldPoint().fromLocalInstance();
    }

    default Optional<WorldPoint> toLocalInstance() {
        return worldPoint().toLocalInstance();
    }

    default LocalPoint localPoint() {
        return LocalPoint.fromScene(tileX(), tileY(), worldViewId());
    }

    default WorldArea worldArea() {
        return new WorldArea(worldX(), worldY(), 1, 1, plane(), worldViewId());
    }

    default int distanceTo(Tile other) {
        return tile().distanceTo(other);
    }

    default int distanceTo(WorldPoint other) {
        return worldPoint().distanceTo(other);
    }

    default int distanceTo(Locatable<?> other) {
        return other == null ? Integer.MAX_VALUE : tile().distanceTo(other.tile());
    }

    default boolean isInMeleeDistance(WorldPoint other) {
        return worldArea().isInMeleeDistance(other);
    }

    default boolean isInMeleeDistance(WorldArea other) {
        return worldArea().isInMeleeDistance(other);
    }

    default boolean isInMeleeDistance(Locatable<?> other) {
        return other != null && worldArea().isInMeleeDistance(other.worldArea());
    }

    default boolean hasLineOfSight(WorldPoint other) {
        return worldArea().hasLineOfSight(other);
    }

    default boolean hasLineOfSight(WorldArea other) {
        return worldArea().hasLineOfSight(other);
    }

    default boolean hasLineOfSight(Locatable<?> other) {
        return other != null && worldArea().hasLineOfSight(other.worldArea());
    }

    /**
     * True when the local player can walk onto one of this entity's tiles, or onto a
     * tile cardinally adjacent to them from which it can be interacted with (see
     * {@link net.titan.api.utils.BFS#reachableAdjacentTiles}). Uses the per-tick
     * reachability map. Added in Java SDK 0.1.84.
     */
    default boolean isReachable() {
        return net.titan.api.utils.BFS.isReachable(worldArea());
    }
}
