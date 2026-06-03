package net.titan.api;

public final class CollisionFlag {
    public static final int WALL_SE_CORNER = 0x00000001;
    public static final int WALL_SOUTH = 0x00000002;
    public static final int WALL_SW_CORNER = 0x00000004;
    public static final int WALL_WEST = 0x00000008;
    public static final int WALL_NW_CORNER = 0x00000010;
    public static final int WALL_NORTH = 0x00000020;
    public static final int WALL_NE_CORNER = 0x00000040;
    public static final int WALL_EAST = 0x00000080;
    public static final int BLOCK_FLOOR = 0x00000100;
    public static final int BLOCK_FLOOR_DECORATION = 0x00040000;
    public static final int BLOCK_OBJECT = 0x00200000;
    public static final int BLOCK_FULL = 0x01000000;

    public static final int BLOCK_MOVE = BLOCK_FULL | BLOCK_OBJECT
        | BLOCK_FLOOR_DECORATION | BLOCK_FLOOR;

    public static final int BLOCKED_WEST = BLOCK_MOVE | WALL_WEST;
    public static final int BLOCKED_EAST = BLOCK_MOVE | WALL_EAST;
    public static final int BLOCKED_SOUTH = BLOCK_MOVE | WALL_SOUTH;
    public static final int BLOCKED_NORTH = BLOCK_MOVE | WALL_NORTH;
    public static final int BLOCKED_SW = BLOCK_MOVE | WALL_SOUTH | WALL_SW_CORNER | WALL_WEST;
    public static final int BLOCKED_SE = BLOCK_MOVE | WALL_EAST | WALL_SOUTH | WALL_SE_CORNER;
    public static final int BLOCKED_NW = BLOCK_MOVE | WALL_NORTH | WALL_NW_CORNER | WALL_WEST;
    public static final int BLOCKED_NE = BLOCK_MOVE | WALL_EAST | WALL_NE_CORNER | WALL_NORTH;

    private CollisionFlag() {}

    public static boolean isBlocked(Client client, int plane, int x, int y, int dx, int dy) {
        if (client == null || (dx == 0 && dy == 0)) return false;

        int nx = x + dx;
        int ny = y + dy;
        if (dx != 0 && dy == 0) {
            int dest = client.collisionFlag(plane, nx, ny);
            return (dest & (dx < 0 ? BLOCKED_WEST : BLOCKED_EAST)) != 0;
        }
        if (dx == 0) {
            int dest = client.collisionFlag(plane, nx, ny);
            return (dest & (dy < 0 ? BLOCKED_SOUTH : BLOCKED_NORTH)) != 0;
        }

        int diag = client.collisionFlag(plane, nx, ny);
        int cardX = client.collisionFlag(plane, nx, y);
        int cardY = client.collisionFlag(plane, x, ny);
        int diagMask;
        int cardXMask;
        int cardYMask;
        if (dx < 0 && dy < 0) {
            diagMask = BLOCKED_SW;
            cardXMask = BLOCKED_WEST;
            cardYMask = BLOCKED_SOUTH;
        } else if (dx > 0 && dy < 0) {
            diagMask = BLOCKED_SE;
            cardXMask = BLOCKED_EAST;
            cardYMask = BLOCKED_SOUTH;
        } else if (dx < 0) {
            diagMask = BLOCKED_NW;
            cardXMask = BLOCKED_WEST;
            cardYMask = BLOCKED_NORTH;
        } else {
            diagMask = BLOCKED_NE;
            cardXMask = BLOCKED_EAST;
            cardYMask = BLOCKED_NORTH;
        }

        return (diag & diagMask) != 0
            || (cardX & cardXMask) != 0
            || (cardY & cardYMask) != 0;
    }
}
