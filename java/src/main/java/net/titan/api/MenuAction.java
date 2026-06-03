package net.titan.api;

public final class MenuAction {
    public static final int DEPRIORITIZE_OFFSET = 2000;

    public static final int ITEM_USE_ON_GAME_OBJECT = 1;
    public static final int WIDGET_TARGET_ON_GAME_OBJECT = 2;
    public static final int GAME_OBJECT_FIRST_OPTION = 3;
    public static final int GAME_OBJECT_SECOND_OPTION = 4;
    public static final int GAME_OBJECT_THIRD_OPTION = 5;
    public static final int GAME_OBJECT_FOURTH_OPTION = 6;
    public static final int GAME_OBJECT_FIFTH_OPTION = 1001;
    public static final int ITEM_USE_ON_NPC = 7;
    public static final int WIDGET_TARGET_ON_NPC = 8;
    public static final int NPC_FIRST_OPTION = 9;
    public static final int NPC_SECOND_OPTION = 10;
    public static final int NPC_THIRD_OPTION = 11;
    public static final int NPC_FOURTH_OPTION = 12;
    public static final int NPC_FIFTH_OPTION = 13;
    public static final int ITEM_USE_ON_PLAYER = 14;
    public static final int WIDGET_TARGET_ON_PLAYER = 15;
    public static final int ITEM_USE_ON_GROUND_ITEM = 16;
    public static final int WIDGET_TARGET_ON_GROUND_ITEM = 17;
    public static final int GROUND_ITEM_FIRST_OPTION = 18;
    public static final int GROUND_ITEM_SECOND_OPTION = 19;
    public static final int GROUND_ITEM_THIRD_OPTION = 20;
    public static final int GROUND_ITEM_FOURTH_OPTION = 21;
    public static final int GROUND_ITEM_FIFTH_OPTION = 22;
    public static final int WALK = 23;
    public static final int WIDGET_TYPE_1 = 24;
    public static final int WIDGET_TARGET = 25;
    public static final int WIDGET_CLOSE = 26;
    public static final int WIDGET_TYPE_4 = 28;
    public static final int WIDGET_TYPE_5 = 29;
    public static final int WIDGET_CONTINUE = 30;
    public static final int WALK_HERE = 31;
    public static final int ITEM_USE_ON_ITEM = WALK_HERE;
    public static final int WIDGET_USE_ON_ITEM = 32;
    public static final int ITEM_USE = 38;
    public static final int WIDGET_FIRST_OPTION = 39;
    public static final int WIDGET_SECOND_OPTION = 40;
    public static final int WIDGET_THIRD_OPTION = 41;
    public static final int WIDGET_FOURTH_OPTION = 42;
    public static final int WIDGET_FIFTH_OPTION = 43;
    public static final int PLAYER_FIRST_OPTION = 44;
    public static final int PLAYER_SECOND_OPTION = 45;
    public static final int PLAYER_THIRD_OPTION = 46;
    public static final int PLAYER_FOURTH_OPTION = 47;
    public static final int PLAYER_FIFTH_OPTION = 48;
    public static final int PLAYER_SIXTH_OPTION = 49;
    public static final int PLAYER_SEVENTH_OPTION = 50;
    public static final int PLAYER_EIGHTH_OPTION = 51;
    public static final int CC_OP = 57;
    public static final int WIDGET_TARGET_ON_WIDGET = 58;
    public static final int SET_HEADING = 60;
    public static final int WORLD_ENTITY_FIRST_OPTION = 63;
    public static final int WORLD_ENTITY_SECOND_OPTION = 64;
    public static final int WORLD_ENTITY_THIRD_OPTION = 65;
    public static final int WORLD_ENTITY_FOURTH_OPTION = 66;
    public static final int WORLD_ENTITY_FIFTH_OPTION = 67;
    public static final int EXAMINE_OBJECT = 1002;
    public static final int EXAMINE_NPC = 1003;
    public static final int EXAMINE_ITEM_GROUND = 1004;
    public static final int EXAMINE_ITEM = 1005;
    public static final int CANCEL = 1006;
    public static final int CC_OP_LOW_PRIORITY = 1007;
    public static final int EXAMINE_WORLD_ENTITY = 1013;

