#pragma once
#include "../detail/backend.h"
#include <string>
#include <algorithm>
#include <optional>
#include <vector>

namespace titan::utils::Ge {
using BuyOptions = TitanPluginSdk::GeBuyOptions;
using RequestState = TitanPluginSdk::GeRequestState;
using Phase = TitanPluginSdk::GeRequestPhase;
inline bool isComplete(const RequestState& r) {
    return r.phase == Phase::Completed || r.phase == Phase::Cancelled || r.phase == Phase::Failed;
}
/// Enqueues in the C++ host; no script-side tick/worker is required. 0 = rejected.
inline uint64_t addBuyToQueue(const BuyOptions& options) {
    auto* b = titan::detail::backend(); return b ? b->geSubmitBuy(&options) : 0;
}
inline uint64_t addBuyToQueue(int32_t itemId, int32_t quantity, bool toInventory,
                              bool webWalkToAndOpenGe, bool noted = true,
                              int32_t maxAttempts = 3, int32_t waitPerAttemptMs = 10000) {
    BuyOptions o; o.itemId = itemId; o.quantity = quantity; o.toInventory = toInventory;
    o.autoOpen = webWalkToAndOpenGe; o.noted = noted; o.maxAttempts = maxAttempts; o.waitPerAttemptMs = waitPerAttemptMs;
    return addBuyToQueue(o);
}
inline uint64_t addBuyToQueue(int32_t itemId, int32_t quantity, bool autoOpen = false) {
    return addBuyToQueue(itemId, quantity, true, autoOpen);
}
/// Selling is explicitly unsupported. It never enqueues or touches an offer.
inline uint64_t addSellToQueue(int32_t, int32_t) { return 0; }
inline std::optional<RequestState> request(uint64_t id) {
    RequestState out; auto* b = titan::detail::backend();
    if (!b || !b->geGetRequest(id, &out)) return {};
    return out;
}
inline std::vector<RequestState> getRequests() {
    auto* b = titan::detail::backend(); if (!b) return {};
    const auto count = b->geGetRequests(nullptr, 0);
    if (count <= 0 || count > 256) return {};
    std::vector<RequestState> out(count);
    if (b->geGetRequests(out.data(), count) != count) return {};
    return out;
}
inline std::vector<RequestState> getExchangeQueue() {
    auto out = getRequests();
    out.erase(std::remove_if(out.begin(), out.end(), isComplete), out.end()); return out;
}
inline bool abortRequest(uint64_t id) { auto* b = titan::detail::backend(); return b && b->geCancelRequest(id); }
inline bool release(uint64_t id) { auto* b = titan::detail::backend(); return b && b->geReleaseRequest(id); }
inline void clearExchangeQueue() { for (const auto& r : getExchangeQueue()) if (!isComplete(r)) abortRequest(r.requestId); }
inline int32_t getQueueSize() { int count = 0; for (const auto& r : getExchangeQueue()) if (!isComplete(r)) ++count; return count; }
inline bool isExchanging() { return getQueueSize() != 0; }
inline std::string getStatus() { for (const auto& r : getExchangeQueue()) if (!isComplete(r)) return r.message; return {}; }
}
