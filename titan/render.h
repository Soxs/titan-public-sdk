/// @file titan/render.h
/// @brief Overlay registration, draw primitives, and frame-phase schedulers.
///
/// A Plugin owns any number of titan::Overlay members, each targeting a
/// specific Layer (AboveScene, AboveWidgets, AboveAll). AboveWidgets sits below
/// native menus and tooltips; AboveAll sits above them. The injected client DLL calls
/// the plugin's renderOverlay(layer) thunk once per layer pass, and the
/// thunk forwards to the subset of the plugin's overlay members whose layer
/// matches. Draw calls inside an overlay go through titan::overlay() which
/// forwards to the active backend's drawing primitives.

#pragma once

#include "actor.h"
#include "client.h"
#include "detail/abi.h"
#include "detail/backend.h"
#include "detail/plugin_context.h"
#include "detail/registrable.h"
#include "plugin.h"
#include "world_area.h"
#include "world_view.h"

#include <functional>
#include <optional>
#include <utility>

namespace titan {

// The Layer enum itself is defined in detail/registrable.h (pulled in via
// plugin.h) so both the declarative Overlay member style here and the
// constructor-registered Plugin::onRender() API can name it without a
// circular dependency between render.h and plugin.h.

/// Polygon shape for the model-outline overlays (`entityOutline` /
/// `tileObjectOutline`). Convex is the clean tight silhouette; Concave hugs
/// the projected vertices and follows the model's concavities. Added in
/// SDK 123; no-ops as Convex on hosts that predate it.
enum class OutlineMode : uint32_t {
    Convex = 0,
    Concave = 1,
};

/// Self-registering overlay member. Create any number on a Plugin subclass --
/// each runs on the layer declared at construction. Two flavours:
///
/// Lambda style (concise, stateless):
/// @code
///   titan::Overlay boxes{this, titan::Layer::AboveScene, [this] {
///       titan::queries::npcs().forEach([](const titan::Npc& n) {
///           titan::overlay().entityBox(n, 0xFF00FF00);
///       });
///   }};
/// @endcode
///
/// Subclass style (stateful overlays):
/// @code
///   struct HudOverlay : titan::Overlay {
///       HudOverlay(titan::Plugin* p) : Overlay(p, titan::Layer::AboveWidgets) {}
///       void render() override { ... }
///   };
///   HudOverlay hud{this};
/// @endcode
class Overlay : public detail::OverlayBase {
public:
    Overlay(Plugin* owner, Layer layer, std::function<void()> fn)
        : layer_(layer), fn_(std::move(fn)) {
        if (owner) owner->_registerOverlay(this);
    }

    Overlay(Plugin* owner, Layer layer)
        : layer_(layer) {
        if (owner) owner->_registerOverlay(this);
    }

    ~Overlay() override = default;

    /// Subclass hook. The lambda form supplies fn_ which the default impl calls.
    virtual void render() {
        if (fn_) fn_();
    }

    Layer layer() const { return layer_; }

