package net.titan.api.overlay;

import net.titan.api.NPC;
import net.titan.api.Client;
import net.titan.api.LocalPoint;
import net.titan.api.Locatable;
import net.titan.api.Player;
import net.titan.api.ScreenPoint;
import net.titan.api.TileObject;
import net.titan.api.WorldView;
import net.titan.api.WorldPoint;
import net.titan.api.Titan;
import net.titan.api.internal.TitanRuntime;

import java.util.Optional;

public final class OverlayDraw {
    private static final OverlayDraw INSTANCE = new OverlayDraw();

    private OverlayDraw() {}

    public static OverlayDraw current() {
        return INSTANCE;
    }

    public void tileQuad(int tileX, int tileY, int plane,
                         int fillColor, int outlineColor) {
        TitanRuntime.getOverlayBackend().tileQuad(
            tileX, tileY, plane, fillColor, outlineColor);
    }

    public void tileRegion(int minTileX, int minTileY, int maxTileX, int maxTileY,
                           int plane, int fillColor, int outlineColor) {
        TitanRuntime.getOverlayBackend().tileRegion(
            minTileX, minTileY, maxTileX, maxTileY, plane, fillColor, outlineColor);
    }

    public void tileQuadInWorldView(int worldViewId, int tileX, int tileY,
                                    int plane, int fillColor, int outlineColor) {
        TitanRuntime.getOverlayBackend().tileQuadInWorldView(
            worldViewId, tileX, tileY, plane, fillColor, outlineColor);
    }

    public void tileRegionInWorldView(int worldViewId, int minTileX, int minTileY,
                                      int maxTileX, int maxTileY, int plane,
                                      int fillColor, int outlineColor) {
        TitanRuntime.getOverlayBackend().tileRegionInWorldView(
            worldViewId, minTileX, minTileY, maxTileX, maxTileY, plane,
            fillColor, outlineColor);
    }

    public void worldTileRegionInWorldView(int worldViewId,
                                           int minWorldX, int minWorldY,
                                           int maxWorldX, int maxWorldY,
                                           int plane, int fillColor,
                                           int outlineColor) {
        Client client = Titan.client();
        int baseX = worldViewId == WorldView.TOP_LEVEL ? client.topLevelBaseX() : client.baseX();
        int baseY = worldViewId == WorldView.TOP_LEVEL ? client.topLevelBaseY() : client.baseY();
        tileRegionInWorldView(worldViewId,
            minWorldX - baseX, minWorldY - baseY,
            maxWorldX - baseX, maxWorldY - baseY,
            plane, fillColor, outlineColor);
    }

    public void entityBox(int preciseX, int preciseY, int plane,
                          int tileSize, int height, int color) {
        TitanRuntime.getOverlayBackend().entityBox(
            preciseX, preciseY, plane, tileSize, height, color);
    }

    private void entityBoxInWorldView(int worldViewId, int preciseX,
                                      int preciseY, int plane, int tileSize,
                                      int height, int color) {
        int half = tileSize * 64;
        int x0 = preciseX - half;
        int x1 = preciseX + half;
        int z0 = preciseY - half;
        int z1 = preciseY + half;
        int groundY = tileHeightInWorldView(worldViewId, preciseX, preciseY, plane);

        Optional<ScreenPoint>[] bottom = new Optional[] {
            worldToScreenInWorldView(worldViewId, x0, groundY, z0, plane),
            worldToScreenInWorldView(worldViewId, x1, groundY, z0, plane),
            worldToScreenInWorldView(worldViewId, x1, groundY, z1, plane),
            worldToScreenInWorldView(worldViewId, x0, groundY, z1, plane)
        };
        Optional<ScreenPoint>[] top = new Optional[] {
            worldToScreenInWorldView(worldViewId, x0, groundY - height, z0, plane),
            worldToScreenInWorldView(worldViewId, x1, groundY - height, z0, plane),
            worldToScreenInWorldView(worldViewId, x1, groundY - height, z1, plane),
            worldToScreenInWorldView(worldViewId, x0, groundY - height, z1, plane)
        };

        for (int i = 0; i < 4; ++i) {
            lineIfPresent(bottom[i], bottom[(i + 1) % 4], color);
            lineIfPresent(top[i], top[(i + 1) % 4], color);
            lineIfPresent(bottom[i], top[i], color);
        }
    }

