package net.titan.api;

/** Owned snapshot of a C++ request. Poll Ge.request(id) for updated progress. */
public final class GeRequest {
    public enum Phase { QUEUED, OPENING, SELECTING, QUANTITY, PRICING, CONFIRMING, BUYING,
        CANCELLING, COLLECTING, COMPLETED, CANCELLED, FAILED }
    private final long requestId, unitPrice, spent;
    private final int itemId, quantity, filled, remaining, attempts, slot;
    private final Phase phase;
    private final String message;
    public GeRequest(long requestId, int itemId, int quantity, int filled, int remaining,
                     int attempts, int slot, Phase phase, long unitPrice, long spent, String message) {
        this.requestId = requestId; this.itemId = itemId; this.quantity = quantity;
        this.filled = filled; this.remaining = remaining; this.attempts = attempts;
        this.slot = slot; this.phase = phase; this.unitPrice = unitPrice; this.spent = spent; this.message = message;
    }
    public long requestId() { return requestId; }
    public int itemId() { return itemId; }
    public int quantity() { return quantity; }
    public int filled() { return filled; }
    public int remaining() { return remaining; }
    public int attempts() { return attempts; }
    public int slot() { return slot; }
    public Phase phase() { return phase; }
    public long unitPrice() { return unitPrice; }
    public long spent() { return spent; }
    public String message() { return message; }
    public boolean isComplete() { return phase == Phase.COMPLETED || phase == Phase.CANCELLED || phase == Phase.FAILED; }
    public boolean isSuccessful() { return phase == Phase.COMPLETED; }
}
