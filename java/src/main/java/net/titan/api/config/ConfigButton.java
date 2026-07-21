package net.titan.api.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Declares a value-less action button on a config interface. Apply it to a
/// {@code default void} method whose body is the runnable to execute when the
/// button is clicked in the config UI:
///
/// <pre>
/// &#64;ConfigButton(keyName = "resetStats", name = "Reset stats",
///               description = "Zero the counters")
/// default void resetStats() {
///     MyPlugin.resetStats();
/// }
/// </pre>
///
/// THREADING: the click is marshalled onto the game thread (main loop) by the
/// client before the method body runs, so it also fires in pre-login states
/// (title screen / Jagex launcher). Keep the body non-blocking.
///
/// The method runs on the config proxy, which has no injected services, so the
/// body should delegate to a shared/static entry point the plugin publishes if
/// it needs to touch plugin or game state.
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigButton {
    String keyName();
    String name();
    String description() default "";
    String section() default "";
    int position() default 0;
    boolean hidden() default false;
}
