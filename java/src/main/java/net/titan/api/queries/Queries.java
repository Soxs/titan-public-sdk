package net.titan.api.queries;

import net.titan.api.Client;
import net.titan.api.Titan;

public final class Queries {
    private Queries() {}

    public static NPCQuery npcs() { return new NPCQuery(client()); }
    public static PlayerQuery players() { return new PlayerQuery(client()); }
    public static ObjectQuery objects() { return new ObjectQuery(client(), 20); }
    public static ObjectQuery objects(int radius) { return new ObjectQuery(client(), radius); }
    public static GroundItemQuery groundItems() { return new GroundItemQuery(client(), 20); }
    public static GroundItemQuery groundItems(int radius) { return new GroundItemQuery(client(), radius); }
    public static InventoryQuery inventory() { return new InventoryQuery(client()); }
    public static ProjectileQuery projectiles() { return new ProjectileQuery(client()); }
    public static GraphicsObjectQuery graphicsObjects() { return new GraphicsObjectQuery(client()); }
    public static WidgetQuery widgets() { return new WidgetQuery(client(), -1); }
    public static WidgetQuery widgets(int groupId) { return new WidgetQuery(client(), groupId); }

    private static Client client() {
        return Titan.client();
    }
}
