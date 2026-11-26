/// @file titan/game_data.h
/// @brief Friends, clans, items on the ground, held keys, sounds, and what the game's widgets, chat, scripts and scene
/// have, each read or changed in one call.
///
/// Game thread. SDK 192, and 193, 194 or 195 where it says so.
#pragma once

#include "detail/game_data_abi.h"
#include "detail/host.h"

#include <algorithm>
#include <array>
#include <cstdint>
#include <optional>
#include <string>
#include <utility>
#include <variant>
#include <vector>

namespace titan {

using GameDataStatus = TitanPluginSdk::GameDataStatus;

struct GameDataResult {
    GameDataStatus status = GameDataStatus::Unsupported;
    std::vector<uint8_t> bytes;
    bool ready() const { return status == GameDataStatus::Ready; }
};

namespace gameData {

inline GameDataResult run(const TitanPluginSdk::HostApi* host, const std::string& name, const std::vector<int64_t>& numbers = {},
                          const std::vector<std::string>& texts = {}) {
    using namespace TitanPluginSdk;
    if (!host || !host->runGameOperation) return {};
    if (name.find('\0') != std::string::npos || numbers.size() > kGameDataMaxNumbers || texts.size() > kGameDataMaxTexts)
        return {GameDataStatus::Invalid, {}};
    std::vector<const char*> pointers;
    for (const auto& text : texts) {
        if (text.size() > kGameDataMaxTextBytes || text.find('\0') != std::string::npos) return {GameDataStatus::Invalid, {}};
        pointers.push_back(text.c_str());
    }
    std::vector<uint8_t> bytes(64 * 1024);
    for (int attempt = 0; attempt < 3; ++attempt) {
        uint32_t size = 0;
        const auto status = host->runGameOperation(name.c_str(), numbers.data(), static_cast<uint32_t>(numbers.size()), pointers.data(),
            static_cast<uint32_t>(pointers.size()), bytes.data(), static_cast<uint32_t>(bytes.size()), &size);
        if (status == GameDataStatus::BufferTooSmall && size > bytes.size() && size <= kGameDataMaxBytes) {
            bytes.resize(size);
            continue;
        }
        if (status != GameDataStatus::Ready || size > bytes.size()) return {status == GameDataStatus::Ready ? GameDataStatus::NotReady : status, {}};
        bytes.resize(size);
        return {status, std::move(bytes)};
    }
    return {GameDataStatus::NotReady, {}};
}
/// Any operation by its name, with whole numbers and UTF-8 texts. Its answer is little-endian.
inline GameDataResult run(const std::string& name, const std::vector<int64_t>& numbers = {}, const std::vector<std::string>& texts = {}) {
    return run(detail::host(), name, numbers, texts);
}

/// Reads an answer. Every value after its end is zero, and `complete` is then false.
class Reader {
public:
    explicit Reader(std::vector<uint8_t> bytes) : bytes_(std::move(bytes)) {}
    int32_t i32() { return static_cast<int32_t>(value<uint32_t>()); }
    int64_t i64() { return static_cast<int64_t>(value<uint64_t>()); }
    std::string text() {
        const int32_t size = i32();
        if (size < 0 || static_cast<size_t>(size) > bytes_.size() - at_) { whole_ = false; return {}; }
        std::string out(reinterpret_cast<const char*>(bytes_.data()) + at_, static_cast<size_t>(size));
        at_ = (std::min)(at_ + ((static_cast<size_t>(size) + 3) & ~size_t{3}), bytes_.size());
        return out;
    }
    /// A number of rows, or -1 for one which the rest of the answer cannot hold.
    int32_t count(size_t leastBytes) {
        const int32_t rows = i32();
        if (rows > 0 && static_cast<size_t>(rows) > (bytes_.size() - at_) / leastBytes) whole_ = false;
        return whole_ ? rows : -1;
    }
    bool complete() const { return whole_ && at_ == bytes_.size(); }
private:
    template <class T> T value() {
        if (bytes_.size() - at_ < sizeof(T)) { whole_ = false; at_ = bytes_.size(); return 0; }
        T out = 0;
        for (size_t i = 0; i < sizeof(T); ++i) out |= static_cast<T>(static_cast<T>(bytes_[at_ + i]) << (8 * i));
        at_ += sizeof(T);
        return out;
    }
    std::vector<uint8_t> bytes_;
    size_t at_ = 0;
    bool whole_ = true;
};

struct Friend {
    std::string name, previousName;
    int32_t world = 0;  ///< 0 offline
    int32_t rank = 0;
};
struct FriendList {
    bool loaded = false;
    std::vector<Friend> friends, ignored;
};
struct ChatUser {
    std::string name;
    int32_t world = 0, rank = 0;
};
struct FriendsChat {
    std::string name, owner;  ///< No name is no channel.
    int32_t rank = 0, kickRank = 0;
    std::vector<ChatUser> users;
};
enum class Clan : int32_t { Visited = -1, Own = 0, IronmanGroup = 1 };
struct ClanChannel {
    bool present = false;
    std::string name;
    int32_t talkRank = 0, kickRank = 0;
    std::vector<ChatUser> users;
};
struct ClanMember {
    std::string name;
    int32_t rank = 0, joinedDay = 0;  ///< A day in the game's own count.
};
struct ClanSettings {
    bool present = false;
    std::string name;
    std::vector<ClanMember> members;
};
struct GroundPile {
    int32_t plane = 0, sceneX = 0, sceneY = 0;
    int64_t identity = 0;  ///< Of this pile while it lies there.
    int32_t id = -1, quantity = 0;
    int32_t visibleTick = 0, despawnTick = 0;  ///< Server ticks, as `GroundPiles::tick` counts them.
    int32_t ownership = 0;  ///< 0 nobody's, 1 the account's, 2 another player's, 3 the group's
    bool neverVisible = false;
};
struct GroundPiles {
    int32_t tick = 0;
    std::vector<GroundPile> piles;
};
struct HeldInput {
    std::array<uint32_t, 8> keys{};
    int32_t mouseButton = 0;  ///< 0, 1 left, 2 right, 4 middle
    int32_t mouseIdleMillis = 0, keyIdleMillis = 0;
    int64_t mousePressedAt = 0;  ///< Milliseconds since 1970.
    bool held(int32_t virtualKey) const {
        return virtualKey >= 0 && virtualKey < 256 && keys[static_cast<size_t>(virtualKey / 32)] >> (virtualKey % 32) & 1;
    }
};

/// The tile of the top-level scene which the game's own "Walk here" goes to. SDK 193, as is all that follows.
struct HoveredTile {
    bool present = false;
    int32_t plane = 0, sceneX = 0, sceneY = 0;
};
/// How the game draws its minimap.
struct MinimapView {
    std::optional<int32_t> heading;       ///< It is turned by this, in 2048 to a turn.
    std::optional<int32_t> pixelsToATile;
};
/// Where a world entity, such as a boat, stands in the top-level world.
struct WorldEntityPlace {
    bool present = false;
    int32_t x = 0, y = 0;        ///< In 1/128 of a tile of the top-level scene.
    int32_t orientation = 0;     ///< In 2048 to a turn.
    int32_t ownX = 0, ownY = 0;  ///< The point of its own world which is there.
};
struct WorldMapArea {
    int32_t area = -1;  ///< -1 before the map has shown one.
    int32_t tileX = -1, tileY = -1;
};
struct ChatLine {
    bool present = false;
    int32_t channel = 0;
    std::string name, message, sender;
};
enum class ChatLinePart : int32_t { Name = 0, Message = 1, Sender = 2 };
/// What a widget's definition and scripts set of it. Nothing where this game's is not known.
struct WidgetFields {
    std::optional<int32_t> xMode, yMode, widthMode, heightMode, textAlignX, textAlignY, lineHeight;
    std::optional<bool> hasListener;
    std::optional<int32_t> scrollWidth, opacity;
};
/// Those of a widget which scripts made, which its interface has in it, and those of the interface opened in it.
enum class WidgetChildren : int32_t { Dynamic = 0, Static = 1, Nested = 2 };
struct WidgetRef {
    int32_t packedId = 0, childIndex = -1;
    uint64_t incarnation = 0;
};
/// A slot of a flat widget's script-made children: the child there, with the fields a read of it has. SDK 195.
struct WidgetSlot {
    /// 0 where the slot is empty.
    uint64_t incarnation = 0;
    std::optional<int32_t> itemId, type, itemQuantity, spriteId, modelType, modelId, relativeX, relativeY, width, height,
        originalX, originalY, originalWidth, originalHeight, scrollX, scrollY, scrollHeight;
};
using ListenerValue = std::variant<int32_t, std::string, int64_t>;
/// The interpreter's stacks. A place of the second which holds no text is empty.
struct ScriptStacks {
    std::vector<int32_t> integers;
    std::vector<std::optional<std::string>> texts;
};
/// The running script's component and its second one. A packed ID of -1 is none.
struct ScriptComponents {
    int32_t packedId = -1, childIndex = -1, secondPackedId = -1, secondChildIndex = -1;
};

/// Where the open menu is drawn, in the pixels which widgets have. SDK 194, as is all that follows.
struct MenuBounds {
    bool open = false;
    int32_t x = 0, y = 0, width = 0, height = 0;
};
enum class LootSource : int32_t {
    Npc = 0,
    Player = 1,
    Server = 2,  ///< The server says it itself, as it does of some bosses. It has no place.
};
struct LootItem {
    int32_t id = -1, quantity = 0;
};
/// What a monster or a player left for the account.
struct Loot {
    bool present = false;
    LootSource source = LootSource::Npc;
    int32_t npcId = -1, index = -1, combatLevel = 0;
    int32_t worldX = -1, worldY = -1, plane = -1;  ///< Its south-west tile.
    std::string name;
    std::vector<LootItem> items;
};

/// Nothing where the list cannot be read now.
inline std::optional<FriendList> friends(const TitanPluginSdk::HostApi* host) {
    auto answer = run(host, "titan.game.read.friends.v1");
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    FriendList out;
    const int32_t count = in.count(16);
    out.loaded = count >= 0;
    for (int32_t i = 0; i < count; ++i) {
        Friend each{in.text(), in.text()};
        each.world = in.i32();
        each.rank = in.i32();
        out.friends.push_back(std::move(each));
    }
    const int32_t ignored = in.count(8);
    for (int32_t i = 0; i < ignored; ++i) out.ignored.push_back({in.text(), in.text()});
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<FriendsChat> friendsChat(const TitanPluginSdk::HostApi* host) {
    auto answer = run(host, "titan.game.read.friendsChat.v1");
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    FriendsChat out{in.text(), in.text()};
    out.rank = in.i32();
    out.kickRank = in.i32();
    const int32_t count = in.count(12);
    for (int32_t i = 0; i < count; ++i) {
        ChatUser user{in.text()};
        user.world = in.i32();
        user.rank = in.i32();
        out.users.push_back(std::move(user));
    }
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<ClanChannel> clanChannel(const TitanPluginSdk::HostApi* host, Clan which) {
    auto answer = run(host, "titan.game.read.clanChannel.v1", {static_cast<int64_t>(which)});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    ClanChannel out;
    out.present = in.i32() == 1;
    if (out.present) {
        out.name = in.text();
        out.talkRank = in.i32();
        out.kickRank = in.i32();
        const int32_t count = in.count(12);
        for (int32_t i = 0; i < count; ++i) {
            ChatUser user{in.text()};
            user.rank = in.i32();
            user.world = in.i32();
            out.users.push_back(std::move(user));
        }
    }
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<ClanSettings> clanSettings(const TitanPluginSdk::HostApi* host, Clan which) {
    auto answer = run(host, "titan.game.read.clanSettings.v1", {static_cast<int64_t>(which)});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    ClanSettings out;
    out.present = in.i32() == 1;
    if (out.present) {
        out.name = in.text();
        const int32_t count = in.count(12);
        for (int32_t i = 0; i < count; ++i) {
            ClanMember member{in.text()};
            member.rank = in.i32();
            member.joinedDay = in.i32();
            out.members.push_back(std::move(member));
        }
    }
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<GroundPiles> groundItems(const TitanPluginSdk::HostApi* host, int32_t worldViewId) {
    auto answer = run(host, "titan.game.read.groundItems.v1", {worldViewId});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    GroundPiles out;
    const int32_t count = in.count(44);
    out.tick = in.i32();
    for (int32_t i = 0; i < count; ++i) {
        GroundPile pile;
        pile.plane = in.i32();
        pile.sceneX = in.i32();
        pile.sceneY = in.i32();
        pile.identity = in.i64();
        pile.id = in.i32();
        pile.quantity = in.i32();
        pile.visibleTick = in.i32();
        pile.despawnTick = in.i32();
        pile.ownership = in.i32();
        pile.neverVisible = in.i32() == 1;
        out.piles.push_back(pile);
    }
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<HeldInput> input(const TitanPluginSdk::HostApi* host) {
    auto answer = run(host, "titan.game.read.input.v1");
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    HeldInput out;
    for (auto& word : out.keys) word = static_cast<uint32_t>(in.i32());
    out.mouseButton = in.i32();
    out.mouseIdleMillis = in.i32();
    out.keyIdleMillis = in.i32();
    out.mousePressedAt = in.i64();
    return in.complete() ? std::optional(out) : std::nullopt;
}
inline bool playSound(const TitanPluginSdk::HostApi* host, int32_t id, int32_t loops, int32_t delay) {
    return run(host, "titan.game.change.sound.v1", {id, loops, delay}).ready();
}

inline std::optional<HoveredTile> hoveredTile(const TitanPluginSdk::HostApi* host) {
    auto answer = run(host, "titan.game.read.hoveredTile.v1");
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    HoveredTile out;
    out.present = in.i32() == 1;
    if (out.present) {
        out.plane = in.i32();
        out.sceneX = in.i32();
        out.sceneY = in.i32();
    }
    return in.complete() ? std::optional(out) : std::nullopt;
}
inline std::optional<MinimapView> minimap(const TitanPluginSdk::HostApi* host) {
    auto answer = run(host, "titan.game.read.minimapView.v1");
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    const int32_t known = in.i32(), heading = in.i32(), zoom = in.i32();
    MinimapView out;
    if (known & 1) out.heading = heading;
    if (known & 2) out.pixelsToATile = zoom;
    return in.complete() ? std::optional(out) : std::nullopt;
}
inline std::optional<WorldEntityPlace> worldEntity(const TitanPluginSdk::HostApi* host, int32_t worldViewId) {
    auto answer = run(host, "titan.game.read.worldEntity.v1", {worldViewId});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    WorldEntityPlace out;
    out.present = in.i32() == 1;
    if (out.present) for (int32_t* value : {&out.x, &out.y, &out.orientation, &out.ownX, &out.ownY}) *value = in.i32();
    return in.complete() ? std::optional(out) : std::nullopt;
}
inline std::optional<WorldMapArea> worldMapArea(const TitanPluginSdk::HostApi* host) {
    auto answer = run(host, "titan.game.read.worldMap.v1");
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    WorldMapArea out;
    for (int32_t* value : {&out.area, &out.tileX, &out.tileY}) *value = in.i32();
    return in.complete() ? std::optional(out) : std::nullopt;
}
inline std::optional<ChatLine> chatLine(const TitanPluginSdk::HostApi* host, int32_t number) {
    auto answer = run(host, "titan.game.read.chatLine.v1", {number});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    ChatLine out;
    out.present = in.i32() == 1;
    if (out.present) {
        out.channel = in.i32();
        out.name = in.text();
        out.message = in.text();
        out.sender = in.text();
    }
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<std::vector<int32_t>> chatLines(const TitanPluginSdk::HostApi* host, int32_t channel) {
    auto answer = run(host, "titan.game.read.chatLines.v1", {channel});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    std::vector<int32_t> out;
    for (int32_t i = 0, count = in.count(4); i < count; ++i) out.push_back(in.i32());
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<WidgetFields> widgetFields(const TitanPluginSdk::HostApi* host, int32_t packedId, int32_t childIndex) {
    auto answer = run(host, "titan.game.read.widgetFields.v1", {packedId, childIndex, 0});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    const int32_t known = in.i32();
    int32_t bit = 0;
    const auto next = [&]() -> std::optional<int32_t> {
        const int32_t value = in.i32();
        return known >> bit++ & 1 ? std::optional(value) : std::nullopt;
    };
    WidgetFields out;
    for (auto* field : {&out.xMode, &out.yMode, &out.widthMode, &out.heightMode, &out.textAlignX, &out.textAlignY, &out.lineHeight}) *field = next();
    if (const auto listener = next()) out.hasListener = *listener != 0;
    out.scrollWidth = next();
    out.opacity = next();
    return in.complete() ? std::optional(out) : std::nullopt;
}
inline std::optional<std::vector<WidgetRef>> widgetChildren(const TitanPluginSdk::HostApi* host, int32_t packedId, WidgetChildren which, int32_t childIndex) {
    auto answer = run(host, "titan.game.read.widgetFamily.v1", {packedId, childIndex, 0, static_cast<int64_t>(which)});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    std::vector<WidgetRef> out;
    for (int32_t i = 0, count = in.count(16); i < count; ++i) {
        WidgetRef each;
        each.packedId = in.i32();
        each.childIndex = in.i32();
        each.incarnation = static_cast<uint64_t>(in.i64());
        out.push_back(each);
    }
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
/// Every slot of a flat widget's script-made children, the empty ones too, with each child's fields: a container such
/// as the bank's items in one read. Nothing when one of them cannot be read now. SDK 195.
inline std::optional<std::vector<WidgetSlot>> widgetSlots(const TitanPluginSdk::HostApi* host, int32_t packedId) {
    auto answer = run(host, "titan.game.read.widgetSlots.v1", {packedId, 0});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    in.i64();
    std::vector<WidgetSlot> out;
    for (int32_t i = 0, count = in.count(12 + 4 * 17); i < count; ++i) {
        WidgetSlot each;
        each.incarnation = static_cast<uint64_t>(in.i64());
        const int32_t known = in.i32();
        int32_t bit = 0;
        for (auto* field : {&each.itemId, &each.type, &each.itemQuantity, &each.spriteId, &each.modelType, &each.modelId,
                 &each.relativeX, &each.relativeY, &each.width, &each.height, &each.originalX, &each.originalY,
                 &each.originalWidth, &each.originalHeight, &each.scrollX, &each.scrollY, &each.scrollHeight}) {
            const int32_t value = in.i32();
            if (known >> bit++ & 1) *field = value;
        }
        out.push_back(each);
    }
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<std::vector<ListenerValue>> widgetListener(const TitanPluginSdk::HostApi* host, int32_t packedId, int32_t opcode, int32_t childIndex) {
    auto answer = run(host, "titan.game.read.widgetListener.v1", {packedId, childIndex, 0, opcode});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    std::vector<ListenerValue> out;
    for (int32_t i = 0, count = in.count(8); i < count; ++i) switch (in.i32()) {
        case 0: out.emplace_back(in.i32()); break;
        case 1: out.emplace_back(in.text()); break;
        case 2: out.emplace_back(in.i64()); break;
        default: return std::nullopt;
    }
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<ScriptStacks> scriptStacks(const TitanPluginSdk::HostApi* host) {
    auto answer = run(host, "titan.game.read.scriptStacks.v1");
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    ScriptStacks out;
    for (int32_t i = 0, count = in.count(4); i < count; ++i) out.integers.push_back(in.i32());
    for (int32_t i = 0, count = in.count(4); i < count; ++i) out.texts.push_back(in.i32() == 1 ? std::optional(in.text()) : std::nullopt);
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}
inline std::optional<ScriptComponents> scriptComponents(const TitanPluginSdk::HostApi* host) {
    auto answer = run(host, "titan.game.read.scriptComponents.v1");
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    ScriptComponents out;
    for (int32_t* value : {&out.packedId, &out.childIndex, &out.secondPackedId, &out.secondChildIndex}) *value = in.i32();
    return in.complete() ? std::optional(out) : std::nullopt;
}
inline bool setChatLine(const TitanPluginSdk::HostApi* host, int32_t number, ChatLinePart part, const std::string& text) {
    return run(host, "titan.game.change.chatLine.v1", {number, static_cast<int64_t>(part)}, {text}).ready();
}
inline bool setWidgetListener(const TitanPluginSdk::HostApi* host, int32_t packedId, int32_t opcode, int32_t script,
                              const std::vector<std::variant<int32_t, std::string>>& arguments, int32_t childIndex) {
    std::vector<int64_t> numbers{packedId, childIndex, 0, opcode, script};
    std::vector<std::string> texts;
    for (const auto& argument : arguments) {
        const auto* text = std::get_if<std::string>(&argument);
        numbers.push_back(text ? 1 : 0);
        numbers.push_back(text ? static_cast<int64_t>(texts.size()) : std::get<int32_t>(argument));
        if (text) texts.push_back(*text);
    }
    return run(host, "titan.game.change.widgetListener.v1", numbers, texts).ready();
}
inline bool setScriptText(const TitanPluginSdk::HostApi* host, int32_t place, const std::string& text) {
    return run(host, "titan.game.change.scriptStacks.v1", {-1, -1, 0, place}, {text}).ready();
}
inline bool setOverheadText(const TitanPluginSdk::HostApi* host, uint64_t entityPtr, const std::string& text) {
    return run(host, "titan.game.change.overheadText.v1", {-1, static_cast<int64_t>(entityPtr)}, {text}).ready();
}
inline bool openWorldSwitcher(const TitanPluginSdk::HostApi* host) { return run(host, "titan.game.change.world.v1", {0, 0}).ready(); }
inline bool reloadScene(const TitanPluginSdk::HostApi* host) { return run(host, "titan.game.change.gameState.v1", {25}).ready(); }
inline bool dropConnection(const TitanPluginSdk::HostApi* host) { return run(host, "titan.game.change.gameState.v1", {40}).ready(); }

/// Friends and ignored players, as the game's own lists have them.
inline std::optional<FriendList> friends() { return friends(detail::host()); }
inline std::optional<FriendsChat> friendsChat() { return friendsChat(detail::host()); }
/// Not inside onScriptMoment, where the game's own scripts are using these lists.
inline std::optional<ClanChannel> clanChannel(Clan which = Clan::Own) { return clanChannel(detail::host(), which); }
inline std::optional<ClanSettings> clanSettings(Clan which = Clan::Own) { return clanSettings(detail::host(), which); }
/// Every pile in a loaded world. They change with the server's ticks, so read them once in each.
inline std::optional<GroundPiles> groundItems(int32_t worldViewId = 0) { return groundItems(detail::host(), worldViewId); }
/// Which keys and mouse button the game's window was last told are down.
inline std::optional<HeldInput> input() { return input(detail::host()); }
/// As the game's scripts play a sound effect, at the volume the account has set.
inline bool playSound(int32_t id, int32_t loops = 1, int32_t delay = 0) { return playSound(detail::host(), id, loops, delay); }

// --- SDK 193

inline std::optional<MenuBounds> menuBounds(const TitanPluginSdk::HostApi* host, uint64_t generation = 0, uint64_t heading = 0) {
    auto answer = heading ? run(host, "titan.game.read.menuPlace.v1", {static_cast<int64_t>(generation), static_cast<int64_t>(heading)})
        : run(host, "titan.game.read.menuPlace.v1");
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    MenuBounds out;
    out.open = in.i32() == 1;
    if (out.open) {
        out.x = in.i32();
        out.y = in.i32();
        out.width = in.i32();
        out.height = in.i32();
    }
    return in.complete() ? std::optional(out) : std::nullopt;
}
inline std::optional<Loot> loot(const TitanPluginSdk::HostApi* host, int32_t number) {
    auto answer = run(host, "titan.game.read.loot.v1", {number});
    if (!answer.ready()) return std::nullopt;
    Reader in(std::move(answer.bytes));
    Loot out;
    out.present = in.i32() == 1;
    if (!out.present) return in.complete() ? std::optional(out) : std::nullopt;
    const int32_t source = in.i32();
    if (source < 0 || source > 2) return std::nullopt;
    out.source = static_cast<LootSource>(source);
    out.npcId = in.i32();
    out.index = in.i32();
    out.combatLevel = in.i32();
    out.worldX = in.i32();
    out.worldY = in.i32();
    out.plane = in.i32();
    out.name = in.text();
    const int32_t count = in.count(8);
    for (int32_t i = 0; i < count; ++i) {
        LootItem item{in.i32()};
        item.quantity = in.i32();
        out.items.push_back(item);
    }
    return in.complete() ? std::optional(std::move(out)) : std::nullopt;
}

/// It stays while the game's menu is open, and is none over an interface or with an item selected.
inline std::optional<HoveredTile> hoveredTile() { return hoveredTile(detail::host()); }
inline std::optional<MinimapView> minimap() { return minimap(detail::host()); }
/// Of the open menu itself. `Menu::bounds` also has the game's own submenus.
inline std::optional<MenuBounds> menuBounds() { return menuBounds(detail::host()); }
/// By the number which onLootReceived tells of. One of the last 64.
inline std::optional<Loot> loot(int32_t number) { return loot(detail::host(), number); }
/// By the ID of its own world view.
inline std::optional<WorldEntityPlace> worldEntity(int32_t worldViewId) { return worldEntity(detail::host(), worldViewId); }
/// The area which the world map shows, and the tile in the middle of it.
inline std::optional<WorldMapArea> worldMapArea() { return worldMapArea(detail::host()); }
/// A line of the game's chat, by its number.
inline std::optional<ChatLine> chatLine(int32_t number) { return chatLine(detail::host(), number); }
/// The numbers of a channel's lines, the newest first.
inline std::optional<std::vector<int32_t>> chatLines(int32_t channel) { return chatLines(detail::host(), channel); }
/// The chat shows it when it is next built.
inline bool setChatLine(int32_t number, ChatLinePart part, const std::string& text) { return setChatLine(detail::host(), number, part, text); }
/// Of a widget, or of its direct child at an index.
inline std::optional<WidgetFields> widgetFields(int32_t packedId, int32_t childIndex = -1) { return widgetFields(detail::host(), packedId, childIndex); }
inline std::optional<std::vector<WidgetRef>> widgetChildren(int32_t packedId, WidgetChildren which, int32_t childIndex = -1) {
    return widgetChildren(detail::host(), packedId, which, childIndex);
}
/// Every slot of a flat widget's script-made children with each child's fields, in one read. SDK 195.
inline std::optional<std::vector<WidgetSlot>> widgetSlots(int32_t packedId) { return widgetSlots(detail::host(), packedId); }
/// A listener by the opcode which sets it in the game's scripts, such as 1400 on click or 1409 on an option, and the load
/// listener by 0: its script and then its arguments. Empty for none.
inline std::optional<std::vector<ListenerValue>> widgetListener(int32_t packedId, int32_t opcode, int32_t childIndex = -1) {
    return widgetListener(detail::host(), packedId, opcode, childIndex);
}
/// A script of -1 takes it away.
inline bool setWidgetListener(int32_t packedId, int32_t opcode, int32_t script, const std::vector<std::variant<int32_t, std::string>>& arguments = {},
                              int32_t childIndex = -1) {
    return setWidgetListener(detail::host(), packedId, opcode, script, arguments, childIndex);
}
/// Inside onScriptMoment those of the script, and elsewhere what the last one left.
inline std::optional<ScriptStacks> scriptStacks() { return scriptStacks(detail::host()); }
/// Inside onScriptMoment.
inline std::optional<ScriptComponents> scriptComponents() { return scriptComponents(detail::host()); }
/// Inside onScriptMoment: replaces the text at a place of the stack. ScriptMomentEvent::setInteger does an integer's.
inline bool setScriptText(int32_t place, const std::string& text) { return setScriptText(detail::host(), place, text); }
/// What a player or an NPC says above its head, for as long as the game shows it.
inline bool setOverheadText(uint64_t entityPtr, const std::string& text) { return setOverheadText(detail::host(), entityPtr, text); }
template <class ActorT> requires requires(const ActorT& actor) { actor.entityPtr(); }
bool setOverheadText(const ActorT& actor, const std::string& text) { return setOverheadText(static_cast<uint64_t>(actor.entityPtr()), text); }
/// One click nearer to the game's open world switcher, once in a tick until it is open. state::world::hopIngame does all
/// of a hop by itself.
inline bool openWorldSwitcher() { return openWorldSwitcher(detail::host()); }
/// Loads the scene of a player in the game anew, as the game's next cycle begins. False when there is none.
inline bool reloadScene() { return reloadScene(detail::host()); }
/// Drops the connection of a player in the game, which the game then makes again. False when there is none.
inline bool dropConnection() { return dropConnection(detail::host()); }

} // namespace gameData
} // namespace titan
