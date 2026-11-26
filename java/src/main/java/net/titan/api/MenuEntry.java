package net.titan.api;

import java.util.function.Consumer;

/**
 * A menu entry from the current menu generation. Entries are ordered bottom to
 * top; the last root entry is the default action. Fluent setters apply during
 * synchronous menu events and throw when the host rejects a stale or invalid
 * edit. Retained entries may still be read as snapshots after a rebuild.
 *
 * <p>Custom opcodes are plugin identifiers, not native function addresses.
 * A custom entry never invokes a game operation solely because of its opcode.
 * Existing game entries retain their native action identity: changing opcode,
 * identifier, parameters, item ID or world-view ID is unsupported for them.
 * Added in Java SDK 0.1.94.
 */
public interface MenuEntry {
    String getOption();
    MenuEntry setOption(String option);
    String getTarget();
    MenuEntry setTarget(String target);
    /** Raw 32-bit opcode bits; values with the high bit set appear negative in Java. */
    int getOpcode();
    /** Accepts all 32-bit opcode patterns, including negative Java ints. */
    MenuEntry setOpcode(int opcode);
    /** Alias matching RuneLite-style menu code; unknown opcodes stay intact. */
    default int getType() { return getOpcode(); }
    default MenuEntry setType(int type) { return setOpcode(type); }
    int getIdentifier();
    MenuEntry setIdentifier(int identifier);
    int getParam0();
    MenuEntry setParam0(int param0);
    int getParam1();
    MenuEntry setParam1(int param1);
    int getItemId();
    MenuEntry setItemId(int itemId);
    long getWorldViewId();
    /** The native world-view field is signed 32-bit; -1 is the default context. */
    MenuEntry setWorldViewId(long worldViewId);
    boolean isForceLeftClick();
    MenuEntry setForceLeftClick(boolean forceLeftClick);
    boolean isDeprioritized();
    MenuEntry setDeprioritized(boolean deprioritized);
    boolean isCustom();
    /** One of the game's own submenus. */
    boolean isSubmenu();
    /** Parent submenu entry, or null for a root entry. Parents cannot be changed. */
    MenuEntry getParent();
    /** The entries of its submenu, in bottom-to-top order; empty without one. */
    MenuEntry[] getChildren();
    /** Reorder/remove existing leaf children atomically; cannot create or reparent groups. */
    MenuEntry setChildren(MenuEntry[] children);
    /** Whether it is one of the game's own submenus, or has plugins' entries under it. SDK 194. */
    default boolean hasSubMenu() { return isSubmenu() || getChildren().length != 0; }
    /**
     * Creates an entry in this one's submenu, at a place which counts as {@link Client#createMenuEntry(int)}'s does. A submenu
     * of a plugin's own is under an ordinary entry of the menu itself, and no other has one. The game draws it in the menu,
     * right under its heading, with the heading's option before each, and it goes with its heading. SDK 194.
     */
    default MenuEntry createSubMenuEntry(int index) {
        throw new IllegalStateException("Menu editing is unavailable");
    }
    /** Removes every plugin's entries under it. The game's own submenus stay. SDK 194. */
    default void deleteSubMenu() {
        if (isSubmenu()) throw new IllegalArgumentException("The game's own submenus stay");
        setChildren(new MenuEntry[0]);
    }
    /**
     * Where the game draws its own submenu under this entry, in the pixels which widgets have. A plugin's submenu has no
     * place of its own: {@link GameData#menuBounds()} has that of the menu. SDK 194.
     */
    default java.util.Optional<GameData.MenuBounds> getSubMenuBounds() { return java.util.Optional.empty(); }
    boolean isValid();
    /**
     * Register a synchronous callback owned by the calling plugin; null clears
     * it. The normal action of an existing game entry still runs afterward.
     * Consume the global MenuOptionClicked event to suppress the game action
     * (which also prevents this callback). Custom entries remain host-only.
     */
    MenuEntry onClick(Consumer<MenuEntry> callback);
    Consumer<MenuEntry> getOnClick();
}
