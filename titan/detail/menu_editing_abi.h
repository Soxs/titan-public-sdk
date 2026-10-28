#pragma once

#include <cstddef>
#include <cstdint>
#include <type_traits>

namespace TitanPluginSdk {

struct MenuOptionClickedEvent;

inline constexpr uint32_t kMenuEntryTextCapacity = 256;
inline constexpr uint32_t kMaxEditableMenuEntries = 4096;

enum class MenuEditResult : uint32_t {
    Ok = 0, Unavailable, WrongThread, NotEditing, StaleHandle,
    InvalidArgument, TextTooLong, Unsupported, LimitReached,
    OwnerUnavailable, NativeFailure, BufferTooSmall
};

enum class MenuLifecycleKind : uint32_t { PostMenuSort = 0, Opened = 1, EntryAdded = 2 };

namespace MenuEditingCapabilities {
inline constexpr uint32_t Read = 1u << 0;
inline constexpr uint32_t Edit = 1u << 1;
inline constexpr uint32_t Insert = 1u << 2;
inline constexpr uint32_t Callbacks = 1u << 3;
inline constexpr uint32_t Submenus = 1u << 4;
// Mutations are synchronous and accepted only inside a menu edit event.
inline constexpr uint32_t EventTransactions = 1u << 5;
}

namespace MenuEntryFlags {
inline constexpr uint32_t ForceLeftClick = 1u << 0;
inline constexpr uint32_t Deprioritized = 1u << 1;
inline constexpr uint32_t Custom = 1u << 2;
inline constexpr uint32_t Submenu = 1u << 3;
}

namespace MenuEntryFields {
inline constexpr uint32_t Option = 1u << 0;
inline constexpr uint32_t Target = 1u << 1;
inline constexpr uint32_t Opcode = 1u << 2;
inline constexpr uint32_t Identifier = 1u << 3;
inline constexpr uint32_t Param0 = 1u << 4;
inline constexpr uint32_t Param1 = 1u << 5;
inline constexpr uint32_t ItemId = 1u << 6;
inline constexpr uint32_t WorldViewId = 1u << 7;
inline constexpr uint32_t ForceLeftClick = 1u << 8;
inline constexpr uint32_t Deprioritized = 1u << 9;
inline constexpr uint32_t All = (1u << 10) - 1;
}

// IDs are opaque host identities, never native pointers or array offsets.
// A reset/rebuild invalidates the generation. An all-zero parent means root.
struct MenuEntryHandle {
    uint64_t generation = 0;
    uint64_t id = 0;
};

struct MenuState {
    uint64_t generation = 0;
    uint32_t count = 0; // Number of root siblings; public order is bottom to top.
    uint32_t capabilities = 0;
    uint8_t open = 0;
    uint8_t editable = 0;
    uint8_t reserved[6]{};
};

// New frozen payload. Strings are UTF-8, NUL terminated, and never truncated
// on mutation: text that does not fit is rejected with TextTooLong.
struct MenuEntryState {
    MenuEntryHandle handle{};
    MenuEntryHandle parent{};
    uint32_t opcode = 1500; // RuneLite custom-entry logical action ID.
    int32_t identifier = 0;
    int32_t param0 = 0;
    int32_t param1 = 0;
    int32_t itemId = -1;
    int32_t worldViewId = -1;
    uint32_t flags = 0;
    uint32_t reserved = 0;
    char option[kMenuEntryTextCapacity]{};
    char target[kMenuEntryTextCapacity]{};
};

// Borrowed for callback duration. Copy to retain the snapshot; its handles
// still expire with their generation. entry/index are set for EntryAdded.
struct MenuLifecycleEvent {
    MenuState menu{};
    MenuEntryHandle entry{};
    int32_t index = -1;
    uint32_t reserved = 0;
};

using MenuEntryClickFn = void (*)(void* userData, const MenuEntryHandle* entry,
                                 MenuOptionClickedEvent* event);
using MenuEntryCleanupFn = void (*)(void* userData);

static_assert(sizeof(MenuEntryHandle) == 16 && alignof(MenuEntryHandle) == 8);
static_assert(sizeof(MenuState) == 24 && alignof(MenuState) == 8);
static_assert(sizeof(MenuEntryState) == 576 && alignof(MenuEntryState) == 8);
static_assert(offsetof(MenuEntryState, opcode) == 32);
static_assert(offsetof(MenuEntryState, flags) == 56);
static_assert(offsetof(MenuEntryState, option) == 64);
static_assert(offsetof(MenuEntryState, target) == 320);
static_assert(sizeof(MenuLifecycleEvent) == 48 && alignof(MenuLifecycleEvent) == 8);
static_assert(std::is_standard_layout_v<MenuEntryState> && std::is_trivially_copyable_v<MenuEntryState>);
static_assert(std::is_trivially_copyable_v<MenuLifecycleEvent>);

} // namespace TitanPluginSdk
