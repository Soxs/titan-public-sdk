/// @file titan/cross_tab.h
/// @brief Cross-Tab Store facade (SDK 141): small values every tab launched
///        by the same controller sees, for the life of the controller session.
///
/// Each plugin owns one namespace. The host stamps it from the calling plugin
/// instance, so a plugin reads and writes only its own keys:
///
/// @code
///   auto ct = titan::crossTab(*this);
///   ct.put("route.last", "lumbridge");            // every tab sees it
///   if (auto value = ct.get("route.last")) use(value->bytes);
///   ct.erase("route.last");                        // every tab loses it
/// @endcode
///
/// Values are raw bytes, at most 16 KiB, under keys of 1-63 characters of
/// [A-Za-z0-9._:/-]. A namespace holds at most 64 keys, 128 KiB and 16
/// secrets. Values live in the controller's memory for one controller sign-in
/// session and outlive plugin reloads and tab restarts; they are never
/// written to disk. See PUBLIC_API.md ("Cross-Tab Store") for the full rules.
///
/// Every call is safe from any thread and from inside the plugin's own locks.
/// Calls from the plugin's constructor fail: the host binds the namespace
/// only once it has wrapped the instance.

#pragma once

#include "plugin.h"

#include <cstddef>
#include <cstdint>
#include <cstring>
#include <new>
#include <optional>
#include <string>
#include <string_view>
#include <utility>

namespace titan {

namespace detail {

/// Zero @p size bytes through a volatile pointer, so the writes are not
/// dropped as dead stores to memory that is about to be freed.
inline void secureZero(void* data, size_t size) noexcept {
    auto* bytes = static_cast<volatile uint8_t*>(data);
    for (size_t i = 0; i < size; ++i) bytes[i] = 0;
}

}  // namespace detail

/// Bytes that are zeroed before their memory is released. getSecret() fills
/// one, so a secret never passes through a std::string, whose growth copies
/// and short-string buffer are never wiped. Move-only: a copy would be one
/// more place the secret lives. Copying view() into another container
/// escapes the wipe.
class SecureBuffer {
public:
    SecureBuffer() noexcept = default;
    ~SecureBuffer() { release(); }

    SecureBuffer(SecureBuffer&& other) noexcept
        : data_(std::exchange(other.data_, nullptr)),
          size_(std::exchange(other.size_, 0)),
          capacity_(std::exchange(other.capacity_, 0)) {}
    SecureBuffer& operator=(SecureBuffer&& other) noexcept {
        if (this != &other) {
            release();
            data_ = std::exchange(other.data_, nullptr);
            size_ = std::exchange(other.size_, 0);
            capacity_ = std::exchange(other.capacity_, 0);
        }
        return *this;
    }
    SecureBuffer(const SecureBuffer&) = delete;
    SecureBuffer& operator=(const SecureBuffer&) = delete;

    const uint8_t* data() const noexcept { return data_; }
    size_t size() const noexcept { return size_; }
    bool empty() const noexcept { return size_ == 0; }
    /// The bytes as text, valid until the buffer next changes.
    std::string_view view() const noexcept {
        return {reinterpret_cast<const char*>(data_), size_};
    }

    /// Zero the bytes and empty the buffer. The memory is kept for reuse and
    /// zeroed again when it is released.
    void clear() noexcept {
        detail::secureZero(data_, capacity_);
        size_ = 0;
    }

private:
    friend class CrossTab;

    void release() noexcept {
        clear();
        delete[] data_;
        data_ = nullptr;
        capacity_ = 0;
    }

    /// Swap the memory for a zeroed block of @p capacity bytes.
    bool reallocate(size_t capacity) noexcept {
        release();
        data_ = new (std::nothrow) uint8_t[capacity]();
        if (!data_) return false;
        capacity_ = capacity;
        return true;
    }

