package net.titan.api.plugins;

import net.titan.api.config.Config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginDescriptor {
    String id();
    String name();
    String description();
    String author();
    String version();
    int sdkVersion() default 1;
    boolean defaultEnabled() default false;
    Class<? extends Config> config() default Config.class;

    /// Plugins this plugin depends on, referenced by type (RuneLite-style).
    /// The referenced plugin's own {@code @PluginDescriptor.id()} is resolved
    /// at scan time and added to the load-order graph; declared plugins are
    /// guaranteed to load first and can be {@code @Inject}ed directly.
    Class<? extends Plugin>[] dependencies() default {};

    /// Escape hatch: depend on a plugin by its id string. Use this only for
    /// cross-language dependencies where the type is not available on the
    /// classpath; prefer {@link #dependencies()} for same-language deps.
    String[] dependencyIds() default {};
}
