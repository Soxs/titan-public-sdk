package net.titan.api.config;

import java.awt.Dimension;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SettingUnitsTest {
    private static IntSetting integer(String units) {
        return new IntSetting("delay", "Delay", "", "", 0, false, 10, 0, 100, true, units);
    }

    @Test void additiveConstructorsKeepDefaultsValuesAndOriginalEmptyMetadata() {
        IntSetting old = new IntSetting("k", "n", "", "", 0, false, 3, 0, 10);
        assertEquals("", old.units());
        assertEquals("", new BoolSetting("k", "n", "", "", 0, false, true).units());
        IntSetting count = integer("ms");
        count.set(25); assertEquals(25, count.get()); assertEquals(10, count.defaultValue());
        assertEquals("ms", count.units());
        ValueSetting<Long> large = new ValueSetting<>("k", "n", "", "", 0, false,
            ConfigSetting.Kind.LONG, null, Long.MIN_VALUE, "bytes");
        large.set(Long.MAX_VALUE);
        assertEquals("9223372036854775807", large.encodedValue());
        assertEquals("-9223372036854775808", large.encodedDefault());
        assertEquals("bytes", large.units());
        ValueSetting<Double> rate = new ValueSetting<>("k", "n", "", "", 0, false,
            ConfigSetting.Kind.DOUBLE, null, -0.0, "%");
        assertEquals("-0.0", rate.encodedValue()); assertEquals("%", rate.units());
    }

    @Test void unitsUseUtf8BoundAndLiteralSafeUnicodeWithoutTruncation() {
        for (String text : List.of("", "ms", "%", "m\u00b2/s", "\u03a9".repeat(128),
                "\ud83d\udca1".repeat(64), "a\u200db"))
            assertEquals(text, integer(text).units());
        for (String text : List.of("x".repeat(257), "\u03a9".repeat(129), "\ud83d\udca1".repeat(65),
                "\0", "\n", "\t", "\u007f", "\u0085", "\u2028", "\u2029", "\u061c",
                "\u200e", "\u202e", "\u2066", "\u206f", "\ud800", "\udc00"))
            assertThrows(IllegalArgumentException.class, () -> integer(text));
        assertThrows(NullPointerException.class, () -> integer(null));
    }

    @Test void nonnumericProgrammaticHoldersCannotAdvertiseUnits() {
        assertThrows(IllegalArgumentException.class, () -> new ValueSetting<>("k", "n", "", "", 0, false,
            ConfigSetting.Kind.DIMENSION, null, new Dimension(3, 4), "px"));
        assertEquals("", new ValueSetting<>("k", "n", "", "", 0, false,
            ConfigSetting.Kind.DIMENSION, null, new Dimension(3, 4)).units());
    }
}
