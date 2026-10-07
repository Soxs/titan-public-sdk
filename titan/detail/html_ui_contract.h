#pragma once

// Owned, dependency-free HTML UI contract shared by SDK and runtime adapters.
// None of these STL types cross the native DLL boundary (see html_ui_abi.h).
#include <algorithm>
#include <charconv>
#include <cmath>
#include <cstdint>
#include <deque>
#include <limits>
#include <mutex>
#include <string>
#include <string_view>
#include <unordered_set>
#include <utility>
#include <vector>

namespace TitanHtmlUi {
inline constexpr uint32_t kVersion = 1;
inline constexpr size_t kMaxResources = 128;
inline constexpr size_t kMaxResourceBytes = 2 * 1024 * 1024;
inline constexpr size_t kMaxBundleBytes = 8 * 1024 * 1024;
inline constexpr size_t kMaxJsonBytes = 64 * 1024;
inline constexpr size_t kMaxIconBytes = 256 * 1024;
inline constexpr size_t kMaxQueueMessages = 64;
inline constexpr size_t kMaxQueueBytes = 1024 * 1024;
inline constexpr size_t kMaxSidePanels = 8;
inline constexpr size_t kMaxHtmlOverlays = 8;
enum class SurfaceKind : uint8_t { SidePanel = 1, Overlay = 2 };
enum Input : uint8_t { ClickThrough = 0, Buttons = 1, Scroll = 2, Text = 4 };
struct Resource { std::string path, mime; std::vector<uint8_t> bytes; };
struct Bundle { uint32_t version = kVersion; std::string entrypoint; std::vector<Resource> resources; };
struct SurfaceDescriptor {
    SurfaceKind kind = SurfaceKind::SidePanel;
    std::string id, title, icon;
    uint32_t iconColor = 0;
    std::vector<uint8_t> iconPng;
    uint8_t anchor = 0, input = ClickThrough;
    int32_t priority = 50;
    uint32_t width = 220, height = 160;
    bool visible = true;
};

inline bool validUtf8(std::string_view s) noexcept {
    for (size_t i = 0; i < s.size();) {
        const auto c = static_cast<uint8_t>(s[i++]);
        if (c < 0x80) continue;
        uint32_t v = 0, minimum = 0; size_t n = 0;
        if ((c & 0xe0) == 0xc0) { v = c & 31; n = 1; minimum = 0x80; }
        else if ((c & 0xf0) == 0xe0) { v = c & 15; n = 2; minimum = 0x800; }
        else if ((c & 0xf8) == 0xf0) { v = c & 7; n = 3; minimum = 0x10000; }
        else return false;
        if (n > s.size() - i) return false;
        while (n--) { const auto b = static_cast<uint8_t>(s[i++]); if ((b & 0xc0) != 0x80) return false; v = (v << 6) | (b & 63); }
        if (v < minimum || v > 0x10ffff || (v >= 0xd800 && v <= 0xdfff)) return false;
    }
    return true;
}
inline bool validId(std::string_view id) noexcept {
    if (id.empty() || id.size() > 31) return false;
    for (char c : id) if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
        (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '.')) return false;
    return true;
}
// Deliberately no URL decoding: '%' and URL syntax are not bundle path syntax.
// This makes every allowed path have exactly one spelling and interpretation.
inline bool validResourcePath(std::string_view p) noexcept {
    if (p.empty() || p.size() > 255 || p.front() == '/' || p.back() == '/') return false;
    size_t start = 0;
    for (size_t i = 0; i <= p.size(); ++i) {
        if (i == p.size() || p[i] == '/') {
            auto segment = p.substr(start, i - start);
            if (segment.empty() || segment == "." || segment == "..") return false;
            start = i + 1;
        } else {
            const char c = p[i];
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.')) return false;
        }
    }
    return true;
}
inline bool textMime(std::string_view m) noexcept {
    return m == "text/html" || m == "text/css" || m == "text/javascript" ||
        m == "application/javascript" || m == "application/json" || m == "text/plain";
}
inline bool supportedMime(std::string_view m) noexcept {
    return textMime(m) || m == "image/png" || m == "image/jpeg" ||
        m == "image/webp" || m == "font/woff" || m == "font/woff2";
}
inline bool validateBundle(const Bundle& b, std::string& error) {
    auto fail = [&](const char* reason) { error = reason; return false; };
    error.clear();
    if (b.version != kVersion) return fail("unsupported HTML bundle version");
    if (!validResourcePath(b.entrypoint)) return fail("entrypoint must be a normalized relative resource path");
    if (b.resources.empty() || b.resources.size() > kMaxResources) return fail("HTML bundle must contain 1..128 resources");
    size_t total = 0; bool found = false; std::unordered_set<std::string> paths;
    for (const auto& r : b.resources) {
        if (!validResourcePath(r.path)) return fail("invalid resource path (absolute, traversal, percent, backslash or URL syntax)");
        auto folded = r.path;
        for (auto& c : folded) if (c >= 'A' && c <= 'Z') c = static_cast<char>(c + ('a' - 'A'));
        if (!paths.insert(std::move(folded)).second) return fail("duplicate or case-colliding resource path");
        if (!supportedMime(r.mime)) return fail("unsupported resource MIME type");
        if (r.bytes.size() > kMaxResourceBytes) return fail("resource exceeds 2 MiB");
        if (r.bytes.size() > kMaxBundleBytes - total) return fail("bundle exceeds 8 MiB");
        total += r.bytes.size();
        if (textMime(r.mime) && !validUtf8(std::string_view(reinterpret_cast<const char*>(r.bytes.data()), r.bytes.size())))
            return fail("text resource is not valid UTF-8");
        if (r.path == b.entrypoint) { if (r.mime != "text/html" || r.bytes.empty()) return fail("entrypoint must be a nonempty text/html resource"); found = true; }
    }
    return found || fail("entrypoint resource is missing");
}

