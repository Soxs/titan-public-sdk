package net.titan.api;

/**
 * The viewport_clampfov (CS2 6202) bounds, natively 1 / 0x7FFF (SDK 162). Each
 * frame the game keeps its zoom within [zoomMin, zoomMax] by moving the scale
 * within [fovMin, fovMax], letter- or pillarboxing past them.
 */
public final class ViewportFovClamp {
    private final int fovMin;
    private final int fovMax;
    private final int zoomMin;
    private final int zoomMax;

    public ViewportFovClamp(int fovMin, int fovMax, int zoomMin, int zoomMax) {
        this.fovMin = fovMin;
        this.fovMax = fovMax;
        this.zoomMin = zoomMin;
        this.zoomMax = zoomMax;
    }

    public int fovMin() { return fovMin; }
    public int fovMax() { return fovMax; }
    public int zoomMin() { return zoomMin; }
    public int zoomMax() { return zoomMax; }

    @Override public boolean equals(Object other) {
        if (!(other instanceof ViewportFovClamp)) return false;
        ViewportFovClamp that = (ViewportFovClamp) other;
        return fovMin == that.fovMin && fovMax == that.fovMax && zoomMin == that.zoomMin && zoomMax == that.zoomMax;
    }
    @Override public int hashCode() { return ((31 * fovMin + fovMax) * 31 + zoomMin) * 31 + zoomMax; }
    @Override public String toString() {
        return "ViewportFovClamp[fovMin=" + fovMin + ", fovMax=" + fovMax
            + ", zoomMin=" + zoomMin + ", zoomMax=" + zoomMax + "]";
    }
}
