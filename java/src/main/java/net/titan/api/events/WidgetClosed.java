package net.titan.api.events;

/** An interface about to close, whose widgets are still there. Game thread. SDK 189. */
public final class WidgetClosed {
    private final int groupId, modalMode;
    private final boolean unload;

    public WidgetClosed(int groupId, int modalMode, boolean unload) {
        this.groupId = groupId; this.modalMode = modalMode; this.unload = unload;
    }

    public int groupId() { return groupId; }
    /** How the server opened it. */
    public int modalMode() { return modalMode; }
    /** Whether its widgets go. */
    public boolean unload() { return unload; }
}
