package net.titan.api;

import java.util.List;
import java.util.Optional;

/** One available hint-arrow collection, including entries whose kind is NONE. */
public final class HintArrowSnapshot {
    private final List<HintArrow> entries;
    public HintArrowSnapshot(List<HintArrow> entries) { this.entries = List.copyOf(entries); }
    public List<HintArrow> entries() { return entries; }
    /** Server-controlled slot zero; empty when the collection has no slot zero. */
    public Optional<HintArrow> server() { return entries.stream().filter(arrow -> arrow.slot() == 0).findFirst(); }
}
