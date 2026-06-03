package net.titan.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WidgetAddress {
    private int rootPackedId;
    private List<Integer> dynamicPath;

    public WidgetAddress() {}

    public WidgetAddress(int rootPackedId, List<Integer> dynamicPath) {
        this.rootPackedId = rootPackedId;
        this.dynamicPath = dynamicPath == null
            ? Collections.emptyList()
            : new ArrayList<>(dynamicPath);
    }

    public int rootPackedId() { return rootPackedId; }

    public List<Integer> dynamicPath() {
        return dynamicPath == null
            ? Collections.emptyList()
            : Collections.unmodifiableList(dynamicPath);
    }
}
