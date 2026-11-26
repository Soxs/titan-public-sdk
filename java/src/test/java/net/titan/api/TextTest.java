package net.titan.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextTest {
    private static String fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test void originalClassGoldenCasesIncludeMalformedTagsAndUnicode() throws Exception {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(
                TextTest.class.getResourceAsStream("/net/titan/api/game_text_golden.tsv"),
                StandardCharsets.UTF_8))) {
            int count = 0;
            for (String line; (line = input.readLine()) != null;) {
                if (line.startsWith("#")) continue;
                String[] row = line.split("\\t", -1);
                assertEquals(2, row.length);
                assertEquals(fromHex(row[1]), Text.removeTags(fromHex(row[0])), "golden " + count++);
            }
            assertTrue(count >= 25);
        }
    }

    @Test void nullIsRejectedAndUtf16OutsideTagsIsUntouched() {
        assertThrows(NullPointerException.class, () -> Text.removeTags(null));
        assertEquals("", Text.removeTags(""));
        String unpaired = "\uD800x\uDC00";
        assertEquals(unpaired, Text.removeTags(unpaired + "<tag>"));
        assertEquals("\uD800", Text.removeTags("<\uDC00>\uD800"));
    }
}
