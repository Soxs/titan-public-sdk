package net.titan.api.overlay;

import net.titan.api.NPC;
import net.titan.api.Player;
import net.titan.api.ScreenPoint;
import net.titan.api.TileObject;
import net.titan.api.WorldPoint;
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

    public void entityBox(int preciseX, int preciseY, int plane,
                          int tileSize, int height, int color) {
        TitanRuntime.getOverlayBackend().entityBox(
            preciseX, preciseY, plane, tileSize, height, color);
    }

    public void entityBox(NPC npc, int color) {
        entityBox(npc, color, 240);
    }

    public void entityBox(NPC npc, int color, int height) {
        if (npc == null) return;
        entityBox(npc.preciseX(), npc.preciseY(), npc.plane(), 1, height, color);
    }

    public void entityBox(Player player, int color) {
        entityBox(player, color, 240);
    }

    public void entityBox(Player player, int color, int height) {
        if (player == null) return;
        entityBox(player.preciseX(), player.preciseY(), player.plane(), 1, height, color);
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
        long typecode = buildLocTypecode(
            object.id(), object.layer(), object.tileX(), object.tileY(), object.plane());
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
        long typecode = buildLocTypecode(
            object.id(), object.layer(), object.tileX(), object.tileY(), object.plane());
        tileObjectHull(object.entityPtr(), typecode, outline, fill);
    }

    public void tileObjectHull(long locPtr, long typecode, int outline, int fill) {
        TitanRuntime.getOverlayBackend().tileObjectHull(locPtr, typecode, outline, fill);
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

    public Optional<ScreenPoint> tileToScreen(int tileX, int tileY, int plane) {
        return tileToScreen(tileX, tileY, plane, 0);
    }

    public Optional<ScreenPoint> tileToScreen(int tileX, int tileY, int plane, int heightOffset) {
        return TitanRuntime.getOverlayBackend().tileToScreen(tileX, tileY, plane, heightOffset);
    }

    public int tileHeight(int preciseX, int preciseY, int plane) {
        return TitanRuntime.getOverlayBackend().tileHeight(preciseX, preciseY, plane);
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
        if (locId < 0) return 0L;
        long tx = tileX & 0x7FL;
        long tileYBits = tileY & 0x7FL;
        long pl = plane & 0x3L;
        long id = locId & 0xFFFFFFFFL;
        long ly = layer < 0 ? 0L : layer & 0x3L;
        return tx | (tileYBits << 7) | (pl << 14) | (2L << 16) | (id << 20) | (ly << 52);
    }
}
