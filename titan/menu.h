/// @file titan/menu.h
/// @brief Live menu handles, RuneLite-style ordering, and owned click callbacks.
#pragma once

#include "detail/host.h"
#include "detail/plugin_context.h"
#include "menu_action.h"
#include <algorithm>
#include <cstring>
#include <functional>
#include <optional>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

namespace titan {

using MenuEditResult = TitanPluginSdk::MenuEditResult;
using MenuEntryHandle = TitanPluginSdk::MenuEntryHandle;
using MenuEntrySnapshot = TitanPluginSdk::MenuEntryState;
namespace MenuCapabilities = TitanPluginSdk::MenuEditingCapabilities;

class Menu;
class MenuEntry;

/// Borrowed synchronous click event. consume() also prevents a native action.
class MenuEntryClick {
public:
    MenuEntryClick(MenuEntryHandle entry, TitanPluginSdk::MenuOptionClickedEvent* event)
        : entry_(entry), event_(event) {}
    MenuEntry getMenuEntry() const;
    void consume() { if (event_) event_->consumed = 1; }
    bool isConsumed() const { return event_ && event_->consumed != 0; }
    TitanPluginSdk::MenuOptionClickedEvent* raw() const { return event_; }
private:
    MenuEntryHandle entry_{};
    TitanPluginSdk::MenuOptionClickedEvent* event_ = nullptr;
};

/// Opaque live identity. A rebuild/reset invalidates retained handles. Getters
/// read the current host snapshot; use snapshot() for a freely retained copy.
class MenuEntry {
public:
    MenuEntry() = default;
    explicit MenuEntry(MenuEntryHandle handle) : handle_(handle) {}
    MenuEntryHandle handle() const { return handle_; }
    MenuEditResult lastResult() const { return result_; }
    std::optional<MenuEntrySnapshot> snapshot() const {
        const auto* host = detail::host();
        if (!host || !host->menuGetEntry) { result_ = MenuEditResult::Unavailable; return std::nullopt; }
        MenuEntrySnapshot state{};
        result_ = host->menuGetEntry(&handle_, &state);
        if (result_ != MenuEditResult::Ok) return std::nullopt;
        if (state.handle.generation != handle_.generation || state.handle.id != handle_.id
                || !std::memchr(state.option, 0, sizeof(state.option))
                || !std::memchr(state.target, 0, sizeof(state.target))) {
            result_ = MenuEditResult::InvalidArgument; return std::nullopt;
        }
        return state;
    }
    bool isValid() const { return snapshot().has_value(); }
    std::string getOption() const { auto s = snapshot(); return s ? s->option : ""; }
    std::string getTarget() const { auto s = snapshot(); return s ? s->target : ""; }
    uint32_t getOpcode() const { auto s = snapshot(); return s ? s->opcode : 0; }
    MenuAction::Id getType() const { return static_cast<MenuAction::Id>(getOpcode()); }
    int32_t getIdentifier() const { auto s = snapshot(); return s ? s->identifier : 0; }
    int32_t getParam0() const { auto s = snapshot(); return s ? s->param0 : 0; }
    int32_t getParam1() const { auto s = snapshot(); return s ? s->param1 : 0; }
    int32_t getItemId() const { auto s = snapshot(); return s ? s->itemId : -1; }
    int32_t getWorldViewId() const { auto s = snapshot(); return s ? s->worldViewId : -1; }
    bool isForceLeftClick() const { return flag(TitanPluginSdk::MenuEntryFlags::ForceLeftClick); }
    bool isDeprioritized() const { return flag(TitanPluginSdk::MenuEntryFlags::Deprioritized); }
    bool isCustom() const { return flag(TitanPluginSdk::MenuEntryFlags::Custom); }
    bool isSubmenu() const { return flag(TitanPluginSdk::MenuEntryFlags::Submenu); }
    MenuEntry getParent() const { auto s = snapshot(); return MenuEntry{s ? s->parent : MenuEntryHandle{}}; }
    Menu getSubMenu() const;

