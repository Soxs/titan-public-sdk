package net.titan.api;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.List;

public interface Client {
    int tick();
    int plane();
    int localPlayerIndex();
    int playerCount();
    int baseX();
    int baseY();
    int sceneSizeX();
    int sceneSizeY();
    int runEnergy();
    int weight();
    boolean loggedIn();
    Optional<Player> localPlayer();
    List<Player> players();
    List<Npc> npcs();
    List<TileObject> tileObjects(int radius);
    List<TileObject> tileObjectsOnTile(int plane, int tileX, int tileY);
    List<GroundItem> groundItems(int radius);
    List<GroundItem> groundItemsOnTile(int plane, int tileX, int tileY);
    List<Projectile> projectiles();
    List<GraphicsObject> graphicsObjects();
    Optional<Camera> camera();

    int varbit(int id);
    int varp(int id);
    OptionalInt varClientInt(int id);
    boolean setVarClientInt(int id, int value);
    Optional<String> varClientString(int id);
    boolean setVarClientString(int id, String value);
    OptionalLong varClientLong(int id);
    boolean setVarClientLong(int id, long value);

    boolean prayerActive(int prayerOrdinal);
    int boostedSkillLevel(int skillId);
    int realSkillLevel(int skillId);
    int skillExperience(int skillId);
    default int boostedSkillLevel(Skill skill) { return boostedSkillLevel(skill.id()); }
    default int realSkillLevel(Skill skill) { return realSkillLevel(skill.id()); }
    default int skillExperience(Skill skill) { return skillExperience(skill.id()); }

    int collisionFlag(int plane, int tileX, int tileY);
    boolean walkTo(int sceneX, int sceneY);
    boolean walkToWorld(int worldX, int worldY, int plane);
    boolean interactNpc(String action, int npcIdOrNeg1, String nameOrNull);
    boolean interactNpcByIndex(String action, int hashIndex);
    boolean interactObject(String action, int locIdOrNeg1, String nameOrNull);
    boolean interactTileObject(String action, TileObject object);
    boolean interactGroundItem(String action, int itemId, int tileX, int tileY);
    Optional<Npc> findNearestNpc(int npcIdOrNeg1, String nameOrNull);
    Optional<TileObject> findNearestObject(int locIdOrNeg1, String nameOrNull);

    List<InventoryItem> inventoryItems();
    boolean containsInventoryItem(int itemId);
    boolean interactInventoryItem(int itemId, String action);
    boolean interactInventoryItemAtSlot(int slot, int itemId, String action);
    Optional<ItemContainer> itemContainer(int containerId);
    Optional<ItemComposition> itemComposition(int itemId);

    Optional<Widget> widget(int packedId);
    List<Widget> widgetChildren(int parentPackedId);
    Optional<Widget> widgetByText(String query);
    boolean widgetInteract(int opcode, int identifier, int param0, int param1);
    boolean setWidgetText(int packedId, String text);

    OptionalInt currentWorld();
    List<World> worlds();
    boolean hopToWorldId(int worldId);
    boolean hopToWorldIngame(int worldId);

    int idleTimeRemaining();
    void resetIdleTimer();
}
