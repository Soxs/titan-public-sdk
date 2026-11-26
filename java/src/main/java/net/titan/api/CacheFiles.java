package net.titan.api;

import java.util.Objects;
import net.titan.api.internal.TitanRuntime;

/** Owned copies of files in the game's own local cache, checked against its
 * reference table. Any thread. A read goes to the disk, so keep what you reuse. */
public final class CacheFiles {
    private static final int MAX_INDEX = 254;
    private CacheFiles() {}

    public enum Status {
        READY,
        /** The reference table lists no such file. */
        ABSENT,
        /** The cache is not open yet. Ask again later. */
        NOT_READY, INVALID, UNSUPPORTED,
        /** Listed, but not stored, damaged, encrypted or too large. */
        FAILED, BUFFER_TOO_SMALL;

        public static Status fromValue(int value) {
            return value >= 0 && value < values().length ? values()[value] : FAILED;
        }
    }

    /** One file's bytes, empty unless ready. */
    public static final class File {
        private final Status status;
        private final byte[] bytes;
        public File(Status status, byte[] bytes) {
            this.status = Objects.requireNonNull(status, "status");
            this.bytes = status == Status.READY ? bytes.clone() : new byte[0];
        }
        public Status status() { return status; }
        public boolean ready() { return status == Status.READY; }
        public byte[] bytes() { return bytes.clone(); }
    }

    /** One group's file IDs in ascending order, empty unless ready. */
    public static final class Ids {
        private final Status status;
        private final int[] ids;
        public Ids(Status status, int[] ids) {
            this.status = Objects.requireNonNull(status, "status");
            this.ids = status == Status.READY ? ids.clone() : new int[0];
        }
        public Status status() { return status; }
        public boolean ready() { return status == Status.READY; }
        public int[] ids() { return ids.clone(); }
    }

    /** Such as index 8 for sprites, 12 for client scripts or 2 for configuration. */
    public static File read(int index, int group, int file) {
        if (index < 0 || index > MAX_INDEX || group < 0 || file < 0) return new File(Status.INVALID, null);
        return TitanRuntime.getCacheFilesBackend().cacheFile(index, group, file);
    }
    public static File read(int index, int group) { return read(index, group, 0); }
    public static Ids fileIds(int index, int group) {
        if (index < 0 || index > MAX_INDEX || group < 0) return new Ids(Status.INVALID, null);
        return TitanRuntime.getCacheFilesBackend().cacheFileIds(index, group);
    }
}