    MenuEntry& setOption(std::string_view text) { return setText(text, true); }
    MenuEntry& setTarget(std::string_view text) { return setText(text, false); }
    // Existing native entries retain their callback identity: a changed native
    // opcode/identifier/parameter tuple returns Unsupported. Custom entries
    // accept logical IDs; option and target display labels remain editable.
    MenuEntry& setOpcode(uint32_t value) { MenuEntrySnapshot s{}; s.opcode = value; return update(TitanPluginSdk::MenuEntryFields::Opcode, s); }
    MenuEntry& setType(MenuAction::Id value) { return setOpcode(static_cast<uint32_t>(value)); }
    MenuEntry& setIdentifier(int32_t value) { MenuEntrySnapshot s{}; s.identifier = value; return update(TitanPluginSdk::MenuEntryFields::Identifier, s); }
    MenuEntry& setParam0(int32_t value) { MenuEntrySnapshot s{}; s.param0 = value; return update(TitanPluginSdk::MenuEntryFields::Param0, s); }
    MenuEntry& setParam1(int32_t value) { MenuEntrySnapshot s{}; s.param1 = value; return update(TitanPluginSdk::MenuEntryFields::Param1, s); }
    MenuEntry& setItemId(int32_t value) { MenuEntrySnapshot s{}; s.itemId = value; return update(TitanPluginSdk::MenuEntryFields::ItemId, s); }
    MenuEntry& setWorldViewId(int32_t value) { MenuEntrySnapshot s{}; s.worldViewId = value; return update(TitanPluginSdk::MenuEntryFields::WorldViewId, s); }
    MenuEntry& setForceLeftClick(bool value) { MenuEntrySnapshot s{}; s.flags = value ? TitanPluginSdk::MenuEntryFlags::ForceLeftClick : 0; return update(TitanPluginSdk::MenuEntryFields::ForceLeftClick, s); }
    MenuEntry& setDeprioritized(bool value) { MenuEntrySnapshot s{}; s.flags = value ? TitanPluginSdk::MenuEntryFlags::Deprioritized : 0; return update(TitanPluginSdk::MenuEntryFields::Deprioritized, s); }

