package net.titan.api;

import net.titan.api.internal.OverlayBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.overlay.OverlayDraw;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

class TargetTagBuilderTest {
    private static final long HOST_TAG_BITS = 0xABC0000000000000L;
    private static final long LEGACY_ACTOR = 10L | (20L << 7) | (3L << 14) | (1L << 16) | (77L << 20);
    private static final long LEGACY_LOC = 5L | (6L << 7) | (1L << 14) | (2L << 16) | (1234L << 20);

    private final List<List<Object>> packed = new ArrayList<>();
    private OverlayBackend backend;

    @AfterEach void cleanup() {
        if (backend != null) TitanRuntime.clearOverlayBackend(backend);
    }

    @Test void hostPacksTagsWithItsOwnLayout() {
        install(true);
        assertEquals(HOST_TAG_BITS | 77, OverlayDraw.buildActorTypecode(1, 77, 10, 20, 3));
        assertEquals(Arrays.asList(10, 20, 3, 1, 77, WorldView.TOP_LEVEL), packed.get(0));
        assertEquals(HOST_TAG_BITS | 1234, OverlayDraw.buildLocTypecode(1234, 2, 5, 6, 1, 4));
        assertEquals(Arrays.asList(5, 6, 1, 2, 1234, 4), packed.get(1));
    }

    @Test void unresolvedIdsNeverReachTheHost() {
        install(true);
        assertEquals(0L, OverlayDraw.buildActorTypecode(0, -1, 1, 2, 0));
        assertEquals(0L, OverlayDraw.buildLocTypecode(-1, 0, 1, 2, 0));
        assertTrue(packed.isEmpty());
    }

    @Test void olderHostsKeepThePre2421Layout() {
        install(false);
        assertEquals(LEGACY_ACTOR, OverlayDraw.buildActorTypecode(1, 77, 10, 20, 3));
        assertEquals(LEGACY_LOC | (4L << 36), OverlayDraw.buildLocTypecode(1234, 2, 5, 6, 1, 4));
        assertEquals(LEGACY_LOC, OverlayDraw.buildLocTypecode(1234, 2, 5, 6, 1, -1));
        assertEquals(3, packed.size());
    }

    @Test void noOverlayBackendFallsBackInsteadOfThrowing() {
        TitanRuntime.setOverlayBackend(null);
        assertEquals(LEGACY_ACTOR, OverlayDraw.buildActorTypecode(1, 77, 10, 20, 3));
        assertEquals(LEGACY_LOC, OverlayDraw.buildLocTypecode(1234, 2, 5, 6, 1));
    }

    private void install(boolean hostPacks) {
        backend = (OverlayBackend) Proxy.newProxyInstance(
            OverlayBackend.class.getClassLoader(), new Class<?>[] {OverlayBackend.class},
            (proxy, method, args) -> {
                if (!method.getName().equals("packTargetTag")) return null;
                packed.add(Arrays.asList(args));
                return hostPacks ? OptionalLong.of(HOST_TAG_BITS | (Integer) args[4]) : OptionalLong.empty();
            });
        TitanRuntime.setOverlayBackend(backend);
    }
}
