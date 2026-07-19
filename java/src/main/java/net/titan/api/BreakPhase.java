package net.titan.api;

/** Current phase of a coordinated Break Handler epoch. */
public enum BreakPhase {
    NONE(0),
    PREPARE(1),
    BREAK_ACTIVE(2),
    RESUME(3);

    private final int protocolId;

    BreakPhase(int protocolId) {
        this.protocolId = protocolId;
    }

    public int protocolId() {
        return protocolId;
    }

    public static BreakPhase fromProtocolId(int protocolId) {
        for (BreakPhase value : values()) {
            if (value.protocolId == protocolId) return value;
        }
        return NONE;
    }
}
