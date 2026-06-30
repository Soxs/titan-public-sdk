package net.titan.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.titan.api.internal.OverlayBackend;
import net.titan.api.internal.MouseBackend;
import net.titan.api.internal.SchedulerBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.overlay.OverlayDraw;
import net.titan.api.overlay.OverlayPanelStyle;
import net.titan.api.utils.Magic;
import net.titan.api.utils.Mouse;
import net.titan.gamevals.InterfaceID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaApiParityTest {
    private Injector injector;
    private RecordingOverlayBackend overlayBackend;
    private MouseBackend mouseBackend;
    private RecordingSchedulerBackend schedulerBackend;

    @AfterEach
    void clearRuntime() {
        if (injector != null) {
            TitanRuntime.clearInjector(injector);
            injector = null;
        }
        if (overlayBackend != null) {
            TitanRuntime.clearOverlayBackend(overlayBackend);
            overlayBackend = null;
        }
        if (schedulerBackend != null) {
            TitanRuntime.clearSchedulerBackend(schedulerBackend);
            schedulerBackend = null;
        }
        if (mouseBackend != null) {
            TitanRuntime.clearMouseBackend(mouseBackend);
            mouseBackend = null;
        }
    }

    @Test
    void magicCatalogAndPredicatesMatchCppTsShape() {
        TestClientState state = new TestClientState();
        state.spellBook = Magic.SpellBook.STANDARD.id();
        state.realMagic = 99;
        state.boostedMagic = 99;
        state.varps.put(165, 30);
        state.varps.put(335, 110);
        state.varps.put(108, 1);
        installClient(state);

        Magic.SpellInfo varrock = Magic.info(Magic.Standard.VARROCK_TELEPORT);
        assertEquals("VARROCK_TELEPORT", varrock.name());
        assertEquals(25, varrock.level());
        assertEquals(InterfaceID.MagicSpellbook.VARROCK_TELEPORT, varrock.widget());
        assertEquals(Magic.SpellBook.STANDARD, varrock.book());

        assertEquals(Magic.SpellBook.ANCIENT, Magic.info(Magic.Ancient.ICE_BARRAGE).book());
        assertEquals(Magic.SpellBook.LUNAR, Magic.info(Magic.Lunar.SPELLBOOK_SWAP).book());
        assertEquals(Magic.SpellBook.NECROMANCY, Magic.info(Magic.Necromancy.DEATH_CHARGE).book());
        assertTrue(Magic.canCast(Magic.Standard.FIRE_SURGE));
        assertFalse(Magic.canCast(Magic.Ancient.ICE_BARRAGE));
        assertTrue(Magic.isAutoCasting());
        assertEquals(Instant.EPOCH, Magic.lastHomeTeleportUsage());
        assertFalse(Magic.isHomeTeleportOnCooldown());
        assertTrue(Magic.cast(Magic.Standard.VARROCK_TELEPORT));
        assertEquals(MenuAction.WIDGET_TARGET, state.lastWidgetOpcode);
        assertEquals(0, state.lastWidgetIdentifier);
        assertEquals(-1, state.lastWidgetParam0);
        assertEquals(InterfaceID.MagicSpellbook.VARROCK_TELEPORT, state.lastWidgetParam1);

        assertTrue(Magic.cast(Magic.Standard.VARROCK_TELEPORT, 2));
        assertEquals(MenuAction.CC_OP, state.lastWidgetOpcode);
        assertEquals(3, state.lastWidgetIdentifier);
        assertEquals(-1, state.lastWidgetParam0);
        assertEquals(InterfaceID.MagicSpellbook.VARROCK_TELEPORT, state.lastWidgetParam1);

        assertTrue(Magic.cast(Magic.Standard.VARROCK_TELEPORT, 4, MenuAction.CC_OP_LOW_PRIORITY));
        assertEquals(MenuAction.CC_OP_LOW_PRIORITY, state.lastWidgetOpcode);
        assertEquals(5, state.lastWidgetIdentifier);
    }

    @Test
    void magicTargetedCastsSelectSpellThenDispatchTargetOnClientTick() throws Exception {
        TestClientState state = new TestClientState();
        installClient(state);
        schedulerBackend = new RecordingSchedulerBackend();
        TitanRuntime.setSchedulerBackend(schedulerBackend);

        NPC npc = setField(setField(setField(setField(setField(new NPC(), "hashIndex", 12),
            "plane", 2), "sizeX", 2), "sizeY", 3), "entityPtr", 0x1111L);
        assertMagicTarget(state, () -> Magic.castOn(Magic.Standard.WIND_STRIKE, npc),
            MenuAction.WIDGET_TARGET_ON_NPC, 12, 0, 0,
            2, 2, 3, -1, 0x1111L, 0L);

        Player player = setField(setField(setField(new Player(), "hashIndex", 13),
            "plane", 1), "entityPtr", 0x2222L);
        assertMagicTarget(state, () -> Magic.castOn(Magic.Standard.WIND_STRIKE, player),
            MenuAction.WIDGET_TARGET_ON_PLAYER, 13, 0, 0,
            1, 1, 1, -1, 0x2222L, 0L);

        TileObject object = setField(setField(setField(setField(setField(setField(setField(
            setField(setField(new TileObject(), "id", 100), "tileX", 31), "tileY", 45),
            "plane", 3), "sizeX", 4), "sizeY", 5), "layer", 2),
            "entityPtr", 0x3333L), "packedId", 0x4444L);
        assertMagicTarget(state, () -> Magic.castOn(Magic.Standard.WIND_STRIKE, object),
            MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, 100, 31, 45,
            3, 4, 5, 2, 0x3333L, 0x4444L);
        assertMagicTarget(state, () -> object.castOn(Magic.Standard.WIND_STRIKE),
            MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, 100, 31, 45,
            3, 4, 5, 2, 0x3333L, 0x4444L);

        GroundItem item = setField(setField(setField(setField(new GroundItem(), "id", 995),
            "tileX", 32), "tileY", 46), "plane", 2);
        assertMagicTarget(state, () -> Magic.castOn(Magic.Standard.WIND_STRIKE, item),
            MenuAction.WIDGET_TARGET_ON_GROUND_ITEM, 995, 32, 46,
            2, 1, 1, -1, 0L, 0L);

        InventoryItem inventoryItem = setField(setField(new InventoryItem(), "slot", 7), "id", 561);
        assertMagicWidgetTarget(state, () -> Magic.castOn(Magic.Standard.WIND_STRIKE, inventoryItem),
            InterfaceID.MagicSpellbook.WIND_STRIKE, 7, InterfaceID.Inventory.ITEMS);
        assertMagicWidgetTarget(state,
            () -> inventoryItem.castOn(Magic.Standard.HIGH_LEVEL_ALCHEMY),
            InterfaceID.MagicSpellbook.HIGH_ALCHEMY,
            7,
            InterfaceID.Inventory.ITEMS);

        Widget widget = setField(setField(new Widget(), "packedId", 0x12340056),
            "dynamicChildSlot", 9);
        assertMagicWidgetTarget(state, () -> Magic.castOn(Magic.Standard.WIND_STRIKE, widget),
            InterfaceID.MagicSpellbook.WIND_STRIKE, 9, 0x12340056);
        assertMagicWidgetTarget(state, () -> widget.castOn(Magic.Standard.WIND_STRIKE),
            InterfaceID.MagicSpellbook.WIND_STRIKE, 9, 0x12340056);

        Magic.WidgetTarget widgetTarget = new Magic.WidgetTarget(0x23450067, 11, 4151);
        assertMagicWidgetTarget(state, () -> Magic.castOn(Magic.Standard.WIND_STRIKE, widgetTarget),
            InterfaceID.MagicSpellbook.WIND_STRIKE, 11, 0x23450067);
        assertMagicWidgetTarget(state, () -> widgetTarget.castOn(Magic.Standard.WIND_STRIKE),
            InterfaceID.MagicSpellbook.WIND_STRIKE, 11, 0x23450067);
    }

    @Test
    void resolveActionClickPointPreservesCurrentWorldViewSentinel() {
        TestClientState state = new TestClientState();
        installClient(state);

        Optional<ScreenPoint> point = Mouse.resolveActionClickPoint(
            MenuAction.WIDGET_TARGET_ON_NPC, 12, 0, 0, WorldView.CURRENT,
            -1, 1, 1, -1, 0, 0);

        assertTrue(point.isPresent());
        assertEquals(1, state.resolveClickCalls);
        assertEquals(WorldView.CURRENT, state.lastResolveWorldViewId);
        assertEquals(-1, state.lastResolveTargetPlane);
        assertEquals(1, state.lastResolveTargetSizeX);
        assertEquals(1, state.lastResolveTargetSizeY);
        assertEquals(-1, state.lastResolveTargetLayer);
    }

    @Test
    void magicCanCastRequiresBoostedLevelAndStandardUnlocks() {
        TestClientState state = new TestClientState();
        state.spellBook = Magic.SpellBook.STANDARD.id();
        state.realMagic = 99;
        state.boostedMagic = 80;
        state.varps.put(165, 29);
        state.varps.put(335, 109);
        installClient(state);

        assertFalse(Magic.canCast(Magic.Standard.FIRE_SURGE));
        state.boostedMagic = 99;
        assertFalse(Magic.canCast(Magic.Standard.ARDOUGNE_TELEPORT));
        assertFalse(Magic.canCast(Magic.Standard.TROLLHEIM_TELEPORT));
    }

    @Test
    void lineOfSightUsesCollisionFlagsAndWorldViews() {
        TestClientState state = new TestClientState();
        state.baseX = 0;
        state.baseY = 0;
        state.sceneSizeX = 104;
        state.sceneSizeY = 104;
        state.currentWorldViewId = 42;
        installClient(state);

        WorldPoint from = new WorldPoint(1, 1, 0);
        assertTrue(from.hasLineOfSight(new WorldPoint(5, 1, 0, 42)));

        state.collisionFlags.put(collisionKey(0, 3, 1), 0x00010000);
        assertFalse(from.hasLineOfSight(new WorldPoint(5, 1, 0, 42)));
        assertFalse(new WorldPoint(1, 1, 0, 1).hasLineOfSight(new WorldPoint(5, 1, 0, 2)));
    }

    @Test
    void overlayWorldViewHelpersConvertWorldTilesAgainstTopLevelBase() {
        TestClientState state = new TestClientState();
        state.baseX = 100;
        state.baseY = 200;
        state.topLevelBaseX = 3200;
        state.topLevelBaseY = 3210;
        installClient(state);
        overlayBackend = new RecordingOverlayBackend();
        TitanRuntime.setOverlayBackend(overlayBackend);

        OverlayDraw.current().worldTileRegionInWorldView(
            WorldView.TOP_LEVEL, 3205, 3216, 3208, 3219, 0, 0x11, 0x22);
        assertEquals("regionInWorldView", overlayBackend.call);
        assertEquals(5, overlayBackend.values[1]);
        assertEquals(6, overlayBackend.values[2]);
        assertEquals(8, overlayBackend.values[3]);
        assertEquals(9, overlayBackend.values[4]);

        OverlayDraw.current().textAtWorldTileInWorldView(
            WorldView.TOP_LEVEL, 3201, 64, 3212, 0, "x", 0x33);
        assertEquals("textInWorldView", overlayBackend.call);
        assertEquals(128, overlayBackend.values[1]);
        assertEquals(256, overlayBackend.values[3]);

        assertEquals(777, OverlayDraw.current().worldTileHeightInWorldView(
            WorldView.TOP_LEVEL, 3202, 3213, 0));
        assertEquals(256, overlayBackend.values[1]);
        assertEquals(384, overlayBackend.values[2]);
    }

    @Test
    void schedulerFacadeRoutesThroughRuntimeBackend() {
        schedulerBackend = new RecordingSchedulerBackend();
        TitanRuntime.setSchedulerBackend(schedulerBackend);

        Titan.runOnClientTick(() -> schedulerBackend.clientTickRan = true);
        Titan.runOnRender(() -> schedulerBackend.renderRan = true);

        assertTrue(schedulerBackend.clientTickRan);
        assertTrue(schedulerBackend.renderRan);
    }

    @Test
    void constantCatalogsExposeTsNames() {
        assertEquals(2, ChatMessageType.PUBLICCHAT);
        assertEquals(5517, ScriptId.CHAT_SEND);
        assertEquals(2343, QuestId.DESERT_TREASURE_II);
        assertEquals(1, KeyboardTypeCallbackPhase.CLIENT_TICK);
        assertNotNull((KeyboardTypeCallback) completed -> {});
    }

    private void installClient(TestClientState state) {
        Client client = state.client();
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Client.class).toInstance(client);
            }
        });
        TitanRuntime.setInjector(injector);
        mouseBackend = state;
        TitanRuntime.setMouseBackend(state);
    }

    private static int collisionKey(int plane, int x, int y) {
        return (plane << 24) ^ (x << 12) ^ y;
    }

    private void assertMagicTarget(TestClientState state, BooleanSupplier castAction, int opcode,
                                   int identifier, int param0, int param1,
                                   int targetPlane, int targetSizeX, int targetSizeY,
                                   int targetLayer, long targetEntityPtr,
                                   long targetPackedId) {
        state.clearActions();
        schedulerBackend.clientTickRan = false;

        assertTrue(castAction.getAsBoolean());
        assertTrue(schedulerBackend.clientTickRan);
        assertEquals(1, state.widgetInteractCalls);
        assertEquals(MenuAction.WIDGET_TARGET, state.firstWidgetOpcode);
        assertEquals(0, state.firstWidgetIdentifier);
        assertEquals(-1, state.firstWidgetParam0);
        assertEquals(InterfaceID.MagicSpellbook.WIND_STRIKE, state.firstWidgetParam1);

        assertEquals(1, state.menuActionCalls);
        assertEquals(opcode, state.lastMenuOpcode);
        assertEquals(identifier, state.lastMenuIdentifier);
        assertEquals(param0, state.lastMenuParam0);
        assertEquals(param1, state.lastMenuParam1);
        assertEquals(0L, state.lastMenuWorldViewId);
        assertEquals(1, state.resolveClickCalls);
        assertEquals(opcode, state.lastResolveOpcode);
        assertEquals(identifier, state.lastResolveIdentifier);
        assertEquals(param0, state.lastResolveParam0);
        assertEquals(param1, state.lastResolveParam1);
        assertEquals(WorldView.CURRENT, state.lastResolveWorldViewId);
        assertEquals(targetPlane, state.lastResolveTargetPlane);
        assertEquals(targetSizeX, state.lastResolveTargetSizeX);
        assertEquals(targetSizeY, state.lastResolveTargetSizeY);
        assertEquals(targetLayer, state.lastResolveTargetLayer);
        assertEquals(targetEntityPtr, state.lastResolveTargetEntityPtr);
        assertEquals(targetPackedId, state.lastResolveTargetPackedId);
        assertEquals(321, state.lastMenuClickX);
        assertEquals(654, state.lastMenuClickY);
        assertEquals("Cast", state.lastMenuActionText);
        assertEquals("", state.lastMenuTargetText);
    }

    private void assertMagicWidgetTarget(TestClientState state, BooleanSupplier castAction,
                                         int spellWidget, int childIndex, int packedId) {
        state.clearActions();
        schedulerBackend.clientTickRan = false;

        assertTrue(castAction.getAsBoolean());
        assertTrue(schedulerBackend.clientTickRan);
        assertEquals(2, state.widgetInteractCalls);
        assertEquals(MenuAction.WIDGET_TARGET, state.firstWidgetOpcode);
        assertEquals(0, state.firstWidgetIdentifier);
        assertEquals(-1, state.firstWidgetParam0);
        assertEquals(spellWidget, state.firstWidgetParam1);
        assertEquals(MenuAction.WIDGET_TARGET_ON_WIDGET, state.lastWidgetOpcode);
        assertEquals(0, state.lastWidgetIdentifier);
        assertEquals(childIndex, state.lastWidgetParam0);
        assertEquals(packedId, state.lastWidgetParam1);
        assertEquals(0, state.menuActionCalls);
        assertEquals(0, state.resolveClickCalls);
    }

    private static <T> T setField(T target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
        return target;
    }

    private static ScreenPoint screenPoint(int x, int y) throws Exception {
        ScreenPoint point = new ScreenPoint();
        setField(point, "x", x);
        setField(point, "y", y);
        return point;
    }

    private static final class TestClientState implements InvocationHandler, MouseBackend {
        int baseX = 0;
        int baseY = 0;
        int sceneSizeX = 104;
        int sceneSizeY = 104;
        int topLevelBaseX = 0;
        int topLevelBaseY = 0;
        int currentWorldViewId = WorldView.CURRENT;
        int spellBook = Magic.SpellBook.STANDARD.id();
        int realMagic = 1;
        int boostedMagic = 1;
        int widgetInteractCalls = 0;
        int firstWidgetOpcode = -1;
        int firstWidgetIdentifier = 0;
        int firstWidgetParam0 = 0;
        int firstWidgetParam1 = 0;
        int lastWidgetOpcode = -1;
        int lastWidgetIdentifier = 0;
        int lastWidgetParam0 = 0;
        int lastWidgetParam1 = 0;
        int menuActionCalls = 0;
        long lastMenuOpcode = -1;
        int lastMenuIdentifier = 0;
        int lastMenuParam0 = 0;
        int lastMenuParam1 = 0;
        long lastMenuWorldViewId = -1;
        int lastMenuClickX = 0;
        int lastMenuClickY = 0;
        String lastMenuActionText = null;
        String lastMenuTargetText = null;
        int resolveClickCalls = 0;
        long lastResolveOpcode = -1;
        int lastResolveIdentifier = 0;
        int lastResolveParam0 = 0;
        int lastResolveParam1 = 0;
        long lastResolveWorldViewId = -1;
        int lastResolveTargetPlane = 0;
        int lastResolveTargetSizeX = 0;
        int lastResolveTargetSizeY = 0;
        int lastResolveTargetLayer = 0;
        long lastResolveTargetEntityPtr = 0;
        long lastResolveTargetPackedId = 0;
        final Map<Integer, Integer> varps = new HashMap<>();
        final Map<Integer, Integer> collisionFlags = new HashMap<>();

        Client client() {
            return (Client) Proxy.newProxyInstance(
                Client.class.getClassLoader(), new Class<?>[] { Client.class }, this);
        }

        @Override
        public Optional<ScreenPoint> resolveActionClickPoint(long opcode, int identifier,
                                                             int param0, int param1,
                                                             long worldViewId,
                                                             int targetPlane,
                                                             int targetSizeX,
                                                             int targetSizeY,
                                                             int targetLayer,
                                                             long targetEntityPtr,
                                                             long targetPackedId) {
            resolveClickCalls++;
            lastResolveOpcode = opcode;
            lastResolveIdentifier = identifier;
            lastResolveParam0 = param0;
            lastResolveParam1 = param1;
            lastResolveWorldViewId = worldViewId;
            lastResolveTargetPlane = targetPlane;
            lastResolveTargetSizeX = targetSizeX;
            lastResolveTargetSizeY = targetSizeY;
            lastResolveTargetLayer = targetLayer;
            lastResolveTargetEntityPtr = targetEntityPtr;
            lastResolveTargetPackedId = targetPackedId;
            try {
                return Optional.of(screenPoint(321, 654));
            } catch (Exception ex) {
                return Optional.empty();
            }
        }

        void clearActions() {
            widgetInteractCalls = 0;
            firstWidgetOpcode = -1;
            firstWidgetIdentifier = 0;
            firstWidgetParam0 = 0;
            firstWidgetParam1 = 0;
            lastWidgetOpcode = -1;
            lastWidgetIdentifier = 0;
            lastWidgetParam0 = 0;
            lastWidgetParam1 = 0;
            menuActionCalls = 0;
            lastMenuOpcode = -1;
            lastMenuIdentifier = 0;
            lastMenuParam0 = 0;
            lastMenuParam1 = 0;
            lastMenuWorldViewId = -1;
            lastMenuClickX = 0;
            lastMenuClickY = 0;
            lastMenuActionText = null;
            lastMenuTargetText = null;
            resolveClickCalls = 0;
            lastResolveOpcode = -1;
            lastResolveIdentifier = 0;
            lastResolveParam0 = 0;
            lastResolveParam1 = 0;
            lastResolveWorldViewId = -1;
            lastResolveTargetPlane = 0;
            lastResolveTargetSizeX = 0;
            lastResolveTargetSizeY = 0;
            lastResolveTargetLayer = 0;
            lastResolveTargetEntityPtr = 0;
            lastResolveTargetPackedId = 0;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
            String name = method.getName();
            if ("toString".equals(name)) return "TestClient";
            if ("baseX".equals(name)) return baseX;
            if ("baseY".equals(name)) return baseY;
            if ("sceneSizeX".equals(name)) return sceneSizeX;
            if ("sceneSizeY".equals(name)) return sceneSizeY;
            if ("topLevelBaseX".equals(name)) return topLevelBaseX;
            if ("topLevelBaseY".equals(name)) return topLevelBaseY;
            if ("currentWorldViewId".equals(name)) return currentWorldViewId;
            if ("varbit".equals(name)) {
                int id = (Integer) args[0];
                if (id == Magic.SPELLBOOK_VARBIT) return spellBook;
                return 0;
            }
            if ("varp".equals(name)) return varps.getOrDefault((Integer) args[0], 0);
            if ("realSkillLevel".equals(name)) return realMagic;
            if ("boostedSkillLevel".equals(name)) return boostedMagic;
            if ("collisionFlag".equals(name)) {
                return collisionFlags.getOrDefault(
                    collisionKey((Integer) args[0], (Integer) args[1], (Integer) args[2]), 0);
            }
            if ("widgetInteract".equals(name)) {
                int opcode = (Integer) args[0];
                int identifier = (Integer) args[1];
                int param0 = (Integer) args[2];
                int param1 = (Integer) args[3];
                if (widgetInteractCalls == 0) {
                    firstWidgetOpcode = opcode;
                    firstWidgetIdentifier = identifier;
                    firstWidgetParam0 = param0;
                    firstWidgetParam1 = param1;
                }
                widgetInteractCalls++;
                lastWidgetOpcode = opcode;
                lastWidgetIdentifier = identifier;
                lastWidgetParam0 = param0;
                lastWidgetParam1 = param1;
                return true;
            }
            if ("invokeMenuAction".equals(name)) {
                menuActionCalls++;
                lastMenuOpcode = (Long) args[0];
                lastMenuIdentifier = (Integer) args[1];
                lastMenuParam0 = (Integer) args[2];
                lastMenuParam1 = (Integer) args[3];
                lastMenuWorldViewId = (Long) args[4];
                lastMenuClickX = (Integer) args[5];
                lastMenuClickY = (Integer) args[6];
                lastMenuActionText = (String) args[7];
                lastMenuTargetText = (String) args[8];
                return true;
            }
            Class<?> type = method.getReturnType();
            if (type == boolean.class) return false;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == void.class) return null;
            if (Optional.class.equals(type)) return Optional.empty();
            if (java.util.List.class.equals(type)) return Collections.emptyList();
            return null;
        }
    }

    private static final class RecordingSchedulerBackend implements SchedulerBackend {
        boolean clientTickRan;
        boolean renderRan;

        @Override
        public void runOnClientTick(Runnable callback) {
            clientTickRan = true;
            callback.run();
        }

        @Override
        public void runOnRender(Runnable callback) {
            renderRan = true;
            callback.run();
        }
    }

    private static final class RecordingOverlayBackend implements OverlayBackend {
        String call;
        int[] values = new int[8];

        @Override
        public void tileQuad(int tileX, int tileY, int plane, int fillColor, int outlineColor) {}

        @Override
        public void tileRegion(int minTileX, int minTileY, int maxTileX, int maxTileY,
                               int plane, int fillColor, int outlineColor) {}

        @Override
        public void tileQuadInWorldView(int worldViewId, int tileX, int tileY, int plane,
                                        int fillColor, int outlineColor) {
            call = "quadInWorldView";
            values = new int[] { worldViewId, tileX, tileY, plane, fillColor, outlineColor };
        }

        @Override
        public void tileRegionInWorldView(int worldViewId, int minTileX, int minTileY,
                                          int maxTileX, int maxTileY, int plane,
                                          int fillColor, int outlineColor) {
            call = "regionInWorldView";
            values = new int[] { worldViewId, minTileX, minTileY, maxTileX, maxTileY,
                plane, fillColor, outlineColor };
        }

        @Override
        public void entityBox(int preciseX, int preciseY, int plane,
                              int tileSize, int height, int color) {}

        @Override
        public void entityClickbox(long entityPtr, long typecode, int outline, int fill) {}

        @Override
        public void tileObjectClickbox(long locPtr, long typecode, int outline, int fill) {}

        @Override
        public void entityHull(long entityPtr, long typecode, int outline, int fill) {}

        @Override
        public void tileObjectHull(long locPtr, long typecode, int outline, int fill) {}

        @Override
        public void textAtWorld(int worldX, int worldY, int worldZ,
                                String text, int color, boolean centered) {}

        @Override
        public void textAtWorldInWorldView(int worldViewId, int preciseX, int worldY,
                                           int preciseY, int plane, String text,
                                           int color, boolean centered) {
            call = "textInWorldView";
            values = new int[] { worldViewId, preciseX, worldY, preciseY, plane, color };
        }

        @Override
        public void screenText(int x, int y, String text, int color) {}

        @Override
        public void screenRect(int x, int y, int width, int height, int color) {}

        @Override
        public void screenLine(int x1, int y1, int x2, int y2, int color, float thickness) {}

        @Override
        public Optional<ScreenPoint> worldToScreen(int worldX, int worldY, int worldZ) {
            return Optional.empty();
        }

        @Override
        public Optional<ScreenPoint> worldToScreenInWorldView(int worldViewId, int preciseX,
                                                              int worldY, int preciseY,
                                                              int plane) {
            return Optional.empty();
        }

        @Override
        public Optional<ScreenPoint> tileToScreen(int tileX, int tileY,
                                                  int plane, int heightOffset) {
            return Optional.empty();
        }

        @Override
        public int tileHeight(int preciseX, int preciseY, int plane) {
            return 0;
        }

        @Override
        public int tileHeightInWorldView(int worldViewId, int preciseX, int preciseY, int plane) {
            call = "heightInWorldView";
            values = new int[] { worldViewId, preciseX, preciseY, plane };
            return 777;
        }

        @Override
        public int overlayPanelRegister(String pluginId, String panelName,
                                        int defaultAnchor, int defaultPriority) {
            return 0;
        }

        @Override
        public void overlayPanelUnregister(int handle) {}

        @Override
        public void overlayPanelBegin(int handle, int preferredWidth) {}

        @Override
        public void overlayPanelEnd(int handle) {}

        @Override
        public void overlayPanelSetStyle(int handle, OverlayPanelStyle style) {}

        @Override
        public void overlayPanelTitle(int handle, String text, int color) {}

        @Override
        public void overlayPanelLine(int handle, String left, String right,
                                     int leftColor, int rightColor) {}

        @Override
        public void overlayPanelProgressBar(int handle, int value, int minValue,
                                            int maxValue, int fillColor,
                                            int backgroundColor) {}
    }
}
