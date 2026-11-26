package net.titan.api;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import net.titan.api.internal.TitanRuntime;

/** Friends, clans, items on the ground, held keys, sounds, and what the game's widgets, chat, scripts and scene have, each
 * read or changed in one call on the game thread. Empty where it cannot be read now. Lists of friends and clans ask the
 * game once for every row, so keep what you reuse. SDK 192, and later where it says so. */
public final class GameData {
    private GameData() {}

    public enum Status {
        READY,
        /** Nothing to answer with now, such as before the account is logged in. */
        NOT_READY, INVALID,
        /** This client or this build of the game has no such operation. */
        UNSUPPORTED, WRONG_THREAD, BUFFER_TOO_SMALL;

        public static Status fromValue(int value) {
            return value >= 0 && value < values().length ? values()[value] : NOT_READY;
        }
    }
    /** Little-endian bytes, empty unless ready. */
    public static final class Result {
        private final Status status;
        private final byte[] bytes;
        public Result(Status status, byte[] bytes) {
            this.status = Objects.requireNonNull(status, "status");
            this.bytes = status == Status.READY ? bytes.clone() : new byte[0];
        }
        public Status status() { return status; }
        public boolean ready() { return status == Status.READY; }
        public byte[] bytes() { return bytes.clone(); }
    }
    /** Any operation by its name, with whole numbers and texts. */
    public static Result run(String name, long[] numbers, String... texts) {
        Objects.requireNonNull(name, "name");
        for (String text : texts) if (text.indexOf('\0') >= 0) return new Result(Status.INVALID, null);
        return TitanRuntime.getGameDataBackend().gameData(name, numbers.clone(), texts.clone());
    }

