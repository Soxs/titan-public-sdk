package net.titan.runtime.spi;

import java.util.List;
import java.util.Objects;

/** Framework-neutral plugin metadata. Entry classes need not implement Titan Plugin. */
public final class PluginDescriptor {
    private final String id, name, description, author, version, mainClass;
    private final boolean defaultEnabled;
    private final List<String> dependencies;
    public PluginDescriptor(String id, String name, String description, String author,
                            String version, String mainClass, boolean defaultEnabled,
                            List<String> dependencies) {
        this.id = required(id, "id", 31);
        this.name = required(name, "name", 127);
        this.description = Objects.requireNonNull(description, "description");
        this.author = required(author, "author", 127);
        this.version = required(version, "version", 63);
        this.mainClass = required(mainClass, "mainClass", 255);
        this.defaultEnabled = defaultEnabled;
        this.dependencies = List.copyOf(dependencies);
        for (String dependency : this.dependencies) required(dependency, "dependency", 31);
    }
    private static String required(String value, String field, int maximum) {
        Objects.requireNonNull(value, field);
        if (value.isEmpty() || value.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > maximum || value.indexOf('\0') >= 0)
            throw new IllegalArgumentException("Invalid plugin " + field);
        return value;
    }
    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String author() { return author; }
    public String version() { return version; }
    public String mainClass() { return mainClass; }
    public boolean defaultEnabled() { return defaultEnabled; }
    public List<String> dependencies() { return dependencies; }
}
