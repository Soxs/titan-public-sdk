package net.titan.api;

/** Option set used by {@link WebPathRequest}. */
public final class WebPathOptions {
    public static final int TRANSPORTS = 1 << 0;
    public static final int TELEPORTS = 1 << 1;
    public static final int EQUIPPED_ITEM_TELEPORTS = 1 << 2;
    public static final int MINIGAME_TELEPORTS = 1 << 3;
    public static final int POH_ROUTES = 1 << 4;
    public static final int CHARTERS = 1 << 5;
    public static final int AVOID_WILDERNESS = 1 << 6;

    private boolean transports = true;
    private boolean teleports = true;
    private boolean equippedItemTeleports = true;
    private boolean minigameTeleports;
    private boolean pohRoutes = true;
    private boolean charters;
    private boolean avoidWilderness = true;

    public boolean transports() { return transports; }
    public boolean teleports() { return teleports; }
    public boolean equippedItemTeleports() { return equippedItemTeleports; }
    public boolean minigameTeleports() { return minigameTeleports; }
    public boolean pohRoutes() { return pohRoutes; }
    public boolean charters() { return charters; }
    public boolean avoidWilderness() { return avoidWilderness; }

    public WebPathOptions transports(boolean value) {
        transports = value;
        return this;
    }

    public WebPathOptions teleports(boolean value) {
        teleports = value;
        return this;
    }

    public WebPathOptions equippedItemTeleports(boolean value) {
        equippedItemTeleports = value;
        return this;
    }

    public WebPathOptions minigameTeleports(boolean value) {
        minigameTeleports = value;
        return this;
    }

    public WebPathOptions pohRoutes(boolean value) {
        pohRoutes = value;
        return this;
    }

    public WebPathOptions charters(boolean value) {
        charters = value;
        return this;
    }

    public WebPathOptions avoidWilderness(boolean value) {
        avoidWilderness = value;
        return this;
    }

    /** Fixed SDK-v112 option bitset passed to the native host. */
    public int bits() {
        int bits = 0;
        if (transports) bits |= TRANSPORTS;
        if (teleports) bits |= TELEPORTS;
        if (equippedItemTeleports) bits |= EQUIPPED_ITEM_TELEPORTS;
        if (minigameTeleports) bits |= MINIGAME_TELEPORTS;
        if (pohRoutes) bits |= POH_ROUTES;
        if (charters) bits |= CHARTERS;
        if (avoidWilderness) bits |= AVOID_WILDERNESS;
        return bits;
    }
}
