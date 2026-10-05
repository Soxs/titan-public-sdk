/// @file titan/utils/task_framework.h
/// @brief Plugin-side priority-task-dispatch framework with tick cooldowns,
///        conditional waits, and random AFK delays.
///
/// Header-only inline implementation. Composes over existing SDK primitives
/// (`titan::state::client()`, `titan::ChatMessageEvent`, etc.) -- no new ABI
/// surface, no IBackend virtuals.
///
/// Port of the ThePlug `AbstractWorker` / `Task` / `TaskSet` Java framework,
/// adapted for C++ idioms with improved naming and RAII lifecycle.
///
/// Usage:
/// @code
///   #include <titan/utils/task_framework.h>
///
///   class ChopTask : public titan::utils::Task {
///       const char* name() const override { return "Chopping"; }
///       bool validate() override { return treeIsNearby(); }
///       void onGameTick() override { chopNearestTree(); }
///   };
///
///   // In your plugin class:
///   titan::utils::TaskWorker worker_;
///
///   void onEnable() override {
///       worker_.addTask<ChopTask>();
///       worker_.addTask<BankTask>();
///       worker_.start();
///   }
///   void onGameTick(int32_t tick) override { worker_.onGameTick(); }
/// @endcode

#pragma once

#include "../client.h"
#include "../events.h"

#include <chrono>
#include <cstdint>
#include <cstdio>
#include <functional>
#include <memory>
#include <random>
#include <string>
#include <vector>

namespace titan {
namespace utils {

class TaskWorker;

// ---------------------------------------------------------------------------
// Task -- one unit of prioritised work
// ---------------------------------------------------------------------------

/// Base class for a single prioritised task. Override `validate()` to declare
/// when this task is relevant, and `onTick()` to perform one game tick of
/// work. Tasks are checked in registration order (first valid wins).
class Task {
public:
    virtual ~Task() = default;

    /// Short human-readable name shown in status overlays.
    virtual const char* name() const = 0;

    /// Return true when this task should be the active task this tick.
    /// The first task in the list whose `validate()` returns true wins.
    virtual bool validate() = 0;

    /// Perform one game tick of work. Only called when this task is the
    /// valid task AND no wait/cooldown is active.
    virtual void onGameTick() {}

    /// Called every game tick for the valid task, even when a wait or
    /// cooldown is suppressing `onGameTick()`. Use for passive monitoring
    /// (e.g. tracking animation state, counting drops).
    virtual void onGameTickAlways() {}

    /// Called on the valid task each client frame (higher frequency than
    /// game ticks). Default no-op.
    virtual void onClientTick() {}

    /// Called on the valid task for every chat message. Default no-op.
    virtual void onChatMessage(const titan::ChatMessageEvent& /*event*/) {}

    /// Called on the valid task for every applied hitsplat. Default no-op.
    virtual void onHitsplatApplied(const titan::HitsplatAppliedEvent& /*event*/) {}

    /// Called on the valid task for every applied actor spotanim. Default no-op.
    virtual void onActorSpotAnim(const titan::ActorSpotAnimEvent& /*event*/) {}

    /// Called on the valid task for every accepted utterance. Default no-op.
    virtual void onOverheadTextChanged(const titan::OverheadTextChangedEvent& /*event*/) {}
    /// Called on the valid task for accepted actor animation changes.
    virtual void onAnimationChanged(const titan::AnimationChangedEvent& /*event*/) {}

    /// When true (the default), the worker may inject random AFK pauses
    /// before this task executes. Return false for latency-sensitive tasks
    /// (e.g. prayer flicking, eating).
    virtual bool isAfkable() const { return true; }

