package net.titan.api.panel;

/// The value carried by a panel control interaction, delivered to
/// {@code Plugin.onPanelAction}. Mirrors the {@code PluginProtocol::Value}
/// tagged union: a checkbox/selectable toggle sends a boolean, a slider sends
/// an integer, and an input field sends a string. Buttons send a boolean
/// {@code true}.
public final class PanelValue {
    public enum Type { BOOLEAN, INTEGER, STRING }

    private final Type type;
    private final boolean boolValue;
    private final int intValue;
    private final String stringValue;

    private PanelValue(Type type, boolean boolValue, int intValue, String stringValue) {
        this.type = type;
        this.boolValue = boolValue;
        this.intValue = intValue;
        this.stringValue = stringValue == null ? "" : stringValue;
    }

    public static PanelValue ofBoolean(boolean value) {
        return new PanelValue(Type.BOOLEAN, value, value ? 1 : 0, value ? "true" : "false");
    }

    public static PanelValue ofInt(int value) {
        return new PanelValue(Type.INTEGER, value != 0, value, Integer.toString(value));
    }

    public static PanelValue ofString(String value) {
        return new PanelValue(Type.STRING, value != null && !value.isEmpty(), 0, value);
    }

    public Type type() {
        return type;
    }

    public boolean asBoolean() {
        return type == Type.BOOLEAN ? boolValue : intValue != 0;
    }

    public int asInt() {
        if (type == Type.INTEGER) return intValue;
        if (type == Type.BOOLEAN) return boolValue ? 1 : 0;
        try {
            return Integer.parseInt(stringValue.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public String asString() {
        return stringValue;
    }
}
