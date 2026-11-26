import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.titan.api.Experience;

/** Offline oracle; the pinned original is loaded separately from Titan's API. */
public final class ExperienceParity {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    private static int original(Method method, int value) throws Exception {
        return (Integer) method.invoke(null, value);
    }
    private static void originalRejects(Method method, int value) throws Exception {
        try {
            method.invoke(null, value);
            throw new AssertionError("Original accepted " + method.getName() + "(" + value + ")");
        } catch (InvocationTargetException error) {
            check(error.getCause() instanceof IllegalArgumentException, "Original invalid-domain exception");
        }
    }
    private static void titanRejects(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Titan accepted invalid input");
        } catch (IllegalArgumentException expected) {}
    }
    public static void main(String[] args) throws Exception {
        String source = Files.readString(Path.of(args[1]));
        Matcher table = Pattern.compile("xpForLevels = \\{([^}]+)\\}", Pattern.DOTALL).matcher(source);
        check(table.find(), "canonical C++ table found");
        String[] entries = table.group(1).trim().split(",\\s*");
        check(entries.length == 126, "canonical table length");
        try (URLClassLoader originalLoader = new URLClassLoader(
                new java.net.URL[]{Path.of(args[0]).toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            Class<?> type = Class.forName("net.runelite.api.Experience", true, originalLoader);
            Method xp = type.getMethod("getXpForLevel", int.class);
            Method level = type.getMethod("getLevelForXp", int.class);
            check(type.getField("MAX_REAL_LEVEL").getInt(null) == Experience.MAX_REAL_LEVEL, "real cap");
            check(type.getField("MAX_VIRT_LEVEL").getInt(null) == Experience.MAX_VIRT_LEVEL, "virtual cap");
            check(type.getField("MAX_SKILL_XP").getInt(null) == Experience.MAX_SKILL_XP, "skill cap");
            Set<Integer> queries = new LinkedHashSet<>();
            for (int i = 1; i <= 126; ++i) {
                int expected = original(xp, i);
                check(Integer.parseInt(entries[i-1].trim()) == expected, "C++ threshold " + i);
                check(Experience.xpForLevel(i) == expected, "Java threshold " + i);
                queries.add(expected);
                queries.add(expected + 1);
                if (expected > 0) queries.add(expected - 1);
            }
            queries.add(200_000_000); queries.add(200_000_001); queries.add(Integer.MAX_VALUE);
            // Also cover every interval midpoint and a deterministic full-domain sample.
            for (int i = 1; i < 126; ++i) {
                queries.add(original(xp, i) + (original(xp, i+1) - original(xp, i)) / 2);
            }
            java.util.Random random = new java.util.Random(0x745850L);
            for (int i = 0; i < 10_000; ++i) queries.add(random.nextInt(Integer.MAX_VALUE));
            for (int value : queries)
                check(Experience.levelForXp(value) == original(level, value), "Java lookup XP " + value);
            for (int value : new int[]{Integer.MIN_VALUE, -1, 0, 127, Integer.MAX_VALUE}) {
                originalRejects(xp, value);
                titanRejects(() -> Experience.xpForLevel(value));
            }
            for (int value : new int[]{Integer.MIN_VALUE, -1}) {
                originalRejects(level, value);
                titanRejects(() -> Experience.levelForXp(value));
            }
            System.out.println("PASS: 126 original/C++/Java thresholds, " + queries.size()
                + " original/Java XP queries, constants and invalid-domain exceptions");
        }
    }
}
