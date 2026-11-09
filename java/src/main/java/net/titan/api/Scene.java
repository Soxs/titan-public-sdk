package net.titan.api;

import java.util.Optional;

/** Live scene facade backed by generation-checked bulk scene captures. */
public final class Scene {
    private final Client client;
    private final WorldView worldView;

    Scene(Client client, WorldView worldView) {
        this.client = client;
        this.worldView = worldView;
    }

    public WorldView getWorldView() { return worldView; }
    public int getWorldViewId() { return worldView.getId(); }
    public long getGeneration() {
        return snapshot().orElseThrow(() -> new SceneUnavailableException(worldView.getId())).getGeneration();
    }
    public Optional<SceneSnapshot> snapshot() { return client.sceneSnapshot(worldView.getId()); }

    /**
     * Borrowed read-only grid indexed [plane][sceneX][sceneY]. Missing native
     * squares are null. Repeated reads reuse the grid while its generation is
     * unchanged. Retained tile/object handles revalidate against the current
     * scene; a proven replaced scene produces absent results. Do not
     * modify the arrays. An unavailable capture throws SceneUnavailableException
     * so a failed read cannot be mistaken for an empty scene. Use
     * {@link #snapshot()} explicitly when captured values are desired.
     */
    public Tile[][][] getTiles() { return client.sceneTiles(worldView.getId()); }
}
