/// @file titan/events.h
/// @brief Typed event wrappers for Plugin callbacks.
///
/// Plugin lifecycle virtuals take these wrappers instead of raw ABI structs so
/// the SDK surface can evolve (e.g. adding a .consume() method) without ABI
/// breaks. Each wrapper is a thin view over a raw ABI struct owned elsewhere
/// — do not store a wrapper past the callback that received it.

#pragma once

#include "actor.h"
#include "client.h"
#include "detail/abi.h"
#include "menu_action.h"

#include <algorithm>
#include <cstring>
#include <cstdint>
#include <optional>
#include <string>
#include <vector>

namespace titan {

/// Owned copy; both event and offer remain valid when retained by a plugin.
class GrandExchangeOfferChangedEvent {
public:
    explicit GrandExchangeOfferChangedEvent(const TitanPluginSdk::GrandExchangeOfferChangedEvent* event)
        : value_(event ? *event : TitanPluginSdk::GrandExchangeOfferChangedEvent{}) {}
    int32_t slot() const { return value_.slot; }
    GrandExchangeOffer offer() const { return GrandExchangeOffer(value_.offer); }
    int32_t getSlot() const { return slot(); }
    GrandExchangeOffer getOffer() const { return offer(); }
private:
    TitanPluginSdk::GrandExchangeOfferChangedEvent value_;
};

/// Menu-option-clicked event. Mutable so handlers can set `consume()` to
/// suppress the action before it reaches the game.
class MenuClickEvent {
public:
    explicit MenuClickEvent(TitanPluginSdk::MenuOptionClickedEvent* raw) : raw_(raw) {}

    uint32_t opcode() const     { return raw_ ? raw_->opcode   : 0; }
    int32_t  identifier() const { return raw_ ? raw_->identifier : 0; }
    int32_t  param0() const     { return raw_ ? raw_->param0   : 0; }
    int32_t  param1() const     { return raw_ ? raw_->param1   : 0; }
    uint32_t worldViewId() const { return raw_ ? raw_->worldViewId : 0; }
    int32_t  clickX() const     { return raw_ ? raw_->clickX   : 0; }
    int32_t  clickY() const     { return raw_ ? raw_->clickY   : 0; }
    std::string actionText() const { return raw_ ? raw_->actionText : ""; }
    std::string targetText() const { return raw_ ? raw_->targetText : ""; }

    /// Prevent the click from reaching the game. Subsequent plugins still see
    /// the event but the action will not execute.
    void consume() { if (raw_) raw_->consumed = 1; }
    bool consumed() const { return raw_ && raw_->consumed != 0; }

    /// Replace the action that reaches the game while preserving the original
    /// native click / DoAction frame. `consume()` still wins over replacement.
    void replaceWith(const TitanPluginSdk::SyntheticActionEntry& e) {
        if (!raw_) return;
        raw_->replaced = 1;
        raw_->replacementOpcode = e.opcode;
        raw_->replacementIdentifier = e.identifier;
        raw_->replacementParam0 = e.param0;
        raw_->replacementParam1 = e.param1;
        raw_->replacementWorldViewId = e.worldViewId;
        raw_->replacementClickX = e.clickX;
        raw_->replacementClickY = e.clickY;
        copyFixed(raw_->replacementActionText, TitanPluginSdk::kMaxLabelLen, e.actionText);
        copyFixed(raw_->replacementTargetText, TitanPluginSdk::kMaxLabelLen, e.targetText);
    }

    void replaceWith(const MenuAction::Entry& e) {
        TitanPluginSdk::SyntheticActionEntry raw{};
        raw.opcode = static_cast<uint32_t>(e.opcode);
        raw.identifier = e.identifier;
        raw.param0 = e.param0;
        raw.param1 = e.param1;
        raw.worldViewId = e.worldViewId < 0 ? 0u : static_cast<uint32_t>(e.worldViewId);
        raw.clickX = e.clickX;
        raw.clickY = e.clickY;
        raw.actionText = e.actionText;
        raw.targetText = e.targetText;
        replaceWith(raw);
    }

