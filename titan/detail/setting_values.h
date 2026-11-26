#pragma once

// Portable wire codecs shared by the public SDK and host admission. No platform
// types, pointers, JSON library, or native record layout changes are required.
#include <algorithm>
#include <charconv>
#include <cmath>
#include <cstdint>
#include <limits>
#include <iterator>
#include <optional>
#include <string>
#include <string_view>
#include <vector>

namespace titan {
struct SettingKeybind { int32_t keyCode = 0, modifiers = 0; bool operator==(const SettingKeybind&) const = default; };
struct SettingDimension { int32_t width = 0, height = 0; bool operator==(const SettingDimension&) const = default; };
struct SettingPoint { int32_t x = 0, y = 0; bool operator==(const SettingPoint&) const = default; };
struct SettingFont { std::string family; int32_t size = 12; bool bold = false, italic = false; bool operator==(const SettingFont&) const = default; };

namespace settingValues {
inline constexpr int32_t modifierMask = 64 | 128 | 256 | 512; // AWT extended SHIFT/CTRL/META/ALT masks.
inline constexpr size_t maxEncodedBytes = 8192; // Largest value is a 1024-byte UTF-8 font family, JSON escaped.

inline bool utf8(std::string_view s) {
    for (size_t i = 0; i < s.size();) {
        const auto c = static_cast<unsigned char>(s[i++]);
        if (!c) return false;
        if (c < 0x80) continue;
        unsigned n; uint32_t cp;
        if (c >= 0xc2 && c <= 0xdf) { n = 1; cp = c & 31; }
        else if (c >= 0xe0 && c <= 0xef) { n = 2; cp = c & 15; }
        else if (c >= 0xf0 && c <= 0xf4) { n = 3; cp = c & 7; }
        else return false;
        const unsigned count = n;
        if (i + n > s.size()) return false;
        while (n--) { const auto d = static_cast<unsigned char>(s[i++]); if ((d & 0xc0) != 0x80) return false; cp = (cp << 6) | (d & 63); }
        if ((count == 2 && cp < 0x800) || (count == 3 && cp < 0x10000) || cp > 0x10ffff || (cp >= 0xd800 && cp <= 0xdfff)) return false;
    }
    return true;
}
template<class T> inline bool integer(std::string_view s, T& out) {
    if (s.empty()) return false;
    const auto r = std::from_chars(s.data(), s.data() + s.size(), out);
    return r.ec == std::errc{} && r.ptr == s.data() + s.size();
}
inline bool read(std::string_view s, int64_t& out) { return s.size() <= maxEncodedBytes && integer(s, out); }
inline bool read(std::string_view s, double& out) {
    if (s.empty() || s.size() > maxEncodedBytes) return false;
    const auto r = std::from_chars(s.data(), s.data() + s.size(), out, std::chars_format::general);
    return r.ec == std::errc{} && r.ptr == s.data() + s.size() && std::isfinite(out);
}
inline std::string write(int64_t value) { return std::to_string(value); }
inline std::string write(double value) {
    char buffer[64]; const auto r = std::to_chars(buffer, buffer + sizeof(buffer), value, std::chars_format::general, std::numeric_limits<double>::max_digits10);
    return r.ec == std::errc{} ? std::string(buffer, r.ptr) : std::string{};
}
inline bool valid(int64_t) { return true; }
template<class T> inline T normalize(T value) { return value; }
inline SettingKeybind normalize(SettingKeybind value) {
    if (value.keyCode == 16 || value.keyCode == 17 || value.keyCode == 18 || value.keyCode == 157) value.keyCode = 0;
    return value;
}
inline bool valid(double value) { return std::isfinite(value); }
inline bool valid(const SettingKeybind& v) { return v.keyCode >= 0 && v.modifiers >= 0 && !(v.modifiers & ~modifierMask); }
inline bool valid(const SettingDimension& v) { return v.width >= 0 && v.height >= 0; }
inline bool valid(const std::optional<SettingDimension>& v) { return !v || valid(*v); }
inline bool valid(const std::optional<SettingPoint>&) { return true; }
inline bool valid(const SettingFont& v) { return !v.family.empty() && v.family.size() <= 1024 && utf8(v.family) && v.size >= 1; }
inline bool valid(const std::vector<int32_t>& v) {
    if (v.size() > 32) return false;
    for (size_t i = 0; i < v.size(); ++i) if (std::find(v.begin(), v.begin() + i, v[i]) != v.begin() + i) return false;
    return true;
}

class Reader {
    std::string_view source_; size_t pos_ = 0;
public:
    explicit Reader(std::string_view s) : source_(s) {}
    void space() { while (pos_ < source_.size() && (source_[pos_] == ' ' || source_[pos_] == '\t' || source_[pos_] == '\r' || source_[pos_] == '\n')) ++pos_; }
    bool take(char c) { space(); if (pos_ == source_.size() || source_[pos_] != c) return false; ++pos_; return true; }
    bool end() { space(); return pos_ == source_.size(); }
    bool literal(std::string_view value) { space(); if (source_.substr(pos_, value.size()) != value) return false; pos_ += value.size(); return true; }
    bool number(int32_t& out) {
        space(); const size_t start = pos_; if (pos_ < source_.size() && source_[pos_] == '-') ++pos_;
        const size_t digits = pos_;
        while (pos_ < source_.size() && source_[pos_] >= '0' && source_[pos_] <= '9') ++pos_;
        if (pos_ == digits || (pos_ - digits > 1 && source_[digits] == '0')) return false;
        return integer(source_.substr(start, pos_ - start), out);
    }
    bool boolean(bool& out) { if (literal("true")) { out = true; return true; } if (literal("false")) { out = false; return true; } return false; }
    bool hex(uint32_t& out) {
        out = 0; for (int i = 0; i < 4; ++i) {
            if (pos_ == source_.size()) return false; const char c = source_[pos_++];
            unsigned d = c >= '0' && c <= '9' ? c - '0' : c >= 'a' && c <= 'f' ? c - 'a' + 10 : c >= 'A' && c <= 'F' ? c - 'A' + 10 : 16;
            if (d == 16) return false; out = (out << 4) | d;
        } return true;
    }
    bool text(std::string& out) {
        if (!take('"')) return false; out.clear();
        while (pos_ < source_.size()) {
            char c = source_[pos_++]; if (c == '"') return utf8(out);
            if (static_cast<unsigned char>(c) < 32) return false;
            if (c != '\\') { out += c; continue; }
            if (pos_ == source_.size()) return false; c = source_[pos_++];
            switch (c) {
                case '"': case '\\': case '/': out += c; break;
                case 'b': out += '\b'; break; case 'f': out += '\f'; break;
                case 'n': out += '\n'; break; case 'r': out += '\r'; break; case 't': out += '\t'; break;
                case 'u': {
                    uint32_t cp; if (!hex(cp) || !cp) return false;
                    if (cp >= 0xd800 && cp <= 0xdbff) {
                        if (pos_ + 2 > source_.size() || source_[pos_++] != '\\' || source_[pos_++] != 'u') return false;
                        uint32_t lo; if (!hex(lo) || lo < 0xdc00 || lo > 0xdfff) return false; cp = 0x10000 + ((cp - 0xd800) << 10) + lo - 0xdc00;
                    } else if (cp >= 0xdc00 && cp <= 0xdfff) return false;
                    if (cp < 128) out += static_cast<char>(cp);
                    else if (cp < 2048) { out += static_cast<char>(0xc0 | (cp >> 6)); out += static_cast<char>(0x80 | (cp & 63)); }
                    else if (cp < 65536) { out += static_cast<char>(0xe0 | (cp >> 12)); out += static_cast<char>(0x80 | ((cp >> 6) & 63)); out += static_cast<char>(0x80 | (cp & 63)); }
                    else { out += static_cast<char>(0xf0 | (cp >> 18)); out += static_cast<char>(0x80 | ((cp >> 12) & 63)); out += static_cast<char>(0x80 | ((cp >> 6) & 63)); out += static_cast<char>(0x80 | (cp & 63)); }
                    break;
                }
                default: return false;
            }
        } return false;
    }
    template<class F> bool object(unsigned expected, F field) {
        if (!take('{')) return false; unsigned seen = 0;
        for (unsigned i = 0; i < expected; ++i) {
            std::string key; if (i && !take(',')) return false;
            if (!text(key) || !take(':')) return false;
            const unsigned bit = field(key, *this); if (!bit || (seen & bit)) return false; seen |= bit;
        }
        return take('}') && end();
    }
};
inline std::string quote(std::string_view value) {
    std::string out = "\""; constexpr char hex[] = "0123456789abcdef";
    for (unsigned char c : value) {
        if (c == '"' || c == '\\') { out += '\\'; out += static_cast<char>(c); }
        else if (c < 32) { out += "\\u00"; out += hex[c >> 4]; out += hex[c & 15]; }
        else out += static_cast<char>(c);
    } return out + '"';
}
inline std::string write(const SettingKeybind& v) { return "{\"keyCode\":" + std::to_string(v.keyCode) + ",\"modifiers\":" + std::to_string(v.modifiers) + "}"; }
inline std::string write(const SettingDimension& v) { return "{\"width\":" + std::to_string(v.width) + ",\"height\":" + std::to_string(v.height) + "}"; }
inline std::string write(const std::optional<SettingDimension>& v) { return v ? write(*v) : "null"; }
inline std::string write(const std::optional<SettingPoint>& v) { return v ? "{\"x\":" + std::to_string(v->x) + ",\"y\":" + std::to_string(v->y) + "}" : "null"; }
inline std::string write(const SettingFont& v) { return "{\"family\":" + quote(v.family) + ",\"size\":" + std::to_string(v.size) + ",\"bold\":" + (v.bold ? "true" : "false") + ",\"italic\":" + (v.italic ? "true" : "false") + "}"; }
inline std::string write(const std::vector<int32_t>& v) { std::string out = "["; for (size_t i = 0; i < v.size(); ++i) { if (i) out += ','; out += std::to_string(v[i]); } return out + ']'; }
inline bool read(std::string_view s, SettingKeybind& v) {
    if (s.size() > maxEncodedBytes) return false; Reader r(s);
    if (!r.object(2, [&](const std::string& k, Reader& p) { return k == "keyCode" && p.number(v.keyCode) ? 1u : k == "modifiers" && p.number(v.modifiers) ? 2u : 0u; }) || !valid(v)) return false;
    v = normalize(v); return true;
}
inline bool read(std::string_view s, SettingDimension& v) {
    if (s.size() > maxEncodedBytes) return false; Reader r(s);
    return r.object(2, [&](const std::string& k, Reader& p) { return k == "width" && p.number(v.width) ? 1u : k == "height" && p.number(v.height) ? 2u : 0u; }) && valid(v);
}
inline bool read(std::string_view s, std::optional<SettingDimension>& v) {
    if (s.size() > maxEncodedBytes) return false; Reader r(s);
    if (r.literal("null") && r.end()) { v.reset(); return true; }
    SettingDimension dimensions;
    if (!read(s, dimensions)) return false;
    v = dimensions; return true;
}
inline bool read(std::string_view s, std::optional<SettingPoint>& v) {
    if (s.size() > maxEncodedBytes) return false; Reader r(s);
    if (r.literal("null") && r.end()) { v.reset(); return true; }
    Reader p(s); SettingPoint point;
    if (!p.object(2, [&](const std::string& k, Reader& q) { return k == "x" && q.number(point.x) ? 1u : k == "y" && q.number(point.y) ? 2u : 0u; })) return false;
    v = point; return true;
}
inline bool read(std::string_view s, SettingFont& v) {
    if (s.size() > maxEncodedBytes) return false; Reader r(s);
    return r.object(4, [&](const std::string& k, Reader& p) { return k == "family" && p.text(v.family) ? 1u : k == "size" && p.number(v.size) ? 2u : k == "bold" && p.boolean(v.bold) ? 4u : k == "italic" && p.boolean(v.italic) ? 8u : 0u; }) && valid(v);
}
inline bool read(std::string_view s, std::vector<int32_t>& v) {
    if (s.size() > maxEncodedBytes) return false; Reader r(s); std::vector<int32_t> next;
    if (!r.take('[')) return false;
    if (!r.take(']')) { do { int32_t n; if (next.size() == 32 || !r.number(n)) return false; next.push_back(n); } while (r.take(',')); if (!r.take(']')) return false; }
    if (!r.end() || !valid(next)) return false; v = std::move(next); return true;
}
inline std::optional<uint8_t> controlTypeForName(std::string_view name) {
    constexpr std::string_view names[] = {"numberInput", "colorAlpha", "integer64", "real64", "multiSelect", "keybind", "modifierlessKeybind", "dimension", "point", "font"};
    for (uint8_t i = 0; i < 10; ++i) if (name == names[i]) return static_cast<uint8_t>(i + 9);
    return {};
}
template<class Setting, class Value> bool validateExtendedValue(const Setting& setting, const Value& value, std::string& error) {
    const auto control = static_cast<unsigned>(setting.controlType);
    if (control < 10 || control > 18) return true; // Existing controls keep their established normalization.
    error = "Invalid typed setting value";
    if (control == 10) { if (static_cast<unsigned>(value.type) != 2) return false; error.clear(); return true; }
    if (static_cast<unsigned>(value.type) != 3) return false;
    const std::string_view text(value.stringValue);
    if (text.size() > maxEncodedBytes) return false;
    bool okay = false;
    switch (control) {
        case 11: { int64_t n; okay = read(text, n); break; }
        case 12: { double n; okay = read(text, n); break; }
        case 13: {
            std::vector<int32_t> values; okay = read(text, values) && setting.optionCount <= 32;
            if (okay) for (const int32_t n : values) {
                bool found = false; for (size_t i = 0; i < setting.optionCount; ++i) found |= setting.options[i].intValue == n;
                if (!found) { okay = false; break; }
            }
            break;
        }
        case 14: case 15: { SettingKeybind k; okay = read(text, k) && (control != 15 || k.keyCode == 0 || k.modifiers == 0); break; }
        // The common wire admits both forms. The actual SDK setting still
        // decides whether its declared value type permits null on application.
        case 16: { std::optional<SettingDimension> d; okay = read(text, d); break; }
        case 17: { std::optional<SettingPoint> p; okay = read(text, p); break; }
        case 18: { SettingFont f; okay = read(text, f); break; }
    }
    if (okay) error.clear(); return okay;
}
template<class Setting> bool validateExtendedMetadata(const Setting& setting, std::string& error) {
    const auto control = static_cast<unsigned>(setting.controlType);
    if (control < 10 || control > 18) return true;
    if (setting.optionCount > 32) { error = "At most 32 setting choices are supported"; return false; }
    for (size_t i = 0; i < setting.optionCount; ++i) {
        const auto& label = setting.options[i].label;
        const auto end = std::find(std::begin(label), std::end(label), '\0');
        if (end == std::end(label) || !utf8(std::string_view(std::begin(label), static_cast<size_t>(end - std::begin(label))))) {
            error = "Choice label exceeds its UTF-8 wire field"; return false;
        }
        if (control == 13) for (size_t j = 0; j < i; ++j) if (setting.options[i].intValue == setting.options[j].intValue) {
            error = "Duplicate multi-select choice ID"; return false;
        }
    }
    error.clear(); return true;
}
template<class Setting> bool validateExtendedSetting(const Setting& setting, std::string& error) {
    return validateExtendedMetadata(setting, error) && validateExtendedValue(setting, setting.value, error)
        && validateExtendedValue(setting, setting.defaultValue, error);
}
template<class Setting, class Value> bool validateExtendedUpdate(const Setting& setting, const Value& value, std::string& error) {
    // A bad current value must not prevent a corrective edit or reset.
    return validateExtendedMetadata(setting, error) && validateExtendedValue(setting, setting.defaultValue, error)
        && validateExtendedValue(setting, value, error);
}
} // namespace settingValues
} // namespace titan
