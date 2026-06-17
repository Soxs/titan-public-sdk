package net.titan.api.internal;

public interface SchedulerBackend {
    void runOnClientTick(Runnable callback);
    void runOnRender(Runnable callback);
}
