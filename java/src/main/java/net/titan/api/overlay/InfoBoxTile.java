package net.titan.api.overlay;

/** One tile in an ordered HUD group. The icon key refers to artwork registered on that group.
 * Text and tooltip are plain UTF-8. Key zero draws a text-only tile. */
public final class InfoBoxTile {
    private final int iconKey, textArgb, backgroundArgb;
    private final String text, tooltip;

    public InfoBoxTile(int iconKey, int textArgb, int backgroundArgb, String text, String tooltip) {
        if (iconKey < 0) throw new IllegalArgumentException("Negative infobox icon key");
        this.iconKey = iconKey;
        this.textArgb = textArgb;
        this.backgroundArgb = backgroundArgb;
        this.text = checked(text, 128);
        this.tooltip = checked(tooltip, 1024);
    }

    public InfoBoxTile(int iconKey, String text, String tooltip) {
        this(iconKey, 0xffffffff, 0xaa282828, text, tooltip);
    }

    private static String checked(String value, int maxBytes) {
        java.util.Objects.requireNonNull(value);
        int bytes = 0;
        for (int i = 0; i < value.length();) {
            int cp = value.codePointAt(i);
            if (cp == 0 || (cp >= Character.MIN_SURROGATE && cp <= Character.MAX_SURROGATE))
                throw new IllegalArgumentException("Infobox text contains NUL or an unpaired surrogate");
            bytes += cp <= 0x7f ? 1 : cp <= 0x7ff ? 2 : cp <= 0xffff ? 3 : 4;
            if (bytes > maxBytes) throw new IllegalArgumentException("Infobox text exceeds " + maxBytes + " UTF-8 bytes");
            i += Character.charCount(cp);
        }
        return value;
    }

    public int iconKey() { return iconKey; }
    public int textArgb() { return textArgb; }
    public int backgroundArgb() { return backgroundArgb; }
    public String text() { return text; }
    public String tooltip() { return tooltip; }
}
