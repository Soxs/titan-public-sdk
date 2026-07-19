package net.titan.api;

import net.titan.api.internal.BreakHandlerBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.IdentityHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class BreakHandlerTest {
    private TestBackend backend;

    @AfterEach
    void tearDown() {
        if (backend != null) TitanRuntime.clearBreakHandlerBackend(backend);
    }

    @Test
    void instanceIsForwardedForEveryLifecycleOperation() {
        backend = new TestBackend();
        TitanRuntime.setBreakHandlerBackend(backend);
        Plugin plugin = new TestPlugin();

        assertTrue(BreakHandler.register(plugin));
        assertSame(plugin, backend.lastPlugin);
        assertTrue(backend.configurable);
        assertTrue(BreakHandler.start(plugin));
        assertSame(plugin, backend.lastPlugin);
        assertTrue(BreakHandler.stop(plugin));
        assertSame(plugin, backend.lastPlugin);
        assertTrue(BreakHandler.unregister(plugin));
        assertSame(plugin, backend.lastPlugin);
    }

    @Test
    void participantOnlyRegistrationPreservesRole() {
        backend = new TestBackend();
        TitanRuntime.setBreakHandlerBackend(backend);
        Plugin plugin = new TestPlugin();

        assertTrue(BreakHandler.register(plugin, false));
        assertFalse(backend.configurable);
    }

    @Test
    void helperClassCanUseTheExactOwnerInstance() {
        backend = new TestBackend();
        TitanRuntime.setBreakHandlerBackend(backend);
        Plugin plugin = new TestPlugin();
        Helper helper = new Helper(plugin);

        helper.acknowledgePause();

        assertSame(plugin, backend.lastPlugin);
        assertEquals(BreakHandlerBackend.REPORT_SAFE_PAUSED, backend.lastReportState);
    }

    @Test
    void pollAndConvenienceQueriesPreserveCommandData() {
        backend = new TestBackend();
        TitanRuntime.setBreakHandlerBackend(backend);
        Plugin plugin = new TestPlugin();
        backend.command = new BreakCommand(
            true, 42L, BreakPhase.PREPARE, BreakMode.LOGOUT, "auto_fighter");

        BreakCommand command = BreakHandler.poll(plugin);

        assertEquals(42L, command.epoch());
        assertEquals(BreakPhase.PREPARE, command.phase());
        assertEquals(BreakMode.LOGOUT, command.mode());
        assertEquals("auto_fighter", command.triggeringOwnerId());
        assertTrue(command.shouldBreak());
        assertTrue(BreakHandler.shouldBreak(plugin));
        assertFalse(BreakHandler.isBreakActive(plugin));
        assertFalse(BreakHandler.shouldResume(plugin));

        backend.command = new BreakCommand(
            true, 42L, BreakPhase.BREAK_ACTIVE, BreakMode.LOGOUT, "auto_fighter");
        assertTrue(BreakHandler.isBreakActive(plugin));

        backend.command = new BreakCommand(
            true, 42L, BreakPhase.RESUME, BreakMode.LOGOUT, "auto_fighter");
        assertTrue(BreakHandler.shouldResume(plugin));
    }

    @Test
    void reportMethodsUseStableProtocolStatesAndSanitizeNullReasons() {
        backend = new TestBackend();
        TitanRuntime.setBreakHandlerBackend(backend);
        Plugin plugin = new TestPlugin();

        assertTrue(BreakHandler.running(plugin));
        assertEquals(BreakHandlerBackend.REPORT_RUNNING, backend.lastReportState);

        assertTrue(BreakHandler.defer(plugin, 750, "combat"));
        assertEquals(BreakHandlerBackend.REPORT_DEFERRED, backend.lastReportState);
        assertEquals(750, backend.lastRetryAfterMs);
        assertEquals("combat", backend.lastReason);

        assertTrue(BreakHandler.error(plugin, 17, null));
        assertEquals(BreakHandlerBackend.REPORT_ERROR, backend.lastReportState);
        assertEquals(17, backend.lastCode);
        assertEquals("", backend.lastReason);
    }

    @Test
    void invalidInputsFailBeforeCrossingTheBackend() {
        backend = new TestBackend();
        TitanRuntime.setBreakHandlerBackend(backend);

        assertThrows(NullPointerException.class, () -> BreakHandler.register(null));
        assertThrows(IllegalArgumentException.class,
            () -> BreakHandler.defer(new TestPlugin(), -1, "later"));
        assertThrows(IllegalArgumentException.class,
            () -> BreakHandler.error(new TestPlugin(), -1, "bad"));
    }

    @Test
    void backendRequiresExactObjectIdentity() {
        backend = new TestBackend();
        TitanRuntime.setBreakHandlerBackend(backend);
        Plugin loaded = new TestPlugin();
        Plugin lookalike = new TestPlugin();
        backend.loaded.put(loaded, "same_id");

        assertTrue(BreakHandler.register(loaded));
        assertFalse(BreakHandler.register(lookalike));
    }

    private static final class TestPlugin implements Plugin {}

    private static final class Helper {
        private final Plugin owner;

        Helper(Plugin owner) {
            this.owner = owner;
        }

        void acknowledgePause() {
            BreakHandler.paused(owner);
        }
    }

    private static final class TestBackend implements BreakHandlerBackend {
        final Map<Plugin, String> loaded = new IdentityHashMap<>();
        Plugin lastPlugin;
        boolean configurable;
        int lastReportState;
        int lastCode;
        int lastRetryAfterMs;
        String lastReason;
        BreakCommand command = BreakCommand.none();

        private boolean accepts(Plugin plugin) {
            lastPlugin = plugin;
            return loaded.isEmpty() || loaded.containsKey(plugin);
        }

        @Override
        public boolean register(Plugin plugin, boolean configurable) {
            this.configurable = configurable;
            return accepts(plugin);
        }

        @Override
        public boolean start(Plugin plugin) {
            return accepts(plugin);
        }

        @Override
        public boolean stop(Plugin plugin) {
            return accepts(plugin);
        }

        @Override
        public boolean unregister(Plugin plugin) {
            return accepts(plugin);
        }

        @Override
        public BreakCommand poll(Plugin plugin) {
            return accepts(plugin) ? command : BreakCommand.none();
        }

        @Override
        public boolean report(Plugin plugin, int state, int code,
                              int retryAfterMs, String reason) {
            lastReportState = state;
            lastCode = code;
            lastRetryAfterMs = retryAfterMs;
            lastReason = reason;
            return accepts(plugin);
        }
    }
}
