package net.titan.api.utils;

import net.titan.api.Skill;
import net.titan.api.Titan;
import net.titan.api.VarPlayerId;

public final class Magic {
    public static final int SPELLBOOK_VARBIT = 4070;

    private Magic() {}

    public enum SpellBook {
        STANDARD(0),
        ANCIENT(1),
        LUNAR(2),
        NECROMANCY(3);

        private final int id;

        SpellBook(int id) {
            this.id = id;
        }

        public int id() { return id; }

        public static SpellBook fromId(int id) {
            for (SpellBook value : values()) {
                if (value.id == id) return value;
            }
            return STANDARD;
        }
    }

    public static final class SpellInfo {
        private final String name;
        private final int level;
        private final int widget;
        private final SpellBook book;
        private final boolean members;
        private final int menuEntryId;

        public SpellInfo(String name, int level, int widget,
                         SpellBook book, boolean members, int menuEntryId) {
            this.name = name == null ? "" : name;
            this.level = level;
            this.widget = widget;
            this.book = book == null ? SpellBook.STANDARD : book;
            this.members = members;
            this.menuEntryId = menuEntryId;
        }

        public String name() { return name; }
        public int level() { return level; }
        public int widget() { return widget; }
        public SpellBook book() { return book; }
        public boolean members() { return members; }
        public int menuEntryId() { return menuEntryId; }
    }

    public static SpellBook currentSpellBook() {
        return SpellBook.fromId(Titan.client().varbit(SPELLBOOK_VARBIT));
    }

    public static int magicLevel() {
        return Titan.client().realSkillLevel(Skill.MAGIC);
    }

    public static boolean canCast(SpellInfo spell) {
        return spell != null &&
            currentSpellBook() == spell.book() &&
            magicLevel() >= spell.level();
    }

    public static boolean isHomeTeleportOnCooldown() {
        return Titan.client().varp(VarPlayerId.LAST_HOME_TELEPORT) > 0;
    }

    public static boolean select(SpellInfo spell) {
        return false;
    }

    public static boolean cast(SpellInfo spell) {
        return false;
    }

    public static boolean castOn(SpellInfo spell, Object target) {
        return false;
    }
}
