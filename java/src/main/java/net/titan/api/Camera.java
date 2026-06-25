package net.titan.api;

import net.titan.api.internal.TitanRuntime;

public final class Camera {
    private boolean liveHandle = true;
    private int posX;
    private int posY;
    private int posZ;
    private int yaw;
    private int pitch;
    private int viewportW;
    private int viewportH;
    private int zoom;
    private boolean valid;

    private Camera live() { return TitanRuntime.currentLive(this); }

    public int posX() { return live().posX; }
    public int posY() { return live().posY; }
    public int posZ() { return live().posZ; }
    public int yaw() { return live().yaw; }
    public int pitch() { return live().pitch; }
    public int viewportW() { return live().viewportW; }
    public int viewportH() { return live().viewportH; }
    public int zoom() { return live().zoom; }
    public boolean isValid() { return live().valid; }
    public boolean exists() { return TitanRuntime.liveExists(this); }
    public Camera snapshot() { return TitanRuntime.snapshotLive(this); }
}
