/// @file titan/break_handler.h
/// @brief Instance-addressed cross-runtime Break Handler participation API.

#pragma once

#include "plugin.h"

#include <algorithm>
#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

namespace titan {

enum class BreakPhase : uint8_t {
    none = TitanPluginSdk::BREAK_PHASE_NONE,
    prepare = TitanPluginSdk::BREAK_PHASE_PREPARE,
    active = TitanPluginSdk::BREAK_PHASE_ACTIVE,
    resume = TitanPluginSdk::BREAK_PHASE_RESUME,
};

enum class BreakMode : uint8_t {
    afk = TitanPluginSdk::BREAK_MODE_AFK,
    logout = TitanPluginSdk::BREAK_MODE_LOGOUT,
};

enum class BreakReportState : uint8_t {
    none = TitanPluginSdk::BREAK_REPORT_NONE,
    running = TitanPluginSdk::BREAK_REPORT_RUNNING,
    preparing = TitanPluginSdk::BREAK_REPORT_PREPARING,
    safePaused = TitanPluginSdk::BREAK_REPORT_SAFE_PAUSED,
    deferred = TitanPluginSdk::BREAK_REPORT_DEFERRED,
    error = TitanPluginSdk::BREAK_REPORT_ERROR,
};

struct BreakCommand {
    bool available = false;
    uint64_t epoch = 0;
    BreakPhase phase = BreakPhase::none;
    BreakMode mode = BreakMode::afk;
    std::string triggeringOwnerId;

    bool shouldBreak() const noexcept { return phase == BreakPhase::prepare; }
    bool isBreakActive() const noexcept { return phase == BreakPhase::active; }
    bool shouldResume() const noexcept { return phase == BreakPhase::resume; }
    /// Any break phase at all: preparing, active or resuming (SDK 144).
    bool isBreakInProgress() const noexcept { return phase != BreakPhase::none; }
};

struct BreakParticipant {
    uint64_t token = 0;
    uint64_t generation = 0;
    uint64_t activityGeneration = 0;
    uint64_t lastObservedEpoch = 0;
    uint8_t runtimeKind = 0;
    bool configurable = false;
    bool active = false;
    bool enabled = false;
    BreakReportState reportState = BreakReportState::none;
    uint32_t reportCode = 0;
    uint32_t retryAfterMs = 0;
    std::string pluginId;
    std::string displayName;
    std::string reportReason;
    BreakCommand command;
};

/// Static utility matching the legacy instance-oriented Break Handler model.
/// The Plugin reference is used only for the duration of each call. The host
/// validates it against the currently-loaded native instance and stores only
/// stable identity plus a load generation.
class BreakHandler final {
public:
    BreakHandler() = delete;

    /// C++ reserves the word `register`, so only that native operation uses
    /// `registerPlugin`; `unregister` keeps the common cross-SDK spelling.
    static bool registerPlugin(Plugin& plugin, bool configurable = true) {
        auto* backend = detail::backend();
        const char* id = plugin.id();
        if (!backend || !id || !id[0]) return false;

        TitanPluginSdk::BreakRegistrationState registration{};
        registration.structSize = sizeof(registration);
        registration.apiVersion = TitanPluginSdk::kBreakRegistryApiVersion;
        registration.role = configurable
            ? TitanPluginSdk::BREAK_REGISTRATION_OWNS_SCHEDULE
            : TitanPluginSdk::BREAK_REGISTRATION_PARTICIPATES;
        if (!copyIdentity(registration.pluginId, id)) return false;
        const char* name = plugin.name();
        copyText(registration.displayName,
                 name && name[0] ? name : registration.pluginId);
        return backend->breakHandlerRegisterPlugin(
            static_cast<const void*>(&plugin), &registration) != 0;
    }

    static bool start(Plugin& plugin) {
        auto* backend = detail::backend();
        const char* id = plugin.id();
        return backend && id && id[0] &&
            backend->breakHandlerStart(static_cast<const void*>(&plugin), id) != 0;
    }

    static bool stop(Plugin& plugin) {
        auto* backend = detail::backend();
        const char* id = plugin.id();
        return backend && id && id[0] &&
            backend->breakHandlerStop(static_cast<const void*>(&plugin), id) != 0;
    }

    static bool unregister(Plugin& plugin) {
        auto* backend = detail::backend();
        const char* id = plugin.id();
        return backend && id && id[0] &&
            backend->breakHandlerUnregisterPlugin(
                static_cast<const void*>(&plugin), id) != 0;
    }

