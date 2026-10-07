package net.titan.api.plugins;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares an HTML sidebar whose resources remain inside the declaring JAR.
 * The resource root and entrypoint use relative, case-sensitive forward-slash
 * paths. Native and HTML side panels share eight slots and one ID namespace.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(HtmlSidePanels.class)
public @interface HtmlSidePanel {
    String id();
    String title();
    String resourceRoot();
    String entrypoint() default "index.html";
    String icon() default "";
    int iconColor() default 0;
    String image() default "";
}
