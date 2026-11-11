package net.titan.api;

/** Values for the C++ GE queue. Prices use the in-game guide and +5% retry steps. */
public final class GeBuyOptions {
    public final int itemId, quantity, maxAttempts, waitPerAttemptMs, timeoutMs;
    public final boolean toInventory, noted, autoOpen;
    public final long maxUnitPrice;
    /** 0..100 initial +5% button presses; retries add one more. Default 0. */
    public final int initialPriceSteps;
    public GeBuyOptions(int itemId, int quantity) {
        this(itemId, quantity, true, false, true, 3, 10000, 180000, 0);
    }
    public GeBuyOptions(int itemId, int quantity, boolean toInventory, boolean autoOpen, boolean noted,
                        int maxAttempts, int waitPerAttemptMs, int timeoutMs, long maxUnitPrice) {
        this(itemId, quantity, toInventory, autoOpen, noted, maxAttempts, waitPerAttemptMs,
            timeoutMs, maxUnitPrice, 0);
    }
    public GeBuyOptions(int itemId, int quantity, boolean toInventory, boolean autoOpen, boolean noted,
                        int maxAttempts, int waitPerAttemptMs, int timeoutMs, long maxUnitPrice,
                        int initialPriceSteps) {
        this.itemId = itemId; this.quantity = quantity; this.toInventory = toInventory;
        this.autoOpen = autoOpen; this.noted = noted; this.maxAttempts = maxAttempts;
        this.waitPerAttemptMs = waitPerAttemptMs; this.timeoutMs = timeoutMs; this.maxUnitPrice = maxUnitPrice;
        this.initialPriceSteps = initialPriceSteps;
    }
    /** Copy with an initial price increase, independently of the attempt budget (SDK 175). */
    public GeBuyOptions withInitialPriceSteps(int steps) {
        return new GeBuyOptions(itemId, quantity, toInventory, autoOpen, noted, maxAttempts,
            waitPerAttemptMs, timeoutMs, maxUnitPrice, steps);
    }
}
