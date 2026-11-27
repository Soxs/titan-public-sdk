package net.titan.api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.titan.api.internal.GameDataBackend;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class GameDataTest {
    private static final class Backend implements GameDataBackend {
        final List<String> asked = new ArrayList<>();
        GameData.Status status = GameData.Status.READY;
        byte[] answer = new byte[0];
        @Override public GameData.Result gameData(String name, long[] numbers, String[] texts) {
            asked.add(name + " " + Arrays.toString(numbers) + " " + Arrays.toString(texts));
            return new GameData.Result(status, answer);
        }
    }
    private final Backend backend = new Backend();
    private void with(Object... values) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Object value : values) {
            if (value instanceof String) {
                byte[] text = ((String) value).getBytes(StandardCharsets.UTF_8);
                number(out, text.length, 4);
                out.write(text, 0, text.length);
                for (int i = text.length; i % 4 != 0; i++) out.write(0);
            } else number(out, ((Number) value).longValue(), value instanceof Long ? 8 : 4);
        }
        backend.answer = out.toByteArray();
        TitanRuntime.setGameDataBackend(backend);
    }
    private static void number(ByteArrayOutputStream out, long value, int bytes) {
        for (int i = 0; i < bytes; i++) out.write((int) (value >> 8 * i));
    }
    private String asked() { return backend.asked.get(backend.asked.size() - 1); }
    @AfterEach void clear() { TitanRuntime.clearGameDataBackend(backend); }

    @Test void friendsAndChannelsAreReadWhole() {
        with(2, "Ann", "", 301, 2, "Bób", "Bob", 0, 0, 1, "Cad", "");
        GameData.FriendList list = GameData.friends().orElseThrow();
        assertEquals("titan.game.read.friends.v1 [] []", asked());
        assertTrue(list.loaded());
        assertEquals("Ann", list.friends().get(0).name()); assertEquals(301, list.friends().get(0).world()); assertEquals(2, list.friends().get(0).rank());
        assertEquals("Bób", list.friends().get(1).name()); assertEquals("Bob", list.friends().get(1).previousName());
        assertEquals("Cad", list.ignored().get(0).name());
        assertThrows(UnsupportedOperationException.class, () -> list.friends().clear());
        with(-1, 0);
        assertFalse(GameData.friends().orElseThrow().loaded());

        with("Zezima", "Owner", 1, 2, 1, "Ann", 301, 3);
        GameData.FriendsChat chat = GameData.friendsChat().orElseThrow();
        assertEquals("Owner", chat.owner()); assertEquals(2, chat.kickRank()); assertEquals(301, chat.users().get(0).world()); assertEquals(3, chat.users().get(0).rank());
        with(1, "Clan", -1, 100, 1, "Ann", 126, 302);
        GameData.ClanChannel channel = GameData.clanChannel(GameData.Clan.VISITED).orElseThrow();
        assertEquals("titan.game.read.clanChannel.v1 [-1] []", asked());
        assertTrue(channel.present()); assertEquals(302, channel.users().get(0).world()); assertEquals(126, channel.users().get(0).rank());
        with(0);
        assertFalse(GameData.clanChannel(GameData.Clan.OWN).orElseThrow().present());
        with(1, "Clan", 1, "Ann", 126, 7000);
        assertEquals(7000, GameData.clanSettings(GameData.Clan.IRONMAN_GROUP).orElseThrow().members().get(0).joinedDay());
        assertEquals("titan.game.read.clanSettings.v1 [1] []", asked());
    }

    @Test void pilesAndHeldKeysAreReadWhole() {
        with(1, 5000, 0, 12, 13, 1L << 40, 995, 25, 5100, 5300, 1, 0);
        GameData.GroundPiles piles = GameData.groundItems(0).orElseThrow();
        assertEquals(5000, piles.tick());
        GameData.GroundPile coins = piles.piles().get(0);
        assertEquals(995, coins.id()); assertEquals(25, coins.quantity()); assertEquals(1L << 40, coins.identity());
        assertEquals(5300, coins.despawnTick()); assertEquals(1, coins.ownership()); assertFalse(coins.neverVisible());
        with(1 << 16, 0, 1 << 17, 0, 0, 0, 0, 0, 4, 60, 41, 1700000000123L);
        GameData.HeldInput input = GameData.input().orElseThrow();
        assertTrue(input.held(16)); assertTrue(input.held(81)); assertFalse(input.held(17)); assertFalse(input.held(-1)); assertFalse(input.held(256));
        assertEquals(4, input.mouseButton()); assertEquals(1700000000123L, input.mousePressedAt());
        with();
        assertTrue(GameData.playSound(2277));
        assertEquals("titan.game.change.sound.v1 [2277, 1, 0] []", asked());
    }

    @Test void theMenuAndLootAreReadWhole() {
        with(1, 40, 50, 120, 80);
        GameData.MenuBounds menu = GameData.menuBounds().orElseThrow();
        assertTrue(menu.open()); assertEquals(40, menu.x()); assertEquals(50, menu.y()); assertEquals(120, menu.width()); assertEquals(80, menu.height());
        assertEquals("titan.game.read.menuPlace.v1 [] []", asked());
        assertTrue(GameData.menuBounds(7, 12).orElseThrow().open());
        assertEquals("titan.game.read.menuPlace.v1 [7, 12] []", asked());
        with(0);
        assertFalse(GameData.menuBounds().orElseThrow().open());
        assertTrue(GameData.loot(5).isEmpty());
        assertEquals("titan.game.read.loot.v1 [5] []", asked());
        with(1, 0, 3028, 77, 2, 3205, 3206, 0, "Goblin", 2, 526, 1, 995, 12);
        GameData.Loot loot = new net.titan.api.events.LootReceived(5).loot().orElseThrow();
        assertEquals(GameData.LootSource.NPC, loot.source()); assertEquals(3028, loot.npcId()); assertEquals(77, loot.index());
        assertEquals(2, loot.combatLevel()); assertEquals(3205, loot.worldX()); assertEquals(3206, loot.worldY()); assertEquals(0, loot.plane());
        assertEquals("Goblin", loot.name()); assertEquals(2, loot.items().size());
        assertEquals(995, loot.items().get(1).id()); assertEquals(12, loot.items().get(1).quantity());
        with(1, 3, 0, 0, 0, 0, 0, 0, "", 0);
        assertTrue(GameData.loot(5).isEmpty(), "a source which is none");
        with(1, 2, 0, 0, 0, 0);
        assertTrue(GameData.loot(5).isEmpty(), "it ends early");
    }
    @Test void theSceneAndTheMapsAreReadWhole() {
        with(1, 2, 51, 52);
        GameData.HoveredTile tile = GameData.hoveredTile().orElseThrow();
        assertTrue(tile.present()); assertEquals(2, tile.plane()); assertEquals(51, tile.sceneX()); assertEquals(52, tile.sceneY());
        with(0);
        assertFalse(GameData.hoveredTile().orElseThrow().present());
        with(3, 1024, 4);
        assertEquals(1024, GameData.minimap().orElseThrow().heading().orElseThrow());
        assertEquals(4, GameData.minimap().orElseThrow().pixelsToATile().orElseThrow());
        with(0, 0, 0);
        assertTrue(GameData.minimap().orElseThrow().heading().isEmpty()); assertTrue(GameData.minimap().orElseThrow().pixelsToATile().isEmpty());
        with(1, 6400, 6528, 512, 320, 448);
        GameData.WorldEntityPlace boat = GameData.worldEntity(3).orElseThrow();
        assertEquals("titan.game.read.worldEntity.v1 [3] []", asked());
        assertTrue(boat.present()); assertEquals(6400, boat.x()); assertEquals(6528, boat.y()); assertEquals(512, boat.orientation());
        assertEquals(320, boat.ownX()); assertEquals(448, boat.ownY());
        with(0);
        assertFalse(GameData.worldEntity(3).orElseThrow().present());
        with(10, 3222, 3218);
        assertEquals(10, GameData.worldMapArea().orElseThrow().area()); assertEquals(3218, GameData.worldMapArea().orElseThrow().tileY());
    }
    @Test void chatLinesAreReadAndChanged() {
        with(1, 2, "Ann", "Hello", "Chat");
        GameData.ChatLine line = GameData.chatLine(77).orElseThrow();
        assertTrue(line.present()); assertEquals(2, line.channel()); assertEquals("Ann", line.name()); assertEquals("Hello", line.message()); assertEquals("Chat", line.sender());
        with(0);
        assertFalse(GameData.chatLine(77).orElseThrow().present());
        with(2, 77, 76);
        assertEquals(List.of(77, 76), GameData.chatLines(2).orElseThrow());
        with();
        assertTrue(GameData.setChatLine(77, GameData.ChatLinePart.SENDER, "Other"));
        assertEquals("titan.game.change.chatLine.v1 [77, 2] [Other]", asked());
    }
    @Test void widgetsAreReadAndTheirListenersSet() {
        with(0x281, 1, 0, 0, 0, 0, 0, 0, 1, 0, 200);
        GameData.WidgetFields fields = GameData.widgetFields(9, 4).orElseThrow();
        assertEquals("titan.game.read.widgetFields.v1 [9, 4, 0] []", asked());
        assertEquals(1, fields.xMode().orElseThrow()); assertTrue(fields.yMode().isEmpty()); assertTrue(fields.hasListener().orElseThrow());
        assertTrue(fields.scrollWidth().isEmpty()); assertEquals(200, fields.opacity().orElseThrow());
        with(1, 9, 5, 1L << 40);
        GameData.WidgetRef child = GameData.widgetChildren(9, GameData.WidgetChildren.NESTED).orElseThrow().get(0);
        assertEquals("titan.game.read.widgetFamily.v1 [9, -1, 0, 2] []", asked());
        assertEquals(9, child.packedId()); assertEquals(5, child.childIndex()); assertEquals(1L << 40, child.incarnation());
        with(2, 548 << 16 | 1, -1, 1L << 40, 548 << 16 | 9, -1, 3L << 40);
        List<GameData.WidgetRef> roots = GameData.widgetRoots(548).orElseThrow();
        assertEquals("titan.game.read.widgetRoots.v1 [548] []", asked());
        assertEquals(2, roots.size());
        assertEquals(548 << 16 | 1, roots.get(0).packedId()); assertEquals(-1, roots.get(0).childIndex());
        assertEquals(548 << 16 | 9, roots.get(1).packedId()); assertEquals(3L << 40, roots.get(1).incarnation());
        with(0);
        assertTrue(GameData.widgetRoots(548).orElseThrow().isEmpty());
        List<Object> slots = new java.util.ArrayList<>(List.of(1L << 41, 2, 1L << 40, 1 | 1 << 13));
        for (int i = 0; i < 17; i++) slots.add(i == 0 ? 995 : i == 13 ? 32 : 0);
        slots.add(0L); slots.add(0);
        for (int i = 0; i < 17; i++) slots.add(0);
        with(slots.toArray());
        List<GameData.WidgetSlot> found = GameData.widgetSlots(9).orElseThrow();
        assertEquals("titan.game.read.widgetSlots.v1 [9, 0] []", asked());
        assertEquals(2, found.size());
        assertEquals(1L << 40, found.get(0).incarnation()); assertEquals(995, found.get(0).itemId().orElseThrow());
        assertEquals(32, found.get(0).originalHeight().orElseThrow()); assertTrue(found.get(0).originalWidth().isEmpty());
        assertTrue(found.get(1).empty()); assertTrue(found.get(1).itemId().isEmpty());
        with(3, 0, 100, 1, "text", 2, 1L << 40);
        assertEquals(List.of(100, "text", 1L << 40), GameData.widgetListener(9, 1400).orElseThrow());
        with(1, 7, 100);
        assertTrue(GameData.widgetListener(9, 1400).isEmpty(), "no such kind");
        with();
        assertTrue(GameData.setWidgetListener(9, -1, 1400, 100, 5, "a", "b"));
        assertEquals("titan.game.change.widgetListener.v1 [9, -1, 0, 1400, 100, 0, 5, 1, 0, 1, 1] [a, b]", asked());
        assertThrows(IllegalArgumentException.class, () -> GameData.setWidgetListener(9, -1, 1400, 100, 5L));
    }
    @Test void scriptsActorsAndTheGameAreChanged() {
        with(2, 5, 6, 2, 1, "text", 0);
        GameData.ScriptStacks stacks = GameData.scriptStacks().orElseThrow();
        assertEquals(List.of(5, 6), stacks.integers()); assertEquals(Arrays.asList("text", null), stacks.texts());
        with(9, 4, -1, -1);
        assertEquals(4, GameData.scriptComponents().orElseThrow().childIndex()); assertEquals(-1, GameData.scriptComponents().orElseThrow().secondPackedId());
        with();
        assertTrue(GameData.setScriptText(3, "text"));
        assertEquals("titan.game.change.scriptStacks.v1 [-1, -1, 0, 3] [text]", asked());
        assertTrue(GameData.setOverheadText(actor(0x7ff0_0000_1000L), "Hello"));
        assertEquals("titan.game.change.overheadText.v1 [-1, " + 0x7ff0_0000_1000L + "] [Hello]", asked());
        int before = backend.asked.size();
        assertFalse(GameData.setOverheadText(actor(0), "Hello"));
        assertEquals(before, backend.asked.size(), "nobody");
        assertTrue(GameData.openWorldSwitcher());
        assertEquals("titan.game.change.world.v1 [0, 0] []", asked());
        assertTrue(GameData.reloadScene());
        assertEquals("titan.game.change.gameState.v1 [25] []", asked());
        assertTrue(GameData.dropConnection());
        assertEquals("titan.game.change.gameState.v1 [40] []", asked());
        backend.status = GameData.Status.NOT_READY;
        assertFalse(GameData.reloadScene(), "nobody is in the game");
    }
    private static Actor actor(long entity) {
        return (Actor) java.lang.reflect.Proxy.newProxyInstance(Actor.class.getClassLoader(), new Class<?>[]{Actor.class}, (proxy, method, arguments) -> {
            if (method.getName().equals("entityPtr")) return entity;
            throw new AssertionError(method);
        });
    }

    @Test void aRefusalOrAMalformedAnswerIsNone() {
        assertEquals(GameData.Status.UNSUPPORTED, GameData.run("titan.game.read.friends.v1", new long[0]).status(), "no host");
        with(0, 0);
        for (GameData.Status status : GameData.Status.values()) {
            backend.status = status;
            assertEquals(status == GameData.Status.READY, GameData.friends().isPresent(), status.name());
            assertEquals(status, GameData.run("name", new long[]{1}, "text").status());
        }
        assertEquals("name [1] [text]", asked());
        backend.status = GameData.Status.READY;
        assertEquals(GameData.Status.INVALID, GameData.run("name", new long[0], "a\0b").status());
        with(1, "Ann");
        assertTrue(GameData.friends().isEmpty(), "it ends early");
        with(0, 0, 1);
        assertTrue(GameData.friends().isEmpty(), "more follows");
        with(1, 1 << 30);
        assertTrue(GameData.friends().isEmpty(), "a text beyond its end");
        assertEquals(GameData.Status.NOT_READY, GameData.Status.fromValue(99));
    }
}
