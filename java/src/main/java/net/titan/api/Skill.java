package net.titan.api;

public enum Skill {
    ATTACK(0),
    DEFENCE(1),
    STRENGTH(2),
    HITPOINTS(3),
    RANGED(4),
    PRAYER(5),
    MAGIC(6),
    COOKING(7),
    WOODCUTTING(8),
    FLETCHING(9),
    FISHING(10),
    FIREMAKING(11),
    CRAFTING(12),
    SMITHING(13),
    MINING(14),
    HERBLORE(15),
    AGILITY(16),
    THIEVING(17),
    SLAYER(18),
    FARMING(19),
    RUNECRAFT(20),
    HUNTER(21),
    CONSTRUCTION(22),
    SAILING(23);

    /// Total number of trainable skills. Mirrors C++ {@code SkillInfo::MAX_SKILLS}.
    public static final int COUNT = 24;

    private final int id;

    Skill(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static Skill fromId(int id) {
        for (Skill skill : values()) {
            if (skill.id == id) return skill;
        }
        return null;
    }

    public String displayName() {
        String lower = name().toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
