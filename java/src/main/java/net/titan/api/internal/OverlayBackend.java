package net.titan.api.internal;

import net.titan.api.ScreenPoint;
import net.titan.api.overlay.OverlayPanelStyle;

import java.util.Optional;

public interface OverlayBackend {
    void tileQuad(int tileX, int tileZ, int plane, int fillColor, int outlineColor);
    void tileRegion(int minTileX, int minTileZ, int maxTileX, int maxTileZ,
                    int plane, int fillColor, int outlineColor);
    void entityBox(int preciseX, int preciseZ, int plane,
                   int tileSize, int height, int color);
    void entityClickbox(long entityPtr, long typecode, int outline, int fill);
    void tileObjectClickbox(long locPtr, long typecode, int outline, int fill);
    void entityHull(long entityPtr, long typecode, int outline, int fill);
    void tileObjectHull(long locPtr, long typecode, int outline, int fill);
    void textAtWorld(int worldX, int worldY, int worldZ,
                     String text, int color, boolean centered);
    void screenText(int x, int y, String text, int color);
    void screenRect(int x, int y, int width, int height, int color);
    void screenLine(int x1, int y1, int x2, int y2, int color, float thickness);
    Optional<ScreenPoint> worldToScreen(int worldX, int worldY, int worldZ);
    Optional<ScreenPoint> tileToScreen(int tileX, int tileZ, int plane, int heightOffset);
    int tileHeight(int preciseX, int preciseZ, int plane);

    int overlayPanelRegister(String pluginId, String panelName,
                             int defaultAnchor, int defaultPriority);
    void overlayPanelUnregister(int handle);
    void overlayPanelBegin(int handle, int preferredWidth);
    void overlayPanelEnd(int handle);
    void overlayPanelSetStyle(int handle, OverlayPanelStyle style);
    void overlayPanelTitle(int handle, String text, int color);
    void overlayPanelLine(int handle, String left, String right,
                          int leftColor, int rightColor);
    void overlayPanelProgressBar(int handle, int value, int minValue,
                                 int maxValue, int fillColor, int backgroundColor);
}
