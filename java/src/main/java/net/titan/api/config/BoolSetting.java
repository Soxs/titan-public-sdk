package net.titan.api.config;

public final class BoolSetting {
    private final String key;
    private final String name;
    private final String section;
    private final String description;
    private final int position;
    private final boolean hidden;
    private final boolean defaultValue;
    private volatile boolean value;

    public BoolSetting(String key, String name, String section,
                       boolean defaultValue) {
        this(key, name, section, "", 0, false, defaultValue);
    }

    public BoolSetting(String key, String name, String section,
                       String description, int position,
                       boolean hidden, boolean defaultValue) {
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
    public boolean defaultValue() { return defaultValue; }
    public boolean get() { return value; }
    public void set(boolean value) { this.value = value; }
}
