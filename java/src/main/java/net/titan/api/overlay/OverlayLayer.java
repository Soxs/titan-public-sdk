package net.titan.api.overlay;

public enum OverlayLayer {
    /** Above the scene, below native widgets. */
    ABOVE_SCENE(0),
    /** Above ordinary widgets, below native menus and tooltips. */
    ABOVE_WIDGETS(1),
    /** Above native menus and tooltips. */
    ABOVE_ALL(2);

    private final int protocolId;

    OverlayLayer(int protocolId) {
        this.protocolId = protocolId;
    }

    public int protocolId() {
        return protocolId;
    }

    public static OverlayLayer fromProtocolId(int protocolId) {
        switch (protocolId) {
            case 0: return ABOVE_SCENE;
            case 1: return ABOVE_WIDGETS;
            case 2: return ABOVE_ALL;
            default: throw new IllegalArgumentException("Unknown overlay layer: " + protocolId);
        }
    }
}