    private void lineIfPresent(Optional<ScreenPoint> a, Optional<ScreenPoint> b, int color) {
        if (!a.isPresent() || !b.isPresent()) return;
        screenLine(a.get().x(), a.get().y(), b.get().x(), b.get().y(), color, 1.5f);
    }

    public void entityBox(NPC npc, int color) {
        entityBox(npc, color, 240);
    }

    public void entityBox(NPC npc, int color, int height) {
        if (npc == null) return;
        entityBoxInWorldView(npc.worldViewId(), npc.preciseX(), npc.preciseY(),
            npc.plane(), 1, height, color);
    }

    public void entityBox(Player player, int color) {
        entityBox(player, color, 240);
    }

    public void entityBox(Player player, int color, int height) {
        if (player == null) return;
        entityBoxInWorldView(player.worldViewId(), player.preciseX(), player.preciseY(),
            player.plane(), 1, height, color);
    }

    public void entityClickbox(NPC npc, int outline) {
        entityClickbox(npc, outline, 0);
    }

    public void entityClickbox(NPC npc, int outline, int fill) {
        if (npc == null) return;
        long typecode = buildActorTypecode(
            1, npc.hashIndex(), npc.preciseX() >> 7, npc.preciseY() >> 7, npc.plane());
        entityClickbox(npc.entityPtr(), typecode, outline, fill);
    }

    public void entityClickbox(Player player, int outline) {
        entityClickbox(player, outline, 0);
    }

    public void entityClickbox(Player player, int outline, int fill) {
        if (player == null) return;
        long typecode = buildActorTypecode(
            0, player.hashIndex(), player.preciseX() >> 7, player.preciseY() >> 7, player.plane());
        entityClickbox(player.entityPtr(), typecode, outline, fill);
    }

    public void entityClickbox(long entityPtr, long typecode, int outline, int fill) {
        TitanRuntime.getOverlayBackend().entityClickbox(entityPtr, typecode, outline, fill);
    }

    public void tileObjectClickbox(TileObject object, int outline) {
        tileObjectClickbox(object, outline, 0);
    }

    public void tileObjectClickbox(TileObject object, int outline, int fill) {
        if (object == null) return;
        long typecode = object.packedId();
        tileObjectClickbox(object.entityPtr(), typecode, outline, fill);
    }

    public void tileObjectClickbox(long locPtr, long typecode, int outline, int fill) {
        TitanRuntime.getOverlayBackend().tileObjectClickbox(locPtr, typecode, outline, fill);
    }

    public void entityHull(NPC npc, int outline) {
        entityHull(npc, outline, 0);
    }

    public void entityHull(NPC npc, int outline, int fill) {
        if (npc == null) return;
        long typecode = buildActorTypecode(
            1, npc.hashIndex(), npc.preciseX() >> 7, npc.preciseY() >> 7, npc.plane());
        entityHull(npc.entityPtr(), typecode, outline, fill);
    }

    public void entityHull(Player player, int outline) {
        entityHull(player, outline, 0);
    }

    public void entityHull(Player player, int outline, int fill) {
        if (player == null) return;
        long typecode = buildActorTypecode(
            0, player.hashIndex(), player.preciseX() >> 7, player.preciseY() >> 7, player.plane());
        entityHull(player.entityPtr(), typecode, outline, fill);
    }

    public void entityHull(long entityPtr, long typecode, int outline, int fill) {
        TitanRuntime.getOverlayBackend().entityHull(entityPtr, typecode, outline, fill);
    }