    /// Compatibility spelling retained for code written against the first
    /// SDK-97 preview.
    static bool unregisterPlugin(Plugin& plugin) {
        return unregister(plugin);
    }

    static BreakCommand poll(Plugin& plugin) {
        BreakCommand result;
        auto* backend = detail::backend();
        const char* id = plugin.id();
        if (!backend || !id || !id[0]) return result;

        TitanPluginSdk::BreakCommandState raw{};
        raw.structSize = sizeof(raw);
        raw.apiVersion = TitanPluginSdk::kBreakRegistryApiVersion;
        if (!backend->breakHandlerPoll(
                static_cast<const void*>(&plugin), id, &raw)) {
            return result;
        }
        return fromAbi(raw, true);
    }

    static bool shouldBreak(Plugin& plugin) { return poll(plugin).shouldBreak(); }
    static bool isBreakActive(Plugin& plugin) { return poll(plugin).isBreakActive(); }
    static bool shouldResume(Plugin& plugin) { return poll(plugin).shouldResume(); }

    /// Read the break in progress without registering (SDK 144): the command
    /// the coordinator has currently published, for any loaded, enabled
    /// plugin. Phase none means no break is in progress. Nothing is recorded,
    /// so this never joins a pause quorum, and a participant still polls
    /// before reporting. Returns an unavailable command when the host refuses
    /// the caller (not loaded, or disabled).
    static BreakCommand observe(Plugin& plugin) {
        BreakCommand result;
        auto* backend = detail::backend();
        const char* id = plugin.id();
        if (!backend || !id || !id[0]) return result;

        TitanPluginSdk::BreakCommandState raw{};
        raw.structSize = sizeof(raw);
        raw.apiVersion = TitanPluginSdk::kBreakRegistryApiVersion;
        if (!backend->breakHandlerObserve(
                static_cast<const void*>(&plugin), id, &raw)) {
            return result;
        }
        return fromAbi(raw, true);
    }

    /// True while any coordinated break is preparing, active or resuming.
    /// Uses observe(), so the plugin need not be registered (SDK 144).
    static bool isBreakInProgress(Plugin& plugin) {
        return observe(plugin).isBreakInProgress();
    }

    static bool preparing(Plugin& plugin) {
        return report(plugin, BreakReportState::preparing, 0, 0, nullptr);
    }

    static bool paused(Plugin& plugin) {
        return report(plugin, BreakReportState::safePaused, 0, 0, nullptr);
    }

    static bool defer(Plugin& plugin, uint32_t retryAfterMs,
                      const char* reason = nullptr) {
        return report(plugin, BreakReportState::deferred, 0, retryAfterMs, reason);
    }

    static bool error(Plugin& plugin, uint32_t code,
                      const char* reason = nullptr) {
        return report(plugin, BreakReportState::error, code, 0, reason);
    }

    static bool running(Plugin& plugin) {
        return report(plugin, BreakReportState::running, 0, 0, nullptr);
    }

    /// Coordinator-only: list all current registrations. The host accepts
    /// this only from the loaded native plugin whose stable id is
    /// `break_handler`.
    static std::vector<BreakParticipant> registrations(Plugin& coordinator) {
        std::vector<BreakParticipant> result;
        auto* backend = detail::backend();
        const char* id = coordinator.id();
        if (!backend || !id || !id[0]) return result;

        const void* instance = static_cast<const void*>(&coordinator);
        uint32_t capacity = backend->breakHandlerCoordinatorSnapshot(
            instance, id, nullptr, 0);
        if (!capacity) return result;

        for (int attempt = 0; attempt < 3; ++attempt) {
            std::vector<TitanPluginSdk::BreakParticipantState> raw(capacity);
            for (auto& item : raw) {
                item.structSize = sizeof(item);
                item.apiVersion = TitanPluginSdk::kBreakRegistryApiVersion;
                item.command.structSize = sizeof(item.command);
                item.command.apiVersion =
                    TitanPluginSdk::kBreakRegistryApiVersion;
            }
            const uint32_t count = backend->breakHandlerCoordinatorSnapshot(
                instance, id, raw.data(), capacity);
            if (count > capacity) {
                capacity = count;
                continue;
            }
            result.reserve(count);
            for (uint32_t i = 0; i < count; ++i) result.push_back(fromAbi(raw[i]));
            return result;
        }
        return result;
    }

