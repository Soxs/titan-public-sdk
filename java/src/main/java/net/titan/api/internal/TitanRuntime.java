package net.titan.api.internal;

import com.google.inject.Injector;

public final class TitanRuntime {
    private static volatile Injector injector;

    private TitanRuntime() {}

    public static Injector getInjector() {
        Injector value = injector;
        if (value == null) {
            throw new IllegalStateException(
                "Titan Java plugin injector is not available for this client tab yet.");
        }
        return value;
    }

    public static void setInjector(Injector injector) {
        TitanRuntime.injector = injector;
    }

    public static void clearInjector(Injector injector) {
        if (TitanRuntime.injector == injector) {
            TitanRuntime.injector = null;
        }
    }
}
