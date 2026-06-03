package net.titan.api.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScriptFired {
    private final int scriptId;
    private final List<Integer> args;
    private final List<Integer> results;

    public ScriptFired(int scriptId, List<Integer> args, List<Integer> results) {
        this.scriptId = scriptId;
        this.args = args == null ? Collections.emptyList() :
            Collections.unmodifiableList(new ArrayList<>(args));
        this.results = results == null ? Collections.emptyList() :
            Collections.unmodifiableList(new ArrayList<>(results));
    }

    public int scriptId() { return scriptId; }
    public List<Integer> args() { return args; }
    public List<Integer> results() { return results; }
}
