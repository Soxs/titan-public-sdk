package net.titan.api;

import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** SDK 129: InventoryItem.useOn(GroundItem) routes through the InteractionBackend seam. */
class InventoryItemGroundItemTest {
    private InteractionBackend backend;

    @AfterEach
    void clearBackend() {
        if (backend != null) {
            TitanRuntime.clearInteractionBackend(backend);
            backend = null;
        }
    }

    @Test
    void useOnGroundItemForwardsSlotItemAndStackIdentity() {
        AtomicReference<String> call = new AtomicReference<>();
        AtomicReference<Object[]> args = new AtomicReference<>();
        backend = (InteractionBackend) Proxy.newProxyInstance(
            InteractionBackend.class.getClassLoader(),
            new Class<?>[] { InteractionBackend.class },
            (proxy, method, arguments) -> {
                if (method.getDeclaringClass() == Object.class) {
                    return method.getName().equals("hashCode") ? System.identityHashCode(proxy)
                        : method.getName().equals("equals") ? proxy == arguments[0] : "proxy";
                }
                call.set(method.getName());
                args.set(arguments);
                return method.getReturnType() == boolean.class ? Boolean.TRUE : null;
            });
        TitanRuntime.setInteractionBackend(backend);

        InventoryItem source = inventoryItem(4, 995);
        GroundItem target = groundItem(526, 32, 46, 2, 7);
        assertTrue(source.useOn(target));
        assertEquals("useInventoryItemOnGroundItem", call.get());
        assertArrayEquals(new Object[] { 4, 995, 526, 32, 46, 2, 7 }, args.get());

        call.set(null);
        assertFalse(source.useOn((GroundItem) null));
        assertNull(call.get());

        // The loc overload keeps its own seam.
        TileObject object = tileObject(100, 3200, 3201);
        assertTrue(source.useOn(object));
        assertEquals("useInventoryItemOnObject", call.get());
        assertArrayEquals(new Object[] { 4, 995, 100, 3200, 3201 }, args.get());
    }

    @Test
    void defaultBackendRejectsGroundItemPairs() {
        backend = new MinimalBackend();
        TitanRuntime.setInteractionBackend(backend);
        assertFalse(inventoryItem(4, 995).useOn(groundItem(526, 32, 46, 0, -1)));
        assertFalse(backend.useInventoryItemOnGroundItem(4, 995, 526, 32, 46, 0, -1));
    }

    private static InventoryItem inventoryItem(int slot, int id) {
        InventoryItem item = new InventoryItem();
        setInt(item, "slot", slot);
        setInt(item, "id", id);
        return item;
    }

    private static GroundItem groundItem(int id, int tileX, int tileY, int plane, int worldViewId) {
        GroundItem item = new GroundItem();
        setInt(item, "id", id);
        setInt(item, "tileX", tileX);
        setInt(item, "tileY", tileY);
        setInt(item, "plane", plane);
        setInt(item, "worldViewId", worldViewId);
        return item;
    }

    private static TileObject tileObject(int id, int tileX, int tileY) {
        TileObject object = new TileObject();
        setInt(object, "id", id);
        setInt(object, "tileX", tileX);
        setInt(object, "tileY", tileY);
        return object;
    }

    private static void setInt(Object target, String name, int value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.setInt(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }

    /** Implements only the abstract seam so the interface default stays observable. */
    private static final class MinimalBackend implements InteractionBackend {
        @Override public boolean interactNpcByIndex(String action, int hashIndex) { return false; }
        @Override public boolean interactTileObject(String action, TileObject object) { return false; }
        @Override public boolean interactGroundItem(String action, int itemId, int tileX, int tileY) { return false; }
        @Override public boolean interactInventoryItem(int itemId, String action) { return false; }
        @Override public boolean interactInventoryItemAtSlot(int slot, int itemId, String action) { return false; }
        @Override public boolean useInventoryItemOnItem(int srcSlot, int srcItemId, int targetSlot, int targetItemId) { return false; }
        @Override public boolean useInventoryItemOnNpc(int srcSlot, int srcItemId, int npcHashIndex) { return false; }
        @Override public boolean useInventoryItemOnObject(int srcSlot, int srcItemId, int objectId, int tileX, int tileY) { return false; }
        @Override public boolean widgetInteract(int opcode, int identifier, int param0, int param1) { return false; }
        @Override public boolean widgetInteractAtPath(WidgetAddress address, int opcode, int identifier, int childSlot) { return false; }
        @Override public boolean setWidgetText(int packedId, String text) { return false; }
        @Override public boolean setWidgetTextAtPath(WidgetAddress address, String text) { return false; }
    }
}
