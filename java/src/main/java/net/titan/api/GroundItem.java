package net.titan.api;

public final class GroundItem implements Locatable<GroundItem> {
    private static final int ACCOUNT_TYPE_VARBIT = 1777;

    private transient Client client;
    private int tileX;
    private int tileY;
    private int plane;
    private int id = -1;
    private int quantity;
    private String name;
    private int worldX;
    private int worldY;
    private long ownershipType;

    @Override
    public int tileX() { return tileX; }
    @Override
    public int tileY() { return tileY; }
    @Override
    public int plane() { return plane; }
    public int id() { return id; }
    public int quantity() { return quantity; }
    public String name() { return name == null ? "" : name; }
    @Override
    public int worldX() { return worldX; }
    @Override
    public int worldY() { return worldY; }
    public long ownershipType() { return ownershipType; }
    public GroundItemOwnership ownership() { return GroundItemOwnership.fromId(ownershipType); }
    public boolean canLoot() {
        Client value = client;
        if (value == null) value = Titan.client();
        int accountType = value.varbit(ACCOUNT_TYPE_VARBIT);
        boolean ironman = accountType >= 1 && accountType <= 6;
        boolean groupIronman = accountType == 4 || accountType == 5 || accountType == 6;
        switch ((int) ownershipType) {
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
        Client value = client;
        if (value == null) value = Titan.client();
        return value.interactGroundItem(action, id, tileX, tileY);
    }
}
