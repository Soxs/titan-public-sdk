package net.titan.api;

import net.titan.api.internal.ItemSpritesBackend;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class ItemSpritesTest {
    @Test void imagesOwnTheirInputAndReturnedPixels() {
        int[] pixels = {0xff123456};
        SpriteImage image = new SpriteImage(1, 1, 3, 4, 36, 32, pixels);
        pixels[0] = 0;
        image.pixels()[0] = 0;
        assertEquals(0xff123456, image.pixels()[0]);
    }

    @Test void outlineUsesAlphaAndManhattanDistanceAndPreservesCanvasOrigin() {
        SpriteImage image = new SpriteImage(2, 1, 3, 4, 36, 32, new int[]{0x01000000, 0x00ffffff});
        SpriteImage ring = image.outline(0x80ff0000, 1);
        assertEquals(4, ring.width());
        assertEquals(3, ring.height());
        assertEquals(2, ring.xOffset());
        assertEquals(3, ring.yOffset());
        assertEquals(36, ring.canvasWidth());
        assertEquals(32, ring.canvasHeight());
        assertArrayEquals(new int[]{0, 0x80ff0000, 0, 0,
            0x80ff0000, 0, 0x80ff0000, 0,
            0, 0x80ff0000, 0, 0}, ring.pixels());
        assertEquals(0x01000000, image.pixels()[0]);
    }

    @Test void thicknessTwoHasTwelvePixelsAroundOneOccupiedPixel() {
        SpriteImage image = new SpriteImage(1, 1, 0, 0, 1, 1, new int[]{0xff000000});
        SpriteImage ring = image.outline(-1, 2);
        assertEquals(12, java.util.Arrays.stream(ring.pixels()).filter(p -> p != 0).count());
        assertEquals(0, ring.pixels()[12]);
    }

    @Test void boundsAndInvalidRequestsFailBeforeBackend() {
        assertThrows(IllegalArgumentException.class,
            () -> new SpriteImage(1, 1, 0, 0, 1, 1, new int[0]));
        assertThrows(IllegalArgumentException.class,
            () -> new SpriteImage(1, 1, -513, 0, 1, 1, new int[1]));
        SpriteImage image = new SpriteImage(512, 1, 0, 0, 512, 1, new int[512]);
        assertThrows(IllegalArgumentException.class, () -> image.outline(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> image.outline(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ItemSprites.Options(3, 0, ItemSprites.QuantityText.HIDDEN));
        assertThrows(IllegalArgumentException.class, () -> new ItemSprites.Options(0, -1, ItemSprites.QuantityText.HIDDEN));
        assertEquals(ItemSprites.Status.INVALID, Titan.itemSprites().item(1, 0).status());
        assertEquals(ItemSprites.Status.INVALID, Titan.itemSprites().item(-1).status());
    }

    @Test void pendingReadyAndDrawUseTheSameOwnedImageContract() {
        SpriteImage image = new SpriteImage(1, 1, -1, -2, 36, 32, new int[]{-1});
        ItemSpritesBackend backend = new ItemSpritesBackend() {
            int calls;
            @Override public ItemSprites.Result itemSprite(int id, int quantity, ItemSprites.Options options) {
                assertEquals(4151, id);
                assertEquals(10, quantity);
                assertEquals(ItemSprites.QuantityText.HIDDEN, options.quantityText());
                return ++calls == 1 ? new ItemSprites.Result(ItemSprites.Status.PENDING, null)
                    : new ItemSprites.Result(ItemSprites.Status.READY, image);
            }
            @Override public boolean drawSprite(SpriteImage value, int x, int y, float scale) {
                assertSame(image, value);
                assertEquals(100, x); assertEquals(200, y); assertEquals(2.0f, scale);
                return true;
            }
        };
        TitanRuntime.setItemSpritesBackend(backend);
        try {
            assertEquals(ItemSprites.Status.PENDING, Titan.itemSprites().item(4151, 10).status());
            assertSame(image, Titan.itemSprites().item(4151, 10).image().orElseThrow());
            assertTrue(Titan.itemSprites().draw(image, 100, 200, 2));
            assertFalse(Titan.itemSprites().draw(image, 100, 200, Float.NaN));
        } finally { TitanRuntime.clearItemSpritesBackend(backend); }
        assertEquals(ItemSprites.Status.UNSUPPORTED, Titan.itemSprites().item(4151).status());
        assertFalse(Titan.itemSprites().draw(image, 100, 200));
    }
}
