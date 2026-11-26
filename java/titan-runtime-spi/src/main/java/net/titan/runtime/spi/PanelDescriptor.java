package net.titan.runtime.spi;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** A dynamic native side-panel navigation entry. Its Swing contents stay in the provider. */
public final class PanelDescriptor {
    private final String id, title;
    private final int priority;
    private final byte[] iconPng;
    private final String registration;
    private final boolean actionOnly, hasPopup;
    public PanelDescriptor(String id, String title, int priority, byte[] iconPng) {
        this(id, title, priority, iconPng, "", false, false);
    }
    /** Commands require titan.panel.commands.v1 and a fresh token for each registration. */
    public PanelDescriptor(String id, String title, int priority, byte[] iconPng,
                           String registration, boolean actionOnly, boolean hasPopup) {
        this.id = text(id, "id", 31); this.title = text(title, "title", 63);
        this.registration = Objects.requireNonNull(registration, "registration");
        if ((!registration.isEmpty() && !registration.matches("[0-9a-f]{32}"))
                || (actionOnly || hasPopup) && registration.isEmpty())
            throw new IllegalArgumentException("Panel commands require a 32-digit lowercase registration token");
        this.actionOnly = actionOnly; this.hasPopup = hasPopup;
        this.priority = priority;
        if (iconPng != null && iconPng.length > 128 * 1024) throw new IllegalArgumentException("Panel icon exceeds 128 KiB");
        this.iconPng = iconPng == null ? new byte[0] : iconPng.clone();
    }
    private static String text(String value, String field, int maximum) {
        Objects.requireNonNull(value, field);
        if (value.isBlank() || value.indexOf('\0') >= 0 || value.getBytes(StandardCharsets.UTF_8).length > maximum)
            throw new IllegalArgumentException("Invalid panel " + field);
        return value;
    }
    public String id() { return id; }
    public String title() { return title; }
    public int priority() { return priority; }
    public byte[] iconPng() { return iconPng.clone(); }
    public String registration() { return registration; }
    public boolean actionOnly() { return actionOnly; }
    public boolean hasPopup() { return hasPopup; }
}