    static bool publish(Plugin& coordinator, const BreakCommand& command) {
        auto* backend = detail::backend();
        const char* id = coordinator.id();
        if (!backend || !id || !id[0] || command.epoch == 0 ||
            command.phase == BreakPhase::none) {
            return false;
        }
        const auto raw = toAbi(command);
        return backend->breakHandlerCoordinatorPublish(
            static_cast<const void*>(&coordinator), id, &raw) != 0;
    }

    static bool clear(Plugin& coordinator, uint64_t expectedEpoch) {
        auto* backend = detail::backend();
        const char* id = coordinator.id();
        return backend && id && id[0] && expectedEpoch != 0 &&
            backend->breakHandlerCoordinatorClear(
                static_cast<const void*>(&coordinator), id, expectedEpoch) != 0;
    }

private:
    static bool report(Plugin& plugin, BreakReportState state,
                       uint32_t code, uint32_t retryAfterMs,
                       const char* reason) {
        auto* backend = detail::backend();
        const char* id = plugin.id();
        if (!backend || !id || !id[0]) return false;

        TitanPluginSdk::BreakReportState report{};
        report.structSize = sizeof(report);
        report.apiVersion = TitanPluginSdk::kBreakRegistryApiVersion;
        // The host binds epoch zero to this registration's exact last
        // poll-observed epoch, then compares it with the current command.
        report.epoch = 0;
        report.state = static_cast<uint8_t>(state);
        report.code = code;
        report.retryAfterMs = retryAfterMs;
        if (!copyIdentity(report.pluginId, id)) return false;
        copyText(report.reason, reason ? reason : "");
        return backend->breakHandlerReport(
            static_cast<const void*>(&plugin), &report) != 0;
    }

    template <size_t N>
    static bool copyIdentity(char (&out)[N], const char* text) {
        static_assert(N > 1, "identity buffer must include a terminator");
        if (!text || !text[0]) return false;
        size_t count = 0;
        while (count < N && text[count] != '\0') ++count;
        if (count == 0 || count >= N) return false;
        std::memcpy(out, text, count);
        out[count] = '\0';
        return true;
    }

    template <size_t N>
    static void copyText(char (&out)[N], const char* text) {
        static_assert(N > 0, "text buffer must include a terminator");
        size_t count = 0;
        if (text) {
            while (count < N - 1 && text[count] != '\0') ++count;
            if (count) std::memcpy(out, text, count);
        }
        out[count] = '\0';
    }

    static BreakCommand fromAbi(const TitanPluginSdk::BreakCommandState& raw,
                                bool available) {
        BreakCommand out;
        out.available = available;
        out.epoch = raw.epoch;
        out.phase = static_cast<BreakPhase>(raw.phase);
        out.mode = static_cast<BreakMode>(raw.mode);
        out.triggeringOwnerId = raw.triggeringOwnerId;
        return out;
    }

    static BreakParticipant fromAbi(
            const TitanPluginSdk::BreakParticipantState& raw) {
        BreakParticipant out;
        out.token = raw.token;
        out.generation = raw.generation;
        out.activityGeneration = raw.activityGeneration;
        out.lastObservedEpoch = raw.lastObservedEpoch;
        out.runtimeKind = raw.runtimeKind;
        out.configurable = raw.configurable != 0;
        out.active = raw.active != 0;
        out.enabled = raw.enabled != 0;
        out.reportState = static_cast<BreakReportState>(raw.reportState);
        out.reportCode = raw.reportCode;
        out.retryAfterMs = raw.retryAfterMs;
        out.pluginId = raw.pluginId;
        out.displayName = raw.displayName;
        out.reportReason = raw.reportReason;
        out.command = fromAbi(raw.command, true);
        return out;
    }

    static TitanPluginSdk::BreakCommandState toAbi(const BreakCommand& command) {
        TitanPluginSdk::BreakCommandState raw{};
        raw.structSize = sizeof(raw);
        raw.apiVersion = TitanPluginSdk::kBreakRegistryApiVersion;
        raw.epoch = command.epoch;
        raw.phase = static_cast<uint8_t>(command.phase);
        raw.mode = static_cast<uint8_t>(command.mode);
        const size_t count = (std::min)(
            command.triggeringOwnerId.size(),
            static_cast<size_t>(TitanPluginSdk::kBreakPluginIdCapacity - 1));
        if (count) {
            std::memcpy(raw.triggeringOwnerId,
                        command.triggeringOwnerId.data(), count);
        }
        raw.triggeringOwnerId[count] = '\0';
        return raw;
    }
};

} // namespace titan
