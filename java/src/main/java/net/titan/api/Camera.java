package net.titan.api;

public final class Camera {
    private int posX;
    private int posY;
    private int posZ;
    private int yaw;
    private int pitch;
    private int viewportW;
    private int viewportH;
    private int zoom;
    private boolean valid;

    public int posX() { return posX; }
    public int posY() { return posY; }
    public int posZ() { return posZ; }
    public int yaw() { return yaw; }
    public int pitch() { return pitch; }
    public int viewportW() { return viewportW; }
    public int viewportH() { return viewportH; }
    public int zoom() { return zoom; }
    public boolean isValid() { return valid; }
}