    void setOpcode(uint32_t opcode) { ensureReplacement(); if (raw_) raw_->replacementOpcode = opcode; }
    void setIdentifier(int32_t identifier) {
        ensureReplacement();
        if (raw_) raw_->replacementIdentifier = identifier;
    }
    void setParam0(int32_t param0) { ensureReplacement(); if (raw_) raw_->replacementParam0 = param0; }
    void setParam1(int32_t param1) { ensureReplacement(); if (raw_) raw_->replacementParam1 = param1; }
    void setWorldViewId(uint32_t worldViewId) { ensureReplacement(); if (raw_) raw_->replacementWorldViewId = worldViewId; }
    void setClick(int32_t x, int32_t y) {
        ensureReplacement();
        if (!raw_) return;
        raw_->replacementClickX = x;
        raw_->replacementClickY = y;
    }
    void setActionText(const char* text) {
        ensureReplacement();
        if (raw_) copyFixed(raw_->replacementActionText, TitanPluginSdk::kMaxLabelLen, text);
    }
    void setActionText(const std::string& text) { setActionText(text.c_str()); }
    void setTargetText(const char* text) {
        ensureReplacement();
        if (raw_) copyFixed(raw_->replacementTargetText, TitanPluginSdk::kMaxLabelLen, text);
    }
    void setTargetText(const std::string& text) { setTargetText(text.c_str()); }

    void clearReplacement() {
        if (!raw_) return;
        raw_->replaced = 0;
        raw_->replacementOpcode = 0;
        raw_->replacementIdentifier = 0;
        raw_->replacementParam0 = 0;
        raw_->replacementParam1 = 0;
        raw_->replacementWorldViewId = 0;
        raw_->replacementClickX = 0;
        raw_->replacementClickY = 0;
        raw_->replacementActionText[0] = '\0';
        raw_->replacementTargetText[0] = '\0';
    }
    bool replaced() const { return raw_ && raw_->replaced != 0; }

    TitanPluginSdk::MenuOptionClickedEvent* raw() { return raw_; }

private:
    static void copyFixed(char* dst, size_t dstSize, const char* src) {
        if (!dst || dstSize == 0) return;
        dst[0] = '\0';
        if (!src) return;
        const size_t len = std::strlen(src);
        const size_t n = (std::min)(len, dstSize - 1);
        std::memcpy(dst, src, n);
        dst[n] = '\0';
    }

    void ensureReplacement() {
        if (!raw_ || raw_->replaced) return;
        raw_->replaced = 1;
        raw_->replacementOpcode = raw_->opcode;
        raw_->replacementIdentifier = raw_->identifier;
        raw_->replacementParam0 = raw_->param0;
        raw_->replacementParam1 = raw_->param1;
        raw_->replacementWorldViewId = raw_->worldViewId;
        raw_->replacementClickX = raw_->clickX;
        raw_->replacementClickY = raw_->clickY;
        copyFixed(raw_->replacementActionText, TitanPluginSdk::kMaxLabelLen, raw_->actionText);
        copyFixed(raw_->replacementTargetText, TitanPluginSdk::kMaxLabelLen, raw_->targetText);
    }

    TitanPluginSdk::MenuOptionClickedEvent* raw_ = nullptr;
};

/// Chat-message event. Read-only view over the raw ABI struct. Delivered
/// for every native `AddChat` call (server chat, local system messages, and
/// plugin-injected lines via `HostApi::addChatMessage`).
/// Added in SDK 22.
class ChatMessageEvent {
public:
    explicit ChatMessageEvent(const TitanPluginSdk::ChatMessageEvent* raw) : raw_(raw) {}

