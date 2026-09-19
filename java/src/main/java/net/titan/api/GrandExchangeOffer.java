package net.titan.api;

import java.util.Objects;

/**
 * Immutable snapshot of one local Grand Exchange slot. Slot indices are zero based.
 * Price and spent are {@code long} because native clients can store 64-bit amounts.
 * Spent is the completed gold amount for either a buy or a sell offer.
 */
public final class GrandExchangeOffer {
    private final int slot;
    private final int itemId;
    private final int totalQuantity;
    private final int quantitySold;
    private final long price;
    private final long spent;
    private final GrandExchangeOfferState state;
    private final int status;
    private final int type;

    public GrandExchangeOffer(int slot, int itemId, int totalQuantity, int quantitySold,
                             long price, long spent, GrandExchangeOfferState state,
                             int status, int type) {
        this.slot = slot;
        this.itemId = itemId;
        this.totalQuantity = totalQuantity;
        this.quantitySold = quantitySold;
        this.price = price;
        this.spent = spent;
        this.state = Objects.requireNonNull(state, "state");
        this.status = status;
        this.type = type;
    }

    public int slot() { return slot; }
    public int itemId() { return itemId; }
    public int totalQuantity() { return totalQuantity; }
    /** Completed quantity for both buy and sell offers. */
    public int quantitySold() { return quantitySold; }
    public long price() { return price; }
    public long spent() { return spent; }
    public GrandExchangeOfferState state() { return state; }
    /** Raw native status, retained for diagnostic and forward compatibility use. */
    public int status() { return status; }
    /** Raw native side: zero is buy; one is sell. */
    public int type() { return type; }

    public int getSlot() { return slot; }
    public int getItemId() { return itemId; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getQuantitySold() { return quantitySold; }
    public long getPrice() { return price; }
    public long getSpent() { return spent; }
    public GrandExchangeOfferState getState() { return state; }
    public int getStatus() { return status; }
    public int getType() { return type; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof GrandExchangeOffer)) return false;
        GrandExchangeOffer offer = (GrandExchangeOffer) other;
        return slot == offer.slot && itemId == offer.itemId
            && totalQuantity == offer.totalQuantity && quantitySold == offer.quantitySold
            && price == offer.price && spent == offer.spent && state == offer.state
            && status == offer.status && type == offer.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, itemId, totalQuantity, quantitySold, price, spent,
                            state, status, type);
    }
}