    uint8_t* data_ = nullptr;
    size_t size_ = 0;
    size_t capacity_ = 0;
};

/// Options for put() and putIf().
struct CrossTabOptions {
    /// Handle the value as a secret: never logged or persisted, sent only to
    /// tabs this controller launched (other tabs see it redacted), and read
    /// back only through getSecret(). Refused in a tab this controller did
    /// not launch. It is not access control: other plugins in the same tab
    /// share the process.
    bool secret = false;
};

/// A key's state as this tab sees it.
struct CrossTabInfo {
    uint64_t version = 0;   ///< controller-assigned, rising; 0 while pending
    uint32_t size = 0;      ///< value bytes; 0 when redacted
    bool secret = false;
    bool pending = false;   ///< this tab's own put, not yet confirmed
    bool redacted = false;  ///< a secret whose value this tab may not hold
};

/// A non-secret value, as get() returns it.
struct CrossTabValue {
    std::string bytes;      ///< raw bytes; UTF-8 text if text was stored
    uint64_t version = 0;   ///< 0 while pending
    bool pending = false;   ///< this tab's own put, not yet confirmed
};

/// This plugin's Cross-Tab Store namespace. Holds only the plugin reference;
/// make one per use with titan::crossTab(*this).
class CrossTab {
public:
    explicit CrossTab(const Plugin& plugin) noexcept : plugin_(&plugin) {}

    /// Set @p key. Shown in this tab at once (pending, version 0) and sent to
    /// every tab; the last write the controller receives wins. No change
    /// event reaches this instance unless the controller rejects the write.
    /// False when refused locally: a bad key, a value over 16 KiB, a
    /// namespace limit, a secret in a tab this controller did not launch, or
    /// a full outbox of unacknowledged writes.
    bool put(std::string_view key, std::string_view bytes,
             CrossTabOptions options = {}) const {
        return write(key, bytes, secretFlag(options), 0).has_value();
    }

    /// Erase @p key, even if it is absent: the erase reaches every tab,
    /// including tabs that never held the key.
    bool erase(std::string_view key) const {
        return write(key, {}, TitanPluginSdk::CROSS_TAB_ERASE, 0).has_value();
    }

    /// Propose setting @p key only if its version is still
    /// @p expectedVersion (0 = only if absent). Nothing changes locally; the
    /// controller decides, and onCrossTabChanged reports an Outcome (Set) or
    /// Rejected (Conflict or another cause) carrying the returned write id.
    std::optional<uint64_t> putIf(std::string_view key, std::string_view bytes,
                                  uint64_t expectedVersion,
                                  CrossTabOptions options = {}) const {
        return write(key, bytes,
                     secretFlag(options) | TitanPluginSdk::CROSS_TAB_CONDITIONAL,
                     expectedVersion);
    }

    /// Propose erasing @p key only if its version is still
    /// @p expectedVersion; as putIf, with an Outcome of Erased.
    std::optional<uint64_t> eraseIf(std::string_view key,
                                    uint64_t expectedVersion) const {
        return write(key, {},
                     TitanPluginSdk::CROSS_TAB_ERASE |
                         TitanPluginSdk::CROSS_TAB_CONDITIONAL,
                     expectedVersion);
    }

    /// The value of a non-secret key; nullopt when it is absent or secret.
    std::optional<CrossTabValue> get(std::string_view key) const {
        CrossTabValue value;
        for (int attempt = 0; attempt < kReadAttempts; ++attempt) {
            TitanPluginSdk::CrossTabEntryInfo raw{};
            uint8_t* out = value.bytes.empty()
                ? nullptr : reinterpret_cast<uint8_t*>(value.bytes.data());
            const uint32_t size = read(key, out, static_cast<uint32_t>(value.bytes.size()), raw);
            if (size == TitanPluginSdk::kCrossTabAbsent ||
                (raw.flags & TitanPluginSdk::CROSS_TAB_SECRET) != 0) {
                // The key may have turned secret between the size query and
                // the copy; never let those bytes leave in a std::string.
                detail::secureZero(value.bytes.data(), value.bytes.size());
                return std::nullopt;
            }
            if (size <= value.bytes.size()) {
                value.bytes.resize(size);
                value.version = raw.version;
                value.pending = raw.pending != 0;
                return value;
            }
            value.bytes.assign(size, '\0');
        }
        return std::nullopt;
    }

