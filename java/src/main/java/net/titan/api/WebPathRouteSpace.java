package net.titan.api;

/** Coordinate domain used while generating a web path. */
public enum WebPathRouteSpace {
    GLOBAL(0),
    CURRENT_INSTANCE(1);

    private final int value;

    WebPathRouteSpace(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static WebPathRouteSpace fromValue(int value) {
        return value == CURRENT_INSTANCE.value ? CURRENT_INSTANCE : GLOBAL;
    }
}
