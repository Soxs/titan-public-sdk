package net.titan.api.queries;

import net.titan.api.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WidgetQuerySnapshot {
    private final List<Widget> widgets;
    private final boolean truncated;

    public WidgetQuerySnapshot(List<Widget> widgets, boolean truncated) {
        this.widgets = Collections.unmodifiableList(
            new ArrayList<>(widgets == null ? Collections.emptyList() : widgets));
        this.truncated = truncated;
    }

    public List<Widget> widgets() { return widgets; }
    public boolean isTruncated() { return truncated; }
}
