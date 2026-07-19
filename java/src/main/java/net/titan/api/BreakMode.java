package net.titan.api;

/** Break mode selected by the schedule owner that triggered an epoch. */
public enum BreakMode {
    AFK(0),
    LOGOUT(1);

    private final int protocolId;

    BreakMode(int protocolId) {
        this.protocolId = protocolId;
    }

    public int protocolId() {
        return protocolId;
    }

    public static BreakMode fromProtocolId(int protocolId) {
        for (BreakMode value : values()) {
            if (value.protocolId == protocolId) return value;
        }
        return AFK;
    }
}
