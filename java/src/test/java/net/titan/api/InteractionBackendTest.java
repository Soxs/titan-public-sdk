package net.titan.api;

import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionBackendTest {
    private RecordingBackend backend;

    @AfterEach
    void clearBackend() {
        if (backend != null) {
            TitanRuntime.clearInteractionBackend(backend);
            backend = null;
        }
    }

    @Test
    void inventoryItemInteractionsUseRuntimeBackend() {
        useBackend(new RecordingBackend());
        InventoryItem source = inventoryItem(4, 995);
        InventoryItem target = inventoryItem(7, 4151);
        NPC npc = npc(42);
        TileObject object = tileObject(100, 3200, 3201);

        backend.slotResult = false;
        backend.itemResult = true;
        assertTrue(source.interact("Use"));
        assertEquals("inventoryItem", backend.call);
        assertArrayEquals(new Object[] { 995, "Use" }, backend.args);

        assertTrue(source.useOn(target));
        assertEquals("useItemOnItem", backend.call);
        assertArrayEquals(new Object[] { 4, 995, 7, 4151 }, backend.args);

        assertTrue(source.useOn(npc));
        assertEquals("useItemOnNpc", backend.call);
        assertArrayEquals(new Object[] { 4, 995, 42 }, backend.args);

        assertTrue(source.useOn(object));
        assertEquals("useItemOnObject", backend.call);
        assertArrayEquals(new Object[] { 4, 995, 100, 3200, 3201 }, backend.args);
    }

    @Test
    void inventoryItemInteractPrefersSlotAction() {
        useBackend(new RecordingBackend());
        InventoryItem source = inventoryItem(4, 995);

        backend.slotResult = true;
        assertTrue(source.interact("Drop"));
        assertEquals("inventoryItemAtSlot", backend.call);
        assertArrayEquals(new Object[] { 4, 995, "Drop" }, backend.args);
    }

    @Test
    void entityInteractionsUseRuntimeBackend() {
        useBackend(new RecordingBackend());
        NPC npc = npc(42, "Talk-to", "Trade");
        TileObject object = tileObject(100, 3200, 3201, "Open", "Close");
        GroundItem groundItem = groundItem(995, 3210, 3211);

        assertTrue(npc.interact("Talk-to"));
        assertEquals("npcByIndex", backend.call);
        assertArrayEquals(new Object[] { "Talk-to", 42 }, backend.args);

        assertTrue(object.interact("Open"));
        assertEquals("tileObject", backend.call);
        assertEquals("Open", backend.args[0]);
        assertSame(object, backend.args[1]);

        assertTrue(groundItem.interact("Take"));
        assertEquals("groundItem", backend.call);
        assertArrayEquals(new Object[] { "Take", 995, 3210, 3211 }, backend.args);
    }

    @Test
    void widgetActionsUseRuntimeBackend() {
        useBackend(new RecordingBackend());
        Widget staticWidget = widget(1234, -1, 0);
        Widget dynamicWidget = widget(2000, 3, 1000);

        assertTrue(staticWidget.interact(MenuAction.CC_OP, 1));
        assertEquals("widgetInteract", backend.call);
        assertArrayEquals(new Object[] { MenuAction.CC_OP, 1, -1, 1234 }, backend.args);

        assertTrue(dynamicWidget.interact(MenuAction.CC_OP, 2, 9));
        assertEquals("widgetInteractAtPath", backend.call);
        assertEquals(MenuAction.CC_OP, backend.args[1]);
        assertEquals(2, backend.args[2]);
        assertEquals(9, backend.args[3]);
        WidgetAddress address = (WidgetAddress) backend.args[0];
        assertEquals(1000, address.rootPackedId());
        assertEquals(java.util.List.of(3), address.dynamicPath());

        assertTrue(dynamicWidget.setText("hello"));
        assertEquals("setWidgetTextAtPath", backend.call);
        assertEquals("hello", backend.args[1]);
    }

    @Test
    void emptyActionsDoNotReachBackend() {
        useBackend(new RecordingBackend());

        assertFalse(npc(1).interact(""));
        assertFalse(tileObject(2, 3, 4).interact(null));
        assertFalse(groundItem(5, 6, 7).interact(""));
        assertFalse(inventoryItem(8, 9).interact(null));
        assertEquals(null, backend.call);
    }

    @Test
    void missingEntityActionsDoNotReachBackend() {
        useBackend(new RecordingBackend());

        assertFalse(npc(1, "Talk-to").interact("Attack"));
        assertFalse(tileObject(2, 3, 4, "Open").interact("Chop down"));
        assertEquals(null, backend.call);
    }

    private void useBackend(RecordingBackend value) {
        backend = value;
        TitanRuntime.setInteractionBackend(value);
    }

    private static InventoryItem inventoryItem(int slot, int id) {
        InventoryItem item = new InventoryItem();
        setInt(item, "slot", slot);
        setInt(item, "id", id);
        return item;
    }

    private static NPC npc(int hashIndex, String... actions) {
        NPC npc = new NPC();
        setInt(npc, "hashIndex", hashIndex);
        setObject(npc, "actions", Arrays.asList(actions));
        return npc;
    }

    private static TileObject tileObject(int id, int tileX, int tileY, String... actions) {
        TileObject object = new TileObject();
        setInt(object, "id", id);
        setInt(object, "tileX", tileX);
        setInt(object, "tileY", tileY);
        setObject(object, "actions", Arrays.asList(actions));
        return object;
    }

    private static GroundItem groundItem(int id, int tileX, int tileY) {
        GroundItem item = new GroundItem();
        setInt(item, "id", id);
        setInt(item, "tileX", tileX);
        setInt(item, "tileY", tileY);
        return item;
    }

    private static Widget widget(int packedId, int dynamicChildSlot, int rootPackedId) {
        Widget widget = new Widget();
        setInt(widget, "packedId", packedId);
        setInt(widget, "dynamicChildSlot", dynamicChildSlot);
        setInt(widget, "rootPackedId", rootPackedId);
        return widget;
    }

    private static void setInt(Object target, String fieldName, int value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setInt(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }

    private static void setObject(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }

    private static final class RecordingBackend implements InteractionBackend {
        String call;
        Object[] args;
        boolean slotResult = true;
        boolean itemResult = true;

        private boolean record(String name, Object... values) {
            call = name;
            args = values;
            return true;
        }

        @Override
        public boolean interactNpcByIndex(String action, int hashIndex) {
            return record("npcByIndex", action, hashIndex);
        }

        @Override
        public boolean interactTileObject(String action, TileObject object) {
            return record("tileObject", action, object);
        }

        @Override
        public boolean interactGroundItem(String action, int itemId, int tileX, int tileY) {
            return record("groundItem", action, itemId, tileX, tileY);
        }

        @Override
        public boolean interactInventoryItem(int itemId, String action) {
            record("inventoryItem", itemId, action);
            return itemResult;
        }

        @Override
        public boolean interactInventoryItemAtSlot(int slot, int itemId, String action) {
            record("inventoryItemAtSlot", slot, itemId, action);
            return slotResult;
        }

        @Override
        public boolean useInventoryItemOnItem(int srcSlot, int srcItemId,
                                              int targetSlot, int targetItemId) {
            return record("useItemOnItem", srcSlot, srcItemId, targetSlot, targetItemId);
        }

        @Override
        public boolean useInventoryItemOnNpc(int srcSlot, int srcItemId, int npcHashIndex) {
            return record("useItemOnNpc", srcSlot, srcItemId, npcHashIndex);
        }

        @Override
        public boolean useInventoryItemOnObject(int srcSlot, int srcItemId,
                                                int objectId, int tileX, int tileY) {
            return record("useItemOnObject", srcSlot, srcItemId, objectId, tileX, tileY);
        }

        @Override
        public boolean widgetInteract(int opcode, int identifier, int param0, int param1) {
            return record("widgetInteract", opcode, identifier, param0, param1);
        }

        @Override
        public boolean widgetInteractAtPath(WidgetAddress address, int opcode,
                                            int identifier, int childSlot) {
            return record("widgetInteractAtPath", address, opcode, identifier, childSlot);
        }

        @Override
        public boolean setWidgetText(int packedId, String text) {
            return record("setWidgetText", packedId, text);
        }

        @Override
        public boolean setWidgetTextAtPath(WidgetAddress address, String text) {
            return record("setWidgetTextAtPath", address, text);
        }
    }
}