    int32_t      type() const     { return raw_ ? raw_->type : 0; }
    std::string  name() const     { return raw_ ? raw_->name : ""; }
    std::string  message() const  { return raw_ ? raw_->message : ""; }
    std::string  sender() const   { return raw_ ? raw_->sender : ""; }
    int32_t      gameTick() const { return raw_ ? raw_->gameTick : 0; }

    const TitanPluginSdk::ChatMessageEvent* raw() const { return raw_; }

private:
    const TitanPluginSdk::ChatMessageEvent* raw_ = nullptr;
};

/// Sound-played event. Mutable so handlers can `consume()` to mark the event
/// handled and stop later sound handlers in this dispatch. Per-sound playback
/// suppression is not supported by the current hook; use
/// `titan::state::audio()` to mute playback globally instead. Covers queued
/// synth sound effects and MIDI jingles. Added in SDK 69.
class SoundPlayedEvent {
public:
    explicit SoundPlayedEvent(TitanPluginSdk::SoundPlayedEvent* raw) : raw_(raw) {}

    /// TitanPluginSdk::SoundKind: 0 = synth sound effect, 1 = MIDI jingle.
    int32_t kind() const     { return raw_ ? raw_->kind : 0; }
    bool isJingle() const    { return kind() == TitanPluginSdk::SOUND_KIND_JINGLE; }
    bool isSynth() const     { return kind() == TitanPluginSdk::SOUND_KIND_SYNTH; }

    int32_t soundId() const  { return raw_ ? raw_->soundId : 0; }
    /// Synth loop count; -1 for jingles.
    int32_t loops() const    { return raw_ ? raw_->loops : 0; }
    /// Jingle duration in ms; -1 for synths.
    int32_t durationMs() const { return raw_ ? raw_->durationMs : 0; }
    /// Synth packed position/range; -1 for jingles.
    int32_t packedPos() const { return raw_ ? raw_->packedPos : 0; }
    int32_t gameTick() const { return raw_ ? raw_->gameTick : 0; }

    /// Mark this event consumed for handler ordering. This does not suppress
    /// native playback; use `titan::state::audio()` for global muting.
    void consume() { if (raw_) raw_->consumed = 1; }
    bool consumed() const { return raw_ && raw_->consumed != 0; }

    TitanPluginSdk::SoundPlayedEvent* raw() { return raw_; }

private:
    TitanPluginSdk::SoundPlayedEvent* raw_ = nullptr;
};

/// Mouse button press/release event. Mutable so handlers can `consume()` the
/// click before it reaches the game. Delivered for real (non-synthetic) mouse
/// input via `Plugin::onMousePressed` / `onMouseReleased`.
///
/// `x()`/`y()` are game-window client-area pixels (the same space as
/// `titan::state::worldMap()` viewport bounds and `getMousePos`). `button()` is
/// a `TitanPluginSdk::MouseButton` ordinal; `modifiers()` is a
/// `TitanPluginSdk::KeyboardMods` bitmask sampled at click time.
///
/// THREADING: these fire on the input (message-pump) thread, which may not be
/// the game loop. Do NOT read game state or call `titan::*` queries from the
/// handler -- copy the fields out and act from a game-thread callback such as
/// `onClientTick`. Added in SDK 115.
class MouseButtonEvent {
public:
    explicit MouseButtonEvent(TitanPluginSdk::MouseButtonEvent* raw) : raw_(raw) {}

    int32_t x() const { return raw_ ? raw_->x : 0; }
    int32_t y() const { return raw_ ? raw_->y : 0; }

    /// TitanPluginSdk::MouseButton: 0 = LEFT, 1 = RIGHT, 2 = MIDDLE.
    int32_t button() const { return raw_ ? raw_->button : 0; }
    bool isLeft() const   { return button() == TitanPluginSdk::MouseButton::LEFT; }
    bool isRight() const  { return button() == TitanPluginSdk::MouseButton::RIGHT; }
    bool isMiddle() const { return button() == TitanPluginSdk::MouseButton::MIDDLE; }

