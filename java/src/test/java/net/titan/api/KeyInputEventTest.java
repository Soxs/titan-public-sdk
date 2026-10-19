package net.titan.api;

import net.titan.api.events.KeyInputEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KeyInputEventTest {
    @Test void replacementPreservesModifiersAndConsumeCancelsReplacement() {
        KeyInputEvent event = new KeyInputEvent(KeyInputEvent.KEYDOWN, 'W', "", KeyboardModifiers.CTRL, true);
        event.replaceWith(0x26);
        assertTrue(event.consumed());
        assertTrue(event.replaced());
        assertEquals(0x26, event.replacementKeyCode());
        assertEquals(KeyboardModifiers.CTRL, event.replacementMods());
        assertEquals('W', event.keyCode());
        assertTrue(event.repeat());
        event.consume();
        assertTrue(event.consumed());
        assertFalse(event.replaced());
    }

    @Test void invalidReplacementLeavesInputUntouched() {
        KeyInputEvent event = new KeyInputEvent(KeyInputEvent.KEYUP, 'W', "", 0, false);
        assertThrows(IllegalArgumentException.class, () -> event.replaceWith(0));
        assertThrows(IllegalArgumentException.class, () -> event.replaceWith(256));
        assertThrows(IllegalArgumentException.class, () -> event.replaceWith('A', 8));
        assertThrows(IllegalArgumentException.class, () -> event.replaceWith('A', -1));
        assertFalse(event.consumed());
        assertFalse(event.replaced());
        event.replaceWith('A', KeyboardModifiers.SHIFT | KeyboardModifiers.ALT);
        assertEquals(5, event.replacementMods());
    }

    @Test void characterCanBeConsumedButCannotBeReplaced() {
        KeyInputEvent event = new KeyInputEvent(KeyInputEvent.CHAR, 0, "\uD83D", 0, false);
        assertEquals(-1, event.keyCode());
        assertEquals("\uD83D", event.character());
        assertThrows(IllegalStateException.class, () -> event.replaceWith('A'));
        assertFalse(event.consumed());
        event.consume();
        assertTrue(event.consumed());
    }
}
