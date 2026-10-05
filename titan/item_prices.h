#pragma once
#include "detail/backend.h"
#include <algorithm>
#include <optional>
#include <string>
#include <vector>

namespace titan {
struct ItemPriceMetadata {
    int32_t id = 0;
    std::string name, examine;
    std::optional<int64_t> buyLimit, highAlch;
    bool members = false;
};
struct ItemPrice {
    int32_t id = 0;
    std::optional<int64_t> high, low, highTime, lowTime;
    int64_t fetchedAt = 0, lastAttemptAt = 0;
    bool loading = false, pending = false;
    std::string error;
};
struct ItemPriceStatus {
    bool available = false, catalogLoading = false, catalogPending = false;
    int32_t pendingCount = 0, loadingItem = -1;
    int64_t catalogRevision = 0, catalogFetchedAt = 0, catalogLastAttemptAt = 0;
    std::string error;
};

namespace detail {
template<size_t N> inline std::string itemPriceString(const char (&value)[N]) {
    return std::string(value, std::find(value, value + N, '\0'));
}
inline ItemPriceMetadata itemPriceMetadata(const TitanPluginSdk::ItemPriceMetadata& value) {
    return {value.id, itemPriceString(value.name), itemPriceString(value.examine),
        value.present & 1 ? std::optional<int64_t>(value.buyLimit) : std::nullopt,
        value.present & 2 ? std::optional<int64_t>(value.highAlch) : std::nullopt, value.members != 0};
}
inline ItemPrice itemPrice(const TitanPluginSdk::ItemPrice& value) {
    return {value.id,
        value.present & 1 ? std::optional<int64_t>(value.high) : std::nullopt,
        value.present & 2 ? std::optional<int64_t>(value.low) : std::nullopt,
        value.present & 4 ? std::optional<int64_t>(value.highTime) : std::nullopt,
        value.present & 8 ? std::optional<int64_t>(value.lowTime) : std::nullopt,
        value.fetchedAt, value.lastAttemptAt, value.loading != 0, value.pending != 0, itemPriceString(value.error)};
}
}

/// One asynchronous client-wide cache. Reads never start network work; explicit
/// requests coalesce while queued, loading, or fresh. Returned values are owned.
class ItemPricesFacade {
public:
    bool requestCatalog() const {
        auto* api = detail::backend(); return api && api->requestItemPriceCatalog();
    }
    bool request(int32_t id) const {
        auto* api = detail::backend(); return id > 0 && api && api->requestItemPrice(id);
    }
    ItemPriceStatus status() const {
        TitanPluginSdk::ItemPriceStatus value;
        auto* api = detail::backend();
        if (!api || !api->getItemPriceStatus(&value)) return {};
        return {value.available != 0, value.catalogLoading != 0, value.catalogPending != 0,
            value.pendingCount, value.loadingItem, value.catalogRevision, value.catalogFetchedAt,
            value.catalogLastAttemptAt, detail::itemPriceString(value.error)};
    }
    std::optional<ItemPriceMetadata> item(int32_t id) const {
        TitanPluginSdk::ItemPriceMetadata value;
        auto* api = detail::backend();
        if (id <= 0 || !api || !api->getItemPriceMetadata(id, &value) || value.id != id) return std::nullopt;
        return detail::itemPriceMetadata(value);
    }
    std::optional<ItemPrice> price(int32_t id) const {
        TitanPluginSdk::ItemPrice value;
        auto* api = detail::backend();
        if (id <= 0 || !api || !api->getItemPrice(id, &value) || value.id != id) return std::nullopt;
        return detail::itemPrice(value);
    }
    std::vector<ItemPriceMetadata> items() const {
        auto* api = detail::backend();
        if (!api) return {};
        const auto before = status();
        const int32_t count = api->getItemPriceItemIds(nullptr, 0);
        if (!before.available || count <= 0 || count > 100000) return {};
        std::vector<int32_t> ids(static_cast<size_t>(count));
        if (api->getItemPriceItemIds(ids.data(), count) != count) return {};
        std::vector<ItemPriceMetadata> result;
        result.reserve(ids.size());
        for (const int32_t id : ids) {
            auto value = item(id);
            if (!value) return {};
            result.push_back(std::move(*value));
        }
        const auto after = status();
        if (!after.available || after.catalogRevision != before.catalogRevision) return {};
        return result;
    }
};
namespace state { inline ItemPricesFacade itemPrices() { return {}; } }
}
