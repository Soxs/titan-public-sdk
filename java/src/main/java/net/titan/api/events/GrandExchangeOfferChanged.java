package net.titan.api.events;

import java.util.Objects;
import net.titan.api.GrandExchangeOffer;

/**
 * Initialization or a native update to one local Grand Exchange slot.
 * At initial observation after attach/login, the client emits a synthetic
 * {@code EMPTY} baseline for every slot, followed by observed offers. A baseline
 * notification does not mean that a trade was cancelled or collected.
 * Repeated native updates are delivered even when their values are unchanged.
 * Events dispatch on the game thread at the next safe main-loop phase.
 * The offer is an immutable snapshot captured for this event: retaining it never
 * reads a later live offer. Slots are zero based.
 */
public final class GrandExchangeOfferChanged {
    private final GrandExchangeOffer offer;
    private final int slot;

    public GrandExchangeOfferChanged(GrandExchangeOffer offer, int slot) {
        this.offer = Objects.requireNonNull(offer, "offer");
        if (slot != offer.slot()) throw new IllegalArgumentException("Offer slot mismatch");
        this.slot = slot;
    }

    public GrandExchangeOffer offer() { return offer; }
    public GrandExchangeOffer grandExchangeOffer() { return offer; }
    public int slot() { return slot; }
    public GrandExchangeOffer getOffer() { return offer; }
    public GrandExchangeOffer getGrandExchangeOffer() { return offer; }
    public int getSlot() { return slot; }
}
