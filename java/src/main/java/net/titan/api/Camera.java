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
    // SDK 109: live UI-frame -> physical interface-scale factor, encoded on the
    // wire as integer milli-units (1000 = 100% = 1.0x), plus the canvas origin
    // (fixed-mode pillarbox/letterbox offset in physical pixels). Falls back to
    // identity (1000 / 0) when the analyzer didn't detect the canvas transform.
    private int interfaceScaleMilliX = 1000;
    private int interfaceScaleMilliY = 1000;
    private int canvasOriginX;
    private int canvasOriginY;

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

    /**
     * Live horizontal interface-scale factor: the ratio of physical (rendered)
     * UI-frame pixels to widget-frame units. 1.0 at 100% in-game interface
     * scaling, ~1.5 at 150%, ~2.0 at 200%; independent of Windows display
     * scaling. Multiply a plugin's own fixed-pixel geometry by this to line it
     * up with the host-positioned widget overlays. Returns 1.0 when the host
     * predates SDK 109 or the analyzer didn't detect the canvas transform.
     */
    public float interfaceScaleX() { return live().interfaceScaleMilliX / 1000.0f; }
    /** Live vertical interface-scale factor. See {@link #interfaceScaleX()}. */
    public float interfaceScaleY() { return live().interfaceScaleMilliY / 1000.0f; }
    /** Physical-pixel canvas origin X (fixed-mode pillarbox offset). SDK 109. */
    public int canvasOriginX() { return live().canvasOriginX; }
    /** Physical-pixel canvas origin Y (fixed-mode letterbox offset). SDK 109. */
    public int canvasOriginY() { return live().canvasOriginY; }
    public boolean exists() { return TitanRuntime.liveExists(this); }
    public Camera snapshot() { return TitanRuntime.snapshotLive(this); }
}
