package net.titan.api.config;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Owned values for the richer native controls. Values use the existing string
 * storage slot. Mutable AWT objects and sets are copied on both read and write.
 * Normally created from an annotated Config getter by the runtime scanner. */
public final class ValueSetting<T> implements ConfigSetting {
    public static final int MAX_OPTIONS = 32;
    private final String key, name, section, description;
    private final String units;
    private final int position;
    private volatile boolean hidden;
    private final Kind kind;
    private final Class<? extends Enum<?>> enumType;
    private final T defaultValue;
    private volatile T value;

    public ValueSetting(String key, String name, String section, String description, int position,
                        boolean hidden, Kind kind, Class<? extends Enum<?>> enumType, T defaultValue) {
        this(key, name, section, description, position, hidden, kind, enumType, defaultValue, "");
    }
    public ValueSetting(String key, String name, String section, String description, int position,
                        boolean hidden, Kind kind, Class<? extends Enum<?>> enumType, T defaultValue, String units) {
        this.units = SettingUnits.checked(units);
        if (!units.isEmpty() && kind != Kind.LONG && kind != Kind.DOUBLE)
            throw new IllegalArgumentException("Units require a numeric setting");
        this.key = key; this.name = name; this.section = section; this.description = description;
        this.position = position; this.hidden = hidden; this.kind = kind; this.enumType = enumType;
        if (kind == Kind.ENUM_SET && (enumType == null || !enumType.isEnum()
            || enumType.getEnumConstants().length == 0 || enumType.getEnumConstants().length > MAX_OPTIONS))
            throw new IllegalArgumentException("Enum selection requires 1..32 constants");
        this.defaultValue = checked(defaultValue); this.value = copy(this.defaultValue);
    }
    public String key() { return key; }
    public String name() { return name; }
    public String section() { return section; }
    public String description() { return description; }
    public String units() { return units; }
    public int position() { return position; }
    public boolean hidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
    public Kind kind() { return kind; }
    public Class<? extends Enum<?>> enumType() { return enumType; }
    public T defaultValue() { return copy(defaultValue); }
    public T get() { return copy(value); }
    public void set(T next) { value = checked(next); }
    public String encodedValue() { return encode(value); }
    public String encodedDefault() { return encode(defaultValue); }
    public String control() {
        switch (kind) {
            case LONG: return "integer64"; case DOUBLE: return "real64"; case ENUM_SET: return "multiSelect";
            case KEYBIND: return "keybind"; case MODIFIERLESS_KEYBIND: return "modifierlessKeybind";
            case DIMENSION: return "dimension"; case POINT: return "point"; case FONT: return "font";
            default: throw new IllegalArgumentException("Not an extended setting kind");
        }
    }
    private T checked(T next) {
        T owned = copy(next);
        encode(owned); // Validate Unicode and the control representation, never truncate.
        return owned;
    }
    @SuppressWarnings({"unchecked", "rawtypes"}) private T copy(T input) {
        if (input == null && kind != Kind.POINT && kind != Kind.DIMENSION)
            throw new IllegalArgumentException("Setting value cannot be null");
        switch (kind) {
            case LONG: if (input instanceof Long) return input; break;
            case DOUBLE: if (input instanceof Double && Double.isFinite((Double) input)) return input; break;
            case ENUM_SET:
                if (!(input instanceof Set)) break;
                EnumSet result = EnumSet.noneOf((Class) enumType);
                for (Object item : (Set<?>) input) {
                    if (!(item instanceof Enum) || ((Enum<?>) item).getDeclaringClass() != enumType)
                        throw new IllegalArgumentException("Selection constant belongs to another enum");
                    result.add(item);
                }
                return (T) Collections.unmodifiableSet(result);
            case DIMENSION:
                if (input == null) return null;
                if (input instanceof Dimension && ((Dimension) input).width >= 0 && ((Dimension) input).height >= 0)
                    return (T) new Dimension((Dimension) input);
                break;
            case POINT: if (input == null || input instanceof Point) return input == null ? null : (T) new Point((Point) input); break;
            case FONT:
                if (input instanceof Font) {
                    Font font = (Font) input;
                    String family = font.getName();
                    Font plain = new Font(family, font.getStyle(), font.getSize());
                    if (!family.isEmpty() && family.indexOf(0) < 0 && family.getBytes(StandardCharsets.UTF_8).length <= 1024
                        && font.getSize() >= 1 && font.equals(plain)) return (T) plain;
                }
                break;
            case KEYBIND: if (input != null && input.getClass() == Keybind.class) return input; break;
            case MODIFIERLESS_KEYBIND: if (input instanceof ModifierlessKeybind) return input; break;
            default: break;
        }
        throw new IllegalArgumentException("Value does not match setting kind " + kind);
    }
    private String encode(T input) {
        switch (kind) {
            case LONG: return Long.toString((Long) input);
            case DOUBLE: return Double.toString((Double) input);
            case ENUM_SET:
                List<String> ordinals = new ArrayList<>();
                for (Object item : (Set<?>) input) ordinals.add(Integer.toString(((Enum<?>) item).ordinal()));
                return "[" + String.join(",", ordinals) + "]";
            case DIMENSION:
                if (input == null) return "null";
                Dimension size = (Dimension) input; return "{\"width\":" + size.width + ",\"height\":" + size.height + "}";
            case POINT:
                if (input == null) return "null";
                Point point = (Point) input; return "{\"x\":" + point.x + ",\"y\":" + point.y + "}";
            case FONT:
                Font font = (Font) input; return "{\"family\":" + quote(font.getName()) + ",\"size\":" + font.getSize()
                    + ",\"bold\":" + font.isBold() + ",\"italic\":" + font.isItalic() + "}";
            case KEYBIND: case MODIFIERLESS_KEYBIND:
                Keybind key = (Keybind) input; return "{\"keyCode\":" + key.keyCode() + ",\"modifiers\":" + key.modifiers() + "}";
            default: throw new IllegalArgumentException("Not an extended setting kind");
        }
    }
    private static String quote(String value) {
        StringBuilder out = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\"' || c == '\\') out.append('\\').append(c);
            else if (c < 32) out.append(String.format(java.util.Locale.ROOT, "\\u%04x", (int) c));
            else if (Character.isHighSurrogate(c)) {
                if (i + 1 >= value.length() || !Character.isLowSurrogate(value.charAt(i + 1))) throw new IllegalArgumentException("Invalid Unicode");
                out.append(c).append(value.charAt(++i));
            } else if (Character.isLowSurrogate(c)) throw new IllegalArgumentException("Invalid Unicode");
            else out.append(c);
        }
        return out.append('\"').toString();
    }
}
