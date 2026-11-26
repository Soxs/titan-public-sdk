package net.titan.api.config;

/// Integer (slider) plugin setting. Mirrors the C++ {@code titan::IntSetting}.
public final class IntSetting implements ConfigSetting {
    private final String key;
    private final String name;
    private final String section;
    private final String description;
    private final int position;
    private volatile boolean hidden;
    private final boolean numberInput;
    private final String units;
    private final int defaultValue;
    private final int min;
    private final int max;
    private volatile int value;

    public IntSetting(String key, String name, String section,
                      String description, int position, boolean hidden,
                      int defaultValue, int min, int max) {
        this(key, name, section, description, position, hidden, defaultValue, min, max, false);
    }

    public IntSetting(String key, String name, String section, String description, int position, boolean hidden,
                         int defaultValue, int min, int max, boolean numberInput) {
        this(key, name, section, description, position, hidden, defaultValue, min, max, numberInput, "");
    }

    public IntSetting(String key, String name, String section, String description, int position, boolean hidden,
                         int defaultValue, int min, int max, boolean numberInput, String units) {
        this.units = SettingUnits.checked(units);
        this.numberInput = numberInput;
        if (numberInput && min > max) throw new IllegalArgumentException("Invalid integer input range");
        this.key = key;
        this.name = name;
        this.section = section;
        this.description = description;
        this.position = position;
        this.hidden = hidden;
        this.defaultValue = numberInput ? Math.max(min, Math.min(max, defaultValue)) : defaultValue;
        this.min = min;
        this.max = max;
        this.value = this.defaultValue;
    }

    public String key() { return key; }
    public String name() { return name; }
    public String section() { return section; }
    public String description() { return description; }
    public String units() { return units; }
    public int position() { return position; }
    public boolean hidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
    public boolean numberInput() { return numberInput; }
    public int defaultValue() { return defaultValue; }
    public int min() { return min; }
    public int max() { return max; }
    public int get() { return value; }
    public void set(int value) { this.value = numberInput ? Math.max(min, Math.min(max, value)) : value; }

    @Override
    public Kind kind() { return Kind.INTEGER; }
}
