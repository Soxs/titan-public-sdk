/// @file titan/hint_arrow.h
/// @brief Owned hint-arrow snapshots. Read on the game thread; retain freely.
#pragma once

#include "actor.h"
#include "world_point.h"
#include <optional>
#include <utility>
#include <vector>

namespace titan {

using HintArrowKind = TitanPluginSdk::HintArrowKind;
using HintArrowUpdateResult = TitanPluginSdk::HintArrowUpdateResult;

/// World tile and subtile coordinates, with optional resolved world context.
struct HintArrowLocation {
    int32_t tileX = 0, tileY = 0;
    int32_t subX = 0, subY = 0;
    int32_t height = 0; // Native stored value; world rendering uses height * 2.

    HintArrowLocation(int32_t x = 0, int32_t y = 0, int32_t fineX = 0,
                      int32_t fineY = 0, int32_t storedHeight = 0)
        : tileX(x), tileY(y), subX(fineX), subY(fineY), height(storedHeight) {}

    /// Frozen at capture, using the native selected view's current plane.
    /// Empty when the host cannot resolve that exact coordinate snapshot.
    std::optional<WorldPoint> worldPoint() const { return worldPoint_; }

private:
    friend class HintArrow;
    std::optional<WorldPoint> worldPoint_;
};

class HintArrow {
public:
    const TitanPluginSdk::HintArrowState& raw() const { return state_; }
    uint32_t slot() const { return state_.slot; }
    HintArrowKind kind() const { return state_.kind; }
    int32_t rawKind() const { return state_.rawKind; }
    int32_t targetIndex() const { return state_.targetIndex; }
    bool drawInWorld() const { return state_.drawInWorld != 0; }
    int32_t flashPeriod() const { return state_.flashPeriod; }
    int32_t flashThreshold() const { return state_.flashThreshold; }
    bool targetResolved() const { return state_.targetResolved != 0; }
    std::optional<HintArrowLocation> location() const {
        return location_;
    }
    /// Frozen actor snapshot, captured only after matching the host identity.
    /// Empty for non-actors, unresolved targets, or an identity that changed.
    const std::optional<Actor>& target() const { return target_; }

private:
    friend class HintArrowSnapshot;

    /// Materialize on the game thread. No retained native pointer is read.
    static HintArrow capture(const TitanPluginSdk::HintArrowState& state) {
        HintArrow arrow;
        arrow.state_ = state;
        auto* backend = detail::backend();
        if (state.kind == HintArrowKind::Coordinate) {
            arrow.location_ = HintArrowLocation{state.tileX, state.tileY, state.subX, state.subY, state.height};
            TitanPluginSdk::WorldPointState point{};
            if (backend && backend->getHintArrowWorldPoint(&state, &point)
                    && point.x == state.tileX && point.y == state.tileY
                    && point.z >= 0 && point.z <= 3 && point.worldViewId >= 0)
                arrow.location_->worldPoint_ = WorldPoint{point.x, point.y, point.z, point.worldViewId};
            return arrow;
        }
        if (!backend || !state.targetResolved || state.targetIndex < 0
                || state.actorWorldViewId < 0 || !state.actorEntityPtr || !state.actorWorldViewPtr) return arrow;
        auto matches = [&](const auto& fresh) {
            return fresh.hashIndex == state.targetIndex && fresh.worldViewId == state.actorWorldViewId
                && fresh.entityPtr == state.actorEntityPtr && fresh.worldViewPtr == state.actorWorldViewPtr;
        };
        if (state.kind == HintArrowKind::Npc) {
            TitanPluginSdk::NpcState fresh{};
            if (backend->getNpcByIndexInWorldView(state.targetIndex, state.actorWorldViewId, &fresh) && matches(fresh))
                arrow.target_ = Actor{Npc::fromSnapshot(fresh)};
        } else if (state.kind == HintArrowKind::Player) {
            TitanPluginSdk::PlayerState fresh{};
            if (backend->getPlayerByIndexInWorldView(state.targetIndex, state.actorWorldViewId, &fresh) && matches(fresh))
                arrow.target_ = Actor{Player::fromSnapshot(fresh)};
        }
        return arrow;
    }

    TitanPluginSdk::HintArrowState state_{};
    std::optional<HintArrowLocation> location_;
    std::optional<Actor> target_;
};

class HintArrowSnapshot {
public:
    const std::vector<HintArrow>& entries() const { return entries_; }
    /// Slot zero is the server arrow. A present None row means it is cleared;
    /// an empty optional means the readable vector contains no slot-zero row.
    std::optional<HintArrow> server() const {
        for (const auto& arrow : entries_) if (arrow.slot() == 0) return arrow;
        return std::nullopt;
    }

    /// nullopt means unavailable (including a call outside the game thread).
    /// An engaged snapshot with no entries is a valid empty collection.
    static std::optional<HintArrowSnapshot> read() {
        auto* backend = detail::backend();
        if (!backend) return std::nullopt;
        const uint32_t count = backend->getHintArrows(nullptr, 0);
        if (count > TitanPluginSdk::kMaxHintArrows) return std::nullopt;
        HintArrowSnapshot snapshot;
        if (!count) return snapshot;
        std::vector<TitanPluginSdk::HintArrowState> states(count);
        const uint32_t copied = backend->getHintArrows(states.data(), count);
        if (copied > count) return std::nullopt;
        snapshot.entries_.reserve(copied);
        for (uint32_t i = 0; i < copied; ++i) snapshot.entries_.push_back(HintArrow::capture(states[i]));
        return snapshot;
    }

private:
    std::vector<HintArrow> entries_;
};

} // namespace titan
