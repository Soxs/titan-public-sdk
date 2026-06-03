package net.titan.api;

public final class GroundItem {
    private int tileX;
    private int tileY;
    private int plane;
    private int id = -1;
    private int quantity;
    private String name;
    private int worldX;
    private int worldY;
    private long ownershipType;

    public int tileX() { return tileX; }
    public int tileY() { return tileY; }
    public int plane() { return plane; }
    public int id() { return id; }
    public int quantity() { return quantity; }
    public String name() { return name == null ? "" : name; }
    public int worldX() { return worldX; }
    public int worldY() { return worldY; }
    public long ownershipType() { return ownershipType; }
}
