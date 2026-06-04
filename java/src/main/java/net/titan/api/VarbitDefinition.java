package net.titan.api;

/// Cache varbit definition snapshot. Mirrors the C++ {@code titan::VarbitDef}
/// returned by {@code state::cache().varbit(id)}.
public final class VarbitDefinition {
    /// Origin of the {@code {varpIndex, lowBit, highBit}} triple.
    public enum Source {
        LIVE_CACHE,
        NATIVE,
        DISK;

        public static Source fromId(int id) {
            switch (id) {
                case 1: return NATIVE;
                case 2: return DISK;
                default: return LIVE_CACHE;
            }
        }
    }

    private int id;
    private int varpIndex;
    private int lowBit;
    private int highBit;
    private int source;

    public int id() { return id; }
    public int varpIndex() { return varpIndex; }
    public int lowBit() { return lowBit; }
    public int highBit() { return highBit; }
    public Source source() { return Source.fromId(source); }
}
