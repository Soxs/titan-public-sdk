package net.titan.api.config;

public final class Section {
    private final String key;
    private final String name;
    private final String description;
    private final int position;
    private final boolean closedByDefault;
    private final String parentKey;

    public Section(String key, String name, String description) {
        this(key, name, description, 0, false);
    }

    public Section(String key, String name, String description,
                   int position, boolean closedByDefault) {
        this(key, name, description, position, closedByDefault, "");
    }

    /**
     * @param parentKey key of the section to nest this one inside, or "" for
     *                  the top level. SDK 143, Java SDK 0.1.68.
     */
    public Section(String key, String name, String description,
                   int position, boolean closedByDefault, String parentKey) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.position = position;
        this.closedByDefault = closedByDefault;
        this.parentKey = parentKey == null ? "" : parentKey;
    }

    public String key() { return key; }
    public String name() { return name; }
    public String description() { return description; }
    public int position() { return position; }
    public boolean closedByDefault() { return closedByDefault; }
    /** Key of the section this one is nested in; "" at the top level. */
    public String parentKey() { return parentKey; }
}
