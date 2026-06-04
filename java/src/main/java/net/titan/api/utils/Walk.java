package net.titan.api.utils;

import net.titan.api.InterfaceId;
import net.titan.api.MenuAction;
import net.titan.api.Titan;
import net.titan.api.VarPlayerId;

public final class Walk {
    private Walk() {}

    public static boolean isRunEnabled() {
        return Titan.client().varp(VarPlayerId.RUN_ENABLED) == 1;
    }

    public static boolean toggleRun() {
        return Titan.client().widgetInteract(
            MenuAction.CC_OP, 1, -1, InterfaceId.RUN_ORB);
    }

    public static boolean setRunEnabled(boolean enabled) {
        return isRunEnabled() == enabled || toggleRun();
    }

    public static int runEnergy() {
        return Titan.client().runEnergy();
    }

    public static int runEnergyPercent() {
        return runEnergy() / 100;
    }

    public static boolean ensureRunEnabled() {
        return ensureRunEnabled(1);
    }

    public static boolean ensureRunEnabled(int minPercent) {
        if (isRunEnabled()) return true;
        if (runEnergyPercent() < minPercent) return false;
        return toggleRun();
    }
}