    // --- detail::OverlayBase ---
    uint8_t overlayLayer() const override { return static_cast<uint8_t>(layer_); }
    void invoke() override { render(); }

private:
    Layer layer_ = Layer::AboveScene;
    std::function<void()> fn_;
};

// ---------------------------------------------------------------------------
// Screen point helper
// ---------------------------------------------------------------------------

struct ScreenPoint {
    int32_t x = 0;
    int32_t y = 0;
};

// ---------------------------------------------------------------------------
// Target tags
// ---------------------------------------------------------------------------

namespace detail {

/// Kind-field value of a loc in a target tag (0 player, 1 NPC).
inline constexpr int32_t kTargetTagLocKind = 2;

/// One field of the pre-242.1 target-tag layout. Only hosts older than
/// SDK 172, which cannot pack tags, still use it; everything newer gets the
/// running revision's layout from the host.
struct LegacyTargetTagField {
    uint32_t shift;
    uint64_t mask;
};
inline constexpr LegacyTargetTagField kLegacyTargetTagX{0, 0x7F};
inline constexpr LegacyTargetTagField kLegacyTargetTagY{7, 0x7F};
inline constexpr LegacyTargetTagField kLegacyTargetTagPlane{14, 0x3};
inline constexpr LegacyTargetTagField kLegacyTargetTagKind{16, 0x7};
inline constexpr LegacyTargetTagField kLegacyTargetTagIndex{20, 0xFFFF};
inline constexpr LegacyTargetTagField kLegacyTargetTagWorldView{36, 0xFFFF};

inline uint64_t legacyTargetTagField(LegacyTargetTagField field, int32_t value) {
    return (static_cast<uint64_t>(static_cast<uint32_t>(value)) & field.mask) << field.shift;
}

/// Pack a target tag with the host's layout for the running revision, or
/// with the pre-242.1 layout on hosts older than SDK 172. A negative
/// @p worldViewId is the top level.
inline uint64_t packTargetTag(int32_t tileX, int32_t tileY, int32_t plane,
                              int32_t kind, int32_t index, int32_t worldViewId) {
    uint64_t packed = 0;
    if (auto* b = backend();
        b && b->packTargetTag(tileX, tileY, plane, kind, index, worldViewId, &packed)) {
        return packed;
    }
    return legacyTargetTagField(kLegacyTargetTagX, tileX)
         | legacyTargetTagField(kLegacyTargetTagY, tileY)
         | legacyTargetTagField(kLegacyTargetTagPlane, plane)
         | legacyTargetTagField(kLegacyTargetTagKind, kind)
         | legacyTargetTagField(kLegacyTargetTagIndex, index)
         | legacyTargetTagField(kLegacyTargetTagWorldView, worldViewId < 0 ? 0 : worldViewId);
}

}  // namespace detail

// ---------------------------------------------------------------------------
// Draw primitives -- callable from inside Overlay::render()
// ---------------------------------------------------------------------------

class OverlayDraw {
public:
    // --- World space ---
    void tileQuad(int32_t tileX, int32_t tileY, int32_t plane,
                  uint32_t fillColor, uint32_t outlineColor) const {
        if (auto* b = detail::backend()) b->drawTileQuad(tileX, tileY, plane, fillColor, outlineColor);
    }
    void tileQuad(const Tile& tile,
                  uint32_t fillColor, uint32_t outlineColor) const {
        tileQuadInWorldView(tile.worldViewId, tile.x, tile.y, tile.plane, fillColor, outlineColor);
    }
    void tileRegion(int32_t minTX, int32_t minTY, int32_t maxTX, int32_t maxTY,
                    int32_t plane, uint32_t fillColor, uint32_t outlineColor) const {
        if (auto* b = detail::backend())
            b->drawTileRegion(minTX, minTY, maxTX, maxTY, plane, fillColor, outlineColor);
    }
    void tileRegion(const WorldArea& area,
                    uint32_t fillColor, uint32_t outlineColor) const {
        const int32_t width = area.width > 0 ? area.width : 1;
        const int32_t height = area.height > 0 ? area.height : 1;
        worldTileRegionInWorldView(area.worldViewId,
                                   area.x, area.y,
                                   area.x + width - 1, area.y + height - 1,
                                   area.plane, fillColor, outlineColor);
    }
    void tileQuadInWorldView(int32_t worldViewId,
                             int32_t tileX, int32_t tileY, int32_t plane,
                             uint32_t fillColor, uint32_t outlineColor) const {
        if (auto* b = detail::backend()) {
            b->drawTileQuadInWorldView(
                worldViewId, tileX, tileY, plane, fillColor, outlineColor);
        }
    }
    void tileRegionInWorldView(int32_t worldViewId,
                               int32_t minTX, int32_t minTY,
                               int32_t maxTX, int32_t maxTY,
                               int32_t plane,
                               uint32_t fillColor, uint32_t outlineColor) const {
        if (auto* b = detail::backend()) {
            b->drawTileRegionInWorldView(
                worldViewId, minTX, minTY, maxTX, maxTY, plane,
                fillColor, outlineColor);
        }
    }
    void worldTileRegionInWorldView(int32_t worldViewId,
                                    int32_t minWorldX, int32_t minWorldY,
                                    int32_t maxWorldX, int32_t maxWorldY,
                                    int32_t plane,
                                    uint32_t fillColor,
                                    uint32_t outlineColor) const {
        const auto snap = state::client().snapshot();
        if (!snap) return;

        int32_t baseX = snap->baseX;
        int32_t baseY = snap->baseY;
        if (worldViewId == WorldView::TOP_LEVEL) {
            baseX = snap->topLevelBaseX;
            baseY = snap->topLevelBaseY;
        }

        tileRegionInWorldView(worldViewId,
                              minWorldX - baseX,
                              minWorldY - baseY,
                              maxWorldX - baseX,
                              maxWorldY - baseY,
                              plane, fillColor, outlineColor);
    }
    void entityBox(int32_t preciseX, int32_t preciseY, int32_t plane,
                   int32_t tileSize, int32_t height, uint32_t color) const {
        if (auto* b = detail::backend())
            b->drawEntityBox(preciseX, preciseY, plane, tileSize, height, color);
    }
    void entityBoxInWorldView(int32_t worldViewId,
                              int32_t preciseX, int32_t preciseY,
                              int32_t plane, int32_t tileSize,
                              int32_t height, uint32_t color) const {
        const int32_t half = tileSize * 64;
        const int32_t x0 = preciseX - half;
        const int32_t x1 = preciseX + half;
        const int32_t z0 = preciseY - half;
        const int32_t z1 = preciseY + half;
        const int32_t groundY =
            tileHeightInWorldView(worldViewId, preciseX, preciseY, plane);

        auto point = [&](int32_t x, int32_t y, int32_t z) {
            return worldToScreenInWorldView(worldViewId, x, y, z, plane);
        };
        const std::optional<ScreenPoint> bottom[4] = {
            point(x0, groundY, z0),
            point(x1, groundY, z0),
            point(x1, groundY, z1),
            point(x0, groundY, z1),
        };
        const std::optional<ScreenPoint> top[4] = {
            point(x0, groundY - height, z0),
            point(x1, groundY - height, z0),
            point(x1, groundY - height, z1),
            point(x0, groundY - height, z1),
        };

        auto line = [&](const std::optional<ScreenPoint>& a,
                        const std::optional<ScreenPoint>& b) {
            if (a && b) screenLine(a->x, a->y, b->x, b->y, color, 1.5f);
        };
        for (int i = 0; i < 4; ++i) {
            line(bottom[i], bottom[(i + 1) % 4]);
            line(top[i], top[(i + 1) % 4]);
            line(bottom[i], top[i]);
        }
    }
    /// Draw a single-tile box around @p npc at its precise centre position.
    ///
    /// @note This convenience overload intentionally stays 1x1. To cover an
    ///       NPC's full footprint, call the 6-arg raw overload with the live
    ///       NPC size.
    void entityBox(const Npc& npc, uint32_t color, int32_t height = 240) const {
        entityBoxInWorldView(npc.worldViewId(), npc.preciseX(), npc.preciseY(),
                             npc.plane(), 1, height, color);
    }
    void entityBox(const Player& p, uint32_t color, int32_t height = 240) const {
        entityBoxInWorldView(p.worldViewId(), p.preciseX(), p.preciseY(),
                             p.plane(), 1, height, color);
    }

