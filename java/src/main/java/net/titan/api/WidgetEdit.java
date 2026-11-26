package net.titan.api;

import net.titan.api.internal.InteractionBackend;
import net.titan.api.internal.TitanRuntime;

import java.util.OptionalInt;

/**
 * Changes of one root or direct dynamic child's own fields, from {@link Widget#edit()}. Game thread. Each is made
 * before its call returns, as the game's own scripts make it, and the widget is drawn anew. One which cannot be made
 * leaves the others to be made, and {@link #ok()} is false from then on: the widget is gone, the thread is another, or
 * the host is older. A handle's own fields show a change when it is next read anew. SDK 188.
 */
public final class WidgetEdit {
    /** How an item widget shows its quantity. */
    public enum ItemQuantity {
        NEVER(0), STACKABLE(1), ALWAYS(3);
        private final int value;
        ItemQuantity(int value) { this.value = value; }
    }
    private final WidgetAddress address;
    private boolean ok = true;

    WidgetEdit(WidgetAddress address) { this.address = address; }

    /** Every change so far was made. */
    public boolean ok() { return ok; }

    public WidgetEdit hidden(boolean value) { return set(1003, value); }
    /** The text as it is, up to 4096 bytes of UTF-8. */
    public WidgetEdit text(String value) { return text(1112, value); }
    public WidgetEdit textColor(int rgb) { return set(1101, rgb); }
    public WidgetEdit textShadowed(boolean value) { return set(1115, value); }
    /** Each 0 left or top, 1 centre, 2 right or bottom. */
    public WidgetEdit textAlignment(int x, int y) { return set(10008, x).set(10009, y); }
    public WidgetEdit lineHeight(int pixels) { return set(10010, pixels); }
    public WidgetEdit fontId(int id) { return set(1113, id); }
    /** 0 is opaque and 255 cannot be seen. */
    public WidgetEdit opacity(int transparency) { return set(1103, transparency); }
    public WidgetEdit filled(boolean value) { return set(1102, value); }
    public WidgetEdit spriteId(int id) { return set(1105, id); }
    public WidgetEdit spriteTiled(boolean value) { return set(1107, value); }
    public WidgetEdit flipped(boolean horizontally, boolean vertically) { return set(1119, horizontally).set(1118, vertically); }
    public WidgetEdit borderType(int type) { return set(1116, type); }
    public WidgetEdit modelId(int id) { return set(1108, id); }
    public WidgetEdit item(int id, int quantity) { return set(10011, id).set(10012, quantity); }
    public WidgetEdit item(int id, int quantity, ItemQuantity shown) { return set(1213, id, quantity, shown.value); }

    /** What the widget's place comes from. {@link #revalidate()} applies it. */
    public WidgetEdit position(int x, int y) { return set(10000, x).set(10001, y); }
    public WidgetEdit positionModes(int x, int y) { return set(10004, x).set(10005, y); }
    /** What the widget's size comes from. {@link #revalidate()} applies it. */
    public WidgetEdit size(int width, int height) { return set(10002, width).set(10003, height); }
    public WidgetEdit sizeModes(int width, int height) { return set(10006, width).set(10007, height); }
    public WidgetEdit scroll(int x, int y) { return set(10020, x).set(10021, y); }
    public WidgetEdit scrollSize(int width, int height) { return set(10018, width).set(10019, height); }
    public WidgetEdit revalidate() { return set(20000); }
    /** And everything in a layer. */
    public WidgetEdit revalidateScroll() { return set(20001); }

    /** The text beside its actions. Spaces do not break, as the game's own. */
    public WidgetEdit name(String value) { return text(1305, value == null ? null : value.replace(' ', '\u00a0')); }
    /** An action by its index, 0 to 31. */
    public WidgetEdit action(int index, String value) {
        if (index < 0 || index > 31) { ok = false; return this; }
        return text(1300, value, index + 1);
    }
    public WidgetEdit clearActions() { return set(1307); }
    public WidgetEdit targetVerb(String value) { return text(1306, value); }
    public WidgetEdit dragDeadZone(int pixels) { return set(1303, pixels); }
    public WidgetEdit dragDeadTime(int ticks) { return set(1304, ticks); }
    public WidgetEdit stopsClicks(boolean value) { return set(1005, value); }
    public WidgetEdit stopsScrolling(boolean value) { return set(1006, value); }
    public WidgetEdit hasListener(boolean value) { return set(10013, value); }

    /**
     * A new child of a layer which is no child itself, in place of the one which has that index; -1 for the index after
     * the last. Its index, or empty.
     */
    public OptionalInt createChild(int type, int index) {
        int created = make(20002, null, type, index);
        return created < 0 ? OptionalInt.empty() : OptionalInt.of(created);
    }
    public OptionalInt createChild(int type) { return createChild(type, -1); }
    public WidgetEdit deleteChildren() { return set(20003); }

    private WidgetEdit set(int write, boolean value) { return set(write, value ? 1 : 0); }
    private WidgetEdit set(int write, int... values) { make(write, null, values); return this; }
    private WidgetEdit text(int write, String value, int... values) { make(write, value == null ? "" : value, values); return this; }
    private int make(int write, String text, int... values) {
        InteractionBackend backend = TitanRuntime.getInteractionBackend();
        int made = backend == null ? -1 : backend.writeWidget(address, write, values, text);
        if (made < 0) ok = false;
        return made;
    }
}
