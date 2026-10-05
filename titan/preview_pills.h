/// @file titan/preview_pills.h
/// @brief Preview pills facade (SDK 142): short labels a plugin pins over its
///        tab's thumbnail on the controller's Home grid.
///
/// Each plugin owns its own pills. The host stamps them from the calling
/// plugin instance, so a plugin sets and clears only its own:
///
/// @code
///   auto pills = titan::previewPills(*this);
///   pills.set("break", {"Breaking:", titan::PanelTone::info,
///                       std::chrono::minutes(12)});  // "Breaking: 00:12:00", ticking down
///   pills.set("task", {"Mining iron"});              // plain text
///   pills.clear("break");
/// @endcode
///
/// A pill is text, a titan::PanelTone and an optional countdown drawn after
/// the text as HH:MM:SS. The controller ticks the countdown itself, so set it
/// once: it stops at 00:00:00 and stays there until the plugin clears or
/// replaces the pill.
///
/// Keys are 1-32 characters of [A-Za-z0-9._:/-]. A plugin shows at most 2
/// pills and a tab at most 8; a new pill past either limit is refused, never
/// swapped for another. Text becomes one line of UTF-8, cut to 63 bytes.
/// Pills keep the order they were first set in; replacing one keeps its place.
///
/// Only an enabled plugin may set a pill, and disabling, unloading or
/// reloading the plugin drops every pill it set, so pills never outlive the
/// plugin that drew them. Pills appear on the tab's Home-grid card only,
/// never in the game view. See PUBLIC_API.md ("Preview pills").
///
/// Every call is safe from any thread and from inside the plugin's own locks.
/// Calls from the plugin's constructor fail: the host binds the plugin only
/// once it has wrapped the instance.

#pragma once

#include "plugin.h"

#include <chrono>
#include <cstdint>
#include <cstring>
#include <optional>
#include <string>
#include <string_view>

namespace titan {

/// One preview pill.
struct PreviewPill {
    /// Drawn first. May be empty when `countdown` is set.
    std::string text;
    PanelTone tone = PanelTone::neutral;
    /// When set, a live HH:MM:SS countdown of this much time from now, drawn
    /// after the text. Negative counts as zero; past 999:59:59 is shortened.
    std::optional<std::chrono::milliseconds> countdown;
};

/// This plugin's preview pills. Holds only the plugin reference; make one per
/// use with titan::previewPills(*this).
class PreviewPills {
public:
    explicit PreviewPills(const Plugin& plugin) noexcept : plugin_(&plugin) {}

    /// Show pill @p key, replacing the one already under that key. False when
    /// refused: the plugin is disabled or not yet bound, the key is invalid,
    /// the text is empty with no countdown, or a new pill would pass the
    /// per-plugin or per-tab limit.
    bool set(std::string_view key, const PreviewPill& pill) const {
        KeyBuffer keyBuffer;
        if (!copyKey(key, keyBuffer)) return false;
        TitanPluginSdk::PreviewPillWrite raw{};
        raw.key = keyBuffer;
        raw.text = pill.text.c_str();
        raw.tone = static_cast<int32_t>(pill.tone);
        if (pill.countdown) {
            raw.flags = TitanPluginSdk::PREVIEW_PILL_COUNTDOWN;
            const auto ms = pill.countdown->count();
            raw.countdownMs = ms > 0 ? static_cast<uint64_t>(ms) : 0;
        }
        return write(raw);
    }

    /// Remove pill @p key. True once it is gone, including when it was never
    /// shown; false for an invalid key or an unbound plugin.
    bool clear(std::string_view key) const {
        KeyBuffer keyBuffer;
        if (!copyKey(key, keyBuffer)) return false;
        TitanPluginSdk::PreviewPillWrite raw{};
        raw.flags = TitanPluginSdk::PREVIEW_PILL_CLEAR;
        raw.key = keyBuffer;
        return write(raw);
    }

    /// Remove every pill this plugin shows.
    bool clearAll() const {
        TitanPluginSdk::PreviewPillWrite raw{};
        raw.flags = TitanPluginSdk::PREVIEW_PILL_CLEAR;
        return write(raw);
    }

private:
    using KeyBuffer = char[TitanPluginSdk::kPreviewPillMaxKeyLen + 1];

    /// NUL-terminate @p key for the ABI; false when it cannot be a key. The
    /// host checks the character set.
    static bool copyKey(std::string_view key, KeyBuffer& out) noexcept {
        if (key.empty() || key.size() > TitanPluginSdk::kPreviewPillMaxKeyLen ||
            key.find('\0') != std::string_view::npos) {
            return false;
        }
        std::memcpy(out, key.data(), key.size());
        out[key.size()] = '\0';
        return true;
    }

    bool write(const TitanPluginSdk::PreviewPillWrite& raw) const {
        auto* backend = detail::backend();
        const char* id = plugin_->id();
        if (!backend || !id || !id[0]) return false;
        return backend->previewPillWrite(static_cast<const void*>(plugin_), id, &raw) != 0;
    }

    const Plugin* plugin_;
};

/// This plugin's preview pills. Ownership comes from the instance the host
/// loaded, never from a string the caller supplies.
inline PreviewPills previewPills(const Plugin& plugin) noexcept { return PreviewPills{plugin}; }

}  // namespace titan