    private MenuAction() {}

    public static int normalize(int opcode) {
        return opcode >= DEPRIORITIZE_OFFSET ? opcode - DEPRIORITIZE_OFFSET : opcode;
    }

    public static int deprioritize(int opcode) {
        int normalized = normalize(opcode);
        return normalized + DEPRIORITIZE_OFFSET;
    }

    public static boolean isWidgetCcFamily(int opcode) {
        switch (normalize(opcode)) {
        case WIDGET_TYPE_1:
        case WIDGET_TARGET:
        case WIDGET_CLOSE:
        case WIDGET_TYPE_4:
        case WIDGET_TYPE_5:
        case WIDGET_CONTINUE:
        case WIDGET_USE_ON_ITEM:
        case ITEM_USE:
        case WIDGET_FIRST_OPTION:
        case WIDGET_SECOND_OPTION:
        case WIDGET_THIRD_OPTION:
        case WIDGET_FOURTH_OPTION:
        case WIDGET_FIFTH_OPTION:
        case CC_OP:
        case WIDGET_TARGET_ON_WIDGET:
        case CC_OP_LOW_PRIORITY:
            return true;
        default:
            return false;
        }
    }

    public static boolean isCcOpFamily(int opcode) {
        switch (normalize(opcode)) {
        case CC_OP:
        case CC_OP_LOW_PRIORITY:
        case WIDGET_TARGET_ON_WIDGET:
            return true;
        default:
            return false;
        }
    }