    /// KeyboardMods bitmask (SHIFT=1, CTRL=2, ALT=4) sampled at click time.
    int32_t modifiers() const { return raw_ ? raw_->modifiers : 0; }
    bool shift() const { return (modifiers() & TitanPluginSdk::KeyboardMods::SHIFT) != 0; }
    bool ctrl() const  { return (modifiers() & TitanPluginSdk::KeyboardMods::CTRL) != 0; }
    bool alt() const   { return (modifiers() & TitanPluginSdk::KeyboardMods::ALT) != 0; }

    int32_t gameTick() const { return raw_ ? raw_->gameTick : 0; }

    /// On a PRESS: prevent the click from reaching the game's WndProc input
    /// pipeline (the host then also suppresses the matching release, so the
    /// game's button state cannot desync). On a RELEASE this marks the event
    /// handled for handler ordering only -- it does not suppress the native
    /// release. The game's low-level input pipeline still observes real
    /// hardware input regardless.
    void consume() { if (raw_) raw_->consumed = 1; }
    bool consumed() const { return raw_ && raw_->consumed != 0; }

    TitanPluginSdk::MouseButtonEvent* raw() { return raw_; }

private:
    TitanPluginSdk::MouseButtonEvent* raw_ = nullptr;
};

/// Hitsplat-applied event. Read-only view over the raw ABI struct. Delivered
/// for visible applied hitsplats. The actor is resolved by the host before
/// dispatch. Added in SDK 74; native signature corrected in SDK 76.
class HitsplatAppliedEvent {
public:
    explicit HitsplatAppliedEvent(const TitanPluginSdk::HitsplatAppliedEvent* raw)
        : raw_(raw) {}

    uint8_t actorType() const {
        return raw_ ? raw_->actorType : TitanPluginSdk::EntityType::NONE;
    }
    bool isPlayer() const { return actorType() == TitanPluginSdk::EntityType::PLAYER; }
    bool isNpc() const { return actorType() == TitanPluginSdk::EntityType::NPC; }
    bool hasActor() const { return isPlayer() || isNpc(); }

    Actor actor() const {
        if (!raw_) return Actor{};
        if (isPlayer()) return Actor{Player{raw_->player}};
        if (isNpc()) return Actor{Npc{raw_->npc}};
        return Actor{};
    }

    std::optional<Player> player() const {
        if (!raw_ || !isPlayer()) return std::nullopt;
        return Player{raw_->player};
    }

    std::optional<Npc> npc() const {
        if (!raw_ || !isNpc()) return std::nullopt;
        return Npc{raw_->npc};
    }

    std::string kind() const {
        if (isPlayer()) return "player";
        if (isNpc()) return "npc";
        return "none";
    }

    int32_t indexOrId() const {
        if (!raw_) return -1;
        if (isPlayer()) return raw_->player.hashIndex;
        if (isNpc()) return raw_->npc.npcId;
        return -1;
    }

    std::string actorName() const {
        if (!raw_) return "";
        if (isPlayer()) return raw_->player.name;
        if (isNpc()) return raw_->npc.name;
        return "";
    }

    int32_t type() const { return raw_ ? raw_->type : 0; }
    int32_t value() const { return raw_ ? raw_->value : 0; }
    int32_t damage() const { return value(); }
    int32_t limit() const { return raw_ ? raw_->limit : 0; }
    int32_t aux() const { return limit(); }
    int32_t delay() const { return raw_ ? raw_->delay : 0; }
    int32_t cycle() const { return raw_ ? raw_->cycle : 0; }
    int32_t gameTick() const { return raw_ ? raw_->gameTick : 0; }