    /// RuneLite-style consumer of the selected entry. Custom entries never
    /// dispatch an arbitrary numeric opcode into the game's native handler.
    MenuEntry& onClick(std::function<void(MenuEntry&)> callback) {
        if (!callback) return onClickEvent({});
        return onClickEvent([fn = std::move(callback)](MenuEntryClick& event) {
            auto entry = event.getMenuEntry(); fn(entry);
        });
    }
    /// Variant that also permits click consumption. The host owns the callable
    /// until replacement, generation expiry, disable or unload; cleanup is once.
    MenuEntry& onClickEvent(std::function<void(MenuEntryClick&)> callback) {
        const auto* host = detail::host();
        auto* owner = detail::currentPlugin();
        if (!host || !host->menuSetEntryCallback) { result_ = MenuEditResult::Unavailable; return *this; }
        if (!owner) { result_ = MenuEditResult::OwnerUnavailable; return *this; }
        if (!callback) {
            result_ = host->menuSetEntryCallback(owner, &handle_, nullptr, nullptr, nullptr); return *this;
        }
        struct Callback {
            std::function<void(MenuEntryClick&)> function;
            const TitanPluginSdk::HostApi* host;
            detail::IBackend* backend;
            Plugin* owner;
        };
        auto* data = new Callback{std::move(callback), host, detail::backend(), owner};
        result_ = host->menuSetEntryCallback(owner, &handle_,
            +[](void* data, const MenuEntryHandle* handle, TitanPluginSdk::MenuOptionClickedEvent* raw) noexcept {
                auto& callback = *static_cast<Callback*>(data);
                detail::ScopedBackendContext scope(callback.backend, callback.host, callback.owner);
                if (!handle || !raw) return;
                MenuEntryClick event{*handle, raw};
                try { callback.function(event); }
                catch (...) {
                    event.consume();
                    try { if (callback.host->log) callback.host->log("Menu entry callback threw an exception"); }
                    catch (...) {}
                }
            }, data, +[](void* data) {
                auto* callback = static_cast<Callback*>(data);
                detail::ScopedBackendContext scope(callback->backend, callback->host, callback->owner);
                delete callback;
            });
        return *this;
    }
    bool remove() {
        const auto* h = detail::host(); const auto* owner = detail::currentPlugin();
        result_ = !h || !h->menuRemoveEntry ? MenuEditResult::Unavailable
            : !owner ? MenuEditResult::OwnerUnavailable : h->menuRemoveEntry(owner, &handle_);
        return result_ == MenuEditResult::Ok;
    }

private:
    friend class Menu;
    MenuEntry(MenuEntryHandle handle, MenuEditResult result) : handle_(handle), result_(result) {}
    bool flag(uint32_t bit) const { auto s = snapshot(); return s && (s->flags & bit) != 0; }
    MenuEntry& update(uint32_t mask, const MenuEntrySnapshot& value) {
        const auto* h = detail::host(); const auto* owner = detail::currentPlugin();
        result_ = !h || !h->menuUpdateEntry ? MenuEditResult::Unavailable
            : !owner ? MenuEditResult::OwnerUnavailable : h->menuUpdateEntry(owner, &handle_, mask, &value);
        return *this;
    }
    MenuEntry& setText(std::string_view text, bool option) {
        if (text.size() >= TitanPluginSdk::kMenuEntryTextCapacity) { result_ = MenuEditResult::TextTooLong; return *this; }
        if (text.find('\0') != std::string_view::npos) { result_ = MenuEditResult::InvalidArgument; return *this; }
        MenuEntrySnapshot value{};
        if (!text.empty()) std::memcpy(option ? value.option : value.target, text.data(), text.size());
        return update(option ? TitanPluginSdk::MenuEntryFields::Option : TitanPluginSdk::MenuEntryFields::Target, value);
    }
    MenuEntryHandle handle_{};
    mutable MenuEditResult result_ = MenuEditResult::Unavailable;
};

/// Public entries run bottom-to-top: back() is the default/top entry. Negative
/// insertion indices count from the end; -1 appends. All edits are synchronous
/// and require a PostMenuSort/MenuOpened/MenuEntryAdded edit callback.
class Menu {
public:
    Menu() = default;
    explicit Menu(MenuEntryHandle parent) : parent_(parent), submenu_(true) {}
    static Menu forGeneration(uint64_t generation) { Menu menu; menu.generation_ = generation; menu.generationBound_ = true; return menu; }
    uint32_t capabilities() const { const auto* h = detail::host(); return h && h->menuCapabilities ? h->menuCapabilities() : 0; }
    MenuEditResult lastResult() const { return result_; }
    std::optional<TitanPluginSdk::MenuState> state() const {
        const auto* h = detail::host(); TitanPluginSdk::MenuState value{};
        result_ = h && h->menuGetState ? h->menuGetState(&value) : MenuEditResult::Unavailable;
        if (result_ != MenuEditResult::Ok) return std::nullopt;
        if ((generationBound_ && (!generation_ || generation_ != value.generation))
                || (submenu_ && (!parent_.id || parent_.generation != value.generation))) {
            result_ = MenuEditResult::StaleHandle; return std::nullopt;
        }
        return value;
    }
    bool isOpen() const { auto s = state(); return s && s->open != 0; }
    bool isEditable() const { auto s = state(); return s && s->editable != 0; }
    std::vector<MenuEntry> getMenuEntries() const {
        std::vector<MenuEntry> entries; auto s = state(); if (!s) return entries;
        const auto* h = detail::host();
        if (!h || !h->menuGetEntries) { result_ = MenuEditResult::Unavailable; return entries; }
        uint32_t count = 0;
        result_ = h->menuGetEntries(s->generation, parent(), nullptr, 0, &count);
        if (result_ != MenuEditResult::Ok && result_ != MenuEditResult::BufferTooSmall) return entries;
        if (count > TitanPluginSdk::kMaxEditableMenuEntries) { result_ = MenuEditResult::LimitReached; return entries; }
        if (!count) { result_ = MenuEditResult::Ok; return entries; }
        std::vector<MenuEntrySnapshot> values(count); uint32_t copied = 0;
        result_ = h->menuGetEntries(s->generation, parent(), values.data(), count, &copied);
        if (result_ != MenuEditResult::Ok) return entries;
        if (copied > count) { result_ = MenuEditResult::InvalidArgument; return entries; }
        for (uint32_t i = 0; i < copied; ++i) {
            if (values[i].handle.generation != s->generation || !values[i].handle.id) {
                result_ = MenuEditResult::InvalidArgument; return {};
            }
            entries.emplace_back(values[i].handle);
        }
        return entries;
    }
    MenuEntry createMenuEntry(int32_t index = -1) const {
        if (submenu_) { result_ = MenuEditResult::Unsupported; return MenuEntry{{}, result_}; }
        auto s = state(); if (!s) return MenuEntry{{}, result_};
        const auto* h = detail::host(); const auto* owner = detail::currentPlugin();
        MenuEntrySnapshot initial{}, created{};
        result_ = !h || !h->menuCreateEntry ? MenuEditResult::Unavailable : !owner ? MenuEditResult::OwnerUnavailable
            : h->menuCreateEntry(owner, s->generation, index, &initial, &created);
        if (result_ == MenuEditResult::Ok && (!created.handle.id || created.handle.generation != s->generation))
            result_ = MenuEditResult::InvalidArgument;
        return MenuEntry{result_ == MenuEditResult::Ok ? created.handle : MenuEntryHandle{}, result_};
    }
    /// Reorder these siblings; omitted leaves are removed. Every existing
    /// submenu must remain, and entries cannot be moved between parents.
    bool setMenuEntries(const std::vector<MenuEntry>& entries) const {
        auto s = state(); if (!s) return false;
        const auto* h = detail::host(); const auto* owner = detail::currentPlugin();
        if (entries.size() > TitanPluginSdk::kMaxEditableMenuEntries) { result_ = MenuEditResult::LimitReached; return false; }
        std::vector<MenuEntryHandle> handles; handles.reserve(entries.size());
        for (const auto& entry : entries) handles.push_back(entry.handle());
        result_ = !h || !h->menuSetEntries ? MenuEditResult::Unavailable : !owner ? MenuEditResult::OwnerUnavailable
            : h->menuSetEntries(owner, s->generation, parent(), handles.data(), static_cast<uint32_t>(handles.size()));
        return result_ == MenuEditResult::Ok;
    }
    bool swapMenuEntries(uint32_t first, uint32_t second) const {
        auto entries = getMenuEntries(); if (result_ != MenuEditResult::Ok) return false;
        if (first >= entries.size() || second >= entries.size()) { result_ = MenuEditResult::InvalidArgument; return false; }
        std::swap(entries[first], entries[second]); return setMenuEntries(entries);
    }
private:
    const MenuEntryHandle* parent() const { return submenu_ ? &parent_ : nullptr; }
    MenuEntryHandle parent_{};
    uint64_t generation_ = 0;
    bool generationBound_ = false;
    bool submenu_ = false;
    mutable MenuEditResult result_ = MenuEditResult::Unavailable;
};

inline Menu MenuEntry::getSubMenu() const { return Menu{handle_}; }
inline MenuEntry MenuEntryClick::getMenuEntry() const { return MenuEntry{entry_}; }

/// Owned event snapshot. Its Menu/Entry views still obey generation validity.
class MenuEvent {
public:
    explicit MenuEvent(const TitanPluginSdk::MenuLifecycleEvent* event)
        : event_(event ? *event : TitanPluginSdk::MenuLifecycleEvent{}) {}
    Menu getMenu() const { return Menu::forGeneration(event_.menu.generation); }
    MenuEntry getMenuEntry() const { return MenuEntry{event_.entry}; }
    uint64_t generation() const { return event_.menu.generation; }
    int32_t getIndex() const { return event_.index; }
    const TitanPluginSdk::MenuState& state() const { return event_.menu; }
private:
    TitanPluginSdk::MenuLifecycleEvent event_{};
};
using PostMenuSortEvent = MenuEvent;
using MenuOpenedEvent = MenuEvent;
using MenuEntryAddedEvent = MenuEvent;

namespace state { inline Menu menu() { return Menu{}; } }

} // namespace titan