    public void tileObjectHull(TileObject object, int outline) {
        tileObjectHull(object, outline, 0);
    }

    public void tileObjectHull(TileObject object, int outline, int fill) {
        if (object == null) return;
        long typecode = object.packedId();
        tileObjectHull(object.entityPtr(), typecode, outline, fill);
    }

    public void tileObjectHull(long locPtr, long typecode, int outline, int fill) {
        TitanRuntime.getOverlayBackend().tileObjectHull(locPtr, typecode, outline, fill);
    }

    /**
     * Draw the TRUE model silhouette: the 2D convex hull of the entity's
     * actual projected model vertices, rotated and placed exactly where the
     * engine renders it. Tighter than {@link #entityHull} (which hulls the 8
     * AABB corners) -- irregular / tall-thin models get a real outline. No-op
     * on hosts older than SDK 122 or when the model handle isn't bound this
     * frame (host model-AABB hook inactive / revision without Model geometry).
     */
    public void entityOutline(NPC npc, int outline) {
        entityOutline(npc, outline, 0, OutlineMode.CONVEX);
    }

    public void entityOutline(NPC npc, int outline, int fill) {
        entityOutline(npc, outline, fill, OutlineMode.CONVEX);
    }

    public void entityOutline(NPC npc, int outline, int fill, OutlineMode mode) {
        if (npc == null) return;
        long typecode = buildActorTypecode(
            1, npc.hashIndex(), npc.preciseX() >> 7, npc.preciseY() >> 7, npc.plane());
        entityOutline(npc.entityPtr(), typecode, outline, fill, mode);
    }

    public void entityOutline(Player player, int outline) {
        entityOutline(player, outline, 0, OutlineMode.CONVEX);
    }

    public void entityOutline(Player player, int outline, int fill) {
        entityOutline(player, outline, fill, OutlineMode.CONVEX);
    }

    public void entityOutline(Player player, int outline, int fill, OutlineMode mode) {
        if (player == null) return;
        long typecode = buildActorTypecode(
            0, player.hashIndex(), player.preciseX() >> 7, player.preciseY() >> 7, player.plane());
        entityOutline(player.entityPtr(), typecode, outline, fill, mode);
    }

    public void entityOutline(long entityPtr, long typecode, int outline, int fill) {
        entityOutline(entityPtr, typecode, outline, fill, OutlineMode.CONVEX);
    }

    public void entityOutline(long entityPtr, long typecode, int outline, int fill,
                              OutlineMode mode) {
        TitanRuntime.getOverlayBackend().entityOutline(
            entityPtr, typecode, outline, fill,
            (mode == null ? OutlineMode.CONVEX : mode).value());
    }

    public void tileObjectOutline(TileObject object, int outline) {
        tileObjectOutline(object, outline, 0, OutlineMode.CONVEX);
    }

    public void tileObjectOutline(TileObject object, int outline, int fill) {
        tileObjectOutline(object, outline, fill, OutlineMode.CONVEX);
    }

    public void tileObjectOutline(TileObject object, int outline, int fill, OutlineMode mode) {
        if (object == null) return;
        long typecode = object.packedId();
        tileObjectOutline(object.entityPtr(), typecode, outline, fill, mode);
    }

    public void tileObjectOutline(long locPtr, long typecode, int outline, int fill) {
        tileObjectOutline(locPtr, typecode, outline, fill, OutlineMode.CONVEX);
    }

    public void tileObjectOutline(long locPtr, long typecode, int outline, int fill,
                                  OutlineMode mode) {
        TitanRuntime.getOverlayBackend().tileObjectOutline(
            locPtr, typecode, outline, fill,
            (mode == null ? OutlineMode.CONVEX : mode).value());
    }

    public void textAtWorld(int worldX, int worldY, int worldZ,
                            String text, int color) {
        textAtWorld(worldX, worldY, worldZ, text, color, true);
    }

