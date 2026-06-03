package net.titan.api.queries;

import net.titan.api.Actor;
import net.titan.api.Client;
import net.titan.api.Locatable;
import net.titan.api.LocalPoint;
import net.titan.api.NPC;
import net.titan.api.Player;
import net.titan.api.Tile;
import net.titan.api.WorldPoint;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public abstract class LocatableQuery<T extends Locatable<?>, Q extends LocatableQuery<T, Q>>
        extends Query<T, Q> {
    protected final Client client;

    protected LocatableQuery(Client client, Collection<T> source) {
        super(source);
        this.client = client;
    }

    public Q within(int radius, Tile origin) {
        if (origin == null) return clear();
        return where(item -> item.distanceTo(origin) <= radius);
    }

    public Q within(int radius, Actor origin) {
        return origin == null ? clear() : within(radius, origin.tile());
    }

    public Q within(int radius, Player origin) {
        return origin == null ? clear() : within(radius, origin.tile());
    }

    public Q within(int radius, NPC origin) {
        return origin == null ? clear() : within(radius, origin.tile());
    }

    public Q within(int radius, Locatable<?> origin) {
        return origin == null ? clear() : within(radius, origin.tile());
    }

    public Q within(int radius, WorldPoint origin) {
        Tile tile = sceneTile(origin);
        return tile == null ? clear() : within(radius, tile);
    }

    public Q within(int radius, LocalPoint origin) {
        if (origin == null) return clear();
        Tile tile = new Tile(origin.sceneX(), origin.sceneY(), 0);
        return where(item -> item.tile().distanceTo2D(tile) <= radius);
    }

    public Optional<T> nearestTo(Tile origin) {
        return nearestToTile(origin, true);
    }

    public Optional<T> nearestTo(Actor origin) {
        return origin == null ? Optional.empty() : nearestTo(origin.tile());
    }

    public Optional<T> nearestTo(Player origin) {
        return origin == null ? Optional.empty() : nearestTo(origin.tile());
    }

    public Optional<T> nearestTo(NPC origin) {
        return origin == null ? Optional.empty() : nearestTo(origin.tile());
    }

    public Optional<T> nearestTo(Locatable<?> origin) {
        return origin == null ? Optional.empty() : nearestTo(origin.tile());
    }

    public Optional<T> nearestTo(WorldPoint origin) {
        Tile tile = sceneTile(origin);
        return tile == null ? Optional.empty() : nearestTo(tile);
    }

    public Optional<T> nearestTo(LocalPoint origin) {
        return origin == null
            ? Optional.empty()
            : nearestToTile(new Tile(origin.sceneX(), origin.sceneY(), 0), false);
    }

    public Optional<T> nearest() {
        if (client == null) return Optional.empty();
        Optional<Player> local = client.localPlayer();
        return local.isPresent() ? nearestTo(local.get()) : Optional.empty();
    }

    public Q onTile(Tile tile) {
        if (tile == null) return clear();
        return where(item -> tile.equals(item.tile()));
    }

    public Q onTile(int x, int y) {
        return where(item -> item.tileX() == x && item.tileY() == y);
    }

    public Q atWorldPoint(WorldPoint point) {
        Tile tile = sceneTile(point);
        return tile == null ? clear() : onTile(tile);
    }

    public Q sortedByDistanceTo(Tile origin) {
        if (origin == null) return self();
        return sortBy(Comparator.comparingInt(item -> item.distanceTo(origin)));
    }

    public Q sortedByDistanceTo(Actor origin) {
        return origin == null ? self() : sortedByDistanceTo(origin.tile());
    }

    public Q sortedByDistanceTo(Player origin) {
        return origin == null ? self() : sortedByDistanceTo(origin.tile());
    }

    public Q sortedByDistanceTo(NPC origin) {
        return origin == null ? self() : sortedByDistanceTo(origin.tile());
    }

    public Q sortedByDistanceTo(Locatable<?> origin) {
        return origin == null ? self() : sortedByDistanceTo(origin.tile());
    }

    public Q sortedByDistanceTo(WorldPoint origin) {
        Tile tile = sceneTile(origin);
        return tile == null ? self() : sortedByDistanceTo(tile);
    }

    public Q sortedByDistanceTo(LocalPoint origin) {
        if (origin == null) return self();
        Tile tile = new Tile(origin.sceneX(), origin.sceneY(), 0);
        return sortBy(Comparator.comparingInt(item -> item.tile().distanceTo2D(tile)));
    }

    private Optional<T> nearestToTile(Tile origin, boolean includePlane) {
        if (origin == null) return Optional.empty();
        T best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (T item : items) {
            int distance = includePlane
                ? item.distanceTo(origin)
                : item.tile().distanceTo2D(origin);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = item;
            }
        }
        return Optional.ofNullable(best);
    }

    private Tile sceneTile(WorldPoint origin) {
        if (client == null || origin == null) return null;
        return new Tile(origin.x() - client.baseX(), origin.y() - client.baseY(), origin.z());
    }

    private Q clear() {
        items.clear();
        return self();
    }
}
