package net.titan.api.internal;

import com.google.inject.Injector;
import net.titan.api.overlay.OverlayPanel;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TitanRuntime {
    private static volatile Injector injector;
    private static volatile OverlayBackend overlayBackend;
    private static final ThreadLocal<Deque<String>> CURRENT_PLUGIN_IDS =
        ThreadLocal.withInitial(ArrayDeque::new);
    private static final Object OVERLAY_PANEL_LOCK = new Object();
    private static final Map<String, List<OverlayPanel>> OVERLAY_PANELS =
        new LinkedHashMap<>();

    private TitanRuntime() {}

    public static Injector getInjector() {
        Injector value = injector;
        if (value == null) {
            throw new IllegalStateException(
                "Titan Java plugin injector is not available for this client tab yet.");
        }
        return value;
    }

    public static void setInjector(Injector injector) {
        TitanRuntime.injector = injector;
    }

    public static void clearInjector(Injector injector) {
        if (TitanRuntime.injector == injector) {
            TitanRuntime.injector = null;
        }
    }

    public static OverlayBackend getOverlayBackend() {
        OverlayBackend value = overlayBackend;
        if (value == null) {
            throw new IllegalStateException(
                "Titan Java overlay backend is not available for this client tab yet.");
        }
        return value;
    }

    public static void setOverlayBackend(OverlayBackend backend) {
        overlayBackend = backend;
    }

    public static void clearOverlayBackend(OverlayBackend backend) {
        if (overlayBackend == backend) {
            overlayBackend = null;
        }
    }

    public static String currentPluginId() {
        Deque<String> stack = CURRENT_PLUGIN_IDS.get();
        String value = stack.peek();
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                "Titan Java plugin context is not active. Create overlay panels from plugin construction or callbacks.");
        }
        return value;
    }

    public static void enterPlugin(String pluginId) {
        CURRENT_PLUGIN_IDS.get().push(pluginId);
    }

    public static void leavePlugin() {
        Deque<String> stack = CURRENT_PLUGIN_IDS.get();
        if (!stack.isEmpty()) stack.pop();
        if (stack.isEmpty()) CURRENT_PLUGIN_IDS.remove();
    }

    public static void registerOverlayPanel(String pluginId, OverlayPanel panel) {
        if (pluginId == null || pluginId.isEmpty() || panel == null) return;
        synchronized (OVERLAY_PANEL_LOCK) {
            OVERLAY_PANELS.computeIfAbsent(pluginId, ignored -> new ArrayList<>()).add(panel);
        }
    }

    public static void unregisterOverlayPanel(String pluginId, OverlayPanel panel) {
        if (pluginId == null || pluginId.isEmpty() || panel == null) return;
        synchronized (OVERLAY_PANEL_LOCK) {
            List<OverlayPanel> panels = OVERLAY_PANELS.get(pluginId);
            if (panels == null) return;
            panels.remove(panel);
            if (panels.isEmpty()) OVERLAY_PANELS.remove(pluginId);
        }
    }

    public static void closeOverlayPanels(String pluginId) {
        List<OverlayPanel> panels;
        synchronized (OVERLAY_PANEL_LOCK) {
            panels = OVERLAY_PANELS.remove(pluginId);
            if (panels == null || panels.isEmpty()) return;
            panels = new ArrayList<>(panels);
        }
        for (OverlayPanel panel : panels) {
            panel.close();
        }
    }

    public static void closeAllOverlayPanels() {
        List<OverlayPanel> panels = new ArrayList<>();
        synchronized (OVERLAY_PANEL_LOCK) {
            for (List<OverlayPanel> value : OVERLAY_PANELS.values()) {
                panels.addAll(value);
            }
            OVERLAY_PANELS.clear();
        }
        for (OverlayPanel panel : panels) {
            panel.close();
        }
    }
}