    public void textAtWorld(int worldX, int worldY, int worldZ,
                            String text, int color, boolean centered) {
        TitanRuntime.getOverlayBackend().textAtWorld(
            worldX, worldY, worldZ, text, color, centered);
    }

    public void textAtWorld(WorldPoint point, String text, int color) {
        textAtWorld(point, text, color, true);
    }

    public void textAtWorld(WorldPoint point, String text, int color, boolean centered) {
        if (point == null) return;
        textAtWorld(point.x(), point.y(), point.z(), text, color, centered);
    }

    public void textAtWorldInWorldView(int worldViewId, int preciseX, int worldY,
                                       int preciseY, int plane, String text,
                                       int color) {
        textAtWorldInWorldView(worldViewId, preciseX, worldY, preciseY, plane,
                               text, color, true);
    }

    public void textAtWorldInWorldView(int worldViewId, int preciseX, int worldY,
                                       int preciseY, int plane, String text,
                                       int color, boolean centered) {
        TitanRuntime.getOverlayBackend().textAtWorldInWorldView(
            worldViewId, preciseX, worldY, preciseY, plane, text, color, centered);
    }

    public void textAtWorld(LocalPoint point, int worldY, int plane,
                            String text, int color) {
        textAtWorld(point, worldY, plane, text, color, true);
    }

    public void textAtWorld(LocalPoint point, int worldY, int plane,
                            String text, int color, boolean centered) {
        if (point == null) return;
        textAtWorldInWorldView(point.worldViewId(), point.x(), worldY,
                               point.y(), plane, text, color, centered);
    }

    public void textAtWorld(Locatable<?> locatable, int worldY,
                            String text, int color) {
        textAtWorld(locatable, worldY, text, color, true);
    }

    public void textAtWorld(Locatable<?> locatable, int worldY,
                            String text, int color, boolean centered) {
        if (locatable == null) return;
        textAtWorld(locatable.localPoint(), worldY, locatable.plane(),
                    text, color, centered);
    }

    public void textAtWorldTileInWorldView(int worldViewId,
                                           int worldTileX, int worldY,
                                           int worldTileY, int plane,
                                           String text, int color) {
        textAtWorldTileInWorldView(worldViewId, worldTileX, worldY, worldTileY,
                                   plane, text, color, true);
    }

    public void textAtWorldTileInWorldView(int worldViewId,
                                           int worldTileX, int worldY,
                                           int worldTileY, int plane,
                                           String text, int color,
                                           boolean centered) {
        Client client = Titan.client();
        int baseX = worldViewId == WorldView.TOP_LEVEL ? client.topLevelBaseX() : client.baseX();
        int baseY = worldViewId == WorldView.TOP_LEVEL ? client.topLevelBaseY() : client.baseY();
        textAtWorldInWorldView(worldViewId,
            (worldTileX - baseX) * 128, worldY, (worldTileY - baseY) * 128,
            plane, text, color, centered);
    }

    public void screenText(int x, int y, String text, int color) {
        TitanRuntime.getOverlayBackend().screenText(x, y, text, color);
    }

    public void screenRect(int x, int y, int width, int height, int color) {
        TitanRuntime.getOverlayBackend().screenRect(x, y, width, height, color);
    }

    public void screenLine(int x1, int y1, int x2, int y2, int color) {
        screenLine(x1, y1, x2, y2, color, 1.0f);
    }

    public void screenLine(int x1, int y1, int x2, int y2, int color, float thickness) {
        TitanRuntime.getOverlayBackend().screenLine(x1, y1, x2, y2, color, thickness);
    }

    public Optional<ScreenPoint> worldToScreen(int worldX, int worldY, int worldZ) {
        return TitanRuntime.getOverlayBackend().worldToScreen(worldX, worldY, worldZ);
    }

