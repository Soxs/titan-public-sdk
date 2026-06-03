package net.titan.api;

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
}
