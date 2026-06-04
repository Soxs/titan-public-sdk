package net.titan.api;

import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/// Result of a CS2 client-script execution. Mirrors the C++
/// {@code titan::Cs2Result} value returned by {@code state::script().run(...)}.
public final class Cs2Result {
    private boolean success;
    private List<Integer> ints;

    public boolean success() { return success; }

    public List<Integer> ints() {
        return ints == null ? Collections.emptyList() : Collections.unmodifiableList(ints);
    }

    /// Convenience accessor for the first integer result, if any.
    public OptionalInt firstInt() {
        List<Integer> values = ints();
        return values.isEmpty() ? OptionalInt.empty() : OptionalInt.of(values.get(0));
    }
}
