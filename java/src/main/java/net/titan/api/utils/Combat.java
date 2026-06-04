package net.titan.api.utils;

import net.titan.api.InterfaceId;
import net.titan.api.MenuAction;
import net.titan.api.Titan;
import net.titan.api.VarPlayerId;

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
        return Titan.client().widgetInteract(
            MenuAction.CC_OP, 1, -1, InterfaceId.SPEC_ORB);
    }

    public static boolean setAutoRetaliate(boolean enabled) {
        return isAutoRetaliateEnabled() == enabled ||
            Titan.client().widgetInteract(
                MenuAction.CC_OP, 1, -1, InterfaceId.AUTO_RETALIATE);
    }
}
