package net.titan.api;

import net.titan.api.internal.BreakHandlerBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;

import java.util.Objects;

/**
 * Instance-based registration and pause protocol for the native Break Handler.
 *
 * <p>Pass the same plugin instance that the embedded runtime loaded. Helper
 * classes may retain that instance and call this utility outside the plugin
 * class itself; the runtime validates object identity on every call. A normal
 * owner calls {@link #register(Plugin)} and {@link #start(Plugin)} from
 * {@code onEnable}, then {@link #stop(Plugin)} and
 * {@link #unregister(Plugin)} from {@code onDisable}.</p>
 */
public final class BreakHandler {
    private BreakHandler() {}

    /**
     * Register a configurable schedule owner.
     *
     * @param plugin exact loaded plugin instance
     * @return true when registered or already registered with the same role
     */
    public static boolean register(Plugin plugin) {
        return register(plugin, true);
    }

    /**
     * Register a plugin. A non-configurable registration participates in
     * global pauses but does not own an independent run/break schedule.
     *
     * @param plugin exact loaded plugin instance
     * @param configurable true for a schedule owner, false for a participant only
     * @return true when registered or already registered with the same role
     */
    public static boolean register(Plugin plugin, boolean configurable) {
        return backend().register(requirePlugin(plugin), configurable);
    }

    /**
     * Start this registration's scheduling/participation lifecycle.
     *
     * @param plugin exact loaded plugin instance
     * @return true when the registration is active
     */
    public static boolean start(Plugin plugin) {
        return backend().start(requirePlugin(plugin));
    }

    /**
     * Stop this registration while keeping it visible and registered.
     *
     * @param plugin exact loaded plugin instance
     * @return true when the registration was stopped
     */
    public static boolean stop(Plugin plugin) {
        return backend().stop(requirePlugin(plugin));
    }

    /**
     * Stop and remove this registration.
     *
     * @param plugin exact loaded plugin instance
     * @return true when absent after the call
     */
    public static boolean unregister(Plugin plugin) {
        return backend().unregister(requirePlugin(plugin));
    }

    /**
     * Poll and record the current coordinator epoch for this plugin.
     *
     * @param plugin exact loaded plugin instance
     * @return current command, or an unavailable {@link BreakCommand#none()}
     */
    public static BreakCommand poll(Plugin plugin) {
        BreakCommand command = backend().poll(requirePlugin(plugin));
        return command == null ? BreakCommand.none() : command;
    }

    public static boolean shouldBreak(Plugin plugin) {
        return poll(plugin).shouldBreak();
    }

    public static boolean isBreakActive(Plugin plugin) {
        return poll(plugin).isBreakActive();
    }

    public static boolean shouldResume(Plugin plugin) {
        return poll(plugin).shouldResume();
    }

    /**
     * Acknowledge the safe paused boundary for the most recently polled epoch.
     *
     * @param plugin exact loaded plugin instance
     * @return true when the acknowledgement matched the current epoch
     */
    public static boolean paused(Plugin plugin) {
        return report(plugin, BreakHandlerBackend.REPORT_SAFE_PAUSED, 0, 0, "");
    }

    /**
     * Report bounded preparation deferral for the most recently polled epoch.
     *
     * @param plugin exact loaded plugin instance
     * @param retryAfterMs non-negative retry hint; it does not extend the global timeout
     * @param reason sanitized non-secret reason, or null
     * @return true when the report matched the current epoch
     */
    public static boolean defer(Plugin plugin, int retryAfterMs, String reason) {
        if (retryAfterMs < 0) {
            throw new IllegalArgumentException("retryAfterMs must not be negative");
        }
        return report(plugin, BreakHandlerBackend.REPORT_DEFERRED, 0,
            retryAfterMs, reason);
    }

    /**
     * Report an unrecoverable preparation or resume error.
     *
     * @param plugin exact loaded plugin instance
     * @param code non-negative plugin-defined error code
     * @param reason sanitized non-secret reason, or null
     * @return true when the report matched the current epoch
     */
    public static boolean error(Plugin plugin, int code, String reason) {
        if (code < 0) {
            throw new IllegalArgumentException("code must not be negative");
        }
        return report(plugin, BreakHandlerBackend.REPORT_ERROR, code, 0, reason);
    }

    /**
     * Report normal operation after start or after safely handling Resume.
     *
     * @param plugin exact loaded plugin instance
     * @return true when the report matched the current epoch
     */
    public static boolean running(Plugin plugin) {
        return report(plugin, BreakHandlerBackend.REPORT_RUNNING, 0, 0, "");
    }

    private static boolean report(Plugin plugin, int state, int code,
                                  int retryAfterMs, String reason) {
        return backend().report(requirePlugin(plugin), state, code, retryAfterMs,
            reason == null ? "" : reason);
    }

    private static Plugin requirePlugin(Plugin plugin) {
        return Objects.requireNonNull(plugin, "plugin");
    }

    private static BreakHandlerBackend backend() {
        return TitanRuntime.getBreakHandlerBackend();
    }
}
