/// @file titan/activity.h
/// @brief Fixed-ABI contract for an activity one plugin can run for another.
///
/// An activity is something a plugin already knows how to do on its own
/// (play Wintertodt, catch fish at Tempoross) offered so that an orchestrating
/// plugin (Account Maker) can use it as a method for a larger goal: "train
/// Firemaking from 50 by playing Wintertodt". The orchestrator decides when,
/// gathers what the activity says it needs, takes the account to its start
/// point and then drives it one game tick at a time, so one plugin is in
/// charge of the account at any moment.
///
/// ## Publishing
///
/// A provider fills an ActivityProviderV1 and publishes it as an ordinary
/// plugin service under `titan.activity.v1/<its plugin id>`:
///
/// @code
///   titan::registerService(*this, titan::activityServiceId(id()).c_str(), &activities_);
/// @endcode
///
/// That is the whole registration. Orchestrators find providers by asking for
/// that id for every plugin the host lists; nothing is registered with the
/// orchestrator, so load order does not matter and no pointer outlives a lease.
///
/// ## Driving
///
/// @code
///   if (auto provider = titan::service<titan::ActivityProviderV1>(serviceId.c_str());
///       titan::validActivityProviderV1(provider.get())) {
///       titan::ActivityStatus status{};
///       provider->tick(provider->userData, "WINTERTODT", "account_maker",
///                      titan::ActivityRequest::Run, gameTick, &status);
///   }   // the lease ends here: resolve the service again next tick
/// @endcode
///
/// - `acquire` claims the activity for an owner. It fails while the provider
///   is running the activity for itself (its own Start button) or for another
///   owner; while claimed, the provider refuses its own Start.
/// - `tick` does one game tick of work for the owner and reports. It is called
///   on the game thread, from the owner's own tick. With ActivityRequest::Finish
///   the provider winds down to a point the account can walk away from and
///   then reports ActivityStatusFlags::Finished; with ActivityRequest::Leave
///   it does so at once. An account must never be left standing inside an
///   arena that harms it with nothing driving it: an owner that is stopped
///   asks to Leave first.
/// - `release` gives the activity back. A provider also releases an owner that
///   has not ticked it for a while (the owner was stopped or unloaded).
///
/// No C++ objects cross the DLL boundary; every struct is plain data with a
/// size and version first. Strings are NUL-terminated and truncated to fit.

#pragma once

#include <cstddef>
#include <cstdint>
#include <cstring>
#include <string>
#include <string_view>
#include <type_traits>

