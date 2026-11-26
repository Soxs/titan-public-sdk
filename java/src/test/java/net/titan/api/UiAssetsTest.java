package net.titan.api;

import net.titan.api.internal.TitanRuntime;
import net.titan.api.internal.UiAssetsBackend;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UiAssetsTest {
    @AfterEach void clear() { TitanRuntime.clearUiAssetsBackend(TitanRuntime.getUiAssetsBackend()); }

    @Test void missingHostDoesNotSubstituteSystemFontsOrOtherIcons() {
        clear();
        for (Fonts.Face face : Fonts.Face.values()) {
            assertArrayEquals(new byte[0], Fonts.data(face));
            assertTrue(Fonts.font(face).isEmpty());
        }
        for (Skill skill : Skill.values()) {
            assertTrue(SkillIcons.get(skill, false).isEmpty());
            assertTrue(SkillIcons.get(skill, true).isEmpty());
        }
    }
    @Test void fontBytesAreOwnedAndEachFaceUsesItsExactHostId() {
        List<Integer> ids = new ArrayList<>();
        byte[] original = {0, 1, 2, 3};
        TitanRuntime.setUiAssetsBackend(new UiAssetsBackend() {
            public byte[] fontData(int face) { ids.add(face); return original; }
        });
        for (Fonts.Face face : Fonts.Face.values()) {
            byte[] returned = Fonts.data(face);
            assertArrayEquals(original, returned);
            returned[0] = 99;
            assertEquals(0, original[0]);
        }
        assertEquals(List.of(0, 1, 2), ids);
    }
    @Test void malformedHostFontAndNullBytesAreUnavailableWithoutFallback() {
        TitanRuntime.setUiAssetsBackend(new UiAssetsBackend() {
            public byte[] fontData(int face) { return face == 0 ? null : new byte[]{0, 1, 2, 3}; }
        });
        assertEquals(0, Fonts.data(Fonts.Face.REGULAR).length);
        assertTrue(Fonts.font(Fonts.Face.REGULAR).isEmpty());
        assertTrue(Fonts.font(Fonts.Face.BOLD).isEmpty());
        assertTrue(Fonts.font(Fonts.Face.SMALL).isEmpty());
        assertThrows(NullPointerException.class, () -> Fonts.data(null));
    }
    @Test void allSkillIdsAndBothOriginalVariantsPreserveOwnedStraightArgbPixels() {
        List<String> requests = new ArrayList<>();
        TitanRuntime.setUiAssetsBackend(new UiAssetsBackend() {
            public SpriteImage skillIcon(int id, boolean small) {
                requests.add(id + ":" + small);
                return new SpriteImage(2, 1, 0, 0, 2, 1, new int[]{0x80123456, small ? id : ~id});
            }
        });
        for (Skill skill : Skill.values()) for (boolean small : new boolean[]{false, true}) {
            SpriteImage image = SkillIcons.get(skill, small).orElseThrow();
            assertEquals(2, image.width()); assertEquals(1, image.height());
            assertEquals(0x80123456, image.pixels()[0]);
            assertEquals(small ? skill.id() : ~skill.id(), image.pixels()[1]);
            image.pixels()[0] = 0;
            assertEquals(0x80123456, image.pixels()[0]);
        }
        assertEquals(48, requests.size());
        assertEquals("0:false", requests.get(0)); assertEquals("23:true", requests.get(47));
        assertTrue(SkillIcons.get(0).isPresent()); assertTrue(SkillIcons.get(Skill.SAILING).isPresent());
        assertEquals(List.of("0:false", "23:false"), requests.subList(48, 50));
    }
    @Test void invalidSkillsNeverReachTheHost() {
        TitanRuntime.setUiAssetsBackend(new UiAssetsBackend() {
            public SpriteImage skillIcon(int id, boolean small) { fail("Invalid skill reached the host"); return null; }
        });
        for (int id : new int[]{Integer.MIN_VALUE, -1, Skill.COUNT, Integer.MAX_VALUE})
            assertTrue(SkillIcons.get(id, false).isEmpty());
        assertTrue(SkillIcons.get((Skill) null, true).isEmpty());
    }
    @Test void retirementOfAnOlderBackendCannotRevokeItsReplacement() {
        UiAssetsBackend first = new UiAssetsBackend() { public byte[] fontData(int face) { return new byte[]{1}; } };
        UiAssetsBackend next = new UiAssetsBackend() { public byte[] fontData(int face) { return new byte[]{2}; } };
        TitanRuntime.setUiAssetsBackend(first);
        byte[] retained = Fonts.data(Fonts.Face.REGULAR);
        TitanRuntime.setUiAssetsBackend(next);
        TitanRuntime.clearUiAssetsBackend(first);
        assertArrayEquals(new byte[]{2}, Fonts.data(Fonts.Face.REGULAR));
        TitanRuntime.clearUiAssetsBackend(next);
        assertEquals(0, Fonts.data(Fonts.Face.REGULAR).length);
        assertArrayEquals(new byte[]{1}, retained);
    }
}
