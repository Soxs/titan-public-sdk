#pragma once
#include "client.h"
#include <initializer_list>
#include <cctype>
#include <string_view>

namespace titan {
struct BankCacheSnapshot {
    bool known = false, live = false, loading = false;
    int64_t lastObservedAt = 0;
    std::string account, error;
    std::vector<ItemContainerSlot> items;
    int64_t count(int32_t id) const {
        int64_t result = 0;
        for (const auto& item : items) if (item.itemId == id) result += item.quantity;
        return result;
    }
    int64_t count(std::initializer_list<int32_t> ids) const {
        int64_t result = 0;
        for (const auto& item : items)
            if (std::find(ids.begin(), ids.end(), item.itemId) != ids.end()) result += item.quantity;
        return result;
    }
};
class ItemCacheFacade {
public:
    /// Nullopt means unsupported host. A supported, unobserved bank has known=false.
    /// Remembered counts are advisory; confirm live state before issuing actions.
    std::optional<BankCacheSnapshot> bank() const {
        auto* backend = detail::backend();
        TitanPluginSdk::BankCacheState state{};
        if (!backend || !backend->getItemCacheBank(&state)) return {};
        BankCacheSnapshot out;
        out.known = state.known != 0; out.live = state.live != 0; out.loading = state.loading != 0;
        out.account = state.account; out.error = state.error; out.lastObservedAt = state.lastObservedAt;
        if (out.known) for (int32_t i = 0; i < state.bank.writtenCount && i < 2048; ++i)
            if (state.bank.quantities[i] > 0)
                out.items.push_back({state.bank.slots[i], state.bank.itemIds[i], state.bank.quantities[i]});
        return out;
    }
    bool isLoaded() const { const auto value = bank(); return value && value->known; }
    int64_t count(int32_t id) const { const auto value = bank(); return value ? value->count(id) : 0; }
    int64_t count(std::initializer_list<int32_t> ids) const { const auto value = bank(); return value ? value->count(ids) : 0; }
    std::vector<ItemContainerSlot> getBankItems() const { const auto value = bank(); return value ? value->items : std::vector<ItemContainerSlot>{}; }
    int64_t getItemsCountInBank(std::initializer_list<int32_t> ids) const { return count(ids); }
    int64_t countByName(std::initializer_list<std::string_view> names,
                        std::initializer_list<std::string_view> ignore = {}) const {
        auto matches = [](std::string_view name, std::string_view part) {
            return part.empty() || std::search(name.begin(), name.end(), part.begin(), part.end(),
                [](unsigned char a, unsigned char b) { return std::tolower(a) == std::tolower(b); }) != name.end();
        };
        int64_t total = 0;
        for (const auto& item : getBankItems()) {
            auto def = state::itemDef(item.itemId);
            if (!def) continue;
            if (std::any_of(ignore.begin(), ignore.end(), [&](auto text) { return matches(def->name, text); })) continue;
            if (std::any_of(names.begin(), names.end(), [&](auto text) { return matches(def->name, text); })) total += item.quantity;
        }
        return total;
    }
    int64_t getItemsCountInBank(std::initializer_list<std::string_view> names,
                               std::initializer_list<std::string_view> ignore = {}) const { return countByName(names, ignore); }
};
namespace state { inline ItemCacheFacade itemCache() { return {}; } }
}
