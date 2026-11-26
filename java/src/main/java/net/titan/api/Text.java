package net.titan.api;

import java.util.regex.Pattern;

/** Pure game-text helpers. This is not an HTML parser or escaping API. */
public final class Text {
    private static final Pattern TAGS = Pattern.compile("<([^>]*)>");

    private Text() {}

    /**
     * Removes each span from an opening angle bracket through the next closing
     * angle bracket, matching pinned RuneLite Text.removeTags. Empty and nested
     * tag-like spans are removed; incomplete spans and entities are unchanged.
     * @throws NullPointerException if text is null.
     */
    public static String removeTags(String text) {
        return TAGS.matcher(text).replaceAll("");
    }
}
