package net.titan.api.config;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValueSettingTest {
    enum Choice { A, B, C }
    enum TooMany { A0,A1,A2,A3,A4,A5,A6,A7,A8,A9,A10,A11,A12,A13,A14,A15,A16,A17,A18,A19,A20,A21,A22,A23,A24,A25,A26,A27,A28,A29,A30,A31,A32 }
    static <T> ValueSetting<T> value(ConfigSetting.Kind kind, T initial) {
        return new ValueSetting<>("key", "Name", "", "", 0, false, kind, null, initial);
    }
    @Test void exactNumbersRetainSignedZeroAndAllLongBits() {
        ValueSetting<Long> integer = value(ConfigSetting.Kind.LONG, Long.MIN_VALUE);
        assertEquals("-9223372036854775808", integer.encodedValue()); integer.set(Long.MAX_VALUE);
        assertEquals("9223372036854775807", integer.encodedValue());
        ValueSetting<Double> real = value(ConfigSetting.Kind.DOUBLE, -0.0);
        assertEquals("-0.0", real.encodedValue()); real.set(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, real.get());
        assertThrows(IllegalArgumentException.class, () -> real.set(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> real.set(Double.POSITIVE_INFINITY));
        assertEquals(Double.MIN_VALUE, real.get());
    }
    @Test void mutableDimensionsPointsAndSelectionsCannotAliasLiveDefaultsOrValues() {
        Dimension original = new Dimension(10, 20); ValueSetting<Dimension> size = value(ConfigSetting.Kind.DIMENSION, original);
        original.width = 99; size.get().height = 99; size.defaultValue().width = 99;
        assertEquals(new Dimension(10, 20), size.get());
        assertThrows(IllegalArgumentException.class, () -> size.set(new Dimension(-1, 3)));
        ValueSetting<Point> point = value(ConfigSetting.Kind.POINT, null);
        assertNull(point.get()); assertEquals("null", point.encodedValue()); point.set(new Point(Integer.MIN_VALUE, Integer.MAX_VALUE));
        point.get().x = 0; assertEquals(Integer.MIN_VALUE, point.get().x);
        Set<Choice> source = EnumSet.of(Choice.C, Choice.A);
        ValueSetting<Set<Choice>> selection = new ValueSetting<>("k", "n", "", "", 0, false, ConfigSetting.Kind.ENUM_SET, Choice.class, source);
        source.clear(); assertEquals("[0,2]", selection.encodedValue());
        assertThrows(UnsupportedOperationException.class, () -> selection.get().clear());
    }
    @Test void nullableDimensionsRetainNullDefaultsAndOwnedConcreteEdits() {
        ValueSetting<Dimension> size = value(ConfigSetting.Kind.DIMENSION, null);
        assertNull(size.get()); assertNull(size.defaultValue());
        assertEquals("dimension", size.control());
        assertEquals("null", size.encodedValue()); assertEquals("null", size.encodedDefault());

        Dimension edit = new Dimension(0, Integer.MAX_VALUE);
        size.set(edit); edit.width = 99; size.get().height = 99;
        assertEquals(new Dimension(0, Integer.MAX_VALUE), size.get());
        assertEquals("{\"width\":0,\"height\":2147483647}", size.encodedValue());
        size.set(null);
        assertNull(size.get()); assertEquals("null", size.encodedValue());
        assertThrows(IllegalArgumentException.class, () -> size.set(new Dimension(1, -1)));
        assertNull(size.get());

        ValueSetting<Dimension> concrete = value(ConfigSetting.Kind.DIMENSION, new Dimension(10, 20));
        concrete.set(null);
        assertEquals("null", concrete.encodedValue());
        assertEquals("{\"width\":10,\"height\":20}", concrete.encodedDefault());
        concrete.set(concrete.defaultValue());
        assertEquals(new Dimension(10, 20), concrete.get());
        assertThrows(IllegalArgumentException.class, () -> value(ConfigSetting.Kind.FONT, null));
        assertThrows(IllegalArgumentException.class, () -> value(ConfigSetting.Kind.LONG, null));
    }
    @Test void fontKeepsDeclaredFamilyStyleAndWholePointSizeWithoutWireTruncation() {
        Font f = new Font("Dialog", Font.BOLD | Font.ITALIC, 17); ValueSetting<Font> font = value(ConfigSetting.Kind.FONT, f);
        assertEquals(f, font.get()); assertTrue(font.encodedValue().contains("\"bold\":true"));
        assertThrows(IllegalArgumentException.class, () -> font.set(f.deriveFont(17.5f)));
        assertThrows(IllegalArgumentException.class, () -> font.set(new Font("", Font.PLAIN, 17)));
        // Owned protocol strings do not have the old 4095-byte envelope cap.
        font.set(new Font("\u0001".repeat(1024), Font.PLAIN, 12));
        assertTrue(font.encodedValue().length() > 4095);
        assertThrows(IllegalArgumentException.class, () -> font.set(new Font("a".repeat(1025), Font.PLAIN, 12)));
    }
    @Test void keyBindingsUseActualAwtCodesAndRetainModifierOnlyCombinations() {
        Keybind extended = new Keybind(0x010000e9, KeyEvent.CTRL_DOWN_MASK);
        assertEquals(0x010000e9, extended.keyCode());
        assertEquals(0, new Keybind(KeyEvent.VK_CONTROL, KeyEvent.CTRL_DOWN_MASK).keyCode());
        ModifierlessKeybind modifiers = new ModifierlessKeybind(0, KeyEvent.SHIFT_DOWN_MASK);
        assertEquals(KeyEvent.SHIFT_DOWN_MASK, modifiers.modifiers());
        assertEquals(524, new Keybind(KeyEvent.VK_WINDOWS, 0).keyCode());
        assertThrows(IllegalArgumentException.class, () -> new Keybind(12, 1));
        assertThrows(IllegalArgumentException.class, () -> new Keybind(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ModifierlessKeybind(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
    }
    @Test void realOptionQuotaAndIntegerInputRangeAreEnforcedWithoutTruncation() {
        assertThrows(IllegalArgumentException.class, () -> new ValueSetting<>("k","n","","",0,false,
            ConfigSetting.Kind.ENUM_SET,TooMany.class,Set.of()));
        IntSetting input = new IntSetting("k","n","","",0,false,200,-50,50,true);
        assertEquals(50,input.get()); input.set(-200); assertEquals(-50,input.get());
        assertThrows(IllegalArgumentException.class, () -> new IntSetting("k","n","","",0,false,0,10,-10,true));
    }
}
