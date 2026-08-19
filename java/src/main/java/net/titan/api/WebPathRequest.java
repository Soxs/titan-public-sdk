package net.titan.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Mutable request builder for read-only web-path generation. */
public final class WebPathRequest {
    public static final int MAX_FORBIDDEN_TILES = 4096;
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
    public static final Duration MAX_TIMEOUT = Duration.ofMinutes(10);

    private WorldPoint start = new WorldPoint();
    private WorldPoint destination;
    private WebPathRouteSpace routeSpace = WebPathRouteSpace.GLOBAL;
    private WebPathOptions options = new WebPathOptions();
    private Duration timeout = DEFAULT_TIMEOUT;
    private boolean useLocalPlayer = true;
    private List<WorldPoint> forbiddenTiles = Collections.emptyList();

    public WebPathRequest(WorldPoint destination) {
        this.destination = Objects.requireNonNull(destination, "destination");
    }

    public WorldPoint start() { return start; }
    public WorldPoint destination() { return destination; }
    public WebPathRouteSpace routeSpace() { return routeSpace; }
    public WebPathOptions options() { return options; }
    public Duration timeout() { return timeout; }
    public boolean useLocalPlayer() { return useLocalPlayer; }
    public List<WorldPoint> forbiddenTiles() { return forbiddenTiles; }

    /** Set an explicit start and disable the default local-player start. */
    public WebPathRequest start(WorldPoint value) {
        start = Objects.requireNonNull(value, "start");
        useLocalPlayer = false;
        return this;
    }

    public WebPathRequest destination(WorldPoint value) {
        destination = Objects.requireNonNull(value, "destination");
        return this;
    }

    public WebPathRequest routeSpace(WebPathRouteSpace value) {
        routeSpace = Objects.requireNonNull(value, "routeSpace");
        return this;
    }

    public WebPathRequest options(WebPathOptions value) {
        options = Objects.requireNonNull(value, "options");
        return this;
    }

    public WebPathRequest timeout(Duration value) {
        timeout = Objects.requireNonNull(value, "timeout");
        return this;
    }

    public WebPathRequest useLocalPlayer(boolean value) {
        useLocalPlayer = value;
        return this;
    }

    public WebPathRequest forbiddenTiles(List<WorldPoint> value) {
        Objects.requireNonNull(value, "forbiddenTiles");
        if (value.size() > MAX_FORBIDDEN_TILES) {
            throw new IllegalArgumentException(
                "forbiddenTiles exceeds " + MAX_FORBIDDEN_TILES + " entries");
        }
        ArrayList<WorldPoint> copy = new ArrayList<>(value.size());
        for (WorldPoint point : value) {
            copy.add(Objects.requireNonNull(point, "forbidden tile"));
        }
        forbiddenTiles = Collections.unmodifiableList(copy);
        return this;
    }
}
