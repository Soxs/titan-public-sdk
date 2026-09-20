package net.titan.api;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Locale;

/** Read-only Java facade over the C++ host's ItemCache (SDK 138).
 * C++ owns observation, account selection, cache state, and persistence.
 * These methods read JNI snapshots and query them; Java retains no cache state.
 * Banking actions always use live container slots. */
public final class ItemCache {
    private ItemCache() {}
    public static Optional<BankCacheSnapshot> bank() { return Titan.client().getItemCacheBank(); }
    public static boolean isLoaded() { return bank().map(BankCacheSnapshot::known).orElse(false); }
    public static List<InventoryItem> getBankItems() { return bank().map(BankCacheSnapshot::items).orElse(List.of()); }
    public static long getItemsCountInBank(int... ids) { return bank().map(s -> s.count(ids)).orElse(0L); }
    public static long count(int... ids) { return getItemsCountInBank(ids); }
    public static long countByName(String[] names, String... ignore) {
        long total = 0;
        for (InventoryItem item : getBankItems()) {
            var definition = Titan.client().itemComposition(item.id());
            if (definition.isEmpty()) continue;
            String name = definition.get().name().toLowerCase(Locale.ROOT);
            if (Arrays.stream(ignore).anyMatch(s -> name.contains(s.toLowerCase(Locale.ROOT)))) continue;
            if (Arrays.stream(names).anyMatch(s -> name.contains(s.toLowerCase(Locale.ROOT)))) total += item.quantity();
        }
        return total;
    }
    public static long getItemsCountInBank(String... names) { return countByName(names); }
    public static long getItemsCountInBank(String[] names, String[] ignore) { return countByName(names, ignore); }
}
