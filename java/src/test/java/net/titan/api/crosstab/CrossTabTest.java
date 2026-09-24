package net.titan.api.crosstab;

import net.titan.api.internal.CrossTabBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CrossTabTest {
    private TestBackend backend;

    @AfterEach
    void tearDown() {
        if (backend != null) TitanRuntime.clearCrossTabBackend(backend);
    }

    @Test
    void constantsMirrorTheNativeAbi() {
        // kCrossTabMaxKeyLen, kCrossTabMaxValueBytes and the CROSS_TAB_* values.
        assertEquals(63, CrossTab.MAX_KEY_LENGTH);
        assertEquals(16 * 1024, CrossTab.MAX_VALUE_BYTES);
        assertEquals(1, CrossTabBackend.FLAG_SECRET);
        assertEquals(2, CrossTabBackend.FLAG_ERASE);
        assertEquals(4, CrossTabBackend.FLAG_CONDITIONAL);

        assertEquals(0, CrossTabChanged.Kind.SET.value());
        assertEquals(1, CrossTabChanged.Kind.ERASED.value());
        assertEquals(2, CrossTabChanged.Kind.REJECTED.value());
        assertEquals(0, CrossTabChanged.Origin.REMOTE.value());
        assertEquals(1, CrossTabChanged.Origin.REPLAY.value());
        assertEquals(2, CrossTabChanged.Origin.OUTCOME.value());
        assertEquals(0, CrossTabChanged.Cause.WRITER.value());
        assertEquals(1, CrossTabChanged.Cause.SESSION_RESET.value());
        assertEquals(2, CrossTabChanged.Cause.LIMIT.value());
        assertEquals(3, CrossTabChanged.Cause.CONFLICT.value());
        assertEquals(4, CrossTabChanged.Cause.NOT_PERMITTED.value());

        for (CrossTabChanged.Kind kind : CrossTabChanged.Kind.values()) {
            assertSame(kind, CrossTabChanged.Kind.fromValue(kind.value()));
        }
        for (CrossTabChanged.Origin origin : CrossTabChanged.Origin.values()) {
            assertSame(origin, CrossTabChanged.Origin.fromValue(origin.value()));
        }
        for (CrossTabChanged.Cause cause : CrossTabChanged.Cause.values()) {
            assertSame(cause, CrossTabChanged.Cause.fromValue(cause.value()));
        }
        assertThrows(IllegalArgumentException.class, () -> CrossTabChanged.Kind.fromValue(3));
        assertThrows(IllegalArgumentException.class, () -> CrossTabChanged.Origin.fromValue(-1));
        assertThrows(IllegalArgumentException.class, () -> CrossTabChanged.Cause.fromValue(5));
    }

    @Test
    void writesCarryTheExactInstanceAndTheirFlags() {
        install();
        Plugin plugin = new TestPlugin();
        CrossTab ct = CrossTab.of(plugin);
        byte[] value = {1, 2, 3};

        assertTrue(ct.put("route.last", value));
        Write put = backend.last();
        assertSame(plugin, put.plugin);
        assertEquals("route.last", put.key);
        assertSame(value, put.value);
        assertEquals(0, put.flags);
        assertEquals(0L, put.expectedVersion);

        assertTrue(ct.put("vault.password", value, CrossTabOption.SECRET));
        assertEquals(CrossTabBackend.FLAG_SECRET, backend.last().flags);

        assertTrue(ct.erase("vault.password"));
        Write erase = backend.last();
        assertEquals(CrossTabBackend.FLAG_ERASE, erase.flags);
        assertEquals(0, erase.value.length);

        backend.nextWriteId = 41L;
        assertEquals(OptionalLong.of(41L), ct.putIf("lock", value, 7L, CrossTabOption.SECRET));
        Write putIf = backend.last();
        assertEquals(CrossTabBackend.FLAG_SECRET | CrossTabBackend.FLAG_CONDITIONAL, putIf.flags);
        assertEquals(7L, putIf.expectedVersion);

        backend.nextWriteId = 42L;
        assertEquals(OptionalLong.of(42L), ct.eraseIf("lock", 0L));
        Write eraseIf = backend.last();
        assertEquals(CrossTabBackend.FLAG_ERASE | CrossTabBackend.FLAG_CONDITIONAL, eraseIf.flags);
        assertEquals(0L, eraseIf.expectedVersion);
        assertEquals(0, eraseIf.value.length);

        // A refusal in the host is a refusal here.
        backend.nextWriteId = 0L;
        assertFalse(ct.put("route.last", value));
        assertFalse(ct.erase("route.last"));
        assertFalse(ct.putIf("lock", value, 0L).isPresent());
        assertFalse(ct.eraseIf("lock", 3L).isPresent());
    }

    @Test
    void putStringSendsUtf8AndZeroesItsTemporaryBytes() {
        install();
        CrossTab ct = CrossTab.of(new TestPlugin());

        assertTrue(ct.putString("greeting", "héllo", CrossTabOption.SECRET));
        Write write = backend.last();
        assertArrayEquals("héllo".getBytes(StandardCharsets.UTF_8), write.seen);
        assertEquals(CrossTabBackend.FLAG_SECRET, write.flags);
        // The facade's own copy of the text is zeroed once the host has it.
        assertArrayEquals(new byte[write.seen.length], write.value);
    }

    @Test
    void badInputIsRefusedBeforeCrossingTheBackend() {
        install();
        CrossTab ct = CrossTab.of(new TestPlugin());
        String longest = repeat('k', CrossTab.MAX_KEY_LENGTH);

        assertFalse(ct.put("", new byte[0]));
        assertFalse(ct.put(longest + "k", new byte[0]));
        assertFalse(ct.put("has space", new byte[0]));
        assertFalse(ct.put("café", new byte[0]));
        assertFalse(ct.put("nul\u0000", new byte[0]));
        assertFalse(ct.put("big", new byte[CrossTab.MAX_VALUE_BYTES + 1]));
        assertFalse(ct.putIf("key", new byte[0], -1L).isPresent());
        assertFalse(ct.eraseIf("key", -1L).isPresent());
        assertFalse(ct.get("bad key").isPresent());
        assertNull(ct.getSecret("bad key"));
        assertFalse(ct.info("bad key").isPresent());
        assertTrue(backend.writes.isEmpty());
        assertEquals(0, backend.reads);

        // The edges are accepted.
        assertTrue(ct.put(longest, new byte[CrossTab.MAX_VALUE_BYTES]));
        assertTrue(ct.put("A-z_0.9:/-", new byte[0]));
        assertEquals(2, backend.writes.size());

        assertThrows(NullPointerException.class, () -> CrossTab.of(null));
        assertThrows(NullPointerException.class, () -> ct.put(null, new byte[0]));
        assertThrows(NullPointerException.class, () -> ct.put("key", null));
        assertThrows(NullPointerException.class, () -> ct.putString("key", null));
        assertThrows(NullPointerException.class, () -> ct.get(null));
    }

    @Test
    void readsForwardTheExactInstance() {
        install();
        Plugin plugin = new TestPlugin();
        CrossTab ct = CrossTab.of(plugin);
        backend.value = new CrossTabValue("lumbridge".getBytes(StandardCharsets.UTF_8), 9L, false);
        backend.secret = new byte[] {7, 7};
        backend.info = new CrossTabInfo(9L, 2, true, false, false);

        CrossTabValue value = ct.get("route.last").orElseThrow();
        assertEquals("lumbridge", value.text());
        assertEquals(9L, value.version());
        assertFalse(value.pending());
        assertArrayEquals(new byte[] {7, 7}, ct.getSecret("vault.password"));
        CrossTabInfo info = ct.info("vault.password").orElseThrow();
        assertEquals(9L, info.version());
        assertEquals(2, info.size());
        assertTrue(info.secret());
        assertFalse(info.redacted());
        assertSame(plugin, backend.lastReader);
        assertEquals(3, backend.reads);

        backend.value = null;
        backend.secret = null;
        backend.info = null;
        assertFalse(ct.get("route.last").isPresent());
        assertNull(ct.getSecret("vault.password"));
        assertFalse(ct.info("vault.password").isPresent());
    }

    @Test
    void valuesAreImmutableCopies() {
        byte[] source = {1, 2, 3};
        CrossTabValue value = new CrossTabValue(source, 4L, true);
        source[0] = 9;
        byte[] out = value.bytes();
        out[1] = 9;
        assertArrayEquals(new byte[] {1, 2, 3}, value.bytes());
        assertTrue(value.pending());
    }

    @Test
    void missingBackendFailsClosed() {
        assertThrows(IllegalStateException.class,
            () -> CrossTab.of(new TestPlugin()).put("key", new byte[0]));
    }

    private void install() {
        backend = new TestBackend();
        TitanRuntime.setCrossTabBackend(backend);
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
        byte[] value;
        byte[] seen;
        int flags;
        long expectedVersion;
    }

    private static final class TestBackend implements CrossTabBackend {
        final List<Write> writes = new ArrayList<>();
        long nextWriteId = 1L;
        int reads;
        Plugin lastReader;
        CrossTabValue value;
        byte[] secret;
        CrossTabInfo info;

        Write last() {
            return writes.get(writes.size() - 1);
        }

        @Override
        public long crossTabWrite(Plugin plugin, String key, byte[] value, int flags,
                                  long expectedVersion) {
            Write write = new Write();
            write.plugin = plugin;
            write.key = key;
            write.value = value;
            write.seen = value.clone();
            write.flags = flags;
            write.expectedVersion = expectedVersion;
            writes.add(write);
            return nextWriteId;
        }

        @Override
        public Optional<CrossTabValue> crossTabGet(Plugin plugin, String key) {
            read(plugin);
            return Optional.ofNullable(value);
        }

        @Override
        public byte[] crossTabGetSecret(Plugin plugin, String key) {
            read(plugin);
            return secret;
        }

        @Override
        public Optional<CrossTabInfo> crossTabInfo(Plugin plugin, String key) {
            read(plugin);
            return Optional.ofNullable(info);
        }

        private void read(Plugin plugin) {
            lastReader = plugin;
            ++reads;
        }
    }
}