    public static final class Friend {
        private final String name, previousName;
        private final int world, rank;
        private Friend(Reader in, boolean ignored) {
            name = in.text(); previousName = in.text();
            world = ignored ? 0 : in.i32(); rank = ignored ? 0 : in.i32();
        }
        public String name() { return name; }
        public String previousName() { return previousName; }
        /** 0 when offline. */
        public int world() { return world; }
        public int rank() { return rank; }
    }
    public static final class FriendList {
        private final boolean loaded;
        private final List<Friend> friends, ignored;
        private FriendList(Reader in) {
            int count = in.i32();
            loaded = count >= 0;
            friends = in.rows(count, row -> new Friend(row, false));
            ignored = in.rows(in.i32(), row -> new Friend(row, true));
        }
        public boolean loaded() { return loaded; }
        public List<Friend> friends() { return friends; }
        public List<Friend> ignored() { return ignored; }
    }
    public static final class ChatUser {
        private final String name;
        private final int world, rank;
        private ChatUser(String name, int world, int rank) { this.name = name; this.world = world; this.rank = rank; }
        public String name() { return name; }
        public int world() { return world; }
        public int rank() { return rank; }
    }
    /** An empty name is no channel. */
    public static final class FriendsChat {
        private final String name, owner;
        private final int rank, kickRank;
        private final List<ChatUser> users;
        private FriendsChat(Reader in) {
            name = in.text(); owner = in.text(); rank = in.i32(); kickRank = in.i32();
            users = in.rows(in.i32(), row -> new ChatUser(row.text(), row.i32(), row.i32()));
        }
        public String name() { return name; }
        public String owner() { return owner; }
        public int rank() { return rank; }
        public int kickRank() { return kickRank; }
        public List<ChatUser> users() { return users; }
    }
    public enum Clan {
        OWN(0), IRONMAN_GROUP(1), VISITED(-1);
        private final int value;
        Clan(int value) { this.value = value; }
    }
    public static final class ClanChannel {
        private final boolean present;
        private final String name;
        private final int talkRank, kickRank;
        private final List<ChatUser> users;
        private ClanChannel(Reader in) {
            present = in.i32() == 1;
            name = present ? in.text() : "";
            talkRank = present ? in.i32() : 0;
            kickRank = present ? in.i32() : 0;
            users = present ? in.rows(in.i32(), row -> {
                String user = row.text();
                int rank = row.i32();
                return new ChatUser(user, row.i32(), rank);
            }) : Collections.emptyList();
        }
        public boolean present() { return present; }
        public String name() { return name; }
        public int talkRank() { return talkRank; }
        public int kickRank() { return kickRank; }
        public List<ChatUser> users() { return users; }
    }
    public static final class ClanMember {
        private final String name;
        private final int rank, joinedDay;
        private ClanMember(Reader in) { name = in.text(); rank = in.i32(); joinedDay = in.i32(); }
        public String name() { return name; }
        public int rank() { return rank; }
        /** A day in the game's own count. */
        public int joinedDay() { return joinedDay; }
    }
    public static final class ClanSettings {
        private final boolean present;
        private final String name;
        private final List<ClanMember> members;
        private ClanSettings(Reader in) {
            present = in.i32() == 1;
            name = present ? in.text() : "";
            members = present ? in.rows(in.i32(), ClanMember::new) : Collections.emptyList();
        }
        public boolean present() { return present; }
        public String name() { return name; }
        public List<ClanMember> members() { return members; }
    }
    public static final class GroundPile {
        private final int plane, sceneX, sceneY, id, quantity, visibleTick, despawnTick, ownership;
        private final long identity;
        private final boolean neverVisible;
        private GroundPile(Reader in) {
            plane = in.i32(); sceneX = in.i32(); sceneY = in.i32(); identity = in.i64(); id = in.i32(); quantity = in.i32();
            visibleTick = in.i32(); despawnTick = in.i32(); ownership = in.i32(); neverVisible = in.i32() == 1;
        }
        public int plane() { return plane; }
        public int sceneX() { return sceneX; }
        public int sceneY() { return sceneY; }
        /** Of this pile while it lies there. */
        public long identity() { return identity; }
        public int id() { return id; }
        public int quantity() { return quantity; }
        /** Server ticks, as {@link GroundPiles#tick} counts them. */
        public int visibleTick() { return visibleTick; }
        public int despawnTick() { return despawnTick; }
        /** 0 nobody's, 1 the account's, 2 another player's, 3 the group's. */
        public int ownership() { return ownership; }
        public boolean neverVisible() { return neverVisible; }
    }
    public static final class GroundPiles {
        private final int tick;
        private final List<GroundPile> piles;
        private GroundPiles(Reader in) {
            int count = in.i32();
            tick = in.i32();
            piles = in.rows(count, GroundPile::new);
        }
        public int tick() { return tick; }
        public List<GroundPile> piles() { return piles; }
    }
    public static final class HeldInput {
        private final int[] keys = new int[8];
        private final int mouseButton, mouseIdleMillis, keyIdleMillis;
        private final long mousePressedAt;
        private HeldInput(Reader in) {
            for (int i = 0; i < keys.length; i++) keys[i] = in.i32();
            mouseButton = in.i32(); mouseIdleMillis = in.i32(); keyIdleMillis = in.i32(); mousePressedAt = in.i64();
        }
        /** Whether a key is down, by its Windows virtual key code. */
        public boolean held(int virtualKey) { return virtualKey >= 0 && virtualKey < 256 && (keys[virtualKey >> 5] >> (virtualKey & 31) & 1) == 1; }
        /** 0, 1 left, 2 right, 4 middle. */
        public int mouseButton() { return mouseButton; }
        public int mouseIdleMillis() { return mouseIdleMillis; }
        public int keyIdleMillis() { return keyIdleMillis; }
        /** Milliseconds since 1970. */
        public long mousePressedAt() { return mousePressedAt; }
    }

