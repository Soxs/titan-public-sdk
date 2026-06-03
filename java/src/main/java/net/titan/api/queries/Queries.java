package net.titan.api.queries;

import net.titan.api.Client;

public final class Queries {
    private final Client client;

    public Queries(Client client) {
        this.client = client;
    }

    public NPCQuery npcs() { return new NPCQuery(client); }
    public PlayerQuery players() { return new PlayerQuery(client); }
    public ObjectQuery objects() { return new ObjectQuery(client, 20); }
    public ObjectQuery objects(int radius) { return new ObjectQuery(client, radius); }
    public GroundItemQuery groundItems() { return new GroundItemQuery(client, 20); }
    public GroundItemQuery groundItems(int radius) { return new GroundItemQuery(client, radius); }
    public InventoryQuery inventory() { return new InventoryQuery(client); }
    public ProjectileQuery projectiles() { return new ProjectileQuery(client); }
    public GraphicsObjectQuery graphicsObjects() { return new GraphicsObjectQuery(client); }
    public WidgetQuery widgets() { return new WidgetQuery(client, -1); }
    public WidgetQuery widgets(int groupId) { return new WidgetQuery(client, groupId); }
}
