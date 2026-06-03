package net.titan.api.events;

public final class MenuOptionClicked {
    private final long opcode;
    private final int identifier;
    private final int param0;
    private final int param1;
    private final long worldViewId;
    private final int clickX;
    private final int clickY;
    private final String actionText;
    private final String targetText;
    private boolean consumed;
    private boolean replaced;
    private long replacementOpcode;
    private int replacementIdentifier;
    private int replacementParam0;
    private int replacementParam1;
    private long replacementWorldViewId;
    private int replacementClickX;
    private int replacementClickY;
    private String replacementActionText;
    private String replacementTargetText;

    public MenuOptionClicked(long opcode, int identifier, int param0, int param1,
                             long worldViewId, int clickX, int clickY,
                             String actionText, String targetText) {
        this(opcode, identifier, param0, param1, worldViewId, clickX, clickY,
            actionText, targetText, false, false, opcode, identifier, param0,
            param1, worldViewId, clickX, clickY, actionText, targetText);
    }

    public MenuOptionClicked(long opcode, int identifier, int param0, int param1,
                             long worldViewId, int clickX, int clickY,
                             String actionText, String targetText,
                             boolean consumed, boolean replaced,
                             long replacementOpcode, int replacementIdentifier,
                             int replacementParam0, int replacementParam1,
                             long replacementWorldViewId,
                             int replacementClickX, int replacementClickY,
                             String replacementActionText,
                             String replacementTargetText) {
        this.opcode = opcode;
        this.identifier = identifier;
        this.param0 = param0;
        this.param1 = param1;
        this.worldViewId = worldViewId;
        this.clickX = clickX;
        this.clickY = clickY;
        this.actionText = actionText == null ? "" : actionText;
        this.targetText = targetText == null ? "" : targetText;
        this.consumed = consumed;
        this.replaced = replaced;
        this.replacementOpcode = replacementOpcode;
        this.replacementIdentifier = replacementIdentifier;
        this.replacementParam0 = replacementParam0;
        this.replacementParam1 = replacementParam1;
        this.replacementWorldViewId = replacementWorldViewId;
        this.replacementClickX = replacementClickX;
        this.replacementClickY = replacementClickY;
        this.replacementActionText = replacementActionText == null ? "" : replacementActionText;
        this.replacementTargetText = replacementTargetText == null ? "" : replacementTargetText;
    }

    public long opcode() { return opcode; }
    public int identifier() { return identifier; }
    public int param0() { return param0; }
    public int param1() { return param1; }
    public long worldViewId() { return worldViewId; }
    public int clickX() { return clickX; }
    public int clickY() { return clickY; }
    public String actionText() { return actionText; }
    public String targetText() { return targetText; }

    public void consume() { consumed = true; }
    public boolean consumed() { return consumed; }

    public void replaceWith(long opcode, int identifier, int param0, int param1,
                            long worldViewId, int clickX, int clickY,
                            String actionText, String targetText) {
        replaced = true;
        replacementOpcode = opcode;
        replacementIdentifier = identifier;
        replacementParam0 = param0;
        replacementParam1 = param1;
        replacementWorldViewId = worldViewId;
        replacementClickX = clickX;
        replacementClickY = clickY;
        replacementActionText = actionText == null ? "" : actionText;
        replacementTargetText = targetText == null ? "" : targetText;
    }

    public void clearReplacement() { replaced = false; }
    public boolean replaced() { return replaced; }

    public long replacementOpcode() { return replacementOpcode; }
    public int replacementIdentifier() { return replacementIdentifier; }
    public int replacementParam0() { return replacementParam0; }
    public int replacementParam1() { return replacementParam1; }
    public long replacementWorldViewId() { return replacementWorldViewId; }
    public int replacementClickX() { return replacementClickX; }
    public int replacementClickY() { return replacementClickY; }
    public String replacementActionText() { return replacementActionText; }
    public String replacementTargetText() { return replacementTargetText; }

    public void setOpcode(long opcode) { ensureReplacement(); replacementOpcode = opcode; }
    public void setIdentifier(int identifier) { ensureReplacement(); replacementIdentifier = identifier; }
    public void setParam0(int param0) { ensureReplacement(); replacementParam0 = param0; }
    public void setParam1(int param1) { ensureReplacement(); replacementParam1 = param1; }
    public void setWorldViewId(long worldViewId) { ensureReplacement(); replacementWorldViewId = worldViewId; }
    public void setClick(int x, int y) {
        ensureReplacement();
        replacementClickX = x;
        replacementClickY = y;
    }
    public void setActionText(String actionText) {
        ensureReplacement();
        replacementActionText = actionText == null ? "" : actionText;
    }
    public void setTargetText(String targetText) {
        ensureReplacement();
        replacementTargetText = targetText == null ? "" : targetText;
    }

    private void ensureReplacement() {
        if (replaced) return;
        replaced = true;
        replacementOpcode = opcode;
        replacementIdentifier = identifier;
        replacementParam0 = param0;
        replacementParam1 = param1;
        replacementWorldViewId = worldViewId;
        replacementClickX = clickX;
        replacementClickY = clickY;
        replacementActionText = actionText;
        replacementTargetText = targetText;
    }
}