    /// Non-owning back-pointer to the worker driving this task. Set
    /// automatically by `TaskWorker::addTask`. Tasks use this to call
    /// `worker()->sleep(...)`, `worker()->waitUntil(...)`, etc.
    TaskWorker* worker() const { return worker_; }

private:
    friend class TaskWorker;
    TaskWorker* worker_ = nullptr;
};

// ---------------------------------------------------------------------------
// TaskWorker -- orchestrates tasks with waits, cooldowns, and AFK delays
// ---------------------------------------------------------------------------

/// Manages a priority-ordered list of tasks and drives the per-tick dispatch
/// loop including conditional waits (`waitUntil`), raw tick cooldowns
/// (`sleep`), and optional random AFK delays.
///
/// The dispatch order each game tick is:
/// 1. Find the highest-priority valid task.
/// 2. Call `onGameTickAlways()` on it (runs even during waits/cooldowns).
/// 3. If a `waitUntil` predicate is active: test it, decrement expiry,
///    skip `onGameTick()` if still waiting.
/// 4. If a tick cooldown is active: decrement, skip `onGameTick()`.
/// 5. Optionally roll a random AFK delay (if the task is afkable).
/// 6. Call `onGameTick()` on the valid task.
class TaskWorker {
public:
    // --- Task registration (priority = insertion order) -------------------

    /// Construct a task in-place and append it to the priority list.
    /// Returns a reference to the constructed task so callers can store
    /// a typed pointer if they need to configure it later.
    template <typename T, typename... Args>
    T& addTask(Args&&... args) {
        auto ptr = std::make_unique<T>(std::forward<Args>(args)...);
        T& ref = *ptr;
        ref.worker_ = this;
        tasks_.push_back(std::move(ptr));
        return ref;
    }

    /// Remove all registered tasks.
    void clearTasks() { tasks_.clear(); }

    /// Number of registered tasks.
    int taskCount() const { return static_cast<int>(tasks_.size()); }

    // --- Lifecycle --------------------------------------------------------

    /// Start the worker. Resets all internal state (timers, waits,
    /// cooldowns) and begins dispatching on subsequent `onGameTick` calls.
    void start() {
        running_ = true;
        startTime_ = std::chrono::steady_clock::now();
        clearWait();
        sleepTicks_ = 0;
        afkActive_ = false;
        setStatus("Started");
        setSleepStatus("");
    }

    /// Stop the worker. `onGameTick` becomes a no-op until `start()`.
    void stop() {
        running_ = false;
        clearWait();
        sleepTicks_ = 0;
        afkActive_ = false;
        setStatus("Stopped");
        setSleepStatus("");
    }

    bool isRunning() const { return running_; }

    // --- Primary dispatch (call from plugin overrides) --------------------

    /// Drive one game tick of the task framework. Call this from your
    /// plugin's `onGameTick` override.
    void onGameTick() {
        if (!running_) return;

        Task* task = findValidTask();
        if (!task) {
            setStatus("No valid task");
            setSleepStatus("");
            return;
        }

        setStatus(task->name());
        setSleepStatus("");

        // Always-tick runs unconditionally for the valid task.
        task->onGameTickAlways();

        // --- waitUntil gate ---
        if (waitExpiry_ > 0) {
            if (waitPredicate_ && waitPredicate_()) {
                clearWait();
            } else {
                char buf[160];
                if (waitInfo_[0]) {
                    std::snprintf(buf, sizeof(buf), "[Waiting: %s %d/%d]",
                                  waitInfo_, waitExpiry_, waitExpiryOriginal_);
                } else {
                    std::snprintf(buf, sizeof(buf), "[Waiting %d/%d]",
                                  waitExpiry_, waitExpiryOriginal_);
                }
                setSleepStatus(buf);
                --waitExpiry_;
                if (waitExpiry_ == 0) waitBlocking_ = false;
                return;
            }
        }

        // --- Random AFK delay injection ---
        if (task->isAfkable() && sleepTicks_ == 0 &&
            afkFreqPercent_ > 0 && afkMaxTicks_ > 0) {
            if (randInt(0, 100) < afkFreqPercent_) {
                afkActive_ = true;
                sleepTicks_ = randInt(afkMinTicks_, afkMaxTicks_);
                return;
            }
        }

        // --- Tick cooldown gate ---
        if (sleepTicks_ > 0) {
            char buf[160];
            if (afkActive_) {
                std::snprintf(buf, sizeof(buf), "AFK delay: %d", sleepTicks_);
            } else {
                std::snprintf(buf, sizeof(buf), "[Sleep: %d]", sleepTicks_);
            }
            setSleepStatus(buf);
            --sleepTicks_;
            if (sleepTicks_ == 0) afkActive_ = false;
            return;
        }

        // --- Execute ---
        task->onGameTick();
    }