    /** The tile of the top-level scene which the game's own "Walk here" goes to. SDK 193. */
    public static final class HoveredTile {
        private final boolean present;
        private final int plane, sceneX, sceneY;
        private HoveredTile(Reader in) {
            present = in.i32() == 1;
            plane = present ? in.i32() : 0; sceneX = present ? in.i32() : 0; sceneY = present ? in.i32() : 0;
        }
        public boolean present() { return present; }
        public int plane() { return plane; }
        public int sceneX() { return sceneX; }
        public int sceneY() { return sceneY; }
    }
    /** Where the open menu is drawn, in the pixels which widgets have. SDK 194. */
    public static final class MenuBounds {
        private final boolean open;
        private final int x, y, width, height;
        private MenuBounds(Reader in) {
            open = in.i32() == 1;
            x = open ? in.i32() : 0; y = open ? in.i32() : 0; width = open ? in.i32() : 0; height = open ? in.i32() : 0;
        }
        public boolean open() { return open; }
        public int x() { return x; }
        public int y() { return y; }
        public int width() { return width; }
        public int height() { return height; }
    }
    public enum LootSource {
        NPC, PLAYER,
        /** The server says it itself, as it does of some bosses. It has no place. */
        SERVER
    }
    public static final class LootItem {
        private final int id, quantity;
        private LootItem(Reader in) { id = in.i32(); quantity = in.i32(); }
        public int id() { return id; }
        public int quantity() { return quantity; }
    }
    /** What a monster or a player left for the account. SDK 194. */
    public static final class Loot {
        private final LootSource source;
        private final int npcId, index, combatLevel, worldX, worldY, plane;
        private final String name;
        private final List<LootItem> items;
        private Loot(Reader in) {
            int kind = in.i32();
            if (kind < 0 || kind >= LootSource.values().length) throw new BufferUnderflowException();
            source = LootSource.values()[kind];
            npcId = in.i32(); index = in.i32(); combatLevel = in.i32(); worldX = in.i32(); worldY = in.i32(); plane = in.i32();
            name = in.text();
            items = in.rows(in.i32(), LootItem::new);
        }
        public LootSource source() { return source; }
        /** Its definition, or -1 for a player. */
        public int npcId() { return npcId; }
        public int index() { return index; }
        public int combatLevel() { return combatLevel; }
        /** Of its south-west tile, or -1. */
        public int worldX() { return worldX; }
        public int worldY() { return worldY; }
        public int plane() { return plane; }
        public String name() { return name; }
        public List<LootItem> items() { return items; }
    }
    /** How the game draws its minimap. SDK 193. */
    public static final class MinimapView {
        private final int known, heading, zoom;
        private MinimapView(Reader in) { known = in.i32(); heading = in.i32(); zoom = in.i32(); }
        /** It is turned by this, in 2048 to a turn. */
        public OptionalInt heading() { return (known & 1) != 0 ? OptionalInt.of(heading) : OptionalInt.empty(); }
        public OptionalInt pixelsToATile() { return (known & 2) != 0 ? OptionalInt.of(zoom) : OptionalInt.empty(); }
    }
    /** Where a world entity, such as a boat, stands in the top-level world. SDK 193. */
    public static final class WorldEntityPlace {
        private final boolean present;
        private final int[] values = new int[5];
        private WorldEntityPlace(Reader in) {
            present = in.i32() == 1;
            for (int i = 0; present && i < values.length; i++) values[i] = in.i32();
        }
        public boolean present() { return present; }
        /** In 1/128 of a tile of the top-level scene. */
        public int x() { return values[0]; }
        public int y() { return values[1]; }
        /** In 2048 to a turn. */
        public int orientation() { return values[2]; }
        /** The point of its own world which is there. */
        public int ownX() { return values[3]; }
        public int ownY() { return values[4]; }
    }
    /** SDK 193. */
    public static final class WorldMapArea {
        private final int area, tileX, tileY;
        private WorldMapArea(Reader in) { area = in.i32(); tileX = in.i32(); tileY = in.i32(); }
        /** -1 before the map has shown one. */
        public int area() { return area; }
        public int tileX() { return tileX; }
        public int tileY() { return tileY; }
    }
    /** SDK 193. */
    public static final class ChatLine {
        private final boolean present;
        private final int channel;
        private final String name, message, sender;
        private ChatLine(Reader in) {
            present = in.i32() == 1;
            channel = present ? in.i32() : 0;
            name = present ? in.text() : ""; message = present ? in.text() : ""; sender = present ? in.text() : "";
        }
        public boolean present() { return present; }
        public int channel() { return channel; }
        public String name() { return name; }
        public String message() { return message; }
        public String sender() { return sender; }
    }
    public enum ChatLinePart { NAME, MESSAGE, SENDER }
    /** What a widget's definition and scripts set of it. Empty where this game's is not known. SDK 193. */
    public static final class WidgetFields {
        private final int known;
        private final int[] values = new int[10];
        private WidgetFields(Reader in) {
            known = in.i32();
            for (int i = 0; i < values.length; i++) values[i] = in.i32();
        }
        private OptionalInt value(int field) { return (known >> field & 1) != 0 ? OptionalInt.of(values[field]) : OptionalInt.empty(); }
        public OptionalInt xMode() { return value(0); }
        public OptionalInt yMode() { return value(1); }
        public OptionalInt widthMode() { return value(2); }
        public OptionalInt heightMode() { return value(3); }
        public OptionalInt textAlignX() { return value(4); }
        public OptionalInt textAlignY() { return value(5); }
        public OptionalInt lineHeight() { return value(6); }
        public Optional<Boolean> hasListener() { return value(7).isPresent() ? Optional.of(values[7] != 0) : Optional.empty(); }
        public OptionalInt scrollWidth() { return value(8); }
        public OptionalInt opacity() { return value(9); }
    }
    /** Those of a widget which scripts made, which its interface has in it, and those of the interface opened in it. */
    public enum WidgetChildren { DYNAMIC, STATIC, NESTED }
    /** SDK 193. */
    public static final class WidgetRef {
        private final int packedId, childIndex;
        private final long incarnation;
        private WidgetRef(Reader in) { packedId = in.i32(); childIndex = in.i32(); incarnation = in.i64(); }
        public int packedId() { return packedId; }
        /** -1 for one which is no child itself. */
        public int childIndex() { return childIndex; }
        public long incarnation() { return incarnation; }
    }
    /** A slot of a flat widget's script-made children: the child there, with the fields a read of it has. SDK 195. */
    public static final class WidgetSlot {
        private final long incarnation;
        private final int known;
        private final int[] values = new int[17];
        private WidgetSlot(Reader in) {
            incarnation = in.i64();
            known = in.i32();
            for (int i = 0; i < values.length; i++) values[i] = in.i32();
        }
        private OptionalInt value(int field) { return (known >> field & 1) != 0 ? OptionalInt.of(values[field]) : OptionalInt.empty(); }
        /** 0 where the slot is empty. */
        public long incarnation() { return incarnation; }
        public boolean empty() { return incarnation == 0; }
        public OptionalInt itemId() { return value(0); }
        public OptionalInt type() { return value(1); }
        public OptionalInt itemQuantity() { return value(2); }
        public OptionalInt spriteId() { return value(3); }
        public OptionalInt modelType() { return value(4); }
        public OptionalInt modelId() { return value(5); }
        public OptionalInt relativeX() { return value(6); }
        public OptionalInt relativeY() { return value(7); }
        public OptionalInt width() { return value(8); }
        public OptionalInt height() { return value(9); }
        public OptionalInt originalX() { return value(10); }
        public OptionalInt originalY() { return value(11); }
        public OptionalInt originalWidth() { return value(12); }
        public OptionalInt originalHeight() { return value(13); }
        public OptionalInt scrollX() { return value(14); }
        public OptionalInt scrollY() { return value(15); }
        public OptionalInt scrollHeight() { return value(16); }
    }
    /** The interpreter's stacks. A place which holds no text is null. SDK 193. */
    public static final class ScriptStacks {
        private final List<Integer> integers;
        private final List<String> texts;
        private ScriptStacks(Reader in) {
            integers = in.rows(in.i32(), Reader::i32);
            texts = in.rows(in.i32(), row -> row.i32() == 1 ? row.text() : null);
        }
        public List<Integer> integers() { return integers; }
        public List<String> texts() { return texts; }
    }
    /** The running script's component and its second one. A packed ID of -1 is none. SDK 193. */
    public static final class ScriptComponents {
        private final int packedId, childIndex, secondPackedId, secondChildIndex;
        private ScriptComponents(Reader in) { packedId = in.i32(); childIndex = in.i32(); secondPackedId = in.i32(); secondChildIndex = in.i32(); }
        public int packedId() { return packedId; }
        public int childIndex() { return childIndex; }
        public int secondPackedId() { return secondPackedId; }
        public int secondChildIndex() { return secondChildIndex; }
    }

