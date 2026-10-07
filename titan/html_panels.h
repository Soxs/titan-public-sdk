#pragma once

// Public header-only HTML UI facade. Embeds owned bytes; no JSON library,
// renderer headers, loose file loader, or host objects are required.
#include "detail/html_ui_contract.h"
#include <atomic>
#include <functional>
#include <stdexcept>

namespace titan {
using HtmlPanelMessage = TitanHtmlUi::Message;
using HtmlInput = TitanHtmlUi::Input;
class HtmlPanelBundle {
public:
    explicit HtmlPanelBundle(std::string entrypoint) { bundle_.entrypoint = std::move(entrypoint); }
    HtmlPanelBundle& text(std::string path, std::string mime, std::string_view contents) {
        return binary(std::move(path), std::move(mime), reinterpret_cast<const uint8_t*>(contents.data()), contents.size());
    }
    HtmlPanelBundle& binary(std::string path, std::string mime, const uint8_t* bytes, size_t size) {
        if ((!bytes && size) || size > TitanHtmlUi::kMaxResourceBytes || bundle_.resources.size() >= TitanHtmlUi::kMaxResources)
            throw std::invalid_argument("invalid HTML resource bytes/count");
        TitanHtmlUi::Resource r{std::move(path), std::move(mime), {}};
        if (size) r.bytes.assign(bytes, bytes + size);
        bundle_.resources.push_back(std::move(r)); return *this;
    }
    const TitanHtmlUi::Bundle& data() const noexcept { return bundle_; }
private:
    TitanHtmlUi::Bundle bundle_;
};

struct HtmlOverlayOptions {
    // Numeric anchor values deliberately match titan::Anchor without requiring
    // the ImGui overlay facade as a transitive include. 0 is Dynamic.
    uint8_t anchor = 0;
    int32_t priority = 50;
    uint32_t width = 220, height = 160;
    uint8_t input = TitanHtmlUi::ClickThrough;
    bool visible = true;
};

class HtmlSidePanel {
public:
    using MessageFn = std::function<void(const HtmlPanelMessage&)>;
    HtmlSidePanel(std::string id, std::string title, const HtmlPanelBundle& bundle,
                  TitanHtmlUi::SurfaceKind kind = TitanHtmlUi::SurfaceKind::SidePanel,
                  HtmlOverlayOptions options = {}) : bundle_(bundle.data()) {
        std::string error;
        if (!TitanHtmlUi::validId(id)) throw std::invalid_argument("HTML panel ID must contain 1..31 ASCII letters, digits, '.', '_' or '-'");
        if (title.size() > 63 || !TitanHtmlUi::validUtf8(title) || title.find('\0') != std::string::npos)
            throw std::invalid_argument("HTML panel title must be UTF-8 and at most 63 bytes");
        if (!TitanHtmlUi::validateBundle(bundle_, error)) throw std::invalid_argument(error);
        if (options.anchor > 5 || options.width < 80 || options.width > 600 || options.height < 24 || options.height > 600 || (options.input & ~7))
            throw std::invalid_argument("invalid HTML overlay anchor, size or input flags");
        if (options.anchor == 5 && options.input != TitanHtmlUi::ClickThrough)
            throw std::invalid_argument("tooltip HTML overlays must remain click-through");
        descriptor_.kind = kind; descriptor_.id = std::move(id); descriptor_.title = std::move(title);
        descriptor_.anchor = options.anchor; descriptor_.priority = options.priority;
        descriptor_.width = options.width; descriptor_.height = options.height; descriptor_.input = options.input;
        descriptor_.visible = options.visible; visible_.store(options.visible);
        queue_.setVisible(options.visible);
        dimensions_.store((uint64_t{options.width} << 32) | options.height);
    }
    HtmlSidePanel& icon(std::string spec) {
        mutableMetadata(); if (spec.size() > 63 || !TitanHtmlUi::validUtf8(spec) || spec.find('\0') != std::string::npos)
            throw std::invalid_argument("HTML icon spec must be UTF-8 and at most 63 bytes");
        descriptor_.icon = std::move(spec); return *this;
    }
    HtmlSidePanel& iconColor(uint32_t argb) { mutableMetadata(); descriptor_.iconColor = argb; return *this; }
    HtmlSidePanel& image(const uint8_t* bytes, size_t size) {
        mutableMetadata(); if (!bytes || size < 8 || size > TitanHtmlUi::kMaxIconBytes ||
            bytes[0] != 137 || bytes[1] != 'P' || bytes[2] != 'N' || bytes[3] != 'G' || bytes[4] != 13 || bytes[5] != 10 || bytes[6] != 26 || bytes[7] != 10)
            throw std::invalid_argument("HTML icon must be embedded PNG bytes (at most 256 KiB)");
        descriptor_.iconPng.assign(bytes, bytes + size); return *this;
    }
    // False means validation, overflow, or sequence exhaustion. No old message
    // is evicted on overflow; callers can retry or publish coalesced state.
    bool setState(std::string json) { return queue_.setState(std::move(json)); }
    bool postMessage(std::string type, std::string payload, std::string correlationId = {}) {
        return queue_.postMessage(std::move(type), std::move(payload), std::move(correlationId));
    }
    HtmlSidePanel& onMessage(MessageFn callback) { mutableMetadata(); callback_ = std::move(callback); return *this; }
    const std::string& id() const noexcept { return descriptor_.id; }
    const TitanHtmlUi::Bundle& bundle() const noexcept { return bundle_; }
    const TitanHtmlUi::SurfaceDescriptor& descriptor() const noexcept { return descriptor_; }
    TitanHtmlUi::UpdateQueue& _updates() noexcept { return queue_; }
    void _freeze() noexcept { frozen_ = true; }
    bool _visible() const noexcept { return visible_.load(); }
    std::pair<uint32_t, uint32_t> _size() const noexcept {
        const auto size = dimensions_.load(); return {static_cast<uint32_t>(size >> 32), static_cast<uint32_t>(size)};
    }
    bool _dispatch(uint64_t activation, std::string_view json) {
        TitanHtmlUi::Message message;
        if (!queue_.accepts(activation) || !TitanHtmlUi::parseMessage(json, message)) return false;
        if (callback_) callback_(message); return true;
    }
protected:
    void setVisibleImpl(bool visible) { queue_.setVisible(visible); visible_.store(visible); }
    bool setSizeImpl(uint32_t width, uint32_t height) noexcept {
        if (width < 80 || width > 600 || height < 24 || height > 600) return false;
        dimensions_.store((uint64_t{width} << 32) | height); return true;
    }
private:
    void mutableMetadata() const { if (frozen_) throw std::logic_error("HTML metadata and callbacks must be configured in the plugin constructor"); }
    const TitanHtmlUi::Bundle bundle_;
    TitanHtmlUi::SurfaceDescriptor descriptor_;
    TitanHtmlUi::UpdateQueue queue_;
    MessageFn callback_;
    std::atomic<bool> visible_{true};
    std::atomic<uint64_t> dimensions_{(uint64_t{220} << 32) | 160};
    bool frozen_ = false;
};

class HtmlOverlayPanel final : public HtmlSidePanel {
public:
    HtmlOverlayPanel(std::string id, const HtmlPanelBundle& bundle, HtmlOverlayOptions options = {})
        : HtmlSidePanel(id, id, bundle, TitanHtmlUi::SurfaceKind::Overlay, options) {}
    void setVisible(bool visible) { setVisibleImpl(visible); }
    bool setSize(uint32_t width, uint32_t height) noexcept { return setSizeImpl(width, height); }
};
} // namespace titan
