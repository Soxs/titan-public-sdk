package net.titan.api.utils;

import net.titan.api.MenuAction;
import net.titan.api.Titan;
import net.titan.api.VarPlayerId;
import net.titan.api.internal.TitanRuntime;
import net.titan.gamevals.InterfaceID;

public final class Combat {
    private Combat() {}

    public static int getSpecialAttackPercentage() {
        return Titan.client().varp(VarPlayerId.SPECIAL_ATTACK) / 10;
    }

    public static boolean isSpecialAttackEnabled() {
        return Titan.client().varp(VarPlayerId.SPECIAL_ATTACK_ENABLED) == 1;
    }

    public static boolean isAutoRetaliateEnabled() {
        return Titan.client().varp(VarPlayerId.AUTO_RETALIATE) == 0;
    }

    public static boolean enableSpecialAttack() {
        return enableSpecialAttack(false);
    }

    public static boolean enableSpecialAttack(boolean skipMovement) {
        return TitanRuntime.getInteractionBackend().widgetInteract(
            MenuAction.CC_OP, 1, -1, InterfaceID.Orbs.ORB_SPECENERGY);
    }

    public static boolean setAutoRetaliate(boolean enabled) {
        return isAutoRetaliateEnabled() == enabled ||
            TitanRuntime.getInteractionBackend().widgetInteract(
                MenuAction.CC_OP, 1, -1, InterfaceID.CombatInterface.RETALIATE);
    }
}
