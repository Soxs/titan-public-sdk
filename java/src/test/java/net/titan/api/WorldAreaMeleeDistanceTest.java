package net.titan.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldAreaMeleeDistanceTest {
    @Test
    void worldAreaDetectsAdjacentPointsAndAreas() {
        WorldArea area = new WorldArea(10, 10, 2, 2, 0);

        assertTrue(area.isInMeleeDistance(new WorldPoint(12, 10, 0)));
        assertTrue(area.isInMeleeDistance(new WorldArea(12, 10, 1, 1, 0)));
        assertTrue(area.isInMeleeDistance(new WorldArea(8, 10, 2, 2, 0)));
        assertFalse(area.isInMeleeDistance(new WorldPoint(12, 12, 0)));
        assertFalse(area.isInMeleeDistance(new WorldArea(8, 8, 2, 2, 0)));
    }

    @Test
    void worldAreaRejectsOverlapDistanceAndDifferentPlanesOrViews() {
        WorldArea area = new WorldArea(10, 10, 2, 2, 0);

        assertFalse(area.isInMeleeDistance(new WorldPoint(10, 10, 0)));
        assertFalse(area.isInMeleeDistance(new WorldPoint(13, 10, 0)));
        assertFalse(area.isInMeleeDistance(new WorldPoint(12, 10, 1)));
        WorldArea explicitViewArea = new WorldArea(10, 10, 2, 2, 0, 456);
        assertFalse(explicitViewArea.isInMeleeDistance(new WorldPoint(12, 10, 0, 123)));
    }

    @Test
    void worldPointAndLocatableUseWorldAreas() {
        WorldPoint point = new WorldPoint(10, 10, 0);
        TestLocatable oneTile = new TestLocatable(11, 10, 0, 1, 1);
        TestLocatable large = new TestLocatable(11, 9, 0, 2, 3);

        assertTrue(point.isInMeleeDistance(new WorldPoint(11, 10, 0)));
        assertFalse(point.isInMeleeDistance(new WorldPoint(11, 11, 0)));
        assertTrue(point.isInMeleeDistance(oneTile));
        assertTrue(oneTile.isInMeleeDistance(point));
        assertTrue(large.isInMeleeDistance(point));
        assertFalse(point.isInMeleeDistance(new WorldPoint(10, 10, 0)));
    }

    @Test
    void actorsInheritLocatableSurface() {
        Player player = new Player();
        NPC npc = new NPC();
        Actor actor = npc;

        acceptsLocatable(player);
        acceptsLocatable(npc);
        acceptsLocatable(actor);
        assertTrue(actor instanceof Locatable<?>);
    }

    private static void acceptsLocatable(Locatable<?> locatable) {
        assertTrue(locatable instanceof Locatable<?>);
    }

    private static final class TestLocatable implements Locatable<TestLocatable> {
        private final int x;
        private final int y;
        private final int plane;
        private final int width;
        private final int height;

        private TestLocatable(int x, int y, int plane, int width, int height) {
            this.x = x;
            this.y = y;
            this.plane = plane;
            this.width = width;
            this.height = height;
        }

        @Override public int tileX() { return x; }
        @Override public int tileY() { return y; }
        @Override public int plane() { return plane; }
        @Override public int worldX() { return x; }
        @Override public int worldY() { return y; }

        @Override
        public WorldArea worldArea() {
            return new WorldArea(x, y, width, height, plane);
        }
    }
}
