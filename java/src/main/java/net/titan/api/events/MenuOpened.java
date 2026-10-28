package net.titan.api.events;

import net.titan.api.MenuEntry;

/** Synchronous menu-opening event. Its entries are live handles in bottom-to-top order. */
public final class MenuOpened {
    private final MenuEntry[] entries;
    public MenuOpened(MenuEntry[] entries) { this.entries = entries.clone(); }
    public MenuEntry[] getMenuEntries() { return entries.clone(); }
}
