package net.titan.api;

/// Client-side, content-developer integer variable ids (RuneLite-compatible).
/// Mirrors the C++ {@code titan::VarClientInt} catalog.
public final class VarClientInt {
    private VarClientInt() {}

    public static final int TOOLTIP_TIMEOUT = 1;
    /// 0 = no tooltip displayed, 1 = tooltip displaying.
    public static final int TOOLTIP_VISIBLE = 2;
    /// Current message layer mode. See RuneLite's InputType.
    public static final int INPUT_TYPE = 5;
    public static final int BANK_SCROLL = 51;
    /// The game sets this to the same value as CAMERA_ZOOM_RESIZABLE_VIEWPORT.
    public static final int CAMERA_ZOOM_FIXED_VIEWPORT = 73;
    public static final int CAMERA_ZOOM_RESIZABLE_VIEWPORT = 74;
    public static final int MEMBERSHIP_STATUS = 103;
    public static final int INVENTORY_TAB = 171;
    /// Time to block keypresses until.
    public static final int BLOCK_KEYPRESS = 187;
    public static final int WORLD_MAP_SEARCH_FOCUSED = 190;

    public static String nameOf(int id) {
        switch (id) {
            case TOOLTIP_TIMEOUT: return "TOOLTIP_TIMEOUT";
            case TOOLTIP_VISIBLE: return "TOOLTIP_VISIBLE";
            case INPUT_TYPE: return "INPUT_TYPE";
            case BANK_SCROLL: return "BANK_SCROLL";
            case CAMERA_ZOOM_FIXED_VIEWPORT: return "CAMERA_ZOOM_FIXED_VIEWPORT";
            case CAMERA_ZOOM_RESIZABLE_VIEWPORT: return "CAMERA_ZOOM_RESIZABLE_VIEWPORT";
            case MEMBERSHIP_STATUS: return "MEMBERSHIP_STATUS";
            case INVENTORY_TAB: return "INVENTORY_TAB";
            case BLOCK_KEYPRESS: return "BLOCK_KEYPRESS";
            case WORLD_MAP_SEARCH_FOCUSED: return "WORLD_MAP_SEARCH_FOCUSED";
            default: return null;
        }
    }
}
