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
    int getOpcode();
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
    MenuEntry setWorldViewId(long worldViewId);
    boolean isForceLeftClick();
    MenuEntry setForceLeftClick(boolean forceLeftClick);
    boolean isDeprioritized();
    MenuEntry setDeprioritized(boolean deprioritized);
    boolean isCustom();
    boolean isSubmenu();
    /** Parent submenu entry, or null for a root entry. Parents cannot be changed. */
    MenuEntry getParent();
    /** Existing submenu children, in bottom-to-top order; empty for a leaf. */
    MenuEntry[] getChildren();
    /** Reorder/remove existing leaf children atomically; cannot create or reparent groups. */
    MenuEntry setChildren(MenuEntry[] children);
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
