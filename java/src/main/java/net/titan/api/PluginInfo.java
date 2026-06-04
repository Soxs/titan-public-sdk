package net.titan.api;

/// Identity + lifecycle snapshot for an installed plugin. Mirrors the C++
/// {@code titan::PluginInfo} returned by the plugin-manager queries.
public final class PluginInfo {
    private String id;
    private String name;
    private boolean enabled;
    private boolean hasPanel;
    private String description;
    private String author;
    private String version;

    public String id() { return id == null ? "" : id; }
    public String name() { return name == null ? "" : name; }
    public boolean enabled() { return enabled; }
    public boolean hasPanel() { return hasPanel; }
    public String description() { return description == null ? "" : description; }
    public String author() { return author == null ? "" : author; }
    public String version() { return version == null ? "" : version; }
}
