/// @file titan/detail/registrable.h
/// @brief Abstract bases for self-registering plugin members.
///
/// Settings, sections and overlays declared as members of a titan::Plugin
/// subclass push themselves onto the plugin's registry at construction time.
/// The Plugin iterates those registries to serialize settings, dispatch
/// overlays, answer setSetting, etc. Declared here (rather than in setting.h /
/// render.h) so plugin.h can hold pointers to these bases without circular
/// includes.

#pragma once

#include "abi.h"

#include <functional>
#include <string>
#include <utility>

namespace titan {

/// Layer enum raised to the top-level titan namespace so both the declarative
/// Overlay member style (render.h) and the constructor-registered onRender
/// style (plugin.h) can name it without including render.h from plugin.h.
enum class Layer : uint8_t {
    AboveScene   = TitanPluginSdk::RenderLayerAbi::ABOVE_SCENE,
    AboveWidgets = TitanPluginSdk::RenderLayerAbi::ABOVE_WIDGETS,
};

namespace detail {

class SectionBase {
public:
    virtual ~SectionBase() = default;
    virtual const char* key() const = 0;
    virtual void serialize(TitanNativeRecords::Section& out) const = 0;
    /// The section this one is nested in (SDK 143), or null at the top
    /// level. serialize() cannot fill Section::parentIndex itself -- it is a
    /// position in the whole list -- so thunkGetSections resolves this.
    virtual const SectionBase* parentSection() const { return nullptr; }
};

class SettingBase {
public:
    virtual ~SettingBase() = default;
    virtual const char* key() const = 0;
    virtual void serialize(TitanNativeRecords::Setting& out) const = 0;
    /// Apply a new value from the controller. Returns empty string on success,
    /// else a diagnostic written into the PluginApi::setSetting error buffer.
    virtual std::string apply(const TitanNativeRecords::Value& value) = 0;
    /// This setting's current value alone, without the metadata. Used to
    /// hand the value to the host alongside a plugin-authored change
    /// report (SDK 140).
    ///
    /// Every concrete type overrides this to write the value directly. The
    /// default is a correct fallback for a hypothetical out-of-tree
    /// SettingBase, but it materializes a whole TitanNativeRecords::Setting
    /// (kilobytes, most of it fixed-size label and tooltip buffers) to read
    /// one union out of it, so nothing in-tree should reach it.
    virtual void buildValue(TitanNativeRecords::Value& out) const {
        TitanNativeRecords::Setting scratch{};
        serialize(scratch);
        out = scratch.value;
    }
    virtual bool isHidden() const = 0;
    virtual void setHidden(bool v) = 0;
    virtual int32_t position() const = 0;
    /// Restore the setting to its declared default value.
    virtual void reset() = 0;
};

class OverlayBase {
public:
    virtual ~OverlayBase() = default;
    virtual uint8_t overlayLayer() const = 0;
    virtual void invoke() = 0;
};

/// Concrete OverlayBase wrapping a std::function. Used by
/// Plugin::onRender() so plugins can register overlays from their
/// constructor body, where lambdas see a fully-declared class (unlike
/// default member initializers, which some IDEs flag as unresolved).
class CallbackOverlay : public OverlayBase {
public:
    CallbackOverlay(Layer layer, std::function<void()> fn)
        : layer_(layer), fn_(std::move(fn)) {}

    uint8_t overlayLayer() const override {
        return static_cast<uint8_t>(layer_);
    }
    void invoke() override { if (fn_) fn_(); }

private:
    Layer layer_;
    std::function<void()> fn_;
};

}  // namespace detail
}  // namespace titan
