package net.titan.api.overlay;

public enum OverlayLayer {
    ABOVE_SCENE(0),
    ABOVE_WIDGETS(1);

    private final int protocolId;

    OverlayLayer(int protocolId) {
        this.protocolId = protocolId;
    }

    public int protocolId() {
        return protocolId;
    }

    public static OverlayLayer fromProtocolId(int protocolId) {
        return protocolId == ABOVE_WIDGETS.protocolId ? ABOVE_WIDGETS : ABOVE_SCENE;
    }
}
