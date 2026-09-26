package net.titan.api.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ConfigSection {
    String keyName();
    String name();
    String description() default "";
    int position() default 0;
    boolean closedByDefault() default false;

    /**
     * keyName of another section in the same config to nest this one inside.
     * The controller draws it as a collapsing header within the parent's,
     * after the parent's own settings, ordered by position among its
     * siblings. An unknown parent, a section that is its own parent, and
     * parents that loop are rejected when the plugin is scanned.
     * SDK 143, Java SDK 0.1.68.
     */
    String parent() default "";
}
