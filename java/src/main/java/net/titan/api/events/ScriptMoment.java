package net.titan.api.events;

import java.util.Objects;
import java.util.Optional;

/**
 * A script of the game's, about to run or about to return, for the ids which the plugin named with
 * {@link net.titan.api.Client#watchScripts}. SDK 187.
 *
 * <p>It arrives on the game thread and inside the interpreter, which waits. Widgets and variables read as at any
 * other time. The interpreter can start no other script meanwhile: those wait, as menu actions do, for the next
 * client tick. Do little here. The values are those of this moment, so read them in the handler.</p>
 */
public final class ScriptMoment {
    /** The moment's values, from the host. */
    public interface Values {
        /** Null when they are not known. */
        int[] read(boolean arguments);
        boolean replace(boolean argument, int index, int value);
    }

    private final int scriptId;
    private final boolean before;
    private final boolean nested;
    private final Values values;

    public ScriptMoment(int scriptId, boolean before, boolean nested, Values values) {
        this.scriptId = scriptId;
        this.before = before;
        this.nested = nested;
        this.values = Objects.requireNonNull(values, "values");
    }

    public int getScriptId() { return scriptId; }
    /** The script is about to run. Otherwise it is about to return. */
    public boolean isBefore() { return before; }
    /** Another script called this one. Its arguments are on top of the integers before it runs. */
    public boolean isNested() { return nested; }
    /** What the game started the script with, before it runs, when all of it is integers. */
    public Optional<int[]> getArguments() { return Optional.ofNullable(values.read(true)); }
    /** The interpreter's integer stack: a called script's arguments, or a returning script's results, are on top. */
    public Optional<int[]> getIntegers() { return Optional.ofNullable(values.read(false)); }
    /** Replaces an argument before the script starts. False when there is none such. */
    public boolean setArgument(int index, int value) { return values.replace(true, index, value); }
    /** Replaces a value on the interpreter's stack. False when there is none such. */
    public boolean setInteger(int index, int value) { return values.replace(false, index, value); }

    @Override
    public String toString() {
        return "ScriptMoment[" + scriptId + (before ? ", before" : ", after") + (nested ? ", nested]" : "]");
    }
}
