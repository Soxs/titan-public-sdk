package net.titan.api.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// Fluent builder for plugin side panels. A plugin's
/// {@code buildPanel(panelId, Panel)} method receives a {@code Panel} and
/// appends commands (text, buttons, checkboxes, sliders, tables, ...). The
/// runtime serializes the resulting command list into the shared panel wire
/// format and the controller replays the commands with ImGui.
///
/// This mirrors the native {@code titan::Panel} and the TypeScript panel
/// builder so all three plugin surfaces have identical capabilities.
public final class Panel {
    private final List<PanelElement> elements = new ArrayList<>();

    // --- Text ---
    public Panel text(String s) { push(PanelElementType.TEXT, s); return this; }
    public Panel wrapped(String s) { push(PanelElementType.TEXT_WRAPPED, s); return this; }
    public Panel disabled(String s) { push(PanelElementType.TEXT_DISABLED, s); return this; }
    public Panel bullet(String s) { push(PanelElementType.BULLET_TEXT, s); return this; }

    public Panel colored(String s, int color) {
        PanelElement el = push(PanelElementType.TEXT_COLORED, s);
        el.color = color;
        return this;
    }

    public Panel label(String label, String value) {
        PanelElement el = push(PanelElementType.LABEL_TEXT, label);
        el.textSecondary = value == null ? "" : value;
        return this;
    }

    // --- Layout ---
    public Panel separator() { push(PanelElementType.SEPARATOR, ""); return this; }
    public Panel separatorText(String s) { push(PanelElementType.SEPARATOR_TEXT, s); return this; }
    public Panel spacing() { push(PanelElementType.SPACING, ""); return this; }
    public Panel sameLine() { push(PanelElementType.SAME_LINE, ""); return this; }
    public Panel newLine() { push(PanelElementType.NEW_LINE, ""); return this; }
    public Panel indent() { push(PanelElementType.INDENT, ""); return this; }
    public Panel unindent() { push(PanelElementType.UNINDENT, ""); return this; }

    public Panel dummy(float width, float height) {
        PanelElement el = push(PanelElementType.DUMMY, "");
        el.widthVal = width;
        el.heightVal = height;
        return this;
    }

    // --- Interactive controls ---
    public Panel button(String label, int actionId) {
        PanelElement el = push(PanelElementType.BUTTON, label);
        el.actionId = actionId;
        return this;
    }

    public Panel smallButton(String label, int actionId) {
        PanelElement el = push(PanelElementType.SMALL_BUTTON, label);
        el.actionId = actionId;
        return this;
    }

    public Panel selectable(String label, int actionId) {
        return selectable(label, actionId, false);
    }

    public Panel selectable(String label, int actionId, boolean selected) {
        PanelElement el = push(PanelElementType.SELECTABLE, label);
        el.actionId = actionId;
        el.boolVal = selected;
        return this;
    }

    public Panel checkbox(String label, int actionId, boolean value) {
        PanelElement el = push(PanelElementType.CHECKBOX, label);
        el.actionId = actionId;
        el.boolVal = value;
        return this;
    }

    public Panel sliderInt(String label, int actionId, int value, int minValue, int maxValue) {
        PanelElement el = push(PanelElementType.SLIDER_INT, label);
        el.actionId = actionId;
        el.intVal = value;
        el.intVal2 = minValue;
        el.intVal3 = maxValue;
        return this;
    }

    public Panel sliderFloat(String label, int actionId, float value, float minValue, float maxValue) {
        PanelElement el = push(PanelElementType.SLIDER_FLOAT, label);
        el.actionId = actionId;
        el.floatVal = value;
        el.floatVal2 = minValue;
        el.floatVal3 = maxValue;
        return this;
    }

    public Panel inputText(String label, int actionId, String value) {
        return inputText(label, actionId, NO_SUBMIT, value);
    }

    /// Same as {@link #inputText(String, int, String)} but additionally fires
    /// {@code submitActionId} when the user presses Enter inside the field.
    public Panel inputText(String label, int actionId, int submitActionId, String value) {
        PanelElement el = push(PanelElementType.INPUT_TEXT, label);
        el.actionId = actionId;
        el.intVal2 = submitActionId;
        el.textSecondary = value == null ? "" : value;
        return this;
    }

    // --- Progress ---
    public Panel progress(float fraction) {
        return progress(fraction, "");
    }

    public Panel progress(float fraction, String overlay) {
        PanelElement el = push(PanelElementType.PROGRESS_BAR, overlay);
        el.floatVal = fraction;
        return this;
    }

    // --- Tree / collapsing ---
    public Panel collapsing(String label) { push(PanelElementType.COLLAPSING_HEADER, label); return this; }
    public Panel treeNode(String label) { push(PanelElementType.TREE_NODE, label); return this; }
    public Panel treePop() { push(PanelElementType.TREE_POP, ""); return this; }

    // --- Tables ---
    public Panel beginTable(String id, int columns) {
        return beginTable(id, columns, 0);
    }

    public Panel beginTable(String id, int columns, int flags) {
        PanelElement el = push(PanelElementType.BEGIN_TABLE, id);
        el.intVal = columns;
        el.intVal2 = flags;
        return this;
    }

    public Panel endTable() { push(PanelElementType.END_TABLE, ""); return this; }
    public Panel tableNextRow() { push(PanelElementType.TABLE_NEXT_ROW, ""); return this; }
    public Panel tableNextColumn() { push(PanelElementType.TABLE_NEXT_COLUMN, ""); return this; }
    public Panel tableHeadersRow() { push(PanelElementType.TABLE_HEADERS_ROW, ""); return this; }

    public Panel tableSetupColumn(String label) {
        return tableSetupColumn(label, 0, 0.0f);
    }

    public Panel tableSetupColumn(String label, int flags, float width) {
        PanelElement el = push(PanelElementType.TABLE_SETUP_COLUMN, label);
        el.intVal = flags;
        el.floatVal = width;
        return this;
    }

    // --- Tooltip on previous item ---
    public Panel tooltip(String t) { push(PanelElementType.SET_TOOLTIP, t); return this; }

    /// Raw escape hatch: append a custom element directly and return it so
    /// extra fields can be set in place.
    public PanelElement push(PanelElementType type, String text) {
        PanelElement el = new PanelElement(type, text);
        elements.add(el);
        return el;
    }

    /// The accumulated command list (read-only). Consumed by the runtime.
    public List<PanelElement> elements() {
        return Collections.unmodifiableList(elements);
    }

    private static final int NO_SUBMIT = -1;
}
