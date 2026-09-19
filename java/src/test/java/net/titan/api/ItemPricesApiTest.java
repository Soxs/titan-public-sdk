package net.titan.api;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import net.titan.api.internal.ItemPricesBackend;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemPricesApiTest {
    private ItemPricesBackend backend;
    @AfterEach void clear() { TitanRuntime.clearItemPricesBackend(backend); }

    @Test void missingBackendFailsClosedWithoutStartingRequests() {
        assertFalse(Titan.itemPrices().requestCatalog());
        assertFalse(Titan.itemPrices().request(4151));
        assertFalse(Titan.itemPrices().status().available());
        assertEquals(-1, Titan.itemPrices().status().loadingItem());
        assertTrue(Titan.itemPrices().item(4151).isEmpty());
        assertTrue(Titan.itemPrices().price(4151).isEmpty());
        assertTrue(Titan.itemPrices().items().isEmpty());
    }

    @Test void requestsAndCachedReadsUseTheSameSharedBackendWithoutImplicitFetch() throws Throwable {
        final int[] requested = {0, 0};
        ItemPriceMetadata metadata = new ItemPriceMetadata(4151, "Abyssal whip", "A weapon",
            OptionalLong.of(70), OptionalLong.of(Long.MAX_VALUE), true);
        ItemPrice quote = new ItemPrice(4151, OptionalLong.of(9_007_199_254_740_993L), OptionalLong.empty(),
            OptionalLong.of(123), OptionalLong.empty(), 124, 130, false, true, "Refresh failed");
        List<ItemPriceMetadata> items = new ArrayList<>(List.of(metadata));
        ItemPriceStatus status = new ItemPriceStatus(true, false, true, 2, 4151, Long.MAX_VALUE, 120, 130, "Retrying");
        backend = new ItemPricesBackend() {
            public boolean requestItemPriceCatalog() { requested[0]++; return true; }
            public boolean requestItemPrice(int id) { requested[1] = id; return true; }
            public ItemPriceStatus itemPriceStatus() { return status; }
            public Optional<ItemPriceMetadata> itemPriceMetadata(int id) { return id == 4151 ? Optional.of(metadata) : Optional.empty(); }
            public List<ItemPriceMetadata> itemPriceItems() { return items; }
            public Optional<ItemPrice> itemPrice(int id) { return id == 4151 ? Optional.of(quote) : Optional.empty(); }
        };
        TitanRuntime.setItemPricesBackend(backend);
        Client client = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(), new Class<?>[]{Client.class},
            (proxy, method, args) -> MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup())
                .unreflectSpecial(method, method.getDeclaringClass()).bindTo(proxy)
                .invokeWithArguments(args == null ? new Object[0] : args));
        assertSame(Titan.itemPrices(), client.itemPrices());
        assertSame(status, client.itemPrices().status());
        assertEquals(Long.MAX_VALUE, status.catalogRevision());
        assertEquals(9_007_199_254_740_993L, client.itemPrices().price(4151).orElseThrow().high().orElseThrow());
        assertTrue(quote.low().isEmpty());
        assertTrue(quote.lowTime().isEmpty());
        assertEquals(124, quote.fetchedAt());
        assertEquals("Refresh failed", quote.error());
        assertEquals(Long.MAX_VALUE, client.itemPrices().item(4151).orElseThrow().highAlch().orElseThrow());
        List<ItemPriceMetadata> retained = client.itemPrices().items();
        items.clear();
        assertEquals(1, retained.size());
        assertThrows(UnsupportedOperationException.class, retained::clear);
        assertArrayEquals(new int[]{0, 0}, requested, "cache reads cannot initiate network requests");
        assertFalse(client.itemPrices().request(0));
        assertFalse(client.itemPrices().request(-1));
        assertTrue(client.itemPrices().price(-1).isEmpty());
        assertTrue(client.itemPrices().item(0).isEmpty());
        assertArrayEquals(new int[]{0, 0}, requested);
        assertTrue(client.itemPrices().requestCatalog());
        assertTrue(client.itemPrices().request(4151));
        assertArrayEquals(new int[]{1, 4151}, requested);
    }

    @Test void backendTeardownOnlyClearsTheMatchingRuntime() {
        ItemPricesBackend old = new ItemPricesBackend() {};
        backend = new ItemPricesBackend() { public boolean requestItemPriceCatalog() { return true; } };
        TitanRuntime.setItemPricesBackend(backend);
        TitanRuntime.clearItemPricesBackend(old);
        assertTrue(Titan.itemPrices().requestCatalog());
        TitanRuntime.clearItemPricesBackend(backend);
        assertFalse(Titan.itemPrices().requestCatalog());
    }

    @Test void optionalZeroAndNullTextRemainUnambiguous() {
        ItemPrice quote = new ItemPrice(1, OptionalLong.of(0), OptionalLong.empty(), OptionalLong.empty(),
            OptionalLong.empty(), 0, 0, false, false, null);
        assertTrue(quote.high().isPresent());
        assertEquals(0, quote.high().getAsLong());
        assertTrue(quote.low().isEmpty());
        assertEquals("", quote.error());
        ItemPriceMetadata item = new ItemPriceMetadata(1, null, null, OptionalLong.empty(), OptionalLong.empty(), false);
        assertEquals("", item.name());
        assertEquals("", item.examine());
        assertThrows(NullPointerException.class, () -> new ItemPriceMetadata(1, "", "", null, OptionalLong.empty(), false));
    }
}
