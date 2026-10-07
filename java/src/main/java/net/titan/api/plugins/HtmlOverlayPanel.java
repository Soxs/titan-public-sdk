package net.titan.api.plugins;

import net.titan.api.overlay.OverlayPanelAnchor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Declares one packaged HTML overlay. At most eight may be declared. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(HtmlOverlayPanels.class)
public @interface HtmlOverlayPanel {
    String id();
    String resourceRoot();
    String entrypoint() default "index.html";
    int width() default 220;
    int height() default 160;
    OverlayPanelAnchor anchor() default OverlayPanelAnchor.DYNAMIC;
    int priority() default 50;
    boolean interactive() default false;
    boolean visible() default true;
}
