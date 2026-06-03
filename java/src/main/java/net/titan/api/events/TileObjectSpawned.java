package net.titan.api.events;

import net.titan.api.TileObject;

public final class TileObjectSpawned {
    private final TileObject tileObject;

    public TileObjectSpawned(TileObject tileObject) {
        this.tileObject = tileObject;
    }

    public TileObject tileObject() { return tileObject; }
}
