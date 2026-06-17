package net.titan.api;

import net.titan.api.internal.TitanRuntime;
import net.titan.api.queries.WidgetQuerySnapshot;

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
    int currentWorldViewId();
    int topLevelBaseX();
    int topLevelBaseY();
    int topLevelPlane();
    int topLevelSceneSizeX();
    int topLevelSceneSizeY();
    int topLevelLocalPlayerTileX();
    int topLevelLocalPlayerTileY();
    int topLevelLocalPlayerPlane();
    boolean topLevelLocalPlayerTileValid();
    default int accountType() { return varbit(Varbits.ACCOUNT_TYPE); }
    default boolean isIronman() {
        int type = accountType();
        return type >= 1 && type <= 6;
    }
    default boolean isIronMan() { return isIronman(); }
    default boolean isGroupIronman() {
        int type = accountType();
        return type == 4 || type == 5 || type == 6;
    }
    default boolean isGroupIronMan() { return isGroupIronman(); }
    OptionalLong currentWorldViewPtr();
    OptionalLong worldViewPtr(int worldViewId);
    OptionalLong topLevelWorldViewPtr();
    boolean isInInstance();
    Optional<int[][][]> instanceTemplateChunks();
    Optional<WorldPoint> fromLocalInstance(WorldPoint point);
    Optional<WorldPoint> toLocalInstance(WorldPoint point);
    Optional<LocalPoint> getLocalDestinationLocation();
    Optional<WorldPoint> getWorldDestinationLocation();
    Optional<Player> localPlayer();
    List<Player> players();
    List<NPC> npcs();
    List<TileObject> tileObjects(int radius);
    default List<TileObject> tileObjects() { return tileObjects(10); }
    List<TileObject> tileObjectsOnTile(int plane, int tileX, int tileY);
    default List<TileObject> tileObjectsOnTile(Tile tile) {
        return tile == null
            ? java.util.Collections.emptyList()
            : tileObjectsOnTile(tile.plane(), tile.x(), tile.y());
    }
    default List<TileObject> tileObjectsOnTile(Locatable<?> locatable) {
        return locatable == null
            ? java.util.Collections.emptyList()
            : tileObjectsOnTile(locatable.plane(), locatable.tileX(), locatable.tileY());
    }
    List<GroundItem> groundItems(int radius);
    default List<GroundItem> groundItems() { return groundItems(10); }
    List<GroundItem> groundItemsOnTile(int plane, int tileX, int tileY);
    default List<GroundItem> groundItemsOnTile(Tile tile) {
        return tile == null
            ? java.util.Collections.emptyList()
            : groundItemsOnTile(tile.plane(), tile.x(), tile.y());
    }
    default List<GroundItem> groundItemsOnTile(Locatable<?> locatable) {
        return locatable == null
            ? java.util.Collections.emptyList()
            : groundItemsOnTile(locatable.plane(), locatable.tileX(), locatable.tileY());
    }
    List<Projectile> projectiles();
    List<GraphicsObject> graphicsObjects();
    Optional<Camera> camera();
    Optional<ScreenPoint> mousePosition();
    Optional<ScreenPoint> worldToScreen(int worldX, int worldY, int worldZ);
    default Optional<ScreenPoint> worldToScreen(WorldPoint point) {
        return point == null
            ? Optional.empty()
            : worldToScreen(point.x(), point.y(), point.z());
    }
    default Optional<ScreenPoint> worldToScreen(Locatable<?> locatable) {
        return locatable == null
            ? Optional.empty()
            : worldToScreen(locatable.worldX(), locatable.worldY(), locatable.plane());
    }
    Optional<ScreenPoint> worldToScreenInWorldView(int worldViewId,
                                                   int preciseX,
                                                   int worldY,
                                                   int preciseY,
                                                   int plane);
    default Optional<ScreenPoint> worldToScreen(LocalPoint point, int worldY, int plane) {
        return point == null
            ? Optional.empty()
            : worldToScreenInWorldView(point.worldViewId(), point.x(), worldY, point.y(), plane);
    }
    default Optional<ScreenPoint> worldToScreen(Locatable<?> locatable, int worldY) {
        return locatable == null
            ? Optional.empty()
            : worldToScreen(locatable.localPoint(), worldY, locatable.plane());
    }
    Optional<ScreenPoint> tileToScreen(int tileX, int tileY, int plane, int heightOffset);
    default Optional<ScreenPoint> tileToScreen(Tile tile, int heightOffset) {
        return tile == null
            ? Optional.empty()
            : tileToScreen(tile.x(), tile.y(), tile.plane(), heightOffset);
    }
    int tileHeight(int preciseX, int preciseY, int plane);
    int tileHeightInWorldView(int worldViewId, int preciseX, int preciseY, int plane);
    default int tileHeight(LocalPoint point, int plane) {
        return point == null ? 0 : tileHeightInWorldView(point.worldViewId(), point.x(), point.y(), plane);
    }
    default int worldTileHeightInWorldView(int worldViewId, int worldTileX, int worldTileY, int plane) {
        int baseX = worldViewId == WorldView.TOP_LEVEL ? topLevelBaseX() : baseX();
        int baseY = worldViewId == WorldView.TOP_LEVEL ? topLevelBaseY() : baseY();
        return tileHeightInWorldView(worldViewId, (worldTileX - baseX) * 128,
                                     (worldTileY - baseY) * 128, plane);
    }

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
    default int collisionFlag(Tile tile) {
        return tile == null ? 0 : collisionFlag(tile.plane(), tile.x(), tile.y());
    }
    default int collisionFlag(Locatable<?> locatable) {
        return locatable == null ? 0
            : collisionFlag(locatable.plane(), locatable.tileX(), locatable.tileY());
    }
    boolean entityHidden(int entityType);
    boolean setEntityHidden(int entityType, boolean hidden);

    /**
     * Whether global sound-effect playback is currently suppressed. Added in
     * SDK 69.
     */
    boolean audioPlaybackDisabled();

    /**
     * Globally suppress (or re-enable) sound playback. When disabled, the
     * sound hooks still fire {@code onSoundPlayed} but skip the native playback
     * call. Per-sound playback suppression is not supported by the current
     * hook; {@code event.consume()} only affects handler ordering. Added in
     * SDK 69.
     *
     * @return true if the toggle request was accepted.
     */
    boolean setAudioPlaybackDisabled(boolean disabled);
    boolean walkTo(int sceneX, int sceneY);
    boolean walkToWorld(int worldX, int worldY, int plane);
    default boolean walkTo(WorldPoint point) {
        return point != null && walkToWorld(point.x(), point.y(), point.z());
    }
    default boolean walkTo(Locatable<?> locatable) {
        return locatable != null && walkToWorld(
            locatable.worldX(), locatable.worldY(), locatable.plane());
    }
    boolean invokeMenuAction(long opcode, int identifier, int param0, int param1,
                             long worldViewId, int clickX, int clickY,
                             String actionText, String targetText, boolean skipClick);
    boolean interactNpc(String action, int npcIdOrNeg1, String nameOrNull);
    @Deprecated
    default boolean interactNpcByIndex(String action, int hashIndex) {
        return TitanRuntime.getInteractionBackend().interactNpcByIndex(action, hashIndex);
    }
    boolean interactObject(String action, int locIdOrNeg1, String nameOrNull);
    @Deprecated
    default boolean interactTileObject(String action, TileObject object) {
        return TitanRuntime.getInteractionBackend().interactTileObject(action, object);
    }
    @Deprecated
    default boolean interactGroundItem(String action, int itemId, int tileX, int tileY) {
        return TitanRuntime.getInteractionBackend().interactGroundItem(action, itemId, tileX, tileY);
    }
    Optional<NPC> findNearestNpc(int npcIdOrNeg1, String nameOrNull);
    default Optional<NPC> findNearestNpc(int npcId) {
        return findNearestNpc(npcId, null);
    }
    default Optional<NPC> findNearestNpc(String name) {
        return findNearestNpc(-1, name);
    }
    Optional<TileObject> findNearestObject(int locIdOrNeg1, String nameOrNull);
    default Optional<TileObject> findNearestObject(int locId) {
        return findNearestObject(locId, null);
    }
    default Optional<TileObject> findNearestObject(String name) {
        return findNearestObject(-1, name);
    }

    List<InventoryItem> inventoryItems();
    boolean containsInventoryItem(int itemId);
    @Deprecated
    default boolean interactInventoryItem(int itemId, String action) {
        return TitanRuntime.getInteractionBackend().interactInventoryItem(itemId, action);
    }
    @Deprecated
    default boolean interactInventoryItemAtSlot(int slot, int itemId, String action) {
        return TitanRuntime.getInteractionBackend().interactInventoryItemAtSlot(slot, itemId, action);
    }
    @Deprecated
    default boolean useInventoryItemOnItem(int srcSlot, int srcItemId,
                                           int targetSlot, int targetItemId) {
        return TitanRuntime.getInteractionBackend().useInventoryItemOnItem(
            srcSlot, srcItemId, targetSlot, targetItemId);
    }
    @Deprecated
    default boolean useInventoryItemOnNpc(int srcSlot, int srcItemId, int npcHashIndex) {
        return TitanRuntime.getInteractionBackend().useInventoryItemOnNpc(
            srcSlot, srcItemId, npcHashIndex);
    }
    @Deprecated
    default boolean useInventoryItemOnObject(int srcSlot, int srcItemId,
                                             int objectId, int tileX, int tileY) {
        return TitanRuntime.getInteractionBackend().useInventoryItemOnObject(
            srcSlot, srcItemId, objectId, tileX, tileY);
    }
    Optional<ItemContainer> itemContainer(int containerId);
    Optional<ItemComposition> itemComposition(int itemId);

    Optional<Widget> widget(int packedId);
    List<Widget> widgets(int groupId);
    default List<Widget> widgets() { return widgets(-1); }
    default WidgetQuerySnapshot widgetQuery(int groupId) {
        return new WidgetQuerySnapshot(widgets(groupId), false);
    }
    List<Widget> widgetChildren(int parentPackedId);
    List<Widget> widgetChildrenAtPath(WidgetAddress address);
    default WidgetQuerySnapshot widgetChildrenQueryAtPath(WidgetAddress address) {
        return new WidgetQuerySnapshot(widgetChildrenAtPath(address), false);
    }
    Optional<Widget> widgetByText(String query);
    @Deprecated
    default boolean widgetInteract(int opcode, int identifier, int param0, int param1) {
        return TitanRuntime.getInteractionBackend().widgetInteract(opcode, identifier, param0, param1);
    }
    @Deprecated
    default boolean widgetInteractAtPath(WidgetAddress address, int opcode,
                                         int identifier, int childSlot) {
        return TitanRuntime.getInteractionBackend().widgetInteractAtPath(
            address, opcode, identifier, childSlot);
    }
    @Deprecated
    default boolean setWidgetText(int packedId, String text) {
        return TitanRuntime.getInteractionBackend().setWidgetText(packedId, text);
    }
    @Deprecated
    default boolean setWidgetTextAtPath(WidgetAddress address, String text) {
        return TitanRuntime.getInteractionBackend().setWidgetTextAtPath(address, text);
    }

    OptionalInt currentWorld();
    List<World> worlds();
    List<WorldMetadata> worldMetadata();
    boolean refreshWorldMetadata();
    boolean hopToWorldId(int worldId);
    boolean hopToWorldIngame(int worldId);
    boolean hopToListIndex(int listIndex);

    int idleTimeRemaining();
    void resetIdleTimer();

    int questState(int questId);

    // --- CS2 client scripts ---
    Optional<Cs2Result> runClientScript(int scriptId, int[] intArgs);
    default Optional<Cs2Result> runClientScript(int scriptId) {
        return runClientScript(scriptId, new int[0]);
    }
    default OptionalInt runClientScriptForInt(int scriptId, int[] intArgs) {
        return runClientScript(scriptId, intArgs)
            .filter(Cs2Result::success)
            .map(Cs2Result::firstInt)
            .orElse(OptionalInt.empty());
    }

    // --- Cache definition lookups ---
    Optional<ItemDefinition> itemDefinition(int itemId);
    Optional<NPCDefinition> npcDefinition(int npcId);
    Optional<ObjectDefinition> objectDefinition(int objectId);
    Optional<VarbitDefinition> varbitDefinition(int varbitId);

    // --- Plugin manager ---
    List<PluginInfo> plugins();
    Optional<PluginInfo> plugin(String pluginId);
    boolean setPluginEnabled(String pluginId, boolean enabled);
    /// 1 = enabled, 0 = disabled, -1 = unknown id.
    int pluginEnabledState(String pluginId);

    // --- Login / account state ---
    Optional<LoginState> loginState();
    boolean setLoginUsername(String username);
    boolean setLoginPassword(String password);
    boolean setLoginAuthenticator(String code);
    boolean setLoginIndex(int loginIndex);
    boolean setLoginDisplayName(String displayName);
    boolean setLoginOAuth2Credentials(String accessToken, String refreshToken);
    boolean setLoginGameSessionCredentials(String sessionId, String characterId);
    boolean setLoginCharacter(String displayName, String characterId, String sessionId);
    boolean resetLoginCharacter();

    boolean addChatMessage(int type, String name, String message, String sender);
    boolean sendKeyboardString(String text);
    boolean sendKeyboardKey(int key, int modifiers);
    default boolean sendKeyboardKey(int key) { return sendKeyboardKey(key, 0); }

    // --- Human-like delayed keyboard typing ---
    boolean typeKeyboardString(String text);
    boolean typeKeyboardString(String text, int minDelayMs, int maxDelayMs);
    boolean typeKeyboardString(String text, int minDelayMs, int maxDelayMs,
                               int callbackPhase, KeyboardTypeCallback callback);
    void cancelKeyboardType();
    boolean isKeyboardTyping();
}
