package net.titan.api;

import net.titan.api.internal.LiveStateBackend;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class WidgetOpsApiTest {
    private static final int INVENTORY = (149 << 16) | 0;
    private static final int REWARD = (919 << 16) | 21;

    @Test void dynamicChildReadsNameAndRuneLiteShapedActionsAtItsPath() {
        RecordingBackend backend = new RecordingBackend();
        TitanRuntime.setLiveStateBackend(backend);
        try {
            Widget shark = widget(INVENTORY, List.of(3));
            assertEquals("<col=ff9040>Shark</col>", shark.name());
            assertArrayEquals(new String[]{"Eat", null, null, "Use", "Drop"}, shark.actions());
            assertTrue(shark.opsAvailable());
            assertEquals(INVENTORY, backend.lastAddress.rootPackedId());
            assertEquals(List.of(3), backend.lastAddress.dynamicPath());
        } finally { TitanRuntime.clearLiveStateBackend(backend); }
    }

    @Test void flatWidgetWithoutOpBaseOrOpArrayDiffersFromAnUnreadableHost() {
        RecordingBackend backend = new RecordingBackend();
        TitanRuntime.setLiveStateBackend(backend);
        try {
            Widget reward = widget(REWARD, List.of());
            assertEquals("", reward.name());
            assertNull(reward.actions(), "RuneLite returns null when the widget has no op array");
            assertTrue(reward.opsAvailable(), "an empty widget is still readable");
            assertTrue(backend.lastAddress.dynamicPath().isEmpty());

            Widget gone = widget(REWARD, List.of(9));
            assertNull(gone.name());
            assertNull(gone.actions());
            assertFalse(gone.opsAvailable(), "opsAvailable tells an unreadable host from an empty widget");
        } finally { TitanRuntime.clearLiveStateBackend(backend); }
    }

    @Test void olderBackendsWithoutTheReadsReportUnavailable() {
        LiveStateBackend passive = new PassiveBackend();
        TitanRuntime.setLiveStateBackend(passive);
        try {
            Widget shark = widget(INVENTORY, List.of(3));
            assertNull(shark.name());
            assertNull(shark.actions());
            assertFalse(shark.opsAvailable());
        } finally { TitanRuntime.clearLiveStateBackend(passive); }
    }

    private static Widget widget(int root, List<Integer> path) {
        Widget widget = new Widget();
        set(widget, "packedId", root);
        set(widget, "rootPackedId", root);
        set(widget, "dynamicPath", new ArrayList<>(path));
        set(widget, "dynamicChildSlot", path.isEmpty() ? -1 : path.get(path.size() - 1));
        return widget;
    }

    private static class PassiveBackend implements LiveStateBackend {
        public <T> T current(T value) { return value; }
        public boolean exists(Object value) { return true; }
        public String identity(Object value) { return null; }
        public <T> T snapshot(T value) { return value; }
    }

    private static final class RecordingBackend extends PassiveBackend {
        WidgetAddress lastAddress;

        @Override public String widgetName(WidgetAddress address) {
            lastAddress = address;
            if (address.rootPackedId() == INVENTORY && address.dynamicPath().equals(List.of(3)))
                return "<col=ff9040>Shark</col>";
            return address.rootPackedId() == REWARD && address.dynamicPath().isEmpty() ? "" : null;
        }

        @Override public String[] widgetActions(WidgetAddress address) {
            lastAddress = address;
            if (address.rootPackedId() == INVENTORY && address.dynamicPath().equals(List.of(3)))
                return new String[]{"Eat", null, null, "Use", "Drop"};
            return address.rootPackedId() == REWARD && address.dynamicPath().isEmpty() ? new String[0] : null;
        }
    }

    private static void set(Object target, String name, Object value) {
        try { Field field = target.getClass().getDeclaredField(name); field.setAccessible(true); field.set(target, value); }
        catch (ReflectiveOperationException error) { throw new AssertionError(error); }
    }
}