    /// Build the engine's target tag (`ModelTypecodeType`, the key the
    /// scene, menu builders and mouse picker share) for an actor from its
    /// (entityType, hashIndex, scene tile, plane). @p entityType: 0 player,
    /// 1 NPC. The host packs it with the layout of the revision it runs --
    /// 242.1 widened the index and moved the WorldView id -- so nothing here
    /// is compiled in. Hosts older than SDK 172 get the pre-242.1 layout.
    /// Not constexpr since SDK 172.
    static uint64_t buildActorTypecode(int32_t entityType,
                                       int32_t hashIndex,
                                       int32_t tileX, int32_t tileY,
                                       int32_t plane) {
        // An actor with hashIndex < 0 (unresolved) has no well-defined
        // typecode; fall back to 0 so the host skips typecode lookup.
        if (hashIndex < 0) return 0;
        return detail::packTargetTag(tileX, tileY, plane, entityType, hashIndex,
                                     WorldView::TOP_LEVEL);
    }

    /// Build the native loc picking key from the scene-tag fields.
    ///
    /// Older SDK docs described loc-layer bits in the picker key. Win
    /// 238.5 research confirmed native loc picking uses the raw scene tag:
    /// tile, plane, entity type, loc id, and WorldView id. @p layer is kept
    /// for source compatibility and is intentionally ignored. Packed by the
    /// host like buildActorTypecode; prefer `TileObject::packedId()`, the
    /// native tag itself, when you have the object.
    static uint64_t buildLocTypecode(int32_t locId,
                                     int32_t layer,
                                     int32_t tileX, int32_t tileY,
                                     int32_t plane,
                                     int32_t worldViewId = WorldView::TOP_LEVEL) {
        (void)layer;
        if (locId < 0) return 0;
        return detail::packTargetTag(tileX, tileY, plane, detail::kTargetTagLocKind,
                                     locId, worldViewId);
    }

