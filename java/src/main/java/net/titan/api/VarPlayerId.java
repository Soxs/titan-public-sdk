package net.titan.api;

public final class VarPlayerId {
    public static final int ATTACK_STYLE = 43;
    public static final int SPECIAL_ATTACK = 301;
    public static final int SPECIAL_ATTACK_ENABLED = 300;
    public static final int RUN_ENABLED = 173;
    public static final int POISON = 102;
    public static final int AUTO_RETALIATE = 172;
    public static final int DISEASE = 456;
    public static final int WEIGHT = 451;
    public static final int HP_HUD_1 = 3209;
    public static final int HP_HUD_2 = 3210;
    public static final int PRAYER_POINTS = 2382;
    public static final int LAST_HOME_TELEPORT = 892;

    private VarPlayerId() {}

    public static String nameOf(int id) {
        switch (id) {
        case ATTACK_STYLE: return "ATTACK_STYLE";
        case SPECIAL_ATTACK: return "SPECIAL_ATTACK";
        case SPECIAL_ATTACK_ENABLED: return "SPECIAL_ATTACK_ENABLED";
        case RUN_ENABLED: return "RUN_ENABLED";
        case POISON: return "POISON";
        case AUTO_RETALIATE: return "AUTO_RETALIATE";
        case DISEASE: return "DISEASE";
        case WEIGHT: return "WEIGHT";
        case HP_HUD_1: return "HP_HUD_1";
        case HP_HUD_2: return "HP_HUD_2";
        case PRAYER_POINTS: return "PRAYER_POINTS";
        case LAST_HOME_TELEPORT: return "LAST_HOME_TELEPORT";
        default: return null;
        }
    }
}
