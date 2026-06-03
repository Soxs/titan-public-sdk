package net.titan.api.queries;

import net.titan.api.Client;
import net.titan.api.Widget;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class WidgetQuery extends Query<Widget, WidgetQuery> {
    private final Client client;
    private boolean truncated;

    public WidgetQuery(Client client, int groupId) {
        this(client, client == null ? null : client.widgetQuery(groupId));
    }

    private WidgetQuery(Client client, WidgetQuerySnapshot snapshot) {
        super(snapshot == null ? null : snapshot.widgets());
        this.client = client;
        this.truncated = snapshot != null && snapshot.isTruncated();
    }

    private WidgetQuery(Client client, List<Widget> widgets, boolean truncated) {
        super(widgets);
        this.client = client;
        this.truncated = truncated;
    }

    public static WidgetQuery from(Client client, List<Widget> widgets) {
        return new WidgetQuery(client, widgets, false);
    }

    public WidgetQuery packedId(int packedId) {
        where(widget -> widget.packedId() == packedId);
        boolean hasFlat = false;
        for (Widget widget : items) {
            if (!widget.isDynamic()) {
                hasFlat = true;
                break;
            }
        }
        if (hasFlat) {
            items.removeIf(Widget::isDynamic);
        }
        return this;
    }

    public WidgetQuery group(int groupId) {
        return where(widget -> ((widget.packedId() >>> 16) & 0xFFFF) == groupId);
    }

    public WidgetQuery child(int childId) {
        return where(widget -> (widget.packedId() & 0xFFFF) == childId);
    }

    public WidgetQuery slot(int index) {
        children();
        return where(widget -> widget.dynamicChildSlot() == index);
    }

    public WidgetQuery textContains(String needle) {
        return where(widget -> NamedQuery.containsIgnoreCase(widget.text(), needle));
    }

    public WidgetQuery textEquals(String text) {
        return where(widget -> NamedQuery.equalsIgnoreCase(widget.text(), text));
    }

    public WidgetQuery isVisible() {
        return where(Widget::isVisible);
    }

    public WidgetQuery isHidden() {
        return where(widget -> !widget.isVisible());
    }

    public WidgetQuery type(int type) {
        return where(widget -> widget.type() == type);
    }

    public WidgetQuery contentType(int contentType) {
        return where(widget -> widget.contentType() == contentType);
    }

    public WidgetQuery itemId(int itemId) {
        return where(widget -> widget.itemId() == itemId);
    }

    public WidgetQuery children() {
        if (client == null) {
            items.clear();
            return this;
        }

        Map<String, Widget> children = new LinkedHashMap<>();
        for (Widget parent : items) {
            WidgetQuerySnapshot snapshot = client.widgetChildrenQueryAtPath(parent.address());
            truncated = truncated || snapshot.isTruncated();
            for (Widget child : snapshot.widgets()) {
                children.put(addressKey(child), child);
            }
        }
        items.clear();
        items.addAll(children.values());
        return this;
    }

    public boolean isTruncated() {
        return truncated;
    }

    private String addressKey(Widget widget) {
        return widget.rootPackedId() + ":" + widget.dynamicPath();
    }
}