    /// Draw the accurate world-space AABB clickbox around @p npc. Reads
    /// the per-entity AABB from the host's typecode-keyed picking cache
    /// (populated by the mouse-picking dispatcher hooks), projects the
    /// 8 corners through W2S, and renders 12 wireframe edges plus
    /// optional translucent face fills. Silent no-op when the host DLL
    /// predates SDK 34 or the current binary's dispatcher offsets
    /// haven't been detected.
    void entityClickbox(const Npc& npc, uint32_t outline, uint32_t fill = 0) const {
        if (auto* b = detail::backend()) {
            const uint64_t tc = buildActorTypecode(
                    /*entityType=*/1, npc.hashIndex(),
                    npc.preciseX() >> 7, npc.preciseY() >> 7, npc.plane());
            b->drawEntityClickbox(npc.entityPtr(), tc, outline, fill);
        }
    }
    void entityClickbox(const Player& p, uint32_t outline, uint32_t fill = 0) const {
        if (auto* b = detail::backend()) {
            const uint64_t tc = buildActorTypecode(
                    /*entityType=*/0, p.hashIndex(),
                    p.preciseX() >> 7, p.preciseY() >> 7, p.plane());
            b->drawEntityClickbox(p.entityPtr(), tc, outline, fill);
        }
    }
    /// Raw entry for plugin code that already holds the entity pointer
    /// and its typecode. The typecode-keyed picking cache is the only
    /// picking evidence the host consults; passing @p typecode = 0
    /// skips it, leaving only the GraphNode tiers and (for actors) the
    /// approximate synthesized footprint.
    void entityClickbox(uint64_t entityPtr, uint64_t typecode,
                        uint32_t outline, uint32_t fill = 0) const {
        if (auto* b = detail::backend()) b->drawEntityClickbox(entityPtr, typecode, outline, fill);
    }

    /// Draw the accurate world-space AABB clickbox around @p obj (a scene
    /// object -- wall, decor, standing loc, ground decor). Uses the raw
    /// scene tag carried by `TileObjectState::packedId`, which is the key
    /// the native picker dispatcher records for locs.
    void tileObjectClickbox(const TileObject& obj, uint32_t outline,
                            uint32_t fill = 0) const {
        if (auto* b = detail::backend()) {
            const uint64_t tc = obj.packedId();
            b->drawTileObjectClickbox(obj.entityPtr(), tc, outline, fill);
        }
    }
    void tileObjectClickbox(uint64_t locPtr, uint64_t typecode,
                            uint32_t outline, uint32_t fill = 0) const {
        if (auto* b = detail::backend()) b->drawTileObjectClickbox(locPtr, typecode, outline, fill);
    }

    /// Draw the 2D convex hull of the entity's projected AABB. This is
    /// the recommended "highlight this entity" primitive: one clean
    /// closed polyline with optional translucent fill, no interior
    /// clutter. Backed by the same AABB data as `entityClickbox`; a
    /// future SDK revision may upgrade the source to real mesh
    /// vertices when the analyzer can extract per-instance geometry.
    void entityHull(const Npc& npc, uint32_t outline, uint32_t fill = 0) const {
        if (auto* b = detail::backend()) {
            const uint64_t tc = buildActorTypecode(
                    /*entityType=*/1, npc.hashIndex(),
                    npc.preciseX() >> 7, npc.preciseY() >> 7, npc.plane());
            b->drawEntityHull(npc.entityPtr(), tc, outline, fill);
        }
    }
    void entityHull(const Player& p, uint32_t outline, uint32_t fill = 0) const {
        if (auto* b = detail::backend()) {
            const uint64_t tc = buildActorTypecode(
                    /*entityType=*/0, p.hashIndex(),
                    p.preciseX() >> 7, p.preciseY() >> 7, p.plane());
            b->drawEntityHull(p.entityPtr(), tc, outline, fill);
        }
    }
    void entityHull(uint64_t entityPtr, uint64_t typecode,
                    uint32_t outline, uint32_t fill = 0) const {
        if (auto* b = detail::backend()) b->drawEntityHull(entityPtr, typecode, outline, fill);
    }
    void tileObjectHull(const TileObject& obj, uint32_t outline,
                        uint32_t fill = 0) const {
        if (auto* b = detail::backend()) {
            const uint64_t tc = obj.packedId();
            b->drawTileObjectHull(obj.entityPtr(), tc, outline, fill);
        }
    }
    void tileObjectHull(uint64_t locPtr, uint64_t typecode,
                        uint32_t outline, uint32_t fill = 0) const {
        if (auto* b = detail::backend()) b->drawTileObjectHull(locPtr, typecode, outline, fill);
    }

