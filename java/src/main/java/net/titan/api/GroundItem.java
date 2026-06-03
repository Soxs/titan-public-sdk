package net.titan.api;

public final class GroundItem implements Locatable<GroundItem>, ClientBacked {
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
    public void bindClient(Client client) { this.client = client; }

    @Override
    public Client client() { return client; }

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

    public boolean interact(String action) {
        if (action == null || action.isEmpty()) return false;
        Client value = client();
        return value != null && value.interactGroundItem(action, id, tileX, tileY);
    }
}