    const TitanPluginSdk::HitsplatAppliedEvent* raw() const { return raw_; }

private:
    const TitanPluginSdk::HitsplatAppliedEvent* raw_ = nullptr;
};

/// Actor-attached spot animation event. Read-only view over the raw ABI
/// struct. Delivered only for apply/write events; clear/removal ids are
/// filtered by the client. Added in SDK 76.
class ActorSpotAnimEvent {
public:
    explicit ActorSpotAnimEvent(const TitanPluginSdk::ActorSpotAnimEvent* raw)
        : raw_(raw) {}

    uint8_t actorType() const {
        return raw_ ? raw_->actorType : TitanPluginSdk::EntityType::NONE;
    }
    bool isPlayer() const { return actorType() == TitanPluginSdk::EntityType::PLAYER; }
    bool isNpc() const { return actorType() == TitanPluginSdk::EntityType::NPC; }
    bool hasActor() const { return isPlayer() || isNpc(); }

    Actor actor() const {
        if (!raw_) return Actor{};
        if (isPlayer()) return Actor{Player{raw_->player}};
        if (isNpc()) return Actor{Npc{raw_->npc}};
        return Actor{};
    }

    std::optional<Player> player() const {
        if (!raw_ || !isPlayer()) return std::nullopt;
        return Player{raw_->player};
    }

    std::optional<Npc> npc() const {
        if (!raw_ || !isNpc()) return std::nullopt;
        return Npc{raw_->npc};
    }

    std::string kind() const {
        if (isPlayer()) return "player";
        if (isNpc()) return "npc";
        return "none";
    }

    int32_t indexOrId() const {
        if (!raw_) return -1;
        if (isPlayer()) return raw_->player.hashIndex;
        if (isNpc()) return raw_->npc.npcId;
        return -1;
    }

    std::string actorName() const {
        if (!raw_) return "";
        if (isPlayer()) return raw_->player.name;
        if (isNpc()) return raw_->npc.name;
        return "";
    }

    int32_t slot() const { return raw_ ? raw_->slot : 0; }
    int32_t id() const { return raw_ ? raw_->id : -1; }
    int32_t height() const { return raw_ ? raw_->height : 0; }
    int32_t delay() const { return raw_ ? raw_->delay : 0; }
    int32_t cycle() const { return raw_ ? raw_->cycle : 0; }
    int32_t gameTick() const { return raw_ ? raw_->gameTick : 0; }

    const TitanPluginSdk::ActorSpotAnimEvent* raw() const { return raw_; }

private:
    const TitanPluginSdk::ActorSpotAnimEvent* raw_ = nullptr;
};

/// Actor animation changed event. Read-only view over the raw ABI struct.
/// Delivered only after the native setter accepts a different Actor::Animation
/// value. Same-animation resets are filtered by the client. Added in SDK 78.
class AnimationChangedEvent {
public:
    explicit AnimationChangedEvent(const TitanPluginSdk::AnimationChangedEvent* raw)
        : raw_(raw) {}

    uint8_t actorType() const {
        return raw_ ? raw_->actorType : TitanPluginSdk::EntityType::NONE;
    }
    bool isPlayer() const { return actorType() == TitanPluginSdk::EntityType::PLAYER; }
    bool isNpc() const { return actorType() == TitanPluginSdk::EntityType::NPC; }
    bool hasActor() const { return isPlayer() || isNpc(); }

    Actor actor() const {
        if (!raw_) return Actor{};
        if (isPlayer()) return Actor{Player{raw_->player}};
        if (isNpc()) return Actor{Npc{raw_->npc}};
        return Actor{};
    }

    std::optional<Player> player() const {
        if (!raw_ || !isPlayer()) return std::nullopt;
        return Player{raw_->player};
    }

    std::optional<Npc> npc() const {
        if (!raw_ || !isNpc()) return std::nullopt;
        return Npc{raw_->npc};
    }

    std::string kind() const {
        if (isPlayer()) return "player";
        if (isNpc()) return "npc";
        return "none";
    }

