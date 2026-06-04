package net.titan.api.config;

/// String (text input) plugin setting. When {@code secret} is set the value is
/// stored as a DPAPI-protected string. Mirrors the C++ {@code titan::StringSetting}
/// / {@code titan::ProtectedStringSetting}.
public final class StringSetting implements ConfigSetting {
    private final String key;
    private final String name;
    private final String section;
    private final String description;
    private final int position;
    private final boolean hidden;
    private final boolean secret;
    private final String defaultValue;
    private volatile String value;

    public StringSetting(String key, String name, String section,
                         String description, int position, boolean hidden,
                         boolean secret, String defaultValue) {
        this.key = key;
        this.name = name;
        this.section = section;
        this.description = description;
        this.position = position;
        this.hidden = hidden;
        this.secret = secret;
        this.defaultValue = defaultValue == null ? "" : defaultValue;
        this.value = this.defaultValue;
    }

    public String key() { return key; }
    public String name() { return name; }
    public String section() { return section; }
    public String description() { return description; }
    public int position() { return position; }
    public boolean hidden() { return hidden; }
    public boolean secret() { return secret; }
    public String defaultValue() { return defaultValue; }
    public String get() { return value; }
    public void set(String value) { this.value = value == null ? "" : value; }

    @Override
    public Kind kind() { return secret ? Kind.PROTECTED_STRING : Kind.STRING; }
}
