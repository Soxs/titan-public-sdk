package net.titan.api.events;

import net.titan.api.KeyboardModifiers;

/**
 * Synchronous keyboard input before the game receives it. Subscribe with
 * {@code @Subscribe public void onKeyEvent(KeyInputEvent event)}. Keep handlers
 * short: they execute on the window input thread, not necessarily the game thread.
 *
 * <p>The initial keydown determines the route for its repeats and paired keyup.
 * Changes made to those later events cannot change an existing held-key route.
 * Consuming or replacing a keydown also suppresses its original typed characters.
 * Replacement emits key edges only; it does not synthesize typed characters.
 */
public final class KeyInputEvent {
    public static final String KEYDOWN = "keydown";
    public static final String KEYUP = "keyup";
    public static final String CHAR = "char";

    private final String type;
    private final int keyCode;
    private final String character;
    private final int mods;
    private final boolean repeat;
    private boolean consumed;
    private boolean replaced;
    private int replacementKeyCode;
    private int replacementMods;

    public KeyInputEvent(String type, int keyCode, String character, int mods, boolean repeat) {
        this(type, keyCode, character, mods, repeat, false, false, keyCode, mods);
    }

    public KeyInputEvent(String type, int keyCode, String character, int mods,
                         boolean repeat, boolean consumed, boolean replaced,
                         int replacementKeyCode, int replacementMods) {
        if (!KEYDOWN.equals(type) && !KEYUP.equals(type) && !CHAR.equals(type)) {
            throw new IllegalArgumentException("Unknown key event type: " + type);
        }
        this.type = type;
        this.keyCode = CHAR.equals(type) ? -1 : keyCode;
        this.character = CHAR.equals(type) && character != null ? character : "";
        this.mods = mods;
        this.repeat = repeat;
        this.consumed = consumed;
        this.replacementKeyCode = keyCode;
        this.replacementMods = mods;
        if (replaced) replaceWith(replacementKeyCode, replacementMods);
    }

    public String type() { return type; }
    /** Raw Win32 virtual-key code (1..255), or -1 for a character event. */
    public int keyCode() { return keyCode; }
    /** One UTF-16 WM_CHAR code unit, or an empty string for key edges. */
    public String character() { return character; }
    /** Bitmask of {@link KeyboardModifiers} values at dispatch time. */
    public int mods() { return mods; }
    public boolean repeat() { return repeat; }
    public boolean consumed() { return consumed; }
    public boolean replaced() { return replaced; }
    public int replacementKeyCode() { return replacementKeyCode; }
    public int replacementMods() { return replacementMods; }

    /** Suppress the original event. */
    public void consume() { consumed = true; replaced = false; }

    /** Replace a key edge, preserving the original modifier mask. */
    public void replaceWith(int keyCode) { replaceWith(keyCode, mods); }

    /** Replace a key edge with a raw Win32 VK code and modifier mask. */
    public void replaceWith(int keyCode, int mods) {
        if (CHAR.equals(type)) {
            throw new IllegalStateException("Character events cannot be replaced");
        }
        if (keyCode < 1 || keyCode > 255) {
            throw new IllegalArgumentException("Replacement keyCode must be a Win32 VK code (1..255)");
        }
        if ((mods & ~(KeyboardModifiers.SHIFT | KeyboardModifiers.CTRL | KeyboardModifiers.ALT)) != 0) {
            throw new IllegalArgumentException("Unknown keyboard modifier bits");
        }
        replacementKeyCode = keyCode;
        replacementMods = mods;
        replaced = true;
        consumed = true;
    }
}
