package net.titan.api.utils;

import net.titan.api.InterfaceId;
import net.titan.api.MenuAction;
import net.titan.api.Titan;
import net.titan.api.Widget;

import java.util.Optional;

public final class Dialogue {
    private static final int[] CONTINUE_CANDIDATES = {
        InterfaceId.pack(231, 5),
        InterfaceId.pack(217, 5),
        InterfaceId.pack(11, 4),
        InterfaceId.pack(229, 2),
        InterfaceId.pack(193, 2),
        InterfaceId.pack(633, 0),
        InterfaceId.pack(162, 42),
        InterfaceId.pack(162, 43),
    };

    private Dialogue() {}

    public static boolean continueMake() {
        return visible(InterfaceId.MAKE_BUTTON).isPresent() &&
            clickWholeWidget(InterfaceId.MAKE_BUTTON, MenuAction.CC_OP, 1);
    }

    public static int getContinueWidgetPackedId() {
        for (int packedId : CONTINUE_CANDIDATES) {
            Optional<Widget> widget = visible(packedId);
            if (!widget.isPresent()) continue;
            if ((packedId == InterfaceId.pack(162, 42) ||
                    packedId == InterfaceId.pack(162, 43)) &&
                    !containsIgnoreCase(widget.get().text(), "Click here to continue")) {
                continue;
            }
            return packedId;
        }
        return 0;
    }

    public static boolean continueDialogue() {
        int packedId = getContinueWidgetPackedId();
        return packedId != 0 &&
            clickWholeWidget(packedId, MenuAction.WIDGET_CONTINUE, 0);
    }

    public static boolean inDialogue() {
        return getContinueWidgetPackedId() != 0 ||
            visible(InterfaceId.DIALOG_OPTIONS).isPresent();
    }

    public static boolean isQuestCompletionOpen() {
        return visible(InterfaceId.QUESTSCROLL_CONTENT).isPresent() ||
            visible(InterfaceId.QUESTSCROLL_CLOSE).isPresent();
    }

    public static boolean closeQuestCompletion() {
        return isQuestCompletionOpen() &&
            visible(InterfaceId.QUESTSCROLL_CLOSE).isPresent() &&
            clickWholeWidget(InterfaceId.QUESTSCROLL_CLOSE, MenuAction.CC_OP, 1);
    }

    public static boolean hasOption(String text) {
        return optionSlot(text) >= 0;
    }

    public static boolean selectOption(String text) {
        int slot = optionSlot(text);
        return slot >= 0 && Titan.client().widgetInteract(
            MenuAction.WIDGET_CONTINUE, 0, slot, InterfaceId.DIALOG_OPTIONS);
    }

    public static boolean handleDialogue(String text) {
        if (selectOption(text)) return true;
        return continueDialogue();
    }

    private static int optionSlot(String text) {
        for (Widget child : Titan.client().widgetChildren(InterfaceId.DIALOG_OPTIONS)) {
            if (child.isVisible() && containsIgnoreCase(child.text(), text)) {
                int slot = child.dynamicChildSlot();
                return slot >= 0 ? slot : child.packedId() & 0xffff;
            }
        }
        return -1;
    }

    private static Optional<Widget> visible(int packedId) {
        return Titan.client().widget(packedId).filter(Widget::isVisible);
    }

    private static boolean clickWholeWidget(int packedId, int opcode, int identifier) {
        return Titan.client().widgetInteract(opcode, identifier, -1, packedId);
    }

    private static boolean containsIgnoreCase(String haystack, String needle) {
        if (needle == null || needle.isEmpty()) return true;
        return haystack != null &&
            haystack.toLowerCase().contains(needle.toLowerCase());
    }
}
