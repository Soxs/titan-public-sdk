package net.titan.api;

/** Immutable semantic menu request for an explicit source/target pair (SDK 128).
 * Addresses pin target identity; they are never native callbacks or replay handles. */
public final class MenuActionRequest {
    private final int opcode, identifier, param0, param1, clickX, clickY;
    private final long worldViewId, targetEntityPtr, targetPackedId;
    private final int targetPlane, targetSizeX, targetSizeY, targetLayer;
    private final String actionText, targetText;
    private final boolean skipClick;

    public MenuActionRequest(int opcode, int identifier, int param0, int param1) {
        this(opcode, identifier, param0, param1, -1, -1, -1, "", "", false, -1, 1, 1, -1, 0, 0);
    }

    public MenuActionRequest(int opcode, int identifier, int param0, int param1,
                             long worldViewId, int clickX, int clickY, String actionText, String targetText,
                             boolean skipClick, int targetPlane, int targetSizeX, int targetSizeY,
                             int targetLayer, long targetEntityPtr, long targetPackedId) {
        this.opcode = opcode; this.identifier = identifier; this.param0 = param0; this.param1 = param1;
        this.worldViewId = worldViewId; this.clickX = clickX; this.clickY = clickY;
        this.actionText = actionText == null ? "" : actionText;
        this.targetText = targetText == null ? "" : targetText; this.skipClick = skipClick;
        this.targetPlane = targetPlane; this.targetSizeX = targetSizeX; this.targetSizeY = targetSizeY;
        this.targetLayer = targetLayer; this.targetEntityPtr = targetEntityPtr; this.targetPackedId = targetPackedId;
    }
    public int opcode() { return opcode; }
    public int identifier() { return identifier; }
    public int param0() { return param0; }
    public int param1() { return param1; }
    public long worldViewId() { return worldViewId; }
    public int clickX() { return clickX; }
    public int clickY() { return clickY; }
    public String actionText() { return actionText; }
    public String targetText() { return targetText; }
    public boolean skipClick() { return skipClick; }
    public int targetPlane() { return targetPlane; }
    public int targetSizeX() { return targetSizeX; }
    public int targetSizeY() { return targetSizeY; }
    public int targetLayer() { return targetLayer; }
    public long targetEntityPtr() { return targetEntityPtr; }
    public long targetPackedId() { return targetPackedId; }
}
