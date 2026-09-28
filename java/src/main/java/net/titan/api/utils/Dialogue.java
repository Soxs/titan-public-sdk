package net.titan.api.utils;

import net.titan.api.KeyboardKey;
import net.titan.api.MenuAction;
import net.titan.api.Titan;
import net.titan.api.Widget;
import net.titan.api.internal.TitanRuntime;
import net.titan.gamevals.InterfaceID;

import java.util.List;
import java.util.Optional;

/**
 * Dialogue / continue / make / quest-scroll helpers. Mirrors the native
 * {@code titan::utils::Dialogue} helpers: continue prompts are advanced with
 * Space and dialog option N is picked with digit key N, as RuneLite's
 * {@code handleDialogue} does. The Make button and quest-scroll close are
 * still widget clicks.
 */
public final class Dialogue {
    private static final String CONTINUE_TEXT = "Click here to continue";

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

    // Last native candidate: Objectbox has no fixed continue component, its
    // prompt is this dynamic child of Objectbox.UNIVERSE.
    private static final int OBJECTBOX_CONTINUE_SLOT = 2;

    private Dialogue() {}

    public static boolean continueMake() {
        return visible(InterfaceID.Skillmulti.BOTTOM).isPresent() &&
            clickWholeWidget(InterfaceID.Skillmulti.BOTTOM, MenuAction.CC_OP, 1);
    }

    /**
     * Packed id of the active continue prompt, or 0. For Objectbox's prompt,
     * dynamic child 2 of {@code Objectbox.UNIVERSE}, this is
     * {@code Objectbox.UNIVERSE}; that prompt is not clickable as a whole
     * widget, so advance it with {@link #continueDialogue()}.
     */
    public static int getContinueWidgetPackedId() {
        for (int packedId : CONTINUE_CANDIDATES) {
            Optional<Widget> widget = visible(packedId);
            if (!widget.isPresent()) continue;
            if ((packedId == InterfaceID.Chatbox.CLOSE_ICON ||
                    packedId == InterfaceID.Chatbox.MES_TEXT) &&
                    !containsIgnoreCase(widget.get().text(), CONTINUE_TEXT)) {
                continue;
            }
            return packedId;
        }
        if (objectboxContinueVisible()) return InterfaceID.Objectbox.UNIVERSE;
        return 0;
    }

    public static boolean continueDialogue() {
        int packedId = getContinueWidgetPackedId();
        // Mouse dispatch, disabled in favour of the keyboard. Before
        // re-enabling: the Objectbox prompt is dynamic child 2 and must be
        // clicked with param0 = 2, param1 = Objectbox.UNIVERSE.
        //
        // return packedId != 0 &&
        //     clickWholeWidget(packedId, MenuAction.WIDGET_CONTINUE, 0);

        // Space advances every continue prompt, matching the native helper.
        return packedId != 0 && Titan.client().sendKeyboardKey(KeyboardKey.SPACE);
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
        // Mouse dispatch, disabled in favour of the keyboard.
        //
        // return slot >= 0 && TitanRuntime.getInteractionBackend().widgetInteract(
        //     MenuAction.WIDGET_CONTINUE, 0, slot, InterfaceID.Chatmenu.OPTIONS);

        // Option N is dynamic child N and answers to the digit key N.
        return slot >= 1 && slot <= 9 &&
            Titan.client().sendKeyboardString(String.valueOf((char) ('0' + slot)));
    }

    public static boolean handleDialogue(String... needles) {
        if (selectOption(needles)) return true;
        return continueDialogue();
    }

    private static int optionSlot(String... needles) {
        if (needles == null || needles.length == 0) return -1;
        if (!visible(InterfaceID.Chatmenu.OPTIONS).isPresent()) return -1;
        List<Widget> children = Titan.client().widgetChildren(InterfaceID.Chatmenu.OPTIONS);
        // Slot 0 is the dialog title/header. It is NOT needle-proof (a header
        // like "Travel to where?" contains "Travel"), so options start at 1.
        // Within the options, an exact match beats a substring: needles like
        // "Yes" would otherwise take "Yes, but first..." simply because it is
        // listed earlier.
        for (String needle : needles) {
            int substringSlot = -1;
            for (Widget child : children) {
                int slot = slotOf(child);
                if (slot < 1 || !child.isVisible()) continue;
                if (equalsIgnoreCaseTrimmed(child.text(), needle)) return slot;
                if (substringSlot < 0 && containsIgnoreCase(child.text(), needle)) {
                    substringSlot = slot;
                }
            }
            if (substringSlot >= 0) return substringSlot;
        }
        return -1;
    }

    private static boolean objectboxContinueVisible() {
        if (!visible(InterfaceID.Objectbox.UNIVERSE).isPresent()) return false;
        for (Widget child : Titan.client().widgetChildren(InterfaceID.Objectbox.UNIVERSE)) {
            if (slotOf(child) != OBJECTBOX_CONTINUE_SLOT) continue;
            return child.isVisible() && containsIgnoreCase(child.text(), CONTINUE_TEXT);
        }
        return false;
    }

    private static int slotOf(Widget child) {
        int slot = child.dynamicChildSlot();
        return slot >= 0 ? slot : child.packedId() & 0xffff;
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

    // Trims the widget side only, so " Yes " still matches "Yes".
    private static boolean equalsIgnoreCaseTrimmed(String text, String needle) {
        String trimmed = text == null ? "" : text.trim();
        return trimmed.equalsIgnoreCase(needle == null ? "" : needle);
    }
}
