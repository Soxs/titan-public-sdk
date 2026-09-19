package net.titan.api.internal;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.titan.api.ItemPrice;
import net.titan.api.ItemPriceMetadata;
import net.titan.api.ItemPriceStatus;

/** Runtime implementation behind the client-wide public item-price cache. */
public interface ItemPricesBackend {
    ItemPricesBackend UNAVAILABLE = new ItemPricesBackend() {};
    default boolean requestItemPriceCatalog() { return false; }
    default boolean requestItemPrice(int id) { return false; }
    default ItemPriceStatus itemPriceStatus() { return ItemPriceStatus.unavailable(); }
    default Optional<ItemPriceMetadata> itemPriceMetadata(int id) { return Optional.empty(); }
    default List<ItemPriceMetadata> itemPriceItems() { return Collections.emptyList(); }
    default Optional<ItemPrice> itemPrice(int id) { return Optional.empty(); }
}
