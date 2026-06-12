package net.titan.api;

public final class EntityType {
    public static final int LOCATION = 0;
    public static final int NPC = 1;
    public static final int PLAYER = 2;
    public static final int TILE_OBJECT = LOCATION;
    public static final int GROUND_ITEM = 3;
    public static final int PROJECTILE = 4;
    public static final int GRAPHICS_OBJECT = 5;
    public static final int NONE = 0x7F;

    private EntityType() {}
}
