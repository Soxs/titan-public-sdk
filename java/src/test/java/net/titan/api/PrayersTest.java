package net.titan.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.LiveStateBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.utils.Prayers;
import net.titan.gamevals.InterfaceID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrayersTest {
    private static final int[] EXPECTED_WIDGETS = {
        // Standard prayer book.
        InterfaceID.Prayerbook.PRAYER1,
        InterfaceID.Prayerbook.PRAYER2,
        InterfaceID.Prayerbook.PRAYER3,
        InterfaceID.Prayerbook.PRAYER19,
        InterfaceID.Prayerbook.PRAYER22,
        InterfaceID.Prayerbook.PRAYER4,
        InterfaceID.Prayerbook.PRAYER5,
        InterfaceID.Prayerbook.PRAYER6,
        InterfaceID.Prayerbook.PRAYER7,
        InterfaceID.Prayerbook.PRAYER8,
        InterfaceID.Prayerbook.PRAYER9,
        InterfaceID.Prayerbook.PRAYER20,
        InterfaceID.Prayerbook.PRAYER23,
        InterfaceID.Prayerbook.PRAYER10,
        InterfaceID.Prayerbook.PRAYER11,
        InterfaceID.Prayerbook.PRAYER12,
        InterfaceID.Prayerbook.PRAYER13,
        InterfaceID.Prayerbook.PRAYER14,
        InterfaceID.Prayerbook.PRAYER15,
        InterfaceID.Prayerbook.PRAYER21,
        InterfaceID.Prayerbook.PRAYER24,
        InterfaceID.Prayerbook.PRAYER16,
        InterfaceID.Prayerbook.PRAYER17,
        InterfaceID.Prayerbook.PRAYER18,
        InterfaceID.Prayerbook.PRAYER26,
        InterfaceID.Prayerbook.PRAYER21,
        InterfaceID.Prayerbook.PRAYER24,
        InterfaceID.Prayerbook.PRAYER27,
        InterfaceID.Prayerbook.PRAYER29,
        InterfaceID.Prayerbook.PRAYER25,
        InterfaceID.Prayerbook.PRAYER28,

        // Ruinous Powers, in Prayer enum order.
        InterfaceID.Prayerbook.PRAYER12,
        InterfaceID.Prayerbook.PRAYER1,
        InterfaceID.Prayerbook.PRAYER2,
        InterfaceID.Prayerbook.PRAYER3,
        InterfaceID.Prayerbook.PRAYER24,
        InterfaceID.Prayerbook.PRAYER13,
        InterfaceID.Prayerbook.PRAYER10,
        InterfaceID.Prayerbook.PRAYER9,
        InterfaceID.Prayerbook.PRAYER8,
        InterfaceID.Prayerbook.PRAYER4,
        InterfaceID.Prayerbook.PRAYER16,
        InterfaceID.Prayerbook.PRAYER11,
        InterfaceID.Prayerbook.PRAYER15,
        InterfaceID.Prayerbook.PRAYER22,
        InterfaceID.Prayerbook.PRAYER23,
        InterfaceID.Prayerbook.PRAYER5,
        InterfaceID.Prayerbook.PRAYER6,
        InterfaceID.Prayerbook.PRAYER7,
        InterfaceID.Prayerbook.PRAYER17,
        InterfaceID.Prayerbook.PRAYER19,
        InterfaceID.Prayerbook.PRAYER18,
        InterfaceID.Prayerbook.PRAYER20,
        InterfaceID.Prayerbook.PRAYER14,
        InterfaceID.Prayerbook.PRAYER21,
    };

    private Injector injector;
    private RecordingBackend backend;
    private LiveStateBackend liveStateBackend;

    @AfterEach
    void clearRuntime() {
        if (injector != null) {
            TitanRuntime.clearInjector(injector);
            injector = null;
        }
        if (backend != null) {
            TitanRuntime.clearInteractionBackend(backend);
            backend = null;
        }
        if (liveStateBackend != null) {
            TitanRuntime.clearLiveStateBackend(liveStateBackend);
            liveStateBackend = null;
        }
    }

    @Test
    void togglesEveryMappedWidgetWithoutReadingPrayerState() {
        ClientState state = install();
        Prayer[] prayers = Prayer.values();
        assertEquals(prayers.length, EXPECTED_WIDGETS.length);

        for (int i = 0; i < prayers.length; ++i) {
            state.reset();
            backend.reset();
            state.activePrayer = i;

            assertTrue(Prayers.toggle(prayers[i]), "toggle ordinal " + i);
            assertEquals(0, state.prayerReads, "toggle state reads " + i);
            assertEquals(1, state.widgetLookups, "widget lookups " + i);
            assertEquals(EXPECTED_WIDGETS[i], state.lastWidgetLookup,
                "mapped widget ordinal " + i);
            assertEquals(1, backend.interactions, "interactions " + i);
            assertEquals(MenuAction.CC_OP, backend.opcode, "opcode " + i);
            assertEquals(1, backend.identifier, "identifier " + i);
            assertEquals(-1, backend.param0, "param0 " + i);
            assertEquals(EXPECTED_WIDGETS[i], backend.param1, "param1 " + i);
        }
    }

    @Test
    void readsEveryPrayerOrdinal() {
        ClientState state = install();
        Prayer[] prayers = Prayer.values();

        for (int i = 0; i < prayers.length; ++i) {
            state.reset();
            state.activePrayer = i;
            assertTrue(Prayers.isActive(prayers[i]), "active ordinal " + i);
            assertEquals(1, state.prayerReads, "active reads " + i);
            assertEquals(prayers[i].varbitId(), state.lastPrayerRead,
                "forwarded varbit " + i);
        }
    }

    @Test
    void settersAreIdempotentAndConvenienceMethodsDelegate() {
        ClientState state = install();
        Prayer prayer = Prayer.PROTECT_FROM_MELEE;
        int ordinal = prayer.ordinal();
        int packedId = InterfaceID.Prayerbook.PRAYER15;

        state.activePrayer = ordinal;
        assertTrue(Prayers.setActive(prayer, true));
        assertEquals(1, state.prayerReads);
        assertEquals(0, state.widgetLookups);
        assertEquals(0, backend.interactions);

        state.reset();
        backend.reset();
        state.activePrayer = -1;
        assertTrue(Prayers.setActive(prayer, false));
        assertEquals(1, state.prayerReads);
        assertEquals(0, state.widgetLookups);
        assertEquals(0, backend.interactions);

        state.reset();
        backend.reset();
        state.activePrayer = -1;
        assertTrue(Prayers.enable(prayer));
        assertEquals(1, state.prayerReads);
        assertEquals(packedId, state.lastWidgetLookup);
        assertEquals(1, backend.interactions);

        state.reset();
        backend.reset();
        state.activePrayer = ordinal;
        assertTrue(Prayers.disable(prayer));
        assertEquals(1, state.prayerReads);
        assertEquals(packedId, state.lastWidgetLookup);
        assertEquals(1, backend.interactions);
    }

    @Test
    void invalidAndUnavailableRuntimeInputsFailClosed() {
        ClientState state = install();

        assertFalse(Prayers.isActive(null));
        assertFalse(Prayers.toggle(null));
        assertFalse(Prayers.setActive(null, true));
        assertFalse(Prayers.setActive(null, false));
        assertFalse(Prayers.enable(null));
        assertFalse(Prayers.disable(null));
        assertEquals(0, state.prayerReads);
        assertEquals(0, state.widgetLookups);
        assertEquals(0, backend.interactions);

        state.reset();
        backend.reset();
        state.varbitAvailable = false;
        assertFalse(Prayers.setActive(Prayer.THICK_SKIN, false));
        assertFalse(Prayers.disable(Prayer.THICK_SKIN));
        assertEquals(0, state.widgetLookups);
        assertEquals(0, backend.interactions);

        TitanRuntime.clearInjector(injector);
        TitanRuntime.clearInteractionBackend(backend);
        assertFalse(Prayers.isActive(Prayer.THICK_SKIN));
        assertFalse(Prayers.toggle(Prayer.THICK_SKIN));
        assertFalse(Prayers.setActive(Prayer.THICK_SKIN, false));
        assertFalse(Prayers.enable(Prayer.THICK_SKIN));
        assertFalse(Prayers.disable(Prayer.THICK_SKIN));
    }

    @Test
    void missingWidgetsAndRejectedInteractionsReturnFalse() {
        ClientState state = install();

        state.widgetAvailable = false;
        assertFalse(Prayers.toggle(Prayer.THICK_SKIN));
        assertEquals(1, state.widgetLookups);
        assertEquals(0, backend.interactions);

        state.reset();
        backend.reset();
        state.widgetAvailable = true;
        backend.accepted = false;
        assertFalse(Prayers.toggle(Prayer.THICK_SKIN));
        assertEquals(1, state.widgetLookups);
        assertEquals(1, backend.interactions);
    }

    @Test
    void staleWidgetHandlesReturnFalseWithoutDispatching() {
        ClientState state = install();
        liveStateBackend = new LiveStateBackend() {
            @Override
            public <T> T current(T value) {
                return value;
            }

            @Override
            public boolean exists(Object value) {
                return false;
            }

            @Override
            public String identity(Object value) {
                return null;
            }

            @Override
            public <T> T snapshot(T value) {
                return value;
            }
        };
        TitanRuntime.setLiveStateBackend(liveStateBackend);

        assertFalse(Prayers.toggle(Prayer.THICK_SKIN));
        assertEquals(1, state.widgetLookups);
        assertEquals(0, backend.interactions);
    }

    private ClientState install() {
        ClientState state = new ClientState();
        Client client = state.client();
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Client.class).toInstance(client);
            }
        });
        TitanRuntime.setInjector(injector);
        backend = new RecordingBackend();
        TitanRuntime.setInteractionBackend(backend);
        return state;
    }

    private static Widget widget(int packedId) {
        Widget widget = new Widget();
        try {
            Field field = Widget.class.getDeclaredField("packedId");
            field.setAccessible(true);
            field.setInt(widget, packedId);
            return widget;
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }

    private static final class ClientState implements InvocationHandler {
        int activePrayer = -1;
        int prayerReads;
        int lastPrayerRead = -1;
        int widgetLookups;
        int lastWidgetLookup;
        boolean widgetAvailable = true;
        boolean varbitAvailable = true;

        Client client() {
            return (Client) Proxy.newProxyInstance(
                Client.class.getClassLoader(), new Class<?>[] {Client.class}, this);
        }

        void reset() {
            prayerReads = 0;
            lastPrayerRead = -1;
            widgetLookups = 0;
            lastWidgetLookup = 0;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            switch (method.getName()) {
                case "toString":
                    return "PrayerTestClient";
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "equals":
                    return proxy == args[0];
                case "varbit":
                    ++prayerReads;
                    lastPrayerRead = (Integer) args[0];
                    if (!varbitAvailable) return -1;
                    if (activePrayer < 0 ||
                            activePrayer >= Prayer.values().length) {
                        return 0;
                    }
                    return lastPrayerRead ==
                        Prayer.values()[activePrayer].varbitId() ? 1 : 0;
                case "widget":
                    ++widgetLookups;
                    lastWidgetLookup = (Integer) args[0];
                    return widgetAvailable
                        ? Optional.of(widget(lastWidgetLookup))
                        : Optional.empty();
                default:
                    return defaultValue(method.getReturnType());
            }
        }

        private static Object defaultValue(Class<?> type) {
            if (type == boolean.class) return false;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == Optional.class) return Optional.empty();
            if (type == OptionalInt.class) return OptionalInt.empty();
            if (type == OptionalLong.class) return OptionalLong.empty();
            if (java.util.List.class.isAssignableFrom(type)) {
                return Collections.emptyList();
            }
            return null;
        }
    }

    private static final class RecordingBackend implements InteractionBackend {
        int interactions;
        int opcode;
        int identifier;
        int param0;
        int param1;
        boolean accepted = true;

        void reset() {
            interactions = 0;
            opcode = 0;
            identifier = 0;
            param0 = 0;
            param1 = 0;
            accepted = true;
        }

        @Override
        public boolean widgetInteract(int opcode, int identifier,
                                      int param0, int param1) {
            ++interactions;
            this.opcode = opcode;
            this.identifier = identifier;
            this.param0 = param0;
            this.param1 = param1;
            return accepted;
        }

        @Override
        public boolean widgetInteractAtPath(WidgetAddress address, int opcode,
                                             int identifier, int childSlot) {
            return false;
        }

        @Override
        public boolean interactNpcByIndex(String action, int hashIndex) {
            return false;
        }

        @Override
        public boolean interactTileObject(String action, TileObject object) {
            return false;
        }

        @Override
        public boolean interactGroundItem(String action, int itemId,
                                           int tileX, int tileY) {
            return false;
        }

        @Override
        public boolean interactInventoryItem(int itemId, String action) {
            return false;
        }

        @Override
        public boolean interactInventoryItemAtSlot(int slot, int itemId,
                                                    String action) {
            return false;
        }

        @Override
        public boolean useInventoryItemOnItem(int srcSlot, int srcItemId,
                                               int targetSlot, int targetItemId) {
            return false;
        }

        @Override
        public boolean useInventoryItemOnNpc(int srcSlot, int srcItemId,
                                              int npcHashIndex) {
            return false;
        }

        @Override
        public boolean useInventoryItemOnObject(int srcSlot, int srcItemId,
                                                 int objectId, int tileX,
                                                 int tileY) {
            return false;
        }

        @Override
        public boolean setWidgetText(int packedId, String text) {
            return false;
        }

        @Override
        public boolean setWidgetTextAtPath(WidgetAddress address, String text) {
            return false;
        }
    }
}
