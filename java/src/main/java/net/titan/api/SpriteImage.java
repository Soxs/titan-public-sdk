package net.titan.api;

import java.util.Objects;

/** Owned, immutable row-major straight ARGB32 pixels. No client memory is retained.
 * Offsets place these pixels relative to the original canvas origin. */
public final class SpriteImage {
    public static final int MAX_DIMENSION = 512;
    public static final int MAX_PIXELS = MAX_DIMENSION * MAX_DIMENSION;

    private final int width, height, xOffset, yOffset, canvasWidth, canvasHeight;
    private final int[] pixels;

    public SpriteImage(int width, int height, int xOffset, int yOffset,
                       int canvasWidth, int canvasHeight, int[] pixels) {
        Objects.requireNonNull(pixels, "pixels");
        if (width <= 0 || height <= 0 || width > MAX_DIMENSION || height > MAX_DIMENSION
                || canvasWidth <= 0 || canvasHeight <= 0
                || canvasWidth > MAX_DIMENSION || canvasHeight > MAX_DIMENSION
                || xOffset < -MAX_DIMENSION || xOffset > MAX_DIMENSION
                || yOffset < -MAX_DIMENSION || yOffset > MAX_DIMENSION
                || pixels.length != (long) width * height) {
            throw new IllegalArgumentException("Invalid sprite dimensions or pixel count");
        }
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.pixels = pixels.clone();
    }

    public int width() { return width; }
    public int height() { return height; }
    public int xOffset() { return xOffset; }
    public int yOffset() { return yOffset; }
    public int canvasWidth() { return canvasWidth; }
    public int canvasHeight() { return canvasHeight; }
    /** Returns a copy; modifying it cannot change this image or the host cache. */
    public int[] pixels() { return pixels.clone(); }

    public SpriteImage outline(int argb) { return outline(argb, 1); }

    /** Pure CPU Manhattan-distance dilation of all pixels with nonzero alpha.
     * The result contains only the outer ring. Its expanded bounds preserve the
     * original canvas origin, so draw the image and outline at the same x/y.
     * Request the source without quantity text, native border, or shadow when
     * an item silhouette is wanted. Thickness is 1..8. */
    public SpriteImage outline(int argb, int thickness) {
        if (thickness < 1 || thickness > 8 || width + 2 * thickness > MAX_DIMENSION
                || height + 2 * thickness > MAX_DIMENSION
                || xOffset - thickness < -MAX_DIMENSION
                || yOffset - thickness < -MAX_DIMENSION) {
            throw new IllegalArgumentException("Invalid outline thickness or expanded dimensions");
        }
        int outWidth = width + 2 * thickness;
        int outHeight = height + 2 * thickness;
        int[] distance = new int[outWidth * outHeight];
        java.util.Arrays.fill(distance, thickness + 1);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((pixels[y * width + x] >>> 24) != 0)
                    distance[(y + thickness) * outWidth + x + thickness] = 0;
            }
        }
        for (int y = 0; y < outHeight; y++) {
            for (int x = 0; x < outWidth; x++) {
                int at = y * outWidth + x;
                if (x > 0) distance[at] = Math.min(distance[at], distance[at - 1] + 1);
                if (y > 0) distance[at] = Math.min(distance[at], distance[at - outWidth] + 1);
            }
        }
        for (int y = outHeight - 1; y >= 0; y--) {
            for (int x = outWidth - 1; x >= 0; x--) {
                int at = y * outWidth + x;
                if (x + 1 < outWidth) distance[at] = Math.min(distance[at], distance[at + 1] + 1);
                if (y + 1 < outHeight) distance[at] = Math.min(distance[at], distance[at + outWidth] + 1);
            }
        }
        int[] out = new int[distance.length];
        for (int at = 0; at < out.length; at++) {
            if (distance[at] > 0 && distance[at] <= thickness) out[at] = argb;
        }
        return new SpriteImage(outWidth, outHeight, xOffset - thickness,
            yOffset - thickness, canvasWidth, canvasHeight, out);
    }
}
