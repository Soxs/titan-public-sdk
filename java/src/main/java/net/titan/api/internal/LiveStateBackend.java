package net.titan.api.internal;

public interface LiveStateBackend {
    default String actorOverheadText(long entityPtr) { return null; }
    default Integer actorOverheadTextCycles(long entityPtr) { return null; }
    <T> T current(T value);
    boolean exists(Object value);
    String identity(Object value);
    <T> T snapshot(T value);
}
