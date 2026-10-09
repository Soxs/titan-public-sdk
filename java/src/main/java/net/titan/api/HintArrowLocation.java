package net.titan.api;

import java.util.Optional;

/** Copied coordinate-arrow position, with an optional host-resolved world point. */
public final class HintArrowLocation {
    private final int tileX, tileY, subX, subY, height;
    private final WorldPoint worldPoint;
    public HintArrowLocation(int tileX, int tileY, int subX, int subY, int height) {
        this(tileX, tileY, subX, subY, height, null);
    }
    public HintArrowLocation(int tileX, int tileY, int subX, int subY, int height, WorldPoint worldPoint) {
        this.tileX = tileX; this.tileY = tileY; this.subX = subX; this.subY = subY; this.height = height;
        this.worldPoint = worldPoint != null && worldPoint.x() == tileX && worldPoint.y() == tileY
                && worldPoint.z() >= 0 && worldPoint.z() <= 3 && worldPoint.worldViewId() >= 0
            ? new WorldPoint(worldPoint.x(), worldPoint.y(), worldPoint.z(), worldPoint.worldViewId()) : null;
    }
    public int tileX() { return tileX; }
    public int tileY() { return tileY; }
    /** Offset within the tile, in 1/128-tile units; an edge may be 128. */
    public int subX() { return subX; }
    public int subY() { return subY; }
    /** Raw native arrow height, not a plane. */
    public int height() { return height; }
    /** SDK 149+: cached tile, selected WorldView and its plane; empty when the host cannot resolve them. */
    public Optional<WorldPoint> worldPoint() { return Optional.ofNullable(worldPoint); }
}
