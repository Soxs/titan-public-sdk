package net.titan.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Mutable option set for {@link WebWalk#walkTo}. */
public final class WebWalkOptions {
    public static final int MANAGE_RUN = 1 << 0;
    public static final int DRINK_STAMINA = 1 << 1;
    public static final int MANUAL_TICK = 1 << 2;

    private WebPathRouteSpace routeSpace = WebPathRouteSpace.GLOBAL;
    private WebPathOptions pathOptions = new WebPathOptions();
    private Duration timeout = WebPathRequest.DEFAULT_TIMEOUT;
    private boolean manageRun = true;
    private boolean drinkStamina;
    private boolean manualTick;
    private int arriveRadius;
    private int maxDurationTicks;
    private List<WorldPoint> forbiddenTiles = Collections.emptyList();

    public WebPathRouteSpace routeSpace() { return routeSpace; }
    public WebPathOptions pathOptions() { return pathOptions; }
    public Duration timeout() { return timeout; }
    public boolean manageRun() { return manageRun; }
    public boolean drinkStamina() { return drinkStamina; }
    public boolean manualTick() { return manualTick; }
    public int arriveRadius() { return arriveRadius; }
    public int maxDurationTicks() { return maxDurationTicks; }
    public List<WorldPoint> forbiddenTiles() { return forbiddenTiles; }

    public WebWalkOptions routeSpace(WebPathRouteSpace value) {
        routeSpace = Objects.requireNonNull(value, "routeSpace");
        return this;
    }

    /** Route-planning feature toggles for the embedded path request. */
    public WebWalkOptions pathOptions(WebPathOptions value) {
        pathOptions = Objects.requireNonNull(value, "pathOptions");
        return this;
    }

    /** Route-planning timeout; defaults to 60 seconds, capped at 10 minutes. */
    public WebWalkOptions timeout(Duration value) {
        timeout = Objects.requireNonNull(value, "timeout");
        return this;
    }

    /** Toggle run and manage run energy during the walk. Defaults to true. */
    public WebWalkOptions manageRun(boolean value) {
        manageRun = value;
        return this;
    }

    /** Drink a carried stamina potion below 50% energy. Defaults to false. */
    public WebWalkOptions drinkStamina(boolean value) {
        drinkStamina = value;
        return this;
    }

    /**
     * Advance only via {@link WebWalk#advance} once per game tick instead of
     * the host's own game-tick pump. Defaults to false.
     */
    public WebWalkOptions manualTick(boolean value) {
        manualTick = value;
        return this;
    }

    /** Chebyshev arrival tolerance around the destination; zero = exact tile. */
    public WebWalkOptions arriveRadius(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("arriveRadius must be >= 0");
        }
        arriveRadius = value;
        return this;
    }

    /** Hard budget for the whole walk in game ticks; zero = unlimited. */
    public WebWalkOptions maxDurationTicks(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("maxDurationTicks must be >= 0");
        }
        maxDurationTicks = value;
        return this;
    }

    public WebWalkOptions forbiddenTiles(List<WorldPoint> value) {
        Objects.requireNonNull(value, "forbiddenTiles");
        if (value.size() > WebPathRequest.MAX_FORBIDDEN_TILES) {
            throw new IllegalArgumentException(
                "forbiddenTiles exceeds " + WebPathRequest.MAX_FORBIDDEN_TILES
                    + " entries");
        }
        ArrayList<WorldPoint> copy = new ArrayList<>(value.size());
        for (WorldPoint point : value) {
            copy.add(Objects.requireNonNull(point, "forbidden tile"));
        }
        forbiddenTiles = Collections.unmodifiableList(copy);
        return this;
    }

    /** Fixed SDK-v114 walk-flag bitset passed to the native host. */
    public int walkFlags() {
        int bits = 0;
        if (manageRun) bits |= MANAGE_RUN;
        if (drinkStamina) bits |= DRINK_STAMINA;
        if (manualTick) bits |= MANUAL_TICK;
        return bits;
    }
}
