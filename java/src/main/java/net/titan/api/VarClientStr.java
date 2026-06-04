package net.titan.api;

/// Client-side, content-developer string variable ids (RuneLite-compatible).
/// Mirrors the C++ {@code titan::VarClientStr} catalog.
public final class VarClientStr {
    private VarClientStr() {}

    public static final int CHATBOX_TYPED_TEXT = 335;
    public static final int INPUT_TEXT = 359;
    public static final int PRIVATE_MESSAGE_TARGET = 360;
    public static final int RECENT_FRIENDS_CHAT = 362;
    public static final int NOTIFICATION_TOP_TEXT = 387;
    public static final int NOTIFICATION_BOTTOM_TEXT = 388;

    public static String nameOf(int id) {
        switch (id) {
            case CHATBOX_TYPED_TEXT: return "CHATBOX_TYPED_TEXT";
            case INPUT_TEXT: return "INPUT_TEXT";
            case PRIVATE_MESSAGE_TARGET: return "PRIVATE_MESSAGE_TARGET";
            case RECENT_FRIENDS_CHAT: return "RECENT_FRIENDS_CHAT";
            case NOTIFICATION_TOP_TEXT: return "NOTIFICATION_TOP_TEXT";
            case NOTIFICATION_BOTTOM_TEXT: return "NOTIFICATION_BOTTOM_TEXT";
            default: return null;
        }
    }
}
