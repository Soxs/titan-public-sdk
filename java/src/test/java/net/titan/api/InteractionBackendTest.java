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
    void widgetEditsAreMadeOneByOneAtTheExactAddress() {
        useBackend(new RecordingBackend());
        Widget staticWidget = widget(1234, -1, 0);
        Widget dynamicWidget = widget(2000, 3, 1000);

        assertTrue(dynamicWidget.edit().text("a b").textColor(0xFF0000).hidden(true).name("Big bones").action(0, "Bury").ok());
        assertEquals(java.util.List.of("1112 [] a b", "1101 [16711680]", "1003 [1]", "1305 [] Big\u00a0bones", "1300 [1] Bury"), backend.writes);
        WidgetAddress address = (WidgetAddress) backend.args[0];
        assertEquals(1000, address.rootPackedId());
        assertEquals(java.util.List.of(3), address.dynamicPath());

        backend.writes.clear();
        assertTrue(staticWidget.edit().textShadowed(true).textAlignment(1, 2).lineHeight(3).fontId(4).opacity(5).filled(true).spriteId(6)
            .spriteTiled(true).flipped(true, false).borderType(7).modelId(8).item(995, 20).item(995, 20, WidgetEdit.ItemQuantity.ALWAYS).ok());
        assertEquals(java.util.List.of("1115 [1]", "10008 [1]", "10009 [2]", "10010 [3]", "1113 [4]", "1103 [5]", "1102 [1]", "1105 [6]", "1107 [1]",
            "1119 [1]", "1118 [0]", "1116 [7]", "1108 [8]", "10011 [995]", "10012 [20]", "1213 [995, 20, 3]"), backend.writes);
        assertEquals(1234, ((WidgetAddress) backend.args[0]).rootPackedId());
        backend.writes.clear();
        assertTrue(staticWidget.edit().position(1, 2).positionModes(3, 4).size(5, 6).sizeModes(7, 8).scroll(9, 10).scrollSize(11, 12)
            .revalidate().revalidateScroll().clearActions().targetVerb(null).dragDeadZone(13).dragDeadTime(14).stopsClicks(true)
            .stopsScrolling(false).hasListener(true).deleteChildren().ok());
        assertEquals(java.util.List.of("10000 [1]", "10001 [2]", "10004 [3]", "10005 [4]", "10002 [5]", "10003 [6]", "10006 [7]", "10007 [8]",
            "10020 [9]", "10021 [10]", "10018 [11]", "10019 [12]", "20000 []", "20001 []", "1307 []", "1306 [] ", "1303 [13]", "1304 [14]",
            "1005 [1]", "1006 [0]", "10013 [1]", "20003 []"), backend.writes);

        backend.writes.clear(); backend.writeResult = 7;
        assertEquals(java.util.OptionalInt.of(7), staticWidget.edit().createChild(4));
        assertEquals(java.util.OptionalInt.of(7), staticWidget.edit().createChild(5, 2));
        assertEquals(java.util.List.of("20002 [4, -1]", "20002 [5, 2]"), backend.writes);

        backend.writeResult = -1;
        WidgetEdit edit = staticWidget.edit();
        edit.hidden(true);
        backend.writeResult = 0;
        assertFalse(edit.hidden(false).ok());
        assertEquals(4, backend.writes.size());
        assertFalse(staticWidget.edit().action(32, "Use").ok());
        assertFalse(staticWidget.edit().action(-1, "Use").ok());
        assertEquals(4, backend.writes.size());
    }

    @Test
    void namedWidgetActionsQueueTheAddressAndOrderedLabels() {
        useBackend(new RecordingBackend());
        Widget widget = widget(2000, 7, 1000);
        setObject(widget, "dynamicPath", java.util.List.of(3, 7));
        String[] requested = { "Quantity/All", null, " ", "\u2003", "Withdraw" };

        assertTrue(widget.interact(requested));
        assertEquals("widgetInteractByActions", backend.call);
        WidgetAddress address = (WidgetAddress) backend.args[0];
        assertEquals(1000, address.rootPackedId());
        assertEquals(java.util.List.of(3, 7), address.dynamicPath());
        requested[0] = "Changed";
        assertArrayEquals(new String[] { "Quantity/All", "\u2003", "Withdraw" }, (String[]) backend.args[1]);

        backend.widgetQueueAccepted = false;
        assertFalse(widget.interact("Withdraw"));
    }

    @Test
    void emptyActionsDoNotReachBackend() {
        useBackend(new RecordingBackend());

        assertFalse(npc(1).interact(""));
        assertFalse(tileObject(2, 3, 4).interact(null));
        assertFalse(groundItem(5, 6, 7).interact(""));
        assertFalse(inventoryItem(8, 9).interact(null));
        Widget widget = widget(1234, -1, 0);
        assertFalse(widget.interact());
        assertFalse(widget.interact((String[]) null));
        assertFalse(widget.interact(null, "", " \t\n\u000b\f\r\u00a0"));
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
        boolean widgetQueueAccepted = true;
        final java.util.List<String> writes = new java.util.ArrayList<>();
        int writeResult;

        @Override
        public int writeWidget(WidgetAddress address, int write, int[] integers, String text) {
            record("writeWidget", address, write, integers, text);
            writes.add(write + " " + java.util.Arrays.toString(integers) + (text == null ? "" : " " + text));
            return writeResult;
        }

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
        public boolean widgetInteractByActions(WidgetAddress address, String[] actions) {
            record("widgetInteractByActions", address, actions);
            return widgetQueueAccepted;
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
