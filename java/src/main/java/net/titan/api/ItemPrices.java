package net.titan.api;

import java.util.List;
import java.util.Optional;
import net.titan.api.internal.TitanRuntime;

/** Client-wide public Wiki price cache (SDK 137). Requests are asynchronous and
 * coalesced across plugins; reads never start network requests. No private offer
 * or account information is uploaded. All returned values are owned snapshots. */
public final class ItemPrices {
    ItemPrices() {}
    public boolean requestCatalog() { return TitanRuntime.getItemPricesBackend().requestItemPriceCatalog(); }
    /** True when accepted, already loading/pending, or already fresh in cache. */
    public boolean request(int id) { return id > 0 && TitanRuntime.getItemPricesBackend().requestItemPrice(id); }
    public ItemPriceStatus status() { return TitanRuntime.getItemPricesBackend().itemPriceStatus(); }
    public Optional<ItemPriceMetadata> item(int id) {
        return id > 0 ? TitanRuntime.getItemPricesBackend().itemPriceMetadata(id) : Optional.empty();
    }
    /** Copies the complete cached catalog; use status().catalogRevision() to
     * avoid repeatedly rebuilding unchanged item lists. */
    public List<ItemPriceMetadata> items() { return List.copyOf(TitanRuntime.getItemPricesBackend().itemPriceItems()); }
    public Optional<ItemPrice> price(int id) {
        return id > 0 ? TitanRuntime.getItemPricesBackend().itemPrice(id) : Optional.empty();
    }
}