    public static Optional<FriendList> friends() { return read("titan.game.read.friends.v1", FriendList::new); }
    public static Optional<FriendsChat> friendsChat() { return read("titan.game.read.friendsChat.v1", FriendsChat::new); }
    /** Not inside a script moment, where the game's own scripts are using these lists. */
    public static Optional<ClanChannel> clanChannel(Clan which) { return read("titan.game.read.clanChannel.v1", ClanChannel::new, which.value); }
    public static Optional<ClanSettings> clanSettings(Clan which) { return read("titan.game.read.clanSettings.v1", ClanSettings::new, which.value); }
    /** Every pile in a loaded world. They change with the server's ticks, so read them once in each. */
    public static Optional<GroundPiles> groundItems(int worldViewId) { return read("titan.game.read.groundItems.v1", GroundPiles::new, worldViewId); }
    /** Which keys and mouse button the game's window was last told are down. */
    public static Optional<HeldInput> input() { return read("titan.game.read.input.v1", HeldInput::new); }
    /** As the game's scripts play a sound effect, at the volume the account has set. */
    public static boolean playSound(int id, int loops, int delay) {
        return run("titan.game.change.sound.v1", new long[]{id, loops, delay}).ready();
    }
    public static boolean playSound(int id) { return playSound(id, 1, 0); }

