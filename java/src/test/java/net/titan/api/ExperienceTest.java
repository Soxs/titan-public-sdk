package net.titan.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExperienceTest {
    @Test void everyThresholdAndItsAdjacentValuesFollowOriginalFormula() {
        int points = 0;
        for (int level = 1; level <= 126; level++) {
            int xp = points / 4;
            assertEquals(xp, Experience.xpForLevel(level), "threshold " + level);
            assertEquals(level, Experience.levelForXp(xp), "exact " + level);
            assertEquals(level, Experience.levelForXp(xp + 1), "above " + level);
            if (level > 1) assertEquals(level - 1, Experience.levelForXp(xp - 1), "below " + level);
            points += (int) (level + 300.0 * Math.pow(2.0, level / 7.0));
        }
    }

    @Test void informationalCapDoesNotLimitAcceptedExperience() {
        assertEquals(0, Experience.xpForLevel(1));
        assertEquals(83, Experience.xpForLevel(2));
        assertEquals(13_034_431, Experience.xpForLevel(99));
        assertEquals(188_884_740, Experience.xpForLevel(126));
        assertEquals(1, Experience.levelForXp(0));
        assertEquals(126, Experience.levelForXp(Experience.MAX_SKILL_XP));
        assertEquals(126, Experience.levelForXp(Experience.MAX_SKILL_XP + 1));
        assertEquals(126, Experience.levelForXp(Integer.MAX_VALUE));
        assertEquals(99, Experience.MAX_REAL_LEVEL);
        assertEquals(126, Experience.MAX_VIRT_LEVEL);
    }

    @Test void rejectsInvalidDomainInsteadOfClampingOrWrapping() {
        for (int level : new int[]{Integer.MIN_VALUE, -1, 0, 127, Integer.MAX_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> Experience.xpForLevel(level));
        }
        for (int xp : new int[]{Integer.MIN_VALUE, -1}) {
            assertThrows(IllegalArgumentException.class, () -> Experience.levelForXp(xp));
        }
    }
}
