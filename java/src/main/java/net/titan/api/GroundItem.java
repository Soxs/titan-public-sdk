package net.titan.api;

import net.titan.api.internal.TitanRuntime;

public final class GroundItem implements Locatable<GroundItem> {
    private static final int ACCOUNT_TYPE_VARBIT = 1777;

    private boolean liveHandle = true;
    private int worldViewId = WorldView.CURRENT;
    private long worldViewPtr;
    private int tileX;
    private int tileY;
    private int plane;
    private int id = -1;
    private int quantity;
    private String name;
    private int worldX;
    private int worldY;
    private long ownershipType;

    private GroundItem live() { return TitanRuntime.currentLive(this); }

    @Override
    public int tileX() { return live().tileX; }
    @Override
    public int tileY() { return live().tileY; }
    @Override
    public int plane() { return live().plane; }
    @Override
    public int worldViewId() { return live().worldViewId; }
    @Override
    public long worldViewPtr() { return live().worldViewPtr; }
    public int id() { return live().id; }
    public int quantity() { return live().quantity; }
    public String name() {
        String value = live().name;
        return value == null ? "" : value;
    }
    @Override
    public int worldX() { return live().worldX; }
    @Override
    public int worldY() { return live().worldY; }
    public long ownershipType() { return live().ownershipType; }
    public GroundItemOwnership ownership() { return GroundItemOwnership.fromId(ownershipType()); }
    public boolean canLoot() {
        int accountType = Titan.client().varbit(ACCOUNT_TYPE_VARBIT);
        boolean ironman = accountType >= 1 && accountType <= 6;
        boolean groupIronman = accountType == 4 || accountType == 5 || accountType == 6;
        switch ((int) ownershipType()) {
        case 0:
        case 1:
            return true;
        case 2:
            return !ironman;
        case 3:
            return groupIronman;
        default:
            return !ironman;
        }
    }

    public boolean interact(String action) {
        if (action == null || action.isEmpty()) return false;
        return TitanRuntime.getInteractionBackend().interactGroundItem(
            action, id(), tileX(), tileY(), worldViewId());
    }

    public boolean exists() { return TitanRuntime.liveExists(this); }

    public GroundItem snapshot() { return TitanRuntime.snapshotLive(this); }

    @Override
    public boolean equals(Object other) {
        return TitanRuntime.liveEquals(this, other);
    }

    @Override
    public int hashCode() {
        return TitanRuntime.liveHashCode(this);
    }
}
