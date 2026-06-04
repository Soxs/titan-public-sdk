package net.titan.api.utils;

import net.titan.api.Cs2Result;
import net.titan.api.Titan;

import java.util.Optional;
import java.util.OptionalInt;

/// CS2 client-script runner + quest state. Mirrors the C++
/// {@code titan::state::script()} facade.
public final class Script {
    private Script() {}

    public static Optional<Cs2Result> run(int scriptId, int... intArgs) {
        return Titan.client().runClientScript(scriptId, intArgs);
    }

    public static OptionalInt runAndGetInt(int scriptId, int... intArgs) {
        return Titan.client().runClientScriptForInt(scriptId, intArgs);
    }

    /// Returns: 0 = in progress, 1 = not started, 2 = finished, -1 = error.
    public static int questState(int questId) {
        return Titan.client().questState(questId);
    }
}