namespace detail {
inline bool finiteJsonNumber(std::string_view token) {
    double value = 0;
    const auto parsed = std::from_chars(token.data(), token.data() + token.size(), value);
    if (parsed.ec == std::errc{}) return std::isfinite(value);
    if (parsed.ec != std::errc::result_out_of_range) return false;
    // from_chars reports both overflow and underflow as out_of_range. JSON
    // runtimes accept tiny underflows as zero. Determine the decimal order
    // without a locale-sensitive strtod or allocating unbounded big integers.
    size_t position = token.front() == '-' ? 1 : 0;
    int64_t beforeDecimal = 0, digitIndex = 0, firstNonzero = -1, exponent = 0;
    bool fraction = false;
    for (; position < token.size() && token[position] != 'e' && token[position] != 'E'; ++position) {
        const char c = token[position]; if (c == '.') { fraction = true; continue; }
        if (!fraction) ++beforeDecimal;
        if (c != '0' && firstNonzero < 0) firstNonzero = digitIndex;
        ++digitIndex;
    }
    if (firstNonzero < 0) return true;
    if (position < token.size()) {
        ++position; bool negative = false;
        if (position < token.size() && (token[position] == '+' || token[position] == '-')) negative = token[position++] == '-';
        for (; position < token.size(); ++position) exponent = (std::min)(int64_t{1000000}, exponent * 10 + (token[position] - '0'));
        if (negative) exponent = -exponent;
    }
    return beforeDecimal - firstNonzero - 1 + exponent < 0;
}
// Strict bounded JSON recognizer, including duplicate object keys, Unicode
// surrogate pairing and numeric grammar. No evaluation or numeric conversion.
class JsonReader {
public:
    std::string_view s; size_t pos = 0;
    explicit JsonReader(std::string_view source) : s(source) {}
    void ws() { while (pos < s.size() && (s[pos] == ' ' || s[pos] == '\t' || s[pos] == '\r' || s[pos] == '\n')) ++pos; }
    bool take(char c) { ws(); if (pos == s.size() || s[pos] != c) return false; ++pos; return true; }
    static void appendUtf8(std::string& out, uint32_t v) {
        if (v < 0x80) out += static_cast<char>(v);
        else if (v < 0x800) { out += static_cast<char>(0xc0 | (v >> 6)); out += static_cast<char>(0x80 | (v & 63)); }
        else if (v < 0x10000) { out += static_cast<char>(0xe0 | (v >> 12)); out += static_cast<char>(0x80 | ((v >> 6) & 63)); out += static_cast<char>(0x80 | (v & 63)); }
        else { out += static_cast<char>(0xf0 | (v >> 18)); out += static_cast<char>(0x80 | ((v >> 12) & 63)); out += static_cast<char>(0x80 | ((v >> 6) & 63)); out += static_cast<char>(0x80 | (v & 63)); }
    }
    bool hex4(uint32_t& out) {
        out = 0; if (s.size() - pos < 4) return false;
        for (int n = 0; n < 4; ++n) { char c = s[pos++]; uint32_t x;
            if (c >= '0' && c <= '9') x = c - '0'; else if (c >= 'a' && c <= 'f') x = c - 'a' + 10;
            else if (c >= 'A' && c <= 'F') x = c - 'A' + 10; else return false;
            out = (out << 4) | x; } return true;
    }
    bool string(std::string* result = nullptr) {
        if (!take('"')) return false; std::string out;
        while (pos < s.size()) {
            char c = s[pos++]; if (c == '"') { if (result) *result = std::move(out); return true; }
            if (static_cast<uint8_t>(c) < 32) return false;
            if (c != '\\') { out += c; continue; }
            if (pos == s.size()) return false; c = s[pos++];
            switch (c) {
            case '"': case '\\': case '/': out += c; break;
            case 'b': out += '\b'; break; case 'f': out += '\f'; break; case 'n': out += '\n'; break;
            case 'r': out += '\r'; break; case 't': out += '\t'; break;
            case 'u': { uint32_t v; if (!hex4(v)) return false;
                if (v >= 0xd800 && v <= 0xdbff) { uint32_t low;
                    if (s.size() - pos < 6 || s[pos++] != '\\' || s[pos++] != 'u' || !hex4(low) || low < 0xdc00 || low > 0xdfff) return false;
                    v = 0x10000 + ((v - 0xd800) << 10) + low - 0xdc00;
                } else if (v >= 0xdc00 && v <= 0xdfff) return false;
                appendUtf8(out, v); break; }
            default: return false;
            }
        } return false;
    }
    bool value(unsigned depth = 0) {
        ws(); if (depth > 32 || pos == s.size()) return false;
        if (s[pos] == '"') return string();
        if (s[pos] == '{') { ++pos; std::unordered_set<std::string> keys; if (take('}')) return true;
            do { std::string key; if (!string(&key) || !keys.insert(key).second || !take(':') || !value(depth + 1)) return false;
                if (take('}')) return true; } while (take(',')); return false; }
        if (s[pos] == '[') { ++pos; if (take(']')) return true;
            do { if (!value(depth + 1)) return false; if (take(']')) return true; } while (take(',')); return false; }
        for (auto word : {std::string_view("true"), std::string_view("false"), std::string_view("null")})
            if (s.substr(pos, word.size()) == word) { pos += word.size(); return true; }
        const auto numberStart = pos;
        if (s[pos] == '-') ++pos;
        if (pos == s.size()) return false;
        if (s[pos] == '0') ++pos;
        else { if (s[pos] < '1' || s[pos] > '9') return false; while (pos < s.size() && s[pos] >= '0' && s[pos] <= '9') ++pos; }
        if (pos < s.size() && s[pos] == '.') { ++pos; auto begin = pos; while (pos < s.size() && s[pos] >= '0' && s[pos] <= '9') ++pos; if (pos == begin) return false; }
        if (pos < s.size() && (s[pos] == 'e' || s[pos] == 'E')) { ++pos; if (pos < s.size() && (s[pos] == '+' || s[pos] == '-')) ++pos;
            auto begin = pos; while (pos < s.size() && s[pos] >= '0' && s[pos] <= '9') ++pos; if (pos == begin) return false; }
        return finiteJsonNumber(s.substr(numberStart, pos - numberStart));
    }
};
}
inline bool validJson(std::string_view json) {
    if (json.empty() || json.size() > kMaxJsonBytes || !validUtf8(json)) return false;
    detail::JsonReader p(json); if (!p.value()) return false; p.ws(); return p.pos == json.size();
}
inline std::string quoteJson(std::string_view s) {
    static constexpr char hex[] = "0123456789abcdef"; std::string out = "\"";
    for (unsigned char c : s) { if (c == '"' || c == '\\') { out += '\\'; out += c; }
        else if (c < 32) { out += "\\u00"; out += hex[c >> 4]; out += hex[c & 15]; } else out += c; }
    return out + '"';
}
struct Message { std::string type, payload, correlationId; };
inline bool validMessageType(std::string_view t) noexcept {
    if (t.empty() || t.size() > 128) return false;
    for (unsigned char c : t) if (c < 33 || c > 126) return false;
    return true;
}
inline std::string serializeMessage(const Message& m) {
    return "{\"version\":1," + (m.correlationId.empty() ? std::string{} : "\"id\":" + quoteJson(m.correlationId) + ',') +
        "\"type\":" + quoteJson(m.type) + ",\"payload\":" + m.payload + '}';
}
inline bool parseMessage(std::string_view json, Message& out) {
    out = {}; if (!validJson(json)) return false;
    detail::JsonReader p(json); if (!p.take('{')) return false; bool version = false, type = false, payload = false;
    do { std::string key; if (!p.string(&key) || !p.take(':')) return false;
        if (key == "version") { p.ws(); auto start = p.pos; if (!p.value() || p.s.substr(start, p.pos - start) != "1") return false; version = true; }
        else if (key == "type") { if (!p.string(&out.type)) return false; type = true; }
        else if (key == "id") { if (!p.string(&out.correlationId) || out.correlationId.size() > 128 || out.correlationId.find('\0') != std::string::npos) return false; }
        else if (key == "payload") { p.ws(); auto start = p.pos; if (!p.value()) return false; out.payload = std::string(p.s.substr(start, p.pos - start)); payload = true; }
        else return false;
        if (p.take('}')) break;
        if (!p.take(',')) return false;
    } while (true);
    return version && type && payload && validMessageType(out.type);
}
struct Update { uint64_t sequence = 0, stateRevision = 0; std::string type, payload, correlationId; };
struct UpdateBatch { uint64_t activation = 0, lastSequence = 0, stateRevision = 0; std::string state = "null"; std::vector<Update> messages; };

