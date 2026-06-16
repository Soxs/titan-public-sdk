package net.titan.api;

public final class WorldView {
    public static final int CURRENT = -1;
    public static final int TOP_LEVEL = 0;

    private WorldView() {}

    public static boolean same(int a, int b) {
        return a == b || a == CURRENT || b == CURRENT;
    }
}
