package net.titan.api;

import java.util.List;
import java.util.Optional;

/**
 * Runtime-only helpers used by Titan's Java bridge after decoding snapshots.
 */
public final class ApiBindings {
    private ApiBindings() {}

    public static <T> T bind(T value, Client client) {
        if (value instanceof ClientBacked) {
            ((ClientBacked) value).bindClient(client);
        }
        if (value instanceof ItemContainer) {
            ((ItemContainer) value).bindChildren(client);
        }
        return value;
    }

    public static <T> Optional<T> bind(Optional<T> value, Client client) {
        value.ifPresent(item -> bind(item, client));
        return value;
    }

    public static <T> List<T> bind(List<T> values, Client client) {
        for (T value : values) bind(value, client);
        return values;
    }
}
