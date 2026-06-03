package net.titan.api.queries;

import net.titan.api.Client;
import net.titan.api.Locatable;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Function;

public abstract class NamedLocatableQuery
        <T extends Locatable<?>, Q extends NamedLocatableQuery<T, Q>>
        extends LocatableQuery<T, Q> {
    private final Function<T, String> nameExtractor;

    protected NamedLocatableQuery(Client client, Collection<T> source,
                                  Function<T, String> nameExtractor) {
        super(client, source);
        this.nameExtractor = nameExtractor;
    }

    public Q nameContains(String needle) {
        return where(item -> containsIgnoreCase(name(item), needle));
    }

    public Q nameEquals(String name) {
        return where(item -> equalsIgnoreCase(name(item), name));
    }

    public Q namesAnyOf(String... names) {
        return where(item -> {
            String value = name(item);
            if (names == null) return false;
            for (String name : names) {
                if (containsIgnoreCase(value, name)) return true;
            }
            return false;
        });
    }

    protected String name(T item) {
        String value = nameExtractor == null ? "" : nameExtractor.apply(item);
        return value == null ? "" : value;
    }

    protected static boolean containsIgnoreCase(String haystack, String needle) {
        if (needle == null || needle.isEmpty()) return true;
        if (haystack == null || haystack.length() < needle.length()) return false;
        return haystack.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
    }

    protected static boolean equalsIgnoreCase(String left, String right) {
        if (left == null) left = "";
        if (right == null) right = "";
        return left.equalsIgnoreCase(right);
    }
}
