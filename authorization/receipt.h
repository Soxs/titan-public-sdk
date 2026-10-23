#pragma once
#include "third_party/json.hpp"
#include <cstdint>
#include <string>
#include <string_view>
#include <vector>

namespace titan::authorization::detail {
using Json = nlohmann::json;
struct Key { std::string_view id, pem; };
struct Expected {
    std::string operation, requestId, challenge, clientInstanceId, pluginId, runId;
    std::uint64_t generation = 0, requestedAt = 0;
    std::string buildHash;
    bool development = false;
};
struct Verified {
    bool verified = false;
    Json result;
    std::int64_t issuedAt = 0, expiresAt = 0;
    std::uint64_t deadline = 0;
    std::string error = "invalid_receipt";
};
std::uint64_t monotonicMs();
std::int64_t unixSeconds();
std::string randomId();
std::string randomChallenge();
bool parseStrict(std::string_view value, Json& out);
std::string sha256(std::string_view value);
Verified verifyReceipt(std::string_view reply, const Expected& expected,
                       const std::vector<Key>& testKeys = {}, std::int64_t now = 0,
                       std::uint64_t tick = 0);
}
