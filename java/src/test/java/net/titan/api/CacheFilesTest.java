package net.titan.api;

import static org.junit.jupiter.api.Assertions.*;
import net.titan.api.internal.CacheFilesBackend;
import net.titan.api.internal.TitanRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class CacheFilesTest {
    @AfterEach void clear() { TitanRuntime.clearCacheFilesBackend(TitanRuntime.getCacheFilesBackend()); }

    @Test void missingHostsAndInvalidSelectorsAreExplicitAndNeverReachTheHost() {
        assertEquals(CacheFiles.Status.UNSUPPORTED, CacheFiles.read(8, 1).status());
        assertEquals(CacheFiles.Status.UNSUPPORTED, CacheFiles.fileIds(8, 1).status());
        int[] calls = {0};
        TitanRuntime.setCacheFilesBackend(new CacheFilesBackend() {
            @Override public CacheFiles.File cacheFile(int index, int group, int file) { calls[0]++; return new CacheFiles.File(CacheFiles.Status.ABSENT, null); }
            @Override public CacheFiles.Ids cacheFileIds(int index, int group) { calls[0]++; return new CacheFiles.Ids(CacheFiles.Status.ABSENT, null); }
        });
        for (int[] bad : new int[][]{{-1, 0, 0}, {255, 0, 0}, {8, -1, 0}, {8, 0, -1}})
            assertEquals(CacheFiles.Status.INVALID, CacheFiles.read(bad[0], bad[1], bad[2]).status());
        assertEquals(CacheFiles.Status.INVALID, CacheFiles.fileIds(255, 0).status());
        assertEquals(CacheFiles.Status.INVALID, CacheFiles.fileIds(0, -1).status());
        assertEquals(0, calls[0]);
        assertEquals(CacheFiles.Status.ABSENT, CacheFiles.read(254, 0, 0).status());
        assertEquals(1, calls[0]);
    }

    @Test void contentsAreOwnedAndOnlyAReadyResultHasAny() {
        byte[] source = {1, 2, 3};
        int[] ids = {0, 4};
        TitanRuntime.setCacheFilesBackend(new CacheFilesBackend() {
            @Override public CacheFiles.File cacheFile(int index, int group, int file) {
                assertArrayEquals(new int[]{8, 7, 0}, new int[]{index, group, file});
                return new CacheFiles.File(CacheFiles.Status.READY, source);
            }
            @Override public CacheFiles.Ids cacheFileIds(int index, int group) { return new CacheFiles.Ids(CacheFiles.Status.READY, ids); }
        });
        CacheFiles.File file = CacheFiles.read(8, 7);
        assertTrue(file.ready());
        source[0] = 9;
        file.bytes()[1] = 9;
        assertArrayEquals(new byte[]{1, 2, 3}, file.bytes());
        CacheFiles.Ids found = CacheFiles.fileIds(8, 7);
        ids[0] = 9;
        found.ids()[1] = 9;
        assertArrayEquals(new int[]{0, 4}, found.ids());
        assertEquals(0, new CacheFiles.File(CacheFiles.Status.FAILED, new byte[]{1}).bytes().length);
        assertEquals(CacheFiles.Status.FAILED, CacheFiles.Status.fromValue(99));
        assertEquals(CacheFiles.Status.ABSENT, CacheFiles.Status.fromValue(1));
    }
}
