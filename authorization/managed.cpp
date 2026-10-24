#include "titan/authorization.h"
#include "titan/detail/authorization_abi.h"
#include "titan/worker.h"
#include "receipt.h"
#include <atomic>
#include <chrono>
#include <map>
#include <mutex>
#include <set>
#include <thread>

namespace titan::authorization::detail {
#ifdef TITAN_AUTHORIZATION_TESTING
static Verified (*testVerifier)(std::string_view, const Expected&) = nullptr;
void setManagedTestVerifier(Verified (*verifier)(std::string_view, const Expected&)) { testVerifier = verifier; }
static std::atomic_uint64_t testClockOffset{0};
void setManagedTestClockOffset(std::uint64_t milliseconds) { testClockOffset.store(milliseconds); }
#endif
namespace {
std::uint64_t managedNow() {
#ifdef TITAN_AUTHORIZATION_TESTING
    return monotonicMs() + testClockOffset.load();
#else
    return monotonicMs();
#endif
}
Verified verifyManagedReceipt(std::string_view reply, const Expected& expected) {
#ifdef TITAN_AUTHORIZATION_TESTING
    if (testVerifier) return testVerifier(reply, expected);
#endif
    return verifyReceipt(reply, expected);
}
template<class T> std::shared_future<T> ready(T value) {
    std::promise<T> promise; promise.set_value(std::move(value)); return promise.get_future().share();
}
struct Request {
    std::uint64_t ticket = 0;
    Expected expected;
};
class Transport {
public:
    explicit Transport(Plugin& owner) : owner_(owner), pluginId_(owner.id()) {}
    Request send(const std::string& operation, Json payload, const std::string& runId = {}) {
        Request request;
        if (!api_) {
            const auto* host = owner_.host();
            if (!host || !host->getHostService) return request;
            api_ = static_cast<const TransportV1*>(host->getHostService(kServiceId));
            if (!api_ || api_->version != 1 || api_->size < sizeof(TransportV1) || !api_->submit || !api_->poll || !api_->context) { api_ = nullptr; return request; }
        }
        char contextBytes[4096]{};
        if (api_->context(&owner_, contextBytes, sizeof(contextBytes)) == UINT32_MAX) return request;
        Json context;
        if (!parseStrict(contextBytes, context)) return request;
        if (context.value("runtime_plugin_id", "") != pluginId_) return request;
        if (operation != "cancel" && context.contains("session_valid") && context["session_valid"] == false) return request;
        request.expected = {operation, randomId(), randomChallenge(), context.value("client_instance_id", ""),
            pluginId_, runId, context.value("runtime_generation", std::uint64_t{0}), managedNow()};
        request.expected.buildHash = context.value("build_sha256", "");
        request.expected.development = context.value("development", false);
        if (request.expected.buildHash.size() != 64) return {};
        if (request.expected.clientInstanceId.empty() || !request.expected.generation) return {};
        const auto json = Json{{"request_id", request.expected.requestId}, {"challenge", request.expected.challenge}, {"payload", std::move(payload)}}.dump();
        request.ticket = api_->submit(&owner_, operation.c_str(), json.c_str());
        return request;
    }
    std::string poll(const Request& request) {
        if (!api_ || !request.ticket) return R"({"error":"unsupported_host"})";
        std::vector<char> bytes(kMaxReplyBytes);
        const auto size = api_->poll(&owner_, request.ticket, bytes.data(), kMaxReplyBytes);
        if (size == UINT32_MAX) return R"({"error":"transport_unavailable"})";
        return {bytes.data(), size};
    }
    void cancel(const Request& request) { if (api_ && api_->cancel && request.ticket) api_->cancel(&owner_, request.ticket); }
    bool sessionValid() const {
        if (!api_) return true;
        char bytes[4096]{};
        if (api_->context(&owner_, bytes, sizeof(bytes)) == UINT32_MAX) return true;
        Json context;
        return !parseStrict(bytes, context) || !context.contains("session_valid") || context["session_valid"] != false;
    }
private:
    Plugin& owner_;
    const std::string pluginId_;
    const TransportV1* api_ = nullptr;
};
std::optional<std::int64_t> timeValue(const Json& entry, const char* key) {
    const auto it = entry.find(key);
    return it != entry.end() && it->is_number_integer() ? std::optional(it->get<std::int64_t>()) : std::nullopt;
}
std::optional<std::uint32_t> limitValue(const Json& entry, const char* key) {
    const auto it = entry.find(key);
    return it != entry.end() && it->is_number_unsigned() ? std::optional(it->get<std::uint32_t>()) : std::nullopt;
}
std::string identifier(const Json& entry, const char* key) {
    const auto it = entry.find(key);
    if (it == entry.end()) return {};
    return it->is_string() ? it->get<std::string>() : it->is_number_integer() ? it->dump() : std::string{};
}
std::string textValue(const Json& entry, const char* key, const char* fallback = "") {
    const auto it = entry.find(key);
    return it != entry.end() && it->is_string() ? it->get<std::string>() : std::string(fallback);
}
std::string renewalFailure(std::string_view reply) {
    Json value;
    if (!parseStrict(reply, value)) return "invalid_receipt";
    const auto reason = textValue(value, "error");
    if (reason == "runtime_context_unavailable") {
        const auto status = value.find("http_status");
        if (status != value.end() && status->is_number_integer())
            for (const auto code : {401, 403, 429, 503})
                if (*status == code) return "runtime_context_http_" + std::to_string(code);
    }
    // Transport replies are untrusted. Keep diagnostics useful without copying
    // arbitrary server text, credentials, or receipt contents into plugin logs.
    for (const auto* known : {"unsupported_host", "transport_unavailable", "owner_unavailable",
        "request_unavailable", "authentication_changed", "local_deadline_expired", "request_timeout",
        "gateway_queue_timeout", "plugin_authorization_unavailable", "authentication_unavailable",
        "main_loop_stale", "gateway_unavailable", "runtime_context_unavailable", "request_too_large",
        "reauthorization-required", "runtime-build-denied", "runtime-publisher-ambiguous", "runtime-context-mismatch", "runtime-context-revoked",
        "development-denied", "feature-disabled", "plugin-authorization-temporarily-unavailable",
        "plugin-authorization-unavailable", "plugin-tab-not-owned", "invalid_gateway_response", "gateway_response_too_large"})
        if (reason == known) return reason;
    return "invalid_receipt";
}
std::string diagnosticPluginId(const char* value) {
    auto id = std::string(value).substr(0, 96);
    for (auto& character : id)
        if (!((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z') ||
            (character >= '0' && character <= '9') || character == '_' || character == '-' || character == '.')) character = '_';
    return id;
}
Entitlement entitlement(const Json& row) {
    Entitlement out;
    out.key = row.value("key", ""); out.pluginId = identifier(row, "plugin_id"); out.canonicalPluginId = identifier(row, "canonical_plugin_id");
    out.runtimePluginId = textValue(row, "runtime_plugin_id"); out.name = textValue(row, "display_name"); out.accessMode = textValue(row, "access_mode");
    out.entitled = row.value("entitled", false); out.owned = row.value("owned", false); out.runtimeEligible = row.value("runtime_eligible", false);
    out.deliveryEligible = row.value("delivery_eligible", false);
    out.accessExpiresAt = timeValue(row, "access_expires_at"); out.nextChangeAt = timeValue(row, "next_change_at");
    out.includedInstances = limitValue(row, "included_instances"); out.totalInstances = limitValue(row, "total_instances"); out.extraInstances = row.value("extra_instances", 0U);
    if (row.contains("usage") && row["usage"].is_object()) {
        const auto& usage = row["usage"];
        out.activeInstances = usage.value("active", 0U); out.retiringInstances = usage.value("retiring", 0U);
        out.usageObservedAt = usage.value("observed_at", std::int64_t{0});
    }
    if (row.contains("source") && row["source"].is_array()) out.sources = row["source"].get<std::vector<std::string>>();
    if (row.contains("extra_expiries")) for (const auto& extra : row["extra_expiries"])
        out.extraExpiries.push_back({extra.value("quantity", 0U), timeValue(extra, "expires_at")});
    return out;
}
}

class InstanceState final : public titan::detail::ManagedLifecycle {
public:
    InstanceState(Plugin& owner, Bot callbacks) : owner_(owner), transport_(owner), bot_(std::move(callbacks)),
        diagnosticPluginId_(diagnosticPluginId(owner.id())),
        watchdog_([this](std::stop_token stop) { watch(stop); }) {}
    ~InstanceState() override {
        watchdog_.request_stop(); watchdog_.join();
        stop();
    }
    std::shared_future<StartResult> start(std::function<void(std::stop_token)> task = {}) {
        std::lock_guard lock(mutex_);
        if (closed_) return ready(StartResult{false, "owner_unloaded"});
        if (!transport_.sessionValid()) return ready(StartResult{false, "account_changed"});
        if (status_.state == RunState::Starting || status_.active()) return startFuture_;
        if (status_.state == RunState::Stopping || worker_ || joiningWorker_) return ready(StartResult{false, "stopping"});
        try {
            // The plugin is bound before Start; its constructor can create this
            // scope earlier. Never read the owner's backend on the watchdog.
            const auto* host = owner_.host();
            diagnosticLog_ = host ? host->log : nullptr;
            runId_ = randomId(); leaseId_.clear(); permitSequence_ = 0; permitDeadline_ = 0;
            lastPermitObserved_ = 0; lastRenewalFailure_.clear(); lastRenewalOperation_.clear();
            task_ = std::move(task); cancellation_ = std::stop_source{};
            startupEntered_ = false; cleanupPending_ = false; stopped_ = false; cancellationPending_ = false;
            status_.state = RunState::Starting; status_.reason.clear(); status_.validUntil = 0;
            nextAcquire_ = 0;
            startPromise_ = std::make_shared<std::promise<StartResult>>(); startFuture_ = startPromise_->get_future().share();
            acquire();
            if (!request_.ticket) {
                status_.state = RunState::Stopped; status_.reason = transport_.sessionValid() ? "unsupported_host" : "account_changed"; resolveStart(false, status_.reason);
            }
        } catch (...) { status_.state = RunState::Stopped; status_.reason = "startup_failed"; resolveStart(false, status_.reason); }
        return startFuture_.valid() ? startFuture_ : ready(StartResult{false, "unavailable"});
    }
    void stop() override {
        { std::lock_guard lock(mutex_); requestStop("stopped"); }
        cleanup();
    }
    void close() override {
        { std::lock_guard lock(mutex_); closed_ = true; requestStop("owner_unloaded"); }
        cleanup();
    }
    Status status() const {
        std::lock_guard lock(mutex_);
        auto result = status_;
        if (nextAcquire_ && result.state == RunState::Starting) {
            const auto now = managedNow();
            const auto remaining = nextAcquire_ > now ? (nextAcquire_ - now + 999) / 1000 : 0;
            result.reason = "Waiting for an instance. Checking again in " + std::to_string(remaining) + " seconds; Stop cancels.";
        }
        return result;
    }
    Plugin& owner() { std::lock_guard lock(mutex_); if (closed_) throw std::logic_error("Owner is unloaded"); return owner_; }
    void pump() override try {
        bool startup = false;
        {
            std::lock_guard lock(mutex_);
            if (!transport_.sessionValid()) requestStop("account_changed");
            else pollCancellations();
            if (status_.state == RunState::Starting && startingDeadline_ && managedNow() >= startingDeadline_) requestStop("startup_timeout");
            if ((status_.active() || status_.state == RunState::Starting) && permitDeadline_ && managedNow() >= permitDeadline_)
                requestStop("authorization_expired");
            if (status_.state == RunState::Starting || status_.active()) {
                if (nextAcquire_ && managedNow() >= nextAcquire_) {
                    acquire();
                    if (!request_.ticket) requestStop("authorization_unavailable");
                }
                if (request_.ticket) {
                    const auto reply = transport_.poll(request_);
                    if (!reply.empty()) {
                        const auto expected = request_.expected; request_ = {};
                        const auto proof = verifyManagedReceipt(reply, expected);
                        if ((permitDeadline_ && managedNow() >= permitDeadline_) ||
                            (status_.state == RunState::Starting && startingDeadline_ && managedNow() >= startingDeadline_)) {
                            requestStop(permitDeadline_ ? "authorization_expired" : "startup_timeout");
                        } else if (!proof.verified) {
                            if (status_.active()) reconnect(renewalFailure(reply), expected);
                            else requestStop("authorization_unavailable");
                        } else if (proof.result.contains("allowed") && proof.result["allowed"] == false) {
                            if (proof.result.value("run_id", "") == runId_) rememberHold(proof.result);
                            if (!waitForCapacity(proof, expected)) requestStop(textValue(proof.result, "reason", "not_entitled"));
                        } else {
                            Json grant = proof.result;
                            if (expected.operation == "report") {
                                grant = Json{};
                                if (proof.result.value("complete", false) && proof.result.contains("runs"))
                                    for (const auto& entry : proof.result["runs"]) if (entry.value("run_id", "") == runId_) grant = entry;
                            }
                            if (!grant.is_object() || grant.value("run_id", "") != runId_) {
                                if (status_.active()) reconnect("invalid_receipt", expected);
                                else requestStop("invalid_receipt");
                            } else if (grant.value("decision", "") != "allow" || !grant.value("allowed", false)) {
                                rememberHold(grant);
                                requestStop(textValue(grant, "reason", "not_entitled"));
                            } else {
                                const auto expiry = grant.value("valid_until", std::int64_t{0});
                                const auto revision = grant.value("permit_sequence", std::uint64_t{0});
                                const auto lease = grant.value("lease_id", "");
                                const auto now = unixSeconds();
                                if (expiry <= now || expiry > proof.expiresAt || !revision || lease.empty() ||
                                    (permitSequence_ && (revision < permitSequence_ || lease != leaseId_))) {
                                    requestStop("invalid_permit");
                                } else {
                                    auto deadline = std::min(proof.deadline, managedNow() + static_cast<std::uint64_t>((expiry - now) * 1000));
                                    // A retry of the same permit cannot reset its monotonic lifetime.
                                    if (revision == permitSequence_ && permitDeadline_) deadline = std::min(deadline, permitDeadline_);
                                    permitSequence_ = revision; leaseId_ = lease; permitDeadline_ = deadline;
                                    status_.validUntil = expiry; rememberHold(grant);
                                    if (!lastRenewalFailure_.empty()) diagnostic("renewal_recovered", lastRenewalFailure_, expected);
                                    lastRenewalFailure_.clear(); lastRenewalOperation_.clear(); lastPermitObserved_ = managedNow();
                                    status_.reason.clear(); retryAttempt_ = 0;
                                    const auto observed = managedNow();
                                    nextReport_ = observed + std::min<std::uint64_t>(20000, deadline > observed ? std::max<std::uint64_t>(1, (deadline - observed) / 2) : 1);
                                    if (status_.state == RunState::Starting && !startupEntered_) {
                                        startingDeadline_ = observed + 30000;
                                        startupEntered_ = true; callbackActive_ = true; startup = true;
                                    }
                                    else status_.state = RunState::Running;
                                }
                            }
                        }
                    }
                }
                if (status_.active() && request_.ticket && managedNow() - request_.expected.requestedAt >= 15000) {
                    const auto expected = request_.expected;
                    transport_.cancel(request_); request_ = {};
                    reconnect("request_timeout", expected);
                }
                if (status_.active() && !request_.ticket && managedNow() >= nextReport_) report();
            }
        }
        if (startup) {
            bool failed = false;
            try { if (bot_.start) bot_.start(); } catch (...) { failed = true; }
            {
                std::lock_guard lock(mutex_);
                callbackActive_ = false;
                if (failed) requestStop("startup_failed");
                if (!stopped_ && managedNow() < startingDeadline_ && managedNow() < permitDeadline_) {
                    status_.state = RunState::Running;
                    if (task_) {
                        try {
                            taskDone_.store(false);
                            const auto token = cancellation_.get_token();
                            worker_ = std::make_shared<PluginWorker>(owner_, [this, token, task = std::move(task_)] {
                                bool failedTask = false;
                                try { task(token); } catch (...) { failedTask = true; }
                                { std::lock_guard taskLock(mutex_); requestStop(failedTask ? "task_failed" : "completed"); }
                                taskDone_.store(true, std::memory_order_release);
                            });
                        } catch (...) { requestStop("worker_start_failed"); }
                    }
                    if (!stopped_) { resolveStart(true, {}); report(); }
                } else if (!stopped_) requestStop("startup_timeout");
            }
        }
        cleanup();
    } catch (...) {
        {
            std::lock_guard lock(mutex_);
            if (status_.active()) reconnect("invalid_receipt");
            else requestStop("invalid_receipt");
        }
        cleanup();
    }
    void tick() override {
        {
            std::lock_guard lock(mutex_);
            if (!status_.active() || callbackActive_) return;
            if (!transport_.sessionValid()) { requestStop("account_changed"); return; }
            if (managedNow() >= permitDeadline_) { requestStop("authorization_expired"); return; }
            callbackActive_ = true;
        }
        bool failed = false;
        try { if (bot_.tick) bot_.tick(); } catch (...) { failed = true; }
        { std::lock_guard lock(mutex_); callbackActive_ = false; if (failed) requestStop("callback_failed"); }
        cleanup();
    }
private:
    void acquire() {
        nextAcquire_ = 0;
        startingDeadline_ = managedNow() + 30000;
        status_.reason = "Checking instance availability";
        request_ = transport_.send("acquire", {{"run_id", runId_}, {"sequence", ++sequence_}}, runId_);
    }
    bool waitForCapacity(const Verified& proof, const Expected& expected) {
        if (expected.operation != "acquire" || startupEntered_ || stopped_ || status_.state != RunState::Starting ||
            proof.result.value("run_id", "") != runId_ || textValue(proof.result, "code") != "instance-limit") return false;
        const auto retry = timeValue(proof.result, "retry_at");
        if (!retry || *retry <= 0) return false;
        // This is an opportunity to recheck, never authority to run or a promise
        // that another active instance will release its slot at that time.
        const auto seconds = std::clamp<std::int64_t>(*retry - unixSeconds(), 1, 300);
        const auto jitter = std::hash<std::string>{}(runId_ + std::to_string(sequence_)) % 1001;
        nextAcquire_ = managedNow() + static_cast<std::uint64_t>(seconds * 1000) + jitter;
        startingDeadline_ = 0;
        return true;
    }
    void rememberHold(const Json& receipt) {
        const auto held = timeValue(receipt, "held_until");
        const auto expiry = timeValue(receipt, "valid_until");
        // On a denial valid_until describes response freshness, not ownership
        // of capacity. Only an actual grant can supply the expiry fallback.
        status_.releasePendingUntil = std::max(status_.releasePendingUntil,
            std::max(held.value_or(0), receipt.value("allowed", false) ? expiry.value_or(0) : std::int64_t{0}));
    }
    void pollCancellations() {
        for (auto it = cancellations_.begin(); it != cancellations_.end();) {
            const auto reply = transport_.poll(*it);
            if (!reply.empty()) {
                try {
                    const auto proof = verifyManagedReceipt(reply, it->expected);
                    if (proof.verified && proof.result.value("run_id", "") == it->expected.runId) rememberHold(proof.result);
                } catch (...) {}
                it = cancellations_.erase(it);
            } else if (managedNow() - it->expected.requestedAt >= 300000) {
                // The host owns delivery of cancellation; dropping the SDK
                // observer cannot cancel its durable retirement request.
                it = cancellations_.erase(it);
            } else ++it;
        }
    }
    void diagnostic(const char* event, const std::string& reason, const Expected& expected = {}) {
        const auto now = managedNow();
        const auto operation = expected.operation.empty() ? lastRenewalOperation_ : expected.operation;
        const auto elapsed = lastPermitObserved_ && now >= lastPermitObserved_ ? now - lastPermitObserved_ : 0;
        const auto requestElapsed = expected.requestedAt && now >= expected.requestedAt ? now - expected.requestedAt : 0;
        diagnostics_.push_back("[Authorization] plugin=" + diagnosticPluginId_ + " event=" + event + " operation=" +
            (operation.empty() ? "report" : operation) + " reason=" + reason +
            " request_ms=" + std::to_string(requestElapsed) + " elapsed_ms=" + std::to_string(elapsed) +
            " remaining_ms=" + std::to_string(permitDeadline_ > now ? permitDeadline_ - now : 0));
    }
    void flushDiagnostics() {
        std::vector<std::string> messages;
        void (*log)(const char*) = nullptr;
        { std::lock_guard lock(mutex_); messages.swap(diagnostics_); log = diagnosticLog_; }
        // Logging is a host callback. Do not invoke it while holding the scope
        // lock, including when the independent watchdog notices expiry.
        try {
            if (log) for (const auto& message : messages) log(message.c_str());
        } catch (...) {}
    }
    void reconnect(const std::string& reason, const Expected& expected = {}) {
        if (status_.state != RunState::Reconnecting || lastRenewalFailure_ != reason)
            diagnostic("renewal_failed", reason, expected);
        lastRenewalFailure_ = reason;
        lastRenewalOperation_ = expected.operation.empty() ? "report" : expected.operation;
        status_.state = RunState::Reconnecting; status_.reason = "reconnecting: " + reason;
        const auto cap = std::min<std::uint64_t>(15000, 1000ULL << std::min(retryAttempt_++, 4U));
        const auto jitter = std::hash<std::string>{}(runId_ + std::to_string(sequence_));
        nextReport_ = managedNow() + cap / 2 + jitter % (cap / 2 + 1);
    }
    void resolveStart(bool success, const std::string& reason) {
        if (startPromise_) { startPromise_->set_value({success, reason}); startPromise_.reset(); }
    }
    void report() {
        Json run{{"run_id", runId_}, {"lease_id", leaseId_}, {"ack_sequence", permitSequence_}, {"state", "running"}};
        request_ = transport_.send("report", {{"sequence", ++sequence_}, {"complete", true}, {"runs", Json::array({run})}}, runId_);
        const auto now = managedNow();
        nextReport_ = now + 20000;
        if (permitDeadline_ > now) nextReport_ = std::min(nextReport_, now + (permitDeadline_ - now) / 2);
        if (!request_.ticket) reconnect("transport_unavailable", request_.expected);
    }
    void requestStop(const std::string& reason) {
        if (reason == "account_changed") { status_.validUntil = 0; status_.releasePendingUntil = 0; }
        if (stopped_ || status_.state == RunState::Stopped) return;
        if (reason == "authorization_expired") diagnostic("authorization_expired",
            lastRenewalFailure_.empty() ? "renewal_not_observed" : lastRenewalFailure_, request_.expected);
        stopped_ = true; cancellationPending_ = true; status_.state = RunState::Stopping; status_.reason = reason;
        nextAcquire_ = 0; startingDeadline_ = 0;
        transport_.cancel(request_); request_ = {};
        // This queue is host-owned: cancellation survives SDK object teardown.
        try {
            Json cancel{{"run_id", runId_}, {"sequence", ++sequence_}};
            if (!leaseId_.empty()) cancel["lease_id"] = leaseId_;
            auto request = transport_.send("cancel", std::move(cancel), runId_);
            if (request.ticket) cancellations_.push_back(std::move(request));
        } catch (...) {}
        cleanupPending_ = startupEntered_; resolveStart(false, reason);
        if (!cleanupPending_ && !worker_ && !joiningWorker_) status_.state = RunState::Stopped;
    }
    void cleanup() {
        signalCancellation();
        flushDiagnostics();
        bool call = false;
        {
            std::lock_guard lock(mutex_);
            if (cleanupPending_ && !callbackActive_ && !worker_ && !joiningWorker_) { cleanupPending_ = false; callbackActive_ = true; call = true; }
        }
        if (call) {
            try { if (bot_.stop) bot_.stop(); } catch (...) {}
            std::lock_guard lock(mutex_); callbackActive_ = false;
            if (!worker_ && !joiningWorker_) status_.state = RunState::Stopped;
        }
    }
    void signalCancellation() {
        std::optional<std::stop_source> signal;
        {
            std::lock_guard lock(mutex_);
            if (cancellationPending_) { cancellationPending_ = false; signal = cancellation_; }
        }
        // stop_callback can execute developer code: never invoke it under our lock
        // or before closing renewal and queuing the durable cancellation.
        if (signal) signal->request_stop();
    }
    void watch(std::stop_token stop) {
        while (!stop.stop_requested()) {
            std::shared_ptr<PluginWorker> finished;
            {
                std::lock_guard lock(mutex_);
                const auto now = managedNow();
                if (!transport_.sessionValid()) requestStop("account_changed");
                else if (status_.state == RunState::Starting && startingDeadline_ && now >= startingDeadline_) requestStop("startup_timeout");
                else if ((status_.active() || status_.state == RunState::Starting) && permitDeadline_ && now >= permitDeadline_) requestStop("authorization_expired");
                if (worker_ && taskDone_.load(std::memory_order_acquire)) { joiningWorker_ = true; finished = std::move(worker_); }
                if (stopped_ && !callbackActive_ && !cleanupPending_ && !worker_ && !joiningWorker_) status_.state = RunState::Stopped;
            }
            signalCancellation();
            flushDiagnostics();
            if (finished) { finished->join(); std::lock_guard lock(mutex_); joiningWorker_ = false; }
            std::this_thread::sleep_for(std::chrono::milliseconds(50));
        }
    }
    Plugin& owner_; Transport transport_; Bot bot_;
    mutable std::mutex mutex_;
    Status status_; Request request_;
    std::vector<Request> cancellations_;
    std::string runId_, leaseId_;
    const std::string diagnosticPluginId_;
    void (*diagnosticLog_)(const char*) = nullptr;
    std::string lastRenewalFailure_, lastRenewalOperation_;
    std::vector<std::string> diagnostics_;
    std::uint64_t lastPermitObserved_ = 0;
    std::uint64_t sequence_ = 0, permitSequence_ = 0, permitDeadline_ = 0, startingDeadline_ = 0, nextReport_ = 0, nextAcquire_ = 0;
    bool startupEntered_ = false, callbackActive_ = false, stopped_ = true, cleanupPending_ = false, cancellationPending_ = false;
    bool joiningWorker_ = false;
    unsigned retryAttempt_ = 0;
    bool closed_ = false;
    std::function<void(std::stop_token)> task_;
    std::stop_source cancellation_;
    std::shared_ptr<PluginWorker> worker_;
    std::atomic_bool taskDone_{false};
    std::shared_ptr<std::promise<StartResult>> startPromise_;
    std::shared_future<StartResult> startFuture_;
    std::jthread watchdog_;
};

class EntitlementState final : public titan::detail::ManagedLifecycle {
    struct Check {
        Request request;
        std::shared_ptr<std::promise<Result>> promise;
        std::shared_future<Result> future;
    };
public:
    explicit EntitlementState(Plugin& owner) : transport_(owner) {}
    std::shared_future<Result> check(std::string key) {
        std::lock_guard lock(mutex_);
        if (closed_) return ready(Result{Decision::Unavailable, "owner_unloaded"});
        if (!transport_.sessionValid()) { invalidateSession(); return ready(Result{Decision::Unavailable, "account_changed"}); }
        if (const auto it = checks_.find(key); it != checks_.end()) return it->second.future;
        auto promise = std::make_shared<std::promise<Result>>(); auto future = promise->get_future().share();
        try {
            auto request = transport_.send("check", key.empty() ? Json::object() : Json{{"plugin_id", key}});
            if (!request.ticket) { promise->set_value({Decision::Unavailable, "unsupported_host"}); return future; }
            checks_.emplace(std::move(key), Check{std::move(request), promise, future});
        } catch (...) { promise->set_value({Decision::Unavailable, "unavailable"}); }
        return future;
    }
    std::shared_future<Snapshot> list() {
        std::lock_guard lock(mutex_); return listLocked();
    }
    Snapshot snapshot() const {
        std::lock_guard lock(mutex_); auto result = snapshot_;
        if (!transport_.sessionValid()) { result = {}; result.reason = "account_changed"; return result; }
        result.stale = !result.complete || monotonicMs() >= snapshotDeadline_;
        return result;
    }
    std::function<void()> onChanged(std::function<void(const Snapshot&)> callback, std::weak_ptr<EntitlementState> self) {
        std::lock_guard lock(mutex_);
        if (closed_ || !transport_.sessionValid()) return [] {};
        const auto id = ++callbackId_; callbacks_[id] = std::move(callback); nextRefresh_ = 0;
        return [self, id] { if (const auto state = self.lock()) { std::lock_guard guard(state->mutex_); state->callbacks_.erase(id); } };
    }
    void stop() override {
        std::lock_guard lock(mutex_);
        for (auto& [_, check] : checks_) { transport_.cancel(check.request); check.promise->set_value({Decision::Unavailable, "owner_stopped"}); }
        checks_.clear();
        if (listPromise_) { transport_.cancel(listRequest_); auto failed = snapshot_; failed.stale = true; failed.reason = "owner_stopped"; listPromise_->set_value(failed); listPromise_.reset(); }
        callbacks_.clear(); snapshot_.stale = true; snapshotDeadline_ = 0;
    }
    void close() override {
        { std::lock_guard lock(mutex_); closed_ = true; }
        stop();
        std::lock_guard lock(mutex_); snapshot_ = {}; ++cacheRevision_;
    }
    void pump() override {
        std::vector<std::function<void(const Snapshot&)>> notify; Snapshot notification;
        {
            std::lock_guard lock(mutex_);
            if (!transport_.sessionValid()) {
                for (const auto& [_, callback] : callbacks_) notify.push_back(callback);
                invalidateSession(); notification = snapshot_;
            }
            for (auto it = checks_.begin(); it != checks_.end();) {
                const auto raw = transport_.poll(it->second.request);
                if (raw.empty() && monotonicMs() - it->second.request.expected.requestedAt < 30000) { ++it; continue; }
                Result result;
                const auto proof = verifyManagedReceipt(raw, it->second.request.expected);
                try {
                    if (proof.verified) {
                        result.verifiedAt = proof.issuedAt; result.validUntil = proof.expiresAt; result.reason = textValue(proof.result, "reason");
                        if (proof.result.contains("entitlement") && proof.result["entitlement"].is_object()) result.entitlement = entitlement(proof.result["entitlement"]);
                        result.decision = result.reason == "scope-denied" ? Decision::ScopeDenied
                            : result.entitlement ? (result.entitlement->entitled ? Decision::Entitled : Decision::NotEntitled)
                            : result.reason == "not-entitled" ? Decision::NotEntitled : Decision::Unavailable;
                        if (result.decision == Decision::NotEntitled) {
                            // A fresh denial must never leave an old cached approval visible as current.
                            for (auto& entry : snapshot_.plugins)
                                if ((result.entitlement && entry.key == result.entitlement->key) || entry.key == it->first) {
                                    if (result.entitlement) entry = *result.entitlement;
                                    else { entry.entitled = false; entry.runtimeEligible = false; }
                                }
                            snapshotDeadline_ = 0; snapshot_.stale = true; nextRefresh_ = 0; ++cacheRevision_;
                        }
                    } else result.reason = "unavailable";
                } catch (...) { result = {Decision::Unavailable, "invalid_receipt"}; }
                it->second.promise->set_value(std::move(result)); it = checks_.erase(it);
            }
            if (listPromise_) {
                const auto raw = transport_.poll(listRequest_);
                if (!raw.empty() || monotonicMs() - listRequest_.expected.requestedAt >= 30000) {
                    const auto proof = verifyManagedReceipt(raw, listRequest_.expected);
                    bool good = false;
                    try {
                        if (proof.verified && proof.result.value("allowed", false) && listRevision_ == cacheRevision_) {
                            const auto& page = proof.result;
                            const auto id = page.value("snapshot_id", "");
                            if (!id.empty() && (snapshotId_.empty() || id == snapshotId_)) {
                                if (snapshotId_.empty()) { snapshotId_ = id; assembling_.verifiedAt = proof.issuedAt; assembling_.validUntil = proof.expiresAt; assemblingDeadline_ = proof.deadline; }
                                if (assembling_.validUntil == proof.expiresAt) {
                                    assemblingDeadline_ = std::min(assemblingDeadline_, proof.deadline);
                                    if (!page.contains("entries") || !page["entries"].is_array()) throw std::runtime_error("Incomplete entitlement page");
                                    if (page.value("offset", std::uint64_t{UINT64_MAX}) != assembling_.plugins.size()) throw std::runtime_error("Reordered entitlement page");
                                    const auto total = page.at("total_entries").get<std::uint64_t>();
                                    if (expectedTotal_ && *expectedTotal_ != total) throw std::runtime_error("Mixed entitlement snapshot");
                                    expectedTotal_ = total;
                                    for (const auto& row : page["entries"]) {
                                        auto item = entitlement(row);
                                        if (item.key.empty() || !entryKeys_.insert(item.key).second) throw std::runtime_error("Duplicate entitlement entry");
                                        assembling_.plugins.push_back(std::move(item));
                                    }
                                    if (assembling_.plugins.size() > total || total > 10000) throw std::runtime_error("Invalid entitlement total");
                                    if (page.contains("public_client") && page["public_client"].is_object()) {
                                        const auto& client = page["public_client"];
                                        if (client.contains("features")) assembling_.features = client["features"].get<std::vector<std::string>>();
                                        if (client.contains("tier") && client["tier"].is_string()) assembling_.tier = client["tier"].get<std::string>();
                                    }
                                    const auto cursor = page.contains("next_cursor") && page["next_cursor"].is_string() ? page["next_cursor"].get<std::string>() : std::string{};
                                    if (assembling_.plugins.size() <= 10000 && monotonicMs() < assemblingDeadline_) {
                                        if (!cursor.empty() && cursors_.insert(cursor).second) {
                                            listRequest_ = transport_.send("list", {{"cursor", cursor}}); good = listRequest_.ticket != 0;
                                        } else if (cursor.empty() && page.value("complete", false) && assembling_.plugins.size() == total) {
                                            assembling_.complete = true; assembling_.stale = false; snapshot_ = assembling_; snapshotDeadline_ = assemblingDeadline_;
                                            listPromise_->set_value(snapshot_); listPromise_.reset(); notification = snapshot_;
                                            for (const auto& [_, fn] : callbacks_) notify.push_back(fn);
                                            nextRefresh_ = monotonicMs() + 20000; good = true;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (...) {}
                    if (!good && listPromise_) {
                        auto failure = snapshot_; failure.stale = monotonicMs() >= snapshotDeadline_; failure.reason = "unavailable";
                        listPromise_->set_value(failure); listPromise_.reset(); nextRefresh_ = monotonicMs() + 15000;
                    }
                }
            } else if (!callbacks_.empty() && monotonicMs() >= nextRefresh_) listLocked();
            const bool stale = !snapshot_.complete || monotonicMs() >= snapshotDeadline_;
            if (stale != observedStale_) {
                observedStale_ = stale; notification = snapshot_; notification.stale = stale;
                notify.clear(); for (const auto& [_, callback] : callbacks_) notify.push_back(callback);
            }
        }
        for (const auto& callback : notify) { try { callback(notification); } catch (...) {} }
    }
private:
    void invalidateSession() {
        snapshot_ = {}; snapshot_.reason = "account_changed"; snapshotDeadline_ = 0; ++cacheRevision_;
        for (auto& [_, check] : checks_) {
            transport_.cancel(check.request); check.promise->set_value({Decision::Unavailable, "account_changed"});
        }
        checks_.clear(); callbacks_.clear(); assembling_ = {}; observedStale_ = true;
        if (listPromise_) {
            transport_.cancel(listRequest_); listPromise_->set_value(snapshot_); listPromise_.reset();
        }
    }
    std::shared_future<Snapshot> listLocked() {
        if (closed_) { auto closed = Snapshot{}; closed.reason = "owner_unloaded"; return ready(closed); }
        if (!transport_.sessionValid()) { invalidateSession(); return ready(snapshot_); }
        if (listPromise_) return listFuture_;
        listPromise_ = std::make_shared<std::promise<Snapshot>>(); listFuture_ = listPromise_->get_future().share();
        assembling_ = {}; snapshotId_.clear(); cursors_.clear(); entryKeys_.clear(); expectedTotal_.reset(); listRevision_ = cacheRevision_;
        try { listRequest_ = transport_.send("list", Json::object()); } catch (...) { listRequest_ = {}; }
        if (!listRequest_.ticket) { auto failure = snapshot_; failure.reason = "unsupported_host"; failure.stale = true; listPromise_->set_value(failure); listPromise_.reset(); nextRefresh_ = monotonicMs() + 15000; }
        return listFuture_;
    }
    mutable std::mutex mutex_; Transport transport_;
    std::map<std::string, Check> checks_;
    std::map<std::uint64_t, std::function<void(const Snapshot&)>> callbacks_;
    std::uint64_t callbackId_ = 0, nextRefresh_ = 0, snapshotDeadline_ = 0, assemblingDeadline_ = 0;
    Snapshot snapshot_, assembling_; std::string snapshotId_; std::set<std::string> cursors_, entryKeys_;
    std::optional<std::uint64_t> expectedTotal_;
    std::uint64_t cacheRevision_ = 0, listRevision_ = 0;
    bool closed_ = false;
    bool observedStale_ = true;
    Request listRequest_; std::shared_ptr<std::promise<Snapshot>> listPromise_; std::shared_future<Snapshot> listFuture_;
};
}

namespace titan {
ManagedInstance instance(Plugin& owner, authorization::Bot bot) {
    if (!owner._managedInstance) owner._managedInstance = std::make_shared<authorization::detail::InstanceState>(owner, std::move(bot));
    return ManagedInstance(std::static_pointer_cast<authorization::detail::InstanceState>(owner._managedInstance));
}
Entitlements entitlements(Plugin& owner) {
    if (!owner._managedEntitlements) owner._managedEntitlements = std::make_shared<authorization::detail::EntitlementState>(owner);
    return Entitlements(std::static_pointer_cast<authorization::detail::EntitlementState>(owner._managedEntitlements));
}
std::shared_future<authorization::StartResult> ManagedInstance::start() const {
    return state_ ? state_->start() : authorization::detail::ready(authorization::StartResult{false, "unbound"});
}
void ManagedInstance::stop() const { if (state_) state_->stop(); }
authorization::Status ManagedInstance::status() const { return state_ ? state_->status() : authorization::Status{}; }
SidePanel& ManagedInstance::controls(const char* panelId, const char* title) const {
    if (!state_) throw std::logic_error("Cannot add controls to an unbound instance");
    const std::weak_ptr<authorization::detail::InstanceState> weak = state_;
    return state_->owner().panel(panelId, title, [weak](Panel& panel) {
        const auto state = weak.lock(); if (!state) return;
        const auto status = state->status();
        static constexpr const char* names[] = {"Stopped", "Starting", "Running", "Reconnecting", "Stopping"};
        panel.label("Status", names[static_cast<unsigned>(status.state)]);
        if (!status.reason.empty()) panel.text(status.reason);
        panel.beginDisabled(status.state != authorization::RunState::Stopped).button("Start", 1).endDisabled();
        panel.beginDisabled(status.state == authorization::RunState::Stopped).button("Stop", 2).endDisabled();
        const auto remaining = status.releasePendingUntil - authorization::detail::unixSeconds();
        if (status.state == authorization::RunState::Stopped && remaining > 0)
            panel.text("Last confirmed reservation: " + std::to_string(remaining) + " seconds remaining.");
    }).onAction([weak](int32_t action, const auto&) {
        if (const auto state = weak.lock()) { if (action == 1) state->start(); else if (action == 2) state->stop(); }
    });
}
std::shared_future<authorization::StartResult> ManagedInstance::run(std::function<void(std::stop_token)> task) const {
    return state_ ? state_->start(std::move(task)) : authorization::detail::ready(authorization::StartResult{false, "unbound"});
}
std::shared_future<authorization::Result> Entitlements::check(std::string key) const { return state_->check(std::move(key)); }
std::shared_future<authorization::Snapshot> Entitlements::list() const { return state_->list(); }
authorization::Snapshot Entitlements::snapshot() const { return state_->snapshot(); }
std::function<void()> Entitlements::onChanged(std::function<void(const authorization::Snapshot&)> fn) const { return state_->onChanged(std::move(fn), state_); }
}
