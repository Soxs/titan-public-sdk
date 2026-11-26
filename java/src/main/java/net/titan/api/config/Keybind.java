package net.titan.api.config;

import java.awt.event.KeyEvent;
import java.util.Objects;

/** Immutable AWT key code plus SHIFT/CTRL/META/ALT extended modifier flags.
 * The unset value is keyCode=0, modifiers=0. No RuneLite classes are required. */
public class Keybind {
    public static final int MODIFIER_MASK = 960;
    public static final Keybind NOT_SET = new Keybind(0, 0);
    private final int keyCode, modifiers;
    public Keybind(int keyCode, int modifiers) {
        if (keyCode < 0 || modifiers < 0 || (modifiers & ~MODIFIER_MASK) != 0)
            throw new IllegalArgumentException("Invalid key code/modifiers");
        this.keyCode = isModifier(keyCode) ? 0 : keyCode;
        this.modifiers = modifiers;
    }
    static boolean isModifier(int code) {
        return code == KeyEvent.VK_SHIFT || code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_META || code == KeyEvent.VK_ALT;
    }
    public final int keyCode() { return keyCode; }
    public final int modifiers() { return modifiers; }
    @Override public final boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        Keybind value = (Keybind) other; return keyCode == value.keyCode && modifiers == value.modifiers;
    }
    @Override public final int hashCode() { return Objects.hash(getClass(), keyCode, modifiers); }
}
