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

    /// Column labels for a checkbox-matrix item, left to right. Declaring
    /// {@code columns()} and {@link #rows()} selects the checkbox-grid control;
    /// the method must then return {@code boolean[][]} and, unlike every other
    /// config item, it is ABSTRACT rather than {@code default} -- the grid's
    /// cells and initial state come from {@link MatrixRow}, so there is no
    /// method body to read a default from. Authors read a cell as
    /// {@code config.item()[row][column]}.
    ///
    /// The value travels as a cell bitmask, {@code bit = row *
    /// columns().length + column}. At most 31 cells, so bit 31 is never used
    /// and the mask is always a positive {@code int}.
    String[] columns() default {};

    /// Rows for a checkbox-matrix item, top to bottom. Each row names the
    /// columns it has; see {@link MatrixRow}.
    MatrixRow[] rows() default {};
}
