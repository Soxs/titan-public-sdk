package net.titan.api.events;

public final class ConfigChanged {
    private final String group;
    private final String key;

    public ConfigChanged(String group, String key) {
        this.group = group;
        this.key = key;
    }

    public String group() { return group; }
    public String key() { return key; }
}
