package net.titan.api.panel;

/// Visual style hint for side-panel buttons.
public enum PanelButtonStyle {
    NORMAL(0),
    PRIMARY(1),
    SECONDARY(2),
    DANGER(3),
    GHOST(4);

    private final int protocolId;

    PanelButtonStyle(int protocolId) {
        this.protocolId = protocolId;
    }

    public int protocolId() {
        return protocolId;
    }
}