    int32_t indexOrId() const {
        if (!raw_) return -1;
        if (isPlayer()) return raw_->player.hashIndex;
        if (isNpc()) return raw_->npc.npcId;
        return -1;
    }

    std::string actorName() const {
        if (!raw_) return "";
        if (isPlayer()) return raw_->player.name;
        if (isNpc()) return raw_->npc.name;
        return "";
    }

    int32_t oldAnimation() const { return raw_ ? raw_->oldAnimation : -1; }
    int32_t newAnimation() const { return raw_ ? raw_->newAnimation : -1; }
    int32_t gameTick() const { return raw_ ? raw_->gameTick : 0; }

    const TitanPluginSdk::AnimationChangedEvent* raw() const { return raw_; }

private:
    const TitanPluginSdk::AnimationChangedEvent* raw_ = nullptr;
};

/// Borrowed callback view; overheadText() returns an owned UTF-8 copy.
class OverheadTextChangedEvent {
public:
    explicit OverheadTextChangedEvent(const TitanPluginSdk::OverheadTextChangedEvent* raw) : raw_(raw) {}
    uint8_t actorType() const { return raw_ ? raw_->actorType : TitanPluginSdk::EntityType::NONE; }
    bool isPlayer() const { return actorType() == TitanPluginSdk::EntityType::PLAYER; }
    bool isNpc() const { return actorType() == TitanPluginSdk::EntityType::NPC; }
    Actor actor() const {
        if (isPlayer()) return Actor{Player::fromSnapshot(raw_->player, overheadText())};
        if (isNpc()) return Actor{Npc::fromSnapshot(raw_->npc, overheadText())};
        return Actor{};
    }
    Actor getActor() const { return actor(); }
    std::string overheadText() const {
        return raw_ && raw_->overheadText
            ? std::string(raw_->overheadText, static_cast<size_t>(raw_->overheadTextLength)) : std::string{};
    }
    std::string getOverheadText() const { return overheadText(); }
    int32_t gameTick() const { return raw_ ? raw_->gameTick : 0; }
private:
    const TitanPluginSdk::OverheadTextChangedEvent* raw_;
};

/// Item-container-changed event. Read-only view over the raw ABI struct;
/// carries the container id plus a snapshot of occupied slot entries.
/// Detection is tick-level diff on this revision -- see `HostApi` doc for
/// why. Added in SDK 26.
class ItemContainerChangedEvent {
public:
    struct Slot {
        int32_t slot = -1;
        int32_t itemId = -1;
        int32_t quantity = 0;
    };

    explicit ItemContainerChangedEvent(
        const TitanPluginSdk::ItemContainerChangedEvent* raw) : raw_(raw) {}

    int32_t containerId() const { return raw_ ? raw_->containerId : -1; }
    int32_t capacity() const    { return raw_ ? raw_->capacity : 0; }
    int32_t gameTick() const    { return raw_ ? raw_->gameTick : 0; }
    int32_t size() const        { return boundedWrittenCount(); }

    std::vector<Slot> items() const {
        if (!raw_) return {};
        std::vector<Slot> out;
        const int32_t n = boundedWrittenCount();
        out.reserve(n);
        for (int32_t i = 0; i < n; ++i) {
            out.push_back({ raw_->slots[i], raw_->itemIds[i], raw_->quantities[i] });
        }
        return out;
    }

    Slot slot(int32_t index) const {
        if (!raw_ || index < 0 || index >= boundedWrittenCount()) return {};
        return { raw_->slots[index], raw_->itemIds[index], raw_->quantities[index] };
    }

    const TitanPluginSdk::ItemContainerChangedEvent* raw() const { return raw_; }

private:
    int32_t boundedWrittenCount() const {
        if (!raw_ || raw_->writtenCount <= 0) return 0;
        return (std::min)(raw_->writtenCount,
            static_cast<int32_t>(TitanPluginSdk::kMaxItemContainerSlots));
    }

