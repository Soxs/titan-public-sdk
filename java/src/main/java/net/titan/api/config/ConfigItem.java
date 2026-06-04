package net.titan.api.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigItem {
    String keyName();
    String name();
    String description();
    String section() default "";
    int position() default 0;
    boolean hidden() default false;

    /// Lower bound for {@code int}-returning slider settings.
    int min() default 0;
    /// Upper bound for {@code int}-returning slider settings.
    int max() default 100;
    /// When true, an {@code int}-returning item is rendered as a color picker
    /// (the value is a packed 0xRRGGBB / 0xAARRGGBB color) instead of a slider.
    boolean color() default false;
    /// When true, a {@code String}-returning item is stored as a DPAPI-protected
    /// string and rendered as a masked text field.
    boolean secret() default false;
}
