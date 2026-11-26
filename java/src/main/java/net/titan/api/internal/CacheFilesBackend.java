package net.titan.api.internal;

import net.titan.api.CacheFiles;

/** Optional runtime binding; absent hosts fail closed. */
public interface CacheFilesBackend {
    CacheFilesBackend UNAVAILABLE = new CacheFilesBackend() {};
    default CacheFiles.File cacheFile(int index, int group, int file) {
        return new CacheFiles.File(CacheFiles.Status.UNSUPPORTED, null);
    }
    default CacheFiles.Ids cacheFileIds(int index, int group) {
        return new CacheFiles.Ids(CacheFiles.Status.UNSUPPORTED, null);
    }
}
