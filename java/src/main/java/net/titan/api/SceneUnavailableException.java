package net.titan.api;

/** A current complete scene cannot be read; retry on the client/game thread. */
public final class SceneUnavailableException extends IllegalStateException {
    public SceneUnavailableException(int worldViewId) {
        super("A complete current scene is unavailable for world view " + worldViewId
            + "; read the scene on the client/game thread");
    }
}
