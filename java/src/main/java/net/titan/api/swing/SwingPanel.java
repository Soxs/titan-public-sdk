package net.titan.api.swing;

import java.util.concurrent.CompletionStage;

/** One sidebar panel or action registration belonging to one plugin enable cycle. */
public interface SwingPanel extends AutoCloseable {
    /** Completes when the native sidebar entry is registered, or fails if unavailable. */
    CompletionStage<Void> ready();

    /** Request selection in the owning game's sidebar; acceptance is not visibility.
     * Action-only registrations return false without invoking their action. */
    boolean requestOpen();

    /** Revoke callbacks immediately and dispose the owned Swing peers asynchronously. */
    CompletionStage<Void> closeAsync();

    /** Starts the same asynchronous cleanup; never waits for the EDT. */
    @Override void close();
}
