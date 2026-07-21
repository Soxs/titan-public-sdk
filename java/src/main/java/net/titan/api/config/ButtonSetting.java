package net.titan.api.config;

/// Value-less action button setting. Unlike the other {@link ConfigSetting}
/// holders it stores no value; it is pure metadata. The action is the body of
/// the {@code @ConfigButton default void} method, which the runtime invokes on
/// the config proxy when the button is clicked.
///
/// Runtime-constructed (like {@link BoolSetting}); plugin authors declare a
/// button via {@link ConfigButton}, not by instantiating this class.
public final class ButtonSetting implements ConfigSetting {
    private final String key;
    private final String name;
    private final String section;
    private final String description;
    private final int position;
    private final boolean hidden;

    public ButtonSetting(String key, String name, String section,
                         String description, int position, boolean hidden) {
        this.key = key;
        this.name = name;
        this.section = section;
        this.description = description;
        this.position = position;
        this.hidden = hidden;
    }

    public String key() { return key; }
    public String name() { return name; }
    public String section() { return section; }
    public String description() { return description; }
    public int position() { return position; }
    public boolean hidden() { return hidden; }

    @Override
    public Kind kind() { return Kind.BUTTON; }
}
