package net.titan.api;

import com.google.inject.Injector;
import net.titan.api.internal.TitanRuntime;

public final class Titan {
    private static final WebWalker WEB_WALKER = new WebWalker();
    private static final WebWalk WEB_WALK = new WebWalk();
    private static final WorldMap WORLD_MAP = new WorldMap();

    private Titan() {}

    public static Injector getInjector() {
        return TitanRuntime.getInjector();
    }

    public static <T> T getInstance(Class<T> type) {
        return getInjector().getInstance(type);
    }

    public static <T> T getInjector(Class<T> type) {
        return getInstance(type);
    }

    public static <T> T get(Class<T> type) {
        return getInstance(type);
    }

    public static Client client() {
        return getInstance(Client.class);
    }

    /** Read-only asynchronous web-path generation (SDK 112). */
    public static WebWalker webWalker() {
        return WEB_WALKER;
    }

    /** Host-driven web-walk executor over generated routes (SDK 114). */
    public static WebWalk webWalk() {
        return WEB_WALK;
    }

    /** Read-only state and projection helpers for the in-game world map (SDK 113). */
    public static WorldMap worldMap() {
        return WORLD_MAP;
    }

    public static void runOnClientTick(Runnable callback) {
        TitanRuntime.getSchedulerBackend().runOnClientTick(callback);
    }

    public static void runOnRender(Runnable callback) {
        TitanRuntime.getSchedulerBackend().runOnRender(callback);
    }
}
