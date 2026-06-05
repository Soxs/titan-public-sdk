package net.titan.api.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Container for repeated {@link SidePanel} annotations. The Java compiler
/// synthesizes this automatically when a class declares more than one
/// {@code @SidePanel}; plugins do not use it directly.
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SidePanels {
    SidePanel[] value();
}
