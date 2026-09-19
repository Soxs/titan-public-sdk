package net.titan.api;

import java.util.Objects;
import java.util.OptionalLong;

/** Immutable public Wiki item metadata, shared across plugins (SDK 137). */
public final class ItemPriceMetadata {
    private final int id;
    private final String name, examine;
    private final OptionalLong buyLimit, highAlch;
    private final boolean members;

    public ItemPriceMetadata(int id, String name, String examine,
                             OptionalLong buyLimit, OptionalLong highAlch, boolean members) {
        this.id = id;
        this.name = name == null ? "" : name;
        this.examine = examine == null ? "" : examine;
        this.buyLimit = Objects.requireNonNull(buyLimit, "buyLimit");
        this.highAlch = Objects.requireNonNull(highAlch, "highAlch");
        this.members = members;
    }
    public int id() { return id; }
    public String name() { return name; }
    public String examine() { return examine; }
    public OptionalLong buyLimit() { return buyLimit; }
    public OptionalLong highAlch() { return highAlch; }
    public boolean members() { return members; }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getExamine() { return examine; }
    public OptionalLong getBuyLimit() { return buyLimit; }
    public OptionalLong getHighAlch() { return highAlch; }
    public boolean isMembers() { return members; }
}
