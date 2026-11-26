package net.titan.api.events;

import net.titan.api.Skill;

/** One named skill whose XP, real level or boosted level changed since the previous client tick. */
public final class StatChanged {
    private final Skill skill;
    private final int xp, level, boostedLevel;

    public StatChanged(Skill skill, int xp, int level, int boostedLevel) {
        this.skill = java.util.Objects.requireNonNull(skill);
        this.xp = xp;
        this.level = level;
        this.boostedLevel = boostedLevel;
    }

    public Skill skill() { return skill; }
    public int xp() { return xp; }
    public int level() { return level; }
    public int boostedLevel() { return boostedLevel; }
}
