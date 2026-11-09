package net.titan.api;

public final class WorldView {
    public static final int CURRENT = -1;
    public static final int TOP_LEVEL = 0;
    public static final int TOPLEVEL = TOP_LEVEL;

    private final Client client;
    private final int id;
    private final Scene scene;

    /** Runtime facade constructor; prefer Client.getWorldView(id). */
    public WorldView(Client client, int id) {
        this.client = java.util.Objects.requireNonNull(client, "client");
        this.id = id;
        this.scene = new Scene(client, this);
    }

    public int getId() { return id; }
    public boolean isTopLevel() { return id == TOP_LEVEL; }
    public Scene getScene() { return scene; }
    public int getPlane() { return client.sceneSnapshot(id).map(SceneSnapshot::getPlane).orElse(-1); }
    public int getBaseX() { return client.sceneSnapshot(id).map(SceneSnapshot::getBaseX).orElse(0); }
    public int getBaseY() { return client.sceneSnapshot(id).map(SceneSnapshot::getBaseY).orElse(0); }
    public int getSizeX() { return client.sceneSnapshot(id).map(SceneSnapshot::getSizeX).orElse(0); }
    public int getSizeY() { return client.sceneSnapshot(id).map(SceneSnapshot::getSizeY).orElse(0); }

    @Override public boolean equals(Object value) {
        return value instanceof WorldView && ((WorldView) value).client == client
            && ((WorldView) value).id == id;
    }
    @Override public int hashCode() { return 31 * System.identityHashCode(client) + id; }

    public static boolean same(int a, int b) {
        return a == b || a == CURRENT || b == CURRENT;
    }
}
