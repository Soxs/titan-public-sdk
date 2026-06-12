package net.titan.api.panel;

/// Semantic tone for status text/callouts in controller side panels.
public enum PanelTone {
    NEUTRAL(0),
    ACCENT(1),
    SUCCESS(2),
    WARNING(3),
    DANGER(4),
    INFO(5);

    private final int protocolId;

    PanelTone(int protocolId) {
        this.protocolId = protocolId;
    }

    public int protocolId() {
        return protocolId;
    }
}
