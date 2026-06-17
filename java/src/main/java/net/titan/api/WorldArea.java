package net.titan.api;

import net.titan.api.internal.LineOfSight;

import java.util.Objects;

public final class WorldArea {
    private int x;
    private int y;
    private int width = 1;
    private int height = 1;
    private int plane;
    private int worldViewId = WorldView.CURRENT;

    public WorldArea() {}

    public WorldArea(int x, int y, int width, int height, int plane) {
        this(x, y, width, height, plane, WorldView.CURRENT);
    }

    public WorldArea(int x, int y, int width, int height, int plane, int worldViewId) {
        this.x = x;
        this.y = y;
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
        this.plane = plane;
        this.worldViewId = worldViewId;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int width() { return width; }
    public int height() { return height; }
    public int plane() { return plane; }
    public int worldViewId() { return worldViewId; }

    public boolean contains(WorldPoint point) {
        return point != null && point.z() == plane
            && WorldView.same(point.worldViewId(), worldViewId) && contains2D(point);
    }

    public boolean contains2D(WorldPoint point) {
        return point != null
            && WorldView.same(point.worldViewId(), worldViewId)
            && point.x() >= x && point.x() < x + width
            && point.y() >= y && point.y() < y + height;
    }

    public int distanceTo(WorldPoint point) {
        if (point == null || point.z() != plane || !WorldView.same(point.worldViewId(), worldViewId)) {
            return Integer.MAX_VALUE;
        }
        int dx = 0;
        int dy = 0;
        if (point.x() < x) dx = x - point.x();
        else if (point.x() >= x + width) dx = point.x() - (x + width - 1);
        if (point.y() < y) dy = y - point.y();
        else if (point.y() >= y + height) dy = point.y() - (y + height - 1);
        return Math.max(dx, dy);
    }

    public int distanceTo(WorldArea other) {
        if (other == null || other.plane != plane || !WorldView.same(other.worldViewId, worldViewId)) {
            return Integer.MAX_VALUE;
        }
        int dx = 0;
        int dy = 0;
        if (other.x + other.width <= x) dx = x - (other.x + other.width - 1);
        else if (other.x >= x + width) dx = other.x - (x + width - 1);
        if (other.y + other.height <= y) dy = y - (other.y + other.height - 1);
        else if (other.y >= y + height) dy = other.y - (y + height - 1);
        return Math.max(dx, dy);
    }

    public boolean isInMeleeDistance(WorldPoint point) {
        return point != null && isInMeleeDistance(
            new WorldArea(point.x(), point.y(), 1, 1, point.z(), point.worldViewId()));
    }

    public boolean isInMeleeDistance(WorldArea other) {
        if (other == null || other.plane != plane || !WorldView.same(other.worldViewId, worldViewId)) {
            return false;
        }
        int dx = 0;
        int dy = 0;
        if (other.x + other.width <= x) dx = x - (other.x + other.width - 1);
        else if (other.x >= x + width) dx = other.x - (x + width - 1);
        if (other.y + other.height <= y) dy = y - (other.y + other.height - 1);
        else if (other.y >= y + height) dy = other.y - (y + height - 1);
        return dx + dy == 1;
    }

    public boolean isInMeleeDistance(Locatable<?> other) {
        return other != null && isInMeleeDistance(other.worldArea());
    }

    public boolean hasLineOfSight(WorldPoint point) {
        return point != null && LineOfSight.hasLineOfSight(this, point.worldArea());
    }

    public boolean hasLineOfSight(WorldArea other) {
        return LineOfSight.hasLineOfSight(this, other);
    }

    public boolean hasLineOfSight(Locatable<?> other) {
        return other != null && hasLineOfSight(other.worldArea());
    }

    public WorldPoint center() {
        return new WorldPoint(x + width / 2, y + height / 2, plane, worldViewId);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof WorldArea)) return false;
        WorldArea other = (WorldArea) object;
        return x == other.x && y == other.y && width == other.width
            && height == other.height && plane == other.plane
            && WorldView.same(worldViewId, other.worldViewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height, plane, worldViewId);
    }

    @Override
    public String toString() {
        return "WorldArea{" + x + "," + y + "," + width + "x" + height
            + "," + plane + ",wv=" + worldViewId + "}";
    }
}