// Canonical state survives activation changes. Transients do not. Mutations
// and immutable snapshots are protected for off-game-thread copy operations.
class UpdateQueue {
public:
    bool setState(std::string json) {
        if (!validJson(json)) return false; std::lock_guard<std::mutex> lock(mutex_);
        if (revision_ == UINT64_MAX || sequence_ == UINT64_MAX) return false;
        state_ = std::move(json); ++revision_; ++sequence_; return true;
    }
    bool postMessage(std::string type, std::string payload, std::string correlationId = {}) {
        Message m{std::move(type), std::move(payload), std::move(correlationId)};
        if (!validMessageType(m.type) || !validJson(m.payload) || m.correlationId.size() > 128 ||
            !validUtf8(m.correlationId) || m.correlationId.find('\0') != std::string::npos) return false;
        const auto envelope = serializeMessage(m); const auto bytes = envelope.size();
        if (bytes > kMaxJsonBytes || !validJson(envelope)) return false;
        std::lock_guard<std::mutex> lock(mutex_);
        if (!activation_ || !visible_ || messages_.size() >= kMaxQueueMessages || bytes > kMaxQueueBytes - queuedBytes_ || sequence_ == UINT64_MAX) return false;
        messages_.push_back({++sequence_, revision_, std::move(m.type), std::move(m.payload), std::move(m.correlationId)});
        queuedBytes_ += bytes; return true;
    }
    bool reset(uint64_t activation) {
        std::lock_guard<std::mutex> lock(mutex_);
        // A duplicate reset is a retry, not permission to erase new messages.
        if (activation == activation_) return true;
        activation_ = activation; messages_.clear(); queuedBytes_ = 0;
        // Sequence remains global to the surface, but acknowledgements begin
        // at zero for each new activation. The controller's first drain uses
        // ack=0 even when canonical state predates the activation.
        issued_ = acknowledged_ = 0; return true;
    }
    bool snapshot(uint64_t activation, UpdateBatch& out) {
        std::lock_guard<std::mutex> lock(mutex_); if (!activation || activation != activation_) return false;
        out = {activation_, sequence_, revision_, state_, {messages_.begin(), messages_.end()}}; issued_ = sequence_; return true;
    }
    bool acknowledge(uint64_t activation, uint64_t through) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (!activation || activation != activation_ || through > issued_ || through < acknowledged_) return false;
        while (!messages_.empty() && messages_.front().sequence <= through) { auto& m = messages_.front();
            queuedBytes_ -= serializeMessage({m.type, m.payload, m.correlationId}).size(); messages_.pop_front(); }
        acknowledged_ = through; return true;
    }
    bool accepts(uint64_t activation) const { std::lock_guard<std::mutex> lock(mutex_); return visible_ && activation && activation == activation_; }
    void setVisible(bool visible) {
        std::lock_guard<std::mutex> lock(mutex_); visible_ = visible;
        if (!visible) { messages_.clear(); queuedBytes_ = 0; }
    }
    bool copyState(uint64_t revision, std::string& out) const {
        std::lock_guard<std::mutex> lock(mutex_); if (revision != revision_) return false; out = state_; return true;
    }
    bool copyMessage(uint64_t activation, uint32_t index, Update& out) const {
        std::lock_guard<std::mutex> lock(mutex_); if (!activation || activation != activation_ || index >= messages_.size()) return false;
        out = messages_[index]; return true;
    }
private:
    mutable std::mutex mutex_;
    std::string state_ = "null";
    std::deque<Update> messages_;
    uint64_t activation_ = 0, sequence_ = 0, revision_ = 0, issued_ = 0, acknowledged_ = 0;
    size_t queuedBytes_ = 0;
    bool visible_ = true;
};
} // namespace TitanHtmlUi
