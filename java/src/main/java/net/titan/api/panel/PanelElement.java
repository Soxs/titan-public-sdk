package net.titan.api.panel;

/// A single ImGui command appended to a {@link Panel}. Mirrors the
/// {@code PluginProtocol::PanelElement} wire struct field-for-field so the
/// runtime can serialize it into the shared panel command format.
///
/// Plugins do not normally construct these directly; use the fluent
/// {@link Panel} builder methods instead.
public final class PanelElement {
    /// Default color sentinel (opaque white) shared with the native SDK.
    public static final int DEFAULT_COLOR = 0xFFFFFFFF;
    /// Default action id sentinel ("no action") shared with the native SDK.
    public static final int NO_ACTION = -1;

    public PanelElementType type = PanelElementType.TEXT;
    public String text = "";
    public String textSecondary = "";
    public int color = DEFAULT_COLOR;
    public int actionId = NO_ACTION;
    public int intVal = 0;
    public int intVal2 = 0;
    public int intVal3 = 0;
    public float floatVal = 0.0f;
    public float floatVal2 = 0.0f;
    public float floatVal3 = 0.0f;
    public float widthVal = 0.0f;
    public float heightVal = 0.0f;
    public boolean boolVal = false;

    public PanelElement(PanelElementType type, String text) {
        this.type = type == null ? PanelElementType.TEXT : type;
        this.text = text == null ? "" : text;
    }
}