namespace titan {

inline constexpr const char* kActivityServicePrefix = "titan.activity.v1/";
inline constexpr uint32_t kActivityApiVersion = 1;
inline constexpr uint32_t kActivityMaxSkills = 8;
inline constexpr uint32_t kActivityMaxSkillRequirements = 8;
inline constexpr uint32_t kActivityMaxItemSets = 8;
inline constexpr uint32_t kActivityMaxSetItems = 32;
inline constexpr uint32_t kActivityMaxWorlds = 8;

/// The service id a plugin publishes its activities under.
inline std::string activityServiceId(std::string_view pluginId) {
    return std::string(kActivityServicePrefix) + std::string(pluginId);
}

namespace ActivityKind { enum : uint32_t { Skill = 1 }; }
namespace ActivityFlags { enum : uint32_t { Members = 1u << 0 }; }
namespace ActivitySkillFlags {
enum : uint32_t {
    /// Trained as a side effect, or a slower way than the usual one: chosen
    /// only when a plan asks for this activity by name.
    Secondary = 1u << 0,
};
}
namespace ActivityItemPlace { enum : uint32_t { Inventory = 0, Equipped = 1, OnPlayer = 2 }; }
namespace ActivityItemFlags {
enum : uint32_t {
    /// Taken when the account owns it; never a reason to stop or to go and get one.
    Optional = 1u << 0,
    /// A tool that can also be wielded (an axe): worn when the account has
    /// the levels to, which frees an inventory slot; carried otherwise.
    Wield = 1u << 1,
};
}
namespace ActivityState { enum : uint32_t { Working = 1, Waiting = 2, Blocked = 3 }; }
namespace ActivityStatusFlags {
enum : uint32_t {
    /// The account is somewhere only the activity leaves properly (inside the
    /// arena): the owner should not walk it away or bank in the middle.
    Inside = 1u << 0,
    /// Nothing is lost by stopping this tick (between rounds).
    SafeToStop = 1u << 1,
    /// After ActivityRequest::Finish: wound down, the account can walk away.
    Finished = 1u << 2,
    /// The account is not where the activity can begin. The provider does
    /// nothing this tick; the owner brings the account to the start point.
    Away = 1u << 3,
};
}
namespace ActivityRequest {
enum : uint32_t {
    Run = 0,
    /// Wind down: finish what is under way if that is quick (the round), then
    /// get the account out, and report Finished.
    Finish = 1,
    /// Get the account out now, whatever is under way, and report Finished.
    /// The owner is stopping, or the account should not be here (wrong world).
    Leave = 2,
};
}

/// A skill the activity trains while the account's level is in [minLevel, maxLevel).
struct ActivitySkill {
    char skill[24] = {};      ///< Upper case, as in a plan line: "FIREMAKING".
    int32_t minLevel = 1;
    int32_t maxLevel = 100;   ///< Exclusive.
    uint32_t flags = 0;       ///< ActivitySkillFlags.
    uint32_t reserved = 0;
};

/// A level the account needs before the activity can be used at all.
struct ActivitySkillRequirement {
    char skill[24] = {};
    int32_t level = 1;
    uint32_t reserved = 0;
};

/// "`count` different items out of these", in preference order (best first).
/// `groups[i]` other than 0 names the slot item `i` competes for: at most one
/// item is chosen per group (one body piece, one pair of gloves).
struct ActivityItemSet {
    char label[32] = {};      ///< For people: "Warm clothing".
    uint32_t count = 1;
    uint32_t place = ActivityItemPlace::Inventory;
    uint32_t flags = 0;       ///< ActivityItemFlags.
    uint32_t itemCount = 0;
    int32_t items[kActivityMaxSetItems] = {};
    uint8_t groups[kActivityMaxSetItems] = {};
};

struct ActivityInfo {
    uint32_t structSize = sizeof(ActivityInfo);
    uint32_t apiVersion = kActivityApiVersion;
    char id[32] = {};         ///< Stable key, upper case; a plan pins it: "WINTERTODT".
    char name[64] = {};
    uint32_t kind = ActivityKind::Skill;
    uint32_t flags = 0;       ///< ActivityFlags.
    uint32_t skillCount = 0;
    uint32_t requirementCount = 0;
    uint32_t itemSetCount = 0;
    /// Inventory slots the activity wants free when it begins.
    uint32_t freeSlots = 0;
    /// Where the owner brings the account when the activity reports Away.
    int32_t startX = 0, startY = 0, startPlane = 0;
    uint32_t startRadius = 0;
    /// The box the activity takes place in (on startPlane), all zero when it
    /// has none. While the account stands in it the owner neither prepares
    /// supplies nor walks it elsewhere: only the activity leaves it properly.
    int32_t areaMinX = 0, areaMinY = 0, areaMaxX = 0, areaMaxY = 0;
    /// Worlds the activity is meant to be played on (a minigame's official
    /// worlds, where the other players are), 0 ending the list; all zero when
    /// any world will do. The owner hops to the busiest of them at the start
    /// point, before the activity begins.
    int32_t worlds[kActivityMaxWorlds] = {};
    uint32_t reserved[4] = {};
    ActivitySkill skills[kActivityMaxSkills] = {};
    ActivitySkillRequirement requirements[kActivityMaxSkillRequirements] = {};
    ActivityItemSet itemSets[kActivityMaxItemSets] = {};
};

struct ActivityStatus {
    uint32_t structSize = sizeof(ActivityStatus);
    uint32_t apiVersion = kActivityApiVersion;
    uint32_t state = ActivityState::Waiting;
    uint32_t flags = 0;       ///< ActivityStatusFlags.
    /// Changes whenever the activity gets somewhere (experience gained, a
    /// round begun): the owner's stall guard watches it.
    uint64_t progress = 0;
    char message[160] = {};
};

/// The service table. Every callback returns 1 on success and 0 on rejection
/// and is called on the game thread. Pointers are borrowed for the call.
struct ActivityProviderV1 {
    uint32_t structSize = sizeof(ActivityProviderV1);
    uint32_t apiVersion = kActivityApiVersion;
    void* userData = nullptr;

    uint32_t (*count)(void* userData) = nullptr;
    uint8_t (*describe)(void* userData, uint32_t index, ActivityInfo* outInfo) = nullptr;
    uint8_t (*acquire)(void* userData, const char* activityId, const char* owner) = nullptr;
    uint8_t (*release)(void* userData, const char* activityId, const char* owner) = nullptr;
    uint8_t (*tick)(void* userData, const char* activityId, const char* owner,
                    uint32_t request, int32_t gameTick, ActivityStatus* outStatus) = nullptr;
};

static_assert(std::is_standard_layout_v<ActivityInfo> && std::is_trivially_copyable_v<ActivityInfo>);
static_assert(std::is_standard_layout_v<ActivityStatus> && std::is_trivially_copyable_v<ActivityStatus>);
static_assert(std::is_standard_layout_v<ActivityProviderV1> && std::is_trivially_copyable_v<ActivityProviderV1>);
static_assert(sizeof(ActivitySkill) == 40);
static_assert(sizeof(ActivitySkillRequirement) == 32);
static_assert(sizeof(ActivityItemSet) == 32 + 16 + 4 * kActivityMaxSetItems + kActivityMaxSetItems);
static_assert(offsetof(ActivityInfo, skills) == 8 + 32 + 64 + 16 + 8 + 16 + 32 + 4 * kActivityMaxWorlds);
static_assert(sizeof(ActivityStatus) == 24 + 160);
static_assert(sizeof(ActivityProviderV1) == 16 + 5 * sizeof(void*));

/// Validate a resolved table before use. Appended fields are allowed; the
/// complete V1 prefix is mandatory.
inline bool validActivityProviderV1(const ActivityProviderV1* provider) noexcept {
    return provider
        && provider->structSize >= sizeof(ActivityProviderV1)
        && provider->apiVersion == kActivityApiVersion
        && provider->count && provider->describe
        && provider->acquire && provider->release && provider->tick;
}

/// Copy text into a fixed field, truncated and always terminated.
template <std::size_t N>
inline void activityText(char (&field)[N], std::string_view text) noexcept {
    const std::size_t length = text.size() < N - 1 ? text.size() : N - 1;
    std::memcpy(field, text.data(), length);
    field[length] = '\0';
}

}  // namespace titan
