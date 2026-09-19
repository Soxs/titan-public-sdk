package net.titan.api;

import java.util.Objects;
import java.util.OptionalLong;

/** Immutable cached public quote. Missing prices/times stay absent, never zero.
 * A failed refresh retains the earlier quote and fetchedAt with a new error. */
public final class ItemPrice {
    private final int id;
    private final OptionalLong high, low, highTime, lowTime;
    private final long fetchedAt, lastAttemptAt;
    private final boolean loading, pending;
    private final String error;

    public ItemPrice(int id, OptionalLong high, OptionalLong low, OptionalLong highTime,
                     OptionalLong lowTime, long fetchedAt, long lastAttemptAt,
                     boolean loading, boolean pending, String error) {
        this.id = id;
        this.high = Objects.requireNonNull(high, "high");
        this.low = Objects.requireNonNull(low, "low");
        this.highTime = Objects.requireNonNull(highTime, "highTime");
        this.lowTime = Objects.requireNonNull(lowTime, "lowTime");
        this.fetchedAt = fetchedAt;
        this.lastAttemptAt = lastAttemptAt;
        this.loading = loading;
        this.pending = pending;
        this.error = error == null ? "" : error;
    }
    public int id() { return id; }
    /** Latest observed instant-buy price, using lossless 64-bit GP. */
    public OptionalLong high() { return high; }
    /** Latest observed instant-sell price, using lossless 64-bit GP. */
    public OptionalLong low() { return low; }
    /** UTC Unix seconds for the corresponding observed trade. */
    public OptionalLong highTime() { return highTime; }
    public OptionalLong lowTime() { return lowTime; }
    /** UTC Unix seconds of the last successful fetch; zero before success. */
    public long fetchedAt() { return fetchedAt; }
    public long lastAttemptAt() { return lastAttemptAt; }
    public boolean loading() { return loading; }
    public boolean pending() { return pending; }
    public String error() { return error; }
    public int getId() { return id; }
    public OptionalLong getHigh() { return high; }
    public OptionalLong getLow() { return low; }
    public OptionalLong getHighTime() { return highTime; }
    public OptionalLong getLowTime() { return lowTime; }
    public long getFetchedAt() { return fetchedAt; }
    public long getLastAttemptAt() { return lastAttemptAt; }
    public boolean isLoading() { return loading; }
    public boolean isPending() { return pending; }
    public String getError() { return error; }
}
