package net.titan.api;

import com.google.inject.Injector;
import net.titan.api.internal.TitanRuntime;

public final class Titan {
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

    public static void runOnClientTick(Runnable callback) {
        TitanRuntime.getSchedulerBackend().runOnClientTick(callback);
    }

    public static void runOnRender(Runnable callback) {
        TitanRuntime.getSchedulerBackend().runOnRender(callback);
    }
}
