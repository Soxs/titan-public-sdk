package net.titan.api.events;

/** An interface whose load listeners have run: its widgets are there. Game thread. SDK 189. */
public final class WidgetLoaded {
    private final int groupId;

    public WidgetLoaded(int groupId) { this.groupId = groupId; }

    public int groupId() { return groupId; }
}
