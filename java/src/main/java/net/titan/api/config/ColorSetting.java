package net.titan.api.config;

/// Color (0xRRGGBB / 0xAARRGGBB) plugin setting. The value is the packed
/// integer color. Mirrors the C++ {@code titan::ColorSetting}.
public final class ColorSetting implements ConfigSetting {
    private final String key;
    private final String name;
    private final String section;
    private final String description;
    private final int position;
    private final boolean hidden;
    private final int defaultValue;
    private volatile int value;

    public ColorSetting(String key, String name, String section,
                        String description, int position, boolean hidden,
                        int defaultValue) {
        this.key = key;
        this.name = name;
        this.section = section;
        this.description = description;
        this.position = position;
        this.hidden = hidden;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String key() { return key; }
    public String name() { return name; }
    public String section() { return section; }
    public String description() { return description; }
    public int position() { return position; }
    public boolean hidden() { return hidden; }
    public int defaultValue() { return defaultValue; }
    public int get() { return value; }
    public void set(int value) { this.value = value; }

    @Override
    public Kind kind() { return Kind.COLOR; }
}
