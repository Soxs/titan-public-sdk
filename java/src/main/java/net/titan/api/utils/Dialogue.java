package net.titan.api.utils;

import net.titan.api.InterfaceId;
import net.titan.api.MenuAction;
import net.titan.api.Titan;
import net.titan.api.Widget;
import net.titan.api.internal.TitanRuntime;

import java.util.Optional;

public final class Dialogue {
    // Mirrors C++ InterfaceIds::ContinueCandidates (order matters).
    private static final int[] CONTINUE_CANDIDATES = {
        InterfaceId.pack(233, 3),   // LEVEL_UP_CONTINUE
        InterfaceId.pack(229, 2),   // MINIGAME_DIALOG_CONTINUE
        InterfaceId.pack(231, 5),   // DIALOG_NPC_CONTINUE
        InterfaceId.pack(217, 5),   // DIALOG_PLAYER_CONTINUE
        InterfaceId.pack(11, 4),    // DIALOG2_SPRITE_CONTINUE
        InterfaceId.pack(229, 1),   // DIALOG_NOTIFICATION_CONTINUE
        InterfaceId.pack(162, 42),  // tutorial-island continue (text-gated)
        InterfaceId.pack(162, 43),  // tutorial-island continue (text-gated)
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

    public static boolean hasOption(String... needles) {
        return optionSlot(needles) >= 0;
    }

    public static boolean selectOption(String... needles) {
        int slot = optionSlot(needles);
        return slot >= 0 && TitanRuntime.getInteractionBackend().widgetInteract(
            MenuAction.WIDGET_CONTINUE, 0, slot, InterfaceId.DIALOG_OPTIONS);
    }

    public static boolean handleDialogue(String... needles) {
        if (selectOption(needles)) return true;
        return continueDialogue();
    }

    private static int optionSlot(String... needles) {
        if (needles == null || needles.length == 0) return -1;
        for (String needle : needles) {
            for (Widget child : Titan.client().widgetChildren(InterfaceId.DIALOG_OPTIONS)) {
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
