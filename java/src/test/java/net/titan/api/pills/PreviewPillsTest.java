package net.titan.api.pills;

import net.titan.api.internal.PreviewPillBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.panel.PanelTone;
import net.titan.api.plugins.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PreviewPillsTest {
    private TestBackend backend;

    @AfterEach
    void tearDown() {
        if (backend != null) TitanRuntime.clearPreviewPillBackend(backend);
    }

    @Test
    void constantsMirrorTheNativeAbi() {
        // kPreviewPillMaxKeyLen, kPreviewPillMaxTextBytes, kPreviewPillsPerPlugin,
        // kPreviewPillsPerTab and the PREVIEW_PILL_* values.
        assertEquals(32, PreviewPills.MAX_KEY_LENGTH);
        assertEquals(63, PreviewPills.MAX_TEXT_BYTES);
        assertEquals(2, PreviewPills.MAX_PER_PLUGIN);
        assertEquals(8, PreviewPills.MAX_PER_TAB);
        assertEquals(1, PreviewPillBackend.FLAG_CLEAR);
        assertEquals(2, PreviewPillBackend.FLAG_COUNTDOWN);
    }

    @Test
    void setCarriesTheExactInstanceTextToneAndCountdown() {
        install();
        Plugin plugin = new TestPlugin();
        PreviewPills pills = PreviewPills.of(plugin);

        assertTrue(pills.set("break", PreviewPill.of("Breaking:")
            .withTone(PanelTone.INFO)
            .withCountdown(Duration.ofMinutes(12))));
        Write write = backend.last();
        assertSame(plugin, write.plugin);
        assertEquals("break", write.key);
        assertEquals("Breaking:", write.text);
        assertEquals(PanelTone.INFO.protocolId(), write.tone);
        assertEquals(PreviewPillBackend.FLAG_COUNTDOWN, write.flags);
        assertEquals(12L * 60L * 1000L, write.countdownMs);

        assertTrue(pills.set("task", PreviewPill.of("Mining iron")));
        write = backend.last();
        assertEquals("Mining iron", write.text);
        assertEquals(PanelTone.NEUTRAL.protocolId(), write.tone);
        assertEquals(0, write.flags);
        assertEquals(0L, write.countdownMs);
    }

    @Test
    void countdownsAreClampedBeforeCrossingTheBridge() {
        install();
        PreviewPills pills = PreviewPills.of(new TestPlugin());

        assertTrue(pills.set("late", PreviewPill.countdown(Duration.ofSeconds(-5))));
        assertEquals("", backend.last().text);
        assertEquals(PreviewPillBackend.FLAG_COUNTDOWN, backend.last().flags);
        assertEquals(0L, backend.last().countdownMs);

        assertTrue(pills.set("forever", PreviewPill.countdown(Duration.ofSeconds(Long.MAX_VALUE))));
        assertEquals(Long.MAX_VALUE, backend.last().countdownMs);
    }

    @Test
    void clearAndClearAllUseTheClearFlag() {
        install();
        Plugin plugin = new TestPlugin();
        PreviewPills pills = PreviewPills.of(plugin);

        assertTrue(pills.clear("break"));
        assertSame(plugin, backend.last().plugin);
        assertEquals("break", backend.last().key);
        assertEquals(PreviewPillBackend.FLAG_CLEAR, backend.last().flags);

        assertTrue(pills.clearAll());
        assertEquals("", backend.last().key);
        assertEquals(PreviewPillBackend.FLAG_CLEAR, backend.last().flags);
    }

    @Test
    void invalidKeysNeverReachTheBackend() {
        install();
        PreviewPills pills = PreviewPills.of(new TestPlugin());
        PreviewPill pill = PreviewPill.of("x");

        assertFalse(pills.set("", pill));
        assertFalse(pills.set("has space", pill));
        assertFalse(pills.set("bad*char", pill));
        assertFalse(pills.set(repeat('k', PreviewPills.MAX_KEY_LENGTH + 1), pill));
        assertFalse(pills.clear(""));
        assertFalse(pills.clear("noé"));
        assertEquals(0, backend.writes.size());

        assertTrue(pills.set(repeat('k', PreviewPills.MAX_KEY_LENGTH), pill));
        assertTrue(pills.set("a.b_c:d/e-F9", pill));
        assertEquals(2, backend.writes.size());
        assertThrows(NullPointerException.class, () -> pills.set(null, pill));
        assertThrows(NullPointerException.class, () -> pills.set("key", null));
    }

    @Test
    void refusalsFromTheHostAreReported() {
        install();
        backend.accept = false;
        assertFalse(PreviewPills.of(new TestPlugin()).set("k", PreviewPill.of("x")));
        assertFalse(PreviewPills.of(new TestPlugin()).clearAll());
    }

    @Test
    void pillsAreImmutableValues() {
        PreviewPill base = PreviewPill.of("Breaking:");
        PreviewPill toned = base.withTone(PanelTone.WARNING);
        PreviewPill timed = toned.withCountdown(Duration.ofSeconds(5));

        assertEquals(PanelTone.NEUTRAL, base.tone());
        assertEquals(Optional.empty(), base.countdown());
        assertEquals(PanelTone.WARNING, toned.tone());
        assertEquals(Optional.of(Duration.ofSeconds(5)), timed.countdown());
        assertEquals(Optional.empty(), timed.withCountdown(null).countdown());
        assertEquals(timed, PreviewPill.of("Breaking:").withTone(PanelTone.WARNING)
            .withCountdown(Duration.ofSeconds(5)));
        assertNotEquals(base, toned);
        assertThrows(NullPointerException.class, () -> PreviewPill.of(null));
        assertThrows(NullPointerException.class, () -> base.withTone(null));
        assertThrows(NullPointerException.class, () -> PreviewPill.countdown(null));
    }

    @Test
    void missingBackendFailsClosed() {
        assertThrows(IllegalStateException.class,
            () -> PreviewPills.of(new TestPlugin()).set("key", PreviewPill.of("x")));
    }

    private void install() {
        backend = new TestBackend();
        TitanRuntime.setPreviewPillBackend(backend);
    }

    private static String repeat(char c, int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    private static final class TestPlugin implements Plugin {}

    private static final class Write {
        Plugin plugin;
        String key;
        String text;
        int tone;
        int flags;
        long countdownMs;
    }

    private static final class TestBackend implements PreviewPillBackend {
        final List<Write> writes = new ArrayList<>();
        boolean accept = true;

        Write last() {
            return writes.get(writes.size() - 1);
        }

        @Override
        public boolean previewPillWrite(Plugin plugin, String key, String text, int tone,
                                        int flags, long countdownMs) {
            Write write = new Write();
            write.plugin = plugin;
            write.key = key;
            write.text = text;
            write.tone = tone;
            write.flags = flags;
            write.countdownMs = countdownMs;
            writes.add(write);
            return accept;
        }
    }
}
