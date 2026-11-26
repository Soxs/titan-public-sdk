import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.titan.api.Text;

/** Developer-only actual-class oracle for the shared game-text golden corpus. */
public final class GameTextParity {
    private static void check(boolean good, String message) {
        if (!good) throw new AssertionError(message);
    }
    private static String fromHex(String hex) {
        byte[] bytes = new byte[hex.length()/2];
        for (int i=0;i<bytes.length;i++)
            bytes[i]=(byte) Integer.parseInt(hex.substring(i*2,i*2+2),16);
        return new String(bytes, StandardCharsets.UTF_8);
    }
    public static void main(String[] args) throws Exception {
        List<URL> urls = new ArrayList<>();
        for (int i=1;i<args.length;i++) urls.add(Path.of(args[i]).toUri().toURL());
        try (URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]),
                ClassLoader.getPlatformClassLoader())) {
            Method remove = Class.forName("net.runelite.client.util.Text", true, loader)
                .getMethod("removeTags", String.class);
            int count=0;
            for (String line: Files.readAllLines(Path.of(args[0]), StandardCharsets.UTF_8)) {
                if (line.startsWith("#")) continue;
                String[] row=line.split("\\t",-1);
                check(row.length==2,"golden row");
                String input=fromHex(row[0]), expected=fromHex(row[1]);
                check(expected.equals(remove.invoke(null,input)),"original golden "+count);
                check(expected.equals(Text.removeTags(input)),"Titan golden "+count);
                count++;
            }
            String[] utf16 = {"\uD800x\uDC00<tag>", "<\uDC00>\uD800"};
            for (String input:utf16)
                check(remove.invoke(null,input).equals(Text.removeTags(input)),"unpaired UTF-16 preserved");
            try {
                remove.invoke(null, new Object[]{null});
                throw new AssertionError("original accepted null");
            } catch (InvocationTargetException error) {
                check(error.getCause() instanceof NullPointerException,"original null exception");
            }
            try { Text.removeTags(null); throw new AssertionError("Titan accepted null"); }
            catch (NullPointerException expected) {}
            System.out.println("PASS: "+count+" original/Titan golden cases, unpaired UTF-16 and null");
        }
    }
}
