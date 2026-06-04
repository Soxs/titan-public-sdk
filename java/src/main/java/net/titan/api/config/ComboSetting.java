package net.titan.api.config;

import java.util.Collections;
import java.util.List;

/// Enum-like combo plugin setting. The value is the selected option's integer
/// (for enum-backed config items this is the enum constant ordinal). Mirrors
/// the C++ {@code titan::ComboSetting}.
public final class ComboSetting implements ConfigSetting {
    public static final class Option {
        private final int value;
        private final String label;

        public Option(int value, String label) {
            this.value = value;
            this.label = label == null ? "" : label;
        }

        public int value() { return value; }
        public String label() { return label; }
    }

    private final String key;
    private final String name;
    private final String section;
    private final String description;
    private final int position;
    private final boolean hidden;
    private final int defaultValue;
    private final List<Option> options;
    private volatile int value;

    public ComboSetting(String key, String name, String section,
                        String description, int position, boolean hidden,
                        int defaultValue, List<Option> options) {
        this.key = key;
        this.name = name;
        this.section = section;
        this.description = description;
        this.position = position;
        this.hidden = hidden;
        this.defaultValue = defaultValue;
        this.options = options == null ? Collections.emptyList()
            : Collections.unmodifiableList(options);
        this.value = defaultValue;
    }

    public String key() { return key; }
    public String name() { return name; }
    public String section() { return section; }
    public String description() { return description; }
    public int position() { return position; }
    public boolean hidden() { return hidden; }
    public int defaultValue() { return defaultValue; }
    public List<Option> options() { return options; }
    public int get() { return value; }
    public void set(int value) { this.value = value; }

    @Override
    public Kind kind() { return Kind.COMBO; }
}
