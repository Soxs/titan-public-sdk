package net.titan.api.internal;

import net.titan.api.GameData;

/** Optional runtime binding; absent hosts fail closed. */
public interface GameDataBackend {
    GameDataBackend UNAVAILABLE = new GameDataBackend() {};
    default GameData.Result gameData(String name, long[] numbers, String[] texts) {
        return new GameData.Result(GameData.Status.UNSUPPORTED, null);
    }
}
