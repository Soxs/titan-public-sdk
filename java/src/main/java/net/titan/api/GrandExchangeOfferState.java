package net.titan.api;

/** RuneLite-style state of one of the local player's Grand Exchange offers. */
public enum GrandExchangeOfferState {
    UNKNOWN(-1), EMPTY(0), CANCELLED_BUY(1), CANCELLED_SELL(2),
    BUYING(3), BOUGHT(4), SELLING(5), SOLD(6);

    private final int value;

    GrandExchangeOfferState(int value) { this.value = value; }

    public int value() { return value; }

    public static GrandExchangeOfferState fromValue(int value) {
        for (GrandExchangeOfferState state : values()) {
            if (state.value == value) return state;
        }
        return UNKNOWN;
    }
}
