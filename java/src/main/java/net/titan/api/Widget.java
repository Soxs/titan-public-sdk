package net.titan.api;

import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Widget {
    private boolean liveHandle = true;
    private int screenX;
    private int screenY;
    private int width;
    private int height;
    private int relativeX;
    private int relativeY;
    private int scrollX;
    private int scrollY;
    private int type;
    private int contentType;
    private int opacity;
    private int itemId = -1;
    private int itemQuantity;
    private int parentId = -1;
    private boolean hidden;
    private boolean selfHidden;
    private boolean visible;
    private String text;
    private int packedId;
    private int dynamicParentPackedId;
    private int dynamicChildSlot = -1;
    private int rootPackedId;
    private List<Integer> dynamicPath;

    private Widget live() { return TitanRuntime.currentLive(this); }

    public int screenX() { return live().screenX; }
    public int screenY() { return live().screenY; }
    public int width() { return live().width; }
    public int height() { return live().height; }
    public int relativeX() { return live().relativeX; }
    public int relativeY() { return live().relativeY; }
    public int scrollX() { return live().scrollX; }
    public int scrollY() { return live().scrollY; }
    public int type() { return live().type; }
    public int contentType() { return live().contentType; }
    public int opacity() { return live().opacity; }
    public int itemId() { return live().itemId; }
    public int itemQuantity() { return live().itemQuantity; }
    public int parentId() { return live().parentId; }
    public boolean isHidden() { return live().hidden; }
    public boolean isSelfHidden() { return live().selfHidden; }
    public boolean isVisible() { return live().visible; }
    public String text() {
        String value = live().text;
        return value == null ? "" : value;
    }
    public int packedId() { return live().packedId; }
    public int dynamicParentPackedId() { return live().dynamicParentPackedId; }
    public int dynamicChildSlot() { return live().dynamicChildSlot; }
    public int rootPackedId() { return live().rootPackedId; }
    public List<Integer> dynamicPath() {
        List<Integer> value = live().dynamicPath;
        return value == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(value);
    }

    public WidgetAddress address() {
        Widget w = live();
        int root = w.rootPackedId != 0
            ? w.rootPackedId
            : w.dynamicParentPackedId != 0 ? w.dynamicParentPackedId : w.packedId;
        List<Integer> path = dynamicPath();
        if (path.isEmpty() && w.dynamicChildSlot >= 0) {
            path = new ArrayList<>();
            path.add(w.dynamicChildSlot);
        }
        return new WidgetAddress(root, path);
    }

    public boolean isDynamic() {
        return dynamicChildSlot() >= 0 || !dynamicPath().isEmpty();
    }

    public boolean interact(int opcode, int identifier, int param0, int param1) {
        return TitanRuntime.getInteractionBackend().widgetInteract(
            opcode, identifier, param0, param1);
    }

    public boolean interact(int opcode, int identifier, int childSlot) {
        InteractionBackend actions = TitanRuntime.getInteractionBackend();
        if (isDynamic()) {
            return actions.widgetInteractAtPath(address(), opcode, identifier, childSlot);
        }
        return actions.widgetInteract(opcode, identifier, childSlot, packedId());
    }

    public boolean interact(int opcode, int identifier) {
        int slot = dynamicChildSlot() >= 0 ? dynamicChildSlot() : -1;
        InteractionBackend actions = TitanRuntime.getInteractionBackend();
        if (isDynamic()) {
            return actions.widgetInteractAtPath(address(), opcode, identifier, -1);
        }
        return actions.widgetInteract(opcode, identifier, slot, packedId());
    }

    public boolean setText(String value) {
        InteractionBackend actions = TitanRuntime.getInteractionBackend();
        boolean ok;
        if (isDynamic()) {
            ok = actions.setWidgetTextAtPath(address(), value);
        } else {
            ok = actions.setWidgetText(packedId(), value);
        }
        if (ok) text = value == null ? "" : value;
        return ok;
    }

    public boolean exists() { return TitanRuntime.liveExists(this); }

    public Widget snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) {
        return TitanRuntime.liveEquals(this, other);
    }

    @Override
    public int hashCode() {
        return TitanRuntime.liveHashCode(this);
    }
}
