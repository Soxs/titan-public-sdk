package net.titan.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.titan.api.internal.OverlayBackend;
import net.titan.api.internal.SchedulerBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.overlay.OverlayDraw;
import net.titan.api.overlay.OverlayPanelStyle;
import net.titan.api.utils.Magic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaApiParityTest {
    private Injector injector;
    private RecordingOverlayBackend overlayBackend;
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
        assertEquals(InterfaceId.MagicSpellbook.VARROCK_TELEPORT, varrock.widget());
        assertEquals(Magic.SpellBook.STANDARD, varrock.book());

        assertEquals(Magic.SpellBook.ANCIENT, Magic.info(Magic.Ancient.ICE_BARRAGE).book());
        assertEquals(Magic.SpellBook.LUNAR, Magic.info(Magic.Lunar.SPELLBOOK_SWAP).book());
        assertEquals(Magic.SpellBook.NECROMANCY, Magic.info(Magic.Necromancy.DEATH_CHARGE).book());
        assertTrue(Magic.canCast(Magic.Standard.FIRE_SURGE));
        assertFalse(Magic.canCast(Magic.Ancient.ICE_BARRAGE));
        assertTrue(Magic.isAutoCasting());
        assertEquals(Instant.EPOCH, Magic.lastHomeTeleportUsage());
        assertFalse(Magic.isHomeTeleportOnCooldown());
        assertFalse(Magic.cast(Magic.Standard.VARROCK_TELEPORT));
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
    }

    private static int collisionKey(int plane, int x, int y) {
        return (plane << 24) ^ (x << 12) ^ y;
    }

    private static final class TestClientState implements InvocationHandler {
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
        final Map<Integer, Integer> varps = new HashMap<>();
        final Map<Integer, Integer> collisionFlags = new HashMap<>();

        Client client() {
            return (Client) Proxy.newProxyInstance(
                Client.class.getClassLoader(), new Class<?>[] { Client.class }, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
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
            callback.run();
        }

        @Override
        public void runOnRender(Runnable callback) {
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
