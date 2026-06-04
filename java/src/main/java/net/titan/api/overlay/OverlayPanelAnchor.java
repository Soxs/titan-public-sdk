package net.titan.api.overlay;

public enum OverlayPanelAnchor {
    DYNAMIC(0),
    TOP_CENTER(1),
    LEFT_CENTER(2),
    RIGHT_CENTER(3),
    ABOVE_CHATBOX_RIGHT(4),
    TOOLTIP(5);

    private final int protocolId;

    OverlayPanelAnchor(int protocolId) {
        this.protocolId = protocolId;
    }

    public int protocolId() {
        return protocolId;
    }
}
