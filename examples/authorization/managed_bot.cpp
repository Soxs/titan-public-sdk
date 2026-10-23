#include <titan/authorization.h>
#include <chrono>
#include <memory>

class ManagedBotExample final : public titan::Plugin {
    TITAN_PLUGIN("managed_bot_example", "Managed Bot Example")
    struct Bot {
        void start() { ticks = 0; }
        void tick() { ++ticks; }
        void stop() { ticks = 0; }
        int ticks = 0;
    };
public:
    ManagedBotExample()
        : run_(titan::instance(*this, std::make_shared<Bot>())), access_(titan::entitlements(*this)) {
        run_.controls();
        panel("library", "My entitlements", [this](titan::Panel& p) {
            p.button("Refresh", 1);
            const auto view = access_.snapshot();
            if (view.stale) p.text("Entitlement information is not current.");
            for (const auto& item : view.plugins) p.label(item.name, item.entitled ? "Entitled" : "No access");
        }).onAction([this](int32_t action, const auto&) {
            if (action == 1) pending_ = access_.list();
        });
    }
    void onMainLoop() override {
        // Never block this callback waiting for network activity.
        if (pending_.valid() && pending_.wait_for(std::chrono::seconds(0)) == std::future_status::ready) {
            const auto result = pending_.get();
            if (!result.complete && host() && host()->log) host()->log("Entitlement list is temporarily unavailable.");
            pending_ = {};
        }
    }
private:
    titan::ManagedInstance run_;
    titan::Entitlements access_;
    std::shared_future<titan::authorization::Snapshot> pending_;
};

TITAN_REGISTER_PLUGIN(ManagedBotExample, "managed_bot_example")