    /// Draw the TRUE model silhouette: the convex hull of the entity's
    /// actual projected model vertices, rotated and placed exactly where
    /// the engine renders it. Tighter than `entityHull` (which hulls the 8
    /// AABB corners) -- irregular / tall-thin models get a real outline.
    /// Silent no-op on hosts older than SDK 122 (null fn pointer) or when
    /// the model handle isn't bound this frame. Requires the host's
    /// model-AABB hook to be active (revisions with `Model` geometry).
    /// Old-host safety: `IBackend` is never null and always defines these
    /// virtuals; the real "host predates SDK 122" guard is the null
    /// HostApi function pointer, checked one layer down in
    /// `ExternalBackend::drawEntityOutline` (backend_external.h), which
    /// no-ops when `api_->drawEntityOutline` is null. So these facades
    /// call straight through, exactly like the entityHull facades above.
    void entityOutline(const Npc& npc, uint32_t outline, uint32_t fill = 0,
                       OutlineMode mode = OutlineMode::Convex) const {
        if (auto* b = detail::backend()) {
            const uint64_t tc = buildActorTypecode(
                    /*entityType=*/1, npc.hashIndex(),
                    npc.preciseX() >> 7, npc.preciseY() >> 7, npc.plane());
            b->drawEntityOutline(npc.entityPtr(), tc, outline, fill,
                                 static_cast<uint32_t>(mode));
        }
    }
    void entityOutline(const Player& p, uint32_t outline, uint32_t fill = 0,
                       OutlineMode mode = OutlineMode::Convex) const {
        if (auto* b = detail::backend()) {
            const uint64_t tc = buildActorTypecode(
                    /*entityType=*/0, p.hashIndex(),
                    p.preciseX() >> 7, p.preciseY() >> 7, p.plane());
            b->drawEntityOutline(p.entityPtr(), tc, outline, fill,
                                 static_cast<uint32_t>(mode));
        }
    }
    void entityOutline(uint64_t entityPtr, uint64_t typecode,
                       uint32_t outline, uint32_t fill = 0,
                       OutlineMode mode = OutlineMode::Convex) const {
        if (auto* b = detail::backend())
            b->drawEntityOutline(entityPtr, typecode, outline, fill,
                                 static_cast<uint32_t>(mode));
    }
    void tileObjectOutline(const TileObject& obj, uint32_t outline,
                           uint32_t fill = 0,
                           OutlineMode mode = OutlineMode::Convex) const {
        if (auto* b = detail::backend())
            b->drawTileObjectOutline(obj.entityPtr(), obj.packedId(), outline, fill,
                                     static_cast<uint32_t>(mode));
    }
    void tileObjectOutline(uint64_t locPtr, uint64_t typecode,
                           uint32_t outline, uint32_t fill = 0,
                           OutlineMode mode = OutlineMode::Convex) const {
        if (auto* b = detail::backend())
            b->drawTileObjectOutline(locPtr, typecode, outline, fill,
                                     static_cast<uint32_t>(mode));
    }

