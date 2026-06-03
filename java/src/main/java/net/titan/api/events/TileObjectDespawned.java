package net.titan.api.events;

import net.titan.api.TileObject;

public final class TileObjectDespawned {
    private final TileObject tileObject;

    public TileObjectDespawned(TileObject tileObject) {
        this.tileObject = tileObject;
    }

    public TileObject tileObject() { return tileObject; }
}
