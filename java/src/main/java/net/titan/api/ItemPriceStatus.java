package net.titan.api;

/** Immutable shared item-price service status (SDK 137). */
public final class ItemPriceStatus {
    private final boolean available, catalogLoading, catalogPending;
    private final int pendingCount, loadingItem;
    private final long catalogRevision, catalogFetchedAt, catalogLastAttemptAt;
    private final String error;

    public ItemPriceStatus(boolean available, boolean catalogLoading, boolean catalogPending,
                          int pendingCount, int loadingItem, long catalogRevision,
                          long catalogFetchedAt, long catalogLastAttemptAt, String error) {
        this.available = available;
        this.catalogLoading = catalogLoading;
        this.catalogPending = catalogPending;
        this.pendingCount = pendingCount;
        this.loadingItem = loadingItem;
        this.catalogRevision = catalogRevision;
        this.catalogFetchedAt = catalogFetchedAt;
        this.catalogLastAttemptAt = catalogLastAttemptAt;
        this.error = error == null ? "" : error;
    }
    public static ItemPriceStatus unavailable() { return new ItemPriceStatus(false, false, false, 0, -1, 0, 0, 0, ""); }
    /** True when a catalog is cached, including while a refresh is pending. */
    public boolean available() { return available; }
    public boolean catalogLoading() { return catalogLoading; }
    public boolean catalogPending() { return catalogPending; }
    public int pendingCount() { return pendingCount; }
    /** -1 idle; 0 catalog request; positive values identify an item request. */
    public int loadingItem() { return loadingItem; }
    /** Changes after each successful catalog refresh. Cache lists against it. */
    public long catalogRevision() { return catalogRevision; }
    public long catalogFetchedAt() { return catalogFetchedAt; }
    public long catalogLastAttemptAt() { return catalogLastAttemptAt; }
    public String error() { return error; }
    public boolean isAvailable() { return available; }
    public boolean isCatalogLoading() { return catalogLoading; }
    public boolean isCatalogPending() { return catalogPending; }
    public int getPendingCount() { return pendingCount; }
    public int getLoadingItem() { return loadingItem; }
    public long getCatalogRevision() { return catalogRevision; }
    public long getCatalogFetchedAt() { return catalogFetchedAt; }
    public long getCatalogLastAttemptAt() { return catalogLastAttemptAt; }
    public String getError() { return error; }
}
