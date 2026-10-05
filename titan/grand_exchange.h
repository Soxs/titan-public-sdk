#pragma once

#include "detail/backend.h"
#include <optional>
#include <vector>

namespace titan {
using GrandExchangeOfferState = TitanPluginSdk::GrandExchangeOfferState;

/// Immutable owned offer snapshot; safe to retain after event delivery.
class GrandExchangeOffer {
public:
    explicit GrandExchangeOffer(const TitanPluginSdk::GrandExchangeOffer& value) : value_(value) {}
    int32_t slot() const { return value_.slot; }
    int32_t itemId() const { return value_.itemId; }
    int32_t totalQuantity() const { return value_.totalQuantity; }
    int32_t quantitySold() const { return value_.quantitySold; }
    int64_t price() const { return value_.price; }
    int64_t spent() const { return value_.spent; }
    GrandExchangeOfferState state() const { return value_.state; }
    uint8_t status() const { return value_.status; }
    uint8_t type() const { return value_.type; }
    int32_t getSlot() const { return slot(); }
    int32_t getItemId() const { return itemId(); }
    int32_t getTotalQuantity() const { return totalQuantity(); }
    int32_t getQuantitySold() const { return quantitySold(); }
    int64_t getPrice() const { return price(); }
    int64_t getSpent() const { return spent(); }
    GrandExchangeOfferState getState() const { return state(); }
private:
    TitanPluginSdk::GrandExchangeOffer value_;
};

class GrandExchangeFacade {
public:
    /// Full snapshots require all captured offer events to finish delivery.
    /// False also covers a transient event backlog or initialization batch.
    bool available() const {
        auto* backend = detail::backend();
        return backend && backend->isGrandExchangeAvailable();
    }
    /// An owned slot can remain readable while the full snapshot is unsettled.
    std::optional<GrandExchangeOffer> offer(int32_t slot) const {
        auto* backend = detail::backend();
        TitanPluginSdk::GrandExchangeOffer value;
        if (!backend || !backend->getGrandExchangeOffer(slot, &value)) return std::nullopt;
        return GrandExchangeOffer(value);
    }
    /// Empty until a complete snapshot and its preceding events have settled.
    std::vector<GrandExchangeOffer> offers() const {
        auto* backend = detail::backend();
        if (!backend) return {};
        const auto count = backend->getGrandExchangeOffers(nullptr, 0);
        if (count <= 0 || count > 64) return {};
        std::vector<TitanPluginSdk::GrandExchangeOffer> buffer(static_cast<size_t>(count));
        if (backend->getGrandExchangeOffers(buffer.data(), count) != count) return {};
        std::vector<GrandExchangeOffer> result;
        result.reserve(buffer.size());
        for (const auto& value : buffer) result.emplace_back(value);
        return result;
    }
};

namespace state {
inline GrandExchangeFacade grandExchange() { return {}; }
}
} // namespace titan