    /// Copy a key's value into @p out and fill @p info if non-null. False,
    /// leaving @p out empty, when the key is absent or its value is withheld
    /// here (redacted; @p info still says so). Reads non-secret keys too.
    bool getSecret(std::string_view key, SecureBuffer& out,
                   CrossTabInfo* info = nullptr) const {
        out.clear();
        for (int attempt = 0; attempt < kReadAttempts; ++attempt) {
            TitanPluginSdk::CrossTabEntryInfo raw{};
            const uint32_t capacity = static_cast<uint32_t>(out.capacity_);
            const uint32_t size = read(key, out.data_, capacity, raw);
            if (size == TitanPluginSdk::kCrossTabAbsent) break;
            if (size <= capacity) {
                if (info) *info = toInfo(raw);
                if (raw.redacted != 0) break;
                out.size_ = size;
                return true;
            }
            if (!out.reallocate(size)) break;
        }
        out.clear();
        return false;
    }

    /// A key's state without its value; nullopt when absent.
    std::optional<CrossTabInfo> info(std::string_view key) const {
        TitanPluginSdk::CrossTabEntryInfo raw{};
        if (read(key, nullptr, 0, raw) == TitanPluginSdk::kCrossTabAbsent) {
            return std::nullopt;
        }
        return toInfo(raw);
    }

private:
    /// A value can change between the size query and the copy; retry a few
    /// times rather than loop against a writer that never stops.
    static constexpr int kReadAttempts = 4;

    using KeyBuffer = char[TitanPluginSdk::kCrossTabKeyCapacity];

    static uint32_t secretFlag(CrossTabOptions options) noexcept {
        return options.secret ? uint32_t(TitanPluginSdk::CROSS_TAB_SECRET) : 0u;
    }

    /// NUL-terminate @p key for the ABI; false when it cannot be a key.
    static bool copyKey(std::string_view key, KeyBuffer& out) noexcept {
        if (key.empty() || key.size() > TitanPluginSdk::kCrossTabMaxKeyLen ||
            key.find('\0') != std::string_view::npos) {
            return false;
        }
        std::memcpy(out, key.data(), key.size());
        out[key.size()] = '\0';
        return true;
    }

    static CrossTabInfo toInfo(const TitanPluginSdk::CrossTabEntryInfo& raw) noexcept {
        CrossTabInfo info;
        info.version = raw.version;
        info.size = raw.size;
        info.secret = (raw.flags & TitanPluginSdk::CROSS_TAB_SECRET) != 0;
        info.pending = raw.pending != 0;
        info.redacted = raw.redacted != 0;
        return info;
    }

    std::optional<uint64_t> write(std::string_view key, std::string_view bytes,
                                  uint32_t flags, uint64_t expectedVersion) const {
        auto* backend = detail::backend();
        const char* id = plugin_->id();
        KeyBuffer keyBuffer;
        if (!backend || !id || !id[0] || !copyKey(key, keyBuffer) ||
            bytes.size() > TitanPluginSdk::kCrossTabMaxValueBytes) {
            return std::nullopt;
        }
        TitanPluginSdk::CrossTabWrite raw{};
        raw.flags = flags;
        raw.key = keyBuffer;
        raw.data = reinterpret_cast<const uint8_t*>(bytes.data());
        raw.size = static_cast<uint32_t>(bytes.size());
        raw.expectedVersion = expectedVersion;
        uint64_t writeId = 0;
        if (!backend->crossTabWrite(static_cast<const void*>(plugin_), id, &raw,
                                    &writeId)) {
            return std::nullopt;
        }
        return writeId;
    }

    uint32_t read(std::string_view key, uint8_t* out, uint32_t capacity,
                  TitanPluginSdk::CrossTabEntryInfo& info) const {
        auto* backend = detail::backend();
        const char* id = plugin_->id();
        KeyBuffer keyBuffer;
        if (!backend || !id || !id[0] || !copyKey(key, keyBuffer)) {
            return TitanPluginSdk::kCrossTabAbsent;
        }
        return backend->crossTabRead(static_cast<const void*>(plugin_), id,
                                     keyBuffer, out, capacity, &info);
    }

    const Plugin* plugin_;
};

/// This plugin's Cross-Tab Store namespace. The namespace comes from the
/// instance the host loaded, never from a string the caller supplies.
inline CrossTab crossTab(const Plugin& plugin) noexcept { return CrossTab{plugin}; }

}  // namespace titan
