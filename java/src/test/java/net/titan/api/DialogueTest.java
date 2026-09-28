package net.titan.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.utils.Dialogue;
import net.titan.gamevals.InterfaceID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Keeps Java's dialogue helpers at parity with the native SDK header. */
class DialogueTest {
    private static final String CONTINUE_TEXT = "Click here to continue";

    private Injector injector;
    private InteractionBackend backend;
    private ClientState state;

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
    }

    @Test
    void continuePressesSpaceInsteadOfClicking() {
        install();
        state.visible.add(InterfaceID.Messagebox.CONTINUE_);

        assertEquals(InterfaceID.Messagebox.CONTINUE_, Dialogue.getContinueWidgetPackedId());
        assertTrue(Dialogue.handleDialogue("missing option"));
        assertEquals(1, state.keyPresses);
        assertEquals(KeyboardKey.SPACE, state.lastKey);
        assertEquals(0, state.lastKeyModifiers);
        assertEquals(0, state.typedStrings);
        assertEquals(0, state.interactions);
    }

    @Test
    void objectboxPromptIsDynamicChildTwo() {
        install();
        state.visible.add(InterfaceID.Objectbox.UNIVERSE);
        state.children.put(InterfaceID.Objectbox.UNIVERSE, List.of(
            child(0, "", true),
            child(1, "You find some treasure.", true),
            child(2, CONTINUE_TEXT, true)));

        assertEquals(InterfaceID.Objectbox.UNIVERSE, Dialogue.getContinueWidgetPackedId());
        assertTrue(Dialogue.inDialogue());
        assertTrue(Dialogue.continueDialogue());
        assertEquals(1, state.keyPresses);
        assertEquals(KeyboardKey.SPACE, state.lastKey);
        assertEquals(0, state.interactions);

        state.children.put(InterfaceID.Objectbox.UNIVERSE, List.of(
            child(0, "", true),
            child(1, "You find some treasure.", true),
            child(2, CONTINUE_TEXT, false)));
        assertEquals(0, Dialogue.getContinueWidgetPackedId(), "hidden prompt");

        state.children.put(InterfaceID.Objectbox.UNIVERSE, List.of(
            child(0, "", true),
            child(1, CONTINUE_TEXT, true),
            child(2, "You find some treasure.", true)));
        assertEquals(0, Dialogue.getContinueWidgetPackedId(), "text on another slot");

        state.reset();
        assertFalse(Dialogue.continueDialogue());
        assertEquals(0, state.keyPresses);
    }

    @Test
    void optionsPressTheirDigitAndSkipTheTitle() {
        install();
        state.visible.add(InterfaceID.Chatmenu.OPTIONS);
        state.children.put(InterfaceID.Chatmenu.OPTIONS, List.of(
            child(0, "Select an option", true),
            child(1, "Yes, but first...", true),
            child(2, " yes ", true),
            child(3, "No", false)));

        assertTrue(Dialogue.selectOption("Yes"));
        assertEquals(1, state.typedStrings);
        assertEquals("2", state.lastString, "exact match beats an earlier substring");

        state.reset();
        assertTrue(Dialogue.selectOption("but first"));
        assertEquals("1", state.lastString, "substring fallback");

        state.reset();
        assertFalse(Dialogue.selectOption("Select an option"), "title is never picked");
        assertFalse(Dialogue.hasOption("Select an option"), "title is not an option");
        assertFalse(Dialogue.selectOption("No"), "hidden option");
        assertEquals(0, state.typedStrings);
        assertEquals(0, state.keyPresses);
        assertEquals(0, state.interactions);
    }

    @Test
    void hiddenOptionsWidgetSelectsNothing() {
        install();
        state.children.put(InterfaceID.Chatmenu.OPTIONS, List.of(
            child(0, "Select an option", true),
            child(1, "Yes", true)));

        assertFalse(Dialogue.hasOption("Yes"));
        assertFalse(Dialogue.selectOption("Yes"));
        assertEquals(0, state.typedStrings);
    }

    private void install() {
        state = new ClientState();
        Client client = (Client) Proxy.newProxyInstance(
            Client.class.getClassLoader(), new Class<?>[] {Client.class}, state);
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Client.class).toInstance(client);
            }
        });
        TitanRuntime.setInjector(injector);
        backend = (InteractionBackend) Proxy.newProxyInstance(
            InteractionBackend.class.getClassLoader(),
            new Class<?>[] {InteractionBackend.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "widgetInteract":
                    case "widgetInteractAtPath":
                        ++state.interactions;
                        return true;
                    case "hashCode":
                        return System.identityHashCode(proxy);
                    case "equals":
                        return proxy == args[0];
                    case "toString":
                        return "DialogueTestBackend";
                    default:
                        return defaultValue(method.getReturnType());
                }
            });
        TitanRuntime.setInteractionBackend(backend);
    }

    private static Widget widget(int packedId, boolean visible) {
        Widget widget = new Widget();
        set(widget, "packedId", packedId);
        set(widget, "visible", visible);
        return widget;
    }

    private static Widget child(int slot, String text, boolean visible) {
        Widget widget = new Widget();
        set(widget, "dynamicChildSlot", slot);
        set(widget, "text", text);
        set(widget, "visible", visible);
        return widget;
    }

    private static void set(Widget widget, String name, Object value) {
        try {
            Field field = Widget.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(widget, value);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == Optional.class) return Optional.empty();
        if (type == OptionalInt.class) return OptionalInt.empty();
        if (type == OptionalLong.class) return OptionalLong.empty();
        if (List.class.isAssignableFrom(type)) return Collections.emptyList();
        return null;
    }

    private static final class ClientState implements InvocationHandler {
        final Set<Integer> visible = new HashSet<>();
        final Map<Integer, List<Widget>> children = new HashMap<>();
        int keyPresses;
        int lastKey = -1;
        int lastKeyModifiers = -1;
        int typedStrings;
        String lastString;
        int interactions;

        void reset() {
            keyPresses = 0;
            lastKey = -1;
            lastKeyModifiers = -1;
            typedStrings = 0;
            lastString = null;
            interactions = 0;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            switch (method.getName()) {
                case "toString":
                    return "DialogueTestClient";
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "equals":
                    return proxy == args[0];
                case "widget": {
                    int packedId = (Integer) args[0];
                    return Optional.of(widget(packedId, visible.contains(packedId)));
                }
                case "widgetChildren":
                    return new ArrayList<>(children.getOrDefault(
                        (Integer) args[0], Collections.emptyList()));
                case "sendKeyboardKey":
                    ++keyPresses;
                    lastKey = (Integer) args[0];
                    lastKeyModifiers = args.length > 1 ? (Integer) args[1] : 0;
                    return true;
                case "sendKeyboardString":
                    ++typedStrings;
                    lastString = (String) args[0];
                    return true;
                default:
                    return defaultValue(method.getReturnType());
            }
        }
    }
}
