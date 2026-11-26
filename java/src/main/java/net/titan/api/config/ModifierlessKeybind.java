package net.titan.api.config;

/** A non-modifier key ignores modifiers; modifier-only combinations remain meaningful. */
public final class ModifierlessKeybind extends Keybind {
    public static final ModifierlessKeybind NOT_SET = new ModifierlessKeybind(0, 0);
    public ModifierlessKeybind(int keyCode, int modifiers) {
        super(keyCode, modifiers);
        if (keyCode != 0 && !isModifier(keyCode) && modifiers != 0)
            throw new IllegalArgumentException("Modifierless key combinations must have zero modifiers");
    }
}