    void textAtWorld(int32_t worldX, int32_t worldY, int32_t worldZ,
                     const char* text, uint32_t color, bool centered = true) const {
        if (auto* b = detail::backend())
            b->drawTextAtWorld(worldX, worldY, worldZ, text, color, centered ? 1 : 0);
    }
    void textAtWorld(const WorldPos& p, const char* text, uint32_t color,
                     bool centered = true) const {
        textAtWorld(p.x, p.y, p.z, text, color, centered);
    }
    void textAtWorldInWorldView(int32_t worldViewId,
                                int32_t preciseX, int32_t worldY,
                                int32_t preciseY, int32_t plane,
                                const char* text, uint32_t color,
                                bool centered = true) const {
        if (auto* b = detail::backend()) {
            b->drawTextAtWorldInWorldView(
                worldViewId, preciseX, worldY, preciseY, plane,
                text, color, centered ? 1 : 0);
        }
    }
    void textAtWorld(const LocalPoint& p, int32_t worldY, int32_t plane,
                     const char* text, uint32_t color,
                     bool centered = true) const {
        textAtWorldInWorldView(p.worldViewId, p.x, worldY, p.y, plane,
                               text, color, centered);
    }
    template <typename T>
    void textAtWorld(const Locatable<T>& locatable, int32_t worldY,
                     const char* text, uint32_t color,
                     bool centered = true) const {
        const auto& entity = static_cast<const T&>(locatable);
        textAtWorld(entity.localPoint(), worldY, entity.plane(), text, color, centered);
    }
    void textAtWorldTileInWorldView(int32_t worldViewId,
                                    int32_t worldTileX, int32_t worldY,
                                    int32_t worldTileY, int32_t plane,
                                    const char* text, uint32_t color,
                                    bool centered = true) const {
        const auto snap = state::client().snapshot();
        if (!snap) return;

        int32_t baseX = snap->baseX;
        int32_t baseY = snap->baseY;
        if (worldViewId == WorldView::TOP_LEVEL) {
            baseX = snap->topLevelBaseX;
            baseY = snap->topLevelBaseY;
        }

        textAtWorldInWorldView(worldViewId,
                               (worldTileX - baseX) * 128,
                               worldY,
                               (worldTileY - baseY) * 128,
                               plane, text, color, centered);
    }

    // --- Screen space ---
    void screenText(int32_t x, int32_t y, const char* text, uint32_t color) const {
        if (auto* b = detail::backend()) b->drawScreenText(x, y, text, color);
    }
    void screenRect(int32_t x, int32_t y, int32_t w, int32_t h_, uint32_t color) const {
        if (auto* b = detail::backend()) b->drawScreenRect(x, y, w, h_, color);
    }
    void screenLine(int32_t x1, int32_t y1, int32_t x2, int32_t y2,
                    uint32_t color, float thickness = 1.0f) const {
        if (auto* b = detail::backend()) b->drawScreenLine(x1, y1, x2, y2, color, thickness);
    }

    // --- Projection ---
    std::optional<ScreenPoint> worldToScreen(int32_t worldX, int32_t worldY, int32_t worldZ) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        int32_t sx = 0, sy = 0;
        if (!b->worldToScreen(worldX, worldY, worldZ, &sx, &sy)) return std::nullopt;
        return ScreenPoint{sx, sy};
    }
    std::optional<ScreenPoint> worldToScreen(const WorldPos& p) const {
        return worldToScreen(p.x, p.y, p.z);
    }
    std::optional<ScreenPoint> worldToScreenInWorldView(int32_t worldViewId,
                                                         int32_t preciseX,
                                                         int32_t worldY,
                                                         int32_t preciseY,
                                                         int32_t plane) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        int32_t sx = 0, sy = 0;
        if (!b->worldToScreenInWorldView(
                worldViewId, preciseX, worldY, preciseY, plane, &sx, &sy)) {
            return std::nullopt;
        }
        return ScreenPoint{sx, sy};
    }
    std::optional<ScreenPoint> worldToScreen(const LocalPoint& p,
                                             int32_t worldY,
                                             int32_t plane) const {
        return worldToScreenInWorldView(p.worldViewId, p.x, worldY, p.y, plane);
    }
    template <typename T>
    std::optional<ScreenPoint> worldToScreen(const Locatable<T>& locatable,
                                             int32_t worldY) const {
        const auto& entity = static_cast<const T&>(locatable);
        return worldToScreen(entity.localPoint(), worldY, entity.plane());
    }
    std::optional<ScreenPoint> tileToScreen(int32_t tileX, int32_t tileY,
                                             int32_t plane, int32_t heightOffset = 0) const {
        auto* b = detail::backend();
        if (!b) return std::nullopt;
        int32_t sx = 0, sy = 0;
        if (!b->tileToScreen(tileX, tileY, plane, heightOffset, &sx, &sy)) return std::nullopt;
        return ScreenPoint{sx, sy};
    }
    int32_t tileHeight(int32_t preciseX, int32_t preciseY, int32_t plane) const {
        auto* b = detail::backend();
        return b ? b->getTileHeight(preciseX, preciseY, plane) : 0;
    }
    int32_t tileHeightInWorldView(int32_t worldViewId,
                                  int32_t preciseX, int32_t preciseY,
                                  int32_t plane) const {
        auto* b = detail::backend();
        return b ? b->getTileHeightInWorldView(worldViewId, preciseX, preciseY, plane) : 0;
    }
    int32_t tileHeight(const LocalPoint& p, int32_t plane) const {
        return tileHeightInWorldView(p.worldViewId, p.x, p.y, plane);
    }
    int32_t worldTileHeightInWorldView(int32_t worldViewId,
                                       int32_t worldTileX,
                                       int32_t worldTileY,
                                       int32_t plane) const {
        const auto snap = state::client().snapshot();
        if (!snap) return 0;

        int32_t baseX = snap->baseX;
        int32_t baseY = snap->baseY;
        if (worldViewId == WorldView::TOP_LEVEL) {
            baseX = snap->topLevelBaseX;
            baseY = snap->topLevelBaseY;
        }

        return tileHeightInWorldView(
            worldViewId,
            (worldTileX - baseX) * 128,
            (worldTileY - baseY) * 128,
            plane);
    }
};

