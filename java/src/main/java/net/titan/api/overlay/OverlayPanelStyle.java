package net.titan.api.overlay;

public final class OverlayPanelStyle {
    public static final int DEFAULT_BACKGROUND = 0xC8141821;
    public static final int DEFAULT_BORDER_COLOR = 0xFF3B5566;
    public static final float DEFAULT_BORDER_THICKNESS = 1.0f;
    public static final float DEFAULT_CORNER_RADIUS = 4.0f;
    public static final int DEFAULT_PAD_HORIZONTAL = 8;
    public static final int DEFAULT_PAD_VERTICAL = 6;
    public static final int DEFAULT_LINE_GAP = 2;
    public static final int DEFAULT_TITLE_COLOR = 0xFF9DEBFF;
    public static final int DEFAULT_LINE_LEFT_COLOR = 0xFFEAF2F8;
    public static final int DEFAULT_LINE_RIGHT_COLOR = 0xFFEAF2F8;
    public static final int DEFAULT_BAR_FILL_COLOR = 0xFF60E060;
    public static final int DEFAULT_BAR_BACKGROUND_COLOR = 0xFF333333;

    private int background = DEFAULT_BACKGROUND;
    private int borderColor = DEFAULT_BORDER_COLOR;
    private float borderThickness = DEFAULT_BORDER_THICKNESS;
    private float cornerRadius = DEFAULT_CORNER_RADIUS;
    private int padHorizontal = DEFAULT_PAD_HORIZONTAL;
    private int padVertical = DEFAULT_PAD_VERTICAL;
    private int lineGap = DEFAULT_LINE_GAP;
    private int titleColor = DEFAULT_TITLE_COLOR;
    private int lineLeftColor = DEFAULT_LINE_LEFT_COLOR;
    private int lineRightColor = DEFAULT_LINE_RIGHT_COLOR;
    private int barFillColor = DEFAULT_BAR_FILL_COLOR;
    private int barBackgroundColor = DEFAULT_BAR_BACKGROUND_COLOR;

    public static OverlayPanelStyle defaults() {
        return new OverlayPanelStyle();
    }

    public OverlayPanelStyle copy() {
        OverlayPanelStyle out = new OverlayPanelStyle();
        out.background = background;
        out.borderColor = borderColor;
        out.borderThickness = borderThickness;
        out.cornerRadius = cornerRadius;
        out.padHorizontal = padHorizontal;
        out.padVertical = padVertical;
        out.lineGap = lineGap;
        out.titleColor = titleColor;
        out.lineLeftColor = lineLeftColor;
        out.lineRightColor = lineRightColor;
        out.barFillColor = barFillColor;
        out.barBackgroundColor = barBackgroundColor;
        return out;
    }

    public int background() { return background; }
    public int borderColor() { return borderColor; }
    public float borderThickness() { return borderThickness; }
    public float cornerRadius() { return cornerRadius; }
    public int padHorizontal() { return padHorizontal; }
    public int padVertical() { return padVertical; }
    public int lineGap() { return lineGap; }
    public int titleColor() { return titleColor; }
    public int lineLeftColor() { return lineLeftColor; }
    public int lineRightColor() { return lineRightColor; }
    public int barFillColor() { return barFillColor; }
    public int barBackgroundColor() { return barBackgroundColor; }

    public OverlayPanelStyle background(int value) { background = value; return this; }
    public OverlayPanelStyle borderColor(int value) { borderColor = value; return this; }
    public OverlayPanelStyle borderThickness(float value) { borderThickness = value; return this; }
    public OverlayPanelStyle cornerRadius(float value) { cornerRadius = value; return this; }
    public OverlayPanelStyle padding(int horizontal, int vertical) {
        padHorizontal = horizontal;
        padVertical = vertical;
        return this;
    }
    public OverlayPanelStyle lineGap(int value) { lineGap = value; return this; }
    public OverlayPanelStyle titleColor(int value) { titleColor = value; return this; }
    public OverlayPanelStyle lineColors(int left, int right) {
        lineLeftColor = left;
        lineRightColor = right;
        return this;
    }
    public OverlayPanelStyle progressBarColors(int fill, int background) {
        barFillColor = fill;
        barBackgroundColor = background;
        return this;
    }
}
