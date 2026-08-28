package net.titan.api.internal;

import com.google.inject.Injector;
import net.titan.api.Actor;
import net.titan.api.overlay.OverlayPanel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TitanRuntime {
    private static volatile Injector injector;
    private static volatile OverlayBackend overlayBackend;
    private static volatile ActorResolver actorResolver;
    private static volatile InteractionBackend interactionBackend;
    private static volatile MouseBackend mouseBackend;
    private static volatile SchedulerBackend schedulerBackend;
    private static volatile BreakHandlerBackend breakHandlerBackend;
    private static volatile LiveStateBackend liveStateBackend;
    private static volatile WebWalkerBackend webWalkerBackend;
    private static volatile WorldMapBackend worldMapBackend;
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

    public static void setActorResolver(ActorResolver resolver) {
        actorResolver = resolver;
    }

    public static void clearActorResolver(ActorResolver resolver) {
        if (actorResolver == resolver) {
            actorResolver = null;
        }
    }

    public static void setLiveStateBackend(LiveStateBackend backend) {
        liveStateBackend = backend;
    }

    public static void clearLiveStateBackend(LiveStateBackend backend) {
        if (liveStateBackend == backend) {
            liveStateBackend = null;
        }
    }

    public static WebWalkerBackend getWebWalkerBackend() {
        WebWalkerBackend value = webWalkerBackend;
        if (value == null) {
            throw new IllegalStateException(
                "Titan Java web walker backend is not available for this client tab yet.");
        }
        return value;
    }

    public static void setWebWalkerBackend(WebWalkerBackend backend) {
        webWalkerBackend = backend;
    }

    public static void clearWebWalkerBackend(WebWalkerBackend backend) {
        if (webWalkerBackend == backend) {
            webWalkerBackend = null;
        }
    }

    public static WorldMapBackend getWorldMapBackend() {
        WorldMapBackend value = worldMapBackend;
        if (value == null) {
            throw new IllegalStateException(
                "Titan Java world-map backend is not available for this client tab yet.");
        }
        return value;
    }

    public static void setWorldMapBackend(WorldMapBackend backend) {
        worldMapBackend = backend;
    }

    public static void clearWorldMapBackend(WorldMapBackend backend) {
        if (worldMapBackend == backend) {
            worldMapBackend = null;
        }
    }

    public static boolean liveExists(Object value) {
        LiveStateBackend backend = liveStateBackend;
        if (isDetachedSnapshot(value)) return true;
        return backend == null || value == null || backend.exists(value);
    }

    public static <T> T currentLive(T value) {
        LiveStateBackend backend = liveStateBackend;
        if (isDetachedSnapshot(value)) return value;
        return backend == null || value == null ? value : backend.current(value);
    }

    public static String liveIdentity(Object value) {
        LiveStateBackend backend = liveStateBackend;
        if (isDetachedSnapshot(value)) return null;
        return backend == null || value == null ? null : backend.identity(value);
    }

    public static boolean liveEquals(Object self, Object other) {
        if (self == other) return true;
        if (self == null || other == null || self.getClass() != other.getClass()) return false;
        String identity = liveIdentity(self);
        return identity != null && identity.equals(liveIdentity(other));
    }

    public static int liveHashCode(Object value) {
        String identity = liveIdentity(value);
        return identity != null ? identity.hashCode() : System.identityHashCode(value);
    }

    public static <T> T snapshotLive(T value) {
        LiveStateBackend backend = liveStateBackend;
        if (backend != null && value != null) return backend.snapshot(value);
        return detachedCopy(value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T detachedCopy(T value) {
        return detachedCopy(value, new IdentityHashMap<>());
    }

    @SuppressWarnings("unchecked")
    private static <T> T detachedCopy(T value, IdentityHashMap<Object, Object> seen) {
        if (value == null) return null;
        Object existing = seen.get(value);
        if (existing != null) return (T) existing;
        try {
            Class<?> type = value.getClass();
            Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object copy = ctor.newInstance();
            seen.put(value, copy);
            for (Class<?> cursor = type; cursor != null && cursor != Object.class;
                    cursor = cursor.getSuperclass()) {
                for (Field field : cursor.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    field.setAccessible(true);
                    field.set(copy, detachedValue(field.get(value), seen));
                }
            }
            markDetachedSnapshot(copy);
            return (T) copy;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return value;
        }
    }

    private static Object detachedValue(Object value, IdentityHashMap<Object, Object> seen) {
        if (value instanceof List<?>) {
            List<?> raw = (List<?>) value;
            List<Object> out = new ArrayList<>(raw.size());
            for (Object item : raw) out.add(detachedValue(item, seen));
            return out;
        }
        if (isSdkObject(value)) return detachedCopy(value, seen);
        return value;
    }

    private static boolean isSdkObject(Object value) {
        if (value == null) return false;
        Package pkg = value.getClass().getPackage();
        return pkg != null && pkg.getName().startsWith("net.titan.api");
    }

    // The "liveHandle" Field per class, resolved once. This ran uncached on
    // every SDK accessor (via currentLive/liveExists/liveIdentity). ClassValue
    // is keyed on the Class and collected with it, so plugin hot-reload does not
    // leak classes (unlike a ConcurrentMap<Class,..> would). A sentinel marks
    // classes that have no such field.
    private static final Field NO_LIVE_HANDLE;

    static {
        Field placeholder = null;
        try {
            placeholder = LiveHandleHolder.class.getDeclaredField("placeholder");
        } catch (NoSuchFieldException ignored) {
        }
        NO_LIVE_HANDLE = placeholder;
    }

    private static final class LiveHandleHolder {
        @SuppressWarnings("unused") private boolean placeholder;
    }

    private static final ClassValue<Field> LIVE_HANDLE_FIELD = new ClassValue<Field>() {
        @Override protected Field computeValue(Class<?> type) {
            try {
                Field f = type.getDeclaredField("liveHandle");
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException | RuntimeException ex) {
                return NO_LIVE_HANDLE;
            }
        }
    };

    private static Field liveHandleField(Object value) {
        if (value == null) return null;
        Field f = LIVE_HANDLE_FIELD.get(value.getClass());
        return f == NO_LIVE_HANDLE ? null : f;
    }

    private static boolean isDetachedSnapshot(Object value) {
        Field field = liveHandleField(value);
        if (field == null) return false;
        try {
            return !field.getBoolean(value);
        } catch (IllegalAccessException | RuntimeException ignored) {
            return false;
        }
    }

    private static void markDetachedSnapshot(Object value) {
        Field field = liveHandleField(value);
        if (field == null) return;
        try {
            field.setBoolean(value, false);
        } catch (IllegalAccessException | RuntimeException ignored) {
        }
    }

    public static InteractionBackend getInteractionBackend() {
        InteractionBackend value = interactionBackend;
        if (value == null) {
            throw new IllegalStateException(
                "Titan Java interaction backend is not available for this client tab yet.");
        }
        return value;
    }

    public static void setInteractionBackend(InteractionBackend backend) {
        interactionBackend = backend;
    }

    public static void clearInteractionBackend(InteractionBackend backend) {
        if (interactionBackend == backend) {
            interactionBackend = null;
        }
    }

    public static MouseBackend getMouseBackend() {
        MouseBackend value = mouseBackend;
        if (value == null) {
            throw new IllegalStateException(
                "Titan Java mouse backend is not available for this client tab yet.");
        }
        return value;
    }

    public static void setMouseBackend(MouseBackend backend) {
        mouseBackend = backend;
    }

    public static void clearMouseBackend(MouseBackend backend) {
        if (mouseBackend == backend) {
            mouseBackend = null;
        }
    }

    public static SchedulerBackend getSchedulerBackend() {
        SchedulerBackend value = schedulerBackend;
        if (value == null) {
            throw new IllegalStateException(
                "Titan Java scheduler backend is not available for this client tab yet.");
        }
        return value;
    }

    public static void setSchedulerBackend(SchedulerBackend backend) {
        schedulerBackend = backend;
    }

    public static void clearSchedulerBackend(SchedulerBackend backend) {
        if (schedulerBackend == backend) {
            schedulerBackend = null;
        }
    }

    public static BreakHandlerBackend getBreakHandlerBackend() {
        BreakHandlerBackend value = breakHandlerBackend;
        if (value == null) {
            throw new IllegalStateException(
                "Titan Java Break Handler backend is not available for this client tab yet.");
        }
        return value;
    }

    public static void setBreakHandlerBackend(BreakHandlerBackend backend) {
        breakHandlerBackend = backend;
    }

    public static void clearBreakHandlerBackend(BreakHandlerBackend backend) {
        if (breakHandlerBackend == backend) {
            breakHandlerBackend = null;
        }
    }

    public static Optional<Actor> resolveInteracting(int interactingIndex, int interactingType) {
        return resolveInteracting(interactingIndex, interactingType, -1);
    }

    public static Optional<Actor> resolveInteracting(int interactingIndex,
                                                     int interactingType,
                                                     int worldViewId) {
        ActorResolver value = actorResolver;
        if (value == null) return Optional.empty();
        Optional<Actor> actor = value.resolveInteracting(interactingIndex, interactingType, worldViewId);
        return actor == null ? Optional.empty() : actor;
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
