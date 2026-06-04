package net.titan.api.config;

/// Integer (slider) plugin setting. Mirrors the C++ {@code titan::IntSetting}.
public final class IntSetting implements ConfigSetting {
    private final String key;
    private final String name;
    private final String section;
    private final String description;
    private final int position;
    private final boolean hidden;
    private final int defaultValue;
    private final int min;
    private final int max;
    private volatile int value;

    public IntSetting(String key, String name, String section,
                      String description, int position, boolean hidden,
                      int defaultValue, int min, int max) {
        this.key = key;
        this.name = name;
        this.section = section;
        this.description = description;
        this.position = position;
        this.hidden = hidden;
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.value = defaultValue;
    }

    public String key() { return key; }
    public String name() { return name; }
    public String section() { return section; }
    public String description() { return description; }
    public int position() { return position; }
    public boolean hidden() { return hidden; }
    public int defaultValue() { return defaultValue; }
    public int min() { return min; }
    public int max() { return max; }
    public int get() { return value; }
    public void set(int value) { this.value = value; }

    @Override
    public Kind kind() { return Kind.INTEGER; }
}
