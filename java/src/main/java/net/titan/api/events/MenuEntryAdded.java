package net.titan.api.events;

import net.titan.api.MenuEntry;
import java.util.Objects;

/** A newly added live menu entry. Handlers may edit it synchronously. */
public final class MenuEntryAdded {
    private final MenuEntry entry;
    public MenuEntryAdded(MenuEntry entry) { this.entry = Objects.requireNonNull(entry); }
    public MenuEntry getMenuEntry() { return entry; }
    public String getOption() { return entry.getOption(); }
    public String getTarget() { return entry.getTarget(); }
    public int getOpcode() { return entry.getOpcode(); }
    public int getIdentifier() { return entry.getIdentifier(); }
    public int getActionParam0() { return entry.getParam0(); }
    public int getActionParam1() { return entry.getParam1(); }
}
