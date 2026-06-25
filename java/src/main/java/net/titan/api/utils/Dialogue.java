package net.titan.api.utils;

import net.titan.api.MenuAction;
import net.titan.api.Titan;
import net.titan.api.Widget;
import net.titan.api.internal.TitanRuntime;
import net.titan.gamevals.InterfaceID;

import java.util.Optional;

public final class Dialogue {
    // Mirrors the native gameval-backed continue candidate order.
    private static final int[] CONTINUE_CANDIDATES = {
        InterfaceID.LevelupDisplay.CONTINUE_,
        InterfaceID.Messagebox.CONTINUE_,
        InterfaceID.Messagebox.CONTENT,
        InterfaceID.ChatLeft.CONTINUE_,
        InterfaceID.ChatRight.CONTINUE_,
        InterfaceID.ObjectboxDouble.PAUSEBUTTON,
        InterfaceID.Messagebox.SAFEZONE,
        InterfaceID.Chatbox.CLOSE_ICON,
        InterfaceID.Chatbox.MES_TEXT,
    };

    private Dialogue() {}

    public static boolean continueMake() {
        return visible(InterfaceID.Skillmulti.BOTTOM).isPresent() &&
            clickWholeWidget(InterfaceID.Skillmulti.BOTTOM, MenuAction.CC_OP, 1);
    }

    public static int getContinueWidgetPackedId() {
        for (int packedId : CONTINUE_CANDIDATES) {
            Optional<Widget> widget = visible(packedId);
            if (!widget.isPresent()) continue;
            if ((packedId == InterfaceID.Chatbox.CLOSE_ICON ||
                    packedId == InterfaceID.Chatbox.MES_TEXT) &&
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
            visible(InterfaceID.Chatmenu.OPTIONS).isPresent();
    }

    public static boolean isQuestCompletionOpen() {
        return visible(InterfaceID.Questscroll.CONTENT).isPresent() ||
            visible(InterfaceID.Questscroll.CLOSE_BUTTON).isPresent();
    }

    public static boolean closeQuestCompletion() {
        return isQuestCompletionOpen() &&
            visible(InterfaceID.Questscroll.CLOSE_BUTTON).isPresent() &&
            clickWholeWidget(InterfaceID.Questscroll.CLOSE_BUTTON, MenuAction.CC_OP, 1);
    }

    public static boolean hasOption(String... needles) {
        return optionSlot(needles) >= 0;
    }

    public static boolean selectOption(String... needles) {
        int slot = optionSlot(needles);
        return slot >= 0 && TitanRuntime.getInteractionBackend().widgetInteract(
            MenuAction.WIDGET_CONTINUE, 0, slot, InterfaceID.Chatmenu.OPTIONS);
    }

    public static boolean handleDialogue(String... needles) {
        if (selectOption(needles)) return true;
        return continueDialogue();
    }

    private static int optionSlot(String... needles) {
        if (needles == null || needles.length == 0) return -1;
        for (String needle : needles) {
            for (Widget child : Titan.client().widgetChildren(InterfaceID.Chatmenu.OPTIONS)) {
                if (child.isVisible() && containsIgnoreCase(child.text(), needle)) {
                    int slot = child.dynamicChildSlot();
                    return slot >= 0 ? slot : child.packedId() & 0xffff;
                }
            }
        }
        return -1;
    }

    private static Optional<Widget> visible(int packedId) {
        return Titan.client().widget(packedId).filter(Widget::isVisible);
    }

    private static boolean clickWholeWidget(int packedId, int opcode, int identifier) {
        return TitanRuntime.getInteractionBackend().widgetInteract(
            opcode, identifier, -1, packedId);
    }

    private static boolean containsIgnoreCase(String haystack, String needle) {
        if (needle == null || needle.isEmpty()) return true;
        return haystack != null &&
            haystack.toLowerCase().contains(needle.toLowerCase());
    }
}