    // --- SDK 193

    /** It stays while the game's menu is open, and is none over an interface or with an item selected. */
    public static Optional<HoveredTile> hoveredTile() { return read("titan.game.read.hoveredTile.v1", HoveredTile::new); }
    /** Of the open menu itself. {@link MenuEntry#getSubMenuBounds()} has the game's own submenus. */
    public static Optional<MenuBounds> menuBounds() { return read("titan.game.read.menuPlace.v1", MenuBounds::new); }
    /** Of the game's own submenu under an entry, by the menu's generation and the entry's own number. */
    public static Optional<MenuBounds> menuBounds(long generation, long entry) {
        return read("titan.game.read.menuPlace.v1", MenuBounds::new, generation, entry);
    }
    /** By the number which {@link net.titan.api.events.LootReceived} tells of. Empty for one which is not of the last 64. */
    public static Optional<Loot> loot(int number) {
        return read("titan.game.read.loot.v1", in -> in.i32() == 1 ? Optional.of(new Loot(in)) : Optional.<Loot>empty(), number)
            .flatMap(Function.identity());
    }
    public static Optional<MinimapView> minimap() { return read("titan.game.read.minimapView.v1", MinimapView::new); }
    /** By the ID of its own world view. */
    public static Optional<WorldEntityPlace> worldEntity(int worldViewId) { return read("titan.game.read.worldEntity.v1", WorldEntityPlace::new, worldViewId); }
    /** The area which the world map shows, and the tile in the middle of it. */
    public static Optional<WorldMapArea> worldMapArea() { return read("titan.game.read.worldMap.v1", WorldMapArea::new); }
    /** A line of the game's chat, by its number. */
    public static Optional<ChatLine> chatLine(int number) { return read("titan.game.read.chatLine.v1", ChatLine::new, number); }
    /** The numbers of a channel's lines, the newest first. */
    public static Optional<List<Integer>> chatLines(int channel) { return read("titan.game.read.chatLines.v1", in -> in.rows(in.i32(), Reader::i32), channel); }
    /** The chat shows it when it is next built. */
    public static boolean setChatLine(int number, ChatLinePart part, String text) { return change("chatLine", new long[]{number, part.ordinal()}, text); }
    /** Of a widget, or with an index of its direct child there. */
    public static Optional<WidgetFields> widgetFields(int packedId, int childIndex) {
        return read("titan.game.read.widgetFields.v1", WidgetFields::new, packedId, childIndex, 0);
    }
    public static Optional<WidgetFields> widgetFields(int packedId) { return widgetFields(packedId, -1); }
    public static Optional<List<WidgetRef>> widgetChildren(int packedId, WidgetChildren which, int childIndex) {
        return read("titan.game.read.widgetFamily.v1", in -> in.rows(in.i32(), WidgetRef::new), packedId, childIndex, 0, which.ordinal());
    }
    public static Optional<List<WidgetRef>> widgetChildren(int packedId, WidgetChildren which) { return widgetChildren(packedId, which, -1); }
    /** Every slot of a flat widget's script-made children, the empty ones too, with each child's fields: a container such as
     * the bank's items in one read. Empty when one of them cannot be read now. SDK 195. */
    public static Optional<List<WidgetSlot>> widgetSlots(int packedId) {
        return read("titan.game.read.widgetSlots.v1", in -> { in.i64(); return in.rows(in.i32(), WidgetSlot::new); }, packedId, 0);
    }
    /** A listener by the opcode which sets it in the game's scripts, such as 1400 on click or 1409 on an option, and the
     * load listener by 0: its script and then its arguments, each an Integer, a String or a Long. Empty for none. */
    public static Optional<List<Object>> widgetListener(int packedId, int opcode, int childIndex) {
        return read("titan.game.read.widgetListener.v1", in -> in.rows(in.i32(), GameData::listenerValue), packedId, childIndex, 0, opcode);
    }
    public static Optional<List<Object>> widgetListener(int packedId, int opcode) { return widgetListener(packedId, opcode, -1); }
    private static Object listenerValue(Reader in) {
        switch (in.i32()) {
            case 0: return in.i32();
            case 1: return in.text();
            case 2: return in.i64();
            default: throw new BufferUnderflowException();
        }
    }
    /** Arguments are Integers and Strings. A script of -1 takes the listener away. */
    public static boolean setWidgetListener(int packedId, int childIndex, int opcode, int script, Object... arguments) {
        long[] numbers = new long[5 + 2 * arguments.length];
        numbers[0] = packedId; numbers[1] = childIndex; numbers[3] = opcode; numbers[4] = script;
        List<String> texts = new ArrayList<>();
        for (int i = 0; i < arguments.length; i++) {
            boolean text = arguments[i] instanceof String;
            if (!text && !(arguments[i] instanceof Integer)) throw new IllegalArgumentException("A listener's argument is an Integer or a String");
            numbers[5 + 2 * i] = text ? 1 : 0;
            numbers[6 + 2 * i] = text ? texts.size() : (Integer) arguments[i];
            if (text) texts.add((String) arguments[i]);
        }
        return change("widgetListener", numbers, texts.toArray(new String[0]));
    }
    /** Inside a script moment those of the script, and elsewhere what the last one left. */
    public static Optional<ScriptStacks> scriptStacks() { return read("titan.game.read.scriptStacks.v1", ScriptStacks::new); }
    /** Inside a script moment. */
    public static Optional<ScriptComponents> scriptComponents() { return read("titan.game.read.scriptComponents.v1", ScriptComponents::new); }
    /** Inside a script moment: replaces the text at a place of the stack. */
    public static boolean setScriptText(int place, String text) { return change("scriptStacks", new long[]{-1, -1, 0, place}, text); }
    /** What a player or an NPC says above its head, for as long as the game shows it. */
    public static boolean setOverheadText(Actor actor, String text) {
        long entity = actor.entityPtr();
        return entity != 0 && change("overheadText", new long[]{-1, entity}, text);
    }
    /** One click nearer to the game's open world switcher, once in a tick until it is open. */
    public static boolean openWorldSwitcher() { return change("world", new long[]{0, 0}); }
    /** Loads the scene of a player in the game anew, as the game's next cycle begins. False when there is none. */
    public static boolean reloadScene() { return change("gameState", new long[]{25}); }
    /** Drops the connection of a player in the game, which the game then makes again. False when there is none. */
    public static boolean dropConnection() { return change("gameState", new long[]{40}); }
    private static boolean change(String name, long[] numbers, String... texts) { return run("titan.game.change." + name + ".v1", numbers, texts).ready(); }

