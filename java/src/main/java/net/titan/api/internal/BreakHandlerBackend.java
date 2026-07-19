package net.titan.api.internal;

import net.titan.api.BreakCommand;
import net.titan.api.plugins.Plugin;

/** Runtime bridge behind the public instance-based Break Handler utility. */
public interface BreakHandlerBackend {
    int REPORT_RUNNING = 1;
    int REPORT_PREPARING = 2;
    int REPORT_SAFE_PAUSED = 3;
    int REPORT_DEFERRED = 4;
    int REPORT_ERROR = 5;

    boolean register(Plugin plugin, boolean configurable);
    boolean start(Plugin plugin);
    boolean stop(Plugin plugin);
    boolean unregister(Plugin plugin);
    BreakCommand poll(Plugin plugin);
    boolean report(Plugin plugin, int state, int code, int retryAfterMs, String reason);
}
