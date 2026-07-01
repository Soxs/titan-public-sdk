package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.SpotanimIDEntries;

public final class SpotanimID
        implements SpotanimIDConstants_0,
                   SpotanimIDConstants_1,
                   SpotanimIDConstants_2,
                   SpotanimIDConstants_3,
                   SpotanimIDConstants_4,
                   SpotanimIDConstants_5,
                   SpotanimIDConstants_6,
                   SpotanimIDConstants_7 {
    private SpotanimID() {}

    public static final String SOURCE_CATALOG = "spottypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static GamevalEntry[] entries() { return SpotanimIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return SpotanimIDEntries.byId(id);
    }
}