    public Optional<ScreenPoint> worldToScreen(WorldPoint point) {
        return point == null ? Optional.empty() : worldToScreen(point.x(), point.y(), point.z());
    }

    public Optional<ScreenPoint> worldToScreenInWorldView(int worldViewId,
                                                          int preciseX,
                                                          int worldY,
                                                          int preciseY,
                                                          int plane) {
        return TitanRuntime.getOverlayBackend().worldToScreenInWorldView(
            worldViewId, preciseX, worldY, preciseY, plane);
    }

    public Optional<ScreenPoint> worldToScreen(LocalPoint point, int worldY, int plane) {
        return point == null
            ? Optional.empty()
            : worldToScreenInWorldView(point.worldViewId(), point.x(), worldY, point.y(), plane);
    }

    public Optional<ScreenPoint> worldToScreen(Locatable<?> locatable, int worldY) {
        return locatable == null
            ? Optional.empty()
            : worldToScreen(locatable.localPoint(), worldY, locatable.plane());
    }

    public Optional<ScreenPoint> tileToScreen(int tileX, int tileY, int plane) {
        return tileToScreen(tileX, tileY, plane, 0);
    }

    public Optional<ScreenPoint> tileToScreen(int tileX, int tileY, int plane, int heightOffset) {
        return TitanRuntime.getOverlayBackend().tileToScreen(tileX, tileY, plane, heightOffset);
    }

    public int tileHeight(int preciseX, int preciseY, int plane) {
        return TitanRuntime.getOverlayBackend().tileHeight(preciseX, preciseY, plane);
    }

    public int tileHeightInWorldView(int worldViewId, int preciseX, int preciseY, int plane) {
        return TitanRuntime.getOverlayBackend().tileHeightInWorldView(
            worldViewId, preciseX, preciseY, plane);
    }

    public int tileHeight(LocalPoint point, int plane) {
        return point == null ? 0 : tileHeightInWorldView(point.worldViewId(), point.x(), point.y(), plane);
    }

    public int worldTileHeightInWorldView(int worldViewId,
                                          int worldTileX, int worldTileY,
                                          int plane) {
        Client client = Titan.client();
        int baseX = worldViewId == WorldView.TOP_LEVEL ? client.topLevelBaseX() : client.baseX();
        int baseY = worldViewId == WorldView.TOP_LEVEL ? client.topLevelBaseY() : client.baseY();
        return tileHeightInWorldView(worldViewId,
            (worldTileX - baseX) * 128, (worldTileY - baseY) * 128, plane);
    }

    public static long buildActorTypecode(int entityType, int hashIndex,
                                          int tileX, int tileY, int plane) {
        if (hashIndex < 0) return 0L;
        long tx = tileX & 0x7FL;
        long tileYBits = tileY & 0x7FL;
        long pl = plane & 0x3L;
        long typeBits = entityType & 0x7L;
        long id = hashIndex & 0xFFFFFFFFL;
        return tx | (tileYBits << 7) | (pl << 14) | (typeBits << 16) | (id << 20);
    }

    public static long buildLocTypecode(int locId, int layer,
                                        int tileX, int tileY, int plane) {
        return buildLocTypecode(locId, layer, tileX, tileY, plane, WorldView.TOP_LEVEL);
    }

    public static long buildLocTypecode(int locId, int layer,
                                        int tileX, int tileY, int plane,
                                        int worldViewId) {
        // Native loc picking uses the raw scene tag; layer is kept in
        // the signature for source compatibility.
        if (locId < 0) return 0L;
        long tx = tileX & 0x7FL;
        long tileYBits = tileY & 0x7FL;
        long pl = plane & 0x3L;
        long id = locId & 0xFFFFL;
        long wv = worldViewId < 0 ? 0L : worldViewId & 0xFFFFL;
        return tx | (tileYBits << 7) | (pl << 14) | (2L << 16)
            | (id << 20) | (wv << 36);
    }
}
