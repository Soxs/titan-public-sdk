package net.titan.api.queries;

import net.titan.api.Client;
import net.titan.api.InventoryItem;
import net.titan.api.ItemComposition;
import net.titan.api.ItemDefinition;
import net.titan.api.NPC;
import net.titan.api.NPCDefinition;
import net.titan.api.TileObject;
import net.titan.api.Widget;
import net.titan.api.WorldArea;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionQueryTest {
    @Test
    void npcQueryMatchesAnySuppliedAction() {
        NPC guard = npc("Talk-to", "Attack");
        NPC shopKeeper = npc("Trade");
        NPC citizen = npc("Pickpocket");
        Client client = client(List.of(guard, shopKeeper, citizen), List.of());

        assertEquals(1, new NPCQuery(client).hasAction("Attack").count());
        assertEquals(2, new NPCQuery(client).hasAction("attack", "Trade").count());
        assertEquals(1, new NPCQuery(client).hasAction("talk").count());
        assertEquals(0, new NPCQuery(client).hasAction().count());
    }

    @Test
    void objectQueryMatchesAnySuppliedAction() {
        TileObject chest = tileObject("Open", "Search");
        TileObject bankBooth = tileObject("Bank");
        TileObject tree = tileObject("Chop down");
        Client client = client(List.of(), List.of(chest, bankBooth, tree));

        assertEquals(1, new ObjectQuery(client, 20).hasAction("Bank").count());
        assertEquals(2, new ObjectQuery(client, 20).hasAction("Search", "Chop").count());
        assertEquals(0, new ObjectQuery(client, 20).hasAction().count());
    }

    @Test
    void locatableQueryFiltersByWorldArea() {
        NPC inside = npcAt(3201, 3201, 0, 0);
        NPC otherPlane = npcAt(3201, 3201, 1, 0);
        NPC otherWorldView = npcAt(3201, 3201, 0, 1);
        Client client = client(List.of(inside, otherPlane, otherWorldView), List.of());

        WorldArea area = new WorldArea(3200, 3200, 3, 3, 0, 0);
        assertEquals(1, new NPCQuery(client).within(area).count());
    }

    @Test
    void inventoryQueryFiltersByRuntimeActionAndNotedDefinition() {
        InventoryItem bones = inventoryItem(0, 300, "Bones");
        InventoryItem shark = inventoryItem(1, 301, "Shark");
        InventoryItem platebody = inventoryItem(2, 302, "Rune platebody");
        Client client = client(
            List.of(),
            List.of(),
            List.of(bones, shark, platebody),
            Map.of(
                300, itemComposition("Bury", "Drop"),
                301, itemComposition("Eat", "Drop"),
                302, itemComposition("Wear", "Drop")),
            Map.of(
                300, itemDefinition(false),
                301, itemDefinition(true),
                302, itemDefinition(false)),
            Map.of());

        assertEquals(1, new InventoryQuery(client).hasAction("eat").count());
        assertEquals(3, new InventoryQuery(client).hasAction("Drop").count());
        assertEquals(0, new InventoryQuery(client).hasAction("").count());
        assertEquals(1, new InventoryQuery(client).isNoted().count());
    }

    @Test
    void npcQueryFiltersCombatLevelsAndExactIdentities() {
        NPC low = npcIdentity(100, 10, 0);
        NPC middle = npcIdentity(101, 11, 0);
        NPC high = npcIdentity(102, 12, 1);
        Client client = client(
            List.of(low, middle, high),
            List.of(),
            List.of(),
            Map.of(),
            Map.of(),
            Map.of(
                100, npcDefinition(5),
                101, npcDefinition(50),
                102, npcDefinition(100)));

        assertEquals(2, new NPCQuery(client).combatLevelAbove(50).count());
        assertEquals(2, new NPCQuery(client).combatLevelBelow(50).count());
        assertEquals(1, new NPCQuery(client).combatLevelBetween(10, 90).count());
        assertEquals(1, new NPCQuery(client).combatLevelBetween(90, 10).count());

        NPC sameIdentityAsLow = npcIdentity(100, 10, 0);
        assertEquals(2, new NPCQuery(client).exclude(sameIdentityAsLow).count());
        assertEquals(1, new NPCQuery(client).exclude(sameIdentityAsLow, middle).count());
    }

    @Test
    void widgetQueryMatchesAnySuppliedText() {
        Widget continueWidget = widget("Click here to continue");
        Widget warningWidget = widget("Warning: inventory full");
        Widget otherWidget = widget("Other");

        assertEquals(2, WidgetQuery.from(
            null, List.of(continueWidget, warningWidget, otherWidget))
            .textContains("continue", "warning")
            .count());
        assertEquals(0, WidgetQuery.from(null, List.of(continueWidget)).textContains().count());
    }

    private static NPC npc(String... actions) {
        NPC npc = new NPC();
        setField(npc, "actions", List.of(actions));
        return npc;
    }

    private static NPC npcAt(int worldX, int worldY, int plane, int worldViewId) {
        NPC npc = new NPC();
        setField(npc, "worldX", worldX);
        setField(npc, "worldY", worldY);
        setField(npc, "plane", plane);
        setField(npc, "worldViewId", worldViewId);
        return npc;
    }

    private static NPC npcIdentity(int id, int hashIndex, int worldViewId) {
        NPC npc = new NPC();
        setField(npc, "id", id);
        setField(npc, "hashIndex", hashIndex);
        setField(npc, "worldViewId", worldViewId);
        return npc;
    }

    private static TileObject tileObject(String... actions) {
        TileObject object = new TileObject();
        setField(object, "actions", List.of(actions));
        return object;
    }

    private static InventoryItem inventoryItem(int slot, int id, String name) {
        InventoryItem item = new InventoryItem();
        setField(item, "slot", slot);
        setField(item, "id", id);
        setField(item, "quantity", 1);
        setField(item, "name", name);
        return item;
    }

    private static ItemComposition itemComposition(String... actions) {
        ItemComposition definition = new ItemComposition();
        setField(definition, "inventoryActions", List.of(actions));
        return definition;
    }

    private static ItemDefinition itemDefinition(boolean noted) {
        ItemDefinition definition = new ItemDefinition();
        setField(definition, "noted", noted);
        return definition;
    }

    private static NPCDefinition npcDefinition(int combatLevel) {
        NPCDefinition definition = new NPCDefinition();
        setField(definition, "combatLevel", combatLevel);
        return definition;
    }

    private static Widget widget(String text) {
        Widget widget = new Widget();
        setField(widget, "text", text);
        return widget;
    }

    private static Client client(List<NPC> npcs, List<TileObject> objects) {
        return client(npcs, objects, List.of(), Map.of(), Map.of(), Map.of());
    }

    private static Client client(List<NPC> npcs,
                                 List<TileObject> objects,
                                 List<InventoryItem> inventory,
                                 Map<Integer, ItemComposition> compositions,
                                 Map<Integer, ItemDefinition> definitions,
                                 Map<Integer, NPCDefinition> npcDefinitions) {
        return (Client) Proxy.newProxyInstance(
            ActionQueryTest.class.getClassLoader(),
            new Class<?>[] { Client.class },
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "npcs":
                        return npcs;
                    case "tileObjects":
                        return objects;
                    case "inventoryItems":
                        return inventory;
                    case "itemComposition":
                        return Optional.ofNullable(compositions.get((Integer) args[0]));
                    case "itemDefinition":
                        return Optional.ofNullable(definitions.get((Integer) args[0]));
                    case "npcDefinition":
                        return Optional.ofNullable(npcDefinitions.get((Integer) args[0]));
                    default:
                        return defaultValue(method.getReturnType());
                }
            });
    }

    private static Object defaultValue(Class<?> type) {
        if (type == Void.TYPE) return null;
        if (type == Boolean.TYPE) return false;
        if (type == Byte.TYPE) return (byte) 0;
        if (type == Short.TYPE) return (short) 0;
        if (type == Integer.TYPE) return 0;
        if (type == Long.TYPE) return 0L;
        if (type == Float.TYPE) return 0.0f;
        if (type == Double.TYPE) return 0.0d;
        if (type == Optional.class) return Optional.empty();
        if (type == OptionalInt.class) return OptionalInt.empty();
        if (type == OptionalLong.class) return OptionalLong.empty();
        if (type == List.class) return Collections.emptyList();
        return null;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