inline OverlayDraw overlay() { return OverlayDraw{}; }

// ---------------------------------------------------------------------------
// Frame-phase schedulers
// ---------------------------------------------------------------------------

namespace detail {

// SDK 116: invoke and free are split. The host owns the free via the cleanup
// callback, which it fires exactly once -- after the invoke thunk ran, or
// when the queued request was dropped without running (previously the
// std::function leaked on every dropped task).
struct ScheduledCallback {
    IBackend* backend = nullptr;
    const TitanPluginSdk::HostApi* host = nullptr;
    Plugin* owner = nullptr;
    std::function<void()> callback;
};

inline void scheduleThunkInvoke(void* userData) {
    auto* task = static_cast<ScheduledCallback*>(userData);
    if (!task) return;
    ScopedBackendContext context(task->backend, task->host, task->owner);
    if (task->callback) task->callback();
}

inline void scheduleThunkCleanup(void* userData) {
    auto* task = static_cast<ScheduledCallback*>(userData);
    if (!task) return;
    ScopedBackendContext context(task->backend, task->host, task->owner);
    delete task;
}

inline void scheduleOwnedCallback(IBackend* backend,
                                  const TitanPluginSdk::HostApi* host,
                                  Plugin* owner, std::function<void()> fn,
                                  bool render) {
    if (!backend) return;
    auto* task = new ScheduledCallback{backend, host, owner, std::move(fn)};
    if (render) {
        backend->runOnRender(&scheduleThunkInvoke, task, &scheduleThunkCleanup);
    } else {
        backend->runOnClientTick(&scheduleThunkInvoke, task, &scheduleThunkCleanup);
    }
}

}  // namespace detail

/// Enqueue a callback to run during the next client-tick drain (same timing
/// as native doAction). The callback is heap-allocated and freed by the
/// host's cleanup exactly once, whether or not the task ran.
inline void runOnClientTick(std::function<void()> fn) {
    detail::scheduleOwnedCallback(detail::backend(), detail::host(),
                                  detail::currentPlugin(), std::move(fn), false);
}

/// Enqueue a callback to run during the next render frame (SwapBuffers).
inline void runOnRender(std::function<void()> fn) {
    detail::scheduleOwnedCallback(detail::backend(), detail::host(),
                                  detail::currentPlugin(), std::move(fn), true);
}

/// Explicit-owner form for code outside a plugin dispatch callback. The owner
/// must remain alive for this call (worker code should hold a lifetime lease).
inline void runOnClientTick(Plugin& owner, std::function<void()> fn) {
    auto* backend = owner._sdkBackend()
        ? static_cast<detail::IBackend*>(owner._sdkBackend()) : owner.backend();
    detail::scheduleOwnedCallback(backend, owner.host(), &owner,
                                  std::move(fn), false);
}

inline void runOnRender(Plugin& owner, std::function<void()> fn) {
    auto* backend = owner._sdkBackend()
        ? static_cast<detail::IBackend*>(owner._sdkBackend()) : owner.backend();
    detail::scheduleOwnedCallback(backend, owner.host(), &owner,
                                  std::move(fn), true);
}

}  // namespace titan
