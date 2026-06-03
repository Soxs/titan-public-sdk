package net.titan.api;

public enum HeadIcon {
    MELEE(0, "Protect from Melee", "MELEE", 0xE0FF4040),
    RANGED(1, "Protect from Ranged", "RANGE", 0xE040FF60),
    MAGIC(2, "Protect from Magic", "MAGE", 0xE04080FF),
    RETRIBUTION(3, "Retribution", "RETRI", 0xE0FF4040),
    SMITE(4, "Smite", "SMITE", 0xE0FFD040),
    REDEMPTION(5, "Redemption", "REDEM", 0xE0FFD040),
    RANGE_MAGE(6, "Protect Range+Magic", "R+M", 0xE0C060FF),
    RANGE_MELEE(7, "Protect Range+Melee", "R+Me", 0xE0C060FF),
    MAGE_MELEE(8, "Protect Magic+Melee", "M+Me", 0xE0C060FF),
    RANGE_MAGE_MELEE(9, "Protect All", "R+M+Me", 0xE0C060FF),
    WRATH(10, "Wrath", "WRATH", 0xE0FF8030),
    SOUL_SPLIT(11, "Soul Split", "SOUL", 0xE0E0E0E0),
    DEFLECT_MELEE(12, "Deflect Melee", "D-Mel", 0xE0FF4040),
    DEFLECT_RANGE(13, "Deflect Ranged", "D-Rng", 0xE040FF60),
    DEFLECT_MAGE(14, "Deflect Magic", "D-Mag", 0xE04080FF),
    UNKNOWN(-1, "Unknown", "?", 0xE0C0C0C0);

    private final int id;
    private final String displayName;
    private final String shortName;
    private final int color;

    HeadIcon(int id, String displayName, String shortName, int color) {
        this.id = id;
        this.displayName = displayName;
        this.shortName = shortName;
        this.color = color;
    }

    public int id() { return id; }
    public String displayName() { return displayName; }
    public String shortName() { return shortName; }
    public int color() { return color; }
    public boolean isPrayer() { return id >= MELEE.id && id <= RANGE_MAGE_MELEE.id; }
    public boolean isCurse() { return id >= WRATH.id && id <= DEFLECT_MAGE.id; }

    public static boolean isValid(int raw) {
        return raw >= MELEE.id && raw <= DEFLECT_MAGE.id;
    }

    public static HeadIcon fromId(int raw) {
        for (HeadIcon icon : values()) {
            if (icon.id == raw) return icon;
        }
        return UNKNOWN;
    }
}