    /// Forward client-tick events to the current valid task.
    void onClientTick() {
        if (!running_) return;
        if (Task* t = findValidTask()) t->onClientTick();
    }

    /// Forward chat messages to the current valid task.
    void onChatMessage(const titan::ChatMessageEvent& event) {
        if (!running_) return;
        if (Task* t = findValidTask()) t->onChatMessage(event);
    }

    /// Forward applied hitsplats to the current valid task.
    void onHitsplatApplied(const titan::HitsplatAppliedEvent& event) {
        if (!running_) return;
        if (Task* t = findValidTask()) t->onHitsplatApplied(event);
    }

    /// Forward actor spotanims to the current valid task.
    void onActorSpotAnim(const titan::ActorSpotAnimEvent& event) {
        if (!running_) return;
        if (Task* t = findValidTask()) t->onActorSpotAnim(event);
    }

    /// Forward each accepted utterance to the current valid task.
    void onOverheadTextChanged(const titan::OverheadTextChangedEvent& event) {
        if (!running_) return;
        if (Task* t = findValidTask()) t->onOverheadTextChanged(event);
    }
    /// Forward actor animation changes to the current valid task.
    void onAnimationChanged(const titan::AnimationChangedEvent& event) {
        if (!running_) return;
        if (Task* t = findValidTask()) t->onAnimationChanged(event);
    }

    // --- waitUntil --------------------------------------------------------
    //
    // Pause task execution for up to `expiryTicks` game ticks, or until
    // `predicate` returns true (whichever comes first). While waiting,
    // `onGameTickAlways()` still fires but `onGameTick()` is suppressed.

    /// Wait until `predicate` is true, or `expiryTicks` have elapsed.
    void waitUntil(std::function<bool()> predicate, int expiryTicks) {
        setWait(std::move(predicate), expiryTicks, false);
    }

    /// Wait with a randomised expiry in `[minTicks, maxTicks]`.
    void waitUntil(std::function<bool()> predicate, int minTicks, int maxTicks) {
        setWait(std::move(predicate), randInt(minTicks, maxTicks), false);
    }

    /// Blocking wait -- prevents subsequent `waitUntil` calls from
    /// replacing this one until it expires or the predicate fires.
    void waitUntilBlocking(std::function<bool()> predicate, int expiryTicks) {
        setWait(std::move(predicate), expiryTicks, true);
    }

    /// Blocking wait with randomised expiry.
    void waitUntilBlocking(std::function<bool()> predicate, int minTicks, int maxTicks) {
        setWait(std::move(predicate), randInt(minTicks, maxTicks), true);
    }

    /// Annotate the current wait for overlay display (e.g. "animation idle").
    void setWaitInfo(const char* info) {
        if (info) {
            std::snprintf(waitInfo_, sizeof(waitInfo_), "%s", info);
        } else {
            waitInfo_[0] = '\0';
        }
    }

    /// True when a `waitUntil` is currently active.
    bool isWaiting() const { return waitExpiry_ > 0; }

    /// Cancel any active wait.
    void clearWait() {
        waitPredicate_ = nullptr;
        waitExpiry_ = 0;
        waitExpiryOriginal_ = 0;
        waitBlocking_ = false;
        waitInfo_[0] = '\0';
    }

    // --- sleep (raw tick cooldown) ----------------------------------------
    //
    // Suppress `onGameTick()` for exactly N ticks. Simpler than `waitUntil`
    // when there is no early-exit condition.

    /// Sleep for exactly `ticks` game ticks.
    void sleep(int ticks) {
        sleepTicks_ = ticks;
        afkActive_ = false;
    }

