#pragma once
#include "plugin.h"
#include "detail/diagnostics_abi.h"
#include <span>
#include <string>
#include <string_view>

namespace titan::diagnostics {
// Construct after the SDK binds the plugin (for example onEnable). This handle can then
// be used from its worker threads. No network request or thread starts during construction.
// Upload only recordings covered by the user's consent. Cancel/release pending work on
// opt-out; disabling/unloading the owner also cancels it. Credentials stay inside Titan.
class Client {
public:
    explicit Client(Plugin& owner) : owner_(&owner) {
        auto* host = owner.host();
        auto* service = host && host->getHostService ? static_cast<const detail::TransportV1*>(
            host->getHostService(detail::kServiceId)) : nullptr;
        if (service && service->version == 1 && service->size >= sizeof(detail::TransportV1)
                && service->submit && service->poll && service->cancel && service->release) service_ = service;
    }
    bool available() const { return service_ != nullptr; }
    std::uint64_t submit(std::string_view name, std::string_view sha256,
                         std::span<const std::uint8_t> gzip) const {
        if (!service_ || name.size() != 70 || sha256.size() != 64 || gzip.empty()
                || gzip.size() > detail::kMaxGzipBytes || name.find('\0') != name.npos
                || sha256.find('\0') != sha256.npos) return 0;
        const std::string ownedName(name), ownedHash(sha256);
        return service_->submit(owner_, ownedName.c_str(), ownedHash.c_str(), gzip.data(),
                                 static_cast<std::uint32_t>(gzip.size()));
    }
    Status poll(std::uint64_t ticket) const {
        if (!service_ || !ticket) return Status::Unavailable;
        const auto status = service_->poll(owner_, ticket);
        return status <= static_cast<std::uint32_t>(Status::Cancelled)
            ? static_cast<Status>(status) : Status::Unavailable;
    }
    void cancel(std::uint64_t ticket) const { if (service_ && ticket) service_->cancel(owner_, ticket); }
    void release(std::uint64_t ticket) const { if (service_ && ticket) service_->release(owner_, ticket); }
private:
    const void* owner_;
    const detail::TransportV1* service_ = nullptr;
};
} // namespace titan::diagnostics
