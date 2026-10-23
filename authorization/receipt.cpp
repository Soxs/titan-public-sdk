#ifndef NOMINMAX
#define NOMINMAX
#endif
#include <windows.h>
#include <bcrypt.h>
#include <wincrypt.h>
#include "receipt.h"
#include "public_keys.h"
#include <algorithm>
#include <array>
#include <chrono>
#include <set>

namespace titan::authorization::detail {
namespace {
std::string base64(const unsigned char* bytes, std::size_t size) {
    DWORD length = 0;
    if (!CryptBinaryToStringA(bytes, static_cast<DWORD>(size), CRYPT_STRING_BASE64 | CRYPT_STRING_NOCRLF, nullptr, &length)) return {};
    std::string value(length, '\0');
    if (!CryptBinaryToStringA(bytes, static_cast<DWORD>(size), CRYPT_STRING_BASE64 | CRYPT_STRING_NOCRLF, value.data(), &length)) return {};
    value.resize(length);
    while (!value.empty() && (value.back() == '=' || value.back() == '\0')) value.pop_back();
    std::replace(value.begin(), value.end(), '+', '-'); std::replace(value.begin(), value.end(), '/', '_');
    return value;
}
std::vector<unsigned char> decode(std::string_view input) {
    if (input.empty() || input.size() > 65536 || !std::all_of(input.begin(), input.end(), [](unsigned char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-';
    })) return {};
    std::string value(input); std::replace(value.begin(), value.end(), '-', '+'); std::replace(value.begin(), value.end(), '_', '/');
    while (value.size() % 4) value += '=';
    DWORD length = 0;
    if (!CryptStringToBinaryA(value.data(), static_cast<DWORD>(value.size()), CRYPT_STRING_BASE64, nullptr, &length, nullptr, nullptr)) return {};
    std::vector<unsigned char> out(length);
    if (!CryptStringToBinaryA(value.data(), static_cast<DWORD>(value.size()), CRYPT_STRING_BASE64, out.data(), &length, nullptr, nullptr)) return {};
    out.resize(length);
    if (base64(out.data(), out.size()) != input) return {};
    return out;
}
std::array<unsigned char, 32> digest(std::string_view value) {
    std::array<unsigned char, 32> result{};
    if (BCryptHash(BCRYPT_SHA256_ALG_HANDLE, nullptr, 0,
                   reinterpret_cast<PUCHAR>(const_cast<char*>(value.data())), static_cast<ULONG>(value.size()),
                   result.data(), static_cast<ULONG>(result.size())) < 0) throw std::runtime_error("SHA256 unavailable");
    return result;
}
bool verifySignature(std::string_view pem, std::string_view message, const std::vector<unsigned char>& signature) {
    DWORD size = 0;
    if (!CryptStringToBinaryA(pem.data(), static_cast<DWORD>(pem.size()), CRYPT_STRING_BASE64HEADER, nullptr, &size, nullptr, nullptr)) return false;
    std::vector<unsigned char> der(size);
    if (!CryptStringToBinaryA(pem.data(), static_cast<DWORD>(pem.size()), CRYPT_STRING_BASE64HEADER, der.data(), &size, nullptr, nullptr)) return false;
    CERT_PUBLIC_KEY_INFO* info = nullptr;
    DWORD infoSize = 0;
    if (!CryptDecodeObjectEx(X509_ASN_ENCODING, X509_PUBLIC_KEY_INFO, der.data(), size,
                             CRYPT_DECODE_ALLOC_FLAG, nullptr, &info, &infoSize)) return false;
    BCRYPT_KEY_HANDLE key = nullptr;
    const bool imported = CryptImportPublicKeyInfoEx2(X509_ASN_ENCODING, info, 0, nullptr, &key) != 0;
    LocalFree(info);
    if (!imported) return false;
    const auto hash = digest(message);
    BCRYPT_PKCS1_PADDING_INFO padding{BCRYPT_SHA256_ALGORITHM};
    const auto status = BCryptVerifySignature(key, &padding, const_cast<PUCHAR>(hash.data()), 32,
        const_cast<PUCHAR>(signature.data()), static_cast<ULONG>(signature.size()), BCRYPT_PAD_PKCS1);
    BCryptDestroyKey(key);
    return status >= 0;
}
bool jws(std::string_view value, std::string_view purpose, const std::vector<Key>& keys, Json& claims, std::int64_t now, int ttl) {
    if (value.size() > 65536) return false;
    const auto a = value.find('.'), b = value.find('.', a == value.npos ? 0 : a + 1);
    if (a == value.npos || b == value.npos || value.find('.', b + 1) != value.npos) return false;
    const auto h = decode(value.substr(0, a)), p = decode(value.substr(a + 1, b - a - 1)), s = decode(value.substr(b + 1));
    Json header;
    if (h.empty() || p.empty() || s.empty() || !parseStrict({reinterpret_cast<const char*>(h.data()), h.size()}, header)) return false;
    if (header.value("alg", "") != "RS256" || header.value("typ", "") != "titan-authorization+jwt") return false;
    for (const char* field : {"crit", "jku", "jwk", "x5u", "x5c", "b64"}) if (header.contains(field)) return false;
    const auto kid = header.value("kid", "");
    const auto key = std::find_if(keys.begin(), keys.end(), [&](const auto& k) { return k.id == kid; });
    if (key == keys.end() || !verifySignature(key->pem, value.substr(0, b), s) ||
        !parseStrict({reinterpret_cast<const char*>(p.data()), p.size()}, claims)) return false;
    for (const char* field : {"v", "iat", "nbf", "exp"}) if (!claims.contains(field) || !claims[field].is_number_integer()) return false;
    const auto iat = claims["iat"].get<std::int64_t>(), exp = claims["exp"].get<std::int64_t>(), nbf = claims["nbf"].get<std::int64_t>();
    return claims["v"] == 2 && claims.value("iss", "") == "https://titan.lc" && claims.value("aud", "") == "titan-authorization" &&
        claims.value("purpose", "") == purpose && iat > 0 && iat <= now + 30 && nbf <= iat && nbf <= now + 30 && exp > now && exp > iat && exp - iat <= ttl;
}
}
std::uint64_t monotonicMs() { return GetTickCount64(); }
std::int64_t unixSeconds() { return std::chrono::duration_cast<std::chrono::seconds>(std::chrono::system_clock::now().time_since_epoch()).count(); }
std::string sha256(std::string_view value) {
    constexpr char hex[] = "0123456789abcdef";
    const auto hash = digest(value); std::string out; out.reserve(64);
    for (const auto b : hash) { out += hex[b >> 4]; out += hex[b & 15]; }
    return out;
}
std::string randomChallenge() {
    std::array<unsigned char, 32> bytes{};
    if (BCryptGenRandom(nullptr, bytes.data(), 32, BCRYPT_USE_SYSTEM_PREFERRED_RNG) < 0) throw std::runtime_error("Secure random unavailable");
    return base64(bytes.data(), bytes.size());
}
std::string randomId() { return sha256(randomChallenge()).substr(0, 32); }
bool parseStrict(std::string_view value, Json& out) {
    if (value.empty() || value.size() > 1024 * 1024) return false;
    bool duplicate = false, deep = false; std::vector<std::set<std::string>> objects;
    try {
        out = Json::parse(value, [&](int depth, Json::parse_event_t event, Json& token) {
            if (depth > 32) { deep = true; return false; }
            if (event == Json::parse_event_t::object_start) objects.emplace_back();
            if (event == Json::parse_event_t::object_end && !objects.empty()) objects.pop_back();
            if (event == Json::parse_event_t::key && !objects.empty() && !objects.back().insert(token.get<std::string>()).second) duplicate = true;
            return true;
        });
        return !duplicate && !deep && out.is_object();
    } catch (...) { return false; }
}
Verified verifyReceipt(std::string_view raw, const Expected& expected, const std::vector<Key>& testKeys, std::int64_t now, std::uint64_t tick) {
    Verified result;
    try {
        if (!now) now = unixSeconds(); if (!tick) tick = monotonicMs();
        const auto& keys = testKeys.empty() ? publicKeys() : testKeys;
        Json reply, context, proof;
        if (!parseStrict(raw, reply) || !reply.contains("caller_context") || !reply.contains("proof") || !reply.contains("result_json")) return result;
        const auto cert = reply["caller_context"].get<std::string>();
        const auto sidecar = reply["result_json"].get<std::string>();
        const auto purpose = expected.operation == "check" || expected.operation == "list" ? "plugin_entitlements" : "plugin_run";
        if (!jws(cert, "plugin_runtime_context", keys, context, now, 1800) ||
            !jws(reply["proof"].get<std::string>(), purpose, keys, proof, now, 300)) return result;
        if (context.value("client_instance_id", "") != expected.clientInstanceId ||
            context.value("runtime_plugin_id", "") != expected.pluginId ||
            context.value("runtime_generation", std::uint64_t{0}) != expected.generation ||
            context.value("build_sha256", "") != expected.buildHash ||
            context.value("development", false) != expected.development ||
            proof.value("caller_context_sha256", "") != sha256(cert) ||
            proof.value("result_sha256", "") != sha256(sidecar) ||
            proof.value("request_id", "") != expected.requestId || proof.value("challenge", "") != expected.challenge ||
            proof.value("operation", "") != expected.operation) return result;
        for (const char* field : {"sub", "did", "dv", "caller_plugin_id", "canonical_plugin_id", "client_instance_id", "runtime_generation", "runtime_plugin_id", "build_sha256", "development", "publisher_id", "dependency_ids"})
            if (!context.contains(field) || !proof.contains(field) || context[field] != proof[field]) return result;
        if (!parseStrict(sidecar, result.result)) return result;
        result.issuedAt = proof["iat"].get<std::int64_t>(); result.expiresAt = proof["exp"].get<std::int64_t>();
        if (!expected.runId.empty() && expected.operation == "acquire" && result.result.value("run_id", "") != expected.runId) return result;
        if (expected.requestedAt > tick || !expected.requestedAt) return result;
        const auto duration = static_cast<std::uint64_t>((result.expiresAt - result.issuedAt) * 1000);
        // Never add network time to a grant, and never gain time after wall-clock rollback.
        result.deadline = std::min(expected.requestedAt + duration, tick + static_cast<std::uint64_t>((result.expiresAt - now) * 1000));
        if (tick >= result.deadline) return result;
        result.verified = true; result.error.clear(); return result;
    } catch (...) { return result; }
}
}
