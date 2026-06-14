package net.titan.api.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Declares a side panel exposed by a plugin. A plugin may declare several
/// {@code @SidePanel} annotations on its {@code @PluginDescriptor} class; the
/// controller renders one navigation button per panel.
///
/// The panel content is produced by overriding
/// {@code Plugin.buildPanel(panelId, Panel)} and control interactions are
/// delivered to {@code Plugin.onPanelAction(panelId, actionId, value)}, both
/// keyed by {@link #id()}.
///
/// @code
///   @PluginDescriptor(id = "stats", name = "Stats", ...)
///   @SidePanel(id = "main", title = "Statistics",
///              icon = "awesome:chart-bar", iconColor = 0xFF68CC92)
///   @SidePanel(id = "about", title = "About", image = "icons/about.png")
///   public class StatsPlugin implements Plugin { ... }
/// @endcode
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(SidePanels.class)
public @interface SidePanel {
    /// Stable identifier used to route panel content and actions.
    String id();

    /// Display title shown on the navigation button tooltip / header.
    String title();

    /// Optional side-panel icon spec for the navigation button. Use
    /// {@code awesome:alias}, {@code lucide:name}, {@code phosphor:name[:weight]},
    /// or a legacy raw Font Awesome glyph.
    String icon() default "";

    /// Optional icon tint in Titan ARGB format (0xAARRGGBB). Zero uses the
    /// controller theme. Ignored when {@link #image()} is present.
    int iconColor() default 0;

    /// Optional classpath resource path to a custom PNG image icon
    /// (RuneLite-style). Loaded from the plugin JAR via the plugin class
    /// loader. Takes precedence over {@link #icon()} when present.
    String image() default "";
}
