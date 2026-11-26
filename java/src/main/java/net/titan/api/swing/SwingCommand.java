package net.titan.api.swing;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** One ordered toolbar popup choice. The callback runs on the owning plugin's
 * EDT scope when that exact popup snapshot is selected. */
public final class SwingCommand implements Runnable {
    private final String title;
    private final Runnable action;

    public SwingCommand(String title, Runnable action) {
        this.title = Objects.requireNonNull(title, "title");
        this.action = Objects.requireNonNull(action, "action");
        if (title.isEmpty() || !StandardCharsets.UTF_8.newEncoder().canEncode(title)
                || title.getBytes(StandardCharsets.UTF_8).length > 256)
            throw new IllegalArgumentException("Command title must contain 1 to 256 UTF-8 bytes");
        for (int i = 0; i < title.length(); i++)
            if (title.charAt(i) < 32 || title.charAt(i) == 127)
                throw new IllegalArgumentException("Command title cannot contain control characters");
    }

    public String title() { return title; }
    @Override public void run() { action.run(); }
}
