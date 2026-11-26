package net.titan.api;

/**
 * Pure RuneLite-compatible experience thresholds. No client connection is required.
 * Levels are 1..126; experience accepts the full nonnegative signed-int range.
 * The 200 million skill cap is informational, not an input limit.
 */
public final class Experience {
    public static final int MAX_REAL_LEVEL = 99;
    public static final int MAX_VIRT_LEVEL = 126;
    public static final int MAX_SKILL_XP = 200_000_000;

    // Mirrors the canonical shared/titan/experience.h table. Verify both ports
    // against the pinned original class with scripts/check_experience_parity.py.
    private static final int[] XP_FOR_LEVEL = {
        0, 83, 174, 276, 388, 512, 650, 801,
        969, 1154, 1358, 1584, 1833, 2107, 2411, 2746,
        3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028,
        7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456,
        18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224,
        41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014,
        91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040,
        203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015,
        449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257,
        992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068,
        2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776,
        4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577,
        10692629, 11805606, 13034431, 14391160, 15889109, 17542976, 19368992, 21385073,
        23611006, 26068632, 28782069, 31777943, 35085654, 38737661, 42769801, 47221641,
        52136869, 57563718, 63555443, 70170840, 77474828, 85539082, 94442737, 104273167,
        115126838, 127110260, 140341028, 154948977, 171077457, 188884740
    };

    private Experience() {}

    /** @throws IllegalArgumentException if level is outside 1..126. */
    public static int xpForLevel(int level) {
        if (level < 1 || level > MAX_VIRT_LEVEL) {
            throw new IllegalArgumentException("level must be between 1 and 126");
        }
        return XP_FOR_LEVEL[level - 1];
    }

    /** Returns 1..126; XP above the final threshold remains level 126.
     * @throws IllegalArgumentException if xp is negative.
     */
    public static int levelForXp(int xp) {
        if (xp < 0) throw new IllegalArgumentException("xp must be nonnegative");
        int low = 0, high = XP_FOR_LEVEL.length;
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (XP_FOR_LEVEL[middle] <= xp) low = middle + 1;
            else high = middle;
        }
        return low;
    }
}
