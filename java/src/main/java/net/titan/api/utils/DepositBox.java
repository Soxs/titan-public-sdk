package net.titan.api.utils;

import net.titan.api.Client;
import net.titan.api.MenuAction;
import net.titan.api.Titan;
import net.titan.api.Varbits;
import net.titan.api.Widget;
import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.gamevals.InterfaceID;

/// Plugin-side bank deposit box state and action helpers. Mirrors the C++
/// {@code titan::utils::DepositBox} surface (SDK 133). Every deposit box
/// object opens the shared {@code BANK_DEPOSITBOX} interface.
///
/// Every action is the interface's own component operation (CC_OP), which is
/// what a real click sends on both menu conventions; the close X is a
/// component operation on a dynamic child of the frame, not a
/// {@code WIDGET_CLOSE} request.
public final class DepositBox {
    private DepositBox() {}

    /** Dynamic child slot of {@code BankDepositbox.FRAME} carrying the close X. */
    public static final int CLOSE_CHILD_SLOT = 11;
    /** {@link Varbits#BANK_QUANTITY_TYPE} value for the "All" deposit quantity. */
    public static final int QUANTITY_ALL = 4;

    private static Client client() {
        return Titan.client();
    }

    private static InteractionBackend actions() {
        return TitanRuntime.getInteractionBackend();
    }

    private static boolean widgetVisible(int packedId) {
        return client().widget(packedId).map(Widget::isVisible).orElse(false);
    }

    private static boolean componentOp(int packedId, int childSlot) {
        return actions().widgetInteract(MenuAction.CC_OP, 1, childSlot, packedId);
    }

    // --- State reads -----------------------------------------------------

    /** True while the deposit box interface is open. */
    public static boolean isOpen() {
        return widgetVisible(InterfaceID.BankDepositbox.MENU_BUTTON)
            || widgetVisible(InterfaceID.BankDepositbox.WORN);
    }

    /** True when the deposit quantity mode is "All". */
    public static boolean isDepositAllSelected() {
        return client().varbit(Varbits.BANK_QUANTITY_TYPE) == QUANTITY_ALL;
    }

    // --- Actions (queue acceptance, not gameplay success) ----------------

    /** Close the deposit box through its frame's close X component operation. */
    public static boolean close() {
        return componentOp(InterfaceID.BankDepositbox.FRAME, CLOSE_CHILD_SLOT);
    }

    /** Deposit the whole inventory. */
    public static boolean depositInventory() {
        return componentOp(InterfaceID.BankDepositbox.DEPOSIT_INV, -1);
    }

    /** Deposit all worn equipment. */
    public static boolean depositWorn() {
        return componentOp(InterfaceID.BankDepositbox.DEPOSIT_WORN, -1);
    }

    /** Deposit the looting bag's contents. */
    public static boolean depositLootingBag() {
        return componentOp(InterfaceID.BankDepositbox.DEPOSIT_LOOTINGBAG, -1);
    }

    /** Select the "All" deposit quantity; true immediately when already selected. */
    public static boolean selectDepositAll() {
        if (isDepositAllSelected()) return true;
        return componentOp(InterfaceID.BankDepositbox.ALL, -1);
    }
}