    /// Sleep for a random number of ticks in `[minTicks, maxTicks]`.
    void sleep(int minTicks, int maxTicks) {
        sleepTicks_ = randInt(minTicks, maxTicks);
        afkActive_ = false;
    }

    /// Ticks remaining on the current sleep cooldown.
    int sleepRemaining() const { return sleepTicks_; }

    // --- Random AFK delay configuration -----------------------------------
    //
    // When enabled, the worker rolls a random chance each tick (for afkable
    // tasks only) and may inject an extra idle pause. Disabled by default.

    /// Configure the random AFK delay system.
    /// @param minTicks   Minimum AFK pause length when triggered.
    /// @param maxTicks   Maximum AFK pause length.
    /// @param freqPercent  Chance per tick (0-100) to trigger. 0 = disabled.
    void configureAfkDelay(int minTicks, int maxTicks, int freqPercent) {
        afkMinTicks_ = minTicks;
        afkMaxTicks_ = maxTicks;
        afkFreqPercent_ = freqPercent;
    }

    // --- Status for overlays ----------------------------------------------

    /// Current task name / status string.
    const char* status() const { return status_; }

    /// Sleep/wait annotation string (empty when not sleeping).
    const char* sleepStatus() const { return sleepStatus_; }

    /// Name of the currently valid task, or "None".
    const char* activeTaskName() const {
        if (Task* t = const_cast<TaskWorker*>(this)->findValidTask())
            return t->name();
        return "None";
    }

    /// Seconds elapsed since `start()`.
    double elapsedSeconds() const {
        auto now = std::chrono::steady_clock::now();
        return std::chrono::duration<double>(now - startTime_).count();
    }

    /// Formatted elapsed time string ("HH:MM:SS" or "MM:SS").
    std::string elapsedFormatted() const {
        int total = static_cast<int>(elapsedSeconds());
        int hrs = total / 3600;
        int mins = (total % 3600) / 60;
        int secs = total % 60;
        char buf[16];
        if (hrs > 0)
            std::snprintf(buf, sizeof(buf), "%02d:%02d:%02d", hrs, mins, secs);
        else
            std::snprintf(buf, sizeof(buf), "%02d:%02d", mins, secs);
        return buf;
    }

    // --- Utility ----------------------------------------------------------

    /// Uniform random integer in `[min, max]` (inclusive).
    static int randInt(int min, int max) {
        if (max <= min) return min;
        thread_local std::mt19937 rng{std::random_device{}()};
        std::uniform_int_distribution<int> dist(min, max);
        return dist(rng);
    }

private:
    std::vector<std::unique_ptr<Task>> tasks_;
    bool running_ = false;
    std::chrono::steady_clock::time_point startTime_;

    // waitUntil state
    std::function<bool()> waitPredicate_;
    int waitExpiry_ = 0;
    int waitExpiryOriginal_ = 0;
    bool waitBlocking_ = false;
    char waitInfo_[128] = {};

    // sleep state
    int sleepTicks_ = 0;
    bool afkActive_ = false;

    // AFK delay config
    int afkMinTicks_ = 0;
    int afkMaxTicks_ = 0;
    int afkFreqPercent_ = 0;

    // status
    char status_[160] = "Idle";
    char sleepStatus_[160] = {};

    /// Priority scan: return the first task whose `validate()` is true.
    Task* findValidTask() {
        for (auto& t : tasks_) {
            if (t->validate()) return t.get();
        }
        return nullptr;
    }

    void setStatus(const char* s) {
        std::snprintf(status_, sizeof(status_), "%s", s ? s : "");
    }

    void setSleepStatus(const char* s) {
        std::snprintf(sleepStatus_, sizeof(sleepStatus_), "%s", s ? s : "");
    }

    void setWait(std::function<bool()> predicate, int expiry, bool blocking) {
        if (!blocking && waitBlocking_ && waitExpiry_ > 0) return;
        waitPredicate_ = std::move(predicate);
        waitExpiry_ = expiry;
        waitExpiryOriginal_ = expiry;
        waitBlocking_ = blocking;
        waitInfo_[0] = '\0';
    }
};

}  // namespace utils
}  // namespace titan
