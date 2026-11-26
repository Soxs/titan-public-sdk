package net.titan.api.config;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** Shared validation for annotation-backed and directly constructed holders. */
final class SettingUnits {
    private SettingUnits() {}

    static String checked(String text) {
        Objects.requireNonNull(text, "units");
        if (text.getBytes(StandardCharsets.UTF_8).length > 256)
            throw new IllegalArgumentException("Setting units exceed 256 UTF-8 bytes");
        for (int offset = 0; offset < text.length();) {
            int codePoint = text.codePointAt(offset);
            int type = Character.getType(codePoint);
            if (Character.isISOControl(codePoint) || codePoint == 0x2028 || codePoint == 0x2029
                || codePoint == 0x061c || codePoint == 0x200e || codePoint == 0x200f
                || codePoint >= 0x202a && codePoint <= 0x202e
                || codePoint >= 0x2066 && codePoint <= 0x206f || type == Character.SURROGATE)
                throw new IllegalArgumentException("Setting units contain unsafe control characters");
            offset += Character.charCount(codePoint);
        }
        return text;
    }
}
