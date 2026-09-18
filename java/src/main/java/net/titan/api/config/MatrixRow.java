package net.titan.api.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// One row of a checkbox-matrix {@link ConfigItem}.
///
/// A row names the column labels it actually has, so the grid's shape is
/// declared in the same vocabulary the UI renders rather than by position.
/// Columns a row does not name do not exist on it: they render as a blank gap
/// and can never be checked. Every name in {@link #cells()} and
/// {@link #checked()} is resolved against {@link ConfigItem#columns()} when the
/// plugin is scanned, so a misspelling is a load-time error rather than a
/// checkbox that quietly moved.
///
/// ```java
/// @ConfigItem(keyName = "obstacles", name = "Obstacles", description = "...",
///     columns = {"Bridge", "Grapple", "Brazier", "Portal"},
///     rows = {
///         @MatrixRow(label = "Floor 1", cells = {"Bridge", "Grapple"}),
///         @MatrixRow(label = "Floor 2", cells = {"Grapple", "Brazier"}),
///         @MatrixRow(label = "Floor 3", cells = {"Bridge", "Portal"},
///                    checked = {"Portal"}),
///     })
/// boolean[][] obstacles();
/// ```
///
/// {@code @Target({})} on purpose: this annotation is only ever usable as a
/// member value of {@link ConfigItem}, never on a declaration of its own.
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface MatrixRow {
    /// Row label, rendered down the left-hand side of the grid.
    String label();

    /// Column labels present on this row, drawn from
    /// {@link ConfigItem#columns()}. Empty (the default) means every column.
    String[] cells() default {};

    /// Subset of {@link #cells()} that starts checked. Empty means none.
    /// Naming a column this row does not have is a load-time error.
    String[] checked() default {};
}
