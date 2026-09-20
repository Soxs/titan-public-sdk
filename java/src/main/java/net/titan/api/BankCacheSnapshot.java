package net.titan.api;

import java.util.List;
import java.util.ArrayList;

/** Owned remembered-bank snapshot. Unknown is distinct from a known empty bank.
 * Last observed time is UTC Unix seconds. Remembered quantities are advisory. */
public final class BankCacheSnapshot {
    private final String account, error;
    private final boolean known, live, loading;
    private final long lastObservedAt;
    private final List<InventoryItem> items;
    public BankCacheSnapshot(String account, boolean known, boolean live, boolean loading,
                             long lastObservedAt, String error, List<InventoryItem> items) {
        this.account = account; this.known = known; this.live = live; this.loading = loading;
        this.lastObservedAt = lastObservedAt; this.error = error;
        List<InventoryItem> owned = new ArrayList<>();
        if (known) for (InventoryItem item : items) if (item.quantity() > 0)
            owned.add(InventoryItem.rememberedBankItem(item.slot(), item.id(), item.quantity()));
        this.items = List.copyOf(owned);
    }
    public String account() { return account; }
    public boolean known() { return known; }
    public boolean live() { return live; }
    public boolean loading() { return loading; }
    public long lastObservedAt() { return lastObservedAt; }
    public String error() { return error; }
    public List<InventoryItem> items() { return items; }
    public long count(int... ids) {
        long total = 0;
        for (InventoryItem item : items) for (int id : ids) if (item.id() == id) {
            total += item.quantity(); break;
        }
        return total;
    }
}