    public static String nameFor(int opcode) {
        switch (normalize(opcode)) {
        case ITEM_USE_ON_GAME_OBJECT: return "ITEM_USE_ON_GAME_OBJECT";
        case WIDGET_TARGET_ON_GAME_OBJECT: return "WIDGET_TARGET_ON_GAME_OBJECT";
        case GAME_OBJECT_FIRST_OPTION: return "GAME_OBJECT_FIRST_OPTION";
        case GAME_OBJECT_SECOND_OPTION: return "GAME_OBJECT_SECOND_OPTION";
        case GAME_OBJECT_THIRD_OPTION: return "GAME_OBJECT_THIRD_OPTION";
        case GAME_OBJECT_FOURTH_OPTION: return "GAME_OBJECT_FOURTH_OPTION";
        case GAME_OBJECT_FIFTH_OPTION: return "GAME_OBJECT_FIFTH_OPTION";
        case ITEM_USE_ON_NPC: return "ITEM_USE_ON_NPC";
        case WIDGET_TARGET_ON_NPC: return "WIDGET_TARGET_ON_NPC";
        case NPC_FIRST_OPTION: return "NPC_FIRST_OPTION";
        case NPC_SECOND_OPTION: return "NPC_SECOND_OPTION";
        case NPC_THIRD_OPTION: return "NPC_THIRD_OPTION";
        case NPC_FOURTH_OPTION: return "NPC_FOURTH_OPTION";
        case NPC_FIFTH_OPTION: return "NPC_FIFTH_OPTION";
        case ITEM_USE_ON_PLAYER: return "ITEM_USE_ON_PLAYER";
        case WIDGET_TARGET_ON_PLAYER: return "WIDGET_TARGET_ON_PLAYER";
        case ITEM_USE_ON_GROUND_ITEM: return "ITEM_USE_ON_GROUND_ITEM";
        case WIDGET_TARGET_ON_GROUND_ITEM: return "WIDGET_TARGET_ON_GROUND_ITEM";
        case GROUND_ITEM_FIRST_OPTION: return "GROUND_ITEM_FIRST_OPTION";
        case GROUND_ITEM_SECOND_OPTION: return "GROUND_ITEM_SECOND_OPTION";
        case GROUND_ITEM_THIRD_OPTION: return "GROUND_ITEM_THIRD_OPTION";
        case GROUND_ITEM_FOURTH_OPTION: return "GROUND_ITEM_FOURTH_OPTION";
        case GROUND_ITEM_FIFTH_OPTION: return "GROUND_ITEM_FIFTH_OPTION";
        case WALK: return "WALK";
        case WIDGET_TYPE_1: return "WIDGET_TYPE_1";
        case WIDGET_TARGET: return "WIDGET_TARGET";
        case WIDGET_CLOSE: return "WIDGET_CLOSE";
        case WIDGET_TYPE_4: return "WIDGET_TYPE_4";
        case WIDGET_TYPE_5: return "WIDGET_TYPE_5";
        case WIDGET_CONTINUE: return "WIDGET_CONTINUE";
        case WALK_HERE: return "WALK_HERE";
        case WIDGET_USE_ON_ITEM: return "WIDGET_USE_ON_ITEM";
        case ITEM_USE: return "ITEM_USE";
        case WIDGET_FIRST_OPTION: return "WIDGET_FIRST_OPTION";
        case WIDGET_SECOND_OPTION: return "WIDGET_SECOND_OPTION";
        case WIDGET_THIRD_OPTION: return "WIDGET_THIRD_OPTION";
        case WIDGET_FOURTH_OPTION: return "WIDGET_FOURTH_OPTION";
        case WIDGET_FIFTH_OPTION: return "WIDGET_FIFTH_OPTION";
        case PLAYER_FIRST_OPTION: return "PLAYER_FIRST_OPTION";
        case PLAYER_SECOND_OPTION: return "PLAYER_SECOND_OPTION";
        case PLAYER_THIRD_OPTION: return "PLAYER_THIRD_OPTION";
        case PLAYER_FOURTH_OPTION: return "PLAYER_FOURTH_OPTION";
        case PLAYER_FIFTH_OPTION: return "PLAYER_FIFTH_OPTION";
        case PLAYER_SIXTH_OPTION: return "PLAYER_SIXTH_OPTION";
        case PLAYER_SEVENTH_OPTION: return "PLAYER_SEVENTH_OPTION";
        case PLAYER_EIGHTH_OPTION: return "PLAYER_EIGHTH_OPTION";
        case CC_OP: return "CC_OP";
        case WIDGET_TARGET_ON_WIDGET: return "WIDGET_TARGET_ON_WIDGET";
        case SET_HEADING: return "SET_HEADING";
        case WORLD_ENTITY_FIRST_OPTION: return "WORLD_ENTITY_FIRST_OPTION";
        case WORLD_ENTITY_SECOND_OPTION: return "WORLD_ENTITY_SECOND_OPTION";
        case WORLD_ENTITY_THIRD_OPTION: return "WORLD_ENTITY_THIRD_OPTION";
        case WORLD_ENTITY_FOURTH_OPTION: return "WORLD_ENTITY_FOURTH_OPTION";
        case WORLD_ENTITY_FIFTH_OPTION: return "WORLD_ENTITY_FIFTH_OPTION";
        case EXAMINE_OBJECT: return "EXAMINE_OBJECT";
        case EXAMINE_NPC: return "EXAMINE_NPC";
        case EXAMINE_ITEM_GROUND: return "EXAMINE_ITEM_GROUND";
        case EXAMINE_ITEM: return "EXAMINE_ITEM";
        case CANCEL: return "CANCEL";
        case CC_OP_LOW_PRIORITY: return "CC_OP_LOW_PRIORITY";
        case EXAMINE_WORLD_ENTITY: return "EXAMINE_WORLD_ENTITY";
        default: return "UNKNOWN";
        }
    }
}
