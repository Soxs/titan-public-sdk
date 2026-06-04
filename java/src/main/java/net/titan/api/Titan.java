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
}
