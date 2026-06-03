package net.titan.api;

public interface Locatable<T extends Locatable<T>> {
    int tileX();
    int tileY();
    int plane();
    int worldX();
    int worldY();

    default Tile tile() {
        return new Tile(tileX(), tileY(), plane());
    }

    default WorldPoint worldPoint() {
        return new WorldPoint(worldX(), worldY(), plane());
    }

    default LocalPoint localPoint() {
        return LocalPoint.fromScene(tileX(), tileY());
    }

    default WorldArea worldArea() {
        return new WorldArea(worldX(), worldY(), 1, 1, plane());
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
}
