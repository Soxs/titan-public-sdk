package net.titan.api.overlay;

import net.titan.api.internal.OverlayBackend;
import net.titan.api.internal.TitanRuntime;

public class OverlayPanel implements AutoCloseable {
    private final String pluginId;
    private final String name;
    private int handle = -1;
    private int preferredWidth = 220;
    private OverlayPanelStyle style = OverlayPanelStyle.defaults();
    private boolean closed;

    public OverlayPanel(String name) {
        this(name, OverlayPanelAnchor.DYNAMIC, 50);
    }

    public OverlayPanel(String name, OverlayPanelAnchor anchor) {
        this(name, anchor, 50);
    }

    public OverlayPanel(String name, OverlayPanelAnchor anchor, int priority) {
        this.pluginId = TitanRuntime.currentPluginId();
        this.name = name == null ? "" : name;
        OverlayPanelAnchor safeAnchor = anchor == null ? OverlayPanelAnchor.DYNAMIC : anchor;
        this.handle = backend().overlayPanelRegister(
            pluginId, this.name, safeAnchor.protocolId(), priority);
        backend().overlayPanelSetStyle(handle, style);
        TitanRuntime.registerOverlayPanel(pluginId, this);
    }

    public String pluginId() { return pluginId; }
    public String name() { return name; }
    public int handle() { return handle; }
    public int preferredWidth() { return preferredWidth; }
    public OverlayPanelStyle style() { return style.copy(); }

    public OverlayPanel render(Runnable renderer) {
        if (closed || handle < 0 || renderer == null) return this;
        OverlayBackend backend = backend();
        backend.overlayPanelBegin(handle, preferredWidth);
        try {
            renderer.run();
        } finally {
            backend.overlayPanelEnd(handle);
        }
        return this;
    }

    public OverlayPanel begin() {
        if (!closed && handle >= 0) backend().overlayPanelBegin(handle, preferredWidth);
        return this;
    }

    public OverlayPanel end() {
        if (!closed && handle >= 0) backend().overlayPanelEnd(handle);
        return this;
    }

    public OverlayPanel title(String text) {
        return title(text, OverlayPanelStyle.DEFAULT_TITLE_COLOR);
    }

    public OverlayPanel title(String text, int color) {
        if (!closed && handle >= 0) backend().overlayPanelTitle(handle, text, color);
        return this;
    }

    public OverlayPanel line(String text) {
        return line(text, "", OverlayPanelStyle.DEFAULT_LINE_LEFT_COLOR,
            OverlayPanelStyle.DEFAULT_LINE_RIGHT_COLOR);
    }

    public OverlayPanel line(String left, String right) {
        return line(left, right, OverlayPanelStyle.DEFAULT_LINE_LEFT_COLOR,
            OverlayPanelStyle.DEFAULT_LINE_RIGHT_COLOR);
    }

    public OverlayPanel line(String left, String right, int leftColor, int rightColor) {
        if (!closed && handle >= 0) {
            backend().overlayPanelLine(handle, left, right, leftColor, rightColor);
        }
        return this;
    }

    public OverlayPanel progressBar(int value, int minValue, int maxValue) {
        return progressBar(value, minValue, maxValue,
            OverlayPanelStyle.DEFAULT_BAR_FILL_COLOR,
            OverlayPanelStyle.DEFAULT_BAR_BACKGROUND_COLOR);
    }

    public OverlayPanel progressBar(int value, int minValue, int maxValue,
                                    int fillColor, int backgroundColor) {
        if (!closed && handle >= 0) {
            backend().overlayPanelProgressBar(
                handle, value, minValue, maxValue, fillColor, backgroundColor);
        }
        return this;
    }

    public OverlayPanel setPreferredWidth(int pixels) {
        if (pixels < 80) pixels = 80;
        if (pixels > 600) pixels = 600;
        preferredWidth = pixels;
        return this;
    }

    public OverlayPanel setStyle(OverlayPanelStyle style) {
        this.style = style == null ? OverlayPanelStyle.defaults() : style.copy();
        if (!closed && handle >= 0) backend().overlayPanelSetStyle(handle, this.style);
        return this;
    }

    public OverlayPanel setBackgroundColor(int argb) {
        return setStyle(style.copy().background(argb));
    }

    public OverlayPanel setBorderColor(int argb) {
        return setStyle(style.copy().borderColor(argb));
    }

    public OverlayPanel setBorderThickness(float pixels) {
        return setStyle(style.copy().borderThickness(pixels));
    }

    public OverlayPanel setCornerRadius(float pixels) {
        return setStyle(style.copy().cornerRadius(pixels));
    }

    public OverlayPanel setPadding(int horizontal, int vertical) {
        return setStyle(style.copy().padding(horizontal, vertical));
    }

    public OverlayPanel setLineGap(int pixels) {
        return setStyle(style.copy().lineGap(pixels));
    }

    public OverlayPanel setTitleColor(int argb) {
        return setStyle(style.copy().titleColor(argb));
    }

    public OverlayPanel setLineColors(int leftArgb, int rightArgb) {
        return setStyle(style.copy().lineColors(leftArgb, rightArgb));
    }

    public OverlayPanel setProgressBarColors(int fillArgb, int backgroundArgb) {
        return setStyle(style.copy().progressBarColors(fillArgb, backgroundArgb));
    }

    public OverlayPanel setOpacity(float alpha01) {
        if (alpha01 < 0.0f) alpha01 = 0.0f;
        if (alpha01 > 1.0f) alpha01 = 1.0f;
        OverlayPanelStyle next = style.copy()
            .background(rebaseAlpha(style.background(), alpha01))
            .borderColor(rebaseAlpha(style.borderColor(), alpha01))
            .titleColor(rebaseAlpha(style.titleColor(), alpha01))
            .lineColors(rebaseAlpha(style.lineLeftColor(), alpha01),
                rebaseAlpha(style.lineRightColor(), alpha01))
            .progressBarColors(rebaseAlpha(style.barFillColor(), alpha01),
                rebaseAlpha(style.barBackgroundColor(), alpha01));
        return setStyle(next);
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        int current = handle;
        handle = -1;
        TitanRuntime.unregisterOverlayPanel(pluginId, this);
        if (current >= 0) {
            backend().overlayPanelUnregister(current);
        }
    }

    private static int rebaseAlpha(int argb, float alpha01) {
        int original = (argb >>> 24) & 0xFF;
        int rebased = Math.round(original * alpha01) & 0xFF;
        return (argb & 0x00FFFFFF) | (rebased << 24);
    }

    private static OverlayBackend backend() {
        return TitanRuntime.getOverlayBackend();
    }
}
