package net.titan.api;

import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Widget {
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

    public int screenX() { return screenX; }
    public int screenY() { return screenY; }
    public int width() { return width; }
    public int height() { return height; }
    public int relativeX() { return relativeX; }
    public int relativeY() { return relativeY; }
    public int scrollX() { return scrollX; }
    public int scrollY() { return scrollY; }
    public int type() { return type; }
    public int contentType() { return contentType; }
    public int opacity() { return opacity; }
    public int itemId() { return itemId; }
    public int itemQuantity() { return itemQuantity; }
    public int parentId() { return parentId; }
    public boolean isHidden() { return hidden; }
    public boolean isSelfHidden() { return selfHidden; }
    public boolean isVisible() { return visible; }
    public String text() { return text == null ? "" : text; }
    public int packedId() { return packedId; }
    public int dynamicParentPackedId() { return dynamicParentPackedId; }
    public int dynamicChildSlot() { return dynamicChildSlot; }
    public int rootPackedId() { return rootPackedId; }
    public List<Integer> dynamicPath() {
        return dynamicPath == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(dynamicPath);
    }

    public WidgetAddress address() {
        int root = rootPackedId != 0
            ? rootPackedId
            : dynamicParentPackedId != 0 ? dynamicParentPackedId : packedId;
        List<Integer> path = dynamicPath();
        if (path.isEmpty() && dynamicChildSlot >= 0) {
            path = new ArrayList<>();
            path.add(dynamicChildSlot);
        }
        return new WidgetAddress(root, path);
    }

    public boolean isDynamic() {
        return dynamicChildSlot >= 0 || !dynamicPath().isEmpty();
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
        return actions.widgetInteract(opcode, identifier, childSlot, packedId);
    }

    public boolean interact(int opcode, int identifier) {
        int slot = dynamicChildSlot >= 0 ? dynamicChildSlot : -1;
        InteractionBackend actions = TitanRuntime.getInteractionBackend();
        if (isDynamic()) {
            return actions.widgetInteractAtPath(address(), opcode, identifier, -1);
        }
        return actions.widgetInteract(opcode, identifier, slot, packedId);
    }

    public boolean setText(String value) {
        InteractionBackend actions = TitanRuntime.getInteractionBackend();
        if (isDynamic()) {
            return actions.setWidgetTextAtPath(address(), value);
        }
        return actions.setWidgetText(packedId, value);
    }
}
