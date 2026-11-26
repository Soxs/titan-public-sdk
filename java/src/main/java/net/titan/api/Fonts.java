package net.titan.api;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import net.titan.api.internal.TitanRuntime;

/** Fonts supplied by the running Titan host. The SDK bundles no font files.
 * Calls require no game thread. Unsupported hosts return empty results. */
public final class Fonts {
    private Fonts() {}

    public enum Face { REGULAR, BOLD, SMALL }

    /** An owned copy of the host's original TrueType bytes, or an empty array. */
    public static byte[] data(Face face) {
        byte[] bytes = TitanRuntime.getUiAssetsBackend().fontData(Objects.requireNonNull(face, "face").ordinal());
        return bytes == null ? new byte[0] : bytes.clone();
    }

    /** Creates a size-16 font from the actual host bytes, without installing it
     * globally or falling back to a system font. Callers may retain/derive the
     * returned immutable Font for drawing at other sizes. */
    public static Optional<Font> font(Face face) {
        byte[] bytes = data(face);
        if (bytes.length == 0) return Optional.empty();
        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, input);
            return Optional.of(font.deriveFont(face == Face.BOLD ? Font.BOLD : Font.PLAIN, 16.0f));
        } catch (FontFormatException | IOException unavailable) {
            return Optional.empty();
        }
    }
}
