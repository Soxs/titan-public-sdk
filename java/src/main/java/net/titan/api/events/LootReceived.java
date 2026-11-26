package net.titan.api.events;

import java.util.Optional;
import net.titan.api.GameData;

/**
 * What a monster or a player left for the account. Game thread, before the {@link GameTick} of the server tick in which the
 * items came onto the ground or the server said itself what was looted. SDK 194.
 */
public final class LootReceived {
    private final int number;

    public LootReceived(int number) { this.number = number; }

    public int number() { return number; }
    /** Of the last 64. Empty for one which is longer ago. */
    public Optional<GameData.Loot> loot() { return GameData.loot(number); }
}
