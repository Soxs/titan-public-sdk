package net.titan.api.panel;

/// ImGui command type replayed by the controller to render plugin panels.
///
/// The {@code protocolId()} values mirror {@code PluginProtocol::PanelElementType}
/// on the native side and ride the wire as the {@code "t"} field.
public enum PanelElementType {
    TEXT(0),
    TEXT_WRAPPED(1),
    TEXT_COLORED(2),
    TEXT_DISABLED(3),
    BULLET_TEXT(4),
    LABEL_TEXT(5),

    SEPARATOR(10),
    SEPARATOR_TEXT(11),
    SPACING(12),
    SAME_LINE(13),
    NEW_LINE(14),
    INDENT(15),
    UNINDENT(16),
    DUMMY(17),

    BUTTON(20),
    SMALL_BUTTON(21),

    SELECTABLE(30),

    TREE_NODE(40),
    TREE_POP(41),
    COLLAPSING_HEADER(42),

    BEGIN_TABLE(50),
    END_TABLE(51),
    TABLE_NEXT_ROW(52),
    TABLE_NEXT_COLUMN(53),
    TABLE_SETUP_COLUMN(54),
    TABLE_HEADERS_ROW(55),

    CHECKBOX(60),
    SLIDER_INT(61),
    SLIDER_FLOAT(62),
    INPUT_TEXT(63),
    COMBO(64),

    PROGRESS_BAR(70),

    BEGIN_TAB_BAR(80),
    END_TAB_BAR(81),
    BEGIN_TAB_ITEM(82),
    END_TAB_ITEM(83),

    BEGIN_GROUP(90),
    END_GROUP(91),
    BEGIN_CHILD(92),
    END_CHILD(93),

    PUSH_STYLE_COLOR(100),
    POP_STYLE_COLOR(101),

    SET_TOOLTIP(110),

    ALIGN_BEGIN(111),
    ALIGN_END(112);

    private final int protocolId;

    PanelElementType(int protocolId) {
        this.protocolId = protocolId;
    }

    public int protocolId() {
        return protocolId;
    }
}
