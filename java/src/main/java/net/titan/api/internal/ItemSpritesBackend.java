package net.titan.api.internal;

import net.titan.api.ItemSprites;
import net.titan.api.SpriteImage;

/** Optional runtime binding; absent hosts fail closed without touching native state. */
public interface ItemSpritesBackend {
    ItemSpritesBackend UNAVAILABLE = new ItemSpritesBackend() {};
    default ItemSprites.Result itemSprite(int itemId, int quantity, ItemSprites.Options options) {
        return new ItemSprites.Result(ItemSprites.Status.UNSUPPORTED, null);
    }
    default boolean drawSprite(SpriteImage image, int x, int y, float scale) { return false; }
}
