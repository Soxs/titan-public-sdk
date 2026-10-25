package net.titan.api;

import net.titan.api.internal.TitanRuntime;

import java.util.Optional;

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
    /** Compass heading in 16384ths of a turn, [0, 16384). */
    public int yaw() { return live().yaw; }
    /**
     * Vertical tilt in 16384ths of a turn: [0x400, 0xBF8] normally, [1, 0x1040]
     * with {@link #pitchUnlocked()}; 0x1000 looks straight down. Hosts before
     * SDK 162 reported yaw and pitch the other way round.
     */
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

    // SDK 162 camera controls. They live on the client rather than in this
    // snapshot, so every Camera (snapshot or live) reads and writes them live.

    /**
     * Jagex's unlockCam flag: false keeps pitch in [0x400, 0xBF8] (about
     * 22.5-67.3 degrees), true allows [1, 0x1040] (0x1000 is straight down).
     * Empty when the host or revision lacks it. Any thread.
     */
    public Optional<Boolean> pitchUnlocked() { return Optional.ofNullable(TitanRuntime.cameraPitchUnlocked()); }

    /**
     * Writes the flag; the renderer applies the range next frame and the server
     * can also change it. Game thread only; true when the host wrote it.
     */
    public boolean setPitchUnlocked(boolean unlocked) { return TitanRuntime.setCameraPitchUnlocked(unlocked); }

    /**
     * The tilt camera input moves, which the renderer copies into {@link #pitch()}
     * on the next frame. Empty when the host or revision lacks it. Any thread.
     */
    public Optional<Integer> pitchTarget() { return Optional.ofNullable(TitanRuntime.cameraPitchTarget()); }

    /**
     * Moves the camera's tilt, clamped to the range the unlock flag allows now
     * ([0x400, 0xBF8], or [1, 0x1040] unlocked). The game clamps the tilt only
     * when input moves it. Game thread only; true when the host wrote it.
     */
    public boolean setPitchTarget(int pitch) { return TitanRuntime.setCameraPitchTarget(pitch); }

    /** The current viewport_setfov scales; empty when unavailable. Any thread. */
    public Optional<ViewportFov> fov() {
        int[] values = TitanRuntime.viewportFov();
        return values == null ? Optional.empty() : Optional.of(new ViewportFov(values[0], values[1]));
    }

    /**
     * Writes both scales, each clamped to [1, 0x7FFF]. Scripts calling
     * viewport_setfov (scrolling, the zoom slider, layout changes) overwrite
     * them. Game thread only; true when the host wrote them.
     */
    public boolean setFov(ViewportFov value) {
        return value != null && TitanRuntime.setViewportFov(value.smallViewport(), value.largeViewport());
    }

    /** The current viewport_clampfov bounds; empty when unavailable. Any thread. */
    public Optional<ViewportFovClamp> fovClamp() {
        int[] values = TitanRuntime.viewportFovClamp();
        return values == null ? Optional.empty()
            : Optional.of(new ViewportFovClamp(values[0], values[1], values[2], values[3]));
    }

    /**
     * Writes the four bounds, each clamped to [1, 0x7FFF], with each maximum
     * raised to its minimum. Game thread only; true when the host wrote them.
     */
    public boolean setFovClamp(ViewportFovClamp value) {
        return value != null && TitanRuntime.setViewportFovClamp(
            value.fovMin(), value.fovMax(), value.zoomMin(), value.zoomMax());
    }
}
