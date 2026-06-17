package net.titan.api.internal;

import net.titan.api.Client;
import net.titan.api.Titan;
import net.titan.api.WorldArea;
import net.titan.api.WorldPoint;
import net.titan.api.WorldView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class LineOfSight {
    private static final int BLOCK_LINE_OF_SIGHT_NORTH = 0x00000400;
    private static final int BLOCK_LINE_OF_SIGHT_EAST = 0x00001000;
    private static final int BLOCK_LINE_OF_SIGHT_SOUTH = 0x00004000;
    private static final int BLOCK_LINE_OF_SIGHT_WEST = 0x00010000;
    private static final int BLOCK_LINE_OF_SIGHT_FULL = 0x00020000;

    private LineOfSight() {}

    public static boolean hasLineOfSight(WorldPoint from, WorldPoint to) {
        return from != null && to != null
            && hasLineOfSight(from.worldArea(), to.worldArea());
    }

    public static boolean hasLineOfSight(WorldArea fromRaw, WorldArea toRaw) {
        if (fromRaw == null || toRaw == null) return false;
        Client client = Titan.client();
        Area from = normalized(fromRaw);
        Area to = normalized(toRaw);
        if (!sameWorldView(from.worldViewId, to.worldViewId, client)) return false;
        if (from.plane != to.plane) return false;
        if (client.sceneSizeX() <= 0 || client.sceneSizeY() <= 0) return false;
        if (!areaInScene(from, client) || !areaInScene(to, client)) return false;
        if (intersects(from, to, client)) return true;

        List<Point> fromCandidates = visibleCandidates(to, from, client);
        List<Point> toCandidates = visibleCandidates(from, to, client);

        for (Point source : fromCandidates) {
            int sx = source.x - client.baseX();
            int sy = source.y - client.baseY();
            if (!inScene(sx, sy, client)) continue;

            for (Point target : toCandidates) {
                int tx = target.x - client.baseX();
                int ty = target.y - client.baseY();
                if (!inScene(tx, ty, client)) continue;
                if (hasLineOfSightTile(client, from.plane, sx, sy, tx, ty)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Area normalized(WorldArea area) {
        return new Area(area.x(), area.y(), Math.max(1, area.width()),
            Math.max(1, area.height()), area.plane(), area.worldViewId());
    }

    private static boolean sameWorldView(int a, int b, Client client) {
        if (a == b) return true;
        return resolveWorldViewId(a, client) == resolveWorldViewId(b, client);
    }

    private static int resolveWorldViewId(int worldViewId, Client client) {
        return worldViewId == WorldView.CURRENT ? client.currentWorldViewId() : worldViewId;
    }

    private static boolean intersects(Area a, Area b, Client client) {
        return sameWorldView(a.worldViewId, b.worldViewId, client)
            && a.plane == b.plane
            && a.x < b.x + b.width && b.x < a.x + a.width
            && a.y < b.y + b.height && b.y < a.y + a.height;
    }

    private static int chebyshev(Point a, Point b) {
        if (a.plane != b.plane) return Integer.MAX_VALUE;
        return Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y));
    }

    private static Point comparisonPoint(Area area, Area other) {
        int cx = area.x;
        int cy = area.y;

        if (other.x <= area.x) {
            cx = area.x;
        } else if (other.x >= area.x + area.width - 1) {
            cx = area.x + area.width - 1;
        } else {
            cx = other.x;
        }

        if (other.y <= area.y) {
            cy = area.y;
        } else if (other.y >= area.y + area.height - 1) {
            cy = area.y + area.height - 1;
        } else {
            cy = other.y;
        }

        return new Point(cx, cy, area.plane);
    }

    private static boolean isEdgePoint(Area area, Point point) {
        return point.x == area.x || point.x == area.x + area.width - 1
            || point.y == area.y || point.y == area.y + area.height - 1;
    }

    private static boolean isVisibleCandidate(Area viewer, Area subject,
                                              Point point, Client client) {
        if (intersects(viewer, subject, client)) return false;

        int viewerMaxX = viewer.x + viewer.width - 1;
        int viewerMaxY = viewer.y + viewer.height - 1;
        int subjectMaxX = subject.x + subject.width - 1;
        int subjectMaxY = subject.y + subject.height - 1;

        if (viewerMaxX > subjectMaxX) {
            if (viewer.y < subject.y) return point.x == subjectMaxX || point.y == subject.y;
            if (viewerMaxY > subjectMaxY) return point.x == subjectMaxX || point.y == subjectMaxY;
            return point.x == subjectMaxX;
        }

        if (viewer.x < subject.x) {
            if (viewer.y < subject.y) return point.x == subject.x || point.y == subject.y;
            if (viewerMaxY > subjectMaxY) return point.x == subject.x || point.y == subjectMaxY;
            return point.x == subject.x;
        }

        if (viewer.y > subjectMaxY) return point.y == subjectMaxY;
        if (viewer.y < subject.y) return point.y == subject.y;
        return false;
    }

    private static List<Point> visibleCandidates(Area viewer, Area subject,
                                                 Client client) {
        Point comparison = comparisonPoint(viewer, subject);
        List<Point> out = new ArrayList<>();
        for (int dx = 0; dx < subject.width; dx++) {
            for (int dy = 0; dy < subject.height; dy++) {
                Point point = new Point(subject.x + dx, subject.y + dy, subject.plane);
                if (!isEdgePoint(subject, point)) continue;
                if (!isVisibleCandidate(viewer, subject, point, client)) continue;
                out.add(point);
            }
        }
        out.sort(Comparator.comparingInt(point -> chebyshev(point, comparison)));
        return out;
    }

    private static boolean areaInScene(Area area, Client client) {
        int sx = area.x - client.baseX();
        int sy = area.y - client.baseY();
        return sx >= 0 && sy >= 0
            && area.width <= client.sceneSizeX() - sx
            && area.height <= client.sceneSizeY() - sy;
    }

    private static boolean inScene(int x, int y, Client client) {
        return x >= 0 && y >= 0 && x < client.sceneSizeX() && y < client.sceneSizeY();
    }

    private static boolean blockedAt(Client client, int plane, int x, int y, int mask) {
        if (!inScene(x, y, client)) return true;
        return (client.collisionFlag(plane, x, y) & mask) != 0;
    }

    private static boolean hasLineOfSightTile(Client client, int plane,
                                              int fromX, int fromY,
                                              int toX, int toY) {
        if (plane < 0 || plane > 3) return false;
        if (!inScene(fromX, fromY, client) || !inScene(toX, toY, client)) return false;
        if (fromX == toX && fromY == toY) return true;

        int dx = toX - fromX;
        int dy = toY - fromY;
        int dxAbs = Math.abs(dx);
        int dyAbs = Math.abs(dy);
        int xFlags = BLOCK_LINE_OF_SIGHT_FULL
            | (dx < 0 ? BLOCK_LINE_OF_SIGHT_EAST : BLOCK_LINE_OF_SIGHT_WEST);
        int yFlags = BLOCK_LINE_OF_SIGHT_FULL
            | (dy < 0 ? BLOCK_LINE_OF_SIGHT_NORTH : BLOCK_LINE_OF_SIGHT_SOUTH);

        if (dxAbs > dyAbs) {
            int x = fromX;
            int yBig = fromY << 16;
            int slope = (dy << 16) / dxAbs;
            yBig += 0x8000;
            if (dy < 0) yBig--;
            int direction = dx < 0 ? -1 : 1;

            while (x != toX) {
                x += direction;
                int y = yBig >> 16;
                if (blockedAt(client, plane, x, y, xFlags)) return false;
                yBig += slope;
                int nextY = yBig >> 16;
                if (nextY != y && blockedAt(client, plane, x, nextY, yFlags)) return false;
            }
        } else {
            int y = fromY;
            int xBig = fromX << 16;
            int slope = (dx << 16) / dyAbs;
            xBig += 0x8000;
            if (dx < 0) xBig--;
            int direction = dy < 0 ? -1 : 1;

            while (y != toY) {
                y += direction;
                int x = xBig >> 16;
                if (blockedAt(client, plane, x, y, yFlags)) return false;
                xBig += slope;
                int nextX = xBig >> 16;
                if (nextX != x && blockedAt(client, plane, nextX, y, xFlags)) return false;
            }
        }

        return true;
    }

    private static final class Area {
        final int x;
        final int y;
        final int width;
        final int height;
        final int plane;
        final int worldViewId;

        Area(int x, int y, int width, int height, int plane, int worldViewId) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.plane = plane;
            this.worldViewId = worldViewId;
        }
    }

    private static final class Point {
        final int x;
        final int y;
        final int plane;

        Point(int x, int y, int plane) {
            this.x = x;
            this.y = y;
            this.plane = plane;
        }
    }
}
