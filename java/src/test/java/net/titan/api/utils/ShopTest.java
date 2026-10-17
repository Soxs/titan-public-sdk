package net.titan.api.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.titan.api.Client;
import net.titan.api.ItemDefinition;
import net.titan.api.MenuAction;
import net.titan.api.Widget;
import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.gamevals.InterfaceID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Keeps Java's Shop at parity with tests/shop_sdk_tests.cpp (the C++ header JS binds to). */
class ShopTest {
    private Injector injector;
    private InteractionBackend backend;
    private boolean open;
    private final List<int[]> stock = new ArrayList<>();
    private final List<int[]> inventory = new ArrayList<>();
    private final List<int[]> actions = new ArrayList<>();

    @BeforeEach
    void install() {
        open = true;
        stock.add(new int[] {1925, 30});
        stock.add(new int[] {1931, 0});
        stock.add(new int[] {1735, 7});
        inventory.add(new int[] {6512, 0});
        inventory.add(new int[] {-1, 0});
        inventory.add(new int[] {995, 120});
        inventory.add(new int[] {1736, 3});
        inventory.add(new int[] {1925, 1});
        Client client = (Client) Proxy.newProxyInstance(
            Client.class.getClassLoader(), new Class<?>[] {Client.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "widget": {
                        int packed = (Integer) args[0];
                        Widget w = new Widget();
                        set(w, "visible", packed == InterfaceID.Shopmain.ITEMS && open);
                        return packed == InterfaceID.Shopmain.ITEMS ? Optional.of(w) : Optional.empty();
                    }
                    case "widgetChildren": {
                        int packed = (Integer) args[0];
                        if (!open) return Collections.emptyList();
                        if (packed == InterfaceID.Shopmain.ITEMS) return widgets(stock);
                        if (packed == InterfaceID.Shopside.ITEMS) return widgets(inventory);
                        return Collections.emptyList();
                    }
                    case "itemDefinition":
                        return item((Integer) args[0]);
                    case "hashCode": return System.identityHashCode(proxy);
                    case "equals": return proxy == args[0];
                    case "toString": return "ShopTestClient";
                    default: return defaultValue(method.getReturnType());
                }
            });
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Client.class).toInstance(client);
            }
        });
        TitanRuntime.setInjector(injector);
        backend = (InteractionBackend) Proxy.newProxyInstance(
            InteractionBackend.class.getClassLoader(), new Class<?>[] {InteractionBackend.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "widgetInteract":
                        actions.add(new int[] {(Integer) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3]});
                        return true;
                    case "hashCode": return System.identityHashCode(proxy);
                    case "equals": return proxy == args[0];
                    case "toString": return "ShopTestBackend";
                    default: return defaultValue(method.getReturnType());
                }
            });
        TitanRuntime.setInteractionBackend(backend);
    }

    @AfterEach
    void clearRuntime() {
        TitanRuntime.clearInjector(injector);
        TitanRuntime.clearInteractionBackend(backend);
    }

    private static List<Widget> widgets(List<int[]> slots) {
        List<Widget> out = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            Widget w = new Widget();
            set(w, "itemId", slots.get(i)[0]);
            set(w, "itemQuantity", slots.get(i)[1]);
            set(w, "visible", true);
            set(w, "dynamicChildSlot", i);
            out.add(w);
        }
        return out;
    }

    private static Optional<ItemDefinition> item(int id) {
        String name;
        int value;
        switch (id) {
            case 1925: name = "Bucket"; value = 2; break;
            case 1931: name = "Pot"; value = 1; break;
            case 1735:
            case 1736: name = "Shears"; value = 1; break;
            default: return Optional.empty();
        }
        ItemDefinition def = new ItemDefinition();
        set(def, "id", id);
        set(def, "name", name);
        set(def, "storeValue", value);
        return Optional.of(def);
    }

    private void assertLast(int identifier, int param0, int param1) {
        assertFalse(actions.isEmpty(), "an action was sent");
        assertArrayEquals(new int[] {MenuAction.CC_OP, identifier, param0, param1}, actions.get(actions.size() - 1));
    }

    @Test
    void stockReads() {
        assertTrue(Shop.isOpen());
        List<Shop.ShopItem> items = Shop.items();
        assertEquals(3, items.size());
        assertEquals(1931, items.get(1).itemId());
        assertEquals(0, items.get(1).quantity());
        assertEquals(1, items.get(1).slot());
        assertEquals(30, Shop.stock(1925));
        assertEquals(0, Shop.stock(4151));
        assertEquals(OptionalInt.of(2), Shop.slotOf(1735));
        assertEquals(OptionalInt.empty(), Shop.slotOf(4151));
        assertTrue(Shop.has(1925, 30));
        assertFalse(Shop.has(1925, 31));
        assertFalse(Shop.has(1931));
        assertEquals(2, Shop.storeValue(1925));
        assertEquals(0, Shop.storeValue(4151));
    }

    @Test
    void buyPicksTheLargestButtonNotAboveTheRequest() {
        int grid = InterfaceID.Shopmain.ITEMS;
        assertTrue(Shop.buy(1925, 1)); assertLast(2, 0, grid);
        assertTrue(Shop.buy(1925, 4)); assertLast(2, 0, grid);
        assertTrue(Shop.buy(1925, 5)); assertLast(3, 0, grid);
        assertTrue(Shop.buy(1925, 9)); assertLast(3, 0, grid);
        assertTrue(Shop.buy(1925, 10)); assertLast(4, 0, grid);
        assertTrue(Shop.buy(1925, 30)); assertLast(4, 0, grid);
        assertTrue(Shop.buy(1735, 7)); assertLast(3, 2, grid);
        stock.get(0)[1] = 60;
        assertTrue(Shop.buy(1925, 50)); assertLast(5, 0, grid);
        int before = actions.size();
        assertFalse(Shop.buy(1735, 8));
        assertFalse(Shop.buy(1931, 1));
        assertFalse(Shop.buy(4151, 1));
        assertFalse(Shop.buy(1925, 0));
        assertEquals(before, actions.size());
    }

    @Test
    void sell() {
        int side = InterfaceID.Shopside.ITEMS;
        assertTrue(Shop.sell(1925, 1)); assertLast(2, 4, side);
        assertTrue(Shop.sell(995, 50)); assertLast(5, 2, side);
        assertTrue(Shop.sell(995, 12)); assertLast(4, 2, side);
        assertTrue(Shop.sellByName("SHEAR", 5)); assertLast(3, 3, side);
        assertTrue(Shop.hasItemToSell("bucket"));
        assertFalse(Shop.hasItemToSell("rune"));
        int before = actions.size();
        assertFalse(Shop.sell(4151, 1));
        assertFalse(Shop.sell(6512, 1));
        assertFalse(Shop.sellByName("rune", 1));
        assertFalse(Shop.sellByName("", 1));
        assertEquals(before, actions.size());
    }

    @Test
    void close() {
        assertTrue(Shop.close());
        assertLast(1, 11, InterfaceID.Shopmain.FRAME);
        open = false;
        actions.clear();
        assertTrue(Shop.close());
        assertTrue(actions.isEmpty());
    }

    @Test
    void closedShopFailsClosed() {
        open = false;
        assertFalse(Shop.isOpen());
        assertTrue(Shop.items().isEmpty());
        assertEquals(0, Shop.stock(1925));
        assertFalse(Shop.buy(1925, 1));
        assertFalse(Shop.sell(1925, 1));
        assertFalse(Shop.sellByName("bucket", 1));
        assertFalse(Shop.hasItemToSell("bucket"));
        assertTrue(actions.isEmpty());
    }

    private static void set(Object target, String name, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == Optional.class) return Optional.empty();
        if (type == OptionalInt.class) return OptionalInt.empty();
        if (type == List.class) return Collections.emptyList();
        return null;
    }
}
