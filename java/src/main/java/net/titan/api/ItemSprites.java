package net.titan.api;

import java.util.Objects;
import java.util.Optional;
import net.titan.api.internal.TitanRuntime;

/** Native item icons copied into owned images. Calls may be made on plugin
 * workers: accepted misses return Pending; call again on a later frame.
 * NotReady means native rendering has not yet been observed, resources are
 * unavailable, or the queue is full; retry later.
 * Cache hits never invoke the native renderer on the caller's thread. */
public final class ItemSprites {
    ItemSprites() {}

    public enum Status {
        READY, PENDING, UNSUPPORTED, INVALID, NOT_READY, FAILED, BUFFER_TOO_SMALL;

        public static Status fromValue(int value) {
            return value >= 0 && value < values().length ? values()[value] : FAILED;
        }
    }

    public enum QuantityText {
        HIDDEN, ALWAYS
    }

    /** Native appearance controls. Defaults omit text, border, and shadow. */
    public static final class Options {
        public static final Options DEFAULT = new Options(0, 0, QuantityText.HIDDEN);
        private final int border, shadowRgb;
        private final QuantityText quantityText;

        public Options(int border, int shadowRgb, QuantityText quantityText) {
            if (border < 0 || border > 2 || (shadowRgb & 0xff000000) != 0)
                throw new IllegalArgumentException("Border must be 0..2 and shadow must be RGB24");
            this.border = border;
            this.shadowRgb = shadowRgb;
            this.quantityText = Objects.requireNonNull(quantityText, "quantityText");
        }
        public int border() { return border; }
        public int shadowRgb() { return shadowRgb; }
        public QuantityText quantityText() { return quantityText; }
    }

    public static final class Result {
        private final Status status;
        private final SpriteImage image;

        public Result(Status status, SpriteImage image) {
            this.status = Objects.requireNonNull(status, "status");
            if ((status == Status.READY) != (image != null))
                throw new IllegalArgumentException("Only Ready results contain an image");
            this.image = image;
        }
        public Status status() { return status; }
        public Optional<SpriteImage> image() { return Optional.ofNullable(image); }
        public boolean ready() { return status == Status.READY; }
    }

    public Result item(int itemId) { return item(itemId, 1, Options.DEFAULT); }
    public Result item(int itemId, int quantity) { return item(itemId, quantity, Options.DEFAULT); }
    public Result item(int itemId, int quantity, Options options) {
        Objects.requireNonNull(options, "options");
        if (itemId < 0 || quantity <= 0) return new Result(Status.INVALID, null);
        return TitanRuntime.getItemSpritesBackend().itemSprite(itemId, quantity, options);
    }

    public SpriteImage outline(SpriteImage image, int argb) { return outline(image, argb, 1); }
    public SpriteImage outline(SpriteImage image, int argb, int thickness) {
        return Objects.requireNonNull(image, "image").outline(argb, thickness);
    }

    public boolean draw(SpriteImage image, int x, int y) { return draw(image, x, y, 1.0f); }
    /** Overlay callbacks only. Coordinates are the canvas origin, before image
     * offsets. Scale must be finite, greater than zero, and at most 16.
     * The host copies pixels before returning. False when unavailable
     * or outside the drawing context. */
    public boolean draw(SpriteImage image, int x, int y, float scale) {
        Objects.requireNonNull(image, "image");
        if (!Float.isFinite(scale) || scale <= 0) return false;
        return TitanRuntime.getItemSpritesBackend().drawSprite(image, x, y, scale);
    }
}