    const TitanPluginSdk::ItemContainerChangedEvent* raw_ = nullptr;
};

/// Varbit-changed event. Read-only view over the raw ABI struct; carries
/// the varbit id plus the resolved old/new values. Delivered only on actual
/// value changes (no-op SetVarbit writes are filtered by the host).
/// Added in SDK 21.
class VarbitChangedEvent {
public:
    explicit VarbitChangedEvent(const TitanPluginSdk::VarbitChangedEvent* raw) : raw_(raw) {}

    int32_t varbitId() const  { return raw_ ? raw_->varbitId  : 0; }
    int32_t oldValue() const  { return raw_ ? raw_->oldValue  : 0; }
    int32_t newValue() const  { return raw_ ? raw_->newValue  : 0; }
    int32_t gameTick() const  { return raw_ ? raw_->gameTick  : 0; }

    /// Convenience accessors.
    int32_t delta() const     { return newValue() - oldValue(); }

    const TitanPluginSdk::VarbitChangedEvent* raw() const { return raw_; }

private:
    const TitanPluginSdk::VarbitChangedEvent* raw_ = nullptr;
};

/// Game-state-changed event. Read-only view over native Client.GameState
/// transitions accepted by SetGameState. Loading (25) is exposed in SDK 126.
/// Added in SDK 91.
class GameStateChangedEvent {
public:
    explicit GameStateChangedEvent(
        const TitanPluginSdk::GameStateChangedEvent* raw) : raw_(raw) {}

    LoginGameState oldState() const {
        return raw_ ? static_cast<LoginGameState>(raw_->oldState)
                    : LoginGameState::Unknown;
    }
    LoginGameState newState() const {
        return raw_ ? static_cast<LoginGameState>(raw_->newState)
                    : LoginGameState::Unknown;
    }
    int32_t tickCount() const { return raw_ ? raw_->tickCount : 0; }

    const TitanPluginSdk::GameStateChangedEvent* raw() const { return raw_; }

private:
    const TitanPluginSdk::GameStateChangedEvent* raw_ = nullptr;
};

/// CS2 script-fired event. Read-only view over the int args/results captured
/// at the end of the script.
class ScriptEvent {
public:
    explicit ScriptEvent(const TitanPluginSdk::ScriptFiredEvent* raw) : raw_(raw) {}

    int32_t scriptId() const { return raw_ ? raw_->scriptId : 0; }

    std::vector<int32_t> args() const {
        if (!raw_) return {};
        const int32_t n = boundedArgCount();
        return std::vector<int32_t>(raw_->intArgs, raw_->intArgs + n);
    }
    std::vector<int32_t> results() const {
        if (!raw_) return {};
        const int32_t n = boundedResultCount();
        return std::vector<int32_t>(raw_->intResults, raw_->intResults + n);
    }

    int32_t arg(size_t i, int32_t def = 0) const {
        if (!raw_ || i >= static_cast<size_t>(boundedArgCount())) return def;
        return raw_->intArgs[i];
    }
    int32_t result(size_t i, int32_t def = 0) const {
        if (!raw_ || i >= static_cast<size_t>(boundedResultCount())) return def;
        return raw_->intResults[i];
    }

    const TitanPluginSdk::ScriptFiredEvent* raw() const { return raw_; }

private:
    int32_t boundedArgCount() const {
        if (!raw_ || raw_->intArgCount <= 0) return 0;
        return (std::min)(raw_->intArgCount,
            static_cast<int32_t>(TitanPluginSdk::kMaxCs2IntResults));
    }

    int32_t boundedResultCount() const {
        if (!raw_ || raw_->intResultCount <= 0) return 0;
        return (std::min)(raw_->intResultCount,
            static_cast<int32_t>(TitanPluginSdk::kMaxCs2IntResults));
    }

    const TitanPluginSdk::ScriptFiredEvent* raw_ = nullptr;
};

}  // namespace titan
