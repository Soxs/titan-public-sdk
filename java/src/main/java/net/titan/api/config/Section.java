package net.titan.api.config;

public final class Section {
    private final String key;
    private final String name;
    private final String description;
    private final int position;
    private final boolean closedByDefault;

    public Section(String key, String name, String description) {
        this(key, name, description, 0, false);
    }

    public Section(String key, String name, String description,
                   int position, boolean closedByDefault) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.position = position;
        this.closedByDefault = closedByDefault;
    }

    public String key() { return key; }
    public String name() { return name; }
    public String description() { return description; }
    public int position() { return position; }
    public boolean closedByDefault() { return closedByDefault; }
}
