package net.titan.api.internal;

import net.titan.api.SpriteImage;

/** Optional host-owned local artwork. No game-state or RuneLite dependency. */
public interface UiAssetsBackend {
    UiAssetsBackend UNAVAILABLE = new UiAssetsBackend() {};
    default byte[] fontData(int face) { return new byte[0]; }
    default SpriteImage skillIcon(int skillId, boolean small) { return null; }
}