    private static <T> Optional<T> read(String name, Function<Reader,T> decode, long... numbers) {
        Result answer = run(name, numbers);
        if (!answer.ready()) return Optional.empty();
        Reader in = new Reader(answer.bytes);
        try {
            T value = decode.apply(in);
            return in.bytes.hasRemaining() ? Optional.empty() : Optional.of(value);
        } catch (BufferUnderflowException malformed) { return Optional.empty(); }
    }
    private static final class Reader {
        final ByteBuffer bytes;
        Reader(byte[] answer) { bytes = ByteBuffer.wrap(answer).order(ByteOrder.LITTLE_ENDIAN); }
        int i32() { return bytes.getInt(); }
        long i64() { return bytes.getLong(); }
        String text() {
            int size = bytes.getInt();
            if (size < 0 || size > bytes.remaining()) throw new BufferUnderflowException();
            byte[] text = new byte[size];
            bytes.get(text);
            bytes.position(bytes.position() + (4 - size % 4) % 4);
            return new String(text, StandardCharsets.UTF_8);
        }
        <T> List<T> rows(int count, Function<Reader,T> row) {
            List<T> rows = new ArrayList<>();
            for (int i = 0; i < count; i++) rows.add(row.apply(this));
            return Collections.unmodifiableList(rows);
        }
    }
}
