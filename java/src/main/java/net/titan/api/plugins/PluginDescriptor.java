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
}
