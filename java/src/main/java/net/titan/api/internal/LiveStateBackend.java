package net.titan.api.internal;

public interface LiveStateBackend {
    <T> T current(T value);
    boolean exists(Object value);
    String identity(Object value);
    <T> T snapshot(T value);
}
